package it.eng.sil.module.coop;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class DynamicStatementRicercaCPI implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicStatementRicercaCPI.class.getName());

	/*
	 * <STATEMENT name="SELECT_CERCA_CPI_CODICE_APPROSSIMATA" query="SELECT c.codcpi, c.strdescrizione FROM de_cpi c
	 * WHERE c.codcpi like ? || '%' and SUBSTR(C.CODCPI, LENGTH(C.CODCPI), 1) &gt;= '0' AND SUBSTR(C.CODCPI,
	 * LENGTH(C.CODCPI), 1) &lt;= '9' and SYSDATE &gt;= c.datinizioval and SYSDATE &lt;= c.datfineval ORDER BY
	 * strdescrizione "/>
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		String codCom = Utils.notNull(requestContainer.getServiceRequest().getAttribute("codcomdom"));
		// Ricerco i comuni (il codice NON deve iniziare per 'Z')
		StringBuffer query = new StringBuffer();
		query.append("SELECT nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
				+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia ");

		if (codCom.length() == 4) {
			// Il codice comune ha 4 cifre. Quindi eseguo una ricerca puntuale
			// (corrispondenza 1-1 codice-comune)
			query.append(" WHERE c.codcom = '").append(codCom.toUpperCase()).append("' ");
		} else if (codCom.length() < 4) {
			// Eseguo una ricerca generica
			query.append(" WHERE c.codcom like '").append(codCom.toUpperCase()).append("%'  ");
		} else {
			_logger.debug("Impossibile trovare un cpi per il codCom " + codCom);

			return "";
		}
		query.append(" AND substr(codcom,1,1) <> 'Z'");
		query.append(" AND c.codcom <> 'NT'");
		query.append(" ORDER BY codcom");
		return query.toString();
	}
}