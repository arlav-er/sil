package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di un iscritto dato: - o il codice fiscale - o il nome - o il cognome - o nessuna delle
 * precedenti (restituisce TUTTO)
 * 
 */

public class DynRicercaAgendaLavoratori implements IDynamicStatementProvider {
	public DynRicercaAgendaLavoratori() {
	}

	private static final String SELECT_SQL_BASE = " SELECT l.CDNLAVORATORE,  " + " l.STRCODICEFISCALE, "
			+ " l.STRCOGNOME, " + " l.STRNOME, " + " l.STRSESSO, " + " TO_CHAR(l.DATNASC, 'DD/MM/YYYY') DATNASC, "
			+ " l.CODCOMNAS, " + " l.CODCITTADINANZA, " + " l.CODCITTADINANZA2, " + " l.CODSTATOCIVILE, "
			+ " l.CODCOMRES, " + " l.NUMFIGLI, " + " l.STRINDIRIZZORES, " + " l.STRLOCALITARES, " + " l.STRCAPRES, "
			+ " l.CODCOMDOM, " + " l.STRINDIRIZZODOM, " + " l.STRLOCALITADOM, " + " l.STRCAPDOM, " + " l.STRTELRES, "
			+ " l.STRTELDOM, " + " l.STRTELALTRO, " + " l.STRCELL, " + " l.STREMAIL, " + " l.STRFAX, " + " l.STRNOTE, "
			+ " l.CDNUTINS, " + " TO_CHAR(l.DTMINS, 'DD/MM/YYYY') DTMINS, " + " l.CDNUTMOD, "
			+ " TO_CHAR(l.DTMMOD, 'DD/MM/YYYY') DTMMOD, " + " NUMKLOLAVORATORE, " + " FLGCFOK, "
			+ " decode(gen.codProvinciaSil, null, 'Fuori Provincia', cpi.strDescrizione) as CpiTit "
			+ " from AN_LAVORATORE l"
			+ " inner join an_lav_storia_inf ai on (l.cdnLavoratore=ai.cdnLavoratore and ai.datFine is null) "
			+ " left join de_cpi cpi on (ai.codCpiTit=cpi.codCpi) "
			+ " left join ts_generale gen on (cpi.codProvincia=gen.codProvinciaSil) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) req.getAttribute("strNome_ric");
		String cognome = (String) req.getAttribute("strCognome_ric");
		String cf = (String) req.getAttribute("strCodiceFiscale_ric");
		String datnasc = (String) req.getAttribute("datnasc");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(l.strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(l.strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(l.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(l.strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(l.strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(l.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		} // else

		if ((datnasc != null) && (!datnasc.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" l.datnasc=TO_DATE('" + datnasc + "','DD/MM/YYYY') ");
		}

		buf.append("ORDER BY l.STRCOGNOME, l.STRNOME");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}