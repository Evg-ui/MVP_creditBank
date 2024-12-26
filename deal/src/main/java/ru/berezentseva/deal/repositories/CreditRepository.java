package ru.berezentseva.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Statement;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditRepository extends JpaRepository<Credit, UUID> {
    Optional<Credit> findCreditByCreditUuid(UUID uuid);
}
