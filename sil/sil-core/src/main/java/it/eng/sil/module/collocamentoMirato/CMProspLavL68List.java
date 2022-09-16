package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dispatching.module.impl.ListModule;

/*
 * Modulo che effettua la select della lista dei candidati aderenti alla grqduatoria
 * 
 * la particolarità di questo modulo sta nel fatto che viene chiamato
 * prima della select il metodo che permette di inserire in un vettore 
 * recuperato dalla session tutti i cdnLavoratori dei candidati selezionati
 * tramite il checkbox nella lista
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMProspLavL68List extends ListModule {

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

	}

}