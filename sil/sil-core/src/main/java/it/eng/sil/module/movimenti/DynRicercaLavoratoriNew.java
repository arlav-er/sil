package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Questa classe restituisce la query per la ricerca dei lavoratori con pulsante di lookup
 * <p>
 * 
 * @author: Paolo Roccetti
 * 
 */

public class DynRicercaLavoratoriNew implements IDynamicStatementProvider {
	public DynRicercaLavoratoriNew() {
	}

	private static final String SELECT_SQL_BASE = " SELECT LAV.CDNLAVORATORE CDNLAVORATORE,  "
			+ " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, " + " LAV.STRNOME STRNOME,"
			+ " ST.CODCPITIT CODCPILAV, " + " LAV.STRSESSO STRSESSO, TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, "
			+ " LAV.CODCOMNAS CODCOMNAS, (cNAS.STRDENOMINAZIONE || ' (' || p.STRTARGA || ')') STRCOMNAS,"
			+ " LAV.CODCITTADINANZA CODCITTADINANZA,"
			+ " LAV.CODCITTADINANZA2 CODCITTADINANZA2, LAV.CODSTATOCIVILE CODSTATOCIVILE, "
			+ " LAV.CODCOMRES CODCOMRES, LAV.NUMFIGLI NUMFIGLI, LAV.STRINDIRIZZORES STRINDIRIZZORES,"
			+ " LAV.STRLOCALITARES STRLOCALITARES, LAV.STRCAPRES STRCAPRES, LAV.CODCOMDOM CODCOMDOM, "
			+ " LAV.STRINDIRIZZODOM STRINDIRIZZODOM, LAV.STRLOCALITADOM STRLOCALITADOM, LAV.STRCAPDOM STRCAPDOM, "
			+ " LAV.STRTELRES STRTELRES, LAV.STRTELDOM STRTELDOM, LAV.STRTELALTRO STRTELALTRO, LAV.STRCELL STRCELL, "
			+ " LAV.STREMAIL STREMAIL, LAV.STRFAX STRFAX, LAV.STRNOTE STRNOTE, LAV.CDNUTINS CDNUTINS, "
			+ " TO_CHAR(LAV.DTMINS, 'DD/MM/YYYY') DTMINS, "
			+ " LAV.CDNUTMOD CDNUTMOD, TO_CHAR(LAV.DTMMOD, 'DD/MM/YYYY') DTMMOD, LAV.NUMKLOLAVORATORE NUMKLOLAVORATORE, "
			+ " decode(ST.CODMONOTIPOCPI, 'C',cpiT.STRDESCRIZIONE, cpiO.STRDESCRIZIONE) cpicompetente, "
			+ " decode (de_stato_occupaz_ragg.codstatooccupazragg, de_stato_occupaz.codstatooccupaz, "
			+ " 		   de_stato_occupaz_ragg.strdescrizione,"
			+ "		   (de_stato_occupaz_ragg.strdescrizione || ': ' || de_stato_occupaz.strdescrizione)) "
			+ "			AS descrStatoOcc, " + " TO_CHAR (statoOcc.DATINIZIO, 'DD/MM/YYYY') datInizioOcc, "
			+ " TO_CHAR (statoOcc.DATANZIANITADISOC, 'DD/MM/YYYY') datAnzOcc "
			+ " FROM AN_LAVORATORE LAV,  AN_LAV_STORIA_INF ST," + "      DE_COMUNE cNAS,  DE_PROVINCIA p,"
			+ "      de_cpi cpiT, de_cpi cpiO, " + "      am_stato_occupaz statoOcc, " + "      de_stato_occupaz, "
			+ "      de_stato_occupaz_ragg " + " WHERE LAV.CDNLAVORATORE = ST.CDNLAVORATORE "
			+ " AND ST.DATFINE IS NULL " + " AND LAV.CODCOMNAS = cNAS.CODCOM"
			+ " AND cNAS.CODPROVINCIA = p.CODPROVINCIA" + " AND ST.CODCPITIT  = cpiT.CODCPI(+) "
			+ " AND ST.CODCPIORIG = cpiO.CODCPI(+) "
			+ " AND statoOcc.CODSTATOOCCUPAZ = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ (+) "
			+ " AND statoOcc.CDNLAVORATORE (+) = st.CDNLAVORATORE AND statoOcc.DATFINE IS NULL "
			+ " AND de_stato_occupaz.codstatooccupazragg = de_stato_occupaz_ragg.codstatooccupazragg (+)";

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
				buf.append(" AND");
				buf.append(" upper(strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				buf.append(" AND");
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				buf.append(" AND");
				buf.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND");
				buf.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				buf.append(" AND");
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				buf.append(" AND");
				buf.append(" upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		} // else

		if ((datnasc != null) && (!datnasc.equals(""))) {
			buf.append(" AND");
			buf.append(" datnasc=TO_DATE('" + datnasc + "','DD/MM/YYYY') ");
		}

		buf.append("ORDER BY STRCOGNOME, STRNOME, STRCODICEFISCALE");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
