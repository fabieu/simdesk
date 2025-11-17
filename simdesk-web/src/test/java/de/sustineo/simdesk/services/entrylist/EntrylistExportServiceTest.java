package de.sustineo.simdesk.services.entrylist;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylist;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylistEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccDriverCategory;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccNationality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({ProfileManager.PROFILE_ENTRYLIST})
@SpringBootTest(classes = {
        EntrylistExportService.class,
        ProfileManager.class
})
class EntrylistExportServiceTest {
    @Autowired
    private EntrylistExportService exportService;

    private AccEntrylist entrylist;

    @BeforeEach
    void setUp() {
        entrylist = AccEntrylist.create();
        List<AccEntrylistEntry> entries = new ArrayList<>();

        // Entry 1: Complete entry with all fields
        AccEntrylistEntry entry1 = AccEntrylistEntry.create();
        entry1.setRaceNumber(42);
        entry1.setForcedCarModel(0); // Porsche 991 GT3 R
        entry1.setDefaultGridPosition(1);
        entry1.setBallastKg(10);
        entry1.setRestrictor(5);
        entry1.setCustomCar("custom_porsche");
        entry1.setOverrideDriverInfo(1);
        entry1.setIsServerAdmin(1);
        
        AccDriver driver1 = AccDriver.create();
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setShortName("JDO");
        driver1.setPlayerId("S76561198000000001");
        driver1.setDriverCategory(AccDriverCategory.GOLD);
        driver1.setNationality(AccNationality.USA);
        entry1.setDrivers(List.of(driver1));
        entries.add(entry1);

        // Entry 2: Entry with multiple drivers and default values
        AccEntrylistEntry entry2 = AccEntrylistEntry.create();
        entry2.setRaceNumber(99);
        entry2.setForcedCarModel(1); // Mercedes-AMG GT3
        entry2.setDefaultGridPosition(2);
        entry2.setBallastKg(0);
        entry2.setRestrictor(0);
        entry2.setCustomCar("");
        entry2.setOverrideDriverInfo(0);
        entry2.setIsServerAdmin(0);
        
        AccDriver driver2a = AccDriver.create();
        driver2a.setFirstName("Jane");
        driver2a.setLastName("Smith");
        driver2a.setShortName("JSM");
        driver2a.setPlayerId("S76561198000000002");
        
        AccDriver driver2b = AccDriver.create();
        driver2b.setFirstName("Bob");
        driver2b.setLastName("Johnson");
        driver2b.setShortName("BJO");
        driver2b.setPlayerId("S76561198000000003");
        
        entry2.setDrivers(List.of(driver2a, driver2b));
        entries.add(entry2);

        entrylist.setEntries(entries);
    }

    @Test
    void testExportToCsv_WithValidEntrylist() {
        String csv = exportService.exportToCsv(entrylist);

        assertThat(csv).isNotNull();
        assertThat(csv).contains("Race Number,Drivers,Car Model,Grid Position,Ballast (kg),Restrictor (%),Custom Car,Override Driver Info,Server Admin");
        assertThat(csv).contains("42,\"John Doe\",\"Porsche 991 GT3 R\",1,10,5,\"custom_porsche\",Yes,Yes");
        assertThat(csv).contains("99,\"Jane Smith; Bob Johnson\",\"Mercedes-AMG GT3\",2,0,0,\"\",No,No");
    }

    @Test
    void testExportToCsv_WithEmptyEntrylist() {
        AccEntrylist emptyEntrylist = AccEntrylist.create();
        emptyEntrylist.setEntries(new ArrayList<>());

        String csv = exportService.exportToCsv(emptyEntrylist);

        assertThat(csv).isNotNull();
        assertThat(csv).isEqualTo("Race Number,Drivers,Car Model,Grid Position,Ballast (kg),Restrictor (%),Custom Car,Override Driver Info,Server Admin\n");
    }

