package it.eng.sil.action.report.rosaCandidati;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.inet.report.Area;
import com.inet.report.Box;
import com.inet.report.Engine;
import com.inet.report.FieldPart;
import com.inet.report.Fields;
import com.inet.report.Paragraph;
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
public class ApiStampaRosa {
	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;

	private Vector elencoCandidati = null;

	private SourceBean infoGenerali = null;

	//
	private Engine eng = null;
	private String installAppPath = null;
	private StyleFactory styleFactory;
	private String fileType = Engine.EXPORT_PDF;

	public String getFileType() {
		return this.fileType;
	}

	// public void setNumProtocollo(BigDecimal numProtocollo){this.nProtocollo =
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

	public void setElencoCandidati(Vector elencoCandidati) {
		this.elencoCandidati = elencoCandidati;
	}

	public void setInfoGenerali(SourceBean infoGenerali) {
		this.infoGenerali = infoGenerali;
	}

	public Engine getEngine() {
		return this.eng;
	}

	/** Creates a new instance of ApiStampaRosa */
	public ApiStampaRosa() {
	}

	public ApiStampaRosa(String[] a) throws Exception {
		start();
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("report ApiStampaRosa di test .....");
			eng.setLocale(new Locale("IT", "it"));
			initReport(eng);
			/*
			 * calcolo quanto puo' essere lungo un campo di testo in una pagina con layout landscape int xx =
			 * eng.getPageWidth(); int ww = eng.getMarginLeft()+eng.getMarginRight(); int xxx = StyleUtils.toMM(xx-ww);
			 * 
			 * esattamente 2842
			 */
			//
			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toTwips(50));
			// addBR(ph, 80);
			// -
			// protocollazione(details);
			// addBR(details, 50);

			Area rh = eng.getArea("RH");
			rh.getSection(0).setHeight(StyleUtils.toTwips(50));
			testata(rh, infoGenerali);
			addBR(rh, 80);

			Area details = eng.getArea("D");
			String prgTipoIncrocio = (String) infoGenerali.getAttribute("PRGTIPOINCROCIO");

			if (prgTipoIncrocio.equalsIgnoreCase("1") || prgTipoIncrocio.equals("4")) {
				stampaIntestazioneElencoLavoratori(ph, elencoCandidati, false);
			} else {
				stampaIntestazioneElencoLavoratori(ph, elencoCandidati, true);
			}

