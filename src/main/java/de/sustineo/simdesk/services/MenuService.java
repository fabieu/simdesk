package de.sustineo.simdesk.services;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.Reference;
import de.sustineo.simdesk.entities.auth.Role;
import de.sustineo.simdesk.entities.menu.MenuItem;
import de.sustineo.simdesk.entities.menu.MenuItemCategory;
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

    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(MenuItem.of(MenuItemCategory.MAIN, "Home", VaadinIcon.HOME, MainView.class));

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            items.add(MenuItem.of(MenuItemCategory.LEADERBOARD, "Lap Times", VaadinIcon.CLOCK, OverallLapTimesView.class));
            items.add(MenuItem.of(MenuItemCategory.LEADERBOARD, "Sessions", LumoIcon.ORDERED_LIST, SessionView.class));
        }

        if (ProfileManager.isBopProfileEnabled()) {
            items.add(MenuItem.of(MenuItemCategory.BALANCE_OF_PERFORMANCE, "Overview", VaadinIcon.EYE, BopDisplayView.class));

            if (securityService.hasAnyRole(Role.ADMIN, Role.BOP_MANAGER)) {
                items.add(MenuItem.of(MenuItemCategory.BALANCE_OF_PERFORMANCE, "Management", VaadinIcon.COG, BopManagementView.class));
            }

            items.add(MenuItem.of(MenuItemCategory.BALANCE_OF_PERFORMANCE, "Editor", VaadinIcon.SCALE, BopEditorView.class));
        }

        if (ProfileManager.isEntrylistProfileEnabled()) {
            items.add(MenuItem.of(MenuItemCategory.ENTRYLIST, "Validator", VaadinIcon.COG, EntrylistValidatorView.class));
        }

        items.add(MenuItem.of(MenuItemCategory.EXTERNAL_LINKS, "Feedback", VaadinIcon.CHAT, Reference.FEEDBACK));

        return items;
    }

    public Map<MenuItemCategory, List<MenuItem>> getItemsByCategory() {
        return getItems().stream().collect(Collectors.groupingBy(MenuItem::getCategory, LinkedHashMap::new, Collectors.toList()));
    }
}
