package it.eng.myportal.rest.yg;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.DatiLavoratoreAppuntamentoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.YgGaranziaOver;
import it.eng.myportal.entity.YgImpostazioni;
import it.eng.myportal.entity.enums.TipoAppuntamentoEnum;
import it.eng.myportal.entity.home.AgAppAnagraficaHome;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;
import it.eng.myportal.entity.home.YgImpostazioniHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbienteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAppuntamentoHome;
import it.eng.myportal.enums.ErroriFissaAppuntamento;
import it.eng.myportal.exception.FissaAppuntamentoException;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.DatiContatto;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.ParametriAppuntamento.ContattiAutomatici;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.ParametriAppuntamento.DatiRicerca;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.UtenteLavoratore;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.UtenteLavoratore.Lavoratore;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.UtenteLavoratore.Lavoratore.Domicilio;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Appuntamento.UtenteLavoratore.Lavoratore.Residenza;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.ObjectFactory;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.input.Sesso;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta.Esito;
import it.eng.myportal.utils.CfUtils;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.agenda.appuntamento.AppuntamentoServiceProxy;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.xml.sax.SAXException;

@Stateless
@Path("rest/services/")
public class PrenotaAppuntamento {

	protected static Log log = LogFactory.getLog(PrenotaAppuntamento.class);
	private static String ESITO_POSITIVO = "00";
	private static String ESITO_ERRORE_CONCORRENZA = "98";

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private YgGaranziaOverHome ygGaranziaOverHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeTipoAppuntamentoHome deTipoAppuntamentoHome;

	@EJB
	private DeAmbienteSilHome deAmbienteSilHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private AgAppAnagraficaHome agAppAnagraficaHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private YgImpostazioniHome ygImpostazioniHome;

	@GET
	@Path("fissa_appuntamento")
	public String fissaAppuntamentoYG(@QueryParam("user_id") Integer idPfPrincipal,
			@QueryParam("id_yg_adesione") Integer idYgAdesione,
			@QueryParam("identificativo_slot") BigInteger identificativoSlot, @Context HttpServletRequest request) {
		/*
		 * al servizio di prenotazione ripasso tutti i dati anche se probabilmente molti sono inutili dato che ho
		 * l'identificativo dello slot. Ciononostante si e' oculatamente deciso di lasciare anche tutti i dati di prima
		 * senza un motivo apparente.
		 */
		String outputXML = null;
		try {
			Principal princ = request.getUserPrincipal();
			if (princ == null) {
				log.error("Prenotaziona appuntamento fallita: sessione nulla, utente non loggato");
				throw new FissaAppuntamentoException();
			}
			String username = AuthUtil.removeSocialPrefix(princ.getName());

			if (idPfPrincipal == null) {
				log.error("Prenotaziona appuntamento fallita: id utente non passato");
				throw new FissaAppuntamentoException();
			}

			UtenteCompletoDTO utenteCompletoDTO = utenteInfoHome.findDTOCompletoByUsername(username);
			if (utenteCompletoDTO == null) {
				log.error("Prenotaziona appuntamento fallita: recupero utente nullo username=" + username);
				throw new FissaAppuntamentoException();
			} else if (utenteCompletoDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new FissaAppuntamentoException("Non puoi fissare un appuntamento per un altro lavoratore.");
			}

			if (StringUtils.isEmpty(utenteCompletoDTO.getCodiceFiscale())) {
				throw new FissaAppuntamentoException("Non hai indicato il tuo codice fiscale.");
			}

			DeProvinciaDTO provinciaRiferimento = utenteCompletoDTO.getProvinciaRiferimento();
			YgAdesione adesione = ygAdesioneHome.findById(idYgAdesione);

			if (adesione.getDtPresaInCarico() != null) {
				throw new FissaAppuntamentoException("E' stato già fissato un appuntamento per la tua adesione.");
			}

			String inputXML = "";
			utenteCompletoDTO.setCodiceFiscale(utenteCompletoDTO.getCodiceFiscale().toUpperCase());
			utenteCompletoDTO.setNome(utenteCompletoDTO.getNome().toUpperCase());
			utenteCompletoDTO.setCognome(utenteCompletoDTO.getCognome().toUpperCase());
			boolean invioEmail = true;
			try {
				inputXML = inputToXml(utenteCompletoDTO, provinciaRiferimento.getId(), adesione.getDeCpiAssegnazione()
						.getCodCpi(), utenteCompletoDTO.getFlgConsensoSms(), invioEmail, identificativoSlot,
						TipoAppuntamentoEnum.PL_APPGG);
				log.debug("FissaAppuntamento input:\n" + inputXML);
			} catch (JAXBException e) {
				log.error(e);
				return Utils.buildErrorResponse(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio())
						.toString();
			} catch (SAXException e) {
				log.error(e);
				return Utils.buildErrorResponse(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio())
						.toString();
			}

			Risposta risposta = null;
			// Proxy del servizio su SIL
			AppuntamentoServiceProxy service = new AppuntamentoServiceProxy(
					wsEndpointHome.getAppuntamentoAddress(provinciaRiferimento.getId()));

			// gestione errore concorrenza
			int numeroTentativi = 0;
			boolean tentaFissaApputamento = true;

			while (tentaFissaApputamento) {
				numeroTentativi += 1;

				outputXML = service.fissaAppuntamento(inputXML);
				risposta = xmlToOutput(outputXML);

				if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
					tentaFissaApputamento = false;
				} else if (risposta.getEsito().getCodice().equals(ESITO_ERRORE_CONCORRENZA)) {
					if (numeroTentativi < ConstantsSingleton.MAX_NUM_TENTATIVI_PRESA_APPUNTAMENTO) {
						tentaFissaApputamento = true;
					} else {
						log.error("Prenotaziona appuntamento fallita dopo " + numeroTentativi
								+ " tentativi per problemi di concorrenza.");
						tentaFissaApputamento = false;
					}
				} else {
					tentaFissaApputamento = false;
				}

			}

			if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
				agAppuntamentoHome.persistAppuntamento(idPfPrincipal, risposta.getDatiAppuntamento(),
						TipoAppuntamentoEnum.PL_APPGG, adesione);
				ygAdesioneHome.aggiornaAdesione(adesione.getIdYgAdesione(), risposta.getDatiAppuntamento(),
						utenteCompletoDTO);
			}

