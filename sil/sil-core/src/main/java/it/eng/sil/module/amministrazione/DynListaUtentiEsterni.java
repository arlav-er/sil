package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynListaUtentiEsterni implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select lav.CDNLAVORATORE CDNLAVORATORE, lav.STRCOGNOME, lav.STRNOME, "
			+ " to_char(lav.DATNASC,'dd/mm/yyyy') datnasc, mans.strdescrizione mansione, "
			+ " com.strdenominazione comune, lav.strindirizzores indirizzo, lav.strtelres, "
			+ " to_char(mob.DATINIZIO,'dd/mm/yyyy') datInizio, destatoocc.strdescrizione descStatoOccupaz, "
			+ " to_char(mob.DATFINE,'dd/mm/yyyy') datFine, azi.STRRAGIONESOCIALE, tipo.STRDESCRIZIONE tipoMob "
			+ " from am_mobilita_iscr mob "
			+ " inner join an_lavoratore lav on (lav.CDNLAVORATORE = mob.CDNLAVORATORE) "
			+ " inner join de_comune com on (com.CODCOM = lav.CODCOMRES) "
			+ " inner join de_mb_tipo tipo on (tipo.CODMBTIPO = mob.CODTIPOMOB) "
			+ " left join an_azienda azi on (azi.PRGAZIENDA = mob.PRGAZIENDA) "
			+ " left join de_mansione mans on (mans.codmansione = mob.codmansione) "
			+ " inner join an_lav_storia_inf inf on (lav.cdnLavoratore=inf.cdnLavoratore and inf.datFine is null) "
			+ " left join am_stato_occupaz occupaz on (lav.cdnLavoratore=occupaz.cdnLavoratore and occupaz.datFine is null) "
			+ " left join de_stato_occupaz destatoocc on (occupaz.codstatooccupaz = destatoocc.codstatooccupaz) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String dataIn = StringUtils.getAttributeStrNotNull(req, "dataIn");
		// String dataInA = StringUtils.getAttributeStrNotNull(req, "dataInA");
		String tipoLista = StringUtils.getAttributeStrNotNull(req, "CodTipoLista");
		String codEsito = StringUtils.getAttributeStrNotNull(req, "codEsito");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CodCPI");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// se codCpi è vuoto, allora devo considerare solo i lavoratori la cui competenza è provinciale
		if (codCpi.equals("")) {
			buf.append(" inner join de_cpi on (de_cpi.codcpi=inf.codcpitit) ");
			buf.append(" inner join ts_generale on (ts_generale.codprovinciasil=de_cpi.codprovincia) ");
			buf.append(" where inf.codmonotipocpi = 'C' ");
		} else {
			buf.append(" where inf.codcpitit = '" + codCpi + "' ");
			buf.append(" and inf.codmonotipocpi = 'C' ");
		}
		// flitro per le mobilità aperte ad oggi
		buf.append(" and trunc(nvl(mob.datFine, sysdate)) >= sysdate ");

		if (!dataIn.equals("")) {
			buf.append(" and trunc(mob.datcrt) <= to_date('" + dataIn + "', 'dd/mm/yyyy') ");
		}

		/*
		 * if (!dataInA.equals("")) { buf.append(" and trunc(mob.datdomanda) <= to_date('" + dataInA +
		 * "', 'dd/mm/yyyy') "); }
		 */

		if (!tipoLista.equals("")) {
			buf.append(" and mob.codtipoMob = '" + tipoLista + "' ");
		}

		if (!codEsito.equals("")) {
			buf.append(" and mob.cdnmbstatorich = to_number(" + codEsito + ") ");
		}

		buf.append(" order by lav.STRCOGNOME, lav.STRNOME ");
		query_totale.append(buf.toString());

		return query_totale.toString();
	}
}
