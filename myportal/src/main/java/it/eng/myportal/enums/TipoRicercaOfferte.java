package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public enum TipoRicercaOfferte {
	SI("Valide"), NO("Scadute"), LAV("In Lavorazione"), PUB("Pubblicata"), ARC("Archiviata"), TUTTE("Tutte");
private static List<SelectItem> tipiRicerca;
private String label;

TipoRicercaOfferte(String label) {
	this.label = label;
}

public String getLabel() {
	return label;
}

static {
	tipiRicerca = new ArrayList<SelectItem>();
	for (TipoRicercaOfferte tipo : TipoRicercaOfferte.values()) {
		tipiRicerca.add(new SelectItem(tipo, tipo.getLabel()));
	}
}

public static List<SelectItem> asSelectItems() {
	return tipiRicerca;
}
}