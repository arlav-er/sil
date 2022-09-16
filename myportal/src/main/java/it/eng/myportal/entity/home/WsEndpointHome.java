package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.entity.WsEndpoint;
import it.eng.myportal.entity.WsEndpoint_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeRegione_;
import it.eng.myportal.entity.decodifiche.DeTipoServizio;
import it.eng.myportal.entity.decodifiche.DeTipoServizio_;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.MyPortalException;

/**
 * Classe Home per WsEndpoint
 * 
 * @see it.eng.myportal.entity.WsEndpoint
 * @author Rodi A.
 */
@Stateless
public class WsEndpointHome extends AbstractHibernateHome<WsEndpoint, Integer> {

	public WsEndpoint findById(Integer id) {
		return findById(WsEndpoint.class, id);
	}

	/**
	 * Restituisce l'indirizzo del WebService in base al tipo di servizio richiesto e la provincia di riferimento.
	 * 
	 * @param codTipoServizio
	 *            tipo del servizio che si vuole invocare
	 * @param codProvincia
	 *            codice della provincia di riferimento
	 * @return l'indirizzo del WS da impostare come endpoint del proxy
	 */
	public String getWebServiceAddress(TipoServizio tipoServizio, String codProvincia) throws MyPortalException {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> query = qb.createQuery(String.class);
		Root<WsEndpoint> wsEndpoint = query.from(WsEndpoint.class);
		query.select(wsEndpoint.get(WsEndpoint_.address));
		Join<WsEndpoint, DeProvincia> provinciaEndpoint = wsEndpoint.join(WsEndpoint_.deProvincia);
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);

