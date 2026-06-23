package cl.brodriguez.compliancerag.web;

import cl.brodriguez.compliancerag.ingestion.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final IngestionService ingestionService;

    public DocumentController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    /** Sube un documento (PDF), lo procesa e indexa. */
    @PostMapping
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) throws IOException {
        ingestionService.ingest(file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Lista los nombres de los documentos indexados. */
    @GetMapping
    public List<String> list() {
        return ingestionService.listDocuments();
    }
}
