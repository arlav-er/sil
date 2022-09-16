package it.eng.sil.module.delega;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class SPListaDeleghePerLavoratoreSelezionato implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		String cdnlavoratore = (String) requestContainer.getServiceRequest().getAttribute("cdnlavoratore");

		String statement = " SELECT DELEGA.PRGDELEGA as IDDELEGA,  "
				+ "        case when DELEGA.DATFINEVAL IS NULL then 1 else 0 end AS ISDATAFINENULL, "
				+ " TO_CHAR (DELEGA.DATINIZIOVAL, 'DD/MM/YYYY')  as DATAACQUISIZIONEDELEGA, "
				+ " TO_CHAR (DELEGA.DATFINEVAL, 'DD/MM/YYYY')  as DATAFINEDELEGA, "
				+ " InitCap(ut.STRCOGNOME) || ' ' || InitCap(ut.STRNOME)  as INSERIMENTO " + " FROM AM_DELEGA DELEGA"
				+ " INNER JOIN AN_LAVORATORE lav on lav.CDNLAVORATORE = DELEGA.CDNLAVORATORE "
				+ " inner join TS_UTENTE ut on (ut.CDNUT = DELEGA.CDNUTINS) " + " WHERE lav.CDNLAVORATORE = "
				+ cdnlavoratore;

		return statement;

	}
}
