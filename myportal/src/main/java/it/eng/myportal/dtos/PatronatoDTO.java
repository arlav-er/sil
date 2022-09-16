package it.eng.myportal.dtos;




public class PatronatoDTO extends AbstractUpdatablePkDTO {
	
	private static final long serialVersionUID = 1615195624709382694L;
	private Integer idPfPrincipal;
	private DeProvinciaDTO provinciaRif;
	private String denominazione;
    
	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}
	
	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}
	
	public DeProvinciaDTO getProvinciaRif() {
		return provinciaRif;
	}
	
	public void setProvinciaRif(DeProvinciaDTO provinciaRif) {
		this.provinciaRif = provinciaRif;
	}
	
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
}
