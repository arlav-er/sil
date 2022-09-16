package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_stato_titolo", schema = "myportal")
public class DeStatoTitolo extends AbstractDecodeEntity {
	private static final long serialVersionUID = 3286799405657332303L;

	private String codStatoTitolo;
	private String descrizione;

	@Id
	@Column(name = "cod_stato_titolo", unique = true, nullable = false, length = 8)
	public String getCodStatoTitolo() {
		return codStatoTitolo;
	}

	public void setCodStatoTitolo(String codStatoTitolo) {
		this.codStatoTitolo = codStatoTitolo;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
