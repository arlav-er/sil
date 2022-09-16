package it.eng.sil.module.mobilita;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.cig.bean.ImportaIscrizioneMBOBean;
import it.eng.sil.module.amministrazione.BackGroundValidatorMobilita;
import it.eng.sil.module.amministrazione.ValidatorNew;
import it.eng.sil.module.movimenti.HtmlResultLogger;
import it.eng.sil.module.movimenti.MultipleResultLogger;
import it.eng.sil.module.movimenti.ResultLogFormatter;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.security.User;
import it.eng.sil.util.StatementUtils;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/**
 * Classe per la gestione del processo di validazione di una domanda MBO arrivata da SIL Regionale. Valida ed
 * eventualmente inserisce l'evidenza sul lavoratore
 * 
 * @author manuel
 */
public class ValidazioneXMLMbo {

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ValidazioneXMLMbo.class.getName());

	private BigDecimal prgAmMobIscrApp;

	private RequestContainer rc;

	private ImportaIscrizioneMBOBean importaIscrMBOBean;

	public ValidazioneXMLMbo(BigDecimal prgAmMobIscrApp, ImportaIscrizioneMBOBean importaIscrMBOBean) {
		this.prgAmMobIscrApp = prgAmMobIscrApp;
		this.importaIscrMBOBean = importaIscrMBOBean;
	}

	@SuppressWarnings("unchecked")
	public void valida() throws SourceBeanException {

		// preparo il request container mock
		preparaContesto();

		Vector<SourceBean> vettConfigGenerale = new Vector<SourceBean>();
		SourceBean sbGenerale = null;
		try {
			sbGenerale = DBLoad.getInfoGenerali();
		} catch (Exception e) {
			logger.error("Impossibile leggere dalla ts_generale: " + e);
			return;
		}
		vettConfigGenerale.add(0, sbGenerale);

		SourceBean sbApp = null;
		SourceBean sbConfigLoc = null;
		SourceBean resultSB;
		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			trans.initTransaction();

			resultSB = (SourceBean) trans.executeQuery("MOB_GET_CONFIG_LOC", new Object[0], "SELECT");
			Vector<SourceBean> vettConfigLoc = resultSB.getAttributeAsVector("M_MOB_GETCONFIGLOC.ROWS.ROW");
			if (vettConfigLoc != null) {
				for (int i = 0; i < vettConfigLoc.size(); i++) {
					sbApp = vettConfigLoc.get(i);
					if (sbApp.containsAttribute("STRVALORE")
							&& sbApp.getAttribute("STRVALORE").toString().equalsIgnoreCase("VALMAN")) {
						sbConfigLoc = new SourceBean("CONFIG");
						BigDecimal num = (BigDecimal) sbApp.getAttribute("NUM");
						sbConfigLoc.setAttribute("NUMCONFIGLOC", num);
						break;
					}
				}
			}
			vettConfigGenerale.add(1, sbConfigLoc);

			SessionContainer sessione = rc.getSessionContainer();
			ArrayList<BigDecimal> prgMobAppArray = new ArrayList<BigDecimal>();
			prgMobAppArray.add(prgAmMobIscrApp);

			// Oggetto per validazione delle mobilita
			ValidatorNew validator = new ValidatorNew();
			validator.setContesto(MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA);
			// creo l'oggetto che esegue il log dei risultati
			ResultLogger resultLogger = null;
			BigDecimal prgValidazioneMassiva = null;
			try {
				resultLogger = new HtmlResultLogger();
				// puo generare LogException
				MultipleResultLogger dbLogger = new MultipleResultLogger(prgMobAppArray.size(), sessione,
						MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA);
				prgValidazioneMassiva = dbLogger.getPrgValidazioneMassiva();
				validator.setPrgValidazione(prgValidazioneMassiva);
				resultLogger.addChildResultLogger(dbLogger); // (x)->Html->DB
			} catch (Exception e) {
				if (trans != null) {
					trans.rollBackTransaction();
				}
				logger.error("ERRORE durante la validazione automatica: " + e);
				return;
			}
			// Creo l'oggetto per il recupero dei risultati
			ResultLogFormatter risultatiCorrenti = null;
			try {
				risultatiCorrenti = new ResultLogFormatter(prgValidazioneMassiva);
			} catch (Exception e) {
				if (trans != null) {
					trans.rollBackTransaction();
				}
				logger.error("ERRORE durante la validazione automatica: " + e);
				return;
			}

			BackGroundValidatorMobilita bgValidator = new BackGroundValidatorMobilita(rc, validator, resultLogger,
					prgMobAppArray, vettConfigGenerale, true);
			bgValidator.setPrgAmMobIscrApp(prgAmMobIscrApp);
			bgValidator.setImportaIscrMBOBean(importaIscrMBOBean);
			Thread t = new Thread(bgValidator, "VALIDATOREMASSIVOMOBILITA");
			sessione.setAttribute("VALIDATOREMASSIVOMOBILITACORRENTE", t);
			sessione.setAttribute("PROGRESSIVOULTIMAVALIDAZIONEMOBILITA", prgValidazioneMassiva);
			sessione.setAttribute("RISULTATI_ULTIMA_VALIDAZIONE_MOBILITA", risultatiCorrenti);
			sessione.setAttribute("VALIDATORMOBILITACORRENTE", validator);

			// Avvio la validazione massiva
			t.start();

			trans.commitTransaction();

			rimuoviContesto();
		} catch (EMFInternalError e1) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();
				}
			} catch (EMFInternalError e) {
				logger.error("ERRORE durante la validazione automatica: " + e);
				return;
			}

			// INSERIMENTO EVIDENZA
			insertEvidenza(prgAmMobIscrApp, importaIscrMBOBean);

			logger.error("ERRORE durante la validazione automatica: " + e1);
			return;
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				logger.error("ERRORE durante la validazione automatica: " + e1);
				return;
			}

			// INSERIMENTO EVIDENZA
			insertEvidenza(prgAmMobIscrApp, importaIscrMBOBean);

			logger.error("ERRORE durante la validazione automatica: " + e);
			return;
		}
	}

	private void preparaContesto() {
		try {
			rc = new RequestContainer();
			ResponseContainer respCont = new ResponseContainer();

			RequestContainer.setRequestContainer(rc);

			SourceBean serviceRequest = new SourceBean("SERVICE_REQUEST");
			SourceBean serviceResponse = new SourceBean("SERVICE_RESPONSE");

			SessionContainer sessionCont = new SessionContainer(false);
			if (sessionCont.getAttribute("_ENCRYPTER_KEY_") == null
					|| "".equals(sessionCont.getAttribute("_ENCRYPTER_KEY_"))) {
				String keyCifratura = System.getProperty("_ENCRYPTER_KEY_");
				sessionCont.setAttribute("_ENCRYPTER_KEY_", keyCifratura);
			}
			rc.setServiceRequest(serviceRequest);
			rc.setSessionContainer(sessionCont);
			respCont.setErrorHandler(new EMFErrorHandler());
			respCont.setServiceResponse(serviceResponse);

			BigDecimal codUtente = new BigDecimal(190);
			sessionCont.setAttribute(Values.CODUTENTE, codUtente);

			SourceBean sb = StatementUtils.getSourceBeanByStatement("GET_UTENTE_INFO", codUtente.toString());
			String username = Utils.notNull(sb.getAttribute("ROW.STRLOGIN"));
			String nome = Utils.notNull(sb.getAttribute("ROW.STRNOME"));
			String cognome = Utils.notNull(sb.getAttribute("ROW.STRCOGNOME"));

			User user = new User(codUtente.intValue(), username, nome, cognome);
			sessionCont.setAttribute(User.USERID, user);

			user.setCdnProfilo(4); // Per i permessi sul lavoratore o sull'azienda
			SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_CODCPICAPOLUOGO", null, "SELECT",
					Values.DB_SIL_DATI);
			String codCpiCapoluogo = (String) row.getAttribute("ROW.RESULT");
			user.setCodRif(codCpiCapoluogo); // Per il calcolo del CPI di competenza
		} catch (Exception e) {
			logger.error("impossibile creare il contesto framework del servizio per la protocollazione del documento",
					e);

		}
	}

	private void rimuoviContesto() {
		if (this.rc != null)
			RequestContainer.delRequestContainer();
	}

	/**
	 * Inserimento evidenza
	 */
	public static void insertEvidenza(BigDecimal prgAmMobIscrApp, ImportaIscrizioneMBOBean importaIscrMBOBean) {

		if (prgAmMobIscrApp == null || importaIscrMBOBean == null) {
			logger.info("I parametri per inserire l'evidenza sono nulli");
			return;
		}

		logger.info("Inserimento evidenza");
		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			trans.initTransaction();

			/* verifico che la validazione sia andata a buon fine */
			Object[] params = new Object[1];
			params[0] = prgAmMobIscrApp;
			SourceBean esito = (SourceBean) trans.executeQuery("CHECK_APP_EXIST", params, "SELECT");
			BigDecimal countApp = (BigDecimal) esito.getAttribute("ROW.countapp");

			/* se il record sta ancora in appoggio, col piffero che e' andata a buon fine */
			if (countApp.intValue() > 0) {

				/* recupero il prg del tipo evidenza */
				SourceBean esitoSB = (SourceBean) trans.executeQuery("GET_PRG_TIPO_EVIDENZA", new Object[0], "SELECT");
				BigDecimal prgTipoEvidenza = (BigDecimal) esitoSB.getAttribute("ROW.PRGTIPOEVIDENZA");

				/* esiste il lavoratore? */
				params = new Object[1];
				params[0] = importaIscrMBOBean.getCodiceFiscaleLav();
				SourceBean result = (SourceBean) trans.executeQuery("GET_CDNLAVORATORE", params, "SELECT");
				BigDecimal cdnLavoratore = (BigDecimal) result.getAttribute("ROW.cdnlavoratore");

				/* esiste, quindi procedo inserendo la benedetta evidenza */
				if (cdnLavoratore != null) {
					if (!doInsert(trans, prgTipoEvidenza, cdnLavoratore)) {
						trans.rollBackTransaction();
						logger.error("ERRORE durante l'inserimento dell'evidenza");
						return;
					}
				}
			}

			trans.commitTransaction();
		} catch (EMFInternalError e) {
			try {
				trans.rollBackTransaction();
			} catch (EMFInternalError e1) {
				logger.error("ERRORE durante l'inserimento dell'evidenza: " + e1);
				return;
			}
			logger.error("ERRORE durante l'inserimento dell'evidenza: " + e);
			return;
		}
	}

	/**
	 * Inserisce l'evidenza per il lavoratore accorpante (solo se l'accorpamento va a buon fine)
	 * 
	 * @param trans
	 *            TransactionQueryExecutor
	 * @param prgTipoEvidenza
	 *            BigDecimal
	 * @param cdnLavoratore
	 *            BigDecimal
	 * @return <code>true</code> se tutto va a buon fine
	 * @throws EMFInternalError
	 */
	public static boolean doInsert(TransactionQueryExecutor trans, BigDecimal prgTipoEvidenza, BigDecimal cdnLavoratore)
			throws EMFInternalError {

		String strEvidenza = new StringBuffer(
				"Per questo lavoratore sono presenti domande di mobilità ordinaria da validare").toString();

		int i = -1;
		Object[] params = new Object[3];
		params[++i] = cdnLavoratore;
		params[++i] = strEvidenza;
		params[++i] = prgTipoEvidenza;
		boolean esito = ((Boolean) trans.executeQuery("INSERT_EVIDENZA", params, "INSERT")).booleanValue();

		return esito;
	}
}
