package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicElAnagraf implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "  SELECT " + " lav.cdnLavoratore," + " lav.strCodiceFiscale,  "
			+ " lav.strCognome," + " lav.strNome,  " + " ea.prgElencoAnagrafico,  "
			+ " to_char(ea.datinizio, 'dd/mm/yyyy') as datInizio, " + " ea.strNote,  "
			+ " to_char(ea.datCan, 'dd/mm/yyyy') as dtmCan, " + " ea.codTipoCan as cdnTipoCan,  ea.cdnUtIns,  "
			+ " to_char(ea.dtmIns, 'dd/mm/yyyy') as dtmIns, "
			+ " ea.cdnutmod,  to_char(ea.dtmMod, 'dd/mm/yyyy') as dtmMod," + " ea.numkloelencoanag,  "
			+ " to_char(lav.datNasc, 'dd/mm/yyyy') as datNasc" + " FROM an_lavoratore lav, "
			+ " am_elenco_anagrafico ea," + " an_lav_storia_inf_coll  coll," + " an_lav_storia_inf inf "
			+ " WHERE lav.cdnLavoratore = ea.cdnLavoratore "
			+ " AND nvl(ea.datCan,to_date('01/01/2100','DD/MM/YYYY')) = to_date('01/01/2100','DD/MM/YYYY')"
			+ " AND ea.prgElencoAnagrafico = coll.strChiaveTabella" + " AND coll.codLstTab = 'EA'"
			+ " AND coll.prgLavStoriaInf = inf.prgLavStoriaInf " + " AND inf.datFine is null ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) req.getAttribute("NOME");
		String cognome = (String) req.getAttribute("COGNOME");
		String datInizio = (String) req.getAttribute("DATINIZIO");
		String cf = (String) req.getAttribute("CF");
		String CodCPI = (String) req.getAttribute("CODCPI");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strNome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(lav.strCognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strNome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(lav.strCognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		}
		// ricerca per cpi competente
		if ((CodCPI != null) && (!CodCPI.equals(""))) {
			buf.append(" AND inf.codCpiTit= '" + CodCPI + "'");
			buf.append(" AND inf.codMonoTipoCpi = 'C' ");
		}

		if ((datInizio != null) && (!datInizio.equals(""))) {
			buf.append(" AND ea.datInizio = to_date('" + datInizio + "','dd/mm/yyyy' ) ");

		}

		buf.append(" ORDER BY STRCOGNOME,STRNOME,STRCODICEFISCALE");

		return buf.toString();

	}

	public static void main(String[] args) throws Exception {
		RequestContainer rc = new RequestContainer();
		SourceBean request = new SourceBean("row");
		rc.setServiceRequest(request);
		request.setAttribute("tipoRicerca", "esatta");
		request.setAttribute("nome", "adriano");
		request.setAttribute("cognome", "rossi");
		request.setAttribute("datinizio", "01/01/2004");
		request.setAttribute("cf", "");
		request.setAttribute("codCpi", "");
		DynamicElAnagraf d = new DynamicElAnagraf();
		System.out.println(d.getStatement(rc, null));
	}
}