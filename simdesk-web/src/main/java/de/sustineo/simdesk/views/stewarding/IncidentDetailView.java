package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.*;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.stewarding.*;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/series/:seriesId/rounds/:roundId/incidents/:incidentId", layout = MainLayout.class)
@AnonymousAllowed
public class IncidentDetailView extends BaseView {
    private final StewardingIncidentService incidentService;
    private final StewardDecisionService decisionService;
    private final StewardingAppealService appealService;
    private final PenaltyCatalogService catalogService;
    private final ReasoningTemplateService templateService;
    private final SeriesService seriesService;
    private final RoundService roundService;
    private final StewardingEntrylistService entrylistService;
    private final SecurityService securityService;
    private final NotificationService notificationService;
    private String seriesId;
    private String roundId;
    private String incidentId;

    public IncidentDetailView(StewardingIncidentService incidentService, StewardDecisionService decisionService,
                              StewardingAppealService appealService, PenaltyCatalogService catalogService,
                              ReasoningTemplateService templateService, SeriesService seriesService,
                              RoundService roundService, StewardingEntrylistService entrylistService,
                              SecurityService securityService, NotificationService notificationService) {
        this.incidentService = incidentService;
        this.decisionService = decisionService;
        this.appealService = appealService;
        this.catalogService = catalogService;
        this.templateService = templateService;
        this.seriesService = seriesService;
        this.roundService = roundService;
        this.entrylistService = entrylistService;
        this.securityService = securityService;
        this.notificationService = notificationService;
    }

    @Override
    public String getPageTitle() {
        return "Incident Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        String seriesIdParam = event.getRouteParameters().get("seriesId").orElse(null);
        String roundIdParam = event.getRouteParameters().get("roundId").orElse(null);
        String incidentIdParam = event.getRouteParameters().get("incidentId").orElse(null);
        if (seriesIdParam == null || roundIdParam == null || incidentIdParam == null) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        seriesId = seriesIdParam;
        roundId = roundIdParam;
        incidentId = incidentIdParam;

        Series series = seriesService.getSeriesById(seriesId);
        Round round = roundService.getRoundById(roundId);
        Incident incident = incidentService.getIncidentById(incidentId);
        if (series == null || round == null || incident == null) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        add(createViewHeader(incident.getTitle()));

        Button backButton = new Button("← Back to " + round.getTitle(), e ->
                getUI().ifPresent(ui -> ui.navigate(RoundDetailView.class,
                        new RouteParameters(
                                new RouteParam("seriesId", String.valueOf(seriesId)),
                                new RouteParam("roundId", String.valueOf(roundId))
                        ))));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backButton);

        // Incident details
        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(true);
        detailsLayout.setSpacing(false);

        if (incident.getDescription() != null && !incident.getDescription().isEmpty()) {
            detailsLayout.add(new Span(incident.getDescription()));
        }
        if (incident.getLap() != null) {
            detailsLayout.add(createDetailRow("Lap", String.valueOf(incident.getLap())));
        }
        if (incident.getTimestampInSession() != null) {
            detailsLayout.add(createDetailRow("Time in Session", incident.getTimestampInSession()));
        }
        if (incident.getMapMarkerX() != null && incident.getMapMarkerY() != null) {
            detailsLayout.add(createDetailRow("Map Location", String.format("X: %.2f, Y: %.2f", incident.getMapMarkerX(), incident.getMapMarkerY())));
        }
        if (incident.getInvolvedCarsText() != null) {
            detailsLayout.add(createDetailRow("Involved Cars", incident.getInvolvedCarsText()));
        }
        if (incident.getVideoUrl() != null && !incident.getVideoUrl().isEmpty()) {
            Anchor videoLink = new Anchor(incident.getVideoUrl(), "Video Evidence");
            videoLink.setTarget("_blank");
            HorizontalLayout videoRow = new HorizontalLayout();
            Span videoLabel = new Span("Evidence: ");
            videoLabel.getStyle().set("font-weight", "bold");
            videoRow.setSpacing(false);
            videoRow.getStyle().set("gap", "var(--lumo-space-xs)");
            videoRow.add(videoLabel, videoLink);
            detailsLayout.add(videoRow);
        }
        detailsLayout.add(createDetailRow("Status", incident.getStatus() != null ? incident.getStatus().getDescription() : "-"));
        add(detailsLayout);

