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
import javax.persistence.UniqueConstraint;

import it.eng.myportal.entity.decodifiche.DeComune;

@Entity
@Table(name = "vch_enti_accreditati", schema = "myportal",
uniqueConstraints = @UniqueConstraint(columnNames = { "codice_fiscale_ente", "cod_sede" }))
public class VchEntiAccreditati extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private Integer idVchEntiAccreditati;
	private String codiceFiscaleEnte;
	private String codSede;
	private String denominazione;
	private String indirizzo;
	private DeComune deComune;
	
	//Constructor
	public VchEntiAccreditati(){
		super();
	}

	//Getter and setter
	@Id
	@Column(columnDefinition = "serial", name = "id_vch_enti_accreditati", unique = true, nullable = false)
	@SequenceGenerator(name = "VchEntiAccreditatiSequence", sequenceName = "vch_enti_accreditati_id_vch_enti_accreditati_seq", schema = "myportal", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VchEntiAccreditatiSequence")
	public Integer getIdVchEntiAccreditati() {
		return idVchEntiAccreditati;
	}

	public void setIdVchEntiAccreditati(Integer idVchEntiAccreditati) {
		this.idVchEntiAccreditati = idVchEntiAccreditati;
	}

	@Column(name="codice_fiscale_ente", length=16, nullable=false)
	public String getCodiceFiscaleEnte() {
		return codiceFiscaleEnte;
	}

	public void setCodiceFiscaleEnte(String codiceFiscaleEnte) {
		this.codiceFiscaleEnte = codiceFiscaleEnte;
	}

	@Column(name="cod_sede", length=8, nullable=false)
	public String getCodSede() {
		return codSede;
	}

	public void setCodSede(String codSede) {
		this.codSede = codSede;
	}

	@Column(length=100)
	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	@Column(length=60)
	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="cod_com", nullable=false)
	public DeComune getDeComune() {
		return deComune;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idVchEntiAccreditati == null) ? 0 : idVchEntiAccreditati.hashCode());
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
		VchEntiAccreditati other = (VchEntiAccreditati) obj;
		if (idVchEntiAccreditati == null) {
			if (other.idVchEntiAccreditati != null)
				return false;
		} else if (!idVchEntiAccreditati.equals(other.idVchEntiAccreditati))
			return false;
		return true;
	}
	
}
