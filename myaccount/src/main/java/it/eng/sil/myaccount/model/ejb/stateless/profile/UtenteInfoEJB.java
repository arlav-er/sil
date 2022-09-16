package it.eng.sil.myaccount.model.ejb.stateless.profile;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.model.utils.PortletsUtils;
import it.eng.sil.myaccount.model.utils.YgUtils;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.entity.decodifiche.DeCpi;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal_;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo_;
import it.eng.sil.mycas.model.manager.AbstractTabellaProfiloEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCpiEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpProfilaturaEJB;

@Stateless
public class UtenteInfoEJB extends AbstractTabellaProfiloEJB<UtenteInfo, Integer> {

	@EJB
	DeCpiEJB deCpiEJB;

	@EJB
	ConstantsSingleton constantsSingleton;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	DeComuneEJB deComuneEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	@EJB
	DeCittadinanzaEJB deCittadinanzaEJB;

	@EJB
	DeTitoloSoggiornoEJB deTitoloSoggiornoEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	@EJB
	GpProfilaturaEJB gpProfilaturaEJB;

	@Override
	public String getFriendlyName() {
		return "Informazioni cittadino";
	}

	@Override
	public Class<UtenteInfo> getEntityClass() {
		return UtenteInfo.class;
	}

	public Integer register(PfPrincipal pfPrincipalRegister, UtenteInfo utenteInfoRegister, boolean pwdScaduta,
			boolean isLavoroPerTeInstallato) throws Exception {

		PfPrincipal principalUtente = pfPrincipalMyAccountEJB.registerCittadino(pfPrincipalRegister, pwdScaduta);

		// creazione info utente
		UtenteInfo utenteInfo = new UtenteInfo();
		utenteInfo.setPfPrincipal(principalUtente);
		utenteInfo.setIdPfPrincipal(principalUtente.getIdPfPrincipal());
		// Aggiunte per Trento
		utenteInfo.setDtNascita(utenteInfoRegister.getDtNascita());
		utenteInfo.setCellulare(utenteInfoRegister.getCellulare());
		utenteInfo.setTelCasa(utenteInfoRegister.getTelCasa());
		utenteInfo.setIndirizzoDomicilio(utenteInfoRegister.getIndirizzoDomicilio());
		utenteInfo.setIndirizzoResidenza(utenteInfoRegister.getIndirizzoResidenza());

		if (utenteInfoRegister.getDeComuneNascita() != null
				&& utenteInfoRegister.getDeComuneNascita().getCodCom() != null) {
			utenteInfo.setDeComuneNascita(deComuneEJB.findById(utenteInfoRegister.getDeComuneNascita().getCodCom()));
		}
		if (utenteInfoRegister.getDeComuneResidenza() != null
				&& utenteInfoRegister.getDeComuneResidenza().getCodCom() != null) {
			utenteInfo
					.setDeComuneResidenza(deComuneEJB.findById(utenteInfoRegister.getDeComuneResidenza().getCodCom()));
		}
		if (utenteInfoRegister.getDeComuneDomicilio() != null
				&& utenteInfoRegister.getDeComuneDomicilio().getCodCom() != null) {
			utenteInfo
					.setDeComuneDomicilio(deComuneEJB.findById(utenteInfoRegister.getDeComuneDomicilio().getCodCom()));
		}
		if (utenteInfoRegister.getDeProvincia() != null
				&& utenteInfoRegister.getDeProvincia().getCodProvincia() != null) {
			utenteInfo.setDeProvincia(deProvinciaEJB.findById(utenteInfoRegister.getDeProvincia().getCodProvincia()));
		}
		if (utenteInfoRegister.getDeCittadinanza() != null
				&& utenteInfoRegister.getDeCittadinanza().getCodCittadinanza() != null) {
			utenteInfo.setDeCittadinanza(
					deCittadinanzaEJB.findById(utenteInfoRegister.getDeCittadinanza().getCodCittadinanza()));
		}
		if (utenteInfoRegister.getCodiceFiscale() != null
				&& !("").equalsIgnoreCase(utenteInfoRegister.getCodiceFiscale())) {
			utenteInfo.setCodiceFiscale((utenteInfoRegister.getCodiceFiscale()).toUpperCase());
		}
		utenteInfo.setEmailPEC(utenteInfoRegister.getEmailPEC());
		if (utenteInfoRegister.getDocumentoSoggiorno() != null
				&& utenteInfoRegister.getDocumentoSoggiorno().getCodTitoloSoggiorno() != null) {
			utenteInfo.setDocumentoSoggiorno(
					deTitoloSoggiornoEJB.findById(utenteInfoRegister.getDocumentoSoggiorno().getCodTitoloSoggiorno()));
		}
		utenteInfo.setDocumentoIdentita(utenteInfoRegister.getDocumentoIdentita());
		utenteInfo.setNumeroDocumento(utenteInfoRegister.getNumeroDocumento());
		utenteInfo.setDataScadenzaDocumento(utenteInfoRegister.getDataScadenzaDocumento());
		utenteInfo.setNumeroAssicurata(utenteInfoRegister.getNumeroAssicurata());
		utenteInfo.setDataAssicurata(utenteInfoRegister.getDataAssicurata());
		// accettazione informative
		utenteInfo.setFlgAcceptedInformativaDid(false);
		utenteInfo.setFlgAcceptedInformativaPercLav(false);
		utenteInfo.setFlgAcceptedInformativaStatoOcc(false);
		utenteInfo.setFlagAcceptedInformativaConfermaPeriodica(false);
		utenteInfo.setFlagAcceptedInformativaRinnovoPatto(false);

		//utenteInfo.setCodServiziAmministrativi(generateUniqueCodServAmm());

		persist(0, utenteInfo);

		// NUOVA PROFILATURA : Aggiungo le profilature di default
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.CIT);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(principalUtente);
			gpProfilaturaEJB.persist(0, newProfilatura);
		}

		return principalUtente.getIdPfPrincipal();
	}

	public UtenteInfo registerFromPannello(UtenteInfo newCittadino, Integer idPrincipalIns,
			boolean isLavoroPerTeInstallato) throws MyCasException {
		// Persist di PfPrincipal
		PfPrincipal persistedPfPrincipal = pfPrincipalMyAccountEJB
				.registerCittadinoFromPannello(newCittadino.getPfPrincipal(), idPrincipalIns);

		// Persist di UtenteInfo
		newCittadino.setPfPrincipal(persistedPfPrincipal);
		newCittadino.setIdPfPrincipal(persistedPfPrincipal.getIdPfPrincipal());
		newCittadino.setFlgAcceptedInformativaDid(false);
		newCittadino.setFlgAcceptedInformativaPercLav(false);
		newCittadino.setFlgAcceptedInformativaStatoOcc(false);
		newCittadino.setFlagAcceptedInformativaConfermaPeriodica(false);
		newCittadino.setFlagAcceptedInformativaRinnovoPatto(false);
		newCittadino = persist(idPrincipalIns, newCittadino);

		// NUOVA PROFILATURA : Aggiungo le profilature di default
		List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(GpDeMacroTipoEnum.CIT);
		for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setGpRuoloGruppo(ruoloGruppo);
			newProfilatura.setPfPrincipal(newCittadino.getPfPrincipal());
			gpProfilaturaEJB.persist(0, newProfilatura);
		}

		return newCittadino;
	}

	public void updateProfile(UtenteInfo utenteInfoRegister) throws MyCasException {

		pfPrincipalMyAccountEJB.updateCittadino(utenteInfoRegister.getPfPrincipal());

		// aggiornamento info utente
		UtenteInfo utenteInfo = findById(utenteInfoRegister.getIdPfPrincipal());
		utenteInfo.setDtNascita(utenteInfoRegister.getDtNascita());
		utenteInfo.setCellulare(utenteInfoRegister.getCellulare());
		utenteInfo.setCellulareOTP(utenteInfoRegister.getCellulareOTP());
		utenteInfo.setTelCasa(utenteInfoRegister.getTelCasa());
		utenteInfo.setIndirizzoDomicilio(utenteInfoRegister.getIndirizzoDomicilio());
		utenteInfo.setIndirizzoResidenza(utenteInfoRegister.getIndirizzoResidenza());

		if (utenteInfoRegister.getDeComuneNascita() != null
				&& utenteInfoRegister.getDeComuneNascita().getCodCom() != null) {
			utenteInfo.setDeComuneNascita(deComuneEJB.findById(utenteInfoRegister.getDeComuneNascita().getCodCom()));
		}
		if (utenteInfoRegister.getDeComuneDomicilio() != null
				&& utenteInfoRegister.getDeComuneDomicilio().getCodCom() != null) {
			utenteInfo
					.setDeComuneDomicilio(deComuneEJB.findById(utenteInfoRegister.getDeComuneDomicilio().getCodCom()));
		}
		if (utenteInfoRegister.getDeComuneResidenza() != null
				&& utenteInfoRegister.getDeComuneResidenza().getCodCom() != null) {
			utenteInfo
					.setDeComuneResidenza(deComuneEJB.findById(utenteInfoRegister.getDeComuneResidenza().getCodCom()));
		}
		if (utenteInfoRegister.getDeProvincia() != null
				&& utenteInfoRegister.getDeProvincia().getCodProvincia() != null) {
			utenteInfo.setDeProvincia(deProvinciaEJB.findById(utenteInfoRegister.getDeProvincia().getCodProvincia()));
		}
		if (utenteInfoRegister.getDeCittadinanza() != null
				&& utenteInfoRegister.getDeCittadinanza().getCodCittadinanza() != null) {
			utenteInfo.setDeCittadinanza(
					deCittadinanzaEJB.findById(utenteInfoRegister.getDeCittadinanza().getCodCittadinanza()));
		}

		utenteInfo.setEmailPEC(utenteInfoRegister.getEmailPEC());
		if (utenteInfoRegister.getDocumentoSoggiorno() != null
				&& utenteInfoRegister.getDocumentoSoggiorno().getCodTitoloSoggiorno() != null) {
			utenteInfo.setDocumentoSoggiorno(
					deTitoloSoggiornoEJB.findById(utenteInfoRegister.getDocumentoSoggiorno().getCodTitoloSoggiorno()));
		}
		if (utenteInfoRegister.getCodiceFiscale() != null
				&& !("").equalsIgnoreCase(utenteInfoRegister.getCodiceFiscale())) {
			utenteInfo.setCodiceFiscale((utenteInfoRegister.getCodiceFiscale()).toUpperCase());
		}

		utenteInfo.setDocumentoIdentita(utenteInfoRegister.getDocumentoIdentita());
		utenteInfo.setNumeroDocumento(utenteInfoRegister.getNumeroDocumento());
		utenteInfo.setDataScadenzaDocumento(utenteInfoRegister.getDataScadenzaDocumento());
		utenteInfo.setNumeroAssicurata(utenteInfoRegister.getNumeroAssicurata());
		utenteInfo.setDataAssicurata(utenteInfoRegister.getDataAssicurata());

		merge(0, utenteInfo);

	}

	public void updateRichiestaToken(UtenteInfo utenteInfoRegister) {
		try {
			pfPrincipalMyAccountEJB.updateRichiestaToken(utenteInfoRegister.getPfPrincipal());
		} catch (MyCasException e) {
			throw new EJBException(e);
		}
	}

	public Boolean addPortlet(Integer idPfPrincipal) throws MyCasNoResultException {
		String MYPORTAL_PORTLETS_SERVICE_URL = constantsSingleton.getPortaleURL() + "/secure/rest/admin/addPortlets/";
		return PortletsUtils.addPortlets(idPfPrincipal, "CITT", 0, MYPORTAL_PORTLETS_SERVICE_URL);
	}

	public Boolean updateYG(UtenteInfo utenteInfoRegister) throws MyCasNoResultException {
		Integer idPfPrincipal = utenteInfoRegister.getIdPfPrincipal();
		DeProvincia prov = utenteInfoRegister.getDeProvincia();
		if (prov != null) {
			String MYPORTAL_PORTLETS_SERVICE_URL = constantsSingleton.getPortaleURL()
					+ "/secure/rest/admin/updateAdesioneYG/";
			return YgUtils.updateAdesioneYg(idPfPrincipal, prov.getCodProvincia(), MYPORTAL_PORTLETS_SERVICE_URL);
		}
		return true;
	}

	public UtenteInfo loadUser(Integer idPfPrincipal) throws MyCasNoResultException {
		try {
			UtenteInfo utenteInfo = findById(idPfPrincipal);

			if (utenteInfo.getDeComuneNascita() != null)
				utenteInfo.setDeComuneNascita(deComuneEJB.findById(utenteInfo.getDeComuneNascita().getCodCom()));

			if (utenteInfo.getDeComuneDomicilio() != null)
				utenteInfo.setDeComuneDomicilio(deComuneEJB.findById(utenteInfo.getDeComuneDomicilio().getCodCom()));

			if (utenteInfo.getDeComuneResidenza() != null)
				utenteInfo.setDeComuneResidenza(deComuneEJB.findById(utenteInfo.getDeComuneResidenza().getCodCom()));

			if (utenteInfo.getDeCittadinanza() != null)
				utenteInfo.setDeCittadinanza(
						deCittadinanzaEJB.findById(utenteInfo.getDeCittadinanza().getCodCittadinanza()));

			if (utenteInfo.getDeProvincia() != null)
				utenteInfo.setDeProvincia(deProvinciaEJB.findById(utenteInfo.getDeProvincia().getCodProvincia()));

			if (utenteInfo.getDocumentoSoggiorno() != null) {
				utenteInfo.setDocumentoSoggiorno(
						deTitoloSoggiornoEJB.findById(utenteInfo.getDocumentoSoggiorno().getCodTitoloSoggiorno()));
			}
			return utenteInfo;

		} catch (MyCasNoResultException e) {
			// probabili dati sporchi
			throw new EJBException("Dati sporchi");
		}
	}

	/**
	 * Recupera il CPI di riferimento per l'utente (quello del comune di domicilio oppure quello del capoluogo della
	 * provincia di riferimento).
	 */
	private DeCpi recuperaCpiServiziAmministrativi(UtenteInfo utenteInfo)
			throws MyCasNoResultException, MyCasTooManyResultsException {
		if (utenteInfo.getDeComuneDomicilio().getDeProvincia().equals(utenteInfo.getDeProvincia())) {
			// Se abita nella provincia di riferimento, prendo il CPI del comune di domicilio.
			return utenteInfo.getDeComuneDomicilio().getDeCpi();
		} else {
			// Se abita fuori dalla provincia di riferimento, prendo il CPI del capoluogo della provincia
			// di riferimento.
			DeComune capoluogoRiferimento = deComuneEJB
					.findCapoluogoProvincia(utenteInfo.getDeProvincia().getCodProvincia());
			return capoluogoRiferimento.getDeCpi();
		}
	}

	/**
	 * Recupera l'indirizzo e-mail del CPI di riferimento per l'utente.
	 */
	public String recuperaMailCpiServiziAmministrativi(UtenteInfo utenteInfo) {
		DeCpi cpiServizi = null;
		String result = "";
		try {
			cpiServizi = recuperaCpiServiziAmministrativi(utenteInfo);
			result = cpiServizi.getEmail();
		} catch (MyCasException e) {
			log.error("Errore durante la ricerca del CPI di riferimento: " + e);
		}

		return result;
	}

	/**
	 * Recupera la e-mail per i servizi online del CPI di riferimento per l'utente.
	 */
	public String recuperaMailServiziOnlineCpiServiziAmministrativi(UtenteInfo utenteInfo) {
		DeCpi cpiServizi = null;
		String result = "";
		try {
			cpiServizi = recuperaCpiServiziAmministrativi(utenteInfo);
			result = cpiServizi.getEmailServiziOnline();
		} catch (MyCasException e) {
			log.error("Errore durante la ricerca del CPI di riferimento: " + e);
		}

		return result;
	}

	/**
	 * Recupera la e-mail per l'abilitazione ai servizi amministrativi senza PEC del CPI di riferimento per l'utente.
	 */
	public String recuperaMailAbilitazioneNoPecCpiServiziAmministrativi(UtenteInfo utenteInfo) {
		DeCpi cpiServizi = null;
		String result = "";
		try {
			cpiServizi = recuperaCpiServiziAmministrativi(utenteInfo);
			result = cpiServizi.getEmailAbilitazioneNoPec();
		} catch (MyCasException e) {
			log.error("Errore durante la ricerca del CPI di riferimento: " + e);
		}

		return result;
	}

	public String generateRichiestaToken(UtenteInfo utenteInfo) throws MyCasException {
		utenteInfo.getPfPrincipal().setRichiestaRegForteToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		updateRichiestaToken(utenteInfo);
		updateProfile(utenteInfo);

		return recuperaMailServiziOnlineCpiServiziAmministrativi(utenteInfo);
	}

	public Integer cercaUtentiPerAbilitazioneForteCount(String nome, String cognome, String codiceFiscale,
			String codProvinciaRichiedente, Boolean abilitazionePec) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> query = qb.createQuery(Long.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		Root<UtenteInfo> utenteInfoRoot = query.from(UtenteInfo.class);
		Join<UtenteInfo, PfPrincipal> pfPrincipalJoin = utenteInfoRoot.join(UtenteInfo_.pfPrincipal);
		query.select(qb.count(utenteInfoRoot));

		if (codProvinciaRichiedente != null) {
			Join<UtenteInfo, DeProvincia> provinciaUtenteJoin = utenteInfoRoot.join(UtenteInfo_.deProvincia);
			whereConditions.add(qb.equal(provinciaUtenteJoin.get(DeProvincia_.codProvincia), codProvinciaRichiedente));
		}

		if (!it.eng.sil.myaccount.utils.StringUtils.isEmptyNoBlank(nome)) {
			whereConditions
					.add(qb.like(qb.upper(pfPrincipalJoin.get(PfPrincipal_.nome)), nome.trim().toUpperCase() + "%"));
		}

		if (!it.eng.sil.myaccount.utils.StringUtils.isEmptyNoBlank(cognome)) {
			whereConditions.add(
					qb.like(qb.upper(pfPrincipalJoin.get(PfPrincipal_.cognome)), cognome.trim().toUpperCase() + "%"));
		}

		if (!it.eng.sil.myaccount.utils.StringUtils.isEmptyNoBlank(codiceFiscale)) {
			whereConditions.add(qb.like(qb.upper(utenteInfoRoot.get(UtenteInfo_.codiceFiscale)),
					codiceFiscale.trim().toUpperCase() + "%"));
		}

		whereConditions.add(qb.isNotNull(pfPrincipalJoin.get(PfPrincipal_.flagAbilitaPec)));
		if (abilitazionePec != null) {
			whereConditions.add(qb.equal(pfPrincipalJoin.get(PfPrincipal_.flagAbilitaPec), abilitazionePec));
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> typedQuery = entityMan.createQuery(query);
		return typedQuery.getSingleResult().intValue();
	}

	public List<UtenteInfo> cercaUtentiPerAbilitazioneForte(String nome, String cognome, String codiceFiscale,
			String codProvinciaRichiedente, Boolean abilitazionePec, int first, int pageSize) {
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<UtenteInfo> query = qb.createQuery(UtenteInfo.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		Root<UtenteInfo> utenteInfoRoot = query.from(UtenteInfo.class);
		Join<UtenteInfo, PfPrincipal> pfPrincipalJoin = utenteInfoRoot.join(UtenteInfo_.pfPrincipal);

		if (codProvinciaRichiedente != null) {
			Join<UtenteInfo, DeProvincia> provinciaUtenteJoin = utenteInfoRoot.join(UtenteInfo_.deProvincia);
			whereConditions.add(qb.equal(provinciaUtenteJoin.get(DeProvincia_.codProvincia), codProvinciaRichiedente));
		}

		if (!it.eng.sil.myaccount.utils.StringUtils.isEmptyNoBlank(nome)) {
			whereConditions
					.add(qb.like(qb.upper(pfPrincipalJoin.get(PfPrincipal_.nome)), nome.trim().toUpperCase() + "%"));
		}

		if (!it.eng.sil.myaccount.utils.StringUtils.isEmptyNoBlank(cognome)) {
			whereConditions.add(
					qb.like(qb.upper(pfPrincipalJoin.get(PfPrincipal_.cognome)), cognome.trim().toUpperCase() + "%"));
		}

		if (!it.eng.sil.myaccount.utils.StringUtils.isEmptyNoBlank(codiceFiscale)) {
			whereConditions.add(qb.like(qb.upper(utenteInfoRoot.get(UtenteInfo_.codiceFiscale)),
					codiceFiscale.trim().toUpperCase() + "%"));
		}

		whereConditions.add(qb.isNotNull(pfPrincipalJoin.get(PfPrincipal_.flagAbilitaPec)));
		if (abilitazionePec != null) {
			whereConditions.add(qb.equal(pfPrincipalJoin.get(PfPrincipal_.flagAbilitaPec), abilitazionePec));
		}

		query.orderBy(qb.asc(utenteInfoRoot.get(UtenteInfo_.codiceFiscale)),
				qb.asc(pfPrincipalJoin.get(PfPrincipal_.cognome)), qb.asc(pfPrincipalJoin.get(PfPrincipal_.nome)));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<UtenteInfo> typedQuery = entityMan.createQuery(query);
		typedQuery.setFirstResult(first);
		typedQuery.setMaxResults(pageSize);
		return typedQuery.getResultList();
	}

}
