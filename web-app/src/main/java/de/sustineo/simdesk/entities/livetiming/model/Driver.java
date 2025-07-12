package de.sustineo.simdesk.entities.livetiming.model;

import de.sustineo.simdesk.entities.livetiming.protocol.enums.DriverCategory;
import de.sustineo.simdesk.entities.livetiming.protocol.enums.Nationality;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Driver {
    public String firstName = "";
    public String lastName = "";
    public String shortName = "";
    public DriverCategory category = DriverCategory.ERROR;
    public Nationality nationality;

    public String fullName() {
        return firstName + " " + lastName;
    }

    public String truncatedName() {
        return firstName.substring(0, Math.min(firstName.length(), 1)) + ". " + lastName;
    }

    public synchronized Driver copy() {
        Driver driver = new Driver();
        driver.firstName = this.firstName;
        driver.lastName = this.lastName;
        driver.shortName = this.shortName;
        driver.category = this.category;
        driver.nationality = this.nationality;
        return driver;
    }
}
