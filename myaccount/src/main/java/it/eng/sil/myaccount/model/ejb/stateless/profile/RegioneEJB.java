package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Regione;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeRegioneEJB;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RegioneEJB extends AbstractTabellaProfiloEJB<Regione, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	DeRegioneEJB deRegioneEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni utente provinciale";
	}

	@Override
	public Class<Regione> getEntityClass() {
		return Regione.class;
	}

	/**
	 * Crea un nuovo utente regionale. Aggiunge dati sia per la vecchia profilatura di MyPortal che per la nuova
	 * profilatura.
	 */
	public Regione register(PfPrincipal utente, Regione newRegione, Integer idPrincipalIns) throws MyCasException {
		// Creo la riga sulla tabella PfPrincipal.
		utente = pfPrincipalMyAccountEJB.registerRegione(utente, false);

		// Creo la riga sulla tabella Regione.
		newRegione.setPfPrincipal(utente);
		newRegione.setIdPfPrincipal(utente.getIdPfPrincipal());
		newRegione = persist(idPrincipalIns, newRegione);

		// NUOVA PROFILATURA : Inserisco le profilature di default per questo utente.
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.REG);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(utente);
			gpProfilaturaEJB.persist(idPrincipalIns, newProfilatura);
		}

		return newRegione;
	}

	public Regione findByPfPrincipalId(Integer id) throws MyCasNoResultException {
		return entityMan.createNamedQuery("findRegioneByPfPrincipalId", Regione.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}

}
