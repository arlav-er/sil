package it.eng.sil.action.report.redditoattivazione;

import java.math.BigDecimal;
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

public class ApiListaNuovoRA {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ApiListaNuovoRA.class.getName());

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";

	private int LEFT = 80;

	private Vector elencoNRA = null;
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

	public void setElencoNRA(Vector elenco) {
		this.elencoNRA = elenco;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiListaNuovoRA(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("ESITO RICERCA NUOVO REDDITO DI ATTIVAZIONE");
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

			stampaIntestazioneRA(ph, elencoNRA);
			stampaElencoRA(details, elencoNRA);

		} catch (Exception e) {
			throw e;
		}
	}

	private void testata(Area area) throws Exception {
		Section sec = area.addSection();
		//
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		Text text = sec.addText(StyleUtils.toTwips(950), StyleUtils.toTwips(100), StyleUtils.toTwips(1400),
				StyleUtils.toTwips(100));
		Paragraph p = text.addParagraph();
		p.setHeight(0);
		TextPart tp = p.addTextPart("ESITO RICERCA NUOVO REDDITO DI ATTIVAZIONE");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
		//
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

	private void stampaIntestazioneRA(Area ph, Vector rows) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setFontName("Times New Roman");
		s.setFontSize(10);
		Text t = null;

		t = null;
		t = ReportUtils.addText(section, "Cognome", s, LEFT - 15);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Nome", s, LEFT + 250);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Codice Fiscale", s, LEFT + 590);
		t.setHeight(700);
		t = addText(section, "Dt inizio prest.", s, LEFT + 990);
		t.setHeight(700);
		t = addText(section, "Dt fine prest.", s, LEFT + 1230);
		t.setHeight(700);
		t = addText(section, "Tipo domanda", s, LEFT + 1460);
		t.setHeight(700);
		t = addText(section, "Tipo evento", s, LEFT + 1710);
		t.setHeight(700);
		t = addText(section, "Stato domanda", s, LEFT + 1960);
		t.setHeight(700);
		t = addText(section, "Autorizzabile", s, LEFT + 2210);
		t.setHeight(700);
		t = addText(section, "Importo Tot.", s, LEFT + 2430);
		t.setHeight(700);

		addBR(ph, 10);
	}

	private void stampaElencoRA(Area details, Vector rows) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			rigaRA(details, row);
		}
	}

	private void rigaRA(Area details, SourceBean row) throws Exception {
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

		String strCognome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "cognome");
		String strNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "nome");
		String codFiscale = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRCODICEFISCALE");
		String dataInizioPrestazione = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row,
				"DATAINIZIOPRESTAZIONEASDINRA");
		String dataFinePrestazione = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row,
				"DATAFINEPRESTAZIONEASDINRA");
		String tipoDomanda = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "tipodomanda");
		String evento = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "strdescrizione");
		String statoDomanda = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "statodomanda");
		String flgAutorizzabile = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "flgautorizzabile");
		BigDecimal importoComplessivoNra = (BigDecimal) row.getAttribute("importocomplessivonra");
		String importoTot = "";
		if (importoComplessivoNra != null) {
			importoTot = importoComplessivoNra.toString();
		}

		t = addText(section, strCognome, 8, LEFT - 15, 350);
		t.setCanGrow(true);

		t = addText(section, strNome, 8, LEFT + 290, 300);
		t.setCanGrow(true);

		t = addText(section, codFiscale, 8, LEFT + 590, 400);
		t.setCanGrow(true);

		t = addText(section, dataInizioPrestazione, 8, LEFT + 990, 240);
		t.setCanGrow(true);

		t = addText(section, dataFinePrestazione, 8, LEFT + 1230, 230);
		t.setCanGrow(true);

		t = addText(section, tipoDomanda, 8, LEFT + 1460, 250);
		t.setCanGrow(true);

		t = addText(section, evento, 8, LEFT + 1710, 250);
		t.setCanGrow(true);

		t = addText(section, statoDomanda, 8, LEFT + 1960, 250);
		t.setCanGrow(true);

		t = addText(section, flgAutorizzabile, 8, LEFT + 2210, 220);
		t.setCanGrow(true);

		t = addText(section, importoTot, 8, LEFT + 2430, 200);
		t.setCanGrow(true);
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
		// Style s = StyleFactory.makeStyle(style);
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

	public String getDataProt() {
		return dataProt;
	}

	/**
	 * @return
	 */
	public String getNumAnno() {
		return numAnno;
	}

	/**
	 * @return
	 */
	public String getNumProt() {
		return numProt;
	}

	/**
	 * @param string
	 */
	public void setDataProt(String string) {
		dataProt = string;
	}

	/**
	 * @param string
	 */
	public void setNumAnno(String string) {
		numAnno = string;
	}

	/**
	 * @param string
	 */
	public void setNumProt(String string) {
		numProt = string;
	}

}
