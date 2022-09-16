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
@Table(name = "de_turno", schema = "myportal")
public class DeTurno extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 999239L;

	private String codTurno;
	private String descrizione;

	public DeTurno() {
	}

	public DeTurno(String codTurno, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codTurno = codTurno;
		this.descrizione = descrizione;

	}

	@Id
	@Column(name = "cod_turno", unique = true, nullable = false, length = 8)
	public String getCodTurno() {
		return this.codTurno;
	}

	public void setCodTurno(String codTurno) {
		this.codTurno = codTurno;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
