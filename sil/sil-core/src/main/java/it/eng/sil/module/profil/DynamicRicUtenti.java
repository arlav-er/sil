package it.eng.sil.module.profil;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicRicUtenti implements IDynamicStatementProvider {
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String statement = SQLStatements.getStatement("PROF_LISTA_UTENTI");
		StringBuffer buf = new StringBuffer(statement);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		String cdnTipoOrg = (String) serviceReq.getAttribute("ComboTipoOrg");
		String cdnOrg = (String) serviceReq.getAttribute("ComboOrg");
		String cdnRuolo = (String) serviceReq.getAttribute("ComboRuolo");
		// String flagPergona = (String) serviceReq.getAttribute("FlagPergona");
		String nome = (String) serviceReq.getAttribute("nome");
		String cognome = (String) serviceReq.getAttribute("cognome");
		String ragSoc = (String) serviceReq.getAttribute("rag_soc");
		String valAccount = (String) serviceReq.getAttribute("valAccount");
		String flagAbilitato = (String) serviceReq.getAttribute("FlagAbilitato");
		String flagUtConvenzione = (String) serviceReq.getAttribute("FlagUtConvenzione");
		String strLogin = (String) serviceReq.getAttribute("strLogin");

		if (!cdnTipoOrg.equals("")) {
			buf.append(" and pu.cdngruppo in ");
			buf.append("(select cdngruppo from ts_gruppo where Cdntipogruppo=" + cdnTipoOrg + ")");
		}

		if (!cdnOrg.equals("")) {
			buf.append(" and PU.Cdngruppo=" + cdnOrg);
		}

		if (!cdnRuolo.equals("")) {
			buf.append(" and PU.CDNPROFILO=" + cdnRuolo);
		}

		if (!nome.equals("")) {
			buf.append(" and UPPER(U.strNOME) like '%" + nome.toUpperCase() + "%'");
		}

		if (!cognome.equals("")) {
			buf.append(" and UPPER(U.strcognome) like '%" + cognome.toUpperCase() + "%'");
		}

		if (!flagAbilitato.equals("")) {
			buf.append(" and UPPER(U.Flgabilitato) = '" + flagAbilitato.toUpperCase() + "'");
		}

		if (!flagUtConvenzione.equals("")) {
			buf.append(" and UPPER(U.FlgUtConvenzione) = '" + flagUtConvenzione.toUpperCase() + "'");
		}

		if (valAccount.equals("V")) {
			buf.append("  and sysdate between u.datinizioval and u.datfineval");
		}

		if (valAccount.equals("NAV")) {
			buf.append("  and sysdate < u.datinizioval");
		}

		if (valAccount.equals("S")) {
			buf.append("  and sysdate > u.datfineval");
		}

		if (!strLogin.equals("")) {
			buf.append(" and upper(U.strlogin) like '" + strLogin.toUpperCase() + "%'");
		}

		buf.append(" order by login");

		return buf.toString();
	}
}
