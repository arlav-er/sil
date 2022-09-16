package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/*
 * @author: Paolo Cavaciocchi
 * modifiche: Stefania Orioli
 */

public class DynamicListaAgenda implements IDynamicStatementProvider {
	public DynamicListaAgenda() {
	}

	private static final String SELECT_SQL_BASE = "SELECT CODCPI, PRGAPPUNTAMENTO, to_char(DTMDATAORA,'dd/mm/yyyy') as DTMDATAORA, NUMMINUTI FROM AG_AGENDA WHERE 1=1 ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codcpi = (String) req.getAttribute("CODCPI");
		String prgappuntamento = (String) req.getAttribute("PRGAPPUNTAMENTO");
		String dtmdataora = (String) req.getAttribute("DTMDATAORA");
		String codservizio = (String) req.getAttribute("CODSERVIZIO");

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		if ((codcpi != null) && (!codcpi.equals(""))) {
			buf.append(" AND upper(CODCPI) = upper('" + codcpi + "')");
		}

		if ((prgappuntamento != null) && (!prgappuntamento.equals(""))) {
			buf.append(" AND PRGAPPUNTAMENTO = " + prgappuntamento);
		}

		if ((dtmdataora != null) && (!dtmdataora.equals(""))) {
			buf.append(" AND to_char(DTMDATAORA,'dd-mm-yyyy,hh24:mi:ss') = '" + dtmdataora + "')");
		}

		if ((codservizio != null) && (!codservizio.equals(""))) {
			buf.append(" AND upper(CODSERVIZIO) = upper('" + codservizio + "')");
		}

		return buf.toString();
	}

}