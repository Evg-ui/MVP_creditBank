package ru.berezentseva.deal.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
//import org.hibernate.annotations.TypeDef;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;

import java.sql.Timestamp;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
//@TypeDef(name = "json", typeClass = JsonType.class)
public class Statement {

    public void setStatementId(UUID statementId) {
        this.statementId = UUID.randomUUID();
    }

    @Id
    @GeneratedValue//(strategy = GenerationType.IDENTITY)
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)  // FK
    private Client clientUuid;

    @OneToOne
    @JoinColumn(name = "credit_id", nullable = true)  // FK
    private Credit creditUuid;

   // @OneToOne
    @JoinColumn(name = "status", nullable  = true, unique = false)
    private ApplicationStatus status;

    @Column(name = "creation_date", nullable = false, unique = false)
    private Timestamp creationDate;


    // @Type(type = "jsonb")
    @Column(name = "applied_offer",  nullable = true, columnDefinition ="jsonb")
    private String appliedOffer;

    @Column(name = "sign_date", nullable = true, unique = false)
    private Timestamp signDate;

    @Column(name = "ses_code", nullable = true, unique = false)
    private String sesCode;

   // @Type(type = "jsonb")
//    @OneToOne
//    @JoinColumn(name = "status_history", nullable = true, unique = false)
    @Column(name = "status_history", nullable = true, unique = false) // времеенно, потому что ничего непонятно тут
    private String statusHistory;

    //    // Метод для получения JsonNode
//    @Transient
//    public JsonNode getAppliedOfferAsJsonNode() {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            return mapper.readTree(appliedOffer);
//        } catch (Exception e) {
//            throw new RuntimeException("Ошибка при преобразовании appliedOffer в JsonNode", e);
//        }
//    }
//
//    // Метод для установки JsonNode
    public void setAppliedOfferFromJsonNode(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.appliedOffer = mapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при преобразовании JsonNode в строку", e);
        }
}

  //    private static final ObjectMapper objectMapper = new ObjectMapper();

//    public void setAppliedOffer(LoanOfferDto offer) {
//        try {
//            this.appliedOffer = objectMapper.writeValueAsString(offer);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Обработка ошибок
//        }
//    }
//
//    public LoanOfferDto getAppliedOffer() {
//        try {
//            return objectMapper.readValue(this.appliedOffer, LoanOfferDto.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Обработка ошибок
//            return null;
//        }
//    }

    @PrePersist
    public void prePersist() {
        if (this.appliedOffer == null || this.appliedOffer.isEmpty()) {
            this.appliedOffer = "{}"; // Устанавливаем пустой JSON объект по умолчанию
        }
    }


}
