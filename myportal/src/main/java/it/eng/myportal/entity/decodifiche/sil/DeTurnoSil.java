package it.eng.myportal.entity.decodifiche.sil;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_turno_sil", schema = "mycas")
public class DeTurnoSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = -2051600824254484076L;

	private String codTurnoSil;
	private String descrizione;

	@Id
	@Column(name = "cod_turno_sil", unique = true, nullable = false, length = 8)
	public String getCodTurnoSil() {
		return codTurnoSil;
	}

	public void setCodTurnoSil(String codTurnoSil) {
		this.codTurnoSil = codTurnoSil;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
