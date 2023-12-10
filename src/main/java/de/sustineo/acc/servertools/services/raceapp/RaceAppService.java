package de.sustineo.acc.servertools.services.raceapp;

import com.opencsv.CSVWriter;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppDriver;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppEventBooking;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppEventWrapper;
import de.sustineo.acc.servertools.entities.raceapp.RaceAppSeries;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Profile(ProfileManager.PROFILE_RACEAPP)
@Service
@Log
public class RaceAppService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://raceapp.eu/api";
    private static final String SERIES_ENDPOINT_TEMPLATE = BASE_URL + "/series/%s";
    private static final String EVENT_ENDPOINT_TEMPLATE = BASE_URL + "/event/%s";

    public RaceAppSeries fetchSeries(int seriesId) {
        ResponseEntity<RaceAppSeries> response = restTemplate.getForEntity(String.format(SERIES_ENDPOINT_TEMPLATE, seriesId), RaceAppSeries.class);
        return response.getBody();
    }

    public RaceAppEventWrapper fetchEvent(int eventId) {
        ResponseEntity<RaceAppEventWrapper> response = restTemplate.getForEntity(String.format(EVENT_ENDPOINT_TEMPLATE, eventId), RaceAppEventWrapper.class);
        return response.getBody();
    }

    public File getEventBookingsFile(int eventId) {
        RaceAppEventWrapper event = fetchEvent(eventId);

        if (event == null) {
            log.warning("Event with ID " + eventId + " could not be fetched from RaceApp API!");
            return null;
        }

        List<String[]> lines = new ArrayList<>();
        int maxDriversCount = event.getEvent().getEventBookings().stream()
                .mapToInt(booking -> booking.getDrivers() == null ? 0 : booking.getDrivers().size())
                .max()
                .orElse(0);

        for (RaceAppEventBooking booking : event.getEvent().getEventBookings()) {
            List<String> bookingLine = getBookingLine(booking, maxDriversCount);

            lines.add(bookingLine.toArray(new String[0]));
        }


        try {
            File file = File.createTempFile("raceapp_event_bookings_" + eventId, ".csv");
            CSVWriter writer = new CSVWriter(new FileWriter(file, StandardCharsets.UTF_8), ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(lines);
            writer.close();

            return file;
        } catch (IOException e) {
            log.severe("An error occurred during creation of CSV resource: " + e.getMessage());
            return null;
        }
    }

    private static List<String> getBookingLine(RaceAppEventBooking booking, int maxDriversCount) {
        List<String> bookingLine = new ArrayList<>();
        bookingLine.add(StringUtils.defaultString(booking.getCarNumber()));
        bookingLine.add(StringUtils.defaultString(booking.getCarClass()));
        bookingLine.add(StringUtils.defaultString(booking.getCarModel()));
        bookingLine.add(StringUtils.defaultString(booking.getCarName()));
        bookingLine.add(StringUtils.defaultString(booking.getTeamName()));


        int driverCount = 0;
        for (RaceAppDriver driver : booking.getDrivers()) {
            bookingLine.add(driver.getName());
            driverCount++;
        }

        for (int i = driverCount; i < maxDriversCount; i++) {
            bookingLine.add("");
        }

        return bookingLine;
    }
}
