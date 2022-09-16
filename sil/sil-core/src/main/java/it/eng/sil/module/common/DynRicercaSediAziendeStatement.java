package it.eng.sil.module.common;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di una azienda dati: - o la partita iva - o il codice fiscale - o la ragione sociale - o
 * nessuna delle precedenti (restituisce TUTTO)
 * 
 * @author Alessio Rolfini
 * 
 */
public class DynRicercaSediAziendeStatement implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select "
			+ "az.prgAzienda, auz.prgUnita, decode(auz.flgsede,'S', 'SI', 'N', 'NO', 'NO') as flgsede, az.strCodiceFiscale, az.strPartitaIva, az.strRagioneSociale, "
			+ "tipoaz.strDescrizione as desTipoAzienda, "
			+ "decode(auz.strIndirizzo, '', '', auz.strIndirizzo ||'  ' || VW_INDIRIZZI_COM_PROV.DENOMINAZIONECOMUNE || nvl(' (' || VW_INDIRIZZI_COM_PROV.DENOMINAZIONEPROV ||') ', VW_INDIRIZZI_COM_PROV.CODPROVINCIA))  as strIndirizzo, "
			+ "VW_INDIRIZZI_COM_PROV.DENOMINAZIONEPROV as provIstat, stato.strDescrizione desStato "
			+ "FROM AN_AZIENDA az " + "INNER JOIN AN_UNITA_AZIENDA auz on az.prgAzienda=auz.prgAzienda "
			+ "LEFT JOIN VW_INDIRIZZI_COM_PROV on VW_INDIRIZZI_COM_PROV.codcom = auz.codcom "
			+ "LEFT JOIN DE_TIPO_AZIENDA tipoaz on tipoaz.codTipoAzienda=az.codTipoAzienda "
			+ "LEFT JOIN DE_AZ_STATO stato on stato.codAzStato=auz.codAzStato ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("cf");
		String pi = (String) req.getAttribute("piva");
		String ragsoc = (String) req.getAttribute("RagioneSociale");
		String codTipoAzienda = (String) req.getAttribute("codTipoAzienda");
		String naturaAz = (String) req.getAttribute("codNatGiuridica");

		/*
		 * Parametri non richiesti (per ora) String codAzStato =(String) req.getAttribute("codAzStato"); String
		 * codProvincia =(String) req.getAttribute("codProvincia"); String codComAz =(String)
		 * req.getAttribute("codComAz"); String indirizzo =(String) req.getAttribute("indirizzo"); String CPI =(String)
		 * req.getAttribute("codCPI"); String flgSedeLegale =(String) req.getAttribute("flgSedeLegale"); String
		 * strDenominazioneAz = (String) req.getAttribute("strDenominazioneAz");
		 */

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((ragsoc != null) && (!ragsoc.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}
			ragsoc = StringUtils.replace(ragsoc, "'", "''");
			buf.append(" lower(az.strRagioneSociale)  like ('%" + ragsoc.toLowerCase() + "%')");
		}

		if ((pi != null) && (!pi.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}
			buf.append(" az.strPartitaIva like '" + pi + "%'");
		}

		if ((cf != null) && (!cf.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(az.strcodicefiscale) like ('" + cf.toUpperCase() + "%')");
		}

		if ((codTipoAzienda != null) && (!codTipoAzienda.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}
			buf.append(" az.codTipoAzienda = '" + codTipoAzienda + "'");
		}

		if ((naturaAz != null) && (!naturaAz.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}
			buf.append(" az.CODNATGIURIDICA = '" + naturaAz + "'");
		}

		/*
		 * parametri non richiesti (per ora)
		 * 
		 * if ( (codAzStato!=null) && (!codAzStato.equals("")) ) { if (buf.length()==0){buf.append(" WHERE ");} else
		 * {buf.append(" AND");} buf.append(" auz.codAzStato = '" + codAzStato + "'"); }
		 * 
		 * 
		 * if ( (codProvincia!=null) && (!codProvincia.equals("")) ) { if (buf.length()==0){buf.append(" WHERE ");} else
		 * {buf.append(" AND");} buf.append(" VW_INDIRIZZI_COM_PROV.codProvincia = '" + codProvincia + "'"); }
		 * 
		 * if ( (codComAz!=null) && !codComAz.equals("") ) { if (buf.length()==0){buf.append(" WHERE ");} else
		 * {buf.append(" AND");} buf.append(" UPPER(VW_INDIRIZZI_COM_PROV.codcom) = ('" + codComAz.toUpperCase() +
		 * "')"); }
		 * 
		 * if ( (indirizzo!=null) && !indirizzo.equals("") ) { if (buf.length()==0){buf.append(" WHERE ");} else
		 * {buf.append(" AND");} indirizzo = StringUtils.replace(indirizzo, "'", "''"); buf.append("
		 * UPPER(auz.strIndirizzo) LIKE ('%" + indirizzo.toUpperCase() + "%')"); }
		 * 
		 * if ( (CPI!=null) && !CPI.equals("") ) { if (buf.length()==0){buf.append(" WHERE ");} else
		 * {buf.append(" AND");} buf.append(" VW_INDIRIZZI_COM_PROV.CODCPI = '" + CPI + "'"); }
		 * 
		 * 
		 * 
		 * if ( (flgSedeLegale!=null) && flgSedeLegale.equals("on") ) { if (buf.length()==0) {buf.append(" WHERE ");}
		 * else {buf.append(" AND");} buf.append(" auz.flgsede like 'S' "); }
		 * 
		 * if ( (strDenominazioneAz!=null) && (!strDenominazioneAz.equals("")) ) { if
		 * (buf.length()==0){buf.append(" WHERE ");} else {buf.append(" AND");} strDenominazioneAz =
		 * StringUtils.replace(strDenominazioneAz, "'", "''"); buf.append(" lower(auz.strDenominazione) like ('%" +
		 * strDenominazioneAz.toLowerCase() + "%')"); }
		 */

		buf.append(" order by az.strCodiceFiscale,auz.codcom,auz.strIndirizzo");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}