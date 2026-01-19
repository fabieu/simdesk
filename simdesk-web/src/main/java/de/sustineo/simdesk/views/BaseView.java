package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ScrollIntoViewOption;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class BaseView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver, HasDynamicTitle {
    public static final String QUERY_PARAMETER_TIME_RANGE = "timeRange";
    public static final String QUERY_PARAMETER_SIMULATION_ID = "simulationId";
    public static final String QUERY_PARAMETER_TRACK_ID = "trackId";
    public static final String QUERY_PARAMETER_CAR_ID = "carId";
    public static final String ROUTE_PARAMETER_CAR_ID = "carId";
    public static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    public static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    public static final String ROUTE_PARAMETER_FILE_CHECKSUM = "fileChecksum";
    public static final String ROUTE_PARAMETER_DRIVER_ID = "driverId";

    protected static final String TEXT_DELIMITER = " - ";
    protected static final String GRID_RANKING_WIDTH = "70px";

    protected RouteParameters routeParameters;
    protected QueryParameters queryParameters;

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // This method is called after navigation to the view.
        // No default behavior, can be overridden by subclasses
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // This method is called before entering the view.
        // No default behavior, can be overridden by subclasses
    }

    @Override
    public String getPageTitle() {
        // Default title, can be overridden by subclasses
        return "SimDesk";
    }

    protected Component createViewHeader() {
        return createViewHeader(getPageTitle());
    }

    protected Component createViewHeader(String heading, Component... components) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header");

        H2 headerText = new H2(heading);
        headerText.getStyle()
                .setColor("var(--lumo-header-text-color)")
                .setTextAlign(Style.TextAlign.CENTER)
                .setFontWeight(Style.FontWeight.BOLD);

        layout.add(headerText);
        layout.add(components);

        return layout;
    }

    /**
     * Updates the query parameters of the current view with the given time range.
     * Assign the full deep linking URL directly using History object: changes the URL in the browser but doesn't reload the page.
     */
    protected void updateQueryParameters(RouteParameters routeParameters, QueryParameters queryParameters) {
        String deepLinkingUrl = RouteConfiguration.forSessionScope().getUrl(getClass(), routeParameters);
        String deepLinkingUrlWithParam = UriComponentsBuilder.fromPath(deepLinkingUrl)
                .queryParams(new LinkedMultiValueMap<>(queryParameters.getParameters()))
                .toUriString();
        getUI().ifPresent(ui -> ui.getPage().getHistory().replaceState(null, deepLinkingUrlWithParam));
    }

    protected void scrollToComponent(Component component) {
        component.scrollIntoView(ScrollIntoViewOption.Behavior.SMOOTH);
    }
}
