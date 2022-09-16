package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo_;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpRuoloEJB;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

@Stateless
public class GpRuoloMyAccountEJB extends GpRuoloEJB {

	@EJB
	DeSistemaEJB deSistemaEJB;

	@EJB
	GpAbiComponenteMyAccountEJB gpAbiComponenteEJB;

	@EJB
	GpComponenteMyAccountEJB gpComponenteEJB;

	@EJB
	GpAbiAttributoMyAccountEJB gpAbiAttributoEJB;

	@EJB
	GpAttributoMyAccountEJB gpAttributoEJB;

	/**
	 * Crea un nuovo ruolo, ed eventualmente copia le abilitazioni di un altro ruolo.
	 */
	public void creaNuovoRuolo(DeSistema sistema, String descrizione, GpRuolo ruoloCopiaAbi, Integer idPrincipalIns)
			throws MyCasException {
		GpRuolo newRuolo = new GpRuolo();
		newRuolo.setDeSistema(deSistemaEJB.findById(sistema.getCodSistema()));
		newRuolo.setDescrizione(descrizione);
		newRuolo = persist(idPrincipalIns, newRuolo);

		if (ruoloCopiaAbi != null) {
			ruoloCopiaAbi = findById(ruoloCopiaAbi.getIdGpRuolo()); // Per avere entity attached.
			for (GpAbiComponente abiComponente : ruoloCopiaAbi.getComponentiAbilitati()) {
				// Creo una copia di ogni abiComponente, collegata al ruolo appena creato.
				GpAbiComponente abiComponenteCopy = new GpAbiComponente();
				abiComponenteCopy.setGpComponente(gpComponenteEJB.findById(abiComponente.getGpComponente()
						.getIdGpComponente()));
				abiComponenteCopy.setGpRuolo(newRuolo);
				abiComponenteCopy = gpAbiComponenteEJB.persist(idPrincipalIns, abiComponenteCopy);

				// Per ogni abiComponente, copio anche tutte le abiAttributo ad essa collegate.
				for (GpAbiAttributo abiAttributo : abiComponente.getAttributiAbilitati()) {
					GpAbiAttributo abiAttributoCopy = new GpAbiAttributo();
					abiAttributoCopy.setGpAttributo(gpAttributoEJB.findById(abiAttributo.getGpAttributo()
							.getIdGpAttributo()));
					abiAttributoCopy.setGpAbiComponente(abiComponenteCopy);
					abiAttributoCopy = gpAbiAttributoEJB.persist(idPrincipalIns, abiAttributoCopy);
				}
			}
		}
	}

	/**
	 * Seleziona una lista di ruoli in base ad alcuni parametri di ricerca.
	 */
	public List<GpRuolo> findByFilter(String descrizione, DeSistema sistema) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpRuolo> cq = qb.createQuery(GpRuolo.class);
		Root<GpRuolo> gpRuolo = cq.from(GpRuolo.class);
		cq.select(gpRuolo);
		List<Predicate> predicati = new ArrayList<Predicate>();

		if (descrizione != null && !descrizione.isEmpty()) {
			Predicate inizioFrasePred = qb.like(qb.upper(gpRuolo.get(GpRuolo_.descrizione)), descrizione.toUpperCase()
					+ "%");
			Predicate inizioParolaPred = qb.like(qb.upper(gpRuolo.get(GpRuolo_.descrizione)),
					"% " + descrizione.toUpperCase() + "%");
			predicati.add(qb.or(inizioFrasePred, inizioParolaPred));
		}

		if (sistema != null) {
			if (sistema.getCodSistema() != null && !sistema.getCodSistema().isEmpty()) {
				Join<GpRuolo, DeSistema> deSistema = gpRuolo.join(GpRuolo_.deSistema);
				Predicate codSistemaPred = qb.equal(qb.upper(deSistema.get(DeSistema_.codSistema)), sistema
						.getCodSistema().toUpperCase());
				predicati.add(codSistemaPred);
			}
		}

		if (predicati.size() > 0) {
			Predicate qsPred = qb.and(predicati.toArray(new Predicate[predicati.size()]));
			cq.where(qsPred);
		}

		// Ordine di default: alfabetico sulla descrizione.
		cq.orderBy(qb.asc(gpRuolo.get(GpRuolo_.descrizione)));

		TypedQuery<GpRuolo> query = entityMan.createQuery(cq);
		List<GpRuolo> resultList = query.getResultList();
		return resultList;
	}

	/**
	 * Restituisce una lista di ruoli per cui è possibile creare un RuoloGruppo insieme ad un certo gruppo.
	 */
	public List<GpRuolo> findRuoliAssegnabiliByGruppo(String par, GpGruppo gruppo, DeSistema sistema) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpRuolo> criteria = cb.createQuery(GpRuolo.class);
		Root<GpRuolo> gpRuoloRoot = criteria.from(GpRuolo.class);
		criteria.select(gpRuoloRoot);
		criteria.orderBy(cb.asc(gpRuoloRoot.get(GpRuolo_.descrizione)));
		List<Predicate> predicati = new ArrayList<Predicate>();
		par = par.trim().toUpperCase();

		// Condizione opzionale sul parametro stringa (inizio frase o parola)
		if (par != null && !par.isEmpty()) {
			predicati.add(cb.or(cb.like(cb.upper(gpRuoloRoot.get(GpRuolo_.descrizione)), par + "%"),
					cb.like(cb.upper(gpRuoloRoot.get(GpRuolo_.descrizione)), "% " + par + "%")));
		}

		// Condizione opzionale sul sistema.
		if (sistema != null) {
			Join<GpRuolo, DeSistema> deSistemaJoin = gpRuoloRoot.join(GpRuolo_.deSistema);
			predicati.add(cb.equal(deSistemaJoin.get(DeSistema_.codSistema), sistema.getCodSistema()));
		}

		// Condizione : voglio solo ruoli per cui non esiste già un RuoloGruppo con questo gruppo.
		Subquery<Integer> subquery = criteria.subquery(Integer.class);
		Root<GpRuoloGruppo> subRoot = subquery.from(GpRuoloGruppo.class);
		Join<GpRuoloGruppo, GpGruppo> subGruppoJoin = subRoot.join(GpRuoloGruppo_.gpGruppo);
		Join<GpRuoloGruppo, GpRuolo> subRuoloJoin = subRoot.join(GpRuoloGruppo_.gpRuolo);
		subquery.select(subRuoloJoin.get(GpRuolo_.idGpRuolo));
		List<Predicate> subqueryPredicati = new ArrayList<Predicate>();
		subqueryPredicati.add(cb.equal(subGruppoJoin.get(GpGruppo_.idGpGruppo), gruppo.getIdGpGruppo()));
		subqueryPredicati.add(cb.equal(subRuoloJoin.get(GpRuolo_.idGpRuolo), gpRuoloRoot.get(GpRuolo_.idGpRuolo)));
		subquery.where(subqueryPredicati.toArray(new Predicate[predicati.size()]));
		predicati.add(gpRuoloRoot.get(GpRuolo_.idGpRuolo).in(subquery).not());

		// Lancio la query e restituisco la lista di risultati
		criteria.where(predicati.toArray(new Predicate[predicati.size()]));
		TypedQuery<GpRuolo> query = entityMan.createQuery(criteria);
		return query.getResultList();
	}
}
