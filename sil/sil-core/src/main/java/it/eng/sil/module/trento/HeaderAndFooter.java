package it.eng.sil.module.trento;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

/*
 * HeaderAndFooter class
 */
public class HeaderAndFooter extends PdfPageEventHelper {

	private String content = "numero verde 800 264 760 (dal lunedì al venerdì dalle 08:00 alle 18:00)";
	private String content2 = "www.agenzialavoro.tn.it";
	private boolean footerContent = false;
	private boolean footerContentPAT = false;
	private boolean footerContentModCert = false;
	private String contentPAT1 = "Provincia autonoma di Trento";
	private String contentPAT2 = "Sede Centrale: Piazza Dante, 15 - 38122 Trento - T +39 0461 495111 - www.provincia.tn.it - C.F. e P.IVA 00337460224";
	private String contentModCert1 = "Modulo certificato ai sensi dell’art. 9, comma 4, della l.p. 23/1992 e approvato con deterrmine n. 803 dd. 12.06.2018 e n. 1401 dd. 29.10.2018 dell’Agenzia del Lavoro.";


	protected Phrase footer;
	protected Phrase header;


	/*
	 * constructor
	 */
	public HeaderAndFooter(boolean footerContent , boolean footerContentPAT, boolean footerContentModCert) {

		super();

		this.footerContent = footerContent;
		this.footerContentPAT = footerContentPAT;
		this.footerContentModCert = footerContentModCert;


		header = new Phrase("***** Header *****");
		footer = new Phrase("**** Footer ****");
	}


	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		Rectangle rect = new Rectangle(36, 5, 559, 50);


