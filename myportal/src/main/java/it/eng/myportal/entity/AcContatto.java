package it.eng.myportal.entity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ac_contatto", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "findAcContattoByCVId", query = "SELECT c FROM AcContatto c WHERE c.acCandidatura.cvDatiPersonali.idCvDatiPersonali = :par")

})
public class AcContatto extends AbstractEntity implements java.io.Serializable {

	private static final long serialVersionUID = 6598236534136652447L;

	private Integer idAcContatto;
	private AcCandidatura acCandidatura;
	private VaDatiVacancy vaDatiVacancy;
	private String emailDestinatario;
	private Date dataContatto;
	
	public AcContatto() {
	}

	@Id
	@SequenceGenerator(name = "ac_contatto_id_ac_contatto_seq", sequenceName = "ac_contatto_id_ac_contatto_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ac_contatto_id_ac_contatto_seq")
	@Column(name = "id_ac_contatto", unique = true, nullable = false)
	public Integer getIdAcContatto() {
		return this.idAcContatto;
	}

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_va_dati_vacancy", nullable = false)
	public VaDatiVacancy getVaDatiVacancy() {
		return this.vaDatiVacancy;
	}


	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

	public void setIdAcContatto(Integer idAcContatto) {
		this.idAcContatto = idAcContatto;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ac_candidatura")
	public AcCandidatura getAcCandidatura() {
		return acCandidatura;
	}

	public void setAcCandidatura(AcCandidatura acCandidatura) {
		this.acCandidatura = acCandidatura;
	}
		

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_contatto", nullable = true, length = 29)
	public Date getDataContatto() {
		return dataContatto;
	}

	public void setDataContatto(Date dataContatto) {
		this.dataContatto = dataContatto;
	}
	@Column(name = "email_destinatario", nullable = true, length = 2000)
	public String getEmailDestinatario() {
		return emailDestinatario;
	}

	public void setEmailDestinatario(String emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idAcContatto == null) ? 0 : idAcContatto.hashCode());
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
		AcContatto other = (AcContatto) obj;
		if (idAcContatto == null) {
			if (other.idAcContatto != null)
				return false;
		} else if (!idAcContatto.equals(other.idAcContatto))
			return false;
		return true;
	}
	
}
