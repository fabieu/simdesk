
package de.sustineo.simdesk.client.protocol;

import de.sustineo.simdesk.client.protocol.enums.DriverCategory;
import de.sustineo.simdesk.client.protocol.enums.Nationality;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverInfo {
    @Builder.Default
    private String firstName = "";
    @Builder.Default
    private String lastName = "";
    @Builder.Default
    private String shortName = "";
    @Builder.Default
    private DriverCategory category = DriverCategory.ERROR;
    private Nationality driverNationality;
}

