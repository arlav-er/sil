package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_regione", schema = "myportal")
public class DeRegione extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 367748L;
	private String codRegione;
	private String denominazione;
	private String codMin;

	private Set<DeProvincia> deProvincias = new HashSet<DeProvincia>(0);

	public DeRegione() {
	}

	public DeRegione(String codRegione, String denominazione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codRegione = codRegione;
		this.denominazione = denominazione;

	}

	@Id
	@Column(name = "cod_regione", unique = true, nullable = false, length = 8)
	public String getCodRegione() {
		return this.codRegione;
	}

	public void setCodRegione(String codRegione) {
		this.codRegione = codRegione;
	}

	@Column(name = "denominazione", nullable = false, length = 100)
	public String getDenominazione() {
		return this.denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deRegione")
	public Set<DeProvincia> getDeProvincias() {
		return this.deProvincias;
	}

	public void setDeProvincias(Set<DeProvincia> deProvincias) {
		this.deProvincias = deProvincias;
	}

	@Column(name = "cod_min", length = 8)
	public String getCodMin() {
		return this.codMin;
	}

	public void setCodMin(String codMin) {
		this.codMin = codMin;
	}
}
