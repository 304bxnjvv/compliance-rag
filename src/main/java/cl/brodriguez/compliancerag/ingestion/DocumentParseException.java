package cl.brodriguez.compliancerag.ingestion;

/** Se lanza cuando un documento no se puede leer o procesar. */
public class DocumentParseException extends RuntimeException {

    public DocumentParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
