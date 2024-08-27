package de.sustineo.simdesk.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import de.sustineo.simdesk.views.enums.ThemeVariant;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeService {
    public static final String SESSION_ATTRIBUTE_THEME_LUMO = "vaadin.lumo.theme";

    public final ThemeVariant themeVariant;

    public ThemeService(@Value("${simdesk.theme}") String customThemeName) {
        this.themeVariant = EnumUtils.getEnumIgnoreCase(ThemeVariant.class, customThemeName, ThemeVariant.DEFAULT);
    }

    public void init() {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        themeList.clear();
        themeList.add(themeVariant.getAttribute());
        themeList.add(getCurrentLumoTheme());
    }

    public String getCurrentLumoTheme() {
        String lumoTheme = (String) VaadinSession.getCurrent().getAttribute(SESSION_ATTRIBUTE_THEME_LUMO);
        return lumoTheme != null ? lumoTheme : Lumo.DARK;
    }

    public void setLumoTheme(String lumoTheme) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        List.of(Lumo.DARK, Lumo.LIGHT).forEach(themeList::remove);
        themeList.add(lumoTheme);

        VaadinSession.getCurrent().setAttribute(SESSION_ATTRIBUTE_THEME_LUMO, lumoTheme);
    }
}