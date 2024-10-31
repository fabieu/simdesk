package de.sustineo.simdesk.services.bop;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccBopEntry;
import de.sustineo.simdesk.entities.mapper.BopMapper;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_BOP)
@Service
public class BopService {
    private final BopMapper bopMapper;

    public BopService(BopMapper bopMapper) {
        this.bopMapper = bopMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void initializeBopTable() {
        Set<Pair<String, Integer>> availableTrackCarPairs = Track.getAllSortedByName().stream()
                .flatMap(track -> Car.getAllSortedByName().stream().map(car -> Pair.of(track.getTrackId(), car.getCarId())))
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

    @Cacheable("bops")
    public List<Bop> getAll() {
        return bopMapper.findAll();
    }

    @Cacheable("bops-active")
    public List<Bop> getActive() {
        return bopMapper.findActive();
    }

    @CacheEvict(value = {"bops", "bops-active"}, allEntries = true)
    public void insert(Bop bop) {
        bopMapper.insert(bop);
    }

    @CacheEvict(value = {"bops", "bops-active"}, allEntries = true)
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
}
