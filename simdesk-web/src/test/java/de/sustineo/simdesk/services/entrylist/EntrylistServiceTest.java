package de.sustineo.simdesk.services.entrylist;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.CustomCar;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylist;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylistEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({SpringProfile.ENTRYLIST})
@SpringBootTest(classes = {
        EntrylistService.class,
        SpringProfile.class
})
class EntrylistServiceTest {
    @Autowired
    private EntrylistService entrylistService;

    private AccEntrylist entrylist;

    @BeforeEach
    void setUp() {
        // Create a sample entrylist with two entries
        entrylist = AccEntrylist.create();
        List<AccEntrylistEntry> entries = new ArrayList<>();

        // Entry 1: Race number 1 with existing custom car value
        AccEntrylistEntry entry1 = AccEntrylistEntry.create();
        entry1.setRaceNumber(1);
        entry1.setCustomCar("old_custom_car_1");
        entry1.setOverrideCarModelForCustomCar(0);
        entry1.setDrivers(List.of(AccDriver.create()));
        entries.add(entry1);

        // Entry 2: Race number 2 with existing custom car value
        AccEntrylistEntry entry2 = AccEntrylistEntry.create();
        entry2.setRaceNumber(2);
        entry2.setCustomCar("old_custom_car_2");
        entry2.setOverrideCarModelForCustomCar(1);
        entry2.setDrivers(List.of(AccDriver.create()));
        entries.add(entry2);

        // Entry 3: Race number 3 without custom car value
        AccEntrylistEntry entry3 = AccEntrylistEntry.create();
        entry3.setRaceNumber(3);
        entry3.setCustomCar("");
        entry3.setDrivers(List.of(AccDriver.create()));
        entries.add(entry3);

        entrylist.setEntries(entries);
    }

    @Test
    void testUpdateFromCustomCars_OverwritesExistingValues() {
        // Create custom cars array with new values
        CustomCar customCar1 = new CustomCar();
        customCar1.setCarNumber(1);
        customCar1.setCustomCar("new_custom_car_1");
        customCar1.setOverrideCarModelForCustomCar(true);

        CustomCar customCar2 = new CustomCar();
        customCar2.setCarNumber(2);
        customCar2.setCustomCar("new_custom_car_2");
        customCar2.setOverrideCarModelForCustomCar(false);

        CustomCar[] customCars = new CustomCar[]{customCar1, customCar2};

        // Update entrylist with new custom cars
        entrylistService.updateFromCustomCars(entrylist, customCars);

        // Verify that existing custom car values were overwritten
        AccEntrylistEntry entry1 = entrylist.getEntries().get(0);
        assertThat(entry1.getCustomCar()).isEqualTo("new_custom_car_1");
        assertThat(entry1.getOverrideCarModelForCustomCar()).isEqualTo(1);

        AccEntrylistEntry entry2 = entrylist.getEntries().get(1);
        assertThat(entry2.getCustomCar()).isEqualTo("new_custom_car_2");
        assertThat(entry2.getOverrideCarModelForCustomCar()).isEqualTo(0);

        // Entry 3 should remain unchanged (no matching custom car)
        AccEntrylistEntry entry3 = entrylist.getEntries().get(2);
        assertThat(entry3.getCustomCar()).isEmpty();
    }

    @Test
    void testUpdateFromCustomCars_UpdatesEmptyValues() {
        // Create custom car for entry 3 which has empty custom car
        CustomCar customCar3 = new CustomCar();
        customCar3.setCarNumber(3);
        customCar3.setCustomCar("new_custom_car_3");
        customCar3.setOverrideCarModelForCustomCar(true);

        CustomCar[] customCars = new CustomCar[]{customCar3};

        // Update entrylist
        entrylistService.updateFromCustomCars(entrylist, customCars);

        // Verify that empty custom car was updated
        AccEntrylistEntry entry3 = entrylist.getEntries().get(2);
        assertThat(entry3.getCustomCar()).isEqualTo("new_custom_car_3");
        assertThat(entry3.getOverrideCarModelForCustomCar()).isEqualTo(1);
    }

    @Test
    void testUpdateFromCustomCars_HandlesNonMatchingCarNumbers() {
        // Create custom car with non-matching car number
        CustomCar customCar = new CustomCar();
        customCar.setCarNumber(999);
        customCar.setCustomCar("new_custom_car_999");
        customCar.setOverrideCarModelForCustomCar(true);

        CustomCar[] customCars = new CustomCar[]{customCar};

        // Update entrylist
        entrylistService.updateFromCustomCars(entrylist, customCars);

        // Verify that no entries were updated
        assertThat(entrylist.getEntries().get(0).getCustomCar()).isEqualTo("old_custom_car_1");
        assertThat(entrylist.getEntries().get(1).getCustomCar()).isEqualTo("old_custom_car_2");
        assertThat(entrylist.getEntries().get(2).getCustomCar()).isEmpty();
    }

    @Test
    void testUpdateFromCustomCars_HandlesNullOverrideValue() {
        // Create custom car with null override value
        CustomCar customCar = new CustomCar();
        customCar.setCarNumber(1);
        customCar.setCustomCar("new_custom_car_1");
        customCar.setOverrideCarModelForCustomCar(null);

        CustomCar[] customCars = new CustomCar[]{customCar};

        // Store original override value
        Integer originalOverride = entrylist.getEntries().get(0).getOverrideCarModelForCustomCar();

        // Update entrylist
        entrylistService.updateFromCustomCars(entrylist, customCars);

        // Verify that custom car was updated but override value remained unchanged
        AccEntrylistEntry entry1 = entrylist.getEntries().get(0);
        assertThat(entry1.getCustomCar()).isEqualTo("new_custom_car_1");
        assertThat(entry1.getOverrideCarModelForCustomCar()).isEqualTo(originalOverride);
    }
}
