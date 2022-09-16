package it.eng.sil.module.delega;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class ListaPratichePatronatoDynStmt implements IDynamicStatementProvider {

	private static String ELENCO = "elenco";
	private static String CONTEGGIO = "conteggio";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean serviceRequest = requestContainer.getServiceRequest();

		String datInizio = StringUtils.getAttributeStrNotNull(serviceRequest, "datInizio");
		String datFine = StringUtils.getAttributeStrNotNull(serviceRequest, "datFine");
		String flgDid = StringUtils.getAttributeStrNotNull(serviceRequest, "flgDid");
		String flgDomandeMobilita = StringUtils.getAttributeStrNotNull(serviceRequest, "flgDomandeMobilita");
		String patronato = StringUtils.getAttributeStrNotNull(serviceRequest, "patronato");
		String cdnTipoPatronato = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnTipoPatronato");
		String cdnUfficioPatronato = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnUfficioPatronato");
		String cdnOperatorePatronato = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnOperatorePatronato");
		String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");

		// tipo pratica
		boolean isFlagDid = flgDid.equals("S");
		boolean isFlagDomandeMobilita = flgDomandeMobilita.equals("S");

		// patronato
		boolean isOperatorePatronatoSel = patronato.equalsIgnoreCase("flgOperatorePatronato");
		boolean isUfficioPatronatoSel = patronato.equalsIgnoreCase("flgUfficioPatronato");
		boolean isTipoPatronatoSel = patronato.equalsIgnoreCase("flgTipoPatronato");

		// TODO: 1 eventuali controlli sulla data? se assente? se range errato?
		// TODO: 2 eventuali controlli su flgDid e flgDomandaMobilita entrambi FALSE?
		// TODO: 3 eventuali controlli su tipo/ufficio/operatore patronato nel caso in cui sia selezionata la radio
		// button, ma non sia stato inserito un valore!

		if (!isFlagDid && !isFlagDomandeMobilita) {

			// TODO: 2

		}

		StringBuffer query = new StringBuffer("");

		query.append(" SELECT");

		if (tipoRicerca.equalsIgnoreCase(ELENCO)) {
			query.append(" * ");
		}

		if (tipoRicerca.equalsIgnoreCase(CONTEGGIO)) {
			query.append(" COUNT(*) AS NUMERO_PRATICHE, TIPO_PRATICA, PATRONATO ");
		}

		query.append(" FROM (");

		if (isFlagDid) {
			query.append(" SELECT ");
			query.append("         amdd.dtmIns as DATA_PRATICA,");
			query.append("         TO_CHAR(amdd.dtmIns,'DD/MM/YYYY') as DATA_PRATICA_STR,");
			query.append("         'DID' as TIPO_PRATICA, ");
			query.append("        tstg.strDenominazione as PATRONATO,");
			query.append("        tsg.strDenominazione as UFFICIO,");
			query.append("        anl.strCodiceFiscale as CF_LAVORATORE,");
			query.append("        anl.strCognome || ' ' || anl.strNome AS LAVORATORE");
			query.append("  FROM  ");
			query.append("        am_dich_disponibilita amdd,");
			query.append("        AM_elenco_anagrafico amea,");
			query.append("        AN_lavoratore anl,");
			query.append("        ts_utente tsu,");
			query.append("        ts_profilatura_utente tspu,");
			query.append("        ts_gruppo tsg,");
			query.append("        ts_tipo_gruppo tstg");
			query.append("  WHERE");
			query.append("        tstg.cdntipogruppo = tsg.cdntipogruppo");
			query.append("    AND tsg.cdngruppo = tspu.cdngruppo");
			query.append("    AND tspu.cdnut = tsu.cdnut");
			query.append("    AND tsu.cdnut = amdd.cdnutins");
			query.append("    AND amdd.codstatoatto = 'PR'");
			query.append("    AND amdd.prgelencoanagrafico = amea.prgelencoanagrafico");
			query.append("    AND amea.cdnlavoratore = anl.cdnlavoratore");
			query.append("    AND tstg.codtipo = 'P'");

			if (!datInizio.equals("")) {
				query.append("    AND TO_DATE(TO_CHAR(amdd.dtmIns,'DD/MM/YYYY'),'DD/MM/YYYY') >= TO_DATE('" + datInizio
						+ "','DD/MM/YYYY')");
			}

			if (!datFine.equals("")) {
				query.append("    AND TO_DATE(TO_CHAR(amdd.dtmIns,'DD/MM/YYYY'),'DD/MM/YYYY') <= TO_DATE('" + datFine
						+ "','DD/MM/YYYY')");
			}

			if (isOperatorePatronatoSel && !cdnOperatorePatronato.equals("")) {
				query.append("    AND tsu.cdnut = '" + cdnOperatorePatronato + "'");
			}

			if (isUfficioPatronatoSel && !cdnUfficioPatronato.equals("")) {
				query.append("    AND tsg.cdngruppo = '" + cdnUfficioPatronato + "'");
			}

			if (isTipoPatronatoSel && !cdnTipoPatronato.equals("")) {
				query.append("    AND tstg.cdnTipoGruppo = '" + cdnTipoPatronato + "'");
			}

		}

		if (isFlagDid && isFlagDomandeMobilita) {
			query.append("  union");
		}

		if (isFlagDomandeMobilita) {

			query.append("  SELECT");
			query.append("        ami.dtmIns as DATA_PRATICA,");
			query.append("        TO_CHAR(ami.dtmIns,'DD/MM/YYYY') as DATA_PRATICA_STR,");
			query.append("        'Domanda di mobilità' as TIPO_PRATICA,");
			query.append("        tstg.strDenominazione as PATRONATO,");
			query.append("        tsg.strDenominazione as UFFICIO,");
			query.append("        anl.strCodiceFiscale as CF_LAVORATORE,");
			query.append("        anl.strCognome || ' ' || anl.strNome AS LAVORATORE");
			query.append("  FROM  ");
			query.append("        am_mobilita_iscr ami,");
			query.append("        an_lavoratore anl,");
			query.append("        ts_utente tsu,");
			query.append("        ts_profilatura_utente tspu,");
			query.append("        ts_gruppo tsg,");
			query.append("        ts_tipo_gruppo tstg");
			query.append("  WHERE");
			query.append("        tstg.cdntipogruppo = tsg.cdntipogruppo");
			query.append("    AND tsg.cdngruppo = tspu.cdngruppo");
			query.append("    AND tspu.cdnut = tsu.cdnut");
			query.append("    AND tsu.cdnut = ami.cdnutins");
			query.append("    AND ami.cdnlavoratore = anl.cdnlavoratore");
			query.append("    AND tstg.codtipo = 'P'");

			if (!datInizio.equals("")) {
				query.append("    AND TO_DATE(TO_CHAR(ami.dtmIns,'DD/MM/YYYY'),'DD/MM/YYYY') >= TO_DATE('" + datInizio
						+ "','DD/MM/YYYY')");
			}

			if (!datFine.equals("")) {
				query.append("    AND TO_DATE(TO_CHAR(ami.dtmIns,'DD/MM/YYYY'),'DD/MM/YYYY') <= TO_DATE('" + datFine
						+ "','DD/MM/YYYY')");
			}

			if (isOperatorePatronatoSel && !cdnOperatorePatronato.equals("")) {
				query.append("    AND tsu.cdnut = '" + cdnOperatorePatronato + "'");
			}

			if (isUfficioPatronatoSel && !cdnUfficioPatronato.equals("")) {
				query.append("    AND tsg.cdngruppo = '" + cdnUfficioPatronato + "'");
			}

			if (isTipoPatronatoSel && !cdnTipoPatronato.equals("")) {
				query.append("    AND tstg.cdnTipoGruppo = '" + cdnTipoPatronato + "'");
			}

		}

		query.append(" ) ");

		if (tipoRicerca.equalsIgnoreCase(CONTEGGIO)) {

			query.append(" GROUP BY");
			query.append("  PATRONATO,");
			query.append("  TIPO_PRATICA");

			query.append(" ORDER BY");
			query.append("  PATRONATO,");
			query.append("  TIPO_PRATICA");

		}

		if (tipoRicerca.equalsIgnoreCase(ELENCO)) {

			query.append(" ORDER BY");
			query.append("  DATA_PRATICA DESC,");
			query.append("  PATRONATO,");
			query.append("  UFFICIO,");
			query.append("  CF_LAVORATORE");

		}

		return query.toString();

	}

}
