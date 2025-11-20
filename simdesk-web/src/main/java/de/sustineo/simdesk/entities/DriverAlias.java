package de.sustineo.simdesk.entities;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverAlias {
    private Integer id;
    private String driverId;
    private String firstName;
    private String lastName;
    private Instant createdAt;

    public String getFullName() {
        if (firstName == null || lastName == null) {
            return "Unknown Driver";
        }
        return String.format("%s %s", firstName, lastName);
    }
}
