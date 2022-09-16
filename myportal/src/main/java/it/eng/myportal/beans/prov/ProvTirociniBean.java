package it.eng.myportal.beans.prov;

import java.util.ArrayList;
import java.util.List;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.utente.messaging.UtenteMessagingBean;
import it.eng.myportal.entity.DoTirocini;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.ejb.tirocini.DoTirociniEjb;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.helpers.LazyDoTirociniModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

@ManagedBean(name = "provTirociniBean")
@ViewScoped
public class ProvTirociniBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(UtenteMessagingBean.class);

	private String currentSection;

	private DoTirocini searchDoTirocini;
	private LazyDataModel<DoTirocini> doTirociniList;

	@EJB
	private DoTirociniEjb doTirociniEjb;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;

	@PostConstruct
	public void init() {
		searchDoTirocini = new DoTirocini();
	}

	public String getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(String currentSection) {
		this.currentSection = currentSection;
	}

	public DoTirocini getSearchDoTirocini() {
		return searchDoTirocini;
	}

	public void setSearchDoTirocini(DoTirocini searchDoTirocini) {
		this.searchDoTirocini = searchDoTirocini;
	}

	public LazyDataModel<DoTirocini> getDoTirociniList() {
		return doTirociniList;
	}

	public void setDoTirociniList(LazyDataModel<DoTirocini> doTirociniList) {
		this.doTirociniList = doTirociniList;
	}

	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
	}

	public List<DeMansioneMin> getMansioneMinList() {
		if (searchDoTirocini.getDeMansione() == null)
			return null;

		List<DeMansioneMin> result = autoCompleteBean.completeMansioneMinisterialeByCodMansione(searchDoTirocini
				.getDeMansione().getCodMansione());

		if (result == null) {
			result = new ArrayList<DeMansioneMin>();
		}

		return result;
	}

	/**
	 * Main navigation - xhtml path passed from here
	 * */
	public void updateSection() {
		currentSection = getRequestParameter("currentSection");
	}

	public void initList() {
		doTirociniList = new LazyDoTirociniModel(LazyDoTirociniModel.FILTER_PROV, session.getPrincipalId(),
				searchDoTirocini);
	}

}
