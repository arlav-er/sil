package it.eng.myportal.entity.decodifiche.min;

// Generated 31-lug-2012 16.32.42 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil;

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
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_mansione_min", schema = "mycas")
@NamedQueries({
		@NamedQuery(name = "DeMansioneMinFindSuggestion", query = "select d from DeMansioneMin as d where UPPER(d.descrizione) like UPPER(:query)"),
		@NamedQuery(name = "DeMansioneMinFindSuggestionFigli", query = "select d from DeMansioneMin as d where UPPER(d.descrizione) like UPPER(:query) and cod_mansione_padre is not null"),
		@NamedQuery(name = "DeMansioneMinFindByCodmansione", query = "select d from DeMansioneMin d , DeMansioneSil s where d.deMansioneSil = s.codMansioneSil AND UPPER(s.deMansione) like UPPER(:query)"),
		@NamedQuery(name = "findDeMansioneMinBySuggestionAndCodMansione", query = "SELECT manMin FROM DeMansioneMin manMin JOIN manMin.deMansioneSil sil WHERE sil.deMansione.codMansione = :codMansione AND UPPER(manMin.descrizione) LIKE UPPER(:par)"),
		@NamedQuery(name = "findDeMansioneMinByCodMansione", query = "SELECT manMin FROM DeMansioneMin manMin JOIN manMin.deMansioneSil sil WHERE sil.deMansione.codMansione = :codMansione") })
public class DeMansioneMin extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 717426L;
	private DeMansioneSil deMansioneSil;
	private String codMansioneMin;
	private String descrizione;
	private DeMansioneMin padre;
	private Set<DeMansioneMin> figli = new HashSet<DeMansioneMin>(0);
	private Set<CvEsperienzeProf> cvEsperienzeProfs = new HashSet<CvEsperienzeProf>(0);

	public DeMansioneMin() {
	}

	@Id
	@Column(name = "cod_mansione_min", nullable = false, length = 8)
	public String getCodMansioneMin() {
		return this.codMansioneMin;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deMansioneMin")
	public Set<CvEsperienzeProf> getCvEsperienzeProfs() {
		return this.cvEsperienzeProfs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_mansione_dot", nullable = false)
	public DeMansioneSil getDeMansioneSil() {
		return this.deMansioneSil;
	}

	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre")
	public Set<DeMansioneMin> getFigli() {
		return figli;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_mansione_padre")
	public DeMansioneMin getPadre() {
		return padre;
	}

	public void setCodMansioneMin(String codMansioneMin) {
		this.codMansioneMin = codMansioneMin;
	}

	public void setCvEsperienzeProfs(Set<CvEsperienzeProf> cvEsperienzeProfs) {
		this.cvEsperienzeProfs = cvEsperienzeProfs;
	}

	public void setDeMansioneSil(DeMansioneSil deMansioneSil) {
		this.deMansioneSil = deMansioneSil;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setFigli(Set<DeMansioneMin> figli) {
		this.figli = figli;
	}

	public void setPadre(DeMansioneMin padre) {
		this.padre = padre;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodMansioneMin() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeMansioneMin)) {
			return false;
		} else {
			return this.getCodMansioneMin().equalsIgnoreCase(((DeMansioneMin) obj).getCodMansioneMin());
		}
	}

	@Override
	public int hashCode() {
		return getCodMansioneMin().hashCode();
	}

}
