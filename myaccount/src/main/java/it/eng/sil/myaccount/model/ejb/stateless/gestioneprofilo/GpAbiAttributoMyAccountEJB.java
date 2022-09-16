package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpAbiAttributoEJB;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class GpAbiAttributoMyAccountEJB extends GpAbiAttributoEJB {

	@EJB
	GpAbiComponenteMyAccountEJB gpAbiComponenteEJB;

	@EJB
	GpAttributoMyAccountEJB gpAttributoEJB;

	/**
	 * Disabilita un attributo per un ruolo. Se è già disabilitato, non fa nulla.
	 */
	public void disabilita(GpRuolo ruolo, GpAttributo attributo) throws MyCasException {
		GpAbiAttributo abiAttributo = findByRuoloAndAttributo(ruolo, attributo);
		remove(abiAttributo);
	}

	/**
	 * Abilita un attributo per un ruolo. Se è già abilitato, non fa nulla.
	 */
	public void abilita(GpRuolo ruolo, GpAttributo attributo, Integer idPrincipalIns) throws MyCasException {
		GpAbiAttributo abiAttributo = new GpAbiAttributo();
		GpAbiComponente abiComponente = gpAbiComponenteEJB.findByRuoloAndComponente(ruolo, attributo.getGpComponente());
		abiAttributo.setGpAbiComponente(abiComponente);
		abiAttributo.setGpAttributo(gpAttributoEJB.findById(attributo.getIdGpAttributo()));
		persist(idPrincipalIns, abiAttributo);
	}

	/**
	 * Abilita un attributo per un ruolo se è disabilitato, lo disabilita se è abilitato.
	 */
	public void toggle(GpRuolo ruolo, GpAttributo attributo, Integer idPrincipalIns) throws MyCasException {
		try {
			// Se l'attributo è abilitato, devo disabilitarlo.
			GpAbiAttributo abilitazione = findByRuoloAndAttributo(ruolo, attributo);
			remove(abilitazione);
		} catch (MyCasNoResultException e) {
			// Non è un errore, vuol dire che l'attributo è disabilitato e devo abilitarlo.
			GpAbiAttributo newAbilitazione = new GpAbiAttributo();
			GpAbiComponente abiComponente = gpAbiComponenteEJB.findByRuoloAndComponente(ruolo,
					attributo.getGpComponente());
			newAbilitazione.setGpAbiComponente(abiComponente);
			newAbilitazione.setGpAttributo(gpAttributoEJB.findById(attributo.getIdGpAttributo()));
			persist(idPrincipalIns, newAbilitazione);
		}
	}

	/**
	 * Trova un abiAttributo per un certo ruolo ed un certo attributo.
	 */
	public GpAbiAttributo findByRuoloAndAttributo(GpRuolo ruolo, GpAttributo attributo) throws MyCasNoResultException,
			MyCasTooManyResultsException {
		TypedQuery<GpAbiAttributo> query = entityMan.createNamedQuery("findGpAbiAttributoByRuoloAndAttributo",
				GpAbiAttributo.class);
		query.setParameter("idGpRuolo", ruolo.getIdGpRuolo());
		query.setParameter("idGpAttributo", attributo.getIdGpAttributo());

		List<GpAbiAttributo> resultList = query.getResultList();
		if (resultList == null || resultList.isEmpty()) {
			throw new MyCasNoResultException("GpAbiAttributo per ruolo " + ruolo.getIdGpRuolo() + " e attributo "
					+ attributo.getIdGpAttributo() + " non trovata.");
		} else if (resultList.size() > 1) {
			throw new MyCasTooManyResultsException("Due GpAbiAttributo trovati per lo stesso ruolo e attributo");
		} else {
			return resultList.get(0);
		}
	}
}
