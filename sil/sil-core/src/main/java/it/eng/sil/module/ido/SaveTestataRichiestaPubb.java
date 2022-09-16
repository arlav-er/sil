package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.Sottosistema;

public class SaveTestataRichiestaPubb extends AbstractSimpleModule {
	final int LUNGHEZZA_CAMPO = 2000;

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean errors = false;
		boolean flag_prevalorizza = false;

		if (request.containsAttribute("salva")) {
			String strSalva = request.getAttribute("salva").toString();
			flag_prevalorizza = strSalva.equalsIgnoreCase("PREVALORIZZA");
		}

		if (flag_prevalorizza) {
			String codMonoCMcatPubb = StringUtils.getAttributeStrNotNull(request, "codMonoCMcatPubb");
			String strDatiAziendaPubb = StringUtils.getAttributeStrNotNull(request, "strDatiAziendaPubb");
			String strMansionePubb = StringUtils.getAttributeStrNotNull(request, "strMansionePubb");
			String strLuogoLavoro = StringUtils.getAttributeStrNotNull(request, "strLuogoLavoro");
			String strFormazionePubb = StringUtils.getAttributeStrNotNull(request, "strFormazionePubb");
			String txtCondContrattuale = StringUtils.getAttributeStrNotNull(request, "txtCondContrattuale");
			String strConoscenzePubb = StringUtils.getAttributeStrNotNull(request, "strConoscenzePubb");
			String txtCaratteristFigProf = StringUtils.getAttributeStrNotNull(request, "txtCaratteristFigProf");
			String strNoteOrarioPubb = StringUtils.getAttributeStrNotNull(request, "strNoteOrarioPubb");
			String strRifCandidaturaPubb = StringUtils.getAttributeStrNotNull(request, "strRifCandidaturaPubb");
			String datPubblicazione = StringUtils.getAttributeStrNotNull(request, "datPubblicazione");
			String datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(request, "datScadenzaPubblicazione");

			Vector mansioneVec = null;
			Vector comuneVec = null;
			Vector provinciaVec = null;

			String strCognomeRifPubb = StringUtils.getAttributeStrNotNull(request, "strCognomeRifPubb");
			String strNomeRifPubb = StringUtils.getAttributeStrNotNull(request, "strNomeRifPubb");
			String strTelRifPubb = StringUtils.getAttributeStrNotNull(request, "strTelRifPubb");
			String strFaxRifPubb = StringUtils.getAttributeStrNotNull(request, "strFaxRifPubb");
			String strEmailRifPubb = StringUtils.getAttributeStrNotNull(request, "strEmailRifPubb");

			String codEvasione = StringUtils.getAttributeStrNotNull(request, "codEvasione");
			boolean flgPubbPalese = (codEvasione.equalsIgnoreCase("DFD") || codEvasione.equalsIgnoreCase("DPR"));
			boolean flgPubbAnonima = (codEvasione.equalsIgnoreCase("DFA") || codEvasione.equalsIgnoreCase("DRA"));

			ResponseContainer rc = this.getResponseContainer();
			SourceBean serviceResponse = rc.getServiceResponse();

			/* prelevo codice regione */
			boolean checkRER = false;
			if (request.containsAttribute("strcheckRER")) {
				String strcheckRER = request.getAttribute("strcheckRER").toString();
				checkRER = strcheckRER.equalsIgnoreCase("1");
			}
			/**/
			boolean isFlussoVacancy = false;
			String numConfigFlusso = serviceResponse.containsAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM")
					? serviceResponse.getAttribute("M_CONFIG_FLUSSO_VACANCY.ROWS.ROW.NUM").toString()
					: Properties.DEFAULT_CONFIG;
			if (Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigFlusso)) {
				isFlussoVacancy = true;
			}

			SourceBean numGg = (SourceBean) serviceResponse.getAttribute("M_GetNumGgPubb.ROWS.ROW");
			int numGPubb = 0;
			GregorianCalendar dataPubb = new GregorianCalendar();
			GregorianCalendar dataCh = new GregorianCalendar();
			SimpleDateFormat df = new SimpleDateFormat(DateUtils.FORMATO_DATA);

