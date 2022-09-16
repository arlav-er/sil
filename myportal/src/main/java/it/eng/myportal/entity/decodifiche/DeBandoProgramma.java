package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name="de_bando_programma"
    ,schema="myportal"
)
public class DeBandoProgramma extends AbstractDecodeEntity {

	private static final long serialVersionUID = 6298638003259430866L;
	
	private String codBandoProgramma;
	private String descrizione;
	
	//Costruttore
	public DeBandoProgramma(){}
	
	//Getter and setter
	@Id
	@Column(name = "cod_bando_programma", unique = true, nullable = false, length = 8)
	public String getCodBandoProgramma() {
		return codBandoProgramma;
	}
	
	public void setCodBandoProgramma(String codBandoProgramma) {
		this.codBandoProgramma = codBandoProgramma;
	}
	
	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return descrizione;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	//HashCode and Equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codBandoProgramma == null) ? 0 : codBandoProgramma.hashCode());
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
		DeBandoProgramma other = (DeBandoProgramma) obj;
		if (codBandoProgramma == null) {
			if (other.codBandoProgramma != null)
				return false;
		} else if (!codBandoProgramma.equals(other.codBandoProgramma))
			return false;
		return true;
	}
	
}
