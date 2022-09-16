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
 * 
 * @author Administrator
 */
public class ApiStampaDBFInviati {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";

	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;
	/*
	 * private int SEP = 30; private int LEFT2 = LEFT+1000; private int X_COL_1 = 700; private int X_COL_2 = 1600;
	 * private int LEFT_TITOLO_PUNTO = LEFT; private int LEFT_TITOLO_PUNTO_T = LEFT_TITOLO_PUNTO+100; //private int
	 * ETICHETTA_WIDTH = StyleUtils.toMM(StyleFactory.makeStyle(Style.ETICHETTA).getWidth()); //private int LEFT_COL_T =
	 * X_COL_1 - ETICHETTA_WIDTH; //private int LEFT2_COL_T = X_COL_2 - ETICHETTA_WIDTH;
	 * 
	 * private int LEFT_COL_V = X_COL_1+SEP; private int LEFT2_COL_V = X_COL_2+SEP;
	 */
	//
	// private SourceBean testata = null;
	private Vector elencoAzioni = null;
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

	// public void setNumProtocollo(String numProtocollo){this.nProtocollo =
	// numProtocollo;}
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

	/* Prepara il vettore su cui sono memorizzati tutti i lavoratori esportati (DBF) */
	public void setElencoMobEsportati(Vector elencoAzioni) {
		this.elencoAzioni = elencoAzioni;
	}

	public Engine getEngine() {
		return this.eng;
	}

	// public ApiAzioniConcordate(){}
	public ApiStampaDBFInviati(SourceBean conf) {
		this.config = conf;
	}

	// public ApiAzioniConcordate(String[] a) throws Exception {
	// start();
	// }

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Esportazione delle mobilità");
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

