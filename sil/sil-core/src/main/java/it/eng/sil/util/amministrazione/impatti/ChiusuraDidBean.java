package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.SourceBeanException;

public class ChiusuraDidBean extends DidBean {
	public ChiusuraDidBean(DidBean sb) throws SourceBeanException {
		super(sb.getSource());
		setStatoOccupazionale(sb.getStatoOccupazionale());
	}

	public int getTipoEventoAmministrativo() {
		return EventoAmministrativo.CHIUSURA_DID;
	}

	public String toString() {
		return "CHIUSURA DID:" + super.toString();
	}
}