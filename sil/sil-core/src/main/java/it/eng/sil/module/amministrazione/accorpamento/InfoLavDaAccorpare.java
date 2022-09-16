/*
 * Created on Jun 21, 2005
 *
 */
package it.eng.sil.module.amministrazione.accorpamento;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Il modulo esegue per i due lavoratori passati nella service request le stesse operazioni di lettura dati. Il nome
 * della statement corrisponde al nome dell'elemento query del modulo. I risultati vengono restituiti in SourceBean con
 * chiave NOME_MODULO.LAVX.NOME_QUERY.ROWS.ROW
 * 
 * @author savino
 */
public class InfoLavDaAccorpare extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfoLavDaAccorpare.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		try {
			String cdnLavoratore1 = (String) serviceRequest.getAttribute("cdnLavoratore1");
			String cdnLavoratore2 = (String) serviceRequest.getAttribute("cdnLavoratore2");
			Object prgDichDisponibilita = null;
			disableMessageIdSuccess();
			// lavoratore 1
			serviceRequest.updAttribute("cdnLavoratore", cdnLavoratore1);
			SourceBean lav1 = new SourceBean("LAV1");
			executeQuery("GET_AN_LAVORATORE_ANAG", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_AN_LAVORATORE_INDIRIZZI", lav1, serviceRequest, serviceResponse);
			executeQuery("GET_ULTIMO_AN_DISPO_DA_CDNLAV", lav1, serviceRequest, serviceResponse);
			prgDichDisponibilita = lav1.getAttribute("GET_ULTIMO_AN_DISPO_DA_CDNLAV.ROWS.ROW.PRGDICHDISPONIBILITA");
			// per l'esecuzione della query dinamica che estrae i dati della
			// protocollazione della did
			// aperta di un lavoratore bisogna fornire il tipo di documento e la
			// page associata
			if (prgDichDisponibilita != null) {
				serviceRequest.updAttribute("PRGDICHDISPONIBILITA", prgDichDisponibilita);
				executeQueryDispo("GET_AN_DISPO", lav1, serviceRequest, serviceResponse);
				serviceRequest.updAttribute("CODTIPODOCUMENTO", "IM");
				serviceRequest.updAttribute("PAGE", "DISPODETTAGLIOPAGE");
				executeDynamicQuery("PROTOCOLLO_DID", lav1, serviceRequest, serviceResponse);
				// rimuovo gli attributi inseriti.
				// ATTENZIONE: se altre query utilizzassero questi attributi,
				// potrebbe essere necessario reimpostare il
				// valore originario inviato dal browser.
				serviceRequest.delAttribute("PAGE");
				serviceRequest.delAttribute("CODTIPODOCUMENTO");
			}
			executeQuery("GET_ULTIMO_MOV", lav1, serviceRequest, serviceResponse);
			// lavoratore 2
			serviceRequest.updAttribute("cdnLavoratore", cdnLavoratore2);
			SourceBean lav2 = new SourceBean("LAV2");
			executeQuery("GET_AN_LAVORATORE_ANAG", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_AN_LAVORATORE_INDIRIZZI", lav2, serviceRequest, serviceResponse);
			executeQuery("GET_ULTIMO_AN_DISPO_DA_CDNLAV", lav2, serviceRequest, serviceResponse);
			prgDichDisponibilita = lav2.getAttribute("GET_ULTIMO_AN_DISPO_DA_CDNLAV.ROWS.ROW.PRGDICHDISPONIBILITA");
			// per l'esecuzione della query dinamica che estrae i dati della
			// protocollazione della did
			// aperta di un lavoratore bisogna fornire il tipo di documento e la
			// page associata
			if (prgDichDisponibilita != null) {
				serviceRequest.updAttribute("PRGDICHDISPONIBILITA", prgDichDisponibilita);
				executeQueryDispo("GET_AN_DISPO", lav2, serviceRequest, serviceResponse);
				serviceRequest.updAttribute("CODTIPODOCUMENTO", "IM");
				serviceRequest.updAttribute("PAGE", "DISPODETTAGLIOPAGE");
				executeDynamicQuery("PROTOCOLLO_DID", lav2, serviceRequest, serviceResponse);
				serviceRequest.delAttribute("PAGE");
				serviceRequest.delAttribute("CODTIPODOCUMENTO");
			}
			executeQuery("GET_ULTIMO_MOV", lav2, serviceRequest, serviceResponse);
			serviceResponse.setAttribute(lav1);
			serviceResponse.setAttribute(lav2);
		} catch (Exception e) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
			reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		}

	}

	private void executeQuery(String query, SourceBean lav, SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		setSectionQuerySelect(query);
		SourceBean row = doSelect(serviceRequest, serviceResponse, false);
		if (row == null)
			throw new Exception("impossibile recuperare le informazioni relative a " + query + " per il lavoratore "
					+ lav.getName());
		lav.setAttribute(query, row);
	}

	private void executeQueryDispo(String query, SourceBean lav, SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		setSectionQuerySelect(query);
		SourceBean row = doSelect(serviceRequest, serviceResponse, false);
		if (row == null)
			throw new Exception("impossibile recuperare le informazioni relative a " + query + " per il lavoratore "
					+ lav.getName());
		Vector rowDid = row.getAttributeAsVector("ROW");
		if (rowDid != null && rowDid.size() > 1) {
			SourceBean ris = new SourceBean("ROWS");
			row = (SourceBean) rowDid.elementAt(0);
			ris.setAttribute(row);
			lav.setAttribute(query, ris);
		} else {
			lav.setAttribute(query, row);
		}
	}

	private void executeDynamicQuery(String query, SourceBean lav, SourceBean serviceRequest,
			SourceBean serviceResponse) throws Exception {
		setSectionQuerySelect(query);
		SourceBean row = doDynamicSelect(serviceRequest, serviceResponse, false);
		if (row == null)
			throw new Exception("impossibile recuperare le informazioni relative a " + query + " per il lavoratore "
					+ lav.getName());
		lav.setAttribute(query, row);
	}

}