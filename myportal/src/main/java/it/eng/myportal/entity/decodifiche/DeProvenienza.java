package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.VaDatiVacancy;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_provenienza", schema = "myportal")
@NamedQueries(
		@NamedQuery(name = "findByCodMyPortalAndSil", query = "select dp from DeProvenienza dp where codProvenienza = 'MYPORTAL' or codProvenienza like 'SIL%'")
		)
public class DeProvenienza extends AbstractDecodeEntity implements
		java.io.Serializable {
	private static final long serialVersionUID = 551991L;
	private String codProvenienza;
	private String descrizione;
	

	private Set<VaDatiVacancy> vaDatiVacancies = new HashSet<VaDatiVacancy>(0);
	private Set<CvDatiPersonali> cvDatiPersonalis = new HashSet<CvDatiPersonali>(
			0);

	public DeProvenienza() {
	}

	public DeProvenienza(String codProvenienza, String descrizione,
			Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codProvenienza = codProvenienza;
		this.descrizione = descrizione;

	}

	@Id
	@Column(name = "cod_provenienza", unique = true, nullable = false, length = 8)
	public String getCodProvenienza() {
		return this.codProvenienza;
	}

	public void setCodProvenienza(String codProvenienza) {
		this.codProvenienza = codProvenienza;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deProvenienzaVacancy")
	public Set<VaDatiVacancy> getVaDatiVacancies() {
		return this.vaDatiVacancies;
	}

	public void setVaDatiVacancies(Set<VaDatiVacancy> vaDatiVacancies) {
		this.vaDatiVacancies = vaDatiVacancies;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "provenienzaCurriculum")
	public Set<CvDatiPersonali> getCvDatiPersonalis() {
		return cvDatiPersonalis;
	}

	public void setCvDatiPersonalis(Set<CvDatiPersonali> cvDatiPersonalis) {
		this.cvDatiPersonalis = cvDatiPersonalis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codProvenienza == null) ? 0 : codProvenienza.hashCode());
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
		DeProvenienza other = (DeProvenienza) obj;
		if (codProvenienza == null) {
			if (other.codProvenienza != null)
				return false;
		} else if (!codProvenienza.equals(other.codProvenienza))
			return false;
		return true;
	}

}
