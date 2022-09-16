package it.eng.myportal.entity.decodifiche;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_tipo_appuntamento", schema = "myportal")
public class DeTipoAppuntamento extends AbstractDecodeEntity implements java.io.Serializable {

	private static final long serialVersionUID = -5624106888797224742L;
	private String codTipoAppuntamento;
	private String descrizione;

	public DeTipoAppuntamento() {
	}

	public DeTipoAppuntamento(String codTipoAbilitato, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_tipo_appuntamento", unique = true, nullable = false, length = 8)
	public String getCodTipoAppuntamento() {
		return this.codTipoAppuntamento;
	}

	public void setCodTipoAppuntamento(String codTipoAppuntamento) {
		this.codTipoAppuntamento = codTipoAppuntamento;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
