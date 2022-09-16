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

public class InsertAgevolazioniApp implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertAgevolazioni.class.getName());
	private String className;
	private String name;
	private TransactionQueryExecutor trans;
	private Object keyTable;
	private boolean avviamentoVeloce = false;

	public InsertAgevolazioniApp(String name, TransactionQueryExecutor transexec, boolean avviamentoCPTVE) {
		className = this.getClass().getName();
		name = "Inserisci benefici";
		trans = transexec;
		avviamentoVeloce = avviamentoCPTVE;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		String configproc = configbase + "processors" + File.separator;
		RecordProcessor insertAgevolazione;
		BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
				.getAttribute("_CDUT_");

		if (record == null) {
			return ProcessorsUtils.createResponse(name, className, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, null);
		}
		try {
			// CO UniLav l'agevolazione viene riportata in BeneficiAvv (avv, pro, tra, ces)
			// CO UniSomm l'agevolazione viene riportata in BeneficiAvv (avv, pro, tra, ces)
			// CO VarDatori l'agevolazione viene riportata in NuoviBeneficiProTrasf
			String codAgevolazioneApp = record.containsKey("BeneficiAvv") ? record.get("BeneficiAvv").toString() : "";
			if (codAgevolazioneApp.equals("")) {
				codAgevolazioneApp = record.containsKey("NuoviBeneficiProTrasf")
						? record.get("NuoviBeneficiProTrasf").toString()
						: "";
			}
			if (!avviamentoVeloce) {
				keyTable = record.get("PRGMOVIMENTOAPP") != null ? record.get("PRGMOVIMENTOAPP") : null;
			} else {
				keyTable = record.get("PRGMOVIMENTOAPPCVE") != null ? record.get("PRGMOVIMENTOAPPCVE") : null;
			}
			if (!codAgevolazioneApp.equals("")) {
				String[] vettAgevolazioniMov = codAgevolazioneApp.split(",");
				if (keyTable != null) {
					for (int nAgev = 0; nAgev < vettAgevolazioniMov.length; nAgev++) {
						String codAgevolazCurr = vettAgevolazioniMov[nAgev].trim();
						record.put("CODAGEVOLAZIONEMOV", codAgevolazCurr);
						if (avviamentoVeloce) {
							insertAgevolazione = new InsertData("Inserimento Beneficio Appoggio", trans,
									configproc + "InsertMovimentoAppCVEAgevolazione.xml",
									"INSERT_MOVIMENTO_APP_PER_CVE_AGEVOLAZIONE", user);
						} else {
							insertAgevolazione = new InsertData("Inserimento Beneficio Appoggio", trans,
									configproc + "InsertMovimentoAppAgevolazione.xml",
									"INSERT_MOVIMENTO_APP_AGEVOLAZIONE", user);
						}
						SourceBean resultAgev = insertAgevolazione.processRecord(record);
						if (resultAgev != null && ProcessorsUtils.isError(resultAgev)) {
							// Errore
							return resultAgev;
						}
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InsertAgevolazioniApp", e);
			return ProcessorsUtils.createResponse(name, className, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Importazione benefici in appoggio fallita. ", warnings, nested);
		}
		return null;
	}
}