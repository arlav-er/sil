package it.eng.myportal.entity.decodifiche.min;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "de_cittadinanza_min", schema = "myportal")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DeCittadinanzaMin extends AbstractDecodeEntity {
	private static final long serialVersionUID = 3622556189388304026L;

	private String codCittadinanzaMin;
	private String descrizione;

	@Id
	@Column(name = "cod_cittadinanza_min", unique = true, nullable = false, length = 8)
	public String getCodCittadinanzaMin() {
		return codCittadinanzaMin;
	}

	public void setCodCittadinanzaMin(String codCittadinanzaMin) {
		this.codCittadinanzaMin = codCittadinanzaMin;
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
		result = prime * result + ((codCittadinanzaMin == null) ? 0 : codCittadinanzaMin.hashCode());
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
		DeCittadinanzaMin other = (DeCittadinanzaMin) obj;
		if (codCittadinanzaMin == null) {
			if (other.codCittadinanzaMin != null)
				return false;
		} else if (!codCittadinanzaMin.equals(other.codCittadinanzaMin))
			return false;
		return true;
	}

}
