package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynServizioLav implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select lav.STRCOGNOME, lav.STRNOME, to_char(lav.DATNASC,'dd/mm/yyyy') datnasc, lav.strindirizzodom indirizzo,"
			+ "	       lav.STRLOCALITADOM, lav.STRCAPDOM, lav.STRTELDOM, lav.STRTELRES,"
			+ "		   to_char(mob.DATFINE,'dd/mm/yyyy') datFine, tipo.STRDESCRIZIONE tipoMob,"
			+ "		   cpi.strdescrizione cpi, com.strdenominazione comune " + " from am_mobilita_iscr mob "
			+ " inner join an_lavoratore lav on (lav.CDNLAVORATORE = mob.CDNLAVORATORE) "
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.datFine is null) "
			+ " left join de_cpi cpi on (cpi.codCpi=inf.codCpiTit) "
			+ " inner join de_comune com on (com.CODCOM = lav.CODCOMDOM) "
			+ " inner join de_mb_tipo tipo on (tipo.CODMBTIPO = mob.CODTIPOMOB)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		String sesso = StringUtils.getAttributeStrNotNull(req, "sesso");
		String datda = StringUtils.getAttributeStrNotNull(req, "datada");

		buf.append(" where tipo.CODMBTIPO in ('LA', 'LB') ");

		/* Donne > 45 anni e uomini 50 */
		if ((sesso != null) && (!sesso.equals(""))) {
			buf.append(" and lav.strsesso='" + sesso + "' ");

			if (sesso.equals("F")) {
				buf.append("and ((" + "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'yyyy')) - to_number(to_char(trunc(lav.datnasc), 'yyyy')) > 45) "
						+ "or (to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'yyyy')) - to_number(to_char(trunc(lav.datnasc), 'yyyy')) = 45 and "
						+ "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'mm')) - to_number(to_char(trunc(lav.datnasc), 'mm')) > 0) " + " or "
						+ "(to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'yyyy')) - to_number(to_char(trunc(lav.datnasc), 'yyyy')) = 45 and "
						+ "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'mm')) - to_number(to_char(trunc(lav.datnasc), 'mm')) = 0 and "
						+ "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'dd')) - to_number(to_char(trunc(lav.datnasc), 'dd')) > 0)) ");
			} else {
				buf.append("and ((" + "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'yyyy')) - to_number(to_char(trunc(lav.datnasc), 'yyyy')) > 50) "
						+ "or (to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'yyyy')) - to_number(to_char(trunc(lav.datnasc), 'yyyy')) = 50 and "
						+ "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'mm')) - to_number(to_char(trunc(lav.datnasc), 'mm')) > 0) " + " or "
						+ "(to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'yyyy')) - to_number(to_char(trunc(lav.datnasc), 'yyyy')) = 50 and "
						+ "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'mm')) - to_number(to_char(trunc(lav.datnasc), 'mm')) = 0 and "
						+ "to_number(to_char(trunc(to_date('" + datda
						+ "','dd/mm/yyyy')), 'dd')) - to_number(to_char(trunc(lav.datnasc), 'dd')) > 0)) ");
			}
		}
		/* Sei mesi indietro dalla data selezionata */
		buf.append(" and trunc(mob.datfine) >= trunc(to_date('" + datda + "','dd/mm/yyyy')) ");
		buf.append(" and trunc(mob.datfine) <= ADD_MONTHS (trunc(to_date('" + datda + "','dd/mm/yyyy')),6) ");
		// ordinamento
		buf.append(" order by lav.STRCOGNOME, lav.STRNOME ");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
