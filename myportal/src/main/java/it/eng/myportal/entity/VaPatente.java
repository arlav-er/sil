package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;

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
 * VaPatente generated by hbm2java
 */
@Entity
@Table(name = "va_patente", schema = "myportal")

@NamedQueries({
	@NamedQuery(name = "VaPatenteFindByVaId", query = "select d from VaPatente d where d.vaDatiVacancy.idVaDatiVacancy = :vaId")
	})

public class VaPatente extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 690654L;
	private Integer idVaPatente;

	private DePatente dePatente;
	private DePatenteSil dePatenteSil;
	private VaDatiVacancy vaDatiVacancy;
	private String opzIndispensabile;

	public VaPatente() {
	}

	public VaPatente(Integer idVaPatente, PfPrincipal pfPrincipalByIdPrincipalMod,
			PfPrincipal pfPrincipalByIdPrincipalIns, DePatente dePatente, VaDatiVacancy vaDatiVacancy, Date dtmIns,
			Date dtmMod) {
		super(pfPrincipalByIdPrincipalIns, pfPrincipalByIdPrincipalMod, dtmIns, dtmMod);
		this.idVaPatente = idVaPatente;

		this.dePatente = dePatente;
		this.vaDatiVacancy = vaDatiVacancy;

	}

	@Id
	@SequenceGenerator(name = "va_patente_id_va_patente_seq", sequenceName = "va_patente_id_va_patente_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "va_patente_id_va_patente_seq")
	@Column(name = "id_va_patente", unique = true, nullable = false)
	public Integer getIdVaPatente() {
		return this.idVaPatente;
	}

	public void setIdVaPatente(Integer idVaPatente) {
		this.idVaPatente = idVaPatente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_patente")
	public DePatente getDePatente() {
		return this.dePatente;
	}

	public void setDePatente(DePatente dePatente) {
		this.dePatente = dePatente;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_patente_sil")
	public DePatenteSil getDePatenteSil() {
		return dePatenteSil;
	}

	public void setDePatenteSil(DePatenteSil dePatenteSil) {
		this.dePatenteSil = dePatenteSil;
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
		result = prime * result + ((idVaPatente == null) ? 0 : idVaPatente.hashCode());
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
		VaPatente other = (VaPatente) obj;
		if (idVaPatente == null) {
			if (other.idVaPatente != null)
				return false;
		} else if (!idVaPatente.equals(other.idVaPatente))
			return false;
		return true;
	}
}
