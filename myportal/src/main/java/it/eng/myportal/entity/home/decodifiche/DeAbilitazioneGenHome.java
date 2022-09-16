package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAbilitazioneGenDTO;
import it.eng.myportal.entity.decodifiche.DeAbilitazioneGen;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class DeAbilitazioneGen.
 * 
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAbilitazioneGen
 * @author Rodi A.
 */
@Stateless
public class DeAbilitazioneGenHome extends AbstractDecodeHome<DeAbilitazioneGen, DeAbilitazioneGenDTO> {

	private static final String QUERY_SEL_PATENTI = "select new javax.faces.model.SelectItem(c.codDeAbilitazioneGen,c.descrizione ) from DeAbilitazioneGen c where c.deTipoAbilitazioneGen.codTipoAbilitazioneGen = 'PG'";

	public DeAbilitazioneGen findById(String id) {
		return findById(DeAbilitazioneGen.class, id);
	}

	public List<SelectItem> selectPatentiItems(boolean addBlank) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(QUERY_SEL_PATENTI, SelectItem.class).setHint("org.hibernate.cacheable", true);
		return getListItems(typedQuery, addBlank);
	}

}
