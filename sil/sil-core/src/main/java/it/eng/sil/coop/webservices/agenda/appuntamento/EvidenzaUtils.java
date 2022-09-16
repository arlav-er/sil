package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

public class EvidenzaUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EvidenzaUtils.class.getName());

	public static boolean insertEvidenza(BigDecimal cdnLavoratore, int numConfigurazione, String strEvidenza,
			String codiceRichiesta) {

		try {
			String codTipoEvidenza = recuperaTipoEvidenzaConfigurazione(codiceRichiesta, numConfigurazione);
			if (codTipoEvidenza != null && !codTipoEvidenza.equals("")) {
				BigDecimal prgEvidenza = recuperaProgressivoEvidenza(codTipoEvidenza);
				if (prgEvidenza != null) {
					Object params[] = new Object[3];
					params[0] = cdnLavoratore;
					params[1] = strEvidenza;
					params[2] = prgEvidenza;

					Boolean res = (Boolean) QueryExecutor.executeQuery("INSERT_EVIDENZA_APPUNTAMENTO_ONLINE", params,
							"INSERT", Values.DB_SIL_DATI);
					if (!res.booleanValue()) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static String recuperaTipoEvidenzaConfigurazione(String codiceRichiesta, int configurazione)
			throws Exception {
		Object params[] = new Object[2];
		params[0] = codiceRichiesta;
		params[1] = configurazione;
		String ret = null;

		SourceBean res = (SourceBean) QueryExecutor.executeQuery("LOAD_TIPO_EVIDENZA_DA_CONFIGURAZIONE", params,
				"SELECT", Values.DB_SIL_DATI);
		if (res != null) {
			res = res.containsAttribute("ROW") ? (SourceBean) res.getAttribute("ROW") : res;
			ret = res.containsAttribute("strvalore") ? res.getAttribute("strvalore").toString() : null;
		}

		return ret;
	}

	public static BigDecimal recuperaProgressivoEvidenza(String codTipoEvidenza) throws Exception {
		Object params[] = new Object[1];
		params[0] = codTipoEvidenza;
		BigDecimal ret = null;

		SourceBean res = (SourceBean) QueryExecutor.executeQuery("LOAD_PROGRESSIVO_TIPO_EVIDENZA", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null) {
			res = res.containsAttribute("ROW") ? (SourceBean) res.getAttribute("ROW") : res;
			ret = (BigDecimal) res.getAttribute("prgtipoevidenza");
		}

		return ret;
	}

}
