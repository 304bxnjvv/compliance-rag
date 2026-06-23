package cl.brodriguez.compliancerag.rag;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Núcleo del RAG: recupera los fragmentos más relevantes para una pregunta,
 * arma el contexto, pide la respuesta al LLM y devuelve respuesta + citas.
 */
@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final int topK;

    public RagService(VectorStore vectorStore, ChatModel chatModel,
                      @Value("${rag.top-k:4}") int topK) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.topK = topK;
    }

    public RagAnswer ask(String question) {
        List<Document> context = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(topK).build());

        ChatResponse response = chatModel.call(new Prompt(buildPrompt(question, context)));
        String answer = response.getResult().getOutput().getText();

        List<Citation> citations = context.stream()
                .map(this::toCitation)
                .distinct()
                .toList();

        return new RagAnswer(answer, citations);
    }

    private String buildPrompt(String question, List<Document> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un asistente de normativa. Responde la pregunta usando ÚNICAMENTE el ");
        sb.append("contexto entregado. Si el contexto no contiene la respuesta, indícalo claramente ");
        sb.append("en vez de inventar. Cita la fuente de lo que afirmes.\n\n");
        sb.append("Contexto:\n");
        for (Document d : context) {
            sb.append("[").append(d.getMetadata().get("source"))
              .append(", pág. ").append(d.getMetadata().get("page")).append("]\n")
              .append(d.getText()).append("\n\n");
        }
        sb.append("Pregunta: ").append(question);
        return sb.toString();
    }

    private Citation toCitation(Document d) {
        return new Citation(
                (String) d.getMetadata().get("source"),
                ((Number) d.getMetadata().get("page")).intValue());
    }
}
