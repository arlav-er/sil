package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRegione;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * RvTestata generated by hbm2java
 */
@Entity
@Table(name = "rv_testata", schema = "myportal")
public class RvTestata extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 597024L;
	private Integer idRvTestata;
	private PfPrincipal pfPrincipal;

	private DeProvincia deProvincia;
	private DeComune deComune;
	private DeRegione deRegione;
	private DeMansione deMansione;
	private String descrizione;
	private String cosa;
	private String dove;
	private Integer raggioRicerca;

	private Set<RvTrasferta> rvTrasfertas = new HashSet<RvTrasferta>(0);
	private Set<RvLingua> rvLinguas = new HashSet<RvLingua>(0);
	private Set<RvOrario> rvOrarios = new HashSet<RvOrario>(0);
	private Set<RvMansione> rvMansiones = new HashSet<RvMansione>(0);
	private Set<RvAttivita> rvAttivitas = new HashSet<RvAttivita>(0);
	private Set<RvTitolo> rvTitolos = new HashSet<RvTitolo>(0);
	private Set<RvContratto> rvContrattos = new HashSet<RvContratto>(0);
	private Set<RvPatente> rvPatentes = new HashSet<RvPatente>(0);
	private Set<RvAgricolo> rvAgricolos = new HashSet<RvAgricolo>(0);

	public RvTestata() {
	}

	@Id
	@SequenceGenerator(name = "rv_testata_id_rv_testata_seq", sequenceName = "rv_testata_id_rv_testata_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rv_testata_id_rv_testata_seq")
	@Column(name = "id_rv_testata", unique = true, nullable = false)
	public Integer getIdRvTestata() {
		return this.idRvTestata;
	}

	public void setIdRvTestata(Integer idRvTestata) {
		this.idRvTestata = idRvTestata;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia")
	public DeProvincia getDeProvincia() {
		return this.deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_com")
	public DeComune getDeComune() {
		return this.deComune;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_regione")
	public DeRegione getDeRegione() {
		return this.deRegione;
	}

	public void setDeRegione(DeRegione deRegione) {
		this.deRegione = deRegione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_mansione")
	public DeMansione getDeMansione() {
		return this.deMansione;
	}

	public void setDeMansione(DeMansione deMansione) {
		this.deMansione = deMansione;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "cosa", length = 200)
	public String getCosa() {
		return this.cosa;
	}

	public void setCosa(String cosa) {
		this.cosa = cosa;
	}

	@Column(name = "dove", length = 200)
	public String getDove() {
		return this.dove;
	}

	public void setDove(String dove) {
		this.dove = dove;
	}

	@Column(name = "raggio_ricerca")
	public Integer getRaggioRicerca() {
		return this.raggioRicerca;
	}

	public void setRaggioRicerca(Integer raggioRicerca) {
		this.raggioRicerca = raggioRicerca;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvTrasferta> getRvTrasfertas() {
		return this.rvTrasfertas;
	}

	public void setRvTrasfertas(Set<RvTrasferta> rvTrasfertas) {
		this.rvTrasfertas = rvTrasfertas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvLingua> getRvLinguas() {
		return this.rvLinguas;
	}

	public void setRvLinguas(Set<RvLingua> rvLinguas) {
		this.rvLinguas = rvLinguas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvOrario> getRvOrarios() {
		return this.rvOrarios;
	}

	public void setRvOrarios(Set<RvOrario> rvOrarios) {
		this.rvOrarios = rvOrarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvMansione> getRvMansiones() {
		return this.rvMansiones;
	}

	public void setRvMansiones(Set<RvMansione> rvMansiones) {
		this.rvMansiones = rvMansiones;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvAttivita> getRvAttivitas() {
		return this.rvAttivitas;
	}

	public void setRvAttivitas(Set<RvAttivita> rvAttivitas) {
		this.rvAttivitas = rvAttivitas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvTitolo> getRvTitolos() {
		return this.rvTitolos;
	}

	public void setRvTitolos(Set<RvTitolo> rvTitolos) {
		this.rvTitolos = rvTitolos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvAgricolo> getRvAgricolos() {
		return this.rvAgricolos;
	}

	public void setRvAgricolos(Set<RvAgricolo> rvAgricolos) {
		this.rvAgricolos = rvAgricolos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvContratto> getRvContrattos() {
		return this.rvContrattos;
	}

	public void setRvContrattos(Set<RvContratto> rvContrattos) {
		this.rvContrattos = rvContrattos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rvTestata")
	public Set<RvPatente> getRvPatentes() {
		return this.rvPatentes;
	}

	public void setRvPatentes(Set<RvPatente> rvPatentes) {
		this.rvPatentes = rvPatentes;
	}
}
