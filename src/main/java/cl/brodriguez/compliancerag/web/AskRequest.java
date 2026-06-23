package cl.brodriguez.compliancerag.web;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo de la petición a POST /ask. */
public record AskRequest(@NotBlank String question) {
}
