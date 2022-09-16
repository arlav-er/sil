package it.eng.sil.module.sap.errori;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito;

public class DynRicercaInvioErrori implements IDynamicStatementProvider {

	public DynRicercaInvioErrori() {
	}

	private static final String SELECT_SQL_BASE = "SELECT TS_INVIO_AUT_SAP.CDNLAVORATORE, TS_INVIO_AUT_SAP.strcodicefiscale, "
			+ "TO_CHAR(TS_INVIO_AUT_SAP.datinvio, 'DD/MM/YYYY') STRDATINVIO, TS_INVIO_AUT_SAP.STRNOTE, TS_INVIO_AUT_SAP.datinvio  "
			+ "FROM TS_INVIO_AUT_SAP "
			+ "INNER JOIN AN_LAVORATORE LAV ON (TS_INVIO_AUT_SAP.CDNLAVORATORE = LAV.CDNLAVORATORE) "
			+ "WHERE upper(TS_INVIO_AUT_SAP.flginviosap) != '" + Risposta_invioSAP_TypeEsito._OK.toUpperCase() + "'";

	private static final String SELECT_SQL_BASE_CPI = "SELECT TS_INVIO_AUT_SAP.CDNLAVORATORE, TS_INVIO_AUT_SAP.strcodicefiscale, "
			+ "TO_CHAR(TS_INVIO_AUT_SAP.datinvio, 'DD/MM/YYYY') STRDATINVIO, TS_INVIO_AUT_SAP.STRNOTE, TS_INVIO_AUT_SAP.datinvio  "
			+ "FROM TS_INVIO_AUT_SAP "
			+ "INNER JOIN AN_LAVORATORE LAV ON (TS_INVIO_AUT_SAP.CDNLAVORATORE = LAV.CDNLAVORATORE) "
			+ "INNER JOIN an_lav_storia_inf INF on (LAV.CDNLAVORATORE = INF.CDNLAVORATORE AND INF.DATFINE IS NULL) "
			+ "WHERE upper(TS_INVIO_AUT_SAP.flginviosap) != '" + Risposta_invioSAP_TypeEsito._OK.toUpperCase() + "'";

	private static final String SELECT_SQL_TS_TRACC_SAP = " AND TS_INVIO_AUT_SAP.CDNLAVORATORE NOT IN (SELECT "
			+ " CDNLAVORATORE FROM TS_TRACCIAMENTO_SAP  TS "
			+ " WHERE TS.CDNLAVORATORE = TS_INVIO_AUT_SAP.CDNLAVORATORE " + " AND UPPER(TS.STRESITOMIN) = '"
			+ Risposta_invioSAP_TypeEsito._OK.toUpperCase() + "'" + " AND TS.DATINVIOMIN > TS_INVIO_AUT_SAP.datinvio )";

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nome = SourceBeanUtils.getAttrStrNotNull(req, "strNome");
		String cognome = SourceBeanUtils.getAttrStrNotNull(req, "strCognome");
		String cf = SourceBeanUtils.getAttrStrNotNull(req, "strCodiceFiscale");
		String tipoRic = SourceBeanUtils.getAttrStrNotNull(req, "tipoRicerca");
		String dataInvioDa = SourceBeanUtils.getAttrStrNotNull(req, "dataInvioDa");
		String dataInvioA = SourceBeanUtils.getAttrStrNotNull(req, "dataInvioA");
		String codiceCpi = SourceBeanUtils.getAttrStrNotNull(req, "codcpi");

		StringBuffer query_totale = null;

		if (StringUtils.isFilledNoBlank(codiceCpi)) {
			query_totale = new StringBuffer(SELECT_SQL_BASE_CPI);
		} else {
			query_totale = new StringBuffer(SELECT_SQL_BASE);
		}

		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {

			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND upper(lav.strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" AND upper(lav.strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				buf.append(" AND upper(TS_INVIO_AUT_SAP.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND upper(lav.strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" AND upper(lav.strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				buf.append(" AND upper(TS_INVIO_AUT_SAP.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		}
		if ((dataInvioDa != null) && (!dataInvioDa.equals("")) && (dataInvioA != null) && (!dataInvioA.equals(""))) {
			buf.append(" AND TRUNC(TS_INVIO_AUT_SAP.DATINVIO) between to_date('" + dataInvioDa
					+ "', 'dd/mm/yyyy') AND to_date('" + dataInvioA + "', 'dd/mm/yyyy')");
		}
		if (StringUtils.isFilledNoBlank(codiceCpi)) {
			buf.append(" AND Inf.Codcpitit = '" + codiceCpi + "' ");
		}
		buf.append(SELECT_SQL_TS_TRACC_SAP);

		buf.append(" ORDER BY TRUNC(TS_INVIO_AUT_SAP.DATINVIO) DESC, TS_INVIO_AUT_SAP.strCodiceFiscale ASC ");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}
