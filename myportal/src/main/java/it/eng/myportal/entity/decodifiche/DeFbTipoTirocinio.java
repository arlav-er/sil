package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_fb_tipo_tirocinio", schema = "myportal")
public class DeFbTipoTirocinio extends AbstractDecodeEntity {
	private static final long serialVersionUID = 743339227393490494L;

	private String codFbTipoTirocinio;
	private String descrizione;

	@Id
	@Column(name = "cod_fb_tipo_tirocinio", unique = true, nullable = false, length = 8)
	public String getCodFbTipoTirocinio() {
		return codFbTipoTirocinio;
	}

	public void setCodFbTipoTirocinio(String codFbTipoTirocinio) {
		this.codFbTipoTirocinio = codFbTipoTirocinio;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
