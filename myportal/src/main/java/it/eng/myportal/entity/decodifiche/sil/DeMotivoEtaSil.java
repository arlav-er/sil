package it.eng.myportal.entity.decodifiche.sil;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_motivo_eta_sil", schema = "mycas")
public class DeMotivoEtaSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 5581741369286762696L;

	private String codMotivoEtaSil;
	private String descrizione;

	@Id
	@Column(name = "cod_motivo_eta_sil", unique = true, nullable = false, length = 8)
	public String getCodMotivoEtaSil() {
		return codMotivoEtaSil;
	}

	public void setCodMotivoEtaSil(String codMotivoEtaSil) {
		this.codMotivoEtaSil = codMotivoEtaSil;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
