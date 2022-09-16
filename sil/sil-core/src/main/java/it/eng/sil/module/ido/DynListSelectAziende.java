/*
 * Creato il 19-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynListSelectAziende implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("cf_ric");
		String piva = (String) req.getAttribute("piva_ric");
		String ragsoc = (String) req.getAttribute("ragsoc_ric");

		DynamicStatementUtils dsu = new DynamicStatementUtils();
		dsu.addSelect(" AN_AZIENDA.PRGAZIENDA," + " InitCap(AN_AZIENDA.STRRAGIONESOCIALE) as STRRAGIONESOCIALE,"
				+ " AN_AZIENDA.STRPARTITAIVA," + " AN_AZIENDA.STRCODICEFISCALE");
		dsu.addFrom(" AN_AZIENDA");
		dsu.addWhereIfFilledStrLikeUpper("AN_AZIENDA.STRCODICEFISCALE", cf, DynamicStatementUtils.DO_LIKE_INIZIA);
		dsu.addWhereIfFilledStrLikeUpper("AN_AZIENDA.STRPARTITAIVA", piva, DynamicStatementUtils.DO_LIKE_INIZIA);
		// su "ragione sociale" uso una ricerca per "contiene" la stringa
		dsu.addWhereIfFilledStrLikeUpper("AN_AZIENDA.STRRAGIONESOCIALE", ragsoc,
				DynamicStatementUtils.DO_LIKE_CONTIENE);

		dsu.addOrder("AN_AZIENDA.STRRAGIONESOCIALE");

		String query = dsu.getStatement();

		return query;
	}

}