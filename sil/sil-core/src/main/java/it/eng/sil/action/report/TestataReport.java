/*
 * TestataReport.java
 *
 * Created on 20 aprile 2004, 23.27
 */

package it.eng.sil.action.report;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Area;
import com.inet.report.Box;
import com.inet.report.Engine;
import com.inet.report.Paragraph;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.sil.action.report.test.Style;
import it.eng.sil.action.report.test.StyleFactory;
import it.eng.sil.action.report.test.StyleUtils;
import it.eng.sil.util.Utils;

/**
 * 
 * @author Administrator
 */
public class TestataReport {

	/** Creates a new instance of TestataReport */
	public TestataReport() {
	}

	/**
	 * Maledizioni a chi non toglie la roba vecchia
	 */
	@Deprecated
	private static Engine inserisciTestata(Engine engine, String rootPath, SourceBean infoGenerali) throws Exception {
		Area details = engine.getArea("D");
		setTestata(details, rootPath, infoGenerali);
		return engine;
	}

	private static void setTestata(Area area, String rootPath, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		String cpi = (String) row.getAttribute("CPI");
		String provincia = (String) row.getAttribute("PROVINCIA");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		String indirizzo = (String) row.getAttribute("INDIRIZZO");
		//
		Section sec = area.getSection(0);
		sec.setHeight(StyleUtils.toInch(200));

		Box box = sec.addBox(0, 0, StyleUtils.toInch(1450), StyleUtils.toInch(200));

		//
		Text text = sec.addText(StyleUtils.toInch(100), StyleUtils.toInch(2), StyleUtils.toInch(1250),
				StyleUtils.toInch(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(15);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
		tp.setUnderline(true);
		//
		text = sec.addText(StyleUtils.toInch(100), StyleUtils.toInch(100), StyleUtils.toInch(350),
				StyleUtils.toInch(80));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = StyleFactory.getStyle(Style.CAMPO);
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toInch(470), StyleUtils.toInch(100), StyleUtils.toInch(350),
				StyleUtils.toInch(80));
		p = text.addParagraph();
		tp = p.addTextPart("fax. " + Utils.notNull(fax));
		s = StyleFactory.getStyle(Style.CAMPO);
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toInch(840), StyleUtils.toInch(100), StyleUtils.toInch(580),
				StyleUtils.toInch(80));
		p = text.addParagraph();
		tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		s = StyleFactory.getStyle(Style.CAMPO);
		s.setStyle(tp);
		tp.setBold(true);
		// /////////
		sec.addBox(StyleUtils.toInch(1450), 0, StyleUtils.toInch(550), StyleUtils.toInch(200));
		text = sec.addText(StyleUtils.toInch(1600), StyleUtils.toInch(2), StyleUtils.toInch(350),
				StyleUtils.toInch(80));
		p = text.addParagraph();
		tp = p.addTextPart("PROVINCIA DI " + Utils.notNull(provincia).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
		text.setCanGrow(true);
		//
		sec.addPicture(StyleUtils.toInch(1480), StyleUtils.toInch(26), StyleUtils.toInch(79), StyleUtils.toInch(79),
				rootPath + "/img/loghi/logo.gif");

	}
}
