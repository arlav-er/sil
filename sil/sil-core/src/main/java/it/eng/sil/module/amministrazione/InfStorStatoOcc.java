package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class InfStorStatoOcc implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfStorStatoOcc.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT AN_LAVORATORE.STRNOME NOME,"
			+ "       AN_LAVORATORE.STRCOGNOME COGNOME," + "       AN_LAVORATORE.STRCODICEFISCALE CF,"
			+ "       AM_STATO_OCCUPAZ.PRGSTATOOCCUPAZ," + "       AM_STATO_OCCUPAZ.CDNLAVORATORE,"
			+ "       AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ,"
			+ "		  pg_utils.trunc_desc(DE_STATO_OCCUPAZ.STRDESCRIZIONE, 60, '...') AS DESCRIZIONESTATO,"
			+ "       to_char(AM_STATO_OCCUPAZ.DATINIZIO,'DD/MM/YYYY') DATINIZIO,"
			+ "       AM_STATO_OCCUPAZ.DATINIZIO datasort, "
			+ "       to_char(AM_STATO_OCCUPAZ.DATFINE  ,'DD/MM/YYYY') DATFINE,"
			+ "       AM_STATO_OCCUPAZ.FLGINDENNIZZATO," + "       AM_STATO_OCCUPAZ.FLGPENSIONATO,"
			+ "       AM_STATO_OCCUPAZ.NUMMESISOSP,"
			+ "       to_char(AM_STATO_OCCUPAZ.DATANZIANITADISOC,'DD/MM/YYYY') DATANZIANITADISOC,"
			+ "       AM_STATO_OCCUPAZ.STRNOTE," + "       to_char(AM_STATO_OCCUPAZ.DTMINS,'DD/MM/YYYY') DTMINS,"
			+ "       to_char(AM_STATO_OCCUPAZ.DTMMOD,'DD/MM/YYYY') DTMMOD," + "       AM_STATO_OCCUPAZ.CDNUTINS,"
			+ "       AM_STATO_OCCUPAZ.CDNUTMOD," + "       AM_STATO_OCCUPAZ.NUMREDDITO,"
			+ "       AM_STATO_OCCUPAZ.STRNUMATTO,  " + "       to_char(AM_STATO_OCCUPAZ.DATATTO,'DD/MM/YYYY') DATATTO,"
			+ "       AM_STATO_OCCUPAZ.CODSTATOATTO," + "       DE_STATO_ATTO.STRDESCRIZIONE AS DESCRIZIONEATTO,"
			+ "       AM_STATO_OCCUPAZ.PRGSTATOOCCUPAZPREC," + "       AM_STATO_OCCUPAZ.NUMKLOSTATOOCCUPAZ,"
			+ "       DECODE( AM_STATO_OCCUPAZ.DATRICORSOGIURISDIZ, NULL,"
			+ "               DECODE(AM_STATO_OCCUPAZ.DATRICHREVISIONE, NULL, NULL,"
			+ "                      'Rev.' || to_char(AM_STATO_OCCUPAZ.DATRICHREVISIONE,'DD/MM/YYYY')"
			+ "                     ),"
			+ "               'Ric.' || to_char(AM_STATO_OCCUPAZ.DATRICORSOGIURISDIZ,'DD/MM/YYYY')"
			+ "             ) REV_RIC,"
			+ "       (TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)) - NVL(AM_STATO_OCCUPAZ.NUMMESISOSP,0)) MESI_ANZ,"
			+ " DECODE(AM_STATO_OCCUPAZ.codmonoprovenienza,'A','Da reg. anag.','D','Da D.I.D.','M','Da movimenti','B','Da mobilita','T','Da trasferimento comp.','P','Da Porting','N','Reg. manuale','G','Agg. manuale','O','Reg./Agg. manuale') Provenienza"
			+ " FROM  AM_STATO_OCCUPAZ, DE_STATO_OCCUPAZ, DE_STATO_OCCUPAZ_RAGG,"
			+ "        DE_STATO_ATTO, AN_LAVORATORE " + " WHERE AM_STATO_OCCUPAZ.DATFINE IS NOT NULL"
			+ "   AND AM_STATO_OCCUPAZ.CODSTATOATTO        = DE_STATO_ATTO.CODSTATOATTO(+)"
			+ "   AND AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ     = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ(+)"
			+ "   AND DE_STATO_OCCUPAZ.CODSTATOOCCUPAZRAGG = DE_STATO_OCCUPAZ_RAGG.CODSTATOOCCUPAZRAGG"
			+ "   AND AM_STATO_OCCUPAZ.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		query_totale.append(" AND AM_STATO_OCCUPAZ.CDNLAVORATORE = ");
		query_totale.append(cdnLavoratore);

		query_totale.append(" AND AM_STATO_OCCUPAZ.PRGSTATOOCCUPAZ in (");
		query_totale.append(" select max(AM_STATO_OCCUPAZ.PRGSTATOOCCUPAZ) ");
		query_totale.append("	from am_stato_occupaz ");
		query_totale.append(" where AM_STATO_OCCUPAZ.CDNLAVORATORE = " + cdnLavoratore);
		query_totale.append(" group by AM_STATO_OCCUPAZ.DATINIZIO )");

		query_totale.append(
				" ORDER BY AM_STATO_OCCUPAZ.DATINIZIO DESC, AM_STATO_OCCUPAZ.DATFINE DESC, AM_STATO_OCCUPAZ.PRGSTATOOCCUPAZ DESC");
		// Debug
		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}// class InfStorStatoOcc
