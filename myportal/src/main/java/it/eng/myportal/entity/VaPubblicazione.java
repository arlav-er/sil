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

@Entity
@Table(name = "va_pubblicazione", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findVaPubblicazioneByVaDatiVacancyId", query = "select pub from VaPubblicazione pub where pub.vaDatiVacancy.idVaDatiVacancy = :idDaVacancy") })
public class VaPubblicazione extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = -4590721383380558111L;

	private Integer idVaPubblicazione;
	private VaDatiVacancy vaDatiVacancy;
	private String datiAzienda;
	private String contenuto;
	private String luogo;
	private String formazione;
	private String contratto;
	private String conoscenze;
	private String caratteristiche;
	private String orario;
	private String candidatura;
	private String opzIndispensabileForm;
	private String opzPreferibileForm;

	@Id
	@SequenceGenerator(name = "va_pubblicazione_id_va_pubblicazione_seq", sequenceName = "va_pubblicazione_id_va_pubblicazione_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "va_pubblicazione_id_va_pubblicazione_seq")
	@Column(name = "id_va_pubblicazione", unique = true, nullable = false)
	public Integer getIdVaPubblicazione() {
		return idVaPubblicazione;
	}

	public void setIdVaPubblicazione(Integer idVaPubblicazione) {
		this.idVaPubblicazione = idVaPubblicazione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_va_dati_vacancy", nullable = false)
	public VaDatiVacancy getVaDatiVacancy() {
		return vaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

	@Column(name = "dati_azienda", length = 2000)
	public String getDatiAzienda() {
		return datiAzienda;
	}

	public void setDatiAzienda(String datiAzienda) {
		this.datiAzienda = datiAzienda;
	}

	@Column(name = "contenuto", length = 2000)
	public String getContenuto() {
		return contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	@Column(name = "luogo", length = 2000)
	public String getLuogo() {
		return luogo;
	}

	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}

	@Column(name = "formazione", length = 2000)
	public String getFormazione() {
		return formazione;
	}

	public void setFormazione(String formazione) {
		this.formazione = formazione;
	}

	@Column(name = "contratto", length = 2000)
	public String getContratto() {
		return contratto;
	}

	public void setContratto(String contratto) {
		this.contratto = contratto;
	}

	@Column(name = "conoscenze", length = 2000)
	public String getConoscenze() {
		return conoscenze;
	}

	public void setConoscenze(String conoscenze) {
		this.conoscenze = conoscenze;
	}

	@Column(name = "caratteristiche", length = 2000)
	public String getCaratteristiche() {
		return caratteristiche;
	}

	public void setCaratteristiche(String caratteristiche) {
		this.caratteristiche = caratteristiche;
	}

	@Column(name = "orario", length = 2000)
	public String getOrario() {
		return orario;
	}

	public void setOrario(String orario) {
		this.orario = orario;
	}

	@Column(name = "candidatura", length = 2000)
	public String getCandidatura() {
		return candidatura;
	}

	public void setCandidatura(String candidatura) {
		this.candidatura = candidatura;
	}

	@Column(name = "opz_indispensabile_form", length = 1)
	public String getOpzIndispensabileForm() {
		return opzIndispensabileForm;
	}

	public void setOpzIndispensabileForm(String opzIndispensabileForm) {
		this.opzIndispensabileForm = opzIndispensabileForm;
	}

	@Column(name = "opz_preferibile_form", length = 1)
	public String getOpzPreferibileForm() {
		return opzPreferibileForm;
	}

	public void setOpzPreferibileForm(String opzPreferibileForm) {
		this.opzPreferibileForm = opzPreferibileForm;
	}

}
