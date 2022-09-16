/*
 * Creato il Dec 9, 2004
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author savino (11/12/2004)
 * 
 */
public class ElencoMansioniDettaglioStampa extends AbstractSimpleModule {
	/**
	 * Attenzione al nome dato alle query. La dynamic query prende solo il
	 * dynamic statement "QUERY" e purtroppo se presente non viene questa
	 * query-attribute non viene vista la nuova query impostata con la chiamata
	 * di setSectionQuerySelect(); per cui sono stato costretto ad usare in modo
	 * esplicito il QueryExecutor. Ho ripreso la costruzione delle informazioni
	 * da stampare dalla action (del progetto sil)
	 * it.eng.sil.action.report.pubb.pubbMain.java
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		SourceBean rows = doDynamicSelect(request, response);
		Vector vPubblicazioni = rows.getAttributeAsVector("ROW");
		// setSectionQuerySelect("SELECT_MANSIONI_QUERY");

		for (int i = 0; i < vPubblicazioni.size(); i++) {
			BigDecimal richiesta = (BigDecimal) ((SourceBean) vPubblicazioni.get(i)).getAttribute("PRGRICHIESTAAZ");
			request.setAttribute("richiesta", richiesta);
			// doSelectWithStatement(request, response,
			// "SELECT_MANSIONI_QUERY");
			SourceBean query = (SourceBean) getConfig().getAttribute("SELECT_MANSIONI_QUERY");
			SourceBean listaMansioniRichiesta = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), getPool(), query, "select");
			((SourceBean) vPubblicazioni.get(i)).setAttribute("RIF_MANSIONI", listaMansioniRichiesta);
			request.delAttribute("richiesta");
		}
		response.setAttribute("LISTA_PUBBLICAZIONI", vPubblicazioni);
	}

}
