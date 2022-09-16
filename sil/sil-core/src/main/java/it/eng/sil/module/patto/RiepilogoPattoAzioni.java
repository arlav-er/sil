package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class RiepilogoPattoAzioni implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RiepilogoPattoAzioni.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT " + "TO_CHAR(PT.DATSTIPULA,'DD/MM/YYYY') DATSTIPULA, "
			+ "TO_CHAR(PT.DATFINE,'DD/MM/YYYY') DATFINE, " + "TO_CHAR(PT.DATSCADCONFERMA,'DD/MM/YYYY') DATSCADENZA, "
			+ "TO_CHAR(PT.DATADESIONEPA,'DD/MM/YYYY') DATADESIONEPA, TIPO.STRDESCRIZIONE MISURE, "
			+ "DE_CODIFICA_PATTO.STRDESCRIZIONE TIPOLOGIA, " + "getAzioniPatto(PT.PRGPATTOLAVORATORE) AZIONI "
			+ "FROM AM_PATTO_LAVORATORE PT " + "INNER JOIN DE_TIPO_PATTO TIPO ON (PT.CODTIPOPATTO = TIPO.CODTIPOPATTO) "
			+ "INNER JOIN DE_CODIFICA_PATTO ON (PT.CODCODIFICAPATTO = DE_CODIFICA_PATTO.CODCODIFICAPATTO) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		query_totale.append(" WHERE PT.CODSTATOATTO = 'PR'");

		if (cdnLavoratore != null) {
			query_totale.append(" AND PT.CDNLAVORATORE = " + cdnLavoratore);
		}

		query_totale.append(" ORDER BY PT.DATSTIPULA DESC, PT.DTMINS DESC");

		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}
