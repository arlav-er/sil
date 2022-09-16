package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_ws_vch_esito", schema = "myportal")
public class DeWsVchEsito extends AbstractDecodeEntity {

	private static final long serialVersionUID = 1L;
	private String codRisultato;
	private String descrizione;
	private String messaggio;
	
	//Constructor
	public DeWsVchEsito() {
		super();
	}
	
	//Getter and setter
	@Id
	@Column(name = "cod_risultato", unique = true, nullable = false, length = 3)
	public String getCodRisultato() {
		return codRisultato;
	}
	
	public void setCodRisultato(String codRisultato) {
		this.codRisultato = codRisultato;
	}
	
	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return descrizione;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "messaggio", length = 100, nullable=false)
	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}
	
	
}
