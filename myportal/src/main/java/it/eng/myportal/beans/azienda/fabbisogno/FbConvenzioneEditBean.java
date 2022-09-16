package it.eng.myportal.beans.azienda.fabbisogno;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeTipoFbConvenzioneDTO;
import it.eng.myportal.dtos.FbAllegatoDTO;
import it.eng.myportal.dtos.FbConvenzioneDTO;
import it.eng.myportal.dtos.FbConvenzioneSedeDTO;
import it.eng.myportal.entity.FbAllegato;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.FbAllegatoHome;
import it.eng.myportal.entity.home.FbConvenzioneHome;
import it.eng.myportal.entity.home.FbConvenzioneSedeHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbConvenzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoFbConvenzioneHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "fbConvenzioneEditBean")
@ViewScoped
public class FbConvenzioneEditBean extends AbstractBaseBean {

	private static final String PATH_CONVENZIONE_LIST = "/faces/secure/azienda/convenzioni_quadro/convenzioni/convenzione_list.xhtml";

	@EJB
	private FbConvenzioneHome fbConvenzioneHome;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private FbConvenzioneSedeHome fbConvenzioneSedeHome;

	@EJB
	private DeTipoFbConvenzioneHome deTipoFbConvenzioneHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private DeStatoFbConvenzioneHome deStatoConvenzioneHome;

	@EJB
	private FbAllegatoHome fbAllegatoHome;

	private FbConvenzioneDTO fbConvenzioneDto;
	private List<FbConvenzioneSedeDTO> convenzioneSediDTOList;
	private FbConvenzioneSedeDTO sediConvenzioneDTO;
	private FbConvenzioneSedeDTO newSedi;
	private FbConvenzioneSedeDTO sedeDaEliminare;
	private List<DeTipoFbConvenzioneDTO> tipos;

	private String fbDeTipoConvenzione;
	private String comune;
	private AziendaSessionDTO connectedAzienda;
	private String strTargaComuneAzienda;
	private String completeComune;
	private Integer convenzioneId;
	private boolean showPanel;

	private List<FbAllegatoDTO> fbAllegatoDTOList;
	private FbAllegatoDTO fbAllegatoDTO;
	private UploadedFile allegatoUpload;
	private String tipo;
	private FbAllegatoDTO eleiminateAlligato;
	private List<String> tipoList;
	private String allegatoFileName;

