/*
 * StyleExample.java
 *
 * Created on 19 gennaio 2004, 23.44
 */

package it.eng.sil.action.report.utils.inet;

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
 * @author Andrea Savino
 */
public class StyleExample {

	public StyleExample() {
		try {

			// creare le variabili dei tipi di stile che verranno utilizzati nel
			// report
			StyleFactory style = new StyleFactory();
			Style campo = style.getStyle(StyleFactory.CAMPO);
			Style titoloSezione = style.getStyle(StyleFactory.TITOLO_SEZIONE);
			Style etichetta = style.getStyle(StyleFactory.ETICHETTA);
			Style etichetta2 = style.getStyle(StyleFactory.ETICHETTA2);
			// nel caso in cui si debba usare uno stile non previsto nel
			// factory, allore se ne puo'
			// creare uno a partire da quello piu' simile.
			Style etichettaParticolare = (Style) etichetta.clone();
			etichettaParticolare.setItalic(true);
			//
			Engine eng = RDC.createEmptyEngine(Engine.EXPORT_PDF);
			eng.setReportTitle("report di test: Ciao mondo");
			Area details = eng.getArea("D"); // D: area Dettaglio
			Section section = details.getSection(0);
			ReportUtils.addText(section, "Titolo sez.", titoloSezione, 500);
			section = details.addSection();

			ReportUtils.addText(section, "ciao mondo", etichetta2, 000);
			ReportUtils.addText(section, "bye bye", campo, 800);
			section = details.addSection();
			Text t = ReportUtils.addText(section, "data ", etichetta, 200);
			Paragraph p = t.getParagraph(0);
			p = ReportUtils.addTextPart(p, "11", campo);
			p = ReportUtils.addTextPart(p, " / ", etichetta);
			p = ReportUtils.addTextPart(p, "2004", campo);
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
			//

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] a) {
		StyleExample s = new StyleExample();
	}
}
