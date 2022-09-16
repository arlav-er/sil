package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_autorizzazione_sare", schema = "mycas")
public class DeAutorizzazioneSare extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 84004L;
	private String codAutorizzazioneSare;
	private String descrizione;

	public DeAutorizzazioneSare() {
	}

	public DeAutorizzazioneSare(String codAlbo, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codAutorizzazioneSare = codAlbo;

	}

	@Id
	@Column(name = "cod_autorizzazione_sare", unique = true, nullable = false, length = 8)
	public String getCodAutorizzazioneSare() {
		return this.codAutorizzazioneSare;
	}

	public void setCodAutorizzazioneSare(String codAlbo) {
		this.codAutorizzazioneSare = codAlbo;
	}

	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
