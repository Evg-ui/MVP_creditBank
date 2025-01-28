package ru.berezentseva.gateway;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.exception.StatementException;

import java.util.UUID;

@Slf4j
@Service
public class GatewayService {

    private final RestTemplate restTemplate;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, LoanStatementRequestDto request) throws ScoreException{
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка 4xx клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
           // throw e;
        //    throw new ScoreException("Ошибка клиента: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка 5xx сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
         //   throw e;
          //  throw new ScoreException("Ошибка сервера: " + e.getMessage());
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
         //   throw e;
         //   throw new ScoreException("Общая ошибка при вызове API: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка");
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, LoanOfferDto request) {
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, FinishRegistrationRequestDto request) {

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
          //  return ResponseEntity.ok(responseEntity.getBody());
            return ResponseEntity.ok("Успешно!");
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, UUID statementId ) {

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(statementId, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }
}
