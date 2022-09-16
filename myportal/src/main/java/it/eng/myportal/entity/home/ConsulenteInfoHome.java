package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.ConsulenteInfoDTO;
import it.eng.myportal.dtos.RegisterConsulenteDTO;
import it.eng.myportal.entity.ConsulenteInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.home.decodifiche.DeAtpEnteConsulenteHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPraticaHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Home object for domain model class Consulente.
 * 
 * @see it.eng.myportal.entity.Consulente
 * @author Rodi A.
 */
@Stateless
public class ConsulenteInfoHome extends AbstractUpdatableHome<ConsulenteInfo, ConsulenteInfoDTO> {

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	PtPortletHome ptPortletHome;

	@EJB
	DeTipoPraticaHome deTipoPraticaHome;

	@EJB
	DeAtpEnteConsulenteHome deAtpEnteConsulenteHome;

	public ConsulenteInfo findById(Integer id) {
		ConsulenteInfo obj = findById(ConsulenteInfo.class, id);
		return obj;
	}

	@Override
	public ConsulenteInfoDTO toDTO(ConsulenteInfo entity) {
		if (entity == null)
			return null;
		ConsulenteInfoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdPfPrincipal());

		if (entity.getDeTipoPratica() != null && entity.getDeTipoPratica().getCodTipoPratica() != null) {
			dto.setDeTipoPratica(deTipoPraticaHome.toDTO(entity.getDeTipoPratica()));
		}
		dto.setNome(entity.getPfPrincipal().getNome());
		dto.setCognome(entity.getPfPrincipal().getCognome());
		dto.setUsername(entity.getPfPrincipal().getUsername());
		dto.setEmail(entity.getPfPrincipal().getEmail());
		dto.setSindacato(deAtpEnteConsulenteHome.toDTO(entity.getEnteConsulente()));
		dto.setMonteOre(entity.getMonteOre());
		dto.setOreSvolte(entity.getOreSvolte());
		dto.setOpzPrivilegio(entity.getOpzPrivilegio());
		dto.setOpzAtipicoPIva(entity.getOpzAtipicoPIva());
		dto.setInizio(entity.getDtmInizio());
		dto.setFine(entity.getDtmFine());
		// richiedente
		dto.setStileSelezionato(entity.getPfPrincipal().getStileSelezionato());

		return dto;
	}

	@Override
	public ConsulenteInfo fromDTO(ConsulenteInfoDTO dto) {
		if (dto == null)
			return null;
		ConsulenteInfo entity = super.fromDTO(dto);

		entity.setIdPfPrincipal(dto.getId());

		if (dto.getDeTipoPratica() != null && dto.getDeTipoPratica().getId() != null) {
			entity.setDeTipoPratica(deTipoPraticaHome.findById(dto.getDeTipoPratica().getId()));
		}
		entity.setEnteConsulente(deAtpEnteConsulenteHome.findById(dto.getSindacato().getId()));
		entity.setMonteOre(dto.getMonteOre());
		entity.setOreSvolte(dto.getOreSvolte());
		entity.setOpzPrivilegio(dto.getOpzPrivilegio());
		entity.setOpzAtipicoPIva(dto.getOpzAtipicoPIva());
		entity.setDtmInizio(dto.getInizio());
		entity.setDtmFine(dto.getFine());
		entity.getPfPrincipal().setNome(dto.getNome());
		entity.getPfPrincipal().setCognome(dto.getCognome());
		entity.getPfPrincipal().setUsername(dto.getUsername());
		entity.getPfPrincipal().setEmail(dto.getEmail());

		return entity;
	}

	public ConsulenteInfo findByUsername(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getConsulenteInfo() == null)
			return null;

		return pf.getConsulenteInfo();
	}

	public ConsulenteInfoDTO findDTOByUserName(String name) {
		return toDTO(findByUsername(name));
	}

	public void register(RegisterConsulenteDTO register, Boolean abilitatoSare) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		// crea il principal
		PfPrincipal principalConsulente = pfPrincipalHome.registerConsulente(register, administrator, now,
				abilitatoSare, true);

		ConsulenteInfo consulente = new ConsulenteInfo();
		consulente.setIdPfPrincipal(principalConsulente.getIdPfPrincipal());
		consulente.setPfPrincipal(principalConsulente);
		consulente.setPfPrincipalIns(administrator);
		consulente.setPfPrincipalMod(administrator);

		consulente.setDtmIns(new Date());
		consulente.setDtmMod(new Date());

		consulente.setEnteConsulente(deAtpEnteConsulenteHome.findById(register.getSindacato().getId()));
		consulente.setDeTipoPratica(deTipoPraticaHome.findById(register.getDeTipoPraticaDTO().getId()));
		consulente.setMonteOre(register.getMonteOre());
		consulente.setOreSvolte(register.getOreSvolte());
		consulente.setOpzAtipicoPIva(register.getOpzAtipicoPIva());
		consulente.setOpzPrivilegio(register.getOpzPrivilegio());
		consulente.setDtmInizio(register.getDtmInizio());
		consulente.setDtmFine(register.getDtmFine());

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.CONSULEN);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalConsulente);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principalConsulente);
				iesimaScrivania.setPfPrincipalMod(principalConsulente);

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				// ptScrivaniaHome.findPortletsScrivania(principalConsulente.getIdPfPrincipal());
			}
		}

		persist(consulente);

	}

}
