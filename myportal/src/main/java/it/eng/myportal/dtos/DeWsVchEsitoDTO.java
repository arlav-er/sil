package it.eng.myportal.dtos;


public class DeWsVchEsitoDTO extends GenericDecodeDTO implements ITreeable {

	private static final long serialVersionUID = 1L;
	private String descrizione;
	private String messaggio;
	
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getMessaggio() {
		return messaggio;
	}
	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}
	
}
