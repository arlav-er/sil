package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_tipo_corso_sil", schema = "myportal")
public class DeTipoCorsoSil extends AbstractDecodeEntity {
	private static final long serialVersionUID = 5127230595115728730L;

	private String codTipoCorsoSil;
	private String descrizione;
	private String codTipoCorsoMin;

	@Id
	@Column(name = "cod_tipo_corso_sil", unique = true, nullable = false, length = 8)
	public String getCodTipoCorsoSil() {
		return codTipoCorsoSil;
	}

	public void setCodTipoCorsoSil(String codTipoCorsoSil) {
		this.codTipoCorsoSil = codTipoCorsoSil;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "cod_tipo_corso_min", length = 8)
	public String getCodTipoCorsoMin() {
		return codTipoCorsoMin;
	}

	public void setCodTipoCorsoMin(String codTipoCorsoMin) {
		this.codTipoCorsoMin = codTipoCorsoMin;
	}

}
