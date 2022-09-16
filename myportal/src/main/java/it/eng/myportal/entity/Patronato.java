package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import it.eng.myportal.entity.decodifiche.DeProvincia;

@Entity
@Table(name = "patronato", schema = "mycas")
public class Patronato extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1009803886820008167L;
	private PfPrincipal pfPrincipal;
	private Integer idPatronato;
	private DeProvincia deProvinciaRif;
	private String denominazione;
	
	public Patronato() {
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipalByIdPfPrincipal) {
		this.pfPrincipal = pfPrincipalByIdPfPrincipal;
	}

	@Id
	@SequenceGenerator(name = "patronato_id_patronato_seq", sequenceName = "mycas.patronato_id_patronato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patronato_id_patronato_seq")
	@Column(name = "id_patronato", unique = true, nullable = false)
	public Integer getIdPatronato() {
		return this.idPatronato;
	}

	public void setIdPatronato(Integer idPatronato) {
		this.idPatronato = idPatronato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia_rif", nullable = false)
	public DeProvincia getDeProvinciaRif() {
		return this.deProvinciaRif;
	}

	public void setDeProvinciaRif(DeProvincia deProvinciaRif) {
		this.deProvinciaRif = deProvinciaRif;
	}
	
	@Column(name = "denominazione", length = 100)
	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	
}
