package it.eng.myportal.entity.home;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.PfIdentityProvider;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.enums.TipoProvider;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class PfPrincipal.
 * 
 * @see it.eng.myportal.entity.PfPrincipal
 * @author Rodi A.
 */
@Stateless
public class PfIdentityProviderHome extends AbstractHibernateHome<PfPrincipal, Integer> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@Override
	public PfPrincipal findById(Integer id) {
		return findById(PfPrincipal.class, id);
	}

	/**
	 * Metodo utilizzato all'atto dell'autenticazione tramite IDP. Il metodo
	 * verifica se l'account ha già acceduto in precedenza a MyPortal e di
	 * conseguenza ha già un account associato o se, al contrario, non esiste.
	 * 
	 * @param identifier
	 *            identificativo univoco per quell'utente dell'IDP
	 * @param provider
	 *            provider dal quale proviene l'utente
	 * @return il DTO dell'utente registrato su MyPortal.
	 */
	public UtenteDTO isRegistered(String identifier, TipoProvider provider) {
		return null;
	}

	/**
	 * Metodo di test
	 * 
	 * @param principalId
	 */
	public PfIdentityProvider create(Integer principalId, TipoProvider tipoProvider,
			Map<String, ? extends Object> providerParameters) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		PfIdentityProvider provider = new PfIdentityProvider();
		provider.setCodTipoProvider(tipoProvider);
		provider.setDtmIns(new Date());
		provider.setDtmMod(new Date());
		provider.setPfPrincipal(pfPrincipal);
		provider.setPfPrincipalIns(pfPrincipal);
		provider.setPfPrincipalMod(pfPrincipal);
		provider.setData(toData(tipoProvider,providerParameters));
		entityManager.persist(provider);
		return provider;
	}

	public PfIdentityProvider createForVda(String newUsername, Integer principalId, TipoProvider tipoProvider,
			Map<String, ? extends Object> providerParameters) {

		Calendar c1 = GregorianCalendar.getInstance();
		c1.set(2100, 11, 31); 
		Date scadIllimitata = c1.getTime();
		
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		
		pfPrincipal.setUsername(newUsername);
		pfPrincipal.setPassWord("xxx");
		pfPrincipal.setFlagAbilitato(true);
		pfPrincipal.setFlagAbilitatoServizi(true);
		pfPrincipal.setDtFineValidita(scadIllimitata);
		pfPrincipal.setDtmMod(new Date());
		pfPrincipal.setDtScadenza(scadIllimitata);
		pfPrincipal.setPfPrincipalMod(pfPrincipal);
		
		PfPrincipal merged = entityManager.merge(pfPrincipal);
		
		
		PfIdentityProvider provider = new PfIdentityProvider();
		provider.setCodTipoProvider(tipoProvider);
		provider.setDtmIns(new Date());
		provider.setDtmMod(new Date());
		provider.setPfPrincipal(merged);
		provider.setPfPrincipalIns(merged);
		provider.setPfPrincipalMod(merged);
		provider.setData(toData(tipoProvider,providerParameters));

		entityManager.persist(provider);
		return provider;

	}

	
	
	/**
	 * Verifica se esiste già un account legato al provider, utilizzando i dati
	 * forniti
	 * 
	 * @param tipoProvider
	 * @param headers
	 * @return l'utente, se esiste, null altrimenti
	 */
	public PfPrincipalDTO getAccount(TipoProvider tipoProvider, Map<String, ? extends Object> headers) {

		String data = toData(tipoProvider, headers);
		TypedQuery<PfIdentityProvider> query = entityManager.createNamedQuery("FIND_BY_DATA", PfIdentityProvider.class);
		query.setParameter("data", data);
		query.setParameter("provider", tipoProvider);
		List<PfIdentityProvider> providers = query.getResultList();
		if (providers != null && providers.size() == 1) {
			return pfPrincipalHome.toDTO(providers.get(0).getPfPrincipal());
		} else {
			return null;
		}
	}

	private String toData(TipoProvider tipoAccount, Map<String, ? extends Object> parametersMap) {
		switch (tipoAccount) {
		case SPID: {
			return "SPID_" + parametersMap.get(ConstantsSingleton.Spid.UNIQUE_ID);
		}
		case ICAR: {
			return "ICAR_" + parametersMap.get(ConstantsSingleton.Icar.UNIQUE_ID);
		}
		case FEDERA: {
			String domain = (String) parametersMap.get(ConstantsSingleton.Federa.DOMAIN);
			String codiceFiscale = (String) parametersMap.get(ConstantsSingleton.Federa.CODICEFISCALE);
			return ConstantsSingleton.Federa.DOMAIN + "=" + domain + ";" + ConstantsSingleton.Federa.CODICEFISCALE
					+ "=" + codiceFiscale;
		}
		case TWITTER: {
			String id = (String) parametersMap.get(ConstantsSingleton.Auth.ID);
			return ConstantsSingleton.Auth.ID + "=" + id;
		}
		case GOOGLE: {
			String id = (String) parametersMap.get(ConstantsSingleton.Auth.ID);
			return ConstantsSingleton.Auth.ID + "=" + id;
		}
		case LINKEDIN: {
			String id = (String) parametersMap.get(ConstantsSingleton.Auth.ID);
			return ConstantsSingleton.Auth.ID + "=" + id;
		}
		case FACEBOOK: {
			String id = (String) parametersMap.get(ConstantsSingleton.Auth.ID);
			return ConstantsSingleton.Auth.ID + "=" + id;
		}
		
		default:
			throw new MyPortalException("Provider non supportato", true);
		}
	}
	
	// executing "FIND_BY_PFP_ID" Named Query in PfIndentityProvider
	//takes PfPrincipal id as a parameter
	public List<PfIdentityProvider> findByPFPId(int PFPId) {
		TypedQuery<PfIdentityProvider> query = entityManager.createNamedQuery("FIND_BY_PFP_ID", PfIdentityProvider.class);
		query.setParameter("PFPId", PFPId);
		return query.getResultList();
	}

}
