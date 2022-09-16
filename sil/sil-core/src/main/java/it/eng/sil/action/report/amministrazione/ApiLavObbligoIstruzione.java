package it.eng.sil.action.report.amministrazione;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Area;
import com.inet.report.Element;
import com.inet.report.Engine;
import com.inet.report.FieldPart;
import com.inet.report.Fields;
import com.inet.report.Line;
import com.inet.report.Paragraph;
import com.inet.report.Picture;
import com.inet.report.RDC;
import com.inet.report.ReportProperties;
import com.inet.report.Section;
import com.inet.report.SpecialField;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.util.Utils;

public class ApiLavObbligoIstruzione {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";

	private int LEFT = 80;
	private Vector elencoLav = null;
	private String numProt = null;
	private String numAnno = null;
	private String dataProt = null;
	private String immagineCpi = null;
	private String immagineProvincia = null;
	private SourceBean config = null;

	//
	private Engine eng = null;
	private String installAppPath = null;
	private StyleFactory styleFactory;
	private String fileType = Engine.EXPORT_PDF;

	public String getFileType() {
		return this.fileType;
	}

	public void setInstallAppPath(String installAppPath) {
		this.installAppPath = installAppPath;
	}

	public void setFileType(String eft) {
		if (eft.equalsIgnoreCase("PDF"))
			this.fileType = Engine.EXPORT_PDF;
		else if (eft.equalsIgnoreCase("RTF"))
			this.fileType = Engine.EXPORT_RTF;
		else if (eft.equalsIgnoreCase("HTML"))
			this.fileType = Engine.EXPORT_HTML;
		else if (eft.equalsIgnoreCase("XLS"))
			this.fileType = Engine.EXPORT_XLS;

		else
			this.fileType = Engine.EXPORT_PDF;

	}

