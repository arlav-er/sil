package it.eng.sil.module.trento;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;




public class ConvertToPdf {
	/**
	 * converte un html in un pdf
	 * 
	 * @param html La stringa html da convertire in xhtml
	 * @param stream Lo stream della response
	 * @return OutputStream Ritorna il pdf 
	 * @throws DocumentException
	 * @throws IOException
	 */

	public OutputStream createPDF(String html, OutputStream stream,boolean footerContent, boolean footerContentPAT, boolean footerContentModCert) throws DocumentException, IOException {
		PdfWriter pdfWriter = null;


		// create a new document
		Document document = new Document();

		// get Instance of the PDFWriter
		pdfWriter = PdfWriter.getInstance(document, stream);

		// document header attributes
		document.addAuthor("Engineering");
		document.addCreationDate();
		document.addProducer();
		document.addCreator("Engineering");
		document.addTitle("stampa");
		document.setPageSize(PageSize.A4);
		document.setMargins(20, 20, 20, 50);

	   
		// open document
		document.open();
		
		pdfWriter.setPageEvent(new HeaderAndFooter(footerContent, footerContentPAT, footerContentModCert));
		
		InputStream is = new ByteArrayInputStream(html.getBytes());
		
		// create new input stream reader
		InputStreamReader isr = new InputStreamReader(is);

		// get the XMLWorkerHelper Instance
		XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
		
		// convert to PDF
		worker.parseXHtml(pdfWriter, document, isr);

		// close the document
		document.close();
		
		// close the writer
		pdfWriter.close();

		return stream;

	}

}
