package it.eng.sil.action.report.anagrafica;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;

/**
 * @author rolfini featuring roccetti (TraIntraProvinciale), savino (page to action )
 * 
 */
public class RichiestaDocumenti extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RichiestaDocumenti.class.getName());

	private it.eng.afExt.utils.TransactionQueryExecutor txExecutor;

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		String isPresaAtto = (String) request.getAttribute("isPresaAtto");
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			setStrDescrizione("Richiesta documenti");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("RichiestaDocumenti." + tipoFile);
			else
				setStrNomeDoc("RichiestaDocumenti.pdf");

			/*
			 * Per la regione Valle d'Aosta il report è personalizzato nell'intestazione Si è deciso di suddividere le
			 * personalizzazioni per regioni in diversi file di report ATTENZIONE: in caso di bugfix apportare la
			 * modifica in tutti i file di report.
			 */
			AccessoSemplificato _db = new AccessoSemplificato(this);
			SourceBean beanRows = null;
			_db.setSectionQuerySelect("GET_CODREGIONE");
			beanRows = _db.doSelect(request, response, false);

			String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

			// nel caso della VALLE D'AOSTA (codregione=2) è stata creata una nuova stampa
			if (regione.equals("2"))
				setReportPath("Anagrafica/RichiestaDoc_VDA_CC.rpt");
			else
				setReportPath("Anagrafica/RichiestaDoc_CC.rpt");

			Map prompts = new HashMap();
			prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
			prompts.put("isPresaAtto", request.getAttribute("isPresaAtto"));
			Object cdnUt = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			prompts.put("pCdnUt", cdnUt.toString());
			// solo se e' richiesta la protocollazione i parametri vengono
			// inseriti nella Map
			// se manca anche solo un parametro il metodo lancia una eccezione.
			try {
				addPromptFieldsProtocollazione(prompts, request);
			} catch (EMFUserError ue) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "", (XMLObject) ue);

				setOperationFail(request, response, ue);
				reportOperation.reportFailure(ue, "RichiestaDocumenti.service()",
						"la visualizzazione dei dati della protocollazione non e' possibile a causa della mancanza di un parametro necessario al report.");
				return;
			}
			// ora si chiede di usare il passaggio dei parametri per nome e
			// non per posizione (col vettore, passaggio di default)
			setPromptFields(prompts);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true")) {
				try {
					AccessoSemplificato db = new AccessoSemplificato(this);
					txExecutor = new TransactionQueryExecutor(db.getPool());
					db.enableTransactions(txExecutor);
					txExecutor.initTransaction();
					db.disableMessageIdFail();
					db.disableMessageIdSuccess();
					// SourceBean row = doSelect(request, response,
					// "GET_INFO_STAMPA_TRASF");
					db.setSectionQuerySelect("GET_INFO_STAMPA_TRASF");
					SourceBean row = db.doSelect(request, response);
					if (row == null)
						throw new Exception("impossibile leggere le informazioni per la stampa richiesta documenti");

					String presaAtto = (String) request.getAttribute("isPresaAtto");
					checkStampaPossibile(row, Boolean.valueOf(presaAtto).booleanValue(), reportOperation);
					Object prg = row.getAttribute("row.prgLavStoriaInf");
					setStrChiavetabella(prg.toString());
					if (!insertDocument(request, response, txExecutor))
						throw new Exception("Processo di generazione del report fallito");
					request.setAttribute("flgStampaDoc", "S");
					aggiornaInfoStampa(request, response, db);
					if (isPresaAtto != null && isPresaAtto.equals("false")) {
						// sono in trasferimento, sicuramente avvenuto
						getRequestContainer().getSessionContainer().setAttribute("trasferito", "true");
						getRequestContainer().getSessionContainer().setAttribute("richDocStampata", "true");
					}
					txExecutor.commitTransaction();
					reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
				} catch (Exception e) {
					try {
						if (txExecutor != null) {
							txExecutor.rollBackTransaction();
						}
						setOperationFail(request, response, e);
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					} catch (Exception e1) {
						setOperationFail(request, response, e1);
						reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					}
				}
			} else if (apri != null && apri.equalsIgnoreCase("true")) {
				showDocument(request, response);
			}
		}
	}

	private void aggiornaInfoStampa(SourceBean request, SourceBean response, AccessoSemplificato db) throws Exception {
		db.setSectionQuerySelect("GET_INFO_STAMPA_TRASF");
		SourceBean row = db.doSelect(request, response);
		if (row == null)
			throw new Exception("impossibile leggere l'informazione sulla stampa del trasferimento di un lavoratore");
		// SourceBean row = doSelect(request, response,
		// "GET_INFO_STAMPA_TRASF");
		BigDecimal numKlo = (BigDecimal) row.getAttribute("row.numklolavstoriainf");
		Object prgLavStoriaInf = row.getAttribute("row.prgLavStoriaInf");
		numKlo = numKlo.add(new BigDecimal(1));
		request.updAttribute("numKloLavStoriaInf", numKlo);
		request.updAttribute("prgLavStoriaInf", prgLavStoriaInf);
		db.setSectionQueryUpdate("UPDATE_INFO_STAMPA");
		boolean ret = db.doUpdate(request, response);
		// Boolean ret = (Boolean)doUpdate(request, response,
		// "UPDATE_INFO_STAMPA");
		if (!ret)
			throw new Exception("impossibile aggiornare l'informazione di stampa del trasferimento di un lavoratore");
	}

	private void checkStampaPossibile(SourceBean row, boolean presaAtto, ReportOperationResult reportOperation)
			throws Exception {
		// vedo se la stampa non sia gia' stata protocollata
		String flgStampaTrasf = (String) row.getAttribute("row.flgStampaTrasf");
		String flgStampaDoc = (String) row.getAttribute("row.flgStampaDoc");
		if (!presaAtto && flgStampaTrasf != null && flgStampaTrasf.equals("S") && flgStampaDoc != null
				&& flgStampaDoc.equals("N"))
			;
		else if (presaAtto && flgStampaDoc != null && flgStampaDoc.equals("N") && flgStampaTrasf == null)
			;
		else {
			int errCode = presaAtto ? MessageCodes.Trasferimento.STAMPA_RICH_DOC_PRESA_ATTO
					: MessageCodes.Trasferimento.STAMPA_RICH_DOC_PRESA_ATTO;
			reportOperation.reportFailure(errCode);
			throw new Exception("Stampa della richiesta documenti già fatta");
		}
	}

}