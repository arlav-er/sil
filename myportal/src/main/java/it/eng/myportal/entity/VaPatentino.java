package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * VaPatentino generated by hbm2java
 */
@Entity
@Table(name = "va_patentino", schema = "myportal")

@NamedQueries({
	@NamedQuery(name = "VaPatentinoFindByVaId", query = "select d from VaPatentino d where d.vaDatiVacancy.idVaDatiVacancy = :vaId")
	})
public class VaPatentino extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 303346L;
	private Integer idVaPatentino;

	private DePatentino dePatentino;
	private DePatentinoSil dePatentinoSil;
	private VaDatiVacancy vaDatiVacancy;
	private String opzIndispensabile;

	public VaPatentino() {
	}

	public VaPatentino(Integer idVaPatentino, PfPrincipal pfPrincipalByIdPrincipalMod,
			PfPrincipal pfPrincipalByIdPrincipalIns, DePatentino dePatentino, VaDatiVacancy vaDatiVacancy, Date dtmIns,
			Date dtmMod) {
		super(pfPrincipalByIdPrincipalIns, pfPrincipalByIdPrincipalMod, dtmIns, dtmMod);
		this.idVaPatentino = idVaPatentino;
		this.dePatentino = dePatentino;
		this.vaDatiVacancy = vaDatiVacancy;
	}

	@Id
	@SequenceGenerator(name = "va_patentino_id_va_patentino_seq", sequenceName = "va_patentino_id_va_patentino_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "va_patentino_id_va_patentino_seq")
	@Column(name = "id_va_patentino", unique = true, nullable = false)
	public Integer getIdVaPatentino() {
		return this.idVaPatentino;
	}

	public void setIdVaPatentino(Integer idVaPatentino) {
		this.idVaPatentino = idVaPatentino;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_patentino")
	public DePatentino getDePatentino() {
		return this.dePatentino;
	}

	public void setDePatentino(DePatentino dePatentino) {
		this.dePatentino = dePatentino;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_patentino_sil")
	public DePatentinoSil getDePatentinoSil() {
		return dePatentinoSil;
	}

	public void setDePatentinoSil(DePatentinoSil dePatentinoSil) {
		this.dePatentinoSil = dePatentinoSil;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_va_dati_vacancy", nullable = false)
	public VaDatiVacancy getVaDatiVacancy() {
		return this.vaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

	@Column(name = "opz_indispensabile", length = 1)
	public String getOpzIndispensabile() {
		return opzIndispensabile;
	}

	public void setOpzIndispensabile(String opzIndispensabile) {
		this.opzIndispensabile = opzIndispensabile;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idVaPatentino == null) ? 0 : idVaPatentino.hashCode());
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
		VaPatentino other = (VaPatentino) obj;
		if (idVaPatentino == null) {
			if (other.idVaPatentino != null)
				return false;
		} else if (!idVaPatentino.equals(other.idVaPatentino))
			return false;
		return true;
	}

}
