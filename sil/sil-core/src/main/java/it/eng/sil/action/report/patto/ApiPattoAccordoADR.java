package it.eng.sil.action.report.patto;

import java.util.Locale;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.inet.report.Area;
import com.inet.report.Box;
import com.inet.report.Element;
import com.inet.report.Engine;
import com.inet.report.Field;
import com.inet.report.FieldPart;
import com.inet.report.GeneralProperties;
import com.inet.report.Paragraph;
import com.inet.report.Picture;
import com.inet.report.PromptField;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.util.Utils;

public class ApiPattoAccordoADR {
	Style campo;
	Style campoI;
	Style titoloSezione;
	Style etichetta;
	Style etichetta2;
	Style titolo2;
	Style titolo3;
	Style titolo2Centrato;
	StyleFactory styleFactory;

	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;
	private int LEFT_TITOLO_PUNTO = LEFT;
	private int LEFT_TITOLO_PUNTO_T = LEFT_TITOLO_PUNTO + 100;
	//
	private Vector appuntamenti = null;
	private Vector azioniConcordate = null;
	private SourceBean infoGenerali = null;
	private SourceBean operatore = null;
	private String docInOut;
	private String regione;
	private SourceBean config = null;
	//

	private String riferimentoDoc;
	//
	private Engine eng = null;
	private String installAppPath = null;
	private String codMonoIO;
	private String fileType = Engine.EXPORT_PDF;

	private static final String RER = "8";

	public String getFileType() {
		return this.fileType;
	}

	public void setInstallAppPath(String installAppPath) {
		this.installAppPath = installAppPath;
	}

	/**
	 * Viene impostato il tipo di report da generare (il tipo nostro viene mappato col tipo interno di crystal clear)
	 * 
	 * @param tipoFile
	 *            e' l'estensione del file del report (es. pdf, doc)
	 */
	public void setFileType(String tipoFile) {
		if (tipoFile.equalsIgnoreCase("PDF"))
			this.fileType = Engine.EXPORT_PDF;
		else if (tipoFile.equalsIgnoreCase("RTF"))
			this.fileType = Engine.EXPORT_RTF;
		else if (tipoFile.equalsIgnoreCase("HTML"))
			this.fileType = Engine.EXPORT_HTML;
		else if (tipoFile.equalsIgnoreCase("XLS"))
			this.fileType = Engine.EXPORT_XLS;
		else
			this.fileType = Engine.EXPORT_PDF;
	}

	public void setRiferimentoDoc(String s) {
		this.riferimentoDoc = s;
	}

	public void setInfoGenerali(SourceBean infoGenerali) {
		this.infoGenerali = infoGenerali;
	}

	public void setAzioniConcordate(Vector azioniConcordate) {
		this.azioniConcordate = azioniConcordate;
	}

	public void setAppuntamenti(Vector appuntamenti) {
		this.appuntamenti = appuntamenti;
	}

