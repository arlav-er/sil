package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_professione", schema = "myportal")
public class DeProfessione extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 36916L;
	private String codProfessione;
	private String descrizione;

	public DeProfessione() {
	}

	public DeProfessione(String codProfessione, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codProfessione = codProfessione;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_professione", unique = true, nullable = false, length = 8)
	public String getCodProfessione() {
		return this.codProfessione;
	}

	public void setCodProfessione(String codProfessione) {
		this.codProfessione = codProfessione;
	}

	@Column(name = "descrizione", nullable = false, length = 250)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
