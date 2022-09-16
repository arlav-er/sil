package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_fb_categoria", schema = "myportal")
public class DeFbCategoria extends AbstractDecodeEntity {
	private static final long serialVersionUID = -8138294524756175233L;

	private String codFbCategoria;
	private String descrizione;

	@Id
	@Column(name = "cod_fb_categoria", unique = true, nullable = false, length = 8)
	public String getCodFbCategoria() {
		return codFbCategoria;
	}

	public void setCodFbCategoria(String codFbCategoria) {
		this.codFbCategoria = codFbCategoria;
	}

	@Column(name = "descrizione", nullable = false, length = 1000)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
