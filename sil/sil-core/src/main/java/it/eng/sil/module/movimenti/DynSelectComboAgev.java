package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * Effettua la selezione degli elementi della combo dei tipi di agevolazione in base al tipo dell'azienda, natura
 * giuridica dell'azienda, tipo di contratto (determinato o indeterminato), e tipo di assunzione passati.
 */
public class DynSelectComboAgev implements IDynamicStatementProvider {

	private String SELECT_PROLOGO = "select " + " codAgevolazione as codice, "
			+ " SUBSTR(strdescrizione || DECODE(SYSDATE, "
			+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', " + " ''),1,100) AS descrizione "
			+ " from DE_AGEVOLAZIONE" + " where codAgevolazione IN ( ";

	private String SELECT_PARTE_TIAZ = " SELECT COLL.STRCHIAVETABELLA " + " FROM DE_MV_TIPO_ASS_COLL COLL "
			+ " WHERE COLL.CODLSTTABPARTENZA = 'DE_TIAZ' " + " AND COLL.CODLSTTAB = 'DE_AGEV' ";

	private String SELECT_PARTE_NATG = " INTERSECT " + " SELECT COLL.STRCHIAVETABELLA "
			+ " FROM DE_MV_TIPO_ASS_COLL COLL " + " WHERE COLL.CODLSTTABPARTENZA = 'DE_NATG' "
			+ " AND COLL.CODLSTTAB = 'DE_AGEV' ";

	private String SELECT_PARTE_TASS = " INTERSECT " + " SELECT COLL.STRCHIAVETABELLA "
			+ " FROM DE_MV_TIPO_ASS_COLL COLL " + " WHERE COLL.CODLSTTABPARTENZA = 'DE_MVAS' "
			+ " AND COLL.CODLSTTAB = 'DE_AGEV' ";

	private String SELECT_PARTE_CONT = " INTERSECT " + " SELECT COLL.STRCHIAVETABELLA "
			+ " FROM DE_MV_TIPO_ASS_COLL COLL " + " WHERE COLL.CODLSTTABPARTENZA = 'DE_CONT' "
			+ " AND COLL.CODLSTTAB = 'DE_AGEV' ";

	private String SELECT_EPILOGO = " ) ORDER BY STRDESCRIZIONE";

	public DynSelectComboAgev() {
	}

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codTipoAzienda = (String) req.getAttribute("CODTIPOAZIENDA");
		String codNatGiuridica = (String) req.getAttribute("CODNATGIURIDICA");
		String codTipoAss = (String) req.getAttribute("CODTIPOASS");
		String codMonoTempo = (String) req.getAttribute("CODMONOTEMPO");
		String filtrato = req.containsAttribute("filtrato") ? (String) req.getAttribute("filtrato") : "";
		String queryFilter = req.containsAttribute("qFilter") ? (String) req.getAttribute("qFilter") : "";

		boolean tiaz = (codTipoAzienda != null && !codTipoAzienda.equals(""));
		boolean natg = (codNatGiuridica != null && !codNatGiuridica.equals(""));
		boolean tass = (codTipoAss != null && !codTipoAss.equals(""));
		boolean cont = (codMonoTempo != null && !codMonoTempo.equals(""));
		boolean filtro = false;

		if (filtrato.equalsIgnoreCase("DL")) {
			// Non visualizzare gli scaduti
			SELECT_PROLOGO = "select " + " codAgevolazione as codice, "
					+ " SUBSTR(strdescrizione,1,35) || '...' AS descrizione " + " from DE_AGEVOLAZIONE"
					+ " where SYSDATE between DATINIZIOVAL and DATFINEVAL " + " order by strdescrizione ";
			filtro = true;
		} else if (filtrato.equalsIgnoreCase("NF")) {
			SELECT_PROLOGO = "select " + " codAgevolazione as codice, " + " SUBSTR(strdescrizione || DECODE(SYSDATE, "
					+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', " + " ''),1,35) || '...' AS descrizione "
					+ " from DE_AGEVOLAZIONE" + " order by strdescrizione ";
			filtro = true;
		}
		if (queryFilter.equalsIgnoreCase("S")) {
			// effettua criteri di filtro in base alle selezioni effettuate tra
			// le
			// opzioni non scadute
			SELECT_PROLOGO = "select " + " codAgevolazione as codice, "
					+ " SUBSTR(strdescrizione,1,35) || '...' AS descrizione " + " from DE_AGEVOLAZIONE"
					+ " where SYSDATE between DATINIZIOVAL and DATFINEVAL " + " AND codAgevolazione IN ( ";
		}

		if (filtro) {
			return SELECT_PROLOGO;
		} else {
			// Creo la parte dinamica della query e la ritorno
			return SELECT_PROLOGO + SELECT_PARTE_TIAZ
					+ (tiaz ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" + codTipoAzienda + "' " : "") + SELECT_PARTE_NATG
					+ (natg ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" + codNatGiuridica + "' " : "")
					+ SELECT_PARTE_TASS + (tass ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" + codTipoAss + "' " : "")
					+ SELECT_PARTE_CONT
					+ (cont ? " AND COLL.STRCHIAVETABELLAPARTENZA = DECODE('" + codMonoTempo + "','D','LT', 'I', 'LP')"
							: "")
					+ SELECT_EPILOGO;
		}
	}
}