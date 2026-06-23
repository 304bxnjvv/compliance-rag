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
        PdfDocumentParser parser = new PdfDocumentParser(800);

        List<Document> fragmentos = parser.parse("norma.pdf", pdf);

        assertThat(fragmentos).isNotEmpty();
        Document primero = fragmentos.get(0);
        assertThat(primero.getText()).contains("proteccion de datos");
        assertThat(primero.getMetadata()).containsEntry("source", "norma.pdf");
        assertThat(primero.getMetadata()).containsEntry("page", 1);
    }

    @Test
    void parse_divideUnaPaginaLargaEnVariosFragmentosConservandoLaMetadata() throws IOException {
        byte[] pdf = pdfDeVariasLineas(12, "El responsable debe proteger los datos personales del cliente.");
        PdfDocumentParser parser = new PdfDocumentParser(20); // chunk pequeño para forzar la división

        List<Document> fragmentos = parser.parse("norma.pdf", pdf);

        assertThat(fragmentos).hasSizeGreaterThan(1);
        assertThat(fragmentos).allSatisfy(f ->
                assertThat(f.getMetadata())
                        .containsEntry("source", "norma.pdf")
                        .containsEntry("page", 1));
    }

    private byte[] pdfDeUnaPagina(String texto) throws IOException {
        return pdfDeVariasLineas(1, texto);
    }

    /** Genera en memoria un PDF de una página con `numLineas` repeticiones de `linea`. */
    private byte[] pdfDeVariasLineas(int numLineas, String linea) throws IOException {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.setLeading(14);
                cs.newLineAtOffset(50, 750);
                for (int i = 0; i < numLineas; i++) {
                    cs.showText(linea);
                    cs.newLine();
                }
                cs.endText();
            }
            doc.save(out);
            return out.toByteArray();
        }
    }
}
