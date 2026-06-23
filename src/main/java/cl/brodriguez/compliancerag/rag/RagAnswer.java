package cl.brodriguez.compliancerag.rag;

import java.util.List;

/** Respuesta del asistente: el texto generado y las fuentes citadas. */
public record RagAnswer(String answer, List<Citation> citations) {
}
