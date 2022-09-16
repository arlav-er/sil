package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_evasione_rich", schema = "myportal")
public class DeEvasioneRich extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 366662978L;
	private String codEvasione;
	private String descrizione;

	public DeEvasioneRich() {
	}

	 
	@Id
	@Column(name = "cod_evasione", unique = true, nullable = false, length = 4)
	public String getCodEvasione() {
		return this.codEvasione;
	}

	public void setCodEvasione(String codEvasione) {
		this.codEvasione = codEvasione;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codEvasione == null) ? 0 : codEvasione.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeEvasioneRich other = (DeEvasioneRich) obj;
		if (codEvasione == null) {
			if (other.codEvasione != null)
				return false;
		} else if (!codEvasione.equals(other.codEvasione))
			return false;
		return true;
	}
	
}
