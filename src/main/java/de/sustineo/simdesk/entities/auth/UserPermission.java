package de.sustineo.simdesk.entities.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "user_permission")
@Data
@NoArgsConstructor
public class UserPermission {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_name")
    private String roleName;

    @CreationTimestamp
    @Column(name = "insert_datetime")
    private Instant insertDatetime;
}
