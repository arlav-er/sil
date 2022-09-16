package it.eng.myportal.entity.decodifiche.min;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "de_posizione_professionale_min", schema = "myportal")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DePosizioneProfessionaleMin extends AbstractDecodeEntity {
	private static final long serialVersionUID = -6473876855160990690L;
	private String codPosizioneProfessionaleMin;
	private String descrizione;

	@Id
	@Column(name = "cod_posizione_professionale_min", unique = true, nullable = false, length = 8)
	public String getCodPosizioneProfessionaleMin() {
		return codPosizioneProfessionaleMin;
	}

	public void setCodPosizioneProfessionaleMin(String codPosizioneProfessionaleMin) {
		this.codPosizioneProfessionaleMin = codPosizioneProfessionaleMin;
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
		result = prime * result
				+ ((codPosizioneProfessionaleMin == null) ? 0 : codPosizioneProfessionaleMin.hashCode());
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
		DePosizioneProfessionaleMin other = (DePosizioneProfessionaleMin) obj;
		if (codPosizioneProfessionaleMin == null) {
			if (other.codPosizioneProfessionaleMin != null)
				return false;
		} else if (!codPosizioneProfessionaleMin.equals(other.codPosizioneProfessionaleMin))
			return false;
		return true;
	}
}
