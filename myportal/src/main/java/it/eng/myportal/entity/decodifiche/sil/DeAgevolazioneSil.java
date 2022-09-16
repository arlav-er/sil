package it.eng.myportal.entity.decodifiche.sil;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_agevolazione_sil", schema = "mycas")
public class DeAgevolazioneSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 6358895061355442602L;

	private String codAgevolazioneSil;
	private String descrizione;

	@Id
	@Column(name = "cod_agevolazione_sil", unique = true, nullable = false, length = 8)
	public String getCodAgevolazioneSil() {
		return codAgevolazioneSil;
	}

	public void setCodAgevolazioneSil(String codAgevolazioneSil) {
		this.codAgevolazioneSil = codAgevolazioneSil;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