	public void setElencolav(Vector elencoLav) {
		this.elencoLav = elencoLav;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiLavObbligoIstruzione(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Lavoratori tipo condizione");
			eng.setLocale(new Locale("IT", "it"));
			initReport(eng);

			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toTwips(1));

			Area rh = eng.getArea("RH");
			rh.getSection(0).setHeight(StyleUtils.toTwips(0));
			stampaIntestazioneCpi(rh);
			testata(rh);
			addBR(rh, 20);

			Area details = eng.getArea("D");

			stampaIntestazione(ph, elencoLav, true);
			stampaElencoLav(details, elencoLav, false);
		} catch (Exception e) {
			throw e;
		}
	}

	private void testata(Area area) throws Exception {
		Section sec = area.addSection();

		styleFactory = new StyleFactory();

		Text text = sec.addText(StyleUtils.toTwips(2250), 20, StyleUtils.toTwips(1300), 20);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Data di stampa ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(2500), 20, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();

		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		tp = p.addTextPart(sdf.format(d));
		s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(1200), StyleUtils.toTwips(100), StyleUtils.toTwips(1400),
				StyleUtils.toTwips(100));
		p = text.addParagraph();
		p.setHeight(0);
		tp = p.addTextPart("LISTA LAVORATORI");
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
	}

	private void stampaLav(Area details) throws Exception {
		String codCpi = "";
	}

	private void stampaIntestazioneCpi(Area area) throws Exception {

		Section section = area.addSection();
		String nomeFile = (String) (config.getAttribute("LOGO_PROVINCIA.FILE"));
		int larg = (new Integer(((String) config.getAttribute("LOGO_PROVINCIA.WIDTH")))).intValue();
		int alt = (new Integer(((String) config.getAttribute("LOGO_PROVINCIA.HEIGHT")))).intValue();

		section.addPicture(LEFT + 400, 50, (larg), (alt), installAppPath + nomeFile);

		nomeFile = (String) (config.getAttribute("LOGO_CENTRIIMPIEGO.FILE"));
		larg = (new Integer(((String) config.getAttribute("LOGO_CENTRIIMPIEGO.WIDTH")))).intValue();
		alt = (new Integer(((String) config.getAttribute("LOGO_CENTRIIMPIEGO.HEIGHT")))).intValue();

		section.addPicture(13250, 1, (larg), (alt), installAppPath + nomeFile);
		Vector elements = section.getElementsV();
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				((Element) elements.get(i)).setSuppress(true);
			}
		}
		section = area.addSection();
		section.addHorizontalLine(400, StyleUtils.toTwips(1), 14800);

	}

	private void stampaIntestazione(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setFontName("Times New Roman");
		s.setFontSize(12);
		Text t = null;

		// t = ReportUtils.addText(section, "CF\nCognome Nome", s, LEFT - 15);

		t = ReportUtils.addText(section, "Cognome", s, LEFT - 15);
		t.setHeight(600);

		t = addText(section, "Nome", s, LEFT + 280);
		t.setHeight(600);

		t = addText(section, "Codice Fiscale", s, LEFT + 590);
		t.setHeight(600);

		t = addText(section, "Data di\nNascita", s, LEFT + 1000);
		t.setHeight(600);

		/*
		 * t = addText(section, "Comune di\nnascita", s, LEFT + 780); t.setHeight(600); t = addText(section,
		 * "Cittadinanza", s, LEFT + 1100); t.setHeight(600);
		 */

		t = addText(section, "Comune di nascita ", s, LEFT + 1210);
		t.setHeight(600);

		/*
		 * t = addText(section, "Obbligo di\nformazione", s, LEFT + 1380); t.setHeight(600); t = addText(section,
		 * "Obbligo di\nistruzione", s, LEFT + 1690); t.setHeight(600); t = addText(section, "CPI Titolare", s, LEFT +
		 * 1950); t.setHeight(600); t = addText(section, "Tipo condizione", s, LEFT + 2300); t.setHeight(600);
		 */

		t.setHeight(600);
		t = addText(section, "Obbligo di\nIstruzione", s, LEFT + 1700);
		t.setHeight(600);
		t = addText(section, "CPI Titolare", s, LEFT + 1950);
		t.setHeight(600);
		t = addText(section, "Tipo condizione", s, LEFT + 2300);
		t.setHeight(600);

		addBR(ph, 10);
	}

	private void stampaElencoLav(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			azione(details, row);
		}
	}

	private void azione(Area details, SourceBean infoAzione) throws Exception {
		Section section = details.addSection();
		section.addHorizontalLine(350, StyleUtils.toTwips(1), 14850);

		Element[] elements = section.getElements();
		Line line = null;
		for (int i = 0; i < elements.length; i++)
			if (elements[i].getType() == Element.LINE)
				line = (Line) elements[i];
		try {
			line.setHeight(StyleUtils.toTwips(1));
		} catch (Exception e) {
		}
		section.setHeight(StyleUtils.toTwips(2));

		Text t = null;

		/*
		 * String strCfCognomeNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione,
		 * "strcodicefiscale") + " " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "strcognome") +
		 * " " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "strnome");
		 */

		String strCognome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "strCognome");
		String strNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "strNome");
		String strCf = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "strCf");

		String datNasc = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "datNasc");

		String comNascCittadinanza = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione,
				"comNascCittadinanza");

		String comNasc = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "comNasc");
		String cittadinanza = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "cittadinanza");
		// String flgObbligoFormativo = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione,
		// "FLGOBFORMATIVO");
		String flgObbligoScolastico = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione,
				"FLGOBSCOLASTICO");
		String cpi = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "cpi");
		String tipoCond = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "tipoCond");

		StringBuffer rec = new StringBuffer();

		t = addText(section, strCognome, 8, LEFT - 15, 280);
		t.setCanGrow(true);

		//

		t = addText(section, strNome, 8, LEFT + 280, 295);
		t.setCanGrow(true);

		t = addText(section, strCf, 8, LEFT + 590, 350);
		t.setCanGrow(true);

		// String comCit = comNasc;

		t = addText(section, datNasc, 8, LEFT + 1000, 200);
		t.setCanGrow(true);

		t = addText(section, comNasc, 8, LEFT + 1210, 300);
		t.setCanGrow(true);

		/*
		 * t = addText(section, comNascCittadinanza, 8, LEFT + 1180, 1700); t.setCanGrow(true);
		 */

		/*
		 * t = addText(section, cittadinanza, 8, LEFT + 1100, 280); t.setCanGrow(true);
		 */

		/*
		 * t = addText(section, flgObbligoFormativo, 8, LEFT + 1380, 220); t.setCanGrow(true);
		 */

		t = addText(section, flgObbligoScolastico, 8, LEFT + 1700, 300);
		t.setCanGrow(true);

		t = addText(section, cpi, 8, LEFT + 1950, 300);
		t.setCanGrow(true);

		t = addText(section, tipoCond, 8, LEFT + 2300, 300);
		t.setCanGrow(true);
	}

	private int getMoltSecHeight(int moltCondiz, int moltSpec, int moltRecap, int moltDisp) {
		int ret = 0;
		ret = Math.max(Math.max(moltCondiz, moltSpec), Math.max(moltRecap, moltDisp));
		return ret;
	}

	private void addBR(Area details, int height) throws Exception {
		Section section = details.addSection();
		section.setHeight(StyleUtils.toTwips(height));
	}

	private void initReport(Engine eng) throws Exception {
		eng.getReportProperties().setPaperOrient(ReportProperties.LANDSCAPE, ReportProperties.PAPER_A4);
		Area header = eng.getArea("RH");
		header.setSuppress(false);
		Section sec = header.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));
		//
		header = eng.getArea("PH");
		sec = header.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));
		//
		Area footer = eng.getArea("RF");
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toTwips(0));
		//
		footer = eng.getArea("PF");
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));
		//
		Fields fields = eng.getFields();
		SpecialField pageNumberField = fields.getSpecialField(SpecialField.PAGE_NUMBER);
		SpecialField totalPageCountField = fields.getSpecialField(SpecialField.TOTAL_PAGE_COUNT);
		Text text = sec.addText(StyleUtils.toTwips(1300), 0, StyleUtils.toTwips(300), StyleUtils.toTwips(85));
		Paragraph par = text.addParagraph();
		TextPart tp = par.addTextPart("Pagina ");
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		FieldPart fp = par.addFieldPart(pageNumberField);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		tp = par.addTextPart(" di ");
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		fp = par.addFieldPart(totalPageCountField);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");

	}

	public Text addText(Section section, String text, int style, int x) throws Exception {
		Style s = styleFactory.makeStyle(style);
		Text t = section.addText(StyleUtils.toTwips(x), 0, s.getWidth(), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}

	/**
	 * Espande il campo Text moltiplicando x per l'altezza originaria
	 */
	public void espandiText(Text text, int x) throws Exception {
		Paragraph p = text.getParagraph(0);
		p.setHeight(p.getHeight() * x);
	}

	public Text addText(Section section, String text, int style, int x, int width) throws Exception {
		Style s = styleFactory.makeStyle(style);
		Text t = section.addText(StyleUtils.toTwips(x), 0, StyleUtils.toTwips(width), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}

	public Text addText(Section section, String text, Style s, int x) throws Exception {
		Text t = section.addText(StyleUtils.toTwips(x), 0, s.getWidth(), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}
}
