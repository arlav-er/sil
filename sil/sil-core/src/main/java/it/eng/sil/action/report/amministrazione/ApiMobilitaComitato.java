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

public class ApiMobilitaComitato {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";

	private int LEFT = 80;
	private Vector elencoLav = null;
	private String numProt = null;
	private String numAnno = null;
	private String dataProt = null;
	private String immagineCpi = null;
	private String immagineProvincia = null;
	private SourceBean config = null;
	private String sesso = null;

	// filtri di ricerca da visualizzare nella stampa
	private String dataCPM = "";
	// private String dataDomandaA = "";
	private String tipoLista = "";
	private String statoRichiesta = "";
	private String descCpi = "";

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

	public ApiMobilitaComitato(SourceBean conf) {
		this.config = conf;
	}

	public void start() throws Exception {
		try {
			styleFactory = new StyleFactory();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Lista Provinciale per Comitato");
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
			stampaIntestazione(ph, elencoLav);
			stampaElencoLav(details, elencoLav);
		} catch (Exception e) {
			throw e;
		}
	}

	private void testata(Area area) throws Exception {
		// DOCAREA : da questo momento bisogna visualizzare i valori del protocollo come promptField
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

		text = sec.addText(StyleUtils.toTwips(1200), StyleUtils.toTwips(100), StyleUtils.toTwips(1400),
				StyleUtils.toTwips(100));
		p = text.addParagraph();
		p.setHeight(0);

		tp = p.addTextPart("Lista mobilita' per Comitato/Iscritti con sospesi");

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
		section.addHorizontalLine(400, StyleUtils.toTwips(1), 14800);
	}

	private void stampaFiltriRicerca(Area ph) throws Exception {
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setWidth(50000);
		s.setHeight(1000);
		s.setFontName("Times New Roman");
		s.setFontSize(10);

		String textFinale = "Filtri di ricerca:";
		if (!getDataCPM().equals("")) {
			textFinale = textFinale + " Data CPM/Delibera Provinciale " + getDataCPM() + ";";
		}

		/*
		 * if (!getDataDomandaA().equals("")) { textFinale = textFinale + " Data domanda a " + getDataDomandaA() + ";";
		 * }
		 */

		if (!getTipoLista().equals("")) {
			textFinale = textFinale + " Tipo lista " + getTipoLista() + ";";
		}

		if (!getStatoRichiesta().equals("")) {
			textFinale = textFinale + " Stato della domanda " + getStatoRichiesta() + ";";
		}

		if (!getCodCpi().equals("")) {
			textFinale = textFinale + " Cpi comp. " + getCodCpi();
		}
		int sizeFinale = textFinale.length();
		String textFinaleRighe = "";
		int fine = 0;
		int inizio = 0;
		if (sizeFinale > 150) {
			int linee = sizeFinale / 150;
			for (int i = 0; i < linee; i++) {
				inizio = i * 150;
				fine = inizio + 150;
				if (textFinaleRighe.equals("")) {
					textFinaleRighe = textFinaleRighe + textFinale.substring(inizio, fine);
				} else {
					textFinaleRighe = textFinaleRighe + "\n" + textFinale.substring(inizio, fine);
				}
			}
			if (fine < sizeFinale) {
				textFinaleRighe = textFinaleRighe + "\n" + textFinale.substring(fine, sizeFinale);
			}
		} else {
			textFinaleRighe = textFinale;
		}
		Text t = ReportUtils.addText(section, textFinaleRighe, s, 50);
	}

	private void stampaIntestazione(Area ph, Vector rows) throws Exception {
		if (rows == null)
			return;
		Section section = ph.addSection();
		Style s = styleFactory.makeStyle(StyleFactory.CAMPO2);
		s.setFontName("Times New Roman");
		s.setFontSize(12);
		Text t = null;

		t = ReportUtils.addText(section, "Cognome", s, 50);
		t.setHeight(600);

		t = ReportUtils.addText(section, "Nome", s, 300);
		t.setHeight(600);

		t = addText(section, "Data di\nnascita", s, 550);
		t.setHeight(600);

		t = addText(section, "Residenza", s, 750);
		t.setHeight(600);

		t = addText(section, "Data\ninizio lista", s, 1300);
		t.setHeight(600);

		t = addText(section, "Data\nfine lista", s, 1500);
		t.setHeight(600);

		t = addText(section, "Qualifica", s, 1700);
		t.setHeight(600);

		t = addText(section, "Rag.Sociale", s, 2000);
		t.setHeight(600);

		t = addText(section, "Stato\nOcc.", s, 2400);
		t.setHeight(600);

		addBR(ph, 10);
	}

	private void stampaElencoLav(Area details, Vector rows) throws Exception {
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

		String strCognome = StringUtils.getAttributeStrNotNull(infoAzione, "strcognome");
		String strNome = StringUtils.getAttributeStrNotNull(infoAzione, "strnome");
		String datNasc = StringUtils.getAttributeStrNotNull(infoAzione, "datNasc");
		String comune = StringUtils.getAttributeStrNotNull(infoAzione, "comune");
		String indirizzo = StringUtils.getAttributeStrNotNull(infoAzione, "indirizzo");
		String residenza = indirizzo + " " + comune;
		String azienda = StringUtils.getAttributeStrNotNull(infoAzione, "strragionesociale");
		String datInizio = StringUtils.getAttributeStrNotNull(infoAzione, "datInizio");
		String datFine = StringUtils.getAttributeStrNotNull(infoAzione, "datFine");
		String qualifica = StringUtils.getAttributeStrNotNull(infoAzione, "mansione");
		String descStatoOcc = StringUtils.getAttributeStrNotNull(infoAzione, "descStatoOccupaz");

		t = addText(section, strCognome, 8, 50, 250);
		t.setCanGrow(true);

		t = addText(section, strNome, 8, 300, 250);
		t.setCanGrow(true);

		t = addText(section, datNasc, 8, 550, 200);
		t.setCanGrow(true);

		t = addText(section, residenza, 8, 750, 550);
		t.setCanGrow(true);

		t = addText(section, datInizio, 8, 1300, 200);
		t.setCanGrow(true);

		t = addText(section, datFine, 8, 1500, 200);
		t.setCanGrow(true);

		t = addText(section, qualifica, 8, 1700, 300);
		t.setCanGrow(true);

		t = addText(section, azienda, 8, 2000, 400);
		t.setCanGrow(true);

		t = addText(section, descStatoOcc, 8, 2400, 150);
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

	public String getDataProt() {
		return dataProt;
	}

	public String getNumAnno() {
		return numAnno;
	}

	public String getNumProt() {
		return numProt;
	}

	public void setDataProt(String string) {
		dataProt = string;
	}

	public void setNumAnno(String string) {
		numAnno = string;
	}

	public void setNumProt(String string) {
		numProt = string;
	}

	public void setDataCPM(String val) {
		this.dataCPM = val;
	}

	public String getDataCPM() {
		return this.dataCPM;
	}

	/*
	 * public void setDataDomandaA(String val) { this.dataDomandaA = val; }
	 * 
	 * public String getDataDomandaA() { return this.dataDomandaA; }
	 */

	public void setTipoLista(String val) {
		this.tipoLista = val;
	}

	public String getTipoLista() {
		return this.tipoLista;
	}

	public void setStatoRichiesta(String val) {
		this.statoRichiesta = val;
	}

	public String getStatoRichiesta() {
		return this.statoRichiesta;
	}

	public void setCodCpi(String val) {
		this.descCpi = val;
	}

	public String getCodCpi() {
		return this.descCpi;
	}

}
