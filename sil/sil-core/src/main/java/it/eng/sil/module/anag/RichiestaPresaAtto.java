/*
 * Created on Aug 16, 2006
 *
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class RichiestaPresaAtto extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		// si prende dalla request il prgpresaatto
		String prgPresaAtto = Utils.notNull(serviceRequest.getAttribute("prgPresaAtto"));
		if (!"".equals(prgPresaAtto)) {
			// se esiste esegue la query precisa e mette nella response il
			// risultato
			setSectionQuerySelect("RICHIESTA_PRESA_ATTO");

		} else {
			// se non esiste esegue la query generica ... che si basa sul
			// cdnlavoratore
			setSectionQuerySelect("ULTIMA_RICHIESTA_PRESA_ATTO");
		}
		doSelect(serviceRequest, serviceResponse);
	}

}
