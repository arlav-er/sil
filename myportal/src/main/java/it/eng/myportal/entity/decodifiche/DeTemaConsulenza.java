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
@Table(name = "de_tema_consulenza", schema = "myportal")
public class DeTemaConsulenza extends AbstractDecodeEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8754452436950988039L;
	private String codTema;
	private String descrizione;
	private String flagCittadino;
	private String flagAzienda;

	public DeTemaConsulenza() {
	}

	public DeTemaConsulenza(String codTema, String descrizione, String flagCittadino, String flagAzienda, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codTema = codTema;
		this.descrizione = descrizione;
		this.flagCittadino = flagCittadino;
		this.flagAzienda = flagAzienda;
	}

	@Id
	@Column(name = "cod_tema", unique = true, nullable = false, length = 8)
	public String getCodTema() {
		return this.codTema;
	}

	public void setCodTema(String codTema) {
		this.codTema = codTema;
	}

	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	@Column(name = "flag_cittadino", nullable = false, length = 1)
	public String getFlagCittadino() {
		return this.flagCittadino;
	}

	public void setFlagCittadino(String flagCittadino) {
		this.flagCittadino = flagCittadino;
	}
	
	@Column(name = "flag_azienda", nullable = false, length = 1)
	public String getFlagAzienda() {
		return this.flagAzienda;
	}

	public void setFlagAzienda(String flagAzienda) {
		this.flagAzienda = flagAzienda;
	}
}
