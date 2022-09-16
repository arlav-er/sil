/*
 * Creato il 18-nov-04
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.security.TransactionProfileDataFilter;
import it.eng.sil.security.User;

/**
 * Questo processor controlla che si abbiano i diritti per gli impatti e per inserire il movimento. Se si hanno i
 * diritti per calcolare gli impatti all'uscita dal processor nell map il campo PERMETTIIMPATTI sarà valorizzato con una
 * String di valore "true", altrimenti sarà valorizzata a "false"
 * 
 * @author roccetti
 */
public class ControlloPermessi implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per le transazioni */
	private TransactionQueryExecutor trans;
	private boolean checkForzaValidazione = false;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlloPermessi.class.getName());

	/**
	 * 
	 */
	public ControlloPermessi(String name, TransactionQueryExecutor transqueryexec) {
		this.name = name;
		this.trans = transqueryexec;
		checkForzaValidazione = ProcessorsUtils.checkForzaValidazione(trans);
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}
		String contesto = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String codTipoMov = record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString() : "";
		// checkForzaValidazione = false allora il movimento non viene rimosso e messo nella am_movimento_appoggio_doppi
		checkForzaValidazione = false;

		Boolean permettiImpatti = new Boolean(true);
		if (record.get("PERMETTIIMPATTI") != null) {
			permettiImpatti = new Boolean((String) record.get("PERMETTIIMPATTI"));
		}
		boolean canView = true;
		SessionContainer session = RequestContainer.getRequestContainer().getSessionContainer();
		User usr = (User) session.getAttribute(User.USERID);
		// Controllo competenza amministrativa sul lavoratore e/o azienda nel caso di inserimento(o rettifica)
		if (contesto.equalsIgnoreCase("inserisci") && record.get("CDNLAVORATORE") != null
				&& record.get("PRGAZIENDA") != null && record.get("PRGUNITAPRODUTTIVA") != null) {
			String pageDaValutare = "";
			if (codTipoMov.equals("AVV")) {
				pageDaValutare = "MovDettaglioAvviamentoInserisciPage";
			} else {
				if (codTipoMov.equals("CES")) {
					pageDaValutare = "MovDettaglioCessazioneInserisciPage";
				} else {
					if (codTipoMov.equals("TRA") || codTipoMov.equals("PRO")) {
						pageDaValutare = "MovDettaglioTrasfProInserisciPage";
					}
				}
			}

			if (!pageDaValutare.equals("")) {
				boolean canViewInsert = false;
				boolean canEditLavInsert = false;
				boolean canEditAzInsert = false;
				TransactionProfileDataFilter tProfileFilter = new TransactionProfileDataFilter(usr, pageDaValutare,
						trans.getDataConnection());
				tProfileFilter.setCdnLavoratore((BigDecimal) record.get("CDNLAVORATORE"));
				canEditLavInsert = tProfileFilter.canEditLavoratore();
				tProfileFilter.setCdnLavoratore(null);
				tProfileFilter.setPrgAzienda((BigDecimal) record.get("PRGAZIENDA"));
				tProfileFilter.setPrgUnita((BigDecimal) record.get("PRGUNITAPRODUTTIVA"));
				canEditAzInsert = tProfileFilter.canEditUnitaAzienda();
				canViewInsert = (canEditLavInsert || canEditAzInsert);
				if (!canViewInsert) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_NO_PERMESSI_LAV_AZ),
							"Manca la competenza amministrativa sul lavoratore e/o sull'azienda.", warnings, nested);
				}
			}
		}

		// PermettiImpatti nella validazione massiva e valida manuale, dopo l'inserimento del lavoratore
		if ((contesto.equalsIgnoreCase("validazioneMassiva") || contesto.equalsIgnoreCase("valida")
				|| contesto.equalsIgnoreCase("validaArchivio")) && (record.get("CDNLAVORATORE") != null)) {
			String _page = "";
			if (contesto.equalsIgnoreCase("validazioneMassiva"))
				_page = "MovListaValidazionePage";
			else if (record.get("CODTIPOMOV") != null) {
				if (record.get("CODTIPOMOV").equals("AVV"))
					_page = "MovValidaAvviamentoPage";
				else if (record.get("CODTIPOMOV").equals("CES"))
					_page = "MovValidaCessazionePage";
				else if (record.get("CODTIPOMOV").equals("TRA") || record.get("CODTIPOMOV").equals("PRO"))
					_page = "MovValidaTrasfProPage";
			}
			if (!_page.equals("") && (_page != null)) {
				TransactionProfileDataFilter tProfile = new TransactionProfileDataFilter(usr, _page,
						trans.getDataConnection());
				tProfile.setCdnLavoratore((BigDecimal) record.get("CDNLAVORATORE"));
				permettiImpatti = Boolean.valueOf(tProfile.canEditLavoratore());
			}
		}

		if (!permettiImpatti.booleanValue()) {
			Warning w = new Warning(MessageCodes.ImportMov.WAR_NO_COMPETENZA_LAV,
					"L'utente non ha i diritti sul lavoratore");
			warnings.add(w);
		}

		// Se ho i diritti sul lavoratore, o sull'azienda si inserisce il movimento
		if ((contesto.equalsIgnoreCase("validazioneMassiva") || contesto.equalsIgnoreCase("valida")
				|| contesto.equalsIgnoreCase("validaArchivio")) && (record.get("CDNLAVORATORE") != null)
				&& (record.get("PRGAZIENDA") != null) && (record.get("PRGUNITAPRODUTTIVA") != null)) {
			String _page = "";
			if (contesto.equalsIgnoreCase("validazioneMassiva"))
				_page = "MovListaValidazionePage";
			else if (record.get("CODTIPOMOV") != null) {
				if (record.get("CODTIPOMOV").equals("AVV"))
					_page = "MovValidaAvviamentoPage";
				else if (record.get("CODTIPOMOV").equals("CES"))
					_page = "MovValidaCessazionePage";
				else if (record.get("CODTIPOMOV").equals("TRA") || record.get("CODTIPOMOV").equals("PRO"))
					_page = "MovValidaTrasfProPage";
			}
			if (!_page.equals("") && (_page != null)) {
				TransactionProfileDataFilter tProfile = new TransactionProfileDataFilter(usr, _page,
						trans.getDataConnection());
				tProfile.setCdnLavoratore((BigDecimal) record.get("CDNLAVORATORE"));
				Boolean canEditLav = new Boolean(tProfile.canEditLavoratore());
				tProfile.setCdnLavoratore(null);
				tProfile.setPrgAzienda(new BigDecimal(record.get("PRGAZIENDA").toString()));
				tProfile.setPrgUnita(new BigDecimal(record.get("PRGUNITAPRODUTTIVA").toString()));
				Boolean canEditAz = new Boolean(tProfile.canEditUnitaAzienda());
				canView = (canEditLav.booleanValue() || canEditAz.booleanValue());
			}
		}
		// Gestione trasferimenti d'azienda nella validazione massiva dei movimenti.
		// Se il movimento che si sta validando si riferisce a un trasferimento d'azienda, il movimento
		// va inserito anche se non si hanno i permessi sul lavoratore e/o sull'azienda
		String codTipoTrasf = (String) record.get("CODTIPOTRASF");
		if ((contesto.equalsIgnoreCase("validazioneMassiva") || contesto.equalsIgnoreCase("valida")
				|| contesto.equalsIgnoreCase("validaArchivio")) && (codTipoTrasf != null)
				&& (codTipoMov.equalsIgnoreCase("TRA"))) {
			boolean trasferimentoAzienda = (record.get("FLGTRASFER") != null
					&& record.get("FLGTRASFER").toString().equalsIgnoreCase("S")) ? true : false;
			if (trasferimentoAzienda) {
				canView = true;
			}
		}
		// Controllo sui permessi per l'inserimento del movimento
		if (!canView) {
			// se sono in rettifica devo inserire il movimento o perche' ho la competenza per azienda e/o lavoratore
			// oppure se il movimento precedente è stato rettificato e protocollo comunque il nuovo
			String codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
			if (codTipoComunic != null && codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)) {
				// Imposto nella map il nuovo valore di PERMETTIIMPATTI
				record.put("PERMETTIIMPATTI", permettiImpatti.toString());
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
			} else {
				if (record.get("FLGASSDACESS") != null && record.get("FLGASSDACESS").toString().equalsIgnoreCase("S")) {
					// sto nel caso di avviamento veloce (da proroga, trasformazione o cessazione), se non ho i permessi
					// nè sul lavoratore e nè sull'azienda devo fermare il processo di validazione dell'avviamento
					// veloce
					// e consentire al movimento reale di essere inserito orfano (trasferimento lavoratore e avviamento
					// veloce senza permessi)
					Warning w = new Warning(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV,
							"Non si hanno i permessi sul lavoratore e sull'azienda.");
					warnings.add(w);
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested, true);
				} else {
					if (checkForzaValidazione) {
						StoredProcedureCommand command = null;
						DataResult dr = null;
						try {
							DataConnection conn = trans.getDataConnection();
							String sqlStr = SQLStatements.getStatement("ADD_AM_MOVIMENTO_APPOGGIO_DOPPI");
							command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
							// Prelevo i valori dei parametri dalla map
							Object prgMovApp = record.get("PRGMOVIMENTOAPP");
							Object prgMovAppCVE = record.get("PRGMOVIMENTOAPPCVE");
							int paramIndex = 0;
							ArrayList parameters = new ArrayList(4);
							// Parametro di Ritorno
							parameters.add(conn.createDataField("codiceRit", Types.BIGINT, null));
							command.setAsOutputParameters(paramIndex++);
							// preparazione dei Parametri di Input
							parameters.add(conn.createDataField("prgMovApp", Types.BIGINT, prgMovApp));
							command.setAsInputParameters(paramIndex++);
							parameters.add(conn.createDataField("codTipoMov", Types.VARCHAR, codTipoMov));
							command.setAsInputParameters(paramIndex++);
							parameters.add(conn.createDataField("prgMovAppCVE", Types.BIGINT, prgMovAppCVE));
							command.setAsInputParameters(paramIndex++);
							// Chiamata alla Stored Procedure
							dr = command.execute(parameters);
							Warning w = new Warning(MessageCodes.ImportMov.ERR_NO_PERMESSI_LAV_AZ,
								"Non si hanno i permessi sul lavoratore e sull'azienda:il movimento è stato rimosso dalla lista dei movimenti da validare. E' rimasto memorizzato nel sistema ma non è accessibile all'operatore e non comparirà nelle validazioni successive.");
							warnings.add(w);
							return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested, true);
						} catch (Exception e) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"errore nell'inserimento del movimento nella tabella am_movimento_appoggio_doppi",
									(Exception) e);
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_NO_PERMESSI_LAV_AZ),
									"Non si hanno i permessi sul lavoratore e sull'azienda", warnings, nested);
						} finally {
							Utils.releaseResources(null, command, dr);
						}
					} else {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_NO_PERMESSI_LAV_AZ),
								"Non si hanno i permessi sul lavoratore e sull'azienda", warnings, nested);
					}
				}
			}
		}

		// Imposto nella map il nuovo valore di PERMETTIIMPATTI
		record.put("PERMETTIIMPATTI", permettiImpatti.toString());

		// Se ho warning o risultati annidati li inserisco nella risposta, altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}
}
