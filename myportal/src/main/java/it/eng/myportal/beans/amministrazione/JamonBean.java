package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.utils.ConstantsSingleton;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.jamonapi.MonitorFactory;

@ManagedBean
@RequestScoped
public class JamonBean {
	private String columnIndex = "2";
	private String order = "desc";
	
	//private boolean enabled = true;

	public JamonBean() { }
	
	public boolean isEnabled() {
		return MonitorFactory.getRootMonitor().isEnabled();
	}

	public long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}
	

	public String doNothing() {
		return "";
	}

	public String getColumnIndex() {
		return columnIndex;
	}

	public String getOrder() {
		return order;
	}

	public String getReportHTML() {
		return MonitorFactory.getRootMonitor().getReport(
				Integer.parseInt(columnIndex), order);
	}

	public void setColumnIndex(String columnIndex) {
		this.columnIndex = columnIndex;
	}
	public void setOrder(String order) {
		this.order = order;
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
		return ConstantsSingleton.MYACCOUNT_URL + "/secure/jamon";
	}
	
	public String getPortaleURL() {
		return ConstantsSingleton.BASE_URL + "/MyPortal";
	}
	
}
