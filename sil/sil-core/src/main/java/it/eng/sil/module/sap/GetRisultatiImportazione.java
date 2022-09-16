package it.eng.sil.module.sap;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class GetRisultatiImportazione extends AbstractSimpleModule {

	private Integer currentPage = null;
	private static final int numElementiPage = 20;
	private Integer numPagesProcessate = null;
	private Integer numeroDa = null;
	private Integer numeroA = null;
	private Integer processedTotal = null;

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		SourceBean rows = null;
		if (request.containsAttribute("IMPORTA") && request.containsAttribute("PROGRESSIVO")) {
			this.currentPage = new Integer(1);
			this.setSectionQuerySelect("QUERY_SELECT_IMPORTAZIONE_CORRENTE");
			rows = doSelect(request, response);
		} else {
			if (request.containsAttribute("VISUALIZZARISULTATI")) {
				if (request.containsAttribute("NUMBERPAGECURRENT")) {
					this.currentPage = new Integer(request.getAttribute("NUMBERPAGECURRENT").toString());
				} else {
					this.currentPage = new Integer(1);
				}
				this.setSectionQuerySelect("QUERY_SELECT_ULTIMA_IMPORTAZIONE");
				rows = doSelect(request, response);
			}
		}
		StringBuffer risultato = null;
		String sommario = null;
		String processed = "0";
		int numErroriProcessed = 0;
		int numOKProcessed = 0;
		int numFileProcessati = 0;
		Vector<SourceBean> detail = null;
		if (rows != null) {
			detail = rows.getAttributeAsVector("ROW");
			if (detail != null && detail.size() > 0) {
				numFileProcessati = detail.size();
				risultato = new StringBuffer();
				SourceBean row = (SourceBean) detail.get(0);
				processed = row.containsAttribute("numfile") ? row.getAttribute("numfile").toString() : "";
				processedTotal = new Integer(processed);
				// int numeroPages = (new Integer(processed).intValue())/numElementiPage;
				int numeroPagesProcessate = numFileProcessati / numElementiPage;
				// int resto = (new Integer(processed).intValue())%numElementiPage;
				// if (resto > 0) {
				// numeroPages = numeroPages + 1;
				// }
				int restoProcessate = numFileProcessati % numElementiPage;
				if (restoProcessate > 0) {
					numeroPagesProcessate = numeroPagesProcessate + 1;
				}
				// this.numPages = new Integer(numeroPages);
				this.numPagesProcessate = new Integer(numeroPagesProcessate);
				int numRecordDa = ((currentPage.intValue() - 1) * numElementiPage) + 1;
				int numRecordA = (numRecordDa + numElementiPage) - 1;
				if (numRecordA > processedTotal.intValue()) {
					numRecordA = processedTotal.intValue();
				}
				this.numeroDa = new Integer(numRecordDa);
				this.numeroA = new Integer(numRecordA);

				for (int i = 0; i < numFileProcessati; i++) {
					if ((i + 1) >= numeroDa.intValue() && (i + 1) <= numeroA.intValue()) {
						risultato.append("<ul>\n");
						SourceBean rowCurr = (SourceBean) detail.get(i);
						String codErrore = rowCurr.containsAttribute("coderrore")
								? rowCurr.getAttribute("coderrore").toString()
								: "";
						String fglAnagPresente = rowCurr.containsAttribute("flganagpresente")
								? rowCurr.getAttribute("flganagpresente").toString()
								: "";
						String flgDidPresente = rowCurr.containsAttribute("flgdidpresente")
								? rowCurr.getAttribute("flgdidpresente").toString()
								: "";
						String numEsperienze = rowCurr.containsAttribute("numesperienzesap")
								? rowCurr.getAttribute("numesperienzesap").toString()
								: "";
						String numEsperienzeImp = rowCurr.containsAttribute("numesperienzeimp")
								? rowCurr.getAttribute("numesperienzeimp").toString()
								: "";
						String numEsperienzeNonImp = rowCurr.containsAttribute("numesperienzenonimp")
								? rowCurr.getAttribute("numesperienzenonimp").toString()
								: "";
						String cfLav = rowCurr.containsAttribute("strcodicefiscale")
								? rowCurr.getAttribute("strcodicefiscale").toString()
								: "";
						String strEsito = rowCurr.containsAttribute("stresito")
								? rowCurr.getAttribute("stresito").toString()
								: "";

						if (codErrore.equalsIgnoreCase("01") || codErrore.equalsIgnoreCase("99")) {
							numErroriProcessed = numErroriProcessed + 1;
						} else {
							numOKProcessed = numOKProcessed + 1;
						}

						String righe = "";
						boolean aperta = false;
						String colorePunto = "luceVerde";

						String nomeFile = StringUtils.getAttributeStrNotNull(rowCurr, "strnomefile");
						String righeRecord = "";
						String sottolista = "";
						String dettaglioImportazione = "";

						if (codErrore.equalsIgnoreCase("01")) {
							dettaglioImportazione = dettaglioImportazione + "<li><b>ERRORE</b>:&nbsp;"
									+ "Importazione sezione dati anagrafici fallita" + "</li>\n";
							dettaglioImportazione = dettaglioImportazione
									+ "<li><strong>L&acute;ERRORE HA PROVOCATO L&acute;ANNULLAMENTO "
									+ "DELL&acute;OPERAZIONE</strong></li>";
							colorePunto = "luceRossa";
							aperta = true;
						} else {
							if (codErrore.equalsIgnoreCase("99")) {
								dettaglioImportazione = dettaglioImportazione + "<li><b>ERRORE</b>:&nbsp;"
										+ "Dati anagrafici e/o obbligatori mancanti" + "</li>\n";
								dettaglioImportazione = dettaglioImportazione
										+ "<li><strong>L&acute;ERRORE HA PROVOCATO L&acute;ANNULLAMENTO "
										+ "DELL&acute;OPERAZIONE</strong></li>";
								colorePunto = "luceRossa";
								aperta = true;
							} else {
								if (fglAnagPresente.equalsIgnoreCase("S")) {
									dettaglioImportazione = "<li><b>Anagrafica gia&acute;presente</b></li>\n";
								} else {
									dettaglioImportazione = "<li><b>Anagrafica inserita</b></li>\n";
								}
								if (!codErrore.equalsIgnoreCase("00")) {
									colorePunto = "luceGialla";
									if (codErrore.equalsIgnoreCase("02")) {
										dettaglioImportazione = dettaglioImportazione + "<li><b>ATTENZIONE</b>:&nbsp;"
												+ "Importazione sezione dati amministrativi fallita" + "</li>\n";
									} else {
										if (flgDidPresente.equalsIgnoreCase("S")) {
											dettaglioImportazione = dettaglioImportazione
													+ "<li><b>Dichiarazione immediata disponibilita&acute;presente</b></li>\n";
										} else {
											if (flgDidPresente.equalsIgnoreCase("N")) {
												dettaglioImportazione = dettaglioImportazione
														+ "<li><b>Dichiarazione immediata disponibilita&acute;inserita</b></li>\n";
											} else {
												dettaglioImportazione = dettaglioImportazione
														+ "<li><b>Data dichiarazione disponibilita&acute;non presente</b></li>\n";
											}
										}
									}

								}
								dettaglioImportazione = dettaglioImportazione + "<li><b>Esperienze presenti nella SAP "
										+ numEsperienze;
								dettaglioImportazione = dettaglioImportazione + " - Esperienze importate "
										+ numEsperienzeImp;
								dettaglioImportazione = dettaglioImportazione + " - Esperienze non importate "
										+ numEsperienzeNonImp;
								dettaglioImportazione = dettaglioImportazione + "</b></li>\n";
							}
						}

						if (!strEsito.equals("")) {
							dettaglioImportazione = dettaglioImportazione + "<b>AVVISO:&nbsp;" + strEsito + "</b>\n";
						}
						sottolista = dettaglioImportazione;

						righeRecord = righeRecord + sottolista;
						String stile = "style=\"background-color:#99FFFF\"";

						String tendinaRecord = "<div class='sezione2' " + stile + " id='sezioneRisultato" + i + "'>\n"
								+ " <img id='tendinaRisultato" + i + "' alt='" + (aperta ? "Chiudi" : "Apri")
								+ "' src='" + (aperta ? "../../img/aperto.gif" : "../../img/chiuso.gif")
								+ "' onclick='cambia(this, document.getElementById(\"listaRisultato" + i + "\"));'/>\n";

						tendinaRecord += " File: " + nomeFile + " - Identificativo lavoratore: " + cfLav;
						tendinaRecord = tendinaRecord + "<br>\n </div>\n";

						String listaRecord = "<div style='display: " + (aperta ? "inline" : "none")
								+ ";' id='listaRisultato" + i + "'><ul style=\"list-style-image: none\">" + righeRecord
								+ "</ul></div>\n";

						righe = righe + "<li class=\"" + colorePunto + "\">" + tendinaRecord + listaRecord + "</li>\n";

						risultato.append(righe);
						risultato.append("<script language=\"javascript\">document.getElementById(\"listaRisultato" + i
								+ "\").aperta = " + (aperta ? "true" : "false") + "</script>");

						risultato.append("</ul>\n");
					} else {
						SourceBean rowCurr = (SourceBean) detail.get(i);
						String codErrore = rowCurr.containsAttribute("coderrore")
								? rowCurr.getAttribute("coderrore").toString()
								: "";
						if (codErrore.equalsIgnoreCase("01") || codErrore.equalsIgnoreCase("99")) {
							numErroriProcessed = numErroriProcessed + 1;
						} else {
							numOKProcessed = numOKProcessed + 1;
						}
					}
				}
			}
		}
		if (risultato == null) {
			risultato = new StringBuffer();
			risultato.append("<ul>\n<p class=\"titolo\">Nessuna informazione disponibile sui record processati<p/>");
			risultato.append("</ul>\n");
		}
		sommario = "<p class=\"titolo\">Numero di file processati: " + numFileProcessati + " (di "
				+ processedTotal.intValue() + ")<br/>\n" + "File importati: " + numOKProcessed + "<br/>\n"
				+ "File non importati: " + numErroriProcessed + "</p>\n";

		String pulsantiNavigazione = showPulsantiImportazione(numFileProcessati);

		response.setAttribute("SOMMARIO", sommario);
		response.setAttribute("PULSANTINAVIGAZIONE", pulsantiNavigazione);
		response.setAttribute("RISULTATO", risultato.toString());
	}

	public String showPulsantiImportazione(int numFileProcessati) throws SourceBeanException {
		String pulsanti = new String();
		int numeroPaginaPrecedente = 0;
		int numeroPaginaSuccessiva = 0;
		int numeroPaginaLast = numPagesProcessate.intValue();
		if (currentPage.intValue() > 1) {
			numeroPaginaPrecedente = currentPage.intValue() - 1;
		} else {
			numeroPaginaPrecedente = currentPage.intValue();
		}
		if (currentPage.intValue() < numPagesProcessate.intValue()) {
			numeroPaginaSuccessiva = currentPage.intValue() + 1;
		} else {
			numeroPaginaSuccessiva = currentPage.intValue();
		}

		pulsanti += "<img src=\"../../img/list_first.gif\" alt=\"Prima pagina\" title=\"Prima pagina\" onclick=\"getPage(1);\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_prev.gif\" alt=\"Pagina precedente\" title=\"Pagina precedente\" onclick=\"getPage("
				+ numeroPaginaPrecedente + ");\"/>&nbsp;";
		boolean aggiorna = false;

		if (processedTotal.intValue() > numFileProcessati) {
			aggiorna = true;
		}

		if (aggiorna) {
			pulsanti += "<img src=\"../../img/add.gif\" alt=\"Aggiorna risultati\" title=\"Aggiorna risultati\" onclick=\"getPage("
					+ currentPage.intValue() + ");\"/>&nbsp;";
		}

		pulsanti += "<img src=\"../../img/list_next.gif\" alt=\"Pagina successiva\" title=\"Pagina successiva\" onclick=\"getPage("
				+ numeroPaginaSuccessiva + ");\"/>&nbsp;";
		pulsanti += "<img src=\"../../img/list_last.gif\" alt=\"Ultima pagina\" title=\"Ultima pagina\" onclick=\"getPage("
				+ numeroPaginaLast + ");\"/>&nbsp;";
		// Pulsante "VAI A PAG"
		if (numPagesProcessate.intValue() > 1) {
			pulsanti += "&nbsp;<select name=\"selPage\" onChange=\"allineaSelPageIndex(this.selectedIndex);\">";
			for (int i = 1; i <= numPagesProcessate.intValue(); i++) {
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

		int numFileA = numeroA;
		if (numFileA > numFileProcessati) {
			numFileA = numFileProcessati;
		}
		pulsanti += "<p class=\"titolo\"><b>Pag. " + currentPage + " di " + numPagesProcessate + " (da " + numeroDa
				+ " a " + numFileA + " di " + numFileProcessati + ")</b></p>";
		return pulsanti;
	}

}
