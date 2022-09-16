package it.eng.sil.module.cig;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Data : 24/02/2010 Programmatore : Alessandro Donisi Analista : Luca Cicale Motivo : La classe fa il Controllo dei
 * corsi Catalogo attivi. Un corso attivo non ha DataCanc e n ha datafine o ( datafine > data attuale ) Input :
 * cdnlavoratore, il limite di corsi della provincia. Output : Se il limite della provincia è: = 0 -> Non si fa
 * inserimenti. Return CONTROLCORSO = KP --> Nessuno corso disponibile. > 0 -> il valore è il limite. Return Ok quando
 * Limite < Numero di corsi disponibili oppure CONTROLCORSO = KL --> Il lavoratore ha raggiunto il numero massimo di
 * corsi. < 0 -> nessun limite. Return CONTROLCORSO = OK
 * 
 */

public class ControlCorsiCig extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlCorsiCig.class.getName());

	private final String className = StringUtils.getClassName(this);
	BigDecimal userid;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		String cdnlavoratore = StringUtils.getAttributeStrNotNull(request, "cdnlavoratore");

		DataConnectionManager dataConnectioManager = DataConnectionManager.getInstance();
		DataConnection dc = dataConnectioManager.getConnection();

		try {

			Object[] inputParameter = new Object[1];
			inputParameter[0] = cdnlavoratore;

			// Prendo il n di corsi attivi del lavoratore
			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("S_ControlCorsiCig", inputParameter,
					"SELECT", "SIL_DATI");

			Vector corsi = rowsSourceBean.getContainedAttributes();

			int qtdecorsi;
			if (corsi.isEmpty()) {
				qtdecorsi = 0;
			} else
				qtdecorsi = corsi.size();

			// Prendo il limite di corsi della provincia

			SourceBean rowsSourceBean2 = (SourceBean) QueryExecutor.executeQuery("S_Limite_Iscrizione", null, "SELECT",
					"SIL_DATI");
			String slimitecorsi = (String) rowsSourceBean2.getAttribute("ROW.limitecorso");
			int limitecorsi = -1;
			if (slimitecorsi != null) {
				limitecorsi = Integer.parseInt(slimitecorsi);
			}

			if (limitecorsi < 0 || qtdecorsi < limitecorsi) {
				response.setAttribute("CONTROLCORSO", "OK");
			} else if (limitecorsi == 0) {
				response.setAttribute("CONTROLCORSO", "KP");
			} else
				response.setAttribute("CONTROLCORSO", "KL");

		} catch (Exception e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", e);
			_logger.error(className + "::service()", e);

		}

		Utils.releaseResources(dc, null, null);

	}

}
