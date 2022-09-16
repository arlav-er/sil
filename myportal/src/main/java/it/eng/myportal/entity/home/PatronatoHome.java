package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.PatronatoDTO;
import it.eng.myportal.dtos.RegisterPatronatoDTO;
import it.eng.myportal.entity.PatrAbiProvincia;
import it.eng.myportal.entity.Patronato;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Classe Home per la provincia. TODO aggiornare appena c'è la tabella
 * 
 * @author Rodi A.
 * 
 */
@Stateless
public class PatronatoHome extends AbstractUpdatableHome<Patronato, PatronatoDTO> {

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	private PtScrivaniaHome ptScrivaniaHome;

	@EJB
	private PtPortletHome ptPortletHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@EJB
	PatrAbiProvinciaHome patrAbiProvinciaHome;

	public Patronato findById(Integer id) {
		return findById(Patronato.class, id);
	}

	public Patronato findByIdPfPrincipal(Integer idPfPrincipal) {
		PfPrincipal pf = pfPrincipalHome.findById(idPfPrincipal);
		if (pf == null || pf.getPatronatosForIdPfPrincipal().size() == 0) {
			return null;
		}
		return pf.getPatronatosForIdPfPrincipal().iterator().next();
	}

	public Patronato findByUsername(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getPatronatosForIdPfPrincipal().size() == 0)
			return null;

		return pf.getPatronatosForIdPfPrincipal().iterator().next();
	}

	public PatronatoDTO findDTOByUserName(String name) {
		return toDTO(findByUsername(name));
	}

	public PatronatoDTO findDTOByIdPfPrincipal(Integer idPfPrincipal) {
		return toDTO(findByIdPfPrincipal(idPfPrincipal));
	}

	@Override
	public PatronatoDTO toDTO(Patronato entity) {
		if (entity == null)
			return null;
		PatronatoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdPatronato());
		PfPrincipal pfPrincipal = entity.getPfPrincipal();
		dto.setIdPfPrincipal(pfPrincipal.getIdPfPrincipal());
		dto.setProvinciaRif(deProvinciaHome.toDTO(entity.getDeProvinciaRif()));
		dto.setDenominazione(entity.getDenominazione());

		return dto;
	}

	@Override
	public Patronato fromDTO(PatronatoDTO dto) {
		if (dto == null)
			return null;
		Patronato entity = super.fromDTO(dto);
		entity.setIdPatronato(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setDeProvinciaRif(deProvinciaHome.findById(dto.getProvinciaRif().getId()));
		entity.setDenominazione(dto.getDenominazione());

		return entity;
	}

	/**
	 * Registra un nuovo utente di tipo "Patronato".
	 * 
	 * @param register
	 *            L'utente da registrare.
	 */
	public void register(RegisterPatronatoDTO register) {
		// L'utente che registra quello nuovo è l'amministratore.
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		// Creo il PfPrincipal del nuovo utente.
		PfPrincipal principal = new PfPrincipal();
		principal.setFlagAbilitato(false);
		principal.setFlagAbilitatoSare(false);
		principal.setFlagAbilitatoServizi(false);
		principal.setRichiestaRegForteToken(null);
		principal.setUsername(register.getUsername());
		principal.setPassWord(Utils.SHA1.encrypt(register.getPassword()));

		principal.setDtmIns(now);
		principal.setDtmMod(now);
		principal.setDtScadenza(DateUtils.addDays(now, -1));
		principal.setPfPrincipalIns(administrator);
		principal.setPfPrincipalMod(administrator);

		principal.setCognome(register.getCognome());
		principal.setNome(register.getNome());
		principal.setEmail(register.getEmail());
		principal.setDomanda(register.getDomanda());
		principal.setRisposta(register.getRisposta());
		principal.setDtFineValidita(DateUtils.addDays(now, -1));
		principal.setStileSelezionato("myportal");
		principal.setConfirmationToken(register.getActivateToken());
		principal.setIndirizzoUtente(register.getIndirizzo());
		principal.setTelefonoUtente(register.getTelefono());
		principal.setPrivacy("Y");
		principal.setNumTentativi(0);
		principal.setDeRuoloPortale(deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.PATRONATO));

		pfPrincipalHome.persist(principal);

		// Creo la entry nella tabella Patronato per il nuovo utente.
		Patronato patronato = new Patronato();
		patronato.setPfPrincipal(principal);
		patronato.setPfPrincipalIns(administrator);
		patronato.setPfPrincipalMod(administrator);
		patronato.setDtmIns(new Date());
		patronato.setDtmMod(new Date());
		patronato.setDeProvinciaRif(deProvinciaHome.findById(register.getProvincia().getId()));
		patronato.setDenominazione(register.getDenominazione());

		// Creo la homepage per il nuovo utente.
		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.PATRONATO);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principal);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principal);
				iesimaScrivania.setPfPrincipalMod(principal);

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);
				ptScrivaniaHome.findPortletsScrivania(principal.getIdPfPrincipal());
			}
		}

		// Faccio la persist finale del patronato.
		persist(patronato);

		// Creo le entry nella tabella PatrAbiProivincia per le altre province del patronato.
		List<DeProvinciaDTO> altreProvince = register.getAltreProvince();
		for (DeProvinciaDTO altraProvincia : altreProvince) {
			DeProvincia provinciaEntity = deProvinciaHome.findById(altraProvincia.getId());
			PatrAbiProvincia patrAbiProvinciaEntity = new PatrAbiProvincia();

			patrAbiProvinciaEntity.setDtmIns(new Date());
			patrAbiProvinciaEntity.setDtmMod(new Date());
			patrAbiProvinciaEntity.setPfPrincipalIns(administrator);
			patrAbiProvinciaEntity.setPfPrincipalMod(administrator);
			patrAbiProvinciaEntity.setPatronato(patronato);
			patrAbiProvinciaEntity.setDeProvincia(provinciaEntity);
			patrAbiProvinciaHome.persist(patrAbiProvinciaEntity);
		}
	}
}
