package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la selezione degli elementi della combo dei tipi di assunzione in base al tipo dell'azienda, natura
 * giuridica dell'azienda e tipo di contratto (determinato o indeterminato).
 */
public class DynSelectAss implements IDynamicStatementProvider2 {

	private String SELECT_PROLOGO = "select " + " CODTIPOASS as codice, " + " strdescrizione || DECODE(SYSDATE, "
			+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " '') AS descrizione, CODMONOTIPO, CODCONTRATTO" + " from DE_MV_TIPO_ASS" + " where CODTIPOASS IN ( ";

	private String SELECT_PARTE_TIAZ = " SELECT COLL.STRCHIAVETABELLA " + " FROM DE_MV_TIPO_ASS_COLL COLL "
			+ " WHERE COLL.CODLSTTABPARTENZA = 'DE_TIAZ' " + " AND COLL.CODLSTTAB = 'DE_MVAS' ";

	private String SELECT_PARTE_NATG = " INTERSECT " + " SELECT COLL.STRCHIAVETABELLA "
			+ " FROM DE_MV_TIPO_ASS_COLL COLL " + " WHERE COLL.CODLSTTABPARTENZA = 'DE_NATG' "
			+ " AND COLL.CODLSTTAB = 'DE_MVAS' ";

	private String SELECT_PARTE_CONT = " INTERSECT " + " SELECT COLL.STRCHIAVETABELLA "
			+ " FROM DE_MV_TIPO_ASS_COLL COLL " + " WHERE COLL.CODLSTTABPARTENZA = 'DE_CONT' "
			+ " AND COLL.CODLSTTAB = 'DE_MVAS' ";

	private String SELECT_EPILOGO = " )";

	public DynSelectAss() {
	}

	public String getStatement(SourceBean req, SourceBean response) {
		// SourceBean req = requestContainer.getServiceRequest();

		// Estraggo il criterio di ricerca
		String criterio = StringUtils.getAttributeStrNotNull(req, "CRITERIO");
		boolean cercaCodice = criterio.equalsIgnoreCase("codice");
		boolean cercaDescrizione = criterio.equalsIgnoreCase("descrizione");
		String codTipoAzienda = StringUtils.getAttributeStrNotNull(req, "CODTIPOAZIENDA");
		String codNatGiuridica = StringUtils.getAttributeStrNotNull(req, "CODNATGIURIDICA");
		String codMonoTempo = StringUtils.getAttributeStrNotNull(req, "CODMONOTEMPO");
		String codTipoAss = StringUtils.getAttributeStrNotNull(req, "codTipoAss");
		String descrTipoAss = StringUtils.getAttributeStrNotNull(req, "descrTipoAss");
		String filtrato = req.containsAttribute("filtrato") ? (String) req.getAttribute("filtrato") : "";
		String provenienza = req.containsAttribute("provenienza") ? (String) req.getAttribute("provenienza") : "";
		boolean tiaz = (codTipoAzienda != null && !codTipoAzienda.equals(""));
		boolean natg = (codNatGiuridica != null && !codNatGiuridica.equals(""));
		boolean cont = (codMonoTempo != null && !codMonoTempo.equals(""));
		boolean filtro = false;

		if (filtrato.equalsIgnoreCase("DL")) {
			// Non visualizzare gli scaduti
			SELECT_PROLOGO = "select " + " CODTIPOASS as codice, " + " strdescrizione || DECODE(SYSDATE, "
					+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " '') AS descrizione, CODMONOTIPO, CODCONTRATTO" + " from DE_MV_TIPO_ASS "
					+ " ORDER BY CODTIPOASS";
			filtro = true;
		}
		String query = "";
		if (filtro) {
			query = SELECT_PROLOGO;
		} else {
			// Creo la parte dinamica della query e la ritorno
			/*
			 * query = SELECT_PROLOGO + SELECT_PARTE_TIAZ + (tiaz ? " AND COLL.STRCHIAVETABELLAPARTENZA =
			 * '" + codTipoAzienda + "' " : "") + SELECT_PARTE_NATG + (natg ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" +
			 * codNatGiuridica + "' " : "") + SELECT_PARTE_CONT + (cont ? " AND COLL.STRCHIAVETABELLAPARTENZA =
			 * " + (codMonoTempo=="D" ? "'LT'":"'LP'") : "") + SELECT_EPILOGO + ((cercaCodice && !codTipoAss.equals(""))
			 * ? " AND upper(CODTIPOASS) like '%" + codTipoAss.toUpperCase() + "%' " : "") + ((cercaDescrizione &&
			 * !descrTipoAss.equals("")) ? " AND upper(STRDESCRIZIONE) like '%" + StringUtils.replace(descrTipoAss, "'",
			 * "''").toUpperCase() + "%' " : "") + " ORDER BY CODTIPOASS";
			 */
			query = SELECT_PROLOGO + SELECT_PARTE_TIAZ
					+ (tiaz ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" + codTipoAzienda + "' " : "") + SELECT_PARTE_NATG
					+ (natg ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" + codNatGiuridica + "' " : "")
					+ SELECT_PARTE_CONT
					+ (cont ? " AND COLL.STRCHIAVETABELLAPARTENZA = DECODE('" + codMonoTempo + "','D','LT', 'I', 'LP')"
							: "")
					+ SELECT_EPILOGO
					+ ((cercaCodice && !codTipoAss.equals(""))
							? " AND upper(CODTIPOASS) like upper('%" + codTipoAss + "%') "
							: "")
					+ ((cercaDescrizione && !descrTipoAss.equals("")) ? " AND upper(STRDESCRIZIONE) like upper('%"
							+ StringUtils.replace(descrTipoAss, "'", "''") + "%') " : "");
			if (provenienza.equalsIgnoreCase("TrasfRamoAzienda")) {
				query = query + " AND NVL(de_mv_tipo_ass.codmonotipo,' ') <>'T' ";
			}
			query = query + "ORDER BY CODTIPOASS";
		}
		return query;
	}
}
