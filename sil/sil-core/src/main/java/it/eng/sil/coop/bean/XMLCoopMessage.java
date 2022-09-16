/*
 * Created on Aug 9, 2006
 */
package it.eng.sil.coop.bean;

import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.SynchronizedSourceBean;

/**
 * @author savino
 */
public class XMLCoopMessage extends SourceBean {

	/**
	 * @param arg0
	 * @throws SourceBeanException
	 */
	public XMLCoopMessage() throws SourceBeanException {
		super("RICHIESTA");
		setAttribute(new SourceBean("PROVENIENZA"));
		setAttribute(new SourceBean("SERVIZIO"));
		setAttribute(new SourceBean("MESSAGGIO"));
		((SourceBean) getAttribute("SERVIZIO")).setAttribute(new SourceBean("DATI"));
	}

	/**
	 * Crea un oggetto XMLCoopMessage a partire dalla stringa documento xml (vedi SourceBean.fromXMLString())
	 * 
	 * @param xmlString
	 * @throws SourceBeanException
	 */
	public XMLCoopMessage(String s) throws SourceBeanException {
		super("REQUEST");
		SourceBean sb = SynchronizedSourceBean.fromXMLStringSynch(s);
		setBean(sb);
	}

	public void setCodiceCPIMitt(String s) throws SourceBeanException {
		setAttribute("PROVENIENZA.codiceCPI", s);
	}

	public void setDescrizioneMitt(String s) throws SourceBeanException {
		setAttribute("PROVENIENZA.DESCRIZIONE", s);
	}

	public void setNomeServizio(String s) throws SourceBeanException {
		setAttribute("SERVIZIO.NOME", s);
	}

	public String getCodiceCPIMitt() throws SourceBeanException {
		return (String) getAttribute("PROVENIENZA.codiceCPI");
	}

	public String getDescrizioneMitt() throws SourceBeanException {
		return (String) getAttribute("PROVENIENZA.DESCRIZIONE");
	}

	public String getNomeServizio() throws SourceBeanException {
		return (String) getAttribute("SERVIZIO.NOME");
	}

	/**
	 * se value null viene inserita una stringa vuota
	 * 
	 * @param key
	 * @param value
	 * @throws SourceBeanException
	 */
	public void setDati(String key, String value) throws SourceBeanException {
		setDati(key, value, "");
	}

	public void setMessaggio(String s) throws SourceBeanException {
		setAttribute("MESSAGGIO.TESTO", s);
	}

	public String getDati(String key) {
		return (String) getAttribute("servizio.dati." + key);
	}

	public SourceBean getDatiAsSourceBean() {
		return (SourceBean) getAttribute("servizio.dati");
	}

	public void setDati(String key, String value, String defaultValue) throws SourceBeanException {
		if (value == null)
			value = defaultValue;
		((SourceBean) getAttribute("SERVIZIO.DATI")).setAttribute(key, value);
	}

	/**
	 * Passa tutti i parametri con valore stringa al source bean. I parametri di tipo diverso vengono saltati.
	 * 
	 * @param sBean
	 * @throws SourceBeanException
	 */
	public void setDati(SourceBean sBean) throws SourceBeanException {
		// Recupero i campi contenuti nel SourceBean...
		Vector campi = sBean.getContainedAttributes();
		Iterator iter = campi.iterator();

		while (iter.hasNext()) {
			SourceBeanAttribute sBA = ((SourceBeanAttribute) iter.next());
			String campo = sBA.getKey();
			String valore = null;
			if (!(sBA.getValue() instanceof String))
				continue;
			valore = (String) sBA.getValue();

			// ...e li metto nell XMLCoopMessage
			((SourceBean) getAttribute("SERVIZIO.DATI")).setAttribute(campo, valore);
		}

	}

	protected String notNull(String s) {
		return s == null ? "" : s;
	}

	public static void main(String[] s) throws Exception {
		XMLCoopMessage m = null;
		if (1 == 0) {
			m = new XMLCoopMessage();
			m.setCodiceCPIMitt("001");
			m.setDescrizioneMitt("ciioaa");
			m.setNomeServizio("nome servizio");
			m.setDati("nome", "andrea");
			m.setDati("cognome", "savino");
			m.setMessaggio("messaggio  ");
		} else {
			m = new XMLCoopMessage("<ciao><pippo nome=\"andrea\"/></ciao>");
		}
		// System.out.println(m);
	}
}
