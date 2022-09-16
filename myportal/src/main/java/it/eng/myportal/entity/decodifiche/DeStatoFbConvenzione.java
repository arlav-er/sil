package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "de_fb_stato_convenzione", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findDeFbStatoConvenzioneRicercabile", query = "SELECT stato FROM DeStatoFbConvenzione stato WHERE NOT codStatoConv = :codStatoLav") })
public class DeStatoFbConvenzione extends AbstractDecodeEntity implements Serializable {
	private static final long serialVersionUID = 7914305183523080447L;

	private String codStatoConv;
	private String descrizione;

	public DeStatoFbConvenzione() {
	}

	@Id
	@Column(name = "cod_fb_stato_convenzione", length = 8, nullable = false)
	public String getCodStatoConv() {
		return codStatoConv;
	}

	@Column(name = "descrizione", length = 100, nullable = false)
	public String getDescrizione() {
		return descrizione;
	}

	public void setCodStatoConv(String codStatoConv) {
		this.codStatoConv = codStatoConv;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
