package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.weather.OpenWeatherModel;
import de.sustineo.simdesk.entities.weather.OpenWeatherPrecipitation;
import de.sustineo.simdesk.services.weather.WeatherService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.utils.VaadinUtils;
import org.springframework.context.annotation.Profile;
import software.xdev.vaadin.maps.leaflet.MapContainer;
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng;
import software.xdev.vaadin.maps.leaflet.controls.LControlAttribution;
import software.xdev.vaadin.maps.leaflet.controls.LControlAttributionOptions;
import software.xdev.vaadin.maps.leaflet.controls.LControlLayers;
import software.xdev.vaadin.maps.leaflet.layer.LLayerGroup;
import software.xdev.vaadin.maps.leaflet.layer.raster.LTileLayer;
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarker;
import software.xdev.vaadin.maps.leaflet.map.LMap;
import software.xdev.vaadin.maps.leaflet.map.LMapOptions;
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry;
import software.xdev.vaadin.maps.leaflet.registry.LDefaultComponentManagementRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Profile(ProfileManager.PROFILE_MAP)
@Route(value = "/map")
@AnonymousAllowed
public class MapView extends BaseView {
    private static final int MINIMUM_ZOOM_LEVEL = 3;
    private static final int DEFAULT_RACE_HOURS = 24;

    private final WeatherService weatherService;

    // Create the registry which is needed so that components can be reused and their methods invoked
    // Note: You normally don't need to invoke any methods of the registry and just hand it over to the components
    private final LComponentManagementRegistry registry = new LDefaultComponentManagementRegistry(this);
    private final HashMap<Track, LMarker> trackMarkers = new HashMap<>();

    public MapView(WeatherService weatherService) {
        this.weatherService = weatherService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        add(createMapHeader());
        addAndExpand(createMap());

        updateWeatherMarkers(DEFAULT_RACE_HOURS);
    }

    @Override
    public String getPageTitle() {
        return "Map";
    }

    private Component createMapHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setJustifyContentMode(JustifyContentMode.START);
        layout.setAlignItems(Alignment.CENTER);

        Span title = new Span("Race duration (hours):");

        IntegerField raceHoursField = new IntegerField(event -> updateWeatherMarkers(event.getValue()));
        raceHoursField.setValue(DEFAULT_RACE_HOURS);
        raceHoursField.setMin(0);
        raceHoursField.setMax(48);
        raceHoursField.setStepButtonsVisible(true);
        raceHoursField.setStep(1);