			stampaIntestazioneElencoEsportati(ph, elencoAzioni, true);
			stampaElencoMobilitaEsportate(details, elencoAzioni, false);

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
		Text text = sec.addText(StyleUtils.toTwips(100), 0, StyleUtils.toTwips(1000), 0);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Anno ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(200), 0, StyleUtils.toTwips(1000), 0);
		p = text.addParagraph();
		// tp = p.addTextPart(Utils.notNull(numAnno));
		FieldPart fp = p.addFieldPart(pFieldAnnoProt);
		s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(fp);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		fp.setBold(true);

		//
		text = sec.addText(StyleUtils.toTwips(350), 0, StyleUtils.toTwips(1000), 0);
		p = text.addParagraph();
		tp = p.addTextPart("Num ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(450), 0, StyleUtils.toTwips(1000), 0);
		p = text.addParagraph();
		// tp = p.addTextPart(Utils.notNull(numProt).toUpperCase());
		fp = p.addFieldPart(pFieldNumProt);
		// TODO Andrea : DOCAREA sostituito il campo con il prompt.....
		s.setStyle(fp);
		fp.setFontSize(10);
		fp.setFontName("Times New Roman");
		fp.setBold(true);
		// //
		text = sec.addText(StyleUtils.toTwips(630), 0, StyleUtils.toTwips(1000), 0);
		p = text.addParagraph();
		tp = p.addTextPart("Data di prot. ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(850), 0, StyleUtils.toTwips(1000), 0);
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(dataProt).toUpperCase());
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(true);

		// Aggiungo la data di stampa
		text = sec.addText(StyleUtils.toTwips(2150), 0, StyleUtils.toTwips(1000), 0);
		p = text.addParagraph();
		tp = p.addTextPart("Data di esportazione ");
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(2500), 0, StyleUtils.toTwips(1000), 0);
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
		tp = p.addTextPart("LISTA MOBILITA' APPROVATE");
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

	private void stampaIntestazioneElencoEsportati(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setFontName("Times New Roman");
		s.setFontSize(10);
		Text t = null;

		t = null;
		t = ReportUtils.addText(section, "Nome", s, LEFT - 15);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Cognome", s, LEFT + 180);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Codice Fiscale", s, LEFT + 400);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Data Nascita", s, LEFT + 800);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Comune Nascita", s, LEFT + 1100);
		t.setHeight(700);
		t = ReportUtils.addText(section, "CPI Competente", s, LEFT + 1500);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Ragione Sociale\nAzienda", s, LEFT + 1800);
		t.setHeight(700);
		t = ReportUtils.addText(section, "Tipo Mobilità", s, LEFT + 2100);
		t.setHeight(700);
		t = addText(section, "Data\nLicenziamento", s, LEFT + 2400);
		t.setHeight(700);
		/*
		 * t = addText(section, "Esito", s, LEFT + 1110); t.setHeight(700); t = addText(section, "Indirizzo\ndom.", s,
		 * LEFT + 1370); t.setHeight(700); t = addText(section, "Telefono", s, LEFT + 1640); t.setHeight(700); t =
		 * addText(section, "Mesi\nanz.", s, LEFT + 1870); t.setHeight(700); t = addText(section, "Stato\nDID", s, LEFT
		 * + 2040); t.setHeight(700); t = addText(section, "Liste\nspeciali", s, LEFT + 2220); t.setHeight(700); t =
		 * addText(section, "CPI comp/tit", s, LEFT + 2390); t.setHeight(700);
		 */
		// addBR(ph, 10);
		// section.addHorizontalLine(400,800,14500);
	}

	private void stampaElencoMobilitaEsportate(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			dumpLine(details, row);
		}
	}

	private void dumpLine(Area details, SourceBean infomobiscrizione) throws Exception {
		// (11/11/2004 Andrea) la riga di separazione ora e' contenuta in una
		// sezione a parte
		Section section = details.addSection();
		section.addHorizontalLine(350, StyleUtils.toTwips(1), 14850);

		Element[] elements = section.getElements();
		Line line = null;
		// tutto cio' potrebbe essere assolutamente inutile. Comunque si
		// potrebbe controllare la dimensione in pixel
		// della riga (FALLIRA' SULLA VERSIONE 4 DEL SERVER DELLA REGIONE?)
		for (int i = 0; i < elements.length; i++)
			if (elements[i].getType() == Element.LINE)
				line = (Line) elements[i];
		try {
			line.setHeight(StyleUtils.toTwips(1));
		} catch (Exception e) {
		}
		section.setHeight(StyleUtils.toTwips(2));
		// section = details.addSection();

		Text t = null;

		/*
		 * String strCfCognomeNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione,
		 * "CODFIS_LA") + " " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "cognome") +
		 * " " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "nome");
		 */ String strCognome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "STRCOGNOME");
		String strNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "STRNOME");

		String strCodFiscale = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione,
				"STRCODICEFISCALELAV");
		String strDataNas = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "DATNASC");
		String strComNas = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "COMNAS");
		String strRagSoc = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione,
				"STRRAGIONESOCIALE");
		String strCpi = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "CODCPILAV");
		String strDataLicenz = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "DATFINEMOV");

		String strDescMob = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "MOBDESC");

		String telDom = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "telDom");
		String telRes = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "telRes");
		String telCell = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infomobiscrizione, "telCell");
		StringBuffer rec = new StringBuffer();
		if (!telDom.equals(""))
			rec.append("dom.\n\r" + telDom + "\n\r");
		if (!telRes.equals(""))
			rec.append("res.\n\r" + telRes + "\n\r");
		if (!telCell.equals(""))
			rec.append("cell.\n\r" + telCell);

		t = addText(section, strNome, 8, LEFT - 15, 200);
		t.setCanGrow(true);
		t = addText(section, strCognome, 8, LEFT + 200, 200);
		t.setCanGrow(true);
		t = addText(section, strCodFiscale, 8, LEFT + 400, 400);
		t.setCanGrow(true);

		t = addText(section, strDataNas, 8, LEFT + 800, 300);
		t.setCanGrow(true);

		t = addText(section, strComNas, 8, LEFT + 1100, 400);
		t.setCanGrow(true);

		t = addText(section, strCpi, 8, LEFT + 1500, 300);
		t.setCanGrow(true);

		t = addText(section, strRagSoc, 8, LEFT + 1800, 300);
		t.setCanGrow(true);

		t = addText(section, strDescMob, 8, LEFT + 2100, 300);
		t.setCanGrow(true);

		t = addText(section, strDataLicenz, 8, LEFT + 2400, 300);
		t.setCanGrow(true);

		/*
		 * t = addText(section, strEsito, 8, LEFT + 1110, 250); t.setCanGrow(true);
		 * 
		 * t = addText(section, strIndirizzo, 8, LEFT + 1370, 260); t.setCanGrow(true);
		 * 
		 * t = addText(section, rec.toString(), 8, LEFT + 1640, 220); t.setCanGrow(true);
		 * 
		 * 
		 * t = addText(section, strStatoDid, 8, LEFT + 2040, 150); t.setCanGrow(true);
		 * 
		 * t = addText(section, strListeSpec, 8, LEFT + 2220, 160); t.setCanGrow(true);
		 * 
		 * t = addText(section, strCpiCompTit, 8, LEFT + 2390, 180); t.setCanGrow(true);
		 */
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
	 * @param args
	 *            the command line arguments
	 */
	// public static void main(String[] args) throws Exception {
	// try {
	// ApiAzioniConcordate p = new ApiAzioniConcordate(args);}
	// finally{
	// System.out.println("exit");
	// System.exit(0);
	// }
	// }
	/**
	 * @return
	 */
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
