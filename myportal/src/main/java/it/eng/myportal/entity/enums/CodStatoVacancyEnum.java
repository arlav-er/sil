package it.eng.myportal.entity.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public enum CodStatoVacancyEnum {
	
	
	PUB("Pubblicata"), LAV("In Lavorazione"), NAS("Nascosta"), ARC("Archiviata");
	
	private static List<SelectItem> tipoStato;
	private String descrizione;
	private CodStatoVacancyEnum(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
	
	static {
		tipoStato = new ArrayList<SelectItem>();
		for (CodStatoVacancyEnum current : CodStatoVacancyEnum.values()) {
			tipoStato.add(new SelectItem(current, current.getDescrizione()));
		}
	}

	public static List<SelectItem> asSelectItems() {
		return tipoStato;
	}
}
