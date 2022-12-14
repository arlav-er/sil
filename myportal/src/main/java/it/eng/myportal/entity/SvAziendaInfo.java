package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.decodifiche.DeSvTemplate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * SvAziendaInfo generated by hbm2java
 */
@Entity
@Table(name = "sv_azienda_info", schema = "myportal")
public class SvAziendaInfo extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 168972L;

	private Integer idPfPrincipal;
	private PfPrincipal pfPrincipal;

	private DeSvTemplate deSvTemplate;
	private String chiSiamo;
	private String doveSiamo;
	private String mission;
	private String storia;
	private String profiliRichiesti;
	private String formazioneCrescita;
	private Boolean flagPubblicabile;
	private Date dataPubblicazione;

	public SvAziendaInfo() {
	}

	@Id
	@Column(name = "id_pf_principal", unique = true, nullable = false)
	public Integer getIdPfPrincipal() {
		return this.idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	@PrimaryKeyJoinColumn
	@OneToOne(fetch = FetchType.LAZY)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_sv_template", nullable = false)
	public DeSvTemplate getDeSvTemplate() {
		return this.deSvTemplate;
	}

	public void setDeSvTemplate(DeSvTemplate deSvTemplate) {
		this.deSvTemplate = deSvTemplate;
	}

	@Column(name = "chi_siamo", length = 4000)
	public String getChiSiamo() {
		return this.chiSiamo;
	}

	public void setChiSiamo(String chiSiamo) {
		this.chiSiamo = chiSiamo;
	}

	@Column(name = "dove_siamo", length = 4000)
	public String getDoveSiamo() {
		return this.doveSiamo;
	}

	public void setDoveSiamo(String doveSiamo) {
		this.doveSiamo = doveSiamo;
	}

	@Column(name = "mission", length = 4000)
	public String getMission() {
		return this.mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	@Column(name = "storia", length = 4000)
	public String getStoria() {
		return this.storia;
	}

	public void setStoria(String storia) {
		this.storia = storia;
	}

	@Column(name = "profili_richiesti", length = 4000)
	public String getProfiliRichiesti() {
		return this.profiliRichiesti;
	}

	public void setProfiliRichiesti(String profiliRichiesti) {
		this.profiliRichiesti = profiliRichiesti;
	}

	@Column(name = "formazione_crescita", length = 4000)
	public String getFormazioneCrescita() {
		return this.formazioneCrescita;
	}

	public void setFormazioneCrescita(String formazioneCrescita) {
		this.formazioneCrescita = formazioneCrescita;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_pubblicabile", nullable = false, length = 1)
	public Boolean getFlagPubblicabile() {
		return this.flagPubblicabile;
	}

	public void setFlagPubblicabile(Boolean flagPubblicabile) {
		this.flagPubblicabile = flagPubblicabile;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_pubblicazione", length = 13)
	public Date getDataPubblicazione() {
		return this.dataPubblicazione;
	}

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

}