			if (prgTipoIncrocio.equalsIgnoreCase("1")) {
				stampaElencoLavoratori(details, elencoCandidati, false);
			} else {
				stampaElencoLavoratori(details, elencoCandidati, true);
			}
			// RDC.saveEngine(new File("c:/prova.rpt"),eng);
		} catch (Exception e) {
			throw e;
		}

	}

	private void stampaIntestazioneElencoLavoratori(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO);
		s.setFontSize(10);
		s.setFontName("Arial");
		// il dimensione massima del campo di testo in questo report e' 2842 mm
		s.setWidth(StyleUtils.toTwips(2842));
		Text t = null;
		t = ReportUtils.addText(section, "Cognome e Nome", s, LEFT);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Data di\nNascita", s, LEFT + 720);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Stato\nOcc.", s, LEFT + 920);
		t.setHeight(500);
		if (flgMatchingPesato) {
			t = ReportUtils.addText(section, "Indice\nVicin.", s, LEFT + 1020);
			t.setHeight(500);
		}
		t = ReportUtils.addText(section, "Cond.", s, LEFT + 1130);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Reperibilità", s, LEFT + 1250);
		t.setHeight(500);

		t = ReportUtils.addText(section, "Domicilio", s, LEFT + 1530);
		t.setHeight(500);

		t = ReportUtils.addText(section, "Liste\nSpeciali", s, LEFT + 1880);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Disponibilità", s, LEFT + 2090);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Patto\n297", s, LEFT + 2350);
		t.setHeight(500);
		t = ReportUtils.addText(section, "Ultimo\nContatto", s, LEFT + 2530);
		t.setHeight(500);
		// section.addHorizontalLine(400,600,14500);
	}

	private void stampaElencoLavoratori(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			lavoratore(details, row, flgMatchingPesato);
		}
	}

	private void lavoratore(Area details, SourceBean infoLav, boolean flgMatchingPesato) throws Exception {
		// (11/11/2004 Andrea) la riga di separazione ora e' contenuta in una
		// sezione a parte
		Section section = details.addSection();
		try {
			section.addHorizontalLine(400, StyleUtils.toTwips(1), 15400);
			/*
			 * Element[] elements = section.getElements(); Line line = null; // tutto cio' potrebbe essere assolutamente
			 * inutile. Comunque si potrebbe controllare la dimensione in pixel // della riga (FALLIRA' SULLA VERSIONE 4
			 * DEL SERVER DELLA REGIONE?) for (int i=0;i<elements.length;i++) if (elements[i].getType()==Element.LINE)
			 * line = (Line)elements[i]; try{ line.setHeight(StyleUtils.toTwips(1)); } catch (Exception e) { }
			 */
			section.setHeight(StyleUtils.toTwips(2));
		} catch (Exception e) {
		}
		section = details.addSection();
		//

		Text t = null;

		String strCognomeNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "STRCOGNOMENOME");
		Style style = styleFactory.makeStyle(StyleFactory.CAMPO);
		style.setFontName("Arial");
		t = ReportUtils.addText(section, strCognomeNome, style, LEFT, 720);
		t.setCanGrow(true);

		String dtaNascita = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "DATNASC");
		t = ReportUtils.addText(section, dtaNascita, style, LEFT + 720, 200);
		t.setCanGrow(true);

		String statoOccup = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "CODSTATOOCCUPAZ");
		t = ReportUtils.addText(section, statoOccup, style, LEFT + 920, 100);
		t.setCanGrow(true);

		if (flgMatchingPesato) {
			BigDecimal DECINDICEVICINANZA = (BigDecimal) infoLav.getAttribute("DECINDICEVICINANZA");
			String strDeIndiceVicinanza = null;
			if (DECINDICEVICINANZA != null) {
				strDeIndiceVicinanza = DECINDICEVICINANZA.toString();
			}
			t = ReportUtils.addText(section, strDeIndiceVicinanza, style, LEFT + 1020, 100);
			t.setCanGrow(true);
		}

		String CONDIZIONI = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "CONDIZIONICC");

		String reperibilita = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "ReperibCC");
		// (11/11/2004 Andrea) campi aggiunti alla query per semplificare la
		// costruzione delle righe del campo dei recapiti
		String tel = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "tel");
		String cell = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "cell");
		String email = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "email");

		/** *********** */

		String LISTESPECIALI = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "LISTESPECIALICC");
		String DISPONIBILITA = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "DISPONIBILITA");
		String DOMICILIO = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "DOMICILIO");

		int moltDom = 0;
		if (DOMICILIO.length() > 14)
			moltDom += (DOMICILIO.length() / 14) + 1;
		espandiText(t, moltDom);
		t = ReportUtils.addText(section, DOMICILIO, style, LEFT + 1535, 340);
		t.setCanGrow(true);

		// (11/11/2004 Andrea) anche le disponibilita' possono generare piu'
		// righe
		t = ReportUtils.addText(section, DISPONIBILITA, style, LEFT + 2090, 280);
		t.setCanGrow(true);
		int moltDisp = 0;
		if (DISPONIBILITA.length() > 14) {
			moltDisp = DISPONIBILITA.length() / 14;
			espandiText(t, moltDisp);
		}

		String PATTO297 = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav, "PATTO297");
		t = ReportUtils.addText(section, PATTO297, style, LEFT + 2350, 200);
		t.setCanGrow(true);

		String DATAULTIMOCONTATTO = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(infoLav,
				"DATAULTIMOCONTATTO");
		t = ReportUtils.addText(section, DATAULTIMOCONTATTO, style, LEFT + 2530, 200);
		t.setCanGrow(true);

		/** ****************** */
		// (11/11/2004 Andrea)
		// Recapiti
		ArrayList rec = new ArrayList();
		if (!tel.equals(""))
			rec.add("Tel " + tel);
		if (!cell.equals(""))
			rec.add("Cell " + cell);
		if (!email.equals(""))
			rec.add("E-mail " + email);
		int moltRecap = 0;
		if (rec.size() > 0) {
			reperibilita = "";
			for (int i = 0; i < rec.size(); i++)
				reperibilita += (String) rec.get(i) + ((i == rec.size() - 1) ? "" : "\n\r");
			t = ReportUtils.addText(section, reperibilita, style, LEFT + 1220, 310);
			t.setCanGrow(true);
			// n. di righe di base
			moltRecap = rec.size();
			String s = (String) rec.get(rec.size() - 1);
			if (s.length() > 24 && s.startsWith("E-mail")) // E-Mail = 6
															// caratteri
				moltRecap += ((s.length() - 6) / 24) + 1;
			espandiText(t, moltRecap);
		}
		// Liste speciali
		// sistemo le stringhe una per ogni riga (la stringa puo' contenere la
		// sequenza "XXXXXX o XXXXX\n\r XXXXX")
		StringTokenizer stLSpec = new StringTokenizer(LISTESPECIALI, "\n\r");
		ArrayList spec = new ArrayList();
		if (stLSpec.hasMoreElements()) {
			String s = stLSpec.nextToken();
			String s1 = null;
			if (s.indexOf(" o ") > 0) {
				s1 = s.substring(0, s.indexOf(" o "));
				spec.add(s1);
				s = s.substring(s.indexOf(" o "));
			}
			spec.add(s);
			while (stLSpec.hasMoreElements())
				spec.add(stLSpec.nextElement());
		}
		int moltSpec = 0;
		if (spec.size() > 0) {
			t = ReportUtils.addText(section, LISTESPECIALI.trim(), style, LEFT + 1880, 210);
			moltSpec = spec.size();
			espandiText(t, moltSpec);
			t.setCanGrow(true);
		}
		// Condizioni
		StringTokenizer stCond = new StringTokenizer(CONDIZIONI);
		int moltCondiz = 0;
		if (stCond.countTokens() > 0) {
			t = ReportUtils.addText(section, CONDIZIONI, style, LEFT + 1120, 100);
			t.setCanGrow(true);
			moltCondiz = stCond.countTokens();
			espandiText(t, moltCondiz);
		}
		//
		int moltSecHeight = getMoltSecHeight(moltCondiz, moltSpec, moltRecap, moltDisp);
		if (moltSecHeight > 0)
			section.setHeight(section.getHeight() * moltSecHeight);
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
		sec.setHeight(StyleUtils.toTwips(0));
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
		tp.setFontName("Arial");
		tp.setItalic(true);
		FieldPart fp = par.addFieldPart(pageNumberField);
		fp.setItalic(true);
		tp = par.addTextPart(" di ");
		tp.setFontName("Arial");
		tp.setItalic(true);
		fp = par.addFieldPart(totalPageCountField);
		fp.setFontName("Arial");
		fp.setItalic(true);
	}

	/**
	 * Intestazione report. OK
	 */
	private void testata(Area area, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		BigDecimal prgRosa = (BigDecimal) row.getAttribute("PRGROSA");
		String numRichiesta = (String) row.getAttribute("NUMRICHIESTAORIG");
		if (numRichiesta == null || numRichiesta.equals("")) {
			numRichiesta = (String) row.getAttribute("NUMRICHIESTA");
		}
		String tipoRosa = (String) row.getAttribute("TIPOROSA");
		String tipoIncrocio = (String) row.getAttribute("TIPOINCROCIO");
		String numAnno = (String) row.getAttribute("NUMANNO");
		String prgAlternativa = (String) row.getAttribute("PRGALTERNATIVA");
		//
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(150));
		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(80), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Richiesta num. ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO);
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(400), StyleUtils.toTwips(80), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(numRichiesta) + "/" + Utils.notNull(numAnno));
		s = styleFactory.makeStyle(StyleFactory.CAMPO);
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);

		//
		text = sec.addText(StyleUtils.toTwips(750), StyleUtils.toTwips(80), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Identificativo Rosa ");
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(1150), StyleUtils.toTwips(80), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(prgRosa).toUpperCase());
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toTwips(1400), StyleUtils.toTwips(80), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Alternativa utilizzata ");
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(1800), StyleUtils.toTwips(80), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(prgAlternativa));
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toTwips(2050), StyleUtils.toTwips(80), StyleUtils.toTwips(500),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Tipo Incrocio ");
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(2350), StyleUtils.toTwips(80), StyleUtils.toTwips(500),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(tipoIncrocio));
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);

		// Aggiungo la data di stampa
		text = sec.addText(StyleUtils.toTwips(2100), StyleUtils.toTwips(0), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Data di stampa ");
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(false);
		//
		text = sec.addText(StyleUtils.toTwips(2420), StyleUtils.toTwips(0), StyleUtils.toTwips(500),
				StyleUtils.toTwips(80));
		p = text.addParagraph();

		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		tp = p.addTextPart(sdf.format(d));
		s = styleFactory.makeStyle(StyleFactory.CAMPO);
		s.setStyle(tp);
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		// Savino 08/09/2005
		// a questo punto si aggiungono le mansioni
		// il box viene esteso fino alla sezione specificata come ultimo
		// parametro per 10 mm
		s = styleFactory.makeStyle(StyleFactory.ETICHETTA);
		s.setItalic(true);
		s.setFontName("Arial");
		s.setFontSize(12);
		ReportUtils.addText(area.addSection(), "Mansioni:", s, 100);
		Vector mansioni = (Vector) row.getAttribute("mansioni");
		for (int i = 0; i < mansioni.size(); i++) {
			SourceBean manXML = (SourceBean) mansioni.get(i);
			titoloPuntato(area, manXML.getAttribute("strdescrizione").toString(), s);
		}
		Section sezioneFineBox = area.addSection();
		Box box = sec.addBox(StyleUtils.toTwips(80), 300, StyleUtils.toTwips(2720), sec.getHeight(),
				StyleUtils.toTwips(10), sezioneFineBox);

	}

	/**
	 * 
	 */
	/*
	 * public Text addText(Section section, String text, int style, int x) throws Exception { Style s =
	 * styleFactory.makeStyle(style); Text t = section.addText(StyleUtils.toTwips(x), 0, s.getWidth(), s.getHeight());
	 * 
	 * Paragraph p = t.addParagraph(); p.setX(0); p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); s.setStyle(p); s.setStyle(t); s.setStyle(tp);
	 * s.setStyle(section); return t; }
	 */
	/**
	 * Espande il campo Text moltiplicando x per l'altezza originaria
	 */
	public void espandiText(Text text, int x) throws Exception {
		Paragraph p = text.getParagraph(0);
		p.setHeight(p.getHeight() * x);
	}

	/*
	 * public Text addText(Section section, String text, int style, int x, int width) throws Exception { Style s =
	 * styleFactory.makeStyle(style); Text t = section.addText(StyleUtils.toTwips(x), 0, StyleUtils.toTwips(width),
	 * s.getHeight());
	 * 
	 * Paragraph p = t.addParagraph(); p.setX(0); p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); s.setStyle(p); s.setStyle(t); s.setStyle(tp);
	 * s.setStyle(section); return t; }
	 */
	/*
	 * public Paragraph addTextPart(Paragraph p, String text, int style) throws Exception { Style s =
	 * StyleFactory.makeStyle(style);
	 * 
	 * //p.setX(0); //p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); //s.setStyle(p); s.setStyle(tp);
	 * 
	 * return p; }
	 */
	/*
	 * public Text addText(Section section, String text, Style s, int x) throws Exception { //Style s =
	 * StyleFactory.makeStyle(style); Text t = section.addText(StyleUtils.toTwips(x), 0, s.getWidth(), s.getHeight());
	 * 
	 * Paragraph p = t.addParagraph(); p.setX(0); p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); s.setStyle(p); s.setStyle(t); s.setStyle(tp);
	 * s.setStyle(section); return t; }
	 */
	private void titoloPuntato(Area details, String titolo, Style s) throws Exception {
		Section section = details.addSection();
		section.addPicture(StyleUtils.toTwips(200), StyleUtils.toTwips(19), StyleUtils.toTwips(17),
				StyleUtils.toTwips(17), ConfigSingleton.getRootPath() + "/WEB-INF/report/img/punto_carta.gif");
		ReportUtils.addText(section, titolo, s, 250);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			ApiStampaRosa p = new ApiStampaRosa(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}
}
