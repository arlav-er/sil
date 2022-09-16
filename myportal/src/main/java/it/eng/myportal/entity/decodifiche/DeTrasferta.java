package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.RvTrasferta;
import it.eng.myportal.entity.VaDatiVacancy;

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

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_trasferta", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "findDeTrasferta", query = "SELECT m FROM DeTrasferta m WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) ") })


public class DeTrasferta extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 261258L;
	private String codTrasferta;
	private String descrizione;

	private Set<RvTrasferta> rvTrasfertas = new HashSet<RvTrasferta>(0);
	private Set<CvAltreInfo> cvAltreInfos = new HashSet<CvAltreInfo>(0);
	private Set<VaDatiVacancy> vaDatiVacancies = new HashSet<VaDatiVacancy>(0);

	public DeTrasferta() {
	}

	public DeTrasferta(String codTrasferta, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codTrasferta = codTrasferta;
		this.descrizione = descrizione;
	}

	public DeTrasferta(String codTrasferta, String descrizione, Date dtInizioVal, Date dtFineVal,
			Set<RvTrasferta> rvTrasfertas, Set<CvAltreInfo> cvAltreInfos, Set<VaDatiVacancy> vaDatiVacancies) {
		super(dtInizioVal, dtFineVal);
		this.codTrasferta = codTrasferta;
		this.descrizione = descrizione;
		this.rvTrasfertas = rvTrasfertas;
		this.cvAltreInfos = cvAltreInfos;
		this.vaDatiVacancies = vaDatiVacancies;
	}

	@Id
	@Column(name = "cod_trasferta", unique = true, nullable = false, length = 8)
	public String getCodTrasferta() {
		return this.codTrasferta;
	}

	public void setCodTrasferta(String codTrasferta) {
		this.codTrasferta = codTrasferta;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTrasferta")
	public Set<RvTrasferta> getRvTrasfertas() {
		return this.rvTrasfertas;
	}

	public void setRvTrasfertas(Set<RvTrasferta> rvTrasfertas) {
		this.rvTrasfertas = rvTrasfertas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTrasferta")
	public Set<CvAltreInfo> getCvAltreInfos() {
		return this.cvAltreInfos;
	}

	public void setCvAltreInfos(Set<CvAltreInfo> cvAltreInfos) {
		this.cvAltreInfos = cvAltreInfos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTrasferta")
	public Set<VaDatiVacancy> getVaDatiVacancies() {
		return this.vaDatiVacancies;
	}

	public void setVaDatiVacancies(Set<VaDatiVacancy> vaDatiVacancies) {
		this.vaDatiVacancies = vaDatiVacancies;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codTrasferta == null) ? 0 : codTrasferta.hashCode());
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
		DeTrasferta other = (DeTrasferta) obj;
		if (codTrasferta == null) {
			if (other.codTrasferta != null)
				return false;
		} else if (!codTrasferta.equals(other.codTrasferta))
			return false;
		return true;
	}
	
}
