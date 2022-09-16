package it.eng.sil.module.bonusoccupazionale;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicRicercaBonus implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT notifica.prgspnotificapn, notifica.strcodicefiscale, lav.cdnlavoratore, to_char(notifica.datadesione, 'dd/mm/yyyy') datadesione, "
			+ " to_char(notifica.datricezione, 'dd/mm/yyyy') datricezione, notifica.flgpresoincarico, de_provincia.strDenominazione "
			+ " FROM sp_notifica_pn notifica "
			+ " left join an_lavoratore lav on (notifica.strcodicefiscale = lav.strcodicefiscale) "
			+ " left join de_provincia on (notifica.codprovincia = de_provincia.codprovincia) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cf = StringUtils.getAttributeStrNotNull(req, "strCodiceFiscale");
		String datin = StringUtils.getAttributeStrNotNull(req, "datadesioneda");
		String datTo = StringUtils.getAttributeStrNotNull(req, "datadesionea");
		String codProvincia = StringUtils.getAttributeStrNotNull(req, "codProv");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String flgPresaCarico = StringUtils.getAttributeStrNotNull(req, "flgPresaCarico");
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (!cdnLavoratore.equals("")) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" lav.cdnlavoratore = " + cdnLavoratore + " ");
		} else {
			if (tipoRic.equalsIgnoreCase("esatta")) {
				if (!cf.equals("")) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(notifica.strcodicefiscale) = '" + cf.toUpperCase() + "' ");
				}
			} else {
				if (!cf.equals("")) {
					if (buf.length() == 0) {
						buf.append(" WHERE ");
					} else {
						buf.append(" AND ");
					}
					buf.append(" upper(notifica.strcodicefiscale) like '" + cf.toUpperCase() + "%' ");
				}
			}

			if (!datin.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" notifica.datadesione >= TO_DATE('" + datin + "','DD/MM/YYYY') ");
			}

			if (!datTo.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" notifica.datadesione <= TO_DATE('" + datTo + "','DD/MM/YYYY') ");
			}

			if (!codProvincia.equals("")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}

				buf.append(" notifica.codprovincia ='" + codProvincia + "' ");
			}

			if (flgPresaCarico.equals("S")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}

				buf.append(" nvl(notifica.flgpresoincarico, 'N') = 'S' ");
			}
		}

		buf.append(" ORDER BY notifica.strcodicefiscale asc, notifica.datadesione DESC,  notifica.datricezione DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}