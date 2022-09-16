/**
 * 
 */
package it.eng.sil.module.voucher;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author Fatale
 *
 */
public class DynamicRicListaVoucher implements IDynamicStatementProvider {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicRicListaVoucher.class.getName());

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug("Sono dentro l'azione della ricerca per lista");

		String statement = SQLStatements.getStatement("GET_LISTA_CRUSCOTTO_VOUCHER");
		// String statement = SQLStatements.getStatement("GET_LISTA_CRUSCOTTO_VOUCHER_TEST");
		StringBuffer buf = new StringBuffer(statement);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		String codiceFiscaleVoucher = (String) serviceReq.getAttribute("cf");
		String cognomeVoucher = (String) serviceReq.getAttribute("cognomeSel");
		String nomeVoucher = (String) serviceReq.getAttribute("nomeSel");
		String idAzione = (String) serviceReq.getAttribute("PRGAZIONE");
		String idStato = (String) serviceReq.getAttribute("PRGSTATO");

		_logger.debug("Il valore di codiceFiscale è:::" + codiceFiscaleVoucher);
		_logger.debug("Il valore di cognome è:::" + cognomeVoucher);
		_logger.debug("Il valore di nome è:::" + nomeVoucher);

		if (codiceFiscaleVoucher != null && !codiceFiscaleVoucher.isEmpty()) {
			buf.append(" AND AN_LAVORATORE.STRCODICEFISCALE='" + codiceFiscaleVoucher + "'");
		}

		if (cognomeVoucher != null && !cognomeVoucher.isEmpty()) {
			buf.append(" AND AN_LAVORATORE.strcognome like '%" + cognomeVoucher + "%'");
		}

		if (nomeVoucher != null && !nomeVoucher.isEmpty()) {
			buf.append(" AND AN_LAVORATORE.strnome like '%" + nomeVoucher + "%'");
		}
		if (idStato != null && !"".equals(idStato)) {
			buf.append(" AND vou.codstatovoucher ='" + idStato + "'");
		}
		if (idAzione != null && !"".equals(idAzione)) {
			buf.append(" AND per.prgazioni =" + idAzione);
		}

		return buf.toString();
	}

}
