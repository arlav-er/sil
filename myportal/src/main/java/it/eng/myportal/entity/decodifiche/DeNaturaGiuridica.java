package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import it.eng.myportal.entity.AbstractDecodeEntity;

@Entity
@Table(name = "de_natura_giuridica", schema = "myportal")
public class DeNaturaGiuridica extends AbstractDecodeEntity {

	private static final long serialVersionUID = -5774728177443075247L;

	private String codNaturaGiuridica;
	private String descrizione;

	public DeNaturaGiuridica() {

	}

	@Id
	@Column(name = "cod_natura_giuridica", unique = true, nullable = false, length = 8)
	public String getCodNaturaGiuridica() {
		return codNaturaGiuridica;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return descrizione;
	}

	public void setCodNaturaGiuridica(String codNaturaGiuridica) {
		this.codNaturaGiuridica = codNaturaGiuridica;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
