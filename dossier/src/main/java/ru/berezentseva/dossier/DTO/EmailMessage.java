package ru.berezentseva.dossier.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.berezentseva.dossier.DTO.Enums.Theme;

@ToString
@Getter
@Setter
public class EmailMessage
{
private String address;
private Theme theme;
private Long statementId;
private String text;
}
