package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CoordinatoreInfoDTO;
import it.eng.myportal.dtos.RegisterCoordinatoreDTO;
import it.eng.myportal.entity.CoordinatoreInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Home object for domain model class Coordinatore.
 * 
 * @see it.eng.myportal.entity.Coordinatore
 * @author Rodi A.
 */
@Stateless
public class CoordinatoreInfoHome extends AbstractUpdatableHome<CoordinatoreInfo, CoordinatoreInfoDTO> {

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	PtPortletHome ptPortletHome;

	public CoordinatoreInfo findById(Integer id) {
		CoordinatoreInfo obj = findById(CoordinatoreInfo.class, id);
		return obj;
	}

	@Override
	public CoordinatoreInfoDTO toDTO(CoordinatoreInfo entity) {
		if (entity == null)
			return null;
		CoordinatoreInfoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdPfPrincipal());
		dto.setPrincipal(pfPrincipalHome.toDTO(entity.getPfPrincipal()));

		// richiedente
		dto.setNome(entity.getNome());
		dto.setCognome(entity.getCognome());
		dto.setStileSelezionato(entity.getPfPrincipal().getStileSelezionato());
		dto.setEmail(entity.getEmail());
		dto.setInizio(entity.getDtmInizio());
		dto.setFine(entity.getDtmFine());
		dto.setPrivilegio(entity.getOpzPrivilegio());

		return dto;
	}

	@Override
	public CoordinatoreInfo fromDTO(CoordinatoreInfoDTO dto) {
		if (dto == null)
			return null;
		CoordinatoreInfo entity = super.fromDTO(dto);

		entity.setIdPfPrincipal(dto.getId());

		entity.setNome(dto.getNome());
		entity.setCognome(dto.getCognome());
		entity.setEmail(dto.getEmail());
		entity.setDtmInizio(dto.getInizio());
		entity.setDtmFine(dto.getFine());
		entity.setOpzPrivilegio(dto.getPrivilegio());

		return entity;
	}

	public CoordinatoreInfo findByUsername(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getCoordinatoreInfo() == null)
			return null;

		return pf.getCoordinatoreInfo();
	}

	public CoordinatoreInfoDTO findDTOByUserName(String name) {
		return toDTO(findByUsername(name));
	}

	public void register(RegisterCoordinatoreDTO register, Boolean abilitatoSare) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		// crea il principal
		PfPrincipal principalCoordinatore = pfPrincipalHome.registerCoordinatore(register, administrator, now,
				abilitatoSare, true);

		CoordinatoreInfo coordinatore = new CoordinatoreInfo();
		coordinatore.setIdPfPrincipal(principalCoordinatore.getIdPfPrincipal());
		coordinatore.setPfPrincipal(principalCoordinatore);
		coordinatore.setPfPrincipalIns(administrator);
		coordinatore.setPfPrincipalMod(administrator);
		coordinatore.setNome(register.getNome());
		coordinatore.setCognome(register.getCognome());
		coordinatore.setEmail(register.getEmail());

		coordinatore.setDtmIns(new Date());
		coordinatore.setDtmMod(new Date());

		coordinatore.setOpzPrivilegio(register.getOpzPrivilegio());
		coordinatore.setDtmInizio(register.getDtmInizio());
		coordinatore.setDtmFine(register.getDtmFine());

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.COORDINA);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalCoordinatore);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principalCoordinatore);
				iesimaScrivania.setPfPrincipalMod(principalCoordinatore);

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				// ptScrivaniaHome.findPortletsScrivania(principalCoordinatore.getIdPfPrincipal());
			}
		}

		persist(coordinatore);

	}

}
