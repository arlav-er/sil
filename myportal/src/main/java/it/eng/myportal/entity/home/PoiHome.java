package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;

// Generated 2-nov-2011 14.43.14 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.PoiDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.Poi;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComunePoi;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeTipoPoi;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPoiHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeComunePoiHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.geocoder.Coordinate;
import it.eng.myportal.utils.geocoder.GeocoderFactory;
import it.eng.myportal.utils.geocoder.IGeocoder;
import it.eng.myportal.utils.geocoder.Indirizzo;

/**
 * Home object for domain model class Poi.
 * 
 * @see it.eng.myportal.entity.Poi
 * @author Hibernate Tools
 */
@Stateless
public class PoiHome extends AbstractUpdatableHome<Poi, PoiDTO> {

	@EJB
	DeTipoPoiHome deTipoPoiHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeComunePoiHome deComunePoiHome;

	@EJB
	DeComuneHome deComuneHome;

	@Override
	public Poi findById(Integer id) {
		return findById(Poi.class, id);
	}

	@Override
	public Poi fromDTO(PoiDTO dto) {
		if (dto == null) {
			return null;
		}

		Poi entity = super.fromDTO(dto);

		entity.setIdPoi(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setLat(dto.getLat());
		entity.setLon(dto.getLon());
		entity.setDeTipoPoi(deTipoPoiHome.findById(dto.getCodTipoPoi()));

		return entity;
	}

	@Override
	public PoiDTO toDTO(Poi entity) {
		if (entity == null) {
			return null;
		}

		PoiDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdPoi());
		dto.setDescrizione(entity.getDescrizione());
		dto.setLat(entity.getLat());
		dto.setLon(entity.getLon());
		dto.setCodTipoPoi(entity.getDeTipoPoi().getCodTipoPoi());

		return dto;
	}

	/**
	 * Trova il POI e lo inserisce/aggiorna su DB (tabella "poi")
	 * 
	 * transazione autonoma in quanto invocato nella persist/merge, non deve interferire
	 * 
	 * @since 1.28.3 geoloc 'di ufficio', basata su de_comune_poi
	 * 
	 * @param data
	 *            Offerta di lavoro da geoloc
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Poi createOrUpdatePoi(VaDatiVacancy data) {
		DeComune deComune = data.getDeComune();
		String cap = deComune.getCap();
		DeProvincia deProvincia = deComune.getDeProvincia();
		String targa = deProvincia.getTarga();
		Poi oldPoi = data.getPoi();
		Poi newPoi = null;

		// Ottengo l'indirizzo della sede di lavoro specificato nella vacancy
		Indirizzo indirizzo = new Indirizzo(data.getIndirizzoLavoro(), deComuneHome.toDTO(deComune), cap, targa);

		// Ottengo la lista di POI corrispondente all'indirizzo passato
		List<Poi> poiList;

		poiList = getPoiList(indirizzo, ConstantsSingleton.TipoPoi.VACANCY,
				data.getPfPrincipalIns().getIdPfPrincipal());

		if (poiList.size() < 1) {
			log.warn("Il geocoder ha fallito, uso fallback per vacancy a " + deComune.getDenominazione());
			try {
				poiList.add(getComunePoi(deComune, ConstantsSingleton.TipoPoi.VACANCY,
						data.getPfPrincipalIns().getIdPfPrincipal()));
			} catch (Exception e) {
				log.error("Ha fallito anche il fall-back sul comune: " + e.getMessage());
			}
		}

		// Se ottengo almeno un POI setto il primo della lista come POI della
		// vacancy
		if (poiList.size() > 0) {
			newPoi = poiList.get(0);
			if (oldPoi != null) {
				newPoi.setIdPoi(oldPoi.getIdPoi());
				merge(newPoi);
			} else {
				persist(newPoi);
			}
		}

		return newPoi;
	}

	/**
	 * Metodo di fallback per POI da comune
	 * 
	 * @param codComune
	 * @param tipoPoiId
	 * @return
	 * @throws MyPortalNoResultFoundException
	 *             se non trova comunePoi
	 */
	private Poi getComunePoi(DeComune targetCom, String tipoPoiId, Integer pfPrincipalId)
			throws MyPortalNoResultFoundException {
		Poi poi = new Poi();

		DeTipoPoi tipoPoi = deTipoPoiHome.findById(tipoPoiId);

		if (tipoPoi != null) {

			DeComunePoi fallback = deComunePoiHome.findByDeComune(targetCom);
			Coordinate coordinate = new Coordinate(fallback.getLat().doubleValue(), fallback.getLon().doubleValue());

			Date now = new Date();

			poi.setDeTipoPoi(tipoPoi);
			poi.setDescrizione("Posizione approssimativa presso " + targetCom.getDenominazione());
			poi.setLat(coordinate.getLatitudine());
			poi.setLon(coordinate.getLongitudine());
			poi.setDtmIns(now);
			poi.setDtmMod(now);
			poi.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			poi.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
		}

		return poi;
	}

