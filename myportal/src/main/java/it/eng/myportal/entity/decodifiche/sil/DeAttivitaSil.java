package it.eng.myportal.entity.decodifiche.sil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_attivita_sil", schema = "mycas")
public class DeAttivitaSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 899005L;
	private String codAttivitaSil;
	private DeAttivita deAttivita;
	private DeAttivitaMin deAttivitaMin;

	public DeAttivitaSil() {
	}

	public DeAttivitaSil(String codAttivitaSil, DeAttivita deAttivita, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codAttivitaSil = codAttivitaSil;
		this.deAttivita = deAttivita;

	}

	@Id
	@Column(name = "cod_attivita_sil", unique = true, nullable = false, length = 8)
	public String getCodAttivitaSil() {
		return this.codAttivitaSil;
	}

	public void setCodAttivitaSil(String codAttivitaSil) {
		this.codAttivitaSil = codAttivitaSil;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_ateco", nullable = false)
	public DeAttivita getDeAttivita() {
		return this.deAttivita;
	}

	public void setDeAttivita(DeAttivita deAttivita) {
		this.deAttivita = deAttivita;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_attivita_sil", insertable = false, updatable = false)
	public DeAttivitaMin getDeAttivitaMin() {
		return deAttivitaMin;
	}

	public void setDeAttivitaMin(DeAttivitaMin deAttivitaMin) {
		this.deAttivitaMin = deAttivitaMin;
	}

}
