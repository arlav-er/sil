package it.eng.myportal.dtos;

public class DeNaturaGiuridicaDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = 1746217985021186888L;

	private String descrizione;

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
