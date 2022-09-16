package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoPraticaDTO;
import it.eng.myportal.entity.decodifiche.DeTipoPratica;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class DeTipoPratica.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoPratica
 * @author Rodi A.
 */
@Stateless
public class DeTipoPraticaHome extends AbstractSuggestibleHome<DeTipoPratica, DeTipoPraticaDTO> {

	public DeTipoPratica findById(String id) {
		return findById(DeTipoPratica.class, id);
	}

	@Override
	public DeTipoPraticaDTO toDTO(DeTipoPratica contratto) {
		if (contratto == null)
			return null;
		DeTipoPraticaDTO ret = super.toDTO(contratto);
		ret.setId(contratto.getCodTipoPratica());
		ret.setDescrizione(contratto.getDescrizione());
		return ret;
	}

	@Override
	public DeTipoPratica fromDTO(DeTipoPraticaDTO contratto) {
		if (contratto == null)
			return null;
		DeTipoPratica ret = super.fromDTO(contratto);
		ret.setCodTipoPratica(contratto.getId());
		ret.setDescrizione(contratto.getDescrizione());
		return ret;
	}
	
	private static final String QUERY = "select new javax.faces.model.SelectItem(c.codTipoPratica,c.descrizione) from DeTipoPratica c where c.codTipoPratica in (:tipiPratica)";

	public List<SelectItem> getPartitaIvaListItems(boolean addBlank) {
		TypedQuery<SelectItem> q = entityManager.createQuery(QUERY, SelectItem.class);
		q.setParameter("tipiPratica", Arrays.asList("CNS_CONT", "CNS_PREV", "CNS_FISC", "AUT_IMPR", "AMB_SIC",
				"PRIVACY"));
		return getListItems(q, addBlank);
	}

	public List<SelectItem> getAtipiciListItems(boolean addBlank) {
		TypedQuery<SelectItem> q = entityManager.createQuery(QUERY, SelectItem.class);
		q.setParameter("tipiPratica", Arrays.asList("CNS_CONT", "CNS_PREV", "CNS_FISC", "AUT_IMPR"));
		return getListItems(q, addBlank);
	}

}
