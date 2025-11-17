package de.sustineo.simdesk.services;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
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

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            items.add(MenuEntity.ofInternal(MenuEntityCategory.LEADERBOARD, "Lap Records", VaadinIcon.TROPHY, LeaderboardOverallLapTimesView.class));
            items.add(MenuEntity.ofInternal(MenuEntityCategory.LEADERBOARD, "Sessions", LumoIcon.UNORDERED_LIST, LeaderboardSessionsView.class));
        }

        if (ProfileManager.isEntrylistProfileEnabled()) {
            items.add(MenuEntity.ofInternal(MenuEntityCategory.ENTRYLIST, "Editor", VaadinIcon.CLIPBOARD_CHECK, EntrylistEditorView.class));
        }

        if (ProfileManager.isBopProfileEnabled()) {
            items.add(MenuEntity.ofInternal(MenuEntityCategory.BALANCE_OF_PERFORMANCE, "Overview", VaadinIcon.CHART_3D, BopDisplayView.class));

            if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
                items.add(MenuEntity.ofInternal(MenuEntityCategory.BALANCE_OF_PERFORMANCE, "Management", VaadinIcon.COG, BopManagementView.class));
            }

            items.add(MenuEntity.ofInternal(MenuEntityCategory.BALANCE_OF_PERFORMANCE, "Editor", VaadinIcon.SCALE, BopEditorView.class));
        }

        if (ProfileManager.isMapProfileEnabled()) {
            items.add(MenuEntity.ofInternal(MenuEntityCategory.MAP, "Map", VaadinIcon.MAP_MARKER, MapView.class));
        }

        return items;
    }

    public Map<MenuEntityCategory, List<MenuEntity>> getItemsByCategory() {
        return getItems().stream().collect(Collectors.groupingBy(MenuEntity::getCategory, LinkedHashMap::new, Collectors.toList()));
    }
}
