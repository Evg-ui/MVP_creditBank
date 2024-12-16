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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.*;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Passport;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.*;
import ru.berezentseva.calculator.DTO.LoanOfferDto;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
@Slf4j
public class DealService {


    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final PassportRepository passportRepository;
    private final EmploymentRepository employmentRepository;

    @Autowired
    public DealService(RestTemplate restTemplate, ClientRepository clientRepository, StatementRepository statementRepository, CreditRepository creditRepository, PassportRepository passportRepository, EmploymentRepository employmentRepository) { //Constructor
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.statementRepository = statementRepository;
        this.creditRepository = creditRepository;
        this.passportRepository = passportRepository;
        this.employmentRepository = employmentRepository;
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
        //JsonNode appliedOfferNode;
        ObjectNode appliedOfferNode;

        if (statement.getAppliedOffer() == null) {
            appliedOfferNode = mapper.createObjectNode();
            log.info("AppliedOffer - null, создаём новый JSON объект");
        } else {
            try {
               // appliedOfferNode = mapper.readTree(statement.getAppliedOffer());
                appliedOfferNode = (ObjectNode) mapper.readTree(statement.getAppliedOffer());
            } catch (Exception e) {
                log.error("Ошибка разбора существующего AppliedOffer JSON: {}", e.getMessage());
                throw new RuntimeException("Ошибка разбора существующего AppliedOffer JSON.", e);
            }
        }
       //((ObjectNode) appliedOfferNode).set("selectedOffer", mapper.valueToTree(offerDto));
       // appliedOfferNode = mapper.readTree(statement.getAppliedOffer());

//        // Сериализация обновленного JSON-объекта в строку
//        String jsonOutput = mapper.writeValueAsString(appliedOfferNode);
//        statement.setAppliedOffer(jsonOutput); // Устанавливаем строку JSON в поле appliedOffer
        appliedOfferNode.set("selectedOffer", mapper.valueToTree(offerDto));
        statement.setAppliedOffer(mapper.writeValueAsString(appliedOfferNode));
        log.info("Устанавливаем значение json: {}", statement.getAppliedOffer());

        statementRepository.save(statement);
//        log.info("Строка json: " + jsonOutput);
//        statement.setAppliedOffer(jsonOutput);
        log.info("Поле AppliedOffer сущности: {}", statement.getAppliedOffer());

        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("Новый статус заявки: {}", statement.getStatus());
        statement.setAppliedOffer(String.valueOf(appliedOfferNode));
       // mapper.readTree(appliedOfferNode);
        log.info("Выбранное предложение: {}", statement.getAppliedOffer());
        //   log.info("Парсим json " + jsonNode.get("requestedAmount").asText());   //в  junit тест можно такое засунуть

        statementRepository.save(statement);
        log.info("Выбранное предложение обновлено в базе данных.");

        // List<StatementStatusHistoryDto> тоже надо как-то обновить
    }

