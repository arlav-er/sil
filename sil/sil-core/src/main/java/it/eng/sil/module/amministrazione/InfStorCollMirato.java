package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class InfStorCollMirato implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfStorCollMirato.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRCODICEFISCALE CF,"
			+ "        I.PRGCMISCR, " + "        I.CDNLAVORATORE, " + "        I.CODCMTIPOISCR, "
			+ "        DE_CM_TIPO_ISCR.STRDESCRIZIONE AS DESCRIZIONEISCR, "
			+ "        to_char(I.DATDATAINIZIO,'DD/MM/YYYY') DATINIZIO, " + "        I.DATDATAINIZIO datainiziosort, "
			+ "        to_char(I.DATDATAFINE,'DD/MM/YYYY') DATFINE, " + "        I.CODTIPOINVALIDITA, "
			+ "        DE_CM_TIPO_INVALIDITA.STRDESCRIZIONE AS DESCRIZIONEINV, " + "        I.NUMPERCINVALIDITA, "
			+ "        I.STRNOTE, " + "        to_char(I.DTMINS,'DD/MM/YYYY') DTMINS, "
			+ "        to_char(I.DTMMOD,'DD/MM/YYYY') DTMMOD, " + "        I.CDNUTINS, " + "        I.CDNUTMOD, "
			+ "        I.NUMKLOCMISCR " + "  FROM AM_CM_ISCR I, DE_CM_TIPO_ISCR, DE_CM_TIPO_INVALIDITA, AN_LAVORATORE "
			+ " WHERE I.CODCMTIPOISCR     = DE_CM_TIPO_ISCR.CODCMTIPOISCR(+) "
			+ "   AND I.CODTIPOINVALIDITA = DE_CM_TIPO_INVALIDITA.CODTIPOINVALIDITA(+) "
			+ "   AND nvl(I.DATDATAFINE, sysdate ) <  sysdate ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		query_totale.append(" AND DECRYPT(I.CDNLAVORATORE, '" + encryptKey + "') = AN_LAVORATORE.CDNLAVORATORE ");

		query_totale.append(" AND I.CDNLAVORATORE = ");
		query_totale.append(cdnLavoratore);
		query_totale.append(" ORDER BY I.DATDATAINIZIO DESC");
		// Debug
		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}// class InfStorCollMirato
