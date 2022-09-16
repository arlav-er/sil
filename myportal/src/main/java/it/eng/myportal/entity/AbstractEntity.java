package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * AcCandidatura generated by hbm2java
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEntity implements java.io.Serializable, IEntity {
	private static final long serialVersionUID = 655003L;

	private PfPrincipal pfPrincipalMod;

	private PfPrincipal pfPrincipalIns;

	private Date dtmIns;

	private Date dtmMod;

	public AbstractEntity() {
	}

	public AbstractEntity(PfPrincipal pfPrincipalIns, PfPrincipal pfPrincipalMod, Date dtmIns, Date dtmMod) {
		this.pfPrincipalIns = pfPrincipalIns;
		this.pfPrincipalMod = pfPrincipalMod;
		this.dtmIns = dtmIns;
		this.dtmMod = dtmMod;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_principal_mod", nullable = false)
	public PfPrincipal getPfPrincipalMod() {
		return this.pfPrincipalMod;
	}

	public void setPfPrincipalMod(PfPrincipal pfPrincipalByIdPrincipalMod) {
		this.pfPrincipalMod = pfPrincipalByIdPrincipalMod;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_principal_ins", nullable = false)
	public PfPrincipal getPfPrincipalIns() {
		return this.pfPrincipalIns;
	}

	public void setPfPrincipalIns(PfPrincipal pfPrincipalByIdPrincipalIns) {
		this.pfPrincipalIns = pfPrincipalByIdPrincipalIns;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_ins", nullable = false, length = 29)
	public Date getDtmIns() {
		return this.dtmIns;
	}

	public void setDtmIns(Date dtmIns) {
		this.dtmIns = dtmIns;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dtm_mod", nullable = false, length = 29)
	public Date getDtmMod() {
		return this.dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

	@PrePersist
	protected void onCreate() {
		dtmIns = dtmMod = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		dtmMod = new Date();
	}
}
