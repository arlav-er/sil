package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.ejb.DbManagerEjb;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean per la gestione portlet dell'amministratore
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class AdministratorPortletsBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AdministratorPortletsBean.class);

	private List<SelectItem> gruppi;

	private String codGruppo;
	private Integer idPortlet;
	private Integer position;
	private String column;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	DbManagerEjb dbManagerEjb;

	@PostConstruct
	public void postConstruct() {
		if (!"amministratore".equals(getSession().getUsername())) {
			redirectPublicIndex();
		}
		gruppi = deRuoloPortaleHome.getListItems(false);
	}

	public List<SelectItem> getGruppi() {
		return gruppi;
	}

	public void setGruppi(List<SelectItem> gruppi) {
		this.gruppi = gruppi;
	}

	public String getCodGruppo() {
		return codGruppo;
	}

	public void setCodGruppo(String codGruppo) {
		this.codGruppo = codGruppo;
	}

	public Integer getIdPortlet() {
		return idPortlet;
	}

	public void setIdPortlet(Integer idPortlet) {
		this.idPortlet = idPortlet;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void aggiungiPortlet() {
		// TODO PROFILATURA
		// ptScrivaniaHome.addPortletToGroup(idPortlet, codGruppo, position, column);
	}

}
