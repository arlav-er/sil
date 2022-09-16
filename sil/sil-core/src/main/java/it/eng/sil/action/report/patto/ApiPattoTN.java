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
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.module.patto.MansioniRow;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

/**
 * 
 * @author Administrator
 */
public class ApiPattoTN {
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

	private String datProtocollazione;
	private String codMonoIO;
	private String riferimentoDoc;
	//
	private Engine eng = null;
	private String installAppPath = null;

	private Vector<String> programmiAperti;

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

	public void setDatProtocollazione(String s) {
		this.datProtocollazione = s;
	}

	public void setCodMonoIO(String s) {
		this.codMonoIO = s;
	}

	public void setDocInOut(String s) {
		this.docInOut = s;
	}

	/** Creates a new instance of Patto */
	public ApiPattoTN() {
		styleFactory = new StyleFactory();
		campo = styleFactory.getStyle(StyleFactory.CAMPO);
		titoloSezione = styleFactory.getStyle(StyleFactory.TITOLO_SEZIONE);
		etichetta = styleFactory.getStyle(StyleFactory.ETICHETTA);
		etichetta2 = styleFactory.getStyle(StyleFactory.ETICHETTA2);
		titolo2 = styleFactory.getStyle(StyleFactory.TITOLO2);
		titolo3 = styleFactory.getStyle(StyleFactory.TITOLO3);
		titolo2Centrato = styleFactory.getStyle(StyleFactory.TITOLO2CENTRATO);

	}

	public ApiPattoTN(String[] a) throws Exception {
		start();
	}

