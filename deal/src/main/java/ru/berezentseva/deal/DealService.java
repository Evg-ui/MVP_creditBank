package ru.berezentseva.deal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.berezentseva.calculator.DTO.CreditDto;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.DTO.ScoringDataDto;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.ClientRepository;
import ru.berezentseva.deal.repositories.CreditRepository;
import ru.berezentseva.deal.repositories.StatementRepository;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Transactional
@Service
@Slf4j
//@Component
public class DealService {


    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;

    @Autowired // Ensure this is annotated appropriately
    public DealService(RestTemplate restTemplate, ClientRepository clientRepository, StatementRepository statementRepository, CreditRepository creditRepository) { //Constructor
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.statementRepository = statementRepository;
        this.creditRepository = creditRepository;
    }

    @Transactional
    public List<LoanOfferDto> createApplication(LoanStatementRequestDto request){
        // try catch добавить
        log.info("Received request into createApp: {}", request);
        log.info("Создание нового клиента");
        Client client = new Client();
        client.setClientUuid(UUID.randomUUID());
        client.setLastName(request.getLastName());
        log.info("LastName {}", request.getLastName());
        client.setFirstName(request.getFirstName());
        log.info("FirstName {}", request.getFirstName());
        client.setMiddleName(request.getMiddleName());
        log.info("MiddleName {}", request.getMiddleName());
        client.setBirthDate(request.getBirthDate());
        log.info("BirthDate {}", request.getBirthDate().toString());
        client.setEmail(request.getEmail());
        log.info("Email {}", request.getEmail());
        log.info("Сохраняем клиента");
        client = clientRepository.save(client);
        log.info("UUID клиента {}", client.getClientUuid().toString());
        log.info("Клиент создан");

        log.info("Создание новой заявки");
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClientUuid(client);
        log.info("ClientId {}", statement.getClientUuid().toString());
        statement.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        log.info("Creation_date {}", statement.getCreationDate().toString());
       // statement.setAppliedOffer("{}");
//        ObjectMapper objectMapper = new ObjectMapper();
//        statement.setAppliedOffer(objectMapper.createObjectNode().toString()); // Это важно!
        log.info("Сохраняем заявку");
        statement.prePersist();
        log.info("Applied Offer: {}", statement.getAppliedOffer());
      //  statement = statementRepository.save(statement);
        log.info("UUID заявки {}", statement.getStatementId().toString());
        log.info("Заявка создана");

//         Отправка запроса на /calculator/offers.
//          try catch надо
        log.info("Отправляем запрос в /calculator/offers");

        String baseUrl = "http://localhost:8080"; // Replace with your actual base URL
        String offersPath = "/calculator/offers"; // Replace if needed
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl).path(offersPath).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoanStatementRequestDto> entity = new HttpEntity<>(request, headers);
        ResponseEntity<LoanOfferDto[]> response;

        try {
            response = restTemplate.exchange(uri, HttpMethod.POST, entity, LoanOfferDto[].class);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при вызове API: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении предложений: " + e.getStatusCode());
        }

        List<LoanOfferDto> offers  = List.of(response.getBody());
        Statement finalStatement = statement;
        for (LoanOfferDto offer : offers) {
            offer.setStatementId(finalStatement.getStatementId());
        }
    return offers;

    }

    public void selectOffer(LoanOfferDto offerDto) throws JsonProcessingException {
        //проверка существования заявки с таким ID
        Statement statement = statementRepository.findStatementByStatementId(offerDto.getStatementId()).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + offerDto.getStatementId()));
        log.info("Запрос по заявке: {}", offerDto.getStatementId().toString());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode appliedOfferNode;
        if (statement.getAppliedOffer() == null) {
            appliedOfferNode = mapper.createObjectNode();
            log.info("AppliedOffer - null, создаём новый JSON объект");
        } else {
            try {
                appliedOfferNode = (ObjectNode) mapper.readTree(statement.getAppliedOffer()); // Обратите внимание на toString() здесь
            } catch (Exception e) {
                log.error("Ошибка разбора существующего AppliedOffer JSON: {}", e.getMessage());
                throw new RuntimeException("Ошибка разбора существующего AppliedOffer JSON.", e);
            }
        }
        appliedOfferNode.set("selectedOffer", mapper.valueToTree(offerDto));
       // statement.setAppliedOffer(mapper.writeValueAsString(appliedOfferNode)); // Напрямую присваиваем ObjectNode
        String jsonOutput = mapper.writeValueAsString(appliedOfferNode); // Сериализация в строку JSON
        log.info("Строка json: " + jsonOutput);
        statement.setAppliedOffer(jsonOutput);
        log.info("Поле AppliedOffer сущности: " + statement.getAppliedOffer());
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("Новый статус заявки: " + statement.getStatus());
      //  statement.setAppliedOffer(String.valueOf(appliedOfferNode));
       // mapper.readTree(appliedOfferNode);
        log.info("Выбранное предложение: {}", statement.getAppliedOffer());
        //   log.info("Парсим json " + jsonNode.get("requestedAmount").asText());   //в  junit тест можно такое засунуть

        statement = statementRepository.save(statement);
        log.info("Выбранное предложение обновлено в базе данных.");

        // List<StatementStatusHistoryDto> тоже надо как-то обновить
    }

    public void finishRegistration(UUID statementId, FinishRegistrationRequestDto request) throws IOException {
        Statement statement = statementRepository.findStatementByStatementId(statementId).orElseThrow();

        Statement finalStatement = statement;
        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + finalStatement.getClientUuid())).getClientUuid();

        ObjectMapper mapper = new ObjectMapper();
       // JsonNode jsonNode = mapper.readTree(statement.getAppliedOffer());

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setBirthdate(client.getBirthDate());
        scoringDataDto.setPassportNumber(client.getPassport().getNumber());
        scoringDataDto.setPassportSeries(client.getPassport().getSeries());
        // scoringDataDto.setEmployment(client.getEmployment());
        scoringDataDto.setAccountNumber(client.getAccountNumber());
        // scoringDataDto.setAmount(statement.getAppliedOffer());
//        scoringDataDto.setTerm(jsonNode.get("term").asInt());
//        scoringDataDto.setAmount(jsonNode.get("amount").decimalValue());

        CreditDto creditDto = restTemplate.postForObject("/calculator/calc", scoringDataDto, CreditDto.class);

        Credit credit = new Credit();
        //  credit.setCreditDto(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        credit = creditRepository.save(credit);
        //  statement.setStatus(statement.CALCULATED);
        statement = statementRepository.save(statement);
    }
}