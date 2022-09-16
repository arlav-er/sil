/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author Fatale
 *
 */
public class DynamicListaSoggettiAccVoucher implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaSoggettiAccVoucher.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT " + "  e.strcodicefiscale codicefiscale,         "
			+ "  upper(e.codsede)  codsede  ,           " + "  e.strdenominazione  denominazione  ,  "
			+ "  e.strindirizzo  indirizzo , e.codcom codcom, "
			+ "   c.strdenominazione comune   , e.STRNOTESTAMPE STRNOTESTAMPE        " + "  FROM AN_VCH_ENTE E "
			+ "   Left outer join de_comune c        " + "   on e.codcom=c.codcom " + "   WHERE 1=1                  ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug("Sono dentro l'azione della ricerca per lista per soggetti accreditati");

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		SourceBean serviceReq = requestContainer.getServiceRequest();
		SessionContainer session = requestContainer.getSessionContainer();

		String cfSel = "";
		String denominazioneSel = "";
		String codComuneSel = "";
		String tipoRicerca = "";

		String[] inputParam = (String[]) session.getAttribute("tipoRicercaSogAcc");

		if (inputParam != null && inputParam.length > 0) {
			cfSel = inputParam[0];
			denominazioneSel = inputParam[1];
			codComuneSel = inputParam[2];
			tipoRicerca = inputParam[3];
		} else {

			cfSel = (String) serviceReq.getAttribute("cfSel");
			denominazioneSel = (String) serviceReq.getAttribute("denominazioneSel");
			codComuneSel = (String) serviceReq.getAttribute("codComuneSel");
			tipoRicerca = StringUtils.getAttributeStrNotNull(serviceReq, "tipoRicerca");
		}

		if (tipoRicerca != null && tipoRicerca.equals("esatta")) {
			if (!cfSel.equals("")) {
				buf.append(" AND");
				buf.append(" upper(e.strcodicefiscale) = '" + cfSel.toUpperCase() + "'");
			}
			if (!denominazioneSel.equals("")) {
				buf.append(" AND");
				denominazioneSel = StringUtils.replace(denominazioneSel, "'", "''");
				buf.append(" upper( e.strdenominazione) = '" + denominazioneSel.toUpperCase() + "'");
			}

		} else {

			if (!cfSel.equals("")) {
				buf.append(" AND");
				buf.append(" upper(e.strcodicefiscale) like '" + cfSel.toUpperCase() + "%'");
			}

			if (!denominazioneSel.equals("")) {
				buf.append(" AND");
				denominazioneSel = StringUtils.replace(denominazioneSel, "'", "''");
				buf.append(" upper( e.strdenominazione) like '" + denominazioneSel.toUpperCase() + "%'");
			}
		}

		if (codComuneSel != null && !codComuneSel.equals("")) {
			buf.append(" AND   e.codcom  ='" + codComuneSel + "'");
		}

		buf.append(" Order by    e.strcodicefiscale ");

		_logger.debug("Query ottenuta per la lista dettaglio ::: " + buf.toString());

		// Memorizzo in sessione il tipo di ricerca
		String[] inputParameters = new String[4];
		inputParameters[0] = cfSel;
		inputParameters[1] = denominazioneSel;
		inputParameters[2] = codComuneSel;
		inputParameters[3] = tipoRicerca;

		session.setAttribute("tipoRicercaSogAcc", inputParameters);

		return buf.toString();
	}

}
