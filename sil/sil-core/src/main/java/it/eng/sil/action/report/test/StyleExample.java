/*
 * StyleExample.java
 *
 * Created on 19 gennaio 2004, 23.44
 */

package it.eng.sil.action.report.test;

import java.io.File;

import com.inet.report.Area;
import com.inet.report.Element;
import com.inet.report.Engine;
import com.inet.report.Paragraph;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Subreport;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * 
 * @author Administrator
 */
public class StyleExample {

	/** Creates a new instance of StyleExample */

	public StyleExample() {
		try {
			Engine eng = RDC.createEmptyEngine(Engine.EXPORT_PDF);
			eng.setReportTitle("report di test: Ciao mondo");
			Area details = eng.getArea("D");
			Section section = details.getSection(0);
			addText(section, "Titolo sez.", Style.TITOLO_SEZIONE, 500);
			section = details.addSection();
			addText(section, "ciao mondo", Style.ETICHETTA2, 000);
			addText(section, "quel porco", Style.CAMPO, 800);
			section = details.addSection();
			Text t = addText(section, "data ", Style.ETICHETTA, 200);
			Paragraph p = t.getParagraph(0);
			p = addTextPart(p, "11", Style.CAMPO, 100);
			p = addTextPart(p, " / ", Style.ETICHETTA, 50);
			p = addTextPart(p, "2004", Style.CAMPO, 200);
			// includi sottoreport
			section = details.addSection();
			Subreport sub = section.addSubreport(0, 0, 2000 * 4, 200, "file:d:/report/sotto_report.rpt");
			Engine subEngine = sub.getEngine();
			Area subDet = subEngine.getArea("D");
			Section subSec = subDet.getSection(0);
			Element els[] = subSec.getElements();
			for (int i = 0; i < els.length; i++) {
				if (els[i] instanceof Text) {
					Text tx = (Text) els[i];
					int pCount = tx.getParagraphCount();
					for (int j = 0; j < pCount; j++) {
						Paragraph px = tx.getParagraph(j);
						int partCount = px.getPartCount();
						for (int f = 0; f < partCount; f++) {
							TextPart tpx = (TextPart) px.getPart(f);
							tpx.setText(tpx.getText() + " cucu");
						}
					} /*
						 * TextPart tpx = (TextPart)px.getPart(0); String testo = tpx.getText(); testo = testo +" cucu";
						 * tpx.setText(testo);
						 */
				}
			}
			RDC.saveEngine(new File("d:/report/ciaoMondo.rpt"), eng);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Text addText(Section section, String text, int style, int x) throws Exception {
		Style s = StyleFactory.getStyle(style);
		Text t = section.addText(StyleUtils.toInch(x), 0, s.getWidth(), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);
		TextPart tp = p.addTextPart(text);
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		return t;
	}

	public Text addText(Section section, String text, int style, int x, int width) throws Exception {
		Style s = StyleFactory.getStyle(style);
		Text t = section.addText(StyleUtils.toInch(x), 0, width, s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);
		TextPart tp = p.addTextPart(text);
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		return t;
	}

	public Paragraph addTextPart(Paragraph p, String text, int style, int x) throws Exception {
		Style s = StyleFactory.getStyle(style);

		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);
		TextPart tp = p.addTextPart(text);
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(tp);
		return p;
	}

	public Text addText(Section section, String text, Style s, int x) throws Exception {
		// Style s = StyleFactory.getStyle(style);
		Text t = section.addText(StyleUtils.toInch(x), 0, s.getWidth(), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);
		TextPart tp = p.addTextPart(text);
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		return t;
	}

	public static void main(String[] a) {
		StyleExample s = new StyleExample();
	}
}
