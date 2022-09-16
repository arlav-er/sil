/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

/**
 * @author Fatale
 *
 */
public class DynComboAltriCpu implements IDynamicStatementProvider2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2#getStatement(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */

	public String getStatement(SourceBean request, SourceBean response) {

		String codcpi = (String) request.getAttribute("codiceCPISel");

		String newQuery = "select distinct de_cpi.codcpi as codice,  de_cpi.strDescrizione || ' - ' || de_cpi.codcpi as descrizione  from de_cpi, de_comune, de_provincia ";
		newQuery = newQuery
				+ "  where de_cpi.codcpi = de_comune.codcpi   and de_comune.codprovincia = de_provincia.codprovincia                                                                                                           ";
		newQuery = newQuery + "and de_cpi.codcpi!='" + codcpi + "'";
		newQuery = newQuery
				+ "  and de_provincia.codregione = (select de_provincia.CODREGIONE from de_provincia inner join ts_generale on (de_provincia.codprovincia = ts_generale.CODPROVINCIASIL))  order by descrizione                ";

		StringBuffer query_totale = new StringBuffer(newQuery);

		return query_totale.toString();
	}

}
