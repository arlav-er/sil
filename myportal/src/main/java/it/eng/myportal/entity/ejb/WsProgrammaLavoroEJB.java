package it.eng.myportal.entity.ejb;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VerificaRequisitiGaranziaOverResponseDTO;
import it.eng.myportal.entity.BdAdesione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.WsEndpoint;
import it.eng.myportal.entity.decodifiche.DeBandoProgramma;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.home.BdAdesioneHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeBandoProgrammaHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.VerificaRequisitiNotFoundException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverProxy;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverRequest;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverResponse;

@Stateless
public class WsProgrammaLavoroEJB {

	@EJB
	private WsEndpointHome wsEndpointHome;
	@EJB
	private DeProvinciaHome deProvinciaHome;
	@EJB
	private BdAdesioneHome bdAdesioneHome;
	@EJB
	private DeCpiHome deCpiHome;
	@EJB
	private DeProvenienzaHome deProvenienzaHome;
	@EJB
	private PfPrincipalHome pfPrincipalHome;
	@EJB
	private DeBandoProgrammaHome deBandoProgrammaHome;

	private static final String OVER30 = "OVER30";
	private static final String FLAG_PERCETTORE_AMMORTIZZATORI_FALSE = "N";
	private static Log log = LogFactory.getLog(WsProgrammaLavoroEJB.class.getName());

	public VerificaRequisitiGaranziaOverResponseDTO callWsServiceAndPersistIfOk(UtenteCompletoDTO utenteCompletoDTO, 
																				String dichiarazione,
																				String codProvinciaRiferimento,
																				String codCpi,
																				Date dtAdesioneGG,
																				String codStatoAdesioneMin) throws VerificaRequisitiNotFoundException{
		//Call service
		VerificaRequisitiGaranziaOverResponseDTO resultDTO = callServiceAdesioneProgrammaLavoro(utenteCompletoDTO, 
				dichiarazione, codProvinciaRiferimento);
		
		if(resultDTO.getCodice() == 0){
			//Persist
			BdAdesione bdAdesione = fillEntityToPersist(utenteCompletoDTO,
					codCpi,
					ConstantsSingleton.COD_PROVINCIA_PERUGIA.equals(resultDTO.getResultFromWhichEndpoint().getDeProvincia().getCodProvincia())? 
							ConstantsSingleton.DeProvenienza.COD_SILPG:ConstantsSingleton.DeProvenienza.COD_SILTR,
					ConstantsSingleton.DeBandoProgramma.COD_UMBAT, dtAdesioneGG, codStatoAdesioneMin,
					dichiarazione);
			bdAdesioneHome.persist(bdAdesione);
		}
		return resultDTO;
	}
	
	//Restituisce una mappa con un solo oggetto contenente l'esito
	private VerificaRequisitiGaranziaOverResponseDTO callServiceAdesioneProgrammaLavoro(UtenteCompletoDTO utenteCompletoDTO, 
																						String dichiarazione,
																						String codProvinciaRiferimento) throws VerificaRequisitiNotFoundException {
		VerificaRequisitiGaranziaOverResponseDTO dto = null;
		try {
			VerificaRequisitiGaranziaOverResponse firstResponse = 
					callServiceAdesioneProgrammaByProvincia(utenteCompletoDTO, dichiarazione, codProvinciaRiferimento);
			WsEndpoint provRiferimentoEndpoint = wsEndpointHome
					.findByTipoServizioAndProvincia(TipoServizio.CHECK_GARANZIA_OVER, codProvinciaRiferimento);
			dto = fillResponseWsDTO(firstResponse, provRiferimentoEndpoint);
			if (firstResponse.getCodice() == 0){
				return dto;
			}
		} catch (Exception e) {
			log.error("Errore durante la chiamata a CheckGaranziaOver per la provincia " + codProvinciaRiferimento
					+ ": " + e);
		}
		if(dto==null){
			throw new VerificaRequisitiNotFoundException("Errore durante l'invocazione del servizio di verifica requisiti");
		}
		//Caso in cui restituisco un esito != 0
		log.info("Il Servizio Umbriattiva ha restituito un esito con codice: " + dto.getCodice());
		return dto;
	}
	
