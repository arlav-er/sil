package it.eng.sil.myaccount.model.ejb.stateless.auth;

import it.eng.sil.base.exceptions.GamificationBadgeValueException;
import it.eng.sil.base.exceptions.GamificationDisabledException;
import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.StringUtils;
import it.eng.sil.mycas.model.business.gamification.GamificationClientEJB;
import it.eng.sil.mycas.model.entity.gamification.CodBadgeEnum;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.stateless.gamification.GpBadgeEJB;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class GamificationClientMyAccountEJB extends GamificationClientEJB {

	private static final double NUMERO_TOTALE_CAMPI_PROFILO = 12;

	@EJB
	GpBadgeEJB gpBadgeEJB;

	@EJB
	ConstantsSingleton constantsSingleton;

	public Double calcolaProfiloCompletoValueBadge(UtenteInfo utente) {
		int campiNotNull = 0;
		if (!StringUtils.isEmptyNoBlank(utente.getPfPrincipal().getNome()))
			campiNotNull++;
		if (!StringUtils.isEmptyNoBlank(utente.getPfPrincipal().getCognome()))
			campiNotNull++;
		if (utente.getDeCittadinanza() != null) {
			campiNotNull++;
		}
		if (!StringUtils.isEmptyNoBlank(utente.getCodiceFiscale()))
			campiNotNull++;

		if (utente.getDtNascita() != null)
			campiNotNull++;
		if (utente.getDeComuneNascita() != null)
			campiNotNull++;

		if (!StringUtils.isEmptyNoBlank(utente.getIndirizzoResidenza()))
			campiNotNull++;
		if (utente.getDeComuneResidenza() != null)
			campiNotNull++;

		if (!StringUtils.isEmptyNoBlank(utente.getIndirizzoDomicilio()))
			campiNotNull++;
		if (utente.getDeComuneDomicilio() != null)
			campiNotNull++;

		if (!StringUtils.isEmptyNoBlank(utente.getCellulare()))
			campiNotNull++;
		if (!StringUtils.isEmptyNoBlank(utente.getTelCasa()))
			campiNotNull++;

		return campiNotNull / NUMERO_TOTALE_CAMPI_PROFILO;
	}

	public void assignProfiloCompletoBadge(Integer idPfPrincipal, Double newValue) {
		try {
			super.assignBadge(
					constantsSingleton.isMyAuthLocalCallMode() ? null : constantsSingleton
							.getGamificationAssignBadgeURL(), CodBadgeEnum.COMCOMPR.toString(), idPfPrincipal, newValue);
		} catch (GamificationBadgeValueException e) {
			log.warn("Non assegno il badge 'Immagine Inserita' perchè l'utente lo ha già.");
		} catch (GamificationDisabledException e) {
			log.warn("Gamification disabilitata, non assegno immagine inserita a " + idPfPrincipal);
		} catch (GamificationException e) {
			log.warn("Errore durante la assignProfiloCompletoBadge() per utente " + idPfPrincipal + " : " + e);
		} catch (Exception e) {
			log.error("GRAVE: Errore imprevisto durante la assignProfiloCompletoBadge() per utente " + idPfPrincipal
					+ " : " + e);
		}
	}

	public void assignImmagineInseritaBadge(Integer idPfPrincipal, Double newValue) {
		try {
			super.assignBadge(
					constantsSingleton.isMyAuthLocalCallMode() ? null : constantsSingleton
							.getGamificationAssignBadgeURL(), CodBadgeEnum.INSIMMPR.toString(), idPfPrincipal, newValue);
		} catch (GamificationBadgeValueException e) {
			log.warn("Non assegno il badge 'Immagine Inserita' perchè l'utente lo ha già.");
		} catch (GamificationDisabledException e) {
			log.warn("Gamification disabilitata, non assegno immagine inserita a " + idPfPrincipal);
		} catch (GamificationException e) {
			log.warn("Errore durante la assignImmagineInseritaBadge() per utente " + idPfPrincipal + " : " + e);
		} catch (Exception e) {
			log.error("GRAVE: Errore imprevisto durante la assignImmagineInseritaBadge() per utente " + idPfPrincipal
					+ " : " + e);
		}
	}

	public void assignServiziAmministrativiBadge(Integer idPfPrincipal, Double newValue) {
		try {
			super.assignBadge(
					constantsSingleton.isMyAuthLocalCallMode() ? null : constantsSingleton
							.getGamificationAssignBadgeURL(), CodBadgeEnum.ABILSERV.toString(), idPfPrincipal, newValue);
		} catch (GamificationBadgeValueException e) {
			log.warn("Non assegno il badge 'Immagine Inserita' perchè l'utente lo ha già.");
		} catch (GamificationDisabledException e) {
			log.warn("Gamification disabilitata, non assegno immagine inserita a " + idPfPrincipal);
		} catch (GamificationException e) {
			log.warn("Errore durante la assignServiziAmministrativiBadge() per utente " + idPfPrincipal + " : " + e);
		} catch (Exception e) {
			log.error("GRAVE: Errore imprevisto durante la assignServiziAmministrativiBadge() per utente "
					+ idPfPrincipal + " : " + e);
		}
	}
}
