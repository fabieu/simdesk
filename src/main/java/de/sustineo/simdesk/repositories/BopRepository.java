package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.Bop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BopRepository extends JpaRepository<Bop, Bop.BopId> {
    List<Bop> findByActive(boolean isActive);
}