	/**
	 * Restituisce la lista di POI corrispondente all'indirizzo passato come input.
	 * 
	 * @param indirizzo
	 *            Indirizzo da georeferenziare
	 * @param tipoPoiId
	 *            tipo di POI
	 * @param pfPrincipalId
	 *            id dell'utente che esegue l'operazione
	 * @return la lista dei POI corrispondente all'indirizzo passato come input
	 */
	public List<Poi> getPoiList(Indirizzo indirizzo, String tipoPoiId, Integer pfPrincipalId) {
		List<Poi> poiList = new ArrayList<Poi>();
		IGeocoder googleGeocoder = GeocoderFactory.getGeocoder();

		DeTipoPoi tipoPoi = deTipoPoiHome.findById(tipoPoiId);

		if (tipoPoi != null) {
			List<Coordinate> coordinates = new ArrayList<Coordinate>();

			try {
				coordinates = googleGeocoder.getCoordinates(indirizzo);
			} catch (MyPortalException mpe) {
				log.warn("Impossibile geolocalizzare '" + indirizzo + "'");

			}

			for (Coordinate coordinate : coordinates) {
				Poi poi = new Poi();
				Date now = new Date();

				poi.setDeTipoPoi(tipoPoi);
				// String pDescrizione = pDescrizione;
				String pDescrizione = indirizzo.getIndirizzoFormattato();
				// descrizione ha lung max 200, se l'indirizzo completo è troppo
				// lungo bisogna preservare le info più utili
				if (StringUtils.length(pDescrizione) > 200) {
					// 50 x l'indirizzo,
					// 150 x IndirizzoShort comune, cap... (e lo spazio )
					String indShort = indirizzo.getIndirizzoShort();
					String indRaw = indirizzo.getIndirizzo();
					pDescrizione = StringUtils.abbreviate(indRaw, 50) + " " + StringUtils.abbreviate(indShort, 149);
				}
				poi.setDescrizione(pDescrizione);
				poi.setLat(coordinate.getLatitudine());
				poi.setLon(coordinate.getLongitudine());
				poi.setDtmIns(now);
				poi.setDtmMod(now);
				poi.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
				poi.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

				poiList.add(poi);
			}
		}

		return poiList;
	}

	public Poi copyById(Integer pfPrincipalId, Poi poi, CvDatiPersonali cvDatiPersonali) {
		if (poi != null) {
			Date now = new Date();
			Poi copyPoi = new Poi();

			copyPoi.setDeTipoPoi(poi.getDeTipoPoi());
			copyPoi.setDescrizione(poi.getDescrizione());
			copyPoi.setLat(poi.getLat());
			copyPoi.setLon(poi.getLon());
			copyPoi.setDtmIns(now);
			copyPoi.setDtmMod(now);
			copyPoi.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			copyPoi.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(copyPoi);

			return copyPoi;
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a cv_informatica con id "
					+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	public Poi findByIdVaDatiVacancy(Integer idVaDatiVacancy) throws MyPortalNoResultFoundException {
		try {
			Poi poi = entityManager.createNamedQuery("findByVacancy", Poi.class)
					.setParameter("idVaDatiVacancy", idVaDatiVacancy).getSingleResult();
			return poi;
		} catch (NoResultException e) {
			throw new MyPortalNoResultFoundException();
		}
	}
}
