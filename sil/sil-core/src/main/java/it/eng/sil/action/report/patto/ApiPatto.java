package it.eng.sil.action.report.patto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.inet.report.Area;
import com.inet.report.BorderProperties;
import com.inet.report.Element;
import com.inet.report.Engine;
import com.inet.report.Field;
import com.inet.report.FieldPart;
import com.inet.report.Paragraph;
import com.inet.report.Picture;
import com.inet.report.PromptField;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Subreport;
import com.inet.report.Text;
import com.inet.report.TextPart;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.MansioniRow;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

/**
 * 
 * @author Administrator
 */
public class ApiPatto {

	private final Logger _logger = Logger.getLogger(ApiPatto.class.getName());

	Style campo;
	Style titoloSezione;
	Style etichetta;
	Style etichetta2;
	Style etichetta4;
	Style titolo2;
	Style titolo3;
	Style titolo2Centrato;
	StyleFactory styleFactory;
	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;
	private int LEFT_TITOLO_PUNTO = LEFT;
	private int LEFT_TITOLO_PUNTO_T_INTERMEDIO = LEFT_TITOLO_PUNTO + 70;
	private int LEFT_TITOLO_PUNTO_T = LEFT_TITOLO_PUNTO + 100;

	private int ACCETTAZIONELENMAXLAV = 30;
	private int SPAZI_TRA_OPERATORE_LAV = 85;
	private int SPAZI_TRA_OPERATORE_LAV_MIN = 90;
	//
	private SourceBean statoOccupazionale = null;
	private Vector ambitoProfessionale = null;
	private SourceBean cat181 = null;
	private Vector appuntamenti = null;
	private Vector azioniConcordate = null;
	private SourceBean conferimentoDid = null;
	private SourceBean infoGenerali = null;
	private SourceBean operatore = null;
	private Vector impegni = null;
	private Vector laurea;
	private Vector movimenti;
	private String docInOut;
	private String regione;
	private Vector<String> programmi;
	private HashMap<BigDecimal, String> codMonoProgColloqui;
	private Vector<String> programmiAperti;
	private static final String[] misureGG = { PattoBean.DB_MISURE_GARANZIA_GIOVANI,
			PattoBean.DB_MISURE_NUOVA_GARANZIA_GIOVANI };
	//

	private String datProtocollazione;
	private String codMonoIO;
	private String riferimentoDoc;
	//
	private Engine eng = null;
	private String installAppPath = null;

	private String fileType = Engine.EXPORT_PDF;

	private String codTipoPattoMisura;

	private String privacy;

	private String flgPattoOnline;

	private Vector soggettiAccreditati = null;
	private TransactionQueryExecutor txExec = null;

