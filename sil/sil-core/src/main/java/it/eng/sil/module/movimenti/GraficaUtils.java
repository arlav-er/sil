package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

/**
 * Classe di utilita per la generazione di alcuni elementi ricorrenti nelle pagine JSP di presentazione dei risultati
 * dell'inserimento/validazione dei movimenti
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class GraficaUtils {
	/**
	 * Costruttore.
	 */
	public GraficaUtils() {
	}

	/**
	 * Metodo per la formattazione delle informazioni di testata dei risultati <br/>
	 * 
	 * @param result
	 *            Risultato della processazione
	 * @return Stringa HTML delle informazioni globali sui risultati
	 */
	public static String showInfoGlobali(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		String processed = result.containsAttribute("PROCESSED") ? result.getAttribute("PROCESSED").toString() : "";
		String numRecord = result.containsAttribute("NUMRECORD") ? result.getAttribute("NUMRECORD").toString() : "";
		String flgStopUtente = result.containsAttribute("FLGSTOPUTENTE")
				? result.getAttribute("FLGSTOPUTENTE").toString()
				: "";
		String correctProcessed = result.containsAttribute("CORRECTPROCESSED")
				? result.getAttribute("CORRECTPROCESSED").toString()
				: "";
		String ErrorProcessed = result.containsAttribute("ERRORPROCESSED")
				? result.getAttribute("ERRORPROCESSED").toString()
				: "";

		String sommario = "<p class=\"titolo\">Numero di record processati: " + processed + " (di " + numRecord
				+ (flgStopUtente.equalsIgnoreCase("S") ? ", Interrotta dall'utente" : "") + ")<br/>\n"
				+ "Record validati: " + correctProcessed + "<br/>\n" + "Record non validati: " + ErrorProcessed
				+ "</p>\n";
		return sommario;
	}

	/**
	 * Metodo per la formattazione delle informazioni di testata dei risultati dell'importazione <br/>
	 * 
	 * @param result
	 *            Risultato della processazione
	 * @return Stringa HTML delle informazioni globali sui risultati
	 */
	public static String showInfoGlobaliImportazione(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}

		String processed = result.containsAttribute("PROCESSED") ? result.getAttribute("PROCESSED").toString() : "";
		String numRecord = result.containsAttribute("NUMRECORD") ? result.getAttribute("NUMRECORD").toString() : "";
		String flgStopUtente = result.containsAttribute("FLGSTOPUTENTE")
				? result.getAttribute("FLGSTOPUTENTE").toString()
				: "";
		String correctProcessed = result.containsAttribute("CORRECTPROCESSED")
				? result.getAttribute("CORRECTPROCESSED").toString()
				: "";
		String ErrorProcessed = result.containsAttribute("ERRORPROCESSED")
				? result.getAttribute("ERRORPROCESSED").toString()
				: "";

		String sommario = "<p class=\"titolo\">Numero di record processati: " + processed + " (di " + numRecord
				+ (flgStopUtente.equalsIgnoreCase("S") ? ", Interrotta dall'utente" : "") + ")<br/>\n"
				+ "Record importati: " + correctProcessed + "<br/>\n" + "Record non importati: " + ErrorProcessed
				+ "</p>\n";
		return sommario;
	}

	public static String showInfoGlobaliCopiaProsp(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		String processed = result.containsAttribute("PROCESSED") ? result.getAttribute("PROCESSED").toString() : "";
		String numRecord = result.containsAttribute("NUMRECORD") ? result.getAttribute("NUMRECORD").toString() : "";
		String correctProcessedSC = result.containsAttribute("CORRECTPROCESSEDSC")
				? result.getAttribute("CORRECTPROCESSEDSC").toString()
				: "";
		String correctProcessedAC = result.containsAttribute("CORRECTPROCESSEDAC")
				? result.getAttribute("CORRECTPROCESSEDAC").toString()
				: "";
		String ErrorProcessed = result.containsAttribute("ERRORPROCESSED")
				? result.getAttribute("ERRORPROCESSED").toString()
				: "";

		String sommario = "<p class=\"titolo\">Numero di PI processati: " + processed + " (di " + numRecord + ")<br/>\n"
				+ "PI storicizzati e copiati: " + correctProcessedSC + "<br/>\n" + "PI annullati e copiati: "
				+ correctProcessedAC + "<br/>\n" + "PI in errore: " + ErrorProcessed + "</p>\n";
		return sommario;
	}

	/**
	 * Metodo per la formattazione dei pulsanti di navigazione dei risultati <br/>
	 * 
	 * @param result
	 *            Risultato della processazione
	 * @return Stringa HTML dei pusanti di navigazione sui risultati
	 */
	public static String showPulsantiNavigazione(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}

		// Guardo se ho processato tutti i record o meno per decidere se
		// visualizzare il pulsante di aggiorna
		Integer numRecords = (Integer) result.getAttribute("NUMRECORD");
		Integer processed = (Integer) result.getAttribute("PROCESSED");
		Integer currentPage = (Integer) result.getAttribute("CURRENTPAGE");
		Integer numPages = (Integer) result.getAttribute("NUMPAGES");
		Integer numeroDa = (Integer) result.getAttribute("NUMERODA");
		Integer numeroA = (Integer) result.getAttribute("NUMEROA");

		// Mostro il pulsante di aggiorna finchè questi sono i risultati
		// correnti e in sessione ho il validator
		// corrente attivo
		boolean aggiorna = false;
		String prgValMassiva = result.containsAttribute("PRGVALIDAZIONEMASSIVA")
				? result.getAttribute("PRGVALIDAZIONEMASSIVA").toString()
				: "";
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		ResultLogFormatter formatter = (ResultLogFormatter) sessione.getAttribute("RISULTATICORRENTI");

		if (formatter != null) {
			String currentPrgValMassiva = formatter.getPrgValidazioneMassiva().toString();
			if (prgValMassiva.equalsIgnoreCase(currentPrgValMassiva)
					&& (sessione.getAttribute("VALIDATORCORRENTE") != null)) {
				aggiorna = true;
			}
		}
		String pulsanti = new String();

		pulsanti += "<img src=\"../../img/list_first.gif\" alt=\"Prima pagina\" title=\"Prima pagina\" onclick=\"getPage('FIRST');\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_prev.gif\" alt=\"Pagina precedente\" title=\"Pagina precedente\" onclick=\"getPage('PREVIOUS');\"/>&nbsp;";
		if (aggiorna) {
			pulsanti += "<img src=\"../../img/add.gif\" alt=\"Aggiorna risultati\" title=\"Aggiorna risultati\" onclick=\"getPage('SAME');\"/>&nbsp;";
		}
		pulsanti += "<img src=\"../../img/list_next.gif\" alt=\"Pagina successiva\" title=\"Pagina successiva\" onclick=\"getPage('NEXT');\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_last.gif\" alt=\"Ultima pagina\" title=\"Ultima pagina\" onclick=\"getPage('LAST');\"/>&nbsp;";
		// Pulsante "VAI A PAG"
		if (numPages.intValue() > 1) {
			pulsanti += "&nbsp;<select name=\"selPage\" onChange=\"allineaSelPageIndex(this.selectedIndex);\">";
			for (int i = 1; i <= numPages.intValue(); i++) {
				pulsanti += "<option value=\"" + i + "\"";
				if (i == currentPage.intValue()) {
					pulsanti += " selected";
				}
				pulsanti += ">" + i + "</option>";
			}
			pulsanti += "</select>&nbsp;<button width=\"16px\" type=\"button\" class=\"pulsanti\" ";
			pulsanti += "onClick=\"goPage();\">Vai</button>\n";
			// funzione javascript
			pulsanti += "<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n";
			pulsanti += "<!--\n";
			pulsanti += "function goPage() {\n";
			pulsanti += "var coll = document.getElementsByName(\"selPage\");\n";
			pulsanti += "if(coll.length>0) {\n";
			pulsanti += "var i = coll[0].value;\n";
			pulsanti += "getPage(String(i));\n";
			pulsanti += "}\n";
			pulsanti += "}\n";
			pulsanti += "function allineaSelPageIndex(idx) {\n";
			pulsanti += "var coll = document.getElementsByName(\"selPage\");\n";
			pulsanti += "if(coll.length>0) {\n";
			pulsanti += "for(i=0; i < coll.length; i++) { coll[i].selectedIndex = idx; }\n";
			pulsanti += "}\n";
			pulsanti += "}\n";
			pulsanti += "// -->\n";
			pulsanti += "</SCRIPT>\n";
		}
		pulsanti += "<p class=\"titolo\"><b>Pag. " + currentPage + " di " + numPages + " (da " + numeroDa + " a "
				+ numeroA + " di " + processed + ")</b></p>";
		return pulsanti;
	}

	/**
	 * Metodo per la formattazione dei pulsanti di navigazione dei risultati della validazione massiva della mobilità
	 * 
	 * @param result
	 * @return
	 * @throws SourceBeanException
	 */
	public static String showPulsantiNavigazioneValidazioneMob(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		// Guardo se ho processato tutti i record o meno per decidere se
		// visualizzare il pulsante di aggiorna
		Integer numRecords = (Integer) result.getAttribute("NUMRECORD");
		Integer processed = (Integer) result.getAttribute("PROCESSED");
		Integer currentPage = (Integer) result.getAttribute("CURRENTPAGE");
		Integer numPages = (Integer) result.getAttribute("NUMPAGES");
		Integer numeroDa = (Integer) result.getAttribute("NUMERODA");
		Integer numeroA = (Integer) result.getAttribute("NUMEROA");

		// Mostro il pulsante di aggiorna finchè questi sono i risultati
		// correnti e in sessione ho il validator
		// corrente attivo
		boolean aggiorna = false;
		String prgValMassiva = result.containsAttribute("PRGVALIDAZIONEMASSIVA")
				? result.getAttribute("PRGVALIDAZIONEMASSIVA").toString()
				: "";
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		BigDecimal prgCurrPrgValMassiva = (BigDecimal) sessione.getAttribute("PROGRESSIVOULTIMAVALIDAZIONEMOBILITA");
		if (prgCurrPrgValMassiva != null) {
			String currentPrgValMassiva = prgCurrPrgValMassiva.toString();
			if (prgValMassiva.equalsIgnoreCase(currentPrgValMassiva)
					&& (sessione.getAttribute("VALIDATORMOBILITACORRENTE") != null)) {
				aggiorna = true;
			}
		}

		String pulsanti = new String();

		pulsanti += "<img src=\"../../img/list_first.gif\" alt=\"Prima pagina\" title=\"Prima pagina\" onclick=\"getPage('FIRST');\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_prev.gif\" alt=\"Pagina precedente\" title=\"Pagina precedente\" onclick=\"getPage('PREVIOUS');\"/>&nbsp;";
		if (aggiorna) {
			pulsanti += "<img src=\"../../img/add.gif\" alt=\"Aggiorna risultati\" title=\"Aggiorna risultati\" onclick=\"getPage('SAME');\"/>&nbsp;";
		}
		pulsanti += "<img src=\"../../img/list_next.gif\" alt=\"Pagina successiva\" title=\"Pagina successiva\" onclick=\"getPage('NEXT');\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_last.gif\" alt=\"Ultima pagina\" title=\"Ultima pagina\" onclick=\"getPage('LAST');\"/>&nbsp;";
		// Pulsante "VAI A PAG"
		if (numPages.intValue() > 1) {
			pulsanti += "&nbsp;<select name=\"selPage\" onChange=\"allineaSelPageIndex(this.selectedIndex);\">";
			for (int i = 1; i <= numPages.intValue(); i++) {
				pulsanti += "<option value=\"" + i + "\"";
				if (i == currentPage.intValue()) {
					pulsanti += " selected";
				}
				pulsanti += ">" + i + "</option>";
			}
			pulsanti += "</select>&nbsp;<button width=\"16px\" type=\"button\" class=\"pulsanti\" ";
			pulsanti += "onClick=\"goPage();\">Vai</button>\n";
			// funzione javascript
			pulsanti += "<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n";
			pulsanti += "<!--\n";
			pulsanti += "function goPage() {\n";
			pulsanti += "var coll = document.getElementsByName(\"selPage\");\n";
			pulsanti += "if(coll.length>0) {\n";
			pulsanti += "var i = coll[0].value;\n";
			pulsanti += "getPage(String(i));\n";
			pulsanti += "}\n";
			pulsanti += "}\n";
			pulsanti += "function allineaSelPageIndex(idx) {\n";
			pulsanti += "var coll = document.getElementsByName(\"selPage\");\n";
			pulsanti += "if(coll.length>0) {\n";
			pulsanti += "for(i=0; i < coll.length; i++) { coll[i].selectedIndex = idx; }\n";
			pulsanti += "}\n";
			pulsanti += "}\n";
			pulsanti += "// -->\n";
			pulsanti += "</SCRIPT>\n";
		}
		pulsanti += "<p class=\"titolo\"><b>Pag. " + currentPage + " di " + numPages + " (da " + numeroDa + " a "
				+ numeroA + " di " + processed + ")</b></p>";
		return pulsanti;
	}

	public static String showPulsantiNavigazioneCopiaProsp(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}

		Integer processed = (Integer) result.getAttribute("PROCESSED");
		Integer currentPage = (Integer) result.getAttribute("CURRENTPAGE");
		Integer numPages = (Integer) result.getAttribute("NUMPAGES");
		Integer numeroDa = (Integer) result.getAttribute("NUMERODA");
		Integer numeroA = (Integer) result.getAttribute("NUMEROA");

		// Mostro il pulsante di aggiorna finchè questi sono i risultati correnti e in sessione ho il validator corrente
		// attivo
		boolean aggiorna = false;
		String prgCopiaMassiva = result.containsAttribute("PRGVALIDAZIONEMASSIVA")
				? result.getAttribute("PRGVALIDAZIONEMASSIVA").toString()
				: "";
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		BigDecimal prgCurrPrgCopiaMassiva = (BigDecimal) sessione.getAttribute("PROGRESSIVOULTIMACOPIAPROSP");
		if (prgCurrPrgCopiaMassiva != null) {
			String currentPrgCopiaMassiva = prgCurrPrgCopiaMassiva.toString();
			if (prgCopiaMassiva.equalsIgnoreCase(currentPrgCopiaMassiva)
					&& (sessione.getAttribute("COPIAPROSPETTOCORRENTE") != null)) {
				aggiorna = true;
			}
		}

		String pulsanti = new String();

		pulsanti += "<img src=\"../../img/list_first.gif\" alt=\"Prima pagina\" title=\"Prima pagina\" onclick=\"getPage('FIRST');\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_prev.gif\" alt=\"Pagina precedente\" title=\"Pagina precedente\" onclick=\"getPage('PREVIOUS');\"/>&nbsp;";
		if (aggiorna) {
			pulsanti += "<img src=\"../../img/add.gif\" alt=\"Aggiorna risultati\" title=\"Aggiorna risultati\" onclick=\"getPage('SAME');\"/>&nbsp;";
		}
		pulsanti += "<img src=\"../../img/list_next.gif\" alt=\"Pagina successiva\" title=\"Pagina successiva\" onclick=\"getPage('NEXT');\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_last.gif\" alt=\"Ultima pagina\" title=\"Ultima pagina\" onclick=\"getPage('LAST');\"/>&nbsp;";
		// Pulsante "VAI A PAG"
		if (numPages.intValue() > 1) {
			pulsanti += "&nbsp;<select name=\"selPage\" onChange=\"allineaSelPageIndex(this.selectedIndex);\">";
			for (int i = 1; i <= numPages.intValue(); i++) {
				pulsanti += "<option value=\"" + i + "\"";
				if (i == currentPage.intValue()) {
					pulsanti += " selected";
				}
				pulsanti += ">" + i + "</option>";
			}
			pulsanti += "</select>&nbsp;<button width=\"16px\" type=\"button\" class=\"pulsanti\" ";
			pulsanti += "onClick=\"goPage();\">Vai</button>\n";
			// funzione javascript
			pulsanti += "<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n";
			pulsanti += "<!--\n";
			pulsanti += "function goPage() {\n";
			pulsanti += "var coll = document.getElementsByName(\"selPage\");\n";
			pulsanti += "if(coll.length>0) {\n";
			pulsanti += "var i = coll[0].value;\n";
			pulsanti += "getPage(String(i));\n";
			pulsanti += "}\n";
			pulsanti += "}\n";
			pulsanti += "function allineaSelPageIndex(idx) {\n";
			pulsanti += "var coll = document.getElementsByName(\"selPage\");\n";
			pulsanti += "if(coll.length>0) {\n";
			pulsanti += "for(i=0; i < coll.length; i++) { coll[i].selectedIndex = idx; }\n";
			pulsanti += "}\n";
			pulsanti += "}\n";
			pulsanti += "// -->\n";
			pulsanti += "</SCRIPT>\n";
		}
		pulsanti += "<p class=\"titolo\"><b>Pag. " + currentPage + " di " + numPages + " (da " + numeroDa + " a "
				+ numeroA + " di " + processed + ")</b></p>";
		return pulsanti;
	}

	/**
	 * Metodo per la formattazione della lista dei risultati <br/>
	 * 
	 * @param result
	 *            Risultato della processazione
	 * @param showLink
	 *            indica se occorre visualizzare i link verso il dettaglio dei movimenti non validati
	 * @param open
	 *            indica se le tendine dei risultati devono essere sempre aperte o sempre chiuse, se null saranno aperte
	 *            solo quelle con errori.
	 * @return Stringa HTML dei risultati formattati
	 */
	public static String showRisultati(SourceBean result, boolean showLink, Boolean open) throws SourceBeanException {
		return showRisultati(result, showLink, open, "", false);
	}

	/**
	 * DAVIDE Questo metodo è stato aggiunto per dare una evidenza cromatica alla sezione che riguarda la gestione dei
	 * risultati da validazione massiva. Con questo metodo compaiono dei 'pallini' rossi se c'è stato un errore in
	 * validazione, gialli se c'è solo una segnalazione, verdi se è andato tutto OK. In più quando dalla validazione
	 * manuale si vuole tornare alla lista dei risultati l'ultimo movimento validato viene evidenziato (in azzurro)
	 */
	public static String showRisultati(SourceBean result, boolean showLink, Boolean open, String RitornaPrgRisultato,
			boolean colorare) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		List records = result.getAttributeAsVector("RECORD");

		String righe = "";
		String colorePunto = "";

		if ((records == null) || (records.size() == 0)) {
			righe = "<p>Nessuna informazione disponibile sui record processati<p/>";
		} else {
			int i = 0;
			// Creo le sottosezioni per i record
			Iterator iterator = records.iterator();
			while (iterator.hasNext()) {
				i++;
				// Recupero il record
				SourceBean record = (SourceBean) iterator.next();
				// estraggo il vettore dei processori per il record, il
				// risultato e il prg del record.
				List processors = record.getAttributeAsVector("PROCESSOR");
				String recordResult = StringUtils.getAttributeStrNotNull(record, "RESULT");
				String prgmovapp = (record.containsAttribute("RECORDID") ? record.getAttribute("RECORDID").toString()
						: null);
				String errorLogCode = (record.containsAttribute("LOGERROR.code")
						? record.getAttribute("LOGERROR.code").toString()
						: null);

				// Estraggo l'identificativo del movimento dalla risposta del
				// record e i dati dall'identificativo da
				// inserire nella tendina dei risultati
				SourceBean idMov = (SourceBean) record.getAttribute("IDMOV");
				String codTipoMov = "";
				String nomeLav = "";
				String cognomeLav = "";
				String ragsocaz = "";
				String strCFlavoratore = "";
				String strNumAnnoProt = "";
				if (idMov != null) {
					codTipoMov = StringUtils.getAttributeStrNotNull(idMov, "codTipoMov");
					nomeLav = StringUtils.getAttributeStrNotNull(idMov, "nomeLav");
					cognomeLav = StringUtils.getAttributeStrNotNull(idMov, "cognomeLav");
					ragsocaz = StringUtils.getAttributeStrNotNull(idMov, "ragSocAzienda");
					strCFlavoratore = StringUtils.getAttributeStrNotNull(idMov, "strCfLavoratore");
					strNumAnnoProt = ""; // StringUtils.getAttributeStrNotNull(idMov,
											// "strNumAnnoProt");
					// Substring delle info troppo lunghe
					if (ragsocaz.length() > 30) {
						ragsocaz = ragsocaz.substring(0, 30) + "...";
					}
				}
				String righeRecord = "";
				if (processors != null && (processors.size() > 0)) {
					righeRecord = righeRecord + generaElementiLista(processors);
				}

				// Inserisco la tendina con la lista delle informazioni e il
				// link verso il dettaglio,
				// lo stato dipende dal
				// valore di open se è true la tendina sarà sempre aperta, se è
				// false sempre chiusa,
				// altrimenti sarà aperta solo
				// se ho avuto errori, chiusa se ho solo "WARNING"
				// SourceBean rowsTemp = (SourceBean) record.getAttribute("");
				String prgRisultato = "";
				String stile = "";

				// Definisce la classe usata nel tag <li> per caricare le
				// immagini nella lista risultati
				colorePunto = "luceVerde";

				if (record.containsAttribute("ROWS.ROW.PRGRISULTATO")) {
					prgRisultato = ((BigDecimal) record.getAttribute("ROWS.ROW.PRGRISULTATO")).toString();
				}

				if (RitornaPrgRisultato != null && !RitornaPrgRisultato.equals("")) {
					if (RitornaPrgRisultato.equalsIgnoreCase(prgRisultato)) {
						// stile = "style=\"background-color:gold\"";
						stile = "style=\"background-color:#99FFFF\"";
					}
				}

				boolean aperta = (open != null ? open.booleanValue() : ProcessorsUtils.isError(record));
				String tendinaRecord = "<a name=\"RITORNA_PRGRISULTATO_" + prgRisultato
						+ "\"></a><div class='sezione2' " + stile + " id='sezioneRisultato" + i + "'>\n"
						+ " <img id='tendinaRisultato" + i + "' alt='" + (aperta ? "Chiudi" : "Apri") + "' src='"
						+ (aperta ? "../../img/aperto.gif" : "../../img/chiuso.gif")
						+ "' onclick='cambia(this, document.getElementById(\"listaRisultato" + i + "\"));'/>\n";

				if (ProcessorsUtils.isWarning(record)) {
					colorePunto = "luceGialla";
				}

				// Inserisco il link se ho un prgmovapp valido e un errore (o un
				// errore nel Log)
				if (prgmovapp != null && showLink
						&& (ProcessorsUtils.isError(record) || ProcessorsUtils.isLogError(record))) {
					tendinaRecord = tendinaRecord + " <a href='AdapterHTTP?PAGE=MovValidaDettaglioGeneralePage"
							+ "&CDNFUNZIONE=52&PROVENIENZA=validazione&destinazione=MovValidaDettaglioGeneralePage"
							+ "&PRGMOVIMENTOAPP=" + prgmovapp
							+ "&PAGERITORNOLISTA=MOVRISULTVALIDAZIONEPAGE&PRGRISULTATO=" + prgRisultato + "'>"
							+ "  <img id='dettaglioRecord" + i
							+ "' alt='Valida manualmente' src='../../img/detail.gif'/>" + " </a>";
					colorePunto = "luceRossa";
				}

				// Dati per identificazione del movimento se presenti
				if (idMov != null) {
					if (!strNumAnnoProt.equals("")) {
						tendinaRecord += " -PR: " + strNumAnnoProt + " -";
					}
					tendinaRecord += "Tipo Mov: " + codTipoMov + " - Azienda: " + ragsocaz + " - Lavoratore: "
							+ cognomeLav + "&nbsp;" + nomeLav + "&nbsp;&nbsp;";
					if (!strCFlavoratore.equals("")) {
						tendinaRecord += strCFlavoratore;
					}
				} else {
					tendinaRecord += "Dettagli dell'elaborazione&nbsp;&nbsp;";
				}

				// Errore nella scrittura del log se presente
				String strErrorLog = "";
				if (errorLogCode != null) {
					strErrorLog = "<li style=\"list-style-image: none\"><b>ATTENZIONE</b>:&nbsp;"
							+ MessageBundle.getMessage(errorLogCode) + "</li>\n";
				}

				tendinaRecord = tendinaRecord + "<br>\n </div>\n";
				String listaRecord = "<div style='display: " + (aperta ? "inline" : "none") + ";' id='listaRisultato"
						+ i + "'><ul style=\"list-style-image: none\">" + righeRecord + strErrorLog + "</ul></div>\n";

				// righe = righe + "<li style=\"list-style-image:
				// url(../../img/" + colorePunto + ")\">" + tendinaRecord +
				// listaRecord + "</li>\n";
				if (colorare) {
					righe = righe + "<li class=\"" + colorePunto + "\">" + tendinaRecord + listaRecord + "</li>\n";
				} else {
					righe = righe + "<li>" + tendinaRecord + listaRecord + "</li>\n";
				}

				// aggiungo l'indicazione se la sezione è aperta o chiusa
				righe = righe + "<script language=\"javascript\">document.getElementById(\"listaRisultato" + i
						+ "\").aperta = " + (aperta ? "true" : "false") + "</script>";
			}
		}

		// ritorno la lista con i risultati
		return "<ul>\n" + righe + "</ul>\n";
	}

	public static String showRisultatiValidazioneMobilita(SourceBean result, boolean showLink, Boolean open,
			String RitornaPrgRisultato, boolean colorare) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		List records = result.getAttributeAsVector("RECORD");
		String righe = "";
		String colorePunto = "";

		if ((records == null) || (records.size() == 0)) {
			righe = "<p>Nessuna informazione disponibile sui record processati<p/>";
		} else {
			int i = 0;
			// Creo le sottosezioni per i record
			Iterator iterator = records.iterator();
			while (iterator.hasNext()) {
				i++;
				// Recupero il record
				SourceBean record = (SourceBean) iterator.next();
				// estraggo il vettore dei processori per il record, il
				// risultato e il prg del record.
				List processors = record.getAttributeAsVector("PROCESSOR");
				String recordResult = StringUtils.getAttributeStrNotNull(record, "RESULT");
				String prgMobIscrApp = (record.containsAttribute("RECORDID")
						? record.getAttribute("RECORDID").toString()
						: null);
				String errorLogCode = (record.containsAttribute("LOGERROR.code")
						? record.getAttribute("LOGERROR.code").toString()
						: null);

				// Estraggo l'identificativo del movimento dalla risposta del
				// record e i dati dall'identificativo da
				// inserire nella tendina dei risultati
				SourceBean idMob = (SourceBean) record.getAttribute("IDMOB");
				String nomeLav = "";
				String cognomeLav = "";
				String ragsocaz = "";
				String strCFlavoratore = "";
				String codTipoMob = "";
				String codComuneAz = "";
				String strCodiceFiscaleAz = "";
				if (idMob != null) {
					codTipoMob = StringUtils.getAttributeStrNotNull(idMob, "codTipoMob");
					nomeLav = StringUtils.getAttributeStrNotNull(idMob, "nomeLav");
					cognomeLav = StringUtils.getAttributeStrNotNull(idMob, "cognomeLav");
					ragsocaz = StringUtils.getAttributeStrNotNull(idMob, "ragSocAzienda");
					strCFlavoratore = StringUtils.getAttributeStrNotNull(idMob, "strCfLavoratore");
					codComuneAz = StringUtils.getAttributeStrNotNull(idMob, "codComuneAz");
					strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(idMob, "strCodiceFiscaleAz");
					// Substring delle info troppo lunghe
					if (ragsocaz.length() > 30) {
						ragsocaz = ragsocaz.substring(0, 30) + "...";
					}
				}
				String righeRecord = "";
				if (processors != null && (processors.size() > 0)) {
					righeRecord = righeRecord + generaElementiListaMobilita(processors,
							MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA);
				}

				// Inserisco la tendina con la lista delle informazioni e il
				// link verso il dettaglio,
				// lo stato dipende dal
				// valore di open se è true la tendina sarà sempre aperta, se è
				// false sempre chiusa,
				// altrimenti sarà aperta solo
				// se ho avuto errori, chiusa se ho solo "WARNING"
				// SourceBean rowsTemp = (SourceBean) record.getAttribute("");
				String prgRisultato = "";
				String stile = "";

				// Definisce la classe usata nel tag <li> per caricare le
				// immagini nella lista risultati
				colorePunto = "luceVerde";

				if (RitornaPrgRisultato != null && !RitornaPrgRisultato.equals("")) {
					if (RitornaPrgRisultato.equalsIgnoreCase(prgRisultato)) {
						// stile = "style=\"background-color:gold\"";
						stile = "style=\"background-color:#99FFFF\"";
					}
				}

				boolean aperta = (open != null ? open.booleanValue() : ProcessorsUtils.isError(record));
				String tendinaRecord = "<a name=\"RITORNA_PRGRISULTATO_" + prgRisultato
						+ "\"></a><div class='sezione2' " + stile + " id='sezioneRisultato" + i + "'>\n"
						+ " <img id='tendinaRisultato" + i + "' alt='" + (aperta ? "Chiudi" : "Apri") + "' src='"
						+ (aperta ? "../../img/aperto.gif" : "../../img/chiuso.gif")
						+ "' onclick='cambia(this, document.getElementById(\"listaRisultato" + i + "\"));'/>\n";

				if (ProcessorsUtils.isWarning(record)) {
					colorePunto = "luceGialla";
				}

				// Inserisco il link se ho un prgmovapp valido e un errore (o un
				// errore nel Log)
				if (prgMobIscrApp != null && showLink
						&& (ProcessorsUtils.isError(record) || ProcessorsUtils.isLogError(record))) {
					tendinaRecord = tendinaRecord + " <a href='AdapterHTTP?PAGE=ValidazioneMobilitaGeneralePage"
							+ "&CDNFUNZIONE=200&codComune=" + codComuneAz + "&strCodiceFiscale=" + strCodiceFiscaleAz
							+ "&strCodiceFiscaleLav=" + strCFlavoratore + "&prgMobilitaIscrApp=" + prgMobIscrApp
							+ "&PAGERITORNOLISTA=MobVisualizzaRisultValidazionePage&PRGRISULTATO=" + prgRisultato + "'>"
							+ "  <img id='dettaglioRecord" + i
							+ "' alt='Valida manualmente' src='../../img/detail.gif'/>" + " </a>";
					colorePunto = "luceRossa";
				}

				// Dati per identificazione del movimento se presenti
				if (idMob != null) {
					tendinaRecord += "Tipo Mobilità: " + codTipoMob + " - Azienda: " + ragsocaz + " - Lavoratore: "
							+ cognomeLav + "&nbsp;" + nomeLav + "&nbsp;&nbsp;";
					if (!strCFlavoratore.equals("")) {
						tendinaRecord += strCFlavoratore;
					}
				} else {
					tendinaRecord += "Dettagli dell'elaborazione&nbsp;&nbsp;";
				}

				// Errore nella scrittura del log se presente
				String strErrorLog = "";
				if (errorLogCode != null) {
					strErrorLog = "<li style=\"list-style-image: none\"><b>ATTENZIONE</b>:&nbsp;"
							+ MessageBundle.getMessage(errorLogCode) + "</li>\n";
				}

				tendinaRecord = tendinaRecord + "<br>\n </div>\n";
				String listaRecord = "<div style='display: " + (aperta ? "inline" : "none") + ";' id='listaRisultato"
						+ i + "'><ul style=\"list-style-image: none\">" + righeRecord + strErrorLog + "</ul></div>\n";

				// righe = righe + "<li style=\"list-style-image:
				// url(../../img/" + colorePunto + ")\">" + tendinaRecord +
				// listaRecord + "</li>\n";
				if (colorare) {
					righe = righe + "<li class=\"" + colorePunto + "\">" + tendinaRecord + listaRecord + "</li>\n";
				} else {
					righe = righe + "<li>" + tendinaRecord + listaRecord + "</li>\n";
				}

				// aggiungo l'indicazione se la sezione è aperta o chiusa
				righe = righe + "<script language=\"javascript\">document.getElementById(\"listaRisultato" + i
						+ "\").aperta = " + (aperta ? "true" : "false") + "</script>";
			}
		}

		// ritorno la lista con i risultati
		return "<ul>\n" + righe + "</ul>\n";
	}

	public static String showRisultatiCopiaProspetti(SourceBean result, boolean showLink, Boolean open,
			String RitornaPrgRisultato, boolean colorare) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		List records = result.getAttributeAsVector("RECORD");
		String righe = "";
		String colorePunto = "";

		if ((records == null) || (records.size() == 0)) {
			righe = "<p>Nessuna informazione disponibile sui record processati<p/>";
		} else {
			int i = 0;
			// Creo le sottosezioni per i record
			Iterator iterator = records.iterator();
			while (iterator.hasNext()) {
				i++;
				// Recupero il record
				SourceBean record = (SourceBean) iterator.next();
				// estraggo il vettore dei processori per il record, il
				// risultato e il prg del record.
				List processors = record.getAttributeAsVector("PROCESSOR");
				String errorLogCode = (record.containsAttribute("LOGERROR.code")
						? record.getAttribute("LOGERROR.code").toString()
						: null);

				// Estraggo l'identificativo del movimento dalla risposta del
				// record e i dati dall'identificativo da
				// inserire nella tendina dei risultati
				SourceBean idProsp = (SourceBean) record.getAttribute("IDPROSPETTO");
				String ragsocaz = "";
				String strIndirizzoAz = "";
				String cfAz = "";
				String provinciaProsp = "";
				String fasciaAz = "";
				String obbligo68 = "";
				if (idProsp != null) {
					ragsocaz = StringUtils.getAttributeStrNotNull(idProsp, "ragSocAzienda");
					// Substring delle info troppo lunghe
					if (ragsocaz.length() > 30) {
						ragsocaz = ragsocaz.substring(0, 30) + "...";
					}
					strIndirizzoAz = StringUtils.getAttributeStrNotNull(idProsp, "indirizzoAz");
					cfAz = StringUtils.getAttributeStrNotNull(idProsp, "strCodiceFiscaleAz");
					provinciaProsp = StringUtils.getAttributeStrNotNull(idProsp, "provinciaProsp");
					fasciaAz = StringUtils.getAttributeStrNotNull(idProsp, "fasciaAZ");
					obbligo68 = StringUtils.getAttributeStrNotNull(idProsp, "obbligoL68");
				}
				String righeRecord = "";
				if (processors != null && (processors.size() > 0)) {
					righeRecord = righeRecord + generaElementiListaProspetti(processors);
				}

				String prgRisultato = "";
				String stile = "";

				// Definisce la classe usata nel tag <li> per caricare le
				// immagini nella lista risultati
				colorePunto = "luceVerde";

				if (RitornaPrgRisultato != null && !RitornaPrgRisultato.equals("")) {
					if (RitornaPrgRisultato.equalsIgnoreCase(prgRisultato)) {
						// stile = "style=\"background-color:gold\"";
						stile = "style=\"background-color:#99FFFF\"";
					}
				}

				boolean aperta = (open != null ? open.booleanValue() : ProcessorsUtils.isError(record));
				String tendinaRecord = "<a name=\"RITORNA_PRGRISULTATO_" + prgRisultato
						+ "\"></a><div class='sezione2' " + stile + " id='sezioneRisultato" + i + "'>\n"
						+ " <img id='tendinaRisultato" + i + "' alt='" + (aperta ? "Chiudi" : "Apri") + "' src='"
						+ (aperta ? "../../img/aperto.gif" : "../../img/chiuso.gif")
						+ "' onclick='cambia(this, document.getElementById(\"listaRisultato" + i + "\"));'/>\n";

				if (ProcessorsUtils.isWarning(record)) {
					colorePunto = "luceGialla";
				}

				if (ProcessorsUtils.isError(record) || ProcessorsUtils.isLogError(record)) {
					colorePunto = "luceRossa";
				}

				// Dati per identificazione del movimento se presenti
				if (idProsp != null) {
					tendinaRecord += "Azienda: " + ragsocaz + " - " + strIndirizzoAz;
				} else {
					tendinaRecord += "Dettagli dell'elaborazione&nbsp;&nbsp;";
				}

				// Errore nella scrittura del log se presente
				String strErrorLog = "";
				if (errorLogCode != null) {
					strErrorLog = "<li style=\"list-style-image: none\"><b>ATTENZIONE</b>:&nbsp;"
							+ MessageBundle.getMessage(errorLogCode) + "</li>\n";
				}

				tendinaRecord = tendinaRecord + "<br>\n </div>\n";

				String listaRecord = "<div style='display: " + (aperta ? "inline" : "none") + ";' id='listaRisultato"
						+ i + "'><table><tr><td>CF:&nbsp;" + cfAz + "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"
						+ "<td>Provincia Prospetto:&nbsp;" + provinciaProsp + "</td></tr>"
						+ "<tr><td>Categoria azienda:&nbsp;" + fasciaAz + "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"
						+ "<td>Obbligo L.68:&nbsp;" + obbligo68 + "</td></tr></table>"
						+ "<ul style=\"list-style-image: none\">" + righeRecord + strErrorLog + "</ul></div>\n";

				if (colorare) {
					righe = righe + "<li class=\"" + colorePunto + "\">" + tendinaRecord + listaRecord + "</li>\n";
				} else {
					righe = righe + "<li>" + tendinaRecord + listaRecord + "</li>\n";
				}

				// aggiungo l'indicazione se la sezione è aperta o chiusa
				righe = righe + "<script language=\"javascript\">document.getElementById(\"listaRisultato" + i
						+ "\").aperta = " + (aperta ? "true" : "false") + "</script>";
			}
		}

		// ritorno la lista con i risultati
		return "<ul>\n" + righe + "</ul>\n";
	}

	/**
	 * METODO INVOCATO NELL'IMPORTAZIONE MOBILITA'
	 * 
	 * @param result
	 * @param showLink
	 * @param open
	 * @return
	 * @throws SourceBeanException
	 */
	public static String showRisultatiMobilita(SourceBean result, boolean showLink, Boolean open)
			throws SourceBeanException {
		return showRisultatiMobilita(result, showLink, open, "", false);
	}

	/**
	 * METODO INVOCATO NELL'IMPORTAZIONE MOBILITA'
	 * 
	 * @param result
	 * @param showLink
	 * @param open
	 * @param RitornaPrgRisultato
	 * @param colorare
	 * @return
	 * @throws SourceBeanException
	 */
	public static String showRisultatiMobilita(SourceBean result, boolean showLink, Boolean open,
			String RitornaPrgRisultato, boolean colorare) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		List records = result.getAttributeAsVector("RECORD");

		// String righe = "";
		String colorePunto = "";
		StringBuffer risultato = null;
		// imposto una dimensione massima per il buffer di 10 milioni di byte
		int dimBuffer = (records.size() * 1000 > 10000000) ? 10000000 : (records.size() * 1000);
		if ((records == null) || (records.size() == 0)) {
			risultato.append("<ul>\n<p>Nessuna informazione disponibile sui record processati<p/>");
			// righe = "<p>Nessuna informazione disponibile sui record
			// processati<p/>";
		} else {
			risultato = new StringBuffer(dimBuffer);
			risultato.append("<ul>\n");
			int i = 0;
			// Creo le sottosezioni per i record
			Iterator iterator = records.iterator();
			while (iterator.hasNext()) {
				i++;
				String righe = "";
				// Recupero il record
				SourceBean record = (SourceBean) iterator.next();
				// estraggo il vettore dei processori per il record, il
				// risultato e il prg del record.
				List processors = record.getAttributeAsVector("PROCESSOR");
				String recordResult = StringUtils.getAttributeStrNotNull(record, "RESULT");
				String prgmobapp = (record.containsAttribute("RECORDID") ? record.getAttribute("RECORDID").toString()
						: null);
				String errorLogCode = (record.containsAttribute("LOGERROR.code")
						? record.getAttribute("LOGERROR.code").toString()
						: null);

				// Estraggo l'identificativo del movimento dalla risposta del
				// record e i dati dall'identificativo da
				// inserire nella tendina dei risultati
				SourceBean idMob = (SourceBean) record.getAttribute("IDMOB");
				String codTipoMob = "";
				String nomeLav = "";
				String cognomeLav = "";
				String ragsocaz = "";
				String strCFlavoratore = "";
				String strNumAnnoProt = "";
				if (idMob != null) {
					codTipoMob = StringUtils.getAttributeStrNotNull(idMob, "codTipoMob");
					nomeLav = StringUtils.getAttributeStrNotNull(idMob, "nomeLav");
					cognomeLav = StringUtils.getAttributeStrNotNull(idMob, "cognomeLav");
					ragsocaz = StringUtils.getAttributeStrNotNull(idMob, "ragSocAzienda");
					strCFlavoratore = StringUtils.getAttributeStrNotNull(idMob, "strCfLavoratore");
					strNumAnnoProt = ""; // StringUtils.getAttributeStrNotNull(idMov,
											// "strNumAnnoProt");
					// Substring delle info troppo lunghe
					if (ragsocaz.length() > 30) {
						ragsocaz = ragsocaz.substring(0, 30) + "...";
					}
				}
				String righeRecord = "";
				if (processors != null && (processors.size() > 0)) {
					righeRecord = righeRecord + generaElementiLista(processors);
				}

				// Inserisco la tendina con la lista delle informazioni e il
				// link verso il dettaglio,
				// lo stato dipende dal
				// valore di open se è true la tendina sarà sempre aperta, se è
				// false sempre chiusa,
				// altrimenti sarà aperta solo
				// se ho avuto errori, chiusa se ho solo "WARNING"
				// SourceBean rowsTemp = (SourceBean) record.getAttribute("");
				String prgRisultato = "";
				String stile = "";

				// Definisce la classe usata nel tag <li> per caricare le
				// immagini nella lista risultati
				colorePunto = "luceVerde";

				if (record.containsAttribute("ROWS.ROW.PRGRISULTATO")) {
					prgRisultato = ((BigDecimal) record.getAttribute("ROWS.ROW.PRGRISULTATO")).toString();
				}

				if (RitornaPrgRisultato != null && !RitornaPrgRisultato.equals("")) {
					if (RitornaPrgRisultato.equalsIgnoreCase(prgRisultato)) {
						// stile = "style=\"background-color:gold\"";
						stile = "style=\"background-color:#99FFFF\"";
					}
				}

				boolean aperta = (open != null ? open.booleanValue() : ProcessorsUtils.isError(record));
				String tendinaRecord = "<a name=\"RITORNA_PRGRISULTATO_" + prgRisultato
						+ "\"></a><div class='sezione2' " + stile + " id='sezioneRisultato" + i + "'>\n"
						+ " <img id='tendinaRisultato" + i + "' alt='" + (aperta ? "Chiudi" : "Apri") + "' src='"
						+ (aperta ? "../../img/aperto.gif" : "../../img/chiuso.gif")
						+ "' onclick='cambia(this, document.getElementById(\"listaRisultato" + i + "\"));'/>\n";

				if (ProcessorsUtils.isWarning(record)) {
					colorePunto = "luceGialla";
				}

				// Dati per identificazione del movimento se presenti
				if (idMob != null) {
					if (!strNumAnnoProt.equals("")) {
						tendinaRecord += " -PR: " + strNumAnnoProt + " -";
					}
					tendinaRecord += "Tipo Mob: " + codTipoMob + " - Azienda: " + ragsocaz + " - Lavoratore: "
							+ cognomeLav + "&nbsp;" + nomeLav + "&nbsp;&nbsp;";
					if (!strCFlavoratore.equals("")) {
						tendinaRecord += strCFlavoratore;
					}
				} else {
					tendinaRecord += "Dettagli dell'elaborazione&nbsp;&nbsp;";
				}

				// Errore nella scrittura del log se presente
				String strErrorLog = "";
				if (errorLogCode != null) {
					strErrorLog = "<li style=\"list-style-image: none\"><b>ATTENZIONE</b>:&nbsp;"
							+ MessageBundle.getMessage(errorLogCode) + "</li>\n";
				}

				tendinaRecord = tendinaRecord + "<br>\n </div>\n";
				String listaRecord = "<div style='display: " + (aperta ? "inline" : "none") + ";' id='listaRisultato"
						+ i + "'><ul style=\"list-style-image: none\">" + righeRecord + strErrorLog + "</ul></div>\n";

				// righe = righe + "<li style=\"list-style-image:
				// url(../../img/" + colorePunto + ")\">" + tendinaRecord +
				// listaRecord + "</li>\n";
				if (colorare) {
					righe = righe + "<li class=\"" + colorePunto + "\">" + tendinaRecord + listaRecord + "</li>\n";
				} else {
					righe = righe + "<li>" + tendinaRecord + listaRecord + "</li>\n";
				}

				// aggiungo l'indicazione se la sezione è aperta o chiusa
				risultato.append(righe);
				risultato.append("<script language=\"javascript\">document.getElementById(\"listaRisultato" + i
						+ "\").aperta = " + (aperta ? "true" : "false") + "</script>");

			}
		}
		if (risultato == null)
			risultato = new StringBuffer("<ul>\n");
		risultato.append("</ul>\n");
		// ritorno la lista con i risultati
		// return "<ul>\n" + righe + "</ul>\n";
		return risultato.toString();
	}

	/**
	 * Metodo per la formattazione degli alert presenti nei risultati
	 */
	public static String showAlert(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}
		List records = result.getAttributeAsVector("RECORD");
		Iterator i = records.iterator();
		int dimBuffer = (records.size() * 100 > 1000000) ? 1000000 : (records.size() * 100);
		StringBuffer alerts = new StringBuffer(dimBuffer);
		// Ciclo sui risultati
		while (i.hasNext()) {
			SourceBean record = (SourceBean) i.next();
			// estraggo il vettore dei processori per il record ed il risultato
			List processors = record.getAttributeAsVector("PROCESSOR");
			boolean verificatoErrore = ((String) record.getAttribute("RESULT")).equalsIgnoreCase("ERROR");
			Iterator j = processors.iterator();
			// Ciclo sui processor di ogni risultato
			while (j.hasNext()) {
				SourceBean processor = (SourceBean) j.next();
				// Recupero tutti gli alert
				List alertList = processor.getAttributeAsVector("ALERT");
				alerts.append(getAlert(alertList, verificatoErrore));

			}
		}
		return alerts.toString();
	}

	/**
	 * Metodo per la formattazione dei confirm presenti nei risultati
	 */
	public static String showConfirm(SourceBean result) throws SourceBeanException {
		if (result == null) {
			throw new SourceBeanException("Risposta nulla!");
		}

		List records = result.getAttributeAsVector("RECORD");
		Iterator i = records.iterator();
		int dimBuffer = (records.size() * 100 > 1000000) ? 1000000 : (records.size() * 100);
		StringBuffer confirms = new StringBuffer(dimBuffer);
		// Ciclo sui risultati
		while (i.hasNext()) {
			SourceBean record = (SourceBean) i.next();
			// estraggo il vettore dei processori per il record ed il risultato
			List processors = record.getAttributeAsVector("PROCESSOR");
			boolean verificatoErrore = ((String) record.getAttribute("RESULT")).equalsIgnoreCase("ERROR");
			Iterator j = processors.iterator();
			// Ciclo sui processor di ogni risultato
			while (j.hasNext()) {
				SourceBean processor = (SourceBean) j.next();
				// Recupero tutti i confirm
				List confirmList = processor.getAttributeAsVector("CONFIRM");
				confirms.append(getConfirm(confirmList, verificatoErrore));
			}
		}
		return confirms.toString();
	}

	/**
	 * Metodo privato per la generazione degli elementi nel risultato di ogni processazione
	 */
	private static String generaElementiLista(List processors) {

		String lista = "";

		// Scorro il vettore
		Iterator iterator = processors.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			// Prendo il tag processor
			SourceBean processor = (SourceBean) iterator.next();
			// Estraggo i dati generali del processore
			String result = (String) processor.getAttribute("RESULT");
			String name = (String) processor.getAttribute("name");
			String classname = (String) processor.getAttribute("class");

			// Recupero tutti i warning
			List warn = processor.getAttributeAsVector("WARNING");

			// Se il risultato è un errore ne estraggo l'eventuale messaggio e
			// il codice
			String errorText = null;
			String errorCode = null;
			if (result.equalsIgnoreCase("ERROR")) {
				errorText = StringUtils.getAttributeStrNotNull(processor, "ERROR.dettaglio");
				if (processor.containsAttribute("ERROR.code")) {
					errorCode = processor.getAttribute("ERROR.code").toString();
				}
			}

			String sottolista = "";

			// Creo le sottorighe per le warning di questo processor
			if (warn != null && (warn.size() > 0)) {
				Iterator iter = warn.iterator();
				while (iter.hasNext()) {
					// Estraggo la warning e aggiungo la riga
					SourceBean wg = (SourceBean) iter.next();
					if (!wg.getAttribute("code").equals("")) {
						sottolista = sottolista + "<li><b>ATTENZIONE</b>:&nbsp;"
								+ wg.getAttribute("messagecode").toString();
						if (!wg.getAttribute("dettaglio").equals("")) {
							// Eventuale sottomessaggio della warning
							sottolista = sottolista + "<br><ul><li><b>Dettagli</b>:&nbsp;"
									+ wg.getAttribute("dettaglio").toString() + "</li></ul>\n";
						}
						sottolista = sottolista + "</li>\n";
					}
				}
			}

			// Creo l'eventuale sottoriga di errore
			if (errorCode != null) {
				sottolista = sottolista + "<li><b>ERRORE</b>:&nbsp;" + MessageBundle.getMessage(errorCode);
				if (errorText != null && !errorText.equals("")) {
					// Sottoelemento per il testo del messaggio di errore
					sottolista = sottolista + "<br><li><b>Dettagli</b>:&nbsp;" + errorText + "</li>\n";
				}
				sottolista = sottolista + "</li>\n";
				sottolista = sottolista + "<li><strong>L&acute;ERRORE HA PROVOCATO L&acute;ANNULLAMENTO "
						+ "DELLE OPERAZIONI PRECEDENTI E IL BLOCCO DEI CONTROLLI SUCCESSIVI</strong></li><br/>\n";
			}
			lista = lista + sottolista;
		}
		return lista;
	}

	private static String generaElementiListaMobilita(List processors, int contesto) {
		String lista = "";
		switch (contesto) {
		case MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA:
			// Scorro il vettore
			Iterator iterator = processors.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				i++;
				// Prendo il tag processor
				SourceBean processor = (SourceBean) iterator.next();
				// Estraggo i dati generali del processore
				String result = (String) processor.getAttribute("RESULT");
				String name = (String) processor.getAttribute("name");
				// Recupero tutti i warning
				List warn = processor.getAttributeAsVector("WARNING");
				// Se il risultato è un errore ne estraggo l'eventuale messaggio
				// e il codice
				String errorText = null;
				String errorCode = null;
				if (result.equalsIgnoreCase("ERROR")) {
					errorText = StringUtils.getAttributeStrNotNull(processor, "ERROR.dettaglio");
					if (processor.containsAttribute("ERROR.code")) {
						errorCode = processor.getAttribute("ERROR.code").toString();
					}
				}
				String sottolista = "";
				// Creo le sottorighe per le warning di questo processor
				if (warn != null && (warn.size() > 0)) {
					Iterator iter = warn.iterator();
					while (iter.hasNext()) {
						// Estraggo la warning e aggiungo la riga
						SourceBean wg = (SourceBean) iter.next();
						if (!wg.getAttribute("code").equals("")) {
							sottolista = sottolista + "<li><b>ATTENZIONE</b>:&nbsp;"
									+ (wg.containsAttribute("messagecode") ? wg.getAttribute("messagecode").toString()
											: "");
							if (!wg.getAttribute("dettaglio").equals("")) {
								// Eventuale sottomessaggio della warning
								sottolista = sottolista + "<br><ul><li><b>Dettagli</b>:&nbsp;"
										+ wg.getAttribute("dettaglio").toString() + "</li></ul>\n";
							}
							sottolista = sottolista + "</li>\n";
						}
					}
				}
				// Creo l'eventuale sottoriga di errore
				if (errorCode != null) {
					sottolista = sottolista + "<li><b>ERRORE</b>:&nbsp;" + MessageBundle.getMessage(errorCode);
					if (errorText != null && !errorText.equals("")) {
						// Sottoelemento per il testo del messaggio di errore
						sottolista = sottolista + "<br><li><b>Dettagli</b>:&nbsp;" + errorText + "</li>\n";
					}
					sottolista = sottolista + "</li>\n";
					sottolista = sottolista + "<li><strong>L&acute;ERRORE HA PROVOCATO L&acute;ANNULLAMENTO "
							+ "DELLE OPERAZIONI PRECEDENTI E IL BLOCCO DEI CONTROLLI SUCCESSIVI</strong></li><br/>\n";
				}
				lista = lista + sottolista;
			}
		}
		return lista;
	}

	private static String generaElementiListaProspetti(List processors) {
		String lista = "";

		Iterator iterator = processors.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			// Prendo il tag processor
			SourceBean processor = (SourceBean) iterator.next();
			// Estraggo i dati generali del processore
			String result = (String) processor.getAttribute("RESULT");
			// Recupero tutti i warning
			List warn = processor.getAttributeAsVector("WARNING");
			// Se il risultato è un errore ne estraggo l'eventuale messaggio
			// e il codice
			String errorText = null;
			String errorCode = null;
			if (result.equalsIgnoreCase("ERROR")) {
				errorText = StringUtils.getAttributeStrNotNull(processor, "ERROR.dettaglio");
				if (processor.containsAttribute("ERROR.code")) {
					errorCode = processor.getAttribute("ERROR.code").toString();
				}
			}
			String sottolista = "";
			// Creo le sottorighe per le warning di questo processor
			if (warn != null && (warn.size() > 0)) {
				Iterator iter = warn.iterator();
				while (iter.hasNext()) {
					// Estraggo la warning e aggiungo la riga
					SourceBean wg = (SourceBean) iter.next();
					if (!wg.getAttribute("code").equals("")) {
						sottolista = sottolista + "<li><b>ATTENZIONE</b>"
								+ (wg.containsAttribute("messagecode")
										? (":&nbsp;" + wg.getAttribute("messagecode").toString())
										: "");
						if (!wg.getAttribute("dettaglio").equals("")) {
							// Eventuale sottomessaggio della warning
							sottolista = sottolista + "<br><ul><li><b>Dettagli</b>:&nbsp;"
									+ wg.getAttribute("dettaglio").toString() + "</li></ul>\n";
						}
						sottolista = sottolista + "</li>\n";
					}
				}
			}
			// Creo l'eventuale sottoriga di errore
			if (errorCode != null) {
				sottolista = sottolista + "<li><b>ERRORE</b>:&nbsp;" + errorText;
				sottolista = sottolista + "</li>\n";
				sottolista = sottolista + "<li><strong>L&acute;ERRORE HA PROVOCATO L&acute;ANNULLAMENTO "
						+ "DELLE OPERAZIONI PRECEDENTI E IL BLOCCO DEI CONTROLLI SUCCESSIVI</strong></li><br/>\n";
			}
			lista = lista + sottolista;
		}
		return lista;
	}

	/**
	 * Metodo per la creazione dello script per la visualizzazione dell'alert
	 * 
	 * @param alerts,
	 *            lista contenente tutti gli alert da visualizzare
	 * @param verificatoErrore
	 *            indica se si è verificato un errore sul processor
	 * @return la stringa rappresentante lo script per la visualizzazione dell'alert
	 */
	private static String getAlert(List alerts, boolean verificatoErrore) {
		Boolean showIfErrors = null;
		String script = "<script language=\"javascript\">";
		String testo = "";
		for (int j = 0; j < alerts.size(); j++) {
			script = script + "\n";
			SourceBean alert = (SourceBean) alerts.get(j);
			if (verificatoErrore) {
				showIfErrors = new Boolean((String) alert.getAttribute("showiferrors"));
				if (!showIfErrors.booleanValue()) {
					// Da non mostrare in caso di errori
					continue;
				}
			}
			testo = (String) alert.getAttribute("text");
			script = script + "alert(\"" + testo + "\");";
		} // for(int j=0; j<alerts.size(); j++)
		script = script + "\n </script>";
		return script;
	}

	/**
	 * Metodo per la creazione dello script per la visualizzazione delle confirm
	 * 
	 * @param confirms,
	 *            vettore contenente tutti i confirm da visualizzare
	 * @return la stringa rappresentante lo script per la visualizzazione dei confirm
	 */
	private static String getConfirm(List confirms, boolean verificatoErrore) {
		Boolean showIfErrors = null;
		String script = "\n<script language=\"javascript\">";
		String testo = "";
		String funzioneJs = "";
		Vector params = null;
		String parametri = "";
		for (int j = 0; j < confirms.size(); j++) {
			script = script + "\n var esito" + j + " = false; \n";
			SourceBean confirm = (SourceBean) confirms.get(j);
			testo = (String) confirm.getAttribute("text");
			if (verificatoErrore) {
				showIfErrors = new Boolean((String) confirm.getAttribute("showiferrors"));
				if (!showIfErrors.booleanValue()) {
					// Da non mostrare in caso di errori
					continue;
				}
			}
			funzioneJs = (String) confirm.getAttribute("jsfunction");
			params = confirm.getAttributeAsVector("PARAM");
			if ((params != null) && (params.size() > 0)) {
				for (int i = 0; i < params.size(); i++) {
					SourceBean sb = (SourceBean) params.get(i);
					if (parametri.equals("")) {
						parametri = "\"" + (String) sb.getAttribute("value") + "\"";
					} else {
						parametri = parametri + ",\"" + (String) sb.getAttribute("value") + "\"";
					}
				} // for(int i=0; i<params.size(); i++)
			} // if((params!=null) && (params.size()>0))
			script = script + "\n esito" + j + " = confirm(\"";
			testo = StringUtils.replace(testo, "\"", "\\\"");
			script = script + testo;
			script = script + "\");";

			script = script + "\n if (esito" + j + ") ";
			script = script + "{\n";
			script = script + funzioneJs + "(" + parametri + ");";
			script = script + "\n}";
		} // for(int j=0; j<confirmss.size(); j++)
		script = script + "\n</script>";
		return script;
	}

	/**
	 * Genera una versione semplificata dei risultati per l'importazione dei movimenti via POST dal SARE
	 * <p>
	 * 
	 * @param result
	 *            risultato della processazione
	 */
	public static String generaXML(SourceBean result) {
		if (result == null) {
			return "";
		}
		Vector records = result.getAttributeAsVector("RECORD");
		StringBuffer buf = new StringBuffer();

		// Genero l'XML per i records
		for (int i = 0; i < records.size(); i++) {
			buf.append(generaXMLRecord((SourceBean) records.get(i)));
		}

		// Aggiungo l'intestazione
		String header = "<RECORDS processed='" + result.getAttribute("PROCESSED") + "' CORRECTPROCESSED='"
				+ result.getAttribute("CORRECTPROCESSED") + "' WARNINGPROCESSED='"
				+ result.getAttribute("WARNINGPROCESSED") + "' ERRORPROCESSED='" + result.getAttribute("ERRORPROCESSED")
				+ "' WARNINGTOTAL='" + result.getAttribute("WARNINGTOTAL") + "'>";
		String footer = "</RECORDS>";
		String total = header + buf.toString() + footer;
		return total;
	}

	/**
	 * Genera l'XML per i record
	 */
	private static String generaXMLRecord(SourceBean record) {

		// estraggo il vettore dei processori per il record, il risultato e il
		// numero del record
		Vector processors = record.getAttributeAsVector("PROCESSOR");
		String recordResult = StringUtils.getAttributeStrNotNull(record, "RESULT");
		Integer recordNumber = (Integer) record.getAttribute("RECORDID");
		String header = "<RECORD RECORDID='" + (recordNumber != null ? recordNumber.toString() : "") + "' result='"
				+ recordResult + "'>";
		String proc = "";
		if (processors != null && (processors.size() > 0)) {
			// Creo l'XML per i processor
			proc = generaXMLProcessor(processors);
		}
		String footer = "</RECORD>";
		String total = header + proc + footer;
		return total;
	}

	/**
	 * Genera l'XML per i processori
	 */
	private static String generaXMLProcessor(AbstractList processors) {
		StringBuffer buf = new StringBuffer();

		// Per ogni Warning o errore contenuto aggiungo il corrispondente tag
		for (int i = 0; i < processors.size(); i++) {
			SourceBean processor = (SourceBean) processors.get(i);

			// Estraggo l'eventuale messaggio di errore
			if (StringUtils.getAttributeStrNotNull(processor, "RESULT").equalsIgnoreCase("ERROR")) {
				SourceBean errorSB = (SourceBean) processor.getAttribute("ERROR");
				if (errorSB != null) {
					String errorDetail = StringUtils.getAttributeStrNotNull(errorSB, "dettaglio");
					buf.append("<ERROR>" + StringUtils.getAttributeStrNotNull(errorSB, "messagecode")
							+ ((!errorDetail.equals("")) ? "<DETAIL>" + errorDetail + "</DETAIL>" : "") + "</ERROR>");
				}
			}

			// Creo i tag per le Warnings
			Vector warnings = processor.getAttributeAsVector("WARNING");
			if (warnings != null && (warnings.size() > 0)) {
				Iterator iter = warnings.iterator();
				while (iter.hasNext()) {
					// Estraggo la warning e aggiungo il tag
					SourceBean warningSB = (SourceBean) iter.next();
					if (warningSB != null) {
						String warningDetail = StringUtils.getAttributeStrNotNull(warningSB, "dettaglio");
						buf.append("<WARNING>" + StringUtils.getAttributeStrNotNull(warningSB, "messagecode")
								+ ((!warningDetail.equals("")) ? "<DETAIL>" + warningDetail + "</DETAIL>" : "")
								+ "</WARNING>");
					}
				}
			}
		}
		String total = buf.toString();
		return total;
	}
}