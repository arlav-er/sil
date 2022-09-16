package it.eng.myportal.entity.home;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.RegioneDTO;
import it.eng.myportal.dtos.RegisterRegioneDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.Regione;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Home object for domain model class PfPrincipal.
 * 
 * @see it.eng.myportal.entity.PfPrincipal
 * @author Rodi A.
 */
@Stateless
public class RegioneHome extends AbstractUpdatableHome<Regione, RegioneDTO> {

	@EJB
	private PtScrivaniaHome ptScrivaniaHome;

	@EJB
	private PtPortletHome ptPortletHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@Override
	public Regione findById(Integer id) {
		return findById(Regione.class, id);
	}

	public Regione findByUsername(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getRegionesForIdPfPrincipal().size() == 0)
			return null;

		return pf.getRegionesForIdPfPrincipal().iterator().next();
	}

	public RegioneDTO findDTOByUserName(String name) {
		return toDTO(findByUsername(name));
	}

	@Override
	public RegioneDTO toDTO(Regione entity) {
		if (entity == null)
			return null;
		RegioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdRegione());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setStileSelezionato(entity.getPfPrincipal().getStileSelezionato());

		dto.setRegione(deRegioneHome.toDTO(entity.getDeRegione()));

		return dto;
	}

	@Override
	public Regione fromDTO(RegioneDTO dto) {
		if (dto == null)
			return null;
		Regione entity = super.fromDTO(dto);
		entity.setIdRegione(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));

		entity.setDeRegione(deRegioneHome.findById(dto.getRegione().getId()));

		return entity;
	}

	public void register(RegisterRegioneDTO register, Boolean abilitatoSare) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		// valorizzo la provincia: mi serve per il codice gruppo
		if (register.getRegione() != null && register.getRegione().getId() != null)
			register.setRegione(deRegioneHome.findDTOById(register.getRegione().getId()));

		// crea il principal
		PfPrincipal principalProvincia = pfPrincipalHome.registerRegione(register, administrator, now, abilitatoSare,
				true);

		Regione regione = new Regione();
		regione.setPfPrincipal(principalProvincia);
		regione.setPfPrincipalIns(administrator);
		regione.setPfPrincipalMod(administrator);
		regione.setDtmIns(new Date());
		regione.setDtmMod(new Date());
		regione.setDeRegione(deRegioneHome.findById(register.getRegione().getId()));

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.REGIONE);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalProvincia);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principalProvincia);
				iesimaScrivania.setPfPrincipalMod(principalProvincia);

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				ptScrivaniaHome.findPortletsScrivania(principalProvincia.getIdPfPrincipal());
			}
		}

		persist(regione);

	}

}
