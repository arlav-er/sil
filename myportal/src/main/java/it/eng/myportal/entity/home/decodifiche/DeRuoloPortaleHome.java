package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeRuoloPortaleDTO;
import it.eng.myportal.entity.PfAbilitazione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeRuoloPortale;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Stateless
public class DeRuoloPortaleHome extends AbstractDecodeHome<DeRuoloPortale, DeRuoloPortaleDTO> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@Override
	public DeRuoloPortale findById(String id) {
		return findById(DeRuoloPortale.class, id);
	}

	public DeRuoloPortaleDTO toDTO(DeRuoloPortale entity) {
		if (entity == null) {
			return null;
		}

		DeRuoloPortaleDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodRuoloPortale());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeRuoloPortale fromDTO(DeRuoloPortaleDTO dto) {
		if (dto == null) {
			return null;
		}

		DeRuoloPortale entity = super.fromDTO(dto);
		entity.setCodRuoloPortale(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	/**
	 * Restituisce le informazioni di profilatura per l'utente passato come parametro
	 * 
	 * @param username
	 *            username dell'utente per il quale si vogliono le informazioni di profilatura
	 * @return l'HashMap contente le informazioni di profilatura
	 */
	public HashMap<String, Map<String, Boolean>> getProfile(String username) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findAbilitatoByUsername(username);
		HashMap<String, Map<String, Boolean>> ret = new HashMap<String, Map<String, Boolean>>();
		for (PfAbilitazione pfAbilitazione : pfPrincipal.getDeRuoloPortale().getAbilitazioneList()) {
			Map<String, Boolean> abilitazione = new HashMap<String, Boolean>();
			abilitazione.put("canAdmin", pfAbilitazione.getFlagAmministrazione());
			abilitazione.put("canDelete", pfAbilitazione.getFlagCancellazione());
			abilitazione.put("canInsert", pfAbilitazione.getFlagInserimento());
			abilitazione.put("canRead", pfAbilitazione.getFlagLettura());
			abilitazione.put("canEdit", pfAbilitazione.getFlagModifica());
			abilitazione.put("canVisibile", pfAbilitazione.getFlagVisibile());
			ret.put(pfAbilitazione.getDeAttivitaPf().getCodAttivitaPf(), abilitazione);
		}
		return ret;
	}

	/**
	 * Restituisce le informazioni di profilatura per l'utente passato come parametro
	 */
	public HashMap<String, Map<TipoAbilitazione, Boolean>> getProfile(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		HashMap<String, Map<TipoAbilitazione, Boolean>> ret = new HashMap<String, Map<TipoAbilitazione, Boolean>>();
		for (PfAbilitazione pfAbilitazione : pfPrincipal.getDeRuoloPortale().getAbilitazioneList()) {
			Map<TipoAbilitazione, Boolean> abilitazione = new HashMap<TipoAbilitazione, Boolean>();
			abilitazione.put(TipoAbilitazione.AMMINISTRAZIONE, pfAbilitazione.getFlagAmministrazione());
			abilitazione.put(TipoAbilitazione.CANCELLAZIONE, pfAbilitazione.getFlagCancellazione());
			abilitazione.put(TipoAbilitazione.INSERIMENTO, pfAbilitazione.getFlagInserimento());
			abilitazione.put(TipoAbilitazione.LETTURA, pfAbilitazione.getFlagLettura());
			abilitazione.put(TipoAbilitazione.MODIFICA, pfAbilitazione.getFlagModifica());
			abilitazione.put(TipoAbilitazione.VISIBILE, pfAbilitazione.getFlagVisibile());
			ret.put(pfAbilitazione.getDeAttivitaPf().getCodAttivitaPf(), abilitazione);
		}
		return ret;
	}

	/**
	 * Restituisce le informazioni di profilatura per il cittadino
	 */
	public HashMap<String, Map<TipoAbilitazione, Boolean>> getProfileCittadino() {
		DeRuoloPortale ruoloCittadino = findById(ConstantsSingleton.DeRuoloPortale.CITTADINO);
		HashMap<String, Map<TipoAbilitazione, Boolean>> ret = new HashMap<String, Map<TipoAbilitazione, Boolean>>();
		for (PfAbilitazione pfAbilitazione : ruoloCittadino.getAbilitazioneList()) {
			Map<TipoAbilitazione, Boolean> abilitazione = new HashMap<TipoAbilitazione, Boolean>();
			abilitazione.put(TipoAbilitazione.AMMINISTRAZIONE, pfAbilitazione.getFlagAmministrazione());
			abilitazione.put(TipoAbilitazione.CANCELLAZIONE, pfAbilitazione.getFlagCancellazione());
			abilitazione.put(TipoAbilitazione.INSERIMENTO, pfAbilitazione.getFlagInserimento());
			abilitazione.put(TipoAbilitazione.LETTURA, pfAbilitazione.getFlagLettura());
			abilitazione.put(TipoAbilitazione.MODIFICA, pfAbilitazione.getFlagModifica());
			abilitazione.put(TipoAbilitazione.VISIBILE, pfAbilitazione.getFlagVisibile());
			ret.put(pfAbilitazione.getDeAttivitaPf().getCodAttivitaPf(), abilitazione);
		}
		return ret;
	}

	/**
	 * Restituisce le informazioni di profilatura per l'azienda
	 */
	public HashMap<String, Map<TipoAbilitazione, Boolean>> getProfileAzienda() {
		DeRuoloPortale ruoloAzienda = findById(ConstantsSingleton.DeRuoloPortale.AZIENDA);
		HashMap<String, Map<TipoAbilitazione, Boolean>> ret = new HashMap<String, Map<TipoAbilitazione, Boolean>>();
		for (PfAbilitazione pfAbilitazione : ruoloAzienda.getAbilitazioneList()) {
			Map<TipoAbilitazione, Boolean> abilitazione = new HashMap<TipoAbilitazione, Boolean>();
			abilitazione.put(TipoAbilitazione.AMMINISTRAZIONE, pfAbilitazione.getFlagAmministrazione());
			abilitazione.put(TipoAbilitazione.CANCELLAZIONE, pfAbilitazione.getFlagCancellazione());
			abilitazione.put(TipoAbilitazione.INSERIMENTO, pfAbilitazione.getFlagInserimento());
			abilitazione.put(TipoAbilitazione.LETTURA, pfAbilitazione.getFlagLettura());
			abilitazione.put(TipoAbilitazione.MODIFICA, pfAbilitazione.getFlagModifica());
			abilitazione.put(TipoAbilitazione.VISIBILE, pfAbilitazione.getFlagVisibile());
			ret.put(pfAbilitazione.getDeAttivitaPf().getCodAttivitaPf(), abilitazione);
		}
		return ret;
	}

	public List<SelectItem> getListItems(boolean addBlank) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			final TypedQuery<DeRuoloPortale> typedQuery = entityManager.createQuery("SELECT a FROM DeRuoloPortale a",
					DeRuoloPortale.class);
			List<DeRuoloPortale> res = typedQuery.getResultList();
			if (addBlank)
				selectItems.add(0, new SelectItem(null, ""));
			for (DeRuoloPortale deRuoloPortale : res) {
				selectItems.add(new SelectItem(deRuoloPortale.getCodRuoloPortale(), deRuoloPortale.getDescrizione()));
			}

		} catch (NoResultException nre) {
			log.error("No result found for type SelectItem using this statement");

		} catch (RuntimeException re) {
			log.error("Cannot find results for type SelectItem using statement: ");
		}
		return selectItems;
	}
}
