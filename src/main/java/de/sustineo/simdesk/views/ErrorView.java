package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.Reference;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.layouts.MainLayout;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.stream.Collectors;

@Log
@Route(layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Error")
@AnonymousAllowed
public class ErrorView extends VerticalLayout implements BeforeEnterObserver {
    public static final String QUERY_PARAMETER_HTTP_STATUS = "httpStatus";
    public static final String QUERY_PARAMETER_ERROR_MESSAGE = "errorMessage";

    public ErrorView() {
        setId("error-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    public Component createStatusCode(String httpStatus) {
        Paragraph paragraph = new Paragraph(httpStatus);
        paragraph.addClassNames("noselect");
        paragraph.getStyle()
                .setColor("var(--lumo-error-text-color)")
                .setFontSize("7rem");

        return paragraph;
    }

    public Component createErrorMessage(String errorMessage) {
        Span errorSpan = new Span("Error: " + errorMessage);
        errorSpan.getStyle()
                .setFontWeight(Style.FontWeight.BOLD)
                .setFontSize("1.5rem");

        return errorSpan;
    }

    private Component createSupportLink() {
        Anchor supportLink = new Anchor(Reference.FEEDBACK, "Contact support (via Discord)", AnchorTarget.BLANK);
        supportLink.addClassNames("button");
        return supportLink;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        QueryParameters queryParameters = beforeEnterEvent.getLocation().getQueryParameters();
        Map<String, String> parametersMap = queryParameters.getParameters().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.join(", ", entry.getValue())
                ));

        String httpStatus = parametersMap.getOrDefault(QUERY_PARAMETER_HTTP_STATUS, "500 - Internal Server Error");
        String errorMessage = parametersMap.getOrDefault(QUERY_PARAMETER_ERROR_MESSAGE, "Unknown error");

        add(createStatusCode(httpStatus));
        add(createErrorMessage(errorMessage));
        add(createSupportLink());
    }
}
