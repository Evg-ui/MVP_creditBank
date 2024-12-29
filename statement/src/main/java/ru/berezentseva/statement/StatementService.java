package ru.berezentseva.statement;

import lombok.extern.slf4j.Slf4j;
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
import ru.berezentseva.deal.exception.StatementException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class StatementService {
    private final RestTemplate restTemplate;
   // private final CalculatorService calculatorService;
    // private final CalculatorService calculatorService;

    public StatementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<LoanOfferDto> returnOffersAfterPrescoring(LoanStatementRequestDto request) throws ScoreException {

        final CalculatorService calculatorService = new CalculatorService();

        //log.info("Received request into createApp: {}", request);
        try {

            calculatorService.preScoringCheck(request);

//         Отправка запроса на /calculator/offers.
     //       log.info("Отправляем запрос в /calculator/offers");
            ResponseEntity<LoanOfferDto[]> responseEntity;
            try {
                responseEntity = restTemplate.exchange(
                        "http://localhost:8081/deal/statement",
                        HttpMethod.POST,
                        new HttpEntity<>(request, new HttpHeaders()),
                        LoanOfferDto[].class);

                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    // Обработка ошибок HTTP
                    String errorMessage = "Ошибка при вызове API: " + responseEntity.getStatusCode() +
                            ", тело ответа: " + Arrays.toString(responseEntity.getBody());
           //         log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
                List<LoanOfferDto> offers = Arrays.asList(responseEntity.getBody());
                return offers;

            } catch (HttpClientErrorException e) {
                // Обработка ошибок клиента (4xx)
                //log.error("Ошибка клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
                throw e;
            } catch (HttpServerErrorException e) {
                // Обработка ошибок сервера (5xx)
             //   log.error("Ошибка сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
                throw e;
            } catch (RestClientException e) {
                // Обработка общих ошибок Rest клиента
               //log.error("Ошибка при вызове API: ", e);
                throw e;
            }
        } catch (ScoreException | IllegalArgumentException e) {
            throw e;
        }

    }

    public void selectOfferFromlDeal(LoanOfferDto offerDto) throws StatementException {
        ResponseEntity<LoanOfferDto[]> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    "http://localhost:8081/deal/offer/select",
                    HttpMethod.POST,
                    new HttpEntity<>(offerDto, new HttpHeaders()),
                    LoanOfferDto[].class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                // Обработка ошибок HTTP
                String errorMessage = "Ошибка при вызове API: " + responseEntity.getStatusCode() +
                        ", тело ответа: " + Arrays.toString(responseEntity.getBody());
                //         log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            List<LoanOfferDto> offers = Arrays.asList(responseEntity.getBody());
            responseEntity.getBody();
            ;

        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            //log.error("Ошибка клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            throw e;
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            //   log.error("Ошибка сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            throw e;
        } catch (RestClientException e) {
            // Обработка общих ошибок Rest клиента
            //log.error("Ошибка при вызове API: ", e);
            throw e;
        }
    }
    }
