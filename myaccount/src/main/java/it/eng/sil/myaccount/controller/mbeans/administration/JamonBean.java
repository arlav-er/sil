package it.eng.sil.myaccount.controller.mbeans.administration;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.jamonapi.MonitorFactory;

import it.eng.sil.myaccount.model.utils.ConstantsSingleton;

/**
 * TO BE DELETED. Le stats jamon di MyAccount si vedono da
 * http://localhost:20000/MyAccount/secure/gestioneProfilatura/statistiche
 * 
 * @author pegoraro
 *
 */
@ManagedBean
@RequestScoped
public class JamonBean {
	private String columnIndex = "2";
	private String order = "desc";

	@EJB
	ConstantsSingleton constantsSingleton;

	public JamonBean() {
	}

	public String doNothing() {
		return "";
	}

	public boolean isEnabled() {
		return MonitorFactory.getRootMonitor().isEnabled();
	}

	public String getTotalMemory() {
		float floatMem = Runtime.getRuntime().totalMemory() / 1000000;
		return String.format("%.2f", floatMem) + " Mb";
	}

	public String getUsedMemory() {
		float floatMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
		return String.format("%.2f", floatMem) + " Mb";
	}

	public String getFreeMemory() {
		float floatMem = Runtime.getRuntime().freeMemory() / 1000000;
		return String.format("%.2f", floatMem) + " Mb";
	}

	public String getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(String columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getReportHTML() {
		String temp = MonitorFactory.getRootMonitor().getReport(Integer.parseInt(columnIndex), order);
		if (temp == null || temp.equals(""))
			return "<div>Nessun dato disponibile.</div>";
		else
			return temp;
	}

	public void reset() {
		MonitorFactory.getRootMonitor().reset();
	}

	public void disable() {
		MonitorFactory.getRootMonitor().disable();
	}

	public void enable() {
		MonitorFactory.getRootMonitor().enable();
	}

	public String getMyAccountURL() {
		return constantsSingleton.getMyAccountURL();
	}

	public String getPortaleURL() {
		return constantsSingleton.getPortaleURL() + "/faces/secure/amministrazione/jamon.xhtml";
	}
}
