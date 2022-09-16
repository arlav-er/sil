package it.eng.sil.module.sifer;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.rdc.DynamicListNotificheRDC;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

public class DynamicListCorsiFormazione implements IDynamicStatementProvider  {


	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicListNotificheRDC.class.getName());
	public String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " SELECT cor.prgcorso, lav.strnome, lav.strcognome, lav.strcodicefiscale, cor.strdescrizione AS desccorso, " +
			"        cor.strente, cor.strindirizzoente, com.strdenominazione, cor.strlocalitaente, " +
			"        cpi.strdescrizione as desccpi, cor.cdnlavoratore " +
			"FROM    PR_CORSO COR " +
			"LEFT OUTER JOIN AN_LAVORATORE LAV ON (cor.cdnlavoratore = lav.cdnlavoratore) " +
			"LEFT OUTER JOIN de_comune COM ON (com.codcom = cor.codcomente ) " +
			"LEFT OUTER JOIN AN_LAV_STORIA_INF storia on (cor.cdnlavoratore = storia.cdnlavoratore and storia.codmonotipocpi='C' and storia.datfine is null) " +
			"LEFT OUTER JOIN de_cpi cpi on (storia.codcpitit = cpi.codcpi) ";


	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String titoloCorso = (String) req.getAttribute("strTitoloCorso");
		String codiceCorso = (String) req.getAttribute("strCodiceCorso");
		String annoCorso = (String) req.getAttribute("annoCorso");
		String strDataDa = StringUtils.getAttributeStrNotNull(req, "dataInizioCorso");
		String strDataA = StringUtils.getAttributeStrNotNull(req, "dataFineCorso");
		String tipoCertificazione = StringUtils.getAttributeStrNotNull(req, "tipoCertificazione");
		String codComune = StringUtils.getAttributeStrNotNull(req, "codComEnte");
		String strEnte = StringUtils.getAttributeStrNotNull(req, "strEnte");



		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();



		if ((titoloCorso != null) && (!titoloCorso.equals(""))) {
			titoloCorso = StringUtils.replace(titoloCorso, "'", "''");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(cor.strdescrizione) like '%" + titoloCorso.toUpperCase() + "%'");
		}

		if ((codiceCorso != null) && (!codiceCorso.equals(""))) {
			codiceCorso = StringUtils.replace(codiceCorso, "'", "''");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(cor.codcorso) like '" + codiceCorso.toUpperCase() + "%'");
		}

		if ((annoCorso != null) && (!annoCorso.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" cor.numanno = " + annoCorso);
		}


		if (strDataDa != null && !strDataDa.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" to_date(to_char(cor.datinizio, 'dd/mm/yyyy'),'dd/mm/yyyy') >= ");
			buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
		} 

		if (strDataA != null && !strDataA.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" to_date(to_char(cor.datfine, 'dd/mm/yyyy'),'dd/mm/yyyy') <= ");
			buf.append("to_date('" + strDataA + "','dd/mm/yyyy')");
		} 

		if ((tipoCertificazione != null) && (!tipoCertificazione.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" cor.codtipocertificato ='"+  tipoCertificazione+ "'");
		}

		if ((strEnte != null) && (!strEnte.equals(""))) {
			strEnte = StringUtils.replace(strEnte, "'", "''");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(cor.strente) like '%" + strEnte.toUpperCase() + "%'");
		}
		
		if ((codComune != null) && (!codComune.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" cor.codcomente ='"+  codComune+ "'");
		}



		buf.append(" ORDER BY 2, 3, 5 ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}

