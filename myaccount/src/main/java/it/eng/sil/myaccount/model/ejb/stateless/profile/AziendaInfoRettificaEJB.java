package it.eng.sil.myaccount.model.ejb.stateless.profile;

import it.eng.sil.myaccount.model.ejb.stateless.myportal.NotificationBuilder;
import it.eng.sil.myaccount.model.ejb.stateless.sare.UtenteAziendaSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.entity.myportal.MsgMessaggio;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfoRettifica;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.manager.AbstractTabellaGestioneEJB;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class AziendaInfoRettificaEJB extends AbstractTabellaGestioneEJB<AziendaInfoRettifica, Integer> {

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	UtenteAziendaSareEJB utenteAziendaSareEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	ProvinciaEJB provinciaEJB;

	@EJB
	private EmailManager emailManager;

	@Override
	public String getFriendlyName() {
		return "Rettifica informazioni azienda";
	}

	@Override
	public Class<AziendaInfoRettifica> getEntityClass() {
		return AziendaInfoRettifica.class;
	}

	/**
	 * elimina la rettifica fatta dall'azienda
	 * 
	 * @param idAziendaInfoRettifica
	 * @throws Exception
	 */
	public void annullaRettifica(Integer idAziendaInfoRettifica, Integer idPfPrincipalMod) throws MyCasException {
		notificaAnnullaRettifica(idAziendaInfoRettifica, idPfPrincipalMod);
		remove(findById(idAziendaInfoRettifica));
	}

	/**
	 * elimina la rettifica da parte della provincia
	 * 
	 * @param idAziendaInfoRettifica
	 * @throws Exception
	 */
	public void deleteRettificaProvincia(Integer idAziendaInfoRettifica, Integer idPfPrincipalMod)
			throws MyCasException {
		notificaAnnullaRettificaProvincia(idAziendaInfoRettifica, idPfPrincipalMod);
		remove(findById(idAziendaInfoRettifica));
	}

	private void notificaAnnullaRettifica(Integer idAziendaInfoRettifica, Integer idPfPrincipalMod)
			throws MyCasException {
		PfPrincipal princ = pfPrincipalMyAccountEJB.findById(idAziendaInfoRettifica);
		Set<MsgMessaggio> n = notificationBuilder.buildNotificationsRettificaDelete(princ);
		notificationBuilder.sendNotification(n);
	}

	/**
	 * Quando notifico il rifiuto di una rettifica da parte della provincia, invio anche una mail all'azienda.
	 */
	private void notificaAnnullaRettificaProvincia(Integer idAziendaInfoRettifica, Integer idPfPrincipalMod)
			throws MyCasException {
		Provincia provincia = provinciaEJB.findByPfPrincipalId(idPfPrincipalMod);
		AziendaInfo aziendaInfo = aziendaInfoEJB.findById(idAziendaInfoRettifica);
		Set<MsgMessaggio> n = notificationBuilder.buildNotificationsRettificaRejected(provincia, aziendaInfo);
		notificationBuilder.sendNotification(n);

		String codProvinciaRiferimento = aziendaInfo.getDeProvincia() == null ? null : aziendaInfo.getDeProvincia()
				.getCodProvincia();
		emailManager.sendEmailNotificaRicevuta(aziendaInfo.getPfPrincipal().getUsername(), aziendaInfo.getPfPrincipal()
				.getEmail(), codProvinciaRiferimento);
	}

	/**
	 * approva la rettifica fatta dall'azienda viene aggiornata la tabella dell'azienda
	 * 
	 * @param idAziendaInfoRettifica
	 * @throws Exception
	 */
	public void approvaRettifica(AziendaInfo aziendaInfoModify, Integer idPfPrincipalMod,
			Boolean editSoggettoAbilitato, Boolean editAgenziaSomministrazione) throws Exception {

		saveModifyAzienda(aziendaInfoModify, idPfPrincipalMod, editSoggettoAbilitato, editAgenziaSomministrazione);

		// elimina la rettifica appena approvata
		Integer idRettifica = aziendaInfoModify.getIdPfPrincipal();
		try {
			remove(findById(idRettifica));
		} catch (MyCasNoResultException e) {
			// gestisco manualmente l'eccezione
			// non è un errore si verifica se esiste o meno il record su DB
		}

	}

	/**
	 * Questo metodo conferma le modifiche ai dati di un'azienda.
	 */
	public void saveModifyAzienda(AziendaInfo aziendaInfoModify, Integer idPfPrincipalMod,
			Boolean editSoggettoAbilitato, Boolean editAgenziaSomministrazione) throws Exception {
		PfPrincipal principalAzienda = pfPrincipalMyAccountEJB.updateAzienda(aziendaInfoModify.getPfPrincipal());

		// Se il SARE è attivo e l'azienda è collegata al SARE, per prima cosa chiamo il webservice per modificare i
		// dati là.
		AziendaInfo aziendaInfo = aziendaInfoEJB.populateAziendaInfo(aziendaInfoModify,
				aziendaInfoModify.getPfPrincipal(), true, editSoggettoAbilitato, editAgenziaSomministrazione);
		if (!constantsSingleton.isSareDisabled() && aziendaInfo.getPfPrincipal().getFlagAbilitatoSare()) {
			String esitoWs = utenteAziendaSareEJB.modificaUtenteSare(principalAzienda, aziendaInfo,
					editSoggettoAbilitato, editAgenziaSomministrazione);
			log.info("SARE: registrazione azienda " + aziendaInfoModify.getPfPrincipal().getUsername() + " con esito="
					+ esitoWs);
		}

		// Se la chiamata al webservice SARE è andata a buon fine, confermo le modifiche e mando notifica e mail.
		notificaApprovaRettifica(aziendaInfo, idPfPrincipalMod);

		String codProvinciaRiferimento = aziendaInfo.getDeProvincia() == null ? null : aziendaInfo.getDeProvincia()
				.getCodProvincia();
		emailManager.sendEmailNotificaRicevuta(aziendaInfo.getPfPrincipal().getUsername(), aziendaInfo.getPfPrincipal()
				.getEmail(), codProvinciaRiferimento);

		// Faccio la merge dei dati dell'azienda.
		aziendaInfoEJB.merge(idPfPrincipalMod, aziendaInfo);
	}

	/**
	 * Invio una notifica su MyPortal per informare l'utente che la rettifica è stata approvata.
	 */
	private void notificaApprovaRettifica(AziendaInfo aziendaInfo, Integer idPfPrincipalMod)
			throws MyCasNoResultException, Exception {
		// idPfPrincipalMod == ID PROVINCIA
		Provincia prov = provinciaEJB.findByPfPrincipalId(idPfPrincipalMod);
		Set<MsgMessaggio> n = notificationBuilder.buildNotificationsAziendaInfoModified(prov, aziendaInfo);
		notificationBuilder.sendNotification(n);
	}

	/**
	 * copia i dati dalla rettifica all'azienda prima di salvarli su DB
	 * 
	 * @param aziendaInfo
	 * @param aziendaInfoRettifica
	 * @return
	 */
	public AziendaInfo copyData(AziendaInfo aziendaInfo, AziendaInfoRettifica aziendaInfoRettifica) {
		if (aziendaInfoRettifica.getRagioneSociale() != null) {
			aziendaInfo.setRagioneSociale(aziendaInfoRettifica.getRagioneSociale());
		}
		if (aziendaInfoRettifica.getCodiceFiscale() != null) {
			aziendaInfo.setCodiceFiscale(aziendaInfoRettifica.getCodiceFiscale());
		}
		// sede operativa
		if (aziendaInfoRettifica.getIndirizzoSede() != null) {
			aziendaInfo.setIndirizzoSede(aziendaInfoRettifica.getIndirizzoSede());
		}
		if (aziendaInfoRettifica.getCapSede() != null) {
			aziendaInfo.setCapSede(aziendaInfoRettifica.getCapSede());
		}
		if (aziendaInfoRettifica.getDeComuneSede() != null) {
			aziendaInfo.setDeComuneSede(aziendaInfoRettifica.getDeComuneSede());
		}
		if (aziendaInfoRettifica.getTelefonoSede() != null) {
			aziendaInfo.setTelefonoSede(aziendaInfoRettifica.getTelefonoSede());
		}
		if (aziendaInfoRettifica.getFaxSede() != null) {
			aziendaInfo.setFaxSede(aziendaInfoRettifica.getFaxSede());
		}

		if (aziendaInfoRettifica.getNomeRic() != null) {
			aziendaInfo.setNomeRic(aziendaInfoRettifica.getNomeRic());
		}
		if (aziendaInfoRettifica.getCognomeRic() != null) {
			aziendaInfo.setCognomeRic(aziendaInfoRettifica.getCognomeRic());
		}
		if (aziendaInfoRettifica.getDtDataNascitaRic() != null) {
			aziendaInfo.setDtDataNascitaRic(aziendaInfoRettifica.getDtDataNascitaRic());
		}
		if (aziendaInfoRettifica.getDeComuneNascitaRic() != null) {
			aziendaInfo.setDeComuneNascitaRic(aziendaInfoRettifica.getDeComuneNascitaRic());
		}
		if (aziendaInfoRettifica.getEmailRic() != null) {
			// aziendaInfo.setEmailRic(aziendaInfoRettifica.getEmailRic());
			aziendaInfo.getPfPrincipal().setEmail(aziendaInfoRettifica.getEmailRic());
		}
		if (aziendaInfoRettifica.getDomanda() != null) {
			aziendaInfo.getPfPrincipal().setDomanda(aziendaInfoRettifica.getDomanda());
		}
		if (aziendaInfoRettifica.getRisposta() != null) {
			aziendaInfo.getPfPrincipal().setRisposta(aziendaInfoRettifica.getRisposta());
		}
		if (aziendaInfoRettifica.getMittenteSare() != null) {
			aziendaInfo.setMittenteSare(aziendaInfoRettifica.getMittenteSare());
		}
		if (aziendaInfoRettifica.getReferenteSare() != null) {
			aziendaInfo.setReferenteSare(aziendaInfoRettifica.getReferenteSare());
		}
		if (aziendaInfoRettifica.getTelefonoReferente() != null) {
			aziendaInfo.setTelefonoReferente(aziendaInfoRettifica.getTelefonoReferente());
		}
		if (aziendaInfoRettifica.getEmailReferente() != null) {
			aziendaInfo.setEmailReferente(aziendaInfoRettifica.getEmailReferente());
		}
		if (aziendaInfoRettifica.getIndirizzoRic() != null) {
			aziendaInfo.setIndirizzoRic(aziendaInfoRettifica.getIndirizzoRic());
		}
		if (aziendaInfoRettifica.getCapRic() != null) {
			aziendaInfo.setCapRic(aziendaInfoRettifica.getCapRic());
		}
		if (aziendaInfoRettifica.getDeComuneRichiedente() != null) {
			aziendaInfo.setDeComuneRichiedente(aziendaInfoRettifica.getDeComuneRichiedente());
		}
		if (aziendaInfoRettifica.getDeTipoAbilitato() != null) {
			aziendaInfo.setDeTipoAbilitato(aziendaInfoRettifica.getDeTipoAbilitato());
		}
		if (aziendaInfoRettifica.getDeTipoDelegato() != null) {
			aziendaInfo.setDeTipoDelegato(aziendaInfoRettifica.getDeTipoDelegato());
		}
		if (aziendaInfoRettifica.getSwCreazioneCo() != null) {
			aziendaInfo.setSwCreazioneCo(aziendaInfoRettifica.getSwCreazioneCo());
		}
		if (aziendaInfoRettifica.getPartitaIva() != null) {
			aziendaInfo.setPartitaIva(aziendaInfoRettifica.getPartitaIva());
		}
		if (aziendaInfoRettifica.getIndirizzoSedeLegale() != null) {
			// sede legale
			aziendaInfo.setIndirizzoSedeLegale(aziendaInfoRettifica.getIndirizzoSedeLegale());
		}
		if (aziendaInfoRettifica.getCapSedeLegale() != null) {
			aziendaInfo.setCapSedeLegale(aziendaInfoRettifica.getCapSedeLegale());
		}
		if (aziendaInfoRettifica.getDeComuneSedeLegale() != null) {
			aziendaInfo.setDeComuneSedeLegale(aziendaInfoRettifica.getDeComuneSedeLegale());
		}
		if (aziendaInfoRettifica.getTelefonoSedeLegale() != null) {
			aziendaInfo.setTelefonoSedeLegale(aziendaInfoRettifica.getTelefonoSedeLegale());
		}
		if (aziendaInfoRettifica.getFaxSedeLegale() != null) {
			aziendaInfo.setFaxSedeLegale(aziendaInfoRettifica.getFaxSedeLegale());
		}

		if (aziendaInfoRettifica.getFlagAgenziaEstera() != null) {
			// agenzia
			aziendaInfo.setFlagAgenziaEstera(aziendaInfoRettifica.getFlagAgenziaEstera());
		}
		if (aziendaInfoRettifica.getIscrProvvedNumero() != null) {
			aziendaInfo.setIscrProvvedNumero(aziendaInfoRettifica.getIscrProvvedNumero());
		}
		if (aziendaInfoRettifica.getDtIscrProvvedData() != null) {
			aziendaInfo.setDtIscrProvvedData(aziendaInfoRettifica.getDtIscrProvvedData());
		}
		if (aziendaInfoRettifica.getDeComuneIscrizione() != null) {
			aziendaInfo.setDeComuneIscrizione(aziendaInfoRettifica.getDeComuneIscrizione());
		}
		if (aziendaInfoRettifica.getIscrNumero() != null) {
			aziendaInfo.setIscrNumero(aziendaInfoRettifica.getIscrNumero());
		}
		if (aziendaInfoRettifica.getDtIscrData() != null) {
			aziendaInfo.setDtIscrData(aziendaInfoRettifica.getDtIscrData());
		}

		if (aziendaInfoRettifica.getIscrOrdine() != null) {
			// soggetto abilitato
			aziendaInfo.setIscrOrdine(aziendaInfoRettifica.getIscrOrdine());
		}
		if (aziendaInfoRettifica.getDeComuneIscrizione() != null) {
			aziendaInfo.setDeComuneIscrizione(aziendaInfoRettifica.getDeComuneIscrizione());
		}
		if (aziendaInfoRettifica.getIscrNumero() != null) {
			aziendaInfo.setIscrNumero(aziendaInfoRettifica.getIscrNumero());
		}
		if (aziendaInfoRettifica.getDtIscrData() != null) {
			aziendaInfo.setDtIscrData(aziendaInfoRettifica.getDtIscrData());
		}

		return aziendaInfo;
	}
}
