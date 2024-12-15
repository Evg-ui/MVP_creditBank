package ru.berezentseva.deal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.berezentseva.calculator.DTO.*;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Passport;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.ClientRepository;
import ru.berezentseva.deal.repositories.CreditRepository;
import ru.berezentseva.deal.repositories.PassportRepository;
import ru.berezentseva.deal.repositories.StatementRepository;
import ru.berezentseva.calculator.DTO.LoanOfferDto;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
//@Component
public class DealService {


    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final PassportRepository passportRepository;

    @Autowired // Ensure this is annotated appropriately
    public DealService(RestTemplate restTemplate, ClientRepository clientRepository, StatementRepository statementRepository, CreditRepository creditRepository, PassportRepository passportRepository) { //Constructor
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.statementRepository = statementRepository;
        this.creditRepository = creditRepository;
        this.passportRepository = passportRepository;
    }

    @Transactional
    public List<LoanOfferDto> createApplication(LoanStatementRequestDto request){
        // try catch добавить
        log.info("Received request into createApp: {}", request);
        log.info("Заполняем данные  паспорта:");
        Passport passport = new Passport();
        passport.setPassportUuid(UUID.randomUUID());
        passport.setSeries(request.getPassportSeries());
        log.info("Серия {}", passport.getSeries());
        passport.setNumber(request.getPassportNumber());
        log.info("Номер {}", passport.getNumber());
        log.info("Сохраняем паспорт клиента");
        passport = passportRepository.save(passport);


        log.info("Создание нового клиента");
        Client client = new Client();
        client.setClientUuid(UUID.randomUUID());
        client.setLastName(request.getLastName());
        log.info("LastName {}", client.getLastName());
        client.setFirstName(request.getFirstName());
        log.info("FirstName {}", client.getFirstName());
        client.setMiddleName(request.getMiddleName());
        log.info("MiddleName {}", client.getMiddleName());
        client.setBirthDate(request.getBirthDate());
        log.info("BirthDate {}", client.getBirthDate().toString());
        client.setEmail(request.getEmail());
        log.info("Email {}", client.getEmail());
        client.setPassport(passport);
        log.info("Passport UUID {}", client.getPassport());
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
//        statement.setAppliedOffer(objectMapper.createObjectNode().toString());
        log.info("Сохраняем заявку");
        statement.prePersist();
        log.info("Applied Offer: {}", statement.getAppliedOffer());
     //   statement = statementRepository.save(statement);
        log.info("UUID заявки {}", statement.getStatementId().toString());
        log.info("Заявка создана");

//         Отправка запроса на /calculator/offers.
//          try catch надо
        log.info("Отправляем запрос в /calculator/offers");

        ResponseEntity<LoanOfferDto[]> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    "http://localhost:8080/calculator/offers",
                    HttpMethod.POST,
                    new HttpEntity<>(request, new HttpHeaders()),
                    LoanOfferDto[].class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                // Обработка ошибок HTTP
                String errorMessage = "Ошибка при вызове API: " + responseEntity.getStatusCode() +
                        ", тело ответа: " + responseEntity.getBody();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            Statement finalStatement = statement;
            List<LoanOfferDto> offers = Arrays.asList(responseEntity.getBody());
            offers.forEach(offer -> offer.setStatementId(finalStatement.getStatementId()));
            return offers;

        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            throw new RuntimeException("Ошибка при вызове API: " + e.getMessage(), e);
        }

