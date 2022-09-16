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
import com.inet.report.PromptField;
import com.inet.report.RDC;
import com.inet.report.ReportProperties;
import com.inet.report.Section;
import com.inet.report.SpecialField;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.util.Utils;

public class ApiListaSanatoriaRedditi {
	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";
	private int LEFT = 80;
	private SourceBean config = null;
	private String installAppPath = null;

	private Vector elenco = null;
	private String data = null;
	private String anno = null;
	private String numProt = null;
	private String numAnno = null;
	private String dataProt = null;

	private String immagineCpi = null;
	private String immagineProvincia = null;

	private Engine eng = null;
	private StyleFactory styleFactory;
	private String fileType = Engine.EXPORT_PDF;

	private int finalTotaleRighe = 0;

	// filtri di ricerca da visualizzare nella stampa
	private String dataInizioRichDa = "";
	private String dataInizioRichA = "";
	private String descCpi = "";

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

	public void setElenco(Vector elenco) {
		this.elenco = elenco;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiListaSanatoriaRedditi(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Elenco Sanatoria Redditi");
			eng.setLocale(new Locale("IT", "it"));
			initReport(eng);

			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toTwips(1));

			Area rh = eng.getArea("RH");
			rh.getSection(0).setHeight(StyleUtils.toTwips(0));
			stampaIntestazioneCpi(rh);
			testata(rh);
			addBR(rh, 10);
			stampaFiltriRicerca(rh);
			addBR(rh, 10);
			Area details = eng.getArea("D");
			stampaIntestazioneColonne(details, elenco);
			stampaLista(details, elenco);
		} catch (Exception e) {
			throw e;
		}
	}

	private void testata(Area area) throws Exception {
		// DOCAREA : da questo momento bisogna visualizzare i valori del
		// protocollo come promptField
		PromptField pFieldNumProt = ReportUtils.addPromptField("numProt", PromptField.STRING, "numero di protocollo",
				eng);
		PromptField pFieldAnnoProt = ReportUtils.addPromptField("numAnnoProt", PromptField.STRING, "anno di protocollo",
				eng);

		Section sec = area.addSection();

		Text text = sec.addText(StyleUtils.toTwips(1300), 20, StyleUtils.toTwips(1300), 250);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Anno ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(1400), 20, StyleUtils.toTwips(1400), 250);
		p = text.addParagraph();
		tp = p.addTextPart(getNumAnno());
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(300), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		tp = p.addTextPart("Num ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(400), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		tp = p.addTextPart(getNumProt());
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(630), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		tp = p.addTextPart("Data di prot. ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(850), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		p.setHeight(0);
		tp = p.addTextPart(Utils.notNull(dataProt).toUpperCase());
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(2250), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		tp = p.addTextPart("Data di stampa ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(2500), 20, StyleUtils.toTwips(1000), 250);
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
		tp = p.addTextPart("Elenco Sanatoria Redditi");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
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
	}

	private void stampaFiltriRicerca(Area ph) throws Exception {
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setWidth(50000);
		s.setHeight(500);
		s.setFontName("Times New Roman");
		s.setFontSize(10);
		String textFinale = "Filtri di ricerca:";
		if (!getDataInizioRicercaDa().equals("")) {
			textFinale = textFinale + " Data inizio ricerca da " + getDataInizioRicercaDa() + ";";
		}

		if (!getDataInizioRicercaA().equals("")) {
			textFinale = textFinale + " Data inizio ricerca a " + getDataInizioRicercaA() + ";";
		}

		if (!getCodCpi().equals("")) {
			textFinale = textFinale + " Cpi comp. " + getCodCpi();
		}

		Text t = ReportUtils.addText(section, textFinale, s, 50);
	}

	private void stampaIntestazioneColonne(Area ph, Vector rows) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);

		s.setFontName("Times New Roman");
		s.setFontSize(10);
		Text t = null;
		t = ReportUtils.addText(section, "Cognome", s, 50);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Nome", s, 550);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Codice Fiscale", s, 1050);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Data di nascita", s, 1550);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Data cessazione", s, 1800);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Tipologia", s, 2050);
		t.setHeight(500);
		addBR(ph, 10);
	}

	private void stampaLista(Area details, Vector rows) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			showRiga(details, row);
		}
	}

	private void showRiga(Area details, SourceBean row) throws Exception {
		Section section = details.addSection();
		section.addHorizontalLine(400, StyleUtils.toTwips(1), 14800);
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

		finalTotaleRighe = finalTotaleRighe + 1;

		String strCognome = StringUtils.getAttributeStrNotNull(row, "STRCOGNOME");
		String strNome = StringUtils.getAttributeStrNotNull(row, "STRNOME");
		String codiceFiscale = StringUtils.getAttributeStrNotNull(row, "STRCODICEFISCALE");
		String dataNasc = StringUtils.getAttributeStrNotNull(row, "DATNASC");
		String dataCessazione = StringUtils.getAttributeStrNotNull(row, "DATCESSAZIONE");
		String tipologia = StringUtils.getAttributeStrNotNull(row, "TIPOLOGIA");

		t = addText(section, strCognome, 8, 50, 500);
		t.setCanGrow(true);

		t = addText(section, strNome, 8, 550, 500);
		t.setCanGrow(true);

		t = addText(section, codiceFiscale, 8, 1050, 500);
		t.setCanGrow(true);

		t = addText(section, dataNasc, 8, 1550, 250);
		t.setCanGrow(true);

		t = addText(section, dataCessazione, 8, 1800, 250);
		t.setCanGrow(true);

		t = addText(section, tipologia, 8, 2050, 500);
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

	public String getData() {
		return data;
	}

	public String getAnno() {
		return anno;
	}

	public String getNumProt() {
		return numProt;
	}

	public void setData(String string) {
		data = string;
	}

	public void setAnno(String string) {
		anno = string;
	}

	public String getDataProt() {
		return dataProt;
	}

	public void setDataProt(String string) {
		dataProt = string;
	}

	public void setNumAnno(String string) {
		numAnno = string;
	}

	public String getNumAnno() {
		return numAnno;
	}

	public void setNumProt(String string) {
		numProt = string;
	}

	public void setDataInizioRicercaDa(String val) {
		this.dataInizioRichDa = val;
	}

	public String getDataInizioRicercaDa() {
		return this.dataInizioRichDa;
	}

	public void setDataInizioRicercaA(String val) {
		this.dataInizioRichA = val;
	}

	public String getDataInizioRicercaA() {
		return this.dataInizioRichA;
	}

	public void setCodCpi(String val) {
		this.descCpi = val;
	}

	public String getCodCpi() {
		return this.descCpi;
	}

}
