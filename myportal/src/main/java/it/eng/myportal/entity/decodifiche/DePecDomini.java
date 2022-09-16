package it.eng.myportal.entity.decodifiche;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_pec_domini", schema = "myportal")
public class DePecDomini extends AbstractDecodeEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9041723332839894989L;

	private String codPecDomini;

	private String descrizione;

	public DePecDomini() {
	}

	@Id
	@Column(name = "cod_pec_domini", unique = true, nullable = false, length = 8)
	public String getCodPecDomini() {
		return codPecDomini;
	}

	public void setCodPecDomini(String codPecDomini) {
		this.codPecDomini = codPecDomini;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
