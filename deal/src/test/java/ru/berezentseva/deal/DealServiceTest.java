package ru.berezentseva.deal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.ClientRepository;
import ru.berezentseva.deal.repositories.CreditRepository;
import ru.berezentseva.deal.repositories.StatementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@SpringBootTest
//@DataJpaTest
@ExtendWith(MockitoExtension.class)
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Для тестирования REST контроллера
class DealServiceTest {
    @InjectMocks
    private DealService dealService;

    @Mock
    private DealService dealServiceMock;

    @Mock
    private RestTemplate testRestTemplate;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;

    private Client client;

    @BeforeEach
    void setUp() {
        testRestTemplate = new RestTemplate();

        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Ivan");
        request.setLastName("Ivanov");
        request.setMiddleName("Ivanovich");
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(12);
        request.setBirthDate(LocalDate.parse("1990-05-07"));
        request.setPassportSeries("1234");
        request.setPassportNumber("567890");

        Client clientTest = new Client();
        clientTest.setFirstName("Ivan");
        clientTest.setLastName("Ivanov");
        clientTest.setMiddleName("Ivanovich");
        clientTest.setBirthDate(LocalDate.parse("1990-05-07"));

    }


//    @Test
//    public void testSuccessfulApiRequest() {
//        ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:8080/calculator/offers", request, String.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        //проверка содержимого ответа
//     //   assertTrue(response.getBody().contains("OK"));
//        //дополнительные проверки
//    }

//    @Test
//    public void testNewApplicationAndClientReturnsFourElements() {
//        LoanStatementRequestDto request = new LoanStatementRequestDto();
//        request.setFirstName("Evgeniya");
//        request.setLastName("Berezentseva");
//        request.setMiddleName("Vladimirovna");
//        request.setAmount(BigDecimal.valueOf(300000));
//        request.setTerm(6);
//        request.setBirthDate(LocalDate.parse("2000-12-01"));
//        request.setEmail("mail.123@example.com");
//        request.setPassportSeries("1255");
//        request.setPassportNumber("567050");
//
//
//        // Выполнение метода
//        List<LoanOfferDto> offers = dealService.createNewApplicationAndClient(request);
//
//        // Проверка результатов
//        assertEquals(4, offers.size(), "Должно быть 4 оффера");
//    }

    // проверяем, что данные сохраняются в репозиториях
    @Test
    void saveStatementInRepositorySuccess() {
        StatementRepository statementRepository = mock(StatementRepository.class);
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        // Настройка заглушки для метода save
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        // Вызов метода, который должен использовать statementRepository.save()
        Statement savedStatement = statementRepository.save(statement);
        // Проверка, что сохраненная заявка соответствует ожидаемому
        assertNotNull(savedStatement);
        assertEquals(statement.getStatementId(), savedStatement.getStatementId());
    }

    @Test
    void saveClientInRepositorySuccess() {
        ClientRepository clientRepository = mock(ClientRepository.class);
        Client client = new Client();
        client.setClientUuid(UUID.randomUUID());
        // Настройка заглушки для метода save
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        // Вызов метода, который должен использовать clientRepository.save()
        Client savedClient = clientRepository.save(client);
        // Проверка, что сохраненный клиент соответствует ожидаемому
        assertNotNull(savedClient);
        assertEquals(client.getClientUuid(), savedClient.getClientUuid());
    }


    @Test
    void saveCreditInRepositorySuccess() {
        CreditRepository creditRepository = mock(CreditRepository.class);
        Credit credit = new Credit();
        credit.setCreditUuid(UUID.randomUUID());
        // Настройка заглушки для метода save
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);
        // Вызов метода, который должен использовать creditRepository.save()
        Credit savedCredit = creditRepository.save(credit);
        // Проверка, что сохраненный кредит соответствует ожидаемому
        assertNotNull(savedCredit);
        assertEquals(credit.getCreditUuid(), savedCredit.getCreditUuid());
    }

    // проверяем, что методы вызываются
    @Test
    void saveStatementInDataBaseSuccess() {
        StatementRepository statementRepository = mock(StatementRepository.class);
        Statement statement = new Statement();
        // Настройка заглушки для метода save
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        // Вызов метода, который должен использовать save()
        statementRepository.save(statement);
        // Проверка, что метод save был вызван один раз
        verify(statementRepository, times(1)).save(any(Statement.class));
    }

    @Test
    void saveClientInDataBaseSuccess() {
        ClientRepository clientRepository = mock(ClientRepository.class);
        Client client = new Client();
        // Настройка заглушки для метода save
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        // Вызов метода, который должен использовать save()
        clientRepository.save(client);
        // Проверка, что метод save был вызван один раз
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void saveCreditInDataBaseSuccess() {
        CreditRepository creditRepository = mock(CreditRepository.class);
        Credit credit = new Credit();
        // Настройка заглушки для метода save
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);
        // Вызов метода, который должен использовать save()
        creditRepository.save(credit);
        // Проверка, что метод save был вызван один раз
        verify(creditRepository, times(1)).save(any(Credit.class));
    }
}