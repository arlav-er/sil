package it.eng.myportal.entity.decodifiche;

// Generated Nov 08, 2017 17:20 PM by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.entity.AbstractDecodeEntity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_fb_tipo_convenzione ", schema = "myportal")
public class DeTipoFbConvenzione extends AbstractDecodeEntity implements Serializable {
	private static final long serialVersionUID = 141033918962238784L;

	private String descrizione;
	private String codTipoConvenzione;

	public DeTipoFbConvenzione() {
	}

	@Column(name = "descrizione", length = 100, nullable = false)
	public String getDescrizione() {
		return descrizione;
	}

	@Id
	@Column(name = "cod_fb_tipo_convenzione", length = 8, nullable = false)
	public String getCodTipoConvenzione() {
		return codTipoConvenzione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setCodTipoConvenzione(String codTipoConvenzione) {
		this.codTipoConvenzione = codTipoConvenzione;
	}

}
