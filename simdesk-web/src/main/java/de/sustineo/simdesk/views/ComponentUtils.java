package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import org.springframework.stereotype.Service;

@Service
public class ComponentUtils {
    public static Component createSpacer() {
        Hr hr = new Hr();
        hr.getStyle()
                .setMargin("var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)");

        return hr;
    }

    public static Component createVerticalSpacer() {
        Div div = new Div();
        div.getStyle()
                .setWidth("1px")
                .setHeight("100%")
                .setBackgroundColor("var(--lumo-contrast-10pct)");
        return div;
    }
}
