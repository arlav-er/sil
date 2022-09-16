package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_motivo_genere_sil", schema = "mycas")
public class DeMotivoGenereSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = -7511703758015101616L;

	private String codMotivoGenereSil;
	private String descrizione;

	@Id
	@Column(name = "cod_motivo_genere_sil", unique = true, nullable = false, length = 8)
	public String getCodMotivoGenereSil() {
		return codMotivoGenereSil;
	}

	public void setCodMotivoGenereSil(String codMotivoGenereSil) {
		this.codMotivoGenereSil = codMotivoGenereSil;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
