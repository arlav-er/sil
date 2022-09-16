package it.eng.sil.module.admin;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class ListaUtentiSistema implements IDynamicStatementProvider {

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		return " SELECT strlogin as login, " + " strcognome as cognome, strnome as nome, "
				+ " stremail as email, flgabilitato as abilitato " + " FROM ts_utente";
	}

}
