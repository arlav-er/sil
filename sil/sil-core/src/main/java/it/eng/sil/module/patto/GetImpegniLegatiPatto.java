/*
 * Creato il 30-set-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author desimone
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class GetImpegniLegatiPatto extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelectWithStatement(request, response, getStatement(request));
	}

	private String getStatement(SourceBean request) throws Exception {
		Object o = request.getAttribute("prgPattoLavoratore");

		if (o instanceof BigDecimal) {
			throw new Exception("il prgPattoLavoratore e' un BigDecimal: era richiesta una String");
		}

		if ((o == null) || ((String) o).equals("")) {
			// ricerca in base al cdnLavoratore
			return "QUERIES.SELECT_FROM_CDNLAVORATORE";
		} else {
			return "QUERIES.SELECT_FROM_PRGPATTOLAVORATORE";
		}
	}
}
