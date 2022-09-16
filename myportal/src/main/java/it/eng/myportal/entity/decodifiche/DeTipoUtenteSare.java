package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_tipo_utente_sare", schema = "myportal")
public class DeTipoUtenteSare extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 84004L;
	private String codTipoUtenteSare;
	private String descrizione;

	public DeTipoUtenteSare() {
	}

	public DeTipoUtenteSare(String codTipoUtenteSare, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codTipoUtenteSare = codTipoUtenteSare;
	}

	@Id
	@Column(name = "cod_tipo_utente_sare", unique = true, nullable = false, length = 1)
	public String getCodTipoUtenteSare() {
		return this.codTipoUtenteSare;
	}

	@Column(name = "descrizione", length = 100, nullable=false)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setCodTipoUtenteSare(String codTipoUtenteSare) {
		this.codTipoUtenteSare = codTipoUtenteSare;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
