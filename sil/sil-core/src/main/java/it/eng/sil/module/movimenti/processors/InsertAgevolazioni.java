package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

public class InsertAgevolazioni implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertAgevolazioni.class.getName());
	private String className;
	private String prc;
	// PRGMOVIMENTO APPENA INSERITO
	private String keyTable;
	private BigDecimal userId;
	private TransactionQueryExecutor trans;
	private ArrayList warnings = null;
	private SourceBean sbInfoGenerale = null;

	public InsertAgevolazioni(BigDecimal user, TransactionQueryExecutor transexec, SourceBean sbGenerale) {
		className = this.getClass().getName();
		prc = "Inserisci benefici";
		userId = user;
		trans = transexec;
		sbInfoGenerale = sbGenerale;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		warnings = new ArrayList();

		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		String configproc = configbase + "processors" + File.separator;
		RecordProcessor insertAgevolazione;
		BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
				.getAttribute("_CDUT_");
		String contesto = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		keyTable = record.get("PRGMOVIMENTO") != null ? record.get("PRGMOVIMENTO").toString() : null;
		String curContext = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String table = "am_mov_agev_app";
		if (record == null) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, null);
		}

		try {
			if (contesto.equalsIgnoreCase("validazioneMassiva")) {
				if (curContext.equalsIgnoreCase("validaArchivio")) {
					table = "am_mov_agev_app_archivio";
				}
				String queryAgevolazioni = "select codAgevolazione from " + table + " where prgmovimentoapp = "
						+ record.get("PRGMOVIMENTOAPP");
				SourceBean resultAgevolazioni = ProcessorsUtils.executeSelectQuery(queryAgevolazioni, trans);
				Vector agevolazioniRows = resultAgevolazioni.getAttributeAsVector("ROW");
				if (keyTable != null) {
					for (int nAgev = 0; nAgev < agevolazioniRows.size(); nAgev++) {
						SourceBean sbAgev = (SourceBean) agevolazioniRows.get(nAgev);
						String codAgevolazCurr = sbAgev.getAttribute("codAgevolazione").toString();
						record.put("CODAGEVOLAZIONEMOV", codAgevolazCurr);
						insertAgevolazione = new InsertData("Inserimento Beneficio", trans,
								configproc + "InsertMovimentoAgevolazione.xml", "INSERT_MOVIMENTO_AGEVOLAZIONE", user);
						insertAgevolazione.processRecord(record);
					}
				}
			} else {
				String codAgevolazioneAvv = record.containsKey("CODAGEVOLAZIONE")
						? record.get("CODAGEVOLAZIONE").toString()
						: "";
				if (!codAgevolazioneAvv.equals("")) {
					if (codAgevolazioneAvv.substring(0, 1).equals("[")) {
						codAgevolazioneAvv = codAgevolazioneAvv.substring(1, codAgevolazioneAvv.length() - 1);
					}
					String[] vettAgevolazioniMov = codAgevolazioneAvv.split(",");
					if (keyTable != null) {
						for (int nAgev = 0; nAgev < vettAgevolazioniMov.length; nAgev++) {
							String codAgevolazCurr = vettAgevolazioniMov[nAgev].trim();
							record.put("CODAGEVOLAZIONEMOV", codAgevolazCurr);
							insertAgevolazione = new InsertData("Inserimento Beneficio", trans,
									configproc + "InsertMovimentoAgevolazione.xml", "INSERT_MOVIMENTO_AGEVOLAZIONE",
									user);
							insertAgevolazione.processRecord(record);
						}
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InsertAgevolazioni", e);
			return ProcessorsUtils.createResponse(className, className,
					new Integer(MessageCodes.General.OPERATION_FAIL), "Inserimento benefici fallito. ", warnings, null);
		}
		return null;
	}
}