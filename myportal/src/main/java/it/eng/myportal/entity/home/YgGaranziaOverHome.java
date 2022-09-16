package it.eng.myportal.entity.home;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.xml.datatype.DatatypeConfigurationException;

import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.YgGaranziaOver;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta.DatiAppuntamento;
import it.eng.myportal.utils.Utils;

@Stateless
public class YgGaranziaOverHome extends AbstractUpdatableHome<YgGaranziaOver, YgGaranziaOverDTO> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@Override
	public YgGaranziaOver findById(Integer id) {
		return findById(YgGaranziaOver.class, id);
	}

	public YgGaranziaOver fromDTO(YgGaranziaOverDTO dto) {
		YgGaranziaOver newEntity = super.fromDTO(dto);
		newEntity.setIdYgGaranziaOver(dto.getId());
		newEntity.setCodiceFiscale(dto.getCodiceFiscale());
		newEntity.setDtAdesione(dto.getDtAdesione());
		newEntity.setDtPresaInCarico(dto.getDtPresaInCarico());
		newEntity.setFlgPercettoreAmmortizzatori(dto.getFlgPercettoreAmmortizzatori());
		newEntity.setStrMessWsAdesione(dto.getStrMessWsAdesione());
		newEntity.setStrMessInterfaccia(dto.getStrMessInterfaccia());
		newEntity.setFlgPresoInCarico(dto.getFlgPresoInCarico());
		newEntity.setPfPrincipal(pfPrincipalHome.fromDTO(dto.getPfPrincipal()));
		newEntity.setDeCpiAdesione(deCpiHome.fromDTO(dto.getDeCpiAdesione()));
		newEntity.setDeProvincia(deProvinciaHome.fromDTO(dto.getDeProvincia()));

		return newEntity;
	}

	public YgGaranziaOverDTO toDTO(YgGaranziaOver entity) {
		YgGaranziaOverDTO newDto = super.toDTO(entity);
		newDto.setId(entity.getIdYgGaranziaOver());
		newDto.setCodiceFiscale(entity.getCodiceFiscale());
		newDto.setDtAdesione(entity.getDtAdesione());
		newDto.setDtPresaInCarico(entity.getDtPresaInCarico());
		newDto.setFlgPercettoreAmmortizzatori(entity.getFlgPercettoreAmmortizzatori());
		newDto.setStrMessWsAdesione(entity.getStrMessWsAdesione());
		newDto.setStrMessInterfaccia(entity.getStrMessInterfaccia());
		newDto.setFlgPresoInCarico(entity.getFlgPresoInCarico());
		newDto.setPfPrincipal(pfPrincipalHome.toDTO(entity.getPfPrincipal()));
		newDto.setDeCpiAdesione(deCpiHome.toDTO(entity.getDeCpiAdesione()));
		newDto.setDeProvincia(deProvinciaHome.toDTO(entity.getDeProvincia()));

		return newDto;
	}

	/**
	 * Cerca un'adesione a Umbriattiva Adulti per un certo utente. Restituisce null se l'utente non ha ancora aderito.
	 */
	public YgGaranziaOverDTO findByPfPrincipal(Integer idPfPrincipal) {
		List<YgGaranziaOver> adesioni = entityManager
				.createNamedQuery("findYgAdesioneOverByIdPfPrincipal", YgGaranziaOver.class)
				.setParameter("id_pf_principal", idPfPrincipal).getResultList();
		if (adesioni != null && adesioni.size() == 1) {
			// La get(0) torna la più recente, order by nella query
			return toDTO(adesioni.get(0));
		} else {
			log.debug("Adesione a Umbriattiva Adulti non trovata per idPfPrincipal: " + idPfPrincipal);
			return null;
		}
	}

	/**
	 * Restituisce TRUE se è presente un'adesione valida con il codice fiscale dato in input.
	 */
	public boolean isAdesioneGiaPresente(String codiceFiscale) {
		TypedQuery<Long> countByCfQuery = entityManager.createNamedQuery("findYgAdesioneOverCountByCf", Long.class);
		countByCfQuery.setParameter("codice_fiscale", codiceFiscale.toUpperCase());
		Long cfCheckResult = countByCfQuery.getSingleResult();
		return (cfCheckResult > 0);
	}

	/**
	 * Crea una nuova richiesta di adesione a Umbriattiva Adulti. All'inizio la richiesta NON è presa in carico.
	 */
	public YgGaranziaOverDTO createNewAdesione(UtenteCompletoDTO utente, Boolean flagPercettoreAmmortizzatori,
			String cpiAdesione, Integer idPrincipalIns) {
		// Controllo che non esista già una richiesta di adesione con questo codice fiscale.
		if (isAdesioneGiaPresente(utente.getCodiceFiscale())) {
			throw new MyPortalException("Esiste già una richiesta di adesione con questo codice fiscale");
		}

		// Creo la richiesta di adesione.
		YgGaranziaOverDTO newAdesione = new YgGaranziaOverDTO();
		newAdesione.setPfPrincipal(pfPrincipalHome.findDTOById(utente.getIdUtente()));
		newAdesione.setCodiceFiscale(utente.getUtenteInfo().getCodiceFiscale());
		newAdesione.setFlgPercettoreAmmortizzatori(flagPercettoreAmmortizzatori ? "Y" : "N");
		newAdesione.setFlgPresoInCarico(false);
		if (cpiAdesione != null)
			newAdesione.setDeCpiAdesione(deCpiHome.findDTOById(cpiAdesione));

		return persistDTO(newAdesione, idPrincipalIns);
	}

	/**
	 * Aggiorna l'adesione dopo la prenotazione di un appuntamento con il CPI.
	 */
	public YgGaranziaOver aggiornaAdesionePostAppuntamento(Integer idYgAdesione, DatiAppuntamento datiAppuntamento,
			UtenteCompletoDTO utenteCompletoDTO) throws DatatypeConfigurationException, ParseException {
		YgGaranziaOver adesione = findById(idYgAdesione);
		adesione.setFlgPresoInCarico(true);
		adesione.setDtPresaInCarico(Utils.gregorianDateToDate(datiAppuntamento.getDataAppuntamento()));
		adesione.setDeCpiAdesione(deCpiHome.findById(datiAppuntamento.getIdCPI()));
		return merge(adesione);
	}

	/**
	 * Restituisce tutte le adesioni a Umbriattiva Adulti andate a buon fine (quindi con dt_adesione non nulla) per un
	 * certo codice fiscale.
	 */
	public List<YgGaranziaOverDTO> findDTOValideByCodFiscale(String codiceFiscale) {
		List<YgGaranziaOver> adesioni = entityManager
				.createNamedQuery("findYgAdesioneOverValideByCf", YgGaranziaOver.class)
				.setParameter("codice_fiscale", codiceFiscale.toUpperCase()).getResultList();
		List<YgGaranziaOverDTO> adesioniDTO = new ArrayList<YgGaranziaOverDTO>();
		for (YgGaranziaOver adesione : adesioni) {
			adesioniDTO.add(toDTO(adesione));
		}
		return adesioniDTO;
	}
	
	
	public List<YgGaranziaOver> findValideByIdPfPrincipal(Integer idPfPrincipal) {
		List<YgGaranziaOver> ygGaranziaOvers = entityManager
				.createNamedQuery("findYgAdesioneOverValideByIdPfPrincipal", YgGaranziaOver.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();
		return ygGaranziaOvers;
	}
	public List<YgGaranziaOver> findValideByIdPfPrincipalAndCodFiscale(Integer idPfPrincipal, String codiceFiscale) {
		List<YgGaranziaOver> ygGaranziaOvers = entityManager
				.createNamedQuery("findYgAdesioneOverValideByIdPfPrincipalAndCodFiscale", YgGaranziaOver.class)
				.setParameter("idPfPrincipal", idPfPrincipal)
				.setParameter("codiceFiscale", codiceFiscale.toUpperCase())
				.getResultList();
		return ygGaranziaOvers;
	}
	
	public List<YgGaranziaOverDTO> findDTOValideByIdPfPrincipal(Integer idPfPrincipal) {
		List<YgGaranziaOver> ygGaranziaOvers = findValideByIdPfPrincipal(idPfPrincipal);
		List<YgGaranziaOverDTO> ygGaranziaOversDTOs = new ArrayList<YgGaranziaOverDTO>();
		for (YgGaranziaOver adesione : ygGaranziaOvers) {
			ygGaranziaOversDTOs.add(toDTO(adesione));
		}
		return ygGaranziaOversDTOs;
	}
	public List<YgGaranziaOverDTO> findDTOValideByIdPfPrincipalAndCodFiscale(Integer idPfPrincipal, String codiceFiscale) {
		List<YgGaranziaOver> ygGaranziaOvers = findValideByIdPfPrincipalAndCodFiscale(idPfPrincipal, codiceFiscale);
		List<YgGaranziaOverDTO> ygGaranziaOversDTOs = new ArrayList<YgGaranziaOverDTO>();
		for (YgGaranziaOver adesione : ygGaranziaOvers) {
			ygGaranziaOversDTOs.add(toDTO(adesione));
		}
		return ygGaranziaOversDTOs;
	}
}