		query.where(qb.equal(provinciaEndpoint.get(DeProvincia_.codProvincia), codProvincia),
				qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio), tipoServizio.getCodTipoServizio()));

		TypedQuery<String> q = entityManager.createQuery(query);

		List<String> address = q.getResultList();
		if (address.size() == 0) {
			log.warn("Nessun endpoint trovato per il servizio:" + tipoServizio);
			throw new MyPortalException("Nessun endpoint trovato per il servizio" + tipoServizio);
		} else if (address.size() == 1) {
			return address.get(0);
		} else {
			log.error("GRAVE: Troppi endpoint trovati per il servizio");
			throw new MyPortalException("GRAVE: Troppi endpoint trovati per il servizio" + tipoServizio);
		}
	}

	/**
	 * Restituisce tutti gli endpoint che erogano un certo servizio all'interno di una certa regione.
	 */
	public List<WsEndpoint> findByTipoServizioAndRegione(TipoServizio tipoServizio, String codRegione) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsEndpoint> criteria = qb.createQuery(WsEndpoint.class);
		Root<WsEndpoint> wsEndpoint = criteria.from(WsEndpoint.class);
		Join<WsEndpoint, DeProvincia> provincia = wsEndpoint.join(WsEndpoint_.deProvincia);
		Join<DeProvincia, DeRegione> regione = provincia.join(DeProvincia_.deRegione);
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);
		criteria.select(wsEndpoint);

		ArrayList<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio),
				tipoServizio.getCodTipoServizio()));
		whereConditions.add(qb.equal(regione.get(DeRegione_.codRegione), codRegione));
		criteria.where(whereConditions.toArray(new Predicate[] {}));

		TypedQuery<WsEndpoint> query = entityManager.createQuery(criteria);
		return query.getResultList();
	}

	/**
	 * Restituisce l'endpoint che eroga un certo servizio per una certa provincia.
	 */
	public WsEndpoint findByTipoServizioAndProvincia(TipoServizio tipoServizio, String codProvincia) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsEndpoint> criteria = qb.createQuery(WsEndpoint.class);
		Root<WsEndpoint> wsEndpoint = criteria.from(WsEndpoint.class);
		Join<WsEndpoint, DeProvincia> provincia = wsEndpoint.join(WsEndpoint_.deProvincia);
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);
		criteria.select(wsEndpoint);

		ArrayList<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio),
				tipoServizio.getCodTipoServizio()));
		whereConditions.add(qb.equal(provincia.get(DeProvincia_.codProvincia), codProvincia));
		criteria.where(whereConditions.toArray(new Predicate[] {}));

		TypedQuery<WsEndpoint> query = entityManager.createQuery(criteria);
		return query.getSingleResult();
	}

	public List<WsEndpoint> findByTipoServizioAndUsername(TipoServizio tipoServizio, String username) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsEndpoint> criteria = qb.createQuery(WsEndpoint.class);
		Root<WsEndpoint> wsEndpoint = criteria.from(WsEndpoint.class);
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);
		criteria.select(wsEndpoint);

		ArrayList<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio),
				tipoServizio.getCodTipoServizio()));
		whereConditions.add(qb.equal(qb.upper(wsEndpoint.get(WsEndpoint_.username)), username.toUpperCase()));
		criteria.where(whereConditions.toArray(new Predicate[] {}));

		TypedQuery<WsEndpoint> query = entityManager.createQuery(criteria);
		return query.getResultList();
	}

	/**
	 * Restituisce l'indirizzo del WebService in base al tipo di servizio richiesto
	 * 
	 * @param codTipoServizio
	 *            tipo del servizio che si vuole invocare
	 * @return l'indirizzo del WS da impostare come endpoint del proxy
	 */
	public String getWebServiceAddress(TipoServizio tipoServizio) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> query = qb.createQuery(String.class);
		Root<WsEndpoint> wsEndpoint = query.from(WsEndpoint.class);
		query.select(wsEndpoint.get(WsEndpoint_.address));
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);

		query.where(qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio), tipoServizio.getCodTipoServizio()));

		TypedQuery<String> q = entityManager.createQuery(query);

		String address = q.getSingleResult();
		return address;
	}

	public String getWebServiceAddressIdxReg(TipoServizio tipoServizio) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> query = qb.createQuery(String.class);
		Root<WsEndpoint> wsEndpoint = query.from(WsEndpoint.class);
		query.select(wsEndpoint.get(WsEndpoint_.address));
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);

		query.where(qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio), tipoServizio.getCodTipoServizio()));

		TypedQuery<String> q = entityManager.createQuery(query);

		String address = q.getResultList().get(0);
		return address;
	}

	/**
	 * Restituisce l'utente del WebService in base al tipo di servizio richiesto e la provincia di riferimento.
	 * 
	 * @param codTipoServizio
	 *            tipo del servizio che si vuole invocare
	 * @param codProvincia
	 *            codice della provincia di riferimento
	 * @return l'indirizzo del WS da impostare come endpoint del proxy
	 */
	public String[] getWebServiceUser(TipoServizio tipoServizio, String codProvincia) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsEndpoint> query = qb.createQuery(WsEndpoint.class);
		Root<WsEndpoint> wsEndpoint = query.from(WsEndpoint.class);
		Join<WsEndpoint, DeProvincia> provinciaEndpoint = wsEndpoint.join(WsEndpoint_.deProvincia);
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);

		query.where(qb.equal(provinciaEndpoint.get(DeProvincia_.codProvincia), codProvincia),
				qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio), tipoServizio.getCodTipoServizio()));

		TypedQuery<WsEndpoint> q = entityManager.createQuery(query);

		List<WsEndpoint> endpoints = q.getResultList();
		if (endpoints.size() > 0) {
			WsEndpoint endpoint = endpoints.get(0);
			String[] userWs = new String[2];
			userWs[0] = endpoint.getUsername();
			userWs[1] = endpoint.getPassWord();

			return userWs;
		} else {
			return null;
		}
	}

	/**
	 * Restituisce l'utente del WebService in base al tipo di servizio richiesto e la provincia di riferimento.
	 * 
	 * @param codTipoServizio
	 *            tipo del servizio che si vuole invocare
	 * @param codProvincia
	 *            codice della provincia di riferimento
	 * @return l'indirizzo del WS da impostare come endpoint del proxy
	 */
	public String[] getWebServiceUser(TipoServizio tipoServizio) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<WsEndpoint> query = qb.createQuery(WsEndpoint.class);
		Root<WsEndpoint> wsEndpoint = query.from(WsEndpoint.class);
		Join<WsEndpoint, DeProvincia> provinciaEndpoint = wsEndpoint.join(WsEndpoint_.deProvincia);
		Join<WsEndpoint, DeTipoServizio> deTipoServizio = wsEndpoint.join(WsEndpoint_.deTipoServizio);

		query.where(qb.equal(deTipoServizio.get(DeTipoServizio_.codTipoServizio), tipoServizio.getCodTipoServizio()));

		TypedQuery<WsEndpoint> q = entityManager.createQuery(query);

		List<WsEndpoint> endpoints = q.getResultList();
		if (endpoints.size() > 0) {
			WsEndpoint endpoint = endpoints.get(0);
			String[] userWs = new String[2];
			userWs[0] = endpoint.getUsername();
			userWs[1] = endpoint.getPassWord();

			return userWs;

		} else {
			return null;
		}
	}

	public String getStipulaDidAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.DID, codProvincia);
	}

	public String getStatoOccupazionaleAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.STATO_OCCPUAZIONALE, codProvincia);
	}

	public String getElencoMovimentiAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.ELENCO_MOVIMENTI, codProvincia);
	}

	public String getPercorsoLavoratoreAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.PERCORSO_LAVORATORE, codProvincia);
	}

	public String getSanaRedditiAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.SANA_REDDITI, codProvincia);
	}

	public String getServiziLavoratoreAddress() {
		return getWebServiceAddressIdxReg(TipoServizio.SERVIZI_LAVORATORE);
	}

	public String getSareAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.SARE, codProvincia);
	}

	public String getNuovoSareAddress() {
		return getWebServiceAddress(TipoServizio.NUOVO_SARE);
	}

	public String getClicLavoroInviaVacancyAddress() {
		return getWebServiceAddress(TipoServizio.CLICLAVORO_INVIAVACANCY);
	}

	public String getClicLavoroInviaCandidaturaAddress() {
		return getWebServiceAddress(TipoServizio.CLICLAVORO_INVIACANDIDATURA);
	}

	public String getClicLavoroInviaMessaggioAddress() {
		return getWebServiceAddress(TipoServizio.CLICLAVORO_INVIAMESSAGGIO);
	}

	public String getYouthGuaranteeAdesioneAddress() {
		return getWebServiceAddress(TipoServizio.YG_ADESIONE);
	}

	public String getYouthGuaranteeCheckUtenteAddress() {
		return getWebServiceAddress(TipoServizio.YG_CHECKUTENTE);
	}

	public String getYouthGuaranteeInvioSapAddress() {
		return getWebServiceAddress(TipoServizio.YG_INVIO_SAP);
	}

	public String getYouthGuaranteeCheckSapAddress() {
		return getWebServiceAddress(TipoServizio.YG_CHECKSAP);
	}

	public String getYouthGuaranteeRichiestaSapAddress() {
		return getWebServiceAddress(TipoServizio.YG_RICHIESTA_SAP);
	}

	public String getConfermaPeriodicaAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.CONFERMA_PERIODICA, codProvincia);
	}

	public String getAppuntamentoAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.APPUNTAMENTO, codProvincia);
	}

	public String getYGGetStatoAdesioneMin() {
		return getWebServiceAddress(TipoServizio.YG_GET_STATO_ADESIONE_MIN);
	}

	public String getYGSetStatoAdesioneMin() {
		return getWebServiceAddress(TipoServizio.YG_SET_STATO_ADESIONE_MIN);
	}

	@Deprecated//USA il constantSingleton
	public String getMyStageAddress() {
		return getWebServiceAddress(TipoServizio.MYSTAGE);
	}

	/*public String getMySapAddress() {
		return getWebServiceAddress(TipoServizio.MYSAP_ROOT);
	}*/

	public String getRinnovoPattoAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.RINNOVO_PATTO, codProvincia);
	}

	public String getConferimentoDidAddress() {
		return getWebServiceAddress(TipoServizio.CONFERIMENTO_DID);
	}
	
	public String getAdesioneReimpiegoAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.ADESIONE_REIMPIEGO, codProvincia);
	}

	public String getInvioPattoFirmatoAddress(String codProvincia) {
		return getWebServiceAddress(TipoServizio.SIL_PATTO_FIRMATO_CLI, codProvincia);
	}
}
