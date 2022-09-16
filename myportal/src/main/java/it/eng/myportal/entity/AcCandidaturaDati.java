package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeProvenienza;

import java.util.Date;

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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "ac_candidatura_dati", schema = "myportal")
public class AcCandidaturaDati extends AbstractEntity {
	private static final long serialVersionUID = -2093881364290238356L;

	private Integer idAcCandidaturaDati;
	private AcCandidatura acCandidatura;

	private String codStatoOccupazionale;
	private String descStatoOccupazionale;
	private String listeSpeciali;
	private Boolean flagDisabile;
	private Boolean flagIntermittente;
	private Date dataDichiarazione;
	private DeProvenienza deProvenienza;

	@Id
	@SequenceGenerator(name = "ac_candidatura_dati_id_ac_candidatura_dati_seq", sequenceName = "ac_candidatura_dati_id_ac_candidatura_dati_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ac_candidatura_dati_id_ac_candidatura_dati_seq")
	@Column(name = "id_ac_candidatura_dati", unique = true, nullable = false)
	public Integer getIdAcCandidaturaDati() {
		return idAcCandidaturaDati;
	}

	public void setIdAcCandidaturaDati(Integer idAcCandidaturaDati) {
		this.idAcCandidaturaDati = idAcCandidaturaDati;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ac_candidatura", nullable = false)
	public AcCandidatura getAcCandidatura() {
		return acCandidatura;
	}

	public void setAcCandidatura(AcCandidatura acCandidatura) {
		this.acCandidatura = acCandidatura;
	}

	@Column(name = "cod_stato_occupazionale", length = 8)
	public String getCodStatoOccupazionale() {
		return codStatoOccupazionale;
	}

	public void setCodStatoOccupazionale(String codStatoOccupazionale) {
		this.codStatoOccupazionale = codStatoOccupazionale;
	}

	@Column(name = "desc_stato_occupazionale", length = 100)
	public String getDescStatoOccupazionale() {
		return descStatoOccupazionale;
	}

	public void setDescStatoOccupazionale(String descStatoOccupazionale) {
		this.descStatoOccupazionale = descStatoOccupazionale;
	}

	@Column(name = "liste_speciali", length = 200)
	public String getListeSpeciali() {
		return listeSpeciali;
	}

	public void setListeSpeciali(String listeSpeciali) {
		this.listeSpeciali = listeSpeciali;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_disabile", length = 1)
	public Boolean getFlagDisabile() {
		return flagDisabile;
	}

	public void setFlagDisabile(Boolean flagDisabile) {
		this.flagDisabile = flagDisabile;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_intermittente", length = 1)
	public Boolean getFlagIntermittente() {
		return flagIntermittente;
	}

	public void setFlagIntermittente(Boolean flagIntermittente) {
		this.flagIntermittente = flagIntermittente;
	}

	@Column(name = "data_dichiarazione")
	public Date getDataDichiarazione() {
		return dataDichiarazione;
	}

	public void setDataDichiarazione(Date dataDichiarazione) {
		this.dataDichiarazione = dataDichiarazione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_provenienza")
	public DeProvenienza getDeProvenienza() {
		return deProvenienza;
	}

	public void setDeProvenienza(DeProvenienza deProvenienza) {
		this.deProvenienza = deProvenienza;
	}
}
