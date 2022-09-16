/*
 * Creato il 25-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Area;
import com.inet.report.BorderProperties;
import com.inet.report.Engine;
import com.inet.report.Field;
import com.inet.report.FieldPart;
import com.inet.report.Paragraph;
import com.inet.report.PromptField;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Subreport;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
import it.eng.afExt.utils.DateUtils;
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.module.patto.MansioniRow;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.Controlli;

public class ApiDiagnosiFunzionale {
	Style campo;
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
	private SourceBean statoOccupazionale = null;
	private Vector ambitoProfessionale = null;
	private SourceBean cat181 = null;
	private Vector appuntamenti = null;
	private Vector azioniConcordate = null;
	private SourceBean infoGenerali = null;
	private Vector impegni = null;
	private Vector laurea;
	private Vector movimenti;
	private String docInOut;
	//

	private String numProtocolloStr;
	private String numAnnoProt;
	private String datProtocollazione;
	private String riferimentoDoc;
	//
	private Engine eng = null;
	private String installAppPath = null;

	private String fileType = Engine.EXPORT_PDF;

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

	public void setLaurea(Vector laurea) {
		this.laurea = laurea;
	}

	public void setMovimenti(Vector movimenti) {
		this.movimenti = movimenti;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public void setNumProtocolloStr(String s) {
		this.numProtocolloStr = s;
	}

	public void setNumAnnoProt(String s) {
		this.numAnnoProt = s;
	}

	public void setDatProtocollazione(String s) {
		this.datProtocollazione = s;
	}

	public void setDocInOut(String s) {
		this.docInOut = s;
	}

	/** Creates a new instance of Patto */
	public ApiDiagnosiFunzionale() {
		styleFactory = new StyleFactory();
		campo = styleFactory.getStyle(StyleFactory.CAMPO);
		titoloSezione = styleFactory.getStyle(StyleFactory.TITOLO_SEZIONE);
		etichetta = styleFactory.getStyle(StyleFactory.ETICHETTA);
		etichetta2 = styleFactory.getStyle(StyleFactory.ETICHETTA2);
		titolo2 = styleFactory.getStyle(StyleFactory.TITOLO2);
		titolo3 = styleFactory.getStyle(StyleFactory.TITOLO3);
		titolo2Centrato = styleFactory.getStyle(StyleFactory.TITOLO2CENTRATO);

	}

	public ApiDiagnosiFunzionale(String[] a) throws Exception {
		start();
	}

	public void start() throws Exception {
		try {
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("Patto");
			eng.setLocale(new Locale("IT", "it"));
			ReportUtils.initReport(eng);
			/*
			 * Fields fields = eng.getFields(); PromptField pField =
			 * fields.addPromptField("numProt","numero di protocollo", PromptField.STRING);
			 */
			// inserisco il parametro del numero di protocollo in modo che se si
			// verifica che il numero di protocollo
			// reale differisce da quello visualizzato nel form di stampa, possa
			// essere sostituito appunto da quello reale.
			// Questa operazione avviene in modo trasparente nella fase di
			// protocollazione nella classe Documento.
			PromptField pField = ReportUtils.addPromptField("numProt", PromptField.STRING, "numero di protocollo", eng);
			// si imposta il valore del parametro col numero di protocollo
			// spedito dal form di stampa
			eng.setPrompt("numProt", this.numProtocolloStr);
			//
			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toTwips(50));
			Area details = eng.getArea("D");
			// -
			testata(details);
			addBR(details, 80);

			// protocollazione(details, pField);
			// addBR(details, 50);

			// titoloMinore(details);
			// addBR(details, 50);

			dichiarazioneLavoratore(details, statoOccupazionale);
			addBR(details, 50);
			// -
			// statoOccupazionale(details);//, statoOccupazionale, infoGenerali,
			// cat181, laurea, movimenti);
			// addBR(details, 50);

			// misure(details, infoGenerali, azioniConcordate);
			// addBR(details, 80);

			// ambitoProfessionale(details, ambitoProfessionale, infoGenerali);
			// addBR(details, 50);
			// -
			// titoloDisponibilita(details);
			// addBR(details, 50);

			// azioniConcordate(details, azioniConcordate);
			// -
			addBR(details, 50);
			appuntamenti(details, appuntamenti);
			addBR(details, 100);
			// impegni(details, impegni);
			// -
			// informazioniUtente(details, infoGenerali);
			// fine del report
			// Section section = details.addSection();
			//
			// generazione del sorgente xml per eventuale debugging col desiger
			//
			// System.out.println("costruzione report terminata. Salvataggio su
			// file");
			// RDC.saveEngine(new
			// File("C:/Progetti/SIL/sviluppo/applicazioni/sil/web/WEB-INF/report/pattoTEST.rpt"),
			// eng);
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
	private void protocollazione(Area details, Field numProt) throws Exception {
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "Anno ", etichetta, LEFT, 500);
		ReportUtils.addTextPart(t.getParagraph(0), this.numAnnoProt, campo);
		t = ReportUtils.addText(section, "Num ", etichetta, 420);
		// ReportUtils.addTextPart(t.getParagraph(0), this.numProtocolloStr,
		// campo);
		// inserisco il promptField nel campo di visualizzazione del numero di
		// protocollo
		// per questa operazione non esiste una utilità
		FieldPart tp = t.getParagraph(0).addFieldPart(numProt);
		tp.setX(0);
		campo.setStyle(tp);
		t = ReportUtils.addText(section, "Data di prot. ", etichetta, 920);
		ReportUtils.addTextPart(t.getParagraph(0), this.datProtocollazione, campo);
		section = details.addSection();
		t = ReportUtils.addText(section, "Doc. di ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), this.docInOut, campo);
		t = ReportUtils.addText(section, "Rif. ", etichetta, 420);
		ReportUtils.addTextPart(t.getParagraph(0), this.riferimentoDoc, campo);
	}

	/**
	 * Sezione misure: misura del patto + de_razione_ragg
	 */
	private void misure(Area details, SourceBean infoGenerali, Vector azioni) throws Exception {
		Section section = details.addSection();
		String deTipoPatto = (String) infoGenerali.getAttribute("deTipoPattoMisura");
		Text t = ReportUtils.addText(section, "", etichetta, LEFT);
		Paragraph p = t.getParagraph(0);
		ReportUtils.addTextPart(p, deTipoPatto.trim(), campo);
		ReportUtils.addTextPart(t.getParagraph(0), " con l'obiettivo di:", etichetta);
		addBR(details, 03);
		Set misure = new HashSet(azioni.size());
		for (int i = 0; i < azioni.size(); i++)
			misure.add(((SourceBean) azioni.get(i)).getAttribute("azione_ragg"));
		Iterator misureIter = misure.iterator();
		for (; misureIter.hasNext();) {
			String misura = (String) misureIter.next();
			titoloPuntato(details, misura, campo);
		}
	}

	/**
	 * parte fissa
	 */
	private void titoloDisponibilita(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Il dettaglio delle disponibilità  dell'utente sono riportate in allegato e costituiscono"
				+ " parte integrante del presente patto.";
		ReportUtils.addText(section, s, etichetta, LEFT);
	}

	/**
	 * Vengono stampate le mansioni in un elenco puntato. Se non sono state associate mansioni al patto viene stampato
	 * il campo note della sezione ambito professionale del patto, sempre che queste note siano presenti.
	 */
	private void ambitoProfessionale(Area details, Vector rows, SourceBean patto) throws Exception {
		Section section = details.addSection();
		Style s = (Style) etichetta.clone();
		s.setFontSize(12);
		if (rows.size() > 0) {
			ReportUtils.addText(section, "nella mansione di:", s, LEFT);
			addBR(details, 50);
			Style stp = (Style) campo.clone();
			stp.setFontSize(12);
			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);
				String mansione = (String) row.getAttribute("mansione");
				titoloPuntato(details, mansione, stp);
			}
		} else {
			Text t = ReportUtils.addText(section, "ambito professionale di:", s, LEFT);
			String noteAmbitoProfessionale = (String) patto.getAttribute("strNoteAmbitoProf");
			if (noteAmbitoProfessionale != null) {
				s = (Style) campo.clone();
				s.setCanGrow(true);
				s.setWidth(StyleUtils.toTwips(1200));
				ReportUtils.addText(section, noteAmbitoProfessionale, s, LEFT + 450);
			}

		}
	}

	private void titoloMinore(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Patto di adesione alle misure concordate con il Centro per l'Impiego per l'uscita dallo stato"
				+ " di disoccupazione ai sensi del D.Lgs 181/00 come modificato dal D.Lgs 150/15.";
		ReportUtils.addText(section, s, etichetta, LEFT);
	}

	/**
	 * sezione impegni lavoratore ed impegni cpi
	 */
	private void impegni(Area details, Vector impegni) throws Exception {

		Style s = (Style) etichetta.clone();
		s.setFontSize(12);
		Section section = details.addSection();
		ReportUtils.addText(section, "In rispetto del presente Patto il lavoratore si impegna a:", s, LEFT);
		addBR(details, 50);
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
		addBR(details, 100);
		section = details.addSection();
		ReportUtils.addText(section, "Il CPI si impegna a:", s, LEFT);
		addBR(details, 50);
		impegniCPI(details, impegni);
	}

	/**
	 * Gli impegni del lavoratore vengono stampati in un elenco puntato
	 */
	private void impegniLavoratore(Area details, Vector impegniLavoratore) throws Exception {
		if (impegniLavoratore == null)
			return;
		Style s = (Style) campo.clone();
		s.setFontSize(12);
		for (int i = 0; i < impegniLavoratore.size(); i++) {
			SourceBean row = (SourceBean) impegniLavoratore.get(i);
			titoloPuntato(details, (String) row.getAttribute("strDescrizione"), s);
			// impegni(details, row);
		}
	}

	/**
	 * Gli impegni del cpi vengono stampati in un elenco puntato
	 */
	private void impegniCPI(Area details, Vector impegniCpi) throws Exception {
		if (impegniCpi == null)
			return;
		Style s = (Style) campo.clone();
		s.setFontSize(12);
		for (int i = 0; i < impegniCpi.size(); i++) {
			SourceBean row = (SourceBean) impegniCpi.get(i);
			titoloPuntato(details, (String) row.getAttribute("strDescrizione"), s);
			// impegni(details, row);
		}
	}

	/**
	 * Sezione appuntamenti
	 */
	private void appuntamenti(Area details, Vector rows) throws Exception {
		addBR(details, 30);
		if (rows == null)
			return;
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			appuntamento(details, row);
		}
	}

	/**
	 * Stampa del dettaglio di un appuntamento
	 */
	private void appuntamento(Area details, SourceBean appuntamenti) throws Exception {
		if (appuntamenti == null)
			appuntamenti = new SourceBean("");
		String servizio = (String) appuntamenti.getAttribute("CODGIUDIZIO");
		String data = (String) appuntamenti.getAttribute("STRDESCRIZIONEPERSTAMPA");
		//
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "mediante presentazione al servizio di: ", etichetta, LEFT);
		Paragraph p = t.getParagraph(0);
		ReportUtils.addTextPart(p, servizio, campo);
		section = details.addSection();
		t = ReportUtils.addText(section, "Da svolgersi entro il: ", etichetta, LEFT);
		p = t.getParagraph(0);
		ReportUtils.addTextPart(p, data, campo);
	}

	/**
	 * Sezione azioni (percorsi) concordatie: vengono raggruppate per data e in elenchi con trattino (quindi non
	 * puntato)
	 */
	private void azioniConcordate(Area details, Vector rows) throws Exception {
		Section section = details.addSection();
		String dataPercorso = null;
		ReportUtils.addText(section, "mediante l'azione di:", etichetta, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			// stampo tutte le azioni raggruppate per la stessa data
			// quindi solo quando cambia la data questa verra' stampata
			String dataPercTmp = (String) row.getAttribute("dataPercorso");
			if (dataPercorso != null && !dataPercorso.equals(dataPercTmp)) {
				dataAzioniConcordate(details, dataPercorso);
			}
			azioneConcordata(details, row);
			dataPercorso = dataPercTmp;
		}
		// uscendo dal ciclo potrebbe esserci ancora la data dell'ultimo blocco
		// da stampare
		if (dataPercorso != null)
			dataAzioniConcordate(details, dataPercorso);
	}

	/**
	 * Dettaglio azione concordata
	 */
	private void azioneConcordata(Area details, SourceBean azioniConcordate) throws Exception {
		if (azioniConcordate == null)
			azioniConcordate = new SourceBean("");
		String azione = (String) azioniConcordate.getAttribute("DESCRIZIONE");
		Section section = details.addSection();
		ReportUtils.addText(section, "- " + azione, campo, LEFT);

		//
	}

	/**
	 * Dettaglio data azione concordata
	 */
	private void dataAzioniConcordate(Area details, String dataAzione) throws Exception {
		addBR(details, 20);
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "da svolgersi entro il: ", etichetta, LEFT);
		Paragraph p = t.getParagraph(0);
		ReportUtils.addTextPart(p, dataAzione, campo);
		addBR(details, 50);
	}

	/**
	 * LE INFORMAZIONI UTENTE VENGONO INSERITE NEL REPORT UTILIZZANDO UN SUBREPORT LETTO DA DISCO, ovvero un subreport
	 * creato col designer di Crystal Clear al quale vengono passati 3 parametri.
	 */
	private void informazioniUtente(Area details, SourceBean infoGenerali) throws Exception {
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");
		String cpi = (String) infoGenerali.getAttribute("CPI");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dataStipula = (String) infoGenerali.getAttribute("datStipula");
		String datScadConferma = (String) infoGenerali.getAttribute("datScadConferma");
		Section section = details.addSection();
		section.setNewPageBefore(true);
		// aggiunta del subreport da disco
		Subreport sub = section.addSubreport(0, 0, StyleUtils.toTwips(1950), StyleUtils.toTwips(56),
				"file:" + this.installAppPath + "/WEB-INF/report/patto/_informazioniUtente_new_CC.rpt");
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
		// passo al subreport 3 parametri con la tecnica del set('nome', valore)
		// e non come di solito accade con un vettore
		// Il 'nome' deve essere esattamente quello visualizzato dal designer.
		eng.setPrompt("cpi", cpi);
		eng.setPrompt("dataStipula", dataStipula);
		eng.setPrompt("dataScadenza", datScadConferma);
		//
	}

	/**
	 * dati anagrafici del lavoratore e data dichiarazione immediata disponibilita'
	 */
	private void dichiarazioneLavoratore(Area details, SourceBean infoLavoratore) throws Exception {
		// le informazioni del lavoratore dovrebbero sempre esserci .....
		if (infoLavoratore == null)
			infoLavoratore = new SourceBean("");

		Section section = details.addSection();
		String nome = Utils.notNull(infoLavoratore.getAttribute("STRNOME"));
		String cognome = Utils.notNull(infoLavoratore.getAttribute("STRCOGNOME"));
		String codiceFiscale = (String) infoLavoratore.getAttribute("STRCODICEFISCALE");
		String dataNasc = (String) infoLavoratore.getAttribute("DATNASC");
		String comNasc = (String) infoLavoratore.getAttribute("STRDENOMINAZIONE");
		String datDiagnosi = (String) infoLavoratore.getAttribute("DATDIAGNOSI");
		String datRevDiagnosi = (String) infoLavoratore.getAttribute("DATARRIVOREVDIAGNOSI");
		String datRichRevDiagnosi = (String) infoLavoratore.getAttribute("DATRICHREVDIAGNOSI");
		String flgRevDiagnosiPerv = "";
		if (infoLavoratore.getAttribute("FLGREVDIAGNOSIPERVENUTA") != null)
			flgRevDiagnosiPerv = (String) infoLavoratore.getAttribute("FLGREVDIAGNOSIPERVENUTA");
		else
			flgRevDiagnosiPerv = "";

		Text nt = ReportUtils.addText(section, "Nome e cognome: ", etichetta, LEFT);
		ReportUtils.addTextPart(nt.getParagraph(0), nome + " " + cognome, campo);

		section = details.addSection();
		Text t = ReportUtils.addText(section, "Comune di nascita: ", etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), comNasc, campo);
		ReportUtils.addTextPart(t.getParagraph(0), ", data di nascita: ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(0), dataNasc, campo);

		t = ReportUtils.addText(section, "Cod. fisc. ", etichetta, 1350);
		ReportUtils.addTextPart(t.getParagraph(0), codiceFiscale, campo);

		section = details.addSection();

		section = details.addSection();
		Text tt = ReportUtils.addText(section, "data richiesta diagnosi da parte dell'Ufficio:  ", etichetta, LEFT);
		ReportUtils.addTextPart(tt.getParagraph(0), datRichRevDiagnosi, campo);

		section = details.addSection();
		Text df = ReportUtils.addText(section, "diagnosi funzionale pervenuta:  ", etichetta, LEFT);
		if (flgRevDiagnosiPerv.equals("S"))
			ReportUtils.addTextPart(df.getParagraph(0), "Sì", campo);
		else if (flgRevDiagnosiPerv.equals("N"))
			ReportUtils.addTextPart(df.getParagraph(0), "No", campo);
		else
			ReportUtils.addTextPart(df.getParagraph(0), "dato non presente", campo);
	}

	/**
	 * Sezione relativa allo stato occupazionale.
	 * 
	 * ******************************************************************************* ATTENZIONE: LE INFORMAZIONI
	 * VENGONO CALCOLATE COME AVVIENE NELLA RELATIVA JSP.
	 * ******************************************************************************* Nel caso in cui nella jsp di
	 * dettaglio dello stato occupazionale cambi la modalita' di calcolo di questi informazioni, bisognera' riportare
	 * queste modifiche anche in questo metodo.
	 */
	private void statoOccupazionale(Area details) throws Exception {

		// parti fisse da stampare
		String dich1 = "che ha reso dichiarazione di immediata disponibilità allo svolgimento di attività lavorativa in data ";
		String dich2 = " e che si trova nella condizione di ";
		// i mesi di anzianita' non vanno piu' mostrati (31/01/2005)
		// String dich3 = ", con mesi di anzianità di disoccupazione ";
		String dich4 = ", concordano per l'uscita dallo stato di disoccupazione:";
		// calcolo delle informazioni da stampare
		int par = 0;
		String tipoDisoccupatoLungaDurata = "";
		// String cdnLavoratore =
		// ((BigDecimal)infoGenerali.getAttribute("cdnLavoratore")).toString();
		String dataDichiarazioneImmDisp = (String) statoOccupazionale.getAttribute("DATDICHIARAZIONE");
		String statoOccupaz = (String) statoOccupazionale.getAttribute("FLGREVACCERTAMENTO");
		String mesiAnzianita = (String) statoOccupazionale.getAttribute("DATREVISIONEACCERT");
		// if (mesiAnzianita == null)
		// mesiAnzianita = new BigDecimal("0");
		// String cat181Desc =
		// (String)statoOccupazionale.getAttribute("DESCRIZIONE181");
		String codStatoOccRagg = (String) statoOccupazionale.getAttribute("DATDIAGNOSI");
		String mesiAnzPrec = (String) statoOccupazionale.getAttribute("FLGINVALIDFISICA");
		/*
		 * String mesiAnzPrecInt = null; BigDecimal mesiAnz = null; String mesiAnzInt = null; BigDecimal numMesiSosp =
		 * null; String numMesiSospInt = null; numMesiSosp = (BigDecimal)statoOccupazionale.getAttribute("NUMMESISOSP");
		 * mesiAnz = (BigDecimal)statoOccupazionale.getAttribute("MESI_ANZ"); BigDecimal numMesiSospPrec = null; String
		 * numMesiSospPrecInt = null; numMesiSospPrec = (BigDecimal)statoOccupazionale.getAttribute("NUMMESISOSPPREC");
		 */
		/*
		 * FLGREVACCERTAMENTO, DATREVISIONEACCERT, DATDIAGNOSI, FLGINVALIDFISICA, FLGINVALIDPSICHICA,
		 * FLGINVALIDSENSORIALE, FLGINVALIDINTELLETTIVA, CODGIUDIZIO, DATRICONOSCIMENTO, FLGPERMANENZASTATO,
		 * DATRICHREVDIAGNOSI, FLGREVDIAGNOSIPERVENUTA, DATARRIVOREVDIAGNOSI, STRCOGNOME, STRNOME, DATNASC,
		 * STRDENOMINAZIONE, STRDESCRIZIONE
		 */

		/*
		 * if (mesiAnz != null) mesiAnzInt = String.valueOf(mesiAnz.intValue()); else mesiAnzInt = "0"; if (mesiAnzPrec
		 * != null) { mesiAnzPrecInt = String.valueOf(mesiAnzPrec.intValue()); } else mesiAnzPrecInt = "0"; if
		 * (numMesiSospPrec != null) { numMesiSospPrecInt = String.valueOf(numMesiSospPrec.intValue()); } else
		 * numMesiSospPrecInt = "0"; if (numMesiSosp != null) numMesiSospInt = String.valueOf(numMesiSosp.intValue());
		 * else numMesiSospInt = "0"; if (numMesiSospInt != null && Integer.parseInt(numMesiSospInt) < 0) numMesiSospInt
		 * = "0"; //null; if (numMesiSospPrecInt != null && Integer.parseInt(numMesiSospPrecInt) < 0) numMesiSospPrecInt
		 * = "0"; if (mesiAnzInt != null && Integer.parseInt(mesiAnzInt) < 0) mesiAnzInt = "0"; //null; if
		 * (mesiAnzPrecInt != null && Integer.parseInt(mesiAnzPrecInt) < 0) mesiAnzPrecInt = "0";
		 */
		BigDecimal eta = new BigDecimal("-1");
		String flgObbSco = null;
		String flgLaurea = "";
		String annoNascita = null;

		if (this.cat181 != null && this.cat181.getAttribute("ANNI") != null) {
			eta = (BigDecimal) this.cat181.getAttribute("ANNI");
			flgObbSco = (String) this.cat181.getAttribute("FLGOBBLIGOSCOLASTICO");
			annoNascita = (String) this.cat181.getAttribute("datNasc");
		}

		flgLaurea = laurea != null && !laurea.isEmpty() ? "S" : "N";
		String cat181Desc = Controlli.getCat181(annoNascita, DateUtils.getNow(), flgObbSco, flgLaurea);
		if (cat181Desc == null)
			cat181Desc = "";
		String codCat181 = null;
		String dataFineMovimento = null;
		BigDecimal mesiInattivita = null;

		// it.eng.sil.module.movimenti.InfoLavoratore _lav =
		// new it.eng.sil.module.movimenti.InfoLavoratore(new
		// BigDecimal(cdnLavoratore));
		if (cat181 != null && cat181Desc.equalsIgnoreCase("GIOVANE"))
			codCat181 = "G";
		if (movimenti != null && movimenti.size() > 0) {
			SourceBean row = (SourceBean) movimenti.get(0);
			dataFineMovimento = (String) row.getAttribute("DATAFINEMOVIMENTO");
			mesiInattivita = (BigDecimal) row.getAttribute("mesiInattivita");
		}
		// Numero effettivo mesi di anzianità
		/*
		 * BigDecimal totMesiAnz = new BigDecimal( String.valueOf( Integer.parseInt(mesiAnzPrecInt) +
		 * Integer.parseInt(mesiAnzInt) - (Integer.parseInt(numMesiSospPrecInt) + Integer.parseInt(numMesiSospInt))));
		 */
		// tipoDisoccupatoLungaDurata =
		// Controlli.disoccInoccLungaDurata(codStatoOccRagg, totMesiAnz,
		// codCat181); //mesiAnz
		// String sesso = _lav.getSesso();
		// boolean donnaInReinserimento =
		// Controlli.donnaInInserimentoLavorativo(codStatoOccRagg,
		// mesiInattivita, sesso);
		// *********** stampa informazioni *********
		Section section = details.addSection();
		// did
		Text t = ReportUtils.addText(section, dich1, etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(par), dataDichiarazioneImmDisp, campo);
		// stato occupazionale
		ReportUtils.addTextPart(t.getParagraph(par), dich2, etichetta);
		ReportUtils.addTextPart(t.getParagraph(par), statoOccupaz, campo);
		// mesi anzianita
		/*
		 * ReportUtils.addTextPart(t.getParagraph(par), dich3, etichetta); ReportUtils.addTextPart(t.getParagraph(par),
		 * totMesiAnz.toString(), campo);
		 */
		// eventuale appartenenza alla categoria 181 (giovane/non giovane)
		if (cat181Desc != null && !cat181Desc.equals("")) {
			ReportUtils.addTextPart(t.getParagraph(par), ", ", etichetta);
			ReportUtils.addTextPart(t.getParagraph(par), cat181Desc, campo);
		}
		// disoccupato di lunga durata
		if (tipoDisoccupatoLungaDurata != null && !tipoDisoccupatoLungaDurata.equals("")) {
			ReportUtils.addTextPart(t.getParagraph(par), ", ", etichetta);
			ReportUtils.addTextPart(t.getParagraph(par), tipoDisoccupatoLungaDurata, campo);
		}
		// donna in reinserimento lavorativo
		// if (donnaInReinserimento) {
		ReportUtils.addTextPart(t.getParagraph(par), ", ", etichetta);
		ReportUtils.addTextPart(t.getParagraph(par), "donna in reinserimento lavorativo", campo);
		// }
		ReportUtils.addTextPart(t.getParagraph(par), dich4, etichetta);

	}

	/**
	 * Intestazione testata del cpi con logo. A causa della cornice non e' possibile utilizzare piu' sezioni, una per
	 * ogni riga (come nel resto del report)
	 */
	private void testata(Area area) throws Exception {

		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));

		// Box box = sec.addBox(StyleUtils.toTwips(80), 0,
		// StyleUtils.toTwips(1370), StyleUtils.toTwips(205));

		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				LEFT_TITOLO_PUNTO_T);
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("DIAGNOSI FUNZIONALE DELLA PERSONA DISABILE");
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(false);
		//
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95), StyleUtils.toTwips(1250),
				LEFT_TITOLO_PUNTO_T);
		p = text.addParagraph();
		tp = p.addTextPart("(L. 12 marzo 1999, n. 68 - art. 1, co.4; D.P.C.M. 13 gennaio 2000)");
		Style s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
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

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			ApiDiagnosiFunzionale p = new ApiDiagnosiFunzionale(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}
}
