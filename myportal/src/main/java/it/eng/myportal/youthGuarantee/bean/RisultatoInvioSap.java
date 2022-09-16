package it.eng.myportal.youthGuarantee.bean;

public class RisultatoInvioSap {
	
	private boolean success;
	private String messaggioErrore;
	private String codiceSAP;
	
	public RisultatoInvioSap() {
		success = false;
		messaggioErrore = "";
		codiceSAP = "";
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
	public String getCodiceSAP() {
		return codiceSAP;
	}
	public void setCodiceSAP(String codiceSAP) {
		this.codiceSAP = codiceSAP;
	}
	
}
