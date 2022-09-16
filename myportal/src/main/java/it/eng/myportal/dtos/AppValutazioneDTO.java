package it.eng.myportal.dtos;

import java.io.Serializable;

/**
 * DTO per le valutazioni da App
 * 
 * @author
 * 
 */
public class AppValutazioneDTO extends AbstractUpdatablePkDTO implements IUpdatable, Serializable {

	private static final long serialVersionUID = -3363643873395003109L;

	private Short numStelle;
	private String messaggio;
	private Integer idPfPrincipalMitt;
	private String email;

	public AppValutazioneDTO() {
	}

	public Short getNumStelle() {
		return numStelle;
	}

	public void setNumStelle(Short numStelle) {
		this.numStelle = numStelle;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public Integer getIdPfPrincipalMitt() {
		return idPfPrincipalMitt;
	}

	public void setIdPfPrincipalMitt(Integer idPfPrincipalMitt) {
		this.idPfPrincipalMitt = idPfPrincipalMitt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
