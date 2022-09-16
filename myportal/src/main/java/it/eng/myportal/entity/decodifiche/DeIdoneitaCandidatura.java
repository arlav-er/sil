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
@Table(name = "de_idoneita_candidatura", schema = "myportal")
public class DeIdoneitaCandidatura extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String codIdoneitaCandidatura;
	private String descrizione;

	public DeIdoneitaCandidatura() {
	}

	public DeIdoneitaCandidatura(String codIdoneitaCandidatura, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codIdoneitaCandidatura = codIdoneitaCandidatura;
		this.descrizione = descrizione;

	}

	@Id
	@Column(name = "cod_idoneita_candidatura", unique = true, nullable = false, length = 2)
	public String getCodIdoneitaCandidatura() {
		return this.codIdoneitaCandidatura;
	}

	public void setCodIdoneitaCandidatura(String codIdoneitaCandidatura) {
		this.codIdoneitaCandidatura = codIdoneitaCandidatura;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}



	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodIdoneitaCandidatura() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeIdoneitaCandidatura)) {
			return false;
		} else {
			return this.getCodIdoneitaCandidatura().equalsIgnoreCase(((DeIdoneitaCandidatura) obj).getCodIdoneitaCandidatura());
		}
	}

	@Override
	public int hashCode() {
		return getCodIdoneitaCandidatura().hashCode();
	}
}
