package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.StringUtils;

public class DynSelectContrattoNullaOsta implements IDynamicStatementProvider2 {

	private String SELECT_PROLOGO = "select " + " CODTIPOCONTRATTO as codice, " + " strdescrizione || DECODE(SYSDATE, "
			+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
			+ " '') AS descrizione, CODMONOTIPO, CODCONTRATTO " + " from de_tipo_contratto "
			+ " where CODTIPOCONTRATTO IN ( ";

	private String SELECT_PARTE_CONT = " SELECT COLL.STRCHIAVETABELLA " + " FROM DE_MV_TIPO_ASS_COLL COLL "
			+ " WHERE COLL.CODLSTTABPARTENZA = 'DE_CONT' " + " AND COLL.CODLSTTAB = 'TIPOCONT' ";

	private String SELECT_EPILOGO = " )";

	public DynSelectContrattoNullaOsta() {
	}

	public String getStatement(SourceBean req, SourceBean response) {

		// Estraggo il criterio di ricerca
		String criterio = StringUtils.getAttributeStrNotNull(req, "CRITERIO");
		boolean cercaCodice = criterio.equalsIgnoreCase("codice");
		boolean cercaDescrizione = criterio.equalsIgnoreCase("descrizione");
		String codTipoAzienda = StringUtils.getAttributeStrNotNull(req, "CODTIPOAZIENDA");
		String codMonoTempo = StringUtils.getAttributeStrNotNull(req, "CODMONOTEMPO");
		String codTipoContratto = StringUtils.getAttributeStrNotNull(req, "codTipoContratto");
		String strTipoContratto = StringUtils.getAttributeStrNotNull(req, "strTipoContratto");
		String filtrato = req.containsAttribute("filtrato") ? (String) req.getAttribute("filtrato") : "";
		String provenienza = req.containsAttribute("provenienza") ? (String) req.getAttribute("provenienza") : "";
		boolean tiaz = (codTipoAzienda != null && !codTipoAzienda.equals(""));
		boolean cont = (codMonoTempo != null && !codMonoTempo.equals(""));
		boolean filtro = false;

		if (filtrato.equalsIgnoreCase("DL")) {
			// Non visualizzare gli scaduti
			SELECT_PROLOGO = "select " + " CODTIPOCONTRATTO as codice, " + " strdescrizione || DECODE(SYSDATE, "
					+ " GREATEST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " LEAST(SYSDATE, DATINIZIOVAL, DATFINEVAL),'(scaduto)', "
					+ " '') AS descrizione, CODMONOTIPO, CODCONTRATTO " + " from de_tipo_contratto "
					+ " ORDER BY CODTIPOCONTRATTO";
			filtro = true;
		}
		String query = "";
		if (filtro) {
			query = SELECT_PROLOGO;
		} else {
			query = SELECT_PROLOGO + SELECT_PARTE_CONT
					+ (cont ? " AND COLL.STRCHIAVETABELLAPARTENZA = DECODE('" + codMonoTempo + "','D','LT', 'I', 'LP')"
							: "")
					+ SELECT_EPILOGO
					+ ((cercaCodice && !codTipoContratto.equals(""))
							? " AND upper(CODTIPOCONTRATTO) like upper('%" + codTipoContratto + "%') "
							: "")
					+ ((cercaDescrizione && !strTipoContratto.equals("")) ? " AND upper(STRDESCRIZIONE) like upper('%"
							+ StringUtils.replace(strTipoContratto, "'", "''") + "%') " : "");
			query = query + "ORDER BY CODTIPOCONTRATTO";
		}
		return query;
	}
}
