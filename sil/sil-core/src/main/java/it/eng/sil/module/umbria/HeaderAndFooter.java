package it.eng.sil.module.umbria;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/*
 * HeaderAndFooter class
 */
public class HeaderAndFooter extends PdfPageEventHelper {

	// private String content = "numero verde 800 264 760 (dal lunedì al venerdì dalle 08:00 alle 18:00)";
	// private String content2 = "www.agenzialavoro.tn.it";
	// private boolean footerContent = false;
	private boolean footerContentUMB = false;
	// private boolean footerContentModCert = false;
	private String contentUMB = "www.arpalumbria.it";
	// private String contentPAT2 = "Sede Centrale: Piazza Dante, 15 - 38122 Trento - T +39 0461 495111 -
	// www.provincia.tn.it - C.F. e P.IVA 00337460224";
	// private String contentModCert1 = "Modulo certificato ai sensi dell'art. 9, comma 4, della l.p. 23/1992 e
	// approvato con deterrmine n. 803 dd. 12.06.2018 e n. 1401 dd. 29.10.2018 dell'Agenzia del Lavoro.";

	protected Phrase footer;
	protected Phrase header;

	/*
	 * constructor
	 */
	public HeaderAndFooter(boolean footerContentUMB) {
		super();
		this.footerContentUMB = footerContentUMB;
		header = new Phrase("***** Header *****");
		footer = new Phrase("**** Footer ****");
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		Rectangle rect = new Rectangle(36, 5, 559, 50);

		if (footerContentUMB) {
			try {
				rect.setTop(50);

				ColumnText ct = new ColumnText(writer.getDirectContent());
				ct.setSimpleColumn(rect);
				Image image = Image
						.getInstance(ConfigSingleton.getRootPath() + "/WEB-INF/report/img/UMB_linea_grigia.png");
				image.scaleToFit(100, 100);
				image.scaleAbsoluteHeight(7);
				image.scaleAbsoluteWidth(520);
				ct.addText(new Chunk(image, 0, 0));
				ct.go();

				PdfGState gState = new PdfGState();
				gState.setFillOpacity(0.5f);
				writer.getDirectContent().setGState(gState);

				Font smallfont = FontFactory.getFont("Helvetica", 9, Font.NORMAL);
				Paragraph para = new Paragraph();// content,smallfont);
				para.setAlignment(Element.ALIGN_CENTER);
				para.add(new Chunk(contentUMB, smallfont));
				para.add(Chunk.NEWLINE);
				ct.addElement(para);
				ct.go();

			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			} catch (Exception e) {
				throw new ExceptionConverter(e);
			}
		}

	}
}