	public void setOperatore(SourceBean operatore) {
		this.operatore = operatore;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public void setCodMonoIO(String s) {
		this.codMonoIO = s;
	}

	public void setDocInOut(String s) {
		this.docInOut = s;
	}

	/** Creates a new instance of Patto */
	public ApiPattoAccordoADR() {
		styleFactory = new StyleFactory();
		campoI = styleFactory.getStyle(StyleFactory.CAMPO);
		campo = styleFactory.getStyle(StyleFactory.CAMPO4);
		titoloSezione = styleFactory.getStyle(StyleFactory.TITOLO_SEZIONE);
		etichetta = styleFactory.getStyle(StyleFactory.ETICHETTA);
		etichetta2 = styleFactory.getStyle(StyleFactory.ETICHETTA2);
		titolo2 = styleFactory.getStyle(StyleFactory.TITOLO2);
		titolo3 = styleFactory.getStyle(StyleFactory.TITOLO3);
		titolo2Centrato = styleFactory.getStyle(StyleFactory.TITOLO2CENTRATO);

	}

	public ApiPattoAccordoADR(String[] a) throws Exception {
		start();
	}

	public void inizializza(SourceBean infoGen, Vector appuntaments, SourceBean operatore, Vector azioniConcordats,
			String installAppPath, String tipoFile, String ambito, String strParam, String docInOut, String regione,
			SourceBean config) throws Exception {
		try {

			this.setInfoGenerali(infoGen);
			this.setAppuntamenti(appuntaments);
			this.setAzioniConcordate(azioniConcordats);
			this.setInstallAppPath(installAppPath);
			this.setFileType(tipoFile);
			this.setRiferimentoDoc(ambito);
			this.setOperatore(operatore);
			this.regione = regione;
			this.config = config;

			if (strParam != null && !strParam.equals("")) {
				this.setCodMonoIO(strParam);
				if (strParam.equalsIgnoreCase("I"))
					this.setDocInOut("Input");
				else
					this.setDocInOut("Output");
			}
			this.start();
		} catch (Exception e) {
			throw e;
		}
	}

	public void start() throws Exception {
		try {
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Patto");
			eng.setLocale(new Locale("IT", "it"));
			ReportUtils.initReport(eng);
			// inserisco il parametro del numero di protocollo in modo che se si
			// verifica che il numero di protocollo
			// reale differisce da quello visualizzato nel form di stampa, possa
			// essere sostituito appunto da quello reale.
			// Questa operazione avviene in modo trasparente nella fase di
			// protocollazione nella classe Documento.
			PromptField pNumProt = ReportUtils.addPromptField("numProt", PromptField.STRING, "numero di protocollo",
					eng);
			PromptField pAnnoProt = ReportUtils.addPromptField("numAnnoProt", PromptField.STRING,
					"anno di protocollazione", eng);
			PromptField pDataProt = ReportUtils.addPromptField("dataProt", PromptField.STRING,
					"data di protocollazione", eng);
			// si imposta il valore del parametro col numero di protocollo
			// spedito dal form di stampa
			//
			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toTwips(50));
			Area details = eng.getArea("D");
			// RER/TRENTO
			if (this.regione.equalsIgnoreCase("8") || this.regione.equalsIgnoreCase("22")) {
				// loghi ADR
				if (this.regione.equalsIgnoreCase("22")) {
					testataCPITrento(details);
					addBR(details, 20);
				} else {
					if (sezioneADR(details)) {
						addBR(details, 40);
					}
				}
				if (this.regione.equalsIgnoreCase("8")) {
					protocollazione(details, pNumProt, pAnnoProt, pDataProt);
				} else {
					protocollazioneTN(details, pNumProt, pAnnoProt, pDataProt);
				}
			} else {
				// VDA
				if (this.regione.equalsIgnoreCase("2")) {
					addBR(details, 50);
					testataCPIVDA(details, infoGenerali);
				} else {
					protocollazione(details, pNumProt, pAnnoProt, pDataProt);

					addBR(details, 40);
					intestazioneUmbria(details);

					addBR(details, 40);
					testataCPIUmbria(details, infoGenerali);
				}
			}
			addBR(details, 40);
			intestazionePattoAccordo(details);
			//
			addBR(details, 40);
			datiSoggettoErogatore(details, operatore, infoGenerali);
			//
			datiDestinatarioAssegno(details, infoGenerali);
			//
			addBR(details, 40);
			impegniCPI(details);
			//
			addBR(details, 40);
			azioniPatto(details, azioniConcordate);
			//
			addBR(details, 40);
			appuntamentiPatto(details, appuntamenti);
			//
			noteAttivazione(details, infoGenerali);

			// String dataRiferimento = (String) infoGenerali.getAttribute("datriferimento");
			String dataStipula = (String) infoGenerali.getAttribute("datStipula");
			addBR(details, 40);
			paginaInformazioni(details, dataStipula);

		} catch (Exception e) {
			throw e;
		}
	}

