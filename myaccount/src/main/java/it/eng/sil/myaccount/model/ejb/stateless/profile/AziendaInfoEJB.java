package it.eng.sil.myaccount.model.ejb.stateless.profile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeAutorizzazioneSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeTipoUtenteSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.myportal.NotificationBuilder;
import it.eng.sil.myaccount.model.ejb.stateless.sare.UtenteAziendaSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.EmailManager;
import it.eng.sil.myaccount.model.entity.myportal.MsgMessaggio;
import it.eng.sil.myaccount.model.exceptions.MyAccountException;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.model.utils.PortletsUtils;
import it.eng.sil.myaccount.utils.EqualsUtils;
import it.eng.sil.myaccount.utils.StringUtils;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia_;
import it.eng.sil.mycas.model.entity.decodifiche.DeRegione;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeAutorizzazioneSare;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeTipoUtenteSare;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfoRettifica;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo_;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal_;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeRegioneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;
import it.eng.sil.mycas.utils.ConstantsCommons;

@Stateless
public class AziendaInfoEJB extends AbstractTabellaProfiloEJB<AziendaInfo, Integer> {

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	DeComuneEJB deComuneEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	@EJB
	DeRegioneEJB deRegioneEJB;

	@EJB
	DeSistemaEJB deSistemaEJB;

	@EJB
	DeCittadinanzaEJB deCittadinanzaEJB;

	@EJB
	DeTitoloSoggiornoEJB deTitoloSoggiornoEJB;

	@EJB
	DeTipoUtenteSareEJB deTipoUtenteSareEJB;

	@EJB
	DeAutorizzazioneSareEJB deAutorizzazioneSareEJB;

	@EJB
	DeTipoAbilitatoEJB deTipoAbilitatoEJB;

	@EJB
	DeTipoDelegatoEJB deTipoDelegatoEJB;

	@EJB
	GpDeTipoGruppoEJB gpDeTipoGruppoEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	@EJB
	UtenteAziendaSareEJB utenteAziendaSareEJB;

	@EJB
	AziendaInfoRettificaEJB aziendaInfoRettificaEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	EmailManager emailManager;

	@Override
	public String getFriendlyName() {
		return "Info azienda";
	}

	@Override
	public Class<AziendaInfo> getEntityClass() {
		return AziendaInfo.class;
	}

	public AziendaInfo loadUser(Integer idPfPrincipal) {
		try {
			AziendaInfo aziendaInfo = findById(idPfPrincipal);

			if (aziendaInfo.getDeComuneSede() != null)
				aziendaInfo.setDeComuneSede(deComuneEJB.findById(aziendaInfo.getDeComuneSede().getCodCom()));

			if (aziendaInfo.getDeComuneNascitaRic() != null)
				aziendaInfo
						.setDeComuneNascitaRic(deComuneEJB.findById(aziendaInfo.getDeComuneNascitaRic().getCodCom()));

			if (aziendaInfo.getDeComuneRichiedente() != null)
				aziendaInfo
						.setDeComuneRichiedente(deComuneEJB.findById(aziendaInfo.getDeComuneRichiedente().getCodCom()));

			if (aziendaInfo.getDeProvincia() != null)
				aziendaInfo.setDeProvincia(deProvinciaEJB.findById(aziendaInfo.getDeProvincia().getCodProvincia()));

			if (aziendaInfo.getDeTipoAbilitato() != null)
				aziendaInfo.setDeTipoAbilitato(
						deTipoAbilitatoEJB.findById(aziendaInfo.getDeTipoAbilitato().getCodTipoAbilitato()));

			if (aziendaInfo.getDeTipoDelegato() != null)
				aziendaInfo.setDeTipoDelegato(
						deTipoDelegatoEJB.findById(aziendaInfo.getDeTipoDelegato().getCodTipoDelegato()));

			if (aziendaInfo.getDeComuneSedeLegale() != null)
				aziendaInfo
						.setDeComuneSedeLegale(deComuneEJB.findById(aziendaInfo.getDeComuneSedeLegale().getCodCom()));

			if (aziendaInfo.getDeComuneIscrizione() != null)
				aziendaInfo
						.setDeComuneIscrizione(deComuneEJB.findById(aziendaInfo.getDeComuneIscrizione().getCodCom()));

			return aziendaInfo;

		} catch (MyCasNoResultException e) {
			throw new EJBException("Dati sporchi:" + e.getMessage());
		}
	}

