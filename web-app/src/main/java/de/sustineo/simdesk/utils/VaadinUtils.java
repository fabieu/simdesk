package de.sustineo.simdesk.utils;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

public class VaadinUtils {
    public static boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }
}