    public void finishRegistration(UUID statementId, FinishRegistrationRequestDto request) throws IOException {
       Statement statement = statementRepository.findStatementByStatementId(statementId).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + statementId));

        Statement finalStatement = statement;

        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + finalStatement.getClientUuid())).getClientUuid();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(statement.getAppliedOffer());
        JsonNode selectedOfferNode = jsonNode.path("selectedOffer");
        log.info("jsonNode: {}", jsonNode);
        EmploymentDto employment = new EmploymentDto();
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        // проверяем, что appliedOffer непустой
        if (selectedOfferNode.isObject()) {
        // насыщаем скоринг
            log.info("Начало заполнения данных для скоринга...");
            if (selectedOfferNode.has("requestedAmount")) {
                scoringDataDto.setAmount(selectedOfferNode.get("requestedAmount").decimalValue());
            } else {
                throw new IllegalArgumentException("Поле 'requestedAmount' отсутствует в selectedOffer");
            }
        //scoringDataDto.setAmount(jsonNode.get("selectedOffer").get(0).get("amount").decimalValue());
            log.info("Полученная из offer сумма кредита requestedAmount: {}", selectedOfferNode.get("requestedAmount").decimalValue().toString());

            if (selectedOfferNode.has("term")) {
                scoringDataDto.setTerm(selectedOfferNode.get("term").asInt());
            } else {
                throw new IllegalArgumentException("Поле 'term' отсутствует в selectedOffer");
            }
            //scoringDataDto.setTerm(jsonNode.get("selectedOffer").get(0).get("term").asInt());
            log.info("Полученный из offer срок кредита term: {}", selectedOfferNode.get("term").asInt());
        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setGender(request.getGender());
        scoringDataDto.setBirthdate(client.getBirthDate());
            log.info("Установленная дата рождения: {}", scoringDataDto.getBirthdate().toString());
        scoringDataDto.setPassportSeries(client.getPassport().getSeries());
        scoringDataDto.setPassportNumber(client.getPassport().getNumber());
        scoringDataDto.setPassportIssueDate(request.getPassportIssueDate());
        scoringDataDto.setPassportIssueBranch(request.getPassportIssueBrach());
        scoringDataDto.setMaritalStatus(request.getMaritalStatus());
        scoringDataDto.setDependentAmount(request.getDependentAmount());
        scoringDataDto.setEmployment(employment);
            log.info("Установленный  работодатель: {}", scoringDataDto.getEmployment().toString());
        scoringDataDto.setAccountNumber(request.getAccountNumber());
            if (selectedOfferNode.has("isSalaryClient")) {
                scoringDataDto.setIsSalaryClient(selectedOfferNode.get("isSalaryClient").asBoolean());
            } else {
                throw new IllegalArgumentException("Поле 'isSalaryClient' отсутствует в selectedOffer");
            }
            log.info("Зарплатный клиент: {}", selectedOfferNode.get("isSalaryClient").asBoolean());
        //scoringDataDto.setIsSalaryClient(jsonNode.get("selectedOffer").get(0).get("isSalaryClient").asBoolean());
            if (selectedOfferNode.has("isInsuranceEnabled")) {
                scoringDataDto.setIsInsuranceEnabled(selectedOfferNode.get("isInsuranceEnabled").asBoolean());
            } else {
                throw new IllegalArgumentException("Поле 'isInsuranceEnabled' отсутствует в selectedOffer");
            }
            log.info("Наличие страховки: {}", selectedOfferNode.get("isInsuranceEnabled").asBoolean());
        //scoringDataDto.setIsSalaryClient(jsonNode.get("selectedOffer").get(0).get("isInsuranceEnabled").asBoolean());
        }
        log.info("Скоринг заполнен!");

        // насыщаем клиент
        client.setAccountNumber(request.getAccountNumber());
        client.setDependentAmount(request.getDependentAmount());
        client.setGender(request.getGender());
        client.setMaritalStatus(request.getMaritalStatus());
      //  client.setEmployment(scoringDataDto.getEmployment().);  // почему не хочет из employment
      //  CreditDto creditDto = restTemplate.postForObject("/calculator/calc", scoringDataDto, CreditDto.class);

        log.info("Отправляем запрос в /calculator/calc");

        ResponseEntity<CreditDto> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    "http://localhost:8080/calculator/calc",
                    HttpMethod.POST,
                    new HttpEntity<>(scoringDataDto, new HttpHeaders()),
                    CreditDto.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                // Обработка ошибок HTTP
                String errorMessage = "Ошибка при вызове API: " + responseEntity.getStatusCode() +
                        ", тело ответа: " + responseEntity.getBody();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
         //   return new ResponseEntity<>(request, HttpStatus.OK);
            CreditDto creditDto = responseEntity.getBody();

        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            throw new RuntimeException("Ошибка при вызове API: " + e.getMessage(), e);
        }

        Credit credit = new Credit();
        //  credit.setCreditDto(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        credit = creditRepository.save(credit);
        statement.setStatus(ApplicationStatus.APPROVED); // или какой надо
        statement = statementRepository.save(statement);
    }
}