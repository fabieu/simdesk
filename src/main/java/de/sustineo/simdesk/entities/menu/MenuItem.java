package de.sustineo.simdesk.entities.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class MenuItem {
    private MenuItemType type;
    private MenuItemCategory category;
    private String name;
    @Getter(AccessLevel.NONE)
    private IconFactory iconFactory;
    private Class<? extends Component> navigationTarget;
    private String href;

    public Icon getIcon() {
        return iconFactory.create();
    }

    public MenuItemType getType() {
        if (ObjectUtils.allNotNull(navigationTarget, href)) {
            throw new IllegalStateException("MenuItem cannot have both navigation target and href");
        }

        if (navigationTarget != null) {
            return MenuItemType.INTERNAL;
        }

        if (href != null) {
            return MenuItemType.EXTERNAL;
        }

        return MenuItemType.UNDEFINED;
    }

    public static MenuItem of(MenuItemCategory category, String name, IconFactory iconFactory, Class<? extends Component> navigationTarget) {
        return MenuItem.builder()
                .category(category)
                .name(name)
                .iconFactory(iconFactory)
                .navigationTarget(navigationTarget)
                .build();
    }

    public static MenuItem of(MenuItemCategory category, String name, IconFactory iconFactory, String href) {
        return MenuItem.builder()
                .category(category)
                .name(name)
                .iconFactory(iconFactory)
                .href(href)
                .build();
    }
}
