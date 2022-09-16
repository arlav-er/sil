package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.security.User;

/**
 * Effettua la ricerca dinamica sui contatti
 * 
 * @author Giovanni Landi
 * 
 */
public class DynStatementRicercaAppuntamenti implements IDynamicStatementProvider {

	private static final int SCADENZA_AMMINISTRATIVA_COLLOQUIO_LAV_POR = 102;

	private static final String SELECT_SQL_BASE = " select ag_agenda.PRGAPPUNTAMENTO, "
			+ "to_char(ag_agenda.DTMDATAORA,'dd/mm/yyyy') as DATA, "
			+ "to_char(ag_agenda.DTMDATAORA,'hh24:mi')  as ORARIO, " + "ag_agenda.NUMMINUTI, "
			+ "de_servizio.STRDESCRIZIONE as SERVIZIO, " + "case "
			+ "    when (an_spi.strNome is null and an_spi.strCognome is null) then " + "       ''" + "    else "
			+ "       concat(concat(an_spi.strNome,' '),an_spi.strCognome)" + "end  " + "    as OPERATORE, "
			+ "de_esito_appunt.STRDESCRIZIONE as ESITO, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM ag_agenda "
			+ "LEFT JOIN de_servizio ON (ag_agenda.CODSERVIZIO = de_servizio.CODSERVIZIO) "
			+ "LEFT JOIN an_spi ON (ag_agenda.PRGSPI = an_spi.PRGSPI) "
			+ "LEFT JOIN de_esito_appunt ON (ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);
		String tipoGruppo = user.getCodTipo();
		SourceBean req = requestContainer.getServiceRequest();
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
			buf.append(" WHERE ag_lavoratore.CDNLAVORATORE = " + cdnLavoratore);
		} else {
			buf.append(" WHERE AG_AGENDA.PRGAZIENDA = " + prgAzienda + " AND AG_AGENDA.PRGUNITA = " + prgUnita);
		}

		if (codCpi.equals("")) {
			codCpi = user.getCodRif();
		}
		buf.append(" AND ag_agenda.CODCPI = '" + codCpi + "'");

		if (tipoGruppo.equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
			buf.append(
					" and de_servizio.codservizio in (SELECT ds.codservizio FROM de_servizio ds WHERE nvl(ds.flgPatronato, 'N') = 'S') ");
		}

		buf.append(" order by AG_AGENDA.DTMDATAORA desc");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}