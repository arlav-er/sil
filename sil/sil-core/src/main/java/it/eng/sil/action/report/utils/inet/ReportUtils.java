/*
 * Creato il Dec 16, 2004
 *
 */
package it.eng.sil.action.report.utils.inet;

import com.inet.report.Area;
import com.inet.report.Engine;
import com.inet.report.FieldPart;
import com.inet.report.Fields;
import com.inet.report.Paragraph;
import com.inet.report.PromptField;
import com.inet.report.ReportException;
import com.inet.report.ReportProperties;
import com.inet.report.Section;
import com.inet.report.SpecialField;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.sil.util.Utils;

/**
 * @author Andrea Savino
 * 
 *         In questa classe sono presenti metodi di utilita' per semplificare le comuni operazioni di stampa di stringhe
 *         di testo.
 */
public class ReportUtils {

	private static String contentUMB = "www.arpalumbria.it";

	/**
	 * Aggiunge un testo ad una sezione.
	 * 
	 * @param section
	 *            la sezione in cui inserire il campo di testo
	 * @param text
	 *            la stringa da stampare
	 * @param s
	 *            il riferimento all'oggetto Style che si vuole utilizzare
	 * @param x
	 *            la posizione dal margine sinistro dove posizionare il campo di testo (in mm)
	 * @param width
	 *            (in mm)
	 * @return il riferimento all'oggetto Text creato
	 */
	public static Text addText(Section section, String text, Style s, int x, int width) throws ReportException {
		Text t = section.addText(StyleUtils.toTwips(x), 0, StyleUtils.toTwips(width), s.getHeight());
		Paragraph p = t.addParagraph();
		p.setX(0);
		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}

	public static Text addTextWithFont(Section section, String text, Style s, int x, int width, String fontName)
			throws ReportException {

		Text t = section.addText(StyleUtils.toTwips(x), 0, StyleUtils.toTwips(width), s.getHeight());
		Paragraph p = t.addParagraph();
		p.setX(0);
		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		tp.setFontName(fontName);
		return t;
	}

	/**
	 * Aggiunge una stringa al report. Dato che si chiede di stampare la stringa a x mm dal bordo sinistro, alla
	 * dimensione dello stile viene sottratta questa misura, evitando cosi' che il campo di testo esca del margine
	 * destro del report.
	 * 
	 * @param section
	 *            la sezione in cui inserire il campo di testo
	 * @param text
	 *            la stringa da stampare
	 * @param s
	 *            il riferimento all'oggetto Style che si vuole utilizzare
	 * @param x
	 *            la posizione dal margine sinistro dove posizionare il campo di testo (in mm)
	 * @return il riferimento all'oggetto Text creato
	 */
	public static Text addText(Section section, String text, Style s, int x) throws ReportException {
		int w = StyleUtils.toMM(s.getWidth()) - x;
		w = w < 200 ? 200 : w;
		return addText(section, text, s, x, w);

	}

	public static Text addTextFont(Section section, String text, Style s, int x, String fontName)
			throws ReportException {
		int w = StyleUtils.toMM(s.getWidth()) - x;
		w = w < 200 ? 200 : w;
		return addTextWithFont(section, text, s, x, w, fontName);
	}

	/**
	 * Aggiunge una stringa ad un paragrafo. La stringa <strong>text</strong> con stile <strong>s</strong> al paragrafo
	 * <strong>p</strong>.
	 * 
	 * @param p
	 *            il Paragraph in cui inserire il campo di testo
	 * @param text
	 *            la stringa da stampare
	 * @param s
	 *            il riferimento all'oggetto Style che si vuole utilizzare (per il TextPart)
	 * 
	 * @return il riferimento al paragraph passato come parametro
	 */
	public static Paragraph addTextPart(Paragraph p, String text, Style s) throws ReportException {

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(tp);

		return p;
	}

	public static Paragraph addTextPartWithFont(Paragraph p, String text, Style s, String fontName)
			throws ReportException {
		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(tp);
		tp.setFontName(fontName);
		return p;
	}

	public static Paragraph addTextPartWithSizeFont(Paragraph p, String text, Style s, String fontName)
			throws ReportException {
		// StyleFactory st = new StyleFactory();
		// Style sty = st.getStyle(StyleFactory.CAMPO);
		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(tp);
		tp.setFontName(fontName);
		tp.setFontSize(10);
		return p;
	}

	public static Paragraph addTextPartWithSizeFont(Paragraph p, String text, Style s, String fontName, int fontSize)
			throws ReportException {
		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(tp);
		tp.setFontName(fontName);
		tp.setFontSize(fontSize);
		return p;
	}

	/**
	 * Aggiunge una stringa ad un campo di testo. Crea un paragrafo in cui inserire una sequenza di stringhe (TextPart)
	 * <br>
	 * N.B. il Text contiene il Paraghraph che contiene i TextPart
	 * 
	 * @param t
	 *            il Text in cui inserire il Paragraph in cui verra' inserito il testo
	 * @param text
	 * @param s
	 * @return il riferimento all'oggetto di classe Paragraph creato e in cui verra' inserito il testo
	 */
	public static Paragraph addTextPart(Text t, String text, Style s) throws ReportException {
		return addTextPart(t.getParagraph(0), text, s);
	}

