package cl.brodriguez.compliancerag.web;

import cl.brodriguez.compliancerag.rag.RagAnswer;
import cl.brodriguez.compliancerag.rag.RagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ask")
public class AskController {

    private final RagService ragService;

    public AskController(RagService ragService) {
        this.ragService = ragService;
    }

    /** Responde una pregunta sobre los documentos indexados, con citas. */
    @PostMapping
    public RagAnswer ask(@Valid @RequestBody AskRequest request) {
        return ragService.ask(request.question());
    }
}
