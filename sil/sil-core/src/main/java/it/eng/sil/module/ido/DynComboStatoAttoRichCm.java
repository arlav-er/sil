/*
 * Creato il 17-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynComboStatoAttoRichCm implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynComboStatoAttoRichCm.class.getName());

	private String className = StringUtils.getClassName(DynComboStatoAttoRichCm.class);

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug(className + ".getStatement() INIZIO");

		String codStatoAtto = null, dataOraProt = null;
		SourceBean req = requestContainer.getServiceRequest();
		String page = (String) req.getAttribute("PAGE");

		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(" distinct sa.CODSTATOATTO AS CODICE "); // la distinct
																// deve esserci
		dsu.addSelect(" sa.STRDESCRIZIONE AS DESCRIZIONE ");
		dsu.addFrom(" DE_STATO_ATTO sa ");
		dsu.addFrom(" DE_STATO_ATTO_LST_TAB  sat ");
		dsu.addWhere(" sa.CODSTATOATTO = sat.CODSTATOATTO ");
		dsu.addWhere(" sat.CODLSTTAB = 'CM_RI_GR' ");
		if (page.equalsIgnoreCase("CMRichGradPage")) {
			boolean nuovo = req.getAttribute("nuovo").equals("true");
			boolean inAttesaPr = req.getAttribute("CODSTATOATTO_P").equals("PA");
			if (nuovo) {
				dsu.addWhere(" nvl(sa.CODMONOPRIMADOPOINS, '1') in ( 'E', 'P' )");
			}
			if (!nuovo) {
				if (!inAttesaPr) {
					dsu.addWhere(" nvl(sa.CODMONOPRIMADOPOINS, '1') not in ( 'P' )");
				}
			}
		}
		dsu.addOrder("DESCRIZIONE");
		String query = dsu.getStatement();
		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}

}