			if (numGg != null) {
				numGPubb = Integer.parseInt(numGg.getAttribute("NUM").toString());
			}

			// controllo la data di pubblicazione. Se è nulla, la setto ad oggi
			if (datPubblicazione.equals("")) {
				datPubblicazione = df.format(dataPubb.getTime());
			}

			// INIT-PARTE-TEMP
			if (Sottosistema.CM.isOff()) {

				if (codEvasione.equals("AS")) {
					String dataChiam = "";
					SourceBean ric = null;
					ric = (SourceBean) serviceResponse.getAttribute("M_GetDataChiamataPubb.ROWS.ROW");
					dataChiam = StringUtils.getAttributeStrNotNull(ric, "DATCHIAMATA");

					// se la data chiamata è minore della data di pubblicazione e la data scadenza non è valorizzata
					// viene settata come data scadenza la data di pubblicazione
					if (datScadenzaPubblicazione == null || ("").equals(datScadenzaPubblicazione)) {
						dataPubb = new GregorianCalendar(Integer.parseInt(datPubblicazione.substring(6, 10)),
								Integer.parseInt(datPubblicazione.substring(3, 5)) - 1,
								Integer.parseInt(datPubblicazione.substring(0, 2)));
						dataCh = new GregorianCalendar(Integer.parseInt(dataChiam.substring(6, 10)),
								Integer.parseInt(dataChiam.substring(3, 5)) - 1,
								Integer.parseInt(dataChiam.substring(0, 2)));
						if (dataCh.before(dataPubb)) {
							request.updAttribute("datScadenzaPubblicazione", datPubblicazione);
						} else {
							request.updAttribute("datScadenzaPubblicazione", dataChiam);
						}
					}

				} else {
					if (datScadenzaPubblicazione != null && datScadenzaPubblicazione.equals("")) {
						dataPubb = new GregorianCalendar(Integer.parseInt(datPubblicazione.substring(6, 10)),
								Integer.parseInt(datPubblicazione.substring(3, 5)) - 1,
								Integer.parseInt(datPubblicazione.substring(0, 2)));
						dataPubb.add(Calendar.DATE, numGPubb);
						datScadenzaPubblicazione = df.format(dataPubb.getTime());
						request.updAttribute("datScadenzaPubblicazione", datScadenzaPubblicazione);
					}
				}

			} else {
				// END-PARTE-TEMP

				if (codEvasione.equals("AS")) {
					String dataChiam = "";
					SourceBean ric = null;
					ric = (SourceBean) serviceResponse.getAttribute("M_GetDataChiamataPubb.ROWS.ROW");
					dataChiam = StringUtils.getAttributeStrNotNull(ric, "DATCHIAMATA");

					// se la data chiamata è minore della data di pubblicazione e la data scadenza non è valorizzata
					// viene settata come data scadenza la data di pubblicazione
					if (datScadenzaPubblicazione == null || ("").equals(datScadenzaPubblicazione)) {
						dataPubb = new GregorianCalendar(Integer.parseInt(datPubblicazione.substring(6, 10)),
								Integer.parseInt(datPubblicazione.substring(3, 5)) - 1,
								Integer.parseInt(datPubblicazione.substring(0, 2)));
						dataCh = new GregorianCalendar(Integer.parseInt(dataChiam.substring(6, 10)),
								Integer.parseInt(dataChiam.substring(3, 5)) - 1,
								Integer.parseInt(dataChiam.substring(0, 2)));
						if (dataCh.before(dataPubb)) {
							request.updAttribute("datScadenzaPubblicazione", datPubblicazione);
						} else {
							request.updAttribute("datScadenzaPubblicazione", dataChiam);
						}
					}

				} else if ("CMA".equalsIgnoreCase(codEvasione)) {
					String dataChiam = "";
					SourceBean ric = null;
					ric = (SourceBean) serviceResponse.getAttribute("M_GetDataChiamataCMPubb.ROWS.ROW");
					dataChiam = StringUtils.getAttributeStrNotNull(ric, "DATCHIAMATA");

					// se la data chiamata CM è minore della data di pubblicazione e la data scadenza non è valorizzata
					// viene settata come data scadenza la data di pubblicazione
					if (datScadenzaPubblicazione == null || ("").equals(datScadenzaPubblicazione)) {
						dataPubb = new GregorianCalendar(Integer.parseInt(datPubblicazione.substring(6, 10)),
								Integer.parseInt(datPubblicazione.substring(3, 5)) - 1,
								Integer.parseInt(datPubblicazione.substring(0, 2)));
						dataCh = new GregorianCalendar(Integer.parseInt(dataChiam.substring(6, 10)),
								Integer.parseInt(dataChiam.substring(3, 5)) - 1,
								Integer.parseInt(dataChiam.substring(0, 2)));
						if (dataCh.before(dataPubb)) {
							request.updAttribute("datScadenzaPubblicazione", datPubblicazione);
						} else {
							request.updAttribute("datScadenzaPubblicazione", dataChiam);
						}
					}

				} else {
					if (datScadenzaPubblicazione != null && datScadenzaPubblicazione.equals("")) {
						dataPubb = new GregorianCalendar(Integer.parseInt(datPubblicazione.substring(6, 10)),
								Integer.parseInt(datPubblicazione.substring(3, 5)) - 1,
								Integer.parseInt(datPubblicazione.substring(0, 2)));
						dataPubb.add(Calendar.DATE, numGPubb);
						datScadenzaPubblicazione = df.format(dataPubb.getTime());
						request.updAttribute("datScadenzaPubblicazione", datScadenzaPubblicazione);
					}
				}

				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			SourceBean cpiRow = (SourceBean) serviceResponse.getAttribute("M_GetInfoCPI.ROWS.ROW");
			String strDescrizioneCPI = StringUtils.getAttributeStrNotNull(cpiRow, "STRDESCRIZIONE");
			String strIndirizzoCPI = StringUtils.getAttributeStrNotNull(cpiRow, "STRINDIRIZZO");
			String strEmailCPI = StringUtils.getAttributeStrNotNull(cpiRow, "STREMAIL");
			String strFaxCPI = StringUtils.getAttributeStrNotNull(cpiRow, "STRFAX");

			SourceBean richiestaRow = (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW");
			String flgAutomunito = StringUtils.getAttributeStrNotNull(richiestaRow, "flgAutomunito");
			String flgMotomunito = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMotomunito");
			String strLocalitaDatiGenerali = StringUtils.getAttributeStrNotNull(richiestaRow, "strLocalita");
			String strLocalita = "";

			boolean datiAziendaPubb = true;
			if (flgPubbAnonima && isFlussoVacancy) {
				datiAziendaPubb = false;
			}

			if (datiAziendaPubb) {
				if (strDatiAziendaPubb != null && strDatiAziendaPubb.equals("")) {
					SourceBean datiAziendaRow = (SourceBean) serviceResponse
							.getAttribute("M_GetTestataAzienda.ROWS.ROW");
					if (datiAziendaRow != null) {
						strDatiAziendaPubb = StringUtils.getAttributeStrNotNull(datiAziendaRow, "STRRAGIONESOCIALE");
						strDatiAziendaPubb += " - ";
					}
					datiAziendaRow = (SourceBean) serviceResponse.getAttribute("M_GETUNITAAZIENDA.ROWS.ROW");
					if (datiAziendaRow != null) {
						strDatiAziendaPubb += StringUtils.getAttributeStrNotNull(datiAziendaRow, "STRDESATECO");
						strDatiAziendaPubb += " - ";
						strDatiAziendaPubb += StringUtils.getAttributeStrNotNull(datiAziendaRow, "STRINDIRIZZO");
						strDatiAziendaPubb += " ";
						strDatiAziendaPubb += StringUtils.getAttributeStrNotNull(datiAziendaRow, "STRLOCALITA");
						strDatiAziendaPubb += " ";
						strDatiAziendaPubb += StringUtils.getAttributeStrNotNull(datiAziendaRow, "STRCAP");
						strDatiAziendaPubb += " ";
						strLocalita = StringUtils.getAttributeStrNotNull(datiAziendaRow, "STRDENOMINAZIONE");
						strDatiAziendaPubb += strLocalita;
						strDatiAziendaPubb += " (";
						strDatiAziendaPubb += StringUtils.getAttributeStrNotNull(datiAziendaRow, "PROVINCIA");
						strDatiAziendaPubb += ")";
					}

					if (strDatiAziendaPubb.length() > LUNGHEZZA_CAMPO) {
						strDatiAziendaPubb = strDatiAziendaPubb.substring(0, LUNGHEZZA_CAMPO);
					}
					request.updAttribute("strDatiAziendaPubb", strDatiAziendaPubb);
				}
			} else {
				if (request.containsAttribute("strDatiAziendaPubb")) {
					request.delAttribute("strDatiAziendaPubb");
				}
			}

			// INIT-PARTE-TEMP
			if (Sottosistema.AS.isOff()) {

				if (strMansionePubb != null && strMansionePubb.equals("")) {
					mansioneVec = (Vector) serviceResponse.getAttributeAsVector("M_LISTIDOMANSIONIPUBB.ROWS.ROW");
					if (mansioneVec != null && !mansioneVec.isEmpty()) {
						for (int i = 0; i < mansioneVec.size(); i++) {
							SourceBean mansioneRow = (SourceBean) mansioneVec.elementAt(i);
							strMansionePubb += StringUtils.getAttributeStrNotNull(mansioneRow, "DESMANSIONE");
							strMansionePubb += "\r\n";
						}
					}
					if (strMansionePubb.length() > LUNGHEZZA_CAMPO) {
						strMansionePubb = strMansionePubb.substring(0, LUNGHEZZA_CAMPO);
					}
					request.updAttribute("strMansionePubb", strMansionePubb);
				}
			} else {
				// END-PARTE-TEMP
				if (codEvasione.equals("AS")) {
					String strMansionePubbAS = ""; // stringa composta dalle diverse mansioni dei diversi profili
					String mansione = ""; // variabile in cui memorizzare la descrizione della mansione
					BigDecimal alternativa = null; // indicativo dei profili
					int alterprec = 1; // valore iniziale dell'alternativa precedente

					// reperisco l'elenco di tutte le mansioni di tutti i profili (PRGALTERNATIVA)
					Vector richMans = serviceResponse.getAttributeAsVector("M_ListIdoMansioniPubbAS.ROWS.ROW");

					for (int i = 0; i < richMans.size(); i++) {

						SourceBean rowVector = (SourceBean) richMans.get(i);
						mansione = StringUtils.getAttributeStrNotNull(rowVector, "DESMANSIONE");
						alternativa = rowVector.getAttribute("PRGALTERNATIVA") != null
								? (BigDecimal) rowVector.getAttribute("PRGALTERNATIVA")
								: null;
						String subordine = "In subordine";

						String stralternativa = "";
						if (alternativa == null) {
							stralternativa = "";
						} else {
							stralternativa = alternativa.toString();
						}

						/*
						 * controllo per fa comparire la stringa "In subordine" nella lista delle mansioni il primo
						 * profilo rimane il principale, gli altri sono subordinati questo è valido solo per l'Art 16
						 * per fare ciò confronto l'alternativa con il precedente e se è diverso aggiungo "in subordine"
						 * *
						 */
						if (alterprec != alternativa.intValue()) {
							strMansionePubbAS += "\n" + subordine + "\n";
							alterprec = alternativa.intValue();
						}
						strMansionePubbAS += mansione + "\n"; // lista delle mansioni
						request.updAttribute("strMansionePubb", strMansionePubbAS); // memorizzo il parametro nella
																					// request
					}
				} else {
					if (strMansionePubb != null && strMansionePubb.equals("")) {
						mansioneVec = (Vector) serviceResponse.getAttributeAsVector("M_LISTIDOMANSIONIPUBB.ROWS.ROW");
						if (mansioneVec != null && !mansioneVec.isEmpty()) {
							for (int i = 0; i < mansioneVec.size(); i++) {
								SourceBean mansioneRow = (SourceBean) mansioneVec.elementAt(i);
								strMansionePubb += StringUtils.getAttributeStrNotNull(mansioneRow, "DESMANSIONE");
								strMansionePubb += "\r\n";
							}
						}
						if (strMansionePubb.length() > LUNGHEZZA_CAMPO) {
							strMansionePubb = strMansionePubb.substring(0, LUNGHEZZA_CAMPO);
						}
						request.updAttribute("strMansionePubb", strMansionePubb);
					}
				}

				// INIT-PARTE-TEMP
			}
			// END-PARTE-TEMP

			// contenuto e contesto del lavoro non prevalorizzato

			if (strLuogoLavoro != null && strLuogoLavoro.equals("")) {
				if (strLocalitaDatiGenerali != null && !strLocalitaDatiGenerali.equals("")) {
					strLuogoLavoro += "Località " + strLocalitaDatiGenerali;
					strLuogoLavoro += "\r\n";
				}
				comuneVec = (Vector) serviceResponse.getAttributeAsVector("MLISTATERRITORICOMUNIRICHIESTA.ROWS.ROW");
				if (comuneVec != null && !comuneVec.isEmpty()) {
					for (int i = 0; i < comuneVec.size(); i++) {
						SourceBean comuneRow = (SourceBean) comuneVec.elementAt(i);
						strLuogoLavoro += "Comune di ";
						strLuogoLavoro += StringUtils.getAttributeStrNotNull(comuneRow, "STRDENOMINAZIONE");
						strLuogoLavoro += " (";
						strLuogoLavoro += StringUtils.getAttributeStrNotNull(comuneRow, "PROVINCIA");
						strLuogoLavoro += ")\r\n";
					}
				}
				provinciaVec = (Vector) serviceResponse
						.getAttributeAsVector("MLISTATERRITORIPROVINCERICHIESTA.ROWS.ROW");
				if (provinciaVec != null && !provinciaVec.isEmpty()) {
					for (int i = 0; i < provinciaVec.size(); i++) {
						SourceBean provinciaRow = (SourceBean) provinciaVec.elementAt(i);
						strLuogoLavoro += "Provincia di ";
						strLuogoLavoro += StringUtils.getAttributeStrNotNull(provinciaRow, "STRDENOMINAZIONE");
						strLuogoLavoro += "\r\n";
					}
				}
				if (comuneVec.isEmpty() && provinciaVec.isEmpty()) {
					if (strLocalita != null && !strLocalita.equals("")) {
						strLuogoLavoro += "Località " + strLocalita;
						strLuogoLavoro += "\r\n";
					}
				}
				if (strLuogoLavoro.length() > LUNGHEZZA_CAMPO) {
					strLuogoLavoro = strLuogoLavoro.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("strLuogoLavoro", strLuogoLavoro);
			}

			if (strFormazionePubb != null && strFormazionePubb.equals("")) {
				Vector formazioneVec = (Vector) serviceResponse
						.getAttributeAsVector("M_GETSTUDIRICHIESTAPUBB.ROWS.ROW");
				if (formazioneVec != null && !formazioneVec.isEmpty()) {
					for (int i = 0; i < formazioneVec.size(); i++) {
						SourceBean formazioneRow = (SourceBean) formazioneVec.elementAt(i);
						strFormazionePubb += StringUtils.getAttributeStrNotNull(formazioneRow, "DESCTITOLO");
						String strSpecifica = StringUtils.getAttributeStrNotNull(formazioneRow, "SPECIFICA");
						if (!strSpecifica.equals("")) {
							strFormazionePubb += "- ";
							strFormazionePubb += strSpecifica;
						}
						String conseguito = StringUtils.getAttributeStrNotNull(formazioneRow, "CONSEGUITO");
						// 06/12/2007 Alessandro Pegoraro modificata gestione NULL -> è come se si richiedesse
						// conseguito
						// strFormazionePubb += (conseguito.equalsIgnoreCase("SI")) ? "- Conseguito" : "- Non
						// conseguito";
						strFormazionePubb += (conseguito.equalsIgnoreCase("SI") || conseguito.equalsIgnoreCase(""))
								? "- Conseguito"
								: "- Non conseguito";
						strFormazionePubb += "\r\n";
					}
				}
				if (strFormazionePubb.length() > LUNGHEZZA_CAMPO) {
					strFormazionePubb = strFormazionePubb.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("strFormazionePubb", strFormazionePubb);
			}

			if (txtCondContrattuale != null && txtCondContrattuale.equals("")) {
				Vector contrattoVec = (Vector) serviceResponse
						.getAttributeAsVector("M_GETCONTRATTIRICHIESTAPUBB.ROWS.ROW");
				if (contrattoVec != null && !contrattoVec.isEmpty()) {
					for (int i = 0; i < contrattoVec.size(); i++) {
						SourceBean contrattoRow = (SourceBean) contrattoVec.elementAt(i);
						txtCondContrattuale += StringUtils.getAttributeStrNotNull(contrattoRow, "STRDESCRIZIONE");
						txtCondContrattuale += "\r\n";
					}
				}
				if (txtCondContrattuale.length() > LUNGHEZZA_CAMPO) {
					txtCondContrattuale = txtCondContrattuale.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("txtCondContrattuale", txtCondContrattuale);
			}

			if (strConoscenzePubb != null && strConoscenzePubb.equals("")) {
				Vector linguaVec = (Vector) serviceResponse.getAttributeAsVector("MLISTALINGUERICHIESTAPUBB.ROWS.ROW");
				if (linguaVec != null && !linguaVec.isEmpty()) {
					strConoscenzePubb = "Lingue:\r\n";
					for (int i = 0; i < linguaVec.size(); i++) {
						SourceBean linguaRow = (SourceBean) linguaVec.elementAt(i);
						strConoscenzePubb += StringUtils.getAttributeStrNotNull(linguaRow, "STRDENOMINAZIONE");
						strConoscenzePubb += " grado ";
						String descr = StringUtils.getAttributeStrNotNull(linguaRow, "DESCRIZIONELETTO");
						strConoscenzePubb += descr;
						strConoscenzePubb += "\r\n";
					}
				}
				Vector informaticaVec = (Vector) serviceResponse
						.getAttributeAsVector("M_GETINFORMATICARICHIESTAPUBB.ROWS.ROW");
				if (informaticaVec != null && !informaticaVec.isEmpty()) {
					strConoscenzePubb += "Informatica:\r\n";
					for (int i = 0; i < informaticaVec.size(); i++) {
						SourceBean informaticaRow = (SourceBean) informaticaVec.elementAt(i);
						String descr = StringUtils.getAttributeStrNotNull(informaticaRow, "DESCRIZIONEDETT");
						strConoscenzePubb += descr;
						strConoscenzePubb += " grado ";
						String grado = StringUtils.getAttributeStrNotNull(informaticaRow, "DESCRIZIONEGRADO");
						strConoscenzePubb += grado;
						strConoscenzePubb += "\r\n";
					}
				}
				if (strConoscenzePubb.length() > LUNGHEZZA_CAMPO) {
					strConoscenzePubb = strConoscenzePubb.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("strConoscenzePubb", strConoscenzePubb);
			}

			if (txtCaratteristFigProf != null && txtCaratteristFigProf.equals("")) {
				Vector etaEsperienzaVec = (Vector) serviceResponse
						.getAttributeAsVector("M_GETIDOETAESPERIENZAPUBB.ROWS.ROW");
				if (etaEsperienzaVec != null && !etaEsperienzaVec.isEmpty()) {
					SourceBean etaEsperienzaRow = (SourceBean) etaEsperienzaVec.elementAt(0);
					if (etaEsperienzaRow.containsAttribute("flgEsperienza")) {
						String flgEsperienza = StringUtils.getAttributeStrNotNull(etaEsperienzaRow, "flgEsperienza");
						String strNumAnni = "";
						if (etaEsperienzaRow.containsAttribute("NUMANNIESPERIENZA")) {
							strNumAnni = etaEsperienzaRow.getAttribute("NUMANNIESPERIENZA").toString();
						}
						if (flgEsperienza != null && !flgEsperienza.equals("")) {
							txtCaratteristFigProf += "Esperienza";
							if (flgEsperienza != null
									&& (flgEsperienza.equalsIgnoreCase("S") || flgEsperienza.equalsIgnoreCase("Y"))) {
								txtCaratteristFigProf += " Si";
								if (strNumAnni != null && !strNumAnni.equals("")) {
									txtCaratteristFigProf += " anni ";
									txtCaratteristFigProf += strNumAnni;
								}
							}
							if (flgEsperienza != null && flgEsperienza.equalsIgnoreCase("N")) {
								txtCaratteristFigProf += " No";
							}
							if (flgEsperienza != null && flgEsperienza.equalsIgnoreCase("P")) {
								txtCaratteristFigProf += " Preferibile";
								if (strNumAnni != null && !strNumAnni.equals("")) {
									txtCaratteristFigProf += " anni ";
									txtCaratteristFigProf += strNumAnni;
								}
							}
							txtCaratteristFigProf += "\r\n";
						}
					}
				}

				if (!isFlussoVacancy) {
					if (flgAutomunito != null && !flgAutomunito.equals("")) {
						txtCaratteristFigProf += "Automunito ";
						if (flgAutomunito.equalsIgnoreCase("S") || flgAutomunito.equalsIgnoreCase("Y")) {
							txtCaratteristFigProf += "Si\r\n";
						}
						if (flgAutomunito.equalsIgnoreCase("N")) {
							txtCaratteristFigProf += "No\r\n";
						}
						if (flgAutomunito.equalsIgnoreCase("P")) {
							txtCaratteristFigProf += "Preferibile\r\n";
						}
					}
					if (flgMotomunito != null && !flgMotomunito.equals("")) {
						txtCaratteristFigProf += "Motomunito ";
						if (flgMotomunito.equalsIgnoreCase("S") || flgMotomunito.equalsIgnoreCase("Y")) {
							txtCaratteristFigProf += "Si\r\n";
						}
						if (flgMotomunito.equalsIgnoreCase("N")) {
							txtCaratteristFigProf += "No\r\n";
						}
						if (flgMotomunito.equalsIgnoreCase("P")) {
							txtCaratteristFigProf += "Preferibile\r\n";
						}
					}
				}

				Vector abilitazioneVec = (Vector) serviceResponse.getAttributeAsVector("M_LISTABILRICH.ROWS.ROW");
				if (abilitazioneVec != null && !abilitazioneVec.isEmpty()) {
					txtCaratteristFigProf += "Patenti e patentini:\r\n";
					for (int i = 0; i < abilitazioneVec.size(); i++) {
						SourceBean abilitazioneRow = (SourceBean) abilitazioneVec.elementAt(i);
						String descr = StringUtils.getAttributeStrNotNull(abilitazioneRow, "STRDESCRIZIONE");
						txtCaratteristFigProf += descr;
						txtCaratteristFigProf += "\r\n";
					}
				}
				if (txtCaratteristFigProf.length() > LUNGHEZZA_CAMPO) {
					txtCaratteristFigProf = txtCaratteristFigProf.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("txtCaratteristFigProf", txtCaratteristFigProf);
			}

			if (strNoteOrarioPubb != null && strNoteOrarioPubb.equals("")) {
				Vector orarioVec = (Vector) serviceResponse.getAttributeAsVector("MLISTAORARIRICHIESTA.ROWS.ROW");
				if (orarioVec != null && !orarioVec.isEmpty()) {
					strNoteOrarioPubb = "Orari:\r\n";
					for (int i = 0; i < orarioVec.size(); i++) {
						SourceBean orarioRow = (SourceBean) orarioVec.elementAt(i);
						String descr = StringUtils.getAttributeStrNotNull(orarioRow, "STRDESCRIZIONE");
						strNoteOrarioPubb += descr;
						strNoteOrarioPubb += "\r\n";
					}
				}
				Vector turnoVec = (Vector) serviceResponse.getAttributeAsVector("M_GETTURNIRICHIESTA.ROWS.ROW");
				if (turnoVec != null && !turnoVec.isEmpty()) {
					strNoteOrarioPubb += "Turni:\r\n";
					for (int i = 0; i < turnoVec.size(); i++) {
						SourceBean turnoRow = (SourceBean) turnoVec.elementAt(i);
						String descr = StringUtils.getAttributeStrNotNull(turnoRow, "STRDESCRIZIONE");
						strNoteOrarioPubb += descr;
						strNoteOrarioPubb += "\r\n";
					}
				}
				if (strNoteOrarioPubb.length() > LUNGHEZZA_CAMPO) {
					strNoteOrarioPubb = strNoteOrarioPubb.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("strNoteOrarioPubb", strNoteOrarioPubb);
			}

			if (strRifCandidaturaPubb != null && strRifCandidaturaPubb.equals("")) {

				if (checkRER) {
					if (flgPubbPalese || flgPubbAnonima) {
						strRifCandidaturaPubb = "Clicca sul pulsante invia candidatura nel portale lavoroperte.regione.emilia-romagna.it o nell'app LavoroXTe";
					}
				} else {
					if (flgPubbPalese) {
						if (strCognomeRifPubb != null && !strCognomeRifPubb.equals("")) {
							strRifCandidaturaPubb = "Contattare ";
							strRifCandidaturaPubb += strCognomeRifPubb + " " + strNomeRifPubb;
						}
						if (strTelRifPubb != null && !strTelRifPubb.equals("")) {
							strRifCandidaturaPubb += "\r\nContattare il numero " + strTelRifPubb;
						}
						if (strFaxRifPubb != null && !strFaxRifPubb.equals("")) {
							strRifCandidaturaPubb += "\r\nInviare il curriculum tramite fax al num. " + strFaxRifPubb;
						}
						if (strEmailRifPubb != null && !strEmailRifPubb.equals("")) {
							strRifCandidaturaPubb += "\r\nInviare il curriculum tramite e-mail a " + strEmailRifPubb;
						}
						strRifCandidaturaPubb += "\r\n";
					}
					if (flgPubbAnonima) {
						strRifCandidaturaPubb = "Contattare il CPI di ";
						strRifCandidaturaPubb += strDescrizioneCPI;
						if (strIndirizzoCPI != null && !strIndirizzoCPI.equals("")) {
							strRifCandidaturaPubb += " - " + strIndirizzoCPI;
						}
						if (strFaxCPI != null && !strFaxCPI.equals("")) {
							strRifCandidaturaPubb += "\r\nInviare il curriculum tramite fax al " + strFaxCPI;
						}
						if (strEmailCPI != null && !strEmailCPI.equals("")) {
							strRifCandidaturaPubb += "\r\nInviare il curriculum tramite e-mail a " + strEmailCPI;
						}
						strRifCandidaturaPubb += "\r\n";
					}
				}
				if (strRifCandidaturaPubb.length() > LUNGHEZZA_CAMPO) {
					strRifCandidaturaPubb = strRifCandidaturaPubb.substring(0, LUNGHEZZA_CAMPO);
				}
				request.updAttribute("strRifCandidaturaPubb", strRifCandidaturaPubb);

			}

			if (codMonoCMcatPubb != null && codMonoCMcatPubb.equals("")) {
				String strCodMonoCMcategoria = StringUtils.getAttributeStrNotNull(richiestaRow, "codMonoCMcategoria");
				request.updAttribute("codMonoCMcatPubb", strCodMonoCMcategoria);
			}

		}

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();
			setSectionQueryUpdate("QUERY_UPDATE_1");
			boolean ret = doUpdate(request, response);
			if (ret) {
				setSectionQueryUpdate("QUERY_UPDATE_2");
				ret = doUpdate(request, response);
			} else {
				throw new Exception("");
			}

			if (ret) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception("");
			}

		} catch (Exception ex) {
			reportOperation.reportFailure(idFail);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}

	}

}
