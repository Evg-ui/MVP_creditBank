package ru.berezentseva.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Statement;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository <Statement, UUID>{
    Optional<Statement> findStatementByStatementId(UUID uuid);
    Optional<Statement> findStatementByClientUuid(Client clientUuid);
  //  Optional<Statement> findStatementByClientUuidAndStatementId(Client clientUuid, UUID uuid);
}
