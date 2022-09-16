package it.eng.sil.module.batch;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.batch.InviaSMS;

public class ListaSimulazioneProgrammazione implements IDynamicStatementProvider {
	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ListaSimulazioneProgrammazione.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String codTipoBatch = StringUtils.getAttributeStrNotNull(req, "codtipobatch");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "codcpi");
		BigDecimal prgMotContatto = req.getAttribute("prgmotcontatto") != null
				&& !req.getAttribute("prgmotcontatto").toString().equals("")
						? new BigDecimal(req.getAttribute("prgmotcontatto").toString())
						: null;
		BigDecimal numGGPrecAvviso = req.getAttribute("numggprecavviso") != null
				&& !req.getAttribute("numggprecavviso").toString().equals("")
						? new BigDecimal(req.getAttribute("numggprecavviso").toString())
						: null;
		String codServizio = StringUtils.getAttributeStrNotNull(req, "codservizio");
		String codStatoApp = StringUtils.getAttributeStrNotNull(req, "codstatoappuntamento");
		BigDecimal numPeriodoDate = req.getAttribute("numggperiododate") != null
				&& !req.getAttribute("numggperiododate").toString().equals("")
						? new BigDecimal(req.getAttribute("numggperiododate").toString())
						: null;
		BigDecimal prgAzioni = req.getAttribute("prgazioni") != null
				&& !req.getAttribute("prgazioni").toString().equals("")
						? new BigDecimal(req.getAttribute("prgazioni").toString())
						: null;
		String codEsito = StringUtils.getAttributeStrNotNull(req, "codesito");
		String motivoFineAttoDid = StringUtils.getAttributeStrNotNull(req, "CODMOTIVOFINEATTODID");
		String flgIscrizioneCM = StringUtils.getAttributeStrNotNull(req, "FLGCMISCR");
		BigDecimal numGiorniRangeFineDid = req.getAttribute("numggrangefinedid") != null
				&& !req.getAttribute("numggrangefinedid").toString().equals("")
						? new BigDecimal(req.getAttribute("numggrangefinedid").toString())
						: null;
		String query_totale = "";

		Integer chiaveBatch = Constants.mapBatch.get(codTipoBatch);
		try {
			InviaSMS inviaSms = new InviaSMS(chiaveBatch.intValue(), codServizio, codStatoApp, prgMotContatto,
					numGGPrecAvviso, numPeriodoDate, prgAzioni, codEsito, motivoFineAttoDid, flgIscrizioneCM,
					numGiorniRangeFineDid, null, null, null, null, null, null, codCpi);

			query_totale = inviaSms.getSqlQuery();
			// Debug
			_logger.debug(className + "::Stringa di ricerca:" + query_totale);

			return query_totale;
		} catch (Exception e) {
			_logger.error(className, e);
			return null;
		}
	}
}