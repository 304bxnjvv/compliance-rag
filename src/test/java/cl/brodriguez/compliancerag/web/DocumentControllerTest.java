package cl.brodriguez.compliancerag.web;

import cl.brodriguez.compliancerag.ingestion.DocumentParseException;
import cl.brodriguez.compliancerag.ingestion.IngestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentControllerTest {

    private final IngestionService ingestionService = mock(IngestionService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new DocumentController(ingestionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void postDocuments_subeElPdfEIndexaYResponde201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "norma.pdf", "application/pdf", "contenido pdf".getBytes());

        mvc.perform(multipart("/documents").file(file))
                .andExpect(status().isCreated());

        verify(ingestionService).ingest(eq("norma.pdf"), any());
    }

    @Test
    void getDocuments_devuelveLaListaDeNombresIndexados() throws Exception {
        when(ingestionService.listDocuments()).thenReturn(List.of("norma.pdf", "ley.pdf"));

        mvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("norma.pdf"))
                .andExpect(jsonPath("$[1]").value("ley.pdf"));
    }

    @Test
    void postDocuments_cuandoElDocumentoNoSePuedeProcesar_responde400() throws Exception {
        doThrow(new DocumentParseException("PDF corrupto", new RuntimeException()))
                .when(ingestionService).ingest(eq("malo.pdf"), any());
        MockMultipartFile file = new MockMultipartFile(
                "file", "malo.pdf", "application/pdf", "x".getBytes());

        mvc.perform(multipart("/documents").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
