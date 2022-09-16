package it.eng.sil.myaccount.model.ejb.stateless.myportal;

import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.entity.myportal.MsgMessaggio;
import it.eng.sil.myaccount.model.utils.AziendaInfoHelper;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.model.utils.MessaggioUtils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfoRettifica;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Provincia;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

@Stateless
public class NotificationBuilder {

	@EJB
	MsgMessaggioEJB msgMessaggioEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	ConstantsSingleton constantsSingleton;

	public static final Integer GIORNI_SCADENZA = 30;

	/**
	 * Invia una notifica. La notifica viene inviata dal sistema (utente '0')
	 * 
	 * @param notification
	 *            la notifica da inviare
	 * @return la notifica inviata
	 * @throws Exception
	 */
	public void sendNotification(Set<MsgMessaggio> notifications) throws MyCasException {
		for (MsgMessaggio notification : notifications) {
			sendNotification(notification);
		}
	}

	public void sendNotification(MsgMessaggio notification) throws MyCasException {
		/* la notifica viene inviata dal sistema: utente '0' */
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, GIORNI_SCADENZA);
		notification.setDtScadenza(c.getTime());
		msgMessaggioEJB.persist(0, notification);
	}

	/**
	 * Data una richiesta di rettifica dati azienda, restituisce l'insieme delle notifiche da inviare
	 * 
	 * @param rettifica
	 * @return
	 * @throws MyAccountNoResultException
	 * @throws EJBException
	 */
	public Set<MsgMessaggio> buildNotifications(AziendaInfoRettifica rettifica) throws EJBException,
			MyCasNoResultException {
		Set<MsgMessaggio> ret = new HashSet<MsgMessaggio>();

		AziendaInfo aziendaInfo = aziendaInfoEJB.findById(rettifica.getIdPfPrincipal());
		PfPrincipal pfPrincipal = aziendaInfo.getPfPrincipal();

		MsgMessaggio toProvincia = new MsgMessaggio(MessaggioUtils.NOTIFICA);
		toProvincia.setOggetto("Richiesta rettifica dati dell'utente " + pfPrincipal.getUsername());

		String strMsgAzienda = "L'utente\n" + "username: " + pfPrincipal.getUsername() + "<br/>" + "ragione sociale: "
				+ aziendaInfo.getRagioneSociale() + "<br/>"
				+ "ha richiesto una rettifica dei seguenti dati profilo:<br/>" + "<ul>";

		// lista dei parametri cambiati dall'azienda
		Map<String, Object> asMap = AziendaInfoHelper.asMap(rettifica);
		for (Entry<String, Object> field : asMap.entrySet()) {
			if (field.getValue() != null) {
				strMsgAzienda += "<li>" + field.getKey() + ": " + field.getValue().toString() + "</li>";
			}
		}

		strMsgAzienda += "</ul>";

		strMsgAzienda += "Accedere alla pagina di rettifica direttamente da <a href=\""
				+ constantsSingleton.getMyAccountURL() + "/secure/azienda/edit/" + rettifica.getIdPfPrincipal()
				+ "\">questo link</a><br/>"
				+ "oppure spostarsi nell'apposita pagina di elenco di richieste di rettifica.";

		msgMessaggioEJB.setDestinatario(toProvincia, pfPrincipal);
		toProvincia.setCorpo(strMsgAzienda);

		ret.add(toProvincia);
		return ret;
	}

	/**
	 * Crea le notifiche per la cancellazione di una richiesta di rettifica.
	 * 
	 * @param pfPrincipal
	 *            utente che ha effettuato l'annullamento della rettifica
	 * @return la lista di notifiche da inviare
	 * @throws MyAccountNoResultException
	 * @throws EJBException
	 */
	public Set<MsgMessaggio> buildNotificationsRettificaDelete(PfPrincipal pfPrincipal) throws EJBException,
			MyCasNoResultException {
		Set<MsgMessaggio> ret = new HashSet<MsgMessaggio>();

		MsgMessaggio toProvincia = new MsgMessaggio(MessaggioUtils.NOTIFICA);
		toProvincia.setOggetto("ANNULLAMENTO Richiesta rettifica dati dell'utente " + pfPrincipal.getUsername());

		String strMsgAzienda = "L'utente<br/>" + "username: " + pfPrincipal.getUsername() + "<br/>"
				+ "ragione sociale: " + pfPrincipal.getAziendaInfo().getRagioneSociale() + "<br/>"
				+ "ha annullato la precedente richiesta di rettifica dei dati profilo.";

		msgMessaggioEJB.setDestinatario(toProvincia, pfPrincipal);
		toProvincia.setCorpo(strMsgAzienda);
		ret.add(toProvincia);
		return ret;
	}

	/**
	 * Crea le notifiche per la cancellazione di una richiesta di rettifica.
	 * 
	 * @return la lista di notifiche da inviare
	 */
	public Set<MsgMessaggio> buildNotificationsRettificaRejected(Provincia provincia, AziendaInfo aziendaInfo) {
		Set<MsgMessaggio> ret = new HashSet<MsgMessaggio>();

		String oggetto = "Richiesta rettifica dati su " + constantsSingleton.getTitle() + " respinta";
		String strMsgAzienda = "La provincia di "
				+ provincia.getDeProvincia().getDescrizione()
				+ " titolare delle informazioni "
				+ "ha respinto la tua richiesta di rettifica dei dati di profilo. Contattare eventualmente la provincia di riferimento per ulteriori informazioni.<br/>"
				+ "Saluti,<br/>" + constantsSingleton.getTitle();

		MsgMessaggio toAzienda = new MsgMessaggio(MessaggioUtils.NOTIFICA);
		toAzienda.setOggetto(oggetto);
		toAzienda.setIdPfPrincipalTo(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		toAzienda.setCorpo(strMsgAzienda);
		ret.add(toAzienda);

		return ret;
	}

	/**
	 * Crea le notifiche per il cambiamento dello stato della richiesta di abilitazione al SARE di un'azienda.
	 * 
	 * @param provincia
	 * @param aziendaInfo
	 * @return
	 */
	public MsgMessaggio buildNotificationAggiornaAbilitazioneSARE(AziendaInfo aziendaInfo, String vecchioStato,
			String nuovoStato) {
		String oggetto = "Stato dell'accreditamento SARE cambiato";
		String strMsgAzienda = "La provincia " + " ha cambiato lo stato del tuo accreditamento SARE. <br/>"
				+ " Lo stato è passato da " + vecchioStato + " a " + nuovoStato + ".<br/><br/>" + " Saluti,<br/>"
				+ constantsSingleton.getTitle();

		MsgMessaggio toAzienda = new MsgMessaggio(MessaggioUtils.NOTIFICA);
		toAzienda.setOggetto(oggetto);
		toAzienda.setIdPfPrincipalTo(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		toAzienda.setCorpo(strMsgAzienda);

		return toAzienda;
	}

	/**
	 * Crea le notifiche da inviare agli utenti di un'azienda quando la provincia ne modifica i dati
	 * 
	 * @param pfPrincipal
	 *            utente che ha effettuato la modifica dei dati
	 * @param aziendaInfo
	 *            azienda alla quale sono stati modificati i dati
	 * @return la lista di notifiche da inviare
	 */
	public Set<MsgMessaggio> buildNotificationsAziendaInfoModified(Provincia provincia, AziendaInfo aziendaInfo) {
		Set<MsgMessaggio> ret = new HashSet<MsgMessaggio>();

		String oggetto = "Sono stati modificati i dati del tuo profilo su " + constantsSingleton.getTitle();

		String strMsgAzienda = "La provincia di " + provincia.getDeProvincia().getDescrizione()
				+ " titolare delle informazioni "
				+ "ha modificato i tuoi dati di profilo. Si prega di verificare che le modifiche siano corrette.<br/>"
				+ "Saluti,<br/>" + constantsSingleton.getTitle();

		MsgMessaggio toAzienda = new MsgMessaggio(MessaggioUtils.NOTIFICA);
		toAzienda.setOggetto(oggetto);
		toAzienda.setIdPfPrincipalTo(aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		toAzienda.setCorpo(strMsgAzienda);
		ret.add(toAzienda);

		return ret;
	}

	public Set<MsgMessaggio> buildNotificationAnnullamentoFirmaMyStage(PfPrincipal pf, String strOggetto) {
		Set<MsgMessaggio> ret = new HashSet<MsgMessaggio>();
		MsgMessaggio msgDTO = new MsgMessaggio(MessaggioUtils.NOTIFICA);

		String messaggio = "A seguito di una modifica dei dati sul sistema tirocini è stata annullata la tua firma al progetto formativo/convenzione: "
				+ strOggetto
				+ "<br/>"
				+ "Si prega di verificare che le modifiche siano corrette.<br/>"
				+ "Saluti,<br/>" + constantsSingleton.getTitle();

		msgDTO.setOggetto("Annullamento firma sistema Gestione tirocini: " + strOggetto);
		msgDTO.setIdPfPrincipalTo(pf.getIdPfPrincipal());
		msgDTO.setIdPfPrincipalFrom(0);
		msgDTO.setCorpo(messaggio);
		ret.add(msgDTO);

		return ret;
	}

}
