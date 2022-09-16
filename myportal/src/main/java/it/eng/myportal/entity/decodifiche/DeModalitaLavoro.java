package it.eng.myportal.entity.decodifiche;

// Generated 31-lug-2012 16.32.42 by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_modalita_lavoro", schema = "myportal")
public class DeModalitaLavoro extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 366201L;
	private String codModalitaLavoro;
	private String descrizione;

	public DeModalitaLavoro() {
	}

	public DeModalitaLavoro(String codModalitaLavoro, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codModalitaLavoro = codModalitaLavoro;
		this.descrizione = descrizione;

	}

	@Id
	@Column(name = "cod_modalita_lavoro", unique = true, nullable = false, length = 8)
	public String getCodModalitaLavoro() {
		return this.codModalitaLavoro;
	}

	public void setCodModalitaLavoro(String codModalitaLavoro) {
		this.codModalitaLavoro = codModalitaLavoro;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
