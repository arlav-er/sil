package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class RicercaUnitaAziendaNew implements IDynamicStatementProvider {
	public RicercaUnitaAziendaNew() {
	}

	private static final String SELECT_SQL_BASE = "select " + "UAZ.PRGAZIENDA PRGAZIENDA, " + "UAZ.PRGUNITA PRGUNITA, "
			+ "InitCap(AZ.STRRAGIONESOCIALE) as strRagioneSociale, " + "AZ.STRPARTITAIVA STRPARTITAIVA, "
			+ "AZ.STRCODICEFISCALE STRCODICEFISCALE, " + "UAZ.STRINDIRIZZO STRINDIRIZZO, "
			+ "InitCap(COM.STRDENOMINAZIONE) as comune_az, " + "COM.codcom CODCOM, " + "PROV.strIstat prov_az,"
			+ "AZ.CODTIPOAZIENDA codTipoAzienda, " + "DETIPOAZ.strDescrizione descrTipoAz, "
			+ "NATGIUR.strDescrizione natGiurAz, " + "AZ.STRNUMALBOINTERINALI STRNUMALBOINTERINALI, "
			+ "UAZ.STRNUMREGISTROCOMMITT STRNUMREGISTROCOMMITT, " + "UAZ.STRTEL STRTEL, " + "UAZ.STRFAX STRFAX, "
			+ "COM.STRCAP STRCAP, " + "DECCNL.STRDESCRIZIONE STRDESCRIZIONECCNL, " + "UAZ.CODCCNL CCNLAZ, "
			+ "UAZ.CODATECO codAteco, " + "DEATT.STRDESCRIZIONE strDesAtecoUAz, att2.strDescrizione as TIPOATECO, "
			+ "AZ.STRPATINAIL strPatInail, UAZ.STRNUMEROINPS strNumeroInps, "
			+ "AZ.FLGDATIOK, AZ.CODNATGIURIDICA CODNATGIURIDICA, "
			+ "UAZ.STRRIFERIMENTOSARE STRREFERENTE, UAZ.STRCFAZESTERA CODFISCAZESTERA, "
			+ "InitCap(UAZ.STRRAGSOCAZESTERA) as RAGSOCAZESTERA, " + "ST.CODCPITIT CODCPI, "
			+ "PROV.CODREGIONE CODREGIONE " + "FROM AN_AZIENDA AZ, " + "AN_UNITA_AZIENDA UAZ, " + "DE_COMUNE COM, "
			+ "DE_PROVINCIA PROV, " + "DE_CONTRATTO_COLLETTIVO DECCNL, " + "DE_TIPO_AZIENDA DETIPOAZ, "
			+ "DE_ATTIVITA DEATT, " + " de_attivita att2, " + "DE_NAT_GIURIDICA NATGIUR, " + "AN_UA_STORIA_INF ST "
			+ "WHERE AZ.prgAzienda = UAZ.prgAzienda " + " AND UAZ.prgAzienda = ST.prgAzienda (+) "
			+ " AND UAZ.prgUnita = ST.prgUnita (+) " + " AND UAZ.codCom = COM.codCom (+) "
			+ " AND COM.codProvincia = PROV.codProvincia (+) " + " AND UAZ.CODCCNL = DECCNL.CODCCNL (+) "
			+ " AND AZ.codTipoAzienda = DETIPOAZ.CODTIPOAZIENDA (+) " + " AND UAZ.CODATECO = DEATT.CODATECO (+) "
			+ " AND AZ.CODNATGIURIDICA = NATGIUR.CODNATGIURIDICA (+) " + " AND att2.codateco (+) = DEATT.codpadre ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("cf");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = "";

		if (cf != null && !cf.equals("")) {
			buf += " AND upper(AZ.STRCODICEFISCALE) = '" + cf + "'";
		}

		if (!buf.equals("")) {
			buf += " order by az.strCodicefiscale, uaz.codcom, uaz.strIndirizzo";
			query_totale.append(buf);
		}
		return query_totale.toString();

	}
}
