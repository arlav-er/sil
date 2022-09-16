package it.eng.sil.coop.webservices.agenda.appuntamento;

public class EsitoControlliCoerenza {

	private boolean success;
	private String codErrore;
	private String descrizioneErrore;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	public String getDescrizioneErrore() {
		return descrizioneErrore;
	}

	public void setDescrizioneErrore(String descrizioneErrore) {
		this.descrizioneErrore = descrizioneErrore;
	}

}
