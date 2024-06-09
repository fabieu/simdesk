package de.sustineo.simdesk.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class BaseView extends VerticalLayout {
    protected static final String QUERY_PARAMETER_TIME_RANGE = "timeRange";
    protected static final String QUERY_PARAMETER_TRACK_ID = "track";
    protected static final String ROUTE_PARAMETER_CAR_ID = "carId";
    protected static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    protected static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    protected static final String ROUTE_PARAMETER_FILE_CHECKSUM = "fileChecksum";

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
}
