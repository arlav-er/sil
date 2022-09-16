package it.eng.sil.module.common;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Questa classe restituisce la query per la ricerca dei lavoratori con pulsante di lookup
 * <p>
 * 
 * @author: Rolfini
 * 
 */

public class DynRicercaLavoratoriStatement implements IDynamicStatementProvider {
	public DynRicercaLavoratoriStatement() {
	}

	private static final String SELECT_SQL_BASE = " SELECT l.CDNLAVORATORE,  " + " l.STRCODICEFISCALE, "
			+ " l.STRCOGNOME, " + " l.STRNOME, " + " TO_CHAR(l.DATNASC, 'DD/MM/YYYY') DATNASC, "
			+ " decode(c.strDenominazione, '', '',  c.strDenominazione || ' ('||trim(pc.strIstat)||')') as comNas, "
			+ " lavsto.codCpiTit, lavsto.codmonotipocpi, lavsto.codcpiorig, "
			+ " decode(codmonotipocpi, 'C', cpiTit.strDescrizione || ' (' || trim(pcTit.strIstat) || ')', "
			+ "        cpiOrig.strDescrizione || ' (' || trim(pcOrig.strIstat)||')') as cpiCompetente "
			+ " FROM AN_LAVORATORE l "
			+ " INNER JOIN an_lav_storia_inf lavsto ON (lavsto.cdnLavoratore=l.cdnLavoratore and lavsto.datFine is null) "
			+ " INNER JOIN de_comune c on (c.codCom=l.codComNas) "
			+ " INNER JOIN de_provincia pc on (pc.CODPROVINCIA=c.codProvincia) "
			+ " LEFT JOIN de_cpi cpiTit on (lavsto.codcpitit=cpiTit.codcpi) "
			+ " LEFT JOIN de_provincia pcTit on (pcTit.codProvincia=cpiTit.codProvincia) "
			+ " LEFT JOIN de_cpi cpiOrig on (lavsto.codCpiOrig=cpiOrig.codcpi) "
			+ " left join de_provincia pcOrig on (pcOrig.codProvincia=cpiOrig.codProvincia) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) req.getAttribute("strNome");
		String cognome = (String) req.getAttribute("strCognome");
		String cf = (String) req.getAttribute("strCodiceFiscale");
		String datnasc = (String) req.getAttribute("datnasc");
		String codComNas = (String) req.getAttribute("codComNas");

		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND");
				buf.append(" upper(strnome) = '" + nome.toUpperCase() + "'");
			}

		} else {

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND");
				buf.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
			}

		} // else

		if ((datnasc != null) && (!datnasc.equals(""))) {
			buf.append(" AND");
			buf.append(" datnasc=TO_DATE('" + datnasc + "','DD/MM/YYYY') ");
		}

		if ((codComNas != null) && (!codComNas.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" codComNas='" + codComNas + "'");
		}

		buf.append("ORDER BY STRCOGNOME, STRNOME, STRCODICEFISCALE");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}