package it.eng.myportal.dtos;

public class DeEvasioneRichDTO extends GenericDecodeDTO implements ISuggestible {
	
	private static final long serialVersionUID = 7964255011416084966L;
	private String descrizione;
	private String codAmbitoDiffusione;
	 
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	@Override
	public String getDescrizione() {
		return descrizione;
	}
	public String getCodAmbitoDiffusione() {
		return codAmbitoDiffusione;
	}
	public void setCodAmbitoDiffusione(String codAmbitoDiffusione) {
		this.codAmbitoDiffusione = codAmbitoDiffusione;
	}
}
