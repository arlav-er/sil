package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.OrCorso;
import it.eng.myportal.entity.RvMansione;
import it.eng.myportal.entity.RvTestata;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_mansione", schema = "mycas")
@NamedQueries({
	@NamedQuery(name = "findMansioniLike", query = "select d from DeMansione d where UPPER(d.descrizione) like UPPER(:codLike)"),
	@NamedQuery(name = "findMansioniLikeFigli", query = "select d from DeMansione d where UPPER(d.descrizione) like UPPER(:codLike) and cod_padre is not null")
})
public class DeMansione extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeMansione> {
	private static final long serialVersionUID = 921580L;
	private String codMansione;
	private String descrizione;
	private DeMansione padre;
	private Set<DeMansione> figli = new HashSet<DeMansione>(0);

	private Set<VaDatiVacancy> vaDatiVacancies = new HashSet<VaDatiVacancy>(0);
	private Set<OrCorso> orCorsos = new HashSet<OrCorso>(0);
	private DeMansioneInfo deMansioneInfo;
	private Set<RvTestata> rvTestatas = new HashSet<RvTestata>(0);
	private Set<RvMansione> rvMansiones = new HashSet<RvMansione>(0);
	private Set<DeMansioneSil> deMansioneSils = new HashSet<DeMansioneSil>(0);
	private Set<CvEsperienzeProf> cvEsperienzeProfs = new HashSet<CvEsperienzeProf>(0);

	@Transient
	private int occurencyRicerca;
	
	public DeMansione() {
	}

	public DeMansione(String codMansione, String descrizione, DeMansione codPadre, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codMansione = codMansione;
		this.descrizione = descrizione;
		this.padre = codPadre;
		
		this.occurencyRicerca = 0;
	}

	@Id
	@Column(name = "cod_mansione", unique = true, nullable = false, length = 6)
	public String getCodMansione() {
		return this.codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_padre")
	public DeMansione getPadre() {
		return this.padre;
	}

	public void setPadre(DeMansione padre) {
		this.padre = padre;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre")
	public Set<DeMansione> getFigli() {
		return this.figli;
	}

	public void setFigli(Set<DeMansione> figli) {
		this.figli = figli;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public Set<VaDatiVacancy> getVaDatiVacancies() {
		return this.vaDatiVacancies;
	}

	public void setVaDatiVacancies(Set<VaDatiVacancy> vaDatiVacancies) {
		this.vaDatiVacancies = vaDatiVacancies;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public Set<OrCorso> getOrCorsos() {
		return this.orCorsos;
	}

	public void setOrCorsos(Set<OrCorso> orCorsos) {
		this.orCorsos = orCorsos;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public DeMansioneInfo getDeMansioneInfo() {
		return this.deMansioneInfo;
	}

	public void setDeMansioneInfo(DeMansioneInfo deMansioneInfo) {
		this.deMansioneInfo = deMansioneInfo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public Set<RvTestata> getRvTestatas() {
		return this.rvTestatas;
	}

	public void setRvTestatas(Set<RvTestata> rvTestatas) {
		this.rvTestatas = rvTestatas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public Set<RvMansione> getRvMansiones() {
		return this.rvMansiones;
	}

	public void setRvMansiones(Set<RvMansione> rvMansiones) {
		this.rvMansiones = rvMansiones;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public Set<DeMansioneSil> getDeMansioneSils() {
		return this.deMansioneSils;
	}

	public void setDeMansioneSils(Set<DeMansioneSil> deMansioneSils) {
		this.deMansioneSils = deMansioneSils;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansione")
	public Set<CvEsperienzeProf> getCvEsperienzeProfs() {
		return this.cvEsperienzeProfs;
	}

	public void setCvEsperienzeProfs(Set<CvEsperienzeProf> cvEsperienzeProfs) {
		this.cvEsperienzeProfs = cvEsperienzeProfs;
	}
	
	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}
	@Transient
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	@Override
	public int compareTo(DeMansione o) {
		int compareQuantity = o.getOccurencyRicerca();
		//ascending order
		return this.occurencyRicerca - compareQuantity;
	}
	
	public int compare(DeMansione obj1, DeMansione obj2) {
		Integer p1 = obj1.getOccurencyRicerca();
		Integer p2 = obj2.getOccurencyRicerca();

		if (p1 > p2) {
			return 1;
		}
		else if (p1 < p2) {
			return -1;
		}
		else {
			return 0;
		}
	}   
	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodMansione() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeMansione)) {
			return false;
		} else {
			return this.getCodMansione().equalsIgnoreCase(
					((DeMansione) obj).getCodMansione());
		}
	}

	@Override
	public int hashCode() {
		return getCodMansione().hashCode();
	}
}
