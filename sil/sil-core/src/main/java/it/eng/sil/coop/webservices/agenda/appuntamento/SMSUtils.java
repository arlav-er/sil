package it.eng.sil.coop.webservices.agenda.appuntamento;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

public class SMSUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SMSUtils.class.getName());

	public static String getCodTipoSms(String codiceRichiesta, String codProvincia) {

		String codTipoSms = null;

		try {

			Object[] args = new Object[2];
			args[0] = codiceRichiesta;
			args[1] = codProvincia;
			SourceBean sourceBean = (SourceBean) QueryExecutor.executeQuery("SELECT_CODTIPOSMS_APPUNTAMENTO_ONLINE",
					args, TransactionQueryExecutor.SELECT, Values.DB_SIL_DATI);

			if (sourceBean != null && sourceBean.containsAttribute("ROW")) {
				codTipoSms = SourceBeanUtils.getAttrStr(sourceBean, "ROW.STRVALORE");
			}

		} catch (Exception e) {
			_logger.error("Errore recupero codTipoSms", e);
		}

		return codTipoSms;

	}

}