	public void setSoggettiAccreditati(Vector soggettiAccreditati) {
		this.soggettiAccreditati = soggettiAccreditati;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setInstallAppPath(String installAppPath) {
		this.installAppPath = installAppPath;
	}

	public void setTransaction(TransactionQueryExecutor txExecCurr) {
		this.txExec = txExecCurr;
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

	public void setConferimentoDid(SourceBean lastConferimentoDid) {
		this.conferimentoDid = lastConferimentoDid;
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

	public void setStatoOccupazionale(SourceBean statoOccupazionale) {
		this.statoOccupazionale = statoOccupazionale;
	}

	public void setAmbitoProfessionale(Vector ambitoProfessionale) throws Exception {
		this.ambitoProfessionale = MansioniRow.getMansioni(ambitoProfessionale);
	}

	public void setImpegni(Vector impegni) {
		this.impegni = impegni;
	}

	public void setCat181(SourceBean cat181) {
		this.cat181 = cat181;
	}

	public void setOperatore(SourceBean operatore) {
		this.operatore = operatore;
	}

	public void setLaurea(Vector laurea) {
		this.laurea = laurea;
	}

	public void setMovimenti(Vector movimenti) {
		this.movimenti = movimenti;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public void setDatProtocollazione(String s) {
		this.datProtocollazione = s;
	}

	public void setCodMonoIO(String s) {
		this.codMonoIO = s;
	}

	public void setDocInOut(String s) {
		this.docInOut = s;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	/** Creates a new instance of Patto */
	public ApiPatto() {
		styleFactory = new StyleFactory();
		campo = styleFactory.getStyle(StyleFactory.CAMPO);
		titoloSezione = styleFactory.getStyle(StyleFactory.TITOLO_SEZIONE);
		etichetta = styleFactory.getStyle(StyleFactory.ETICHETTA);
		etichetta2 = styleFactory.getStyle(StyleFactory.ETICHETTA2);
		etichetta4 = styleFactory.getStyle(StyleFactory.ETICHETTA4);
		titolo2 = styleFactory.getStyle(StyleFactory.TITOLO2);
		titolo3 = styleFactory.getStyle(StyleFactory.TITOLO3);
		titolo2Centrato = styleFactory.getStyle(StyleFactory.TITOLO2CENTRATO);

	}

	public ApiPatto(String[] a) throws Exception {
		start();
	}

	public void inizializza(SourceBean infoGen, SourceBean statoOcc, Vector appuntaments, SourceBean operatore,
			Vector azioniConcordats, Vector ambitoProfs, Vector impegniV, String installAppPath, String tipoFile,
			SourceBean cat181, Vector titoloStudio, Vector mov, String ambito, String strParam, String docInOut,
			String regione, SourceBean documentoIdentita, SourceBean lastConferimentoDid, String privacy,
			Vector soggetti, TransactionQueryExecutor txExec) throws Exception {
		try {
			this.setConferimentoDid(lastConferimentoDid);
			this.setInfoGenerali(infoGen);
			this.setStatoOccupazionale(statoOcc);
			this.setAppuntamenti(appuntaments);
			this.setAzioniConcordate(azioniConcordats);
			this.setAmbitoProfessionale(ambitoProfs);
			this.setOperatore(operatore);
			this.setImpegni(impegniV);
			this.setInstallAppPath(installAppPath);
			this.setFileType(tipoFile);
			this.setCat181(cat181);
			this.setLaurea(titoloStudio);
			this.setMovimenti(mov);
			this.setRiferimentoDoc(ambito);
			this.regione = regione;
			this.setPrivacy(privacy);
			this.setSoggettiAccreditati(soggetti);
			if (txExec != null) {
				this.setTransaction(txExec);
			}

			if (strParam != null && !strParam.equals("")) {
				this.setDatProtocollazione(strParam);
			}

			if (strParam != null && !strParam.equals("")) {
				this.setCodMonoIO(strParam);
				if (strParam.equalsIgnoreCase("I"))
					this.setDocInOut("Input");
				else
					this.setDocInOut("Output");
			}
			if (this.infoGenerali != null) {
				BigDecimal prgPattoLav = (BigDecimal) this.infoGenerali.getAttribute("prgpattolavoratore");
				this.programmi = PattoBean.checkAllProgrammi(prgPattoLav, this.txExec);
				this.codMonoProgColloqui = PattoBean.checkAllProgrammiColloquio(prgPattoLav, this.txExec);
				this.programmiAperti = PattoBean.checkProgrammi(prgPattoLav, this.txExec);
				this.flgPattoOnline = this.infoGenerali.containsAttribute("FLGPATTOONLINE")
						? this.infoGenerali.getAttribute("FLGPATTOONLINE").toString()
						: "";
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
			// loghi ARL
			if (sezioneARL(details)) {
				addBR(details, 40);
			}

			protocollazione(details, pNumProt, pAnnoProt, pDataProt);

			if (!this.regione.equals(Properties.RER)) {
				addBR(details, 40);
				testataCPI(details, infoGenerali);
			}

			addBR(details, 40);
			titoloMinore(details);

			// datiCPI(details, operatore, infoGenerali);
			datiCPINoOperatore(details, infoGenerali);
			//
			datiLavoratore(details, infoGenerali);

			// addBR(details, 40);
			impegni(details, impegni);

			// addBR(details, 40);
			azioniPatto(details, azioniConcordate);

			// addBR(details, 40);
			appuntamentiPatto(details, appuntamenti);

			// addBR(details, 40);
			enteAccreditatoNuovoPatto(details, soggettiAccreditati);

			if (conferimentoDid != null && conferimentoDid.containsAttribute("ROW")) {
				addBR(details, 40);
				conferimentoDid(details, conferimentoDid);
			}

			addBR(details, 40);
			informazioniUtente(details, infoGenerali);

			details.addSection();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Sezione protocollazione
	 * 
	 * @param pField
	 *            il prompt field del numero di protocollo
	 */
	private void protocollazione(Area details, Field numProt, Field annoProt, Field dataProt) throws Exception {
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "Anno ", etichetta, LEFT, 500);
		// ReportUtils.addTextPart(t.getParagraph(0), this.numAnnoProt, campo);
		FieldPart tp = t.getParagraph(0).addFieldPart(annoProt);
		tp.setX(0);
		campo.setStyle(tp);
		t = ReportUtils.addText(section, "Num ", etichetta, 420);
		tp = t.getParagraph(0).addFieldPart(numProt);
		tp.setX(0);
		campo.setStyle(tp);
		t = ReportUtils.addText(section, "Data di prot. ", etichetta, 920);
		tp = t.getParagraph(0).addFieldPart(dataProt);
		tp.setX(0);
		campo.setStyle(tp);

		section = details.addSection();
		t = ReportUtils.addText(section, "Doc. di ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), this.docInOut, campo);
		t = ReportUtils.addText(section, "Rif. ", etichetta, 420);
		ReportUtils.addTextPart(t.getParagraph(0), this.riferimentoDoc, campo);
	}

	private boolean sezioneARL(Area details) throws Exception {
		if (this.regione.equals(Properties.RER)) {
			codTipoPattoMisura = this.infoGenerali != null && this.infoGenerali.containsAttribute("codtipopatto")
					? this.infoGenerali.getAttribute("codtipopatto").toString()
					: "";
			Section section = details.addSection();
			String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;

			boolean isPattoGG = false;

			if (this.programmiAperti != null && !this.programmiAperti.isEmpty()
					&& PattoBean.checkMisureProgramma(this.programmiAperti, misureGG)) {
				isPattoGG = true;
			}

			if (Arrays.asList(misureGG).contains(codTipoPattoMisura) || isPattoGG) {
				section.addPicture(section.getWidth() - 10000, 0, 9372, 1560,
						installAppPath + "WEB-INF/report/img/Regione_Carta_Intestata_Garanzia_Giovani.jpg");
			} else {
				section.addPicture(section.getWidth() - 10000, 0, 9372, 1560,
						installAppPath + "WEB-INF/report/img/Logo_Adr_modificato.jpg");
			}
			return true;
		}
		return false;
	}

	private void titoloMinore(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Patto di servizio personalizzato per adesione alle misure concordate con il Centro per l'Impiego per l'uscita dallo stato"
				+ " di disoccupazione ai sensi del D.Lgs 150/2015";
		ReportUtils.addText(section, s, etichetta, LEFT);
	}

	/**
	 * sezione dati CPI
	 * 
	 * @param operatore
	 * @param infoGenerali
	 */
	private void datiCPI(Area details, SourceBean operatore, SourceBean infoGenerali) throws Exception {
		if (operatore == null)
			operatore = new SourceBean("");
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");

		String nomeOp = Utils.notNull(operatore.getAttribute("ROW.STRNOME"));
		String cognomeOP = Utils.notNull(operatore.getAttribute("ROW.STRCOGNOME"));

		String cpi = Utils.notNull(infoGenerali.getAttribute("CPI"));
		String cpiTelOp = Utils.notNull(operatore.getAttribute("ROW.strteloperatore"));
		String cpiMailOp = Utils.notNull(operatore.getAttribute("ROW.stremail"));

		Section section = details.addSection();
		ReportUtils.addText(section, "Tra", etichetta, LEFT + 800);
		section = details.addSection();
		Text t = ReportUtils.addText(section, "Il Responsabile delle attività(Centro per l'impiego) " + cpi, etichetta,
				LEFT);

		section = details.addSection();
		StringBuilder datiOp = new StringBuilder("Operatore del CPI ").append(nomeOp).append(" ").append(cognomeOP);
		if (!cpiTelOp.equals("")) {
			datiOp.append("\nnumero di telefono ").append(cpiTelOp);
		}
		if (!cpiMailOp.equals("")) {
			datiOp.append("\nindirizzo di posta elettronica ").append(cpiMailOp);
		}
		t = ReportUtils.addText(section, datiOp.toString(), etichetta, LEFT);
	}

	/**
	 * sezione dati CPI rivista
	 * 
	 * @param infoGenerali
	 */
	private void datiCPINoOperatore(Area details, SourceBean infoGenerali) throws Exception {
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");

		String cpi = Utils.notNull(infoGenerali.getAttribute("CPI"));
		String via = Utils.notNull(infoGenerali.getAttribute("INDIRIZZO"));
		String tel = Utils.notNull(infoGenerali.getAttribute("TEL"));
		String fax = Utils.notNull(infoGenerali.getAttribute("FAX"));
		String email = Utils.notNull(infoGenerali.getAttribute("EMAIL"));

		Section section = details.addSection();
		ReportUtils.addText(section, "Tra", etichetta, LEFT + 800);
		section = details.addSection();
		Text t = ReportUtils.addText(section, "Centro per l'impiego di " + cpi.toUpperCase(), etichetta, LEFT);

		section = details.addSection();
		t = ReportUtils.addText(section, "indirizzo: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), via, etichetta);

		section = details.addSection();
		t = ReportUtils.addText(section, "tel.: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), tel, etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), "   fax: ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), fax, etichetta);

		section = details.addSection();
		t = ReportUtils.addText(section, "email: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), email, etichetta);
	}

	/**
	 * sezione dati lavoratore
	 * 
	 * @param details
	 * @param infoLavoratore
	 */
	private void datiLavoratore(Area details, SourceBean infoLavoratore) throws Exception {
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
		String dataDichiarazioneImmDisp = Utils.notNull(infoGenerali.getAttribute("datdichiarazione"));
		String indiceProfilo = Utils.notNull(infoLavoratore.getAttribute("numindicesvantaggio150"));
		String dataRiferimento = Utils.notNull(infoLavoratore.getAttribute("datriferimento150"));
		String strprofiling = Utils.notNull(infoLavoratore.getAttribute("strprofiling150"));
		String indiceGG = Utils.notNull(infoLavoratore.getAttribute("numindicesvantaggio2"));
		String dataRiferimentoGG = Utils.notNull(infoLavoratore.getAttribute("datriferimento"));
		String statoDichiarazione = Utils.notNull(infoLavoratore.getAttribute("statoDichiarazione"));

		Section section = details.addSection();
		ReportUtils.addText(section, "e", etichetta, LEFT + 800);

		section = details.addSection();
		StringBuilder datiLav = new StringBuilder("il/la Signor/a ").append(nome).append(" ").append(cognome);
		Text t = ReportUtils.addText(section, datiLav.toString(), etichetta, LEFT);

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

		if (!telefono.equals("")) {
			section = details.addSection();
			t = ReportUtils.addText(section, "numero di telefono: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), telefono, etichetta);
		}

		if (!email.equals("")) {
			section = details.addSection();
			t = ReportUtils.addText(section, "indirizzo di posta elettronica: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), email, etichetta);
		}

		section = details.addSection();
		if (!statoDichiarazione.equalsIgnoreCase(Properties.PATTO_PROVENIENZA_STIPULA_SO)) {
			t = ReportUtils.addText(section, "data dichiarazione di immediata disponibilità: ", etichetta, LEFT);
		} else {
			t = ReportUtils.addText(section, "data anzianità di disoccupazione: ", etichetta, LEFT);
		}
		ReportUtils.addTextPart(t.getParagraph(0), dataDichiarazioneImmDisp, etichetta);

		if (!indiceProfilo.equals("")) {
			section = details.addSection();
			t = ReportUtils.addText(section, "indice del profilo personale di occupabilità: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), indiceProfilo, etichetta);
			if (!strprofiling.equals("")) {
				ReportUtils.addTextPart(t.getParagraph(0), "	" + strprofiling, campo);
			}
		}

		if (!dataRiferimento.equals("")) {
			section = details.addSection();
			t = ReportUtils.addText(section, "data di riferimento: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), dataRiferimento, etichetta);
		}

		if (!indiceGG.equals("")) {
			section = details.addSection();
			t = ReportUtils.addText(section, "indice GG: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), indiceGG, etichetta);
		}

		if (!dataRiferimentoGG.equals("")) {
			section = details.addSection();
			t = ReportUtils.addText(section, "data di riferimento GG: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), dataRiferimentoGG, etichetta);
		}
	}

	/**
	 * sezione impegni lavoratore ed impegni cpi
	 */
	private void impegni(Area details, Vector impegni) throws Exception {
		if (!impegni.isEmpty()) {
			addBR(details, 40);
		}
		Style s = (Style) etichetta.clone();
		s.setFontSize(12);
		Section section = details.addSection();
		ReportUtils.addText(section, "In rispetto del presente Patto il lavoratore si impegna a:", s, LEFT);
		addBR(details, 20);
		Vector impegniLavoratore = new Vector();
		for (int i = 0; i < impegni.size(); i++) {
			// vengono selezionati gli impegni del lavoratore (codice 'S')
			SourceBean impegno = (SourceBean) impegni.get(i);
			String codiceImp = (String) impegno.getAttribute("codMonoImpegnoDi");
			if (Utils.notNull(codiceImp).equals("S"))
				impegniLavoratore.add(impegno);
		}
		// nel vettore impegni rimangono gli impegni del cpi (vengono tolti gli
		// impegni del lavoratore)
		impegni.removeAll(impegniLavoratore);
		impegniLavoratore(details, impegniLavoratore);
		addBR(details, 20);
		section = details.addSection();
		ReportUtils.addText(section, "Il CPI si impegna a:", s, LEFT);
		addBR(details, 20);
		impegniCPI(details, impegni);
	}

	/**
	 * Gli impegni del lavoratore vengono stampati in un elenco puntato
	 */
	private void impegniLavoratore(Area details, Vector impegniLavoratore) throws Exception {
		if (impegniLavoratore == null)
			return;

		for (int i = 0; i < impegniLavoratore.size(); i++) {
			SourceBean row = (SourceBean) impegniLavoratore.get(i);
			titoloPuntato(details, (String) row.getAttribute("strDescrizione"), etichetta);
			// impegni(details, row);
		}
	}

	/**
	 * Gli impegni del cpi vengono stampati in un elenco puntato
	 */
	private void impegniCPI(Area details, Vector impegniCpi) throws Exception {
		if (impegniCpi == null)
			return;

		for (int i = 0; i < impegniCpi.size(); i++) {
			SourceBean row = (SourceBean) impegniCpi.get(i);
			titoloPuntato(details, (String) row.getAttribute("strDescrizione"), etichetta);
			// impegni(details, row);
		}
	}

	/**
	 * Sezione misure: azioni collegate al patto per gli esiti proposto/in corso, avviato, concluso, rifiutato,
	 * interrotto
	 */
	@SuppressWarnings("rawtypes")
	private void azioniPatto(Area details, Vector azioni) throws Exception {
		if (!azioni.isEmpty()) {
			addBR(details, 40);
		}

		if (!azioni.isEmpty()) {
			codTipoPattoMisura = this.infoGenerali != null && this.infoGenerali.containsAttribute("codtipopatto")
					? this.infoGenerali.getAttribute("codtipopatto").toString()
					: "";
			Section section = details.addSection();
			String temp = "Il patto prevede i programmi/servizi indicati di seguito: ";
			ReportUtils.addText(section, temp, etichetta, LEFT);
			addBR(details, 40);

			String colloquio_before = "";
			String prestazione_before = "";
			boolean poc = false;
			for (int i = 0; i < azioni.size(); i++) {

				SourceBean programma = (SourceBean) azioni.get(i);

				boolean flgProgramma = false;
				String codMonoProgramma = programma.containsAttribute("CODMONOPROGRAMMA")
						? programma.getAttribute("CODMONOPROGRAMMA").toString()
						: "";
				if (codMonoProgramma != null && !codMonoProgramma.equals("")) {
					flgProgramma = true;
				}

				String colloquio = programma.getAttribute("PRGCOLLOQUIO").toString();

				String codMonoProg = this.codMonoProgColloqui.get(new BigDecimal(colloquio));
				if (StringUtils.isFilledNoBlank(codMonoProg)
						&& codMonoProg.equalsIgnoreCase(PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE)) {
					poc = true;
				} else {
					if (this.programmi == null || this.programmi.isEmpty()) {
						poc = codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE);
					} else
						poc = false;
				}
				String dataInizio = Utils.notNull(programma.getAttribute("dataInizioProg"));
				String dataFine = Utils.notNull(programma.getAttribute("dataFineProg"));
				String descrProgrServ = Utils.notNull(programma.getAttribute("descrizioneProgramma"));
				String prestazione = programma.getAttribute("azione_ragg").toString();

				if (flgProgramma && !colloquio_before.equalsIgnoreCase(colloquio)) {
					// PROGRAMMI
					addBR(details, 20);
					section = details.addSection();
					Text t = ReportUtils.addText(section, "Programma: ", etichetta4, LEFT);
					ReportUtils.addTextPart(t.getParagraph(0), descrProgrServ, etichetta);
					ReportUtils.addTextPart(t.getParagraph(0), " data inizio ", etichetta);
					ReportUtils.addTextPart(t.getParagraph(0), dataInizio, etichetta);
					// _logger.info("Programma "+descrProgrServ);
					if (StringUtils.isFilledNoBlank(dataFine)) {
						ReportUtils.addTextPart(t.getParagraph(0), " data fine ", etichetta);
						ReportUtils.addTextPart(t.getParagraph(0), dataFine, etichetta);
					}
					colloquio_before = colloquio;
					// addBR(details, 40);
				} else if (!flgProgramma && !colloquio_before.equalsIgnoreCase(colloquio)) {
					// SERVIZI
					addBR(details, 20);
					section = details.addSection();
					Text t = ReportUtils.addText(section, "Servizio: ", etichetta4, LEFT);
					ReportUtils.addTextPart(t.getParagraph(0), descrProgrServ, etichetta);
					ReportUtils.addTextPart(t.getParagraph(0), " data inizio ", etichetta);
					ReportUtils.addTextPart(t.getParagraph(0), dataInizio, etichetta);
					if (StringUtils.isFilledNoBlank(dataFine)) {
						ReportUtils.addTextPart(t.getParagraph(0), " data fine ", etichetta);
						ReportUtils.addTextPart(t.getParagraph(0), dataFine, etichetta);
					}
					colloquio_before = colloquio;
					// addBR(details, 40);
					// _logger.info("Servizio "+descrProgrServ);
				}

				Text t;
				if (!prestazione.equalsIgnoreCase(prestazione_before)) {
					addBR(details, 20);
					section = details.addSection();
					if (this.regione.equals(Properties.RER)) {
						t = ReportUtils.addText(section, "Prestazione: ", etichetta4, LEFT_TITOLO_PUNTO_T_INTERMEDIO);
					} else {
						t = ReportUtils.addText(section, "Obiettivo: ", etichetta4, LEFT_TITOLO_PUNTO_T_INTERMEDIO);
					}
					ReportUtils.addTextPart(t.getParagraph(0), prestazione, etichetta);
					prestazione_before = prestazione;
					// _logger.info("Prestazione "+prestazione);
				}
				if (!poc) {
					addBR(details, 20);
					section = details.addSection();
					if (this.regione.equals(Properties.RER)) {
						t = ReportUtils.addText(section, "Attività: ", etichetta4, LEFT_TITOLO_PUNTO_T);
					} else {
						t = ReportUtils.addText(section, "Azione: ", etichetta4, LEFT_TITOLO_PUNTO_T);
					}
					ReportUtils.addTextPart(t.getParagraph(0), programma.getAttribute("misura").toString(), etichetta);
					section = details.addSection();
					// _logger.info("Attività "+programma.getAttribute("misura").toString());
					t = ReportUtils.addText(section, "Esito: ", campo, LEFT_TITOLO_PUNTO_T);
					String esito = Utils.notNull(programma.getAttribute("esito"));
					ReportUtils.addTextPart(t.getParagraph(0), esito, etichetta);
					section = details.addSection();
					String notaaggiuntiva = Utils.notNull(programma.getAttribute("notaaggiuntiva"));
					if (StringUtils.isFilledNoBlank(notaaggiuntiva)) {
						String[] infoAzione = notaaggiuntiva.split("-");
						t = ReportUtils.addText(section, infoAzione[0], etichetta, LEFT_TITOLO_PUNTO_T);
						for (int k = 1; k < infoAzione.length; k++) {
							ReportUtils.addTextPart(t.getParagraph(0), infoAzione[k], (k % 2 == 0) ? etichetta : campo);
						}
					}
				}
				// addBR(details, 40);
			}
		}
	}

	/**
	 * appuntamenti legati al patto
	 * 
	 * @param appuntamenti
	 */
	@SuppressWarnings("rawtypes")
	private void appuntamentiPatto(Area details, Vector appuntamenti) throws Exception {

		if (!appuntamenti.isEmpty()) {
			addBR(details, 40);
			for (int i = 0; i < appuntamenti.size(); i++) {
				if (i > 0 && i < appuntamenti.size()) {
					addBR(details, 20);
				}
				Section section = details.addSection();
				SourceBean appuntamento = (SourceBean) appuntamenti.get(i);
				String tipoApp = Utils.notNull(appuntamento.getAttribute("Codstatoappuntamento"));
				StringBuilder incipit = new StringBuilder("è stato fissato un appuntamento con ");
				incipit.append("il Centro per l'impiego mediante presentazione ");
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

	private void enteAccreditatoPatto(Area details, SourceBean infoGenerali) throws Exception {
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");

		String cfEnteAccr = Utils.notNull(infoGenerali.getAttribute("cfEnteAccr"));

		if (Utils.notNull(cfEnteAccr).equals("")) {
			return;
		} else {
			addBR(details, 40);
			String ragSocEnte = Utils.notNull(infoGenerali.getAttribute("ragSocEnte"));
			String indEnte = Utils.notNull(infoGenerali.getAttribute("indEnte"));
			String telEnte = Utils.notNull(infoGenerali.getAttribute("telEnte"));
			String com_ente = Utils.notNull(infoGenerali.getAttribute("com_ente"));
			String notaEnte = Utils.notNull(infoGenerali.getAttribute("notaEnte"));

			Section section = details.addSection();
			if (notaEnte.equals("")) {
				ReportUtils.addText(section, "Soggetto Accreditato:", etichetta, LEFT);
			} else {
				ReportUtils.addText(section, notaEnte + " presso ", etichetta, LEFT);
			}

			section = details.addSection();
			StringBuilder datiEnte1 = new StringBuilder(ragSocEnte).append(" ");
			datiEnte1.append(" C.F. ").append(cfEnteAccr);
			ReportUtils.addText(section, datiEnte1.toString(), etichetta, LEFT);

			section = details.addSection();
			StringBuilder datiEnte2 = new StringBuilder(indEnte).append(" ").append(com_ente);
			ReportUtils.addText(section, datiEnte2.toString(), etichetta, LEFT);

			if (!telEnte.equals("")) {
				section = details.addSection();
				StringBuilder datiEnte3 = new StringBuilder("Tel. ").append(telEnte);
				ReportUtils.addText(section, datiEnte3.toString(), etichetta, LEFT);
			}
		}
	}

	private void enteAccreditatoNuovoPatto(Area details, Vector soggetti) throws Exception {
		if (soggetti == null || soggetti.isEmpty()) {
			return;
		} else {
			addBR(details, 40);
		}

		for (int i = 0; i < soggetti.size(); i++) {
			SourceBean ente = (SourceBean) soggetti.get(i);
			String cfEnteAccr = Utils.notNull(ente.getAttribute("cfEnteAccr"));
			String ragSocEnte = Utils.notNull(ente.getAttribute("ragSocEnte"));
			String indEnte = Utils.notNull(ente.getAttribute("indEnte"));
			String telEnte = Utils.notNull(ente.getAttribute("telEnte"));
			String com_ente = Utils.notNull(ente.getAttribute("com_ente"));
			String notaEnte = Utils.notNull(ente.getAttribute("notaEnte"));

			Section section = details.addSection();
			if (notaEnte.equals("")) {
				ReportUtils.addText(section, "Soggetto Accreditato:", etichetta, LEFT);
			} else {
				ReportUtils.addText(section, notaEnte + " presso ", etichetta, LEFT);
			}

			section = details.addSection();
			StringBuilder datiEnte1 = new StringBuilder(ragSocEnte).append(" ");
			datiEnte1.append(" C.F. ").append(cfEnteAccr);
			ReportUtils.addText(section, datiEnte1.toString(), etichetta, LEFT);

			section = details.addSection();
			StringBuilder datiEnte2 = new StringBuilder(indEnte).append(" ").append(com_ente);
			ReportUtils.addText(section, datiEnte2.toString(), etichetta, LEFT);

			if (!telEnte.equals("")) {
				section = details.addSection();
				StringBuilder datiEnte3 = new StringBuilder("Tel. ").append(telEnte);
				ReportUtils.addText(section, datiEnte3.toString(), etichetta, LEFT);
			}
			addBR(details, 40);
		}
	}

	private void conferimentoDid(Area details, SourceBean confDid) throws Exception {
		String decProfiling = Utils.notNull(confDid.getAttribute("ROW.DECPROFILING"));
		String dataProfiling = Utils.notNull(confDid.getAttribute("ROW.DATAPROFILING"));
		String numEta = Utils.notNull(confDid.getAttribute("ROW.NUMETA"));
		String sesso = Utils.notNull(confDid.getAttribute("ROW.STRSESSO"));
		String nomeProvRes = Utils.notNull(confDid.getAttribute("ROW.nomeprovres"));

		// Dati sul nucleo familiare
		String numNucleoFam = Utils.notNull(confDid.getAttribute("ROW.NUMNUCLEOFAM"));
		String flgFigliaCarico = Utils.notNull(confDid.getAttribute("ROW.FLGFIGLIACARICO"));
		String flgFigliMinorenni = Utils.notNull(confDid.getAttribute("ROW.FLGFIGLIMINORENNI"));

		// Esperienze precedenti
		String flgEspLavoro = Utils.notNull(confDid.getAttribute("ROW.FLGESPLAVORO"));
		String numMesiDiso = Utils.notNull(confDid.getAttribute("ROW.NUMMESIDISOCC"));
		String cupDescrizione = Utils.notNull(confDid.getAttribute("ROW.cupDescrizione"));
		String profDescrizione = Utils.notNull(confDid.getAttribute("ROW.profDescrizione"));

		// Altre informazioni
		String pritDescrizione = Utils.notNull(confDid.getAttribute("ROW.pritDescrizione"));
		String corsoDescrizione = Utils.notNull(confDid.getAttribute("ROW.corsoDescrizione"));
		String numMesiRicerLav = Utils.notNull(confDid.getAttribute("ROW.NUMMESIRICERCALAV"));
		String titoloDescrizione = Utils.notNull(confDid.getAttribute("ROW.titoloDescrizione"));

		// Dati ANPAL
		String cupDescrizioneCal = Utils.notNull(confDid.getAttribute("ROW.cupDescrizioneCal"));
		String numMesiDisoCal = Utils.notNull(confDid.getAttribute("ROW.NUMMESIDISOCC_CALC"));

		Section section = details.addSection();
		ReportUtils.addText(section, "Dati profiling D.Lgs. 150/2015", titoloSezione, LEFT);

		if (!decProfiling.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Profiling: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), decProfiling, etichetta);
		}

		if (!dataProfiling.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Data calcolo profiling: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), dataProfiling, etichetta);
		}

		if (!numEta.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Età: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), numEta, etichetta);
		}

		if (!sesso.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Genere: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), sesso, etichetta);
		}

		if (!nomeProvRes.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Provincia di residenza: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), nomeProvRes, etichetta);
		}

		addBR(details, 20);
		section = details.addSection();
		ReportUtils.addText(section, "Dati sul nucleo familiare", titoloSezione, LEFT);

		if (!numNucleoFam.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Numero di componenti della famiglia: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), numNucleoFam, etichetta);
		}

		if (!flgFigliaCarico.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Presenza di figli coabitanti e/o carico: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), flgFigliaCarico, etichetta);
		}

		if (!flgFigliMinorenni.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Presenza di figli coabitanti e/o carico con meno di 18 anni: ",
					etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), flgFigliMinorenni, etichetta);
		}

		addBR(details, 20);
		section = details.addSection();
		ReportUtils.addText(section, "Esperienze precedenti", titoloSezione, LEFT);

		if (!flgEspLavoro.equals("")) {
			section = details.addSection();
			ReportUtils.addText(section, flgEspLavoro, etichetta, LEFT);
		}

		if (!numMesiDiso.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Da quanti mesi ha concluso l'ultimo rapporto di lavoro: ", etichetta,
					LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), numMesiDiso, etichetta);
		}

