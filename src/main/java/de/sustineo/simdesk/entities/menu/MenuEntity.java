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
public class MenuEntity {
    private MenuEntityType type;
    private MenuEntityCategory category;
    private String name;
    @Getter(AccessLevel.NONE)
    private IconFactory iconFactory;
    private Class<? extends Component> navigationTarget;
    private String href;

    public Icon getIcon() {
        return iconFactory.create();
    }

    public MenuEntityType getType() {
        if (ObjectUtils.allNotNull(navigationTarget, href)) {
            throw new IllegalStateException("MenuEntity cannot have both navigation target and href");
        }

        if (navigationTarget != null) {
            return MenuEntityType.INTERNAL;
        }

        if (href != null) {
            return MenuEntityType.EXTERNAL;
        }

        return MenuEntityType.UNDEFINED;
    }

    public static MenuEntity of(MenuEntityCategory category, String name, IconFactory iconFactory, Class<? extends Component> navigationTarget) {
        return MenuEntity.builder()
                .category(category)
                .name(name)
                .iconFactory(iconFactory)
                .navigationTarget(navigationTarget)
                .build();
    }

    public static MenuEntity of(MenuEntityCategory category, String name, IconFactory iconFactory, String href) {
        return MenuEntity.builder()
                .category(category)
                .name(name)
                .iconFactory(iconFactory)
                .href(href)
                .build();
    }
}
