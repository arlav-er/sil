package it.eng.myportal.beans.voucher;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeWsVchEsitoDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VchEntiAccreditatiDTO;
import it.eng.myportal.dtos.VchRichiestaAttDTO;
import it.eng.myportal.entity.ejb.WsAttivaVchEJB;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VchEntiAccreditatiHome;
import it.eng.myportal.entity.home.VchRichiestaAttHome;
import it.eng.myportal.entity.home.decodifiche.DeWsVchEsitoHome;
import it.eng.myportal.exception.MyPortalException;

@ManagedBean(name="vchBean")
@ViewScoped
public class VchBean extends AbstractBaseBean {

	@EJB
	VchRichiestaAttHome vchRichiestaAttHome;
	
	@EJB
	UtenteInfoHome utenteInfoHome;
	
	@EJB
	VchEntiAccreditatiHome vchEntiAccreditatiHome;
	
	@EJB
	WsAttivaVchEJB wsAttivaVchEJB;
	
	@EJB
	DeWsVchEsitoHome deWsVchEsitoHome;
	
	private List<VchRichiestaAttDTO> vchRichiestaAttDTOList = new ArrayList<VchRichiestaAttDTO>();
	private UtenteCompletoDTO utenteDTO;
	private Integer codAttivazione;
	private VchEntiAccreditatiDTO vchEntiAccreditatiDTO;
	private List<VchEntiAccreditatiDTO> vchEntiAccreditatiDTOList = new ArrayList<VchEntiAccreditatiDTO>();
	private String confirmMessage;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		//Storico delle richieste
		vchRichiestaAttDTOList = vchRichiestaAttHome.findAllDTOByUserIdAndCodeRisultato(session.getPrincipalId());
		utenteDTO = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		vchEntiAccreditatiDTOList = vchEntiAccreditatiHome.findAllDTO();
	}

	public List<VchRichiestaAttDTO> getVchRichiestaAttDTOList() {
		return vchRichiestaAttDTOList;
	}

	public void setVchRichiestaAttDTOList(List<VchRichiestaAttDTO> vchRichiestaAttDTOList) {
		this.vchRichiestaAttDTOList = vchRichiestaAttDTOList;
	}

	public int getSizeList() {
		return vchRichiestaAttDTOList.size();
	}

	public UtenteCompletoDTO getUtenteDTO() {
		return utenteDTO;
	}

	public void setUtenteDTO(UtenteCompletoDTO utenteDTO) {
		this.utenteDTO = utenteDTO;
	}

	public Integer getCodAttivazione() {
		return codAttivazione;
	}

	public void setCodAttivazione(Integer codAttivazione) {
		this.codAttivazione = codAttivazione;
	}

	public List<VchEntiAccreditatiDTO> getVchEntiAccreditatiDTOList() {
		return vchEntiAccreditatiDTOList;
	}

	public void setVchEntiAccreditatiDTOList(List<VchEntiAccreditatiDTO> vchEntiAccreditatiDTOList) {
		this.vchEntiAccreditatiDTOList = vchEntiAccreditatiDTOList;
	}

	public VchEntiAccreditatiDTO getVchEntiAccreditatiDTO() {
		return vchEntiAccreditatiDTO;
	}

	public void setVchEntiAccreditatiDTO(VchEntiAccreditatiDTO vchEntiAccreditatiDTO) {
		this.vchEntiAccreditatiDTO = vchEntiAccreditatiDTO;
	}
	
	public String getConfirmMessage() {
		return confirmMessage;
	}

	public void setConfirmMessage(String confirmMessage) {
		this.confirmMessage = confirmMessage;
	}

	public void attivaVch(){
		try{
			//Controlla la correttezza dei parametri
			wsAttivaVchEJB.checkParameterVch(codAttivazione);
			
			//Popola il DTO e lo memorizza nel db
			VchRichiestaAttDTO vchRichiestaAttDTO = populateVchRichiestaAttDTO();
			vchRichiestaAttDTO = vchRichiestaAttHome.persistDTO(vchRichiestaAttDTO, session.getPrincipalId());
			
			//Chiama il servizio e ricevi la risposta
			String codRisposta = wsAttivaVchEJB.callServiceAttivaVch(utenteDTO, vchEntiAccreditatiDTO, codAttivazione);
			//Recupera il messaggio dal codice contenuto nella risposta
			DeWsVchEsitoDTO deWsVchEsitoDTO = deWsVchEsitoHome.toDTO(deWsVchEsitoHome.findById(codRisposta));
			
			if(deWsVchEsitoDTO != null){
				vchRichiestaAttDTO.setDeWsVchEsito(deWsVchEsitoDTO);
			}else{
				deWsVchEsitoDTO = deWsVchEsitoHome.toDTO(deWsVchEsitoHome.findById("NN"));
				vchRichiestaAttDTO.setDeWsVchEsito(deWsVchEsitoDTO);
				vchRichiestaAttDTO.setErroreNonCodificato(codRisposta);
			}
			
			//Modifica record DB
			vchRichiestaAttHome.mergeDTO(vchRichiestaAttDTO, session.getPrincipalId());

			//Fai apparire i messaggi di conferma o errore all'utente
			String messaggio = deWsVchEsitoDTO.getMessaggio();
			if(codRisposta.equals("0")){
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "",messaggio));
				//Aggiorno la lista
				vchRichiestaAttDTOList = vchRichiestaAttHome.findAllDTOByUserIdAndCodeRisultato(session.getPrincipalId());
			}else if(codRisposta.equals("10")||
					codRisposta.equals("20")||
					codRisposta.equals("40")||
					codRisposta.equals("50")){
				addMessage("voucherForm:codiceAttivazioneInput", new FacesMessage(FacesMessage.SEVERITY_ERROR, "",messaggio));
			}else if(codRisposta.equals("60")){
				addMessage("voucherForm:vchEnteAccreditato", new FacesMessage(FacesMessage.SEVERITY_ERROR, "",messaggio));
			}else{
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",messaggio));
			}
		}catch(MyPortalException e){
			addMessage("voucherForm:codiceAttivazioneInput", new FacesMessage(FacesMessage.SEVERITY_ERROR, "","Il codice di attivazione è formalmente non corretto"));
		} catch (RemoteException e) {
			String msgText = errorsBean.getProperty("generic.servizio_non_disponibile");
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",msgText));
		} catch(Exception e){
			log.error("GRAVE: " + e.getMessage());
			String msgText = errorsBean.getProperty("generic.error");
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",msgText));
		}
	}
	
	//TODO: Aggiustare il lancio dell'eccezione, forse bisogna crearne una personalizzata
	private VchRichiestaAttDTO populateVchRichiestaAttDTO() throws Exception{
		VchRichiestaAttDTO dto = new VchRichiestaAttDTO();
		Date date = new Date();
		dto.setDtRichiesta(date);
		if(utenteDTO.getCodiceFiscale()!=null && utenteDTO.getCodiceFiscale()!=""){
			dto.setCodiceFiscaleCitt(utenteDTO.getCodiceFiscale());
		}else{
			//TODO: Aggiustare il lancio dell'eccezione, forse bisogna crearne una personalizzata
			throw new Exception("Il codice fiscale non è valorizzato. Inserire il codice fiscale");
		}
		//TODO:Capire se bisogna anche qui lanciare un'eccezione per essere certi della presenza dei dati obbligatori
		dto.setVchEntiAccreditati(vchEntiAccreditatiDTO);
		dto.setCodAttivazione(codAttivazione);
		dto.setDeWsVchEsito(null);
		dto.setErroreNonCodificato(null);
		return dto;
		
	}
	
}
