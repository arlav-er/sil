package it.eng.myportal.beans.yg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeQualificaSrqDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgAdesioneAziendaDTO;
import it.eng.myportal.dtos.YgAdesioneAziendaSedeDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.YgAdesioneAziendaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeQualificaSrqHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean
@ViewScoped
public class YgAdesioneAziendaBean extends AbstractBaseBean {

	private YgAdesioneAziendaDTO adesioneAziendaDTO;
	private Boolean editMode;
	private Boolean editSede;
	private Boolean disableTirocinio;
	private AziendaSessionDTO aziendaSessionDTO;
	private UtenteCompletoDTO utente;
	private PfPrincipalDTO pfPrincipal;
	private AziendaInfoDTO aziendaInfoDTO;
	private YgAdesioneAziendaSedeDTO newSede;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	PfPrincipalHome principalHome;

	@EJB
	YgAdesioneAziendaHome ygAdesioneAziendaHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeQualificaSrqHome deQualificaSrqHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		newSede = new YgAdesioneAziendaSedeDTO();
		prepareUserInfo();
		prepareYgAdesioneAzienda();
	}

	public YgAdesioneAziendaDTO getAdesioneAziendaDTO() {
		return adesioneAziendaDTO;
	}

	public void setAdesioneAziendaDTO(YgAdesioneAziendaDTO adesioneAziendaDTO) {
		this.adesioneAziendaDTO = adesioneAziendaDTO;
	}

	public Boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
	}

	public AziendaSessionDTO getAziendaSessionDTO() {
		return aziendaSessionDTO;
	}

	public void setAziendaSessionDTO(AziendaSessionDTO aziendaSessionDTO) {
		this.aziendaSessionDTO = aziendaSessionDTO;
	}

	public UtenteCompletoDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteCompletoDTO utente) {
		this.utente = utente;
	}

	public YgAdesioneAziendaSedeDTO getNewSede() {
		return newSede;
	}

	public void setNewSede(YgAdesioneAziendaSedeDTO newSede) {
		this.newSede = newSede;
	}

	public Boolean getEditSede() {
		return editSede;
	}

	public void setEditSede(Boolean editSede) {
		this.editSede = editSede;
	}

	public Boolean getDisableTirocinio() {
		return disableTirocinio;
	}

	public void setDisableTirocinio(Boolean disableTirocinio) {
		this.disableTirocinio = disableTirocinio;
	}

	private void prepareYgAdesioneAzienda() {
		if (ygAdesioneAziendaHome.adesioneExists(pfPrincipal.getId())) {
			adesioneAziendaDTO = ygAdesioneAziendaHome
					.findDTOByIdPfPrincipal(pfPrincipal.getId());
			editMode = Boolean.TRUE;
		} else {
			adesioneAziendaDTO = new YgAdesioneAziendaDTO();
			editMode = Boolean.FALSE;
		}
	}

	private void prepareUserInfo() {
		aziendaSessionDTO = getSession().getConnectedAzienda();
		aziendaInfoDTO = aziendaInfoHome.findDTOById(getSession()
				.getPrincipalId());
		pfPrincipal = principalHome.findDTOById(getSession().getPrincipalId());
	}

	public void prepareNewSede() {
		newSede = new YgAdesioneAziendaSedeDTO();
		Date now = new Date();
		newSede.setDtmIns(now);
		newSede.setDtmMod(now);
		newSede.setIdPrincipalIns(pfPrincipal.getId());
		newSede.setIdPrincipalMod(pfPrincipal.getId());
		newSede.setPfPrincipalDTO(pfPrincipal);
		newSede.setFlgSedeLegale(Boolean.FALSE);
		newSede.setFlgTirocinio(Boolean.FALSE);
	}

	public void addNewSedeToList() {
		if (adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList() == null)
			adesioneAziendaDTO
					.setYgAdesioneAziendaSedeDTOList(new ArrayList<YgAdesioneAziendaSedeDTO>());

		if (!editSede && validateNewSede()) {
			adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList().add(newSede);
			RequestContext.getCurrentInstance().execute(
					"$.jnotify('La sede è stata aggiunta', 'info', true);");
		} else {
			RequestContext.getCurrentInstance().execute(
					"$.jnotify('La sede è stata modificata', 'info', true);");
		}
	}

	public void removeSedeFromList() {
		adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList().remove(newSede);
	}

	public List<DeComuneDTO> completeComune(String query) {
		return deComuneHome.findBySuggestion(query);
	}

	public List<DeQualificaSrqDTO> completeQualificaSrq(String query) {
		return deQualificaSrqHome.findSuggestion(query);
	}

	public List<DeAttivitaMinDTO> completeAttivitaMin(String query) {
		return deAttivitaMinHome.findByDescription("%" + query.toLowerCase()
				+ "%");
	}

	public void sync() {
		sync(true);
	}

	private void sync(boolean showMessage) {
		adesioneAziendaDTO.setPfPrincipalDTO(pfPrincipal);
		if (adesioneAziendaDTO.getId() == null) {
			adesioneAziendaDTO.setFlgAdesione(Boolean.FALSE);
		}
		try {
			ygAdesioneAziendaHome.sync(adesioneAziendaDTO, pfPrincipal.getId());
			adesioneAziendaDTO = ygAdesioneAziendaHome
					.findDTOByIdPfPrincipal(pfPrincipal.getId());
			if (showMessage) {
				RequestContext
						.getCurrentInstance()
						.execute(
								"$.jnotify('Salvataggio avvenuto correttamente.', 'info', true);");
			}
		} catch (EJBException ex) {
			RequestContext
					.getCurrentInstance()
					.execute(
							"$.jnotify('Errore durante il salvataggio dei dati.', 'error', true);");
		}
	}

	public void aderisci() {
		if (hasSedeLegale()) {
			adesioneAziendaDTO.setFlgAdesione(Boolean.TRUE);
			adesioneAziendaDTO.setDtAdesione(new Date());
			sync(false);
			RequestContext
			.getCurrentInstance()
			.execute("$.jnotify('Adesione avvenuta con successo.<br/> Da ora potrai ricevere informazioni aggiornate sulle singole opportunità e ottenere "
					+ "consulenza per avviare tirocini,<br/> attivare contratti di apprendistato o richiedere un bonus occupazionale,"
					+ " a favore di giovani, tra i 15 e i 29 anni, che hanno aderito all&apos;iniziativa Garanzia Giovani', 'info', true);");
		} else {
			RequestContext.getCurrentInstance().execute(
					"$.jnotify('Non hai una sede legale', 'error', true);");
		}

	}

	public boolean hasSedeLegale() {
		boolean hasSedeLegale = false;
		if (adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList() != null
				&& !adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList()
						.isEmpty()) {
			for (YgAdesioneAziendaSedeDTO dto : adesioneAziendaDTO
					.getYgAdesioneAziendaSedeDTOList()) {
				if (dto.getFlgSedeLegale()) {
					hasSedeLegale = true;
					break;
				}
			}
		}
		return hasSedeLegale;
	}

	public boolean disableSedeLegale() {
		boolean disable = false;

		if (adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList() != null
				&& !adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList()
						.isEmpty()) {
			for (YgAdesioneAziendaSedeDTO dto : adesioneAziendaDTO
					.getYgAdesioneAziendaSedeDTOList()) {
				if (dto.getFlgSedeLegale() != null && dto.getFlgSedeLegale()
						&& !dto.equals(newSede)) {
					disable = true;
					break;
				}
			}
		}
		return disable;
	}

	public boolean validateNewSede() {
		if (adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList() != null
				&& !adesioneAziendaDTO.getYgAdesioneAziendaSedeDTOList()
						.isEmpty()) {
			for (YgAdesioneAziendaSedeDTO dto : adesioneAziendaDTO
					.getYgAdesioneAziendaSedeDTOList()) {
				if (newSede.getIndirizzo().equals(dto.getIndirizzo())
						&& newSede.getDeComuneDTO().getId()
								.equals(dto.getDeComuneDTO().getId())
						&& newSede.getCap().equals(dto.getCap())) {
					RequestContext.getCurrentInstance().execute(
							"$.jnotify('La sede esiste già', 'error', true);");
					return false;
				}
			}
		}
		return true;
	}

	public void comuneChanged() {
		if (newSede.getDeComuneDTO() == null)
			return;
		DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(newSede
				.getDeComuneDTO().getIdProvincia());
		if (!ConstantsSingleton.COD_REGIONE.toString().equals(
				deProvinciaDTO.getIdRegione())) {
			disableTirocinio = Boolean.TRUE;
			newSede.setFlgTirocinio(Boolean.FALSE);
			String messageText = "Non è possibile segnalare la disponibilità al tirocinio per questa sede in quanto non risulta all'interno della Regione";
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
					messageText, messageText);
			addMessage("newSedeForm:tirocinioFlg", message);
		} else {
			disableTirocinio = Boolean.FALSE;
		}
	}
}
