package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la selezione degli elementi della combo dei tipi di assunzione in base al tipo dell'azienda, natura
 * giuridica dell'azienda e tipo di contratto (determinato o indeterminato).
 */
public class DynSelectDiagnosi implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT df.CDNLAVORATORE, " + "        l.STRCODICEFISCALE, "
			+ "        l.STRCOGNOME, " + "        l.STRNOME, " + "	     df.PRGDIAGNOSIFUNZIONALE, "
			+ "        to_char(df.DATDIAGNOSI,'dd/mm/yyyy') as DATDIAGNOSI, " + "	     df.CODAZIENDAASL, "
			+ "        PG_UTILS.TRUNC_DESC(df.STRGIUDIZIODIAGNOSTICO,200,'...') as STRGIUDIZIODIAGNOSTICO ";

	/*
	 * public String getStatement(SourceBean req, SourceBean response) { //SourceBean req =
	 * requestContainer.getServiceRequest();
	 * 
	 * //SessionContainer s = SessionContainer
	 * 
	 * String nome = (String) req.getAttribute("strNome"); String cognome = (String) req.getAttribute("strCognome");
	 * String cf = (String) req.getAttribute("strCodiceFiscale"); String datDiagnosiIn = (String)
	 * req.getAttribute("datDiagnosiIn"); String datDiagnosiFin = (String) req.getAttribute("datDiagnosiFin"); String
	 * codAziendaAsl = (String) req.getAttribute("codAziendaAsl"); String tipoRic =
	 * StringUtils.getAttributeStrNotNull(req,"tipoRicerca"); String codCPIComp =
	 * StringUtils.getAttributeStrNotNull(req,"codCPIComp");
	 * 
	 * StringBuffer query_totale=new StringBuffer(SELECT_SQL_BASE); StringBuffer buf = new StringBuffer();
	 * 
	 * if(tipoRic.equalsIgnoreCase("esatta")) { if ( (nome!=null) && (!nome.equals("")) ) { nome =
	 * StringUtils.replace(nome, "'", "''");
	 * 
	 * buf.append(" AND"); buf.append(" upper(strnome) = '" + nome.toUpperCase() + "'"); }
	 * 
	 * if ( (cognome!=null) && (!cognome.equals("")) ) { buf.append(" AND"); cognome = StringUtils.replace(cognome, "'",
	 * "''"); buf.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'"); }
	 * 
	 * if ( (cf!=null) && (!cf.equals("")) ) { buf.append(" AND"); buf.append(" upper(strCodiceFiscale) =
	 * '" + cf.toUpperCase() + "'"); } } else { if ( (nome!=null) && (!nome.equals("")) ) { nome =
	 * StringUtils.replace(nome, "'", "''"); buf.append(" AND"); buf.append(" upper(strnome) like '" +
	 * nome.toUpperCase() + "%'"); }
	 * 
	 * if ( (cognome!=null) && (!cognome.equals("")) ) { buf.append(" AND"); cognome = StringUtils.replace(cognome, "'",
	 * "''"); buf.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'"); }
	 * 
	 * if ( (cf!=null) && (!cf.equals("")) ) { buf.append(" AND"); buf.append(" upper(strCodiceFiscale) like
	 * '" + cf.toUpperCase() + "%'"); } }
	 * 
	 * if ( ((datDiagnosiIn!=null) && (!datDiagnosiIn.equals(""))) && ((datDiagnosiFin==null) ||
	 * (datDiagnosiFin.equals(""))) ) { buf.append(" AND"); buf.append(" TO_DATE('"+datDiagnosiIn+"','DD/MM/YYYY') ");
	 * buf.append(" <= "); buf.append(" df.DATDIAGNOSI "); }
	 * 
	 * if ( ((datDiagnosiFin!=null) && (!datDiagnosiFin.equals(""))) && ((datDiagnosiIn==null) ||
	 * (datDiagnosiIn.equals(""))) ) { buf.append(" AND"); buf.append(" TO_DATE('"+datDiagnosiFin+"','DD/MM/YYYY') ");
	 * buf.append(" >= "); buf.append(" df.DATDIAGNOSI "); }
	 * 
	 * if ( ((datDiagnosiFin!=null) && (!datDiagnosiFin.equals(""))) && ((datDiagnosiIn!=null) &&
	 * (!datDiagnosiIn.equals(""))) ) { buf.append(" AND"); buf.append(" TO_DATE('"+datDiagnosiFin+"','DD/MM/YYYY') ");
	 * buf.append(" >= "); buf.append(" df.DATDIAGNOSI "); buf.append(" AND");
	 * buf.append(" TO_DATE('"+datDiagnosiIn+"','DD/MM/YYYY') "); buf.append(" <= "); buf.append(" df.DATDIAGNOSI "); }
	 * 
	 * if ( ( codAziendaAsl != null) && (!codAziendaAsl.equals("")) ) { buf.append(" AND");
	 * buf.append(" df.codAziendaAsl='"+codAziendaAsl+"'"); }
	 * 
	 * if ( ( codCPIComp != null) && (!codCPIComp.equals("")) ) { buf.append(" AND"); buf.append("
	 * lav.CODCPITIT='"+codCPIComp+"' "); }
	 * 
	 * buf.append(" ORDER BY UPPER (l.strcognome) ASC, UPPER (l.strnome) ASC, UPPER (l.strcodicefiscale) ASC,
	 * df.DATDIAGNOSI DESC "); query_totale.append(buf.toString()); return query_totale.toString(); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.eng.afExt.dbaccess.sql.IDynamicStatementProvider#getStatement(com.engiweb.framework.base.RequestContainer,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer session = requestContainer.getSessionContainer();

		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String nome = (String) req.getAttribute("strNome");
		String cognome = (String) req.getAttribute("strCognome");
		String cf = (String) req.getAttribute("strCodiceFiscale");
		String datDiagnosiIn = (String) req.getAttribute("datDiagnosiIn");
		String datDiagnosiFin = (String) req.getAttribute("datDiagnosiFin");
		String codAziendaAsl = (String) req.getAttribute("codAziendaAsl");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codCPIComp = StringUtils.getAttributeStrNotNull(req, "codCPIComp");
		String invalidFisica = (String) req.getAttribute("invalidFisica");
		String invalidPsichica = (String) req.getAttribute("invalidPsichica");
		String invalidIntelletiva = (String) req.getAttribute("invalidIntelletiva");
		String invalidSensoriale = (String) req.getAttribute("invalidSensoriale");
		String iscrizL68Aperte = (String) req.getAttribute("iscrizL68Aperte");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// aggiungo la clausola where per il lavoartore cryptato
		buf.append(
				" from an_lavoratore L inner join  CM_DIAGNOSI_FUNZIONALE DF on (L.CDNLAVORATORE = DECRYPT(df.CDNLAVORATORE, '"
						+ encryptKey + "')) ");
		buf.append(" inner join AN_LAV_STORIA_INF LAV  on (L.CDNLAVORATORE = LAV.CDNLAVORATORE ) ");
		if ((iscrizL68Aperte != null) && (iscrizL68Aperte.equals("on"))) {
			buf.append(" inner join (select distinct ISCR.CDNLAVORATORE from AM_CM_ISCR ISCR ");
			buf.append(
					" inner join DE_CM_TIPO_ISCR TIPO_ISCR on (ISCR.CODCMTIPOISCR = TIPO_ISCR.CODCMTIPOISCR and TIPO_ISCR.CODMONOTIPORAGG = 'D') ");
			buf.append(
					" where ISCR.DATDATAFINE is null and ISCR.CODSTATOATTO = 'PR') ISCRIZ on (DECRYPT(ISCRIZ.CDNLAVORATORE, 'chiaveCM') = L.CDNLAVORATORE) ");
		}
		buf.append(" where lav.DATFINE is null ");

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
		}

		if (((datDiagnosiIn != null) && (!datDiagnosiIn.equals("")))
				&& ((datDiagnosiFin == null) || (datDiagnosiFin.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + datDiagnosiIn + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" df.DATDIAGNOSI ");
		}

		if (((datDiagnosiFin != null) && (!datDiagnosiFin.equals("")))
				&& ((datDiagnosiIn == null) || (datDiagnosiIn.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + datDiagnosiFin + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" df.DATDIAGNOSI ");
		}

		if (((datDiagnosiFin != null) && (!datDiagnosiFin.equals("")))
				&& ((datDiagnosiIn != null) && (!datDiagnosiIn.equals("")))) {
			buf.append(" AND");
			buf.append(" TO_DATE('" + datDiagnosiFin + "','DD/MM/YYYY') ");
			buf.append(" >= ");
			buf.append(" df.DATDIAGNOSI ");
			buf.append(" AND");
			buf.append(" TO_DATE('" + datDiagnosiIn + "','DD/MM/YYYY') ");
			buf.append(" <= ");
			buf.append(" df.DATDIAGNOSI ");
		}

		if ((codAziendaAsl != null) && (!codAziendaAsl.equals(""))) {
			buf.append(" AND");
			buf.append(" df.codAziendaAsl='" + codAziendaAsl + "'");
		}

		if ((codCPIComp != null) && (!codCPIComp.equals(""))) {
			buf.append(" AND");
			buf.append(" lav.CODCPITIT='" + codCPIComp + "' ");
			buf.append(" AND");
			buf.append(" lav.CODMONOTIPOCPI = 'C' ");
		}
		if ((invalidFisica != null) && (invalidFisica.equals("on"))) {
			buf.append(" AND");
			buf.append(" df.FLGINVALIDFISICA ='S' ");
		}
		if ((invalidPsichica != null) && (invalidPsichica.equals("on"))) {
			buf.append(" AND");
			buf.append(" df.FLGINVALIDPSICHICA ='S' ");
		}
		if ((invalidIntelletiva != null) && (invalidIntelletiva.equals("on"))) {
			buf.append(" AND");
			buf.append(" df.FLGINVALIDINTELLETTIVA ='S' ");
		}
		if ((invalidSensoriale != null) && (invalidSensoriale.equals("on"))) {
			buf.append(" AND");
			buf.append(" df.FLGINVALIDSENSORIALE ='S' ");
		}

		buf.append(
				" ORDER BY UPPER (l.strcognome) ASC, UPPER (l.strnome) ASC, UPPER (l.strcodicefiscale) ASC, df.DATDIAGNOSI DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}