        layout.add(title, raceHoursField);
        return layout;
    }

    private Component createMap() {
        // Create and add the MapContainer (which contains the map) to the UI
        final MapContainer mapContainer = new MapContainer(registry, new LMapOptions().withAttributionControl(false));
        mapContainer.setSizeFull();

        final LMap map = mapContainer.getlMap();
        // Set the lower limit for the available zoom levels
        map.setMinZoom(MINIMUM_ZOOM_LEVEL);

        // Configure attribution control
        String lastUpdate = weatherService.getLastUpdate() != null ? FormatUtils.formatDatetime(weatherService.getLastUpdate()) : "Never";
        String prefix = "Updated: %s | <a href=\"https://leafletjs.com\" target=\"_blank\">Leaflet</a>".formatted(lastUpdate);
        LControlAttribution controlAttribution = new LControlAttribution(registry, new LControlAttributionOptions().withPrefix(prefix));
        map.addControl(controlAttribution);

        // Configure layer control
        LControlLayers controlLayers = new LControlLayers(registry);
        map.addControl(controlLayers);

        // Add a (default) TileLayer so that we can see something on the map
        LTileLayer defaultLayer = LTileLayer.createDefaultForOpenStreetMapTileServer(registry);
        map.addLayer(defaultLayer);

        // Add a layer for all race track markers
        LLayerGroup trackLayerGroup = new LLayerGroup(registry);
        for (Track track : Track.getAllSortedByNameForAcc()) {
            // Create a new marker for each track and add it to the map
            LMarker trackMarker = new LMarker(registry, new LLatLng(registry, track.getLatitude(), track.getLongitude()));

            if (!VaadinUtils.isMobileDevice()) {
                trackMarker.bindTooltip(track.getName());
            }

            trackMarker.bindPopup("<h4 style=\"color: var(--lumo-header-text-color)\">%s</h4>".formatted(track.getName()));
            trackMarker.addTo(trackLayerGroup);
            trackMarkers.put(track, trackMarker);
        }

        map.addLayer(trackLayerGroup);
        controlLayers.addOverlay(trackLayerGroup, "Race tracks");

        weatherService.getOpenWeatherTemperatureMapUrlTemplate().ifPresent(temperatureMapUrlTemplate -> {
            LTileLayer temperatureLayer = new LTileLayer(registry, temperatureMapUrlTemplate);
            controlLayers.addOverlay(temperatureLayer, "Temperature");
        });

        weatherService.getOpenWeatherCloudsMapUrlTemplate().ifPresent(cloudsMapUrlTemplate -> {
            LTileLayer cloudsLayer = new LTileLayer(registry, cloudsMapUrlTemplate);
            controlLayers.addOverlay(cloudsLayer, "Clouds");
        });

        weatherService.getOpenWeatherPrecipitationMapUrlTemplate().ifPresent(precipitationMapUrlTemplate -> {
            LTileLayer precipitationLayer = new LTileLayer(registry, precipitationMapUrlTemplate);
            controlLayers.addOverlay(precipitationLayer, "Precipitation");
        });

        // Set what part of the world should be shown
        map.setView(new LLatLng(registry, 20.558663, 9.412357), MINIMUM_ZOOM_LEVEL);

        return mapContainer;
    }

    public void updateWeatherMarkers(int raceHours) {
        for (Map.Entry<Track, LMarker> entry : trackMarkers.entrySet()) {
            Track track = entry.getKey();
            LMarker trackMarker = entry.getValue();

            String spacerHtml = ComponentUtils.createSpacer().getElement().getOuterHTML();

            StringBuilder trackMarkerTooltip = new StringBuilder("""
                    <h3 style="color: var(--lumo-header-text-color)">%s</h3>
                    """
                    .formatted(
                            track.getName()
                    ));

            Optional<OpenWeatherModel> weatherModel = weatherService.getOpenWeatherModel(track);
            if (weatherModel.isPresent()) {
                trackMarkerTooltip.append("""
                        %s
                        <h5 style="color: var(--lumo-header-text-color)">Current weather</h5>
                        <b>Temperature:</b> %.0f°C <br>
                        <b>Clouds:</b> %.0f%% <br>
                        <b>Precipitation:</b> %.1f mm/h <br>
                        """
                        .formatted(
                                spacerHtml,
                                weatherModel.get().getCurrent().getTemperature(),
                                weatherModel.get().getCurrent().getClouds(),
                                Optional.ofNullable(weatherModel.get().getCurrent().getRain())
                                        .map(OpenWeatherPrecipitation::getPrecipitation)
                                        .orElse(0.0)
                        )
                );

                AccWeatherSettings accWeatherSettings = weatherService.getAccWeatherSettings(weatherModel.get(), raceHours);
                trackMarkerTooltip.append("""
                    %s
                    <h5 style="color: var(--lumo-header-text-color)">ACC Weather Settings - %s hour(s)</h5>
                    <b>Temperature:</b> %d°C <br>
                    <b>Cloud Level:</b> %.2f <br>
                    <b>Rain Level:</b> %.2f <br>
                    <b>Randomness:</b> %d <br>
                    """
                        .formatted(
                                spacerHtml,
                                raceHours,
                                accWeatherSettings.getAmbientTemperature(),
                                accWeatherSettings.getCloudLevel(),
                                accWeatherSettings.getRainLevel(),
                                accWeatherSettings.getRandomness()
                        ));
            }

            trackMarker.closePopup();
            trackMarker.bindPopup(trackMarkerTooltip.toString());
        }
    }
}
