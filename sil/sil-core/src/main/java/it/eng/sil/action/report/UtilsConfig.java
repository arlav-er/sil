package it.eng.sil.action.report;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

public class UtilsConfig {
	private static final String Default = "0";
	private static final String Custom = "1";
	private String tipoConfig = null;
	public static final String REGIONE_RER = "8";
	public static final String REGIONE_UMBRIA = "10";

	public UtilsConfig(String configurazione) {
		this.tipoConfig = configurazione;
	}

	/**
	 * 
	 * @return "NT" nel caso in cui per la provincia è prevista la configurazione di default "codiceProvinciaSil" se per
	 *         la provincia è prevista la configurazione custom
	 */
	public String getConfigurazione() {
		String codiceRet = "NT";
		try {
			String stmName = "ST_GETCONFIGURAZIONE_PROVINCIA";
			Object[] params = new Object[1];
			params[0] = this.tipoConfig;
			SourceBean result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
			if (result != null) {
				String numConfig = result.getAttribute("ROW.NUM") != null ? result.getAttribute("ROW.NUM").toString()
						: Default;
				if (numConfig.equals(Custom)) {
					codiceRet = result.getAttribute("ROW.CODPROVINCIASIL").toString();
				}
			}
		} catch (Exception e) {
			return "NT";
		}
		return codiceRet;
	}

	/**
	 * 
	 * @return "0" configurazione default "1" configurazione custom
	 */
	public String getConfigurazioneDefault_Custom() {
		String tipoConfig = Default;
		try {
			String stmName = "ST_GETCONFIGURAZIONE_PROVINCIA";
			Object[] params = new Object[1];
			params[0] = this.tipoConfig;
			SourceBean result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
			if (result != null) {
				tipoConfig = result.getAttribute("ROW.NUM") != null ? result.getAttribute("ROW.NUM").toString()
						: Default;
			}
		} catch (Exception e) {
			return tipoConfig;
		}
		return tipoConfig;
	}

	public String getValoreConfigurazione() {
		String valore = "";
		try {
			String stmName = "ST_GETCONFIGURAZIONE_PROVINCIA";
			Object[] params = new Object[1];
			params[0] = this.tipoConfig;
			SourceBean result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
			if (result != null) {
				valore = result.getAttribute("ROW.STRVALORE") != null ? result.getAttribute("ROW.STRVALORE").toString()
						: "";
			}
		} catch (Exception e) {
			return valore;
		}
		return valore;
	}

	public String toString() {
		return "Configurazione modello :" + this.tipoConfig;
	}

}