	@PostConstruct
	public void init() {
		tipos = deTipoFbConvenzioneHome.findAllDTO();

		fbAllegatoDTO = new FbAllegatoDTO();
		fbAllegatoDTOList = new ArrayList<FbAllegatoDTO>();

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if ((map.get("idConvenzione")) != null) {
			try {
				convenzioneId = Integer.parseInt(map.get("idConvenzione"));
				fbConvenzioneDto = fbConvenzioneHome.findDTOById(convenzioneId);
				convenzioneSediDTOList = fbConvenzioneSedeHome.findByConvenzioneId(convenzioneId);
				fbDeTipoConvenzione = fbConvenzioneDto.getCodTipoConvenzione().getDescrizione();

				// Se non sono il proprietario della convenzione, vengo cacciato.
				if (isEntePromotore() && !session.getPrincipalId().equals(fbConvenzioneDto.getIdPrincipalIns())) {
					redirectHome();
				}
			} catch (Exception e) {
				addErrorMessage("data_error_loading");
			}
		} else {
			// registration case
			fbConvenzioneDto = new FbConvenzioneDTO();
			convenzioneSediDTOList = new ArrayList<FbConvenzioneSedeDTO>();
			fbConvenzioneDto.setCodStatoConv(
					deStatoConvenzioneHome.findDTOById(ConstantsSingleton.DeStatoFbConvenzione.IN_LAVORAZIONE));
		}

		// Read Allegato list from the database
		fbAllegatoDTOList = fbAllegatoHome.findAllegatoById(convenzioneId);

		tipoList = new ArrayList<String>();
		tipoList.add("Convenzione");
		tipoList.add("Altro");
		tipo = "Convenzione";

		// Inizializzo la nuova sede (già valida se la domanda non è in lavorazione)
		svuotaNuovaSede();

		// Se sono un'azienda, i miei dati vengono dalla sessione, altrimenti dalla convenzione che sto visualizzando.
		if (isEntePromotore()) {
			connectedAzienda = session.getConnectedAzienda();
		} else {
			connectedAzienda = aziendaInfoHome.findSessionDTOById(fbConvenzioneDto.getIdPrincipalIns());
		}

		strTargaComuneAzienda = "(" + deProvinciaHome.findById(connectedAzienda.getComune().getIdProvincia()).getTarga()
				+ ")";
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

	public boolean getRegioneMode() {
		return session.isRegione();
	}

	/** La data di repertorio deve essere maggiore di fbConvenzione.dataStipula */
	public Date getMinDataRepertorio() {
		return fbConvenzioneDto.getDataStipula();
	}

	/** La data di repertorio deve essere minore della data odierna */
	public Date getMaxDataRepertorio() {
		return new Date();
	}

	public AziendaSessionDTO getConnectedAzienda() {
		return connectedAzienda;
	}

	public void setConnectedAzienda(AziendaSessionDTO connectedAzienda) {
		this.connectedAzienda = connectedAzienda;
	}

	/** Uoload Allegato file */
	public void uploadAllegatoFile(FileUploadEvent event) {
		if (fbAllegatoDTOList.size() >= 3) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Raggiunto il numero massimo di allegati"));
		} else {
			allegatoUpload = event.getFile();
			allegatoFileName = allegatoUpload.getFileName();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					"Allegato caricato correttamente. Per confermare l'invio, premere sul pulsante \"Conferma allegati\" "));
		}
	}

	public void addAllegatoList() {
		try {
			if (allegatoUpload != null) {
				fbAllegatoDTO.setNomeFile(allegatoUpload.getFileName());
				fbAllegatoDTO.setMimeFile(allegatoUpload.getContentType());
				fbAllegatoDTO.setPdf(allegatoUpload.getContents());

				if (tipo != null) {
					if ("Convenzione".equals(tipo))
						fbAllegatoDTO.setTipoAllegato(fbAllegatoDTO.getTipoAllegato().CONVENZIONE);
					else
						fbAllegatoDTO.setTipoAllegato(fbAllegatoDTO.getTipoAllegato().ALTRO);

					if (convenzioneId != null) {
						fbConvenzioneDto = fbConvenzioneHome.findDTOById(convenzioneId);
						fbAllegatoDTO.setIdFbConvenzione(fbConvenzioneDto);
					}
					fbAllegatoDTOList.add(fbAllegatoDTO);

					ArrayList<FbAllegatoDTO> copy = new ArrayList<FbAllegatoDTO>();

					// persist allegati
					for (FbAllegatoDTO dto : fbAllegatoDTOList) {
						if (dto.getId() != null) {
							dto = fbAllegatoHome.mergeDTO(dto, session.getPrincipalId());
							copy.add(dto);
						} else {
							dto = fbAllegatoHome.persistDTO(dto, session.getPrincipalId());
							copy.add(dto);
						}
					}
					fbAllegatoDTOList = copy;
					allegatoUpload = null;

				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"L' allegato è obbligatorio  ", "L' allegato è obbligatorio "));
			}
		} catch (Exception e) {
			addErrorMessage("generic.error");
		}
	}

	public void deleteFromAllegatoFile() {
		if (eleiminateAlligato != null) {
			if (eleiminateAlligato.getIdFbConvenzione().getCodStatoConv().getId()
					.equals(ConstantsSingleton.DeStatoFbConvenzione.CONFERMATA)) {
				fbAllegatoHome.removeById(eleiminateAlligato.getId(), session.getPrincipalId());
				for (int i = 0; i < fbAllegatoDTOList.size(); i++) {
					FbAllegatoDTO element = fbAllegatoDTOList.get(i);
					if (element.getId().equals(eleiminateAlligato.getId())) {
						fbAllegatoDTOList.remove(i);
						break;
					}
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Cancellare l'allegato non è permesso"));
			}
		}
	}

	/** perform confirmation */
	public void confirmConvenzione() {
		// Per prima cosa, accredito tutte le sedi (in futuro chiamerò anche il ws di MyAuth, se necessario)
		for (FbConvenzioneSedeDTO sede : convenzioneSediDTOList) {
			sede.setFlgAccreditata(true);
			fbConvenzioneSedeHome.mergeDTO(sede, session.getPrincipalId());
		}

		// La data scadenza è uguale per tutti: 31/12/2022
		Calendar calScadenza = Calendar.getInstance();
		calScadenza.set(2022, Calendar.DECEMBER, 31);
		fbConvenzioneDto.setDataScadenza(calScadenza.getTime());

		// Setto stato CONFERMATA, data stipula e salvo.
		fbConvenzioneDto.getCodStatoConv().setId(ConstantsSingleton.DeStatoFbConvenzione.CONFERMATA);
		fbConvenzioneDto.setDataStipula(new Date());
		fbConvenzioneDto = fbConvenzioneHome.mergeDTO(fbConvenzioneDto, session.getPrincipalId());
		redirect(PATH_CONVENZIONE_LIST);
	}

	/** Protocolla una convenzione **/
	public void protocollaFbConvenzione() {
		// Per prima cosa, protocollo la convenzione
		fbConvenzioneDto.getCodStatoConv().setId(ConstantsSingleton.DeStatoFbConvenzione.PROTOCOLLATA);
		fbConvenzioneDto.setIdPrincipalProtocollo(session.getPrincipalId());
		// fbConvenzioneDto.setDataProtocollo(new Date());
		fbConvenzioneDto.setDataProtocollazione(new Date());
		fbConvenzioneDto = fbConvenzioneHome.mergeDTO(fbConvenzioneDto, session.getPrincipalId());

		// Il soggetto promotore che ha creato la convenzione diventa anche ente accreditato.
		session.putProfilaturaByDesc(fbConvenzioneDto.getIdPrincipalIns(), ConstantsSingleton.GpGruppo.AZIENDA_DEFAULT,
				ConstantsSingleton.GpRuolo.ENTE_ACCREDITATO_DESC);

		if (fbConvenzioneDto.getCodTipoConvenzione().getId().equals(ConstantsSingleton.DeTipoFbConvenzione.TIROCINI)) {
			// TODO putprofilatura SSE tipo tirocinio
			session.putProfilaturaByDesc(fbConvenzioneDto.getIdPrincipalIns(),
					ConstantsSingleton.GpGruppo.AZIENDA_DEFAULT,
					ConstantsSingleton.GpRuolo.MyStage.ENTE_PROMOTORE_TIR_DESC);
		} else {
			log.info("Non aggiungo profilatura");
		}
		// Provo a geolocalizzare tutte le sedi e sincronizzarle con MyAuth.
		for (FbConvenzioneSedeDTO sede : convenzioneSediDTOList) {
			try {
				sede = fbConvenzioneSedeHome.geolocalizzaSede(sede.getId(), session.getPrincipalId());
			} catch (Exception e) {
				log.error(
						"Errore durante la geolocalizzazione della sede con id " + sede.getId() + " : " + e.toString());
			}

			try {
				sede = fbConvenzioneSedeHome.accreditaSede(sede, fbConvenzioneDto.getIdPrincipalIns(),
						connectedAzienda.getRagioneSociale(), session.getPrincipalId());
			} catch (Exception e) {
				log.error("Errore durante la sincronizzazione con MyAuth della sede con id " + sede.getId() + " : "
						+ e.toString());
			}
		}

		// Ricarico l'intera lista di sedi, per sicurezza
		convenzioneSediDTOList = fbConvenzioneSedeHome.findByConvenzioneId(fbConvenzioneDto.getId());

		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	/** Revoca la convenzione */
	public void revocaFbConvenzione() {
		// Per prima cosa, disaccredito tutte le sedi, se necessario anche su MyAuth
		for (FbConvenzioneSedeDTO sede : convenzioneSediDTOList) {
			sede.setFlgAccreditata(false);
			sede = fbConvenzioneSedeHome.mergeDTO(sede, session.getPrincipalId());
			fbConvenzioneSedeHome.disabilitaSede(sede, session.getPrincipalId());
		}

		// Poi faccio l'effettiva revoca.
		Date now = Calendar.getInstance().getTime();
		fbConvenzioneDto.getCodStatoConv().setId(ConstantsSingleton.DeStatoFbConvenzione.REVOCATA);
		fbConvenzioneDto.setDataRevoca(now);
		fbConvenzioneDto.setIdPrincipalRevoca(session.getPrincipalId());
		fbConvenzioneDto = fbConvenzioneHome.mergeDTO(fbConvenzioneDto, session.getPrincipalId());
		RequestContext.getCurrentInstance().addCallbackParam("success", true);

		// Ricarico l'intera lista di sedi, per sicurezza
		convenzioneSediDTOList = fbConvenzioneSedeHome.findByConvenzioneId(fbConvenzioneDto.getId());
	}

	/** Elimina una sede dalla convenzione */
	public void deleteSede() {
		if (getRegioneMode()) {
			// Se sono utente regionale, significa che la convenzione è protocollata.
			// In questo caso segno solo il flag accreditata della sede a FALSE.
			sedeDaEliminare.setFlgAccreditata(false);
			sedeDaEliminare = fbConvenzioneSedeHome.mergeDTO(sedeDaEliminare, session.getPrincipalId());
			// Inoltre, controllo se è da disabilitare anche su MyAuth
			fbConvenzioneSedeHome.disabilitaSede(sedeDaEliminare, session.getPrincipalId());
			// Ricarico l'intera lista di sedi, per sicurezza
			convenzioneSediDTOList = fbConvenzioneSedeHome.findByConvenzioneId(fbConvenzioneDto.getId());
		} else {
			// Se sono l'ente promotore, significa che la convenzione è ancora in lavorazione.
			// In questo caso elimino effettivamente la sede dal DB.
			convenzioneSediDTOList.remove(sedeDaEliminare);
			// Controllo, perchè potrebbe non essere ancora stata persistita.
			if (sedeDaEliminare.getId() != null) {
				fbConvenzioneSedeHome.removeById(sedeDaEliminare.getId(), session.getPrincipalId());
			}
		}
	}

	/** Delete Convenzione information including sedi **/
	public void deleteAll() {
		if (fbConvenzioneDto.getId() != null) {

			List<FbConvenzioneSedeDTO> listDTO = fbConvenzioneSedeHome.findByConvenzioneId(fbConvenzioneDto.getId());
			for (FbConvenzioneSedeDTO nextSede : listDTO)
				fbConvenzioneSedeHome.removeById(nextSede.getId(), session.getPrincipalId());

			FbConvenzioneDTO convenzioneDTO = fbConvenzioneHome.findDTOById(fbConvenzioneDto.getId());
			if (convenzioneDTO != null)
				fbConvenzioneHome.removeById(fbConvenzioneDto.getId(), session.getPrincipalId());
		}

		redirect(PATH_CONVENZIONE_LIST);
	}

	public List<DeComuneDTO> completeComune(String query) {
		return deComuneHome.findBySuggestion(query);
	}

	/** Regole per chi/quando può salvare la convenzione */
	public boolean canSaveConvenzione() {
		if (isEntePromotore()) {
			return fbConvenzioneDto.isInLavorazione();
		} else {
			return false;
		}
	}

	/** Regole per chi/quando può inserire una nuova sede */
	public boolean canInsertNewSede() {
		if (isEntePromotore()) {
			// L'ente promotore può inserire/eliminare sedi solo quando l'adesione è in lavorazione
			return (fbConvenzioneDto.isInLavorazione());
		} else if (getRegioneMode()) {
			// La regione può inserire/eliminare sedi quando l'adesione è protocollata
			return (fbConvenzioneDto.isProtocolla());
		} else {
			return false;
		}
	}

	/** Regole per chi/quando può rimuovere una sede */
	public boolean canRemoveSede(FbConvenzioneSedeDTO sede) {
		if (isEntePromotore()) {
			// L'ente promotore può inserire/eliminare sedi solo quando l'adesione è in lavorazione
			return (fbConvenzioneDto.isInLavorazione());
		} else if (getRegioneMode()) {
			// Si può invalidare la sede se la convenzione è protocollata, se non è già invalidata e se non è
			// l'ultima.
			return (fbConvenzioneDto.isProtocolla() && hasMoreThanOneSede() && sede.getFlgAccreditata());
		} else {
			return false;
		}
	}

	public boolean hasMoreThanOneSede() {
		// Conto il numero di sedi non invalidate, la regione non può invalidare l'ultima.
		int numSediValide = 0;
		for (FbConvenzioneSedeDTO sedeTemp : convenzioneSediDTOList) {
			if (sedeTemp.getFlgAccreditata()) {
				numSediValide++;
			}
		}
		return numSediValide > 1;
	}

	/** Una sede è rimossa se la convenzione è protocollata/revocata, e la sede ha flgAccreditata a false */
	public boolean isSedeRimossa(FbConvenzioneSedeDTO sede) {
		return (fbConvenzioneDto.isProtocolla() || fbConvenzioneDto.isRevoca()) && !sede.getFlgAccreditata();
	}

	/** Data/numero protocollo sono modificabili dalla regione se la convenzione è CONFERMATA */
	public boolean canEditProtocolloData() {
		return (session.isRegione() && fbConvenzioneDto.isConfirmed());
	}

	/** Se sei un'azienda e i dati non sono nulli, OPPURE se sei regione e la convenzione è PROTOCOLLATA/REVOCATA */
	public boolean canOnlyViewProtocolloData() {
		if (isEntePromotore()) {
			return (fbConvenzioneDto.getDataProtocollo() != null || fbConvenzioneDto.getNumProtocollo() != null);
		} else if (session.isRegione()) {
			return fbConvenzioneDto.isProtocolla() || fbConvenzioneDto.isRevoca();
		} else {
			return false;
		}
	}

	/** Adding new rows each time when the user adding new sede information */
	public void salvaNuovaSede() {
		// Se esiste già una sede con lo stesso indirizzo, fermo tutto.
		if (verificaEsistenzaSede()) {
			RequestContext.getCurrentInstance().addCallbackParam("sedeGiaEsistente", true);
			return;
		}

		// Solo se la convenzione è già stata salvata, salvo/mergio subito la sede sul DB
		convenzioneSediDTOList.add(newSedi);
		if (fbConvenzioneDto.getId() != null) {
			if (newSedi.getId() == null) {
				newSedi.setConvenzione(fbConvenzioneDto);
				FbConvenzioneSedeDTO persisted = fbConvenzioneSedeHome.persistDTO(newSedi, session.getPrincipalId());
				newSedi.setId(persisted.getId());
			} else {
				newSedi = fbConvenzioneSedeHome.mergeDTO(newSedi, session.getPrincipalId());
			}

			// Solo se sono in modalità regione, provo a geolocalizzare la sede e a sincronizzarla con MyAuth
			if (getRegioneMode()) {
				try {
					newSedi = fbConvenzioneSedeHome.geolocalizzaSede(newSedi.getId(), session.getPrincipalId());
				} catch (Exception e) {
					log.error("Errore durante la geolocalizzazione della sede con id " + newSedi.getId() + " : "
							+ e.toString());
				}

				try {
					newSedi = fbConvenzioneSedeHome.accreditaSede(newSedi, fbConvenzioneDto.getIdPrincipalIns(),
							connectedAzienda.getRagioneSociale(), session.getPrincipalId());
				} catch (Exception e) {
					log.error("Errore durante la sincronizzazione con MyAuth della sede con id " + newSedi.getId()
							+ " : " + e.toString());
				}
			}

			// Ricarico l'intera lista di sedi, per sicurezza
			convenzioneSediDTOList = fbConvenzioneSedeHome.findByConvenzioneId(fbConvenzioneDto.getId());
		}

		RequestContext.getCurrentInstance().addCallbackParam("success", true);
		svuotaNuovaSede();
		showPanel = false;
	}

	private boolean verificaEsistenzaSede() {
		for (FbConvenzioneSedeDTO sedeDTO : convenzioneSediDTOList) {
			if (sedeDTO.getFlgAccreditata()) {
				String newIndirizzoNormalizzato = newSedi.getIndirizzo().trim().replaceAll("[^A-Za-z0-9]+", "")
						.toUpperCase();
				String oldIndirizzoNormalizzato = sedeDTO.getIndirizzo().trim().replaceAll("[^A-Za-z0-9]+", "")
						.toUpperCase();

				if (sedeDTO.getComune().getId().equals(newSedi.getComune().getId())
						&& sedeDTO.getCap().equals(newSedi.getCap())
						&& newIndirizzoNormalizzato.equalsIgnoreCase(oldIndirizzoNormalizzato)) {
					return true;
				}
			}
		}

		return false;
	}

	public void svuotaNuovaSede() {
		newSedi = new FbConvenzioneSedeDTO();
		if (!fbConvenzioneDto.isInLavorazione()) {
			newSedi.setFlgAccreditata(true);
		}
	}

	public void onComuneSelected() {
		if (newSedi != null && newSedi.getComune() != null) {
			newSedi.setCap(newSedi.getComune().getCap());
		}
	}

	/** Save convenzione information after editing saved data */
	public void saveEditConvenzione() {
		if (fbConvenzioneDto.getId() != null) {
			fbConvenzioneDto = fbConvenzioneHome.mergeDTO(fbConvenzioneDto, session.getPrincipalId());

		} else {
			// In questo caso, sto salvando una nuova convenzione
			// Controllo che non esista già una convenzione attiva di questo tipo.
			if (fbConvenzioneHome.countConvenzioniAttiveByIdPrincipalAndTipo(session.getPrincipalId(),
					fbConvenzioneDto.getCodTipoConvenzione().getId()) > 0) {
				RequestContext.getCurrentInstance().addCallbackParam("convenzioneGiaEsistente", true);
				return;
			}

			fbConvenzioneDto = fbConvenzioneHome.persistDTO(fbConvenzioneDto, session.getPrincipalId());
			for (FbConvenzioneSedeDTO nextSedi : convenzioneSediDTOList) {
				nextSedi.setConvenzione(fbConvenzioneDto);
				FbConvenzioneSedeDTO persisted = fbConvenzioneSedeHome.persistDTO(nextSedi, session.getPrincipalId());
				nextSedi.setId(persisted.getId());
			}
		}

		// Parametro per dire alla pagina di mostrare il dialog di successo
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	/**
	 * La stampa può essere generata se la convenzione è PROTOCOLLATA o CONFERMATA
	 */
	public boolean canStampaConvenzione() {
		return fbConvenzioneDto.getId() != null && (fbConvenzioneDto.isConfirmed());
	}

	public boolean canUploadConvenzioneAllegato() {
		return !getRegioneMode() && fbConvenzioneDto.getId() != null && (fbConvenzioneDto.isConfirmed());
	}

	public StreamedContent downloadStampaAllegato(Integer allegatoid) throws IOException {
		// FacesContext fc = FacesContext.getCurrentInstance();
		// ExternalContext ec = fc.getExternalContext();

		FbAllegato all = fbAllegatoHome.findById(allegatoid);

		InputStream inputStream = new ByteArrayInputStream(all.getPdf());
		StreamedContent result = new DefaultStreamedContent(inputStream, all.getMimeFile(), all.getNomeFile());
		return result;
	}

	/**
	 * Genera il file PDF corrispondente alla convenzione.
	 */
	public StreamedContent downloadStampaConvenzione() {
		String filename = "";
		if (fbConvenzioneDto.getCodTipoConvenzione().getId()
				.equals(ConstantsSingleton.DeTipoFbConvenzione.MULTIMISURA)) {
			filename = "Convenzione Multimisura";
		} else if (fbConvenzioneDto.getCodTipoConvenzione().getId()
				.equals(ConstantsSingleton.DeTipoFbConvenzione.TIROCINI)) {
			filename = "Convenzione Tirocini";
		} else if (fbConvenzioneDto.getCodTipoConvenzione().getId()
				.equals(ConstantsSingleton.DeTipoFbConvenzione.DOTE)) {
			filename = "Avviso Dote";
		}
		filename = filename + " - " + connectedAzienda.getRagioneSociale() + ".pdf";

		InputStream stampaStream = fbConvenzioneHome.getStampaHtmlFile(fbConvenzioneDto.getId());
		StreamedContent result = new DefaultStreamedContent(stampaStream, "application/pdf", filename);
		return result;
	}

	public void showInsertPanel() {
		showPanel = true;
	}

	public FbConvenzioneDTO getFbConvenzioneDto() {
		return fbConvenzioneDto;
	}

	public void setFbConvenzioneDto(FbConvenzioneDTO fbConvenzioneDto) {
		this.fbConvenzioneDto = fbConvenzioneDto;
	}

	public List<DeTipoFbConvenzioneDTO> getTipos() {
		return tipos;
	}

	public void setTipos(List<DeTipoFbConvenzioneDTO> tipos) {
		this.tipos = tipos;
	}

	public String getFbDeTipoConvenzione() {
		return fbDeTipoConvenzione;
	}

	public void setFbDeTipoConvenzione(String fbDeTipoConvenzione) {
		this.fbDeTipoConvenzione = fbDeTipoConvenzione;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public List<FbConvenzioneSedeDTO> getConvenzioneSediDTOList() {
		return convenzioneSediDTOList;
	}

	public void setConvenzioneSediDTOList(List<FbConvenzioneSedeDTO> convenzioneSediDTOList) {
		this.convenzioneSediDTOList = convenzioneSediDTOList;
	}

	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}

	public FbConvenzioneSedeDTO getSediConvenzioneDTO() {
		return sediConvenzioneDTO;
	}

	public void setSediConvenzioneDTO(FbConvenzioneSedeDTO sediConvenzioneDTO) {
		this.sediConvenzioneDTO = sediConvenzioneDTO;
	}

	public FbConvenzioneSedeDTO getNewSedi() {
		return newSedi;
	}

	public void setNewSedi(FbConvenzioneSedeDTO newSedi) {
		this.newSedi = newSedi;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public String getStrTargaComuneAzienda() {
		return strTargaComuneAzienda;
	}

	public void setStrTargaComuneAzienda(String provinceAzendia) {
		this.strTargaComuneAzienda = provinceAzendia;
	}

	public String getCompleteComune() {
		return completeComune;
	}

	public void setCompleteComune(String completeComune) {
		this.completeComune = completeComune;
	}

	public Integer getConvenzioneId() {
		return convenzioneId;
	}

	public void setConvenzioneId(Integer convenzioneId) {
		this.convenzioneId = convenzioneId;
	}

	public FbConvenzioneSedeDTO getSedeDaEliminare() {
		return sedeDaEliminare;
	}

	public void setSedeDaEliminare(FbConvenzioneSedeDTO sedeDaEliminare) {
		this.sedeDaEliminare = sedeDaEliminare;
	}

	public List<FbAllegatoDTO> getFbAllegatoDTOList() {
		return fbAllegatoDTOList;
	}

	public void setFbAllegatoDTOList(List<FbAllegatoDTO> fbAllegatoDTOList) {
		this.fbAllegatoDTOList = fbAllegatoDTOList;
	}

	public FbAllegatoDTO getFbAllegatoDTO() {
		return fbAllegatoDTO;
	}

	public void setFbAllegatoDTO(FbAllegatoDTO fbAllegatoDTO) {
		this.fbAllegatoDTO = fbAllegatoDTO;
	}

	public UploadedFile getAllegatoUpload() {
		return allegatoUpload;
	}

	public void setAllegatoUpload(UploadedFile allegatoUpload) {
		this.allegatoUpload = allegatoUpload;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public FbAllegatoDTO getEleiminateAlligato() {
		return eleiminateAlligato;
	}

	public void setEliminaAllegato(FbAllegatoDTO eleiminateAlligato) {
		this.eleiminateAlligato = eleiminateAlligato;
	}

	public List<String> getTipoList() {
		return tipoList;
	}

	public void setTipoList(List<String> tipoList) {
		this.tipoList = tipoList;
	}

	public String getAllegatoFileName() {
		return allegatoFileName;
	}

	public void setAllegatoFileName(String allegatoFileName) {
		this.allegatoFileName = allegatoFileName;
	}

}