			YgImpostazioni ygImpostazioni = ygImpostazioniHome.findByCodRegione(ConstantsSingleton.COD_REGIONE
					.toString());
			boolean flgDateVisibili = ygImpostazioni.getFlgAbilitazioneParData();

			JSONObject jsonResponse = Utils.buildAppuntamentoResponse(risposta, flgDateVisibili);
			return jsonResponse.toString();
		} catch (FissaAppuntamentoException e) {
			log.error("response = " + outputXML);
			log.error("fissaAppuntamento: " + e.getMessage());
			return Utils.buildErrorResponse(e.getMessage()).toString();
		} catch (RemoteException e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			return Utils.buildErrorResponse(ErroriFissaAppuntamento.NET_ERROR.getMessaggio()).toString();
		} catch (Exception e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			return Utils.buildErrorResponse(ErroriFissaAppuntamento.ERRORE_GENERICO.getMessaggio()).toString();
		}
	}

	public RispostaFissaAppuntamwento fissaAppuntamentoPatronato(Integer idPfPrincipal,
			DatiLavoratoreAppuntamentoDTO datiLavoratoreAppuntamentoDTO, Date dataDal, Date dataAl,
			String mattinaOPomeriggio, BigInteger identificativoSlot, boolean consensoSMS, boolean consensoEmail,
			String codProvincia, String codCpi, TipoAppuntamentoEnum tipoAppuntamento) {
		if (datiLavoratoreAppuntamentoDTO.getCell() != null) {
			datiLavoratoreAppuntamentoDTO.setCell(datiLavoratoreAppuntamentoDTO.getCell().toUpperCase());
		}
		if (datiLavoratoreAppuntamentoDTO.getNome() != null) {
			datiLavoratoreAppuntamentoDTO.setNome(datiLavoratoreAppuntamentoDTO.getNome().toUpperCase());
		}
		if (datiLavoratoreAppuntamentoDTO.getCognome() != null) {
			datiLavoratoreAppuntamentoDTO.setCognome(datiLavoratoreAppuntamentoDTO.getCognome().toUpperCase());
		}
		Integer idAgAppuntamento = null;
		Integer idAgAppAnagrafica = null;
		Risposta risposta = new Risposta();
		risposta.setEsito(new Esito());
		risposta.getEsito().setCodice("99");

		/*
		 * al servizio di prenotazione ripasso tutti i dati anche se probabilmente molti sono inutili dato che ho
		 * l'identificativo dello slot. Ciononostante si e' oculatamente deciso di lasciare anche tutti i dati di prima
		 * senza un motivo apparente.
		 */
		String outputXML = null;
		try {
			String inputXML = "";
			try {
				inputXML = inputToXml(datiLavoratoreAppuntamentoDTO.getCodiceFiscale(),
						datiLavoratoreAppuntamentoDTO.getCell(), datiLavoratoreAppuntamentoDTO.getEmail(),
						datiLavoratoreAppuntamentoDTO.getNome(), datiLavoratoreAppuntamentoDTO.getCognome(),
						datiLavoratoreAppuntamentoDTO.getCodComuneNascita(),
						datiLavoratoreAppuntamentoDTO.getDataNascita(),
						datiLavoratoreAppuntamentoDTO.getCodComuneDomicilio(),
						datiLavoratoreAppuntamentoDTO.getIndirizzoDomicilio(),
						datiLavoratoreAppuntamentoDTO.getCodComuneResidenza(),
						datiLavoratoreAppuntamentoDTO.getIndirizzoResidenza(),
						datiLavoratoreAppuntamentoDTO.getCodCittadinanza(), datiLavoratoreAppuntamentoDTO.getSesso(),
						codProvincia, codCpi, consensoSMS, consensoEmail, dataDal, dataAl, mattinaOPomeriggio,
						identificativoSlot, tipoAppuntamento);
				log.debug("FissaAppuntamento input:\n" + inputXML);
			} catch (JAXBException e) {
				log.error("fissaAppuntamento: " + e.getMessage());
				risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio());
				return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
			} catch (SAXException e) {
				log.error("fissaAppuntamento: " + e.getMessage());
				risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio());
				return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
			}

			// Proxy del servizio su SIL
			AppuntamentoServiceProxy service = new AppuntamentoServiceProxy(
					wsEndpointHome.getAppuntamentoAddress(codProvincia));

			// gestione errore concorrenza
			int numeroTentativi = 0;
			boolean tentaFissaApputamento = true;

			while (tentaFissaApputamento) {
				numeroTentativi += 1;

				outputXML = service.fissaAppuntamento(inputXML);
				risposta = xmlToOutput(outputXML);

				if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
					tentaFissaApputamento = false;
				} else if (risposta.getEsito().getCodice().equals(ESITO_ERRORE_CONCORRENZA)) {
					if (numeroTentativi < ConstantsSingleton.MAX_NUM_TENTATIVI_PRESA_APPUNTAMENTO) {
						tentaFissaApputamento = true;
					} else {
						log.error("Prenotaziona appuntamento fallita dopo " + numeroTentativi
								+ " tentativi per problemi di concorrenza.");
						tentaFissaApputamento = false;
					}
				} else {
					tentaFissaApputamento = false;
				}
			}

			if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
				/* memorizza ag_appuntamento */
				idAgAppuntamento = agAppuntamentoHome.persistAppuntamento(idPfPrincipal,
						risposta.getDatiAppuntamento(), TipoAppuntamentoEnum.PL_APPSE, null);
				/* memorizzo anagrafica lavoratore */
				idAgAppAnagrafica = agAppAnagraficaHome.persistAnagrafica(idPfPrincipal,
						datiLavoratoreAppuntamentoDTO.getNome(), datiLavoratoreAppuntamentoDTO.getCognome(),
						datiLavoratoreAppuntamentoDTO.getSesso(), datiLavoratoreAppuntamentoDTO.getCodiceFiscale(),
						datiLavoratoreAppuntamentoDTO.getDataNascita(),
						datiLavoratoreAppuntamentoDTO.getCodComuneNascita(),
						datiLavoratoreAppuntamentoDTO.getCodComuneDomicilio(),
						datiLavoratoreAppuntamentoDTO.getIndirizzoDomicilio(), datiLavoratoreAppuntamentoDTO.getCell(),
						idAgAppuntamento);
			}
		} catch (FissaAppuntamentoException e) {
			log.error("response = " + outputXML);
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(e.getMessage());
			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		} catch (RemoteException e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.NET_ERROR.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		} catch (Exception e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_GENERICO.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		}
		return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
	}

	public RispostaFissaAppuntamwento fissaAppuntamentoOrientamentoOrdinario(Integer idPfPrincipal,
			UtenteCompletoDTO utenteCompletoDTO, Date dataDal, Date dataAl, String mattinaOPomeriggio,
			BigInteger identificativoSlot, boolean consensoSMS, boolean consensoEmail, String codProvincia,
			String codCpi, TipoAppuntamentoEnum tipoAppuntamento) {
		if (utenteCompletoDTO.getNome() != null) {
			utenteCompletoDTO.setNome(utenteCompletoDTO.getNome().toUpperCase());
		}
		if (utenteCompletoDTO.getCognome() != null) {
			utenteCompletoDTO.setCognome(utenteCompletoDTO.getCognome().toUpperCase());
		}
		Integer idAgAppuntamento = null;
		Integer idAgAppAnagrafica = null;
		Risposta risposta = new Risposta();
		risposta.setEsito(new Esito());
		risposta.getEsito().setCodice("99");

		/*
		 * al servizio di prenotazione ripasso tutti i dati anche se probabilmente molti sono inutili dato che ho
		 * l'identificativo dello slot. Ciononostante si e' oculatamente deciso di lasciare anche tutti i dati di prima
		 * senza un motivo apparente.
		 */
		String outputXML = null;
		try {
			String inputXML = "";
			try {
				if (utenteCompletoDTO.getIndirizzoResidenza() == null) {
					utenteCompletoDTO.setIndirizzoResidenza(utenteCompletoDTO.getIndirizzoDomicilio());
				}
				if (utenteCompletoDTO.getComuneResidenza() == null) {
					utenteCompletoDTO.setComuneResidenza(utenteCompletoDTO.getComuneDomicilio());
				}
				inputXML = inputToXml(utenteCompletoDTO.getCodiceFiscale(), utenteCompletoDTO.getCellulare(),
						utenteCompletoDTO.getEmail(), utenteCompletoDTO.getNome(), utenteCompletoDTO.getCognome(),
						utenteCompletoDTO.getComuneNascita().getId(), utenteCompletoDTO.getDataNascita(),
						utenteCompletoDTO.getComuneDomicilio().getId(), utenteCompletoDTO.getIndirizzoDomicilio(),
						utenteCompletoDTO.getComuneResidenza().getId(), utenteCompletoDTO.getIndirizzoResidenza(),
						utenteCompletoDTO.getCittadinanza().getId(),
						CfUtils.getSesso(utenteCompletoDTO.getCodiceFiscale()), codProvincia, codCpi, consensoSMS,
						consensoEmail, dataDal, dataAl, mattinaOPomeriggio, identificativoSlot, tipoAppuntamento);
				log.debug("FissaAppuntamento input:\n" + inputXML);
			} catch (JAXBException e) {
				log.error("fissaAppuntamento: " + e.getMessage());
				risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio());
				return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
			} catch (SAXException e) {
				log.error("fissaAppuntamento: " + e.getMessage());
				risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio());
				return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
			}

			// Proxy del servizio su SIL
			AppuntamentoServiceProxy service = new AppuntamentoServiceProxy(
					wsEndpointHome.getAppuntamentoAddress(codProvincia));

			// gestione errore concorrenza
			int numeroTentativi = 0;
			boolean tentaFissaApputamento = true;

			while (tentaFissaApputamento) {
				numeroTentativi += 1;

				outputXML = service.fissaAppuntamento(inputXML);
				risposta = xmlToOutput(outputXML);

				if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
					tentaFissaApputamento = false;
				} else if (risposta.getEsito().getCodice().equals(ESITO_ERRORE_CONCORRENZA)) {
					if (numeroTentativi < ConstantsSingleton.MAX_NUM_TENTATIVI_PRESA_APPUNTAMENTO) {
						tentaFissaApputamento = true;
					} else {
						log.error("Prenotaziona appuntamento fallita dopo " + numeroTentativi
								+ " tentativi per problemi di concorrenza.");
						tentaFissaApputamento = false;
					}
				} else {
					tentaFissaApputamento = false;
				}
			}

			if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
				/* memorizza ag_appuntamento */
				idAgAppuntamento = agAppuntamentoHome.persistAppuntamento(idPfPrincipal,
						risposta.getDatiAppuntamento(), TipoAppuntamentoEnum.PL_APPOB, null);
				/* memorizzo anagrafica lavoratore */
				idAgAppAnagrafica = agAppAnagraficaHome.persistAnagrafica(idPfPrincipal, utenteCompletoDTO.getNome(),
						utenteCompletoDTO.getCognome(), CfUtils.getSesso(utenteCompletoDTO.getCodiceFiscale()),
						utenteCompletoDTO.getCodiceFiscale(), utenteCompletoDTO.getDataNascita(), utenteCompletoDTO
								.getComuneNascita().getId(), utenteCompletoDTO.getComuneDomicilio().getId(),
						utenteCompletoDTO.getIndirizzoDomicilio(), utenteCompletoDTO.getCellulare(), idAgAppuntamento);
			}
		} catch (FissaAppuntamentoException e) {
			log.error("response = " + outputXML);
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(e.getMessage());
			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		} catch (RemoteException e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.NET_ERROR.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		} catch (Exception e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_GENERICO.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		}
		return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
	}

	/**
	 * Fissa un appuntamento presso il CPI per un'adesione a Garanzia Over (basato sul metodo fissaAppuntamento per la
	 * Garanzia Giovani).
	 */
	public RispostaFissaAppuntamwento fissaAppuntamentoGaranziaOver(Integer idPfPrincipal, Integer idYgGaranziaOver,
			BigInteger identificativoSlot, String codCpi) {
		String outputXML = null;
		Risposta risposta = new Risposta();
		risposta.setEsito(new Esito());
		risposta.getEsito().setCodice("99");

		try {
			if (idPfPrincipal == null) {
				log.error("Prenotaziona appuntamento fallita: id utente non passato");
				throw new FissaAppuntamentoException();
			}

			// Recupero i dati dell'utente.
			UtenteCompletoDTO utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(idPfPrincipal);
			if (utenteCompletoDTO == null) {
				log.error("Prenotaziona appuntamento fallita: recupero utente nullo id=" + idPfPrincipal);
				throw new FissaAppuntamentoException();
			} else if (utenteCompletoDTO.getId().intValue() != idPfPrincipal.intValue()) {
				throw new FissaAppuntamentoException("Non puoi fissare un appuntamento per un altro lavoratore.");
			}

			if (StringUtils.isEmpty(utenteCompletoDTO.getCodiceFiscale())) {
				throw new FissaAppuntamentoException("Non hai indicato il tuo codice fiscale.");
			}

			// Recupero i dati dell'adesione a Umbriattiva Adulti
			YgGaranziaOver adesione = ygGaranziaOverHome.findById(idYgGaranziaOver);
			if (adesione.getDtPresaInCarico() != null) {
				throw new FissaAppuntamentoException("E' stato già fissato un appuntamento per la tua adesione.");
			}

			String codProvinciaAdesione = null;
			if (adesione.getDeProvincia() != null) {
				codProvinciaAdesione = adesione.getDeProvincia().getCodProvincia();
			} else {
				throw new FissaAppuntamentoException("La tua adesione a Umbriattiva Adulti non ha una provincia.");
			}

			// Chiamo il ws del SIL della provincia dell'adesione a Garanzia Over.
			String inputXML = "";
			utenteCompletoDTO.setCodiceFiscale(utenteCompletoDTO.getCodiceFiscale().toUpperCase());
			utenteCompletoDTO.setNome(utenteCompletoDTO.getNome().toUpperCase());
			utenteCompletoDTO.setCognome(utenteCompletoDTO.getCognome().toUpperCase());
			boolean invioEmail = true;
			try {
				inputXML = inputToXml(utenteCompletoDTO, codProvinciaAdesione, codCpi,
						utenteCompletoDTO.getFlgConsensoSms(), invioEmail, identificativoSlot,
						TipoAppuntamentoEnum.PL_APPGO);
				log.debug("FissaAppuntamento input:\n" + inputXML);
			} catch (JAXBException e) {
				log.error(e);
				risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio());
				return new RispostaFissaAppuntamwento(risposta, null, null);
			} catch (SAXException e) {
				log.error(e);
				risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_VALIDAZIONE_XSD.getMessaggio());
				return new RispostaFissaAppuntamwento(risposta, null, null);
			}

			// Proxy del servizio su SIL
			AppuntamentoServiceProxy service = new AppuntamentoServiceProxy(
					wsEndpointHome.getAppuntamentoAddress(codProvinciaAdesione));

			// gestione errore concorrenza
			int numeroTentativi = 0;
			boolean tentaFissaApputamento = true;

			while (tentaFissaApputamento) {
				numeroTentativi += 1;

				outputXML = service.fissaAppuntamento(inputXML);
				risposta = xmlToOutput(outputXML);

				if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
					tentaFissaApputamento = false;
				} else if (risposta.getEsito().getCodice().equals(ESITO_ERRORE_CONCORRENZA)) {
					if (numeroTentativi < ConstantsSingleton.MAX_NUM_TENTATIVI_PRESA_APPUNTAMENTO) {
						tentaFissaApputamento = true;
					} else {
						log.error("Prenotaziona appuntamento fallita dopo " + numeroTentativi
								+ " tentativi per problemi di concorrenza.");
						tentaFissaApputamento = false;
					}
				} else {
					tentaFissaApputamento = false;
				}

			}

			Integer idAgAppuntamento = null;
			Integer idAgAppAnagrafica = null;
			if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
				idAgAppuntamento = agAppuntamentoHome.persistAppuntamentoGaranziaOver(idPfPrincipal,
						risposta.getDatiAppuntamento(), TipoAppuntamentoEnum.PL_APPGO, adesione);
				idAgAppAnagrafica = agAppAnagraficaHome.persistAnagrafica(idPfPrincipal, utenteCompletoDTO.getNome(),
						utenteCompletoDTO.getCognome(), CfUtils.getSesso(utenteCompletoDTO.getCodiceFiscale()),
						utenteCompletoDTO.getCodiceFiscale(), utenteCompletoDTO.getDataNascita(), utenteCompletoDTO
								.getComuneNascita().getId(), utenteCompletoDTO.getComuneDomicilio().getId(),
						utenteCompletoDTO.getIndirizzoDomicilio(), utenteCompletoDTO.getCellulare(), idAgAppuntamento);
				ygGaranziaOverHome.aggiornaAdesionePostAppuntamento(adesione.getIdYgGaranziaOver(),
						risposta.getDatiAppuntamento(), utenteCompletoDTO);
			}

			return new RispostaFissaAppuntamwento(risposta, idAgAppuntamento, idAgAppAnagrafica);
		} catch (FissaAppuntamentoException e) {
			log.error("response = " + outputXML);
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_GENERICO.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, null, null);
		} catch (RemoteException e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_GENERICO.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, null, null);
		} catch (Exception e) {
			log.error("fissaAppuntamento: " + e.getMessage());
			risposta.getEsito().setDescrizione(ErroriFissaAppuntamento.ERRORE_GENERICO.getMessaggio());
			return new RispostaFissaAppuntamwento(risposta, null, null);
		}
	}

	private String inputToXml(UtenteCompletoDTO utenteCompletoDTO, String codProvincia, String codCpi,
			Boolean consensoSMS, Boolean invioEmail, BigInteger identificativoSlot,
			TipoAppuntamentoEnum tipoAppuntamento) throws Exception {

		String codComuneNascita = null;
		if (utenteCompletoDTO.getComuneNascita() != null) {
			codComuneNascita = utenteCompletoDTO.getComuneNascita().getId();
		}
		String codComuneDomicilio = null;
		if (utenteCompletoDTO.getComuneDomicilio() != null) {
			codComuneDomicilio = utenteCompletoDTO.getComuneDomicilio().getId();
		}
		String codComuneResidenza = null;
		if (utenteCompletoDTO.getComuneResidenza() != null) {
			codComuneResidenza = utenteCompletoDTO.getComuneResidenza().getId();
		}
		String codCittadinanza = null;
		if (utenteCompletoDTO.getCittadinanza() != null) {
			codCittadinanza = utenteCompletoDTO.getCittadinanza().getId();
		}
		String sesso = null;
		if (utenteCompletoDTO.getGenere() != null) {
			sesso = utenteCompletoDTO.getGenere().getId();
		}
		return inputToXml(utenteCompletoDTO.getCodiceFiscale(), utenteCompletoDTO.getCellulare(),
				utenteCompletoDTO.getEmail(), utenteCompletoDTO.getNome(), utenteCompletoDTO.getCognome(),
				codComuneNascita, utenteCompletoDTO.getDataNascita(), codComuneDomicilio,
				utenteCompletoDTO.getIndirizzoDomicilio(), codComuneResidenza,
				utenteCompletoDTO.getIndirizzoResidenza(), codCittadinanza, sesso, codProvincia, codCpi, consensoSMS,
				invioEmail, null, null, null, identificativoSlot, tipoAppuntamento);
	}

	private String inputToXml(String codiceFiscale, String cellulare, String email, String nome, String cognome,
			String codComuneNascita, Date dataNascita, String codComuneDomicilio, String indirizzoDomicilio,
			String codComuneResidenza, String indirizzoResidenza, String codCittadinanza, String sesso,
			String codProvincia, String codCpi, Boolean consensoSMS, Boolean consensoEmail, Date dataDal, Date dataAl,
			String mattinaPomeriggio, BigInteger identificativoSlot, TipoAppuntamentoEnum tipoAppuntamento)
			throws Exception {

		YgImpostazioni ygImpostazioni = ygImpostazioniHome.findByCodRegione(ConstantsSingleton.COD_REGIONE.toString());
		boolean flgInvioSmsImpostazioni = ygImpostazioni.getFlgInvioSms();

		ObjectFactory factory = new ObjectFactory();
		Appuntamento appuntamento = factory.createAppuntamento();

		appuntamento.setCodiceFiscale(codiceFiscale);
		DatiContatto datiContatto = factory.createAppuntamentoDatiContatto();
		if (cellulare != null) {
			datiContatto.setCellulare(cellulare);
		}
		if (consensoSMS != null) {
			datiContatto.setConsensoSMS(consensoSMS ? "S" : "N");
		} else {
			datiContatto.setConsensoSMS("N");
		}
		if (email != null) {
			datiContatto.setEmail(email);
		}
		appuntamento.setDatiContatto(datiContatto);
		if (codProvincia != null) {
			appuntamento.setIdProvincia(codProvincia);
		}
		Appuntamento.ParametriAppuntamento parametriAppuntamento = factory.createAppuntamentoParametriAppuntamento();
		parametriAppuntamento.setCodiceRichiesta(tipoAppuntamento.getCodTipoAppuntamento());
		ContattiAutomatici contattiAutomatici = factory.createAppuntamentoParametriAppuntamentoContattiAutomatici();
		contattiAutomatici.setInvioEmail(consensoEmail ? "S" : "N");

		if (flgInvioSmsImpostazioni) {
			if (consensoSMS != null && consensoSMS && cellulare != null && !"".equalsIgnoreCase(cellulare)) {
				contattiAutomatici.setInvioSMS("S");
			} else {
				contattiAutomatici.setInvioSMS("N");
			}
		} else {
			contattiAutomatici.setInvioSMS("N");
		}

		parametriAppuntamento.setContattiAutomatici(contattiAutomatici);

		if (codCpi != null) {
			parametriAppuntamento.setIdCPI(codCpi);
		}
		if (identificativoSlot == null) {
			DatiRicerca datiRicerca = factory.createAppuntamentoParametriAppuntamentoDatiRicerca();
			if (dataDal != null) {
				datiRicerca.setDataDal(Utils.dateToGregorianDate(dataDal));
			}
			if (dataAl != null) {
				datiRicerca.setDataAl(Utils.dateToGregorianDate(dataAl));
			}
			datiRicerca.setMattinaPomeriggio(mattinaPomeriggio);
			parametriAppuntamento.setDatiRicerca(datiRicerca);
		} else {
			parametriAppuntamento.setIdentificativoSlot(identificativoSlot);
		}
		appuntamento.setParametriAppuntamento(parametriAppuntamento);

		UtenteLavoratore utenteLavoratore = factory.createAppuntamentoUtenteLavoratore();
		Lavoratore lavoratore = factory.createAppuntamentoUtenteLavoratoreLavoratore();
		if (codCittadinanza != null) {
			lavoratore.setCittadinanza(codCittadinanza);
		}
		if (cognome != null) {
			lavoratore.setCognome(cognome);
		}
		if (codComuneNascita != null) {
			lavoratore.setComuneNascita(codComuneNascita);
		}
		try {
			if (dataNascita != null) {
				lavoratore.setDataNascita(Utils.dateToGregorianDate(dataNascita));
			}
		} catch (Exception e) {
			lavoratore.setDataNascita(null);
		}
		Domicilio domicilio = factory.createAppuntamentoUtenteLavoratoreLavoratoreDomicilio();
		if (codComuneDomicilio != null) {
			domicilio.setComune(codComuneDomicilio);
		}
		if (indirizzoDomicilio != null) {
			domicilio.setIndirizzo(indirizzoDomicilio);
		} else {
			domicilio.setIndirizzo("Non disponibile");
		}
		lavoratore.setDomicilio(domicilio);
		if (nome != null) {
			lavoratore.setNome(nome);
		}
		Residenza residenza = factory.createAppuntamentoUtenteLavoratoreLavoratoreResidenza();
		if (codComuneResidenza != null) {
			residenza.setComune(codComuneResidenza);
			if (indirizzoResidenza != null && !"".equalsIgnoreCase(indirizzoResidenza)) {
				residenza.setIndirizzo(indirizzoResidenza);
			} else {
				residenza.setIndirizzo("Non disponibile");
			}
		} else {
			if (codComuneDomicilio != null) {
				residenza.setComune(codComuneDomicilio);
			}
			residenza.setIndirizzo(indirizzoDomicilio);
		}
		lavoratore.setResidenza(residenza);
		if (sesso != null) {
			lavoratore.setSesso(Sesso.fromValue(sesso));
		}
		utenteLavoratore.setLavoratore(lavoratore);
		utenteLavoratore.setUtenteServizio("L");
		appuntamento.setUtenteLavoratore(utenteLavoratore);

		String inputXML = "";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Appuntamento.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			Schema schema = Utils.getXsdSchema("appuntamento" + File.separator + "InputAppuntamento.xsd");
			jaxbMarshaller.setSchema(schema);

			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(appuntamento, writer);
			inputXML = writer.getBuffer().toString();
		} catch (Exception e) {
			throw e;
		}

		return inputXML;
	}

	private Risposta xmlToOutput(String outputXml) {
		Risposta risposta = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Risposta.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<Risposta> root = unmarshaller.unmarshal(new StreamSource(new StringReader(outputXml)),
					Risposta.class);
			risposta = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return risposta;
	}

	public class RispostaFissaAppuntamwento {
		private it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta risposta;
		private Integer idAgAppuntamento;
		private Integer idAgAppAnagrafica;

		public RispostaFissaAppuntamwento(Risposta risposta, Integer idAgAppuntamento, Integer idAgAppAnagrafica) {
			super();
			this.risposta = risposta;
			this.idAgAppuntamento = idAgAppuntamento;
			this.idAgAppAnagrafica = idAgAppAnagrafica;
		}

		public it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta getRisposta() {
			return risposta;
		}

		public Integer getIdAgAppuntamento() {
			return idAgAppuntamento;
		}

		public Integer getIdAgAppAnagrafica() {
			return idAgAppAnagrafica;
		}
	}
}
