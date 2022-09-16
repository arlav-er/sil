package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class ApplicaFiltro extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ApplicaFiltro.class.getName());

	public void service(SourceBean request, SourceBean response) {
		TransactionQueryExecutor txExec = null;
		boolean ret = false;
		boolean filtraEta = request.containsAttribute("eta");
		boolean filtraNazione = request.containsAttribute("codCittadinanza_1");
		boolean filtraSesso = request.containsAttribute("sesso");
		boolean filtraSvantaggiati = request.containsAttribute("flgSV");
		boolean filtraDisNonIscritti = request.containsAttribute("flgDis");
		Vector<String> listIscr = new Vector<String>();
		if (request.containsAttribute("CodAltreIscr")) {
			listIscr = request.getAttributeAsVector("CodAltreIscr");
		}
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdSuccess();
		try {
			txExec = new TransactionQueryExecutor(getPool());
			txExec.initTransaction();
			enableTransactions(txExec);
			setSectionQueryUpdate("APPLICA_FILTRO");
			if (listIscr.size() > 0) {
				request.setAttribute("FLGFILTROAISCR", Values.FLAG_TRUE);
			}
			ret = doUpdate(request, response);
			if (!ret)
				throw new Exception("aggiornamento dell'applicazione del filtro fallito. Operazione interrotta");
			if (filtraEta) {
				setSectionQueryUpdate("FILTRO_ETA");
				setMessageIdFail(MessageCodes.IDO.FILTRA_ETA);
				ret = doDynamicUpdate(request, response);
				if (!ret)
					throw new Exception("l'applicazione del filtro per eta' fallito. Operazione interrotta");
			}
			if (filtraNazione) {
				setSectionQueryUpdate("FILTRO_NAZIONE");
				setMessageIdFail(MessageCodes.IDO.FILTRA_NAZIONE);
				ret = doDynamicUpdate(request, response);
				if (!ret)
					throw new Exception("l'applicazione del filtro per nazionalita' fallito. Operazione interrotta");
			}
			if (filtraSesso) {
				setSectionQueryUpdate("FILTRO_SESSO");
				setMessageIdFail(MessageCodes.IDO.FILTRA_SESSO);
				ret = doUpdate(request, response);
				if (!ret)
					throw new Exception("l'applicazione del filtro per sesso fallito. Operazione interrotta");
			}
			if (filtraSvantaggiati) {
				setSectionQueryUpdate("FILTRO_ISCRIZIONI");
				setMessageIdFail(MessageCodes.IDO.FILTRA_ISCRIZIONI);
				ret = doUpdate(request, response);
				if (!ret)
					throw new Exception("l'applicazione del filtro per svantaggiati fallito. Operazione interrotta");
			}
			if (filtraDisNonIscritti) {
				setSectionQueryUpdate("FILTRO_DISABILI");
				setMessageIdFail(MessageCodes.IDO.FILTRA_DISABILI);
				ret = doUpdate(request, response);
				if (!ret)
					throw new Exception(
							"l'applicazione del filtro per disabili non iscritti fallito. Operazione interrotta");
			}
			if (listIscr.size() > 0) {
				setMessageIdFail(MessageCodes.IDO.FILTRA_ALTRE_ISCRIZIONI);
				for (int i = 0; i < listIscr.size(); i++) {
					String codTipoIscr = listIscr.elementAt(i);
					if (!codTipoIscr.equals("")) {
						request.setAttribute("CODTIPOISCR", codTipoIscr);
						setSectionQueryInsert("INS_FILTRO_ALTRE_ISCRIZIONI");
						ret = doInsert(request, response);
						if (!ret) {
							throw new Exception(
									"l'applicazione del filtro per altre iscrizioni fallito. Operazione interrotta");
						}
						request.delAttribute("CODTIPOISCR");
					}
				}
				setSectionQueryUpdate("FILTRO_ALTRE_ISCRIZIONI");
				ret = doUpdate(request, response);
				if (!ret) {
					throw new Exception(
							"l'applicazione del filtro per altre iscrizioni fallito. Operazione interrotta");
				}
			}
			txExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			_logger.debug("applicazione filtro rosa: ");

		} catch (Exception e) {
			if (getErrorHandler().getErrors().isEmpty())
				reportOperation.reportFailure(getMessageIdFail(), e, "Impossibile ", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile applicare il filtro alla rosa", e);

			if (txExec != null)
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile eseguire la rollback nell'applicazione del filtro alla rosa", (Exception) e1);

				}
		}

	}

} // class ApplicaFiltro
