package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

public class DynStatementRicercaSOAppuntamenti implements IDynamicStatementProvider {

	private static final int SCADENZA_AMMINISTRATIVA_COLLOQUIO_LAV_POR = 102;

	private static final String SELECT_SQL_BASE = " select ag_agenda.PRGAPPUNTAMENTO, "
			+ "to_char(ag_agenda.DTMDATAORA,'dd/mm/yyyy') as DATA, "
			+ "to_char(ag_agenda.DTMDATAORA,'hh24:mi')  as ORARIO, " + "ag_agenda.NUMMINUTI, "
			+ "de_servizio.STRDESCRIZIONE as SERVIZIO, " + "case "
			+ "    when (an_spi.strNome is null and an_spi.strCognome is null) then " + "       ''" + "    else "
			+ "       concat(concat(an_spi.strNome,' '),an_spi.strCognome)" + "end  " + "    as OPERATORE, "
			+ "de_cpi.CODCPI, " + "de_cpi.strDescrizione || ' - ' || de_cpi.codcpi as CPI, "
			+ "de_esito_appunt.STRDESCRIZIONE as ESITO " + "FROM ag_agenda "
			+ "LEFT JOIN de_servizio ON (ag_agenda.CODSERVIZIO = de_servizio.CODSERVIZIO) "
			+ "LEFT JOIN an_spi ON (ag_agenda.PRGSPI = an_spi.PRGSPI) "
			+ "LEFT JOIN de_esito_appunt ON (ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();
		// String strCodScadenza = req.containsAttribute("SCADENZIARIO")?
		// req.getAttribute("SCADENZIARIO").toString():"";
		String prgAzienda = req.containsAttribute("PRGAZIENDA") ? req.getAttribute("PRGAZIENDA").toString() : "";
		String prgUnita = req.containsAttribute("PRGUNITA") ? req.getAttribute("PRGUNITA").toString() : "";
		String cdnLavoratore = req.containsAttribute("CDNLAVORATORE") ? req.getAttribute("CDNLAVORATORE").toString()
				: "";
		String codCpi = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (cdnLavoratore.compareTo("") != 0) {
			buf.append(" INNER JOIN ag_lavoratore ON (ag_agenda.CODCPI = ag_lavoratore.CODCPI");
			buf.append(" AND ag_agenda.PRGAPPUNTAMENTO = ag_lavoratore.PRGAPPUNTAMENTO)");
			buf.append(" INNER JOIN de_cpi ON (ag_lavoratore.CODCPI = de_cpi.CODCPI)");
			buf.append(" WHERE ag_lavoratore.CDNLAVORATORE = " + cdnLavoratore);
		} else {
			buf.append(" INNER JOIN de_cpi ON (ag_agenda.CODCPI = de_cpi.CODCPI)");
			buf.append(" WHERE AG_AGENDA.PRGAZIENDA = " + prgAzienda + " AND AG_AGENDA.PRGUNITA = " + prgUnita);
		}

		buf.append(" order by AG_AGENDA.DTMDATAORA desc");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}