    @Test
    void testExportToCsv_WithNullEntrylist() {
        String csv = exportService.exportToCsv(null);

        assertThat(csv).isNotNull();
        assertThat(csv).isEqualTo("Race Number,Drivers,Car Model,Grid Position,Ballast (kg),Restrictor (%),Custom Car,Override Driver Info,Server Admin\n");
    }

    @Test
    void testExportToMarkdown_WithValidEntrylist() {
        String markdown = exportService.exportToMarkdown(entrylist);

        assertThat(markdown).isNotNull();
        assertThat(markdown).contains("# Entrylist");
        assertThat(markdown).contains("| Race Number | Drivers | Car Model | Grid Position | Ballast (kg) | Restrictor (%) | Custom Car | Override Driver Info | Server Admin |");
        assertThat(markdown).contains("|-------------|---------|-----------|---------------|--------------|----------------|------------|---------------------|---------------|");
        assertThat(markdown).contains("| 42 | John Doe | Porsche 991 GT3 R | 1 | 10 | 5 | custom_porsche | Yes | Yes |");
        assertThat(markdown).contains("| 99 | Jane Smith; Bob Johnson | Mercedes-AMG GT3 | 2 | 0 | 0 |  | No | No |");
    }

    @Test
    void testExportToMarkdown_WithEmptyEntrylist() {
        AccEntrylist emptyEntrylist = AccEntrylist.create();
        emptyEntrylist.setEntries(new ArrayList<>());

        String markdown = exportService.exportToMarkdown(emptyEntrylist);

        assertThat(markdown).isNotNull();
        assertThat(markdown).isEqualTo("# Entrylist\n\nNo entries available.");
    }

    @Test
    void testExportToMarkdown_WithNullEntrylist() {
        String markdown = exportService.exportToMarkdown(null);

        assertThat(markdown).isNotNull();
        assertThat(markdown).isEqualTo("# Entrylist\n\nNo entries available.");
    }

    @Test
    void testExportToCsv_WithDefaultValues() {
        AccEntrylist entrylistWithDefaults = AccEntrylist.create();
        AccEntrylistEntry entry = AccEntrylistEntry.create();
        entry.setRaceNumber(AccEntrylistEntry.DEFAULT_RACE_NUMBER);
        entry.setForcedCarModel(AccEntrylistEntry.DEFAULT_FORCED_CAR_MODEL);
        entry.setDefaultGridPosition(AccEntrylistEntry.DEFAULT_DEFAULT_GRID_POSITION);
        entry.setBallastKg(AccEntrylistEntry.DEFAULT_BALLAST_KG);
        entry.setRestrictor(AccEntrylistEntry.DEFAULT_RESTRICTOR);
        entry.setCustomCar(AccEntrylistEntry.DEFAULT_CUSTOM_CAR);
        entry.setOverrideDriverInfo(AccEntrylistEntry.DEFAULT_OVERRIDE_DRIVER_INFO);
        entry.setIsServerAdmin(AccEntrylistEntry.DEFAULT_IS_SERVER_ADMIN);
        
        AccDriver driver = AccDriver.create();
        driver.setPlayerId("S76561198000000001");
        entry.setDrivers(List.of(driver));
        
        entrylistWithDefaults.setEntries(List.of(entry));

        String csv = exportService.exportToCsv(entrylistWithDefaults);

        assertThat(csv).contains("-,\"S76561198000000001\",\"-\",-,0,0,\"\",No,No");
    }

    @Test
    void testExportToCsv_WithDriversWithoutNames() {
        AccEntrylist entrylistWithUnnamedDrivers = AccEntrylist.create();
        AccEntrylistEntry entry = AccEntrylistEntry.create();
        entry.setRaceNumber(1);
        
        AccDriver driver = AccDriver.create();
        driver.setPlayerId("S76561198000000001");
        entry.setDrivers(List.of(driver));
        
        entrylistWithUnnamedDrivers.setEntries(List.of(entry));

        String csv = exportService.exportToCsv(entrylistWithUnnamedDrivers);

        assertThat(csv).contains("1,\"S76561198000000001\"");
    }
}
