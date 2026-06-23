package cl.brodriguez.compliancerag.ingestion;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfDocumentParserTest {

    @Test
    void parse_extraeElTextoConNombreDeArchivoYNumeroDePagina() throws IOException {
        byte[] pdf = pdfDeUnaPagina("Norma de prueba sobre proteccion de datos personales del cliente.");
        PdfDocumentParser parser = new PdfDocumentParser();

        List<Document> fragmentos = parser.parse("norma.pdf", pdf);

        assertThat(fragmentos).isNotEmpty();
        Document primero = fragmentos.get(0);
        assertThat(primero.getText()).contains("proteccion de datos");
        assertThat(primero.getMetadata()).containsEntry("source", "norma.pdf");
        assertThat(primero.getMetadata()).containsEntry("page", 1);
    }

    /** Genera en memoria un PDF de una página con el texto dado. */
    private byte[] pdfDeUnaPagina(String texto) throws IOException {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                cs.showText(texto);
                cs.endText();
            }
            doc.save(out);
            return out.toByteArray();
        }
    }
}
