package it.eng.myportal.entity.decodifiche.sil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_patente_sil", schema = "mycas")

@NamedQueries({
	@NamedQuery(name = "DePatenteSilFindSuggestionValide", query = "select q from DePatenteSil as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
})
public class DePatenteSil extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DePatenteSil> {
	private static final long serialVersionUID = 5617825587076170477L;

	private String codPatenteSil;
	private String descrizione;
	private int occurencyRicerca;

	@Id
	@Column(name = "cod_patente_sil", unique = true, nullable = false, length = 8)
	public String getCodPatenteSil() {
		return codPatenteSil;
	}

	public void setCodPatenteSil(String codPatenteSil) {
		this.codPatenteSil = codPatenteSil;
	}

	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	@Override
	public int compareTo(DePatenteSil o) {
		int compareQuantity = o.getOccurencyRicerca();
		// ascending order
		return this.occurencyRicerca - compareQuantity;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodPatenteSil() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DePatenteSil)) {
			return false;
		} else {
			return this.getCodPatenteSil().equalsIgnoreCase(((DePatenteSil) obj).getCodPatenteSil());
		}
	}

	@Override
	public int hashCode() {
		return getCodPatenteSil().hashCode();
	}

}
