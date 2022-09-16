package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PercorsoConcordatoException;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class UpdatePercorso extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -279996068973235570L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdatePercorso.class.getName());

	private int codiceErrore = -1;
	private String dettaglioErrore = "";

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

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		boolean azioneCambiata = request.containsAttribute("AZIONECAMBIATA")
				&& request.getAttribute("AZIONECAMBIATA").equals("true");
		boolean conPatto = request.containsAttribute("CONPATTO") && request.getAttribute("CONPATTO").equals("true");
		BigDecimal cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		BigDecimal prgPattoDaDeassociare = null;
		try {

			this.disableMessageIdSuccess();
			this.disableMessageIdFail();
			transExec = new TransactionQueryExecutor(getPool());
			PattoManager patto = new PattoManager(this, transExec);
			enableTransactions(transExec);
			transExec.initTransaction();
			AssociazioneImpegni assImp = new AssociazioneImpegni(
					request.getAttribute("PRG_PATTO_LAVORATORE").toString(), "OR_PER", request, transExec);
			boolean opImpEseguita = false;
			String pref = "";
			SourceBean amLavPatto = null;
			/*
			 * Controllo programma 186. Non possono essere avviate se dalla data del programma sono trascorsi 180 giorni
			 */
			/*
			 * controllo eliminato 17/02/2020 segnalazione redmine numero 8233
			 */
			String codServizio = StringUtils.getAttributeStrNotNull(request, "codServizio");
			String datInizioProgramma = StringUtils.getAttributeStrNotNull(request, "datInizioProgramma");
			String datAvvioAzione = StringUtils.getAttributeStrNotNull(request, PattoBean.DB_DAT_AVVIO_AZIONE);
			String codEsitoPercorso = StringUtils.getAttributeStrNotNull(request, "CODESITO");
			/*
			 * if(codEsitoPercorso.equalsIgnoreCase(esitoAvviato) &&
			 * (codServizio.equalsIgnoreCase(PattoBean.DB_COD_SERVIZIO_186) ||
			 * codServizio.equalsIgnoreCase(PattoBean.DB_MISURE_NUOVA_GARANZIA_GIOVANI))) { boolean is180Giorni_186 =
			 * PattoBean.is180Giorni_186(datInizioProgramma, datAvvioAzione); if (!is180Giorni_186) throw new
			 * PercorsoConcordatoException(MessageCodes.Patto.ERR_PROG_186_AZIONE_DATA_AVVIO); }
			 */

			if (PattoManager.withPatto(request)) {
				if (((String) request.getAttribute("operazioneColPatto")).equals("1")) {
					if (PattoManager.esisteVoucher(transExec, request.getAttribute("PRG_LAV_PATTO_SCELTA"))) {
						throw new PercorsoConcordatoException(
								MessageCodes.MSGVOUCHER.ERRORE_ESISTE_VOUCHER_AZIONE_PATTO);
					}
					// Cancella la associazione
					assImp.cancellaImpegniAssociati(request.getAttribute("PRG_LAV_PATTO_SCELTA").toString());
					opImpEseguita = true;
				}
				// int il = transExec.getTransaciontIsalation();
				// this.setSectionQueryUpdate("UPDATE_IND_T");
				setSectionQuerySelect("QUERY_SELECT_INFO");
				amLavPatto = doSelect(request, response, false);
				TracciaModifichePatto.cancellazione(getRequestContainer(), amLavPatto, transExec);
			} else {
				if (azioneCambiata && conPatto) {
					if (PattoManager.esisteVoucher(transExec, request.getAttribute("PRG_LAV_PATTO_SCELTA"))) {
						throw new PercorsoConcordatoException(
								MessageCodes.MSGVOUCHER.ERRORE_ESISTE_VOUCHER_AZIONE_PATTO);
					}
					// Cancello gli impegni dell'azione vecchia
					assImp.cancellaImpegniAssociati(request.getAttribute("PRG_LAV_PATTO_SCELTA").toString());
				}
				setSectionQuerySelect("QUERY_SELECT_INFO");
				amLavPatto = doSelect(request, response, false);
			}
			if (amLavPatto.containsAttribute("rows")) {
				pref = "ROWS.";
			} else {
				pref = "";
			}
			prgPattoDaDeassociare = (BigDecimal) amLavPatto.getAttribute(pref + "row.prgPattoLavoratore");

			String prgPercorso = (String) request.getAttribute("PRGPERCORSO");
			String prgColloquio = (String) request.getAttribute("PRGCOLLOQUIO");
			Object prgColloquioObj = request.getAttribute("PRGCOLLOQUIO");
			String prgAzione = (String) request.getAttribute("prgAzioni");
			String lavoratore = (String) request.getAttribute("CDNLAVORATORE");
			String rinnovaPatto = StringUtils.getAttributeStrNotNull(request, "RINNOVAPATTO");
			String codStatoPattoProroga = StringUtils.getAttributeStrNotNull(request, "CODSTATOPATTOPROROGA");
			String flgPattoProroga = StringUtils.getAttributeStrNotNull(request, "FLGPATTO297PROROGA");
			String tipoPattoProroga = StringUtils.getAttributeStrNotNull(request, "CODTIPOPATTOPROROGA");
			Object prgPattoProrogato = request.getAttribute("PRGPATTOPROROGA");
			Object prgPattoCollegatoAzione = request.getAttribute("PRG_PATTO_LAVORATORE");
			boolean pattoProrogabile = false;
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
			 * 
			 * //PROROGA PATTO if (rinnovaPatto.equalsIgnoreCase(Values.FLAG_TRUE) && pattoProrogabile) { String oggi =
			 * DateUtils.getNow(); int annoDataScadenza = DateUtils.getAnno(oggi) + 1; Vector vettDataScadenza =
			 * StringUtils.split(oggi, "/"); String dataScadenzaNew = vettDataScadenza.get(0).toString() + "/" +
			 * vettDataScadenza.get(1).toString() + "/" + annoDataScadenza;
			 * 
			 * String strNoteProroga = "Patto prorogato automaticamente in data " + oggi; Object []paramsUpd = new
			 * Object[]{request.getAttribute("NUMKLOPATTOPROROGA"), strNoteProroga, dataScadenzaNew,
			 * getRequestContainer().getSessionContainer().getAttribute("_CDUT_"), prgPattoProrogato};
			 * 
			 * Boolean res = (Boolean )transExec.executeQuery("PROROGA_PATTO_ESITO_AZIONE_RINNOVO", paramsUpd,
			 * "UPDATE"); if (res == null || !res.booleanValue()) { throw new Exception("Errore proroga del patto"); }
			 * 
			 * //Gestione conferma periodica (esito in corso o proposto e data stimata tra data stipula patto e data
			 * scadenza) BigDecimal prgAzioneConferma = getPrgAzioneConfermaPeriodica(transExec); if
			 * (!prgAzioneConferma.equals(new BigDecimal(prgAzione))) { boolean confermaProrogata = false; boolean
			 * confermaConclusa = false; Object []paramsConferma = new Object[]{request.getAttribute("CDNLAVORATORE"),
			 * request.getAttribute("DATASTIPULAPROROGA"), request.getAttribute("DATSCADENZAPROROGA"),
			 * prgAzioneConferma}; SourceBean rowConferma = (SourceBean)
			 * transExec.executeQuery("GET_AZIONE_CONFERMA_PERIODICA_PROROGA_PATTO", paramsConferma, "SELECT"); //Se
			 * esiste la conferma periodica con esito in corso o proposto, allora prorogo anche la conferma periodica if
			 * (rowConferma != null) { rowConferma =
			 * (rowConferma.containsAttribute("ROW")?(SourceBean)rowConferma.getAttribute("ROW"):rowConferma);
			 * BigDecimal prgPercorsoConferma = (BigDecimal)rowConferma.getAttribute("prgpercorso"); BigDecimal
			 * prgColloquioConferma = (BigDecimal)rowConferma.getAttribute("prgcolloquio");
			 * 
			 * if (prgPercorsoConferma != null && prgColloquioConferma != null) { Object []paramsUpdConferma = new
			 * Object[]{dataScadenzaNew, getRequestContainer().getSessionContainer().getAttribute("_CDUT_"),
			 * prgPercorsoConferma, prgColloquioConferma};
			 * 
			 * res = (Boolean )transExec.executeQuery("UPD_PROROGA_CONFERMA_PERIODICA", paramsUpdConferma, "UPDATE"); if
			 * (res == null || !res.booleanValue()) { throw new Exception("Errore proroga del patto"); } else {
			 * confermaProrogata = true; } } }
			 * 
			 * //Se esiste la conferma periodica con esito concluso, allora inserisco una nuova conferma periodica nello
			 * stesso colloquio if (!confermaProrogata) { paramsConferma = new
			 * Object[]{request.getAttribute("CDNLAVORATORE"), request.getAttribute("DATASTIPULAPROROGA"),
			 * request.getAttribute("DATSCADENZAPROROGA"), prgAzioneConferma}; rowConferma = (SourceBean)
			 * transExec.executeQuery("GET_AZIONE_CONFERMA_PERIODICA_CONCLUSA_PROROGA_PATTO", paramsConferma, "SELECT");
			 * if (rowConferma != null) { rowConferma =
			 * (rowConferma.containsAttribute("ROW")?(SourceBean)rowConferma.getAttribute("ROW"):rowConferma);
			 * BigDecimal prgPercorsoConferma = (BigDecimal)rowConferma.getAttribute("prgpercorso"); BigDecimal
			 * prgColloquioConferma = (BigDecimal)rowConferma.getAttribute("prgcolloquio");
			 * 
			 * if (prgPercorsoConferma != null && prgColloquioConferma != null) { BigDecimal prgPercorsoNew =
			 * PattoBean.getProgressivoPercorso(transExec); boolean isOK = PattoBean.inserisciAzione(prgPercorsoNew,
			 * prgColloquioConferma, esitoInCorso, esitoRendicontatoPrenotato, dataScadenzaNew, prgAzioneConferma,
			 * transExec, cdnUtente); if (!isOK) { throw new Exception("Errore proroga del patto"); } isOK =
			 * PattoBean.collegaAzionePatto(prgPattoProrogato, codLstTabAzionePatto, prgPercorsoNew, transExec); if
			 * (!isOK) { throw new Exception("Errore proroga del patto"); } confermaConclusa = true; } } }
			 * 
			 * if (!confermaProrogata && !confermaConclusa) { // Inserimento colloquio per la Conferma Periodica
			 * BigDecimal prgcolloquioConfermaPeriodica = PattoBean.getProgressivoColloquio(transExec); String
			 * codServizioColloquio = PattoBean.getServizioConfermaPeriodica(ServizioConfermaPeriodica, transExec); if
			 * (prgcolloquioConfermaPeriodica == null) { throw new Exception("Errore proroga del patto"); } String
			 * codCpiPatto = StringUtils.getAttributeStrNotNull(request, "CODCPIPATTOPROROGA"); BigDecimal operatoreSPI
			 * = PattoBean.getOperatoreAdmin(transExec);
			 * 
			 * boolean isOK = PattoBean.inserisciColloquio(prgcolloquioConfermaPeriodica,
			 * request.getAttribute("CDNLAVORATORE"), codServizioColloquio, oggi, codCpiPatto, noteColloquio, transExec,
			 * cdnUtente, operatoreSPI); if (!isOK) { throw new Exception("Errore proroga del patto"); } isOK =
			 * PattoBean.inserisciSchedaColloquio(prgcolloquioConfermaPeriodica, transExec); if (!isOK) { throw new
			 * Exception("Errore proroga del patto"); }
			 * 
			 * BigDecimal prgPercorsoNew = PattoBean.getProgressivoPercorso(transExec);
			 * 
			 * isOK = PattoBean.inserisciAzione(prgPercorsoNew, prgcolloquioConfermaPeriodica, esitoInCorso,
			 * esitoRendicontatoPrenotato, dataScadenzaNew, prgAzioneConferma, transExec, cdnUtente); if (!isOK) { throw
			 * new Exception("Errore proroga del patto"); } isOK = PattoBean.collegaAzionePatto(prgPattoProrogato,
			 * codLstTabAzionePatto, prgPercorsoNew, transExec); if (!isOK) { throw new
			 * Exception("Errore proroga del patto"); } } }
			 * reportOperation.reportSuccess(MessageCodes.Patto.PATTO_PROROGATO_OK); }
			 */

			Vector<String> programmi = new Vector<String>();
			if (prgColloquioObj != null) {
				programmi = PattoBean.checkProgrammaColloquio(prgColloquioObj, transExec);
			}

			String dataAzione = (String) request.getAttribute("datStimata");
			String codSceltaAdesione = StringUtils.getAttributeStrNotNull(request, "prgKeyAdesione");
			String datAdesioneGG = StringUtils.getAttributeStrNotNull(request, "datAdesioneGG");

			String prgAzioneRagg = StringUtils.getAttributeStrNotNull(request, "prgAzioneRagg");

			boolean azioneEsistente = controllaEsistenzaAzione(prgAzione, dataAzione, lavoratore, prgPercorso,
					transExec);

			if (azioneEsistente) {
				throw new PercorsoConcordatoException(MessageCodes.Patto.AZIONE_PERC_CONCORD_ESISTENTE);
			}

			String flgAdesioneGG = "";
			String codMonoPacchetto = "";
			String codazionesifer = "";
			String flgFormazioneAzione = "";
			String flgMisurayeiPA = "";
			String codProgettoAz = "";
			String codTipoAttivitaAz = "";
			SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_DE_AZIONE_BYPRG",
					new Object[] { new BigDecimal(prgAzione) }, "SELECT");
			if (rowAzione != null) {
				flgAdesioneGG = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.FLGADESIONEGG");
				codMonoPacchetto = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.CODMONOPACCHETTO");
				codazionesifer = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.codazionesifer");
				flgFormazioneAzione = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.flgFormazione");
				flgMisurayeiPA = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.flg_misurayei");
				codProgettoAz = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.codProgetto");
				codTipoAttivitaAz = StringUtils.getAttributeStrNotNull(rowAzione, "ROW.codtipoattivita");
				if (!(codTipoAttivitaAz.equalsIgnoreCase("A02") && codProgettoAz.equalsIgnoreCase("05"))) {
					if (request.containsAttribute("datDichAzione")) {
						request.delAttribute("datDichAzione");
					}
				}
			}

			String azioneInizialeDaModificare = StringUtils.getAttributeStrNotNull(request, "descAzioneDaModificare");
			if (azioneInizialeDaModificare.equals("Adesione GG")) {
				if (!flgAdesioneGG.equalsIgnoreCase("S")) {
					boolean azioneCollegataAdesione = controllaEsistenzaCollegamentoAdesione(lavoratore, prgPercorso,
							prgColloquio, transExec);
					if (azioneCollegataAdesione) {
						throw new PercorsoConcordatoException(
								MessageCodes.Patto.ERR_UPDATE_ADESIONE_COLLEGATA_POLITICHE_ATTIVE);
					}
				}
			}

			if (rowAzione != null) {
				if (flgAdesioneGG.equalsIgnoreCase("S")) {
					if (datAdesioneGG.equals("")) {
						throw new PercorsoConcordatoException(MessageCodes.Patto.DATA_ADESIONE_ASSENTE);
					}
					boolean adesioneEsistente = controllaEsistenzaAdesione(prgAzione, datAdesioneGG, lavoratore,
							prgPercorso, prgColloquio, transExec);
					if (adesioneEsistente) {
						throw new PercorsoConcordatoException(MessageCodes.Patto.ADESIONE_PERC_CONCORD_ESISTENTE);
					}
				}
				// CONTROLLI PER PACCHETTO ADULTI SE L'AZIONE E' COLLEGATA AL PATTO
				String operazioneColPatto = (String) request.getAttribute("operazioneColPatto");
				if ((PattoManager.withPatto(request) && operazioneColPatto != null && operazioneColPatto.equals("-1"))
						|| (azioneCambiata && conPatto)) {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI)
							|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
						UtilsConfig utility = new UtilsConfig(PacchettoAdulti.CONFIG_PA);
						String config = utility.getConfigurazioneDefault_Custom();
						if (config.equals(Properties.CUSTOM_CONFIG)) {
							int codiceErrorePA = PattoBean.checkPacchettoAdultiGaranziaGiovani(codMonoPacchetto,
									flgFormazioneAzione, codazionesifer);
							if (codiceErrorePA > 0) {
								throw new PercorsoConcordatoException(codiceErrorePA);
							}
						}
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
							int codiceErroreGGU = PattoBean.checkazioniPattoGGU(flgMisurayeiPA);
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
								|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)
								|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
							int codiceErrore = PattoBean.checkPacchettoAdultiOver3045INA(codMonoPacchetto,
									flgMisurayeiPA);
							if (codiceErrore > 0) {
								throw new PercorsoConcordatoException(codiceErrore);
							}
							/*
							 * if ( (flgFormazioneAzione.equalsIgnoreCase(Values.FLAG_TRUE)) &&
							 * (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06) ||
							 * codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C07)) ) { int codiceErrorePA =
							 * PattoBean.checkLimiteAzioniPacchettoAdulti(programmi, codMonoPacchetto, codazionesifer);
							 * if (codiceErrorePA > 0) { throw new PercorsoConcordatoException(codiceErrorePA); } }
							 */
						}
					}
				}
			}

			if (!prgAzioneRagg.equals("")) {
				BigDecimal prgAzioneRaggScelta = new BigDecimal(prgAzioneRagg);
				if (prgAzioneRaggScelta.equals(YEI_TIROCINIO_EXTRACURRICULARE)) {
					UtilsConfig utility = new UtilsConfig("ETA_TIR");
					String tipoConfig = utility.getConfigurazioneDefault_Custom();
					if (tipoConfig.equals(Properties.CUSTOM_CONFIG)) {
						if (!("-").equals(codSceltaAdesione) && !("").equals(codSceltaAdesione)) {
							String[] vettAdesioneEta = codSceltaAdesione.split("-");
							String prgPercorsoAdesioneEta = vettAdesioneEta[0];
							String prgColloquioAdesioneEta = vettAdesioneEta[1];
							boolean checkEtaLav = controllaEtaLavoratoreAdesione(
									request.getAttribute("CDNLAVORATORE").toString(), prgPercorsoAdesioneEta,
									prgColloquioAdesioneEta, transExec);
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
				controlliCIG(request, response);
			}

			String cfEntePromotore = StringUtils.getAttributeStrNotNull(request, "strCodiceFiscaleEnte");
			String codEsitoRendicont = StringUtils.getAttributeStrNotNull(request, "CODESITORENDICONT");
			// String codEsitoPercorso = StringUtils.getAttributeStrNotNull(request, "CODESITO");
			Object param[] = new Object[1];
			param[0] = prgAzione;
			SourceBean rowAttivitaAzione = (SourceBean) transExec.executeQuery("GET_TIPO_ATTIVITA_AZIONE", param,
					"SELECT");
			if ((rowAttivitaAzione != null) && (rowAttivitaAzione.getAttribute("ROW.CODTIPOATTIVITA") != null)
					&& (!rowAttivitaAzione.getAttribute("ROW.CODTIPOATTIVITA").toString().equals(""))) {
				String tipoAttivitaAz = rowAttivitaAzione.getAttribute("ROW.CODTIPOATTIVITA").toString();
				if (Properties.Tirocini_Apprendistati_Azioni.containsKey(tipoAttivitaAz)) {
					if (!cfEntePromotore.equals("")) {
						try {
							if (CF_utils.verificaParzialeCF(cfEntePromotore) != 0) {
								throw new PercorsoConcordatoException(MessageCodes.CodiceFiscale.ERR_LUNGHEZZA);
							}
						} catch (CfException eCF) {
							throw new PercorsoConcordatoException(eCF.getMessageIdFail());
						}
					} else {
						if (codEsitoRendicont.equalsIgnoreCase(ESITO_RENDIC_EROGATO)
								|| codEsitoRendicont.equalsIgnoreCase(ESITO_RENDIC_EROGATO_NON_REND)) {
							throw new PercorsoConcordatoException(MessageCodes.Patto.ERR_CF_ENTE_PROMOTORE);
						} else {
							if (codEsitoPercorso.equalsIgnoreCase(esitoAvviato)
									|| codEsitoPercorso.equalsIgnoreCase(esitoConcluso)
									|| codEsitoPercorso.equalsIgnoreCase(esitoInterrotto)
									|| codEsitoPercorso.equalsIgnoreCase(esitoRifiutato)) {
								throw new PercorsoConcordatoException(MessageCodes.Patto.ERR_CF_ENTE_PROMOTORE_ESITO);
							}
						}
					}
				}
			}

			if (!("-").equals(codSceltaAdesione) && !("").equals(codSceltaAdesione)) {
				String[] vettAdesione = codSceltaAdesione.split("-");
				request.setAttribute("prgColloquioAdesione", vettAdesione[1]);
				request.setAttribute("prgPercorsoAdesione", vettAdesione[0]);
			}

			ret = this.doUpdate(request, response);

			// lettura info colloquio per numklocolloquio e data fine programma
			SourceBean colloquio = (SourceBean) getServiceResponse().getAttribute("M_GetInfoColloquio.ROWS.ROW");
			BigDecimal numklocoll = (BigDecimal) colloquio.getAttribute("NUMKLOCOLLOQUIO");
			String dataFineProgrammaDB = (String) colloquio.getAttribute("DATAFINEPROGRAMMA");
			numklocoll = numklocoll.add(new BigDecimal(1));
			request.setAttribute("NUMKLOCOLLPROGRAMMA", numklocoll);

			this.setSectionQuerySelect("QUERY_SELECT_AZIONI_IN_CORSO");
			SourceBean rowAzioni = doSelect(request, response, false);
			BigDecimal numAzioniInCorso = (BigDecimal) rowAzioni.getAttribute("ROW.numazioniincorso");
			if (numAzioniInCorso.intValue() == 0) {
				String dataMaxFineProgramma = null;
				this.setSectionQuerySelect("QUERY_GET_MAX_DATA_CONCLUSIONE");
				SourceBean rowAzioniData = doSelect(request, response, false);
				if (rowAzioniData != null) {
					Vector<SourceBean> azioniData = rowAzioniData.getAttributeAsVector("ROW");
					if (azioniData.size() > 0) {
						SourceBean rowAzData = azioniData.get(0);
						dataMaxFineProgramma = rowAzData.getAttribute("datConclusione") != null
								? rowAzData.getAttribute("datConclusione").toString()
								: "";
					}
				}
				if (dataMaxFineProgramma == null || dataMaxFineProgramma.equals("")) {
					dataMaxFineProgramma = DateUtils.getNow();
				}
				request.setAttribute("DATAFINECOLLPROGRAMMA", dataMaxFineProgramma);
				this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
				boolean retProgramma = this.doUpdate(request, response);
				if (!retProgramma) {
					throw new Exception("Errore aggiornamento programma.");
				}
			} else {
				if (dataFineProgrammaDB != null && !dataFineProgrammaDB.equals("")) {
					// prima di riaprire si deve controllare che non esiste un altro programma aperto
					this.setSectionQuerySelect("QUERY_SELECT_EXIST_PROGRAMMA_APERTO");
					SourceBean rowsP = doSelect(request, response);
					BigDecimal numCollAperti = (BigDecimal) rowsP.getAttribute("ROW.numProgrammiAperti");
					if (numCollAperti.intValue() > 0) {
						throw new PercorsoConcordatoException(MessageCodes.Patto.COLLOQUIO_PROGRAMMA_GIA_APERTO);
					} else {
						this.setSectionQueryUpdate("QUERY_UPDATE_RIAPRI_CHIUDI_PROGRAMMA");
						boolean retProgramma = this.doUpdate(request, response);
						if (!retProgramma) {
							throw new Exception("Errore aggiornamento programma.");
						}
					}
				}
			}

			_logger.debug(
					"Record aggiornato su OR_PERCORSO-CONCORDATO con prg = " + request.getAttribute("PRGPERCORSO"));

			if (PattoManager.withPatto(request)) {
				BigDecimal prgPercorsoKey = null;
				if (prgPercorso != null && !prgPercorso.equals("")) {
					prgPercorsoKey = new BigDecimal(prgPercorso);
				}
				if (ret) {
					String op = (String) request.getAttribute("operazioneColPatto");
					// Inserisco associazione con il patto e quindi controllo se l'azione GG è stata già associata a un
					// altro patto
					if (op != null && op.equals("-1")) {
						String prgPattoLavoratore = request.containsAttribute("PRG_PATTO_LAVORATORE")
								? request.getAttribute("PRG_PATTO_LAVORATORE").toString()
								: "";
						BigDecimal prgPattoLav = null;
						if (!prgPattoLavoratore.equals("")) {
							prgPattoLav = new BigDecimal(prgPattoLavoratore);
						}
						if (prgPercorsoKey != null) {
							SourceBean azione = getDataAzione(prgPercorsoKey, transExec);
							if (azione != null) {
								String dataStimataAzione = azione.containsAttribute("ROW.DATSTIMATA")
										? azione.getAttribute("ROW.DATSTIMATA").toString()
										: "";
								String flgMisurayei = azione.containsAttribute("ROW.flg_misurayei")
										? azione.getAttribute("ROW.flg_misurayei").toString()
										: "";
								String flgAdesione = azione.containsAttribute("ROW.flgAdesioneGG")
										? azione.getAttribute("ROW.flgAdesioneGG").toString()
										: "";
								if ((flgMisurayei.equalsIgnoreCase("S") || flgAdesione.equalsIgnoreCase("S"))
										&& (azione.getAttribute("ROW.PRGPATTODISASSOCIATOFORMAZIONE") != null)) {
									BigDecimal prgPattoDiAssociato = (BigDecimal) azione
											.getAttribute("ROW.PRGPATTODISASSOCIATOFORMAZIONE");
									if (prgPattoLav != null && prgPattoDiAssociato.compareTo(prgPattoLav) != 0) {
										String azioneRagg = azione.containsAttribute("ROW.descrizioneAzRagg")
												? azione.getAttribute("ROW.descrizioneAzRagg").toString()
												: "";
										String descAzione = azione.containsAttribute("ROW.descrizioneAz")
												? azione.getAttribute("ROW.descrizioneAz").toString()
												: "";
										codiceErrore = MessageCodes.Patto.ERR_AZIONE_PRECEDENTEMENTE_ASSOCIATA_PATTO;
										if (dettaglioErrore == null || dettaglioErrore.equals("")) {
											dettaglioErrore = "Azione " + azioneRagg + " - " + descAzione
													+ " - Data stimata " + dataStimataAzione;
										} else {
											dettaglioErrore = dettaglioErrore + "<br>" + "Azione " + azioneRagg + " - "
													+ descAzione + " - Data stimata " + dataStimataAzione;
										}
										throw new PercorsoConcordatoException(codiceErrore);
									}
								}
							}
						}
					} else {
						// Cancello associazione con il patto e quindi valorizzo nel percorso
						// PRGPATTODISASSOCIATOFORMAZIONE prgPattoDaDeassociare
						if (op != null && op.equals("1") && prgPattoDaDeassociare != null && prgPercorsoKey != null) {
							Object paramPercorso[] = new Object[3];
							paramPercorso[0] = prgPattoDaDeassociare;
							paramPercorso[1] = cdnUtente;
							paramPercorso[2] = prgPercorsoKey;
							Boolean esitoDeAssocia = (Boolean) transExec.executeQuery("UPD_PERCORSO_DEASSOCIA_PATTO",
									paramPercorso, "UPDATE");
							if (!esitoDeAssocia.booleanValue()) {
								throw new Exception("Errore nella cancellazione dell'associazione al patto.");
							}
						}
					}

					ret = patto.execute(request, response);

				} else {
					throw new Exception("fallito aggiornamento di or_percorso_concordato");
				}
				//
				if (ret) {
					if (((String) request.getAttribute("operazioneColPatto")).equals("-1") && !opImpEseguita) {
						// Inserisci la associazione
						request.setAttribute("PRG_OR_PER", request.getAttribute("PRGPERCORSO").toString());
						ret = assImp.insertImpegni();
						if (!ret)
							throw new Exception("fallito inserimento degli impegni");
					}
					transExec.commitTransaction();
					// this.setMessageIdSuccess(idSuccess);
					// this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
				} else {
					throw new Exception("fallito aggiornamento di a_lav_patto_scelta");
				}
			} else {
				if (azioneCambiata && conPatto && ret) {
					// Inserisco gli impegni della nuova azione
					request.setAttribute("PRG_OR_PER", request.getAttribute("PRGPERCORSO").toString());
					ret = assImp.insertImpegni();
					if (!ret)
						throw new Exception("fallito inserimento degli impegni");
				} else {
					if (!ret)
						throw new Exception("fallito aggiornamento di or_percorso_concordato");
				}

				transExec.commitTransaction();
				reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			}
		} catch (PercorsoConcordatoException ex) {
			if (codiceErrore < 0) {
				reportOperation.reportFailure(((PercorsoConcordatoException) ex).getMessageIdFail());
			} else {
				reportOperation.reportFailure(codiceErrore, true, dettaglioErrore);
			}
			if (transExec != null)
				transExec.rollBackTransaction();
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL, ex, "execute()",
					"aggiornamento or_percorso_concordato in transazone con am_lav_patto_scelta");
			if (transExec != null)
				transExec.rollBackTransaction();
		}
	}

	/**
	 * Controlla che l'azione specificata, nella data specificata non sia già stata inserita per il colloquio
	 * specificato
	 */
	private boolean controllaEsistenzaAzione(String prgAzione, String data, String cdnLavoratore, String prgPercorso,
			TransactionQueryExecutor tExec) throws Exception {
		boolean exist = false;
		Object param[] = new Object[4];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgAzione);
		param[2] = data;
		param[3] = new BigDecimal(prgPercorso);
		SourceBean row = (SourceBean) tExec.executeQuery("CONTROLLA_ESISTENZA_AZIONE", param, "SELECT");
		if ((row != null) && (row.getAttribute("ROW.PRGPERCORSO") != null)
				&& !row.getAttribute("ROW.PRGPERCORSO").equals(""))
			exist = true;
		return exist;
	}

	private boolean controllaEsistenzaAdesione(String prgAzione, String data, String cdnLavoratore, String prgPercorso,
			String prgColloquio, TransactionQueryExecutor tExec) throws Exception {
		boolean exist = false;
		Object param[] = new Object[4];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgAzione);
		param[2] = data;
		param[3] = new BigDecimal(prgPercorso);
		SourceBean row = (SourceBean) tExec.executeQuery("CONTROLLA_ESISTENZA_ADESIONE", param, "SELECT");
		if (row != null) {
			Vector adesioniGG = row.getAttributeAsVector("ROW");
			if (adesioniGG.size() > 0) {
				exist = true;
			}
		}
		return exist;
	}

	private boolean controllaEsistenzaCollegamentoAdesione(String cdnLavoratore, String prgPercorso,
			String prgColloquio, TransactionQueryExecutor tExec) throws Exception {
		boolean exist = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgPercorso);
		param[2] = new BigDecimal(prgColloquio);
		SourceBean row = (SourceBean) tExec.executeQuery("CONTROLLA_ESISTENZA_POLITICA_ATTIVA_COLL_ADESIONE", param,
				"SELECT");
		if (row != null) {
			Vector azioneCollAdesioneGG = row.getAttributeAsVector("ROW");
			if (azioneCollAdesioneGG.size() > 0) {
				exist = true;
			}
		}
		return exist;
	}

	private boolean controllaEtaLavoratoreAdesione(String cdnLavoratore, String prgPercorsoAdesione,
			String prgColloquiAdesione, TransactionQueryExecutor tExec) throws Exception {
		boolean etaOK = false;
		Object param[] = new Object[3];
		param[0] = new BigDecimal(cdnLavoratore);
		param[1] = new BigDecimal(prgPercorsoAdesione);
		param[2] = new BigDecimal(prgColloquiAdesione);
		SourceBean row = (SourceBean) tExec.executeQuery("GET_ETA_LAVORATORE_ADESIONE", param, "SELECT");
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

	private void controlliCIG(SourceBean request, SourceBean response) throws PercorsoConcordatoException {

		String codEsitoRendicont = (String) request.getAttribute("CODESITORENDICONT");

		Vector rowsPercorsi = getServiceResponse().getAttributeAsVector("M_ListPercorsi.ROWS.ROW");
		int countPCIG = 0; // conto i percorsi collegati al colloquio PIC erogati
		int countNotPCIG = 0; // conto i percorsi collegati al colloquio non PIC e erogati

		/* se vi sono altri percorsi collegati a questo colloquio */
		if (rowsPercorsi.size() >= 1) {
			for (Iterator iter = rowsPercorsi.iterator(); iter.hasNext();) {
				SourceBean attributeValueSB = (SourceBean) iter.next();
				String attributeValueAz = String.valueOf((BigDecimal) attributeValueSB.getAttribute("PRGAZIONI"));
				String attributeCodEsitoRendicont = (String) attributeValueSB.getAttribute("CODESITORENDICONT");
				// 151 = Presa In Carico CIG
				if ("E".equals(attributeCodEsitoRendicont)) {
					if ("151".equals(attributeValueAz)) {
						countPCIG += 1;
					} else {
						countNotPCIG += 1;
					}
				}
			}
		}
		String prgAzione = (String) request.getAttribute("prgAzioni");
		this.setSectionQuerySelect("QUERY_SELECT_CIG");
		SourceBean rowCIG = this.doSelect(request, response);
		/* true se PIC erogata */
		String presaincaricoCIGVal = (String) rowCIG.getAttribute("ROW.presaincaricoCIG");

		if (codEsitoRendicont.equals("E")) {
			/* se il percorso che cerco di modificare è PIC erogata */
			if ("true".equals(presaincaricoCIGVal)) {
				if (!"151".equals(prgAzione)) {
					/* se cerco di cambiare l'azione con una diversa da PIC, lancio l'errore */
					throw new PercorsoConcordatoException(MessageCodes.Patto.PERC_CONCORD_UPD_PIN_CIG);
				}
			} else {
				if ("151".equals(prgAzione) && countPCIG == 1) {
					/* se è una PIC erogata e ci sono già altre PIC erogate, lancio l'eccezione */
					throw new PercorsoConcordatoException(MessageCodes.Patto.UNICA_AZIONE_PERC_CONCORD_CIG);
				}
				if (!"151".equals(prgAzione) && countPCIG == 0) {
					/*
					 * se sto cambiando l'azione di una PIC in non PIC e non ci sono altre azioni PIC, lancio l'errore
					 */
					throw new PercorsoConcordatoException(MessageCodes.Patto.PRIMA_AZIONE_PERC_CONCORD_CIG);
				}
			}

			this.setSectionQuerySelect("QUERY_SELECT_MAX_PRESTAZIONE");
			SourceBean rowMaxPrestCIG = this.doSelect(request, response);
			int numMaxRipetizioni = ((BigDecimal) rowMaxPrestCIG.getAttribute("ROW.numMaxRipetizioni")).intValue();

			/* verifico di non superare il numero massimo di servizi erogati */
			if (numMaxRipetizioni != 0) {
				this.setSectionQuerySelect("QUERY_SELECT_PRESTAZIONE");
				SourceBean rowPrestEsistCIG = this.doSelect(request, response);
				int numRipetizioniEsistenti = ((BigDecimal) rowPrestEsistCIG
						.getAttribute("ROW.numRipetizioniEsistenti")).intValue();
				if ((numRipetizioniEsistenti + 1) > numMaxRipetizioni) {
					throw new PercorsoConcordatoException(MessageCodes.Patto.PRESTAZIONICIG_NUMMAXSESSIONI_UPD);
				}
			}
		}
		/*
		 * se sto aggiornando con codEsitoRendicont diverso da erogato una pic erogata e ci sono altre azioni diverse da
		 * pic erogate
		 */
		else {
			if ("true".equals(presaincaricoCIGVal) && countNotPCIG != 0) {
				/* lancio l'errore */
				throw new PercorsoConcordatoException(MessageCodes.Patto.PERC_CONCORD_CANC_PIN_CIG);
			}
		}
	}

	private BigDecimal getPrgAzioneConfermaPeriodica(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgAzione = null;
		Object[] params = new Object[] { ConfermaPeriodica };
		SourceBean row = (SourceBean) txExec.executeQuery("WS_GET_PRG_AZIONE_CONFERMA_PERIODICA", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgAzione = (BigDecimal) row.getAttribute("prgazioni");
		}
		return prgAzione;
	}

	private SourceBean getDataAzione(BigDecimal prgPercorso, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		params[0] = prgPercorso;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_DATI_AZIONE_COLLEGATO_PATTO", params, "SELECT");
		if (sb != null) {
			return sb;
		}
		return null;
	}

}
