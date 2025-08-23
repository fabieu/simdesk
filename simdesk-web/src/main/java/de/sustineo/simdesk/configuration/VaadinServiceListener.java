package de.sustineo.simdesk.configuration;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import de.sustineo.simdesk.views.BrowserTimeZone;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaadinServiceListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(initEvent -> {
            BrowserTimeZone.init();
        });
    }
}
