package it.eng.sil.module.movimenti;

import java.util.Enumeration;
import java.util.Hashtable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * Oggetto che rappresenta un movimento da inserire o da consultare. Contiene tutti i dati del movimento e risiede in
 * sessione. Serve in tutti i casi in cui occorre tenere traccia delle modifiche apportate dall'utente durante la
 * navigazione tra le linguette prima che esse vengano registrate su DB o nel caso che l'inserimento/modifica fallisca.
 * Tutti i campi sono case insensitive e vengono memorizzati in maiuscolo. Per poter essere utilizzato l'oggetto deve
 * essere stato abilitato attraverso il metodo enable()
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class Movimento {
	/** Elenco nomi dei campi del movimento */
	private String[] fieldNames = null;
	/** Contiene i valori correnti dei campi */
	private Hashtable data = new Hashtable();
	/** indica se l'oggetto è stato inizializzato o meno */
	private boolean enabled = false;

	/**
	 * Crea l'oggetto movimento.
	 * <p>
	 * 
	 * @param fieldNames
	 *            Nomi dei campi contenuti nel movimento.
	 */
	public Movimento(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	/**
	 * Crea l'oggetto movimento.
	 */
	public Movimento() {
	}

	/**
	 * Recupera i campi contenuti come attributi di un SourceBean passato come parametro, vengono aggiornati solo i
	 * campi che matchano (case insensitive) con quelli contenuti come attributi nel SourceBean e nell'array dei campi
	 * di quest'oggetto. Essi vengono prelevati come Object e così verranno restituiti.
	 * <p>
	 * 
	 * @param origin
	 *            SourceBean da cui prelevare i dati
	 *            <p>
	 * @throws IllegalArgumentException
	 *             Se il progressivo del movimento passato è nullo o non coincide con quello settato per l'oggetto
	 *             <p>
	 * @return Il numero di campi estratti dal SourceBean e riportati nel movimento
	 */
	public int setFieldsFromSourceBean(SourceBean origin) {

		// Ciclo sugli attributi correnti e se li trovo li estraggo dal
		// SourceBean e li caccio
		// nel Movimento
		int counter = 0;
		if (fieldNames != null && origin != null) {
			for (int i = 0; i < fieldNames.length; i++) {
				Object o = origin.getAttribute(fieldNames[i]);
				if (o != null) {
					data.put(fieldNames[i].toUpperCase(), o);
					counter = counter + 1;
				}
			}
		}
		return counter;
	}

	/**
	 * Recupera tutti i campi contenuti nell'oggetto come SourceBean. I campi saranno indicati come attributi del
	 * SourceBean ritornato. (i nomi delle proprietà coincidono con quelli indicati nell'Array delle proprietà settato
	 * per l'oggetto)
	 * <p>
	 * 
	 * @return Il SourceBean di nome "MOVIMENTO" avente come attributi i campi del movimento valorizzati. null se
	 *         l'oggetto non è stato abilitato.
	 *         <p>
	 * @throws SourceBeanException
	 *             Se si verificano errori nella creazione del SourceBean.
	 */
	public SourceBean getFieldsAsSourceBean() throws SourceBeanException {
		if (!enabled) {
			return null;
		}
		SourceBean mov = new SourceBean("MOVIMENTO");
		Enumeration e = data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = data.get(key.toUpperCase());
			if (key != null && value != null) {
				mov.setAttribute(key, value);
			}
		}
		return mov;
	}

	/**
	 * Recupera uno dei campi del movimento.
	 * <p>
	 * 
	 * @param fieldName
	 *            Nome del campo da recuperare.
	 *            <p>
	 * @return L'oggetto che è stato impostato per il campo, null nel caso che non sia stato impostato alcun valore o se
	 *         l'oggetto non è abilitato o se il nome del campo è null o non riconosciuto.
	 */
	public Object getField(String fieldName) {
		if (!enabled) {
			return null;
		}
		// Cerco il nome del campo tra quelli validi
		boolean found = false;
		if (fieldNames != null && fieldName != null) {
			for (int i = 0; ((i < fieldNames.length) && (!found)); i++) {
				if (fieldNames[i].equalsIgnoreCase(fieldName)) {
					found = true;
				}
			}
		}

		if (found) {
			return data.get(fieldName.toUpperCase());
		} else {
			return null;
		}
	}

	/**
	 * Setta uno dei campi del movimento.
	 * <p>
	 * 
	 * @param fieldName
	 *            Nome del campo da settare, se è null non succede niente.
	 * @param fieldValue
	 *            Valore del campo da settare, se null il valore attuale verrà rimosso dalla tabella
	 *            <p>
	 * @return L'oggetto precedentemente impostato per il campo, null nel caso che non fosse stato impostato alcun
	 *         valore o se non succede nulla.
	 */
	public Object setField(String fieldName, Object fieldValue) {

		// Cerco il nome del campo tra quelli validi
		boolean found = false;
		if (fieldNames != null && fieldName != null) {
			for (int i = 0; ((i < fieldNames.length) && (!found)); i++) {
				if (fieldNames[i].equalsIgnoreCase(fieldName)) {
					found = true;
				}
			}
		}

		if (!found) {
			return null;
		} else {
			// Il nome del campo è valido, devo vedere se il valore è nullo o
			// meno
			if (fieldValue == null) {
				return data.remove(fieldName.toUpperCase());
			} else {
				return data.put(fieldName.toUpperCase(), fieldValue);
			}
		}
	}

	/**
	 * Modifica i campi del movimento, la modifica cancellerà tutti i campi non più supportati e lascerà solo quelli
	 * ancora compatibili con i nuovo campi.
	 * 
	 * @param fieldNames
	 *            Nomi dei nuovi campi contenuti nel movimento.
	 */
	public void setFieldNames(String[] fieldNames) {

		this.fieldNames = fieldNames;
		Hashtable d = new Hashtable();
		if (fieldNames != null) {
			// Ciclo sui valori attuali e copio solo quelli supportati
			for (int i = 0; i < fieldNames.length; i++) {
				Object o = data.get(fieldNames[i].toUpperCase());
				if (o != null) {
					d.put(fieldNames[i].toUpperCase(), o);
				}
			}
			this.data = d;
		}
	}

	/**
	 * Recupera l'array dei nomi dei campi contenuti nel movimento.
	 * 
	 * @return I nomi dei campi contenuti nel movimento.
	 */
	public String[] getFieldNames() {

		return fieldNames;
	}

	/**
	 * Abilita l'oggetto, indica che i campi sono stati inzializzati e possono essere recuperati. Fino a quando
	 * l'oggetto non è stato abilitato i campi non possono essere recuperati
	 */
	public void enable() {
		this.enabled = true;
	}

	/**
	 * Disabilita l'oggetto, indica che i campi non sono stati inzializzati e non possono essere recuperati.
	 */
	public void disable() {
		this.enabled = false;
	}

	/**
	 * Indica se l'oggetto è stato abilitato (con true) e se i campi contenuti possono essare recuperati.
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

}