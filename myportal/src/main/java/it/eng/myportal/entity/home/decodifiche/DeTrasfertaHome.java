package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTrasfertaDTO;
import it.eng.myportal.entity.decodifiche.DeTrasferta;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * 
 * @author Rodi A.
 */
@Stateless
public class DeTrasfertaHome extends AbstractSuggestibleHome<DeTrasferta, DeTrasfertaDTO> {

	public DeTrasferta findById(final String id) {
		return findById(DeTrasferta.class, id);
	}

	@Override
	public DeTrasfertaDTO toDTO(final DeTrasferta trasferta) {
		if (trasferta == null)
			return null;
		DeTrasfertaDTO ret = super.toDTO(trasferta);
		ret.setId(trasferta.getCodTrasferta());
		ret.setDescrizione(trasferta.getDescrizione());
		return ret;
	}

	@Override
	public DeTrasferta fromDTO(final DeTrasfertaDTO trasferta) {
		if (trasferta == null)
			return null;
		DeTrasferta ret = super.fromDTO(trasferta);
		ret.setCodTrasferta(trasferta.getId());
		ret.setDescrizione(trasferta.getDescrizione());
		return ret;
	}

	public List<DeTrasferta> findDeTrasfertaValidi() {
		TypedQuery<DeTrasferta> query = entityManager.createNamedQuery("findDeTrasferta", DeTrasferta.class)
				.setParameter("parDate", new Date()).setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

}
