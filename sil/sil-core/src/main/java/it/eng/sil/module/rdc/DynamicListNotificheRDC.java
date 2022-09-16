package it.eng.sil.module.rdc;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicListNotificheRDC implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicListNotificheRDC.class.getName());
	public String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " SELECT PRGRDC, CDNLAVORATORE, " + " STRCODICEFISCALE, "
			+ " STRCOGNOME, " + " STRNOME, " + " CODMONORUOLO, " + " DECODE(CODMONORUOLO, 'R', 'Richiedente',"
			+ "       'M', 'Altro membro nucleo familiare'," + "      '') AS DESCRCODMONORUOLO, "
			+ " TO_CHAR(DATDOMANDA, 'dd/mm/yyyy') AS STRDATDOMANDA, "
			+ " TO_CHAR(DATRENDICONTAZIONE, 'dd/mm/yyyy') AS STRDATRENDICONTAZIONE, "
			+ " TO_CHAR(DTMINS, 'dd/mm/yyyy') AS STRDATRICEZIONESIL, " + " STRPROTOCOLLOINPS, " + " CODSTATOINPS, "
			+ " STRTEL " + " FROM AM_RDC rdc ";

	private static final String SELECT_SQL_BASE_CVS = " SELECT" + " NVL(rdc.STRCODICEFISCALE,' ') AS CODICE_FISCALE,"
			+ " NVL(rdc.STRCFRICHIEDENTE,' ') AS CODICE_FISCALE_RICHIEDENTE," + " NVL(rdc.STRNOME,' ') AS NOME,"
			+ " NVL(rdc.STRCOGNOME,' ') AS COGNOME," + " NVL(TO_CHAR(rdc.DATNASC, 'dd/mm/yyyy'),' ') AS DATA_NASCITA,"
			+ " NVL(rdc.STRSESSO,' ') AS SESSO," + " DECODE(rdc.CODMONORUOLO, 'R', 'Richiedente',"
			+ "       'M', 'Membro'," + "       'T', 'Tutor'," + "      ' ') AS RUOLO_BENEFICIARIO, "
			+ " NVL(DE_CITTADINANZA.STRDESCRIZIONE,' ') AS CITTADINANZA, "
			+ " NVL(com2.STRDENOMINAZIONE,' ') AS COMUNE_RESIDENZA, "
			+ " NVL(rdc.STRINDIRIZZORES,' ') AS INDIRIZZO_RESIDENZA," + " NVL(rdc.STRCAPRES,' ')  AS CAP_RESIDENZA, "
			+ " NVL(com1.STRDENOMINAZIONE,' ') AS COMUNE_NASCITA, "
			+ " NVL(com3.STRDENOMINAZIONE,' ') AS COMUNE_DOMICILIO, "
			+ " NVL(rdc.STRINDIRIZZODOM,' ') AS INDIRIZZO_DOMICILIO," + " NVL(rdc.STRCAPDOM,' ')  AS CAP_DOMICILIO, "
			+ " NVL(rdc.STRTEL, ' ') AS TELEFONO, " + " NVL(rdc.STREMAIL, ' ') AS EMAIL, "
			+ " NVL(mn_st_cpi.STRDENOMINAZIONE, ' ') AS CPI, "
			+ " NVL(rdc.STRPROTOCOLLOINPS, ' ') AS CODICE_PROTOCOLLO_INPS, "
			+ " NVL(TO_CHAR(DATDOMANDA, 'dd/mm/yyyy'), ' ') AS DATA_DOMANDA, "
			+ " NVL(TO_CHAR(DTMINS, 'dd/mm/yyyy'), ' ') AS DATA_RICEZIONE_SIL, "
			+ " NVL(rdc.STRCODSAP, ' ') AS CODICE_SAP, " + " NVL(rdc.CODSTATOINPS, ' ') AS STATO, "
			+ " NVL(TO_CHAR(rdc.DATRENDICONTAZIONE, 'dd/mm/yyyy'), ' ') AS DECORRENZA_BENEFICIO, "
			+ " NVL(rdc.CODTIPODOMANDA,' ') AS TIPO_DOMANDA, "
			+ " NVL(TO_CHAR(rdc.DATVARIAZIONESTATO, 'dd/mm/yyyy'), ' ') AS DATA_VARIAZIONE_STATO " + " FROM AM_RDC rdc "
			+ " LEFT JOIN DE_CITTADINANZA ON ( rdc.CODCITTADINANZA = DE_CITTADINANZA.CODCITTADINANZA) "
			+ " LEFT JOIN de_comune com1 ON ( rdc.CODCOMNASC = com1.CODCOM) "
			+ " LEFT JOIN de_comune com2 ON ( rdc.CODCOMRES = com2.CODCOM) "
			+ " LEFT JOIN de_comune com3 ON ( rdc.CODCOMDOM = com3.CODCOM) "
			+ " LEFT JOIN mn_st_cpi ON ( rdc.CODCPIMIN = mn_st_cpi.CODCPIMIN) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		boolean isNotCVS = true;
		String moduleName = (String) req.getAttribute("AF_MODULE_NAME");

		if (moduleName.equalsIgnoreCase("M_NotificheRDCRicercaSaveCsv")) {
			isNotCVS = false;
		}
		String nome = (String) req.getAttribute("strNome");
		String cognome = (String) req.getAttribute("strCognome");
		String cf = (String) req.getAttribute("strCodiceFiscale");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String protocolloInps = StringUtils.getAttributeStrNotNull(req, "protocolloInps");
		String strDataDa = StringUtils.getAttributeStrNotNull(req, "dataInvioDa");
		String strDataA = StringUtils.getAttributeStrNotNull(req, "dataInvioA");
		String strDataRendDa = StringUtils.getAttributeStrNotNull(req, "dataRendicontazioneDa");
		String strDataRendA = StringUtils.getAttributeStrNotNull(req, "dataRendicontazioneA");
		String strDataRicSILDa = StringUtils.getAttributeStrNotNull(req, "dataRicezioneSILDa");
		String strDataRicSILA = StringUtils.getAttributeStrNotNull(req, "dataRicezioneSILA");
		String codComune = StringUtils.getAttributeStrNotNull(req, "codComRes");
		String codMonoRuolo = StringUtils.getAttributeStrNotNull(req, "codRuolo");
		String codStatoDomanda = StringUtils.getAttributeStrNotNull(req, "codStatoDomanda");
		String ordDatRendDC = StringUtils.getAttributeStrNotNull(req, "ordDatRendDC");
		String ordDatRend = StringUtils.getAttributeStrNotNull(req, "ordDatRend");
		String ordNucleoDC = StringUtils.getAttributeStrNotNull(req, "ordNucleoDC");
		String ordNucleo = StringUtils.getAttributeStrNotNull(req, "ordNucleo");
		String ordDataDomDC = StringUtils.getAttributeStrNotNull(req, "ordDataDomDC");
		String ordDataDom = StringUtils.getAttributeStrNotNull(req, "ordDataDom");
		/*
		 * String ordDataRicSILDC = StringUtils.getAttributeStrNotNull(req, "ordDataRicSILDC"); String ordDataRicSIL =
		 * StringUtils.getAttributeStrNotNull(req, "ordDataRicSIL");
		 */

		String codTipoDomanda = StringUtils.getAttributeStrNotNull(req, "codTipoDomanda");

		StringBuffer query_totale = isNotCVS ? new StringBuffer(SELECT_SQL_BASE)
				: new StringBuffer(SELECT_SQL_BASE_CVS);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strnome) = '" + nome.toUpperCase() + "'");
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

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append("( upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
				buf.append(" OR upper(STRCFRICHIEDENTE) = '" + cf.toUpperCase() + "' )");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
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

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append("( upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
				buf.append(" OR upper(STRCFRICHIEDENTE) like '" + cf.toUpperCase() + "%' )");
			}
		} // else

		if ((protocolloInps != null) && (!protocolloInps.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}

			String finisceCon = StringUtils.getAttributeStrNotNull(req, "finisceCon");
			if (("si").equalsIgnoreCase(finisceCon)) {
				buf.append(" upper(STRPROTOCOLLOINPS) like '%" + protocolloInps.toUpperCase() + "'");
			} else {
				buf.append(" upper(STRPROTOCOLLOINPS) = '" + protocolloInps.toUpperCase() + "'");
			}

		}

		if ((strDataDa != null && !strDataDa.equals("")) && (strDataA != null && !strDataA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (to_date(to_char(DATDOMANDA, 'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN ");
			buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
			buf.append(" and ");
			buf.append("to_date('" + strDataA + "','dd/mm/yyyy')");
			buf.append(") ");
		} else {
			if (strDataDa != null && !strDataDa.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (to_date(to_char(DATDOMANDA, 'dd/mm/yyyy'),'dd/mm/yyyy') >= ");
				buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
				buf.append(") ");
			}
			if (strDataA != null && !strDataA.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (to_date(to_char(DATDOMANDA, 'dd/mm/yyyy'),'dd/mm/yyyy') <= ");
				buf.append("to_date('" + strDataA + "','dd/mm/yyyy')");
				buf.append(") ");
			}
		}
		if ((strDataRendDa != null && !strDataRendDa.equals(""))
				&& (strDataRendA != null && !strDataRendA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (to_date(to_char(DATRENDICONTAZIONE, 'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN ");
			buf.append("to_date('" + strDataRendDa + "','dd/mm/yyyy')");
			buf.append(" and ");
			buf.append("to_date('" + strDataRendA + "','dd/mm/yyyy')");
			buf.append(") ");
		} else {
			if (strDataRendDa != null && !strDataRendDa.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (to_date(to_char(DATRENDICONTAZIONE, 'dd/mm/yyyy'),'dd/mm/yyyy') >= ");
				buf.append("to_date('" + strDataRendDa + "','dd/mm/yyyy')");
				buf.append(") ");
			}
			if (strDataRendA != null && !strDataRendA.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (to_date(to_char(DATRENDICONTAZIONE, 'dd/mm/yyyy'),'dd/mm/yyyy') <= ");
				buf.append("to_date('" + strDataRendA + "','dd/mm/yyyy')");
				buf.append(") ");
			}
		}
		if ((strDataRicSILDa != null && !strDataRicSILDa.equals(""))
				&& (strDataRicSILA != null && !strDataRicSILA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (to_date(to_char(DTMINS, 'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN ");
			buf.append("to_date('" + strDataRicSILDa + "','dd/mm/yyyy')");
			buf.append(" and ");
			buf.append("to_date('" + strDataRicSILA + "','dd/mm/yyyy')");
			buf.append(") ");
		} else {
			if (strDataRicSILDa != null && !strDataRicSILDa.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (to_date(to_char(DTMINS, 'dd/mm/yyyy'),'dd/mm/yyyy') >= ");
				buf.append("to_date('" + strDataRicSILDa + "','dd/mm/yyyy')");
				buf.append(") ");
			}
			if (strDataRicSILA != null && !strDataRicSILA.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (to_date(to_char(DTMINS, 'dd/mm/yyyy'),'dd/mm/yyyy') <= ");
				buf.append("to_date('" + strDataRicSILA + "','dd/mm/yyyy')");
				buf.append(") ");
			}
		}
		if ((codComune != null) && (!codComune.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" CODCOMRES ='" + codComune + "'");
		}
		if ((codMonoRuolo != null) && (!codMonoRuolo.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" CODMONORUOLO = '" + codMonoRuolo + "'");
		}
		if ((codStatoDomanda != null) && (!codStatoDomanda.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			if (codStatoDomanda.equalsIgnoreCase("AC")) {
				buf.append("( upper(CODSTATOINPS) = 'AC' OR  upper(CODSTATOINPS) = 'ACCOLTO' )");
			} else if (codStatoDomanda.equalsIgnoreCase("OT")) {
				buf.append("( upper(CODSTATOINPS) != 'AC' AND  upper(CODSTATOINPS) != 'ACCOLTO' )");
			}
		}
		if ((codTipoDomanda != null) && (!codTipoDomanda.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append("( upper(CODTIPODOMANDA) = upper('" + codTipoDomanda + "') )");
		}
		if ((ordDatRend == null || ordDatRend.equals("")) && (ordNucleo == null || ordNucleo.equals(""))
				&& (ordDataDom == null || ordDataDom.equals(""))) {
			buf.append(" ORDER BY DATDOMANDA desc, STRCODICEFISCALE ASC, PRGRDC DESC");
		} else {
			StringBuffer bufOrd = new StringBuffer();

			buf.append(" ORDER BY ");
			if (ordDatRend != null && !ordDatRend.equals("")) {
				bufOrd.append(" DATRENDICONTAZIONE ");
				if (ordDatRendDC.equalsIgnoreCase("D"))
					bufOrd.append("DESC");
				else
					bufOrd.append("ASC");
			}
			if (ordNucleo != null && !ordNucleo.equals("")) {
				if (bufOrd.length() > 0) {
					bufOrd.append(",");
				}
				bufOrd.append(" STRPROTOCOLLOINPS ");
				if (ordNucleoDC.equalsIgnoreCase("D"))
					bufOrd.append("DESC");
				else
					bufOrd.append("ASC");
			}
			if (ordDataDom != null && !ordDataDom.equals("")) {
				if (bufOrd.length() > 0) {
					bufOrd.append(",");
				}
				bufOrd.append(" DATDOMANDA ");
				if (ordDataDomDC.equalsIgnoreCase("D"))
					bufOrd.append("DESC");
				else
					bufOrd.append("ASC");
			}
			/*
			 * if( ordDataRicSIL != null && !ordDataRicSIL.equals("")){ if (bufOrd.length() > 0) { bufOrd.append(","); }
			 * bufOrd.append(" DTMINS "); if(ordDataRicSILDC.equalsIgnoreCase("D")) bufOrd.append("DESC"); else
			 * bufOrd.append("ASC"); }
			 */

			buf.append(bufOrd);
		}

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
