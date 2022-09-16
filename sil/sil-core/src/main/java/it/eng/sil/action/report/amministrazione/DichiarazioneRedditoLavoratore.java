package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;

public class DichiarazioneRedditoLavoratore extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DichiarazioneRedditoLavoratore.class.getName());

	private it.eng.afExt.utils.TransactionQueryExecutor txExecutor;

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				txExecutor = new it.eng.afExt.utils.TransactionQueryExecutor(Values.DB_SIL_DATI);
				txExecutor.initTransaction();

				settaParametri(request, response);

				BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
				if (numProt != null) {
					aggiornaStato(request, response);
				}

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
				txExecutor.commitTransaction();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore durante l'inserimento della dichiarazione", e);

				try {
					response.updAttribute("operationResult", "ERROR");
					txExecutor.rollBackTransaction();
				} catch (Exception rbe) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile eseguire la rollBack", rbe);

				}
			}
		}
	}

	private void settaParametri(SourceBean request, SourceBean response) throws EMFUserError {
		/*
		 * LO FA GIA' LA SUPERCLASSE: String tipoDoc = (String) request.getAttribute("tipoDoc"); if (tipoDoc != null )
		 * setCodTipoDocumento(tipoDoc);
		 */

		String tipoFile = (String) request.getAttribute("tipoFile");
		if (tipoFile != null)
			setStrNomeDoc("DichRedLav." + tipoFile);
		else
			setStrNomeDoc("DichRedLav.pdf");

		setStrDescrizione("Dichiarazione reddituale del lavoratore");
		setReportPath("Amministrazione/DichRedLav_CC.rpt");

		// Settaggio strchiavetabella
		String strChiaveTabella = request.containsAttribute("strChiaveTabella")
				? (String) request.getAttribute("strChiaveTabella")
				: "";
		if ((strChiaveTabella != null) && !strChiaveTabella.equals("")) {
			setStrChiavetabella(strChiaveTabella);
		}

		String pagina = request.containsAttribute("pagina") ? (String) request.getAttribute("pagina") : "";
		if (!pagina.equalsIgnoreCase("null"))
			setPagina(pagina);

		String datDichiarazione = null;
		try {
			SourceBean row = doSelect(request, response, "GET_DID_DICH_LAV");
			datDichiarazione = (String) row.getAttribute("ROW.DATDICHIARAZIONE");
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DichiarazioneRedditoLavoratore:Recupero data DID",
					(Exception) e);

		}

		// impostazione parametri del report
		Map prompts = new HashMap();
		prompts.put("p_prgDichLav", request.getAttribute("prgdichlav"));
		prompts.put("datDichiarazione", datDichiarazione);

		// solo se e' richiesta la protocollazione i parametri vengono inseriti
		// nella Map
		addPromptFieldsProtocollazione(prompts, request);

		// ora si chiede di usare il passaggio dei parametri per nome e
		// non per posizione (col vettore, passaggio di default)
		setPromptFields(prompts);

	}

	private void aggiornaStato(SourceBean request, SourceBean response) throws Exception {
		Boolean ret = null;
		request.setAttribute("codStatoAtto", "PR");
		try {
			ret = (Boolean) doUpdate(request, response, "UPD_STATO_DICH");
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"DichiarazioneRedditoLavoratore:Aggiornamento stato dichiarazione", (Exception) e);

		}
		if (!ret.booleanValue())
			throw new Exception("impossibile aggiornare lo stato della dichiarazione");
	}

	public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception {
		SourceBean statement = getSelectStatement(queryName);

		SourceBean beanRows = null;
		beanRows = (SourceBean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement,
				"SELECT");
		try {

			response.setAttribute("SELECT_OK", "TRUE");
		} catch (Exception ex) {
			response.setAttribute("SELECT_FAIL", "TRUE");
		}

		return beanRows;
	}

	public Object doUpdate(SourceBean request, SourceBean response, String queryName) throws Exception {

		SourceBean statement = getSelectStatement(queryName);

		Boolean beanRows = null;
		beanRows = (Boolean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement,
				"UPDATE");
		try {
			response.setAttribute("UPDATE_OK", "TRUE");
		} catch (Exception ex) {
			response.setAttribute("UPDATE_FAIL", "TRUE");
		}

		return beanRows;
	}

	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

}