package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpAbiComponenteEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpComponenteEJB;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class GpAbiComponenteMyAccountEJB extends GpAbiComponenteEJB {

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpComponenteEJB gpComponenteEJB;

	/**
	 * Disabilita un componente per un ruolo.
	 */
	public void disabilita(GpRuolo ruolo, GpComponente componente) throws MyCasException {
		GpAbiComponente abiComponente = findByRuoloAndComponente(ruolo, componente);
		remove(abiComponente);
	}

	/**
	 * Abilita un componente per un ruolo.
	 */
	public void abilita(GpRuolo ruolo, GpComponente componente, Integer idPrincipalIns) throws MyCasException {
		GpAbiComponente abiComponente = new GpAbiComponente();
		abiComponente.setGpRuolo(gpRuoloEJB.findById(ruolo.getIdGpRuolo()));
		abiComponente.setGpComponente(gpComponenteEJB.findById(componente.getIdGpComponente()));
		persist(idPrincipalIns, abiComponente);
	}

	/**
	 * Abilita un componente per un ruolo se è disabilitato, lo disabilita se è abilitato.
	 */
	public void toggle(GpRuolo ruolo, GpComponente componente, Integer idPrincipalIns) throws MyCasException {
		try {
			// Se il componente è abilitato, devo disabilitarlo.
			GpAbiComponente abilitazione = findByRuoloAndComponente(ruolo, componente);
			remove(abilitazione);
		} catch (MyCasNoResultException e) {
			// Non è un errore, vuol dire che il componente è disabilitato e devo abilitarlo.
			GpAbiComponente newAbilitazione = new GpAbiComponente();
			newAbilitazione.setGpRuolo(gpRuoloEJB.findById(ruolo.getIdGpRuolo()));
			newAbilitazione.setGpComponente(gpComponenteEJB.findById(componente.getIdGpComponente()));
			persist(idPrincipalIns, newAbilitazione);
		}
	}

	/**
	 * Recupera l'abilitazione per un ruolo e componente. Lancia un'eccezione se non esiste.
	 */
	public GpAbiComponente findByRuoloAndComponente(GpRuolo ruolo, GpComponente componente)
			throws MyCasNoResultException, MyCasTooManyResultsException {
		TypedQuery<GpAbiComponente> query = entityMan.createNamedQuery("findGpAbiComponenteByRuoloAndComponente",
				GpAbiComponente.class);
		query.setParameter("idGpRuolo", ruolo.getIdGpRuolo());
		query.setParameter("idGpComponente", componente.getIdGpComponente());

		List<GpAbiComponente> resultList = query.getResultList();
		if (resultList == null || resultList.isEmpty()) {
			throw new MyCasNoResultException("GpAbiComponente per ruolo " + ruolo.getIdGpRuolo() + " e componente "
					+ componente.getIdGpComponente() + " non trovata.");
		} else if (resultList.size() > 1) {
			throw new MyCasTooManyResultsException("Due GpAbiComponente trovati per lo stesso ruolo e componente");
		} else {
			return resultList.get(0);
		}
	}
}
