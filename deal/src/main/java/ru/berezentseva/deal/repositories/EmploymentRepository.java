package ru.berezentseva.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Employment;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, UUID> {
    Optional<Employment> findEmploymentByEmploymentUuid(UUID uuid);
}
