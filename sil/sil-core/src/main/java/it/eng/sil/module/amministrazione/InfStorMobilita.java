package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class InfStorMobilita implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfStorMobilita.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT DISTINCT AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRCODICEFISCALE CF,"
			+ "        AM_MOBILITA_ISCR.CDNLAVORATORE," + "		   STATO_RICH.STRDESCRIZIONE stato," + " case "
			+ " when ma.codlistespec is not null "
			+ " then (DE_MB_TIPO.STRDESCRIZIONE || ' (Min: ' || mn.des_listespec|| ')' ) "
			+ " else DE_MB_TIPO.STRDESCRIZIONE || ' (Min: nessuna corrispondenza)' " + " end as  DESCRIZIONE, "
			+ "        to_char(AM_MOBILITA_ISCR.DATINIZIO,'DD/MM/YYYY') DATINIZIO, "
			+ "        AM_MOBILITA_ISCR.DATINIZIO as datainiziosort, "
			+ "        to_char(AM_MOBILITA_ISCR.DATFINE,'DD/MM/YYYY')   DATFINE, "
			+ "        to_char(AM_MOBILITA_ISCR.DATATTO,'DD/MM/YYYY')   DATATTO, "
			+ "        to_char(AM_MOBILITA_ISCR.DATFINEINDENNITA,'DD/MM/YYYY')   DATFINEINDENNITA, "
			+ "        to_char(AM_MOBILITA_ISCR.DATINIZIOINDENNITA,'DD/MM/YYYY') DATINIZIOINDENNITA, "
			+ "        to_char(AM_MOBILITA_ISCR.DATMAXDIFF,'DD/MM/YYYY') DATMAXDIFF, "
			+ "        AM_MOBILITA_ISCR.PRGMOBILITAISCR, " + "        AM_MOBILITA_ISCR.PRGMOVIMENTO, "
			+ "        to_char(AM_MOBILITA_ISCR.DATDOMANDA, 'DD/MM/YYYY' ) DATDOMANDA, to_number(AM_MOBILITA_ISCR.DECORESETT) NUMORESETT, "
			+ "        AM_MOBILITA_ISCR.STRNUMATTO, "
			+ "        case when AM_MOBILITA_ISCR.DATINIZIOMOV is not null then "
			+ "        to_char(AM_MOBILITA_ISCR.DATINIZIOMOV,'DD/MM/YYYY') ||' - '||"
			+ "	    to_char(AM_MOBILITA_ISCR.DATFINEMOV,'DD/MM/YYYY') "
			+ "        else (decode(AM_MOVIMENTO.CODTIPOMOV, 'AVV', to_char(AM_MOVIMENTO.DATINIZIOMOV,'DD/MM/YYYY'), to_char(AM_MOVIMENTO.DATINIZIOAVV,'DD/MM/YYYY')) ||' - '||"
			+ "              decode(AM_MOVIMENTO.CODTIPOMOV, 'AVV', to_char(AM_MOVIMENTO.DATFINEMOVEFFETTIVA,'DD/MM/YYYY'), to_char(AM_MOVIMENTO.DATINIZIOMOV,'DD/MM/YYYY')))  "
			+ "        end as PERIODOLAV, " + "        case when AM_MOBILITA_ISCR.CODMANSIONE is not null then "
			+ "        DE_MANSIONE.STRDESCRIZIONE  " + "        else MANSIONE1.STRDESCRIZIONE "
			+ "        end as MANSIONE, " + "        case when AM_MOBILITA_ISCR.PRGAZIENDA is not null then "
			+ "        AN_AZIENDA.STRRAGIONESOCIALE " + "        else AZ1.STRRAGIONESOCIALE "
			+ "        end as STRRAGIONESOCIALE "
			+ "   FROM AM_MOBILITA_ISCR, DE_MB_TIPO, AM_MOVIMENTO, AN_AZIENDA, AN_UNITA_AZIENDA, "
			+ "        AN_AZIENDA AZ1, AN_UNITA_AZIENDA UAZ1, "
			+ "        ma_listespeciali ma, mn_listespeciali mn, DE_MB_STATO_RICH STATO_RICH, "
			+ "        DE_COMUNE, DE_COMUNE COMUNE1, DE_MANSIONE, DE_MANSIONE MANSIONE1, AN_LAVORATORE "
			+ "  WHERE AM_MOBILITA_ISCR.CODTIPOMOB    = DE_MB_TIPO.CODMBTIPO(+) "
			+ " AND AM_MOVIMENTO.PRGMOVIMENTO(+)   = AM_MOBILITA_ISCR.PRGMOVIMENTO "
			+ " AND AZ1.PRGAZIENDA(+) = UAZ1.PRGAZIENDA " + " AND AM_MOVIMENTO.PRGAZIENDA = UAZ1.PRGAZIENDA(+) "
			+ " AND AM_MOVIMENTO.PRGUNITA = UAZ1.PRGUNITA(+) "
			+ " AND AM_MOBILITA_ISCR.CDNMBSTATORICH = STATO_RICH.CDNMBSTATORICH(+)"
			+ " AND AN_AZIENDA.PRGAZIENDA(+)          = AN_UNITA_AZIENDA.PRGAZIENDA "
			+ " AND AM_MOBILITA_ISCR.PRGAZIENDA          = AN_UNITA_AZIENDA.PRGAZIENDA(+) "
			+ " AND AM_MOBILITA_ISCR.PRGUNITA          = AN_UNITA_AZIENDA.PRGUNITA(+) "
			+ " AND AM_MOBILITA_ISCR.CODMANSIONE       = DE_MANSIONE.CODMANSIONE(+) "
			+ " AND AM_MOVIMENTO.CODMANSIONE = MANSIONE1.CODMANSIONE(+) "
			+ " AND AN_UNITA_AZIENDA.CODCOM        = DE_COMUNE.CODCOM(+) " + " AND UAZ1.CODCOM = COMUNE1.CODCOM(+) "
			+ "    AND AM_MOBILITA_ISCR.DATFINE       IS NOT NULL "
			+ "    AND trunc(AM_MOBILITA_ISCR.DATFINE)  < trunc(SYSDATE) " + " AND DE_MB_TIPO.codmbtipo = ma.codmbtipo "
			+ " AND ma.codlistespec= mn.cod_listespec (+) "
			+ "    AND AM_MOBILITA_ISCR.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		query_totale.append(" AND AM_MOBILITA_ISCR.CDNLAVORATORE = ");
		query_totale.append(cdnLavoratore);
		query_totale.append(" ORDER BY AM_MOBILITA_ISCR.DATINIZIO DESC");
		// Debug
		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}// class InfStorMobilita
