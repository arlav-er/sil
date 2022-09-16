package it.eng.myportal.dtos;

import it.eng.myportal.entity.WsEndpoint;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverResponse;

public class VerificaRequisitiGaranziaOverResponseDTO {

	private byte codice;
	private String descrizione;
	//Campo di supporto
	private WsEndpoint resultFromWhichEndpoint;
	
	//Costruttore
	public VerificaRequisitiGaranziaOverResponseDTO(){
		
	}
	
	//Metodi di supporto
	public VerificaRequisitiGaranziaOverResponseDTO toDTO(VerificaRequisitiGaranziaOverResponse response){
		this.setCodice(response.getCodice());
		this.setDescrizione(response.getDescrizione());
		return this;
	}

	//Get and set
	public byte getCodice() {
		return codice;
	}


	public void setCodice(byte codice) {
		this.codice = codice;
	}


	public String getDescrizione() {
		return descrizione;
	}


	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public WsEndpoint getResultFromWhichEndpoint() {
		return resultFromWhichEndpoint;
	}


	public void setResultFromWhichEndpoint(WsEndpoint resultFromWhichEndpoint) {
		this.resultFromWhichEndpoint = resultFromWhichEndpoint;
	}
	
}
