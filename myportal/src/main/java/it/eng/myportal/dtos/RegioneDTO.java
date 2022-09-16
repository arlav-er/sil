package it.eng.myportal.dtos;


/**
 * Classe contenitore delle informazioni su una Regione in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author Rodi A.
 *
 */
public class RegioneDTO extends AbstractUpdatablePkDTO {
	private Integer idPfPrincipal;
	private DeRegioneDTO regione;
	private String stileSelezionato;
	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}
	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}
	public DeRegioneDTO getRegione() {
		return regione;
	}
	public void setRegione(DeRegioneDTO regione) {
		this.regione = regione;
	}
	public String getStileSelezionato() {
		return stileSelezionato;
	}
	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}
	
	
}
