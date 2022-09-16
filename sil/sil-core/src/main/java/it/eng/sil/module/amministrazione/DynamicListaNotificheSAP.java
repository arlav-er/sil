package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicListaNotificheSAP implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaNotificheSAP.class.getName());

	private String className = this.getClass().getName();

	private String SELECT_SQL_BASE = " SELECT max(sp_lav.dtmins) as dtmins, sp_notifica.codMinSap, "
			+ " lav.strCodiceFiscale, lav.strCognome, lav.strNome, " + " sp_lav.cdnLavoratore, "
			+ " to_char(sp_notifica.datNotifica, 'dd/mm/yyyy hh24:mi:ss') as datNotifica, "
			+ " mn_yg_motivo_modifica.strdescrizione as descNotifica, sp_notifica.datNotifica dataNotifica "
			+ " FROM sp_notifica "
			+ " inner join mn_yg_motivo_modifica on (sp_notifica.codMotivo = mn_yg_motivo_modifica.codMotivoModifica) "
			+ " inner join sp_lavoratore sp_lav on (sp_notifica.codMinSap = sp_lav.codMinSap) "
			+ " inner join an_lavoratore lav on (sp_lav.cdnlavoratore = lav.cdnlavoratore) ";

	private static final String SELECT_SQL_BASE_DID_ATTIVA = " inner join am_elenco_anagrafico ea on (ea.cdnlavoratore = lav.cdnlavoratore) "
			+ " inner join am_dich_disponibilita did on (did.prgelencoanagrafico = ea.prgelencoanagrafico) ";

	private static final String SELECT_SQL_BASE_PATTO_ATTIVO = " inner join am_patto_lavoratore pt on (pt.cdnlavoratore = lav.cdnlavoratore) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String cf = StringUtils.getAttributeStrNotNull(req, "strCodiceFiscale");
		String cognome = StringUtils.getAttributeStrNotNull(req, "strCognome");
		String nome = StringUtils.getAttributeStrNotNull(req, "strNome");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String dataNotificaDa = StringUtils.getAttributeStrNotNull(req, "dataNotificaDa");
		String dataNotificaA = StringUtils.getAttributeStrNotNull(req, "dataNotificaA");
		String codMinSap = StringUtils.getAttributeStrNotNull(req, "codMinSap");
		String codMotivoModifica = StringUtils.getAttributeStrNotNull(req, "codMotivoModifica");
		String ricercaDidAttiva = StringUtils.getAttributeStrNotNull(req, "ricercaDidAttiva");
		String ricercaPattoAttivo = StringUtils.getAttributeStrNotNull(req, "ricercaPattoAttivo");

		if (("si").equalsIgnoreCase(ricercaDidAttiva)) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + SELECT_SQL_BASE_DID_ATTIVA;
		}

		if (("si").equalsIgnoreCase(ricercaPattoAttivo)) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + SELECT_SQL_BASE_PATTO_ATTIVO;
		}

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if (!cf.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strCodiceFiscale) = '" + cf.toUpperCase() + "' ");
			}
			if (!cognome.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strcognome) = '" + cognome.toUpperCase() + "' ");
			}
			if (!nome.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strnome) = '" + nome.toUpperCase() + "' ");
			}
		} else {
			if (!cf.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%' ");
			}
			if (!cognome.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strcognome) like '" + cognome.toUpperCase() + "%' ");
			}
			if (!nome.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strnome) like '" + nome.toUpperCase() + "%' ");
			}
		}

		if (!codMinSap.equals("")) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(sp_notifica.codMinSap) = '" + codMinSap.toUpperCase() + "' ");
		}

		if (!codMotivoModifica.equals("")) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(sp_notifica.codMotivo) = '" + codMotivoModifica.toUpperCase() + "' ");
		}

		if (!dataNotificaDa.equals("")) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" trunc(sp_notifica.datNotifica) >= to_date('" + dataNotificaDa + "', 'dd/mm/yyyy') ");
		}

		if (!dataNotificaA.equals("")) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" trunc(sp_notifica.datNotifica) <= to_date('" + dataNotificaA + "', 'dd/mm/yyyy') ");
		}

		if (("si").equalsIgnoreCase(ricercaDidAttiva)) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" did.codStatoAtto = 'PR' and did.datFine is null ");
		}

		if (("si").equalsIgnoreCase(ricercaPattoAttivo)) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" pt.codStatoAtto = 'PR' and pt.datFine is null ");
		}

		buf.append(
				" group by sp_notifica.codMinSap, lav.strCodiceFiscale, lav.strCognome, lav.strNome, sp_lav.cdnLavoratore, "
						+ "to_char(sp_notifica.datNotifica, 'dd/mm/yyyy hh24:mi:ss'), mn_yg_motivo_modifica.strdescrizione, sp_notifica.datNotifica ");

		buf.append(" order by lav.strCodiceFiscale, sp_notifica.datNotifica desc ");

		query_totale.append(buf.toString());

		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return (query_totale.toString());
	}

}
