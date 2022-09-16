package it.eng.myportal.beans.azienda.fabbisogno;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeStatoFbConvenzioneDTO;
import it.eng.myportal.dtos.DeTipoFbConvenzioneDTO;
import it.eng.myportal.dtos.FbConvenzioneDTO;
import it.eng.myportal.dtos.FbConvenzioneFilterDTO;
import it.eng.myportal.dtos.FbConvenzioneSedeDTO;
import it.eng.myportal.entity.home.FbConvenzioneHome;
import it.eng.myportal.entity.home.FbConvenzioneSedeHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbConvenzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoFbConvenzioneHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "fbConvenzioneListBean")
@ViewScoped
public class FbConvenzioneListBean extends AbstractBaseBean {

	@EJB
	private FbConvenzioneHome fbConvenzioneHome;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private DeTipoFbConvenzioneHome deTipoConvenzioneHome;

	@EJB
	private DeStatoFbConvenzioneHome deStatoConvenzioneHome;

	@EJB
	private FbConvenzioneSedeHome fbConvenzioneSedeHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	private List<FbConvenzioneDTO> fbConvenzioneList;
	private List<DeTipoFbConvenzioneDTO> tipos;
	private List<DeStatoFbConvenzioneDTO> statos;
	private FbConvenzioneFilterDTO filterConvenzioneDTO;
	private FbConvenzioneDTO convenzioneDaEliminare;
	private boolean canCreateConvenzione;
	private boolean showConvenzionetable;
	private String codice;

	private FacesContext context = FacesContext.getCurrentInstance();
	private HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	private HttpSession httpSession = request.getSession(false);

	@PostConstruct
	public void init() {
		tipos = deTipoConvenzioneHome.findAllDTO();
		statos = deStatoConvenzioneHome.findRicercabili();

		filterConvenzioneDTO = (FbConvenzioneFilterDTO) httpSession.getAttribute("filterConvenzioneDTO");
		if (filterConvenzioneDTO == null) {
			filterConvenzioneDTO = new FbConvenzioneFilterDTO();
		} else {
			findListOfConvenzioneByCriteria();
		}

		if (isEntePromotore()) {
			fbConvenzioneList = fbConvenzioneHome.findByIdPrincipal(session.getPrincipalId());

			// L'ente promotore può creare una nuova convenzione solo se non ne ha già una per ogni tipo
			int numConvenzioniTir = fbConvenzioneHome.countConvenzioniAttiveByIdPrincipalAndTipo(
					session.getPrincipalId(), ConstantsSingleton.DeTipoFbConvenzione.TIROCINI);
			int numConvenzioniMulti = fbConvenzioneHome.countConvenzioniAttiveByIdPrincipalAndTipo(
					session.getPrincipalId(), ConstantsSingleton.DeTipoFbConvenzione.MULTIMISURA);
			int numConvenzioniDote = fbConvenzioneHome.countConvenzioniAttiveByIdPrincipalAndTipo(
					session.getPrincipalId(), ConstantsSingleton.DeTipoFbConvenzione.DOTE);
			if (numConvenzioniTir == 0 || numConvenzioniMulti == 0 || numConvenzioniDote == 0) {
				canCreateConvenzione = true;
			}
		}
	}

	public boolean isEntePromotore() {
		return session.isAzienda() && session.isEntePromotoreCalabria();
	}

	/** Ci sono due pagine di questo bean: una accedibile solo dalle regioni, e una solo dagli enti promotori */
	public void secureMeRegione() {
		if (!session.isRegione()) {
			redirectHome();
		}
	}

	/** Ci sono due pagine di questo bean: una accedibile solo dalle regioni, e una solo dagli enti promotori */
	public void secureMeEntePromotore() {
		if (!isEntePromotore()) {
			redirectHome();
		}
	}

	/** Setto la convenzione selezionata dall'utente per l'eliminazione (in attesa della conferma) */
	public void setConvenzioneDaEliminare(FbConvenzioneDTO fbConvenzione) {
		convenzioneDaEliminare = fbConvenzione;
	}

	/** Confermo l'eliminazione della convenzione scelta in precedenza */
	public void confirmEliminaConvenzione() {
		if (convenzioneDaEliminare != null) {
			// Elimino non solo la convenzione, ma tutte le sue sedi.
			List<FbConvenzioneSedeDTO> sediDaEliminare = fbConvenzioneSedeHome
					.findByConvenzioneId(convenzioneDaEliminare.getId());
			for (FbConvenzioneSedeDTO sede : sediDaEliminare) {
				fbConvenzioneSedeHome.removeById(sede.getId(), session.getPrincipalId());
			}

			// Elimino la convenzione
			fbConvenzioneList.remove(convenzioneDaEliminare);
			fbConvenzioneHome.removeById(convenzioneDaEliminare.getId(), session.getPrincipalId());
		}
	}

	/** Search list of Convenzione by using diffeent criteria in regione page */
	public void findListOfConvenzioneByCriteria() {
		fbConvenzioneList = fbConvenzioneHome.findConvenzioneByFilter(filterConvenzioneDTO, this.codice);
		showConvenzionetable = true;
		httpSession.setAttribute("filterConvenzioneDTO", filterConvenzioneDTO);
	}

	public boolean canCreateConvenzione() {
		return canCreateConvenzione;
	}

	public List<DeComuneDTO> completeComune(String query) {
		return deComuneHome.findBySuggestion(query);
	}

	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}

	public List<DeTipoFbConvenzioneDTO> getTipos() {
		return tipos;
	}

	public void setTipos(List<DeTipoFbConvenzioneDTO> tipos) {
		this.tipos = tipos;
	}

	public List<DeStatoFbConvenzioneDTO> getStatos() {
		return statos;
	}

	public void setStatos(List<DeStatoFbConvenzioneDTO> statos) {
		this.statos = statos;
	}

	public FbConvenzioneFilterDTO getFilterConvenzioneDTO() {
		return filterConvenzioneDTO;
	}

	public void setFilterConvenzioneDTO(FbConvenzioneFilterDTO filterConvenzioneDTO) {
		this.filterConvenzioneDTO = filterConvenzioneDTO;
	}

	public List<FbConvenzioneDTO> getFbConvenzioneList() {
		return fbConvenzioneList;
	}

	public void setFbConvenzioneList(List<FbConvenzioneDTO> fbConvenzioneList) {
		this.fbConvenzioneList = fbConvenzioneList;
	}

	public boolean isShowConvenzionetable() {
		return showConvenzionetable;
	}

	public void setShowConvenzionetable(boolean showConvenzionetable) {
		this.showConvenzionetable = showConvenzionetable;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

}
