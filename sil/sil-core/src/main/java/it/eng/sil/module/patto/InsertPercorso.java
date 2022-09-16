package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PercorsoConcordatoException;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class InsertPercorso extends AbstractSimpleModule {

	private static final long serialVersionUID = -278856068973235570L;
	public static final String ESITO_RITIRATO = "NR";
	public static final String ESITO_RENDIC_ANNULLATO = "A";
	public static final String ESITO_RENDIC_EROGATO = "E";
	public static final String ESITO_RENDIC_EROGATO_NON_REND = "ENR";
	public static final BigDecimal YEI_TIROCINIO_EXTRACURRICULARE = new BigDecimal("28");
	public static final int ETA_YEI_TIROCINIO_EXTRACURRICULARE = 25;
	private static final String ConfermaPeriodica = "CONFERMA PERIODICA";
	private static final String esitoInCorso = "CC";
	private static final String esitoAvviato = "AVV";
	private static final String esitoInterrotto = "INT";
	private static final String esitoRifiutato = "RIF";
	private static final String esitoConcluso = "FC";
	private static final String esitoRendicontatoPrenotato = "P";
	private static final String codLstTabAzionePatto = "OR_PER";
	private static final String ServizioConfermaPeriodica = "primo colloquio d.l.gs. 181/2000";
	private static final String noteColloquio = "colloquio inserito per proroga patto";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertPercorso.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean resultIns = false;
		boolean aggiornaDataScadenzaPatto = request.containsAttribute("AGGIORNADATASCADPATTO")
				&& request.getAttribute("AGGIORNADATASCADPATTO").equals("true");
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		this.disableMessageIdSuccess();
		this.disableMessageIdFail();
		Collection elencoAzioni = AbstractSimpleModule.getArgumentValues(request, "prgAzioni");
		TransactionQueryExecutor transExec = null;

		BigDecimal prgPercorso = null;
		String cfEntePromotore = "";
		String codEsitoPercorso = "";
		String codSceltaAdesione = "";
		String codEsitoRendPercorso = "";
		String flgGruppo = "";
		String numPartecipanti = "";
		String datAdesioneGG = "";
		BigDecimal prgPercorsoAdesione = null;
		BigDecimal prgColloquioAdesione = null;
		Boolean ris = new Boolean(false);
		boolean azioneEsistente = false;
		boolean associazioneAlPatto = false;
		int messageCodeCF = -1;
		String rinnovaPatto = "";
		String codStatoPattoProroga = "";
		String flgPattoProroga = "";
		String tipoPattoProroga = "";
		boolean pattoProrogabile = false;
		Object obj[] = new Object[31];
		BigDecimal cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		String datDichiarazioneAzione = "";
		String prgAzioneRagg = "";

		String prgRDC = "";

		try {
			cfEntePromotore = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscaleEnte");
			codEsitoPercorso = StringUtils.getAttributeStrNotNull(request, "CODESITO");
			codEsitoRendPercorso = StringUtils.getAttributeStrNotNull(request, "CODESITORENDICONT");
			datDichiarazioneAzione = StringUtils.getAttributeStrNotNull(request, "datDichAzione");

			codSceltaAdesione = StringUtils.getAttributeStrNotNull(request, "prgKeyAdesione");
			if (!codSceltaAdesione.equalsIgnoreCase("")) {
				String[] vettAdesione = codSceltaAdesione.split("-");
				prgPercorsoAdesione = new BigDecimal(vettAdesione[0]);
				prgColloquioAdesione = new BigDecimal(vettAdesione[1]);
			}

			prgAzioneRagg = StringUtils.getAttributeStrNotNull(request, "prgAzioneRagg");
			rinnovaPatto = StringUtils.getAttributeStrNotNull(request, "RINNOVAPATTO");
			codStatoPattoProroga = StringUtils.getAttributeStrNotNull(request, "CODSTATOPATTOPROROGA");
			flgPattoProroga = StringUtils.getAttributeStrNotNull(request, "FLGPATTO297PROROGA");
			tipoPattoProroga = StringUtils.getAttributeStrNotNull(request, "CODTIPOPATTOPROROGA");
			Object prgPattoProrogato = request.getAttribute("PRGPATTOPROROGA");
			Object prgPattoCollegatoAzione = request.getAttribute("PRG_PATTO_LAVORATORE");
			Object prgColloquio = request.getAttribute("PRGCOLLOQUIO");
			Vector<String> programmi = new Vector<String>();
			if (prgColloquio != null) {
				programmi = PattoBean.checkProgrammaColloquio(prgColloquio, null);
			}

			prgRDC = StringUtils.getAttributeStrNotNull(request, "prgRDC");

			/*
			 * commentato in data 19/12/2018 nuova gestione patto 150 if (prgPattoProrogato != null &&
			 * codStatoPattoProroga.equalsIgnoreCase(PattoBean.STATO_PROTOCOLLATO) && prgPattoCollegatoAzione != null &&
			 * !prgPattoCollegatoAzione.toString().equals("") && prgPattoProrogato.equals(prgPattoCollegatoAzione) &&
			 * !tipoPattoProroga.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI) &&
			 * !tipoPattoProroga.equalsIgnoreCase(PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA) &&
			 * !tipoPattoProroga.equalsIgnoreCase(PattoBean.DB_MISURE_OVER_30) &&
			 * !tipoPattoProroga.equalsIgnoreCase(PattoBean.DB_MISURE_OVER_45) &&
			 * !tipoPattoProroga.equalsIgnoreCase(PattoBean.DB_MISURE_INCLUSIONE_ATTIVA) &&
			 * flgPattoProroga.equalsIgnoreCase(Values.FLAG_TRUE)) { pattoProrogabile = true; }
			 */

			try {
				if (!cfEntePromotore.equals("")) {
					if (CF_utils.verificaParzialeCF(cfEntePromotore) != 0) {
						messageCodeCF = MessageCodes.CodiceFiscale.ERR_LUNGHEZZA;
					}
				}
			} catch (CfException eCF) {
				messageCodeCF = eCF.getMessageIdFail();
			}

			int j = 0;
			obj[j++] = request.getAttribute("PRGCOLLOQUIO");
			obj[j++] = "";
			obj[j++] = request.getAttribute("DATSTIMATA");
			obj[j++] = "";
			obj[j++] = request.getAttribute("CODESITO");
			obj[j++] = request.getAttribute("DATEFFETTIVA");
			obj[j++] = request.getAttribute("STRNOTE");
			obj[j++] = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			obj[j++] = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			obj[j++] = request.getAttribute("CODSERVIZICIG");
			obj[j++] = request.getAttribute("flgMediatore");
			obj[j++] = request.getAttribute("flgAbilita");
			obj[j++] = request.getAttribute("CODESITORENDICONT");
			obj[j++] = request.getAttribute("prgProgrammaq");
			obj[j++] = request.getAttribute("CODTIPOLOGIADURATA");
			obj[j++] = request.getAttribute("NUMYGDURATAMIN");
			obj[j++] = request.getAttribute("NUMYGDURATAMAX");
			obj[j++] = request.getAttribute("NUMYGDURATAEFF");

			flgGruppo = StringUtils.getAttributeStrNotNull(request, "flgGruppo");
			numPartecipanti = StringUtils.getAttributeStrNotNull(request, "numPartecipanti");
			datAdesioneGG = StringUtils.getAttributeStrNotNull(request, "datAdesioneGG");

			if (!prgAzioneRagg.equals("")) {
				BigDecimal prgAzioneRaggScelta = new BigDecimal(prgAzioneRagg);
				if (prgAzioneRaggScelta.equals(YEI_TIROCINIO_EXTRACURRICULARE)) {
					UtilsConfig utility = new UtilsConfig("ETA_TIR");
					String tipoConfig = utility.getConfigurazioneDefault_Custom();
					if (tipoConfig.equals(Properties.CUSTOM_CONFIG)) {
						if (!codSceltaAdesione.equals("")) {
							boolean checkEtaLav = controllaEtaLavoratoreAdesione(
									request.getAttribute("CDNLAVORATORE").toString(), prgPercorsoAdesione,
									prgColloquioAdesione);
							if (!checkEtaLav) {
								throw new PercorsoConcordatoException(
										MessageCodes.Patto.ERR_ETA_TIROCINIO_EXTRACURRICULARE);
							}
						} else {
							throw new PercorsoConcordatoException(
									MessageCodes.Patto.ERR_ETA_TIROCINIO_EXTRACURRICULARE);
						}
					}
				}
			}

			if (request.containsAttribute("showCampiCIG")) {
				controlliCIG(request, response, elencoAzioni);
			}

			/*
			 * commentato in data 19/12/2018 nuova gestione patto 150 BigDecimal prgAzioneConferma = null; boolean
			 * confermaPeriodicaInInserimento = false; if (rinnovaPatto.equalsIgnoreCase(Values.FLAG_TRUE) &&
			 * pattoProrogabile) { //Gestione conferma periodica (data stipula patto e data scadenza) prgAzioneConferma
			 * = getPrgAzioneConfermaPeriodica(); }
			 */

			int indiceAzioni = 0;
			boolean insertAzioneAdesioneGG = false;
			Vector elencoAzioniOrdinate = new Vector();

			/*
			 * Controllo programma 186. Non possono essere avviate se dalla data del programma sono trascorsi 180 giorni
			 */
			/*
			 * controllo eliminato 17/02/2020 segnalazione redmine numero 8233
			 */
			String codServizio = StringUtils.getAttributeStrNotNull(request, "codServizio");
			String datInizioProgramma = StringUtils.getAttributeStrNotNull(request, "datInizioProgramma");
			String datAvvioAzione = StringUtils.getAttributeStrNotNull(request, PattoBean.DB_DAT_AVVIO_AZIONE);
			/*
			 * if(codEsitoPercorso.equalsIgnoreCase(esitoAvviato) &&
			 * (codServizio.equalsIgnoreCase(PattoBean.DB_COD_SERVIZIO_186) ||
			 * codServizio.equalsIgnoreCase(PattoBean.DB_MISURE_NUOVA_GARANZIA_GIOVANI))) { boolean is180Giorni_186 =
			 * PattoBean.is180Giorni_186(datInizioProgramma, datAvvioAzione); if (!is180Giorni_186) throw new
			 * PercorsoConcordatoException(MessageCodes.Patto.ERR_PROG_186_AZIONE_DATA_AVVIO); }
			 */

			for (Iterator iter = elencoAzioni.iterator(); iter.hasNext();) {
				String attributeValue = (String) iter.next();
				Object param[] = new Object[1];
				param[0] = attributeValue;
				// Verifico se tra le azioni inserite è presente la Conferma Periodica

				/*
				 * commentato in data 19/12/2018 nuova gestione patto 150 if (prgAzioneConferma != null &&
				 * prgAzioneConferma.equals(new BigDecimal(attributeValue))) { confermaPeriodicaInInserimento = true; }
				 */

				SourceBean rowAzione = (SourceBean) QueryExecutor.executeQuery("GET_DE_AZIONE_BYPRG", param, "SELECT",
						Values.DB_SIL_DATI);

				if (rowAzione != null && StringUtils.getAttributeStrNotNull(rowAzione, "ROW.FLGADESIONEGG")
						.equalsIgnoreCase(Values.FLAG_TRUE)) {
					if (datAdesioneGG.equals("")) {
						throw new PercorsoConcordatoException(MessageCodes.Patto.DATA_ADESIONE_ASSENTE);
					}
					boolean adesioneEsistente = controllaEsistenzaAzione(attributeValue,
							request.getAttribute("DATSTIMATA").toString(),
							request.getAttribute("CDNLAVORATORE").toString());
					if (adesioneEsistente) {
						throw new PercorsoConcordatoException(MessageCodes.Patto.AZIONE_PERC_CONCORD_ESISTENTE);
					}
					adesioneEsistente = controllaEsistenzaAdesione(attributeValue, datAdesioneGG,
							request.getAttribute("CDNLAVORATORE").toString());
					if (adesioneEsistente) {
						throw new PercorsoConcordatoException(MessageCodes.Patto.ADESIONE_PERC_CONCORD_ESISTENTE);
					}
					// Inserisco se presente prima l'Adesione GG a cui devo collegare le eventuali azioni di politica
					// attiva GG
					elencoAzioniOrdinate.add(0, attributeValue);
					insertAzioneAdesioneGG = true;
				} else {
					elencoAzioniOrdinate.add(indiceAzioni, attributeValue);
				}
				indiceAzioni = indiceAzioni + 1;
			}

			if (insertAzioneAdesioneGG) {
				elencoAzioni = elencoAzioniOrdinate;
			}

			indiceAzioni = -1;
			BigDecimal prgPattoAperto = null;
			boolean associazioneConPatto = false;
			boolean settaPattoRequest = false;
			if (PattoManager.withPatto(request)) {
				associazioneConPatto = true;
			} else {
				SourceBean serviceResponse = getServiceResponse();
				SourceBean pattoAperto = (SourceBean) serviceResponse.getAttribute("M_PATTOAPERTO");
				if (pattoAperto != null) {
					prgPattoAperto = (BigDecimal) pattoAperto.getAttribute("ROWS.ROW.PRGPATTOLAVORATORE");
					if (prgPattoAperto != null) {
						associazioneConPatto = true;
						settaPattoRequest = true;
					}
				}
			}

			this.setSectionQuerySelect("QUERY_GET_ESITO_CONCLUSO");
			SourceBean rowEsito = doSelect(request, response, false);
			String flgConclusa = rowEsito.getAttribute("ROW.flgstatoconcluso") != null
					? rowEsito.getAttribute("ROW.flgstatoconcluso").toString()
					: "";
			this.setSectionQuerySelect("QUERY_GET_INFO_COLLOQUIO");
			SourceBean colloquio = doSelect(request, response, false);
			BigDecimal numklocoll = (BigDecimal) colloquio.getAttribute("ROW.NUMKLOCOLLOQUIO");
			String dataFineProgrammaDB = (String) colloquio.getAttribute("ROW.DATAFINEPROGRAMMA");

			for (Iterator iter = elencoAzioni.iterator(); iter.hasNext();) {
				try {
					indiceAzioni = indiceAzioni + 1;
					transExec = new TransactionQueryExecutor(getPool(), this);
					enableTransactions(transExec);
					transExec.initTransaction();
					prgPercorso = doNextVal(request, response);
					if (prgPercorso != null) {
						obj[1] = prgPercorso;
						// setParameter(prgPercorso, request);
						String attributeValue = (String) iter.next();
						// request.delAttribute("prgAzioni");
						// request.setAttribute("prgAzioni", attributeValue);
						obj[3] = attributeValue;
						String flgGruppoCurr = null;
						String numPartecipantiCurr = null;
						String datAdesioneGGCurr = null;
						String cfEntePromotoreCurr = null;
						BigDecimal prgPercorsoAdesioneCurr = null;
						BigDecimal prgColloquioAdesioneCurr = null;

						String codEsitoRendCurr = codEsitoRendPercorso;

						// doInsert(request, response);
						if (insertAzioneAdesioneGG && indiceAzioni == 0) {
							// in questo caso l'azione = Adesione GG e il controllo è stato fatto sopra
							azioneEsistente = false;
						} else {
							azioneEsistente = controllaEsistenzaAzione(attributeValue,
									request.getAttribute("DATSTIMATA").toString(),
									request.getAttribute("CDNLAVORATORE").toString(), transExec);
						}

						if (!azioneEsistente) {
							boolean isAzionePresaInCarico = false;
							Object param[] = new Object[1];
							param[0] = attributeValue;
							SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_DE_AZIONE_BYPRG", param,
									"SELECT");
							if (rowAzione != null) {
								String flgFormazioneAzione = StringUtils.getAttributeStrNotNull(rowAzione,
										"ROW.flgFormazione");
								String codMonoPacchetto = StringUtils.getAttributeStrNotNull(rowAzione,
										"ROW.CODMONOPACCHETTO");
								String codazionesifer = StringUtils.getAttributeStrNotNull(rowAzione,
										"ROW.codazionesifer");
								String flgMisurayei = StringUtils.getAttributeStrNotNull(rowAzione,
										"ROW.flg_misurayei");
								String codProgettoAz = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.codProgetto");
								String codTipoAttivitaAz = StringUtils.getAttributeStrNotNull(rowAzione,
										"ROW.codtipoattivita");
								if (codTipoAttivitaAz.equalsIgnoreCase("A02") && codProgettoAz.equalsIgnoreCase("05")) {
									isAzionePresaInCarico = true;
								}
								if (flgFormazioneAzione.equals(Values.FLAG_TRUE)) {
									if (!flgGruppo.equals("")) {
										flgGruppoCurr = flgGruppo;
										if (flgGruppo.equals(Values.FLAG_TRUE)) {
											numPartecipantiCurr = numPartecipanti;
										}
									}
								}
								// CONTROLLO PER SETTARE IL RIFERIMENTO ALL'ADESIONE PER LE POLITICHE ATTIVE
								SourceBean rowAzionePolAttive = (SourceBean) transExec
										.executeQuery("GET_DE_AZIONE_POLITICHE_ATTIVE_BYPRG", param, "SELECT");
								if (rowAzionePolAttive != null && rowAzionePolAttive.containsAttribute("ROW.codice")) {
									prgPercorsoAdesioneCurr = prgPercorsoAdesione;
									prgColloquioAdesioneCurr = prgColloquioAdesione;
								}
								// IN CASO DI INSERIMENTO DI ADESIONE GG E POLITICHE ATTIVE, LE POLITICHE ATTIVE VENGONO
								// COLLEGATE ALL'ADESIONE CHE SI STA INSERENDO
								if (StringUtils.getAttributeStrNotNull(rowAzione, "ROW.FLGADESIONEGG")
										.equalsIgnoreCase(Values.FLAG_TRUE)) {
									datAdesioneGGCurr = datAdesioneGG;
									prgPercorsoAdesione = prgPercorso;
									prgColloquioAdesione = new BigDecimal(
											request.getAttribute("PRGCOLLOQUIO").toString());
								}
								// CONTROLLI PER PACCHETTO ADULTI SE L'AZIONE E' COLLEGATA AL PATTO
								if (associazioneConPatto) {
									if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI)
											|| PattoBean.checkMisuraProgramma(programmi,
													PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
										UtilsConfig utility = new UtilsConfig(PacchettoAdulti.CONFIG_PA);
										String config = utility.getConfigurazioneDefault_Custom();
										if (config.equals(Properties.CUSTOM_CONFIG)) {
											int codiceErrore = PattoBean.checkPacchettoAdultiGaranziaGiovani(
													codMonoPacchetto, flgFormazioneAzione, codazionesifer);
											if (codiceErrore > 0) {
												throw new PercorsoConcordatoException(codiceErrore);
											}
										}
										if (PattoBean.checkMisuraProgramma(programmi,
												PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
											int codiceErroreGGU = PattoBean.checkazioniPattoGGU(flgMisurayei);
											if (codiceErroreGGU > 0) {
												throw new PercorsoConcordatoException(codiceErroreGGU);
											}
										} else {
											// patto con misura MGG
											int codiceErroreGG = PattoBean.checkazioniPattoGG(codMonoPacchetto);
											if (codiceErroreGG > 0) {
												throw new PercorsoConcordatoException(codiceErroreGG);
											}
										}
									} else {
										if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)
												|| PattoBean.checkMisuraProgramma(programmi,
														PattoBean.DB_MISURE_OVER_45)
												|| PattoBean.checkMisuraProgramma(programmi,
														PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
											int codiceErrorePA = PattoBean
													.checkPacchettoAdultiOver3045INA(codMonoPacchetto, flgMisurayei);
											if (codiceErrorePA > 0) {
												throw new PercorsoConcordatoException(codiceErrorePA);
											}
											/*
											 * if ( (flgFormazioneAzione.equalsIgnoreCase(Values.FLAG_TRUE)) &&
											 * (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06) ||
											 * codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C07)) ) { int
											 * codiceErrore = PattoBean.checkLimiteAzioniPacchettoAdulti(programmi,
											 * codMonoPacchetto, codazionesifer); if (codiceErrore > 0) { throw new
											 * PercorsoConcordatoException(codiceErrore); } }
											 */
										}
									}
								}
							}
							obj[18] = flgGruppoCurr;
							obj[19] = numPartecipantiCurr;
							obj[20] = datAdesioneGGCurr;
							Object param1[] = new Object[1];
							param1[0] = attributeValue;
							SourceBean rowAttivitaAzione = (SourceBean) transExec
									.executeQuery("GET_TIPO_ATTIVITA_AZIONE", param1, "SELECT");
							if ((rowAttivitaAzione != null)
									&& (rowAttivitaAzione.getAttribute("ROW.CODTIPOATTIVITA") != null)
									&& (!rowAttivitaAzione.getAttribute("ROW.CODTIPOATTIVITA").toString().equals(""))) {
								String tipoAttivitaAz = rowAttivitaAzione.getAttribute("ROW.CODTIPOATTIVITA")
										.toString();
								if (Properties.Tirocini_Apprendistati_Azioni.containsKey(tipoAttivitaAz)) {
									if (!cfEntePromotore.equals("")) {
										if (messageCodeCF > 0) {
											throw new PercorsoConcordatoException(messageCodeCF);
										} else {
											cfEntePromotoreCurr = cfEntePromotore;
										}
									} else {
										if (codEsitoRendCurr.equalsIgnoreCase(ESITO_RENDIC_EROGATO)
												|| codEsitoRendCurr.equalsIgnoreCase(ESITO_RENDIC_EROGATO_NON_REND)) {
											throw new PercorsoConcordatoException(
													MessageCodes.Patto.ERR_CF_ENTE_PROMOTORE);
										} else {
											if (codEsitoPercorso.equalsIgnoreCase(esitoAvviato)
													|| codEsitoPercorso.equalsIgnoreCase(esitoConcluso)
													|| codEsitoPercorso.equalsIgnoreCase(esitoInterrotto)
													|| codEsitoPercorso.equalsIgnoreCase(esitoRifiutato)) {
												throw new PercorsoConcordatoException(
														MessageCodes.Patto.ERR_CF_ENTE_PROMOTORE_ESITO);
											}
										}
									}
								}
							}
							obj[21] = cfEntePromotoreCurr;

							// in caso di azione Garanzia Giovani si deve legare all'adesione GG
							obj[22] = prgColloquioAdesioneCurr;
							obj[23] = prgPercorsoAdesioneCurr;
							obj[24] = request.getAttribute("datAvvioAzione");

							// Borriello: gestione operatori azione
							obj[25] = request.getAttribute("prgOperatorePROPSpi");
							obj[26] = request.getAttribute("prgOperatoreAVVSpi");
							obj[27] = request.getAttribute("prgOperatoreCONCSpi");
							if (isAzionePresaInCarico) {
								obj[28] = datDichiarazioneAzione;
							} else {
								obj[28] = null;
							}
							obj[29] = request.getAttribute("codEnte");

							if (StringUtils.isFilledNoBlank(prgRDC)) {
								BigDecimal prgRedditoCitt = new BigDecimal(prgRDC);
								obj[30] = prgRedditoCitt;
							} else {
								obj[30] = null;
							}

							ris = (Boolean) transExec.executeQuery("INSERT_PERCORSO", obj, "INSERT");
						} else {
							throw new PercorsoConcordatoException(MessageCodes.Patto.AZIONE_PERC_CONCORD_ESISTENTE);
						}

						if (!ris.booleanValue() && !azioneEsistente) {
							throw new Exception("Fallito inserimento in or_percorso_concordato in transazione");
						}

						if (!resultIns) {
							if (!flgConclusa.equalsIgnoreCase(Values.FLAG_TRUE)) {
								if (dataFineProgrammaDB != null && !dataFineProgrammaDB.equals("")) {
									// prima di riaprire si deve controllare che non esiste un altro programma aperto
									this.setSectionQuerySelect("QUERY_SELECT_EXIST_PROGRAMMA_APERTO");
									SourceBean rowsP = doSelect(request, response);
									BigDecimal numCollAperti = (BigDecimal) rowsP
											.getAttribute("ROW.numProgrammiAperti");
									if (numCollAperti.intValue() > 0) {
										throw new PercorsoConcordatoException(
												MessageCodes.Patto.COLLOQUIO_PROGRAMMA_GIA_APERTO);
									} else {
										numklocoll = numklocoll.add(new BigDecimal(1));
										request.setAttribute("NUMKLOCOLLPROGRAMMA", numklocoll);
										this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
										boolean retProgramma = this.doUpdate(request, response);
										if (!retProgramma) {
											throw new Exception("Errore aggiornamento programma.");
										}
									}
								}
							} else {
								this.setSectionQuerySelect("QUERY_SELECT_AZIONI_IN_CORSO");
								SourceBean rowAzioni = doSelect(request, response, false);
								BigDecimal numAzioniInCorso = (BigDecimal) rowAzioni
										.getAttribute("ROW.numazioniincorso");
								if (numAzioniInCorso.intValue() == 0) {
									String dataConclusione = (String) request.getAttribute("DATEFFETTIVA");
									if (dataConclusione != null && !dataConclusione.equals("")) {
										if (dataFineProgrammaDB == null || dataFineProgrammaDB.equals("")
												|| DateUtils.compare(dataConclusione, dataFineProgrammaDB) > 0) {
											numklocoll = numklocoll.add(new BigDecimal(1));
											request.setAttribute("NUMKLOCOLLPROGRAMMA", numklocoll);
											request.setAttribute("DATAFINECOLLPROGRAMMA", dataConclusione);
											this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
											boolean retProgramma = this.doUpdate(request, response);
											if (!retProgramma) {
												throw new Exception("Errore aggiornamento programma.");
											}
										}
									}
								}
							}
						}

						// Associazione col patto
						if (associazioneConPatto) {
							PattoManager patto = new PattoManager(this, transExec);
							if (request.getAttribute("PRG_TAB") != null) {
								request.delAttribute("PRG_TAB");
							}

							request.setAttribute("PRG_TAB", prgPercorso);
							if (settaPattoRequest && prgPattoAperto != null) {
								if (request.containsAttribute("PRG_PATTO_LAVORATORE")) {
									request.delAttribute("PRG_PATTO_LAVORATORE");
								}
								if (request.containsAttribute("operazioneColPatto")) {
									request.delAttribute("operazioneColPatto");
								}
								request.setAttribute("PRG_PATTO_LAVORATORE", prgPattoAperto);
								request.setAttribute("operazioneColPatto", "-1");
							}

							associazioneAlPatto = patto.execute(request, response);
							if (!associazioneAlPatto)
								throw new PercorsoConcordatoException(MessageCodes.Patto.ASSOC_PATTO_PERC_CONCORD);
							else {
								if (request.getAttribute("PRG_OR_PER") != null) {
									request.delAttribute("PRG_OR_PER");
								}
								request.setAttribute("PRG_OR_PER", prgPercorso);
								AssociazioneImpegni assImp = new AssociazioneImpegni(
										request.getAttribute("PRG_PATTO_LAVORATORE").toString(), "OR_PER", request,
										transExec);
								boolean esito = assImp.insertImpegni();
								if (!esito)
									throw new Exception("Errore nell'associazione degli impegni");
								// Eventuale aggiornamento della data di
								// scadenza del patto
								if (aggiornaDataScadenzaPatto)
									assImp.aggiornaDataScadenzaPatto(
											assImp.getDataStimata(request.getAttribute("DATSTIMATA").toString()));
							}
						}

						transExec.commitTransaction();
						resultIns = true;

						_logger.debug("Record inserito su OR_PERCORSO-CONCORDATO con prg = " + obj[0]);
					} // if (prgPercorso != null)
					else {
						throw new Exception("Fallito inserimento in or_percorso_concordato");
					}
				} catch (PercorsoConcordatoException tEx) {
					reportOperation.reportFailure(((PercorsoConcordatoException) tEx).getMessageIdFail());
					if (transExec != null)
						transExec.rollBackTransaction();
				} catch (Exception tEx) {
					reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"service(): errore nell'inserimento delle azioni multiple e/o associazione al patto.", tEx);
					if (transExec != null)
						transExec.rollBackTransaction();
				}
			} // for (Iterator iter = elencoElementi.iterator();
				// iter.hasNext(); )

			if (resultIns) {
				// PROROGA PATTO
				/*
				 * commentato in data 19/12/2018 nuova gestione patto 150 if
				 * (rinnovaPatto.equalsIgnoreCase(Values.FLAG_TRUE) && pattoProrogabile) { TransactionQueryExecutor
				 * txExecutor = null; try { txExecutor = new TransactionQueryExecutor(getPool(), this);
				 * txExecutor.initTransaction();
				 * 
				 * String oggi = DateUtils.getNow(); int annoDataScadenza = DateUtils.getAnno(oggi) + 1; Vector
				 * vettDataScadenza = StringUtils.split(oggi, "/"); String dataScadenzaNew =
				 * vettDataScadenza.get(0).toString() + "/" + vettDataScadenza.get(1).toString() + "/" +
				 * annoDataScadenza; String strNoteProroga = "Patto prorogato automaticamente in data " + oggi; Object
				 * []paramsUpd = new Object[]{request.getAttribute("NUMKLOPATTOPROROGA"), strNoteProroga,
				 * dataScadenzaNew, getRequestContainer().getSessionContainer().getAttribute("_CDUT_"),
				 * prgPattoProrogato};
				 * 
				 * Boolean res = (Boolean )txExecutor.executeQuery("PROROGA_PATTO_ESITO_AZIONE_RINNOVO", paramsUpd,
				 * "UPDATE"); if (res == null || !res.booleanValue()) { throw new Exception("Errore proroga del patto");
				 * }
				 * 
				 * //Gestione conferma periodica (esito in corso o proposto e data stimata tra data stipula patto e data
				 * scadenza) if (!confermaPeriodicaInInserimento) { boolean confermaProrogata = false; boolean
				 * confermaConclusa = false; Object []paramsConferma = new
				 * Object[]{request.getAttribute("CDNLAVORATORE"), request.getAttribute("DATASTIPULAPROROGA"),
				 * request.getAttribute("DATSCADENZAPROROGA"), prgAzioneConferma}; SourceBean rowConferma = (SourceBean)
				 * txExecutor.executeQuery("GET_AZIONE_CONFERMA_PERIODICA_PROROGA_PATTO", paramsConferma, "SELECT");
				 * //Se esiste la conferma periodica con esito in corso o proposto, allora prorogo anche la conferma
				 * periodica if (rowConferma != null) { rowConferma =
				 * (rowConferma.containsAttribute("ROW")?(SourceBean)rowConferma.getAttribute("ROW"):rowConferma);
				 * BigDecimal prgPercorsoConferma = (BigDecimal)rowConferma.getAttribute("prgpercorso"); BigDecimal
				 * prgColloquioConferma = (BigDecimal)rowConferma.getAttribute("prgcolloquio");
				 * 
				 * if (prgPercorsoConferma != null && prgColloquioConferma != null) { Object []paramsUpdConferma = new
				 * Object[]{dataScadenzaNew, getRequestContainer().getSessionContainer().getAttribute("_CDUT_"),
				 * prgPercorsoConferma, prgColloquioConferma};
				 * 
				 * res = (Boolean )txExecutor.executeQuery("UPD_PROROGA_CONFERMA_PERIODICA", paramsUpdConferma,
				 * "UPDATE"); if (res == null || !res.booleanValue()) { throw new Exception("Errore proroga del patto");
				 * } else { confermaProrogata = true; } } }
				 * 
				 * //Se esiste la conferma periodica con esito concluso, allora inserisco una nuova conferma periodica
				 * nello stesso colloquio if (!confermaProrogata) { paramsConferma = new
				 * Object[]{request.getAttribute("CDNLAVORATORE"), request.getAttribute("DATASTIPULAPROROGA"),
				 * request.getAttribute("DATSCADENZAPROROGA"), prgAzioneConferma}; rowConferma = (SourceBean)
				 * txExecutor.executeQuery("GET_AZIONE_CONFERMA_PERIODICA_CONCLUSA_PROROGA_PATTO", paramsConferma,
				 * "SELECT"); if (rowConferma != null) { rowConferma =
				 * (rowConferma.containsAttribute("ROW")?(SourceBean)rowConferma.getAttribute("ROW"):rowConferma);
				 * BigDecimal prgPercorsoConferma = (BigDecimal)rowConferma.getAttribute("prgpercorso"); BigDecimal
				 * prgColloquioConferma = (BigDecimal)rowConferma.getAttribute("prgcolloquio");
				 * 
				 * if (prgPercorsoConferma != null && prgColloquioConferma != null) { BigDecimal prgPercorsoNew =
				 * PattoBean.getProgressivoPercorso(txExecutor); boolean isOK =
				 * PattoBean.inserisciAzione(prgPercorsoNew, prgColloquioConferma, esitoInCorso,
				 * esitoRendicontatoPrenotato, dataScadenzaNew, prgAzioneConferma, txExecutor, cdnUtente); if (!isOK) {
				 * throw new Exception("Errore proroga del patto"); } isOK =
				 * PattoBean.collegaAzionePatto(prgPattoProrogato, codLstTabAzionePatto, prgPercorsoNew, txExecutor); if
				 * (!isOK) { throw new Exception("Errore proroga del patto"); } confermaConclusa = true; } } }
				 * 
				 * if (!confermaProrogata && !confermaConclusa) { // Inserimento colloquio per la Conferma Periodica
				 * BigDecimal prgcolloquioConfermaPeriodica = PattoBean.getProgressivoColloquio(txExecutor); String
				 * codServizioColloquio = PattoBean.getServizioConfermaPeriodica(ServizioConfermaPeriodica, txExecutor);
				 * if (prgcolloquioConfermaPeriodica == null) { throw new Exception("Errore proroga del patto"); }
				 * String codCpiPatto = StringUtils.getAttributeStrNotNull(request, "CODCPIPATTOPROROGA"); BigDecimal
				 * operatoreSPI = PattoBean.getOperatoreAdmin(txExecutor);
				 * 
				 * boolean isOK = PattoBean.inserisciColloquio(prgcolloquioConfermaPeriodica,
				 * request.getAttribute("CDNLAVORATORE"), codServizioColloquio, oggi, codCpiPatto, noteColloquio,
				 * txExecutor, cdnUtente, operatoreSPI); if (!isOK) { throw new Exception("Errore proroga del patto"); }
				 * isOK = PattoBean.inserisciSchedaColloquio(prgcolloquioConfermaPeriodica, txExecutor); if (!isOK) {
				 * throw new Exception("Errore proroga del patto"); }
				 * 
				 * BigDecimal prgPercorsoNew = PattoBean.getProgressivoPercorso(txExecutor);
				 * 
				 * isOK = PattoBean.inserisciAzione(prgPercorsoNew, prgcolloquioConfermaPeriodica, esitoInCorso,
				 * esitoRendicontatoPrenotato, dataScadenzaNew, prgAzioneConferma, txExecutor, cdnUtente); if (!isOK) {
				 * throw new Exception("Errore proroga del patto"); } isOK =
				 * PattoBean.collegaAzionePatto(prgPattoProrogato, codLstTabAzionePatto, prgPercorsoNew, txExecutor); if
				 * (!isOK) { throw new Exception("Errore proroga del patto"); } } } txExecutor.commitTransaction();
				 * reportOperation.reportSuccess(MessageCodes.Patto.PATTO_PROROGATO_OK); } catch (Exception tEx) {
				 * it.eng.sil.util.TraceWrapper.debug(_logger, "service(): errore nella proroga del patto", tEx); if
				 * (txExecutor != null) { txExecutor.rollBackTransaction(); }
				 * reportOperation.reportFailure(MessageCodes.Patto.PATTO_PROROGATO_KO); } }
				 */
				reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
			}
		} catch (PercorsoConcordatoException ex) {
			reportOperation.reportFailure(((PercorsoConcordatoException) ex).getMessageIdFail());
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, ex, "execute()",
					"Fallito inserimento percorsi concordati.");
			it.eng.sil.util.TraceWrapper.debug(_logger, "service(): Fallito inserimento percorsi concordati.", ex);

		}
	}// end method

	// TODO INIZIO PEZZO AGGIUNTO DA VALE
	/**
	 * Controlla che l'azione abbia una data minore di data avvio più sei mesi
	 */
	private boolean controllaDataAzione(String prgProgramma, String data, String cdnLavoratore,
			TransactionQueryExecutor tExec) throws Exception {
		boolean exist = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgProgramma);
		param[2] = data;
		SourceBean row = (SourceBean) tExec.executeQuery("GET_AZIONE_PERCORSO", param, "SELECT");
		if ((row != null) && (row.getAttribute("ROW.PRGPERCORSO") != null)
				&& !row.getAttribute("ROW.PRGPERCORSO").equals(""))
			exist = true;
		return exist;
	}
	// TODO FINE PEZZO AGGIUNTO DA VALE

	/**
	 * Controlla che l'azione specificata, nella data specificata non sia già stata inserita per il colloquio
	 * specificato
	 */
	private boolean controllaEsistenzaAzione(String prgAzione, String data, String cdnLavoratore,
			TransactionQueryExecutor tExec) throws Exception {
		boolean exist = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgAzione);
		param[2] = data;
		SourceBean row = (SourceBean) tExec.executeQuery("GET_AZIONE_PERCORSO", param, "SELECT");
		if ((row != null) && (row.getAttribute("ROW.PRGPERCORSO") != null)
				&& !row.getAttribute("ROW.PRGPERCORSO").equals(""))
			exist = true;
		return exist;
	}

	private boolean controllaEsistenzaAzione(String prgAzione, String data, String cdnLavoratore) throws Exception {
		boolean exist = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgAzione);
		param[2] = data;
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_AZIONE_PERCORSO", param, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			Vector adesioniGG = row.getAttributeAsVector("ROW");
			if (adesioniGG.size() > 0) {
				exist = true;
			}
		}
		return exist;
	}

	private boolean controllaEsistenzaAdesione(String prgAzione, String data, String cdnLavoratore) throws Exception {
		boolean exist = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgAzione);
		param[2] = data;
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_ESISTE_ADESIONE_PERCORSO", param, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			Vector adesioniGG = row.getAttributeAsVector("ROW");
			if (adesioniGG.size() > 0) {
				exist = true;
			}
		}
		return exist;

	}

	private boolean controllaEtaLavoratoreAdesione(String cdnLavoratore, BigDecimal prgPercorsoAdesione,
			BigDecimal prgColloquiAdesione) throws Exception {
		boolean etaOK = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = prgPercorsoAdesione;
		param[2] = prgColloquiAdesione;
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_ETA_LAVORATORE_ADESIONE", param, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			Vector adesione = row.getAttributeAsVector("ROW");
			if (adesione.size() >= 1) {
				SourceBean lav = (SourceBean) adesione.get(0);
				String etaAdesione = lav.containsAttribute("eta") ? lav.getAttribute("eta").toString() : "";
				if (!etaAdesione.equals("")) {
					Integer eta = new Integer(etaAdesione);
					if (eta.intValue() >= 0 && eta.intValue() < ETA_YEI_TIROCINIO_EXTRACURRICULARE) {
						etaOK = true;
					}
				}
			}
		}
		return etaOK;
	}

	/**
	 * Esegue una serie di controlli prima dell'inserimento di un nuovo percorso.
	 * 
	 * @param request
	 * @param response
	 * @param elencoAzioni
	 * @throws PercorsoConcordatoException
	 */
	private void controlliCIG(SourceBean request, SourceBean response, Collection elencoAzioni)
			throws PercorsoConcordatoException {

		String codEsitoRendicont = (String) request.getAttribute("CODESITORENDICONT");

		/* Se esito rendicontazione è EROGATO */
		if ("E".equals(codEsitoRendicont)) {
			Vector rowsPercorsi = getServiceResponse().getAttributeAsVector("M_ListPercorsi.ROWS.ROW");
			/* conto i percorsi PIC con esito erogato */
			int countPCIG = 0;
			if (rowsPercorsi.size() >= 1) {
				for (Iterator iter = rowsPercorsi.iterator(); iter.hasNext();) {
					SourceBean attributeValueSB = (SourceBean) iter.next();
					String attributeValueAz = String.valueOf((BigDecimal) attributeValueSB.getAttribute("PRGAZIONI"));
					String attributeCodEsitoRendicont = (String) attributeValueSB.getAttribute("CODESITORENDICONT");
					// 151 = Presa In Carico CIG
					if ("151".equals(attributeValueAz) && "E".equals(attributeCodEsitoRendicont)) {
						countPCIG += 1;
					}
				}
			}

			/* se non vi sono PIC erogate e sto cercando di inserire una non PIC, lancio l'eccezione */
			if (countPCIG == 0) {
				if (!elencoAzioni.contains("151")) {
					/* la prima azione erogata deve essere una PIC */
					throw new PercorsoConcordatoException(MessageCodes.Patto.PRIMA_AZIONE_PERC_CONCORD_CIG);
				}
			}
			/* se vi è la PIC erogata e sto cercando di inserire una PIC erogata, lancio l'eccezione */
			else if (countPCIG == 1) {
				if (elencoAzioni.contains("151")) {
					/* vi può essere una sola azione PIC erogata */
					throw new PercorsoConcordatoException(MessageCodes.Patto.UNICA_AZIONE_PERC_CONCORD_CIG);
				}
			}

			this.setSectionQuerySelect("QUERY_SELECT_MAX_PRESTAZIONE");
			SourceBean rowMaxPrestCIG = this.doSelect(request, response);
			int numMaxRipetizioni = ((BigDecimal) rowMaxPrestCIG.getAttribute("ROW.numMaxRipetizioni")).intValue();

			if (numMaxRipetizioni != 0) {
				this.setSectionQuerySelect("QUERY_SELECT_PRESTAZIONE");
				SourceBean rowPrestEsistCIG = this.doSelect(request, response);
				int numRipetizioniEsistenti = ((BigDecimal) rowPrestEsistCIG
						.getAttribute("ROW.numRipetizioniEsistenti")).intValue();
				if ((numRipetizioniEsistenti + elencoAzioni.size()) > numMaxRipetizioni) {
					/*
					 * non vi possono essere più di numMaxRipetizioni azioni con lo stesso codserviziocig erogate legato
					 * al colloquio
					 */
					throw new PercorsoConcordatoException(MessageCodes.Patto.PRESTAZIONICIG_NUMMAXSESSIONI_INS);
				}
			}
		}
	}

	private BigDecimal getPrgAzioneConfermaPeriodica() throws Exception {
		BigDecimal prgAzione = null;
		Object[] params = new Object[] { ConfermaPeriodica };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("WS_GET_PRG_AZIONE_CONFERMA_PERIODICA", params,
				"SELECT", Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgAzione = (BigDecimal) row.getAttribute("prgazioni");
		}
		return prgAzione;
	}

}