		if(footerContent && footerContentPAT && footerContentModCert  ) {
			try {
				rect.setTop(100);
				PdfGState gState = new PdfGState();
				gState.setFillOpacity(0.5f);
				writer.getDirectContent().setGState(gState);
				ColumnText ct = new ColumnText(writer.getDirectContent());


				rect.setBorderColor(BaseColor.BLACK);
				rect.setBorderWidth(2);
				ct.setSimpleColumn(rect);
				LineSeparator line = new LineSeparator(); 
				line.setOffset(-2);
				ct.addElement(line);
				ct.go();

				Font smallfont = FontFactory.getFont("Arial", 7,Font.NORMAL);
				Paragraph para = new Paragraph();//content,smallfont);
				para.setAlignment(Element.ALIGN_CENTER);
				para.add(new Chunk(content,smallfont));
				para.add(Chunk.NEWLINE);
				para.add(new Chunk(content2,smallfont));
				para.add(Chunk.NEWLINE);
				para.add(new Chunk(contentPAT1,smallfont));
				para.add(Chunk.NEWLINE);
				para.add(new Chunk(contentPAT2,smallfont));
				para.add(Chunk.NEWLINE);
				para.add(new Chunk(contentModCert1,smallfont));
				ct.addElement(para);
				ct.go();
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}
		else if ((footerContent && footerContentPAT ) || (footerContent && footerContentModCert) || (footerContentPAT && footerContentModCert)) {
			try {
				rect.setTop(75);
				PdfGState gState = new PdfGState();
				gState.setFillOpacity(0.5f);
				writer.getDirectContent().setGState(gState);
				ColumnText ct = new ColumnText(writer.getDirectContent());


				rect.setBorderColor(BaseColor.BLACK);
				rect.setBorderWidth(2);
				ct.setSimpleColumn(rect);
				LineSeparator line = new LineSeparator(); 
				line.setOffset(-2);
				ct.addElement(line);
				ct.go();

				Font smallfont = FontFactory.getFont("Arial", 7,Font.NORMAL);
				Paragraph para = new Paragraph();//content,smallfont);
				para.setAlignment(Element.ALIGN_CENTER);
				if (footerContent) {
					para.add(new Chunk(content,smallfont));
					para.add(Chunk.NEWLINE);
					para.add(new Chunk(content2,smallfont));
				}
				
				if (footerContentPAT) {
					para.add(Chunk.NEWLINE);
					para.add(new Chunk(contentPAT1,smallfont));
					para.add(Chunk.NEWLINE);
					para.add(new Chunk(contentPAT2,smallfont));
				}
				if (footerContentModCert) {
					para.add(Chunk.NEWLINE);
					para.add(new Chunk(contentModCert1,smallfont));
				}
				ct.addElement(para);
				ct.go();
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}
		else if(footerContent){
			try {
				PdfGState gState = new PdfGState();
				gState.setFillOpacity(0.5f);
				writer.getDirectContent().setGState(gState);
				ColumnText ct = new ColumnText(writer.getDirectContent());
				//        ct.setSimpleColumn(new Rectangle(36, 832, 559, 810));
				//        ct.addElement(new Paragraph("1:"+content));
				//        ct.go();
				//rect = new Rectangle(36, 5, 559, 50);

				rect.setBorderColor(BaseColor.BLACK);
				rect.setBorderWidth(2);
				ct.setSimpleColumn(rect);
				LineSeparator line = new LineSeparator(); 
				line.setOffset(-2);
				ct.addElement(line);
				ct.go();

				Font smallfont = FontFactory.getFont("Arial", 7,Font.NORMAL);
				Paragraph para = new Paragraph();//content,smallfont);
				para.setAlignment(Element.ALIGN_CENTER);
				para.add(new Chunk(content,smallfont));
				para.add(Chunk.NEWLINE);
				para.add(new Chunk(content2,smallfont));
				ct.addElement(para);
				ct.go();
				//Font smallfont = FontFactory.getFont("Arial", 7);
				//	        Paragraph para2 = new Paragraph(content2,smallfont);
				//	        para.setAlignment(Element.ALIGN_CENTER);
				//	        ct.addElement(para2);
				//	        ct.go();
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}
		else if(footerContentPAT){
			try {
				PdfGState gState = new PdfGState();
				gState.setFillOpacity(0.5f);
				writer.getDirectContent().setGState(gState);
				ColumnText ct = new ColumnText(writer.getDirectContent());
				//rect = new Rectangle(36, 5, 559, 50);
				rect.setBorderColor(BaseColor.BLACK);
				rect.setBorderWidth(2);
				ct.setSimpleColumn(rect);
				LineSeparator line = new LineSeparator(); 
				line.setOffset(-2);
				ct.addElement(line);
				ct.go();

				Font smallfont = FontFactory.getFont("Arial", 7,Font.NORMAL);
				Paragraph para = new Paragraph();//content,smallfont);
				para.setAlignment(Element.ALIGN_CENTER);
				para.add(new Chunk(contentPAT1,smallfont));
				para.add(Chunk.NEWLINE);
				para.add(new Chunk(contentPAT2,smallfont));
				ct.addElement(para);
				ct.go();
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}
		else if(footerContentModCert){
			try {
				PdfGState gState = new PdfGState();
				gState.setFillOpacity(0.5f);
				writer.getDirectContent().setGState(gState);
				ColumnText ct = new ColumnText(writer.getDirectContent());
				//rect = new Rectangle(36, 5, 559, 50);
				rect.setBorderColor(BaseColor.BLACK);
				rect.setBorderWidth(2);
				ct.setSimpleColumn(rect);
				LineSeparator line = new LineSeparator(); 
				line.setOffset(-2);
				ct.addElement(line);
				ct.go();

				Font smallfont = FontFactory.getFont("Arial", 7,Font.NORMAL);
				Paragraph para = new Paragraph();//content,smallfont);
				para.setAlignment(Element.ALIGN_CENTER);
				para.add(new Chunk(contentModCert1,smallfont));
				ct.addElement(para);
				ct.go();
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}




	}

	//public void onEndPage(PdfWriter writer, Document document) {
	//
	//    PdfContentByte cb = writer.getDirectContent();
	//
	//    //header content
	//    String headerContent = content;
	//
	//    //header content
	//    String footerContent = headerContent;
	//    /*
	//     * Header
	//     */
	////    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(headerContent), 
	////            document.leftMargin() - 1, document.top() + 10, 0);
	//
	//    /*
	//     * Foooter
	//     */
	//    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(new com.itextpdf.text.Chunk(footerContent)),
	////    		(document.left() + document.right()) /2 , document.bottom() - 1, 0);
	////36-700-200-806
	//    Rectangle rect = new Rectangle(36,200,550,806);
	////        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Left"), rect.getLeft(), rect.getBottom(), 0);
	////        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Right"), rect.getRight(), rect.getBottom(), 0);
	//    ColumnText ct = new ColumnText(cb);
	//    ct.setSimpleColumn(36, document.bottom() - 20,550,document.bottom());
	//    ct.addElement(new Paragraph(headerContent));
	//    try {
	//		ct.go();
	//	} catch (DocumentException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	////    ct.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(new com.itextpdf.text.Chunk("I want to add this text in a rectangleI want to add this text in a rectangleI want to add this text in a rectangleI want to add this text in a rectangleI want to add this text in a rectangleI want to add this text in a rectangleI want"
	////    		+ " to add this text in a rectangleI want to add this text in a rectangle defined by the coordinates llx = 36, lly = 600, urx = 200, ury = 800")),
	////    		(document.left() + document.right()) /2 , document.bottom() - 1, 0);
	////    
	//}

}
