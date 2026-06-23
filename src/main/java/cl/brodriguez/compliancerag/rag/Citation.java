package cl.brodriguez.compliancerag.rag;

/** Referencia a la fuente de un fragmento usado para responder. */
public record Citation(String source, int page) {
}
