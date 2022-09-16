/*
 * Patto.java
 *
 * Created on 25 gennaio 2004, 23.38
 * 
 */
package it.eng.sil.action.report.patto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Area;
import com.inet.report.BorderProperties;
import com.inet.report.Box;
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
public class ApiAccordoGenerico {
	Style campo;
	Style titoloSezione;
	Style etichetta;
	Style etichetta2;
	Style titolo2;
	Style titolo3;
	Style titolo2Centrato;
	StyleFactory styleFactory;
	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di
	 * millimetro)
	 */
	private int LEFT = 80;
	private int LEFT_TITOLO_PUNTO = LEFT;
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
	private SourceBean infoGenerali = null;
	private SourceBean operatore = null;
	private Vector impegni = null;
	private Vector laurea;
	private Vector movimenti;
	private String docInOut;
	private String regione;
	private Vector<String>programmiAperti;
	//

	private String datProtocollazione;
	private String codMonoIO;
	private String riferimentoDoc;
	//
	private Engine eng = null;
	private String installAppPath = null;

	private String fileType = Engine.EXPORT_PDF;
	
	private String privacy;
	
	private String flgPattoOnline;
	
	private Vector soggettiAccreditati = null;
	private TransactionQueryExecutor txExec = null;
	
	public void setSoggettiAccreditati(Vector soggettiAccreditati) {
		this.soggettiAccreditati = soggettiAccreditati;
	}
	
	public void setTransaction(TransactionQueryExecutor txExecCurr) {
		this.txExec = txExecCurr;
	}
	
	private static final String RER = "8";

	public String getFileType() {
		return this.fileType;
	}

	public void setInstallAppPath(String installAppPath) {
		this.installAppPath = installAppPath;
	}

	/**
	 * Viene impostato il tipo di report da generare (il tipo nostro viene
	 * mappato col tipo interno di crystal clear)
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
	
	public void setOperatore(SourceBean operatore) {
		this.operatore = operatore;
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
	public ApiAccordoGenerico() {
		styleFactory = new StyleFactory();
		campo = styleFactory.getStyle(StyleFactory.CAMPO);
		titoloSezione = styleFactory.getStyle(StyleFactory.TITOLO_SEZIONE);
		etichetta = styleFactory.getStyle(StyleFactory.ETICHETTA);
		etichetta2 = styleFactory.getStyle(StyleFactory.ETICHETTA2);
		titolo2 = styleFactory.getStyle(StyleFactory.TITOLO2);
		titolo3 = styleFactory.getStyle(StyleFactory.TITOLO3);
		titolo2Centrato = styleFactory.getStyle(StyleFactory.TITOLO2CENTRATO);

	}

	public ApiAccordoGenerico(String[] a) throws Exception {
		start();
	}
	
	public void inizializza(SourceBean infoGen,SourceBean statoOcc,Vector appuntaments,SourceBean operatore,
							Vector azioniConcordats,Vector ambitoProfs,Vector impegniV, 
							String installAppPath,String tipoFile, SourceBean cat181,Vector titoloStudio,Vector mov,
							String ambito, String strParam, String docInOut, String regione, 
							SourceBean documentoIdentita, String privacy, Vector soggetti, TransactionQueryExecutor txExec) throws Exception {
		try {
			
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
				this.flgPattoOnline = this.infoGenerali.containsAttribute("FLGPATTOONLINE")?
						this.infoGenerali.getAttribute("FLGPATTOONLINE").toString():"";
				BigDecimal prgPattoLav = (BigDecimal)this.infoGenerali.getAttribute("prgpattolavoratore");
				this.programmiAperti = PattoBean.checkProgrammi(prgPattoLav, this.txExec);
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
			// -
			protocollazione(details, pNumProt, pAnnoProt, pDataProt);
			addBR(details, 20);
			
			if (this.regione.equalsIgnoreCase(Properties.VDA)) {
				testataCPIVDA(details, infoGenerali);
			}
			else {
				testataCPI(details, infoGenerali);
			}
			
			addBR(details, 20);
			
			titoloStampa(details);
			addBR(details, 20);
			
			dichiarazioneLavoratore(details, infoGenerali);
			addBR(details, 20);
			// -
			
			misure(details, infoGenerali, azioniConcordate);
			// -
			// -
			//addBR(details, 40);
			//ambitoProfessionale(details, ambitoProfessionale, infoGenerali);
			// -
			addBR(details, 20);
			// -
			azioniConcordate(details, azioniConcordate);
			// -
			
			//if (appuntamenti != null && appuntamenti.size() > 0) {
			//	addBR(details, 20);
			//	appuntamenti(details, appuntamenti);
			//}
			
			String indiceSvantaggio = infoGenerali.getAttribute("numindicesvantaggio")==null ? null : ((BigDecimal)infoGenerali.getAttribute("numindicesvantaggio")).toString();
			String indiceSvantaggio2 = infoGenerali.getAttribute("numindicesvantaggio2")==null ? null : ((BigDecimal)infoGenerali.getAttribute("numindicesvantaggio2")).toString();
			String dataStipula = (String) infoGenerali.getAttribute("datStipula");
			
			String dataRiferimento = (String) infoGenerali.getAttribute("datriferimento");
			if (dataRiferimento != null && !dataRiferimento.equals(""))  {
				profilingDati(details, indiceSvantaggio,indiceSvantaggio2, dataRiferimento, dataStipula);
				addBR(details, 20);
			}
			
			// -
			impegni(details, impegni);
			// -
			
			informazioniUtente(details, infoGenerali);
			// fine del report
			
			//Section section = details.addSection();
			//
			// generazione del sorgente xml per eventuale debugging col desiger
			//			
			// System.out.println("costruzione report terminata. Salvataggio su
			// file");
			// RDC.saveEngine(new File("C:/pattoTEST.rpt"), eng);
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
		// ReportUtils.addTextPart(t.getParagraph(0), this.numProtocolloStr,
		// campo);
		// inserisco il promptField nel campo di visualizzazione del numero di
		// protocollo
		// per questa operazione non esiste una utilità
		tp = t.getParagraph(0).addFieldPart(numProt);
		tp.setX(0);
		campo.setStyle(tp);
		t = ReportUtils.addText(section, "Data di prot. ", etichetta, 920);
		// ReportUtils.addTextPart(t.getParagraph(0), this.datProtocollazione,
		// campo);
		tp = t.getParagraph(0).addFieldPart(dataProt);
		tp.setX(0);
		campo.setStyle(tp);
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
		String deTipoPatto = (String) infoGenerali.getAttribute("deTipoPattoMisura");
		Section section = details.addSection();
		Text t = ReportUtils.addText(section, "", etichetta, LEFT);
		Paragraph p = t.getParagraph(0);
		ReportUtils.addTextPart(t.getParagraph(0), "concordano di attivare nell'ambito della misura ", etichetta);
		ReportUtils.addTextPart(p, deTipoPatto.trim(), campo);
		ReportUtils.addTextPart(t.getParagraph(0), " il/i seguente/i obiettivo/i:", etichetta);
		addBR(details, 03);
		Set misure = new HashSet(azioni.size());
		for (int i = 0; i < azioni.size(); i++) {
			misure.add(((SourceBean) azioni.get(i)).getAttribute("azione_ragg"));
		}
		Iterator misureIter = misure.iterator();
		for (; misureIter.hasNext();) {
			String misura = (String) misureIter.next();
			titoloPuntato(details, misura, campo);
		}
	}
	
	/**
	 * Vengono stampate le mansioni in un elenco puntato. Se non sono state
	 * associate mansioni al patto viene stampato il campo note della sezione
	 * ambito professionale del patto, sempre che queste note siano presenti.
	 */
	private void ambitoProfessionale(Area details, Vector rows, SourceBean patto) throws Exception {
		Section section = details.addSection();
		Style s = (Style) etichetta.clone();
		s.setFontSize(12);
		if (rows.size() > 0) {
			ReportUtils.addText(section, "nella mansione di:", s, LEFT);
			addBR(details, 20);
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

	private void titoloStampa(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Accordo di servizio";
		ReportUtils.addText(section, s, titolo2Centrato, 0);
	}

	/**
	 * sezione dati profiling
	 * @param details
	 * @param infoGenerali
	 * @throws Exception
	 */
	private void profilingDati(Area details, String indiceSvantaggio,String indiceSvantaggio2, String dataRiferimento,String datStipula) throws Exception {
		boolean indicePresente = false;
		Section section = details.addSection();
		Paragraph p = null;
		Text t = null;
		String limitProfiling2 = "01/02/2015";
						
		if (DateUtils.compare(datStipula, limitProfiling2) >= 0) {
			if (dataRiferimento != null && !dataRiferimento.equals("")) {
				if (!indicePresente) {
					t = ReportUtils.addText(section, "", etichetta, LEFT);
					p = t.getParagraph(0);
				}
				else {
					ReportUtils.addTextPart(t.getParagraph(0), "                ", etichetta);
				}
				addBR(details, 40);
				ReportUtils.addTextPart(t.getParagraph(0), "Data di riferimento ", etichetta);
				ReportUtils.addTextPart(p, dataRiferimento, campo);
			}
			
			if (datStipula != null && !datStipula.equals("")){
				Section section2 = details.addSection();
				Text tp = null;Paragraph pp = null;																
				if (indiceSvantaggio2 != null && !indiceSvantaggio2.equals("")){				
					addBR(details, 20);
					tp = ReportUtils.addText(section2, "", etichetta, LEFT);
					pp = tp.getParagraph(0);
					ReportUtils.addTextPart(tp.getParagraph(0), "Profiling 2 (Garanzia Giovani) ", etichetta);
					ReportUtils.addTextPart(pp, indiceSvantaggio2, campo);
				}
			}
		}
		else {
			if (indiceSvantaggio != null && !indiceSvantaggio.equals("")) {
				indicePresente = true;
				t = ReportUtils.addText(section, "", etichetta, LEFT);
				p = t.getParagraph(0);
				addBR(details, 40);
				ReportUtils.addTextPart(t.getParagraph(0), "Profiling  (Garanzia Giovani) ", etichetta);
				ReportUtils.addTextPart(p, indiceSvantaggio, campo);
			}
			
			if (dataRiferimento != null && !dataRiferimento.equals("")) {
				if (!indicePresente) {
					t = ReportUtils.addText(section, "", etichetta, LEFT);
					p = t.getParagraph(0);
				}
				else {
					ReportUtils.addTextPart(t.getParagraph(0), "                ", etichetta);
				}
				addBR(details, 20);
				ReportUtils.addTextPart(t.getParagraph(0), "Data di riferimento ", etichetta);
				ReportUtils.addTextPart(p, dataRiferimento, campo);
			}
								
			if (datStipula != null && !datStipula.equals("")){
				Section section2 = details.addSection();
				Text tp = null;Paragraph pp = null;																
				if (indiceSvantaggio2 != null && !indiceSvantaggio2.equals("")){				
					addBR(details, 20);
					tp = ReportUtils.addText(section2, "", etichetta, LEFT);
					pp = tp.getParagraph(0);
					ReportUtils.addTextPart(tp.getParagraph(0), "Profiling 2 (Garanzia Giovani) ", etichetta);
					ReportUtils.addTextPart(pp, indiceSvantaggio2, campo);
				}
			}		
		}
	}
	
	
	/**
	 * sezione impegni lavoratore ed impegni cpi
	 */
	private void impegni(Area details, Vector impegni) throws Exception {

		Style s = (Style) etichetta.clone();
		s.setFontSize(12);
		Section section = details.addSection();
		ReportUtils.addText(section, "In rispetto del presente Accordo l'utente si impegna a:", s, LEFT);
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
	 * Sezione appuntamenti
	 */
	private void appuntamenti(Area details, Vector rows) throws Exception {
		addBR(details, 20);	
		if (rows == null) {
			return;
		}
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			appuntamento(details, row);
		}
	}

	/**
	 * Stampa del dettaglio di un appuntamento
	 */
	private void appuntamento(Area details, SourceBean appuntamenti) throws Exception {
		if (appuntamenti == null) {
			appuntamenti = new SourceBean("");
		}
		String servizio = (String) appuntamenti.getAttribute("DESSERVIZIO");
		String data = (String) appuntamenti.getAttribute("DATA");
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
	 * Sezione azioni (percorsi) concordatie: vengono raggruppate per data e in
	 * elenchi con trattino (quindi non puntato)
	 */
	private void azioniConcordate(Area details, Vector rows) throws Exception {
		Section section = details.addSection();
		String dataPercorso = null;
		ReportUtils.addText(section, "mediante le azioni di:", etichetta, LEFT);
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
		if (dataPercorso != null) {
			dataAzioniConcordate(details, dataPercorso);
		}
			
	}

	/**
	 * Dettaglio azione concordata
	 */
	private void azioneConcordata(Area details, SourceBean azioniConcordate) throws Exception {
		if (azioniConcordate == null) {
			azioniConcordate = new SourceBean("");
		}
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
		addBR(details, 20);
	}

	/**
	 * LE INFORMAZIONI UTENTE VENGONO INSERITE NEL REPORT UTILIZZANDO UN
	 * SUBREPORT LETTO DA DISCO, ovvero un subreport creato col designer di
	 * Crystal Clear al quale vengono passati 3 parametri.
	 */
	private void informazioniUtente(Area details, SourceBean infoGenerali) throws Exception {
		if (infoGenerali == null) {
			infoGenerali = new SourceBean("");
		}
		String cpi = (String) infoGenerali.getAttribute("CPI");
		String dataStipula = (String) infoGenerali.getAttribute("datStipula");
		String datScadConferma = (String) infoGenerali.getAttribute("datScadConferma");
		Section section = details.addSection();
		section.setNewPageBefore(false);
		// aggiunta del subreport da disco
		Subreport sub = section.addSubreport(0, 0, StyleUtils.toTwips(1950), StyleUtils.toTwips(56), "file:"
				+ this.installAppPath + "/WEB-INF/report/patto/_informazioniAccordo_CC.rpt");
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

		paginaInformazioni(details, cpi, dataStipula, datScadConferma);
	}

	/**
	 * dati anagrafici del lavoratore e data dichiarazione immediata
	 * disponibilita'
	 */
	private void dichiarazioneLavoratore(Area details, SourceBean infoLavoratore) throws Exception {
		// le informazioni del lavoratore dovrebbero sempre esserci .....
		if (infoLavoratore == null)
			infoLavoratore = new SourceBean("");
		Section section = details.addSection();
		String cpi = Utils.notNull(infoLavoratore.getAttribute("CPI"));
		String nome = Utils.notNull(infoLavoratore.getAttribute("NOMELAVORATORE"));
		String cognome = Utils.notNull(infoLavoratore.getAttribute("COGNOMELAVORATORE"));
		String codiceFiscale = (String) infoLavoratore.getAttribute("STRCODICEFISCALE");
		String flgSesso = Utils.notNull(infoLavoratore.getAttribute("sessoLavoratore"));
		String lavArticolo = "e la Signora ";

		//
		if ("M".equals(flgSesso.toUpperCase()))
			lavArticolo = "e il Signor ";
		//
		ReportUtils.addText(section, "Il centro per l'impiego di " + cpi, etichetta, LEFT);

		section = details.addSection();
		Text t = ReportUtils.addText(section, lavArticolo, etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(0), cognome + " " + nome, etichetta);
		t = ReportUtils.addText(section, "Cod. fisc. ", etichetta, 1248);
		ReportUtils.addTextPart(t.getParagraph(0), codiceFiscale, campo);
		//

	}

	/**
	 * Intestazione testata del cpi con logo. A causa della cornice non e'
	 * possibile utilizzare piu' sezioni, una per ogni riga (come nel resto del
	 * report)
	 */
	private void testataCPI(Area area, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		String cpi = (String) row.getAttribute("CPI");
		String provincia = (String) row.getAttribute("STRINTESTAZIONESTAMPA");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		String indirizzo = (String) row.getAttribute("INDIRIZZO");
		//
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));

		// Box box = sec.addBox(StyleUtils.toTwips(80), 0,
		// StyleUtils.toTwips(1370), StyleUtils.toTwips(205));
		Box box = sec.addBox(StyleUtils.toTwips(80), 0, StyleUtils.toTwips(1770), StyleUtils.toTwips(205));

		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(5), StyleUtils.toTwips(1250), StyleUtils
				.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(true);
		//
		String codTipoPattoMisura = row.containsAttribute("codtipopatto")?row.getAttribute("codtipopatto").toString():"";
		boolean isPattoGG = false;
		boolean isPattoGGU = false;
		if (this.programmiAperti != null && !this.programmiAperti.isEmpty()) {
			if (PattoBean.checkMisuraProgramma(this.programmiAperti, PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
				isPattoGG = true;
			}
			if (PattoBean.checkMisuraProgramma(this.programmiAperti, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
				isPattoGGU = true;
			}
		}
		if(!regione.equals(RER)) {
			if (codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) ||
				codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) ||
				isPattoGG || isPattoGGU) {
				sec.addPicture(StyleUtils.toTwips(1600), StyleUtils.toTwips(26), 900, 836, this.installAppPath
						+ "/WEB-INF/report/img/logo_internal.png");
			}
		}
		//
		text =
		// sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95),
		// StyleUtils.toTwips(350), StyleUtils.toTwips(50));
		sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95), StyleUtils.toTwips(600), StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		//
		text =
		// sec.addText(StyleUtils.toTwips(470), StyleUtils.toTwips(95),
		// StyleUtils.toTwips(350), StyleUtils.toTwips(50));
		sec.addText(StyleUtils.toTwips(720), StyleUtils.toTwips(95), StyleUtils.toTwips(350), StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("fax. " + Utils.notNull(fax));
		s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		// 		
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(150), StyleUtils.toTwips(1000), StyleUtils
				.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		s = (Style) campo.clone();
		s.setStyle(tp);

		tp.setBold(true);
		// /////////
		/*
		sec.addBox(StyleUtils.toTwips(1448), 0, StyleUtils.toTwips(500), StyleUtils.toTwips(205));
		text = sec.addText(StyleUtils.toTwips(1600), StyleUtils.toTwips(5), StyleUtils.toTwips(350), StyleUtils
				.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(Utils.notNull(provincia).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		text.setCanGrow(true);
		//	
		sec.addPicture(StyleUtils.toTwips(1455), StyleUtils.toTwips(26), 795, 870, this.installAppPath
				+ "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		int contatoreImmagini = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				if (codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) ||
					codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) ||
					isPattoGG || isPattoGGU) {
					if (contatoreImmagini > 0) {
						((Element) elements.get(i)).setSuppress(true);
					}
				}
				else {
					((Element) elements.get(i)).setSuppress(true);
				}
				contatoreImmagini = contatoreImmagini + 1;
			}
		} */
	}
	
	/**
	 * Intestazione testata del cpi regione VDA con logo. A causa della cornice non e'
	 * possibile utilizzare piu' sezioni, una per ogni riga (come nel resto del
	 * report)
	 */
	private void testataCPIVDA(Area area, SourceBean row) throws Exception {
		if (row == null) {
			row = new SourceBean("");
		}
		String cpi = (String) row.getAttribute("CPI");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));
		
		Box box = sec.addBox(StyleUtils.toTwips(80), 0, StyleUtils.toTwips(1170), StyleUtils.toTwips(205));

		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(5), StyleUtils.toTwips(1250), StyleUtils
				.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(true);
		//
		String codTipoPattoMisura = row.containsAttribute("codtipopatto")?row.getAttribute("codtipopatto").toString():"";
		boolean isPattoGG = false;
		boolean isPattoGGU = false;
		if (this.programmiAperti != null && !this.programmiAperti.isEmpty()) {
			if (PattoBean.checkMisuraProgramma(this.programmiAperti, PattoBean.DB_MISURE_GARANZIA_GIOVANI)) {
				isPattoGG = true;
			}
			if (PattoBean.checkMisuraProgramma(this.programmiAperti, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
				isPattoGGU = true;
			}
		}
		if (codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) ||
			codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) ||
			isPattoGG || isPattoGGU) {
			sec.addPicture(StyleUtils.toTwips(1075), StyleUtils.toTwips(26), 900, 836, this.installAppPath
					+ "/WEB-INF/report/img/logo_internal.png");
		}
		//
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95), StyleUtils.toTwips(600), StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toTwips(720), StyleUtils.toTwips(95), StyleUtils.toTwips(350), StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("fax. " + Utils.notNull(fax));
		s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		 		
		text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(150), StyleUtils.toTwips(1000), StyleUtils
				.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		s = (Style) campo.clone();
		s.setStyle(tp);
		
		tp.setBold(true);
		
		sec.addBox(StyleUtils.toTwips(1248), 0, StyleUtils.toTwips(630), StyleUtils.toTwips(205));
		text = sec.addText(StyleUtils.toTwips(1400), StyleUtils.toTwips(50), StyleUtils.toTwips(450), StyleUtils
				.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Regione Autonoma Valle d'Aosta\nRégion Autonome Vallée d'Aoste ");
		tp.setFontSize(8);
		tp.setFontName("Arial");
		tp.setBold(true);
		text.setCanGrow(true);
		//		
		sec.addPicture(StyleUtils.toTwips(1255), StyleUtils.toTwips(26), 795, 870, this.installAppPath
				+ "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		int contatoreImmagini = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				if (codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) ||
					codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) ||
					isPattoGG || isPattoGGU) {
					if (contatoreImmagini > 0) {
						((Element) elements.get(i)).setSuppress(true);
					}
				}
				else {
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
	 * come stile viene usato 'CAMPO' ma con fontSize = 12 (anzicche' 11 dello
	 * stile CAMPO)
	 */
	private void titoloPuntato(Area details, String titolo, Style s) throws Exception {
		Section section = details.addSection();
		section.addPicture(StyleUtils.toTwips(LEFT + 50), StyleUtils.toTwips(19), StyleUtils.toTwips(17), StyleUtils
				.toTwips(17), this.installAppPath + "/WEB-INF/report/img/punto_carta.gif");
		ReportUtils.addText(section, titolo, s, LEFT_TITOLO_PUNTO_T);
	}

	private void paginaInformazioni(Area details, String cpi, String dataStipula, String dataScadenza) throws Exception {
		String nomeUt = "";
		String cognomeUt = "";
		
		addBR(details, 50);
		Section section = details.addSection();
		Text text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("Data aggiornamento accordo " + DateUtils.getNow());
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");
		
		addBR(details, 50);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart(cpi + ", li " + dataStipula);
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");

		addBR(details, 20);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250), StyleUtils
				.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Per il CPI");
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");

		addBR(details, 20);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250), StyleUtils
				.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Firma dell'operatore del CPI");
		
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");
		
		text = section.addText(StyleUtils.toTwips(1480), StyleUtils.toTwips(5), StyleUtils.toTwips(500), StyleUtils
				.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Firma del lavoratore");
		tp.setFontSize(11);
		tp.setFontName("Times New Roman");
		
		addBR(details, 20);
		
		if (this.operatore != null){
			nomeUt = Utils.notNull(this.operatore.getAttribute("ROW.STRNOMEUTENTE"));
			cognomeUt = Utils.notNull(this.operatore.getAttribute("ROW.STRCOGNOMEUTENTE"));
		}
		
		section = details.addSection();
		StringBuilder datiFirmaElettronica = new StringBuilder("sottoscritto con firma elettronica");
		
		if (this.flgPattoOnline != null && this.flgPattoOnline.equalsIgnoreCase(Properties.FLAG_S)) {
			datiFirmaElettronica.append("                                                                             ");
			datiFirmaElettronica.append("sottoscritto con firma elettronica");
		}
		datiFirmaElettronica.append("\nda ").append(nomeUt).append(" ").append(cognomeUt);
		
		if (this.flgPattoOnline != null && this.flgPattoOnline.equalsIgnoreCase(Properties.FLAG_S)) {
			String nomeLav = "";
			String cognomeLav = "";
			String nomeCognomeLav = "";
			if (this.infoGenerali != null){
				nomeLav = Utils.notNull(this.infoGenerali.getAttribute("NOMELAVORATORE"));
				cognomeLav = Utils.notNull(this.infoGenerali.getAttribute("COGNOMELAVORATORE"));
				nomeCognomeLav = "da " + nomeLav + " " + cognomeLav;
			}
			int spaziTotali = SPAZI_TRA_OPERATORE_LAV;
			
			if (nomeCognomeLav.length() > ACCETTAZIONELENMAXLAV) {
				spaziTotali = SPAZI_TRA_OPERATORE_LAV_MIN;
				spaziTotali = spaziTotali - ((nomeCognomeLav.length() - ACCETTAZIONELENMAXLAV)*3);
			}
			else {
				spaziTotali = spaziTotali + (ACCETTAZIONELENMAXLAV - nomeCognomeLav.length());
			}
			
			for (int ispazi = 0; ispazi < spaziTotali; ispazi++){
				datiFirmaElettronica.append(" ");
			}
			
			datiFirmaElettronica.append(nomeCognomeLav);
		}
		
		ReportUtils.addText(section, datiFirmaElettronica.toString(), etichetta, LEFT);
		
		addBR(details, 50);
		
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1750), StyleUtils
				.toTwips(80));
		p = text.addParagraph();
		if(StringUtils.isFilledNoBlank(privacy) && privacy.equalsIgnoreCase("1")){
			tp = p.addTextPart("È stata RICEVUTA/PRESO VISIONE, INOLTRE, L'INFORMATIVA prevista dall'art 13 del Regolamento europeo n. 679/2016");
		}else{
			tp = p.addTextPart("È stata inoltre presa visione dell'informativa relativa al trattamento dei dati personali. (Rif. legge 196/03).");
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
