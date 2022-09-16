package it.eng.myportal.entity.home;

// Generated 7-ott-2011 12.00.37 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.SvImmagineDTO;
import it.eng.myportal.entity.SvImmagine;
import it.eng.myportal.entity.home.decodifiche.DeSvSezioneHome;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class SvImmagine.
 * 
 * @see it.eng.myportal.entity.SvImmagine
 * @author Hibernate Tools
 */
@Stateless
public class SvImmagineHome extends AbstractUpdatableHome<SvImmagine, SvImmagineDTO> {

	private static final Log log = LogFactory.getLog(SvImmagineHome.class);

	@EJB
	private DeSvSezioneHome svDeSezioneHome;

	public SvImmagine findById(Integer id) {
		return findById(SvImmagine.class, id);
	}

	@Override
	public SvImmagine fromDTO(SvImmagineDTO dto) {
		if (dto == null)
			return null;
		SvImmagine entity = super.fromDTO(dto);

		entity.setIdSvImmagine(dto.getId());
		entity.setImmagine(dto.getImmagine());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setDeSvSezione(svDeSezioneHome.findById(dto.getCodSezione()));

		return entity;
	}

	@Override
	public SvImmagineDTO toDTO(SvImmagine entity) {
		if (entity == null)
			return null;
		SvImmagineDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdSvImmagine());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setImmagine(entity.getImmagine());
		dto.setCodSezione(entity.getDeSvSezione().getCodSvSezione());
		dto.setDescrizSezione(entity.getDeSvSezione().getDescrizione());

		return dto;
	}

	/**
	 * Restituisce una lista delle immagini a fronte di un id dell'azienda
	 * 
	 * @param idAzienda
	 *            int
	 * @return lista immagini List<SvImmagine>
	 */
	public List<SvImmagine> findByIdAzienda(int idAzienda) {
		log.debug("getting Images from Azienda: " + idAzienda);
		List<SvImmagine> instances = entityManager.createNamedQuery("findByIdPfPrincipal", SvImmagine.class)
				.setParameter("id", idAzienda).getResultList();
		log.debug("get Images from Azienda: " + idAzienda);
		return instances;
	}

	/**
	 * Restituisce una lista delle immagini in formato DTO da passare al backing
	 * bean a fronte di un id dell'azienda
	 * 
	 * @param idAzienda
	 *            int
	 * @return List<SvImmagineDTO>
	 */
	public List<SvImmagineDTO> getAllImg(int idAzienda) {
		List<SvImmagine> list = findByIdAzienda(idAzienda);
		List<SvImmagineDTO> ret = new ArrayList<SvImmagineDTO>();
		for (SvImmagine svImmagine : list) {
			SvImmagineDTO dto = toDTO(svImmagine);
			ret.add(dto);
		}
		return ret;
	}

	/**
	 * Restituisce l'oggetto DTO SvImmagineDTO a fronte del codice Sezione
	 * 
	 * @param codSezione
	 *            String
	 * @return SvImmagineDTO
	 */
	public SvImmagineDTO findByCodSezione(String codSezione) {
		log.debug("getting SvImmagineDTO from codSezione: " + codSezione);
		SvImmagine svImmagine = (SvImmagine) entityManager.createNamedQuery("findByCodSezione")
				.setParameter("cod", codSezione).getSingleResult();
		SvImmagineDTO svImmagineDTO = toDTO(svImmagine);
		log.debug("get SvImmagineDTO from codSezione: " + codSezione);
		return svImmagineDTO;
	}
}
