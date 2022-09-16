package it.eng.myportal.dtos;


public class DePecDominiDTO extends GenericDecodeDTO implements ISuggestible {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5648815652159147621L;
	private String descrizione;

	@Override
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
