package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.entity.home.local.IHasPrimaryKeyHome;

/**
 * Classe Home astratta che contiene una serie di metodi generici e comuni a tutte le Entity e i relativi Home.
 * 
 * @author Rodi A.
 * 
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 * @param <PKType>
 *            Classe della PrimaryKey.
 */
public abstract class AbstractHome<Entity, DTO extends IHasPrimaryKey<PKType>, PKType>
		extends AbstractHibernateHome<Entity, PKType> implements IHasPrimaryKeyHome<DTO, PKType> {

	protected final static String DTO_PACKAGE_PREFIX = "it.eng.myportal.dtos.";
	protected final static String ENTITY_PACKAGE_PREFIX = "it.eng.myportal.entity.";
	protected final static String DTO_SUFFIX = "DTO";
	private static final String QUERY_ASTERISK = ":*";

	/**
	 * Cerca un elemento su DB a partire dalla chiave primaria e ne restituisce il DTO
	 * 
	 * @param id
	 *            PKType chiave primaria
	 * @return il DTO dell'oggetto trovato. nel caso avvengano errori durante l'estrazione
	 */
	@Override
	public DTO findDTOById(PKType id) {
		return toDTO(findById(id));
	}

	/**
	 * Funzione introdotta nel 2020 per semplificare senza l'uso dei DTO
	 * Entity nelle pagine, detached, non e` reato
	 */
	public Entity findById(PKType id) {
		Entity pers = findById(id);
		if (pers == null) {
			return null;
		}
		return (pers);
	}

	/**
	 * Converte in DTO una entity. E' possibile passare valori null.
	 * 
	 * @param entity
	 * @return l'entity convertita in dto, null se l'entity è null.
	 */
	public DTO toDTO(Entity entity) {
		throw new UnsupportedOperationException("Metodo toDTO() per la classe " + entity.getClass().getSimpleName()
				+ " non implementato. Devi creare i due metodi toDTO() e fromDTO() nella classe "
				+ entity.getClass().getSimpleName() + "Home cretino!");
	}

	/**
	 * <b>Attenzione!<br/>
	 * Non richiamare questo metodo direttamente dai BackingBean in quanto restituisce un'Entity e non un DTO</b><br/>
	 * 
	 * @param dto
	 * @return l'entity creata a partire dal DTO. Null se il DTO è null.
	 */
	public Entity fromDTO(DTO dto) {
		throw new UnsupportedOperationException("Metodo fromDTO() per la classe " + dto.getClass().getSimpleName()
				+ " non implementato. Devi creare i due metodi toDTO() e fromDTO() nella Home adatta, cretino!");
	}

	/**
	 * Salva i dati partendo dal DTO.<br/>
	 * Crea una entity, ne esegue la persist su DB<br/>
	 * e quindi restituisce un DTO aggiornato.<br/>
	 * Attenzione! Non vengono aggiornati i dati del DTO passato come parametro!<br/>
	 * E' necessario recuperare il DTO restituito dal metodo ed andarlo a sostituire al precedente.<br/>
	 * E' inoltre quindi possibile confrontare il DTO 'prima' del salvataggio e quello 'dopo'
	 * 
	 * @param data
	 *            DTO
	 * @return DTO
	 */
	@Override
	public DTO persistDTO(DTO data, Integer idPrincipalIns) {
		Entity entity = fromDTO(data);
		persist(entity);
		return toDTO(entity);
	}

	/**
	 * Aggiorna i dati partendo dal DTO
	 * 
	 * @param data
	 *            DTO
	 * @return DTO
	 */
	@Override
	public DTO mergeDTO(DTO data, Integer idPrincipalMod) {
		Entity entity = fromDTO(data);
		merge(entity);
		return toDTO(entity);
	}

	/**
	 * Restituisce tutte le righe della tabella corrispondente all'entity concreta che invoca il metodo.
	 * 
	 * @return tutte le righe in tabella
	 */
	public List<DTO> findAllDTO() {
		List<Entity> rows = findAll();
		List<DTO> ret = new ArrayList<DTO>();
		for (Entity entity : rows) {
			ret.add(toDTO(entity));
		}
		return ret;
	}

	/**
	 * @param query
	 *            la query che deve essere eseguita.
	 * @return List<DTO> lista dei risultati della query trasformati in DTO
	 * 
	 */
	protected List<DTO> findDTOByQuery(TypedQuery<Entity> query) {
		List<Entity> result = null;

		result = query.getResultList();

		final List<DTO> ret = new ArrayList<DTO>();
		for (Entity entity : result) {
			ret.add(toDTO(entity));
		}

		return ret;
	}

	/**
	 * @param query
	 *            la query che deve essere eseguita.
	 * @return List<DTO> lista dei risultati della query
	 * 
	 */
	protected List<Entity> findByQuery(TypedQuery<Entity> query) {
		List<Entity> result = null;
		result = query.getResultList();

		final List<Entity> ret = new ArrayList<Entity>();

		return ret;
	}

	/**
	 * @param query
	 *            la query che deve essere eseguita.
	 * @return DTO il singolo risultato
	 * @see it.eng.myportal.entity.home.local.IDecodeHome#findBySuggestion(java.lang.String)
	 */
	protected DTO findSingleDTOByQuery(TypedQuery<Entity> query) {
		Entity result = null;

		result = query.getSingleResult();

		return toDTO(result);
	}

	/**
	 * Formatta la stringa passata in input per essere utilizzata in una tsquery. La stringa in input viene divisa in
	 * diverse parti usando lo spazio come separatore, ad ogni parte ottenuta viene aggiunto il suffisso ":*" ed infine
	 * tra una parte e l'altra viene aggiunto il simbolo "&". L'output e' la stringa ottenuta concatenando le parti
	 * ottunete in precedenza.
	 * 
	 * Es data la stringa "sono una stringa", l'output e' "sono:* & una:* & stringa:*"
	 * 
	 * @param param
	 *            la stringa da processare
	 * @return una stringa adatta per le tsquery
	 */
	protected String splitStringForTSQuery(String param) {
		String result = null;
		String[] paramParts = param.split(" ");
		if (paramParts.length <= 1) {
			result = param.trim() + QUERY_ASTERISK;
		} else if (paramParts.length > 1) {
			StringBuilder sbq = new StringBuilder();
			for (int i = 0; i < paramParts.length; i++) {
				if (i != 0) {
					// prima di ogni parte, che non sia la prima, aggiungo
					// l'operatore &
					sbq.append(":* & ");
				}
				sbq.append(paramParts[i]);
			}
			sbq.append(QUERY_ASTERISK);
			result = sbq.toString();
		}
		return result;
	}
}
