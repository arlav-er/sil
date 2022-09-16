package it.eng.myportal.entity.decodifiche;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.MaPatente;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_patente", schema = "myportal")

@NamedQueries({
		@NamedQuery(name = "DePatenteFindSuggestionValide", query = "select q from DePatente as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
})
public class DePatente extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DePatente> {
	private static final long serialVersionUID = 778109L;
	private String codPatente;
	private String descrizione;
	private MaPatente maPatente;

	@Transient
	private int occurencyRicerca;

	public DePatente() {
	}

	public DePatente(String codPatente, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codPatente = codPatente;

		this.occurencyRicerca = 0;
	}

	@Id
	@Column(name = "cod_patente", unique = true, nullable = false, length = 8)
	public String getCodPatente() {
		return this.codPatente;
	}

	public void setCodPatente(String codPatente) {
		this.codPatente = codPatente;
	}

	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "patente")
	public MaPatente getMaPatente() {
		return maPatente;
	}

	public void setMaPatente(MaPatente maPatente) {
		this.maPatente = maPatente;
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
	public int compareTo(DePatente o) {
		int compareQuantity = o.getOccurencyRicerca();
		// ascending order
		return this.occurencyRicerca - compareQuantity;
	}

	public int compare(DePatente obj1, DePatente obj2) {
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
		if (obj == null || this.getCodPatente() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DePatente)) {
			return false;
		} else {
			return this.getCodPatente().equals(((DePatente) obj).getCodPatente());
		}
	}

	@Override
	public int hashCode() {
		return getCodPatente().hashCode();
	}
}
