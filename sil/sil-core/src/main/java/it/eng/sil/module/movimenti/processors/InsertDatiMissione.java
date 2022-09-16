package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

public class InsertDatiMissione implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertTirocinio.class.getName());
	private String className;
	private String prc;
	// PrgMovimento appena inserito
	private String keyTable;
	private BigDecimal userId;
	private TransactionQueryExecutor trans;
	private ArrayList warnings = null;
	private String missione = "";
	private SourceBean sbInfoGenerale = null;

	public InsertDatiMissione(BigDecimal user, TransactionQueryExecutor transexec, SourceBean sbGenerale) {
		className = this.getClass().getName();
		prc = "Inserisci Missione";
		userId = user;
		trans = transexec;
		sbInfoGenerale = sbGenerale;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		String columns = "";
		String values = "";
		warnings = new ArrayList();

		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		String configproc = configbase + "processors" + File.separator;
		RecordProcessor insertMissione;
		BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
				.getAttribute("_CDUT_");

		if (record == null) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, null);
		}
		try {
			String datinizioMis = record.get("DATINIZIORAPLAV") != null ? record.get("DATINIZIORAPLAV").toString() : "";
			String codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString()
					: "";
			String flgAssPropria = record.get("FLGASSPROPRIA") != null ? (String) record.get("FLGASSPROPRIA") : "";
			boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
			// Controllo se si tratta di una missione
			if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria && !datinizioMis.equals("")) {
				// Se il record è nullo non lo posso elaborare, ritorno l'errore
				try {
					keyTable = record.get("PRGMOVIMENTO").toString();
					if (keyTable != null) {
						// record.put("PRGMOVIMENTO", keyTable);
						insertMissione = new InsertData("Inserimento Missione", trans,
								configproc + "InsertMovimentoMissione.xml", "INSERT_MOVIMENTO_MISSIONE", user);
						insertMissione.processRecord(record);

						// return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "InsertMissione", e);
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InsertMissione", e);
		}
		return null;
	}
}