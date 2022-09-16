package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_motivo_cessazione", schema = "myportal")
public class DeMotivoCessazione extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = -3742263863708789083L;

	private String codMotivoCessazione;
	private String descrizione;

	@Id
	@Column(name = "cod_motivo_cessazione", unique = true, nullable = false, length = 2)
	public String getCodMotivoCessazione() {
		return codMotivoCessazione;
	}

	public void setCodMotivoCessazione(String codMotivoCessazione) {
		this.codMotivoCessazione = codMotivoCessazione;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
