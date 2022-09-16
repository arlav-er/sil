package it.eng.myportal.beans.reimpiego;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.BdAdesioneDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.ejb.WsBuonoReimpiegoEJB;
import it.eng.myportal.entity.home.BdAdesioneHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.ws.adesioneReimpiego.input.DichiarazioneCheck;

@ManagedBean
@ViewScoped
public class ReimpiegoBean extends AbstractBaseBean{
	
	@EJB
	UtenteInfoHome utenteInfoHome;
	@EJB
	WsBuonoReimpiegoEJB wsBuonoReimpiegoEJB;
	@EJB
	BdAdesioneHome bdAdesioneHome;
	
	protected static Log log = LogFactory.getLog(ReimpiegoBean.class); 
	
	private DichiarazioneCheck tipoDichiarazione;
	private UtenteCompletoDTO utenteCompletoDTO;
	private List<BdAdesioneDTO> bdAdesioneDTOList;
	private Date dataAdesione;
	private String dichiarazione;
	
	
	@PostConstruct
	protected void postConstruct(){
		super.postConstruct();
		utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		List<String> codBandoProgrammas = new ArrayList<String>();
		codBandoProgrammas.add(ConstantsSingleton.DeBandoProgramma.COD_REI);
		bdAdesioneDTOList = bdAdesioneHome.findDTOByIdPfPrincipalCodBandoProgrammas(session.getPrincipalId(), codBandoProgrammas);
		setAdesioneAndDichiarazione(bdAdesioneDTOList);
	}

	//Getter and setter
	public DichiarazioneCheck[] getTipoDichiarazioneValues() {
		return DichiarazioneCheck.values();
	}

	public DichiarazioneCheck getTipoDichiarazione() {
		return tipoDichiarazione;
	}

	public void setTipoDichiarazione(DichiarazioneCheck dichiarazione) {
		this.tipoDichiarazione = dichiarazione;
	}
	
	public List<BdAdesioneDTO> getBdAdesioneDTOList() {
		return bdAdesioneDTOList;
	}
	
	public Date getDataAdesione() {
		return dataAdesione;
	}

	public void setDataAdesione(Date dataAdesione) {
		this.dataAdesione = dataAdesione;
	}

	public String getDichiarazione() {
		return dichiarazione;
	}

	public void setDichiarazione(String dichiarazione) {
		this.dichiarazione = dichiarazione;
	}

	private void setAdesioneAndDichiarazione(List<BdAdesioneDTO> bdAdesioneDTOList){
		for (BdAdesioneDTO current : bdAdesioneDTOList) {
			dataAdesione = current.getDtAdesione();
			dichiarazione = current.getDichiarazione();
		}
	}
	
	//Metodi
	public void richiestaReimpiego(){
		log.info("Inizio richiesta di reimpiego con dichiarazione: " + tipoDichiarazione.name());
		if(bdAdesioneDTOList.isEmpty()){
			try {
				boolean isEsitoInvocazioneServizioPositivo = wsBuonoReimpiegoEJB.callServiceAdesioneReimpiego(utenteCompletoDTO, tipoDichiarazione);
				if(isEsitoInvocazioneServizioPositivo){
					addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Adesione effettuata"));
				}else{
					if(tipoDichiarazione == DichiarazioneCheck.CIGS){
						addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Dai controlli effettuati dal sistema non risulti essere occupato come previsto dal bando Umbriattiva Reimpiego."));						
					}else if(tipoDichiarazione == DichiarazioneCheck.NASPI){
						addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", 
								"Dai controlli effettuati dal sistema non risulti essere in possesso di uno o più dei seguenti requisiti "
								+ "previsti dal bando Umbriattiva Reimpiego: Dichiarazione di Immediata Disponibilità attiva / beneficiario di NASPI / lavoratore licenziato con motivazione "
								+ "\"licenziamento collettivo\" (risultante da comunicazione obbligatoria trasmessa dal datore di lavoro)."));
						addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", 
								"Per ulteriori informazioni puoi rivolgerti direttamente "
								+ "al centro per l’impiego competente, portando con te la documentazione necessaria per l’eventuale adesione. Nel caso di licenziamento collettivo dovrai portare la "
								+ "lettera di recesso/cessazione del rapporto di lavoro da cui si evinca che trattasi di cessazione a seguito di una procedura di licenziamento collettivo "
								+ "(procedura ai sensi degli articoli 4 e 24 della L. 223/1991)."));
					}
				}
			} catch (Exception e) {
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Errore nell'invocazione del servizio"));
			}
		}
		//In questo caso non mi ci dovrei mai trovare perchè è gestito da front-end. Questo è solo un'ulteriore controllo lato back-end!!!
		else{
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Hai già effettuato l'adesione in precedenza"));
		}
		bdAdesioneDTOList = bdAdesioneHome.findDTOByIdPfPrincipal(session.getPrincipalId());
		setAdesioneAndDichiarazione(bdAdesioneDTOList);
		RequestContext.getCurrentInstance().update("adesione");
		RequestContext.getCurrentInstance().update("messages");
	}
	
	public String getAgendaUrl(){
		return ConstantsSingleton.MYAGENDA_URL;
	}
		
}