        // Steward Decision section (ADMIN only)
        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            add(createDecisionSection(incident, series));
        }

        // Decision History
        add(createDecisionHistorySection(incidentId));

        // Appeals section
        add(createAppealsSection(incidentId));
    }

    private VerticalLayout createDecisionSection(Incident incident, Series series) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.add(new H3("Steward Decision"));

        StewardDecision activeDecision = decisionService.getActiveDecisionByIncidentId(incident.getId());
        if (activeDecision != null) {
            layout.add(new Paragraph("Penalty: " + (activeDecision.getCustomPenalty() != null ? activeDecision.getCustomPenalty() : "-")));
            layout.add(new Paragraph("Reasoning: " + (activeDecision.getReasoning() != null ? activeDecision.getReasoning() : "-")));
            layout.add(new Paragraph("No Further Action: " + (Boolean.TRUE.equals(activeDecision.getIsNoAction()) ? "Yes" : "No")));

            Button reviseButton = new Button("Revise Decision");
            reviseButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            reviseButton.addClickListener(e -> {
                layout.removeAll();
                layout.add(new H3("Revise Decision"));
                layout.add(createDecisionForm(incident, series, activeDecision.getId()));
            });
            layout.add(reviseButton);
        } else {
            layout.add(createDecisionForm(incident, series, null));
        }

        return layout;
    }

    private FormLayout createDecisionForm(Incident incident, Series series, String existingDecisionId) {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        RoundSession session = roundService.getSessionById(incident.getSessionId());

        ComboBox<PenaltyDefinition> penaltyCombo = new ComboBox<>("Penalty");
        if (series.getPenaltyCatalogId() != null && session != null && session.getSessionType() != null) {
            List<PenaltyDefinition> definitions = catalogService.getDefinitionsForSessionType(
                    series.getPenaltyCatalogId(), session.getSessionType().name());
            penaltyCombo.setItems(definitions);
            penaltyCombo.setItemLabelGenerator(d -> d.getCode() + " - " + d.getName());
        }

        TextField customPenaltyField = new TextField("Custom Penalty");
        customPenaltyField.setWidthFull();

        TextArea reasoningField = new TextArea("Reasoning");
        reasoningField.setWidthFull();

        ComboBox<ReasoningTemplate> templateCombo = new ComboBox<>("Reasoning Template");
        templateCombo.setItems(templateService.getAllTemplates());
        templateCombo.setItemLabelGenerator(ReasoningTemplate::getName);
        templateCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                reasoningField.setValue(e.getValue().getTemplateText());
            }
        });

        TextField penalizedCarField = new TextField("Penalized Car");
        penalizedCarField.setWidthFull();

        Checkbox noActionCheckbox = new Checkbox("No Further Action");

        form.add(penaltyCombo, customPenaltyField, templateCombo, reasoningField, penalizedCarField, noActionCheckbox);

        Button saveButton = new Button("Save Decision", e -> {
            StewardDecision decision = StewardDecision.builder()
                    .incidentId(incident.getId())
                    .sessionId(incident.getSessionId())
                    .penaltyDefinitionId(penaltyCombo.getValue() != null ? penaltyCombo.getValue().getId() : null)
                    .customPenalty(customPenaltyField.getValue())
                    .reasoning(reasoningField.getValue())
                    .reasoningTemplateId(templateCombo.getValue() != null ? templateCombo.getValue().getId() : null)
                    .isNoAction(noActionCheckbox.getValue())
                    .penalizedCarText(penalizedCarField.getValue())
                    .isActive(true)
                    .build();

            if (existingDecisionId != null) {
                decisionService.reviseDecision(existingDecisionId, decision);
            } else {
                decisionService.makeDecision(decision);
            }

            notificationService.showSuccessNotification("Decision saved");
            getUI().ifPresent(ui -> ui.navigate(IncidentDetailView.class,
                    new RouteParameters(new RouteParam("seriesId", seriesId), new RouteParam("roundId", roundId), new RouteParam("incidentId", incidentId))));
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        form.add(saveButton);

        return form;
    }

    private VerticalLayout createDecisionHistorySection(String incidentId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.add(new H3("Decision History"));

        List<StewardDecision> history = decisionService.getDecisionHistory(incidentId);
        if (history.isEmpty()) {
            layout.add(new Paragraph("No decisions yet."));
            return layout;
        }

        for (StewardDecision decision : history) {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(Alignment.CENTER);

            Span status = new Span(Boolean.TRUE.equals(decision.getIsActive()) ? "ACTIVE" : "SUPERSEDED");
            Span penalty = new Span(decision.getCustomPenalty() != null ? decision.getCustomPenalty() : "-");
            Span reasoning = new Span(decision.getReasoning() != null ? decision.getReasoning() : "-");
            Span decidedAt = new Span(decision.getDecidedAt() != null ? decision.getDecidedAt().toString() : "-");

            row.add(status, penalty, reasoning, decidedAt);
            layout.add(row);
        }

        return layout;
    }

    private VerticalLayout createAppealsSection(String incidentId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.add(new H3("Appeals"));

        StewardDecision activeDecision = decisionService.getActiveDecisionByIncidentId(incidentId);
        if (activeDecision == null) {
            layout.add(new Paragraph("No active decision to appeal."));
            return layout;
        }

        List<Appeal> appeals = appealService.getAppealsByDecisionId(activeDecision.getId());
        if (appeals.isEmpty()) {
            layout.add(new Paragraph("No appeals filed."));
        } else {
            Grid<Appeal> grid = new Grid<>(Appeal.class, false);
            grid.addColumn(Appeal::getReason).setHeader("Reason").setAutoWidth(true);
            grid.addColumn(appeal -> appeal.getStatus() != null ? appeal.getStatus().getDescription() : "-")
                    .setHeader("Status").setAutoWidth(true);
            grid.addColumn(appeal -> appeal.getResponse() != null ? appeal.getResponse() : "-")
                    .setHeader("Response").setAutoWidth(true);
            grid.addColumn(Appeal::getFiledAt).setHeader("Filed At").setAutoWidth(true);
            grid.setItems(appeals);
            layout.add(grid);
        }

        return layout;
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        Span labelSpan = new Span(label + ": ");
        labelSpan.getStyle().set("font-weight", "bold");
        Span valueSpan = new Span(value);
        HorizontalLayout row = new HorizontalLayout(labelSpan, valueSpan);
        row.setSpacing(false);
        row.getStyle().set("gap", "var(--lumo-space-xs)");
        return row;
    }
}
