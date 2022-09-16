package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class GetProtocolloDynStatement implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetProtocolloDynStatement.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String query = "";

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();
		String PAGE = (String) req.getAttribute("PAGE");
		String CDNLAVORATORE = (String) req.getAttribute("CDNLAVORATORE");
		String CODTIPODOCUMENTO = (String) req.getAttribute("CODTIPODOCUMENTO");
		// strChiaveTabella viene passata dal modulo precedente tramite
		// consequences
		String strChiaveTabella = Utils.notNull(req.getAttribute("strChiaveTabella"));
		// Costruisco la query inserendo la subquery
		if (CODTIPODOCUMENTO.equalsIgnoreCase("IM")) {
			query = "select DOC.prgdocumento prgdocumento, " + " DOC.cdnlavoratore cdnlavoratore, "
					+ " doc.DATPROTOCOLLO, " + " to_char(doc.DATPROTOCOLLO, 'dd/mm/yyyy') datprot,"
					+ " to_char(doc.DATPROTOCOLLO, 'HH24:MI') oraprot,  " + " DOC.numannoprot annoprot, "
					+ " DOC.numprotocollo numprotocollo, " + " DOC.strnomedoc strnomedoc, " + " DOC.CODMONOIO,"
					+ " de_doc_ambito.STRDESCRIZIONE" + " from am_documento DOC, " + " am_documento_coll DOC_COLL, "
					+ " TS_COMPONENTE TSC, " + " an_lavoratore ANL, " + " am_dich_disponibilita," + " de_doc_ambito,"
					+ " de_doc_tipo" + " where DOC.CDNLAVORATORE = ANL.CDNLAVORATORE "
					+ " AND DOC_COLL.prgDocumento  = DOC.prgDocumento "
					+ " AND DOC_COLL.CDNCOMPONENTE = TSC.CDNCOMPONENTE " + " and to_number(doc_coll.strchiavetabella)"
					+ "    = am_dich_disponibilita.PRGDICHDISPONIBILITA"

					+ " AND DOC.codTipoDocumento = de_doc_tipo.CODTIPODOCUMENTO"
					+ " AND de_doc_tipo.CODAMBITODOC = de_doc_ambito.CODAMBITODOC"

					+ " AND DOC.CDNLAVORATORE =  " + CDNLAVORATORE + " AND DOC.CODTIPODOCUMENTO  = '" + CODTIPODOCUMENTO
					+ "' ";

			if (req.containsAttribute("DIDCHIUSA")) {
				query += " AND am_dich_disponibilita.PRGDICHDISPONIBILITA = "
						+ (String) req.getAttribute("PRGDICHDISPONIBILITA");
			} else {
				query += " AND UPPER(TSC.STRPAGE) = '" + PAGE.toUpperCase() + "' ";
				query += " AND DOC.datfine IS NULL ";
				query += " and am_dich_disponibilita.DATFINE IS NULL ";
				// query += " AND DOC.CODSTATOATTO in ( 'PR', 'NP' )";
				// Savino 23/09/05:
				// in ogni caso ho aggiunto un order by in coda: dato che la jsp
				// riprende il primo elemento del
				// vettore ritornato, verra' selezionato il primo documento; se
				// uno di questi e' protocollato
				// allora sara' proprio il primo della lista
				if (strChiaveTabella.equals(""))
					query += " AND DOC.CODSTATOATTO in ('PR')";
				else
					query += " AND am_dich_disponibilita.PRGDICHDISPONIBILITA = " + strChiaveTabella;
			}

		} else if (CODTIPODOCUMENTO.equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
				|| CODTIPODOCUMENTO.equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO)) {
			PAGE = "PattoLavDettaglioPage";
			query = " select DOC.prgdocumento prgdocumento, " + " DOC.cdnlavoratore cdnlavoratore, "
					+ " doc.DATPROTOCOLLO, " + " to_char(doc.DATPROTOCOLLO, 'dd/mm/yyyy') datprot,"
					+ " to_char(doc.DATPROTOCOLLO, 'HH24:MI') oraprot,  " + " DOC.numannoprot annoprot, "
					+ " DOC.numprotocollo numprotocollo, " + " DOC.strnomedoc strnomedoc, " + " DOC.CODMONOIO,"
					+ " de_doc_ambito.STRDESCRIZIONE" + " from am_documento DOC, " + " am_documento_coll DOC_COLL, "
					+ " TS_COMPONENTE TSC, " + " an_lavoratore ANL, " + " am_patto_lavoratore," + " de_doc_ambito,"
					+ " de_doc_tipo" + " where DOC.CDNLAVORATORE = ANL.CDNLAVORATORE "
					+ " AND DOC_COLL.prgDocumento  = DOC.prgDocumento "
					+ " AND DOC_COLL.CDNCOMPONENTE = TSC.CDNCOMPONENTE " + " AND DOC.datfine IS NULL "
					+ " AND DOC.codTipoDocumento = de_doc_tipo.CODTIPODOCUMENTO"
					+ " AND de_doc_tipo.CODAMBITODOC = de_doc_ambito.CODAMBITODOC"
					+ " and to_number(doc_coll.strchiavetabella)" + "         = am_patto_lavoratore.PRGPATTOLAVORATORE"
					+ " and am_patto_lavoratore.DATFINE  IS NULL " + " AND UPPER(TSC.STRPAGE) = '" + PAGE.toUpperCase()
					+ "' " + " AND DOC.CDNLAVORATORE =  " + CDNLAVORATORE + " AND DOC.CODTIPODOCUMENTO  = '"
					+ CODTIPODOCUMENTO + "' "
					// Savino 23/09/05: se il patto e' protocollato ritorno solo
					// quel record
					// se ci sono piu' documenti restituiro come primo quello
					// inserito per ultimo
					// in ogni caso ho aggiunto un order by in coda: dato che la
					// jsp riprende il primo elemento del
					// vettore ritornato, verra' selezionato il primo documento;
					// se uno di questi e' protocollato
					// allora sara' proprio il primo della lista
					+ " and ((doc.CODSTATOATTO = 'PR' and AM_PATTO_LAVORATORE.CODSTATOATTO = 'PR') or "
					+ " (AM_PATTO_LAVORATORE.CODSTATOATTO = 'PP')" + " )";

		} else
			return query = "";

		_logger.debug(className + "::Protocollo: " + query);

		return query + " order by doc.prgDocumento desc";

	}// getStatement

}