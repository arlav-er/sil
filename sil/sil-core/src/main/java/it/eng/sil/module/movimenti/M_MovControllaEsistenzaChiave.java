/*
 * Creato il 15-nov-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Modulo per il controllo dell'esistenza delle chiavi sul DB a partire dalla chiave , dal nome della colonna e da
 * quello della tabella
 * 
 * @author roccetti
 */
public class M_MovControllaEsistenzaChiave extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		doDynamicSelect(request, response);
	}
}
