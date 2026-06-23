package cl.brodriguez.compliancerag.ingestion;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extrae el texto de un PDF página por página, generando un fragmento
 * ({@link Document}) por página con metadata de origen (archivo y nº de página),
 * necesaria para citar la fuente en las respuestas.
 */
@Component
public class PdfDocumentParser implements DocumentParser {

    @Override
    public List<Document> parse(String filename, byte[] content) {
        List<Document> fragments = new ArrayList<>();
        try (PDDocument pdf = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = pdf.getNumberOfPages();
            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(pdf).strip();
                if (text.isEmpty()) {
                    continue;
                }
                fragments.add(new Document(text, Map.of("source", filename, "page", page)));
            }
        } catch (IOException e) {
            throw new DocumentParseException("No se pudo leer el PDF: " + filename, e);
        }
        return fragments;
    }
}
