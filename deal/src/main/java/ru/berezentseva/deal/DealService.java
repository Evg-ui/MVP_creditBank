package ru.berezentseva.deal;

import com.fasterxml.jackson.core.JsonProcessingException;
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


@Service
@Slf4j
public class DealService {


    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final PassportRepository passportRepository;

    //Constructor
    @Autowired
    public DealService(RestTemplate restTemplate,
                       ClientRepository clientRepository,
                       StatementRepository statementRepository,
                       CreditRepository creditRepository,
                       PassportRepository passportRepository) {
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.statementRepository = statementRepository;
        this.creditRepository = creditRepository;
        this.passportRepository = passportRepository;
    }

    @Transactional
    void saveStatement(Statement statement) {statementRepository.save(statement); }

    @Transactional
    Client saveClient(Client client) {return clientRepository.save(client);}

    @Transactional
    void savePassport(Passport passport) {
        passportRepository.save(passport);
    }

    @Transactional
    Credit saveCredit(Credit credit) {
        return creditRepository.save(credit);
    }


    public List<LoanOfferDto> createApplication(LoanStatementRequestDto request) {
        // try catch добавить
        log.info("Received request into createApp: {}", request);
        log.info("Заполняем данные  паспорта:");
        Passport passport = new Passport();
     //   passport.setPassportUuid(UUID.randomUUID());
        log.info("UUID паспорта {}", passport.getPassportUuid());
        passport.setSeries(request.getPassportSeries());
        log.info("Серия {}", passport.getSeries());
        passport.setNumber(request.getPassportNumber());
        log.info("Номер {}", passport.getNumber());
        log.info("Сохраняем паспорт клиента");
      //  savePassport(passport);
        log.info("Паспорт клиента сохранен!");

        log.info("Создание нового клиента");
        Client client = new Client();
       // client.setClientUuid(UUID.randomUUID());
        log.info("UUID клиента {}", client.getClientUuid());
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
        saveClient(client);
        log.info("UUID клиента {}", client.getClientUuid().toString());
        log.info("Клиент создан!");

        log.info("Создание новой заявки");
        Statement statement = new Statement();
    //    statement.setStatementId(UUID.randomUUID());
        statement.setClientUuid(client);
        log.info("ClientId {}", statement.getClientUuid().toString());
        statement.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        log.info("Creation_date {}", statement.getCreationDate().toString());
        log.info("Сохраняем заявку");
        saveStatement(statement);
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
    }

    public void selectOffer(LoanOfferDto offerDto) throws JsonProcessingException {
        //проверка существования заявки с таким ID
        Statement statement = statementRepository.findStatementByStatementId(offerDto.getStatementId()).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + offerDto.getStatementId()));
        log.info("Запрос по заявке: {}", offerDto.getStatementId().toString());

        log.info("Устанавливаем значение json: {}", statement.getAppliedOffer());
        log.info("Обновляем данные заявки");
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("Новый статус заявки: {}", statement.getStatus());
        statement.setAppliedOffer(offerDto);
        log.info("Поле AppliedOffer сущности: {}", statement.getAppliedOffer());
        saveStatement(statement);
        log.info("Выбранное предложение: {}", statement.getAppliedOffer());
        log.info("Данные заявки обновлены!");
        log.info("Выбранное предложение обновлено в базе данных.");
        // List<StatementStatusHistoryDto> тоже надо как-то обновить
    }

    public void finishRegistration(UUID statementId, FinishRegistrationRequestDto request) throws IOException {
        Statement statement = statementRepository.findStatementByStatementId(statementId).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + statementId));

     //  Statement finalStatement = statement;

        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + statement.getClientUuid())).getClientUuid();

        EmploymentDto employment = request.getEmployment();

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        log.info("Подготавливаем Скоринг...");
        scoringDataDto.setAmount(statement.getAppliedOffer().getRequestedAmount());
        scoringDataDto.setTerm(statement.getAppliedOffer().getTerm());
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
        scoringDataDto.setIsSalaryClient(statement.getAppliedOffer().getIsSalaryClient());
        scoringDataDto.setIsInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnabled());
        log.info("Данные для скоринга готовы!");
        // насыщаем клиент
        log.info("Дополняем данные по клиенту...");
        client.setAccountNumber(request.getAccountNumber());
        client.setDependentAmount(request.getDependentAmount());
        client.setGender(request.getGender());
        client.setMaritalStatus(request.getMaritalStatus());
        //client.setEmployment(employment);  // почему не хочет из employment
        //  CreditDto creditDto = restTemplate.postForObject("/calculator/calc", scoringDataDto, CreditDto.class);
        saveClient(client);
        log.info("Данные по клиенту обновлены!");

        log.info("Отправляем запрос в /calculator/calc");
        ResponseEntity<CreditDto> responseEntity;
        CreditDto creditDto;
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
            creditDto = responseEntity.getBody();

            if (creditDto == null) {
                String errorMessage = "Ответ от API не содержит данных.";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            throw new RuntimeException("Ошибка при вызове API: " + e.getMessage(), e);
        }

        log.info("Полученный кредит из calc: {}", creditDto);
        Credit credit = new Credit();
        log.info("UUID кредита {}", credit.getCreditUuid());
        credit.setAmount(creditDto.getAmount());
        credit.setTerm(creditDto.getTerm());
        credit.setMonthlyPayment(creditDto.getMonthlyPayment());
        credit.setRate(creditDto.getRate());
        credit.setPsk(creditDto.getPsk());
        credit.setPayment_schedule(creditDto.getPaymentSchedule());
        credit.setSalaryClient(creditDto.getIsSalaryClient());
        credit.setInsuranceEnabled(creditDto.getIsInsuranceEnabled());
        credit.setCreditStatus(CreditStatus.CALCULATED);
        saveCredit(credit);
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setCreditUuid(credit);
        saveStatement(statement);
    }
}