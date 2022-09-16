package it.eng.myportal.entity.decodifiche.min;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "de_condizione_occupaz_min", schema = "myportal")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DeCondizioneOccupazMin extends AbstractDecodeEntity {
	private static final long serialVersionUID = 8713178316975663787L;

	private String codCondizioneOccupazMin;
	private String descrizione;

	@Id
	@Column(name = "cod_condizione_occupaz_min", unique = true, nullable = false, length = 8)
	public String getCodCondizioneOccupazMin() {
		return codCondizioneOccupazMin;
	}

	public void setCodCondizioneOccupazMin(String codCondizioneOccupazMin) {
		this.codCondizioneOccupazMin = codCondizioneOccupazMin;
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
		result = prime * result + ((codCondizioneOccupazMin == null) ? 0 : codCondizioneOccupazMin.hashCode());
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
		DeCondizioneOccupazMin other = (DeCondizioneOccupazMin) obj;
		if (codCondizioneOccupazMin == null) {
			if (other.codCondizioneOccupazMin != null)
				return false;
		} else if (!codCondizioneOccupazMin.equals(other.codCondizioneOccupazMin))
			return false;
		return true;
	}
}
