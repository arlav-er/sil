package it.eng.myportal.beans.candidature;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;

/**
 * BackingBean della pagina di visualizzazione dell'elenco delle autocandidature
 * 
 * @author Enrico D'Angelo
 * @author Alessandro Pegoraro review 2020
 * 
 */
@ManagedBean
@ViewScoped
public class VisualizzaListaAutocandidatureBean extends AbstractVisualizzaListaCandidatureAziendaBean {

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;
	private List<AcVisualizzaCandidaturaDTO> selectedCandidature;

	private List<AcVisualizzaCandidaturaDTO> filterCandidatureList;
	public String candidatureLabel = "Seleziona...";

	private String filtroVeloce;
	RestoreParameters ret;

	/**
	 * Restituisce la lista di tutte le auto candidature inviate all'azienda passata come input
	 * 
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda
	 * @return lista delle autocandidature inviate all'azienda
	 */
	@Override
	protected List<AcVisualizzaCandidaturaDTO> getListaCandidature(Integer idPfPrincipalAzienda) {
		return acCandidaturaHome.findDtosAutocandidatureByIdPfPrincipalAzienda(idPfPrincipalAzienda);
	}

	public List<AcVisualizzaCandidaturaDTO> getSelectedCandidature() {
		return selectedCandidature;
	}

	public void setSelectedCandidature(List<AcVisualizzaCandidaturaDTO> selectedCandidature) {
		this.selectedCandidature = selectedCandidature;
	}

	public String getOutcome(AcVisualizzaCandidaturaDTO dtoIn) {
		// bruttarello ma cosi` e` la vita
		CvDatiPersonali cv = cvDatiPersonaliHome.findById(dtoIn.getIdCvDatiPersonali());
		return cv.getFlagIdo() ? "visualizza_curriculumIdo" : "visualizza_curriculum";
	}

	public List<AcVisualizzaCandidaturaDTO> getFilterCandidatureList() {
		return filterCandidatureList;
	}

	public void setFilterCandidatureList(List<AcVisualizzaCandidaturaDTO> filterCandidatureList) {
		this.filterCandidatureList = filterCandidatureList;
	}

	// Backing bean
	public void populateLabel() {
		/* Populating the label with the selected options */
		candidatureLabel = new String("");
		if (getLivelliValutazioneSelezionati().size() == 0) {
			candidatureLabel = "Seleziona...";
		} else {
			getLivelliValutazioneSelezionati();
			candidatureLabel = "";
			for (DeIdoneitaCandidatura current : getLivelliValutazioneSelezionati()) {
				candidatureLabel = candidatureLabel + " " + current.getDescrizione();
			}
			generateRestoreParams();
			putParamsIntoSession();
		}
	}

	@Override
	public void CheckSessione(){
		if(beanParamsSess != null){
			if(beanParamsSess.containsKey("filtroVeloce")){
				Object obj=beanParamsSess.get("filtroVeloce");
				filtroVeloce = (String)obj;
				setFiltroVeloce(filtroVeloce);
			}
			if(beanParamsSess.containsKey("livelliSelezione")){
				Object obj = beanParamsSess.get("livelliSelezione");
				List<DeIdoneitaCandidatura> list = (List<DeIdoneitaCandidatura>)obj;
				if(!list.isEmpty()){
					setLivelliValutazioneSelezionati(list);
					populateLabel();
					filtraCandidatureNewVersion();
				}
			}
		}

	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		ret = super.generateRestoreParams();
		if(filtroVeloce != null){
			ret.put("filtroVeloce", filtroVeloce);
		}
		if(!getLivelliValutazioneSelezionati().isEmpty())
			ret.put("livelliSelezione", getLivelliValutazioneSelezionati());
		return ret;
	}

	public void filterListener() {
		generateRestoreParams();
		putParamsIntoSession();
	}

	@Override
	public String getBackTo() {
		if (utils.isRER()) {
			return "/faces/secure/azienda/home.xhtml?faces-redirect=true";
		}
		else{
			return super.getBackTo();
		}
	}


	public String getCandidatureLabel() {
		return candidatureLabel;
	}

	public void setCandidatureLabel(String candidatureLabel) {
		this.candidatureLabel = candidatureLabel;
	}

	public String getFiltroVeloce() {
		return filtroVeloce;
	}

	public void setFiltroVeloce(String filtroVeloce) {
		this.filtroVeloce = filtroVeloce;
	}
}

