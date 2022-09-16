package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.ClInvioComunicazioneDTO;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.MsgMessaggioCl;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoComunicazioneClHome;
import it.eng.myportal.utils.ConstantsSingleton.DeStatoInvioCl;
import it.gov.lavoro.servizi.cliclavoro.CliclavoroWSProxy;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioMessaggio_Type;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_TypeTipo_Risposta;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

/**
 * @author Rodi
 */
@Stateless
public class ClInvioComunicazioneHome extends AbstractUpdatableHome<ClInvioComunicazione, ClInvioComunicazioneDTO> {

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	DeTipoComunicazioneClHome deTipoComunicazioneClHome;
	
	@EJB
	MsgMessaggioHome msgMessaggioHome;
	
	@EJB
	WsEndpointHome wsEndpointHome;

	public ClInvioComunicazione findById(Integer id) {
		return findById(ClInvioComunicazione.class, id);
	}

	@Override
	public ClInvioComunicazioneDTO toDTO(ClInvioComunicazione entity) {
		if (entity == null)
			return null;
		ClInvioComunicazioneDTO dto = super.toDTO(entity);
		dto.setAzioneServizio(entity.getAzioneServizio());
		dto.setCodComunicazione(entity.getCodComunicazione());
		dto.setDescrizioneErrore(entity.getDescrizioneErrore());
		dto.setFileComunicazione(entity.getFileComunicazione());
		dto.setFlagInviato(entity.getFlagInviato());
		dto.setId(entity.getIdClInvioComunicazione());
		dto.setStato(deStatoInvioClHome.toDTO(entity.getDeStatoInvioCl()));
		dto.setMittente(entity.getMittente());
		dto.setDestinatario(entity.getDestinatario());
		
		return dto;
	}

	@Override
	public ClInvioComunicazione fromDTO(ClInvioComunicazioneDTO dto) {
		throw new UnsupportedOperationException("Operazione non supportata");
	}

	
	
	/**
	 * Effettua l'invio di una candidatura verso clicLavoro
	 * 
	 * @param cvCandidaturaCl
	 * @param comunicazione
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws RemoteException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inviaMessaggio(ClInvioComunicazione comunicazione)
			throws JAXBException, SAXException {
		MsgMessaggioCl msgMessaggioCl = msgMessaggioHome.findByCodComunicazione(comunicazione.getCodComunicazione());
		try {
			log.info("Invio la comunicazione " + comunicazione.getCodComunicazione());
			// costruisci la richiesta
			Richiesta_invioMessaggio_Type richiesta = new Richiesta_invioMessaggio_Type();
			richiesta.setMessaggioXML(comunicazione.getFileComunicazione());
			
			// effettua l'invio
			String inviaMessaggioAddress = wsEndpointHome.getClicLavoroInviaMessaggioAddress();
			CliclavoroWSProxy inviaMessaggio = new CliclavoroWSProxy(inviaMessaggioAddress);
			Risposta_invioMessaggio_Type response = inviaMessaggio.invioMessaggio(richiesta);
			log.info("Invio effettuato: "+response);
			String nuovoCodice;
			//String vecchioCodice = msgMessaggioCl.getDeStatoInvioCl().getCodStatoInvioCl();
			// se l'invio è andato a buon fine, imposto il nuovo stato
			// 'completato' alla comunicazione
			if (response.getTipo_Risposta().getValue().equalsIgnoreCase(Risposta_invioCandidatura_TypeTipo_Risposta._OK)) {
				log.info("Response: OK");				
				comunicazione.setDescrizioneErrore(response.getDescr_Esito());
				nuovoCodice = DeStatoInvioCl.PRIMO_INVIO_COMPLETATO;			
			}
			else {
				log.info("Response: KO");
				// salvo l'errore
				comunicazione.setDescrizioneErrore(response.getDescr_Esito());
				nuovoCodice = DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE;				
			}
			log.info("Il nuovo stato della comunicazione è " + nuovoCodice);
			// salva il nuovo stato
			it.eng.myportal.entity.decodifiche.DeStatoInvioCl nuovoStato = deStatoInvioClHome.findById(nuovoCodice);
			comunicazione.setDeStatoInvioCl(nuovoStato);
			msgMessaggioCl.setDeStatoInvioCl(nuovoStato);
		} catch (RemoteException e) {
			log.error("Errore durante l'invio dell'xml" + e);
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
		} catch (Exception e) {
			log.error("Errore Generico durante l'invio dell'xml" + e);
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(DeStatoInvioCl.INVIATA_ERRORE));
			msgMessaggioCl.setDeStatoInvioCl(deStatoInvioClHome.findById(DeStatoInvioCl.INVIATA_ERRORE));
		} finally {
			// segna che l'invio è stato effettuato e aggiorna i record
			// candidatura e comunicazione
			comunicazione.setFlagInviato(true);
			log.info("aggiorno la comunicazione su DB");
			entityManager.merge(comunicazione);
			log.info("aggiorno la candidatura su DB");
			entityManager.merge(msgMessaggioCl);
		}
	}

	public ClInvioComunicazione findFromMyPortalByCodComunicazione(String codice) {
		List<ClInvioComunicazione> clInvioComunicazione = entityManager
				.createNamedQuery("findComunicazioneFromLavoroPerTeByCodComunicazione", ClInvioComunicazione.class)
				.setParameter("codComunicazione", codice).getResultList();
		if (clInvioComunicazione != null && !clInvioComunicazione.isEmpty()) {
			return clInvioComunicazione.get(0);
		} else {
			return null;
		}
		
	}
		
}
