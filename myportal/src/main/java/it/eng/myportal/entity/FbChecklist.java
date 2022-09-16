package it.eng.myportal.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.myportal.entity.decodifiche.DeFbCategoria;
import it.eng.myportal.entity.decodifiche.DeStatoFbChecklist;

@Entity
@Table(name = "fb_checklist", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findFbChecklistByIdPfPrincipal", query = "SELECT chk FROM FbChecklist chk WHERE chk.pfPrincipal.idPfPrincipal = :idPfPrincipal ORDER BY chk.dtValutazione, chk.dtmMod") })
public class FbChecklist extends AbstractEntity {
	private static final long serialVersionUID = 794944360395820872L;

	private Integer idFbChecklist;
	private PfPrincipal pfPrincipal;
	private DeStatoFbChecklist deStatoFbChecklist;
	private DeFbCategoria deFbCategoria;
	private Integer idValutatore;
	private Date dtValutazione;
	private Date dtPubblicazione;

	private String motivoRevoca;

	// REVERSE LOOKUPS
	private List<FbSchedaFabbisogno> fbSchedaFabbisognoList = new ArrayList<FbSchedaFabbisogno>();

	@Id
	@SequenceGenerator(name = "fb_checklist_id_fb_checklist_seq", sequenceName = "fb_checklist_id_fb_checklist_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_checklist_id_fb_checklist_seq")
	@Column(name = "id_fb_checklist", unique = true, nullable = false)
	public Integer getIdFbChecklist() {
		return idFbChecklist;
	}

	public void setIdFbChecklist(Integer idFbChecklist) {
		this.idFbChecklist = idFbChecklist;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_fb_stato_checklist", nullable = false)
	public DeStatoFbChecklist getDeStatoFbChecklist() {
		return deStatoFbChecklist;
	}

	public void setDeStatoFbChecklist(DeStatoFbChecklist deStatoFbChecklist) {
		this.deStatoFbChecklist = deStatoFbChecklist;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_fb_categoria", nullable = false)
	public DeFbCategoria getDeFbCategoria() {
		return deFbCategoria;
	}

	public void setDeFbCategoria(DeFbCategoria deFbCategoria) {
		this.deFbCategoria = deFbCategoria;
	}

	@Column(name = "id_valutatore")
	public Integer getIdValutatore() {
		return idValutatore;
	}

	public void setIdValutatore(Integer idValutatore) {
		this.idValutatore = idValutatore;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_valutazione")
	public Date getDtValutazione() {
		return dtValutazione;
	}

	public void setDtValutazione(Date dtValutazione) {
		this.dtValutazione = dtValutazione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fbChecklist")
	@OrderBy(value = "dtmIns desc")
	public List<FbSchedaFabbisogno> getFbSchedaFabbisognoList() {
		return fbSchedaFabbisognoList;
	}

	public void setFbSchedaFabbisognoList(List<FbSchedaFabbisogno> fbSchedaFabbisognoList) {
		this.fbSchedaFabbisognoList = fbSchedaFabbisognoList;
	}

	@Column(name = "motivo_revoca")
	public String getMotivoRevoca() {
		return motivoRevoca;
	}

	public void setMotivoRevoca(String motivoRevoca) {
		this.motivoRevoca = motivoRevoca;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_pubblicazione")
	public Date getDtPubblicazione() {
		return dtPubblicazione;
	}

	public void setDtPubblicazione(Date dtPubblicazione) {
		this.dtPubblicazione = dtPubblicazione;
	}

}
