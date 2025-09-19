package de.sustineo.simdesk.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.streams.DownloadHandler;
import org.springframework.stereotype.Service;

@Service
public class ComponentFactory {
    public AbstractIcon<?> getDownloadIcon() {
        return new Icon(VaadinIcon.CLOUD_DOWNLOAD_O);
    }

    public AbstractIcon<?> getUploadIcon() {
        return new Icon(VaadinIcon.CLOUD_UPLOAD_O);
    }

    public AbstractIcon<?> getShareIcon() {
        return new Icon(VaadinIcon.SHARE_SQUARE);
    }

    public AbstractIcon<?> getResetIcon() {
        return new Icon(VaadinIcon.ARROW_BACKWARD);
    }

    public AbstractIcon<?> getValidateIcon() {
        return new Icon(VaadinIcon.CLIPBOARD_CHECK);
    }

    public Anchor createDownloadAnchor(DownloadHandler downloadHandler, String label) {
        Anchor anchor = new Anchor(downloadHandler, "");
        anchor.removeAll();
        anchor.add(new Button(label, getDownloadIcon()));
        return anchor;
    }

    public Component createSpacer() {
        Hr hr = new Hr();
        hr.getStyle()
                .setMargin("var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)");

        return hr;
    }

    public Component createVerticalSpacer() {
        Div div = new Div();
        div.getStyle()
                .setWidth("1px")
                .setHeight("100%")
                .setBackgroundColor("var(--lumo-contrast-10pct)");
        return div;
    }
}
