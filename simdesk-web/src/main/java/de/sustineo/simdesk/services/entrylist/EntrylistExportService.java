package de.sustineo.simdesk.services.entrylist;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylist;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylistEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@Service
@RequiredArgsConstructor
public class EntrylistExportService {
    private static final String CSV_HEADER = "Race Number,Drivers,Car Model,Grid Position,Ballast (kg),Restrictor (%),Custom Car,Override Driver Info,Server Admin\n";
    private static final String CSV_DELIMITER = ",";
    private static final String CSV_QUOTE = "\"";
    
    /**
     * Exports the entrylist to CSV format
     * @param entrylist The entrylist to export
     * @return CSV formatted string
     */
    public String exportToCsv(AccEntrylist entrylist) {
        if (entrylist == null || entrylist.getEntries() == null || entrylist.getEntries().isEmpty()) {
            return CSV_HEADER;
        }
        
        StringBuilder csv = new StringBuilder(CSV_HEADER);
        
        for (AccEntrylistEntry entry : entrylist.getEntries()) {
            csv.append(formatCsvRow(entry));
        }
        
        return csv.toString();
    }
    
    /**
     * Exports the entrylist to Markdown format
     * @param entrylist The entrylist to export
     * @return Markdown formatted string
     */
    public String exportToMarkdown(AccEntrylist entrylist) {
        if (entrylist == null || entrylist.getEntries() == null || entrylist.getEntries().isEmpty()) {
            return "# Entrylist\n\nNo entries available.";
        }
        
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Entrylist\n\n");
        markdown.append("| Race Number | Drivers | Car Model | Grid Position | Ballast (kg) | Restrictor (%) | Custom Car | Override Driver Info | Server Admin |\n");
        markdown.append("|-------------|---------|-----------|---------------|--------------|----------------|------------|---------------------|---------------|\n");
        
        for (AccEntrylistEntry entry : entrylist.getEntries()) {
            markdown.append(formatMarkdownRow(entry));
        }
        
        return markdown.toString();
    }
    
    private String formatCsvRow(AccEntrylistEntry entry) {
        StringBuilder row = new StringBuilder();
        
        // Race Number
        row.append(formatRaceNumber(entry.getRaceNumber())).append(CSV_DELIMITER);
        
        // Drivers
        row.append(CSV_QUOTE).append(formatDrivers(entry)).append(CSV_QUOTE).append(CSV_DELIMITER);
        
        // Car Model
        row.append(CSV_QUOTE).append(formatCarModel(entry.getForcedCarModel())).append(CSV_QUOTE).append(CSV_DELIMITER);
        
        // Grid Position
        row.append(formatGridPosition(entry.getDefaultGridPosition())).append(CSV_DELIMITER);
        
        // Ballast
        row.append(formatBallast(entry.getBallastKg())).append(CSV_DELIMITER);
        
        // Restrictor
        row.append(formatRestrictor(entry.getRestrictor())).append(CSV_DELIMITER);
        
        // Custom Car
        row.append(CSV_QUOTE).append(formatCustomCar(entry.getCustomCar())).append(CSV_QUOTE).append(CSV_DELIMITER);
        
        // Override Driver Info
        row.append(formatBoolean(entry.getOverrideDriverInfo())).append(CSV_DELIMITER);
        
        // Server Admin
        row.append(formatBoolean(entry.getIsServerAdmin()));
        
        row.append("\n");
        return row.toString();
    }
    
    private String formatMarkdownRow(AccEntrylistEntry entry) {
        StringBuilder row = new StringBuilder("| ");
        
        // Race Number
        row.append(formatRaceNumber(entry.getRaceNumber())).append(" | ");
        
        // Drivers
        row.append(formatDrivers(entry)).append(" | ");
        
        // Car Model
        row.append(formatCarModel(entry.getForcedCarModel())).append(" | ");
        
        // Grid Position
        row.append(formatGridPosition(entry.getDefaultGridPosition())).append(" | ");
        
        // Ballast
        row.append(formatBallast(entry.getBallastKg())).append(" | ");
        
        // Restrictor
        row.append(formatRestrictor(entry.getRestrictor())).append(" | ");
        
        // Custom Car
        row.append(formatCustomCar(entry.getCustomCar())).append(" | ");
        
        // Override Driver Info
        row.append(formatBoolean(entry.getOverrideDriverInfo())).append(" | ");
        
        // Server Admin
        row.append(formatBoolean(entry.getIsServerAdmin())).append(" |\n");
        
        return row.toString();
    }
    
    private String formatRaceNumber(Integer raceNumber) {
        if (raceNumber == null || raceNumber == AccEntrylistEntry.DEFAULT_RACE_NUMBER) {
            return "-";
        }
        return String.valueOf(raceNumber);
    }
    
    private String formatDrivers(AccEntrylistEntry entry) {
        if (entry.getDrivers() == null || entry.getDrivers().isEmpty()) {
            return "";
        }
        
        return entry.getDrivers().stream()
                .map(this::formatDriver)
                .collect(Collectors.joining("; "));
    }
    
    private String formatDriver(AccDriver driver) {
        StringBuilder driverStr = new StringBuilder();
        
        if (driver.getFirstName() != null && !driver.getFirstName().isEmpty()) {
            driverStr.append(driver.getFirstName()).append(" ");
        }
        if (driver.getLastName() != null && !driver.getLastName().isEmpty()) {
            driverStr.append(driver.getLastName());
        }
        
        String fullName = driverStr.toString().trim();
        if (fullName.isEmpty()) {
            fullName = driver.getPlayerId() != null ? driver.getPlayerId() : "Unknown";
        }
        
        return fullName;
    }
    
    private String formatCarModel(Integer carModelId) {
        if (carModelId == null || carModelId == AccEntrylistEntry.DEFAULT_FORCED_CAR_MODEL) {
            return "-";
        }
        
        AccCar car = AccCar.getCarById(carModelId);
        if (car != null) {
            return car.getModel();
        }
        
        return String.valueOf(carModelId);
    }
    
    private String formatGridPosition(Integer gridPosition) {
        if (gridPosition == null || gridPosition == AccEntrylistEntry.DEFAULT_DEFAULT_GRID_POSITION) {
            return "-";
        }
        return String.valueOf(gridPosition);
    }
    
    private String formatBallast(Integer ballastKg) {
        if (ballastKg == null) {
            return "0";
        }
        return String.valueOf(ballastKg);
    }
    
    private String formatRestrictor(Integer restrictor) {
        if (restrictor == null) {
            return "0";
        }
        return String.valueOf(restrictor);
    }
    
    private String formatCustomCar(String customCar) {
        if (customCar == null || customCar.isEmpty()) {
            return "";
        }
        return customCar;
    }
    
    private String formatBoolean(Integer value) {
        if (value == null || value == 0) {
            return "No";
        }
        return "Yes";
    }
}
