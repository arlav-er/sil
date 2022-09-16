package it.eng.myportal.beans;

import it.eng.myportal.entity.ejb.TextSingletonEjb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * ManagedBean JSF che contiene tutti i testi delle 
 * pagine profilati a seconda delle province
 * 
 */
@ManagedBean(name = "txt")
@ApplicationScoped
public class TextBean implements Map<String, String> {

	protected static Log log = LogFactory.getLog(TextBean.class);
	
	@EJB
	TextSingletonEjb textMap;

	public void refillTesti() {
		textMap.refillTesti();
	}

	public int size() {
		return textMap.size();
	}

	public boolean isEmpty() {
		return textMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return textMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return textMap.containsValue(value);
	}

	public String get(Object key) {
		return textMap.get(key);
	}

	public String put(String key, String value) {
		return textMap.put(key, value);
	}

	public String remove(Object key) {
		return textMap.remove(key);
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		textMap.putAll(m);
	}

	public void clear() {
		textMap.clear();
	}

	public Set<String> keySet() {
		return textMap.keySet();
	}

	public Collection<String> values() {
		return textMap.values();
	}

	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return textMap.entrySet();
	}
	
}
