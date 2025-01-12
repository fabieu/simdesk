package de.sustineo.simdesk.entities.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role")
@Data
@NoArgsConstructor
public class UserRole {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String MANAGER = "ROLE_MANAGER";

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "discord_role_id")
    private String discordRoleId;

    /**
     * The Grid editor needs to know what has changed in order to close the right thing.
     * Make sure that equals and hashCode of <code>UserRole</code> uses unique attributes.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole userRole)) return false;

        return name.equals(userRole.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
