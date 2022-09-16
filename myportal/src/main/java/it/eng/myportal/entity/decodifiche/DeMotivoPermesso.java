package it.eng.myportal.entity.decodifiche;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_motivo_permesso", schema = "mycas")
public class DeMotivoPermesso extends AbstractDecodeEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = -1277993253580233472L;
	
	private String codMotivoPermesso;
	private String descrizione;
	
	@Transient
	private int occurencyRicerca;

	public DeMotivoPermesso() {
	}

	public DeMotivoPermesso(String codMotivoPermesso, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codMotivoPermesso = codMotivoPermesso;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_motivo_permesso", unique = true, nullable = false, length = 8)
	public String getCodMotivoPermesso() {
		return this.codMotivoPermesso;
	}

	public void setCodMotivoPermesso(String codMotivoPermesso) {
		this.codMotivoPermesso = codMotivoPermesso;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
}
