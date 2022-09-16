package it.eng.myportal.beans.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.PercorsoLavoratoreElementDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.ejb.stateless.app.PercorsoLavoratoreEjb;
import it.eng.myportal.entity.SintesiProto;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.SintesiProtoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.exception.MyPortalNotFoundException;
import it.eng.myportal.exception.PercorsoLavoratoreException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.sintesi.protocollo.webservices.ProtocolloServiceClientEJB;

/**
 * BackingBean per la pagina relativa al percorso lavoratore.
 *
 * @author Turrini S, Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class PercorsoLavoratoreBean extends AbstractServiceBaseBean {
	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	ProtocolloServiceClientEJB protocolloServiceClientEJB;

	@EJB
	PercorsoLavoratoreEjb percorsoLavoratoreEjb;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	SintesiProtoHome sintesiProtoHome;

	private boolean canLoadService = false;

	private Date dataInizio;
	private Date dataFine;
	private String tipoInformazione;

	/**
	 * Risultato della chiamata al servizio SIL
	 */
	private String risultato = "";
	private String risultatoProtocollo = "";

	private JSONObject jsonPercLavResponse;
	// tradotti per JSF
	private List<PercorsoLavoratoreElementDTO> listaElementi;

	/**
	 * Chiamata al servizio SIL effettuata
	 */
	private boolean reqTried = Boolean.FALSE;
	private boolean reqProtocollo = Boolean.FALSE;

	private String dataInStr;

	private String dataFinStr;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		reqProtocollo = Boolean.FALSE;
		reqTried = Boolean.FALSE;
		if (!utenteInfo.getAcceptedInformativaPercLav()) {
			addErrorMessage("services.denied.perc_lav");
			redirectHome();
		}
		tipoInformazione = "B_S";
		canLoadService = true;

	}

	public boolean isCanLoadService() {
		return canLoadService;
	}

	public void setCanLoadService(boolean canLoadService) {
		this.canLoadService = canLoadService;
	}

	public void chiamaServizio() {
		Integer idPfPrincipal = session.getConnectedUtente().getId();
		String response = "";
		try {
			reqTried = Boolean.TRUE;
			risultato = "";
			PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOById(idPfPrincipal);
			UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(pfPrincipalDTO.getUsername());

			if (!session.getConnectedUtente().getAbilitatoServizi()) {
				throw new PercorsoLavoratoreException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
			}

			response = percorsoLavoratoreEjb.callPercorso(utenteDTO, dataInizio, dataFine, tipoInformazione);

			jsonPercLavResponse = XML.toJSONObject(response);
			JSONArray elementsArray = PercorsoLavoratoreEjb.toJSONArray(jsonPercLavResponse);

			listaElementi = percorsoLavoratoreEjb.toPercorsoLavoratoreElementList(elementsArray);
			for(PercorsoLavoratoreElementDTO current: listaElementi) {
				Date dataDa = current.getDataDa();
				if(dataDa != null) {
					DateFormat dfDa = new SimpleDateFormat("dd/MM/yyyy");
					current.setDataDaStr(dfDa.format(dataDa));					
				}
				Date dataA = current.getDataA();
				if(dataA != null) {
					DateFormat dfA = new SimpleDateFormat("dd/MM/yyyy");
					current.setDataAStr(dfA.format(dataA));					
				}
			}

			risultato = "OK";

		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException in callPercorso: ", e);
			risultato = PercorsoLavoratoreEjb.ERRORE_GENERICO;
		} catch (TransformerException e) {
			log.error("TransformerException in callPercorso: ", e);
			risultato = PercorsoLavoratoreEjb.ERRORE_GENERICO;
		} catch (SAXException e) {
			log.error("SAXException in callPercorso: ", e);
			risultato = PercorsoLavoratoreEjb.ERRORE_GENERICO;
		} catch (IOException e) {
			log.error("IOException in callPercorso: ", e);
			risultato = PercorsoLavoratoreEjb.ERRORE_GENERICO;
		} catch (PercorsoLavoratoreException e) {
			log.error("PercorsoLavoratoreException in callPercorso: ", e);
			log.error("la RESPONSE era: " + response);
			risultato = e.getMessage();
		} catch (JSONException e) {
			log.error(e.getMessage());
			risultato = PercorsoLavoratoreEjb.ERRORE_GENERICO;
		} catch (Exception e) {
			log.error(e.getMessage());
			risultato = PercorsoLavoratoreEjb.ERRORE_GENERICO;
		}
	}

	public StreamedContent downloadPercorsoPuglia() throws Exception {
		StreamedContent result = null;

		Integer idPfPrincipal = session.getConnectedUtente().getId();
		PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOById(idPfPrincipal);
		UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(pfPrincipalDTO.getUsername());

		if (!session.getConnectedUtente().getAbilitatoServizi()) {
			throw new PercorsoLavoratoreException("Non sei abilitato all'utilizzo dei servizi per il cittadino.");
		}

		DeProvinciaDTO provinciaRif = utenteDTO.getProvinciaRiferimento();
		reqProtocollo = Boolean.TRUE;
		risultatoProtocollo = "";
		try {
			// 2 - creare nuova riga SintesiProto e salvarci il field "jsonPercLavResponse", gia` ottenuto
			SintesiProto nuovaStampaProtocollata = new SintesiProto();

			nuovaStampaProtocollata.setJsonPercLavResponse(jsonPercLavResponse.toString());

			nuovaStampaProtocollata.setCognomeRichiedente(utenteDTO.getCognome());
			nuovaStampaProtocollata.setNomeRichiedente(utenteDTO.getNome());
			nuovaStampaProtocollata.setCodFisRichiedente(utenteDTO.getCodiceFiscale());
			nuovaStampaProtocollata.setPfPrincipal(pfPrincipalHome.fromDTO(pfPrincipalDTO));
			nuovaStampaProtocollata.setTipologiaRichiesta("C2S");
			protocolloServiceClientEJB.staccaProtocolloSintesi(nuovaStampaProtocollata, provinciaRif.getId());
			if (nuovaStampaProtocollata.getNumProtocollo() != null) {
				sintesiProtoHome.persist(nuovaStampaProtocollata, idPfPrincipal);
				risultatoProtocollo = "OK";
				log.info("Inserimento effettuato in SintesiProto:" + nuovaStampaProtocollata.getNumProtocollo());

				// costruisco il jasperReport
				ByteArrayInputStream byteArrayPDF = protocolloServiceClientEJB.generaPdfPercorsoLav(utenteDTO,
						nuovaStampaProtocollata);
				result = new DefaultStreamedContent(byteArrayPDF, "application/pdf",
						"C2Storico_" + nuovaStampaProtocollata.getCognomeRichiedente() + ".pdf");

			} else {
				log.error("Errore durante in fase di storicizzazione sulla sintesi_proto");
				risultatoProtocollo = "Stampa Sinstesi: Erorre durante in fase di storicizzazione";
			}
		} catch (IOException e) {
			log.error("Errore durante la generazione del file pdf" + e.getMessage());
			risultatoProtocollo = "Stampa Sinstesi: Errore durante la generazione del file pdf";
		} catch (MyPortalNotFoundException ex) {
			log.error(ex.getMessage());
		} catch (Exception ex) {
			log.error("Errore in downloadPercorsoPuglia(): " + ex.getMessage());
			risultatoProtocollo = "Stampa Sinstesi: Errore grave impossibile elaborare la richiesta di stampa!";
		}
		return result;
	}
	
	public String downloadPercorso() throws Exception {
		Integer idPfPrincipal = session.getConnectedUtente().getId();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dateAsStr = df.format(dataInizio);
		
		String url = ConstantsSingleton.BASE_URL + "/secure/rest/services/stampa_percorso_lavoratore?user_id=" + idPfPrincipal.toString();
		url += "&data_inizio=" + dateAsStr;
		return url;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public String getTipoInformazione() {
		return tipoInformazione;
	}

	public void setTipoInformazione(String tipoInformazione) {
		this.tipoInformazione = tipoInformazione;
	}

	public String getRisultato() {
		return risultato;
	}

	public void setRisultato(String risultato) {
		this.risultato = risultato;
	}

	public List<PercorsoLavoratoreElementDTO> getListaElementi() {
		return listaElementi;
	}

	public void setListaElementi(List<PercorsoLavoratoreElementDTO> listaElementi) {
		this.listaElementi = listaElementi;
	}

	public String getDataInStr() {
		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		dataInStr = formatter1.format(dataInizio);
		return dataInStr;
	}

	public void setDataInStr(String dataInStr) {
		this.dataInStr = dataInStr;
	}

	public String getDataFinStr() {
		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		dataFinStr = formatter1.format(dataFine);
		return dataFinStr;
	}

	public void setDataFinStr(String dataFinStr) {
		this.dataFinStr = dataFinStr;
	}

	public boolean isReqTried() {
		return reqTried;
	}

	public void setReqTried(boolean reqTried) {
		this.reqTried = reqTried;
	}

	public String getRisultatoProtocollo() {
		return risultatoProtocollo;
	}

	public void setRisultatoProtocollo(String risultatoProtocollo) {
		this.risultatoProtocollo = risultatoProtocollo;
	}

	public boolean isReqProtocollo() {
		return reqProtocollo;
	}

	public void setReqProtocollo(boolean reqProtocollo) {
		this.reqProtocollo = reqProtocollo;
	}
	
	private List<PercorsoLavoratoreElementDTO> filteredValues;
	public List<PercorsoLavoratoreElementDTO> getFilteredValues() {
		return filteredValues;
	}
	public void setFilteredValues(List<PercorsoLavoratoreElementDTO> filteredValues) {
		this.filteredValues = filteredValues;
	}
	
}
