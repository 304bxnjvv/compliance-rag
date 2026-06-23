package cl.brodriguez.compliancerag.web;

import cl.brodriguez.compliancerag.ingestion.DocumentParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/** Traduce excepciones de dominio a respuestas HTTP claras. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentParseException.class)
    public ResponseEntity<Map<String, String>> handleDocumentParse(DocumentParseException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "No se pudo procesar el documento",
                "detail", String.valueOf(ex.getMessage())));
    }
}
