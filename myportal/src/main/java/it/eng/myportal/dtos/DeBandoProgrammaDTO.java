package it.eng.myportal.dtos;

public class DeBandoProgrammaDTO extends GenericDecodeDTO {

	private static final long serialVersionUID = -4129166269279974666L;
	
	private String codBandoProgramma;
	private String descrizione;
	
	//Getter and setter
	public String getCodBandoProgramma() {
		return codBandoProgramma;
	}
	public void setCodBandoProgramma(String codBandoProgramma) {
		this.codBandoProgramma = codBandoProgramma;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
}
