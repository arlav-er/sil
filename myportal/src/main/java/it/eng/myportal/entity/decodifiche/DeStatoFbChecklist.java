package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_fb_stato_checklist", schema = "myportal")
public class DeStatoFbChecklist extends AbstractDecodeEntity {
	private static final long serialVersionUID = -3500610464124243518L;

	private String codStatoFbChecklist;
	private String descrizione;

	@Id
	@Column(name = "cod_fb_stato_checklist", unique = true, nullable = false, length = 8)
	public String getCodStatoFbChecklist() {
		return codStatoFbChecklist;
	}

	public void setCodStatoFbChecklist(String codStatoFbChecklist) {
		this.codStatoFbChecklist = codStatoFbChecklist;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
