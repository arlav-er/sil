package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_fb_stato_scheda", schema = "myportal")
public class DeStatoFbScheda extends AbstractDecodeEntity {
	private static final long serialVersionUID = 1664657580489234151L;

	private String codStatoFbScheda;
	private String descrizione;

	@Id
	@Column(name = "cod_fb_stato_scheda", unique = true, nullable = false, length = 8)
	public String getCodStatoFbScheda() {
		return codStatoFbScheda;
	}

	public void setCodStatoFbScheda(String codStatoFbScheda) {
		this.codStatoFbScheda = codStatoFbScheda;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
