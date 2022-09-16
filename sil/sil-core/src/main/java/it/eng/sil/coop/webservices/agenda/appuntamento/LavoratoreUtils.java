package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.StoriaInfLavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.PutLavoratoreIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.util.InfoProvinciaSingleton;

public class LavoratoreUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LavoratoreUtils.class.getName());

	public static EsitoInserimentoLavoratore inserisciLavoratore(TransactionQueryExecutor trans, BigDecimal cdnUtIns,
			String codCpiInput, String strCodiceFiscale, String strNome, String strCognome, String strDataNascita,
			String codComNas, String strSesso, String codCittadinanza, String strIndirizzoDom, String codComDom,
			String strIndirizzoRes, String codComRes, String strCell, String strEmail, String flgInvioSMS) {

		EsitoInserimentoLavoratore esitoInserimentoLavoratore = new EsitoInserimentoLavoratore();

		BigDecimal cdnLavoratore = null;

		// dati necessari

		String flgCfOk = "N";
		String codstatoCivile = null;
		String flgMilite = null;
		BigDecimal numFigli = null;
		String strNote = null;
		String strLocalitaRes = null;
		String strCapRes = null;
		String strLocalitaDom = null;
		String strCapDom = null;
		String strTelRes = null;
		String strTelDom = null;
		String strTelAltro = null;
		String strFax = null;

		Object[] inputParameters = null;

		boolean success = false;

		// nuovo cdnLavoratore

		cdnLavoratore = getNextCdnLavoratore(trans);

		try {

			// verifica cf

			if (CF_utils.verificaParzialeCF(strCodiceFiscale, strNome, strCognome) == 0) {

				flgCfOk = "S";

			} else {
				_logger.warn("Errore verifica codice fiscale: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_CODICE_FISCALE);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODICE_FISCALE);
				return esitoInserimentoLavoratore;
			}

			// ricerca lavoratore

			inputParameters = new Object[1];
			inputParameters[0] = strCodiceFiscale;
			SourceBean lavoratoreSourceBean = ((SourceBean) trans.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
					"SELECT"));

			if (lavoratoreSourceBean != null && lavoratoreSourceBean.containsAttribute("ROW.CDNLAVORATORE")) {
				_logger.warn("Lavoratore già presente in archivio: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				return esitoInserimentoLavoratore;
			}

			// inserimento lavoratore

			inputParameters = new Object[31];
			inputParameters[0] = cdnLavoratore;
			inputParameters[1] = strCodiceFiscale;
			inputParameters[2] = strCognome;
			inputParameters[3] = strNome;
			inputParameters[4] = strSesso;
			inputParameters[5] = strDataNascita;
			inputParameters[6] = codComNas;
			inputParameters[7] = codCittadinanza;
			inputParameters[8] = codCittadinanza;
			inputParameters[9] = codstatoCivile;
			inputParameters[10] = flgMilite;
			inputParameters[11] = numFigli;
			inputParameters[12] = strNote;
			inputParameters[13] = flgCfOk;
			inputParameters[14] = cdnUtIns;
			inputParameters[15] = cdnUtIns;
			inputParameters[16] = codComRes;
			inputParameters[17] = strIndirizzoRes;
			inputParameters[18] = strLocalitaRes;
			inputParameters[19] = strCapRes;
			inputParameters[20] = codComDom;
			inputParameters[21] = strIndirizzoDom;
			inputParameters[22] = strLocalitaDom;
			inputParameters[23] = strCapDom;
			inputParameters[24] = strTelRes;
			inputParameters[25] = strTelDom;
			inputParameters[26] = strTelAltro;
			inputParameters[27] = strCell;
			inputParameters[28] = strEmail;
			inputParameters[29] = strFax;
			inputParameters[30] = flgInvioSMS;

			success = ((Boolean) trans.executeQuery("INSERT_AN_LAVORATOREANAGIND_APPUNTAMENTO_APPUNTAMENTO_ONLINE",
					inputParameters, "INSERT")).booleanValue();

			if (!success) {
				_logger.warn("Errore durante inserimento lavoratore nella tabella an_lavoratore: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				return esitoInserimentoLavoratore;
			}

			// regola competenza (codmonotipocpi)

			String codMonoTipoCpi = "T";
			String codCpiComuneDomicilio = getCpiComune(trans, codComDom);
			if (codCpiComuneDomicilio != null && codCpiComuneDomicilio.equalsIgnoreCase(codCpiInput)) {
				codMonoTipoCpi = "C";
			}

			// aggiornamento an_lav_storia_inf

			inputParameters = new Object[1];
			inputParameters[0] = cdnLavoratore;
			SourceBean anLavStoriaInfSourceBean = ((SourceBean) trans
					.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));
			BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
					.getAttribute("ROW.NUMKLOLAVSTORIAINF");
			String codCpiOrig = (String) anLavStoriaInfSourceBean.getAttribute("ROW.CODCPIORIG");

			if ("T".equalsIgnoreCase(codMonoTipoCpi)) {
				// se domicilio fuori regione codCpiOrig risulta nullo,
				// si mette il cod cpi del comune di domicilio
				codCpiOrig = codCpiComuneDomicilio;
			}

			inputParameters = new Object[7];
			inputParameters[0] = codCpiInput;
			inputParameters[1] = cdnUtIns;
			inputParameters[2] = codMonoTipoCpi;
			inputParameters[3] = codCpiOrig;
			inputParameters[4] = numkloLavStoriaInf.add(new BigDecimal("1"));
			inputParameters[5] = codComDom;
			inputParameters[6] = cdnLavoratore;

			success = ((Boolean) trans.executeQuery("UPDATE_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters,
					"UPDATE")).booleanValue();

			if (!success) {
				_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				return esitoInserimentoLavoratore;
			}

			// invio dati a indice regionale (per i SIL che lo supportano)

			boolean coopAbilitata = (boolean) ((ConfigSingleton.getInstance()).getAttribute("COOP.ABILITATA"))
					.equals("true");
			if (coopAbilitata) {

				callUpdateIR(trans, cdnLavoratore, strCodiceFiscale, strNome, strCognome, strDataNascita, codComNas,
						codMonoTipoCpi, codComDom, codComRes);

			}

			esitoInserimentoLavoratore.setSuccess(true);
			esitoInserimentoLavoratore.setCdnLavoratore(cdnLavoratore);

		} catch (it.eng.afExt.utils.CfException e) {
			_logger.warn("Errore verifica codice fiscale: " + strCodiceFiscale);
			esitoInserimentoLavoratore.setSuccess(false);
			esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_CODICE_FISCALE);
			esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODICE_FISCALE);
		} catch (Exception e) {
			_logger.warn("Errore avvenuto durante l'inserimento del lavoratore: " + strCodiceFiscale, e);
			esitoInserimentoLavoratore.setSuccess(false);
			esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
		}

		return esitoInserimentoLavoratore;

	}

	public static EsitoInserimentoLavoratore inserisciLavoratoreStipulaDid(TransactionQueryExecutor trans,
			BigDecimal cdnUtIns, String codCpiTit, String codCpiOrig, String codMonoTipoCpi, String strCodiceFiscale,
			String strNome, String strCognome, String strDataNascita, String codComNas, String strSesso,
			String codCittadinanza, String strIndirizzoDom, String codComDom, String strIndirizzoRes, String codComRes,
			String strCell, String strEmail, String flgInvioSMS) {

		EsitoInserimentoLavoratore esitoInserimentoLavoratore = new EsitoInserimentoLavoratore();

		BigDecimal cdnLavoratore = null;

		// dati necessari

		String flgCfOk = "N";
		String codstatoCivile = null;
		String flgMilite = null;
		BigDecimal numFigli = null;
		String strNote = null;
		String strLocalitaRes = null;
		String strCapRes = null;
		String strLocalitaDom = null;
		String strCapDom = null;
		String strTelRes = null;
		String strTelDom = null;
		String strTelAltro = null;
		String strFax = null;

		Object[] inputParameters = null;

		boolean success = false;

		// nuovo cdnLavoratore

		cdnLavoratore = getNextCdnLavoratore(trans);

		try {

			// verifica cf

			if (CF_utils.verificaParzialeCF(strCodiceFiscale, strNome, strCognome) == 0) {
				flgCfOk = "S";
			} else {
				_logger.warn("Errore verifica codice fiscale: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_CODICE_FISCALE);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODICE_FISCALE);
				return esitoInserimentoLavoratore;
			}

			// inserimento lavoratore
			inputParameters = new Object[31];
			inputParameters[0] = cdnLavoratore;
			inputParameters[1] = strCodiceFiscale;
			inputParameters[2] = strCognome;
			inputParameters[3] = strNome;
			inputParameters[4] = strSesso;
			inputParameters[5] = strDataNascita;
			inputParameters[6] = codComNas;
			inputParameters[7] = codCittadinanza;
			inputParameters[8] = codCittadinanza;
			inputParameters[9] = codstatoCivile;
			inputParameters[10] = flgMilite;
			inputParameters[11] = numFigli;
			inputParameters[12] = strNote;
			inputParameters[13] = flgCfOk;
			inputParameters[14] = cdnUtIns;
			inputParameters[15] = cdnUtIns;
			inputParameters[16] = codComRes;
			inputParameters[17] = strIndirizzoRes;
			inputParameters[18] = strLocalitaRes;
			inputParameters[19] = strCapRes;
			inputParameters[20] = codComDom;
			inputParameters[21] = strIndirizzoDom;
			inputParameters[22] = strLocalitaDom;
			inputParameters[23] = strCapDom;
			inputParameters[24] = strTelRes;
			inputParameters[25] = strTelDom;
			inputParameters[26] = strTelAltro;
			inputParameters[27] = strCell;
			inputParameters[28] = strEmail;
			inputParameters[29] = strFax;
			inputParameters[30] = flgInvioSMS;

			success = ((Boolean) trans.executeQuery("INSERT_AN_LAVORATOREANAGIND_APPUNTAMENTO_APPUNTAMENTO_ONLINE",
					inputParameters, "INSERT")).booleanValue();

			if (!success) {
				_logger.warn("Errore durante inserimento lavoratore nella tabella an_lavoratore: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				return esitoInserimentoLavoratore;
			}

			// aggiornamento an_lav_storia_inf
			inputParameters = new Object[1];
			inputParameters[0] = cdnLavoratore;
			SourceBean anLavStoriaInfSourceBean = ((SourceBean) trans
					.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));
			BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
					.getAttribute("ROW.NUMKLOLAVSTORIAINF");

			inputParameters = new Object[7];
			inputParameters[0] = codCpiTit;
			inputParameters[1] = cdnUtIns;
			inputParameters[2] = codMonoTipoCpi;
			inputParameters[3] = codCpiOrig;
			inputParameters[4] = numkloLavStoriaInf.add(new BigDecimal("1"));
			inputParameters[5] = codComDom;
			inputParameters[6] = cdnLavoratore;

			success = ((Boolean) trans.executeQuery("UPDATE_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters,
					"UPDATE")).booleanValue();

			if (!success) {
				_logger.warn("Errore durante aggiornamento tabella an_lav_storia_inf: " + strCodiceFiscale);
				esitoInserimentoLavoratore.setSuccess(false);
				esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				return esitoInserimentoLavoratore;
			}

			esitoInserimentoLavoratore.setSuccess(true);
			esitoInserimentoLavoratore.setCdnLavoratore(cdnLavoratore);

		} catch (it.eng.afExt.utils.CfException e) {
			_logger.warn("Errore verifica codice fiscale: " + strCodiceFiscale);
			esitoInserimentoLavoratore.setSuccess(false);
			esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_CODICE_FISCALE);
			esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODICE_FISCALE);
		} catch (Exception e) {
			_logger.warn("Errore avvenuto durante l'inserimento del lavoratore: " + strCodiceFiscale, e);
			esitoInserimentoLavoratore.setSuccess(false);
			esitoInserimentoLavoratore.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			esitoInserimentoLavoratore.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
		}

		return esitoInserimentoLavoratore;
	}

	private static boolean callUpdateIR(TransactionQueryExecutor trans, BigDecimal cdnLavoratore,
			String strCodiceFiscale, String strNome, String strCognome, String strDataNascita, String codComNas,
			String codMonoTipoCpi, String codComDom, String codComRes) {

		boolean success = false;

		try {

			// dati necessari

			String cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
			String poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

			StoriaInfLavoratore infLav = new StoriaInfLavoratore(cdnLavoratore);
			String strDataInizio = infLav.getDatInizio();
			String codCpiTit = infLav.getCodCpiTit(); // che alla fine è uguale a quello dato in input da xml

			String strMittente = getMittenteIR(trans, Constants.UTENTE_PORTALE);

			// cpi competenza

			String codCpiCompetenza = null;
			if (codMonoTipoCpi != null && "C".equalsIgnoreCase(codMonoTipoCpi)) {
				codCpiCompetenza = codCpiTit;
			} else {
				// si deduce da domicilio/residenza
				if (codComDom != null && !"".equalsIgnoreCase(codComDom)) {
					codCpiCompetenza = getCpiComune(trans, codComDom);
				} else {
					if (codComRes != null && !"".equalsIgnoreCase(codComRes)) {
						codCpiCompetenza = getCpiComune(trans, codComRes);
					}
				}
			}

			if (codCpiCompetenza == null) {
				_logger.debug("codCpiCompetenza non trovato: " + strCodiceFiscale);
				return false;
			}

			// chiamata

			TestataMessageTO testataMessaggio = new TestataMessageTO();
			testataMessaggio.setPoloMittente(poloMittente);
			testataMessaggio.setCdnUtente(cdnUtente);
			testataMessaggio.setCdnGruppo(null); // TODO ok? (d'accordo con Carmela, 2014/05/22)
			testataMessaggio.setCdnProfilo(null); // TODO ok? (d'accordo con Carmela, 2014/05/22)
			testataMessaggio.setStrMittente(strMittente);

			PutLavoratoreIRMessage putLavoratoreIRMessage = new PutLavoratoreIRMessage();
			putLavoratoreIRMessage.setTestata(testataMessaggio);

			// imposto i parametri applicativi
			putLavoratoreIRMessage.setCodiceFiscale(strCodiceFiscale);
			putLavoratoreIRMessage.setNome(strNome);
			putLavoratoreIRMessage.setCognome(strCognome);
			putLavoratoreIRMessage.setDataNascita(strDataNascita);
			putLavoratoreIRMessage.setComune(codComNas);
			putLavoratoreIRMessage.setDataInizio(strDataInizio);
			putLavoratoreIRMessage.setCodCpi(codCpiTit);
			putLavoratoreIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
			putLavoratoreIRMessage.setCpiComp(codCpiCompetenza);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			OutQ outQ = new OutQ();
			putLavoratoreIRMessage.setDataSourceJndi(dataSourceJndiName);
			putLavoratoreIRMessage.send(outQ);

			success = true;

		} catch (Exception e) {

			_logger.error("Errore durante l'invio dei dati all'indice regionale, cf: " + strCodiceFiscale, e);
			success = false;

		}

		return success;

	}

	public static BigDecimal getNextCdnLavoratore(TransactionQueryExecutor trans) {

		BigDecimal cdnLavoratore = null;

		try {

			Object[] args = null;
			SourceBean cdnLavoratoreSourceBean = (SourceBean) trans.executeQuery("GET_AN_NEW_LAVORATORE", args,
					TransactionQueryExecutor.SELECT);

			if (cdnLavoratoreSourceBean != null && cdnLavoratoreSourceBean.containsAttribute("ROW")) {
				cdnLavoratore = SourceBeanUtils.getAttrBigDecimal(cdnLavoratoreSourceBean, "ROW.CDNLAVORATORE");
			}

		} catch (com.engiweb.framework.error.EMFInternalError e) {
			_logger.error("Errore recupero nuovo cdnLavoratore", e);
		}

		return cdnLavoratore;
	}

	public static String getCpiComune(TransactionQueryExecutor trans, String codCom) {

		String codCpi = null;

		try {

			Object[] args = new Object[1];
			args[0] = codCom;
			SourceBean cpiComuneSourceBean = (SourceBean) trans.executeQuery("SELECT_CPI_COMUNE", args,
					TransactionQueryExecutor.SELECT);

			if (cpiComuneSourceBean != null && cpiComuneSourceBean.containsAttribute("ROW")) {
				codCpi = SourceBeanUtils.getAttrStr(cpiComuneSourceBean, "ROW.CODCPI");
			}

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore recupero cpi da comune", ex);
		}

		return codCpi;

	}

	public static SourceBean getCpiProvinciaComune(TransactionQueryExecutor trans, String codCom) {

		SourceBean cpiComuneSourceBean = null;

		try {

			Object[] args = new Object[1];
			args[0] = codCom;
			cpiComuneSourceBean = (SourceBean) trans.executeQuery("SELECT_CPI_COMUNE_DID_ON_LINE", args,
					TransactionQueryExecutor.SELECT);

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore recupero cpi da comune", ex);
		}

		return cpiComuneSourceBean;
	}

	public static SourceBean getInfoCpiProvinciaComune(TransactionQueryExecutor trans, String codCom) {

		SourceBean cpiComuneSourceBean = null;

		try {

			Object[] args = new Object[1];
			args[0] = codCom;
			cpiComuneSourceBean = (SourceBean) trans.executeQuery("SELECT_CPI_COMUNE_IMPORTA_SAP", args,
					TransactionQueryExecutor.SELECT);

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore recupero cpi da comune", ex);
		}

		return cpiComuneSourceBean;
	}

	public static SourceBean getProvinciaDomicilioLav(TransactionQueryExecutor trans, BigDecimal cdnlavoratore) {

		SourceBean cpiComuneSourceBean = null;

		try {

			Object[] args = new Object[1];
			args[0] = cdnlavoratore;
			cpiComuneSourceBean = (SourceBean) trans.executeQuery("SELECT_PROVINCIA_COMUNE_DOMICILIO_LAVORATORE", args,
					TransactionQueryExecutor.SELECT);

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore recupero cpi da comune", ex);
		}

		return cpiComuneSourceBean;
	}

	private static String getMittenteIR(TransactionQueryExecutor trans, BigDecimal cdnUtente) {

		String strMittente = null;

		try {

			Object[] args = new Object[1];
			args[0] = cdnUtente;
			SourceBean sourceBean = (SourceBean) trans.executeQuery("MITTENTE_IR_AGENDA", args,
					TransactionQueryExecutor.SELECT);

			if (sourceBean != null && sourceBean.containsAttribute("ROW")) {
				strMittente = SourceBeanUtils.getAttrStr(sourceBean, "ROW.MITTENTE");
			}

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Errore recupero utente", ex);
		}

		return strMittente;

	}

	/**
	 * 
	 * @param trans
	 *            transazione
	 * @param cdnLavoratore
	 *            lavoratore i cui contatti devono essere aggiornati
	 * @param strCell
	 *            nuovo contatto cellulare
	 * @param strEmail
	 *            nuovo contatto mail
	 * @param numklo
	 *            numero gia incrementato di 1 per la concorrenza
	 * @param cdnUt
	 *            utente di modifica
	 * @return
	 */
	public static boolean aggiornaContattiLavoratore(TransactionQueryExecutor trans, BigDecimal cdnLavoratore,
			String strCell, String strEmail, BigDecimal numklo, BigDecimal cdnUt) {
		boolean success = false;
		Object[] inputParameters = null;
		try {
			inputParameters = new Object[5];
			inputParameters[0] = strCell;
			inputParameters[1] = strEmail;
			inputParameters[2] = cdnUt;
			inputParameters[3] = numklo;
			inputParameters[4] = cdnLavoratore;

			success = ((Boolean) trans.executeQuery("UPDATE_CONTATTI_LAV_BY_CDNLAV", inputParameters, "UPDATE"))
					.booleanValue();
		} catch (Exception e) {
			_logger.error("Errore durante l'aggiornamento dei contatti del lavoratore : " + cdnLavoratore.toString(),
					e);
			success = false;
		}

		return success;
	}

}
