package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

/**
 * Questa classe restituisce la query per la ricerca delle aziende con pulsante di lookup
 * <p>
 * 
 * @author: Paolo Roccetti
 * 
 */

public class RicercaTestateAzienda implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaTestateAzienda.class.getName());

	public RicercaTestateAzienda() {
	}

	private static final String SELECT_SQL_BASE_2 = "select " + "UAZ.PRGAZIENDA PRGAZIENDA, "
			+ "UAZ.PRGUNITA PRGUNITA, " + "InitCap(AZ.STRRAGIONESOCIALE) as strRagioneSociale, "
			+ "AZ.STRPARTITAIVA STRPARTITAIVA, " + "AZ.STRCODICEFISCALE STRCODICEFISCALE, "
			+ "UAZ.STRCFAZESTERA CODFISCAZESTERA, InitCap(UAZ.STRRAGSOCAZESTERA) as RAGSOCAZESTERA, "
			+ "UAZ.STRINDIRIZZO STRINDIRIZZO, " + "InitCap(COM.STRDENOMINAZIONE) as comune_az, " + "COM.codcom CODCOM, "
			+ "PROV.strIstat prov_az," + "AZ.CODTIPOAZIENDA codTipoAzienda, " + "DETIPOAZ.strDescrizione descrTipoAz, "
			+ "NATGIUR.strDescrizione natGiurAz, " + "AZ.STRNUMALBOINTERINALI STRNUMALBOINTERINALI, "
			+ "UAZ.STRNUMREGISTROCOMMITT STRNUMREGISTROCOMMITT, " + "UAZ.STRTEL STRTEL, " + "UAZ.STRFAX STRFAX, "
			+ "COM.STRCAP STRCAP, " + "DECCNL.STRDESCRIZIONE STRDESCRIZIONECCNL, " + "UAZ.CODCCNL CCNLAZ, "
			+ "UAZ.CODATECO codAteco, " + "DEATT.STRDESCRIZIONE strDesAtecoUAz, att2.strDescrizione as TIPOATECO, "
			+ "AZ.STRPATINAIL strPatInail, UAZ.STRNUMEROINPS strNumeroInps, "
			+ "AZ.FLGDATIOK, AZ.CODNATGIURIDICA CODNATGIURIDICA, " + "UAZ.STRRIFERIMENTOSARE STRREFERENTE, "
			+ "ST.CODCPITIT CODCPI " + "FROM AN_AZIENDA AZ, " + "AN_UNITA_AZIENDA UAZ, " + "DE_COMUNE COM, "
			+ "DE_PROVINCIA PROV, " + "DE_CONTRATTO_COLLETTIVO DECCNL, " + "DE_TIPO_AZIENDA DETIPOAZ, "
			+ "DE_ATTIVITA DEATT, " + " de_attivita att2, " + "DE_NAT_GIURIDICA NATGIUR, " + "AN_UA_STORIA_INF ST "
			+ "WHERE AZ.prgAzienda = UAZ.prgAzienda " + " AND UAZ.prgAzienda = ST.prgAzienda "
			+ " AND UAZ.prgUnita = ST.prgUnita " + " AND UAZ.codCom = COM.codCom (+) "
			+ " AND COM.codProvincia = PROV.codProvincia (+) " + " AND UAZ.CODCCNL = DECCNL.CODCCNL (+) "
			+ " AND AZ.codTipoAzienda = DETIPOAZ.CODTIPOAZIENDA (+) " + " AND UAZ.CODATECO = DEATT.CODATECO (+) "
			+ " AND AZ.CODNATGIURIDICA = NATGIUR.CODNATGIURIDICA (+) " + " AND att2.codateco(+)=DEATT.codpadre ";

	private static final String SELECT_SQL_BASE = "select " + "AZ.PRGAZIENDA PRGAZIENDA, "
			+ "InitCap(AZ.STRRAGIONESOCIALE) as strRagioneSociale, " + "AZ.STRPARTITAIVA STRPARTITAIVA, "
			+ "AZ.STRCODICEFISCALE STRCODICEFISCALE, " + "AZ.CODTIPOAZIENDA codTipoAzienda, "
			+ "DETIPOAZ.strDescrizione descrTipoAz, " + "NATGIUR.strDescrizione natGiurAz, "
			+ "AZ.STRNUMALBOINTERINALI STRNUMALBOINTERINALI, "
			// + "DECCNL.STRDESCRIZIONE STRDESCRIZIONECCNL, "
			// + "AZ.CODCCNL CCNLAZ, "
			+ "AZ.STRPATINAIL strPatInail, " + "AZ.FLGDATIOK, " + "AZ.CODNATGIURIDICA CODNATGIURIDICA "
			+ "FROM AN_AZIENDA AZ, "
			// + "DE_CONTRATTO_COLLETTIVO DECCNL, "
			+ "DE_TIPO_AZIENDA DETIPOAZ, " + "DE_NAT_GIURIDICA NATGIUR " + "WHERE " // UAZ.CODCCNL = DECCNL.CODCCNL (+)
																					// "
			+ " AZ.codTipoAzienda = DETIPOAZ.CODTIPOAZIENDA (+) "
			+ " AND AZ.CODNATGIURIDICA = NATGIUR.CODNATGIURIDICA (+) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String ragioneSociale = SourceBeanUtils.getAttrStrAvoidInjection(req, "RagioneSociale");
		String piva = SourceBeanUtils.getAttrStrAvoidInjection(req, "piva");
		String cf = SourceBeanUtils.getAttrStrAvoidInjection(req, "cf");

		String tipoAz = (String) req.getAttribute("codTipoAzienda");
		String prgAz = (String) req.getAttribute("PRGAZ");
		String codComune = (String) req.getAttribute("codComune");
		String naturaAz = (String) req.getAttribute("codNatGiuridica");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = "";

		if (prgAz != null && !prgAz.equals("")) {
			buf += " AND AZ.PRGAZIENDA= " + prgAz + " ";
		}

		if (!ragioneSociale.equals("")) {
			buf += " AND upper(AZ.STRRAGIONESOCIALE) like upper('%" + ragioneSociale + "%')";
		}

		if (!piva.equals("")) {
			buf += " AND AZ.STRPARTITAIVA like '" + piva + "%'";
		}

		if (!cf.equals("")) {
			buf += " AND upper(AZ.STRCODICEFISCALE) like upper('" + cf + "%')";
		}

		if ((tipoAz != null) && (!tipoAz.equals(""))) {
			buf += " AND AZ.CODTIPOAZIENDA = '" + tipoAz + "'";
		}

		if ((codComune != null) && (!codComune.equals(""))) {
			buf += " AND UAZ.codCom = '" + codComune + "'";
		}

		if ((naturaAz != null) && (!naturaAz.equals(""))) {
			buf += " AND AZ.CODNATGIURIDICA = '" + naturaAz + "'";
		}

		if (!buf.equals("")) {
			buf += " order by az.strragionesociale, az.strCodicefiscale, az.strPartitaiva";
			query_totale.append(buf);
		}

		// Debug
		_logger.debug("sil.module.movimenti.RicercaTestateAzienda" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}