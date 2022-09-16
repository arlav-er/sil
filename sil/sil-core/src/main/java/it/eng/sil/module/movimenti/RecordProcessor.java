package it.eng.sil.module.movimenti;

import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/*
 * Interfaccia per le classi che processano i record da importare
 */
public interface RecordProcessor {
	/*
	 * Operazione che processa un record secondo la logica contenuta nel processore
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException;
}