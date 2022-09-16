package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_attivita", schema = "mycas")
public class DeAttivita extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeAttivita> {

	private static final long serialVersionUID = 929070L;
	private String codAteco;
	private String descrizione;	
	private DeAttivita padre;
	private Set<DeAttivita> figli = new HashSet<DeAttivita>(0);

	@Transient
	private int occurencyRicerca;
	
	public DeAttivita() {
	}

	public DeAttivita(String codAteco, String descrizione, DeAttivita padre, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codAteco = codAteco;
		this.descrizione = descrizione;
		this.padre = padre;
		
		this.occurencyRicerca = 0;
	}

	@Id
	@Column(name = "cod_ateco", unique = true, nullable = false, length = 8)
	public String getCodAteco() {
		return this.codAteco;
	}

	public void setCodAteco(String codAteco) {
		this.codAteco = codAteco;
	}

	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_padre",nullable=false)
	public DeAttivita getPadre() {
		return this.padre;
	}

	public void setPadre(DeAttivita padre) {
		this.padre = padre;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre")
	public Set<DeAttivita> getFigli() {
		return this.figli;
	}

	public void setFigli(Set<DeAttivita> figli) {
		this.figli = figli;
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
	public int compareTo(DeAttivita o) {
		int compareQuantity = o.getOccurencyRicerca();
		//ascending order
		return this.occurencyRicerca - compareQuantity;
	}
	
	public int compare(DeAttivita obj1, DeAttivita obj2) {
		Integer p1 = obj1.getOccurencyRicerca();
		Integer p2 = obj2.getOccurencyRicerca();

		if (p1 > p2) {
			return 1;
		}
		else if (p1 < p2) {
			return -1;
		}
		else {
			return 0;
		}
	}   
}
