package cl.brodriguez.compliancerag.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta la ingesta de un documento: lo parsea en fragmentos, los indexa en el
 * vector store y lleva registro de los documentos cargados.
 */
public class IngestionService {

    private final DocumentParser parser;
    private final VectorStore vectorStore;
    private final List<String> documents = new ArrayList<>();

    public IngestionService(DocumentParser parser, VectorStore vectorStore) {
        this.parser = parser;
        this.vectorStore = vectorStore;
    }

    public void ingest(String filename, byte[] content) {
        List<Document> fragments = parser.parse(filename, content);
        vectorStore.add(fragments);
        documents.add(filename);
    }

    public List<String> listDocuments() {
        return List.copyOf(documents);
    }
}
