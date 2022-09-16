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

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import it.eng.sil.util.Utils;

public class ApiStampaProspetti {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";
	private int LEFT = 80;
	private SourceBean config = null;
	private String installAppPath = null;
	private Vector elencoAziProspetto = null;
	private String codMonoCategoria = null;
	private String anno = null;
	private String codMonoStatoProspetto = null;
	private String tolleranza = null;
	private String disScopNom = null;
	private String disScopNum = null;
	private String art18ScopNom = null;
	private String art18ScopNum = null;
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

	public void setAziProspetto(Vector elencoAziProspetto) {
		this.elencoAziProspetto = elencoAziProspetto;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiStampaProspetti(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Elenco Aziende che hanno presentato un prospetto");
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

			stampaIntestazioneAziProspetto(ph, elencoAziProspetto, true);
			stampaAzienda(details, elencoAziProspetto, false);

			// RDC.saveEngine(new File("c:/prova.rpt"),eng);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Intestazione report. OK
	 */
	private void testata(Area area) throws Exception {
		// DOCAREA : da questo momento bisogna visualizzare i valori del protocollo come promptField
		PromptField pFieldNumProt = ReportUtils.addPromptField("numProt", PromptField.STRING, "numero di protocollo",
				eng);
		PromptField pFieldAnnoProt = ReportUtils.addPromptField("numAnnoProt", PromptField.STRING, "anno di protocollo",
				eng);

		Section sec = area.addSection();

		Text text = sec.addText(StyleUtils.toTwips(2250), 20, StyleUtils.toTwips(1000), 250);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Data di stampa ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
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

		if (codMonoStatoProspetto != null) {
			if (codMonoStatoProspetto.equals("A"))
				codMonoStatoProspetto = "In corso d'anno";
			if (codMonoStatoProspetto.equals("S"))
				codMonoStatoProspetto = "Storicizzato";
			if (codMonoStatoProspetto.equals("V"))
				codMonoStatoProspetto = "SARE:Storicizzato";
			if (codMonoStatoProspetto.equals("U"))
				codMonoStatoProspetto = "Storicizzato:uscita dall'obbligo";
		}

		if (codMonoCategoria != null && !codMonoCategoria.equals("")) {
			if (codMonoCategoria.equals("A"))
				codMonoCategoria = "più di 50 dipendenti";
			else if (codMonoCategoria.equals("B"))
				codMonoCategoria = "da 36 a 50 dipendenti";
			else if (codMonoCategoria.equals("C"))
				codMonoCategoria = "da 15 a 35 dipendenti";
			if (codMonoCategoria.equals(ProspettiConstant.CATEGORIA_NULLA))
				codMonoCategoria = "nulla";

			text = sec.addText(StyleUtils.toTwips(50), StyleUtils.toTwips(100), StyleUtils.toTwips(2800),
					StyleUtils.toTwips(100));
			p = text.addParagraph();
			p.setHeight(0);
			tp = p.addTextPart("Lista delle aziende in fascia " + codMonoCategoria + " che nell'anno " + anno
					+ " hanno una scopertura con tolleranza indicata dal parametro " + tolleranza + ". "
					+ codMonoStatoProspetto);
			s.setStyle(tp);
			tp.setFontSize(12);
			tp.setFontName("Times New Roman");
			tp.setBold(true);
		} else {
			text = sec.addText(StyleUtils.toTwips(300), StyleUtils.toTwips(100), StyleUtils.toTwips(2500),
					StyleUtils.toTwips(100));
			p = text.addParagraph();
			p.setHeight(0);
			tp = p.addTextPart("Lista delle aziende che nell'anno " + anno + " hanno una scopertura con tolleranza "
					+ "indicata dal parametro " + tolleranza + ". " + codMonoStatoProspetto);
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

	private void stampaIntestazioneAziProspetto(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);

		s.setFontName("Times New Roman");
		s.setFontSize(10);
		Text t = null;

		t = ReportUtils.addText(section, "Data\nconsegna", s, LEFT - 15);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Tipo\nazienda", s, LEFT + 180);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Cod.fisc./\nP.Iva", s, LEFT + 440);
		t.setHeight(500);
		t = addText(section, "Ragione\nsociale", s, LEFT + 685);
		t.setHeight(500);
		t = addText(section, "Indirizzo", s, LEFT + 1000);
		t.setHeight(500);
		t = addText(section, "Comune", s, LEFT + 1270);
		t.setHeight(500);
		t = addText(section, "Fascia", s, LEFT + 1520);
		t.setHeight(500);
		t = addText(section, "Nom.\nDis", s, LEFT + 1700);
		t.setHeight(500);
		t = addText(section, "Num\nDis", s, LEFT + 1845);
		t.setHeight(500);
		t = addText(section, "Nom\nArt18", s, LEFT + 1990);
		t.setHeight(500);
		t = addText(section, "Num\nArt18", s, LEFT + 2135);
		t.setHeight(500);
		t = addText(section, "Stato", s, LEFT + 2250);
		t.setHeight(500);
		t = addText(section, "Note", s, LEFT + 2430);
		t.setHeight(500);

		addBR(ph, 10);
	}

	private void stampaAzienda(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			aziende(details, row);
		}
	}

	private void aziende(Area details, SourceBean getProspetti) throws Exception {
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

		String TipoAzienda = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "STRDESCRIZIONE");
		String strCodFisc = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "CODICEFISCALE");
		String strPartIva = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "PIVA");
		String strRagSociale = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "RAGIONESOCIALE");
		String strIndirizzo = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "STRINDIRIZZO");
		String strComune = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "STRDENOMINAZIONE");
		String fascia = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "FASCIA");
		String art18ScopNom = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "art18ScopNom");
		String art18ScopNum = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "art18ScopNum");
		String disScopNom = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "disScopNom");
		String disScopNum = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "disScopNum");
		String disScopTot = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "disScopTot");
		String codMonoStatoProspetto = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti,
				"codMonoStatoProspetto");
		String stato = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "STATO");

		String flggradualita = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "FLGGRADUALITA");
		String flgsospensione = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "FLGSOSPENSIONE");
		String flgsospensioneNaz = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti,
				"FLGSOSPENSIONEMOB");
		String datSospensione = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "DATSOSPENSIONE");
		String datConsegna = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getProspetti, "DATCONSEGNA");

		String dataOdierna = DateUtils.getNow();

		if (!codMonoStatoProspetto.equals("")) {
			if (codMonoStatoProspetto.equalsIgnoreCase("A")) {
				stato = "In corso d'anno";
			} else {
				if (codMonoStatoProspetto.equalsIgnoreCase("U")) {
					stato = "Storicizzato: uscita dall'obbligo";
				}
			}
		}

		String grad = "";
		String sosp = "";
		String note = "";

		if (flggradualita.equals("S")) {
			grad = "Gradualità\nScop.: " + disScopTot;
			note = grad;
		}

		if (flgsospensione.equals("S")
				&& (!datSospensione.equals("") && (DateUtils.compare(datSospensione, dataOdierna) >= 0))) {
			sosp = "Sospesa\nfino al " + datSospensione;
			note = sosp;
		}

		if (!grad.equals("") && !sosp.equals("")) {
			note = grad + "\n" + sosp;
		}

		if (flgsospensioneNaz.equalsIgnoreCase("S")) {
			if (note.equals("")) {
				note = "Sospesa a livello nazionale";
			} else {
				note = note + "\nSospesa a livello nazionale";
			}
		}

		t = addText(section, datConsegna, 8, LEFT, 250);
		t.setCanGrow(true);

		t = addText(section, TipoAzienda, 8, LEFT + 180, 260);
		t.setCanGrow(true);

		t = addText(section, strCodFisc + "\n" + strPartIva, 8, LEFT + 440, 200);
		t.setCanGrow(true);

		/*
		 * t = addText(section, TipoAzienda, 8,LEFT,250); t.setCanGrow(true);
		 * 
		 * t = addText(section, strCodFisc, 8,LEFT +200,200); t.setCanGrow(true);
		 * 
		 * t = addText(section, strPartIva, 8,LEFT + 440,370); t.setCanGrow(true);
		 */

		t = addText(section, strRagSociale, 8, LEFT + 685, 300);
		t.setCanGrow(true);

		t = addText(section, strIndirizzo, 8, LEFT + 1000, 250);
		t.setCanGrow(true);

		t = addText(section, strComune, 8, LEFT + 1270, 250);
		t.setCanGrow(true);

		t = addText(section, fascia, 8, LEFT + 1520, 220);
		t.setCanGrow(true);

		t = addText(section, disScopNom, 8, LEFT + 1720, 220);
		t.setCanGrow(true);

		t = addText(section, disScopNum, 8, LEFT + 1865, 220);
		t.setCanGrow(true);

		t = addText(section, art18ScopNom, 8, LEFT + 2010, 220);
		t.setCanGrow(true);

		t = addText(section, art18ScopNum, 8, LEFT + 2155, 220);
		t.setCanGrow(true);

		t = addText(section, stato, 8, LEFT + 2250, 200);
		t.setCanGrow(true);

		t = addText(section, note, 8, LEFT + 2430, 200);
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

	public String getAnno() {
		return anno;
	}

	public String getCodMonoCategoria() {
		return codMonoCategoria;
	}

	public String getCodMonoStatoProspetto() {
		return codMonoStatoProspetto;
	}

	public String getTolleranza() {
		return tolleranza;
	}

	public String getDisScopNom() {
		return disScopNom;
	}

	public String getDisScopNum() {
		return disScopNum;
	}

	public String getArt18ScopNom() {
		return art18ScopNom;
	}

	public String getArt18ScopNum() {
		return art18ScopNum;
	}

	public void setAnno(String string) {
		anno = string;
	}

	public void setCodMonoCategoria(String string) {
		codMonoCategoria = string;
	}

	public void setCodMonoStatoProspetto(String string) {
		codMonoStatoProspetto = string;
	}

	public void setTolleranza(String string) {
		tolleranza = string;
	}
}
