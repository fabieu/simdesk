package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.StreamResource;
import de.sustineo.simdesk.configuration.Reference;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.utils.ApplicationContextProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Year;

public class BaseView extends VerticalLayout {
    protected static final String QUERY_PARAMETER_TIME_RANGE = "timeRange";
    protected static final String QUERY_PARAMETER_TRACK_ID = "track";
    protected static final String ROUTE_PARAMETER_CAR_ID = "carId";
    protected static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    protected static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    protected static final String ROUTE_PARAMETER_FILE_CHECKSUM = "fileChecksum";

    protected static final String TEXT_DELIMITER = " - ";
    protected static final String GRID_RANKING_WIDTH = "70px";

    private final BuildProperties buildProperties;

    public BaseView() {
        this.buildProperties = ApplicationContextProvider.getApplicationContext().getBean(BuildProperties.class);
    }

    protected Component createViewHeader() {
        return createViewHeader(getAnnotatedPageTitle());
    }

    protected Component createViewHeader(String heading) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header");

        H2 headerText = new H2(heading);
        headerText.getStyle()
                .setColor("var(--lumo-primary-text-color)")
                .setTextAlign(Style.TextAlign.CENTER)
                .setFontWeight(Style.FontWeight.BOLD);

        layout.add(headerText);

        return layout;
    }

    protected Component createFooter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("footer", "bg-light");

        Span version = new Span("Version " + buildProperties.getVersion());
        version.getElement().getThemeList().add("badge contrast");

        Text copyright = new Text("Copyright © 2022 - " + Year.now().getValue());
        Anchor creator = new Anchor(Reference.SUSTINEO, "Fabian Eulitz", AnchorTarget.BLANK);
        creator.getStyle()
                .setFontWeight(Style.FontWeight.BOLD);

        Anchor github = new Anchor(Reference.GITHUB);
        github.add(new FontIcon("fa-brands", "fa-github"));
        github.setTarget(AnchorTarget.BLANK);
        github.getStyle()
                .setColor("var(--lumo-body-text-color)");

        layout.add(copyright, creator, github, version);

        return layout;
    }

    protected AbstractIcon<?> getDownloadIcon() {
        return new Icon(VaadinIcon.CLOUD_DOWNLOAD_O);
    }

    protected AbstractIcon<?> getShareIcon() {
        return new Icon(VaadinIcon.SHARE_SQUARE);
    }

    protected Icon getWeatherIcon(Session session) {
        Icon icon;

        if (session.getWetSession()) {
            icon = VaadinIcon.DROP.create();
            icon.setColor("var(--weather-rainy-color)");
        } else {
            icon = VaadinIcon.SUN_O.create();
            icon.setColor("var(--weather-sunny-color)");
        }

        return icon;
    }

    protected Anchor createDownloadAnchor(StreamResource streamResource, String label) {
        Anchor anchor = new Anchor(streamResource, "");
        anchor.getElement().setAttribute("download", true);
        anchor.removeAll();
        anchor.add(new Button(label, getDownloadIcon()));
        return anchor;
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
