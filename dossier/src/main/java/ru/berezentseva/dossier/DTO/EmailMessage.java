package ru.berezentseva.dossier.DTO;

import lombok.*;
import ru.berezentseva.dossier.DTO.Enums.Theme;

import java.util.UUID;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class EmailMessage
{
private String address;
private Theme theme;
private UUID statementId;
private String text;
}
