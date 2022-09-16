package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.FbConvenzioneSedeDTO;
import it.eng.myportal.entity.FbConvenzioneSede;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.geocoder.Coordinate;
import it.eng.myportal.utils.geocoder.GeocoderFactory;
import it.eng.myportal.utils.geocoder.IGeocoder;
import it.eng.myportal.utils.geocoder.Indirizzo;
import it.eng.sil.base.business.AccreditamentoRemoteClient;
import it.eng.sil.base.exceptions.AccreditamentoRemoteException;
import it.eng.sil.base.pojo.accreditamento.SedeAccreditataPOJO;

@Stateless
public class FbConvenzioneSedeHome extends AbstractUpdatableHome<FbConvenzioneSede, FbConvenzioneSedeDTO> {

	@EJB
	private FbConvenzioneHome convenzioneHome;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	public FbConvenzioneSede fromDTO(FbConvenzioneSedeDTO dto) {
		if (dto == null)
			return null;

		FbConvenzioneSede entity = super.fromDTO(dto);
		entity.setIdConvenzioneSedi(dto.getId());
		entity.setNome(dto.getNome());
		entity.setIndirizzo(dto.getIndirizzo());
		entity.setCap(dto.getCap());
		entity.setFlgAccreditata(dto.getFlgAccreditata());
		entity.setLatitudine(dto.getLatitudine());
		entity.setLongitudine(dto.getLongitudine());
		entity.setMotivoEliminazione(dto.getMotivoEliminazione());
		entity.setIdSedeAccreditataMycas(dto.getIdSedeAccreditataMycas());

		if (dto.getConvenzione() != null)
			entity.setConvenzione(convenzioneHome.findById(dto.getConvenzione().getId()));

		if (dto.getComune() != null)
			entity.setDeComune(deComuneHome.findById(dto.getComune().getId()));

		return entity;
	}

	public FbConvenzioneSedeDTO toDTO(FbConvenzioneSede entity) {
		if (entity == null)
			return null;
		FbConvenzioneSedeDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdConvenzioneSedi());
		dto.setNome(entity.getNome());
		dto.setCap(entity.getCap());
		dto.setIndirizzo(entity.getIndirizzo());
		dto.setComune(deComuneHome.toDTO(entity.getDeComune()));
		dto.setConvenzione(convenzioneHome.toDTO(entity.getConvenzione()));
		dto.setFlgAccreditata(entity.getFlgAccreditata());
		dto.setLatitudine(entity.getLatitudine());
		dto.setLongitudine(entity.getLongitudine());
		dto.setMotivoEliminazione(entity.getMotivoEliminazione());
		dto.setIdSedeAccreditataMycas(entity.getIdSedeAccreditataMycas());

		return dto;
	}

	public List<FbConvenzioneSedeDTO> findByConvenzioneId(Integer convenzioneId) {
		TypedQuery<FbConvenzioneSede> query = entityManager.createNamedQuery("findConvenzioneSedeByIdConvenzione",
				FbConvenzioneSede.class);
		query.setParameter("convenzioneId", convenzioneId);
		List<FbConvenzioneSede> results = query.getResultList();
		List<FbConvenzioneSedeDTO> element = new ArrayList<FbConvenzioneSedeDTO>();
		if (results == null)
			return null;
		for (FbConvenzioneSede next : results) {
			element.add(toDTO(next));
		}
		return element;
	}

	/**
	 * Usa il GoogleGeocoder per recuperare latitudine e longitudine di una sede, e il salva sul DB.
	 */
	public FbConvenzioneSedeDTO geolocalizzaSede(Integer idSede, Integer idPrincipalMod) {
		IGeocoder geocoder = GeocoderFactory.getGeocoder();
		FbConvenzioneSedeDTO sede = findDTOById(idSede);
		DeProvinciaDTO provinciaSede = deProvinciaHome.findDTOById(sede.getComune().getIdProvincia());
		Indirizzo indirizzoSede = new Indirizzo(sede.getIndirizzo(), sede.getComune(), sede.getCap(),
				provinciaSede.getTarga());
		List<Coordinate> coordinateSede = geocoder.getCoordinates(indirizzoSede);
		if (coordinateSede != null && !coordinateSede.isEmpty()) {
			sede.setLatitudine(coordinateSede.get(0).getLatitudine());
			sede.setLongitudine(coordinateSede.get(0).getLongitudine());
		}

		return mergeDTO(sede, idPrincipalMod);
	}

	/**
	 * Sincronizzo una sede con l'elenco di sedi accreditate contenuto su MyCas (ci pensa lui a vedere se ce l'ha già o
	 * la deve aggiungere
	 */
	public FbConvenzioneSedeDTO accreditaSede(FbConvenzioneSedeDTO sede, Integer idEnteAccreditato,
			String denominazione, Integer idPrincipalIns) {
		if (sede.getFlgAccreditata()) {
			try {
				//FIXME !! questa roba qui era per la calabria, e si è spaccata DOPO la gestione bandi
				//per la ER. Non so cosa metterci, forse GG? per ora XXX per far compilare
				AccreditamentoRemoteClient wsClient = new AccreditamentoRemoteClient();
				SedeAccreditataPOJO sedeAccreditata = wsClient.putSedeAccreditata(ConstantsSingleton
						.putSedeAccreditataUrl(), idEnteAccreditato, denominazione, sede.getComune().getId(), sede
						.getCap(), sede.getIndirizzo(), sede.getLatitudine(), sede.getLongitudine(), "XXX");
				sede.setIdSedeAccreditataMycas(sedeAccreditata.getIdSedeAccreditata());
				return persistDTO(sede, idPrincipalIns);
			} catch (AccreditamentoRemoteException e) {
				log.error("Errore durante la sincronizzaSede: " + e.toString());
				return sede;
			}
		} else {
			return sede;
		}
	}

	/**
	 * Dico a MyAuth di disabilitare una sede accreditata, SOLO se sul portale non ho altre sedi attive che la stanno
	 * utilizzando.
	 */
	public FbConvenzioneSedeDTO disabilitaSede(FbConvenzioneSedeDTO sede, Integer idPrincipalMod) {
		if (sede.getIdSedeAccreditataMycas() != null) {
			TypedQuery<Long> query = entityManager.createNamedQuery("countAltreSediAccreditate", Long.class);
			query.setParameter("idSedeAccreditata", sede.getIdSedeAccreditataMycas());
			query.setParameter("idSedePortale", sede.getId());
			int numAltreSedi = query.getSingleResult().intValue();
			if (numAltreSedi < 1) {
				try {
					// Questa era l'unica FbSede attiva legata a quella sede accreditata, posso disabilitarla.
					AccreditamentoRemoteClient wsClient = new AccreditamentoRemoteClient();
					wsClient.disattivaSedeAccreditata(ConstantsSingleton.disattivaSedeAccreditataUrl(),
							sede.getIdSedeAccreditataMycas(), idPrincipalMod);
				} catch (AccreditamentoRemoteException e) {
					log.error("Errore durante la sincronizzaSede: " + e.toString());
				}
			}

			// In ogni caso, cancello il riferimento dalla sede.
			sede.setIdSedeAccreditataMycas(null);
			return mergeDTO(sede, idPrincipalMod);
		}

		// Se è andato storto qualcosa, restituisco la sede così com'è.
		return sede;
	}

	@Override
	public FbConvenzioneSede findById(Integer id) {
		return findById(FbConvenzioneSede.class, id);
	}

}
