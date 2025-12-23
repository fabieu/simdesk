package de.sustineo.simdesk.services.bop;

import de.sustineo.simdesk.configuration.CacheNames;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Setting;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.bop.BopProvider;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBopEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.mybatis.mapper.BopMapper;
import de.sustineo.simdesk.services.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Profile(SpringProfile.BOP)
@Service
@RequiredArgsConstructor
public class BopService {
    private final BopMapper bopMapper;
    private final SettingService settingService;

    @EventListener(ApplicationReadyEvent.class)
    protected void initializeBopTable() {
        Set<Pair<String, Integer>> availableTrackCarPairs = Track.getAllOfAccSortedByName().stream()
                .flatMap(track -> AccCar.getAll().stream().map(car -> Pair.of(track.getAccId(), car.getId())))
                .collect(Collectors.toSet());

        Set<Pair<String, Integer>> currentTrackCarPairs = bopMapper.findAll().stream()
                .map(bop -> Pair.of(bop.getTrackId(), bop.getCarId()))
                .collect(Collectors.toSet());

        availableTrackCarPairs.forEach(pair -> {
            if (!currentTrackCarPairs.contains(pair)) {
                Bop defaultBop = Bop.builder()
                        .trackId(pair.getLeft())
                        .carId(pair.getRight())
                        .restrictor(0)
                        .ballastKg(0)
                        .active(false)
                        .updateDatetime(Instant.now())
                        .build();

                bopMapper.insert(defaultBop);
            }
        });
    }

    @Cacheable(cacheNames = CacheNames.BOPS, key = "'all'")
    public List<Bop> getAll() {
        return bopMapper.findAll();
    }

    @Cacheable(cacheNames = CacheNames.BOPS, key = "'active'")
    public List<Bop> getActive() {
        return bopMapper.findActive();
    }

    @CacheEvict(cacheNames = CacheNames.BOPS, allEntries = true)
    public void insert(Bop bop) {
        bopMapper.insert(bop);
    }

    @CacheEvict(cacheNames = CacheNames.BOPS, allEntries = true)
    public void update(Bop bop) {
        if (bop.getTrackId() == null || bop.getCarId() == null) {
            return;
        }

        bopMapper.update(bop);
    }

    public AccBopEntry convertToAccBopEntry(Bop bop) {
        return AccBopEntry.builder()
                .trackId(bop.getTrackId())
                .carId(bop.getCarId())
                .ballastKg(bop.getBallastKg())
                .restrictor(bop.getRestrictor())
                .build();
    }

    @Cacheable(cacheNames = CacheNames.BOP_PROVIDERS)
    public Set<BopProvider> getBopProviders() {
        BopProvider[] bopProviders = settingService.getJson(Setting.BOP_PROVIDERS, BopProvider[].class);
        if (bopProviders == null) {
            return Collections.emptySet();
        }

        return Set.of(bopProviders);
    }

    @CacheEvict(cacheNames = CacheNames.BOP_PROVIDERS, allEntries = true)
    public void setBopProviders(Collection<BopProvider> bopProviders) {
        settingService.setJson(Setting.BOP_PROVIDERS, bopProviders.toArray(BopProvider[]::new));
    }
}
