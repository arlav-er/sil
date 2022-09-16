package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_lingua", schema = "myportal")
public class DeLingua extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeLingua> {
	private static final long serialVersionUID = 761869L;
	private String codLingua;
	private String denominazione;

	@Transient
	private int occurencyRicerca;

	public DeLingua() {
	}

	public DeLingua(String codLingua, String denominazione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codLingua = codLingua;
		this.denominazione = denominazione;

		this.occurencyRicerca = 0;
	}

	@Id
	@Column(name = "cod_lingua", unique = true, nullable = false, length = 3)
	public String getCodLingua() {
		return this.codLingua;
	}

	public void setCodLingua(String codLingua) {
		this.codLingua = codLingua;
	}

	@Column(name = "denominazione", nullable = false, length = 100)
	public String getDenominazione() {
		return this.denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	@Transient
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	@Override
	public int compareTo(DeLingua o) {
		int compareQuantity = o.getOccurencyRicerca();
		// ascending order
		return this.occurencyRicerca - compareQuantity;
	}

	public int compare(DeLingua obj1, DeLingua obj2) {
		Integer p1 = obj1.getOccurencyRicerca();
		Integer p2 = obj2.getOccurencyRicerca();

		if (p1 > p2) {
			return 1;
		} else if (p1 < p2) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodLingua() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeLingua)) {
			return false;
		} else {
			return this.getCodLingua().equalsIgnoreCase(((DeLingua) obj).getCodLingua());
		}
	}

	@Override
	public int hashCode() {
		return getCodLingua().hashCode();
	}
}
