package it.eng.sil.module.amministrazione.redditoAttivazione;

/**
 * Permette di eseguire le query relative alla ricerca dei valori da inserire
 * Nei campi select delle JSP e ritornare i loro risultati
 * 
 * @author Giacomo Pandini
 */
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DoSelectQuery extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
