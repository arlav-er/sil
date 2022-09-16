package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.OrEdizioneAvviataDTO;
import it.eng.myportal.entity.OrEdizioneAvviata;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class OrEdizioneAvviataHome extends AbstractUpdatableHome<OrEdizioneAvviata, OrEdizioneAvviataDTO> {

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	OrCorsoHome orCorsoHome;

	@Override
	public OrEdizioneAvviata findById(Integer id) {
		return findById(OrEdizioneAvviata.class, id);
	}

	@Override
	public OrEdizioneAvviataDTO toDTO(OrEdizioneAvviata entity) {
		if (entity == null)
			return null;
		OrEdizioneAvviataDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdEdizioneAvviata());
		dto.setCodComune(entity.getDeComune().getCodCom());
		dto.setStrComune(entity.getDeComune().getDenominazione());
		String codProvincia = deComuneHome.findDTOById(entity.getDeComune().getCodCom()).getIdProvincia();
		String targa = deProvinciaHome.findDTOById(codProvincia).getTarga();
		dto.setTarga(targa);
		dto.setIdOrCorso(entity.getOrCorso().getIdOrCorso());

		return dto;
	}

	@Override
	public OrEdizioneAvviata fromDTO(OrEdizioneAvviataDTO dto) {
		if (dto == null)
			return null;
		OrEdizioneAvviata entity = super.fromDTO(dto);
		entity.setDeComune(deComuneHome.findById(dto.getCodComune()));
		entity.setOrCorso(orCorsoHome.findById(dto.getIdOrCorso()));

		return entity;
	}
}
