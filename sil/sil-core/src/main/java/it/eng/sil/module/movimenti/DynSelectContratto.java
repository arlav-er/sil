package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynSelectContratto implements IDynamicStatementProvider2 {

	private String SELECT_PROLOGO = "select " + " CODTIPOCONTRATTO as codice, " + " strdescrizione || DECODE(SYSDATE, "
			+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " '') AS descrizione, CODMONOTIPO, CODCONTRATTO, nvl(FLGTI, 'N') as FLGCONTRATTOTI "
			+ " from de_tipo_contratto " + " where CODTIPOCONTRATTO IN ( ";

	private String SELECT_PARTE_TIAZ = " SELECT COLL.STRCHIAVETABELLA " + " FROM DE_MV_TIPO_ASS_COLL COLL "
			+ " WHERE COLL.CODLSTTABPARTENZA = 'DE_TIAZ' " + " AND COLL.CODLSTTAB = 'TIPOCONT' ";

	private String SELECT_PARTE_CONT = " SELECT COLL.STRCHIAVETABELLA " + " FROM DE_MV_TIPO_ASS_COLL COLL "
			+ " WHERE COLL.CODLSTTABPARTENZA = 'DE_CONT' " + " AND COLL.CODLSTTAB = 'TIPOCONT' ";

	private String SELECT_EPILOGO = " )";

	public DynSelectContratto() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		// Estraggo il criterio di ricerca
		String criterio = StringUtils.getAttributeStrNotNull(req, "CRITERIO");
		boolean cercaCodice = criterio.equalsIgnoreCase("codice");
		boolean cercaDescrizione = criterio.equalsIgnoreCase("descrizione");
		String codTipoAzienda = StringUtils.getAttributeStrNotNull(req, "CODTIPOAZIENDA");
		String codMonoTempo = StringUtils.getAttributeStrNotNull(req, "CODMONOTEMPO");
		String codTipoAss = StringUtils.getAttributeStrNotNull(req, "codTipoAss");
		String descrTipoAss = StringUtils.getAttributeStrNotNull(req, "descrTipoAss");
		String filtrato = req.containsAttribute("filtrato") ? (String) req.getAttribute("filtrato") : "";
		String provenienza = req.containsAttribute("provenienza") ? (String) req.getAttribute("provenienza") : "";
		boolean tiaz = (codTipoAzienda != null && !codTipoAzienda.equals(""));
		boolean cont = (codMonoTempo != null && !codMonoTempo.equals(""));
		boolean filtro = false;
		String flgInterna = StringUtils.getAttributeStrNotNull(req, "FLGINTERASSPROPRIA");
		String checkForzaValidazione = StringUtils.getAttributeStrNotNull(req, "checkForzaValidazione");
		// CONTROLLO POST FASE 3 : eliminare il controllo tipo azienda/tipo contratto nella sola validazione massiva
		boolean noForzaValidazione = ((checkForzaValidazione.equals("N") || checkForzaValidazione.equals("")) ? true
				: false);

		String query = "";
		if (filtrato.equalsIgnoreCase("DL")) {
			// Non visualizzare gli scaduti
			SELECT_PROLOGO = "select " + " CODTIPOCONTRATTO as codice, " + " strdescrizione || DECODE(SYSDATE, "
					+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " '') AS descrizione, CODMONOTIPO, CODCONTRATTO, nvl(FLGTI, 'N') as FLGCONTRATTOTI "
					+ " from de_tipo_contratto " + " ORDER BY CODTIPOCONTRATTO";
			filtro = true;
		}
		if (filtro) {
			query = SELECT_PROLOGO;
		} else {
			// CONTROLLO POST FASE 3 : eliminare il controllo tipo azienda/tipo contratto nella sola validazione massiva
			query = SELECT_PROLOGO;
			if (noForzaValidazione) {
				query = query + SELECT_PARTE_TIAZ
						+ (tiaz ? " AND COLL.STRCHIAVETABELLAPARTENZA = '" + codTipoAzienda + "' " : "");
				query = query + " INTERSECT ";
			}
			query = query + SELECT_PARTE_CONT
					+ (cont ? " AND COLL.STRCHIAVETABELLAPARTENZA = DECODE('" + codMonoTempo + "','D','LT', 'I', 'LP')"
							: "")
					+ SELECT_EPILOGO
					+ ((cercaCodice && !codTipoAss.equals(""))
							? " AND upper(CODTIPOCONTRATTO) like upper('%" + codTipoAss + "%') "
							: "");
			if (noForzaValidazione) {
				// Lavoro a domicilio e lavoro marittimo non è applicabile in caso di assunzioni in conto proprio
				query = query
						+ ((cercaCodice && !codTipoAss.equals("") && codTipoAzienda.equals("AZI") && flgInterna.equals(
								"S")) ? " AND upper(CODTIPOCONTRATTO) NOT IN ('A.08.00','A.08.01','F.01.00','F.02.00') "
										: "")
						+ ((cercaDescrizione && !descrTipoAss.equals("")) ? " AND upper(STRDESCRIZIONE) like upper('%"
								+ StringUtils.replace(descrTipoAss, "'", "''") + "%') " : "")
						// Lavoro a domicilio e lavoro marittimo non è applicabile in caso di assunzioni in conto
						// proprio
						+ ((cercaDescrizione && !descrTipoAss.equals("") && codTipoAzienda.equals("AZI")
								&& flgInterna.equals("S"))
										? " AND upper(STRDESCRIZIONE) NOT IN ('LAVORO A DOMICILIO A TEMPO INDETERMINATO',"
												+ "				'LAVORO A DOMICILIO A TEMPO DETERMINATO','LAVORO MARITTIMO A TEMPO INDETERMINATO','LAVORO MARITTIMO A TEMPO DETERMINATO') "
										: "");
			}
			if (provenienza.equalsIgnoreCase("TrasfRamoAzienda")) {
				query = query + " AND NVL(de_tipo_contratto.codmonotipo,' ') <> 'T' ";
			}
			query = query + "ORDER BY CODTIPOCONTRATTO";
		}
		return query;
	}
}
