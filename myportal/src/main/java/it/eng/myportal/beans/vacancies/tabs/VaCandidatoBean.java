package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.dtos.VaCandidatoDTO;
import it.eng.myportal.entity.home.decodifiche.DeMotivoEtaSilHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class VaCandidatoBean extends AbstractVacancyTabBean<VaCandidatoDTO> {

	@EJB(mappedName = "java:module/VaCandidatoHome")
	IVacancyEntityHome<VaCandidatoDTO> vaCandidatoHome;

	@EJB
	DeMotivoEtaSilHome deMotivoEtaSilHome;

	private List<SelectItem> deMotivoEtaSilList;
	private static final String COD_MOTIVO_ETA_ALTRO = "ALT";

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		if (data.getVaEsperienze().getIdVaDatiVacancy() == null) {
			data.getVaEsperienze().setIdVaDatiVacancy(vacancyId);
		}

		if (data.getVaEsperienze().getIdPrincipalIns() == null) {
			data.getVaEsperienze().setIdPrincipalIns(session.getPrincipalId());
		}
		if (data.getVaEsperienze().getIdPrincipalMod() == null) {
			data.getVaEsperienze().setIdPrincipalMod(session.getPrincipalId());
		}
		if (data.getVaCaratteristiche().getIdPrincipalIns() == null) {
			data.getVaCaratteristiche().setIdPrincipalIns(session.getPrincipalId());
		}
		if (data.getVaCaratteristiche().getIdPrincipalMod() == null) {
			data.getVaCaratteristiche().setIdPrincipalMod(session.getPrincipalId());
		}
		if (data.getVaCaratteristiche().getIdVaDatiVacancy() == null) {
			data.getVaCaratteristiche().setIdVaDatiVacancy(vacancyId);
		}

		if (usaDecodificheSil) {
			deMotivoEtaSilList = deMotivoEtaSilHome.getListItems(true);
		}
	}

	/** La textarea 'note motivo età' e abilitata solo se 'cod motivo età' è "ALTRO" */
	public boolean isNoteMotivoEtaDisabled() {
		if (data.getVaEsperienze().getDeMotivoEtaSil() != null
				&& COD_MOTIVO_ETA_ALTRO.equals(data.getVaEsperienze().getDeMotivoEtaSil().getId())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected IVacancyEntityHome<VaCandidatoDTO> getHome() {
		return vaCandidatoHome;
	}

	@Override
	protected VaCandidatoDTO buildNewDataIstance() {
		return new VaCandidatoDTO();
	}

	public List<SelectItem> getDeMotivoEtaSilList() {
		return deMotivoEtaSilList;
	}

	public void setDeMotivoEtaSilList(List<SelectItem> deMotivoEtaSilList) {
		this.deMotivoEtaSilList = deMotivoEtaSilList;
	}
}
