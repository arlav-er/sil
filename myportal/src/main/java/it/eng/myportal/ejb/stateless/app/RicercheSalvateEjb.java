package it.eng.myportal.ejb.stateless.app;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.RvAttivita;
import it.eng.myportal.entity.RvContratto;
import it.eng.myportal.entity.RvLingua;
import it.eng.myportal.entity.RvMansione;
import it.eng.myportal.entity.RvOrario;
import it.eng.myportal.entity.RvPatente;
import it.eng.myportal.entity.RvTestata;
import it.eng.myportal.entity.RvTestata_;
import it.eng.myportal.entity.RvTitolo;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.EmptyParameterException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.RicercheSalvateNotFoundException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.rest.app.exception.WrongParameterException;
import it.eng.myportal.rest.app.pojo.RicercheSalvatePojo;
import it.eng.myportal.rest.app.pojo.RicercheSalvatePojoList;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class RicercheSalvateEjb implements Serializable {

	private static final long serialVersionUID = 9027393961312354504L;
	protected static Log log = LogFactory.getLog(RicercheSalvateEjb.class);

	private final static String ID = "id";

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeMansioneHome deMansioneHome;
	
	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	RvTestataHome rvTestataHome;

	public RicercheSalvatePojoList getRicercheSalvate(String username, Integer start, Integer rows)
			throws AppEjbException {
		RicercheSalvatePojoList ret = new RicercheSalvatePojoList();

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {
				/*
				 * Recupero del numero totale di ricerche salvate
				 */
				ret.setNumRicercheTotali(getNumRicercheTotali(pfPrincipal));

				if (ret.getNumRicercheTotali() != null && ret.getNumRicercheTotali().compareTo(Long.valueOf(0)) > 0) {

					/*
					 * Recupero delle ricerche salvate, eventualmente paginate
					 */
					List<RvTestata> listEntity = getRicercheSalvate(pfPrincipal, start, rows);

					for (RvTestata rvTestata : listEntity) {
						/*
						 * Nel momento in cui vengono recuperate le ricerche salvate si aggiorna la data di modifica
						 * delle stesse. In questo modo si riescono a determinare le nuove vacancy da inviare come
						 * notifica all'app.
						 */

						/*
						 * om20190718: concordata rimozione dell'aggiornamento della data di modifica della ricerca
						 * salvata in fase di recupero della lista. Rimarrà solamente post invio notifiche offerte
						 * lavorative. Eventualmente in futuro si potrà pensare di aggiungerla nel recupero del
						 * dettaglio della ricerca salvata (stile portale), dopo aver verificato se l'app utilizza quel
						 * metodo quando si clicca su una specifica ricerca salvata.
						 */
						// rvTestataHome.mergeDtmMod(rvTestata.getIdRvTestata());
						/*
						 * La flush è necessaria per scaricare immediatamente l'update della data modifica (quindi
						 * eseguire anche la preUpdate di AbstractEntity) e non posticiparla nel momento della chiusura
						 * della transazione
						 */
						// entityManager.flush();
						// fine om20190718

						RicercheSalvatePojo rs = toPojo(rvTestata);
						ret.getListaRicerche().add(rs);
					}
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il recupero delle ricerche salvate");
		}
		return ret;
	}

	public RicercheSalvatePojo dettaglioRicercheSalvate(String username, Integer idRicercaSalvata)
			throws AppEjbException {
		RicercheSalvatePojo ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {
				/*
				 * Recupero della ricerca salvata
				 */
				RvTestata rvTestata = rvTestataHome.findById(idRicercaSalvata);

				if (ricercaSalvataPresente(rvTestata, pfPrincipal)) {
					// Per ora la lettura del dettaglio della ricerca salvata non aggiorna la data di modifica
					// (viene attualmente fatto da getRicercheSalvate e dall'invio della notifica con offerte lavorative
					// /*
					// * Nel momento in cui vengono recuperate le ricerche salvate si aggiorna la data di modifica delle
					// * stesse. In questo modo si riescono a determinare le nuove vacancy da inviare come notifica
					// * all'app.
					// */
					// rvTestataHome.mergeDtmMod(rvTestata.getIdRvTestata());
					// /*
					// * La flush è necessaria per scaricare immediatamente l'update della data modifica (quindi
					// eseguire
					// * anche la preUpdate di AbstractEntity) e non posticiparla nel momento della chiusura della
					// * transazione
					// */
					// entityManager.flush();

					ret = toPojo(rvTestata);
				} else {
					throw new RicercheSalvateNotFoundException(idRicercaSalvata);

				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il recupero del dettaglio delle ricerche salvate");
		}
		return ret;
	}

	public String addRicercheSalvate(String username, RicercheSalvatePojo ricercaSalvata) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {

				/*
				 * ------------------------------- Validazione ricerca e gruppi -----------------------
				 */
				validaRicerca(ricercaSalvata);

				/*
				 * ------------------------------- Map Entity e Persist -------------------------------
				 */
				RvTestata rvTestata = persistTestata(ricercaSalvata, pfPrincipal);

				// Gruppi
				persistRvContratto(ricercaSalvata.getCodContratto(), rvTestata);
				persistRvLingua(ricercaSalvata.getCodLingua(), rvTestata);
				// La mansione non è prevista nel caso di usaDecodificheSilPerVacancy a true (in RvMansione non è presente la fk verso DeBpMansione)
				if (!ConstantsSingleton.usaDecodificheSilPerVacancy()) persistRvMansione(ricercaSalvata.getCodMansione(), rvTestata);
				persistRvOrario(ricercaSalvata.getCodOrario(), rvTestata);
				persistRvPatente(ricercaSalvata.getCodPatente(), rvTestata);
				persistRvAttivita(ricercaSalvata.getCodSettore(), rvTestata);
				persistRvTitolo(ricercaSalvata.getCodTitoloStudio(), rvTestata);

				/*
				 * -------------------------------Ret Json object -------------------------------------
				 */
				JSONObject obj = new JSONObject();
				obj.put(CheckerSec.STATUS, CheckerSec.OK);
				obj.put(ID, rvTestata.getIdRvTestata());

				ret = obj.toString();

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il salvataggio delle ricerche salvate");
		}
		return ret;
	}

	public void delRicercheSalvate(String username, Integer idRicercaSalvata) throws AppEjbException {
		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {
				if (idRicercaSalvata != null) {
					rvTestataHome.removeCascadeById(idRicercaSalvata, pfPrincipal.getIdPfPrincipal());
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante la cancellazione della ricerca salvata");
		}
	}

	private Long getNumRicercheTotali(PfPrincipal pfPrincipal) {

		Class<RvTestata> clazz = (Class<RvTestata>) RvTestata.class;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<RvTestata> rvTestata = cq.from(clazz);

		cq.select(cb.count(rvTestata));

		Predicate p = cb.conjunction();
		p = cb.and(p, cb.equal(rvTestata.get(RvTestata_.pfPrincipal).get(PfPrincipal_.idPfPrincipal),
				pfPrincipal.getIdPfPrincipal()));
		cq.where(p);

		TypedQuery<Long> query = entityManager.createQuery(cq);

		Long ret = query.getSingleResult();

		return ret;
	}

	private List<RvTestata> getRicercheSalvate(PfPrincipal pfPrincipal, Integer start, Integer rows) {

		Class<RvTestata> clazz = (Class<RvTestata>) RvTestata.class;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RvTestata> cq = cb.createQuery(clazz);
		Root<RvTestata> r = cq.from(clazz);

		r.fetch("rvAttivitas", JoinType.LEFT);
		r.fetch("rvContrattos", JoinType.LEFT);
		r.fetch("rvLinguas", JoinType.LEFT);
		r.fetch("rvMansiones", JoinType.LEFT);
		r.fetch("rvOrarios", JoinType.LEFT);
		r.fetch("rvPatentes", JoinType.LEFT);
		r.fetch("rvTitolos", JoinType.LEFT);

		Predicate p = cb.conjunction();
		p = cb.and(p, cb.equal(r.get(RvTestata_.pfPrincipal).get(PfPrincipal_.idPfPrincipal),
				pfPrincipal.getIdPfPrincipal()));
		// Ordinamento per data inserimento decrescente
		cq.select(r).where(p).orderBy(cb.desc(r.get(RvTestata_.dtmIns)));

		TypedQuery<RvTestata> query = entityManager.createQuery(cq);
		query.setFirstResult(0);
		if (start != null && rows != null) {
			query.setFirstResult(start);
			query.setMaxResults(rows);
		}
		List<RvTestata> ret = query.getResultList();

		return ret;
	}

	private void validaRicerca(RicercheSalvatePojo ricercaSalvata) throws AppEjbException {

		if (StringUtils.isBlank(ricercaSalvata.getDescrizione())) {
			throw new EmptyParameterException("nome ricerca");
		}

		if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
			validaGruppi(ricercaSalvata.getCodContratto(), deContrattoSilHome);
			validaGruppi(ricercaSalvata.getCodOrario(), deOrarioSilHome);
			validaGruppi(ricercaSalvata.getCodPatente(), dePatenteSilHome);
			// La mansione non è prevista nel caso di usaDecodificheSilPerVacancy a true (in RvMansione non è presente la fk verso DeBpMansione)
		} else {
			validaGruppi(ricercaSalvata.getCodContratto(), deContrattoHome);
			validaGruppi(ricercaSalvata.getCodOrario(), deOrarioHome);
			validaGruppi(ricercaSalvata.getCodPatente(), dePatenteHome);
			validaGruppi(ricercaSalvata.getCodMansione(), deMansioneHome);
		}
		validaGruppi(ricercaSalvata.getCodLingua(), deLinguaHome);
		validaGruppi(ricercaSalvata.getCodSettore(), deAttivitaHome);
		validaGruppi(ricercaSalvata.getCodTitoloStudio(), deTitoloHome);
	}

	private static <AbstractDecodeEntity> boolean validaGruppi(List<String> listCodici,
			AbstractHibernateHome<AbstractDecodeEntity, String> home) throws AppEjbException {
		boolean ret = true;

		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					AbstractDecodeEntity entity = home.findById(codice);

					if (entity == null) {
						ret = false;
						throw new WrongParameterException(codice + "-" + home.getEntityName());
					}
				}
			}
		}
		return ret;
	}

	private RicercheSalvatePojo toPojo(RvTestata rvTestata) {
		RicercheSalvatePojo rs = new RicercheSalvatePojo();

		rs.setId(rvTestata.getIdRvTestata().toString());
		rs.setDescrizione(rvTestata.getDescrizione());
		rs.setCosa(rvTestata.getCosa());
		rs.setDove(rvTestata.getDove());

		if (rvTestata.getDtmIns() != null) {
			rs.setDtRicerca(
					DateFormatUtils.formatUTC(rvTestata.getDtmIns(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())
							+ "Z");
		}

		for (RvAttivita rvAttivita : rvTestata.getRvAttivitas())
			rs.getCodSettore().add(rvAttivita.getDeAttivita().getCodAteco());
		for (RvContratto rvContratto : rvTestata.getRvContrattos()) {
			// Gestione decodifiche Sil
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				rs.getCodContratto().add(rvContratto.getDeContrattoSil().getCodContrattoSil());
			} else {
				rs.getCodContratto().add(rvContratto.getDeContratto().getCodContratto());
			}
		}
		for (RvLingua rvLingua : rvTestata.getRvLinguas())
			rs.getCodLingua().add(rvLingua.getDeLingua().getCodLingua());
		for (RvMansione rvMansione : rvTestata.getRvMansiones())
			rs.getCodMansione().add(rvMansione.getDeMansione().getCodMansione());
		for (RvOrario rvOrario : rvTestata.getRvOrarios()) {
			// Gestione decodifiche Sil
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				rs.getCodOrario().add(rvOrario.getDeOrarioSil().getCodOrarioSil());
			} else {
				rs.getCodOrario().add(rvOrario.getDeOrario().getCodOrario());
			}
		}
		for (RvPatente rvPatente : rvTestata.getRvPatentes()) {
			// Gestione decodifiche Sil
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				rs.getCodPatente().add(rvPatente.getDePatenteSil().getCodPatenteSil());
			} else {
				rs.getCodPatente().add(rvPatente.getDePatente().getCodPatente());
			}
		}
		for (RvTitolo rvTitolo : rvTestata.getRvTitolos())
			rs.getCodTitoloStudio().add(rvTitolo.getDeTitolo().getCodTitolo());

		return rs;
	}

	private RvTestata toEntity(RicercheSalvatePojo ricercaSalvata, PfPrincipal pfPrincipal) {
		RvTestata ret = new RvTestata();

		ret.setCosa(StringUtils.abbreviate(ricercaSalvata.getCosa(), 200));
		ret.setDeComune(null);
		ret.setDeMansione(null);
		ret.setDeProvincia(null);
		ret.setDeRegione(null);
		ret.setDescrizione(StringUtils.abbreviate(ricercaSalvata.getDescrizione(), 100));
		ret.setDove(StringUtils.abbreviate(ricercaSalvata.getDove(), 200));

		Date now = new Date();
		ret.setDtmIns(now);
		ret.setDtmMod(now);

		ret.setIdRvTestata(null);
		ret.setPfPrincipal(pfPrincipal);
		ret.setPfPrincipalIns(pfPrincipal);
		ret.setPfPrincipalMod(pfPrincipal);
		ret.setRaggioRicerca(null);

		return ret;
	}

	private RvTestata persistTestata(RicercheSalvatePojo ricercaSalvata, PfPrincipal pfPrincipal) {

		RvTestata rvTestata = toEntity(ricercaSalvata, pfPrincipal);
		rvTestataHome.persist(rvTestata);

		return rvTestata;
	}

	private RvContratto toEntityRvContratto(RvTestata rvTestata, String codice) {
		RvContratto ret = new RvContratto();

		if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
			ret.setDeContrattoSil(deContrattoSilHome.findById(codice));
		} else {
			ret.setDeContratto(deContrattoHome.findById(codice));
		}

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvContratto(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvContratto(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvContratto rvContratto = toEntityRvContratto(rvTestata, codice);
					entityManager.persist(rvContratto);
				}
			}
		}
	}

	private RvLingua toEntityRvLingua(RvTestata rvTestata, String codice) {
		RvLingua ret = new RvLingua();

		ret.setDeLingua(deLinguaHome.findById(codice));

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvLingua(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvLingua(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvLingua rvLingua = toEntityRvLingua(rvTestata, codice);
					entityManager.persist(rvLingua);
				}
			}
		}
	}

	private RvMansione toEntityRvMansione(RvTestata rvTestata, String codice) {
		RvMansione ret = new RvMansione();

		ret.setDeMansione(deMansioneHome.findById(codice));

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvMansione(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvMansione(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvMansione rvMansione = toEntityRvMansione(rvTestata, codice);
					entityManager.persist(rvMansione);
				}
			}
		}
	}

	private RvOrario toEntityRvOrario(RvTestata rvTestata, String codice) {
		RvOrario ret = new RvOrario();

		if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
			ret.setDeOrarioSil(deOrarioSilHome.findById(codice));
		} else {
			ret.setDeOrario(deOrarioHome.findById(codice));
		}

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvOrario(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvOrario(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvOrario rvOrario = toEntityRvOrario(rvTestata, codice);
					entityManager.persist(rvOrario);
				}
			}
		}
	}

	private RvPatente toEntityRvPatente(RvTestata rvTestata, String codice) {
		RvPatente ret = new RvPatente();

		if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
			ret.setDePatenteSil(dePatenteSilHome.findById(codice));
		} else {
			ret.setDePatente(dePatenteHome.findById(codice));
		}

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvPatente(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvPatente(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvPatente rvPatente = toEntityRvPatente(rvTestata, codice);
					entityManager.persist(rvPatente);
				}
			}
		}
	}

	private RvAttivita toEntityRvAttivita(RvTestata rvTestata, String codice) {
		RvAttivita ret = new RvAttivita();

		ret.setDeAttivita(deAttivitaHome.findById(codice));

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvAttivita(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvAttivita(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvAttivita rvAttivita = toEntityRvAttivita(rvTestata, codice);
					entityManager.persist(rvAttivita);
				}
			}
		}
	}

	private RvTitolo toEntityRvTitolo(RvTestata rvTestata, String codice) {
		RvTitolo ret = new RvTitolo();

		ret.setDeTitolo(deTitoloHome.findById(codice));

		ret.setDtmIns(rvTestata.getDtmIns());
		ret.setDtmMod(rvTestata.getDtmMod());

		ret.setIdRvTitolo(null);
		ret.setPfPrincipalIns(rvTestata.getPfPrincipalIns());
		ret.setPfPrincipalMod(rvTestata.getPfPrincipalMod());
		ret.setRvTestata(rvTestata);

		return ret;
	}

	private void persistRvTitolo(List<String> listCodici, RvTestata rvTestata) {
		if (listCodici != null && !listCodici.isEmpty()) {
			for (String codice : listCodici) {
				if (StringUtils.isNotBlank(codice)) {
					RvTitolo rvTItolo = toEntityRvTitolo(rvTestata, codice);
					entityManager.persist(rvTItolo);
				}
			}
		}
	}

	private boolean ricercaSalvataPresente(RvTestata rvTestata, PfPrincipal pfPrincipal) {
		boolean ret = false;

		if (rvTestata != null && rvTestata.getPfPrincipal() != null
				&& rvTestata.getPfPrincipal().getIdPfPrincipal().equals(pfPrincipal.getIdPfPrincipal())) {
			// Deve trattarsi di una ricerca dell'utente loggato...
			ret = true;
		}
		return ret;
	}

}
