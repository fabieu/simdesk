package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.leaderboard.SessionFileService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.ButtonComponentFactory;
import de.sustineo.simdesk.views.components.SessionComponentFactory;
import de.sustineo.simdesk.views.enums.TimeRange;
import de.sustineo.simdesk.views.filter.GridFilter;
import de.sustineo.simdesk.views.filter.SessionFilter;
import de.sustineo.simdesk.views.i18n.UploadI18NDefaults;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions")
@PageTitle("Leaderboard - Sessions")
@AnonymousAllowed
public class LeaderboardSessionsView extends BaseView implements BeforeEnterObserver, AfterNavigationObserver {
    private final SessionService sessionService;
    private final SecurityService securityService;
    private final SessionFileService sessionFileService;
    private final NotificationService notificationService;

    private final ButtonComponentFactory buttonComponentFactory;
    private final SessionComponentFactory sessionComponentFactory;

    private Grid<Session> sessionGrid;
    private TimeRange timeRange = TimeRange.LAST_MONTH;
    private RouteParameters routeParameters;
    private QueryParameters queryParameters;

    public LeaderboardSessionsView(SessionService sessionService,
                                   SecurityService securityService,
                                   SessionFileService sessionFileService,
                                   NotificationService notificationService,
                                   ButtonComponentFactory buttonComponentFactory,
                                   SessionComponentFactory sessionComponentFactory) {
        this.sessionService = sessionService;
        this.securityService = securityService;
        this.sessionFileService = sessionFileService;
        this.notificationService = notificationService;
        this.buttonComponentFactory = buttonComponentFactory;
        this.sessionComponentFactory = sessionComponentFactory;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        Optional<String> timeRange = queryParameters.getSingleParameter(QUERY_PARAMETER_TIME_RANGE);
        if (timeRange.isPresent() && EnumUtils.isValidEnumIgnoreCase(TimeRange.class, timeRange.get())) {
            this.timeRange = EnumUtils.getEnumIgnoreCase(TimeRange.class, timeRange.get());
        }

        this.sessionGrid = createSessionGrid(this.timeRange);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        removeAll();

        add(createViewHeader());
        add(createSelectHeader(this.timeRange));
        addAndExpand(sessionGrid);
        add(createFooter());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, this.timeRange.name().toLowerCase()));
    }

    private Component createSelectHeader(TimeRange timeRange) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header");
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.getStyle()
                .setPaddingTop("0")
                .setMarginBottom("0");

        Select<TimeRange> timeRangeSelect = new Select<>();
        timeRangeSelect.setItems(TimeRange.values());
        timeRangeSelect.setValue(timeRange);
        timeRangeSelect.addComponents(TimeRange.LAST_WEEK, ComponentUtils.createSpacer());
        timeRangeSelect.setItemLabelGenerator(TimeRange::getDescription);
        timeRangeSelect.addValueChangeListener(event -> {
            replaceSessionGrid(event.getValue());
            updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, event.getValue().name().toLowerCase()));
        });

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_MANAGER)) {
            Button uploadSessionButton = new Button("Upload sessions");
            uploadSessionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            uploadSessionButton.addClickListener(event -> createUploadSessionDialog().open());

            layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            layout.add(uploadSessionButton);
        }

        layout.add(timeRangeSelect);
        return layout;
    }

    private Dialog createUploadSessionDialog() {
        Dialog dialog = new Dialog("Manual session upload");

        MultiFileBuffer multiFileBuffer = new MultiFileBuffer();

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setLabel("Session datetime");
        dateTimePicker.setRequiredIndicatorVisible(true);
        dateTimePicker.setValue(ZonedDateTime.now(BrowserTimeZone.get()).toLocalDateTime());
        dateTimePicker.setMax(ZonedDateTime.now(BrowserTimeZone.get()).toLocalDateTime());
        dateTimePicker.setStep(Duration.ofMinutes(15));
        dateTimePicker.setI18n(new DateTimePicker.DateTimePickerI18n()
                .setRequiredErrorMessage("Field is required")
                .setBadInputErrorMessage("Invalid date or time format")
                .setMinErrorMessage("Too early, choose another date and time")
                .setMaxErrorMessage("Too late, choose another date and time"));
        dateTimePicker.setDatePickerI18n(new DatePicker.DatePickerI18n()
                .setDateFormat(FormatUtils.DATE_FORMAT));
        dateTimePicker.getStyle()
                .setMarginBottom("var(--lumo-space-m)");

        Upload upload = new Upload();
        upload.setI18n(configureUploadI18N());
        upload.setReceiver(multiFileBuffer);
        upload.setDropAllowed(true);
        upload.setAcceptedFileTypes(MediaType.APPLICATION_JSON_VALUE);
        upload.setMaxFileSize((int) (25 * FileUtils.ONE_MB));
        upload.addSucceededListener(event -> {
            FileData fileData = multiFileBuffer.getFileData(event.getFileName());

            try {
                Path file = fileData.getFile().getAbsoluteFile().toPath();
                Instant sessionDatetime = ZonedDateTime.of(dateTimePicker.getValue(), BrowserTimeZone.get()).toInstant();

                sessionFileService.handleSessionFileWithSessionDatetimeOverride(file, sessionDatetime);

                notificationService.showSuccessNotification(String.format("%s - Session upload successfully", fileData.getFileName()));
            } catch (Exception e) {
                log.log(Level.SEVERE, String.format("Could not upload session file %s", fileData.getFileName()), e);
                notificationService.showErrorNotification(String.format("%s - Session upload failed", fileData.getFileName()));
            } finally {
                boolean deleted = fileData.getFile().delete();
                log.fine(String.format("Deleted temporary file %s: %s", fileData.getFile().getAbsolutePath(), deleted));
            }
        });
        upload.addFileRejectedListener(event -> notificationService.showErrorNotification(Duration.ZERO, event.getErrorMessage()));
        upload.addFailedListener(event -> notificationService.showErrorNotification(event.getReason().getMessage()));

        Paragraph description = new Paragraph("Use the upload form to manually upload one or more session files. The files must conform to the session file format (.json). The maximum file size is 25 MB.");

        dialog.add(description, dateTimePicker, upload);
        dialog.getFooter().add(buttonComponentFactory.createDialogCancelButton(dialog));

        return dialog;
    }

    private UploadI18N configureUploadI18N() {
        UploadI18NDefaults i18n = new UploadI18NDefaults();
        i18n.getAddFiles().setMany("Upload session files...");
        i18n.getDropFiles().setMany("Drop session files here");
        i18n.getError().setIncorrectFileType("The provided file does not have the correct format (.json)");
        i18n.getError().setFileIsTooBig("The provided file is too big. Maximum file size is 25 MB");
        return i18n;
    }

    private Grid<Session> createSessionGrid(TimeRange timeRange) {
        List<Session> sessions = sessionService.getAllBySessionTimeRange(timeRange);

        Grid<Session> grid = new Grid<>(Session.class, false);
        Grid.Column<Session> weatherColumn = grid.addComponentColumn(sessionComponentFactory::getWeatherIcon)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Session> carCountColumn = grid.addColumn(Session::getCarCount)
                .setHeader("Cars")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true);
        Grid.Column<Session> sessionTypeColumn = grid.addColumn(session -> session.getSessionType().getDescription())
                .setHeader("Session")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Session> trackNameColumn = grid.addColumn(Session::getTrackName)
                .setHeader("Track Name")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Session> serverNameColumn = grid.addColumn(Session::getServerName)
                .setHeader("Server Name")
                .setSortable(true)
                .setTooltipGenerator(Session::getServerName);
        Grid.Column<Session> sessionDatetimeColumn = grid.addColumn(session -> FormatUtils.formatDatetime(session.getSessionDatetime()))
                .setHeader("Session Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Session::getSessionDatetime);

        GridListDataView<Session> dataView = grid.setItems(sessions);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        SessionFilter sessionFilter = new SessionFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(serverNameColumn).setComponent(GridFilter.createHeader(sessionFilter::setServerName));
        headerRow.getCell(trackNameColumn).setComponent(GridFilter.createHeader(sessionFilter::setTrackName));
        headerRow.getCell(sessionTypeColumn).setComponent(GridFilter.createHeader(sessionFilter::setSessionDescription));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<Session>, Session> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            Session selectedSession = e.getValue();

            if (selectedSession != null) {
                getUI().ifPresent(ui -> ui.navigate(LeaderboardSessionDetailsView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_FILE_CHECKSUM, selectedSession.getFileChecksum())
                        )
                ));
            }
        });

        return grid;
    }

    private void replaceSessionGrid(TimeRange timeRange) {
        Grid<Session> grid = createSessionGrid(timeRange);
        replace(this.sessionGrid, grid);
        this.sessionGrid = grid;
    }
}
