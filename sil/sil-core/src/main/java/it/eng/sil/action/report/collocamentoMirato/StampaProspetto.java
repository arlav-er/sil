package it.eng.sil.action.report.collocamentoMirato;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;

public class StampaProspetto extends AbstractSimpleReport {
	private static final long serialVersionUID = 1L;

	private static final String disabili = "D";
	private static final String centralinisti = "C";
	private static final String massoFisio = "M";
	private static final String telelavoro = "T";
	private static final String disabiliSomm = "S";
	private static final String disabiliConv = "V";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaProspetto.class.getName());

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		setStrDescrizione("Stampa Prospetto");
		setReportPath("Collocamento_Mirato/Prospetto_CC.rpt");

		// impostazione parametri del report
		Map prompts = new HashMap();
		String prgProspettoInf = (String) request.getAttribute("PRGPROSPETTOINF");
		BigDecimal totaliDisabiliPT = new BigDecimal("0");
		BigDecimal totaliCentralinistiPT = new BigDecimal("0");
		BigDecimal totaliMassoPT = new BigDecimal("0");
		BigDecimal totaliTelelavoroPT = new BigDecimal("0");
		BigDecimal totaliDisSommPT = new BigDecimal("0");
		BigDecimal totaliDisConvPT = new BigDecimal("0");

		BigDecimal totaliDisabiliPTOltre50 = new BigDecimal("0");
		BigDecimal totaliCentralinistiPTOltre50 = new BigDecimal("0");
		BigDecimal totaliMassoPTOltre50 = new BigDecimal("0");
		BigDecimal totaliTelelavoroPTOltre50 = new BigDecimal("0");
		BigDecimal totaliDisSommPTOltre50 = new BigDecimal("0");
		BigDecimal totaliDisConvPTOltre50 = new BigDecimal("0");

		BigDecimal totaliDisabiliPTRip = new BigDecimal("0");
		BigDecimal totaliCentralinistiPTRip = new BigDecimal("0");
		BigDecimal totaliMassoPTRip = new BigDecimal("0");
		BigDecimal totaliTelelavoroPTRip = new BigDecimal("0");
		BigDecimal totaliDisSommPTRip = new BigDecimal("0");
		BigDecimal totaliDisConvPTRip = new BigDecimal("0");

		try {
			Object[] params = new Object[] { request.getAttribute("PRGPROSPETTOINF") };
			SourceBean rowTotaliPT = doSelect("GET_TOTALI_PARTTIME_PROSPETTO", params);
			Vector rows = rowTotaliPT.getAttributeAsVector("ROW");

			if (rows != null && !rows.isEmpty()) {
				for (int k = 0; k < rows.size(); k++) {
					SourceBean row = (SourceBean) rows.get(k);
					String categoria = (String) row.getAttribute("codtipoptdisabile");
					BigDecimal numLavoratori = (BigDecimal) row.getAttribute("totale");
					if (categoria != null && categoria.equalsIgnoreCase(disabili)) {
						totaliDisabiliPT = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(centralinisti)) {
						totaliCentralinistiPT = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(massoFisio)) {
						totaliMassoPT = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(telelavoro)) {
						totaliTelelavoroPT = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(disabiliSomm)) {
						totaliDisSommPT = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(disabiliConv)) {
						totaliDisConvPT = numLavoratori;
					}
				}
			}

			rowTotaliPT = doSelect("GET_TOTALI_PARTTIME_OLTRE50_PROSPETTO", params);
			rows = rowTotaliPT.getAttributeAsVector("ROW");

			if (rows != null && !rows.isEmpty()) {
				for (int k = 0; k < rows.size(); k++) {
					SourceBean row = (SourceBean) rows.get(k);
					String categoria = (String) row.getAttribute("codtipoptdisabile");
					BigDecimal numLavoratori = (BigDecimal) row.getAttribute("totale");
					if (categoria != null && categoria.equalsIgnoreCase(disabili)) {
						totaliDisabiliPTOltre50 = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(centralinisti)) {
						totaliCentralinistiPTOltre50 = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(massoFisio)) {
						totaliMassoPTOltre50 = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(telelavoro)) {
						totaliTelelavoroPTOltre50 = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(disabiliSomm)) {
						totaliDisSommPTOltre50 = numLavoratori;
					} else if (categoria != null && categoria.equalsIgnoreCase(disabiliConv)) {
						totaliDisConvPTOltre50 = numLavoratori;
					}
				}
			}

			rowTotaliPT = doSelect("GET_TOTALI_PARTTIME_RIPROPORZ_PROSPETTO", params);
			rows = rowTotaliPT.getAttributeAsVector("ROW");

			if (rows != null && !rows.isEmpty()) {
				for (int k = 0; k < rows.size(); k++) {
					SourceBean row = (SourceBean) rows.get(k);
					String categoria = (String) row.getAttribute("codtipoptdisabile");
					BigDecimal totale = (BigDecimal) row.getAttribute("totale");
					double totaliP = totale.doubleValue();
					int parteIntera = new BigDecimal(totaliP).intValue();
					if (totaliP - parteIntera > 0.5) {
						totale = new BigDecimal(parteIntera).add(new BigDecimal(1));
					} else {
						totale = new BigDecimal(parteIntera);
					}
					if (categoria != null && categoria.equalsIgnoreCase(disabili)) {
						totaliDisabiliPTRip = totale;
					} else if (categoria != null && categoria.equalsIgnoreCase(centralinisti)) {
						totaliCentralinistiPTRip = totale;
					} else if (categoria != null && categoria.equalsIgnoreCase(massoFisio)) {
						totaliMassoPTRip = totale;
					} else if (categoria != null && categoria.equalsIgnoreCase(telelavoro)) {
						totaliTelelavoroPTRip = totale;
					} else if (categoria != null && categoria.equalsIgnoreCase(disabiliSomm)) {
						totaliDisSommPTRip = totale;
					} else if (categoria != null && categoria.equalsIgnoreCase(disabiliConv)) {
						totaliDisConvPTRip = totale;
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
			return;
		}

		prompts.put("prgProspettoInf", prgProspettoInf);
		prompts.put("totaliDisabiliPT", totaliDisabiliPT.toString());
		prompts.put("totaliCentralinistiPT", totaliCentralinistiPT.toString());
		prompts.put("totaliMassoPT", totaliMassoPT.toString());
		prompts.put("totaliTelelavoroPT", totaliTelelavoroPT.toString());
		prompts.put("totaliDisSommPT", totaliDisSommPT.toString());
		prompts.put("totaliDisConvPT", totaliDisConvPT.toString());
		prompts.put("totaliDisabiliPTOltre50", totaliDisabiliPTOltre50.toString());
		prompts.put("totaliCentralinistiPTOltre50", totaliCentralinistiPTOltre50.toString());
		prompts.put("totaliMassoPTOltre50", totaliMassoPTOltre50.toString());
		prompts.put("totaliTelelavoroPTOltre50", totaliTelelavoroPTOltre50.toString());
		prompts.put("totaliDisSommPTOltre50", totaliDisSommPTOltre50.toString());
		prompts.put("totaliDisConvPTOltre50", totaliDisConvPTOltre50.toString());
		prompts.put("totaliDisabiliPTRip", totaliDisabiliPTRip.toString());
		prompts.put("totaliCentralinistiPTRip", totaliCentralinistiPTRip.toString());
		prompts.put("totaliMassoPTRip", totaliMassoPTRip.toString());
		prompts.put("totaliTelelavoroPTRip", totaliTelelavoroPTRip.toString());
		prompts.put("totaliDisSommPTRip", totaliDisSommPTRip.toString());
		prompts.put("totaliDisConvPTRip", totaliDisConvPTRip.toString());

		// ora si chiede di usare il passaggio dei parametri per nome e
		// non per posizione (col vettore, passaggio di default)
		setPromptFields(prompts);

		String tipoFile = (String) request.getAttribute("tipoFile");
		if (tipoFile != null)
			setStrNomeDoc("Prospetto." + tipoFile);
		else
			setStrNomeDoc("Prospetto.pdf");

		String tipoDoc = (String) request.getAttribute("tipoDoc");

		if (tipoDoc != null) {
			setCodTipoDocumento(tipoDoc);
		}

		try {
			showDocument(request, response);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName() + "::service()", e);
			return;
		}

	}

	protected SourceBean doSelect(String statement, Object[] params) throws Exception {
		SourceBean beanRows = null;
		beanRows = (SourceBean) QueryExecutor.executeQuery(statement, params, "SELECT", Values.DB_SIL_DATI);
		return beanRows;
	}

	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}
}
