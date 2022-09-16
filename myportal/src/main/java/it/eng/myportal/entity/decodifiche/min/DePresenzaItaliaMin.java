package it.eng.myportal.entity.decodifiche.min;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "de_presenza_italia_min", schema = "myportal")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DePresenzaItaliaMin extends AbstractDecodeEntity {
	private static final long serialVersionUID = 6362590488490133981L;

	private String codPresenzaItaliaMin;
	private String descrizione;

	@Id
	@Column(name = "cod_presenza_italia_min", unique = true, nullable = false, length = 8)
	public String getCodPresenzaItaliaMin() {
		return codPresenzaItaliaMin;
	}

	public void setCodPresenzaItaliaMin(String codPresenzaItaliaMin) {
		this.codPresenzaItaliaMin = codPresenzaItaliaMin;
	}

	@Column(name = "descrizione", nullable = false, length = 2000)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codPresenzaItaliaMin == null) ? 0 : codPresenzaItaliaMin.hashCode());
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
		DePresenzaItaliaMin other = (DePresenzaItaliaMin) obj;
		if (codPresenzaItaliaMin == null) {
			if (other.codPresenzaItaliaMin != null)
				return false;
		} else if (!codPresenzaItaliaMin.equals(other.codPresenzaItaliaMin))
			return false;
		return true;
	}
}
