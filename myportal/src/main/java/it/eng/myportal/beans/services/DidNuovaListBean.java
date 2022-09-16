package it.eng.myportal.beans.services;

import it.eng.myportal.dtos.ConferimentoDidDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.home.ConferimentoDidHome;
import it.eng.myportal.entity.home.DidHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsStampaHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoStampa;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.youthGuarantee.sap.LavoratoreType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Backing Bean per una delle pagine della sezione per la nuova DID nazionale.
 * 
 * In questa pagina l'utente vede la lista delle DID passate, e può controllare se è possibile per lui fare richiesta di
 * una nuova DID.
 * 
 * @author gicozza
 */
@ManagedBean
@ViewScoped
public class DidNuovaListBean extends AbstractServiceBaseBean {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final String PDF_MIME_TYPE = "application/pdf";

	private List<WsStampaDTO> stampeDidRegionaliList = new ArrayList<WsStampaDTO>();
	private List<ConferimentoDidDTO> didNazionaliList = new ArrayList<ConferimentoDidDTO>();
	private UtenteCompletoDTO utenteCompleto;
	private Integer idConferimentoDideffettuata;

	@EJB
	private WsStampaHome wsStampaHome;

	@EJB
	private ConferimentoDidHome conferimentoDidHome;

	@EJB
	private DidHome didHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		// Controllo che l'utente abbia accettato l'informativa DID
		if (!utenteInfo.getAcceptedInformativaDid()) {
			addErrorMessage("services.denied.did");
			redirectHome();
			return;
		}

		// Carico i dati dell'utente, ed eventuali DID regionali e nazionali già effettuate
		utenteCompleto = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		stampeDidRegionaliList = wsStampaHome.getStampeDTO(TipoStampa.DID.getCodice(), getSession().getPrincipalId());
		didNazionaliList = conferimentoDidHome.findDTOByIdPfPrincipal(session.getPrincipalId());

		// Se l'utente è arrivato a questa pagina subito dopo aver fatto una did, mi segno il suo ID
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("idConferimentoDid")) {
			String didEffettuataParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("idConferimentoDid");
			idConferimentoDideffettuata = Integer.parseInt(didEffettuataParam);
		}
	}

	/**
	 * Genera il file PDF da scaricare corrispondente a una certa stampa DID regionale
	 */
	public StreamedContent downloadStampaDidRegionale(WsStampaDTO dto) {
		String formattedDataDid = DATE_FORMAT.format(dto.getDtmIns());
		ByteArrayInputStream is = new ByteArrayInputStream(dto.getContenuto());
		return new DefaultStreamedContent(is, PDF_MIME_TYPE, "Dichiarazione DID del " + formattedDataDid + ".pdf");
	}

	/**
	 * Genera il file PDF da scaricare corrispondente a una DID ministeriale.
	 */
	public StreamedContent downloadStampaDidMinistero(ConferimentoDidDTO did) {
		InputStream stampaStream = conferimentoDidHome.getStampaHtmlFile(did.getId());
		StreamedContent result = new DefaultStreamedContent(stampaStream, "application/pdf", "Conferimento_DID_"
				+ did.getDataDid().toString() + ".pdf");
		return result;
	}

	/**
	 * Genera il file PDF corrispondente alla DID appena effettuata.
	 */
	public StreamedContent downloadStampaLatestDidMinistero() {
		InputStream stampaStream = conferimentoDidHome.getStampaHtmlFile(idConferimentoDideffettuata);
		StreamedContent result = new DefaultStreamedContent(stampaStream, "application/pdf", "Conferimento_DID_"
				+ (new Date()).toString() + ".pdf");
		return result;
	}

	/**
	 * Chiamo i WS del ministero per controllare se l'utente può fare richiesta di una DID. Se sì, lo redirigo alla
	 * pagina di richiesta DID, altrimenti lo lascio su questa pagina.
	 */
	public String controllaDisponibilitaDid() {
		String codEntePromotoreSap = "";

		try {
			String identificativoSap = didHome.verificaEsistenzaSap(utenteCompleto.getCodiceFiscale());

			// Se ho la SAP, la richiedo al ministero e controllo se contiene una DID.
			if (identificativoSap != null && !identificativoSap.equalsIgnoreCase("0")) {
				LavoratoreType sap = didHome.richiestaSap(identificativoSap);

				// Se la SAP contiene una DID, impedisco la redirect.
				if (sap.getDatiamministrativi().getStatoinanagrafe() != null
						&& sap.getDatiamministrativi().getStatoinanagrafe().getDisponibilita() != null) {
					return "did_list?didPresente=true&faces-redirect=true";
				}

				// Se il CPI titolare della SAP è fuori dalla regione, impedisco la redirect.
				DeCpiDTO cpiTitolare = deCpiHome.findDTOByCodMin(sap.getDatiinvio().getCodiceentetit());
				DeProvinciaDTO provCpiTitolare = deProvinciaHome.findDTOById(cpiTitolare.getCodProvincia());
				if (provCpiTitolare.getIdRegione().trim().equalsIgnoreCase(ConstantsSingleton.COD_REGIONE.toString())) {
					codEntePromotoreSap = cpiTitolare.getCodCpiMin();
				} else {
					addCustomErrorMessage("La titolarità dei tuoi dati risulta presso un ente esterno alla Regione "
							+ ConstantsSingleton.DESC_REGIONE
							+ ". Ti invitiamo a rivolgerti al tuo Centro per l'Impiego di riferimento.");
					return null;
				}

			}
		} catch (Exception e) {
			log.error("Errore durante la controllaDisponibilitaDid: " + e.toString());
			addCustomErrorMessage("Errore non previsto durante il controllo di disponibilità per la DID.");
			return null;
		}

		// Se è andato tutto bene, mando alla pagina di creazione DID (includendo l'ID dell'ente promotore della SAP)
		return "did_new?faces-redirect=true"
				+ (codEntePromotoreSap.isEmpty() ? "" : "&codEntePromotoreSap=" + codEntePromotoreSap);
	}

	/** Restituisce TRUE se bisogna mostrare il dialog dell'appuntamento dopo il caricamento della pagina */
	public boolean mostraDialogAppuntamento() {
		return idConferimentoDideffettuata != null;
	}

	public boolean mostraDialogDidPresente() {
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("didPresente")) {
			String didPresenteParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("didPresente");
			return Boolean.valueOf(didPresenteParam);
		} else {
			return false;
		}
	}

	public List<WsStampaDTO> getStampeDidRegionaliList() {
		return stampeDidRegionaliList;
	}

	public void setStampeDidRegionaliList(List<WsStampaDTO> stampeDidRegionaliList) {
		this.stampeDidRegionaliList = stampeDidRegionaliList;
	}

	public UtenteCompletoDTO getUtenteCompleto() {
		return utenteCompleto;
	}

	public void setUtenteCompleto(UtenteCompletoDTO utenteCompleto) {
		this.utenteCompleto = utenteCompleto;
	}

	public List<ConferimentoDidDTO> getDidNazionaliList() {
		return didNazionaliList;
	}

	public void setDidNazionaliList(List<ConferimentoDidDTO> didNazionaliList) {
		this.didNazionaliList = didNazionaliList;
	}

	public Integer getIdConferimentoDideffettuata() {
		return idConferimentoDideffettuata;
	}

	public void setIdConferimentoDideffettuata(Integer idConferimentoDideffettuata) {
		this.idConferimentoDideffettuata = idConferimentoDideffettuata;
	}

}
