package cl.brodriguez.compliancerag.ingestion;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IngestionServiceTest {

    @Test
    void ingest_agregaLosFragmentosParseadosAlVectorStore() {
        DocumentParser parser = mock(DocumentParser.class);
        VectorStore vectorStore = mock(VectorStore.class);
        byte[] pdf = "contenido del pdf".getBytes();
        List<Document> fragmentos = List.of(new Document("fragmento 1"), new Document("fragmento 2"));
        when(parser.parse("norma.pdf", pdf)).thenReturn(fragmentos);

        IngestionService service = new IngestionService(parser, vectorStore);
        service.ingest("norma.pdf", pdf);

        verify(vectorStore).add(fragmentos);
    }

    @Test
    void ingest_registraElNombreDelDocumentoIngestado() {
        DocumentParser parser = mock(DocumentParser.class);
        VectorStore vectorStore = mock(VectorStore.class);
        when(parser.parse("norma.pdf", "x".getBytes())).thenReturn(List.of(new Document("frag")));

        IngestionService service = new IngestionService(parser, vectorStore);
        service.ingest("norma.pdf", "x".getBytes());

        assertThat(service.listDocuments()).containsExactly("norma.pdf");
    }

    @Test
    void listDocuments_inicialmenteEstaVacio() {
        IngestionService service = new IngestionService(mock(DocumentParser.class), mock(VectorStore.class));

        assertThat(service.listDocuments()).isEmpty();
    }
}
