package it.eng.myportal.beans;

import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * 
 * Questo BB gestisce la profilatura utenti.<br/>
 * Viene caricato in sessione quando l'utente fa login e permette di profilare gli elementi dell'applicazione.<br/>
 * Pattern delegate con il quale delego tutte le attività di me (mappa) alla mia mappa interna.<br/>
 * <br/>
 * 
 * All'interno della mappa ci sono tutti gli elementi profilati.<br/>
 * Ciascun elemento ha le seguenti proprietà:<br/>
 * <ul>
 * <li>canAdmin</li>
 * <li>canDelete</li>
 * <li>canInsert</li>
 * <li>canRead</li>
 * <li>canEdit</li>
 * </ul>
 * 
 * Esempio di utilizzo:<br/>
 * 
 * rendered="#{profile.element.canRead}"
 * 
 * @author Rodi Alessandro
 * 
 */
@ManagedBean
@SessionScoped
public class ProfileBean extends HashMap<String, Map<String, Boolean>> {
	private static final long serialVersionUID = -1462663968724762581L;

	private HashMap<String, Map<String, Boolean>> delegate;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@PostConstruct
	public void postConstruct() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		Principal up = ec.getUserPrincipal();

		String username = up.getName();
		delegate = deRuoloPortaleHome.getProfile(username);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Object clone() {
		return delegate.clone();
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Map<String, Boolean>>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}

	@Override
	public Map<String, Boolean> get(Object key) {
		return delegate.get(key);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return delegate.keySet();
	}

	@Override
	public Map<String, Boolean> put(String key, Map<String, Boolean> value) {
		return delegate.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Map<String, Boolean>> m) {
		delegate.putAll(m);
	}

	@Override
	public Map<String, Boolean> remove(Object key) {
		return delegate.remove(key);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public Collection<Map<String, Boolean>> values() {
		return delegate.values();
	}

}
