package it.eng.myportal.entity.decodifiche.sil;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_area_sil", schema = "mycas")
public class DeAreaSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 5267519379386706515L;

	private String codAreaSil;
	private String descrizione;

	@Id
	@Column(name = "cod_area_sil", unique = true, nullable = false, length = 8)
	public String getCodAreaSil() {
		return codAreaSil;
	}

	public void setCodAreaSil(String codAreaSil) {
		this.codAreaSil = codAreaSil;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
