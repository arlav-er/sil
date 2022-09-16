package it.eng.myportal.entity.enums;

public enum YgDichiarazioneNeetStatoEnum {
	INCOMPLETO("Incompleta"), COMPLETO("Completa"), CANCELLATO("Cancellata");

	private String descrizione;

	private YgDichiarazioneNeetStatoEnum(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
}
