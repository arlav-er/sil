package it.eng.sil.module.anag;

import java.math.BigDecimal;

import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;
import it.gov.lavoro.servizi.servizicoap.richiestasap.DatiEventoType;
import it.gov.lavoro.servizi.servizicoap.richiestasap.ListaDIDType;
import it.gov.lavoro.servizi.servizicoap.richiestasap.PoliticaAttiva;

public class CheckPrendiTitolarieta extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckPrendiTitolarieta.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		try {
			SourceBean serviceResponse = getResponseContainer().getServiceResponse();
			String flgPoloReg = "";
			String codProvinciaSil = "";
			String codRegioneSil = "";
			String codEnteTit = "";
			boolean checkTitolare = false;
			boolean checkPrendiInCarico = false;
			boolean checkPresenzaPresaCarico = false;
			XMLGregorianCalendar dataDichSap = null;
			if (serviceResponse.containsAttribute("M_InfoTsGenerale.ROWS.ROW")) {
				flgPoloReg = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.FLGPOLOREG").toString();
				codProvinciaSil = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.CODPROVINCIASIL").toString();
				codRegioneSil = serviceResponse.getAttribute("M_InfoTsGenerale.ROWS.ROW.CODREGIONESIL").toString();
			}
			// LavoratoreType lavT = null;
			ListaDIDType lavT1 = null;
			if (serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.SAPWS")
					&& serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS") != null) {
				// lavT = (LavoratoreType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
				lavT1 = (ListaDIDType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
				/*
				 * if (lavT != null) { codEnteTit = lavT.getDatiinvio().getCodiceentetit(); }
				 */
				if (lavT1 != null) {
					codEnteTit = lavT1.getCodiceentetit();
				}
			}

			if (flgPoloReg.equalsIgnoreCase("N")) {
				String provinciaCPI = "";
				Object params[] = new Object[1];
				params[0] = codEnteTit;
				SourceBean row = (SourceBean) QueryExecutor.executeQuery(
						"SELECT_PROVINCIA_CODCPI_COMPETENZA_MINISTERIALE", params, "SELECT", Values.DB_SIL_DATI);
				if (row != null) {
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					provinciaCPI = row.getAttribute("provinciacpi") != null
							? row.getAttribute("provinciacpi").toString()
							: "";
					if (provinciaCPI.equals("") || !provinciaCPI.equalsIgnoreCase(codProvinciaSil)) {
						response.setAttribute("PRENDITITOLARIETA", "S");
					} else {
						if (provinciaCPI.equalsIgnoreCase(codProvinciaSil)) {
							checkTitolare = true;
						}
					}
				}
			} else {
				if (flgPoloReg.equalsIgnoreCase("S")) {
					String regioneCPI = "";
					Object params[] = new Object[1];
					params[0] = codEnteTit;
					SourceBean row = (SourceBean) QueryExecutor.executeQuery(
							"SELECT_REGIONE_CODCPI_COMPETENZA_MINISTERIALE", params, "SELECT", Values.DB_SIL_DATI);
					if (row != null) {
						row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
						regioneCPI = row.getAttribute("codregione") != null ? row.getAttribute("codregione").toString()
								: "";
						if (regioneCPI.equals("") || !regioneCPI.equalsIgnoreCase(codRegioneSil)) {
							response.setAttribute("PRENDITITOLARIETA", "S");
						} else {
							if (regioneCPI.equalsIgnoreCase(codRegioneSil)) {
								checkTitolare = true;
							}
						}
					}
				}
			}
			//
			String denominazioneEnte = "";
			String dataInizio = "";
			String dataFine = "";
			String dataColloquio = "";
			String entePromotore = "";
			String denominazione = "";
			String descrizione = "";
			String tipoAttivitaA02 = "";
			String tipoProgettoA02 = "";
			String dataInizioA02Max = "";
			String dataUltimoEvento = "";
			String codiceUltimoEvento = "";
			String indiceProfiling = "";
			// va verificata se nella sezione 2 dati amministrativi della SAP è presente la data dichiarazione
			// e in questo caso si cerca nella SAP ministeriale se esiste una azione A02 con tipo progetto 05 con data
			// inizio = data fine
			// e codice ente promotore un cpi di altra Provincia/Regione (in base al fatto che il SIL sia Provinciale o
			// Regionale).
			if (lavT1 != null) {
				dataDichSap = lavT1.getDisponibilita();
				// modifica gennaio 2020
				// if (dataDichSap != null){
				PoliticaAttiva politicaA02 = lavT1.getA02();
				if (politicaA02 != null) {
					response.setAttribute("VISUALIZZA_A2", "VISUALIZZA_A2");
					DatiEventoType datiEvento = politicaA02.getUltimoEvento();
					if (datiEvento != null) {
						codiceUltimoEvento = datiEvento.getEvento();
						if (datiEvento.getDataEvento() != null) {
							dataUltimoEvento = DateUtils.formatXMLGregorian(datiEvento.getDataEvento());
						}
					}
					if (politicaA02.getIndiceProfiling() != null) {
						indiceProfiling = politicaA02.getIndiceProfiling().setScale(2, BigDecimal.ROUND_DOWN)
								.toString();
					}

					// if( datiEvento == null || datiEvento.getEvento().equals("01") ||
					// datiEvento.getEvento().equals("02") ){
					tipoAttivitaA02 = politicaA02.getTipoAttivita();
					tipoProgettoA02 = politicaA02.getTitoloProgetto();
					entePromotore = politicaA02.getCodiceEntePromotore();
					XMLGregorianCalendar dataInizioCal = politicaA02.getData();
					XMLGregorianCalendar dataFineCal = politicaA02.getDataFine();
					if (dataInizioCal != null && dataFineCal != null) {
						dataInizio = DateUtils.formatXMLGregorian(dataInizioCal);
						dataFine = DateUtils.formatXMLGregorian(dataFineCal);
						if (DateUtils.compare(dataInizio, dataFine) == 0) {
							checkPresenzaPresaCarico = true;
							// recupero ente promotore
							SourceBean rowEnteA02 = null;
							Object paramsA02[] = new Object[1];
							paramsA02[0] = entePromotore;
							if (flgPoloReg.equalsIgnoreCase("N")) {
								rowEnteA02 = (SourceBean) QueryExecutor.executeQuery(
										"SELECT_PROVINCIA_CODCPI_COMPETENZA_MINISTERIALE", paramsA02, "SELECT",
										Values.DB_SIL_DATI);
							} else {
								if (flgPoloReg.equalsIgnoreCase("S")) {
									rowEnteA02 = (SourceBean) QueryExecutor.executeQuery(
											"SELECT_REGIONE_CODCPI_COMPETENZA_MINISTERIALE", paramsA02, "SELECT",
											Values.DB_SIL_DATI);
								}
							}
							if (rowEnteA02 != null) {
								rowEnteA02 = rowEnteA02.containsAttribute("ROW")
										? (SourceBean) rowEnteA02.getAttribute("ROW")
										: rowEnteA02;
							}
							if (dataInizioA02Max.equals("") || DateUtils.compare(dataInizio, dataInizioA02Max) > 0) {
								dataInizioA02Max = dataInizio;
								denominazione = Utils.notNull(politicaA02.getTitoloDenominazione());
								descrizione = Utils.notNull(politicaA02.getDescrizione());
								if (politicaA02.getDataProposta() != null) {
									dataColloquio = DateUtils.formatXMLGregorian(politicaA02.getDataProposta());
								}
								if (rowEnteA02 != null) {
									denominazioneEnte = rowEnteA02.getAttribute("strdenominazione") != null
											? rowEnteA02.getAttribute("strdenominazione").toString()
											: "";
								}
							}

							if (flgPoloReg.equalsIgnoreCase("N")) {
								String provinciaA02 = "";
								if (rowEnteA02 != null) {
									provinciaA02 = rowEnteA02.getAttribute("provinciacpi") != null
											? rowEnteA02.getAttribute("provinciacpi").toString()
											: "";
									if (provinciaA02.equals("") || !provinciaA02.equalsIgnoreCase(codProvinciaSil)) {
										checkPrendiInCarico = true;
									}
								}
							} else {
								if (flgPoloReg.equalsIgnoreCase("S")) {
									String regioneA02 = "";
									if (rowEnteA02 != null) {
										regioneA02 = rowEnteA02.getAttribute("codregione") != null
												? rowEnteA02.getAttribute("codregione").toString()
												: "";
										if (regioneA02.equals("") || !regioneA02.equalsIgnoreCase(codRegioneSil)) {
											checkPrendiInCarico = true;
										}
									}
								}
							}
						}
					}
					// }
				}
				// }
			}

			/*
			 * if(lavT != null && lavT.getDatiamministrativi() != null &&
			 * lavT.getDatiamministrativi().getStatoinanagrafe() != null){ Statoinanagrafe statoAn =
			 * lavT.getDatiamministrativi().getStatoinanagrafe(); dataDichSap = statoAn.getDisponibilita(); if
			 * (dataDichSap != null){ PoliticheAttiveLst allListaP = lavT.getPoliticheAttiveLst(); if(allListaP !=
			 * null){ List<PoliticheAttive> listaP2 = allListaP.getPoliticheAttive(); if (listaP2 != null &&
			 * listaP2.size() > 0) { int sizePol = listaP2.size(); for (int i = 0; i < sizePol; i++) { PoliticheAttive
			 * item = listaP2.get(i); tipoAttivita = item.getTipoAttivita(); tipoProgetto = item.getTitoloProgetto(); if
			 * (tipoAttivita != null && tipoProgetto != null && tipoAttivita.equalsIgnoreCase("A02") &&
			 * tipoProgetto.equalsIgnoreCase("05")){ tipoAttivitaA02 = item.getTipoAttivita(); tipoProgettoA02 =
			 * item.getTitoloProgetto(); entePromotore = item.getCodiceEntePromotore(); XMLGregorianCalendar
			 * dataInizioCal = item.getData(); XMLGregorianCalendar dataFineCal = item.getDataFine(); if (dataInizioCal
			 * != null && dataFineCal != null) { dataInizio = DateUtils.formatXMLGregorian(dataInizioCal); dataFine =
			 * DateUtils.formatXMLGregorian(dataFineCal); if (DateUtils.compare(dataInizio, dataFine) == 0) {
			 * checkPresenzaPresaCarico = true; // recupero ente promotore SourceBean rowEnteA02 = null; Object
			 * paramsA02[] = new Object [1]; paramsA02[0] = entePromotore; if (flgPoloReg.equalsIgnoreCase("N")) {
			 * rowEnteA02 = (SourceBean)QueryExecutor.executeQuery("SELECT_PROVINCIA_CODCPI_COMPETENZA_MINISTERIALE",
			 * paramsA02, "SELECT", Values.DB_SIL_DATI); } else { if (flgPoloReg.equalsIgnoreCase("S")) { rowEnteA02 =
			 * (SourceBean)QueryExecutor.executeQuery("SELECT_REGIONE_CODCPI_COMPETENZA_MINISTERIALE", paramsA02,
			 * "SELECT", Values.DB_SIL_DATI); } } if (rowEnteA02 != null) { rowEnteA02 =
			 * rowEnteA02.containsAttribute("ROW")?(SourceBean)rowEnteA02.getAttribute("ROW"):rowEnteA02; } if
			 * (dataInizioA02Max.equals("") || DateUtils.compare(dataInizio, dataInizioA02Max) > 0) { dataInizioA02Max =
			 * dataInizio; denominazione = Utils.notNull(item.getTitoloDenominazione()); descrizione =
			 * Utils.notNull(item.getDescrizione()); if (item.getDataProposta() != null) { dataColloquio =
			 * DateUtils.formatXMLGregorian(item.getDataProposta()); } if (rowEnteA02 != null) { denominazioneEnte =
			 * rowEnteA02.getAttribute("strdenominazione")!=null?rowEnteA02.getAttribute("strdenominazione").toString():
			 * ""; } }
			 * 
			 * if (flgPoloReg.equalsIgnoreCase("N")) { String provinciaA02 = ""; if (rowEnteA02 != null) { provinciaA02
			 * = rowEnteA02.getAttribute("provinciacpi")!=null?rowEnteA02.getAttribute("provinciacpi").toString():""; if
			 * (provinciaA02.equals("") || !provinciaA02.equalsIgnoreCase(codProvinciaSil)) { checkPrendiInCarico =
			 * true; } } } else { if (flgPoloReg.equalsIgnoreCase("S")) { String regioneA02 = ""; if (rowEnteA02 !=
			 * null) { regioneA02 =
			 * rowEnteA02.getAttribute("codregione")!=null?rowEnteA02.getAttribute("codregione").toString():""; if
			 * (regioneA02.equals("") || !regioneA02.equalsIgnoreCase(codRegioneSil)) { checkPrendiInCarico = true; } }
			 * } } } } } } } } } }
			 */

			// Visualizzazione Presa in carico SAP
			response.setAttribute("ENTEPROMOTORE", entePromotore + " - " + denominazioneEnte);
			response.setAttribute("DATAPROPOSTA", dataColloquio);
			response.setAttribute("DATAINIZIO", dataInizioA02Max);
			response.setAttribute("DATAFINE", dataInizioA02Max);
			response.setAttribute("DESCRIZIONE", descrizione);
			response.setAttribute("ATTIVITA", tipoAttivitaA02);
			response.setAttribute("DENOMINAZIONE", denominazione);
			response.setAttribute("TITOLOPROGETTO", tipoProgettoA02);
			response.setAttribute("ULTIMOEVENTO", codiceUltimoEvento);
			response.setAttribute("DATA_ULTIMOEVENTO", dataUltimoEvento);
			response.setAttribute("INDICE_PROFILING", indiceProfiling);

			if (checkPresenzaPresaCarico) {
				response.setAttribute("PRESAINCARICOSAP", "S");
			}
			// Prendi in carico
			if (checkPrendiInCarico) {
				response.setAttribute("PRENDIINCARICO", "S");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", e);
		}
	}
}