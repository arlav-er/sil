package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.dtos.RegisterProvinciaDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
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
public class ProvinciaHome extends AbstractUpdatableHome<Provincia, ProvinciaDTO> {

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

	public Provincia findById(Integer id) {
		return findById(Provincia.class, id);
	}

	public Provincia findByIdPfPrincipal(Integer idPfPrincipal) {
		PfPrincipal pf = pfPrincipalHome.findById(idPfPrincipal);
		if (pf == null || pf.getProvinciasForIdPfPrincipal().size() == 0) {
			return null;
		}
		return pf.getProvinciasForIdPfPrincipal().iterator().next();
	}

	public Provincia findByUsername(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getProvinciasForIdPfPrincipal().size() == 0)
			return null;

		return pf.getProvinciasForIdPfPrincipal().iterator().next();
	}

	public ProvinciaDTO findDTOByUserName(String name) {
		return toDTO(findByUsername(name));
	}

	@Override
	public ProvinciaDTO toDTO(Provincia entity) {
		if (entity == null)
			return null;
		ProvinciaDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdProvincia());
		PfPrincipal pfPrincipal = entity.getPfPrincipal();
		dto.setIdPfPrincipal(pfPrincipal.getIdPfPrincipal());
		dto.setStileSelezionato(pfPrincipal.getStileSelezionato());

		dto.setProvincia(deProvinciaHome.toDTO(entity.getDeProvincia()));

		dto.setFlagLavoro(entity.getFlagLavoro());
		dto.setFlagTematica(entity.getFlagTematica());
		dto.setNome(entity.getPfPrincipal().getNome());
		dto.setCognome(entity.getPfPrincipal().getCognome());
		dto.setUsername(entity.getPfPrincipal().getUsername());
		dto.setEmail(entity.getPfPrincipal().getEmail());
		dto.setDtInizio(entity.getDtmInizio());
		dto.setDtFine(entity.getDtmFine());
		dto.setOpzPrivilegio(entity.getOpzPrivilegio());

		return dto;
	}

	@Override
	public Provincia fromDTO(ProvinciaDTO dto) {
		if (dto == null)
			return null;
		Provincia entity = super.fromDTO(dto);
		entity.setIdProvincia(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setDeProvincia(deProvinciaHome.findById(dto.getProvincia().getId()));
		entity.getPfPrincipal().setStileSelezionato(dto.getStileSelezionato());
		entity.setFlagLavoro(dto.getFlagLavoro());
		entity.setFlagTematica(dto.getFlagTematica());
		entity.getPfPrincipal().setNome(dto.getNome());
		entity.getPfPrincipal().setCognome(dto.getCognome());
		entity.getPfPrincipal().setEmail(dto.getEmail());
		entity.setOpzPrivilegio(dto.getOpzPrivilegio());
		entity.setDtmInizio(dto.getDtInizio());
		entity.setDtmFine(dto.getDtFine());
		entity.setOpzPrivilegio(dto.getOpzPrivilegio());

		return entity;
	}

	public void register(RegisterProvinciaDTO register, Boolean abilitatoSare) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		PfPrincipal principal = new PfPrincipal();
		principal.setFlagAbilitato(false);
		principal.setFlagAbilitatoSare(abilitatoSare);
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
		principal.setDtFineValidita(DateUtils.addDays(now, 30));
		principal.setStileSelezionato("myportal");
		principal.setConfirmationToken(register.getActivateToken());
		principal.setIndirizzoUtente(register.getIndirizzo());
		principal.setTelefonoUtente(register.getTelefono());
		principal.setPrivacy("Y");
		principal.setNumTentativi(0);
		principal.setDeRuoloPortale(deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.PROVINCIA));

		pfPrincipalHome.persist(principal);

		Provincia provincia = new Provincia();
		provincia.setPfPrincipal(principal);
		provincia.setPfPrincipalIns(administrator);
		provincia.setPfPrincipalMod(administrator);
		provincia.setDtmIns(new Date());
		provincia.setDtmMod(new Date());
		provincia.setDeProvincia(deProvinciaHome.findById(register.getProvincia().getId()));
		provincia.setFlagLavoro(("Y").equalsIgnoreCase(register.getFlgLavoro()) ? true : false);
		provincia.setFlagTematica(("Y").equalsIgnoreCase(register.getFlgTematica()) ? true : false);

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.PROVINCIA);

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

		persist(provincia);

	}
}
