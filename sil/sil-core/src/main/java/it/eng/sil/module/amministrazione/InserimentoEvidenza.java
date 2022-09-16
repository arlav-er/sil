/*
 * Creato il 18-ott-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.util.StringTokenizer;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author gritti
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InserimentoEvidenza extends AbstractSimpleModule {

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */

	public void service(SourceBean request, SourceBean response) throws Exception {
		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);
		// int idSuccess = this.disableMessageIdSuccess();
		// int idFail = this.disableMessageIdFail();
		//
		String cdnlav = StringUtils.getAttributeStrNotNull(request, "CDNLAVORATORE");
		String datScad = StringUtils.getAttributeStrNotNull(request, "DATDATASCAD");
		String strEv = StringUtils.getAttributeStrNotNull(request, "STREVIDENZA");
		String prgTipoEv = StringUtils.getAttributeStrNotNull(request, "PRGTIPOEVIDENZA");
		// tokenizer per reperire tutti i cdnlavoratore nel caso di una
		// multiselezione
		StringTokenizer st = new StringTokenizer(cdnlav, "|");
		while (st.hasMoreTokens()) {
			cdnlav = st.nextToken();
			request.updAttribute("CDNLAVORATORE", cdnlav);
			request.updAttribute("DATDATASCAD", datScad);
			request.updAttribute("STREVIDENZA", strEv);
			request.updAttribute("PRGTIPOEVIDENZA", prgTipoEv);

			doInsert(request, response);
		}
	}
}