	/**
	 * Inizializzazione delle sezioni del report (header, footer). Viene impostata l'altezza del page header e del page
	 * footer a 100 mm. Viene inserito nel page footer la variabile di conteggio del numero di pagine.
	 * 
	 * @param eng
	 * @throws Exception
	 */
	static public void initReport(Engine eng) throws Exception {
		
		// om20201211: fix orientation and size
		eng.getReportProperties().setPaperOrient(ReportProperties.DEFAULT_PAPER_ORIENTATION,ReportProperties.PAPER_A4);
		eng.getReportProperties().setMarginLeft(720);
		eng.getReportProperties().setMarginRight(720);
		eng.getReportProperties().setMarginTop(720);
		eng.getReportProperties().setMarginBottom(720);
		
		Area header = eng.getArea("RH");
		header.setSuppress(true);
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
		Section s = footer.getSection(0);
		Text text = s.addText(StyleUtils.toTwips(1700), 0, StyleUtils.toTwips(200), StyleUtils.toTwips(85));
		Paragraph par = text.addParagraph();
		TextPart tp = par.addTextPart("Pagina ");
		FieldPart fp = par.addFieldPart(pageNumberField);
		tp = par.addTextPart(" di ");
		fp = par.addFieldPart(totalPageCountField);
	}

	static public void initReportSenzaMargini(Engine eng) throws Exception {
		// om20201203: riporto da 3.35.0, diversa modalità di set orientamento e tipo pagina
		// eng.setPaperOrient(Engine.DEFAULT_PAPER_ORIENTATION, Engine.PAPER_A4);
		eng.getReportProperties().setPaperOrient(ReportProperties.DEFAULT_PAPER_ORIENTATION, ReportProperties.PAPER_A4);

		Area header = eng.getArea("RH");
		header.setSuppress(false);
		Section sec = header.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));
		//
		header = eng.getArea("PH");
		header.setSuppress(true);
		sec = header.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));
		//

		Area footer = eng.getArea("RF");
		sec = footer.getSection(0);
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toTwips(0));

		footer = eng.getArea("PF");
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));

		Fields fields = eng.getFields();
		SpecialField pageNumberField = fields.getSpecialField(SpecialField.PAGE_NUMBER);
		SpecialField totalPageCountField = fields.getSpecialField(SpecialField.TOTAL_PAGE_COUNT);
		Section s = footer.getSection(0);
		Text text = s.addText(StyleUtils.toTwips(1700), 0, StyleUtils.toTwips(200), StyleUtils.toTwips(85));
		Paragraph par = text.addParagraph();
		TextPart tp = par.addTextPart("Pagina ");
		FieldPart fp = par.addFieldPart(pageNumberField);
		tp = par.addTextPart(" di ");
		fp = par.addFieldPart(totalPageCountField);
	}

	static public void initReportSenzaMarginiPattoUMB(Engine eng, String path) throws Exception {
		// om20201203: riporto da 3.35.0, diversa modalità di set orientamento e tipo pagina
		// eng.setPaperOrient(Engine.DEFAULT_PAPER_ORIENTATION, Engine.PAPER_A4);
		eng.getReportProperties().setPaperOrient(ReportProperties.DEFAULT_PAPER_ORIENTATION, ReportProperties.PAPER_A4);

		Area header = eng.getArea("RH");
		header.setSuppress(false);
		Section sec = header.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));

		header = eng.getArea("PH");
		header.setSuppress(true);
		sec = header.getSection(0);
		sec.setHeight(StyleUtils.toTwips(100));

		Area footer = eng.getArea("RF");
		sec = footer.getSection(0);
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toTwips(0));

		footer = eng.getArea("PF");
		sec = footer.addSection();
		sec.setHeight(StyleUtils.toTwips(60));

		sec = footer.addSection();
		sec.setHeight(StyleUtils.toTwips(25));

		Fields fields = eng.getFields();
		SpecialField pageNumberField = fields.getSpecialField(SpecialField.PAGE_NUMBER);
		SpecialField totalPageCountField = fields.getSpecialField(SpecialField.TOTAL_PAGE_COUNT);

		Text text = sec.addText(StyleUtils.toTwips(1665), 250, StyleUtils.toTwips(200), StyleUtils.toTwips(85));
		Paragraph par = text.addParagraph();
		TextPart tp = par.addTextPart("Pagina ");
		tp.setFontName("SansSerif");
		FieldPart fp = par.addFieldPart(pageNumberField);
		tp = par.addTextPart(" di ");
		tp.setFontName("SansSerif");
		fp = par.addFieldPart(totalPageCountField);

		// Link ArpalUmbria
		Text text1 = sec.addText(StyleUtils.toTwips(780), 250, StyleUtils.toTwips(280), StyleUtils.toTwips(85));
		text1.setHyperlinkUrl(contentUMB);
		par = text1.addParagraph();
		tp = par.addTextPart(contentUMB);
		tp.setFontSize(9);
		tp.setFontName("SansSerif");

		sec.addPicture(0, StyleUtils.toTwips(8), StyleUtils.toTwips(1850), StyleUtils.toTwips(25),
				path + "/WEB-INF/report/img/UMB_linea_grigia.png");
	}

	static public PromptField addPromptField(String promptName, int type, String desc, Engine eng)
			throws ReportException {
		Fields fields = eng.getFields();
		return fields.addPromptField(promptName, desc, type);
	}
}
