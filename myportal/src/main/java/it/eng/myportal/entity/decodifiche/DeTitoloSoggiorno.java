package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_titolo_soggiorno", schema = "myportal")
public class DeTitoloSoggiorno extends AbstractDecodeEntity implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5553645102378027109L;
	private String codTitoloSoggiorno;
	private String descrizione;
	
	public DeTitoloSoggiorno() {
		
	}
	

	public DeTitoloSoggiorno(String codTitoloSoggiorno, String descrizione) {
		super();
		this.codTitoloSoggiorno = codTitoloSoggiorno;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_titolo_soggiorno", unique = true, nullable = false,length=8)
	public String getCodTitoloSoggiorno() {
		return codTitoloSoggiorno;
	}

	@Column(name = "descrizione")
	public String getDescrizione() {
		return descrizione;
	}

	public void setCodTitoloSoggiorno(String codTitoloSoggiorno) {
		this.codTitoloSoggiorno = codTitoloSoggiorno;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
