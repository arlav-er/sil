package it.eng.myportal.entity;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.decodifiche.DePatente;

/**
 * @author Turro.
 */
@Entity
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "ma_patente", schema = "myportal")
public class MaPatente extends AbstractDecodeEntity implements
		java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private String codPatenteMin;
	private String codPatente;
	private DePatente patente;

	public MaPatente() {
	}

	@Column(name = "cod_patente_min", nullable = false,length=8)
	public String getCodPatenteMin() {
		return codPatenteMin;
	}

	public void setCodPatenteMin(String codPatenteMin) {
		this.codPatenteMin = codPatenteMin;
	}

	@Id
	@Column(name = "cod_patente", unique = true, nullable = false, length=8)
	public String getCodPatente() {
		return codPatente;
	}

	public void setCodPatente(String codPatente) {
		this.codPatente = codPatente;
	}

	@PrimaryKeyJoinColumn
	@OneToOne(fetch = FetchType.LAZY)
	public DePatente getPatente() {
		return patente;
	}

	public void setPatente(DePatente patente) {
		this.patente = patente;
	}
}