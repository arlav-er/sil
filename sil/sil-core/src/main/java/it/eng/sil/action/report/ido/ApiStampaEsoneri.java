package it.eng.sil.action.report.ido;

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

import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.util.Utils;

public class ApiStampaEsoneri {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";
	private int LEFT = 80;
	private SourceBean config = null;
	private String installAppPath = null;

	private Vector elencoEsoneri = null;
	private String pagamenti = null;
	private String datRichDa = null;
	private String datRichA = null;
	private String dataProt = null;
	private String pagamento = null;
	private String ambitoTerr = null;

	private String immagineCpi = null;
	private String immagineProvincia = null;

	private Engine eng = null;
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

	public void setEsoneri(Vector elencoEsoneri) {
		this.elencoEsoneri = elencoEsoneri;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiStampaEsoneri(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Elenco Esoneri");
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

			stampaIntestazioneEsoneri(ph, elencoEsoneri, true);
			stampaEsoneri(details, elencoEsoneri, false);

			// RDC.saveEngine(new File("c:/prova.rpt"),eng);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Intestazione report. OK
	 */
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
		FieldPart fp = p.addFieldPart(pFieldAnnoProt);
		s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(fp);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		fp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(300), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		tp = p.addTextPart("Num ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(400), 20, StyleUtils.toTwips(1000), 250);
		p = text.addParagraph();
		fp = p.addFieldPart(pFieldNumProt);
		s.setStyle(fp);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		fp.setBold(true);

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

		if (pagamento.equals("noRegola")) {

			text = sec.addText(StyleUtils.toTwips(450), StyleUtils.toTwips(100), StyleUtils.toTwips(2000),
					StyleUtils.toTwips(100));
			p = text.addParagraph();
			p.setHeight(0);
			tp = p.addTextPart("Esoneri: elenco aziende non in regola con i pagamenti. Periodo di riferimento dal "
					+ datRichDa + " al " + datRichA);
			s.setStyle(tp);
			tp.setFontSize(12);
			tp.setFontName("Times New Roman");
			tp.setBold(true);
		} else {
			text = sec.addText(StyleUtils.toTwips(700), StyleUtils.toTwips(100), StyleUtils.toTwips(1400),
					StyleUtils.toTwips(100));
			p = text.addParagraph();
			p.setHeight(0);
			tp = p.addTextPart("Esoneri: elenco aziende. Periodo di riferimento dal " + datRichDa + " al " + datRichA);
			s.setStyle(tp);
			tp.setFontSize(12);
			tp.setFontName("Times New Roman");
			tp.setBold(true);
		}
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

	private void stampaIntestazioneEsoneri(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);

		s.setFontName("Times New Roman");
		s.setFontSize(10);
		Text t = null;

		t = ReportUtils.addText(section, "Tipo azienda", s, LEFT - 80);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Codice fiscale", s, LEFT + 185);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Partita iva", s, LEFT + 475);
		t.setHeight(500);
		t = addText(section, "Ragione sociale", s, LEFT + 733);
		t.setHeight(500);
		t = addText(section, "Indirizzo", s, LEFT + 1065);
		t.setHeight(500);
		t = addText(section, "Comune", s, LEFT + 1385);
		t.setHeight(500);
		t = addText(section, "Data richiesta\nesonero", s, LEFT + 1695);
		t.setHeight(500);
		t = addText(section, "Data ultimo\npagamento", s, LEFT + 1935);
		t.setHeight(500);
		t = addText(section, "Periodo competenza\nultimo pagamento", s, LEFT + 2135);
		t.setHeight(500);
		t = addText(section, "Ambito\nterritoriale", s, LEFT + 2455);
		t.setHeight(500);
		addBR(ph, 10);
	}

	private void stampaEsoneri(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			esoneri(details, row);
		}
	}

	private void esoneri(Area details, SourceBean getAdesioni) throws Exception {
		Section section = details.addSection();
		section.addHorizontalLine(0, StyleUtils.toTwips(1), 15500);
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

		String TipoAzienda = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "STRDESCRIZIONE");
		String strCodFisc = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "STRCODICEFISCALE");
		String strPartIva = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "STRPARTITAIVA");
		String strRagSociale = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "STRRAGIONESOCIALE");
		String strIndirizzo = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "STRINDIRIZZO");
		String strComune = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "STRDENOMINAZIONE");
		String datRichiesta = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "DATRICHIESTA");
		String datPagamento = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "DATPAGAMENTO");
		String periodoCompetenza = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "DATINIZIOCOMP")
				+ "   " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "DATFINECOMP");
		String ambitoTerr = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "AMBITOTERRITORIALE");

		t = addText(section, TipoAzienda, 8, LEFT - 65, 350);
		t.setCanGrow(true);

		t = addText(section, strCodFisc, 8, LEFT + 185, 200);
		t.setCanGrow(true);

		t = addText(section, strPartIva, 8, LEFT + 475, 360);
		t.setCanGrow(true);

		t = addText(section, strRagSociale, 8, LEFT + 733, 320);
		t.setCanGrow(true);
		t = addText(section, strIndirizzo, 8, LEFT + 1065, 250);
		t.setCanGrow(true);

		t = addText(section, strComune, 8, LEFT + 1385, 300);
		t.setCanGrow(true);

		t = addText(section, datRichiesta, 8, LEFT + 1695, 200);
		t.setCanGrow(true);

		t = addText(section, datPagamento, 8, LEFT + 1935, 170);
		t.setCanGrow(true);

		t = addText(section, periodoCompetenza, 8, LEFT + 2135, 300);
		t.setCanGrow(true);

		t = addText(section, ambitoTerr, 8, LEFT + 2455, 250);
		t.setCanGrow(true);

	}

	/**
	 * 
	 * Restituisce il numero col quale moltiplicare l'altezza di base della sezione. E' il numero massimo tra i
	 * moltiplicatori applicati ai campi Text interessati
	 */
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

	public String getDatRichDa() {
		return datRichDa;
	}

	public String getDatRichA() {
		return datRichA;
	}

	public String getPagamento() {
		return pagamento;
	}

	public String getAmbitoTerr() {
		return ambitoTerr;
	}

	public void setDataProt(String string) {
		dataProt = string;
	}

	public void setDatRichDa(String string) {
		datRichDa = string;
	}

	public void setDatRichA(String string) {
		datRichA = string;
	}

	public void setPagamento(String string) {
		pagamento = string;
	}

	public void setAmbitoTerr(String string) {
		ambitoTerr = string;
	}

}
