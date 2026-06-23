package cl.brodriguez.compliancerag.web;

import cl.brodriguez.compliancerag.rag.Citation;
import cl.brodriguez.compliancerag.rag.RagAnswer;
import cl.brodriguez.compliancerag.rag.RagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AskControllerTest {

    private final RagService ragService = mock(RagService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AskController(ragService)).build();
    }

    @Test
    void postAsk_devuelveLaRespuestaYLasCitas() throws Exception {
        when(ragService.ask("Que exige la norma"))
                .thenReturn(new RagAnswer("La norma exige cifrado.", List.of(new Citation("norma.pdf", 2))));

        mvc.perform(post("/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"Que exige la norma\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("La norma exige cifrado."))
                .andExpect(jsonPath("$.citations[0].source").value("norma.pdf"))
                .andExpect(jsonPath("$.citations[0].page").value(2));
    }
}
