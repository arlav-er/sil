package it.eng.myportal.entity.decodifiche.min;

// Generated 31-lug-2012 16.32.42 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.VaDatiVacancy;

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
@Table(name = "de_attivita_min", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findDeAttivitaMinByCodAttivitaAndSuggestion", query = "SELECT padre FROM DeAttivitaSil sil JOIN sil.deAttivitaMin padre WHERE sil.deAttivita.codAteco = :codAttivita AND UPPER(padre.descrizione) LIKE UPPER(:par) AND padre.padre IS NOT NULL"),
		@NamedQuery(name = "findDeAttivitaMinByCodAttivita", query = "SELECT padre FROM DeAttivitaSil sil JOIN sil.deAttivitaMin padre WHERE sil.deAttivita.codAteco = :codAttivita AND padre.padre IS NOT NULL"),
		@NamedQuery(name = "findDeAttivitaMinByCodPadreAndSuggestion", query = "SELECT figlio FROM DeAttivitaMin figlio JOIN figlio.padre padre WHERE padre.codAttivitaMin = :codAttivitaMin AND UPPER(figlio.descrizione) LIKE UPPER(:par)"),
		@NamedQuery(name = "findDeAttivitaMinByCodPadre", query = "SELECT figlio FROM DeAttivitaMin figlio JOIN figlio.padre padre WHERE padre.codAttivitaMin = :codAttivitaMin") })
public class DeAttivitaMin extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 717426L;
	private String codAttivitaMin;
	private String codAttivitaDot;
	private String descrizione;
	private DeAttivitaMin padre;
	private Set<DeAttivitaMin> figli = new HashSet<DeAttivitaMin>(0);
	private Set<VaDatiVacancy> datiVacancy = new HashSet<VaDatiVacancy>(0);

	public DeAttivitaMin() {
	}

	@Id
	@Column(name = "cod_attivita_min", unique = true, nullable = false, length = 8)
	public String getCodAttivitaMin() {
		return codAttivitaMin;
	}

	public void setCodAttivitaMin(String codAttivitaMin) {
		this.codAttivitaMin = codAttivitaMin;
	}

	@Column(name = "cod_attivita_dot", length = 10)
	public String getCodAttivitaDot() {
		return this.codAttivitaDot;
	}

	public void setCodAttivitaDot(String codAttivitaDot) {
		this.codAttivitaDot = codAttivitaDot;
	}

	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_attivita_padre")
	public DeAttivitaMin getPadre() {
		return padre;
	}

	public void setPadre(DeAttivitaMin padre) {
		this.padre = padre;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre")
	public Set<DeAttivitaMin> getFigli() {
		return figli;
	}

	public void setFigli(Set<DeAttivitaMin> figli) {
		this.figli = figli;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "attivitaMin")
	public Set<VaDatiVacancy> getDatiVacancy() {
		return datiVacancy;
	}

	public void setDatiVacancy(Set<VaDatiVacancy> datiVacancy) {
		this.datiVacancy = datiVacancy;
	}
}
