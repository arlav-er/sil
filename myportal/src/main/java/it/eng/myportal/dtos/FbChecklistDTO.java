package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.eng.myportal.utils.ConstantsSingleton;

public class FbChecklistDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -8078878803133255072L;

	private Integer idPfPrincipal;
	private DeStatoFbChecklistDTO deStatoFbChecklist;
	private DeFbCategoriaDTO deFbCategoria;
	private Integer idValutatore;
	private Date dtValutazione;
	private List<FbSchedaFabbisognoDTO> fbSchedaFabbisognoList;
	private String motivoRevoca;
	private Date dtPubblicazione;
	private String codiceFiscale;
	// Campi non presenti sul DB, li aggiungo per comodità
	private String aziendaOspitante;

	public FbChecklistDTO() {
		deStatoFbChecklist = new DeStatoFbChecklistDTO();
		deFbCategoria = new DeFbCategoriaDTO();
		fbSchedaFabbisognoList = new ArrayList<FbSchedaFabbisognoDTO>();
	}

	public boolean isModificabile() {
		return ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE.equals(deStatoFbChecklist.getId());
	}

	public boolean canDettaglioSchedaFabbisogno(FbSchedaFabbisognoDTO bSchedaFabbisognoDTO) {
		return ConstantsSingleton.DeStatoFbChecklist.PUBBLICATA.equals(bSchedaFabbisognoDTO.getDeStatoScheda().getId())
				|| ConstantsSingleton.DeStatoFbChecklist.CHIUSA.equals(bSchedaFabbisognoDTO.getDeStatoScheda().getId());
	}

	public boolean isModificabileSchedaFabbisogno(FbSchedaFabbisognoDTO bSchedaFabbisognoDTO) {
		return ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE
				.equals(bSchedaFabbisognoDTO.getDeStatoScheda().getId());
	}

	public boolean canViewSchedaFabbisogno() {
		if (ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE.equals(deStatoFbChecklist.getId())
				|| ConstantsSingleton.DeStatoFbChecklist.PUBBLICATA.equals(deStatoFbChecklist.getId()))
			return false;
		else if (ConstantsSingleton.DeStatoFbChecklist.REVOCATA.equals(deStatoFbChecklist.getId())
				|| ConstantsSingleton.DeStatoFbChecklist.CHIUSA.equals(deStatoFbChecklist.getId()))
			return (fbSchedaFabbisognoList.size() > 0);
		else
			return ConstantsSingleton.DeStatoFbChecklist.VALIDATA.equals(deStatoFbChecklist.getId());

	}

	public boolean canCreaSchedaFabbisogno() {
		return ConstantsSingleton.DeStatoFbChecklist.VALIDATA.equals(deStatoFbChecklist.getId());
	}

	public boolean isChiusa() {
		return ConstantsSingleton.DeStatoFbChecklist.CHIUSA.equals(deStatoFbChecklist.getId());
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public DeStatoFbChecklistDTO getDeStatoFbChecklist() {
		return deStatoFbChecklist;
	}

	public void setDeStatoFbChecklist(DeStatoFbChecklistDTO deStatoFbChecklist) {
		this.deStatoFbChecklist = deStatoFbChecklist;
	}

	public Integer getIdValutatore() {
		return idValutatore;
	}

	public void setIdValutatore(Integer idValutatore) {
		this.idValutatore = idValutatore;
	}

	public Date getDtValutazione() {
		return dtValutazione;
	}

	public void setDtValutazione(Date dtValutazione) {
		this.dtValutazione = dtValutazione;
	}

	public DeFbCategoriaDTO getDeFbCategoria() {
		return deFbCategoria;
	}

	public void setDeFbCategoria(DeFbCategoriaDTO deFbCategoria) {
		this.deFbCategoria = deFbCategoria;
	}

	public List<FbSchedaFabbisognoDTO> getFbSchedaFabbisognoList() {
		return fbSchedaFabbisognoList;
	}

	public void setFbSchedaFabbisognoList(List<FbSchedaFabbisognoDTO> fbSchedaFabbisognoList) {
		this.fbSchedaFabbisognoList = fbSchedaFabbisognoList;
	}

	public String getAziendaOspitante() {
		return aziendaOspitante;
	}

	public void setAziendaOspitante(String aziendaOspitante) {
		this.aziendaOspitante = aziendaOspitante;
	}

	public String getMotivoRevoca() {
		return motivoRevoca;
	}

	public void setMotivoRevoca(String motivoRevoca) {
		this.motivoRevoca = motivoRevoca;
	}

	public Date getDtPubblicazione() {
		return dtPubblicazione;
	}

	public void setDtPubblicazione(Date dtPubblicazione) {
		this.dtPubblicazione = dtPubblicazione;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

}
