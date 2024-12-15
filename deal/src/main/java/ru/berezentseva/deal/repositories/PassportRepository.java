package ru.berezentseva.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Passport;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<Passport, UUID> {
    Optional<Passport> findStatementByPassportUuid(UUID uuid);
}