		if (!cupDescrizione.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Condizione occupazionale un anno prima: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), cupDescrizione, etichetta);
		}

		if (!profDescrizione.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Posizione nella professione dell'ultima occupazione svolta: ",
					etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), profDescrizione, etichetta);
		}

		addBR(details, 20);
		section = details.addSection();
		ReportUtils.addText(section, "Altre informazioni", titoloSezione, LEFT);

		if (!pritDescrizione.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Durata presenza in Italia: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), pritDescrizione, etichetta);
		}

		if (!corsoDescrizione.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section,
					"Attualmente è iscritto a scuola/università o corso di form. prof. (IFP, IFTS, ITS): ", etichetta,
					LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), corsoDescrizione, etichetta);
		}

		if (!numMesiRicerLav.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Da quanti mesi sta cercando lavoro: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), numMesiRicerLav, etichetta);
		}

		if (!titoloDescrizione.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Titolo di studio: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), titoloDescrizione, etichetta);
		}

		addBR(details, 20);
		section = details.addSection();
		ReportUtils.addText(section, "Dati ANPAL", titoloSezione, LEFT);

		if (!cupDescrizioneCal.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Condizione occupazionale anno precedente calcolata: ", etichetta,
					LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), cupDescrizioneCal, etichetta);
		}

		if (!numMesiDisoCal.equals("")) {
			section = details.addSection();
			Text t = ReportUtils.addText(section, "Durata disoccupazione calcolata: ", etichetta, LEFT);
			ReportUtils.addTextPart(t.getParagraph(0), numMesiDisoCal, etichetta);
		}

	}

	/**
	 * LE INFORMAZIONI UTENTE VENGONO INSERITE NEL REPORT UTILIZZANDO UN SUBREPORT LETTO DA DISCO, ovvero un subreport
	 * creato col designer di Crystal Clear al quale vengono passati 3 parametri.
	 */
	private void informazioniUtente(Area details, SourceBean infoGenerali) throws Exception {
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");
		String cpi = (String) infoGenerali.getAttribute("CPI");
		String dataStipula = (String) infoGenerali.getAttribute("datStipula");
		String datScadConferma = (String) infoGenerali.getAttribute("datScadConferma");
		String responsabileCPI = (String) infoGenerali.getAttribute("responsabile");
		Section section = details.addSection();
		section.setNewPageBefore(false);
		// aggiunta del subreport da disco
		Subreport sub = section.addSubreport(0, 0, StyleUtils.toTwips(1950), StyleUtils.toTwips(56),
				"file:" + this.installAppPath + "/WEB-INF/report/patto/_informazioniUtente_new_CC_RER.rpt");
		// impostazione dei bordi e degli headers e footers
		// N.B. quando viene importato un subreport le informazioni sul layout
		// del page header, page footer,
		// report header e footer vengono perse, per cui e' necessario
		// reimpostarle.
		// In questo caso voglio eliminarli e togliere il bordo.
		sub.setLeftLineStyle(BorderProperties.NO_LINE);
		sub.setBottomLineStyle(BorderProperties.NO_LINE);
		sub.setTopLineStyle(BorderProperties.NO_LINE);
		sub.setRightLineStyle(BorderProperties.NO_LINE);
		Engine eng = sub.getEngine();
		eng.getArea("RH").setHeight(0);
		eng.getArea("RF").setHeight(0);
		eng.getArea("RH").setSuppress(true);
		eng.getArea("RF").setSuppress(true);

		paginaInformazioni(details, cpi, dataStipula, datScadConferma, responsabileCPI);
	}

	/**
	 * Intestazione testata del cpi con logo. A causa della cornice non e' possibile utilizzare piu' sezioni, una per
	 * ogni riga (come nel resto del report) Utilizzato ora per la Calabria
	 */
	private void testataCPI(Area area, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		String cpi = (String) row.getAttribute("CPI");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		String intestazione = (String) row.getAttribute("STRINTESTAZIONESTAMPA");
		//
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));

		sec.addBox(StyleUtils.toTwips(80), 0, StyleUtils.toTwips(1250), StyleUtils.toTwips(205));

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
		codTipoPattoMisura = row.containsAttribute("codtipopatto") ? row.getAttribute("codtipopatto").toString() : "";
		boolean isPattoGG = false;

		if (this.programmiAperti != null && !this.programmiAperti.isEmpty()
				&& PattoBean.checkMisureProgramma(this.programmiAperti, misureGG)) {
			isPattoGG = true;
		}

		if (Arrays.asList(misureGG).contains(codTipoPattoMisura) || isPattoGG) {
			sec.addPicture(StyleUtils.toTwips(1155), StyleUtils.toTwips(26), 900, 836,
					this.installAppPath + "/WEB-INF/report/img/logo_internal.png");
		}

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
		sec.addBox(StyleUtils.toTwips(1328), 0, StyleUtils.toTwips(550), StyleUtils.toTwips(205));
		text = sec.addText(StyleUtils.toTwips(1500), StyleUtils.toTwips(50), StyleUtils.toTwips(350),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(intestazione).toUpperCase());
		tp.setFontSize(11);
		tp.setFontName("Arial");
		tp.setBold(true);
		text.setCanGrow(true);
		//
		sec.addPicture(StyleUtils.toTwips(1345), StyleUtils.toTwips(26), 795, 870,
				this.installAppPath + "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		int contatoreImmagini = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				if (Arrays.asList(misureGG).contains(codTipoPattoMisura) || isPattoGG) {
					if (contatoreImmagini > 0) {
						((Element) elements.get(i)).setSuppress(true);
					}
				} else {
					((Element) elements.get(i)).setSuppress(true);
				}
				contatoreImmagini = contatoreImmagini + 1;
			}
		}
	}

	private void addBR(Area details, int height) throws Exception {
		Section section = details.addSection();
		section.setHeight(StyleUtils.toTwips(height));
	}

	/**
	 * come stile viene usato 'CAMPO' ma con fontSize = 12 (anzicche' 11 dello stile CAMPO)
	 */
	private void titoloPuntato(Area details, String titolo, Style s) throws Exception {
		Section section = details.addSection();
		section.addPicture(StyleUtils.toTwips(LEFT + 50), StyleUtils.toTwips(19), StyleUtils.toTwips(17),
				StyleUtils.toTwips(17), this.installAppPath + "/WEB-INF/report/img/punto_carta.gif");
		ReportUtils.addText(section, titolo, s, LEFT_TITOLO_PUNTO_T);
	}

	private void paginaInformazioni(Area details, String cpi, String dataStipula, String dataScadenza,
			String responsabileCPI) throws Exception {

		boolean isPattoOnline = this.flgPattoOnline != null && this.flgPattoOnline.equalsIgnoreCase(Properties.FLAG_S);

		addBR(details, 50);
		Section section = details.addSection();
		Text text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart(cpi + ", li " + dataStipula);
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");

		addBR(details, 30);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Data stampa " + DateUtils.getNow());
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");

		addBR(details, 60);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Per il CPI");
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");

		addBR(details, 20);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Firma dell'operatore del CPI");

		tp.setFontSize(11);
		tp.setFontName("Times New Roman");
		text = section.addText(StyleUtils.toTwips(1480), StyleUtils.toTwips(5), StyleUtils.toTwips(500),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Firma del lavoratore");
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");

		if (isPattoOnline) {
			String nomeUt = "";
			String cognomeUt = "";
			String nomeLav = "";
			String cognomeLav = "";

			if (this.operatore != null) {
				nomeUt = Utils.notNull(this.operatore.getAttribute("ROW.STRNOMEUTENTE"));
				cognomeUt = Utils.notNull(this.operatore.getAttribute("ROW.STRCOGNOMEUTENTE"));
			}

			if (this.infoGenerali != null) {
				nomeLav = Utils.notNull(this.infoGenerali.getAttribute("NOMELAVORATORE"));
				cognomeLav = Utils.notNull(this.infoGenerali.getAttribute("COGNOMELAVORATORE"));
			}

			addBR(details, 20);

			section = details.addSection();
			StringBuilder datiFirmaElettronica = new StringBuilder("sottoscritto con firma elettronica");
			datiFirmaElettronica
					.append("                                                                             ");
			datiFirmaElettronica.append("sottoscritto con firma elettronica");
			datiFirmaElettronica.append("\nda ").append(nomeUt).append(" ").append(cognomeUt);
			String nomeCognomeLav = "da " + nomeLav + " " + cognomeLav;
			int spaziTotali = SPAZI_TRA_OPERATORE_LAV;

			if (nomeCognomeLav.length() > ACCETTAZIONELENMAXLAV) {
				spaziTotali = SPAZI_TRA_OPERATORE_LAV_MIN;
				spaziTotali = spaziTotali - ((nomeCognomeLav.length() - ACCETTAZIONELENMAXLAV) * 3);
			} else {
				spaziTotali = spaziTotali + (ACCETTAZIONELENMAXLAV - nomeCognomeLav.length());
			}

			for (int ispazi = 0; ispazi < spaziTotali; ispazi++) {
				datiFirmaElettronica.append(" ");
			}

			datiFirmaElettronica.append(nomeCognomeLav);

			Text t = ReportUtils.addText(section, datiFirmaElettronica.toString(), etichetta, LEFT);
		}

		// if (!this.regione.equals(CAL)) {
		// addBR(details, 20);
		// section = details.addSection();
		// text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
		// StyleUtils.toTwips(80));
		// p = text.addParagraph();
		// tp = p.addTextPart(responsabileCPI);
		// tp.setFontSize(11);
		// tp.setFontName("Times New Roman");
		// }

		addBR(details, 50);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1700),
				StyleUtils.toTwips(700));
		p = text.addParagraph();

		if (!this.regione.equals(Properties.CAL)) {
			/*
			 * if (dataScadenza != null) { tp = p.addTextPart("Il presente patto è valido sino alla data: ");
			 * tp.setFontSize(11); tp.setFontName("Times New Roman");
			 * 
			 * tp = p.addTextPart(dataScadenza); tp.setFontSize(11); tp.setFontName("Times New Roman");
			 * tp.setBold(true);
			 * 
			 * tp = p.addTextPart(". "); tp.setFontSize(11); tp.setFontName("Times New Roman");
			 * 
			 * tp = p.addTextPart("Il lavoratore può richiederne il rinnovo entro e non oltre la data di validità. ");
			 * tp.setFontSize(11); tp.setFontName("Times New Roman");
			 * 
			 * tp = p.
			 * addTextPart("\n\nSe la data di fine validità coincide con un giorno festivo o di non apertura del Centro il patto resta valido sino al primo giorno di apertura del Centro."
			 * ); tp.setFontSize(11); tp.setFontName("Times New Roman"); }
			 */
			tp = p.addTextPart(
					"\n\nIl dettaglio completo delle attività previste dalle prestazioni indicate è riportato in allegato e costituisce parte integrante del presente patto.");
			tp.setFontSize(11);
			tp.setFontName("Times New Roman");
		}
		if (StringUtils.isFilledNoBlank(privacy) && privacy.equalsIgnoreCase("1")) {
			tp = p.addTextPart(
					"\n\n\nè stata inoltre ricevuta/presa visione dell'informativa prevista dall'art 13 del Regolamento europeo n. 679/2016.");
		} else {
			tp = p.addTextPart(
					"\n\n\nE' stata inoltre presa visione dell'informativa relativa al trattamento dei dati personali. (Rif. legge 196/03).");
		}
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			ApiPatto p = new ApiPatto(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}
}