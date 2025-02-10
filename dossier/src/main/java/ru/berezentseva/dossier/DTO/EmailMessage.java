package ru.berezentseva.dossier.DTO;


import ru.berezentseva.dossier.DTO.Enums.Theme;

import java.util.UUID;

public class EmailMessage {
    private String address;
    private Theme theme;
    private UUID statementId;
    private String text;

    public EmailMessage() {
    }

    public String getAddress() {
        return this.address;
    }

    public Theme getTheme() {
        return this.theme;
    }

    public UUID getStatementId() {
        return this.statementId;
    }

    public String getText() {
        return this.text;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void setStatementId(UUID statementId) {
        this.statementId = statementId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return "EmailMessage(address=" + this.getAddress() + ", theme=" + this.getTheme() + ", statementId=" + this.getStatementId() + ", text=" + this.getText() + ")";
    }
}
