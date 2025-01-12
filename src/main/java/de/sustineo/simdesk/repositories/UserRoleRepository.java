package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.auth.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    @Modifying
    @Query("update UserRole u set u.discordRoleId = :#{#userRole.discordRoleId} where u.name = :#{#userRole.name}")
    void update(UserRole userRole);
}
