package de.sustineo.simdesk.services.discord;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.auth.DiscordUser;
import de.sustineo.simdesk.services.UserService;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.MemberData;
import discord4j.rest.util.Color;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
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
    private final DiscordService discordService;
    private final UserService userService;
    private final Map<String, Set<CarGroup>> basePermitMap = new LinkedHashMap<>();
    private final Map<String, Set<CarGroup>> nosPermitMap = new LinkedHashMap<>();
    private final Set<String> reviewRoles = new HashSet<>();
    private final Set<String> permitRoles = new HashSet<>();
    private final String communityName;

    public PermitService(@Value("${simdesk.community.name}") String communityName,
                         DiscordService discordService,
                         UserService userService) {
        this.discordService = discordService;
        this.userService = userService;
        this.communityName = communityName;

        // Ensure that the permit maps are sorted by permit level from highest to lowest
        basePermitMap.put("Permit-A", new LinkedHashSet<>(Arrays.asList(CarGroup.GT3, CarGroup.GT2, CarGroup.GTC, CarGroup.GT4, CarGroup.TCX)));
        basePermitMap.put("Permit-B", new LinkedHashSet<>(Arrays.asList(CarGroup.GT4, CarGroup.TCX)));
        basePermitMap.put("Permit-C", new LinkedHashSet<>(List.of(CarGroup.TCX)));

        nosPermitMap.put("Permit-NOS-SP9", new LinkedHashSet<>(List.of(CarGroup.GT3)));
        nosPermitMap.put("Permit-NOS-SPX", new LinkedHashSet<>(List.of(CarGroup.GT2)));
        nosPermitMap.put("Permit-NOS-CUP2", new LinkedHashSet<>(List.of(CarGroup.GTC)));
        nosPermitMap.put("Permit-NOS-SP10", new LinkedHashSet<>(List.of(CarGroup.GT4)));
        nosPermitMap.put("Permit-NOS-CUP5", new LinkedHashSet<>(List.of(CarGroup.TCX)));

        reviewRoles.add("Sichtung");

        permitRoles.addAll(basePermitMap.keySet());
        permitRoles.addAll(nosPermitMap.keySet());
        permitRoles.addAll(reviewRoles);
    }

    public Set<String> getAvailableBasePermitGroups() {
        return new LinkedHashSet<>(basePermitMap.keySet());
    }

    public Optional<String> getBasePermitGroup(Long userId) {
        Set<String> permits = getPermitsByUserId(userId);
        for (String permitRole : basePermitMap.keySet()) {
            if (permits.stream().anyMatch(permit -> permit.equals(permitRole))) {
                return Optional.of(permitRole);
            }
        }

        return Optional.empty();
    }

    public boolean hasBasePermitGroup(Long userId) {
        if (inReview(userId)) {
            return false;
        }

        return getBasePermitGroup(userId).isPresent();
    }

    public Optional<Set<CarGroup>> getBasePermittedCarGroups(Long userId) {
        if (inReview(userId)) {
            return Optional.empty();
        }

        return getBasePermitGroup(userId).map(basePermitMap::get);
    }

    public Component getBasePermitBadge(Long userId) {
        Optional<String> permitGroup = getBasePermitGroup(userId);

        Span permitBadge = new Span();
        permitBadge.addClassNames("permit-badge");

        if (permitGroup.isEmpty()) {
            permitBadge.setText("No permit");
            permitBadge.getElement().getThemeList().add("badge primary error");
        } else if (inReview(userId)) {
            permitBadge.setText("In review");
            permitBadge.setTitle("Participation in a sighting race pending");
            permitBadge.getElement().getThemeList().add("badge primary contrast");
        } else {
            permitBadge.setText(permitGroup.get());
            permitBadge.getElement().getThemeList().add("badge primary success");
        }

        return permitBadge;
    }

    public List<Component> getNosPermitBadges(Long userId) {
        Optional<Set<String>> permitGroups = getNosPermitGroups(userId);

        List<Component> permitBadges = new ArrayList<>();
        if (!inReview(userId) && permitGroups.isPresent()) {
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

    public Optional<Set<String>> getNosPermitGroups(Long userId) {
        Set<String> permits = getPermitsByUserId(userId);
        Set<String> nosPermitGroups = new LinkedHashSet<>();
        for (String permitRole : nosPermitMap.keySet()) {
            if (permits.stream().anyMatch(permit -> permit.equals(permitRole))) {
                nosPermitGroups.add(permitRole);
            }
        }

        return Optional.of(nosPermitGroups);
    }

    public Optional<Set<CarGroup>> getNosPermittedCarGroups(Long userId) {
        Optional<Set<CarGroup>> basePermittedCarGroups = getBasePermittedCarGroups(userId);
        Optional<Set<String>> nosPermitGroups = getNosPermitGroups(userId);

        if (inReview(userId) || basePermittedCarGroups.isEmpty() || nosPermitGroups.isEmpty()) {
            return Optional.empty();
        }

        Set<CarGroup> permittedCarGroups = new LinkedHashSet<>();
        for (String permitGroup : nosPermitGroups.get()) {
            Set<CarGroup> nosPermittedCarGroups = nosPermitMap.get(permitGroup);
            if (nosPermittedCarGroups == null) {
                continue;
            }

            for (CarGroup nosCarGroup : nosPermittedCarGroups) {
                if (basePermittedCarGroups.get().contains(nosCarGroup)) {
                    permittedCarGroups.add(nosCarGroup);
                }
            }
        }

        return Optional.of(permittedCarGroups);
    }

    public boolean inReview(Long userId) {
        return getPermitsByUserId(userId).stream()
                .anyMatch(reviewRoles::contains);
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
                    .permits(permits.isEmpty() ? null : permits)
                    .updateDatetime(Instant.now())
                    .build();
            userService.insertDiscordUser(user);
        }

        log.info(String.format("Finished permit synchronisation in %sms", Duration.between(start, Instant.now()).toMillis()));
    }

    public EmbedCreateSpec createPermitEmbed(Long userId) {
        DiscordUser discordUser = userService.findDiscordUserByUserId(userId);
        if (discordUser == null) {
            return EmbedCreateSpec.builder()
                    .color(Color.RED)
                    .title(String.format("%s Permit Status", communityName))
                    .description("You are not registered in our system. Please contact an administrator.")
                    .timestamp(Instant.now())
                    .build();
        }

        Optional<String> basePermitGroup = getBasePermitGroup(discordUser.getUserId());
        Optional<Set<CarGroup>> basePermittedCarGroups = getBasePermittedCarGroups(discordUser.getUserId());
        Optional<Set<String>> nosPermitGroups = getNosPermitGroups(discordUser.getUserId());
        Optional<Set<CarGroup>> nosPermittedCarGroups = getNosPermittedCarGroups(discordUser.getUserId());
        boolean inReview = inReview(userId);

        String basePermitValue = basePermitGroup
                .map(group -> inReview ? group + " (In review)" : "✅ " + group)
                .orElse("");
        String baseCarGroupsValue = basePermittedCarGroups.stream()
                .flatMap(Set::stream)
                .map(group -> inReview ? "" : "✅ " + group.name())
                .collect(Collectors.joining("\n"));

        String nosPermitValue = nosPermitGroups.stream()
                .flatMap(Set::stream)
                .map(group -> inReview ? group + " (In review)" : "✅ " + group)
                .collect(Collectors.joining("\n"));
        String nosCarGroupsValue = nosPermittedCarGroups.stream()
                .flatMap(Set::stream)
                .map(group -> inReview ? "" : "✅ " + group.name())
                .collect(Collectors.joining("\n"));

        return EmbedCreateSpec.builder()
                .color(DiscordService.DEFAULT_EMBED_COLOR)
                .title(String.format("%s Permit Status", communityName))
                .description(String.format("Driver permit status for %s", DiscordService.getUserMention(Snowflake.of(userId))))
                .addField("Base permit", basePermitValue, true)
                .addField("NOS permits ", nosPermitValue, true)
                .addField("\u200B", "\u200B", false)
                .addField("Base car groups", baseCarGroupsValue, true)
                .addField("NOS car groups", nosCarGroupsValue, true)
                .footer("Last updated", null)
                .timestamp(discordUser.getUpdateDatetime())
                .build();
    }
}
