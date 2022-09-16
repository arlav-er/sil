package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

public class ChiusuraMobilitaBean extends MobilitaBean {
	public ChiusuraMobilitaBean(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public int getTipoEventoAmministrativo() {
		return EventoAmministrativo.CHIUSURA_MOBILITA;
	}
}