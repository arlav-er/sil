package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.HtmlResultLogger;
import it.eng.sil.module.movimenti.MultipleResultLogger;
import it.eng.sil.module.movimenti.ResultLogFormatter;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/**
 * Modulo che esegue la validazione delle mobilità Se nella request è presente l'attributo stringa
 * prgListaMobilitaDaValidare allora vengono validate solo quelle contenute nella lista, altrimenti si procede a una
 * validazione massiva
 */
public class ValidaMobilita extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidaMobilita.class.getName());
	String pool = null;

	public ValidaMobilita() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessione = getRequestContainer().getSessionContainer();
		// Azione specificata dall'utente
		String azione = StringUtils.getAttributeStrNotNull(request, "azioneScelta");
		int numMaxMobDaValidare = 0;
		Vector vettConfigLoc = null;
		Vector vettConfigGenerale = new Vector();
		SourceBean sbApp = null;
		boolean insMovimentoInValMob = false;
		ArrayList prgMobAppArray = null;
		if (sessione.getAttribute("VALIDATOREMASSIVOMOBILITACORRENTE") != null) {
			// thread attivo per la validazione massiva delle mobilità
			// Guardo se devo arrestare la validazione in atto
			if (azione.equalsIgnoreCase("arresta")) {
				try {
					ValidatorNew v = (ValidatorNew) sessione.getAttribute("VALIDATORMOBILITACORRENTE");
					if (v != null) {
						v.stop();
					}
					reportOperation.reportSuccess(
							MessageCodes.LogOperazioniValidazioneMobilita.VALIDAZIONE_MASSIVA_INTERROTTA);
					return;
				} catch (NullPointerException e) {
					// Era già finita, lo segnalo all'utente
					reportOperation.reportFailure(
							MessageCodes.LogOperazioniValidazioneMobilita.VALIDAZIONE_MASSIVA_GIA_TERMINATA);
					return;
				}
			} else if (azione.equalsIgnoreCase("validaSelezionati") || azione.equalsIgnoreCase("validaTutti")
					|| azione.equalsIgnoreCase("validaFiltrati")) {
				reportOperation
						.reportFailure(MessageCodes.LogOperazioniValidazioneMobilita.VALIDAZIONE_MASSIVA_IN_ATTO);
				return;
			}
		} else {
			if (azione.equalsIgnoreCase("validaSelezionati") || azione.equalsIgnoreCase("validaTutti")
					|| azione.equalsIgnoreCase("validaFiltrati")) {
				sessione.delAttribute("PROGRESSIVOULTIMAVALIDAZIONEMOBILITA");
				sessione.delAttribute("RISULTATI_ULTIMA_VALIDAZIONE_MOBILITA");
				// lettura impostazioni nella ts_generale
				SourceBean sbGenerale = null;
				try {
					sbGenerale = DBLoad.getInfoGenerali();
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile leggere dalla ts_generale. ", e);

					return;
				}
				vettConfigGenerale.add(0, sbGenerale);
				// lettura impostazioni nella ts_config_loc
				vettConfigLoc = getServiceResponse().getAttributeAsVector("M_MOB_GETCONFIGLOC.ROWS.ROW");
				SourceBean sbConfigLoc = null;
				if (vettConfigLoc != null) {
					for (int i = 0; i < vettConfigLoc.size(); i++) {
						sbApp = (SourceBean) vettConfigLoc.get(i);
						if (sbApp.containsAttribute("STRVALORE")
								&& sbApp.getAttribute("STRVALORE").toString().equalsIgnoreCase("VALMAS")) {
							sbConfigLoc = new SourceBean("CONFIG");
							BigDecimal num = (BigDecimal) sbApp.getAttribute("NUM");
							sbConfigLoc.setAttribute("NUMCONFIGLOC", num);
							insMovimentoInValMob = true;
							break;
						}
					}
				}
				vettConfigGenerale.add(1, sbConfigLoc);
				// lettura del numero max di mobilità da validare a seconda
				// della protocollazione o meno del movimento
				if (sbGenerale != null) {
					if ((insMovimentoInValMob) && (sbGenerale.containsAttribute("NUMMAXISCMOBDAVALSEPROTMOV"))) {
						numMaxMobDaValidare = Integer
								.parseInt(sbGenerale.getAttribute("NUMMAXISCMOBDAVALSEPROTMOV").toString());
					} else {
						if ((!insMovimentoInValMob) && (sbGenerale.containsAttribute("NUMMAXISCMOBDAVALIDARE"))) {
							numMaxMobDaValidare = Integer
									.parseInt(sbGenerale.getAttribute("NUMMAXISCMOBDAVALIDARE").toString());
						}
					}
				}

				prgMobAppArray = new ArrayList();
				String strListaMobilitaDaValidare = request.containsAttribute("prgListaMobilitaDaValidare")
						? request.getAttribute("prgListaMobilitaDaValidare").toString()
						: "";
				if (!strListaMobilitaDaValidare.equals("") && azione.equalsIgnoreCase("validaSelezionati")) {
					// il vettore delle mobilita da validare sarà formato dagli
					// elementi della lista
					Vector vettPrg = StringUtils.split(strListaMobilitaDaValidare, "#");
					for (int i = 0; i < vettPrg.size(); i++)
						prgMobAppArray.add(new BigDecimal(vettPrg.get(i).toString()));
				} else {
					if (azione.equalsIgnoreCase("validaFiltrati")) {
						int i = 0;
						SourceBean sbMobFiltrato = null;
						Vector mobilitaFiltrate = getServiceResponse()
								.getAttributeAsVector("M_Mob_MobilitaFiltrate.ROWS.ROW");
						for (; i < mobilitaFiltrate.size(); i++) {
							sbMobFiltrato = (SourceBean) mobilitaFiltrate.get(i);
							prgMobAppArray.add((BigDecimal) sbMobFiltrato.getAttribute("PRGMOBILITAISCRAPP"));
						}
					} else {
						// il vettore delle mobilita da validare sarà formato
						// dai progressivi presenti nella
						// tabella e estratti da una query
						Object result = null;
						try {
							result = QueryExecutor.executeQuery("GET_PROGRESSIVI_MOBILITA_APPOGGIO_VALIDAZIONE_MASSIVA",
									null, "SELECT", Values.DB_SIL_DATI);
						} catch (Exception e) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"Errore nel recupero dei progressivi delle mobilità da validare. ", e);

							return;
						}

						// Esamino il risultato
						if (result instanceof SourceBean) {
							// Estraggo i progressivi ritrovati
							Vector v = ((SourceBean) result).getAttributeAsVector("ROW");
							for (int i = 0; i < v.size(); i++) {
								prgMobAppArray.add(((SourceBean) v.get(i)).getAttribute("PRGMOBILITAISCRAPP"));
							}
						} else {
							if (result instanceof Exception) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"Errore nel recupero dei progressivi delle mobilità da validare. ",
										(Exception) result);

								return;
							} else {
								_logger.debug("Errore nel recupero dei progressivi delle mobilità da validare. ");

								return;
							}
						}
					}
				}

				int numMobilitaDaValidare = prgMobAppArray.size();
				if (numMobilitaDaValidare > numMaxMobDaValidare) {
					response.setAttribute("NUM_MAX_MOBILITA_SUPERATO",
							"Superato il numero massimo di mobilità che si possono validare in un'operazione massiva.");
				} else {
					// Oggetto per validazione delle mobilità
					ValidatorNew validator = new ValidatorNew();
					validator.setContesto(MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA);
					// creo l'oggetto che esegue il log dei risultati
					ResultLogger resultLogger = null;
					BigDecimal prgValidazioneMassiva = null;
					try {
						resultLogger = new HtmlResultLogger();
						// può generare LogException
						MultipleResultLogger dbLogger = new MultipleResultLogger(prgMobAppArray.size(), sessione,
								MessageCodes.General.LOG_VALIDAZIONE_MOBILITA_MASSIVA);
						prgValidazioneMassiva = dbLogger.getPrgValidazioneMassiva();
						validator.setPrgValidazione(prgValidazioneMassiva);
						resultLogger.addChildResultLogger(dbLogger); // (x)->Html->DB
					} catch (Exception e) {
						// Segnalo l'impossibilità di scrivere il log e ritorno
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"Impossibile inizalizzare il logger per registrare i risultati", e);

						return;
					}
					// Creo l'oggetto per il recupero dei risultati
					ResultLogFormatter risultatiCorrenti = null;
					try {
						risultatiCorrenti = new ResultLogFormatter(prgValidazioneMassiva);
					} catch (Exception e) {
						// Segnalo l'impossibilità di istanziare l'oggetto per
						// il recupero del log
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"Impossibile inizalizzare il logger per il recupero dei risultati", e);

						return;
					}
					Thread t = new Thread(
							new BackGroundValidatorMobilita(RequestContainer.getRequestContainer(), validator,
									resultLogger, prgMobAppArray, vettConfigGenerale, false),
							"VALIDATOREMASSIVOMOBILITA");
					sessione.setAttribute("VALIDATOREMASSIVOMOBILITACORRENTE", t);
					sessione.setAttribute("PROGRESSIVOULTIMAVALIDAZIONEMOBILITA", prgValidazioneMassiva);
					sessione.setAttribute("RISULTATI_ULTIMA_VALIDAZIONE_MOBILITA", risultatiCorrenti);
					sessione.setAttribute("VALIDATORMOBILITACORRENTE", validator);
					// Avvio la validazione massiva
					t.start();
				}
			}
		}
	}

}