package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DeTrasferta;

/**
 * CvAltreInfo generated by hbm2java
 */
@Entity
@Table(name = "cv_altre_info", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "CvAltreInfoByCvId", query = "select d from CvAltreInfo d where d.cvDatiPersonali.idCvDatiPersonali = :cvId")
})
public class CvAltreInfo extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 865108L;
	private Integer idCvDatiPersonali;

	private CvDatiPersonali cvDatiPersonali;
	private DeTrasferta deTrasferta;
	private Boolean flagAutomunito;
	private Boolean flagMotomunito;
	private String ulterioriInfo;
	private String limitazioni;
	private Boolean flagL68Art1;
	private Boolean flagL68Art18;	
//	private DeAgevolazione deAgevolazione;

	public CvAltreInfo() {
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "cvDatiPersonali"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_cv_dati_personali", unique = true, nullable = false)
	public Integer getIdCvDatiPersonali() {
		return this.idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public CvDatiPersonali getCvDatiPersonali() {
		return this.cvDatiPersonali;
	}

	public void setCvDatiPersonali(CvDatiPersonali cvDatiPersonali) {
		this.cvDatiPersonali = cvDatiPersonali;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_trasferta")
	public DeTrasferta getDeTrasferta() {
		return this.deTrasferta;
	}

	public void setDeTrasferta(DeTrasferta deTrasferta) {
		this.deTrasferta = deTrasferta;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_automunito", nullable = false, length = 1)
	public Boolean getFlagAutomunito() {
		return this.flagAutomunito;
	}

	public void setFlagAutomunito(Boolean flagAutomunito) {
		this.flagAutomunito = flagAutomunito;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_motomunito", nullable = false, length = 1)
	public Boolean getFlagMotomunito() {
		return this.flagMotomunito;
	}

	public void setFlagMotomunito(Boolean flagMotomunito) {
		this.flagMotomunito = flagMotomunito;
	}

	@Column(name = "ulteriori_info", length = 3000)
	public String getUlterioriInfo() {
		return this.ulterioriInfo;
	}

	public void setUlterioriInfo(String ulterioriInfo) {
		this.ulterioriInfo = ulterioriInfo;
	}

	@Column(name = "limitazioni", length = 3000)
	public String getLimitazioni() {
		return limitazioni;
	}

	public void setLimitazioni(String limitazioni) {
		this.limitazioni = limitazioni;
	}
	
	@Type(type = "yes_no")
	@Column(name = "flag_l68_art1", length = 1)
	public Boolean getFlagL68Art1() {
		return flagL68Art1;
	}

	public void setFlagL68Art1(Boolean flagL68Art1) {
		this.flagL68Art1 = flagL68Art1;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_l68_art18", length = 1)
	public Boolean getFlagL68Art18() {
		return flagL68Art18;
	}

	public void setFlagL68Art18(Boolean flagL68Art18) {
		this.flagL68Art18 = flagL68Art18;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCvDatiPersonali == null) ? 0 : idCvDatiPersonali.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CvAltreInfo other = (CvAltreInfo) obj;
		if (idCvDatiPersonali == null) {
			if (other.idCvDatiPersonali != null)
				return false;
		} else if (!idCvDatiPersonali.equals(other.idCvDatiPersonali))
			return false;
		return true;
	}
	
}
