package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.entity.ejb.DbManagerEjb;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class GeneraPOIVacanciesBean {

	private Integer count = 0;
	private boolean executed = false;

	@EJB
	private DbManagerEjb dbManagerEjb;

	public GeneraPOIVacanciesBean() {
		super();
	}

	public void generaPoi() {
		count = dbManagerEjb.generaPoiVacancies();
		executed = true;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

}
