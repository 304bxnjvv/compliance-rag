package cl.brodriguez.compliancerag.ingestion;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extrae el texto de un PDF página por página y lo divide en fragmentos
 * ({@link Document}) con metadata de origen (archivo y nº de página).
 * El sub-chunking ({@link TokenTextSplitter}) mantiene los fragmentos en un
 * tamaño óptimo para la recuperación y conserva la metadata de cada página.
 */
@Component
public class PdfDocumentParser implements DocumentParser {

    private final TokenTextSplitter splitter;

    public PdfDocumentParser(@Value("${rag.chunk-size:800}") int chunkSize) {
        // chunkSize tokens, minChunkSizeChars=5, minChunkLengthToEmbed=1, maxNumChunks=10000, keepSeparator, corta en fin de oración
        this.splitter = new TokenTextSplitter(chunkSize, 5, 1, 10000, true, List.of('.', '?', '!', '\n'));
    }

    @Override
    public List<Document> parse(String filename, byte[] content) {
        List<Document> pages = new ArrayList<>();
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
                pages.add(new Document(text, Map.of("source", filename, "page", page)));
            }
        } catch (IOException e) {
            throw new DocumentParseException("No se pudo leer el PDF: " + filename, e);
        }
        return splitter.apply(pages);
    }
}
