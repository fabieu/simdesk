package de.sustineo.simdesk.services;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.Reference;
import de.sustineo.simdesk.entities.auth.Role;
import de.sustineo.simdesk.entities.menu.MenuEntity;
import de.sustineo.simdesk.entities.menu.MenuEntityCategory;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.views.*;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Getter
public class MenuService {
    private final SecurityService securityService;

    public MenuService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public List<MenuEntity> getItems() {
        List<MenuEntity> items = new ArrayList<>();
        items.add(MenuEntity.of(MenuEntityCategory.MAIN, "Home", VaadinIcon.HOME, MainView.class));

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            items.add(MenuEntity.of(MenuEntityCategory.LEADERBOARD, "Lap Times", VaadinIcon.CLOCK, OverallLapTimesView.class));
            items.add(MenuEntity.of(MenuEntityCategory.LEADERBOARD, "Sessions", LumoIcon.UNORDERED_LIST, SessionView.class));
        }

        if (ProfileManager.isBopProfileEnabled()) {
            items.add(MenuEntity.of(MenuEntityCategory.BALANCE_OF_PERFORMANCE, "Overview", VaadinIcon.CHART_3D, BopDisplayView.class));

            if (securityService.hasAnyRole(Role.ADMIN, Role.BOP_MANAGER)) {
                items.add(MenuEntity.of(MenuEntityCategory.BALANCE_OF_PERFORMANCE, "Management", VaadinIcon.COG, BopManagementView.class));
            }
        }

        if (ProfileManager.isDiscordProfileEnabled()) {
            items.add(MenuEntity.of(MenuEntityCategory.PERMIT, "My Permit", VaadinIcon.USER_CHECK, PermitUserView.class));
        }


        if (ProfileManager.isEntrylistProfileEnabled()) {
            items.add(MenuEntity.of(MenuEntityCategory.TOOLS, "Entrylist Validator", VaadinIcon.CLIPBOARD_CHECK, EntrylistValidatorView.class));
        }

        if (ProfileManager.isBopProfileEnabled()) {
            items.add(MenuEntity.of(MenuEntityCategory.TOOLS, "BoP Editor", VaadinIcon.SCALE, BopEditorView.class));
        }

        items.add(MenuEntity.of(MenuEntityCategory.EXTERNAL_LINKS, "Feedback", VaadinIcon.CHAT, Reference.FEEDBACK));

        return items;
    }

    public Map<MenuEntityCategory, List<MenuEntity>> getItemsByCategory() {
        return getItems().stream().collect(Collectors.groupingBy(MenuEntity::getCategory, LinkedHashMap::new, Collectors.toList()));
    }
}
