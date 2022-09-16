/*
 * CiaoMondo.java
 *
 * Created on 18 gennaio 2004, 11.19
 */

package it.eng.sil.action.report.test;

import java.io.File;

import com.inet.report.Area;
import com.inet.report.Engine;
import com.inet.report.GeneralProperties;
import com.inet.report.Paragraph;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * 
 * @author Administrator
 */
public class CiaoMondo {

	/** Creates a new instance of CiaoMondo */
	public CiaoMondo() {
		System.out.println("CiaoMondo avviato");
		try {
			Engine eng = RDC.createEmptyEngine(Engine.EXPORT_PDF);
			eng.setReportTitle("report di test: Ciao mondo");
			Area details = eng.getArea("D");
			Section section = details.getSection(0);
			Text text1 = section.addText(2000, 0, 3000, 248);
			Paragraph paragraph1 = text1.addParagraph();
			paragraph1.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_RIGHT);
			TextPart textPart1 = paragraph1.addTextPart("Ciao mondo");
			textPart1.setFontSize(12);
			textPart1.setFontColor(RDC.COLOR_BLUE);
			textPart1.setItalic(false);
			textPart1.setBold(false);
			int fst = textPart1.getFontSizeTwips();
			section.setHeight(248);
			int h = section.getHeight();
			Section section1 = details.addSection();
			Text text = section1.addText(2000, 0, 3000, 248);
			section1.setHeight(248);
			Paragraph paragraph = text.addParagraph();
			paragraph.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_RIGHT);
			TextPart textPart = paragraph.addTextPart("Altro mondo: " + fst + " h:" + h);
			textPart.setFontSize(12);
			textPart.setFontColor(RDC.COLOR_BLUE);
			textPart.setItalic(true);
			textPart.setBold(true);
			textPart.setUnderline(true);
			Section section2 = details.addSection();
			Text text2 = section2.addText(2000, 0, 3000, 248);
			section2.setHeight(248 * 2);
			Paragraph paragraph2 = text2.addParagraph();
			paragraph2.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_RIGHT);
			TextPart textPart2 = paragraph2.addTextPart("Altro mondo, dds, sddsfds , sdsdfds  , sdfsdf dsds, sdfsdf");
			textPart2.setFontSize(12);
			textPart2.setFontColor(RDC.COLOR_BLUE);
			//
			text = section2.addText(2000, 0 + 248, 3000, 248);
			paragraph = text.addParagraph();
			paragraph.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
			textPart = paragraph.addTextPart("Ciccio bello:" + text2.getHeight());
			textPart.setFontSize(12);
			textPart.setFontColor(RDC.COLOR_LIME);
			textPart.setFontName("arial narrow");

			// paragraph2.setCanGrow(true);
			text2.setCanGrow(true);
			//
			RDC.saveEngine(new File("d:/report/ciaoMondo.rpt"), eng);
			System.out.println("CiaoMondo scritto in d:/report");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("CiaoMondo terminato");
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		CiaoMondo cm = new CiaoMondo();
	}

}
