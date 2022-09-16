/*
 * Creato il 31-mag-04
 * Author: roccetti paolo
 * 
 */
package it.eng.sil.util;

import java.util.Enumeration;
import java.util.Hashtable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * Un oggetto di classe NavigationCache che consente di mantenere in sessione un insieme di dati immessi dall'utente
 * attraverso una form.
 * <p>
 * E' utile nel caso in cui si debbano riproporre all'utente alcuni dati inseriti precedentemente e non memorizzati su
 * DB. Un oggetto di questa classe svolge la funzione di "memoria cache" durante la navigazione dell'utente su un
 * insieme di pagine prima di effettuare modifiche sul database o nel caso che tali modifiche non vadano a buon fine e
 * l'utente possa correggere le informazioni inserite per ritentare nuovamente. Deve essere configurato per memorizzare
 * solo un certo insieme di campi che interessano.
 * <p>
 * Tutti i campi indicati sono case insensitive e vengono memorizzati in maiuscolo. I campi possono essere memorizzati
 * nell'oggetto attrvareso il metodo: setFieldsFromSourceBean(...) e recuperati con il metodo
 * getFieldsAsSourceBean(...). Esistono anche metodi per impostare o recuperare i valori dei campi singolarmente,
 * rispettivamente setField(...) e getField(...).
 * <p>
 * Per poter recuperare i dati memeorizzati l'oggetto deve essere stato abilitato attraverso il metodo enable(). I
 * metodi di abilitazione servono nel caso che si voglia utilizzare l'oggetto solo a seguito di determinati passi di
 * navigazione. L'oggetto può essere abilitato o disabilitato a volontà. Comunque non è richiesta alcuna abilitazione
 * per memorizzare i dati nell'oggetto.
 * <p>
 * NOTA: <br>
 * Di solito l'utilizzo di questo oggetto è limitato al recupero dei dati dalla ServiceRequest, come tale tutti i dati
 * immessi dall'utente sono memorizzati come attributi del SourceBean di richiesta e non si rende necessaria alcuna
 * navigazione all'interno dell SourceBean indicato. <br>
 * Considerando però che i dati vengono estratti da oggetti di classe SourceBean è possibile specificare anche percorsi
 * di navigazione all'interno del SourceBean per recuperare i dati, ad esempio: "BUSTA1.PARTE2.PARAMETRO1". Ovviamente
 * in seguito l'attributo così memorizzato dovrà essere recuperato con lo stesso nome.
 * <p>
 * Per un esempio di utilizzo si vedano le pagine di inserimento e validazione dei movimenti.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class NavigationCache {
	/** Elenco nomi dei campi del movimento */
	private String[] fieldNames = null;
	/** Contiene i valori correnti dei campi */
	private Hashtable data = new Hashtable();
	/** indica se l'oggetto è stato inizializzato o meno */
	private boolean enabled = false;

	/**
	 * Crea l'oggetto NavigationCache.<br>
	 * Nella NavigationCache verranno memorizzati tutti e soli i campi i cui nomi matchano con quelli specificati
	 * nell'array di String passato come argomento. I campi specificati sono case insensitive, comunque (per
	 * informazione) tutti i campi specificati vengono memorizzati in maiuscolo all'interno dell'oggetto.
	 * <p>
	 * 
	 * @param fieldNames
	 *            Nomi dei campi da memorizzare.
	 */
	public NavigationCache(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	/**
	 * Crea l'oggetto movimento. Per il corretto utilizzo occorrerà impostare l'array di campi da memorizzare attraverso
	 * il metodo setFieldNames().
	 */
	public NavigationCache() {
	}

	/**
	 * Imposta i valori dei campi contenuti nella NavigationCache prelevandoli dagli attributi del SourceBean passato
	 * come parametro, vengono aggiornati solo i campi che matchano (case insensitive) con quelli contenuti come
	 * attributi nel SourceBean e nell'array dei campi di questa NavigationCache.<br>
	 * Essi vengono prelevati come Object e così verranno restituiti.
	 * <p>
	 * 
	 * @param origin
	 *            SourceBean da cui prelevare i dati
	 *            <p>
	 * @return Il numero di campi estratti dal SourceBean e memorizzati nella NavigationCache.
	 */
	public int setFieldsFromSourceBean(SourceBean origin) {

		int counter = 0;
		// Cerco i campi che matchano e li inserisco nell'oggetto
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
	 * Recupera tutti i campi contenuti nella NavigationCache come SourceBean. I campi saranno indicati come attributi
	 * del SourceBean ritornato. (i nomi degli attributi coincidono con quelli indicati nell'Array dei campi settato per
	 * la NavigationCache)
	 * <p>
	 * 
	 * @return Il SourceBean di nome "CACHE" avente come attributi i campi della NavigationCache valorizzati. Null se
	 *         l'oggetto non è stato abilitato.
	 *         <p>
	 * @throws SourceBeanException
	 *             Se si verificano errori nella creazione del SourceBean di risposta.
	 */
	public SourceBean getFieldsAsSourceBean() throws SourceBeanException {
		if (!enabled) {
			return null;
		}
		SourceBean cache = new SourceBean("CACHE");
		Enumeration e = data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = data.get(key.toUpperCase());
			if (key != null && value != null) {
				cache.setAttribute(key, value);
			}
		}
		return cache;
	}

	/**
	 * Recupera uno dei campi dalla NavigationCache
	 * <p>
	 * 
	 * @param fieldName
	 *            Nome del campo da recuperare.
	 *            <p>
	 * @return L'oggetto che è stato impostato per il campo, null nel caso che non sia stato impostato alcun valore o se
	 *         la NavigationCache non è abilitata o se il nome del campo è null o non coincide con nessuno di quelli
	 *         impostati.
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
	 * Setta uno dei campi della NavigationCache
	 * <p>
	 * 
	 * @param fieldName
	 *            Nome del campo da settare, se è null non succede niente.
	 * @param fieldValue
	 *            Valore del campo da settare, se null il valore attuale verrà rimosso dalla NavigationCache.
	 *            <p>
	 * @return L'oggetto precedentemente impostato per il campo, null nel caso che non fosse stato impostato alcun
	 *         valore o se il campo indicato non è tra quelli validi.
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
	 * Modifica i campi validi della NavigationCache la modifica cancellerà tutti i campi non più validi e lascerà solo
	 * quelli ancora compatibili con i nuovi campi.
	 * 
	 * @param fieldNames
	 *            Nomi dei nuovi campi da memeorizzare nella NavigationCache.
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
	 * Recupera l'array dei nomi dei campi da memorizzare nella NavigationCache
	 * 
	 * @return I nomi dei campi da memorizzare nella NavigationCache
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
	 * Indica se l'oggetto è stato abilitato (con true) e se i campi contenuti possono essere recuperati.
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	public String toString() {
		String ret = null;
		ret = "IS_ENABLED=" + enabled + " DATA=" + data;
		return ret;
	}
}
