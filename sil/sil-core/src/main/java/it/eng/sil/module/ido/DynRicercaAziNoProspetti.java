package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaAziNoProspetti implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " select tipo_azienda.strdescrizione, azienda.strcodicefiscale,"
			+ " azienda.strpartitaiva, azienda.strragionesociale,"
			+ " unita.strindirizzo AS strindirizzo ,comune.strdenominazione AS strdenominazione"
			+ " from an_azienda  azienda, an_unita_azienda  unita, de_comune  comune, "
			+ "      de_tipo_azienda tipo_azienda " + " where azienda.prgazienda = unita.prgazienda "
			+ "       and unita.codcom = comune.codcom "
			+ "       and tipo_azienda.codtipoazienda = azienda.CODTIPOAZIENDA "
			+ "       and (unita.prgazienda, unita.prgunita) in "
			+ "	( SELECT an_azienda.PRGAZIENDA, max(an_un.PRGUNITA) as prgunita "
			+ "     FROM an_unita_azienda an_un, de_comune de_c, an_azienda, de_tipo_azienda"
			+ "     WHERE an_azienda.flgobbligol68 = 'S' "
			+ "           and de_tipo_azienda.codtipoazienda = an_azienda.codtipoazienda "
			+ "           and an_azienda.PRGAZIENDA = an_un.PRGAZIENDA " + "           and an_un.codcom = de_c.codcom "
			+ "           and de_c.CODPROVINCIA  in "
			+ "			(select ts_generale.CODPROVINCIASIL from ts_generale) "
			+ " 			and an_azienda.prgazienda NOT IN " + "	(SELECT cm_prospetto_inf.prgazienda "
			+ "    FROM cm_prospetto_inf WHERE cm_prospetto_inf.codmonostatoprospetto in ('A','S','U') ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String anno = StringUtils.getAttributeStrNotNull(req, "anno");
		String data = StringUtils.getAttributeStrNotNull(req, "data");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((data != null) && (!data.equals(""))) {
			buf.append(" AND cm_prospetto_inf.DATCONSEGNAPROSPETTO <= to_date('" + data + "', 'DD/MM/YYYY') ");
		}

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" AND cm_prospetto_inf.numannorifprospetto = '" + anno + "' ) ");
		}

		buf.append(" group by	an_azienda.PRGAZIENDA ) ");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
