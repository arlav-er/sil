package it.eng.myportal.entity.ejb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.home.PfTestiHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.utils.ConstantsSingleton;

@Singleton
@Startup
public class TextSingletonEjb {

	protected static Log log = LogFactory.getLog(TextSingletonEjb.class);
	private HashMap<String, String> delegate = new HashMap<String, String>();

	@EJB
	PfTestiHome pfTestiHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@PostConstruct
	public void postConstruct() {
		Integer codRegione = ConstantsSingleton.COD_REGIONE;
		this.delegate = pfTestiHome.loadTesti(codRegione.toString());
		String nomeRegione;
		try {
			nomeRegione = deRegioneHome.findDTOById(String.valueOf(ConstantsSingleton.COD_REGIONE)).getDenominazione();
		} catch (Exception e) {
			nomeRegione = "Regione non trovata in DE_REGIONE: " + codRegione;
		}
		nomeRegione = WordUtils.capitalizeFully(nomeRegione, " -".toCharArray());
		this.delegate.put("regione.nome", nomeRegione);
	}

	public void clear() {
		delegate.clear();
	}

	public Object clone() {
		return delegate.clone();
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	public Set<Entry<String, String>> entrySet() {
		return delegate.entrySet();
	}

	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	public void refillTesti() {
		log.warn("RICARICO TESTI STATICI");
		delegate.clear();
		Integer codRegione = ConstantsSingleton.COD_REGIONE;
		this.delegate = pfTestiHome.loadTesti(codRegione.toString());
	}

	public String get(Object key) {
		String ret = delegate.get(key);
		if (ret == null)
			ret = "???" + key + "???";
		return ret;
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public Set<String> keySet() {
		return delegate.keySet();
	}

	public String put(String key, String value) {
		return delegate.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		delegate.putAll(m);
	}

	public String remove(Object key) {
		return delegate.remove(key);
	}

	public int size() {
		return delegate.size();
	}

	public String toString() {
		return delegate.toString();
	}

	public Collection<String> values() {
		return delegate.values();
	}

}
