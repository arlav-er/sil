package it.eng.sil.action.report.pubb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Area;
import com.inet.report.Engine;
import com.inet.report.FieldPart;
import com.inet.report.Fields;
import com.inet.report.GeneralProperties;
import com.inet.report.Line;
import com.inet.report.Paragraph;
import com.inet.report.RDC;
import com.inet.report.ReportProperties;
import com.inet.report.Section;
import com.inet.report.SpecialField;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.sil.action.report.utils.inet.StyleUtils;

/**
 * 
 * @author Administrator
 */
public class ApiPubbl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ApiPubbl.class.getName());

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";

	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	/*
	 * private int LEFT = 80; private int SEP = 30; private int LEFT2 = LEFT+1000; private int X_COL_1 = 700; private
	 * int X_COL_2 = 1600; private int LEFT_TITOLO_PUNTO = LEFT; private int LEFT_TITOLO_PUNTO_T =
	 * LEFT_TITOLO_PUNTO+100; //private int ETICHETTA_WIDTH =
	 * StyleUtils.toMM(StyleFactory.makeStyle(Style.ETICHETTA).getWidth()); //private int LEFT_COL_T = X_COL_1 -
	 * ETICHETTA_WIDTH; //private int LEFT2_COL_T = X_COL_2 - ETICHETTA_WIDTH;;
	 * 
	 * private int LEFT_COL_V = X_COL_1+SEP; private int LEFT2_COL_V = X_COL_2+SEP;
	 */
	//
	// private SourceBean testata = null;
	private Vector elencoPubbl = null;

	//
	private Engine eng = null;
	private String installAppPath = null;

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

	public void setElencoPubbl(Vector elencoPubbl) {
		this.elencoPubbl = elencoPubbl;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiPubbl() {
	}

	public ApiPubbl(String[] a) throws Exception {
		start();
	}

	public void start() throws Exception {
		try {
			// StyleFactory.reset();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Pubblicazioni_Aziende");
			eng.setLocale(new Locale("IT", "it"));
			initReport(eng);
			Area details = eng.getArea("D");
			stampaPubblicazioni(details);

		} catch (Exception e) {
			throw e;
		}
	}

	private void stampaPubblicazioni(Area details) throws Exception {
		String codCpi = "";
		for (int i = 0; i < elencoPubbl.size(); i++) {
			SourceBean row = (SourceBean) elencoPubbl.get(i);
			if (!((String) row.getAttribute("CODCPI")).equalsIgnoreCase(codCpi)) {
				stampaIntestazioneCpi(details, row);
				codCpi = new String((String) row.getAttribute("CODCPI"));
			}
			stampaPubblicazione(details, row);
		}
	}

	private void stampaIntestazioneCpi(Area area, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String strProvincia = (String) row.getAttribute("STRINTESTAZIONESTAMPA");
		String strCPI = "Centro per l'impiego di " + (String) row.getAttribute("STRDESCRIZIONE");
		String strIndirizzo = (String) row.getAttribute("STRINDIRIZZO");
		String strTelFax = "tel.: " + (String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRTEL")
				+ "    fax: " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRFAX");
		String strMail = "e-mail: " + it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STREMAIL");

		//

		Text text = null;
		Paragraph par = null;
		TextPart tp = null;
		Section sec = null;
		/*
		 * Section sec = area.addSection(); text = sec.addText(100, 0, sec.getWidth()-100, 200); par =
		 * text.addParagraph(); par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER); tp = par.addTextPart(strTitolo);
		 * tp.setFontName("Time New Roman"); tp.setFontSize(14); tp.setBold(true);
		 * 
		 * sec = area.addSection(); sec.setHeight(150); Line l = sec.addHorizontalLine(0,0,sec.getWidth());
		 * l.setHeight(50);
		 */

		//
		sec = area.addSection();
		text = sec.addText(100, 0, sec.getWidth() - 100, 200);
		text.setCanGrow(true);
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
		tp = par.addTextPart(strProvincia);
		tp.setFontName("Arial");
		tp.setFontSize(12);
		//
		sec = area.addSection();
		text = sec.addText(100, 0, sec.getWidth() - 100, 200);
		text.setCanGrow(true);
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
		tp = par.addTextPart(strCPI);
		tp.setFontName("Arial");
		tp.setFontSize(14);
		tp.setBold(true);
		//
		sec = area.addSection();
		text = sec.addText(100, 0, sec.getWidth() - 100, 200);
		text.setCanGrow(true);
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
		tp = par.addTextPart(strIndirizzo);
		tp.setFontName("Arial");
		tp.setFontSize(10);
		tp.setBold(true);
		//
		sec = area.addSection();
		text = sec.addText(100, 0, sec.getWidth() - 100, 200);
		text.setCanGrow(true);
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
		tp = par.addTextPart(strTelFax);
		tp.setFontName("Arial");
		tp.setFontSize(10);
		tp.setBold(true);
		//
		sec = area.addSection();
		text = sec.addText(100, 0, sec.getWidth() - 100, 200);
		text.setCanGrow(true);
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
		tp = par.addTextPart(strMail);
		tp.setFontName("Arial");
		tp.setFontSize(10);
		tp.setBold(true);

		sec = area.addSection();
		Line l = sec.addHorizontalLine(0, 0, sec.getWidth());
		try {
			l.setHeight(50);
		} catch (Exception e) {
		}

	}

	private void stampaPubblicazione(Area details, SourceBean row) throws Exception {
		BigDecimal numRichiesta = (BigDecimal) (row.getAttribute("NUMRICHIESTAORIG"));
		if (numRichiesta == null) {
			numRichiesta = (BigDecimal) (row.getAttribute("NUMRICHIESTA"));
		}
		String strRichiesta = "Cod. " + numRichiesta + "/" + (BigDecimal) (row.getAttribute("NUMANNO"))
				+ " valida fino al " + (String) (row.getAttribute("DATSCADENZA"));
		String strMansionePub = (String) row.getAttribute("STRMANSIONEPUBB");
		StringBuffer strRifMansioni = new StringBuffer();
		Vector vMansioniRic = row.getAttributeAsVector("RIF_MANSIONI.ROWS.ROW");
		for (int i = 0; i < vMansioniRic.size(); i++) {
			SourceBean sbMansione = (SourceBean) vMansioniRic.get(i);
			strRifMansioni.append((String) sbMansione.getAttribute("CODMANSIONE") + " ");
			strRifMansioni.append((String) sbMansione.getAttribute("STRDESCRIZIONE"));
			strRifMansioni.append("\n");
		}
		//
		StringBuffer strDiffusioni = new StringBuffer();
		Vector vDiffusioniRic = row.getAttributeAsVector("DIFFUSIONI.ROWS.ROW");
		for (int i = 0; i < vDiffusioniRic.size(); i++) {
			SourceBean sbDiffusione = (SourceBean) vDiffusioniRic.get(i);
			strDiffusioni.append((String) sbDiffusione.getAttribute("STRDESCRIZIONE") + "\n");
		}

		Text text = null;
		Paragraph par = null;
		TextPart tp = null;

		Section sec = details.addSection();
		text = sec.addText(100, sec.getY(), sec.getWidth(), 200);
		text.setCanGrow(true);
		par = text.addParagraph();
		tp = par.addTextPart(strRichiesta);
		tp.setFontName("Arial");
		tp.setFontSize(12);
		tp.setBold(true);
		addBR(details, 20);
		//
		String codEv = (String) row.getAttribute("CODEVASIONE");
		if ((codEv != null) && (!codEv.equalsIgnoreCase("DFA")) && (!codEv.equalsIgnoreCase("DRA"))) {

			if (row.getAttribute("STRDATIAZIENDAPUBB") != null) {
				sec = details.addSection();
				text = sec.addText(100, 0, 2300, 200);
				text.setCanGrow(true);
				par = text.addParagraph();
				tp = par.addTextPart("Azienda richiedente ");
				tp.setFontName("Arial");
				tp.setFontSize(12);
				tp.setItalic(true);
				tp.setBold(false);

				text = sec.addText(2500, 0, sec.getWidth() - 2500, 200);
				par = text.addParagraph();
				text.setCanGrow(true);
				tp = par.addTextPart(
						(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRDATIAZIENDAPUBB"));
				tp.setFontName("Arial");
				tp.setFontSize(12);
				tp.setBold(true);
				addBR(details, 20);
			}
		}
		//

		if ((String) row.getAttribute("STRMANSIONEPUBB") != null) {
			if (!((String) row.getAttribute("STRMANSIONEPUBB")).equals("\r\n")) {
				sec = details.addSection();
				text = sec.addText(100, 0, 2300, 200);
				text.setCanGrow(true);
				par = text.addParagraph();
				tp = par.addTextPart("Mansione ");
				tp.setFontName("Arial");
				tp.setFontSize(12);
				tp.setItalic(true);
				tp.setBold(false);

				text = sec.addText(2500, 0, sec.getWidth() - 2500, 200);
				par = text.addParagraph();
				text.setCanGrow(true);
				tp = par.addTextPart(strMansionePub);
				tp.setFontName("Arial");
				tp.setFontSize(12);
				tp.setBold(true);
				addBR(details, 20);
				//
				sec = details.addSection();
				text = sec.addText(2500, 0, sec.getWidth() - 2500, 200);
				text.setCanGrow(true);

				par = text.addParagraph();
				tp = par.addTextPart("Qualifica ISTAT");
				tp.setFontName("Arial");
				tp.setFontSize(10);
				//
				text = sec.addText(4200, 0, sec.getWidth() - 4200, 200);
				text.setCanGrow(true);
				par = text.addParagraph();
				tp = par.addTextPart(strRifMansioni.toString());
				tp.setFontName("Arial");
				tp.setFontSize(12);
				tp.setBold(true);
				addBR(details, 20);
				//
			}
		}

		if (row.getAttribute("TXTFIGURAPROFESSIONALE") != null) {
			sec = details.addSection();
			text = sec.addText(100, sec.getY(), 2300, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart("Contenuti e\nContesto del lavoro");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, sec.getY(), sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "TXTFIGURAPROFESSIONALE"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);

			addBR(details, 20);
		}
		//

		if (row.getAttribute("STRLUOGOLAVORO") != null) {
			sec = details.addSection();
			text = sec.addText(100, 0, 2300, 200);
			text.setCanGrow(true);
			par = text.addParagraph();
			tp = par.addTextPart("Luogo di lavoro ");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, sec.getY(), sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			tp = par.addTextPart((String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRLUOGOLAVORO"));
			text.setCanGrow(true);
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);

			addBR(details, 20);
		}

		//
		if (row.getAttribute("STRFORMAZIONEPUBB") != null) {
			sec = details.addSection();
			text = sec.addText(100, sec.getY(), 2300, 200);
			text.setCanGrow(true);
			par = text.addParagraph();
			tp = par.addTextPart("Formazione");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, sec.getY(), sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRFORMAZIONEPUBB"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);

			addBR(details, 20);
		}
		//
		if (row.getAttribute("TXTCARATTERISTFIGPROF") != null) {
			sec = details.addSection();
			text = sec.addText(100, 0, 2300, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart("Caratteristiche\ncandidati");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, 0, sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "TXTCARATTERISTFIGPROF"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);

			addBR(details, 20);
		}
		//
		if (row.getAttribute("TXTCONDCONTRATTUALE") != null) {
			sec = details.addSection();
			text = sec.addText(100, sec.getY(), 2300, 200);
			text.setCanGrow(true);
			par = text.addParagraph();
			tp = par.addTextPart("Contratto ");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, sec.getY(), sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "TXTCONDCONTRATTUALE"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);
			addBR(details, 20);
		}
		//
		if (row.getAttribute("STRCONOSCENZEPUBB") != null) {
			sec = details.addSection();
			text = sec.addText(100, sec.getY(), 2300, 200);
			text.setCanGrow(true);
			par = text.addParagraph();
			tp = par.addTextPart("Conoscenze ");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, sec.getY(), sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRCONOSCENZEPUBB"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);
			addBR(details, 20);
		}
		//
		if (row.getAttribute("STRNOTEORARIOPUBB") != null) {
			sec = details.addSection();
			text = sec.addText(100, sec.getY(), 2300, 200);
			text.setCanGrow(true);
			par = text.addParagraph();
			tp = par.addTextPart("Orario ");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, sec.getY(), sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRNOTEORARIOPUBB"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);

			addBR(details, 20);
		}
		//
		if (row.getAttribute("STRRIFCANDIDATURAPUBB") != null) {
			sec = details.addSection();
			text = sec.addText(100, 0, 2300, 200);
			text.setCanGrow(true);
			par = text.addParagraph();
			tp = par.addTextPart("Per candidarsi ");
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setItalic(true);
			tp.setBold(false);

			text = sec.addText(2500, 0, sec.getWidth() - 2500, 200);
			par = text.addParagraph();
			text.setCanGrow(true);
			tp = par.addTextPart(
					(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row, "STRRIFCANDIDATURAPUBB"));
			tp.setFontName("Arial");
			tp.setFontSize(12);
			tp.setBold(true);

			addBR(details, 20);
		}

		sec = details.addSection();
		sec.setHeight(100);
		Line l = sec.addHorizontalLine(0, sec.getY(), sec.getWidth());
		try {
			l.setHeight(50);
		} catch (Exception e) {
		}
	}

	private void addBR(Area details, int height) throws Exception {
		Section section = details.addSection();
		section.setHeight(StyleUtils.toTwips(height));
	}

	private void initReport(Engine eng) throws Exception {
		eng.getReportProperties().setPaperOrient(ReportProperties.PORTRAIT, ReportProperties.PAPER_A4);
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
		/*
		 * SpecialField sp = fields.getSpecialField(SpecialField.PAGE_N_OF_M); sec.addFieldElement(sp,
		 * StyleUtils.toTwips(900), 0, StyleUtils.toTwips(300), StyleUtils.toTwips(85));
		 */
		// Savino 24/05/05
		SpecialField pageNumberField = fields.getSpecialField(SpecialField.PAGE_NUMBER);
		SpecialField totalPageCountField = fields.getSpecialField(SpecialField.TOTAL_PAGE_COUNT);
		Text text = null;
		Paragraph par = null;
		TextPart tp = null;
		FieldPart fp = null;
		text = sec.addText(StyleUtils.toTwips(900), 0, StyleUtils.toTwips(300), StyleUtils.toTwips(85));
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_DEFAULT);
		text.setCanGrow(true);
		tp = par.addTextPart("Pagina ");
		tp.setFontName("Arial");
		tp.setFontSize(9);
		tp.setBold(false);
		fp = par.addFieldPart(pageNumberField);
		fp.setFontName("Arial");
		fp.setFontSize(9);
		fp.setBold(false);
		tp = par.addTextPart(" di ");
		tp.setFontName("Arial");
		tp.setFontSize(9);
		tp.setBold(false);
		fp = par.addFieldPart(totalPageCountField);
		fp.setFontName("Arial");
		fp.setFontSize(9);
		fp.setBold(false);
	}

	/*
	 * public Text addText(Section section, String text, int style, int x) throws Exception { Style s =
	 * StyleFactory.makeStyle(style); Text t = section.addText(StyleUtils.toInch(x), 0, s.getWidth(), s.getHeight());
	 * 
	 * Paragraph p = t.addParagraph(); p.setX(0); p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); s.setStyle(p); s.setStyle(t); s.setStyle(tp);
	 * s.setStyle(section); return t; }
	 * 
	 * public Text addText(Section section, String text, int style, int x, int width) throws Exception { Style s =
	 * StyleFactory.makeStyle(style); Text t = section.addText(StyleUtils.toInch(x), 0, StyleUtils.toInch(width),
	 * s.getHeight());
	 * 
	 * Paragraph p = t.addParagraph(); p.setX(0); p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); s.setStyle(p); s.setStyle(t); s.setStyle(tp);
	 * s.setStyle(section); return t; }
	 * 
	 * public Paragraph addTextPart(Paragraph p, String text, int style) throws Exception { Style s =
	 * StyleFactory.makeStyle(style);
	 * 
	 * //p.setX(0); //p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); //s.setStyle(p); s.setStyle(tp);
	 * 
	 * return p; }
	 * 
	 * public Text addText(Section section, String text, Style s, int x) throws Exception { //Style s =
	 * StyleFactory.makeStyle(style); Text t = section.addText(StyleUtils.toInch(x), 0, s.getWidth(), s.getHeight());
	 * 
	 * Paragraph p = t.addParagraph(); p.setX(0); p.setForeColor(RDC.COLOR_PURPLE);
	 * 
	 * TextPart tp = p.addTextPart(Utils.notNull(text)); tp.setX(0); s.setStyle(p); s.setStyle(t); s.setStyle(tp);
	 * s.setStyle(section); return t; }
	 */
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			ApiPubblAz p = new ApiPubblAz(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}
}