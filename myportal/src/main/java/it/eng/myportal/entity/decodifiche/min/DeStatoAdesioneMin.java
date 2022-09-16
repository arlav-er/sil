package it.eng.myportal.entity.decodifiche.min;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_stato_adesione_min", schema = "myportal")
public class DeStatoAdesioneMin extends AbstractDecodeEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = -6319340640495077625L;
	
	private String codStatoAdesioneMin;
	private String descrizione;
	private String codMonoAttiva;
	
	public DeStatoAdesioneMin() {
	}

	@Id
	@Column(name = "cod_stato_adesione_min", unique = true, nullable = false, length = 8)
	public String getCodStatoAdesioneMin() {
		return codStatoAdesioneMin;
	}

	public void setCodStatoAdesioneMin(String codStatoAdesioneMin) {
		this.codStatoAdesioneMin = codStatoAdesioneMin;
	}
	
	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "cod_mono_attiva", length = 1)
	public String getCodMonoAttiva() {
		return codMonoAttiva;
	}

	public void setCodMonoAttiva(String codMonoAttiva) {
		this.codMonoAttiva = codMonoAttiva;
	}
	
}
