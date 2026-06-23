package cl.brodriguez.compliancerag.ingestion;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * Convierte el contenido binario de un documento (ej. un PDF) en una lista de
 * fragmentos ({@link Document}) listos para indexar, con metadata de origen.
 */
public interface DocumentParser {

    List<Document> parse(String filename, byte[] content);
}
