/*
 * Creato il 5-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author gritti
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ASStorValISEE implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ASStorValISEE.class.getName());
	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = "SELECT " + " AS_VALORE_ISEE.PRGVALOREISEE,"
			+ " AS_VALORE_ISEE.CDNLAVORATORE ," + "	AN_LAVORATORE.STRNOME NOME," + " AN_LAVORATORE.STRCOGNOME COGNOME,"
			+ " AN_LAVORATORE.STRCODICEFISCALE CF," + " to_char(AS_VALORE_ISEE.DTMINS,'DD/MM/YYYY') DTMINS,"
			+ " to_char(AS_VALORE_ISEE.DTMMOD,'DD/MM/YYYY') DTMMOD," + " AS_VALORE_ISEE.CDNUTINS,"
			+ " AS_VALORE_ISEE.CDNUTMOD," + " TO_CHAR(DATINIZIOVAL,'dd/mm/yyyy') DATINIZIOVAL,"
			+ "	TO_CHAR(DATFINEVAL,'dd/mm/yyyy')DATFINEVAL," + " NUMVALOREISEE," + " NUMANNO," + " STRNOTA,"
			+ " NUMPUNTIISEE" + " FROM AS_VALORE_ISEE,AN_LAVORATORE";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// al momento la query on necessità dinamicità perciò cablo nella
		// stringa la clausola WHERE
		/*
		 * if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) { if (buf.length() == 0) { buf.append("WHERE"); }
		 * else { buf.append(" AND"); } buf.append( " upper(as_valore_isee.cdnlavoratore) = '" +
		 * cdnLavoratore.toUpperCase() + "'"); }
		 */

		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		buf.append(" WHERE upper(as_valore_isee.cdnlavoratore) = " + cdnLavoratore);
		buf.append(" AND DATFINEVAL IS NOT NULL ");
		buf.append(" AND DATFINEVAL <= TRUNC(SYSDATE) AND DATFINEVAL IS NOT NULL ");
		buf.append(" AND AS_VALORE_ISEE.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE ");
		buf.append(" ORDER BY AS_VALORE_ISEE.PRGVALOREISEE DESC");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}