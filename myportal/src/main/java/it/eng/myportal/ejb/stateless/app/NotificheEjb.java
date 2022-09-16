package it.eng.myportal.ejb.stateless.app;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.AppNotificaHome;
import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.NotifyNotFoundException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.rest.app.helper.StatoNotifica;
import it.eng.myportal.rest.app.pojo.NotifichePojo;
import it.eng.myportal.rest.app.pojo.NotifichePojoList;

@Stateless
public class NotificheEjb {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	private PfIdentityDeviceHome pfIdentityDevice;

	@EJB
	AppNotificaHome appNotificaHome;

	private final Log logger = LogFactory.getLog(this.getClass());

	private final static String ID = "id";

	public NotifichePojoList getNotificheBroadcast(String username, Integer start, Integer rows)
			throws AppEjbException {

		NotifichePojoList ret = new NotifichePojoList();

		try {
			if (pfPrincipalHome.exists(username)) {

				// Stati delle notifiche da recuperare
				List<StatoNotifica> listStato = Arrays.asList(StatoNotifica.R, StatoNotifica.S, StatoNotifica.C);

				// Recupero del totale notifiche
				Long numNotificheTotali = appNotificaHome.getCountNotificheBroadcast(listStato);
				ret.setNumNotificheTotali(numNotificheTotali);

				if (numNotificheTotali != null && numNotificheTotali > 0) {
					List<AppNotifica> listAppNotifica = appNotificaHome.getNotificheBroadcast(listStato, start, rows);

					for (AppNotifica appNotifica : listAppNotifica) {
						NotifichePojo not = new NotifichePojo(appNotifica);
						ret.getListaNotifiche().add(not);
					}
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il recupero delle notifiche broadcast");
		}
		return ret;
	}

	public NotifichePojoList getNotifichePersonali(String username, Integer start, Integer rows)
			throws AppEjbException {

		NotifichePojoList ret = new NotifichePojoList();

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {
				/*
				 * Nel momento in cui vengono recuperate le notifiche personali, per quelle in stato delayed si recupera
				 * lo stato aggiornato dal ProviderNotification, per verificare se è stata nel frattempo inviata
				 */
				refreshStatoNotificheDelayed(pfPrincipal);

				// Stati delle notifiche da recuperare
				List<StatoNotifica> listStato = Arrays.asList(StatoNotifica.R, StatoNotifica.S, StatoNotifica.C);

				// Recupero del totale notifiche
				Long numNotificheTotali = appNotificaHome.getCountNotifichePersonali(pfPrincipal.getIdPfPrincipal(),
						listStato);
				ret.setNumNotificheTotali(numNotificheTotali);

				if (numNotificheTotali != null && numNotificheTotali > 0) {
					// Recupero del totale nuove notifiche
					ret.setNumNuoveNotifiche(
							appNotificaHome.getCountNuoveNotifichePersonali(pfPrincipal.getIdPfPrincipal()));

					// Recupero delle notifiche, eventualmente paginate
					List<AppNotifica> listAppNotifica = appNotificaHome
							.getNotifichePersonali(pfPrincipal.getIdPfPrincipal(), listStato, start, rows);

					for (AppNotifica appNotifica : listAppNotifica) {
						NotifichePojo not = new NotifichePojo(appNotifica);
						ret.getListaNotifiche().add(not);
					}
				}

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il recupero delle notifiche personali");
		}
		return ret;
	}

	public String setClicked(String username, String sid) throws AppEjbException {
		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {

				// Recupero della notifica attraverso il suo sid
				AppNotifica appNotifica = null;

				try {
					appNotifica = appNotificaHome.findBySid(sid);
				} catch (EJBTransactionRolledbackException e) {

					if (e.getCause() instanceof NoResultException) {
						// Notifica non trovata
						throw new NotifyNotFoundException(sid);
					} else {
						throw e;
					}
				}

				if (notificaPresente(appNotifica, pfPrincipal)) {
					// Modifica dello stato (solo se non già letto e confermato)
					if (!appNotifica.getStato().equals(StatoNotifica.C)) {
						appNotificaHome.modificaStato(appNotifica, StatoNotifica.C, true, pfPrincipal);
					}

					JSONObject obj = new JSONObject();
					obj.put(CheckerSec.STATUS, CheckerSec.OK);
					obj.put(ID, appNotifica.getSidNotifica());

					ret = obj.toString();
					// }
				} else {
					// Notifica non trovata
					throw new NotifyNotFoundException(sid);
				}

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il set dello stato letto della notifica");
		}
		return ret;
	}

	private int refreshStatoNotificheDelayed(PfPrincipal pfPrincipal) {

		int numNotificheStato = 0;

		// Lista di tutte le notifiche dell'utente in stato Delayed
		List<StatoNotifica> listStato = Arrays.asList(StatoNotifica.D);
		List<AppNotifica> elencoNotifiche = appNotificaHome.getNotifichePersonali(pfPrincipal.getIdPfPrincipal(),
				listStato, null, null);

		if (elencoNotifiche != null && !elencoNotifiche.isEmpty()) {
			for (AppNotifica appNotifica : elencoNotifiche) {
				try {
					appNotificaHome.aggiornaStatoNotifica(appNotifica);
					numNotificheStato += 1;

				} catch (Exception e) {
					logger.error("Errore durante l'aggiornamento dello stato della notifica con id: "
							+ appNotifica.getIdAppNotifica() + " - notifica sconosciuta a one signal? " + e.getMessage());
				}
			}
		}
		return numNotificheStato;
	}

	private boolean notificaPresente(AppNotifica notifica, PfPrincipal pfPrincipalDest) {
		boolean ret = false;

		if (notifica != null && notifica.getPfPrincipalDest() != null
				&& notifica.getPfPrincipalDest().getIdPfPrincipal().equals(pfPrincipalDest.getIdPfPrincipal())) {
			// Deve trattarsi di una notifica dell'utente loggato...
			ret = true;
		}
		return ret;
	}
}
