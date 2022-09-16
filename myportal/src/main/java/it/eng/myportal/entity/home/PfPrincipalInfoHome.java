//package it.eng.myportal.entity.home;
//
//// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1
//
//import it.eng.myportal.dtos.PfPrincipalInfoDTO;
//import it.eng.myportal.dtos.UtenteDTO;
//import it.eng.myportal.entity.PfPrincipal;
//import it.eng.myportal.entity.PfPrincipalInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//
///**
// * Home object for domain model class PfPrincipal.
// * 
// * @see it.eng.myportal.entity.PfPrincipal
// * @author Rodi A.
// */
//@Stateless
//public class PfPrincipalInfoHome extends AbstractUpdatableHome<PfPrincipalInfo, PfPrincipalInfoDTO> {
//
//	@EJB
//	PfPrincipalHome pfPrincipalHome;
//
//	@EJB
//	UtenteInfoHome utenteInfoHome;
//
//	@EJB
//	AziendaInfoHome aziendaInfoHome;
//
//	private static final String QUERY_BY_EMAIL = " select p " + " from PfPrincipalInfo p where upper(p.email) = :email";
//
//	private static final String QUERY_BY_USERNAME = " select p from PfPrincipalInfo p where p.username = :username";
//
//	/**
//	 * Il codice fiscale lo salviamo sempre UPPERCASED
//	 */
//	@Override
//	public void persist(PfPrincipalInfo transientInstance) {
//		
//		super.persist(transientInstance);
//	}
//
//	@Override
//	public PfPrincipalInfo findById(Integer id) {
//		return findById(PfPrincipalInfo.class, id);
//	}
//
//	public List<PfPrincipalInfo> findByEmail(String email) {
//		List<PfPrincipalInfo> results = entityManager.createQuery(QUERY_BY_EMAIL, PfPrincipalInfo.class)
//				.setParameter("email", email).getResultList();
//
//		return results;
//
//	}
//
//	public List<PfPrincipalInfoDTO> findDTOByEmail(String userOrEmail) {
//		List<PfPrincipalInfoDTO> results = new ArrayList<PfPrincipalInfoDTO>();
//
//		List<PfPrincipalInfo> entities = findByEmail(userOrEmail.toUpperCase());
//
//		for (PfPrincipalInfo pfPrincipalInfo : entities) {
//			results.add(toDTO(pfPrincipalInfo));
//		}
//		
//		return results;
//	}
//
//	public PfPrincipalInfo findByUsername(String username) {
//		List<PfPrincipalInfo> results = entityManager.createQuery(QUERY_BY_USERNAME, PfPrincipalInfo.class)
//				.setParameter("username", username).getResultList();
//		if (results.size() != 1)
//			return null;
//		return results.get(0);
//
//	}
//
//	/**
//	 * Cerca le info associate ad un principal a partire dal parametro
//	 * idPfPrincipal
//	 * 
//	 * @param principalId
//	 *            id del principal del quale si vogliono le info
//	 * @return il dto che rappresenta le informazioni
//	 */
//	public PfPrincipalInfoDTO findDTOByIdPfPrincipal(Integer principalId) {
//		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
//		if (pfPrincipal == null)
//			return null;
//		return toDTO(pfPrincipal.getPfPrincipalInfo());
//	}
//
//	public PfPrincipalInfo findByIdPfPrincipal(Integer principalId) {
//		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
//		if (pfPrincipal == null)
//			return null;
//		return pfPrincipal.getPfPrincipalInfo();
//	}
//
//	@Override
//	public PfPrincipalInfo fromDTO(PfPrincipalInfoDTO dto) {
//		if (dto == null)
//			return null;
//		PfPrincipalInfo entity = super.fromDTO(dto);
//
//		entity.setIdPfPrincipal(dto.getId());
//		entity.setCognome(dto.getCognome());
//		entity.setConfirmationToken(dto.getConfirmationToken());
//		entity.setDomanda(dto.getDomanda());
//		entity.setDtmConfirm(dto.getDtmConfirm());
//		entity.setEmail(dto.getEmail());
//		entity.setNome(dto.getNome());
//		entity.setPasswordToken(dto.getRecuperoPasswordToken());
//		entity.setRegistrazioneForteToken(dto.getRegistrazioneForteToken());
//		entity.setRichiestaRegForteToken(dto.getRichiestaRegForteToken());
//		entity.setRisposta(dto.getRisposta());
//		entity.setStileSelezionato(dto.getStileSelezionato());
//
//		return entity;
//	}
//
//	public PfPrincipalInfoDTO toDTO(PfPrincipalInfo info) {
//		if (info == null) {
//			return null;
//		}
//		
//		PfPrincipalInfoDTO ret = super.toDTO(info);
//		
//		ret.setId(info.getIdPfPrincipal());
//		ret.setNome(info.getNome());
//		ret.setCognome(info.getCognome());
//		ret.setEmail(info.getEmail());
//		ret.setDomanda(info.getDomanda());
//		ret.setRisposta(info.getRisposta());
//		ret.setStileSelezionato(info.getStileSelezionato());
//		ret.setConfirmationToken(info.getConfirmationToken());
//		ret.setDtmConfirm(info.getDtmConfirm());
//		ret.setRecuperoPasswordToken(info.getPasswordToken());
//		ret.setRegistrazioneForteToken(info.getRegistrazioneForteToken());
//		ret.setRichiestaRegForteToken(info.getRichiestaRegForteToken());
//		return ret;
//	}
//
//	/**
//	 * Metodo chiamato dall'operatore CPI quando clicca sul pulsante "Abilita"
//	 * nella pagina di ricerca utente
//	 * 
//	 * @param idLavoratore
//	 *            id del lavoratore da abilitare
//	 * @param token
//	 *            token presente nella mail inviata al lavoratore da confrontare
//	 *            nell'ultima fase della registrazione forte
//	 */
//	public void abilita(Integer idLavoratore, String token) {
//
//		PfPrincipalInfo principalInfo = findByIdPfPrincipal(idLavoratore);
//		principalInfo.setRichiestaRegForteToken(null);
//		principalInfo.setRegistrazioneForteToken(token);
//		merge(principalInfo);
//
//	}
//
//	/**
//	 * Metodo eseguito dalla pagina di conferma della registrazione da parte
//	 * dell'utente
//	 * 
//	 * @param user
//	 *            dati del lavoratore
//	 */
//	public PfPrincipalInfoDTO confermaAbilitazioneForte(UtenteDTO user, String token) {
//
//		PfPrincipalInfo principalInfo = findByIdPfPrincipal(user.getId());
//
//		// Se sono io e ho il token giusto
//		if (principalInfo.getRegistrazioneForteToken() != null
//				&& principalInfo.getRegistrazioneForteToken().equals(token)) {
//			principalInfo.setRegistrazioneForteToken(null);
//			principalInfo.getPfPrincipal().setFlagAbilitatoServizi(true);
//			pfPrincipalHome.merge(principalInfo.getPfPrincipal());
//			PfPrincipalInfo result = merge(principalInfo);
//			return toDTO(result);
//		} else {
//			return null;
//		}
//	}
//}
