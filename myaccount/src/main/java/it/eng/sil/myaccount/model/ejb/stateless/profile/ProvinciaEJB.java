package it.eng.sil.myaccount.model.ejb.stateless.profile;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.myaccount.helpers.GpGruppoFilter;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;

@Stateless
public class ProvinciaEJB extends AbstractTabellaProfiloEJB<Provincia, Integer> {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	DeSistemaEJB deSistemaEJB;

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni utente provinciale";
	}

	@Override
	public Class<Provincia> getEntityClass() {
		return Provincia.class;
	}

	public Provincia findByPfPrincipalId(Integer id) throws MyCasNoResultException {
		return entityMan.createNamedQuery("findProvinciaByPfPrincipalId", Provincia.class)
				.setParameter("idPfPrincipal", id).getSingleResult();
	}

	public void updateProfile(Provincia provinciaRegister) throws Exception {
		pfPrincipalMyAccountEJB.updateProvincia(provinciaRegister.getPfPrincipal());
		merge(provinciaRegister.getPfPrincipal().getIdPfPrincipal(), provinciaRegister);
	}

	public Provincia register(PfPrincipal utente, Provincia newProvincia, Integer idPrincipalIns, String codRuoloPortale)
			throws MyCasException {
		// Creo la riga sulla tabella PfPrincipal.
		utente = pfPrincipalMyAccountEJB.registerProvincia(utente, false, codRuoloPortale);

		// Creo la riga sulla tabella Provincia.
		newProvincia.setPfPrincipal(utente);
		newProvincia.setIdPfPrincipal(utente.getIdPfPrincipal());
		newProvincia = persist(idPrincipalIns, newProvincia);

		// NUOVA PROFILATURA : Inserisco le profilature di default per questo utente.
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.PRV);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(utente);
			gpProfilaturaEJB.persist(idPrincipalIns, newProfilatura);
		}

		return newProvincia;
	}

	/** Questa funzione registra un utente del vecchio SARE di tipo 'P' (operatore provinciale) o 'C' (operatore CPI) **/
	public Provincia registerFromPortingSare(PfPrincipal utente, Provincia newProvincia, Integer idPrincipalIns)
			throws MyCasException {
		// Controllo che la email non sia duplicata
		boolean mailDuplicata = !pfPrincipalMyAccountEJB.isUniqueEmail(utente.getEmail());

		// Come prima cosa, creo l'utente usando la procedura normale di register.
		utente = pfPrincipalMyAccountEJB.registerProvinciaFromPorting(utente);

		// Se la mail era duplicata, faccio una merge al volo per aggiungere il prefisso.
		if (mailDuplicata) {
			utente.setEmail(utente.getIdPfPrincipal().toString() + "_" + utente.getEmail());
			utente = pfPrincipalMyAccountEJB.merge(idPrincipalIns, utente);
		}

		// Creo la riga sulla tabella Provincia.
		newProvincia.setPfPrincipal(utente);
		newProvincia.setIdPfPrincipal(utente.getIdPfPrincipal());
		newProvincia = persist(idPrincipalIns, newProvincia);

		// NUOVA PROFILATURA : Inserisco le profilature di default per questo utente.
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.PRV);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(utente);
			gpProfilaturaEJB.persist(idPrincipalIns, newProfilatura);
		}

		// PROFILATURA SARE : Scelgo il ruolo e il gruppo in base al tipo di utente
		// (Assumo di trovare il ruolo che mi serve, se no mi spacco)
		DeSistema sare = deSistemaEJB.findByEnum(DeSistemaEnum.SARE);
		GpRuolo ruoloUtenteSare = null;
		GpGruppo gruppoUtenteSare = null;
		GpRuoloGruppo ruoloGruppoUtenteSare = null;
		if ("C".equals(newProvincia.getCodTipoUtenteSare())) {
			// "C" : operatore CPI. Gruppo di tipo CPI, Ruolo "Gestione Rettifiche"
			ruoloUtenteSare = gpRuoloEJB.findByFilter("Gestione Rettifiche", sare).get(0);

			// Abbiamo deciso di non fare una mappatura per i CPI, quindi li prendo per descrizione.
			if (newProvincia.getDeCpi() != null) {
				String codCpiUtente = newProvincia.getDeCpi().getCodCpi();
				codCpiUtente = codCpiUtente.substring(0, codCpiUtente.length() - 2);

				// Mappo a mano i nomi dei gruppi CPI.
				String descrizioneLike = null;
				if ("1058001".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Perugia";
				} else if ("1058003".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Città di Castello";
				} else if ("1058004".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Foligno";
				} else if ("1080006".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Terni";
				} else if ("1080007".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Orvieto";
				}else if ("020400100".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Aosta";
				}else if ("020400300".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Verres";
				}else if ("020400200".equals(codCpiUtente)) {
					descrizioneLike = "CPI di Morgex";
				} else {
					log.error("Non ho trovato il CPI rimappato!! codCpiUtente= "+codCpiUtente);
					
				}

				if (descrizioneLike != null) {
					GpGruppoFilter filter = (new GpGruppoFilter()).setCodTipoGruppo(GpDeTipoGruppoEnum.CPI)
							.setDescrizione(descrizioneLike).setDescrizioneEsatta(false);
					List<GpGruppo> gruppiCpi = gpGruppoEJB.findByFilter(filter, null);
					if (gruppiCpi != null && !gruppiCpi.isEmpty()) {
						gruppoUtenteSare = gruppiCpi.get(0);
					}
				}
			}
		} else if ("P".equals(newProvincia.getCodTipoUtenteSare())) {
			// "P" : operatore provinciale. Gruppo di tipo OPE, Ruolo "Visualizza CO"
			ruoloUtenteSare = gpRuoloEJB.findByFilter("Visualizzazione CO", sare).get(0);

			// Cerco un gruppo di tipo GPV già esistente in base alla provincia.
			GpGruppoFilter filter = new GpGruppoFilter();
			filter.setCodTipoGruppo(GpDeTipoGruppoEnum.GPV).setCodProvincia(
					newProvincia.getDeProvincia().getCodProvincia());
			List<GpGruppo> gruppiProvincia = gpGruppoEJB.findByFilter(filter, null);
			if (gruppiProvincia != null && !gruppiProvincia.isEmpty()) {
				gruppoUtenteSare = gruppiProvincia.get(0);
			}
		}

		if (ruoloUtenteSare != null && gruppoUtenteSare != null) {
			// Il RuoloGruppo potrebbe esistere già (se sono stati registrati altri utenti della stessa provincia).
			// Se non esiste, lo creo.
			List<GpRuoloGruppo> ruoliGruppiEsistenti = gpRuoloGruppoEJB.findByFilter(null, ruoloUtenteSare, sare,
					gruppoUtenteSare, null, null);
			if (ruoliGruppiEsistenti.isEmpty()) {
				GpRuoloGruppo newRuoloGruppo = new GpRuoloGruppo();
				newRuoloGruppo.setFlgDefault(false);
				newRuoloGruppo.setGpGruppo(gruppoUtenteSare);
				newRuoloGruppo.setGpRuolo(ruoloUtenteSare);
				ruoloGruppoUtenteSare = gpRuoloGruppoEJB.persist(idPrincipalIns, newRuoloGruppo);
			} else {
				ruoloGruppoUtenteSare = ruoliGruppiEsistenti.get(0);
			}
		} else {
			log.error("Non ho trovato il ruolo o il gruppo da assegnare all'utente! L'utente avrà solo le profilature di default.");
			return newProvincia;
		}

		if (ruoloGruppoUtenteSare != null) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setPfPrincipal(newProvincia.getPfPrincipal());
			newProfilatura.setGpRuoloGruppo(ruoloGruppoUtenteSare);
			newProfilatura = gpProfilaturaEJB.persist(idPrincipalIns, newProfilatura);
		}

		return newProvincia;
	}
}
