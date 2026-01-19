package de.sustineo.simdesk.utils;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

public class VaadinUtils {
    public static boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        //TODO: use a parsing library like ua-parser/uap-java to parse the user agent from getUserAgent()
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }
}
