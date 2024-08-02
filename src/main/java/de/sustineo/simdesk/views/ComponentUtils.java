package de.sustineo.simdesk.views;

import com.vaadin.flow.component.html.Hr;
import org.springframework.stereotype.Service;

@Service
public class ComponentUtils {
    public static Hr createSpacer() {
        String color = "var(--lumo-contrast-10pct)";

        Hr hr = new Hr();
        hr.getStyle()
                .setMargin("var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2)")
                .set("border-top", "1px solid " + color);

        return hr;
    }
}
