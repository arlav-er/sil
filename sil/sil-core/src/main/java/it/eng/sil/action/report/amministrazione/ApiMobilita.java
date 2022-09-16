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

import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.util.Utils;

/**
 * @author Coppola
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ApiMobilita {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";
	private String config_Mobilita = "0";

	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;

	private SourceBean config = null;
	private String installAppPath = null;

	private String dataProt = null;
	private Vector elencoAzioni = null;

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

	public void setMobilita(Vector elencoAzioni) {
		this.elencoAzioni = elencoAzioni;
	}

	public Engine getEngine() {
		return this.eng;
	}

	// public ApiMobilita(){}
	public ApiMobilita(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Elenco Mobilita'");
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

			stampaIntestazioneAzioni(ph, elencoAzioni, true);
			stampaMobilita(details, elencoAzioni, false);
			// RDC.saveEngine(new File("c:/prova.rpt"),eng);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Intestazione report. OK
	 */
	private void testata(Area area) throws Exception {
		Section sec = area.addSection();
		// Andrea 22/03/2007: anno e numero di protocollo vengono passati come
		// parametri (numProt sostituito nella classe Documento)
		PromptField pFieldNumProt = ReportUtils.addPromptField("numProt", PromptField.STRING, "numero di protocollo",
				eng);
		PromptField pFieldAnnoProt = ReportUtils.addPromptField("numAnnoProt", PromptField.STRING, "anno di protocollo",
				eng);
		// sec.setHeight(StyleUtils.toTwips(200));

		// Box box = sec.addBox(StyleUtils.toTwips(80), 300,
		// StyleUtils.toTwips(2570), StyleUtils.toTwips(100));

		//
		Text text = sec.addText(StyleUtils.toTwips(100), 0, StyleUtils.toTwips(1000), 20);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Anno ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(200), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();
		FieldPart fp = p.addFieldPart(pFieldAnnoProt);
		s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(fp);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		fp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(350), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();
		tp = p.addTextPart("Num ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(450), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();
		fp = p.addFieldPart(pFieldNumProt);
		s.setStyle(fp);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		fp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(630), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();
		tp = p.addTextPart("Data di prot. ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

		text = sec.addText(StyleUtils.toTwips(850), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(dataProt).toUpperCase());
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);

		// Aggiungo la data di stampa
		text = sec.addText(StyleUtils.toTwips(2250), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();
		tp = p.addTextPart("Data di stampa ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(2500), 0, StyleUtils.toTwips(1000), 20);
		p = text.addParagraph();

		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		tp = p.addTextPart(sdf.format(d));
		s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
		//

		text = sec.addText(StyleUtils.toTwips(1200), StyleUtils.toTwips(100), StyleUtils.toTwips(1400),
				StyleUtils.toTwips(100));
		p = text.addParagraph();
		p.setHeight(0);
		tp = p.addTextPart("LISTA MOBILITA'");
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Times New Roman");
		tp.setBold(true);
	}

	private void stampaAzioni(Area details) throws Exception {
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
	}

	private void stampaIntestazioneAzioni(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setFontName("Times New Roman");
		s.setFontSize(12);
		Text t = null;

		t = null;
		t = ReportUtils.addText(section, "CF\nCognome Nome", s, LEFT - 15);
		t.setHeight(600);
		t = ReportUtils.addText(section, "Rag. Soc. Azienda", s, LEFT + 370);
		t.setHeight(600);
		t = ReportUtils.addText(section, "Ind. Azienda", s, LEFT + 750);
		t.setHeight(600);
		t = addText(section, "Data Inizio\nData Fine", s, LEFT + 1090);
		t.setHeight(600);
		/**
		 * t = addText(section, "Data Fine", s, LEFT + 1330); t.setHeight(500);
		 */
		t = addText(section, "Stato\ndella richiesta", s, LEFT + 1330);
		t.setHeight(600);
		t = addText(section, "Tipo Lista", s, LEFT + 1630);
		t.setHeight(600);
		t = addText(section, "Data Diff", s, LEFT + 1880);
		t.setHeight(600);
		String infoDelibera = "";
		if (getConfig_Mob().equals("0")) {
			infoDelibera = "Data CRT/\nDel. Reg.";
		} else {
			infoDelibera = "Data CPM/\nDel. Prov.";
		}
		t = addText(section, infoDelibera, s, LEFT + 2130);
		t.setHeight(600);
		t = addText(section, "Prov.", s, LEFT + 2380);
		t.setHeight(600);
		addBR(ph, 10);
	}

	private void stampaMobilita(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
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

		String strCfCognomeNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "CF") + " "
				+ it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "COGNOME") + " "
				+ it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "NOME");
		String ragSociale = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "STRRAGIONESOCIALE");
		String indirizzo = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "STRINDIRIZZO");
		String strDatInizFine = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "DATES");
		// String strDatFine = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "DATFINE");
		String strTipoLIsta = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "STRDESCRIZIONEMOB");
		String strDatDiffMax = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "DATMAXDIFF");
		String strDatCrt = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "DATCRT");
		String provenienza = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "PROVENIENZA");
		String statoMob = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoAzione, "STATO");

		t = addText(section, strCfCognomeNome, 8, LEFT - 15, 400);
		t.setCanGrow(true);

		t = addText(section, ragSociale, 8, LEFT + 370, 370);
		t.setCanGrow(true);

		t = addText(section, indirizzo, 8, LEFT + 750, 370);
		t.setCanGrow(true);

		t = addText(section, strDatInizFine, 8, LEFT + 1090, 180);
		t.setCanGrow(true);

		/**
		 * t = addText(section, strDatFine, 8, LEFT + 1330, 250); t.setCanGrow(true);
		 */

		t = addText(section, statoMob, 8, LEFT + 1330, 250);
		t.setCanGrow(true);

		t = addText(section, strTipoLIsta, 8, LEFT + 1630, 260);
		t.setCanGrow(true);

		t = addText(section, strDatDiffMax, 8, LEFT + 1880, 220);
		t.setCanGrow(true);

		t = addText(section, strDatCrt, 8, LEFT + 2130, 170);
		t.setCanGrow(true);

		t = addText(section, provenienza, 8, LEFT + 2380, 300);
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

	/**
	 * @return
	 */
	public String getDataProt() {
		return dataProt;
	}

	/**
	 * @param string
	 */
	public void setDataProt(String string) {
		dataProt = string;
	}

	public String getConfig_Mob() {
		return config_Mobilita;
	}

	/**
	 * @param string
	 */
	public void setConfig_Mob(String string) {
		config_Mobilita = string;
	}

}
