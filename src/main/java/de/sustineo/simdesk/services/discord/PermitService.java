package de.sustineo.simdesk.services.discord;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.auth.DiscordUser;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.services.UserService;
import de.sustineo.simdesk.services.auth.SecurityService;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_DISCORD)
@Service
public class PermitService {
    private final SecurityService securityService;
    private final DiscordService discordService;
    private final UserService userService;
    private final Map<String, List<CarGroup>> basePermitMap = new LinkedHashMap<>();
    private final Map<String, List<CarGroup>> nosPermitMap = new HashMap<>();
    private final Set<String> reviewRoles = new HashSet<>();
    private final Set<String> permitRoles = new HashSet<>();

    public PermitService(SecurityService securityService,
                         DiscordService discordService,
                         UserService userService) {
        this.securityService = securityService;
        this.discordService = discordService;
        this.userService = userService;

        // Ensure that the permit maps are sorted by permit level from highest to lowest
        basePermitMap.put("Permit-A", List.of(CarGroup.GT3, CarGroup.GT2, CarGroup.GTC, CarGroup.GT4, CarGroup.TCX));
        basePermitMap.put("Permit-B", List.of(CarGroup.GT4, CarGroup.TCX));
        basePermitMap.put("Permit-C", List.of(CarGroup.TCX));

        nosPermitMap.put("Permit-NOS-SP9", List.of(CarGroup.GT3));
        nosPermitMap.put("Permit-NOS-SPX", List.of(CarGroup.GT2));
        nosPermitMap.put("Permit-NOS-CUP2", List.of(CarGroup.GTC));
        nosPermitMap.put("Permit-NOS-SP10", List.of(CarGroup.GT4));
        nosPermitMap.put("Permit-NOS-CUP5", List.of(CarGroup.TCX));

        reviewRoles.add("Sichtung");

        permitRoles.addAll(basePermitMap.keySet());
        permitRoles.addAll(nosPermitMap.keySet());
        permitRoles.addAll(reviewRoles);
    }

    public Component getBasePermitBadge() {
        Optional<String> permitGroup = getBasePermitGroup();

        Span permitBadge = new Span();
        permitBadge.addClassNames("permit-badge");

        if (permitGroup.isEmpty()) {
            permitBadge.setText("No permit");
            permitBadge.getElement().getThemeList().add("badge primary error");
        } else if (inReview()) {
            permitBadge.setText("In review");
            permitBadge.setTitle("Participation in a sighting race pending");
            permitBadge.getElement().getThemeList().add("badge primary contrast");
        } else {
            permitBadge.setText(permitGroup.get());
            permitBadge.getElement().getThemeList().add("badge primary success");
        }

        return permitBadge;
    }

    public Optional<String> getBasePermitGroup() {
        Optional<Long> userId = securityService.getAuthenticatedUser()
                .flatMap(UserPrincipal::getUserId);

        if (userId.isEmpty()) {
            return Optional.empty();
        }

        Set<String> permits = getPermitsByUserId(userId.get());
        for (String permitRole : basePermitMap.keySet()) {
            if (permits.stream().anyMatch(permit -> permit.equals(permitRole))) {
                return Optional.of(permitRole);
            }
        }

        return Optional.empty();
    }

    public Set<String> getAllBasePermitGroups() {
        return new LinkedHashSet<>(basePermitMap.keySet());
    }

    public boolean hasBasePermitGroup() {
        if (inReview()) {
            return false;
        }

        return getBasePermitGroup().isPresent();
    }

    public Optional<List<CarGroup>> getBasePermittedCarGroups() {
        if (inReview()) {
            return Optional.empty();
        }

        return getBasePermitGroup().map(basePermitMap::get);
    }

    public List<Component> getNosPermitBadges() {
        Optional<Set<String>> permitGroups = getNosPermitGroups();

        List<Component> permitBadges = new ArrayList<>();
        if (!inReview() && permitGroups.isPresent()) {
            for (String permitGroup : permitGroups.get()) {
                Span permitBadge = new Span();
                permitBadge.addClassNames("permit-badge");
                permitBadge.setText(permitGroup);
                permitBadge.getElement().getThemeList().add("badge primary success");
                permitBadges.add(permitBadge);
            }
        }

        return permitBadges;
    }

    public Optional<Set<String>> getNosPermitGroups() {
        Optional<Long> userId = securityService.getAuthenticatedUser()
                .flatMap(UserPrincipal::getUserId);

        if (userId.isEmpty()) {
            return Optional.empty();
        }

        Set<String> permits = getPermitsByUserId(userId.get());
        Set<String> nosPermitGroups = new LinkedHashSet<>();
        for (String permitRole : nosPermitMap.keySet()) {
            if (permits.stream().anyMatch(permit -> permit.equals(permitRole))) {
                nosPermitGroups.add(permitRole);
            }
        }

        return Optional.of(nosPermitGroups);
    }

    public Optional<List<CarGroup>> getNosPermittedCarGroups() {
        Optional<Set<String>> nosPermitGroups = getNosPermitGroups();

        if (inReview() || nosPermitGroups.isEmpty()) {
            return Optional.empty();
        }

        List<CarGroup> permittedCarGroups = new ArrayList<>();
        for (String permitGroup : nosPermitGroups.get()) {
            permittedCarGroups.addAll(nosPermitMap.get(permitGroup));
        }

        return Optional.of(permittedCarGroups);
    }

    public boolean inReview() {
        Optional<Long> userId = securityService.getAuthenticatedUser()
                .flatMap(UserPrincipal::getUserId);

        if (userId.isEmpty()) {
            return false;
        }

        Set<String> permits = getPermitsByUserId(userId.get());
        return permits.stream().anyMatch(reviewRoles::contains);
    }

    public Set<String> getPermitsByUserId(Long userId) {
        DiscordUser discordUser = userService.findDiscordUserByUserId(userId);

        if (discordUser == null || discordUser.getPermits() == null) {
            return Collections.emptySet();
        }

        return discordUser.getPermits();
    }

    @Scheduled(fixedDelay = 15, initialDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void fetchPermits() {
        Instant start = Instant.now();

        List<MemberData> guildMembers = discordService.getMembersOfGuild();
        log.info("Starting permit synchronisation for " + guildMembers.size() + " guild members");

        Map<Long, String> guildRoles = discordService.getRolesOfGuildMap();

        for (MemberData member : guildMembers) {
            Set<String> permits = member.roles().stream()
                    .map(Id::asLong)
                    .map(guildRoles::get)
                    .filter(permitRoles::contains)
                    .collect(Collectors.toSet());

            DiscordUser user = DiscordUser.builder()
                    .userId(member.user().id().asLong())
                    .username(member.user().username())
                    .globalName(member.user().globalName().orElse(null))
                    .permits(permits)
                    .updateDatetime(Instant.now())
                    .build();
            userService.insertDiscordUser(user);
        }

        log.info(String.format("Finished permit synchronisation in %sms", Duration.between(start, Instant.now()).toMillis()));
    }
}
