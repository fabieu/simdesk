package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.auth.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUserId(Long userId);
}
