/*
 * Creato il Feb 21, 2005
 * 
 */
package it.eng.sil.action.report.utils.inet;

import java.io.File;
import java.util.Locale;

import com.inet.report.Area;
import com.inet.report.Engine;
import com.inet.report.GeneralProperties;
import com.inet.report.RDC;
import com.inet.report.ReportException;
import com.inet.report.Section;

/**
 * @author Andrea Savino
 * 
 *         Creazione di un report in cui viene scritto un semplice testo.
 */
public class Esempio1 {
	public Esempio1() {
		try {
			System.out.println("Esempio 1");
			System.out.println("\tCreazione dell'engine.....");
			Engine engine = RDC.createEmptyEngine(Engine.EXPORT_PDF);
			engine.setReportTitle("Report di Esempio n. 1");
			engine.setLocale(new Locale("IT", "it"));
			ReportUtils.initReport(engine);
			System.out.println("\tViene aggiunta una sezione all'area di dettaglio");
			Area detailArea = engine.getArea("D");
			// viene aggiunta una sezione
			Section section = detailArea.addSection();
			Style s = new StyleFactory.Campo();
			// modifica dello stile: l'allineamento ora e' centrato
			s.setAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
			// modifica del font originario
			s.setFontSize(20);
			// aggiunta della stringa di testo alla sezione a partire dal
			// margine dx (0 mm)
			ReportUtils.addText(section, "Ciao mondo", s, StyleUtils.toTwips(0));
			System.out.println("\tLa stringa da stampare e' stata inserita nel report");
			// esecuzione del report
			engine.execute();
			// registrazione del file xml rappresentazione dell'engine (puo'
			// essere aperto col designer)
			RDC.saveEngine(new File("c:\\esempio1.rpt"), engine);
			System.out.println("\tL'engine xml e' stato salvato su disco (C:\\esempio1.rpt)");
			// salvataggio del report nel formato voluto
			File file = new File("c:/esempio1.pdf");
			java.io.FileOutputStream fout = new java.io.FileOutputStream(file);
			// il report viene suddiviso in pagine
			System.out.println("\tGenerazione del report in corso......");
			int numPag = engine.getPageCount();
			for (int i = 1; i <= numPag; i++) {
				// per ogni pagina si preleva l'array di byte
				fout.write(engine.getPageData(i));
			}
			fout.close();
			System.out.println("\tC:\\esempio1.pdf generato con successo");
		} catch (ReportException re) {
			re.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Fine esempio 1");
		}
	}

	public static void main(String[] s) {
		Esempio1 e = new Esempio1();
	}
}
