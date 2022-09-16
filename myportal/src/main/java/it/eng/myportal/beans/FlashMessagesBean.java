package it.eng.myportal.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * ManagedBean JSF per contenere i messaggi da mostrare in una pagina
 * in quanto i FacesMessage registrati prima di una redirect
 * nella postConstruct non vengono visualizzati.
 * I messaggi vengono inseriti all'interno di questo Bean ed 
 * estratti all'interno del metodo postConstruct del BackingBean successivo.
 * 
 * Non è necessario gestire direttamente questo Bean.
 * E' sufficiente utilizzare il metodo addMessage() a 
 * disposizione di tutti i BackingBean che estendono AbstractBaseBean 
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@SessionScoped
public class FlashMessagesBean {

	protected static Log log = LogFactory.getLog(FlashMessagesBean.class);

	private List<FacesMessage> list = new ArrayList<FacesMessage>();
		
	public void clear() {
		list.clear();		
	}

	public void addMessage(FacesMessage message) {
		list.add(message);
	}

	public void addMessages(List<FacesMessage> list) {
		this.list.addAll(list);
	}

	public List<FacesMessage> getList() {
		return list;
	}

	public void setList(List<FacesMessage> list) {
		this.list = list;
	}	
	
	
	
}