	public void inizializza(SourceBean infoGen, SourceBean statoOcc, Vector appuntaments, SourceBean operatore,
			Vector azioniConcordats, Vector ambitoProfs, Vector impegniV, String installAppPath, String tipoFile,
			SourceBean cat181, Vector titoloStudio, Vector mov, String ambito, String strParam, String docInOut,
			String regione, SourceBean documentoIdentita) throws Exception {
		try {

			this.setInfoGenerali(infoGen);
			this.setStatoOccupazionale(statoOcc);
			this.setAppuntamenti(appuntaments);
			this.setAzioniConcordate(azioniConcordats);
			this.setAmbitoProfessionale(ambitoProfs);
			this.setImpegni(impegniV);
			this.setInstallAppPath(installAppPath);
			this.setFileType(tipoFile);
			this.setCat181(cat181);
			this.setLaurea(titoloStudio);
			this.setMovimenti(mov);
			this.setRiferimentoDoc(ambito);

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
				this.programmiAperti = PattoBean.checkProgrammi(prgPattoLav, null);
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
			addBR(details, 50);
			testataCPI(details, infoGenerali);
			addBR(details, 80);
			titoloMinore(details);
			//
			addBR(details, 50);
			dichiarazioneLavoratore(details, infoGenerali);
			addBR(details, 50);
			// -
			statoOccupazionale(details);// , statoOccupazionale, infoGenerali,
										// cat181, laurea, movimenti);
			// -
			addBR(details, 50);
			misure(details, infoGenerali, azioniConcordate);
			// -
			// -
			addBR(details, 80);
			ambitoProfessionale(details, ambitoProfessionale, infoGenerali);
			// -
			addBR(details, 50);
			// -
			titoloDisponibilita(details);
			addBR(details, 50);
			azioniConcordate(details, azioniConcordate);
			// -
			if (appuntamenti != null && appuntamenti.size() > 0) {
				addBR(details, 50);
				appuntamenti(details, appuntamenti);
			}
			// -
			String indiceSvantaggio = infoGenerali.getAttribute("numindicesvantaggio") == null ? null
					: ((BigDecimal) infoGenerali.getAttribute("numindicesvantaggio")).toString();
			String indiceSvantaggio2 = infoGenerali.getAttribute("numindicesvantaggio2") == null ? null
					: ((BigDecimal) infoGenerali.getAttribute("numindicesvantaggio")).toString();
			String dataStipula = (String) infoGenerali.getAttribute("datStipula");

			String dataRiferimento = (String) infoGenerali.getAttribute("datriferimento");
			if ((dataRiferimento != null && !dataRiferimento.equals(""))) {
				profilingDati(details, indiceSvantaggio, indiceSvantaggio2, dataRiferimento, dataStipula);
			}

			// -
			addBR(details, 100);
			impegni(details, impegni);
			// -
			informazioniUtente(details, infoGenerali);
			// fine del report
			Section section = details.addSection();
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
	 * parte fissa Modifica Ale 19/04 per Trento
	 */
	private void titoloDisponibilita(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Nel caso di adesione al servizio di incontro domanda/offerta, il dettaglio delle disponibilità dell'utente è riportato in allegato e costituisce"
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
			// addBR(details, 20);
			Style stp = (Style) campo.clone();
			stp.setFontSize(12);
			stp.setCanGrow(true);
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
	 * sezione dati profiling
	 * 
	 * @param details
	 * @param infoGenerali
	 * @throws Exception
	 */
	private void profilingDati(Area details, String indiceSvantaggio, String indiceSvantaggio2, String dataRiferimento,
			String datStipula) throws Exception {
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
				} else {
					ReportUtils.addTextPart(t.getParagraph(0), "                ", etichetta);
				}
				ReportUtils.addTextPart(t.getParagraph(0), "Data di riferimento ", etichetta);
				ReportUtils.addTextPart(p, dataRiferimento, campo);
			}

			if (datStipula != null && !datStipula.equals("")) {
				Section section2 = details.addSection();
				Text tp = null;
				Paragraph pp = null;
				if (indiceSvantaggio2 != null && !indiceSvantaggio2.equals("")) {
					addBR(details, 20);
					tp = ReportUtils.addText(section2, "", etichetta, LEFT);
					pp = tp.getParagraph(0);
					ReportUtils.addTextPart(tp.getParagraph(0), "Profiling 2 (Garanzia Giovani) ", etichetta);
					ReportUtils.addTextPart(pp, indiceSvantaggio2, campo);
				}
			}
		} else {
			if (indiceSvantaggio != null && !indiceSvantaggio.equals("")) {
				indicePresente = true;
				t = ReportUtils.addText(section, "", etichetta, LEFT);
				p = t.getParagraph(0);
				ReportUtils.addTextPart(t.getParagraph(0), "Profiling  (Garanzia Giovani) ", etichetta);
				ReportUtils.addTextPart(p, indiceSvantaggio, campo);
			}

			if (dataRiferimento != null && !dataRiferimento.equals("")) {
				if (!indicePresente) {
					t = ReportUtils.addText(section, "", etichetta, LEFT);
					p = t.getParagraph(0);
				} else {
					ReportUtils.addTextPart(t.getParagraph(0), "                ", etichetta);
				}
				ReportUtils.addTextPart(t.getParagraph(0), "Data di riferimento ", etichetta);
				ReportUtils.addTextPart(p, dataRiferimento, campo);
			}

			if (datStipula != null && !datStipula.equals("")) {
				Section section2 = details.addSection();
				Text tp = null;
				Paragraph pp = null;
				if (indiceSvantaggio2 != null && !indiceSvantaggio2.equals("")) {
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
		addBR(details, 50);
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
		String dataStipula = (String) infoGenerali.getAttribute("datStipula");
		String datScadConferma = (String) infoGenerali.getAttribute("datScadConferma");
		Section section = details.addSection();
		section.setNewPageBefore(true);
		// aggiunta del subreport da disco
		Subreport sub = section.addSubreport(0, 0, StyleUtils.toTwips(1950), StyleUtils.toTwips(56),
				"file:" + this.installAppPath + "/WEB-INF/report/patto/_informazioniUtente_new_CC_TN.rpt");
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
	 * dati anagrafici del lavoratore e data dichiarazione immediata disponibilita'
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
		t = ReportUtils.addText(section, "Cod. fisc. ", etichetta, 1350);
		ReportUtils.addTextPart(t.getParagraph(0), codiceFiscale, campo);
		//

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
	private void statoOccupazionale(Area details/*
												 * , SourceBean statoOccupazionale, SourceBean infoLavoratore,
												 * SourceBean cat181Rows, SourceBean laureaRows, Vector movimenti
												 */) throws Exception {
		// parti fisse da stampare
		String dich1 = "che ha reso dichiarazione di immediata disponibilità allo svolgimento di attività lavorativa in data ";
		String dich2 = " e che si trova nella condizione di ";
		// i mesi di anzianita' non vanno piu' mostrati (31/01/2005)
		// String dich3 = ", con mesi di anzianità di disoccupazione ";
		String dich4 = ", concordano per l'uscita dallo stato di disoccupazione:";
		// calcolo delle informazioni da stampare
		int par = 0;
		String tipoDisoccupatoLungaDurata = "";
		String cdnLavoratore = ((BigDecimal) infoGenerali.getAttribute("cdnLavoratore")).toString();
		String dataDichiarazioneImmDisp = Utils.notNull(infoGenerali.getAttribute("DATDICHIARAZIONE"));
		String statoOccupaz = (String) statoOccupazionale.getAttribute("DESCRIZIONESTATO");
		BigDecimal mesiAnzianita = (BigDecimal) statoOccupazionale.getAttribute("MESI_ANZ");
		if (mesiAnzianita == null)
			mesiAnzianita = new BigDecimal("0");
		// String cat181Desc =
		// (String)statoOccupazionale.getAttribute("DESCRIZIONE181");
		String codStatoOccRagg = (String) statoOccupazionale.getAttribute("codstatooccupazragg");
		BigDecimal mesiAnzPrec = (BigDecimal) statoOccupazionale.getAttribute("MESI_ANZ_PREC");
		String mesiAnzPrecInt = null;
		BigDecimal mesiAnz = null;
		String mesiAnzInt = null;
		BigDecimal numMesiSosp = null;
		String numMesiSospInt = null;
		numMesiSosp = (BigDecimal) statoOccupazionale.getAttribute("NUMMESISOSP");
		mesiAnz = (BigDecimal) statoOccupazionale.getAttribute("MESI_ANZ");
		BigDecimal numMesiSospPrec = null;
		String numMesiSospPrecInt = null;
		numMesiSospPrec = (BigDecimal) statoOccupazionale.getAttribute("NUMMESISOSPPREC");
		if (mesiAnz != null)
			mesiAnzInt = String.valueOf(mesiAnz.intValue());
		else
			mesiAnzInt = "0";
		if (mesiAnzPrec != null) {
			mesiAnzPrecInt = String.valueOf(mesiAnzPrec.intValue());
		} else
			mesiAnzPrecInt = "0";
		if (numMesiSospPrec != null) {
			numMesiSospPrecInt = String.valueOf(numMesiSospPrec.intValue());
		} else
			numMesiSospPrecInt = "0";
		if (numMesiSosp != null)
			numMesiSospInt = String.valueOf(numMesiSosp.intValue());
		else
			numMesiSospInt = "0";
		if (numMesiSospInt != null && Integer.parseInt(numMesiSospInt) < 0)
			numMesiSospInt = "0"; // null;
		if (numMesiSospPrecInt != null && Integer.parseInt(numMesiSospPrecInt) < 0)
			numMesiSospPrecInt = "0";
		if (mesiAnzInt != null && Integer.parseInt(mesiAnzInt) < 0)
			mesiAnzInt = "0"; // null;
		if (mesiAnzPrecInt != null && Integer.parseInt(mesiAnzPrecInt) < 0)
			mesiAnzPrecInt = "0";
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
		it.eng.sil.module.movimenti.InfoLavoratore _lav = new it.eng.sil.module.movimenti.InfoLavoratore(
				new BigDecimal(cdnLavoratore));
		if (cat181 != null && cat181Desc.equalsIgnoreCase("GIOVANE"))
			codCat181 = "G";
		if (movimenti != null && movimenti.size() > 0) {
			SourceBean row = (SourceBean) movimenti.get(0);
			dataFineMovimento = (String) row.getAttribute("DATAFINEMOVIMENTO");
			mesiInattivita = (BigDecimal) row.getAttribute("mesiInattivita");
		}
		// Numero effettivo mesi di anzianità
		BigDecimal totMesiAnz = new BigDecimal(
				String.valueOf(Integer.parseInt(mesiAnzPrecInt) + Integer.parseInt(mesiAnzInt)
						- (Integer.parseInt(numMesiSospPrecInt) + Integer.parseInt(numMesiSospInt))));
		tipoDisoccupatoLungaDurata = Controlli.disoccInoccLungaDurata(codStatoOccRagg, totMesiAnz, codCat181); // mesiAnz
		String sesso = _lav.getSesso();
		boolean donnaInReinserimento = Controlli.donnaInInserimentoLavorativo(codStatoOccRagg, mesiInattivita, sesso);
		// *********** stampa informazioni *********
		Section section = details.addSection();
		// did
		Text t = ReportUtils.addText(section, dich1, etichetta, LEFT);
		ReportUtils.addTextPart(t.getParagraph(par), dataDichiarazioneImmDisp, campo);
		// stato occupazionale
		ReportUtils.addTextPart(t.getParagraph(par), dich2, etichetta);
		ReportUtils.addTextPart(t.getParagraph(par), statoOccupaz, campo);
		// mesi anzianita
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
		if (donnaInReinserimento) {
			ReportUtils.addTextPart(t.getParagraph(par), ", ", etichetta);
			ReportUtils.addTextPart(t.getParagraph(par), "donna in reinserimento lavorativo", campo);
		}
		ReportUtils.addTextPart(t.getParagraph(par), dich4, etichetta);

	}

	/**
	 * Intestazione testata del cpi con logo. A causa della cornice non e' possibile utilizzare piu' sezioni, una per
	 * ogni riga (come nel resto del report)
	 */
	private void testataCPI(Area area, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		String cpi = (String) row.getAttribute("CPI");
		String provincia = (String) row.getAttribute("PROVINCIA");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		String indirizzo = (String) row.getAttribute("INDIRIZZO");
		//
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toTwips(250));

		// Box box = sec.addBox(StyleUtils.toTwips(80), 0,
		// StyleUtils.toTwips(1370), StyleUtils.toTwips(205));
		Box box = sec.addBox(StyleUtils.toTwips(80), 0, StyleUtils.toTwips(1370), StyleUtils.toTwips(205));

		//
		Text text = sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(true);
		String codTipoPattoMisura = row.containsAttribute("codtipopatto") ? row.getAttribute("codtipopatto").toString()
				: "";
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
		if (codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)
				|| codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) || isPattoGG
				|| isPattoGGU) {
			sec.addPicture(StyleUtils.toTwips(1200), StyleUtils.toTwips(26), 900, 836,
					this.installAppPath + "/WEB-INF/report/img/logo_internal.png");
		}

		//
		text =
				// sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95),
				// StyleUtils.toTwips(350), StyleUtils.toTwips(50));
				sec.addText(StyleUtils.toTwips(100), StyleUtils.toTwips(95), StyleUtils.toTwips(600),
						StyleUtils.toTwips(50));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = (Style) campo.clone();
		s.setStyle(tp);
		tp.setBold(true);
		//
		text =
				// sec.addText(StyleUtils.toTwips(470), StyleUtils.toTwips(95),
				// StyleUtils.toTwips(350), StyleUtils.toTwips(50));
				sec.addText(StyleUtils.toTwips(720), StyleUtils.toTwips(95), StyleUtils.toTwips(350),
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
		// /////////
		sec.addBox(StyleUtils.toTwips(1448), 0, StyleUtils.toTwips(500), StyleUtils.toTwips(205));
		text = sec.addText(StyleUtils.toTwips(1600), StyleUtils.toTwips(5), StyleUtils.toTwips(350),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("PROVINCIA DI " + Utils.notNull(provincia).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		text.setCanGrow(true);
		//
		sec.addPicture(StyleUtils.toTwips(1455), StyleUtils.toTwips(26), 795, 870,
				this.installAppPath + "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		int contatoreImmagini = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				if (codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI)
						|| codTipoPattoMisura.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) || isPattoGG
						|| isPattoGGU) {
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

	private void paginaInformazioni(Area details, String cpi, String dataStipula, String dataScadenza)
			throws Exception {

		addBR(details, 50);
		Section section = details.addSection();
		Text text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart(cpi + ", li " + dataStipula);
		tp.setFontSize(12);

		addBR(details, 80);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Per il CPI");
		tp.setFontSize(12);

		addBR(details, 25);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1250),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Firma operatore");
		tp.setFontSize(12);
		text = section.addText(StyleUtils.toTwips(1480), StyleUtils.toTwips(5), StyleUtils.toTwips(500),
				StyleUtils.toTwips(80));
		p = text.addParagraph();
		tp = p.addTextPart("Firma del lavoratore");
		tp.setFontSize(12);

		addBR(details, 50);
		section = details.addSection();
		text = section.addText(StyleUtils.toTwips(130), StyleUtils.toTwips(5), StyleUtils.toTwips(1700),
				StyleUtils.toTwips(400));
		p = text.addParagraph();
		tp = p.addTextPart("Il presente patto è valido sino alla data: ");
		tp.setFontSize(12);
		tp = p.addTextPart(dataScadenza);
		tp.setFontSize(12);
		tp.setBold(true);
		tp = p.addTextPart("\n");
		tp.setFontSize(12);
		tp = p.addTextPart("Il lavoratore può richiederne il rinnovo entro e non oltre la data di validità.\n");
		tp.setFontSize(12);
		tp = p.addTextPart(
				"Se la data di fine validità coincide con un giorno festivo o di non apertura del Centro il patto resta valido sino al primo giorno di apertura del Centro.");
		tp.setFontSize(12);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			ApiPattoTN p = new ApiPattoTN(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}
}
