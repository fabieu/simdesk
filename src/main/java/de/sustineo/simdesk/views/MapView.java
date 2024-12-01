package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.AccWeatherSettings;
import de.sustineo.simdesk.entities.weather.OpenWeatherCurrent;
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

import java.util.Optional;

@Profile(ProfileManager.PROFILE_MAP)
@Route(value = "/map")
@PageTitle("Map")
@AnonymousAllowed
public class MapView extends BaseView {
    private static final int MINIMUM_ZOOM_LEVEL = 3;

    private final WeatherService weatherService;

    public MapView(WeatherService weatherService) {
        this.weatherService = weatherService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createMap());
        add(createFooter());
    }

    private Component createMap() {
        // Create the registry which is needed so that components can be reused and their methods invoked
        // Note: You normally don't need to invoke any methods of the registry and just hand it over to the components
        final LComponentManagementRegistry reg = new LDefaultComponentManagementRegistry(this);

        // Create and add the MapContainer (which contains the map) to the UI
        final MapContainer mapContainer = new MapContainer(reg, new LMapOptions().withAttributionControl(false));
        mapContainer.setSizeFull();

        final LMap map = mapContainer.getlMap();
        // Set the lower limit for the available zoom levels
        map.setMinZoom(MINIMUM_ZOOM_LEVEL);

        // Configure attribution control
        String lastUpdate = weatherService.getLastUpdate() != null ? FormatUtils.formatDatetime(weatherService.getLastUpdate()) : "Never";
        String prefix = "Updated: %s | <a href=\"https://leafletjs.com\" target=\"_blank\">Leaflet</a>".formatted(lastUpdate);
        LControlAttribution controlAttribution = new LControlAttribution(reg, new LControlAttributionOptions().withPrefix(prefix));
        map.addControl(controlAttribution);

        // Configure layer control
        LControlLayers controlLayers = new LControlLayers(reg);
        map.addControl(controlLayers);

        // Add a (default) TileLayer so that we can see something on the map
        LTileLayer defaultLayer = LTileLayer.createDefaultForOpenStreetMapTileServer(reg);
        map.addLayer(defaultLayer);

        // Add a layer for all race track markers
        LLayerGroup trackLayerGroup = new LLayerGroup(reg);
        for (Track track : Track.getAllSortedByName()) {
            // Create a new marker for each track and add it to the map
            LMarker trackMarker = new LMarker(reg, new LLatLng(reg, track.getLatitude(), track.getLongitude()));

            if (!VaadinUtils.isMobileDevice()) {
                trackMarker.bindTooltip(track.getName());
            }

            Optional<AccWeatherSettings> weatherSettings = weatherService.getAccWeatherSettings(track, 24);
            if (weatherSettings.isPresent()) {
                String spacerHtml = ComponentUtils.createSpacer().getElement().getOuterHTML();

                OpenWeatherCurrent currentWeather = weatherSettings.get().getWeatherModel().getCurrent();
                Double currentTemperature = currentWeather.getTemperature();
                Double currentClouds = currentWeather.getClouds();
                Double currentPrecipitation = Optional.ofNullable(currentWeather.getRain())
                        .map(OpenWeatherPrecipitation::getPrecipitation)
                        .orElse(0.0);

                trackMarker.bindPopup("""
                        <h4 style="color: var(--lumo-primary-text-color)">%s</h4>
                        %s
                        <b>Temperature:</b> %.0f°C <br>
                        <b>Clouds:</b> %.0f%% <br>
                        <b>Precipitation:</b> %.1f mm/h <br>
                        %s
                        <b>ACC Temperature:</b> %d°C <br>
                        <b>ACC Cloud Level:</b> %.2f <br>
                        <b>ACC Rain Level </b> %.2f <br>
                        <b>ACC Randomness:</b> %d <br>
                        """
                        .formatted(
                                track.getName(),
                                spacerHtml,
                                currentTemperature,
                                currentClouds,
                                currentPrecipitation,
                                spacerHtml,
                                weatherSettings.get().getAmbientTemperature(),
                                weatherSettings.get().getCloudLevel(),
                                weatherSettings.get().getRainLevel(),
                                weatherSettings.get().getRandomness()
                        )
                );
            } else {
                trackMarker.bindPopup("""
                        <h4 style="color: var(--lumo-primary-text-color)">%s</h4>
                        """
                        .formatted(track.getName()));
            }

            trackMarker.addTo(trackLayerGroup);
        }
        map.addLayer(trackLayerGroup);
        controlLayers.addOverlay(trackLayerGroup, "Race tracks");

        weatherService.getTemperatureMapUrlTemplate().ifPresent(temperatureMapUrlTemplate -> {
            LTileLayer temperatureLayer = new LTileLayer(reg, temperatureMapUrlTemplate);
            controlLayers.addOverlay(temperatureLayer, "Temperature");
        });

        weatherService.getCloudsMapUrlTemplate().ifPresent(cloudsMapUrlTemplate -> {
            LTileLayer cloudsLayer = new LTileLayer(reg, cloudsMapUrlTemplate);
            controlLayers.addOverlay(cloudsLayer, "Clouds");
        });

        weatherService.getPrecipitationMapUrlTemplate().ifPresent(precipitationMapUrlTemplate -> {
            LTileLayer precipitationLayer = new LTileLayer(reg, precipitationMapUrlTemplate);
            controlLayers.addOverlay(precipitationLayer, "Precipitation");
        });

        // Set what part of the world should be shown
        map.setView(new LLatLng(reg, 20.558663, 9.412357), MINIMUM_ZOOM_LEVEL);

        return mapContainer;
    }
}
