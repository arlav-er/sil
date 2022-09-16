package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_modalita_lingua", schema = "myportal")
public class DeModalitaLingua extends AbstractDecodeEntity {
	private static final long serialVersionUID = -2713642779163790145L;

	private String codModalitaLingua;
	private String descrizione;

	@Id
	@Column(name = "cod_modalita_lingua", unique = true, nullable = false, length = 8)
	public String getCodModalitaLingua() {
		return codModalitaLingua;
	}

	public void setCodModalitaLingua(String codModalitaLingua) {
		this.codModalitaLingua = codModalitaLingua;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
