/*
 * Creato il 12-lug-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;

/**
 * @author D'Auria
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynComboStatoAttoDoc implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynComboStatoAttoDoc.class.getName());
	/*
	 * Savino 27/09/05: se si sta accedendo al dettaglio del documento, bisogna estrarre o la lista degli stati
	 * gestibili o la lista degli stati non gestibili (ovvero tutti, tra i quali ci sara' quello in cui si trova il
	 * documento: questo verra' visualizzato in sola lettura, non gestibile)
	 * 
	 * SELECT DISTINCT sa.codstatoatto AS codice, sa.strdescrizione AS descrizione FROM de_stato_atto sa,
	 * de_stato_atto_lst_tab sat WHERE sa.codstatoatto = sat.codstatoatto AND sat.codlsttab = 'AM_DOC' AND (NOT EXISTS (
	 * -- il codice atto del documento (XX) non e' tra quelli gestibili -- NVL (sa1.codmonoprimadopoins, '1') IN ('D',
	 * 'E') -- quindi bisogna estrarre tutti gli stati senza filtro sul campo codmonoprimadopoins SELECT 1 FROM
	 * de_stato_atto sa1, de_stato_atto_lst_tab sat1 WHERE sa1.codstatoatto = sat1.codstatoatto AND sat1.codlsttab =
	 * 'AM_DOC' AND NVL (sa1.codmonoprimadopoins, '1') IN ('D', 'E') AND sa1.codstatoatto = 'XX') ) OR ( NVL
	 * (sa.codmonoprimadopoins, '1') IN ('D', 'E') -- [*] AND EXISTS ( -- il codice atto del documento (XX) e' tra
	 * quelli gestibili -- NVL (sa1.codmonoprimadopoins, '1') IN ('D', 'E') -- quindi bisogna estrarre solo gli stati
	 * che si possono gestire dopo l'inserimento -- utilizzando il filtro sul campo codmonoprimadopoins [*] SELECT 1
	 * FROM de_stato_atto sa1, de_stato_atto_lst_tab sat1 WHERE sa1.codstatoatto = sat1.codstatoatto AND sat1.codlsttab
	 * = 'AM_DOC' AND NVL (sa1.codmonoprimadopoins, '1') IN ('D', 'E') AND sa1.codstatoatto = 'XX') ) ORDER BY
	 * descrizione
	 */

	private String className = StringUtils.getClassName(DynComboStatoAttoDoc.class);

	/**
	 * 
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug(className + ".getStatement() INIZIO");

		String codStatoAtto = null, dataOraProt = null;
		SourceBean req = requestContainer.getServiceRequest();
		String page = (String) req.getAttribute("PAGE");
		// Controllo se mi trovo in inserimento
		boolean isNewDoc = req.containsAttribute("NUOVO"); // ||
															// req.containsAttribute("SALVA");
		Documento doc = (Documento) req.getAttribute("documento");
		/*
		 * if (doc!=null) { codStatoAtto = doc.getCodStatoAtto(); dataOraProt = doc.getDatProtocollazione(); // il n.
		 * prot. potrebbe essere null anche se protocollato }
		 */
		// Savino 19/09/05:
		// se codStatoAtto <> 'PR' and dataOraProt <> null il documento si trova
		// in uno stato non gestibile
		// --> va ripresa la combo senza filtro sul CODMONOPRIMADOPOINS per
		// permettere di estrarre il codice da
		// visualizzare.
		// in pratica la jps e' gestibile se codStatoAtto = 'NP' or 'PR'
		// 27/09/05: questa condizione non funziona sempre: ho modificato la
		// query
		// ORA CREO LA QUERY DINAMICAMENTE (usando un "DynamicStatementUtils"):
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(" distinct sa.CODSTATOATTO AS CODICE "); // la distinct
																// deve esserci
		dsu.addSelect(" sa.STRDESCRIZIONE AS DESCRIZIONE ");

		dsu.addFrom(" DE_STATO_ATTO sa ");
		dsu.addFrom(" DE_STATO_ATTO_LST_TAB  sat ");
		dsu.addWhere(" sa.CODSTATOATTO = sat.CODSTATOATTO ");
		dsu.addWhere(" sat.CODLSTTAB = 'AM_DOC' ");
		if (isNewDoc) {
			dsu.addWhere(" nvl(sa.CODMONOPRIMADOPOINS, '1') in ( 'E', 'P' )");
		} else if ("DettagliDocumentoPage".equalsIgnoreCase(page)
				|| "DettaglioDocumentoPadreStampParamPage".equalsIgnoreCase(page)
				|| "DettagliDocumentoAllegatoStampParamPage".equalsIgnoreCase(page)) {
			// siamo in dettaglio del documento. Quindi il doc deve esserci.
			// (N.B. viene passato tramite "consequences")
			if (doc == null) {
				_logger.debug(
						"dettaglio documento- - classe sql dinamica: non trovata l'istanza del documento nella request.");

				return null;
			}
			StringBuffer exists = new StringBuffer().append("SELECT 1 ")
					.append("FROM de_stato_atto sa1, de_stato_atto_lst_tab sat1 ")
					.append("WHERE sa1.codstatoatto = sat1.codstatoatto ").append("AND sat1.codlsttab = 'AM_DOC' ")
					.append("AND NVL (sa1.codmonoprimadopoins, '1') IN ('D', 'E') ").append("and sa1.codstatoatto = '")
					.append(doc.getCodStatoAtto()).append("'");
			dsu.addWhere(" ( not exists( ");
			dsu.addToWhere(exists.toString());
			dsu.addToWhere(")) ");
			dsu.addToWhere(" or ( nvl(sa.CODMONOPRIMADOPOINS, '1') in ( 'D', 'E' ) ");
			dsu.addToWhere(" and exists (");
			dsu.addToWhere(exists.toString());
			dsu.addToWhere("))");
		}
		dsu.addOrder("DESCRIZIONE");
		String query = dsu.getStatement();
		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}

}