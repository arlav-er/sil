package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/*
 * Questa classe restituisce la query per la ricerca di un'azienda
 * quando il lavoratore si trova in mobilità
 * 
 * @author: Stefania Orioli
 * 
 * @author: modificata da Davide Giuliani
 */

public class InfStorPermSogg implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfStorPermSogg.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRCODICEFISCALE CF,"
			+ "        EX.PRGPERMSOGG, " + "        EX.CDNLAVORATORE, "
			+ "        to_char(EX.datRichiesta,'dd/mm/yyyy') datRichiesta, "
			+ "        to_char(EX.datScadenza ,'dd/mm/yyyy') datScadenza, "
			+ "        to_char(EX.datFine,'dd/mm/yyyy') datFine, " + "        EX.datScadenza datasort, "
			+ "		 de_status_straniero.strdescrizione tipologia, " + "        EX.CODMOTIVORIL, "
			+ "        DE_EX_MOTIVO_RIL.STRDESCRIZIONE AS DESCRIZIONEMOT, " + "        EX.codstatorichiesta, "
			+ "        DE_STATO_ATTO.STRDESCRIZIONE AS DESCRIZIONERICH "
			+ "   FROM AM_EX_PERM_SOGG EX,DE_EX_MOTIVO_RIL,DE_STATO_ATTO, AN_LAVORATORE, DE_STATUS_STRANIERO "
			+ "  WHERE EX.CODMOTIVORIL = DE_EX_MOTIVO_RIL.CODMOTIVORIL(+) AND "
			+ "        EX.CODSTATUS = DE_STATUS_STRANIERO.CODSTATUS(+) AND "
			+ "        EX.CODSTATORICHIESTA = DE_STATO_ATTO.CODSTATOATTO(+) AND "
			+ "        EX.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE AND " + " (((ex.CODSTATUS in ('1', '3') AND "
			+ " trunc(nvl(ex.DATSCADENZA,to_date('31/12/2100','dd/mm/yyyy'))) < trunc(sysdate)) OR "
			+ " ex.datFine is not null) OR "
			+ " (( (ex.CODSTATUS in ('2','5')) AND trunc(nvl(ex.DATSCADENZA,to_date('31/12/2100','dd/mm/yyyy'))) <= trunc(sysdate)) OR "
			+ "         (ex.datFine is not null))) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		query_totale.append(" AND EX.CDNLAVORATORE = ");
		query_totale.append(cdnLavoratore);
		query_totale.append(" ORDER BY EX.datScadenza DESC");
		// Debug
		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}// class InfStorPermSogg