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

public class ApiAdesioniChiamata {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";

	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;

	private SourceBean config = null;
	private String installAppPath = null;

	private Vector elencoAdesioni = null;
	private String dataChiamata = null;
	private String descCpi = null;

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

	public void setAdesioni(Vector elencoAdesioni) {
		this.elencoAdesioni = elencoAdesioni;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public ApiAdesioniChiamata(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Elenco Adesioni Chiamata");
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

			stampaIntestazioneAdesioni(ph, elencoAdesioni, true);
			stampaAdesioni(details, elencoAdesioni, false);

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

		SourceBean rowTestata = (SourceBean) elencoAdesioni.firstElement();
		descCpi = (String) rowTestata.getAttribute("descrizione");
		dataChiamata = (String) rowTestata.getAttribute("datachiam");

		Text text = sec.addText(StyleUtils.toTwips(2250), 0, StyleUtils.toTwips(1000), 0);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Data di stampa ");
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO1);
		s.setStyle(tp);
		tp.setFontSize(10);
		tp.setFontName("Times New Roman");
		tp.setBold(false);

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

		text = sec.addText(StyleUtils.toTwips(800), StyleUtils.toTwips(100), StyleUtils.toTwips(1400),
				StyleUtils.toTwips(100));
		p = text.addParagraph();
		p.setHeight(0);
		tp = p.addTextPart("Adesioni per Chiamata   " + dataChiamata + "   " + descCpi);
		s.setStyle(tp);
		tp.setFontSize(12);
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

	private void stampaIntestazioneAdesioni(Area ph, Vector rows, boolean flgMatchingPesato) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);

		s.setFontName("Times New Roman");
		s.setFontSize(12);
		Text t = null;

		t = ReportUtils.addText(section, "Cognome", s, LEFT);
		t.setHeight(300);
		t = ReportUtils.addText(section, "Nome", s, LEFT + 400);
		t.setHeight(300);
		t = ReportUtils.addText(section, "Data nascita", s, LEFT + 800);
		t.setHeight(300);
		t = addText(section, "Rif.", s, LEFT + 1250);
		t.setHeight(300);
		t = addText(section, "Tipo graduatoria", s, LEFT + 1600);
		t.setHeight(300);
		t = addText(section, "Stato graduatoria", s, LEFT + 2200);
		t.setHeight(300);
		addBR(ph, 10);
	}

	private void stampaAdesioni(Area details, Vector rows, boolean flgMatchingPesato) throws Exception {
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			adesioni(details, row);
		}
	}

	private void adesioni(Area details, SourceBean getAdesioni) throws Exception {
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

		String strCognome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "cognome");
		String strNome = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "nome");
		String dataNascita = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "datnasc");
		String strRichiesta = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "richiesta");
		String strDescrInc = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "descrInc");
		String strDescrRosa = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(getAdesioni, "descrRosa");

		t = addText(section, strCognome, 8, LEFT + 30, 300);
		t.setCanGrow(true);

		t = addText(section, strNome, 8, LEFT + 400, 370);
		t.setCanGrow(true);

		t = addText(section, dataNascita, 8, LEFT + 840, 370);
		t.setCanGrow(true);

		t = addText(section, strRichiesta, 8, LEFT + 1230, 170);
		t.setCanGrow(true);

		t = addText(section, strDescrInc, 8, LEFT + 1600, 450);
		t.setCanGrow(true);

		t = addText(section, strDescrRosa, 8, LEFT + 2230, 260);
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
}