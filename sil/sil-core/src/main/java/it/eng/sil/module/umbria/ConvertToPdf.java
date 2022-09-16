package it.eng.sil.module.umbria;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class ConvertToPdf {
	/**
	 * converte un html in un pdf
	 * 
	 * @param html
	 *            La stringa html da convertire in xhtml
	 * @param stream
	 *            Lo stream della response
	 * @return OutputStream Ritorna il pdf
	 * @throws DocumentException
	 * @throws IOException
	 */

	public OutputStream createPDF(String html, OutputStream stream, boolean footerContentUMB)
			throws DocumentException, IOException {
		PdfWriter pdfWriter = null;
		Document document = new Document(); // create a new document
		pdfWriter = PdfWriter.getInstance(document, stream); // get Instance of the PDFWriter
		document.addAuthor("Engineering"); // document header attributes
		document.addCreationDate();
		document.addProducer();
		document.addCreator("Engineering");
		document.addTitle("stampa");
		document.setPageSize(PageSize.A4);
		document.setMargins(20, 20, 20, 50);

		document.open(); // open document

		pdfWriter.setPageEvent(new HeaderAndFooter(footerContentUMB));

		InputStream is = new ByteArrayInputStream(html.getBytes());
		InputStreamReader isr = new InputStreamReader(is); // create new input stream reader
		XMLWorkerHelper worker = XMLWorkerHelper.getInstance(); // get the XMLWorkerHelper Instance
		worker.parseXHtml(pdfWriter, document, isr); // convert to PDF
		document.close(); // close the document
		pdfWriter.close(); // close the writer

		return stream;

	}

}