	private void testataCPIVDA(Area area, SourceBean row) throws Exception {
		if (row == null) {
			row = new SourceBean("");
		}
		//
		String cpi = (String) row.getAttribute("CPI");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));
		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(true);
		//
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95), StyleUtils.toTwips(600),
				StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toTwips(720), StyleUtils.toTwips(95), StyleUtils.toTwips(350),
				StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("fax. " + Utils.notNull(fax));
		s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);

		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(150), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		s = (Style) campo.clone();
		s.setStyle(tp);

		tp.setBold(true);

		sec.addBox(StyleUtils.toTwips(1248), 0, StyleUtils.toTwips(630), StyleUtils.toTwips(205));
		text = sec.addText(StyleUtils.toTwips(1400), StyleUtils.toTwips(50), StyleUtils.toTwips(450),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Regione Autonoma Valle d'Aosta\nRégion Autonome Vall'e d'Aoste ");
		tp.setFontSize(8);
		tp.setFontName("Arial");
		tp.setBold(true);
		text.setCanGrow(true);
		//
		sec.addPicture(StyleUtils.toTwips(1255), StyleUtils.toTwips(26), 795, 870,
				this.installAppPath + "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		int contatoreImmagini = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				((Element) elements.get(i)).setSuppress(true);
				contatoreImmagini = contatoreImmagini + 1;
			}
		}
	}

	private void intestazioneUmbria(Area area) throws Exception {
		Text text = null;
		Paragraph par = null;
		TextPart tp = null;

		Section sec = area.addSection();
		text = sec.addText(100, 0, sec.getWidth() - 100, 200);
		par = text.addParagraph();
		par.setHorAlign(GeneralProperties.ALIGN_HORIZONTAL_CENTER);
		tp = par.addTextPart("ARPAL Umbria - Agenzia Regionale per le Politiche Attive del Lavoro");
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
	}

	private void testataCPIUmbria(Area area, SourceBean row) throws Exception {
		if (row == null) {
			row = new SourceBean("");
		}
		//
		String cpi = (String) row.getAttribute("CPI");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		// String intestazione = (String) row.getAttribute("STRINTESTAZIONESTAMPA");
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));

		Box box = sec.addBox(StyleUtils.toTwips(80), 0, StyleUtils.toTwips(1470), StyleUtils.toTwips(205));
		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(true);
		//
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95), StyleUtils.toTwips(600),
				StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toTwips(720), StyleUtils.toTwips(95), StyleUtils.toTwips(350),
				StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("fax. " + Utils.notNull(fax));
		s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(150), StyleUtils.toTwips(1000),
				StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		s = (Style) campo.clone();
		s.setStyle(tp);

		tp.setBold(true);
		//
		sec.addBox(StyleUtils.toTwips(1548), 0, StyleUtils.toTwips(240), StyleUtils.toTwips(205));
		/*
		 * text = sec.addText(StyleUtils.toTwips(1420), StyleUtils.toTwips(50), StyleUtils.toTwips(350), StyleUtils
		 * .toTwips(80)); // p = text.addParagraph(); tp = p.addTextPart( Utils.notNull(intestazione).toUpperCase());
		 * tp.setFontSize(11); tp.setFontName("Arial"); tp.setBold(true); text.setCanGrow(true);
		 */
		//
		sec.addPicture(StyleUtils.toTwips(1595), StyleUtils.toTwips(26), 795, 870,
				this.installAppPath + "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		int contatoreImmagini = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				((Element) elements.get(i)).setSuppress(true);
				contatoreImmagini = contatoreImmagini + 1;
			}
		}
	}

	private void testataCPITrento(Area area) throws Exception {

		Section section = area.addSection();
		String nomeFile = (String) (config.getAttribute("LOGO_PROVINCIA.FILE"));
		int larg = (new Integer(((String) config.getAttribute("LOGO_PROVINCIA.WIDTH")))).intValue();
		int alt = (new Integer(((String) config.getAttribute("LOGO_PROVINCIA.HEIGHT")))).intValue();

		section.addPicture(LEFT + 400, 50, (larg), (alt), installAppPath + nomeFile);

		nomeFile = (String) (config.getAttribute("LOGO_CENTRIIMPIEGO.FILE"));
		larg = (new Integer(((String) config.getAttribute("LOGO_CENTRIIMPIEGO.WIDTH")))).intValue();
		alt = (new Integer(((String) config.getAttribute("LOGO_CENTRIIMPIEGO.HEIGHT")))).intValue();

		section.addPicture(LEFT + 8000, 1, (larg), (alt), installAppPath + nomeFile);
		Vector elements = section.getElementsV();
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				((Element) elements.get(i)).setSuppress(true);
			}
		}
		section = area.addSection();
	}

	/**
	 * 0
	 */
	private boolean sezioneADR(Area details) throws Exception {
		String flgpatto297 = this.infoGenerali != null && this.infoGenerali.containsAttribute("flgpatto297")
				? this.infoGenerali.getAttribute("flgpatto297").toString()
				: "";
		Section section = details.addSection();
		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		// controllo sul flgpatto297 per lo stomper
		if (flgpatto297.equalsIgnoreCase(Values.FLAG_FALSE)) {
			section.addPicture(section.getWidth() - 10000, 0, 9372, 1450,
					installAppPath + "WEB-INF/report/img/Logo_Adr_modificato.jpg");
		} else {
			section.addPicture(section.getWidth() - 10000, 0, 9372, 1560,
					installAppPath + "WEB-INF/report/img/Logo_Adr_modificato.jpg");
		}
		return true;
	}

	/**
	 * 1 -Sezione protocollazione
	 * 
	 * @param pField
	 *            il prompt field del numero di protocollo
	 */

	private void protocollazioneTN(Area details, Field numProt, Field annoProt, Field dataProt) throws Exception {
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "Anno ", etichetta, LEFT, 700);
		// ReportUtils.addTextPart(t.getParagraph(0), this.numAnnoProt, campo);
		FieldPart tp = t.getParagraph(0).addFieldPart(annoProt);
		tp.setX(0);
		campoI.setStyle(tp);

		t = ReportUtils.addText(section, "Num ", etichetta, LEFT + 700, 600);
		// t = ReportUtils.addText(section, "Num ", etichetta, 420);

		tp = t.getParagraph(0).addFieldPart(numProt);
		tp.setX(0);
		campoI.setStyle(tp);
		t = ReportUtils.addText(section, "Data di prot. ", etichetta, LEFT + 1300, 700);
		// t = ReportUtils.addText(section, "Data di prot. ", etichetta, 920);
		// ReportUtils.addTextPart(t.getParagraph(0), this.datProtocollazione,
		// campo);
		tp = t.getParagraph(0).addFieldPart(dataProt);
		tp.setX(0);
		campoI.setStyle(tp);
	}

	private void protocollazione(Area details, Field numProt, Field annoProt, Field dataProt) throws Exception {
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "Anno ", etichetta, LEFT, 500);
		// ReportUtils.addTextPart(t.getParagraph(0), this.numAnnoProt, campo);
		FieldPart tp = t.getParagraph(0).addFieldPart(annoProt);
		tp.setX(0);
		campoI.setStyle(tp);
		t = ReportUtils.addText(section, "Num ", etichetta, 420);
		// ReportUtils.addTextPart(t.getParagraph(0), this.numProtocolloStr,
		// campo);
		// inserisco il promptField nel campo di visualizzazione del numero di
		// protocollo
		// per questa operazione non esiste una utilità
		tp = t.getParagraph(0).addFieldPart(numProt);
		tp.setX(0);
		campoI.setStyle(tp);
		t = ReportUtils.addText(section, "Data di prot. ", etichetta, 920);
		// ReportUtils.addTextPart(t.getParagraph(0), this.datProtocollazione,
		// campo);
		tp = t.getParagraph(0).addFieldPart(dataProt);
		tp.setX(0);
		campoI.setStyle(tp);

		section = details.addSection();
		t = ReportUtils.addText(section, "Doc. di ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), this.docInOut, campoI);
		t = ReportUtils.addText(section, "Rif. ", etichetta, 420);
		ReportUtils.addTextPart(t.getParagraph(0), this.riferimentoDoc, campoI);
	}

	/**
	 * 2 - intestazione stampa
	 * 
	 * @param details
	 * @throws Exception
	 */
	private void intestazionePattoAccordo(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Patto di ricerca intensiva alla ricollocazione";
		ReportUtils.addText(section, s.toUpperCase(), titolo2Centrato, LEFT);
	}

	/**
	 * 3 -dati del soggetto erogatore
	 * 
	 * @param operatore
	 * @param infoGenerali
	 */
	private void datiSoggettoErogatore(Area details, SourceBean operatore, SourceBean infoGenerali) throws Exception {
		if (operatore == null)
			operatore = new SourceBean("");
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");

		String nomeTutor = Utils.notNull(operatore.getAttribute("ROW.STRNOME"));
		String cognomeTutor = Utils.notNull(operatore.getAttribute("ROW.STRCOGNOME"));

		String cpi = Utils.notNull(infoGenerali.getAttribute("CPI"));
		String cpiTel = Utils.notNull(operatore.getAttribute("ROW.strteloperatore"));
		String cpiMail = Utils.notNull(operatore.getAttribute("ROW.stremail"));

		Section section = details.addSection();
		ReportUtils.addText(section, "Tra", etichetta, LEFT + 800);
		section = details.addSection();
		Text t = ReportUtils.addText(section, "Il ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), " soggetto erogatore dei servizi di assistenza intensiva: ", campo);
		ReportUtils.addTextPart(t.getParagraph(0), "Centro per l'impiego di " + cpi, etichetta);

		section = details.addSection();
		StringBuilder dati = new StringBuilder("Tutor: ").append(nomeTutor).append(" ").append(cognomeTutor);
		dati.append("\nnumero di telefono: ").append(cpiTel);
		t = ReportUtils.addText(section, dati.toString(), etichetta, LEFT);

		section = details.addSection();
		t = ReportUtils.addText(section, "indirizzo di posta elettronica: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), cpiMail, etichetta);
	}

	/**
	 * 4 -dati lavoratore (destinatario dell'assegno di ricollocazione)
	 * 
	 * @param details
	 * @param infoLavoratore
	 */
	private void datiDestinatarioAssegno(Area details, SourceBean infoLavoratore) throws Exception {
		// le informazioni del lavoratore dovrebbero sempre esserci .....
		if (infoLavoratore == null)
			infoLavoratore = new SourceBean("");

		String nome = Utils.notNull(infoLavoratore.getAttribute("NOMELAVORATORE"));
		String cognome = Utils.notNull(infoLavoratore.getAttribute("COGNOMELAVORATORE"));
		String codiceFiscale = (String) infoLavoratore.getAttribute("STRCODICEFISCALE");
		String luogoNascita = Utils.notNull(infoLavoratore.getAttribute("com_nasc"));
		String dataNascita = Utils.notNull(infoLavoratore.getAttribute("dat_nasc"));
		String comDomicilio = Utils.notNull(infoLavoratore.getAttribute("com_dom"));
		String indDomicilio = Utils.notNull(infoLavoratore.getAttribute("strindirizzodom"));
		String telefono = Utils.notNull(infoLavoratore.getAttribute("strteldom"));
		String email = Utils.notNull(infoLavoratore.getAttribute("stremail"));
		String dataStipula = Utils.notNull(infoLavoratore.getAttribute("datstipula"));
		String dataScadenzaConferma = Utils.notNull(infoLavoratore.getAttribute("datscadconferma"));
		String dataInizioNaspi = Utils.notNull(infoLavoratore.getAttribute("dat_naspi"));
		String indiceProfilo = Utils.notNull(infoLavoratore.getAttribute("numindicesvantaggio150"));
		String dataRiferimento = Utils.notNull(infoLavoratore.getAttribute("datriferimento150"));
		String importoAssegnoNaspi = "";
		if (infoLavoratore.containsAttribute("Importoar") && infoLavoratore.getAttribute("Importoar") != null) {
			importoAssegnoNaspi = infoLavoratore.getAttribute("Importoar").toString() + " euro";
		}

		Section section = details.addSection();
		ReportUtils.addText(section, "e", etichetta, LEFT + 800);
		section = details.addSection();
		Text t = ReportUtils.addText(section, "il", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), " destinatario dell'assegno di ricollocazione ", campo);
		ReportUtils.addTextPart(t.getParagraph(0), "signor/a ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), nome + " " + cognome, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "codice fiscale ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), codiceFiscale, etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), " nato/a a ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), luogoNascita, etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), ", il ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), dataNascita, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "domiciliato/a a ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), comDomicilio, etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), " " + indDomicilio.trim(), etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "numero di telefono: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), telefono, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "indirizzo di posta elettronica: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), email, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "data stipula: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), dataStipula, etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), " data scadenza conferma: ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), dataScadenzaConferma, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "data di inizio fruizione NASPI: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), dataInizioNaspi, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "indice del profilo personale di occupabilità: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), indiceProfilo, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "data di riferimento: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), dataRiferimento, etichetta);
		section = details.addSection();
		t = ReportUtils.addText(section, "importo massimo dell'assegno individuale di ricollocazione: ", etichetta,
				LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), importoAssegnoNaspi, etichetta);
		//

	}

	/**
	 * 5 - parte fissa
	 */
	private void impegniCPI(Area details) throws Exception {
		// parti fisse da stampare
		String intestazioneImpegniCpi = "Il Centro per l'impiego/soggetto privato accreditato eroga al "
				+ "lavoratore disoccupato il servizio di assistenza alla ricollocazione " + "che comprende: ";
		Section section = details.addSection();
		ReportUtils.addText(section, intestazioneImpegniCpi, etichetta, LEFT);
		section = details.addSection();
		String punto1 = "finalizzato ad assistere in modo continuativo il soggetto in tutte le attività "
				+ "necessarie alla sua ricollocazione, attraverso l'assegnazione di un tutor, "
				+ "la definizione e condivisione di un programma personalizzato " + "per la ricerca attiva di lavoro.";
		Text t = ReportUtils.addText(section, "1. ", etichetta, LEFT_TITOLO_PUNTO_T);
		ReportUtils.addTextPart(t.getParagraph(0), "Assistenza alla persona e tutoraggio ", campo);
		ReportUtils.addTextPart(t.getParagraph(0), punto1, etichetta);
		section = details.addSection();
		String punto2 = "finalizzata alla promozione del profilo professionale del "
				+ "titolare dell'assegno di ricollocazione verso i potenziali "
				+ "datori di lavoro, alla selezione dei posti vacanti, all'assitenza alla preselezione, "
				+ "sino alle prime fasi di inserimento in azienda.";
		t = ReportUtils.addText(section, "2. ", etichetta, LEFT_TITOLO_PUNTO_T);
		ReportUtils.addTextPart(t.getParagraph(0), "Ricerca intensiva di opportunità occupazionali ", campo);
		ReportUtils.addTextPart(t.getParagraph(0), punto2, etichetta);

	}

	/**
	 * 6- Sezione misure: azioni collegate al patto per gli esiti proposto/in corso, avviato, concluso, rifiutato,
	 * interrotto
	 */
	@SuppressWarnings("rawtypes")
	private void azioniPatto(Area details, Vector azioni) throws Exception {

		if (!azioni.isEmpty()) {
			Section section = details.addSection();
			String temp = "Il programma di ricerca intensiva prevede le attività indicate di seguito: ";
			ReportUtils.addText(section, temp, etichetta, LEFT);
			addBR(details, 40);
			for (int i = 0; i < azioni.size(); i++) {
				if (i > 0 && i < azioni.size()) {
					addBR(details, 40);
				}
				SourceBean misura = (SourceBean) azioni.get(i);
				section = details.addSection();
				Text t = ReportUtils.addText(section, "Misura: ", campo, LEFT);
				ReportUtils.addTextPart(t.getParagraph(0), misura.getAttribute("misura").toString(), etichetta);
				section = details.addSection();
				t = ReportUtils.addText(section, "Esito: ", campo, LEFT);
				String esito = Utils.notNull(misura.getAttribute("esito"));
				ReportUtils.addTextPart(t.getParagraph(0), esito, etichetta);
				section = details.addSection();
				t = ReportUtils.addText(section, "Note: ", campo, LEFT);
				String strNote = Utils.notNull(misura.getAttribute("Strnote"));
				ReportUtils.addTextPart(t.getParagraph(0), strNote, etichetta);
				section = details.addSection();
				String notaaggiuntiva = Utils.notNull(misura.getAttribute("notaaggiuntiva"));
				if (StringUtils.isFilledNoBlank(notaaggiuntiva)) {
					String[] infoAzione = notaaggiuntiva.split("-");
					t = ReportUtils.addText(section, infoAzione[0], etichetta, LEFT);
					for (int k = 1; k < infoAzione.length; k++) {
						ReportUtils.addTextPart(t.getParagraph(0), infoAzione[k], (k % 2 == 0) ? etichetta : campo);
					}
				}
			}
		}

	}

	/**
	 * 7 - appuntamenti legati al patto
	 * 
	 * @param appuntamenti
	 */
	@SuppressWarnings("rawtypes")
	private void appuntamentiPatto(Area details, Vector appuntamenti) throws Exception {

		if (!appuntamenti.isEmpty()) {

			for (int i = 0; i < appuntamenti.size(); i++) {
				if (i > 0 && i < appuntamenti.size()) {
					addBR(details, 40);
				}
				Section section = details.addSection();
				SourceBean appuntamento = (SourceBean) appuntamenti.get(i);
				String tipoApp = Utils.notNull(appuntamento.getAttribute("Codstatoappuntamento"));
				StringBuilder incipit = new StringBuilder("è stato fissato un appuntamento con ");
				incipit.append("il Centro per l'impiego/soggetto privato accreditato mediante presentazione ");
				incipit.append("al servizio di ").append(appuntamento.getAttribute("DesServizio").toString());
				int tipoAppuntamento = 0;
				if (!tipoApp.equals("")) {
					tipoAppuntamento = Integer.valueOf(tipoApp).intValue();

					switch (tipoAppuntamento) {
					case 2:
						incipit.append(" da svolgersi entro il ");
						break;
					case 3:
						incipit.append(" che si è svolto il ");
						break;
					case 4:
						incipit.append(" che doveva svolgersi entro il ");
						break;

					default:
						break;
					}
					incipit.append(appuntamento.getAttribute("dataApp").toString());
				}
				ReportUtils.addText(section, incipit.toString(), etichetta, LEFT);

				section = details.addSection();
				Text t = ReportUtils.addText(section, "Stato appuntamento: ", campo, LEFT);
				ReportUtils.addTextPart(t.getParagraph(0), appuntamento.getAttribute("DesStato").toString(), etichetta);
				section = details.addSection();
				if (tipoAppuntamento != 2) {
					t = ReportUtils.addText(section, "Esito appuntamento: ", campo, LEFT);
					ReportUtils.addTextPart(t.getParagraph(0), appuntamento.getAttribute("DesEsito").toString(),
							etichetta);
					section = details.addSection();
				}
				t = ReportUtils.addText(section, "Orario: ", campo, LEFT);
				ReportUtils.addTextPart(t.getParagraph(0), appuntamento.getAttribute("orario").toString(), etichetta);
				section = details.addSection();
				t = ReportUtils.addText(section, "Durata appuntamento: ", campo, LEFT);
				String durata = Utils.notNull(appuntamento.getAttribute("Durata"));
				ReportUtils.addTextPart(t.getParagraph(0), durata + " minuti", etichetta);
			}
		}
	}

	/**
	 * 8 - visualizza le eventuali note legate agli elementi di attivazione indicati nella sezione dell'assegno di
	 * ricollocazione
	 * 
	 * @param infoGenerali
	 */
	private void noteAttivazione(Area details, SourceBean infoGenerali) throws Exception {
		//
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");

		String noteAttivazione = Utils.notNull(infoGenerali.getAttribute("Strnoteattivazione"));
		if (StringUtils.isFilledNoBlank(noteAttivazione)) {
			addBR(details, 40);
			Section section = details.addSection();
			ReportUtils.addText(section,
					"Il programma di ricerca intensiva prevede inoltre i seguenti elementi di attivazione:", etichetta,
					LEFT);
			section = details.addSection();
			ReportUtils.addText(section, noteAttivazione, etichetta, LEFT);
		}
		String notePatto = Utils.notNull(infoGenerali.getAttribute("Strnote"));
		if (StringUtils.isFilledNoBlank(notePatto)) {
			addBR(details, 40);
			Section section = details.addSection();
			ReportUtils.addText(section, notePatto, etichetta, LEFT);
		}
	}

	/**
	 * 9 - parte fissa
	 */
	private void paginaInformazioni(Area details, String dataStipula) throws Exception {

		addBR(details, 50);

		Section section = details.addSection();
		ReportUtils.addText(section, "Informazioni per l'utente", titolo2, LEFT);
		StringBuilder infoGenereali = new StringBuilder("");
		infoGenereali.append(
				"Il patto di servizio personalizzato eventualmente stipulato con il Centro per l'impiego competente ");
		infoGenereali.append("è sospeso per tutta la durata del servizio di assistenza alla ricollocazione.\n");
		infoGenereali.append("Il destinatario dell'assegno di ricollocazione si impegna a:\n");
		infoGenereali.append("a) svolgere le attività individuate dal tutor;\n");
		infoGenereali.append("b) accettare un'offerta di lavoro congrua.\n");
		infoGenereali.append("Il soggetto erogatore del servizio di assistenza intensiva ha l'obbligo di comunicare ");
		infoGenereali
				.append("all'ANPAL ed al centro per l'impiego competente (nel caso in cui sia un soggetto diverso) ");
		infoGenereali.append("il rifiuto ingiustificato, da parte della persona interessata, di svolgere le attività ");
		infoGenereali.append(
				"individuate dal tutor o di una offerta di lavoro congrua al fine dell'irrogazione delle sanzioni ");
		infoGenereali.append(
				"di cui al combinato disposto dell'articolo 23, comma 5, lettera e) e dell'articolo 21, commi 7 del ");
		infoGenereali.append("d. lgs. 150/2015, ovvero:\n");
		infoGenereali.append(
				"a) in caso di mancata presentazione, in assenza di giustificato motivo, alle convocazioni ovvero agli ");
		infoGenereali.append(
				"appuntamenti di cui all' art. 20, comma 1 e comma 2, lettera d) e all'art. 21 commi 2 e 6 del d.lgs. 150/2015:");
		section = details.addSection();
		ReportUtils.addText(section, infoGenereali.toString(), etichetta, LEFT);

		infoGenereali = new StringBuilder();
		infoGenereali
				.append("1) la decurtazione di un quarto di una mensilità, in caso di prima mancata presentazione;\n");
		infoGenereali.append("2) la decurtazione di una mensilità, alla seconda mancata presentazione;\n");
		infoGenereali.append(
				"3) la decadenza dalla prestazione e dallo stato di disoccupazione, in caso di ulteriore mancata presentazione;");
		section = details.addSection();
		ReportUtils.addText(section, infoGenereali.toString(), etichetta, LEFT_TITOLO_PUNTO_T);

		infoGenereali = new StringBuilder();
		infoGenereali.append(
				"b) in caso di mancata partecipazione, in assenza di giustificato motivo, alle iniziative di orientamento di cui ");
		infoGenereali.append(
				"all'articolo 20, comma 3, lettera a) del d lgs 150/15, le medesime conseguenze di cui all'art. 21 comma 7 del d lgs 150/15;\n");
		infoGenereali.append(
				"c) in caso di mancata partecipazione, in assenza di giustificato motivo, alle iniziative di cui all'articolo 20, comma 3, lettera b):");
		section = details.addSection();
		ReportUtils.addText(section, infoGenereali.toString(), etichetta, LEFT);

		infoGenereali = new StringBuilder();
		infoGenereali.append("1) la decurtazione di una mensilità, alla prima mancata partecipazione;\n");
		infoGenereali.append(
				"2) la decadenza dalla prestazione e dallo stato di disoccupazione, in caso di ulteriore mancata presentazione;");
		section = details.addSection();
		ReportUtils.addText(section, infoGenereali.toString(), etichetta, LEFT_TITOLO_PUNTO_T);

		infoGenereali = new StringBuilder();
		infoGenereali.append(
				"d) in caso di mancata accettazione di un'offerta di lavoro congrua in assenza di giustificato motivo, ");
		infoGenereali.append("la decadenza dalla prestazione e dallo stato di disoccupazione.\n");
		section = details.addSection();
		ReportUtils.addText(section, infoGenereali.toString(), etichetta, LEFT);

		addBR(details, 40);

		section = details.addSection();
		ReportUtils.addText(section, "SOSPENSIONE", titolo2, LEFT);
		StringBuilder sospensione = new StringBuilder("");
		sospensione.append("Il servizio di assistenza alla ricollocazione è sospeso nel caso di assunzione in prova, ");
		sospensione.append(
				"o a termine, con eventuale ripresa del servizio stesso dopo l'eventuale conclusione del rapporto entro il termine di sei mesi.");

		section = details.addSection();
		ReportUtils.addText(section, sospensione.toString(), etichetta, LEFT);

		addBR(details, 40);

		section = details.addSection();
		ReportUtils.addText(section, "AVVERTENZE", titolo2, LEFT);
		StringBuilder avvertenze = new StringBuilder("");
		avvertenze.append("Nel caso in cui il soggetto erogatore sia diverso dal centro per l'impiego competente al ");
		avvertenze.append("rilascio dell'assegno di ricollocazione, lo stesso soggetto erogatore deve comunicare al ");
		avvertenze.append("centro per l'impiego tutti gli eventi, utili per l'aggiornamento del patto di servizio, ");
		avvertenze.append("nonchè quelli che determinano l'applicazione dei meccanismi di condizionalità, producendo ");
		avvertenze.append("al centro per l'impiego idonea documentazione a supporto.");
		section = details.addSection();
		ReportUtils.addText(section, avvertenze.toString(), etichetta, LEFT);

		addBR(details, 40);

		section = details.addSection();
		ReportUtils.addText(section, "INFORMAZIONI", titolo2, LEFT);
		StringBuilder informazioni = new StringBuilder("");
		informazioni.append(
				"Il presente Patto di assistenza intensiva alla ricollocazione perde efficacia con la perdita ");
		informazioni.append("dello stato di disoccupazione e di decadenza dalla prestazione.");
		section = details.addSection();
		ReportUtils.addText(section, informazioni.toString(), etichetta, LEFT);

		addBR(details, 60);
		section = details.addSection();
		ReportUtils.addText(section, "Data   " + DateUtils.getNow(), etichetta, LEFT);

		addBR(details, 50);
		section = details.addSection();
		ReportUtils.addText(section, "Letto, firmato e sottoscritto", etichetta, LEFT);

		addBR(details, 50);
		section = details.addSection();
		ReportUtils.addText(section, "Il tutor", etichetta, LEFT);
		addBR(details, 30);
		section = details.addSection();
		ReportUtils.addText(section, "______________________________________________", etichetta, LEFT);

		addBR(details, 50);
		section = details.addSection();
		ReportUtils.addText(section, "Il destinatario dell'assegno di ricollocazione", etichetta, LEFT);
		addBR(details, 30);
		section = details.addSection();
		ReportUtils.addText(section, "______________________________________________", etichetta, LEFT);

	}

	private void addBR(Area details, int height) throws Exception {
		Section section = details.addSection();
		section.setHeight(StyleUtils.toTwips(height));
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			ApiPattoAccordoADR p = new ApiPattoAccordoADR(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}

}
