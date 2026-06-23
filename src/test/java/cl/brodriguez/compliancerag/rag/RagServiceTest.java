package cl.brodriguez.compliancerag.rag;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RagServiceTest {

    private final VectorStore vectorStore = mock(VectorStore.class);
    private final ChatModel chatModel = mock(ChatModel.class);
    private final RagService service = new RagService(vectorStore, chatModel, 4);

    @Test
    void ask_devuelveLaRespuestaGeneradaPorElLLM() {
        cuandoSeRecupera(new Document("contenido", Map.of("source", "norma.pdf", "page", 1)));
        cuandoElLlmResponde("La norma exige cifrado.");

        RagAnswer respuesta = service.ask("¿Que exige la norma?");

        assertThat(respuesta.answer()).isEqualTo("La norma exige cifrado.");
    }

    @Test
    void ask_incluyeLosFragmentosRecuperadosComoContextoEnElPrompt() {
        cuandoSeRecupera(new Document("El cliente debe cifrar los datos sensibles.",
                Map.of("source", "norma.pdf", "page", 2)));
        cuandoElLlmResponde("ok");

        service.ask("¿Que hacer con los datos?");

        ArgumentCaptor<Prompt> captor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatModel).call(captor.capture());
        assertThat(captor.getValue().getContents())
                .contains("El cliente debe cifrar los datos sensibles.")
                .contains("norma.pdf");
    }

    @Test
    void ask_devuelveLasCitasDeLosDocumentosUsados() {
        cuandoSeRecupera(new Document("texto", Map.of("source", "norma.pdf", "page", 3)));
        cuandoElLlmResponde("respuesta");

        RagAnswer respuesta = service.ask("pregunta");

        assertThat(respuesta.citations()).containsExactly(new Citation("norma.pdf", 3));
    }

    private void cuandoSeRecupera(Document... docs) {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(docs));
    }

    private void cuandoElLlmResponde(String texto) {
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(texto)))));
    }
}
