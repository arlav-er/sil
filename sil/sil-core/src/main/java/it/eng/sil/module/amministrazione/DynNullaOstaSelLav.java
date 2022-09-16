package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynNullaOstaSelLav implements IDynamicStatementProvider {
	public DynNullaOstaSelLav() {
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
			+ " LAV.FLGCFOK FLGCFOK,"
			+ " decode(ST.CODMONOTIPOCPI, 'C',cpiT.STRDESCRIZIONE, cpiO.STRDESCRIZIONE) cpicompetente"
			+ " FROM AN_LAVORATORE LAV,  AN_LAV_STORIA_INF ST," + " DE_COMUNE cNAS,  DE_PROVINCIA p,"
			+ " de_cpi cpiT, de_cpi cpiO" + " WHERE LAV.CDNLAVORATORE = ST.CDNLAVORATORE " + " AND ST.DATFINE IS NULL "
			+ " AND LAV.CODCOMNAS = cNAS.CODCOM" + " AND cNAS.CODPROVINCIA = p.CODPROVINCIA"
			+ " AND ST.CODCPITIT  = cpiT.CODCPI(+) " + " AND ST.CODCPIORIG = cpiO.CODCPI(+)";

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