        //       String calculatorOffersUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/calculator/offers").toUriString();
 //       List<LoanOfferDto> offers = restTemplate.postForObject(calculatorOffersUrl, request, List.class);
//        String baseUrl = "http://localhost:8080";
//        String offersPath = "/calculator/offers";
//        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl).path(offersPath).build().toUri();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<LoanStatementRequestDto> entity = new HttpEntity<>(request, headers);
        //        ResponseEntity<LoanOfferDto[]> response;
//
//        try {
//            response = restTemplate.exchange(uri, HttpMethod.POST, entity, LoanOfferDto[].class);
//        } catch (HttpClientErrorException e) {
//            log.error("Ошибка при вызове API: {}", e.getMessage());
//            throw new RuntimeException("Ошибка при получении предложений: " + e.getStatusCode());
//        }
//
      //  List<LoanOfferDto> offers  = List.of(response.getBody());
//        Statement finalStatement = statement;
//        for (LoanOfferDto offer : offers) {
//            offer.setStatementId(finalStatement.getStatementId());
//        }
        // сортировка тут не нужна, потому что сортируется на стороне калькулятора?
       // offers = offers.stream().sorted(Comparator.comparing(LoanOfferDto::getRate).reversed()).collect(Collectors.toList());
 //   return offers;
    }

    public void selectOffer(LoanOfferDto offerDto) throws JsonProcessingException {
        //проверка существования заявки с таким ID
        Statement statement = statementRepository.findStatementByStatementId(offerDto.getStatementId()).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + offerDto.getStatementId()));
        log.info("Запрос по заявке: {}", offerDto.getStatementId().toString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode appliedOfferNode;

//        if (statement.getAppliedOffer() == null) {
//            appliedOfferNode = mapper.createObjectNode();
//            log.info("AppliedOffer - null, создаём новый JSON объект");
//        } else {
//            try {
//                appliedOfferNode = mapper.readTree(statement.getAppliedOffer());
//            } catch (Exception e) {
//                log.error("Ошибка разбора существующего AppliedOffer JSON: {}", e.getMessage());
//                throw new RuntimeException("Ошибка разбора существующего AppliedOffer JSON.", e);
//            }
//        }
//        ((ObjectNode) appliedOfferNode).set("selectedOffer", mapper.valueToTree(offerDto));

        // Сериализация обновленного JSON-объекта в строку
        String jsonOutput = mapper.writeValueAsString(mapper.readTree(statement.getAppliedOffer()));
        statement.setAppliedOffer(jsonOutput); // Устанавливаем строку JSON в поле appliedOffer
        log.info("Строка json: " + jsonOutput);
       // statement.setAppliedOffer(jsonOutput);
        log.info("Поле AppliedOffer сущности: " + statement.getAppliedOffer());

        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("Новый статус заявки: " + statement.getStatus());
      //  statement.setAppliedOffer(String.valueOf(appliedOfferNode));
       // mapper.readTree(appliedOfferNode);
        log.info("Выбранное предложение: {}", statement.getAppliedOffer());
        //   log.info("Парсим json " + jsonNode.get("requestedAmount").asText());   //в  junit тест можно такое засунуть

        statementRepository.save(statement);
        log.info("Выбранное предложение обновлено в базе данных.");

        // List<StatementStatusHistoryDto> тоже надо как-то обновить
    }

    public void finishRegistration(UUID statementId, FinishRegistrationRequestDto request) throws IOException {
        Statement statement = statementRepository.findStatementByStatementId(statementId).orElseThrow();
        Statement finalStatement = statement;

        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + finalStatement.getClientUuid())).getClientUuid();

        ObjectMapper mapper = new ObjectMapper();

        EmploymentDto employment = new EmploymentDto();
        JsonNode jsonNode = mapper.readTree(statement.getAppliedOffer());
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        // насыщаем скоринг
        scoringDataDto.setAmount(jsonNode.get("amount").decimalValue());
        scoringDataDto.setTerm(jsonNode.get("term").asInt());
        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setGender(request.getGender());
        scoringDataDto.setBirthdate(client.getBirthDate());
        scoringDataDto.setPassportNumber(client.getPassport().getNumber());
        scoringDataDto.setPassportSeries(client.getPassport().getSeries());
        scoringDataDto.setPassportIssueBranch(request.getPassportIssueBrach());
        scoringDataDto.setPassportIssueDate(request.getPassportIssueDate());
        scoringDataDto.setMaritalStatus(request.getMaritalStatus());
        scoringDataDto.setDependentAmount(request.getDependentAmount());
        scoringDataDto.setEmployment(employment);
        scoringDataDto.setAccountNumber(request.getAccountNumber());
        scoringDataDto.setIsSalaryClient(jsonNode.get("isSalaryClient").asBoolean());
        scoringDataDto.setIsSalaryClient(jsonNode.get("isInsuranceEnabled").asBoolean());

        // насыщаем клиент
        client.setAccountNumber(request.getAccountNumber());
        client.setDependentAmount(request.getDependentAmount());
        client.setGender(request.getGender());
        client.setMaritalStatus(request.getMaritalStatus());
     //   client.setEmployment(scoringDataDto.getEmployment().);  // почему не хочет из employmet
        CreditDto creditDto = restTemplate.postForObject("/calculator/calc", scoringDataDto, CreditDto.class);

        Credit credit = new Credit();
        //  credit.setCreditDto(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        credit = creditRepository.save(credit);
        //  statement.setStatus(statement.CALCULATED);
        statement = statementRepository.save(statement);
    }
}