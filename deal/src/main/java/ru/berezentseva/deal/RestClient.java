package ru.berezentseva.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.deal.model.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class RestClient {

    private final RestTemplate restTemplate;

    @Autowired
    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Statement getStatementById(UUID statementId) {
        return restTemplate.getForObject("http://localhost:8081/deal/statement/" + statementId, Statement.class);
    }

    public List<Statement> getAllStatements() {
        return Arrays.asList(restTemplate.getForObject("http://localhost:8081/deal/statement", Statement[].class));
    }
}
