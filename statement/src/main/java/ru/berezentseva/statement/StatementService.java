package ru.berezentseva.statement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.CalculatorService;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.statement.exception.StatementException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@ComponentScan(basePackages = {"ru.berezentseva.statement", "ru.berezentseva.deal"})
public class StatementService {
    private final RestTemplate restTemplate;

    public StatementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LoanOfferDto> returnOffersAfterPrescoring(LoanStatementRequestDto request) throws ScoreException {

        final CalculatorService calculatorService = new CalculatorService();

        try {

            calculatorService.preScoringCheck(request);

//         Отправка запроса на /calculator/offers.
            log.info("Отправляем запрос в /calculator/offers");
            ResponseEntity<LoanOfferDto[]> responseEntity;
            try {
                responseEntity = restTemplate.exchange(
                        "http://localhost:8081/deal/statement",
                        HttpMethod.POST,
                        new HttpEntity<>(request, new HttpHeaders()),
                        LoanOfferDto[].class);

                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    // Обработка ошибок HTTP
                    String errorMessage = "Ошибка при вызове API deal/statement: " + responseEntity.getStatusCode() +
                            ", тело ответа: " + Arrays.toString(responseEntity.getBody());
                   log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
                List<LoanOfferDto> offers = Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
                return offers;

            } catch (HttpClientErrorException e) {
                // Обработка ошибок клиента (4xx)
                log.error("Ошибка клиента при вызове API deal/statement: {}, статус: {}", e.getMessage(), e.getStatusCode());
                throw e;
            } catch (HttpServerErrorException e) {
                // Обработка ошибок сервера (5xx)
                log.error("Ошибка сервера при вызове API deal/statement: {}, статус: {}", e.getMessage(), e.getStatusCode());
                throw e;
            } catch (RestClientException e) {
                // Обработка общих ошибок Rest клиента
               log.error("Ошибка при вызове API: ", e);
                throw e;
            }
        } catch (ScoreException | IllegalArgumentException e) {
            throw e;
        }

    }

    public void selectOfferFromDeal(LoanOfferDto offerDto) throws StatementException {
        ResponseEntity<LoanOfferDto[]> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    "http://localhost:8081/deal/offer/select",
                    HttpMethod.POST,
                    new HttpEntity<>(offerDto, new HttpHeaders()),
                    LoanOfferDto[].class);

            log.info("Ответ от API: {}", responseEntity.getBody());

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                // Обработка ошибок HTTP
                String errorMessage = "Ошибка при вызове API deal/offer/select: " + responseEntity.getStatusCode() +
                        ", тело ответа: " + Arrays.toString(responseEntity.getBody());
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка клиента при вызове API deal/offer/select: {}, статус: {}", e.getMessage(), e.getStatusCode());
            throw e;
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка сервера при вызове API deal/offer/select: {}, статус: {}", e.getMessage(), e.getStatusCode());
            throw e;
        } catch (RestClientException e) {
            // Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            throw e;
        }
    }


}
