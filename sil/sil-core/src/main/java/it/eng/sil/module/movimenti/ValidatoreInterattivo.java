package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractInteractiveModule;
import it.eng.sil.module.movimenti.runners.ValidatoreRunnable;

/**
 * Classe del modulo che esegue la validazione interattiva
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class ValidatoreInterattivo extends AbstractInteractiveModule {
	public Runnable getRunnableService(SourceBean request, SourceBean response, AbstractInteractiveModule module) {
		return new ValidatoreRunnable(request, response, module);
	}
}