	private VerificaRequisitiGaranziaOverResponse callServiceAdesioneProgrammaByProvincia (UtenteCompletoDTO utenteCompletoDTO, 
			String dichiarazione, String codProvinciaRiferimento) throws RemoteException {
		VerificaRequisitiGaranziaOverRequest request = createRequest(utenteCompletoDTO, dichiarazione);
		WsEndpoint provRiferimentoEndpoint = wsEndpointHome
				.findByTipoServizioAndProvincia(TipoServizio.CHECK_GARANZIA_OVER, codProvinciaRiferimento);
		return callWebservice(request, provRiferimentoEndpoint);
	}

	private VerificaRequisitiGaranziaOverRequest createRequest(UtenteCompletoDTO utenteCompletoDTO, String dichiarazione) {
		VerificaRequisitiGaranziaOverRequest request = new VerificaRequisitiGaranziaOverRequest();
		// Creo la richiesta.
		GregorianCalendar dataAdesioneCal = new GregorianCalendar();
		dataAdesioneCal.setTime(new Date());
		request.setCodiceFiscale(utenteCompletoDTO.getCodiceFiscale());
		request.setDataAdesione(dataAdesioneCal);
		request.setDichiarazione(dichiarazione);
		// Viene passato "N" per default
		request.setPercettoreAmmortizzatori(FLAG_PERCETTORE_AMMORTIZZATORI_FALSE);
		return request;
	}

	private VerificaRequisitiGaranziaOverResponse callWebservice(VerificaRequisitiGaranziaOverRequest request,
			WsEndpoint endpoint) throws RemoteException {
		VerificaRequisitiGaranziaOverProxy service = new VerificaRequisitiGaranziaOverProxy();
		service.setEndpoint(endpoint.getAddress());

		log.info("Inizio chiamata verso endpoint: ");
		return service.getVerificaRequisitiGaranziaOver(request);
	}

	private BdAdesione fillEntityToPersist(UtenteCompletoDTO utenteCompletoDTO, 
											String codCpi, 
											String codProvenienza,
											String codBandoProgramma,
											Date dtAdesione,
											String codStatoAdesioneMin,
											String dichiarazione) {
		BdAdesione bdAdesione = new BdAdesione();
		bdAdesione.setCodiceFiscale(utenteCompletoDTO.getCodiceFiscale());
		bdAdesione.setCognome(utenteCompletoDTO.getCognome());
		bdAdesione.setNome(utenteCompletoDTO.getNome());
		bdAdesione.setDtNascita(utenteCompletoDTO.getDataNascita());
		bdAdesione.setDtAdesione(new Date());

		bdAdesione.setDichiarazione(dichiarazione);
		if (!OVER30.equals(dichiarazione)) {
			// Gestione GG
			bdAdesione.setDtAdesioneGG(dtAdesione);
			bdAdesione.setCodStatoAdesioneMin(codStatoAdesioneMin);
		}

		DeCpi deCpi = deCpiHome.findById(codCpi);
		bdAdesione.setDeCpi(deCpi);

		DeProvenienza deProvenienza = deProvenienzaHome.findById(codProvenienza);
		bdAdesione.setDeProvenienza(deProvenienza);

		PfPrincipal pfPrincipal = pfPrincipalHome
				.findById(utenteCompletoDTO.getUtenteDTO().getPfPrincipalDTO().getId());
		bdAdesione.setPfPrincipal(pfPrincipal);
		bdAdesione.setPfPrincipalIns(pfPrincipal);
		bdAdesione.setPfPrincipalMod(pfPrincipal);
		bdAdesione.setDtmIns(new Date());
		bdAdesione.setDtmMod(new Date());
		DeBandoProgramma deBandoProgramma = deBandoProgrammaHome.findById(codBandoProgramma);
		bdAdesione.setDeBandoProgramma(deBandoProgramma);
		return bdAdesione;
	}
	
	private VerificaRequisitiGaranziaOverResponseDTO fillResponseWsDTO(VerificaRequisitiGaranziaOverResponse response,
			WsEndpoint endpointCalled){
		VerificaRequisitiGaranziaOverResponseDTO dto = new VerificaRequisitiGaranziaOverResponseDTO();
		dto.toDTO(response);
		dto.setResultFromWhichEndpoint(endpointCalled);
		return dto;
	}
}
