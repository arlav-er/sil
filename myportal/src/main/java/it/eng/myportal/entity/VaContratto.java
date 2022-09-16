package it.eng.myportal.entity;

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

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;

/**
 * VaContratto generated by hbm2java
 */
@Entity
@Table(name = "va_contratto", schema = "myportal")
@NamedQueries({ 
	@NamedQuery(name = "VaContrattoFindByVacancyId", query = "select d from VaContratto d where d.vaDatiVacancy.idVaDatiVacancy = :vaId"),
	@NamedQuery(name = "findNonScaduteByIdVaDatiVacancy", query = "select c from VaContratto c where c.deContratto.dtFineVal >= CURRENT_DATE AND c.vaDatiVacancy.idVaDatiVacancy = :idVaDatiVacancy") })
public class VaContratto extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 416947L;
	private Integer idVaContratto;
	private VaDatiVacancy vaDatiVacancy;
	private DeContratto deContratto;
	private DeContrattoSil deContrattoSil;

	public VaContratto() {
	}

	@Id
	@SequenceGenerator(name = "va_contratto_id_va_contratto_seq", sequenceName = "va_contratto_id_va_contratto_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "va_contratto_id_va_contratto_seq")
	@Column(name = "id_va_contratto", unique = true, nullable = false)
	public Integer getIdVaContratto() {
		return this.idVaContratto;
	}

	public void setIdVaContratto(Integer idVaContratto) {
		this.idVaContratto = idVaContratto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_va_dati_vacancy", nullable = false)
	public VaDatiVacancy getVaDatiVacancy() {
		return this.vaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_contratto", nullable = true)//su DB e` cosi dopo mod Trento 2018
	public DeContratto getDeContratto() {
		return this.deContratto;
	}

	public void setDeContratto(DeContratto deContratto) {
		this.deContratto = deContratto;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_contratto_sil", nullable = false)
	public DeContrattoSil getDeContrattoSil() {
		return deContrattoSil;
	}

	public void setDeContrattoSil(DeContrattoSil deContrattoSil) {
		this.deContrattoSil = deContrattoSil;
	}

}
