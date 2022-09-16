package it.eng.myportal.entity.decodifiche;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

/**
 * 
 * @author Rodi A.
 *
 */
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_orario", schema = "myportal")
public class DeOrario extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeOrario> {
	private static final long serialVersionUID = 374039L;
	private String codOrario;
	private String descrizione;
	
	@Transient
	private int occurencyRicerca;

	public DeOrario() {
	}

	public DeOrario(String codOrario, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codOrario = codOrario;
		this.descrizione = descrizione;
		
		this.occurencyRicerca = 0;
	}

	@Id
	@Column(name = "cod_orario", unique = true, nullable = false, length = 8)
	public String getCodOrario() {
		return this.codOrario;
	}

	public void setCodOrario(String codOrario) {
		this.codOrario = codOrario;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
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
	public int compareTo(DeOrario o) {
		int compareQuantity = o.getOccurencyRicerca();
		//ascending order
		return this.occurencyRicerca - compareQuantity;
	}
	
	public int compare(DeOrario obj1, DeOrario obj2) {
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
