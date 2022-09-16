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

import it.eng.myportal.entity.decodifiche.DePatentino;

/**
 * @author Turro.
 */
@Entity
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "ma_patentino", schema = "myportal")
public class MaPatentino extends AbstractDecodeEntity implements
		java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private String codPatentinoMin;
	private String codPatentino;
	private DePatentino patentino;

	public MaPatentino() {
	}

	@Column(name = "cod_patentino_min", nullable = false,length=8)
	public String getCodPatentinoMin() {
		return codPatentinoMin;
	}

	public void setCodPatentinoMin(String codPatentinoMin) {
		this.codPatentinoMin = codPatentinoMin;
	}

	@Id
	@Column(name = "cod_patentino", unique = true, nullable = false, length=8)
	public String getCodPatentino() {
		return codPatentino;
	}

	public void setCodPatentino(String codPatentino) {
		this.codPatentino = codPatentino;
	}

	@PrimaryKeyJoinColumn
	@OneToOne(fetch = FetchType.LAZY)
	public DePatentino getPatentino() {
		return patentino;
	}

	public void setPatentino(DePatentino patentino) {
		this.patentino = patentino;
	}
}