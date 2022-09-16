package it.eng.myportal.youthGuarantee.bean;

public class RisultatoInvioAdesione {

	private boolean success;
	private String messaggioErrore;

	public RisultatoInvioAdesione() {
		success = false;
		messaggioErrore = "";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessaggioErrore() {
		return messaggioErrore;
	}

	public void setMessaggioErrore(String messaggioErrore) {
		this.messaggioErrore = messaggioErrore;
	}

}