	public AziendaInfo registerFromPortingSare(PfPrincipal utente, AziendaInfo aziendaInfo,
			Integer idPrincipalOpPorting) throws MyCasException {
		// Controllo che la email non sia duplicata
		boolean mailDuplicata;
		try {
			mailDuplicata = !pfPrincipalMyAccountEJB.isUniqueEmail(utente.getEmail());
		} catch (PersistenceException pe) {
			log.error("Errore UNIQUE email su: " + utente.getEmail());
			throw pe;
		}
		// Creo la riga sulla tabella PfPrincipal.
		utente = pfPrincipalMyAccountEJB.registerAziendaFromPorting(utente, "AZIENDE");

		// Se la mail era duplicata, faccio una merge al volo per aggiungere il prefisso.
		if (mailDuplicata) {
			utente.setEmail(utente.getIdPfPrincipal().toString() + "_" + utente.getEmail());
			utente = pfPrincipalMyAccountEJB.merge(idPrincipalOpPorting, utente);
		}

		// Creo la riga sulla AziendaInfo
		aziendaInfo.setPfPrincipal(utente);
		aziendaInfo.setIdPfPrincipal(utente.getIdPfPrincipal());
		aziendaInfo = persist(0, aziendaInfo);

		// NUOVA PROFILATURA: Assegno i ruoli di default al nuovo utente.
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.AZI);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(utente);
			gpProfilaturaEJB.persist(0, newProfilatura);
		}

		// Ruolo 'Gestione CO' per aziende SARE. dò per scontato sia solo 1 e lascio si spacchi altrimenti
		DeSistema sare = deSistemaEJB.findByEnum(DeSistemaEnum.SARE);
		GpRuolo ruoloSareAzienda = gpRuoloEJB.findByFilter(ConstantsSingleton.GpRuoloConstants.GESTIONE_CO, sare).get(0);

		// Creo un nuovo GpGruppo per l'azienda.
		GpDeTipoGruppo tipoAzi = gpDeTipoGruppoEJB.findById(GpDeTipoGruppoEnum.AZI);
		GpGruppo aziGruppo = new GpGruppo();
		aziGruppo.setDescrizione(
				"Mittenti SARE per: " + aziendaInfo.getRagioneSociale() + " (" + aziendaInfo.getPartitaIva() + ")");
		aziGruppo.setGpDeTipoGruppo(tipoAzi);
		aziGruppo.setNote("SARE: " + aziendaInfo.getPfPrincipal().getIdPfPrincipal());
		aziGruppo = gpGruppoEJB.persist(idPrincipalOpPorting, aziGruppo);

		// Creo una nuova profilatura, combinando il nuovo gruppo e il ruolo "gestione CO" del SARE.
		GpRuoloGruppo nuovoRuoloGruppo = new GpRuoloGruppo();
		nuovoRuoloGruppo.setGpGruppo(aziGruppo);
		nuovoRuoloGruppo.setGpRuolo(ruoloSareAzienda);
		nuovoRuoloGruppo.setFlgDefault(false);
		nuovoRuoloGruppo = gpRuoloGruppoEJB.persist(idPrincipalOpPorting, nuovoRuoloGruppo);

		// Assegno la profilatura appena creata all'utente.
		GpProfilatura nuovaProfilatura = new GpProfilatura();
		nuovaProfilatura.setGpRuoloGruppo(nuovoRuoloGruppo);
		nuovaProfilatura.setPfPrincipal(utente);
		nuovaProfilatura = gpProfilaturaEJB.persist(idPrincipalOpPorting, nuovaProfilatura);

		return aziendaInfo;
	}

	public AziendaInfo registerFromPannello(PfPrincipal utente, AziendaInfo aziendaInfo, String codPfTipoGruppo,
			Integer idPrincipalIns) throws MyCasException {
		// Creo la riga sulla tabella PfPrincipal.
		utente = pfPrincipalMyAccountEJB.registerAziendaFromPannello(utente, codPfTipoGruppo, true, false);

		// Creo la riga sulla AziendaInfo
		aziendaInfo.setPfPrincipal(utente);
		aziendaInfo = populateAziendaInfoInsert(aziendaInfo, utente, false, false);
		aziendaInfo = persist(0, aziendaInfo);

		// NUOVA PROFILATURA: Assegno i ruoli di default al nuovo utente.
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.AZI);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(utente);
			gpProfilaturaEJB.persist(0, newProfilatura);
		}

		return aziendaInfo;
	}

	public Integer register(PfPrincipal pfPrincipalRegister, AziendaInfo aziendaInfoRegister, boolean pwdScaduta,
			boolean isLavoroPerTeInstallato, Boolean editSoggettoAbilitato, Boolean editAgenziaSomministrazione) {

		PfPrincipal principalAzienda = null;
		try {
			// Creo il record sulla tabella PfPrincipal.
			principalAzienda = pfPrincipalMyAccountEJB.registerAzienda(pfPrincipalRegister, pwdScaduta,
					pfPrincipalRegister.getFlagAbilitatoSare());

			// Creo il record sulla tabella AziendaInfo.
			AziendaInfo aziendaInfoSave = populateAziendaInfoInsert(aziendaInfoRegister, principalAzienda,
					editSoggettoAbilitato, editAgenziaSomministrazione);
			AziendaInfo aziendaInfo = persist(0, aziendaInfoSave);

			// NUOVA PROFILATURA: Assegno i ruoli di default al nuovo utente.
			List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.AZI);
			for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
				GpProfilatura newProfilatura = new GpProfilatura();
				newProfilatura.setGpRuoloGruppo(ruoloGruppo);
				newProfilatura.setPfPrincipal(principalAzienda);
				gpProfilaturaEJB.persist(0, newProfilatura);
			}

			// Se il SARE esiste e l'azienda è abilitata SARE, chiamo un WS del SARE per registrarla anche lì.
			try {
				if (!constantsSingleton.isSareDisabled() && aziendaInfo.getPfPrincipal().getFlagAbilitatoSare()) {
					String esitoWs = utenteAziendaSareEJB.registraUtenteSare(principalAzienda, aziendaInfo,
							editSoggettoAbilitato, editAgenziaSomministrazione);
					log.info("SARE: registrazione azienda " + pfPrincipalRegister.getUsername() + " con esito="
							+ esitoWs);
				}
			} catch (Exception e) {
				log.error("Errore nella registrazione azienda su SARE: " + e.getMessage() + ".", e);
				throw new EJBException(e);
			}
		} catch (Exception e) {
			log.error("Errore nella registrazione: " + e.getMessage() + ".", e);
			throw new EJBException("Errore nella registrazione", e);
		}

		return principalAzienda.getIdPfPrincipal();
	}

	public Boolean addPortlet(Integer idPfPrincipal) throws EJBException, MyCasNoResultException {
		String MYPORTAL_PORTLETS_SERVICE_URL = constantsSingleton.getPortaleURL() + "/secure/rest/admin/addPortlets/";
		return PortletsUtils.addPortlets(idPfPrincipal, "AZIENDE", 0, MYPORTAL_PORTLETS_SERVICE_URL);
	}

	/**
	 * Se l'azienda ha accesso al SARE invio la rettifica e salvo i dati in azienda_info_rettifica; in caso contrario,
	 * aggiorno i dati in azienda_info.
	 * 
	 * @param aziendaInfoRegister
	 * @throws Exception
	 */
	public String updateProfile(AziendaInfo aziendaInfoRegister, Boolean editSoggettoAbilitato,
			Boolean editAgenziaSomministrazione) throws Exception {
		Integer idPfPrincipalRegister = aziendaInfoRegister.getIdPfPrincipal();
		PfPrincipal pfPrincipal = pfPrincipalMyAccountEJB.findById(idPfPrincipalRegister);
		Boolean checkAbilitatoSare = pfPrincipal.getFlagAbilitatoSare();

		if (checkAbilitatoSare) {
			// Se l'azienda è ablitata SARE, deve richiedere una rettifica alla provincia.
			AziendaInfo aziendaInfoSaved = findById(idPfPrincipalRegister);
			AziendaInfoRettifica aziendaInfoRettificaRegister = rettificaAzienda(aziendaInfoSaved, aziendaInfoRegister);
			AziendaInfoRettifica newAzInfoRett = null;

			boolean newRettifica = false;
			try {
				@SuppressWarnings("unused")
				AziendaInfoRettifica aziendaInfoRettifica = aziendaInfoRettificaEJB.findById(idPfPrincipalRegister);
			} catch (MyCasNoResultException e) {
				// gestisco manualmente l'eccezione
				// Non è un errore si verifica se esiste o meno il record su DB
				newRettifica = true;
			}

			if (newRettifica) {
				newAzInfoRett = aziendaInfoRettificaEJB.persist(idPfPrincipalRegister, aziendaInfoRettificaRegister);
			} else {
				newAzInfoRett = aziendaInfoRettificaEJB.merge(idPfPrincipalRegister, aziendaInfoRettificaRegister);
			}

			// Notifica alla provincia le successive rettifiche dell'azienda.
			Set<MsgMessaggio> n = notificationBuilder.buildNotifications(newAzInfoRett);
			notificationBuilder.sendNotification(n);
			return "msg.account.rettifica.sent";

		} else {
			// Se l'azienda non è abilitata SARE, può modificare subito i suoi dati.
			PfPrincipal principalAzienda = pfPrincipalMyAccountEJB.updateAzienda(aziendaInfoRegister.getPfPrincipal());
			AziendaInfo aziendaInfo = populateAziendaInfoUpdate(aziendaInfoRegister,
					aziendaInfoRegister.getPfPrincipal(), editSoggettoAbilitato, editAgenziaSomministrazione);
			merge(principalAzienda.getIdPfPrincipal(), aziendaInfo);

			// Se il SARE esiste e l'azienda ha richiesto ora l'accreditamento SARE, invio la richiesta alla provincia.
			try {
				if (!constantsSingleton.isSareDisabled() && aziendaInfo.getPfPrincipal().getFlagAbilitatoSare()) {
					String esitoWs = utenteAziendaSareEJB.registraUtenteSare(principalAzienda, aziendaInfo,
							editSoggettoAbilitato, editAgenziaSomministrazione);
					log.info("SARE: registrazione azienda " + aziendaInfoRegister.getPfPrincipal().getUsername()
							+ " con esito=" + esitoWs);
				}
			} catch (Exception e) {
				log.error("Errore nell'aggiornamento azienda su SARE", e);
				throw new EJBException(e);
			}

			return "msg.updated";
		}
	}

	private AziendaInfo populateAziendaInfoUpdate(AziendaInfo aziendaInfoRegister, PfPrincipal pfPrincipalRegister,
			Boolean editSoggettoAbilitato, Boolean editAgenziaSomministrazione) throws MyCasNoResultException {
		return populateAziendaInfo(aziendaInfoRegister, pfPrincipalRegister, true, editSoggettoAbilitato,
				editAgenziaSomministrazione);
	}

	private AziendaInfo populateAziendaInfoInsert(AziendaInfo aziendaInfoRegister, PfPrincipal pfPrincipalRegister,
			Boolean editSoggettoAbilitato, Boolean editAgenziaSomministrazione) throws MyCasNoResultException {
		return populateAziendaInfo(aziendaInfoRegister, pfPrincipalRegister, false, editSoggettoAbilitato,
				editAgenziaSomministrazione);
	}

	public AziendaInfo populateAziendaInfo(AziendaInfo aziendaInfoRegister, PfPrincipal pfPrincipalRegister,
			boolean update, Boolean editSoggettoAbilitato, Boolean editAgenziaSomministrazione)
			throws MyCasNoResultException {
		AziendaInfo aziendaInfo = null;
		if (update) {
			aziendaInfo = findById(aziendaInfoRegister.getIdPfPrincipal());
		} else {
			aziendaInfo = new AziendaInfo();
			aziendaInfo.setIdPfPrincipal(pfPrincipalRegister.getIdPfPrincipal());
			aziendaInfo.setPfPrincipal(pfPrincipalRegister);
		}

		aziendaInfo.setNomeRic(aziendaInfoRegister.getNomeRic());
		aziendaInfo.setCognomeRic(aziendaInfoRegister.getCognomeRic());
		if (aziendaInfoRegister.getDtDataNascitaRic() != null) {
			aziendaInfo.setDtDataNascitaRic(aziendaInfoRegister.getDtDataNascitaRic());
		}
		if (aziendaInfoRegister.getDeComuneNascitaRic() != null
				&& aziendaInfoRegister.getDeComuneNascitaRic().getCodCom() != null) {
			try {
				aziendaInfo.setDeComuneNascitaRic(
						deComuneEJB.findById(aziendaInfoRegister.getDeComuneNascitaRic().getCodCom()));
			} catch (MyCasNoResultException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		aziendaInfo.setIndirizzoRic(aziendaInfoRegister.getIndirizzoRic());
		aziendaInfo.setCapRic(aziendaInfoRegister.getCapRic());
		aziendaInfo.setEmailRic(pfPrincipalRegister.getEmail());

		// DATI AZIENDA
		aziendaInfo.setCodiceFiscale(aziendaInfoRegister.getCodiceFiscale().toUpperCase());
		aziendaInfo.setRagioneSociale(aziendaInfoRegister.getRagioneSociale());

		// SEDE OPERATIVA
		aziendaInfo.setIndirizzoSede(aziendaInfoRegister.getIndirizzoSede());
		aziendaInfo.setEmailSede(aziendaInfoRegister.getEmailSede());
		if (aziendaInfoRegister.getCapSede() != null) {
			aziendaInfo.setCapSede(aziendaInfoRegister.getCapSede());
		}
		if (aziendaInfoRegister.getDeComuneSede() != null
				&& aziendaInfoRegister.getDeComuneSede().getCodCom() != null) {
			aziendaInfo.setDeComuneSede(deComuneEJB.findById(aziendaInfoRegister.getDeComuneSede().getCodCom()));
		}
		if (aziendaInfoRegister.getTelefonoSede() != null) {
			aziendaInfo.setTelefonoSede(aziendaInfoRegister.getTelefonoSede());
		}
		if (aziendaInfoRegister.getFaxSede() != null) {
			aziendaInfo.setFaxSede(aziendaInfoRegister.getFaxSede());
		}
		if (aziendaInfoRegister.getTelefonoSedeLegale() != null) {
			aziendaInfo.setTelefonoSedeLegale(aziendaInfoRegister.getTelefonoSedeLegale());
		}
		if (aziendaInfoRegister.getFaxSedeLegale() != null) {
			aziendaInfo.setFaxSedeLegale(aziendaInfoRegister.getFaxSedeLegale());
		}
		aziendaInfo.setFlagAgenziaEstera(false);

		aziendaInfo.setFlagValida(aziendaInfoRegister.getFlagValida());

		// DATI ACCESSO A SARE
		if (pfPrincipalRegister.getFlagAbilitatoSare()) {
			if (update && aziendaInfoRegister.getDeTipoUtenteSare() != null) {
				aziendaInfo.setDeTipoUtenteSare(
						deTipoUtenteSareEJB.findById(aziendaInfoRegister.getDeTipoUtenteSare().getCodTipoUtenteSare()));
				// aziendaInfo.setRequestStatus(reqStatusIn);
				aziendaInfo.setDeAutorizzazioneSare(deAutorizzazioneSareEJB
						.findById(aziendaInfoRegister.getDeAutorizzazioneSare().getCodAutorizzazioneSare()));
			} else {
				aziendaInfo.setDeTipoUtenteSare(deTipoUtenteSareEJB.findById("R"));
				// aziendaInfo.setRequestStatus(reqStatusIn);
				aziendaInfo.setDeAutorizzazioneSare(deAutorizzazioneSareEJB.findById("0"));
			}

			if (update) {
				aziendaInfo.setIdRichiesta(aziendaInfoRegister.getIdPfPrincipal());
			} else {
				aziendaInfo.setIdRichiesta(pfPrincipalRegister.getIdPfPrincipal());
			}

			aziendaInfo.setDeProvincia(deProvinciaEJB.findById(aziendaInfoRegister.getDeProvincia().getCodProvincia()));
			aziendaInfo.setMittenteSare(aziendaInfoRegister.getMittenteSare());
			aziendaInfo.setSwCreazioneCo(aziendaInfoRegister.getSwCreazioneCo());
			if (aziendaInfoRegister.getDeComuneRichiedente() != null
					&& aziendaInfoRegister.getDeComuneRichiedente().getCodCom() != null) {
				aziendaInfo.setDeComuneRichiedente(
						deComuneEJB.findById(aziendaInfoRegister.getDeComuneRichiedente().getCodCom()));
			}
			if (aziendaInfoRegister.getDeTipoAbilitato() != null
					&& aziendaInfoRegister.getDeTipoAbilitato().getCodTipoAbilitato() != null) {
				aziendaInfo.setDeTipoAbilitato(
						deTipoAbilitatoEJB.findById(aziendaInfoRegister.getDeTipoAbilitato().getCodTipoAbilitato()));
			}

			// if (editTipoDelegato) {
			if (aziendaInfoRegister.getDeTipoDelegato() != null
					&& aziendaInfoRegister.getDeTipoDelegato().getCodTipoDelegato() != null) {
				aziendaInfo.setDeTipoDelegato(
						deTipoDelegatoEJB.findById(aziendaInfoRegister.getDeTipoDelegato().getCodTipoDelegato()));
			} else {
				aziendaInfo.setDeTipoDelegato(null);
			}

			// Ulteriori dati aziendali
			aziendaInfo.setPartitaIva(aziendaInfoRegister.getPartitaIva());
			aziendaInfo.setReferenteSare(aziendaInfoRegister.getReferenteSare());
			aziendaInfo.setTelefonoReferente(aziendaInfoRegister.getTelefonoReferente());
			aziendaInfo.setEmailReferente(aziendaInfoRegister.getEmailReferente());

			// SEDE LEGALE
			aziendaInfo.setIndirizzoSedeLegale(aziendaInfoRegister.getIndirizzoSedeLegale());
			aziendaInfo.setCapSedeLegale(aziendaInfoRegister.getCapSedeLegale());
			if (aziendaInfoRegister.getDeComuneSedeLegale() != null
					&& aziendaInfoRegister.getDeComuneSedeLegale().getCodCom() != null) {
				aziendaInfo.setDeComuneSedeLegale(
						deComuneEJB.findById(aziendaInfoRegister.getDeComuneSedeLegale().getCodCom()));
			}
			aziendaInfo.setTelefonoSedeLegale(aziendaInfoRegister.getTelefonoSedeLegale());
			aziendaInfo.setFaxSedeLegale(aziendaInfoRegister.getFaxSedeLegale());

			// AGENZIA DI SOMMINISTRAZIONE
			if (editAgenziaSomministrazione) {
				aziendaInfo.setFlagAgenziaEstera(aziendaInfoRegister.getFlagAgenziaEstera());
				aziendaInfo.setIscrProvvedNumero(aziendaInfoRegister.getIscrNumero());
				aziendaInfo.setDtIscrProvvedData(aziendaInfoRegister.getDtIscrProvvedData());
				if (aziendaInfoRegister.getDeComuneIscrizione() != null
						&& aziendaInfoRegister.getDeComuneIscrizione().getCodCom() != null) {
					aziendaInfo.setDeComuneIscrizione(
							deComuneEJB.findById(aziendaInfoRegister.getDeComuneIscrizione().getCodCom()));
				}
				aziendaInfo.setIscrNumero(aziendaInfoRegister.getIscrNumero());
				aziendaInfo.setDtIscrData(aziendaInfoRegister.getDtIscrData());
			}
			// SOGGETTO ABILITATO
			else if (editSoggettoAbilitato) {
				aziendaInfo.setIscrOrdine(aziendaInfoRegister.getIscrOrdine());
				if (aziendaInfoRegister.getDeComuneIscrizione() != null
						&& aziendaInfoRegister.getDeComuneIscrizione().getCodCom() != null) {
					aziendaInfo.setDeComuneIscrizione(
							deComuneEJB.findById(aziendaInfoRegister.getDeComuneIscrizione().getCodCom()));
				}

				aziendaInfo.setIscrNumero(aziendaInfoRegister.getIscrNumero());
				aziendaInfo.setDtIscrData(aziendaInfoRegister.getDtIscrData());
			}
		}
		return aziendaInfo;
	}

	public AziendaInfoRettifica rettificaAzienda(AziendaInfo aziendaInfoSaved, AziendaInfo aziendaInfoModify)
			throws MyCasNoResultException {
		AziendaInfoRettifica aziendaInfoRettificaRegister = new AziendaInfoRettifica();

		aziendaInfoRettificaRegister.setPfPrincipal(aziendaInfoSaved.getPfPrincipal());
		aziendaInfoRettificaRegister.setIdPfPrincipal(aziendaInfoSaved.getIdPfPrincipal());
		aziendaInfoRettificaRegister.setIdRichiesta(aziendaInfoSaved.getIdPfPrincipal());

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getPfPrincipal().getDomanda(),
				aziendaInfoModify.getPfPrincipal().getDomanda())) {
			aziendaInfoRettificaRegister.setDomanda(aziendaInfoModify.getPfPrincipal().getDomanda());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getPfPrincipal().getRisposta(),
				aziendaInfoModify.getPfPrincipal().getRisposta())) {
			aziendaInfoRettificaRegister.setRisposta(aziendaInfoModify.getPfPrincipal().getRisposta());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getPfPrincipal().getEmail(),
				aziendaInfoModify.getPfPrincipal().getEmail())) {
			aziendaInfoRettificaRegister.setEmailRic(aziendaInfoModify.getPfPrincipal().getEmail());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getEmailRic(), aziendaInfoModify.getEmailRic())) {
			aziendaInfoRettificaRegister.setEmailRic(aziendaInfoModify.getEmailRic());
		}

		aziendaInfoRettificaRegister.setIdPrincipalMod(aziendaInfoSaved.getIdPfPrincipal());
		aziendaInfoRettificaRegister.setIdPrincipalIns(aziendaInfoSaved.getIdPfPrincipal());
		aziendaInfoRettificaRegister.setDtmIns(new Date());
		aziendaInfoRettificaRegister.setDtmMod(new Date());

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCodiceFiscale(), aziendaInfoModify.getCodiceFiscale())) {
			aziendaInfoRettificaRegister.setCodiceFiscale(aziendaInfoModify.getCodiceFiscale());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getRagioneSociale(), aziendaInfoModify.getRagioneSociale())) {
			aziendaInfoRettificaRegister.setRagioneSociale(aziendaInfoModify.getRagioneSociale());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getPartitaIva(), aziendaInfoModify.getPartitaIva())) {
			aziendaInfoRettificaRegister.setPartitaIva(aziendaInfoModify.getPartitaIva());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getIndirizzoSedeLegale(),
				aziendaInfoModify.getIndirizzoSedeLegale())) {
			aziendaInfoRettificaRegister.setIndirizzoSedeLegale(aziendaInfoModify.getIndirizzoSedeLegale());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCapSedeLegale(), aziendaInfoModify.getCapSedeLegale())) {
			aziendaInfoRettificaRegister.setCapSedeLegale(aziendaInfoModify.getCapSedeLegale());
		}

		if (aziendaInfoModify.getDeComuneSedeLegale() != null) {
			if (aziendaInfoSaved.getDeComuneSedeLegale() != null) {
				if (!aziendaInfoSaved.getDeComuneSedeLegale().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneSedeLegale().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneSedeLegale(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneSedeLegale().getCodCom()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeComuneSedeLegale(
						deComuneEJB.findById(aziendaInfoModify.getDeComuneSedeLegale().getCodCom()));
			}
		} else {
			if (aziendaInfoSaved.getDeComuneSedeLegale() != null) {
				if (!aziendaInfoSaved.getDeComuneSedeLegale().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneSedeLegale().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneSedeLegale(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneSedeLegale().getCodCom()));
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getTelefonoSedeLegale(),
				aziendaInfoModify.getTelefonoSedeLegale())) {
			aziendaInfoRettificaRegister.setTelefonoSedeLegale(aziendaInfoModify.getTelefonoSedeLegale());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getFaxSedeLegale(), aziendaInfoModify.getFaxSedeLegale())) {
			aziendaInfoRettificaRegister.setFaxSedeLegale(aziendaInfoModify.getFaxSedeLegale());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCellSedeLegale(), aziendaInfoModify.getCellSedeLegale())) {
			aziendaInfoRettificaRegister.setCellSedeLegale(aziendaInfoModify.getCellSedeLegale());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getNomeRic(), aziendaInfoModify.getNomeRic())) {
			aziendaInfoRettificaRegister.setNomeRic(aziendaInfoModify.getNomeRic());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCognomeRic(), aziendaInfoModify.getCognomeRic())) {
			aziendaInfoRettificaRegister.setCognomeRic(aziendaInfoModify.getCognomeRic());
		}
		if (aziendaInfoSaved.getDtDataNascitaRic() != null && aziendaInfoModify.getDtDataNascitaRic() != null) {
			if (!EqualsUtils.areEqual(
					it.eng.sil.myaccount.utils.StringUtils.formatDate(aziendaInfoSaved.getDtDataNascitaRic(),
							"dd/MM/yyyy"),
					it.eng.sil.myaccount.utils.StringUtils.formatDate(aziendaInfoModify.getDtDataNascitaRic(),
							"dd/MM/yyyy"))) {
				aziendaInfoRettificaRegister.setDtDataNascitaRic(aziendaInfoModify.getDtDataNascitaRic());
			}
		} else {
			aziendaInfoRettificaRegister.setDtDataNascitaRic(aziendaInfoModify.getDtDataNascitaRic());
		}

		if (aziendaInfoModify.getDeComuneNascitaRic() != null) {
			if (aziendaInfoSaved.getDeComuneNascitaRic() != null) {
				if (!aziendaInfoSaved.getDeComuneNascitaRic().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneNascitaRic().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneNascitaRic(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneNascitaRic().getCodCom()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeComuneNascitaRic(
						deComuneEJB.findById(aziendaInfoModify.getDeComuneNascitaRic().getCodCom()));
			}
		} else {
			if (aziendaInfoSaved.getDeComuneNascitaRic() != null) {
				if (!aziendaInfoSaved.getDeComuneNascitaRic().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneNascitaRic().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneNascitaRic(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneNascitaRic().getCodCom()));
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getIndirizzoRic(), aziendaInfoModify.getIndirizzoRic())) {
			aziendaInfoRettificaRegister.setIndirizzoRic(aziendaInfoModify.getIndirizzoRic());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCapRic(), aziendaInfoModify.getCapRic())) {
			aziendaInfoRettificaRegister.setCapRic(aziendaInfoModify.getCapRic());
		}

		if (aziendaInfoModify.getDeComuneRichiedente() != null) {
			if (aziendaInfoSaved.getDeComuneRichiedente() != null) {
				if (!aziendaInfoSaved.getDeComuneRichiedente().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneRichiedente().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneRichiedente(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneRichiedente().getCodCom()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeComuneRichiedente(
						deComuneEJB.findById(aziendaInfoModify.getDeComuneRichiedente().getCodCom()));
			}
		} else {
			if (aziendaInfoSaved.getDeComuneRichiedente() != null) {
				if (!aziendaInfoSaved.getDeComuneRichiedente().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneRichiedente().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneRichiedente(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneRichiedente().getCodCom()));
				}
			}
		}

		if (aziendaInfoModify.getDeProvincia() != null) {
			if (aziendaInfoSaved.getDeProvincia() != null) {
				if (!aziendaInfoSaved.getDeProvincia().getCodProvincia()
						.equalsIgnoreCase(aziendaInfoModify.getDeProvincia().getCodProvincia())) {
					aziendaInfoRettificaRegister.setDeProvincia(
							deProvinciaEJB.findById(aziendaInfoModify.getDeProvincia().getCodProvincia()));
				}
			} else {
				aziendaInfoRettificaRegister
						.setDeProvincia(deProvinciaEJB.findById(aziendaInfoModify.getDeProvincia().getCodProvincia()));
			}
		} else {
			if (aziendaInfoSaved.getDeProvincia() != null) {
				if (!aziendaInfoSaved.getDeProvincia().getCodProvincia()
						.equalsIgnoreCase(aziendaInfoModify.getDeProvincia().getCodProvincia())) {
					aziendaInfoRettificaRegister.setDeProvincia(
							deProvinciaEJB.findById(aziendaInfoModify.getDeProvincia().getCodProvincia()));
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getMittenteSare(), aziendaInfoModify.getMittenteSare())) {
			aziendaInfoRettificaRegister.setMittenteSare(aziendaInfoModify.getMittenteSare());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getReferenteSare(), aziendaInfoModify.getReferenteSare())) {
			aziendaInfoRettificaRegister.setReferenteSare(aziendaInfoModify.getReferenteSare());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getTelefonoReferente(), aziendaInfoModify.getTelefonoReferente())) {
			aziendaInfoRettificaRegister.setTelefonoReferente(aziendaInfoModify.getTelefonoReferente());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getEmailReferente(), aziendaInfoModify.getEmailReferente())) {
			aziendaInfoRettificaRegister.setEmailReferente(aziendaInfoModify.getEmailReferente());
		}

		if (aziendaInfoModify.getDeTipoAbilitato() != null) {
			if (aziendaInfoSaved.getDeTipoAbilitato() != null) {
				if (!aziendaInfoSaved.getDeTipoAbilitato().getCodTipoAbilitato()
						.equalsIgnoreCase(aziendaInfoModify.getDeTipoAbilitato().getCodTipoAbilitato())) {
					aziendaInfoRettificaRegister.setDeTipoAbilitato(
							deTipoAbilitatoEJB.findById(aziendaInfoModify.getDeTipoAbilitato().getCodTipoAbilitato()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeTipoAbilitato(
						deTipoAbilitatoEJB.findById(aziendaInfoModify.getDeTipoAbilitato().getCodTipoAbilitato()));
			}
		} else {
			if (aziendaInfoSaved.getDeTipoAbilitato() != null) {
				if (!aziendaInfoSaved.getDeTipoAbilitato().getCodTipoAbilitato()
						.equalsIgnoreCase(aziendaInfoModify.getDeTipoAbilitato().getCodTipoAbilitato())) {
					aziendaInfoRettificaRegister.setDeTipoAbilitato(
							deTipoAbilitatoEJB.findById(aziendaInfoModify.getDeTipoAbilitato().getCodTipoAbilitato()));
				}
			}
		}

		if (aziendaInfoModify.getDeTipoDelegato() != null) {
			if (aziendaInfoSaved.getDeTipoDelegato() != null) {
				if (!aziendaInfoSaved.getDeTipoDelegato().getCodTipoDelegato()
						.equalsIgnoreCase(aziendaInfoModify.getDeTipoDelegato().getCodTipoDelegato())) {
					aziendaInfoRettificaRegister.setDeTipoDelegato(
							deTipoDelegatoEJB.findById(aziendaInfoModify.getDeTipoDelegato().getCodTipoDelegato()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeTipoDelegato(
						deTipoDelegatoEJB.findById(aziendaInfoModify.getDeTipoDelegato().getCodTipoDelegato()));
			}
		} else {
			if (aziendaInfoSaved.getDeTipoDelegato() != null) {
				if (aziendaInfoModify.getDeTipoDelegato() != null) {
					if (!aziendaInfoSaved.getDeTipoDelegato().getCodTipoDelegato()
							.equalsIgnoreCase(aziendaInfoModify.getDeTipoDelegato().getCodTipoDelegato())) {
						aziendaInfoRettificaRegister.setDeTipoDelegato(
								deTipoDelegatoEJB.findById(aziendaInfoModify.getDeTipoDelegato().getCodTipoDelegato()));
					}
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getIndirizzoSede(), aziendaInfoModify.getIndirizzoSede())) {
			aziendaInfoRettificaRegister.setIndirizzoSede(aziendaInfoModify.getIndirizzoSede());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCapSede(), aziendaInfoModify.getCapSede())) {
			aziendaInfoRettificaRegister.setCapSede(aziendaInfoModify.getCapSede());
		}

		if (aziendaInfoModify.getDeComuneSede() != null) {
			if (aziendaInfoSaved.getDeComuneSede() != null) {
				if (!aziendaInfoSaved.getDeComuneSede().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneSede().getCodCom())) {
					aziendaInfoRettificaRegister
							.setDeComuneSede(deComuneEJB.findById(aziendaInfoModify.getDeComuneSede().getCodCom()));
				}
			} else {
				aziendaInfoRettificaRegister
						.setDeComuneSede(deComuneEJB.findById(aziendaInfoModify.getDeComuneSede().getCodCom()));
			}
		} else {
			if (aziendaInfoSaved.getDeComuneSede() != null) {
				if (!aziendaInfoSaved.getDeComuneSede().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneSede().getCodCom())) {
					aziendaInfoRettificaRegister
							.setDeComuneSede(deComuneEJB.findById(aziendaInfoModify.getDeComuneSede().getCodCom()));
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getEmailSede(), aziendaInfoModify.getEmailSede())) {
			aziendaInfoRettificaRegister.setEmailSede(aziendaInfoModify.getEmailSede());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getFaxSede(), aziendaInfoModify.getFaxSede())) {
			aziendaInfoRettificaRegister.setFaxSede(aziendaInfoModify.getFaxSede());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getTelefonoSede(), aziendaInfoModify.getTelefonoSede())) {
			aziendaInfoRettificaRegister.setTelefonoSede(aziendaInfoModify.getTelefonoSede());
		}

		if (aziendaInfoModify.getDeAutorizzazioneSare() != null) {
			if (aziendaInfoSaved.getDeAutorizzazioneSare() != null) {
				if (!aziendaInfoSaved.getDeAutorizzazioneSare().getCodAutorizzazioneSare()
						.equalsIgnoreCase(aziendaInfoModify.getDeAutorizzazioneSare().getCodAutorizzazioneSare())) {
					aziendaInfoRettificaRegister.setDeAutorizzazioneSare(deAutorizzazioneSareEJB
							.findById(aziendaInfoModify.getDeAutorizzazioneSare().getCodAutorizzazioneSare()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeAutorizzazioneSare(deAutorizzazioneSareEJB
						.findById(aziendaInfoModify.getDeAutorizzazioneSare().getCodAutorizzazioneSare()));
			}
		} else {
			if (aziendaInfoSaved.getDeAutorizzazioneSare() != null) {
				if (!aziendaInfoSaved.getDeAutorizzazioneSare().getCodAutorizzazioneSare()
						.equalsIgnoreCase(aziendaInfoModify.getDeAutorizzazioneSare().getCodAutorizzazioneSare())) {
					aziendaInfoRettificaRegister.setDeAutorizzazioneSare(deAutorizzazioneSareEJB
							.findById(aziendaInfoModify.getDeAutorizzazioneSare().getCodAutorizzazioneSare()));
				}
			}
		}

		if (aziendaInfoModify.getDeTipoUtenteSare() != null) {
			if (aziendaInfoSaved.getDeTipoUtenteSare() != null) {
				if (!aziendaInfoSaved.getDeTipoUtenteSare().getCodTipoUtenteSare()
						.equalsIgnoreCase(aziendaInfoModify.getDeTipoUtenteSare().getCodTipoUtenteSare())) {
					aziendaInfoRettificaRegister.setDeTipoUtenteSare(deTipoUtenteSareEJB
							.findById(aziendaInfoModify.getDeTipoUtenteSare().getCodTipoUtenteSare()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeTipoUtenteSare(
						deTipoUtenteSareEJB.findById(aziendaInfoModify.getDeTipoUtenteSare().getCodTipoUtenteSare()));
			}
		} else {
			if (aziendaInfoSaved.getDeTipoUtenteSare() != null) {
				if (!aziendaInfoSaved.getDeTipoUtenteSare().getCodTipoUtenteSare()
						.equalsIgnoreCase(aziendaInfoModify.getDeTipoUtenteSare().getCodTipoUtenteSare())) {
					aziendaInfoRettificaRegister.setDeTipoUtenteSare(deTipoUtenteSareEJB
							.findById(aziendaInfoModify.getDeTipoUtenteSare().getCodTipoUtenteSare()));
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getFlagAgenziaEstera(), aziendaInfoModify.getFlagAgenziaEstera())) {
			aziendaInfoRettificaRegister.setFlagAgenziaEstera(aziendaInfoModify.getFlagAgenziaEstera());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getFlagValida(), aziendaInfoModify.getFlagValida())) {
			aziendaInfoRettificaRegister.setFlagValida(aziendaInfoModify.getFlagValida());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getIscrOrdine(), aziendaInfoModify.getIscrOrdine())) {
			aziendaInfoRettificaRegister.setIscrOrdine(aziendaInfoModify.getIscrOrdine());
		}

		if (aziendaInfoModify.getDeComuneIscrizione() != null) {
			if (aziendaInfoSaved.getDeComuneIscrizione() != null) {
				if (!aziendaInfoSaved.getDeComuneIscrizione().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneIscrizione().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneIscrizione(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneIscrizione().getCodCom()));
				}
			} else {
				aziendaInfoRettificaRegister.setDeComuneIscrizione(
						deComuneEJB.findById(aziendaInfoModify.getDeComuneIscrizione().getCodCom()));
			}
		} else {
			if (aziendaInfoSaved.getDeComuneIscrizione() != null) {
				if (!aziendaInfoSaved.getDeComuneIscrizione().getCodCom()
						.equalsIgnoreCase(aziendaInfoModify.getDeComuneIscrizione().getCodCom())) {
					aziendaInfoRettificaRegister.setDeComuneIscrizione(
							deComuneEJB.findById(aziendaInfoModify.getDeComuneIscrizione().getCodCom()));
				}
			}
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getIscrNumero(), aziendaInfoModify.getIscrNumero())) {
			aziendaInfoRettificaRegister.setIscrNumero(aziendaInfoModify.getIscrNumero());
		}

		if (aziendaInfoSaved.getDtIscrData() != null && aziendaInfoModify.getDtIscrData() != null) {
			if (!EqualsUtils.areEqual(
					it.eng.sil.myaccount.utils.StringUtils.formatDate(aziendaInfoSaved.getDtIscrData(), "dd/MM/yyyy"),
					it.eng.sil.myaccount.utils.StringUtils.formatDate(aziendaInfoModify.getDtIscrData(),
							"dd/MM/yyyy"))) {
				aziendaInfoRettificaRegister.setDtIscrData(aziendaInfoModify.getDtIscrData());
			}
		} else {
			aziendaInfoRettificaRegister.setDtIscrData(aziendaInfoModify.getDtIscrData());
		}

		if (aziendaInfoSaved.getDtIscrProvvedData() != null && aziendaInfoModify.getDtIscrProvvedData() != null) {
			if (!EqualsUtils.areEqual(
					it.eng.sil.myaccount.utils.StringUtils.formatDate(aziendaInfoSaved.getDtIscrProvvedData(),
							"dd/MM/yyyy"),
					it.eng.sil.myaccount.utils.StringUtils.formatDate(aziendaInfoModify.getDtIscrProvvedData(),
							"dd/MM/yyyy"))) {
				aziendaInfoRettificaRegister.setDtIscrProvvedData(aziendaInfoModify.getDtIscrProvvedData());
			}
		} else {
			aziendaInfoRettificaRegister.setDtIscrProvvedData(aziendaInfoModify.getDtIscrProvvedData());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getIscrProvvedNumero(), aziendaInfoModify.getIscrProvvedNumero())) {
			aziendaInfoRettificaRegister.setIscrProvvedNumero(aziendaInfoModify.getIscrProvvedNumero());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getSwCreazioneCo(), aziendaInfoModify.getSwCreazioneCo())) {
			aziendaInfoRettificaRegister.setSwCreazioneCo(aziendaInfoModify.getSwCreazioneCo());
		}

		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCellReferente(), aziendaInfoModify.getCellReferente())) {
			aziendaInfoRettificaRegister.setCellReferente(aziendaInfoModify.getCellReferente());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getNomeUtente(), aziendaInfoModify.getNomeUtente())) {
			aziendaInfoRettificaRegister.setNomeUtente(aziendaInfoModify.getNomeUtente());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getCodAutorizzazione(), aziendaInfoModify.getCodAutorizzazione())) {
			aziendaInfoRettificaRegister.setCodAutorizzazione(aziendaInfoModify.getCodAutorizzazione());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getRagioneSociale(), aziendaInfoModify.getRagioneSociale())) {
			aziendaInfoRettificaRegister.setCodUtenteSare(aziendaInfoModify.getCodUtenteSare());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getPfPrincipal().getDomanda(),
				aziendaInfoModify.getPfPrincipal().getDomanda())) {
			aziendaInfoRettificaRegister.setDomanda(aziendaInfoModify.getPfPrincipal().getDomanda());
		}
		if (!EqualsUtils.areEqual(aziendaInfoSaved.getPfPrincipal().getRisposta(),
				aziendaInfoModify.getPfPrincipal().getRisposta())) {
			aziendaInfoRettificaRegister.setRisposta(aziendaInfoModify.getPfPrincipal().getRisposta());
		}

		return aziendaInfoRettificaRegister;
	}

	public DeProvincia getProvinciaRiferimento(AziendaInfo aziendaInfo) throws EJBException, MyCasNoResultException {
		PfPrincipal pfPrincipal = aziendaInfo.getPfPrincipal();
		if (pfPrincipal.getFlagAbilitatoSare()) {
			return aziendaInfo.getDeProvincia();
		} else {
			DeProvincia provinciaSede = aziendaInfo.getDeComuneSede().getDeProvincia();
			DeRegione regioneSede = provinciaSede.getDeRegione();
			if (constantsSingleton.getCodRegione().equals(regioneSede.getCodRegione())) {
				return provinciaSede;
			} else {
				DeRegione regioneApp = deRegioneEJB.findById(constantsSingleton.getCodRegione());
				return deRegioneEJB.getCapoluogo(regioneApp);
			}
		}
	}

	/**
	 * Query che ricerca tutte le aziende che corrispondono ai parametri passati in input - sono tutti opzionali.
	 */
	public List<AziendaInfo> cercaUtentiSAREAbilita(String user, DeAutorizzazioneSare autorizzazioneSare,
			String codProvincia, boolean rettificaFlag, int first, int pageSize) {
		// Creo la base della query.
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> criteria = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfoRoot = criteria.from(AziendaInfo.class);
		criteria.select(aziendaInfoRoot);

		// Aggiungo la clausola WHERE.
		List<Predicate> predicateList = creaWhereConditionsCercaUtentiSAREAbilita(aziendaInfoRoot, user,
				autorizzazioneSare, codProvincia, rettificaFlag);
		if (!predicateList.isEmpty()) {
			criteria.where(qb.and(predicateList.toArray(new Predicate[0])));
		}

		// Ordino il risultato per ragione sociale.
		criteria.orderBy(qb.asc(aziendaInfoRoot.get(AziendaInfo_.ragioneSociale)));

		// Eseguo la query paginata.
		TypedQuery<AziendaInfo> query = entityMan.createQuery(criteria);
		if (first > 0) {
			query.setFirstResult(first);
		}

		if (pageSize > 0) {
			query.setMaxResults(pageSize);
		}

		return query.getResultList();
	}

	/**
	 * Query che ricerca il numero di aziende che corrispondono ai parametri passati in input - sono tutti opzionali.
	 */
	public Integer cercaUtentiSAREAbilitaCount(String user, DeAutorizzazioneSare autorizzazioneSare,
			String codProvincia, boolean rettificaFlag) {
		// Creo la base della query.
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = qb.createQuery(Long.class);
		Root<AziendaInfo> aziendaInfoRoot = criteria.from(AziendaInfo.class);
		criteria.select(qb.count(aziendaInfoRoot));

		// Aggiungo la clausola WHERE.
		List<Predicate> predicateList = creaWhereConditionsCercaUtentiSAREAbilita(aziendaInfoRoot, user,
				autorizzazioneSare, codProvincia, rettificaFlag);
		if (!predicateList.isEmpty()) {
			criteria.where(qb.and(predicateList.toArray(new Predicate[0])));
		}

		// Eseguo la query paginata.
		TypedQuery<Long> query = entityMan.createQuery(criteria);
		return query.getSingleResult().intValue();
	}

	private List<Predicate> creaWhereConditionsCercaUtentiSAREAbilita(Root<AziendaInfo> aziendaInfoRoot, String user,
			DeAutorizzazioneSare deAutorizzazioneSare, String codProvincia, boolean rettificaFlag) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		List<Predicate> predicateList = new ArrayList<Predicate>();
		Join<AziendaInfo, PfPrincipal> pfPrincipalJoin = aziendaInfoRoot.join(AziendaInfo_.pfPrincipal, JoinType.INNER);

		// Clausola 1 : DeAutorizzazioneSare != null
		predicateList.add(qb.isNotNull(aziendaInfoRoot.get(AziendaInfo_.deAutorizzazioneSare)));

		// Clausola 2 : DeTipoUtenteSare != null
		predicateList.add(qb.isNotNull(aziendaInfoRoot.get(AziendaInfo_.deTipoUtenteSare)));

		// Clausola 3: Stato della richiesta di autorizzazione
		if (deAutorizzazioneSare != null) {
			predicateList.add(qb.equal(aziendaInfoRoot.get(AziendaInfo_.deAutorizzazioneSare), deAutorizzazioneSare));
		}

		// Clausola 4: Codice provincia.
		if (StringUtils.isFilledNoBlank(codProvincia)) {
			Join<AziendaInfo, DeProvincia> deProvinciaJoin = aziendaInfoRoot.join(AziendaInfo_.deProvincia);
			predicateList.add(qb.equal(deProvinciaJoin.get(DeProvincia_.codProvincia), codProvincia));
		}

		// Clausola 5: solo aziende con rettifiche
		if (rettificaFlag) {
			// Metto in inner join con AziendaInfoRettifica
			@SuppressWarnings("unused")
			Join<PfPrincipal, AziendaInfoRettifica> aziendaInfoRettificaJoin = pfPrincipalJoin
					.join(PfPrincipal_.aziendaInfoRettifica, JoinType.INNER);
		}

		// Clausola 6: parametro di ricerca "utente".
		if (StringUtils.isFilledNoBlank(user)) {
			Predicate codiceFiscalePredicate = qb.equal(qb.upper(aziendaInfoRoot.get(AziendaInfo_.codiceFiscale)),
					user.trim().toUpperCase());
			Predicate ragioneSocialePredicate = qb.like(qb.upper(aziendaInfoRoot.get(AziendaInfo_.ragioneSociale)),
					"%" + user.trim().toUpperCase() + "%");
			Predicate usernamePredicate = qb.like(qb.upper(pfPrincipalJoin.get(PfPrincipal_.username)),
					"%" + user.trim().toUpperCase() + "%");
			Predicate searchParamPredicate = qb.or(codiceFiscalePredicate, ragioneSocialePredicate, usernamePredicate);

			// Se il parametro di ricerca è un numero, lo applico anche all'ID dell'azienda.
			try {
				BigDecimal userParamBigDecimal = new BigDecimal(user);
				Predicate idPfPrincipalPredicate = qb.equal(pfPrincipalJoin.get(PfPrincipal_.idPfPrincipal),
						userParamBigDecimal);
				searchParamPredicate = qb.or(searchParamPredicate, idPfPrincipalPredicate);
			} catch (NumberFormatException e) {
				// Non è un errore, vuol dire semplicemente che il searchparam non è un numero
			}

			predicateList.add(searchParamPredicate);
		}

		return predicateList;
	}

	/**
	 * Questo metodo viene usato nella pagina "valida utenti SARE".
	 */
	public List<AziendaInfo> cercaUtentiSAREValida(String user, String stato, String codProvincia, int first,
			int pageSize) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> criteria = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfoRoot = criteria.from(AziendaInfo.class);
		criteria.select(aziendaInfoRoot);

		// Aggiungo la clausola WHERE.
		List<Predicate> predicateList = creaWhereConditionsCercaUtentiSAREValida(aziendaInfoRoot, user, stato,
				codProvincia);
		if (!predicateList.isEmpty()) {
			criteria.where(qb.and(predicateList.toArray(new Predicate[0])));
		}

		// Ordino il risultato in base alla ragione sociale.
		criteria.orderBy(qb.asc(aziendaInfoRoot.get(AziendaInfo_.ragioneSociale)));

		// Creo ed eseguo la query, con paginazione.
		TypedQuery<AziendaInfo> query = entityMan.createQuery(criteria);
		if (first > 0) {
			query.setFirstResult(first);
		}
		if (pageSize > 0) {
			query.setMaxResults(pageSize);
		}
		return query.getResultList();
	}

	/**
	 * Usato nella pagina "valida utenti SARE".
	 */
	public Integer cercaUtentiSAREValidaCount(String user, String stato, String codProvincia) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = qb.createQuery(Long.class);
		Root<AziendaInfo> aziendaInfoRoot = criteria.from(AziendaInfo.class);
		criteria.select(qb.count(aziendaInfoRoot));

		// Aggiungo la clausola WHERE.
		List<Predicate> predicateList = creaWhereConditionsCercaUtentiSAREValida(aziendaInfoRoot, user, stato,
				codProvincia);
		if (!predicateList.isEmpty()) {
			criteria.where(qb.and(predicateList.toArray(new Predicate[0])));
		}

		// Creo ed eseguo la query.
		TypedQuery<Long> query = entityMan.createQuery(criteria);
		return query.getSingleResult().intValue();
	}

	/**
	 * Usato nella pagina "valida utenti SARE".
	 */
	private List<Predicate> creaWhereConditionsCercaUtentiSAREValida(Root<AziendaInfo> aziendaInfoRoot, String user,
			String stato, String codProvincia) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Predicato: Campo di ricerca "utente" applicato a vari campi del database.
		if (StringUtils.isFilledNoBlank(user)) {
			Join<AziendaInfo, PfPrincipal> pfPrincipalJoin = aziendaInfoRoot.join(AziendaInfo_.pfPrincipal);
			Predicate codiceFiscalePredicate = qb.equal(qb.upper(aziendaInfoRoot.get(AziendaInfo_.codiceFiscale)),
					user.trim().toUpperCase());
			Predicate ragioneSocialePredicate = qb.like(qb.upper(aziendaInfoRoot.get(AziendaInfo_.ragioneSociale)),
					"%" + user.trim().toUpperCase() + "%");
			Predicate usernamePredicate = qb.like(qb.upper(pfPrincipalJoin.get(PfPrincipal_.username)),
					"%" + user.trim().toUpperCase() + "%");
			Predicate searchParamPredicate = qb.or(codiceFiscalePredicate, ragioneSocialePredicate, usernamePredicate);

			// Se il parametro di ricerca è un numero, lo applico anche all'ID dell'azienda.
			try {
				BigDecimal userParamBigDecimal = new BigDecimal(user);
				Predicate idPfPrincipalPredicate = qb.equal(pfPrincipalJoin.get(PfPrincipal_.idPfPrincipal),
						userParamBigDecimal);
				searchParamPredicate = qb.or(searchParamPredicate, idPfPrincipalPredicate);
			} catch (NumberFormatException e) {
				// Non è un errore, vuol dire semplicemente che il searchparam non è un numero
			}

			predicateList.add(searchParamPredicate);
		}

		// Predicato: devono essere aziende di questa provincia (oppure non avere nessuna provincia settata).
		if (StringUtils.isFilledNoBlank(codProvincia)) {
			Join<AziendaInfo, DeProvincia> deProvinciaJoin = aziendaInfoRoot.join(AziendaInfo_.deProvincia,
					JoinType.LEFT);
			Predicate provNullPredicate = qb.isNull(aziendaInfoRoot.get(AziendaInfo_.deProvincia));
			Predicate miaProvPredicate = qb.equal(deProvinciaJoin.get(DeProvincia_.codProvincia), codProvincia);
			predicateList.add(qb.or(provNullPredicate, miaProvPredicate));
		}

		// Predicato: stato della validazione dell'azienda.
		if (StringUtils.isFilledNoBlank(stato)) {
			if (stato.equals("Y")) {
				predicateList.add(qb.equal(aziendaInfoRoot.get(AziendaInfo_.flagValida), true));
			} else if (stato.equals("N")) {
				predicateList.add(qb.equal(aziendaInfoRoot.get(AziendaInfo_.flagValida), false));
			}
		}

		return predicateList;
	}

	/**
	 * Aggiorna l'abilitazione al SARE e/o il tipo utente delle aziende passate come input
	 * 
	 * @param daAggiornare
	 *            lista delle aziende da aggiornare
	 * @throws MyAccountException
	 */
	public void aggiornaUtentiSAREAbilita(List<AziendaInfo> daAggiornare, Integer idPfPrincipalMod)
			throws MyAccountException, Exception {
		for (AziendaInfo utenteSARE : daAggiornare) {
			if (!utenteSARE.getModificaAutorizzazioneSARE().equals(utenteSARE.getCodAutorizzazione())) {
				AziendaInfo azModify = findById(utenteSARE.getIdPfPrincipal());
				String oldAutorizzazione = deAutorizzazioneSareEJB.findById(utenteSARE.getModificaAutorizzazioneSARE())
						.getDescrizione();
				String newAutorizzazione = deAutorizzazioneSareEJB.findById(utenteSARE.getCodAutorizzazione())
						.getDescrizione();

				azModify.setDeAutorizzazioneSare(deAutorizzazioneSareEJB.findById(utenteSARE.getCodAutorizzazione()));
				PfPrincipal pfPrinc = pfPrincipalMyAccountEJB.findById(utenteSARE.getIdPfPrincipal());

				// Se il NUOVO SARE è attivo e sto settando l'autorizzazione a "Concessa", assegno/creo una profilatura
				// "Gestione CO" per questo utente.
				if (constantsSingleton.isMySareProfilaturaEnabled()
						&& ConstantsSingleton.GpRuoloConstants.COD_AUTORIZZAZIONE_SARE_CONCESSA
								.equals(utenteSARE.getCodAutorizzazione())) {
					aggiungiProfilaturaUtenteSARE(azModify, idPfPrincipalMod);
				}

				// Se il SARE è attivo, chiamo il webservice per modificare lo stato sul SARE.
				if (!constantsSingleton.isSareDisabled()) {
					String esitoWs = utenteAziendaSareEJB.abilitaUtenteSare(pfPrinc, azModify);
					log.info("SARE: abilitazione azienda " + pfPrinc.getUsername() + " con esito=" + esitoWs);
				}

				// Se il webservice va a buon fine, confermo le modifiche e mando notifica ed email.
				merge(idPfPrincipalMod, azModify);

				MsgMessaggio notifica = notificationBuilder.buildNotificationAggiornaAbilitazioneSARE(azModify,
						oldAutorizzazione, newAutorizzazione);
				notificationBuilder.sendNotification(notifica);

				String codProvinciaRiferimento = utenteSARE.getDeProvincia() == null ? null
						: utenteSARE.getDeProvincia().getCodProvincia();
				emailManager.sendEmailNotificaRicevuta(pfPrinc.getUsername(), pfPrinc.getEmail(),
						codProvinciaRiferimento);
			}

			if (!utenteSARE.getModificaCodTipoUtenteSare().equals(utenteSARE.getCodUtenteSare())) {
				AziendaInfo azModify = findById(utenteSARE.getIdPfPrincipal());
				azModify.setDeTipoUtenteSare(deTipoUtenteSareEJB.findById(utenteSARE.getCodUtenteSare()));
				merge(idPfPrincipalMod, azModify);

				PfPrincipal pfPrinc = pfPrincipalMyAccountEJB.findById(utenteSARE.getIdPfPrincipal());

				boolean editSoggettoAbilitato = false;
				boolean editAgenziaSomministrazione = false;
				if (ConstantsCommons.DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE
						.equals(azModify.getDeTipoAbilitato().getCodTipoAbilitato())) {
					editAgenziaSomministrazione = true;
				}
				if (ConstantsCommons.DeTipoAbilitato.SOGGETTO_ABILITATO
						.equals(azModify.getDeTipoAbilitato().getCodTipoAbilitato())) {
					editSoggettoAbilitato = true;
				}

				// Se il SARE è abilitato, chiamo il ws per modificare l'utente anche lì.
				if (!constantsSingleton.isSareDisabled()) {
					String esitoWs = utenteAziendaSareEJB.modificaUtenteSare(pfPrinc, azModify, editSoggettoAbilitato,
							editAgenziaSomministrazione);
					log.info("SARE: abilitazione/modifica azienda " + pfPrinc.getUsername() + " con esito=" + esitoWs);
				}
			}

		}
	}

	/**
	 * Controlla se un'azienda ha già una profilatura del tipo "Gestione CO", e se non ce l'ha gliela aggiunge. Deve
	 * essere chiamato quando un'azienda viene abilitata al SARE.
	 */
	private void aggiungiProfilaturaUtenteSARE(AziendaInfo azienda, Integer idPrincipalIns) throws MyCasException {
		List<GpProfilatura> profilature = gpProfilaturaEJB.findForIdPfPrincipal(azienda.getIdPfPrincipal());
		boolean hasProfilatura = false;
		for (GpProfilatura profilatura : profilature) {
			if (profilatura.getGpRuoloGruppo().getGpRuolo().getDescrizione()
					.equals(ConstantsSingleton.GpRuoloConstants.GESTIONE_CO)) {
				hasProfilatura = true;
			}
		}

		if (!hasProfilatura) {
			// Se non ha già la profilatura, controllo se c'è già il suo gruppo in giro.
			GpGruppo gruppoSare = gpGruppoEJB.findGruppoAziendaSare(azienda.getIdPfPrincipal());
			if (gruppoSare == null) {
				gruppoSare = new GpGruppo();
				gruppoSare.setDescrizione(
						"Mittenti SARE per: " + azienda.getRagioneSociale() + " (" + azienda.getPartitaIva() + ")");
				gruppoSare.setGpDeTipoGruppo(gpDeTipoGruppoEJB.findById(GpDeTipoGruppoEnum.AZI));
				gruppoSare.setNote("SARE: " + azienda.getPfPrincipal().getIdPfPrincipal());
				gruppoSare = gpGruppoEJB.persist(idPrincipalIns, gruppoSare);
			}

			// Ora in un modo o nell'altro ho il gruppo, prendo/creo la profilatura.
			List<GpRuolo> ruoli = gpRuoloEJB.findByFilter(ConstantsSingleton.GpRuoloConstants.GESTIONE_CO, null);
			if (ruoli == null || ruoli.isEmpty() || ruoli.size() > 2) {
				throw new MyCasException("ERRORE GRAVE durante la find del ruolo Gestione CO!");
			}

			GpRuolo ruoloGestioneCO = ruoli.get(0);
			GpRuoloGruppo ruoloGruppoSare = gpRuoloGruppoEJB.findOrCreate(ruoloGestioneCO.getIdGpRuolo(),
					gruppoSare.getIdGpGruppo(), idPrincipalIns);
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setPfPrincipal(azienda.getPfPrincipal());
			newProfilatura.setGpRuoloGruppo(ruoloGruppoSare);
			newProfilatura = gpProfilaturaEJB.persist(idPrincipalIns, newProfilatura);
		}
	}

	/**
	 * Questo metodo aggiorna il campo "flagValida" di una lista di elementi AziendaInfo. Usato nella pagina "valida
	 * utenti SARE".
	 */
	public void aggiornaUtentiSAREValida(List<AziendaInfo> daAggiornare, Integer idPfPrincipalMod)
			throws MyCasException {
		for (AziendaInfo utenteSARE : daAggiornare) {
			if (!utenteSARE.getModificaFlagValida().equals(utenteSARE.getFlagValida())) {
				AziendaInfo azModify = findById(utenteSARE.getIdPfPrincipal());
				azModify.setFlagValida(utenteSARE.getFlagValida());
				merge(idPfPrincipalMod, azModify);

				// invio mail
				if (utenteSARE.getFlagValida()) {
					PfPrincipal principal = pfPrincipalMyAccountEJB.findById(azModify.getIdPfPrincipal());
					if (!principal.getFlagAbilitato()) {

						String password = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
						pfPrincipalMyAccountEJB.modifyDatiAccount(principal, password);

						String activationLink = constantsSingleton.getMyAccountURL() + "/register/confirm/"
								+ principal.getUsername() + "/" + principal.getEmail() + "/"
								+ principal.getConfirmationToken();

						String codProvinciaRiferimento = utenteSARE.getDeProvincia() == null ? null
								: utenteSARE.getDeProvincia().getCodProvincia();
						emailManager.sendEmailValidaAzienda(principal.getNome(), principal.getUsername(), password,
								activationLink, principal.getEmail(), codProvinciaRiferimento);
					}
				}

			}
		}
	}

	/**
	 * Questo metodo crea un file .csv contenente il risultato di una ricerca di utenti SARE. Da usare nella pagina
	 * "abilita utenti SARE".
	 */
	public String exportUtentiSareAbilitaCsv(String user, DeAutorizzazioneSare autorizzazioneSare, String codProvincia,
			boolean utenteRetDati, int numPagina, int dimPagina) throws MyCasNoResultException {
		List<AziendaInfo> list = cercaUtentiSAREAbilita(user, autorizzazioneSare, codProvincia, utenteRetDati,
				numPagina, dimPagina);

		String FIELD_DELIMITER = "\"";
		String FIELD_SEPARATOR = ";";
		String LINE_SEPARATOR = "\n";
		StringBuilder csvBuilder = new StringBuilder();

		/* riga dei titoli */
		csvBuilder.append(FIELD_DELIMITER + "Codice Fiscale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Ragione sociale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Telefono referente SARE" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "E-mail referente SARE" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Referente SARE" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Autorizzazione" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Tipo utente" + FIELD_DELIMITER + LINE_SEPARATOR);

		/* dati */
		for (AziendaInfo utenteSARE : list) {

			if (utenteSARE.getCodiceFiscale() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getCodiceFiscale() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getRagioneSociale() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getRagioneSociale().replace("\"", "\"\"")
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getTelefonoReferente() != null) {
				csvBuilder.append(
						FIELD_DELIMITER + utenteSARE.getTelefonoReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getEmailReferente() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getEmailReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getReferenteSare() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getReferenteSare() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getCodAutorizzazione() != null) {
				DeAutorizzazioneSare autSare = deAutorizzazioneSareEJB.findById(utenteSARE.getCodAutorizzazione());
				csvBuilder.append(FIELD_DELIMITER + autSare.getDescrizione() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getCodUtenteSare() != null) {
				DeTipoUtenteSare tipoUtSare = deTipoUtenteSareEJB.findById(utenteSARE.getCodUtenteSare());
				csvBuilder.append(FIELD_DELIMITER + tipoUtSare.getDescrizione() + FIELD_DELIMITER + LINE_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + LINE_SEPARATOR);
			}
		}

		String csvList = csvBuilder.toString();
		return csvList;
	}

	/**
	 * Questo metodo restituisce una stringa corrispondente ad un file .csv contenente il risultato di una ricerca di
	 * utenti SARE. Da utilizzare nella pagina "valida utenti SARE".
	 */
	public String exportUtentiSareValidaCsv(String user, String stato, String codProvincia, int numPagina,
			int dimPagina) throws MyCasNoResultException {
		List<AziendaInfo> list = cercaUtentiSAREValida(user, stato, codProvincia, numPagina, dimPagina);

		String FIELD_DELIMITER = "\"";
		String FIELD_SEPARATOR = ";";
		String LINE_SEPARATOR = "\n";
		StringBuilder csvBuilder = new StringBuilder();

		/* riga dei titoli */
		csvBuilder.append(FIELD_DELIMITER + "Codice Fiscale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Ragione sociale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Telefono referente" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "E-mail referente" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Referente" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Abilitata" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Validata" + FIELD_DELIMITER + LINE_SEPARATOR);

		/* dati */
		for (AziendaInfo utenteSARE : list) {

			if (utenteSARE.getCodiceFiscale() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getCodiceFiscale() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getRagioneSociale() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getRagioneSociale().replace("\"", "\"\"")
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getTelefonoReferente() != null) {
				csvBuilder.append(
						FIELD_DELIMITER + utenteSARE.getTelefonoReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getEmailReferente() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getEmailReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			if (utenteSARE.getReferenteSare() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getReferenteSare() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			// Colonna: Abilitato.
			if (pfPrincipalMyAccountEJB.findFlagAbilitatoByIdPfPrincipal(utenteSARE.getIdPfPrincipal())) {
				csvBuilder.append(FIELD_DELIMITER + "Si'" + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + "No" + FIELD_DELIMITER + FIELD_SEPARATOR);
			}

			// Colonna: Validato.
			if (utenteSARE.getFlagValida() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSARE.getFlagValida() + FIELD_DELIMITER + LINE_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + LINE_SEPARATOR);
			}
		}

		String csvList = csvBuilder.toString();
		return csvList;
	}

	/**
	 * Concede l'autorizzazione SARE ad un utente.
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public AziendaInfo concediAutorizzazioneSare(AziendaInfo azienda, Integer idPrincipalMod) throws MyCasException {
		DeAutorizzazioneSare autorizzazioneConcessa = deAutorizzazioneSareEJB.findAutorizzazioneConcessa();
		azienda.setDeAutorizzazioneSare(autorizzazioneConcessa);
		return merge(idPrincipalMod, azienda);
	}
}
