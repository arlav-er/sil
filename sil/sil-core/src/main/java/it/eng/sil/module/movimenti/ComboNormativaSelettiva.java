package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Esegue la selezione degli elementi della combo tipo normativa in base alla tipologia dell'azienda, alla sua natura
 * giuridica e al tipo di contratto scelto.
 */
public class ComboNormativaSelettiva extends AbstractSimpleModule {
	public ComboNormativaSelettiva() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		// Eseguo la query di ricerca dinamica per riempire la combo
		doDynamicSelect(request, response);
	}
}