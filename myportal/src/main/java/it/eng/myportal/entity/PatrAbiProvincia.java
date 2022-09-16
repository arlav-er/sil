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

import it.eng.myportal.entity.decodifiche.DeProvincia;

@Entity
@Table(name = "patr_abi_provincia", schema = "mycas")
@NamedQueries({
	@NamedQuery(name = "findPatrAbiProvinciaByPatronatoId", query = "SELECT abi FROM PatrAbiProvincia abi JOIN FETCH abi.patronato as pat WHERE pat.idPatronato = :patronatoId"),
})
public class PatrAbiProvincia extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1009803886820008167L;
	private Patronato patronato;
	private Integer idPatrAbiProvincia;
	private DeProvincia deProvincia;
	
	public PatrAbiProvincia() {
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_patronato", nullable = false)
	public Patronato getPatronato() {
		return this.patronato;
	}

	public void setPatronato(Patronato patronato) {
		this.patronato = patronato;
	}

	@Id
	@SequenceGenerator(name = "patr_abi_provincia_id_patr_abi_provincia_seq", sequenceName = "mycas.patr_abi_provincia_id_patr_abi_provincia_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patr_abi_provincia_id_patr_abi_provincia_seq")
	@Column(name = "id_patr_abi_provincia", unique = true, nullable = false)
	public Integer getIdPatrAbiProvincia() {
		return this.idPatrAbiProvincia;
	}

	public void setIdPatrAbiProvincia(Integer idPatrAbiProvincia) {
		this.idPatrAbiProvincia = idPatrAbiProvincia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia", nullable = false)
	public DeProvincia getDeProvincia() {
		return this.deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}
	
	
}
