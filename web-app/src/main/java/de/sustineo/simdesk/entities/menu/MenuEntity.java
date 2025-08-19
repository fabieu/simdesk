package de.sustineo.simdesk.entities.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuEntity {
    private final MenuEntityCategory category;
    private final String name;
    private final Icon icon;
    private final NavigationTarget navigationTarget;

    public static MenuEntity ofInternal(MenuEntityCategory category, String name, IconFactory icon, Class<? extends Component> view) {
        return new MenuEntity(category, name, icon.create(), new InternalLink(view));
    }

    public static MenuEntity ofExternal(MenuEntityCategory category, String name, IconFactory icon, String href) {
        return new MenuEntity(category, name, icon.create(), new ExternalLink(href));
    }
}
