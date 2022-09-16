package it.eng.sil.module.anag.profiloLavoratore;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.anag.profiloLavoratore.bean.ProfiloLavoratore;

public class PersonalitaModule extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8370393768885784612L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PersonalitaModule.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		// segnalazione 5188 rendere non obbligatorie le risposte sulla personalità
		TransactionQueryExecutor tqe = null;
		DataConnection conn = null;
		try {
			String prgProfiloLavStr = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGLAVORATOREPROFILO");
			BigDecimal prgProfiloLav = new BigDecimal(prgProfiloLavStr);
			ProfiloLavoratore profLav = new ProfiloLavoratore(tqe, prgProfiloLav, null, null, null);
			;
			StringBuffer sb = new StringBuffer("'D43','D44','D46','D47','D48','D49','D50','D51','D52','D53'");
			tqe = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			conn = tqe.getDataConnection();
			Integer numeroRispostePers = profLav.getNumeroRisposteDomande(conn, sb.toString());
			serviceResponse.setAttribute("NUM_PERSONALITA", numeroRispostePers.intValue());
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "PersonalitaModule::service()", ex);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}
}
