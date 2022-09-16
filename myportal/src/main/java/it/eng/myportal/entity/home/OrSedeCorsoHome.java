package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.OrSedeCorsoDTO;
import it.eng.myportal.entity.OrSedeCorso;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class OrSedeCorsoHome extends AbstractUpdatableHome<OrSedeCorso, OrSedeCorsoDTO> {

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	OrCorsoHome orCorsoHome;

	@Override
	public OrSedeCorso findById(Integer id) {
		return findById(OrSedeCorso.class, id);
	}

	@Override
	public OrSedeCorsoDTO toDTO(OrSedeCorso entity) {
		if (entity == null)
			return null;
		OrSedeCorsoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdOrSedeCorso());
		dto.setCodComune(entity.getDeComune().getCodCom());
		dto.setStrComune(entity.getDeComune().getDenominazione());
		String codProvincia = deComuneHome.findDTOById(entity.getDeComune().getCodCom()).getIdProvincia();
		String targa = deProvinciaHome.findDTOById(codProvincia).getTarga();
		dto.setTarga(targa);
		dto.setIdOrCorso(entity.getOrCorso().getIdOrCorso());
		dto.setNominativoReferente(entity.getNominativoReferente());
		dto.setTelefonoReferente(entity.getTelefonoReferente());
		dto.setEmailReferente(entity.getEmailReferente());

		return dto;
	}

	@Override
	public OrSedeCorso fromDTO(OrSedeCorsoDTO dto) {
		if (dto == null)
			return null;
		OrSedeCorso entity = super.fromDTO(dto);

		entity.setDeComune(deComuneHome.findById(dto.getCodComune()));
		entity.setOrCorso(orCorsoHome.findById(dto.getIdOrCorso()));
		entity.setNominativoReferente(dto.getNominativoReferente());
		entity.setTelefonoReferente(dto.getTelefonoReferente());
		entity.setEmailReferente(dto.getEmailReferente());

		return entity;
	}
}
