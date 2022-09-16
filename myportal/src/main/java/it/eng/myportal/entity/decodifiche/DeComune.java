package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_comune", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findNazioneValidaBySuggestion", query = "SELECT com FROM DeComune com WHERE com.deProvincia.codProvincia IN ('254', '255') AND UPPER(com.denominazione) LIKE UPPER(:par) AND com.dtInizioVal < :data AND com.dtFineVal > :data") })
public class DeComune extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 346112L;

	private String codCom;
	private DeProvincia deProvincia;
	private String denominazione;
	private String cap;
	private Integer numPopolazione;
	private DeCpi deCpi;

	public DeComune() {
	}

	@Transient
	public boolean isStato() {
		if (codCom.startsWith("Z")) {
			return true;
		} else {
			return false;
		}
	}

	@Id
	@Column(name = "cod_com", unique = true, nullable = false, length = 4)
	public String getCodCom() {
		return this.codCom;
	}

	public void setCodCom(String codCom) {
		this.codCom = codCom;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_provincia", nullable = false)
	public DeProvincia getDeProvincia() {
		return this.deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	@Column(name = "denominazione", nullable = false, length = 100)
	public String getDenominazione() {
		return this.denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	@Column(name = "cap", length = 5)
	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	@Column(name = "num_popolazione", nullable = false)
	public Integer getNumPopolazione() {
		return this.numPopolazione;
	}

	public void setNumPopolazione(Integer numPopolazione) {
		this.numPopolazione = numPopolazione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_cpi", nullable = false)
	public DeCpi getDeCpi() {
		return deCpi;
	}

	public void setDeCpi(DeCpi deCpi) {
		this.deCpi = deCpi;
	}

	@Transient
	public String getStrDenominazioneConTarga() {
		if (getDeProvincia() != null && getDeProvincia().getTarga() != null && !"".equals(getDeProvincia().getTarga())) {
			return denominazione + " (" + getDeProvincia().getTarga() + ")";
		} else {
			return denominazione;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodCom() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeComune)) {
			return false;
		} else {
			return this.getCodCom().equalsIgnoreCase(((DeComune) obj).getCodCom());
		}
	}

	@Override
	public int hashCode() {
		return getCodCom().hashCode();
	}
}
