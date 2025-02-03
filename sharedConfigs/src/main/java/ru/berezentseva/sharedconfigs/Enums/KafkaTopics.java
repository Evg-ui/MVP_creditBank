package ru.berezentseva.sharedconfigs.Enums;

public enum KafkaTopics {
    finishRegistration("finish-registration"),
    createDocuments("create-documents"),
    sendDocuments("send-documents"),
    sendSes("send-ses"),
    creditIssued("credit-issued"),
    statementDenied("statement-denied");

    private final String topic;

    KafkaTopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }
}