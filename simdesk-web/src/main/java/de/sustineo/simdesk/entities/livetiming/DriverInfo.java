
package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccDriverCategory;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccNationality;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverInfo {
    private String firstName;
    private String lastName;
    private String shortName;
    private AccDriverCategory category;
    private AccNationality driverNationality;
}

