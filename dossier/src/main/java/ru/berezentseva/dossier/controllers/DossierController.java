package ru.berezentseva.dossier.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.berezentseva.dossier.services.EmailMessageConsumerService;

@RestController
@RequestMapping("/dossier")
public class DossierController {

    private final EmailMessageConsumerService emailMessageConsumerService;

    public DossierController(EmailMessageConsumerService emailMessageConsumerService) {
        this.emailMessageConsumerService = emailMessageConsumerService;
    }

    @PostMapping("/send-email")
    public void sendEmail(@RequestBody String emailMessage) throws JsonProcessingException {
        emailMessageConsumerService.sendEmail(emailMessage);
    }
}