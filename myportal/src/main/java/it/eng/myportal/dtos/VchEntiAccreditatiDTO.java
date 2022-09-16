package it.eng.myportal.dtos;


public class VchEntiAccreditatiDTO extends AbstractUpdatablePkDTO{
	
	private static final long serialVersionUID = 1L;
	private String codiceFiscaleEnte;
	private String codSede;
	private String denominazione;
	private String indirizzo;
	private DeComuneDTO comune;
	
	//Constructor
	public VchEntiAccreditatiDTO(){
		super();
	}
	
	public String getCodiceFiscaleEnte() {
		return codiceFiscaleEnte;
	}
	public void setCodiceFiscaleEnte(String codiceFiscaleEnte) {
		this.codiceFiscaleEnte = codiceFiscaleEnte;
	}
	public String getCodSede() {
		return codSede;
	}
	public void setCodSede(String codSede) {
		this.codSede = codSede;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public DeComuneDTO getComune() {
		return comune;
	}
	public void setComune(DeComuneDTO comune) {
		this.comune = comune;
	}
	
}
