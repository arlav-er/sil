package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * RvPatente generated by hbm2java
 */
@Entity
@Table(name = "rv_patente", schema = "myportal")
public class RvPatente extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 566419L;
	private Integer idRvPatente;

	private RvTestata rvTestata;
	private DePatente dePatente;
	private DePatenteSil dePatenteSil;

	public RvPatente() {
	}

	@Id
	@SequenceGenerator(name = "rv_patente_id_rv_patente_seq", sequenceName = "rv_patente_id_rv_patente_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rv_patente_id_rv_patente_seq")
	@Column(name = "id_rv_patente", unique = true, nullable = false)
	public Integer getIdRvPatente() {
		return this.idRvPatente;
	}

	public void setIdRvPatente(Integer idRvPatente) {
		this.idRvPatente = idRvPatente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rv_testata", nullable = false)
	public RvTestata getRvTestata() {
		return this.rvTestata;
	}

	public void setRvTestata(RvTestata rvTestata) {
		this.rvTestata = rvTestata;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_patente")
	public DePatente getDePatente() {
		return this.dePatente;
	}

	public void setDePatente(DePatente dePatente) {
		this.dePatente = dePatente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_patente_sil")
	public DePatenteSil getDePatenteSil() {
		return dePatenteSil;
	}

	public void setDePatenteSil(DePatenteSil dePatenteSil) {
		this.dePatenteSil = dePatenteSil;
	}

}
