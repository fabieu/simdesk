package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;
import de.sustineo.simdesk.configuration.Reference;
import de.sustineo.simdesk.utils.ApplicationContextProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class BaseView extends VerticalLayout {
    public static final String QUERY_PARAMETER_TIME_RANGE = "timeRange";
    public static final String QUERY_PARAMETER_TRACK_ID = "track";
    public static final String ROUTE_PARAMETER_CAR_ID = "carId";
    public static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    public static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    public static final String ROUTE_PARAMETER_FILE_CHECKSUM = "fileChecksum";
    public static final String ROUTE_PARAMETER_DRIVER_ID = "driverId";

    protected static final String TEXT_DELIMITER = " - ";
    protected static final String GRID_RANKING_WIDTH = "70px";

    private final BuildProperties buildProperties;
    private final ScrollOptions defaultScrollOptions = new ScrollOptions(ScrollOptions.Behavior.SMOOTH);

    public BaseView() {
        this.buildProperties = ApplicationContextProvider.getApplicationContext().getBean(BuildProperties.class);
    }

    public void scrollToComponent(Component component) {
        component.scrollIntoView(defaultScrollOptions);
    }

    protected Component createViewHeader() {
        return createViewHeader(getAnnotatedPageTitle());
    }

    protected Component createViewHeader(String heading) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header");

        H2 headerText = new H2(heading);
        headerText.getStyle()
                .setColor("var(--lumo-header-text-color)")
                .setTextAlign(Style.TextAlign.CENTER)
                .setFontWeight(Style.FontWeight.BOLD);

        layout.add(headerText);

        return layout;
    }

    protected Component createFooter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("footer", "bg-light");

        // Version badge + reference to GitHub release
        String version = buildProperties.getVersion();
        Span versionBadge = new Span("Version " + version);
        versionBadge.getElement().getThemeList().add("badge contrast");
        Anchor versionAnchor = new Anchor(Reference.GITHUB_RELEASES);
        versionAnchor.add(versionBadge);
        versionAnchor.setTarget(AnchorTarget.BLANK);

        // Maintainer
        Span maintainerSpan = new Span(new Text("Created by Fabian Eulitz"));
        maintainerSpan.getStyle()
                .setColor("var(--lumo-tertiary-text-color)");

        // GitHub icon + reference to GitHub repository
        Anchor githubAnchor = new Anchor(Reference.GITHUB);
        githubAnchor.add(new FontIcon("fa-brands", "fa-github"));
        githubAnchor.setTarget(AnchorTarget.BLANK);
        githubAnchor.getStyle()
                .setColor("var(--lumo-body-text-color)");

        layout.add(githubAnchor, maintainerSpan, versionAnchor);
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

    protected String getAnnotatedPageTitle() {
        return this.getClass().getAnnotation(PageTitle.class).value();
    }
}
