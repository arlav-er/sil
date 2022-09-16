package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di un iscritto dato: - o il codice fiscale - o il nome - o il cognome - o nessuna delle
 * precedenti (restituisce TUTTO)
 * 
 * @author Alessio Rolfini
 * 
 */
public class DynamicRicerca implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT l.CDNLAVORATORE,  " + " l.STRCODICEFISCALE, "
			+ " l.STRCOGNOME, " + " l.STRNOME," + " TO_CHAR(l.DATNASC, 'DD/MM/YYYY') DATNASC, "
			+ " c.strDenominazione || ' ('||trim(pc.strIstat)||')' as comNas, "
			+ " decode (ai.codMonoTipoCpi, 'C', cpi.strDescrizione, 'E', cpi.strDescrizione, 'T', cpiO.strDescrizione) as CpiComp, "
			+ " decode (ai.codMonoTipoCpi, 'C', '', 'T', cpi.strDescrizione, 'E', 'Fuori Provincia') as CpiTit "
			+ " FROM AN_LAVORATORE l "
			+ " inner join an_lav_storia_inf ai on (l.cdnLavoratore=ai.cdnLavoratore and ai.datFine is null) "
			+ " inner join de_comune c on (c.codCom=l.codComNas) "
			+ " inner join de_provincia pc on (c.CODPROVINCIA=pc.codProvincia) "
			+ " left join de_cpi cpi on (ai.codCpiTit=cpi.codCpi) "
			+ " left join de_cpi cpiO on (ai.codCpiOrig=cpiO.codCpi) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) SourceBeanUtils.getAttrStrNotNull(req, "strNome");
		String cognome = (String) SourceBeanUtils.getAttrStrNotNull(req, "strCognome");
		String cf = (String) SourceBeanUtils.getAttrStrNotNull(req, "strCodiceFiscale");
		String datnasc = (String) SourceBeanUtils.getAttrStrNotNull(req, "datnasc");
		String codComNas = (String) SourceBeanUtils.getAttrStrNotNull(req, "codComNas");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if (!nome.equals("")) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strnome) = '" + nome.toUpperCase() + "'");
			}

			if (!cognome.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if (!cf.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if (!nome.equals("")) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
			}

			if (!cognome.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if (!cf.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		}

		if (!datnasc.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" datnasc=TO_DATE('" + datnasc + "','DD/MM/YYYY') ");
		}

		if (!codComNas.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" codComNas='" + codComNas + "'");
		}

		if (buf.length() == 0) {
			buf.append("WHERE 1 = 1 ");
		}

		buf.append(" ORDER BY upper(l.STRCOGNOME), upper(l.STRNOME), upper(l.STRCODICEFISCALE)");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}