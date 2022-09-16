package it.eng.myportal.beans;

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

/**
 * BackingBean per il cambio password
 * 
 * 
 */
@ManagedBean
@RequestScoped
public class CambioPasswordBean extends AbstractBaseBean {

	@EJB
	transient PfPrincipalHome pfPrincipalHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	private String passwordOld;
	private String passwordNew;
	private String passwordConfirm;

	private PfPrincipalDTO data;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
	}

	public void cambiaPassword() {
		try {
			pfPrincipalHome.cambiaPassword(session.getUsername(), passwordNew, passwordOld);

			session.setNuovaPassword(passwordNew);

			redirectHome();

		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	/**
	 * @return the passwordOld
	 */
	public String getPasswordOld() {
		return passwordOld;
	}

	/**
	 * @return the passwordNew
	 */
	public String getPasswordNew() {
		return passwordNew;
	}

	/**
	 * @return the passwordConfirm
	 */
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	/**
	 * @param passwordOld
	 *            the passwordOld to set
	 */
	public void setPasswordOld(String passwordOld) {
		this.passwordOld = passwordOld;
	}

	/**
	 * @param passwordNew
	 *            the passwordNew to set
	 */
	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	/**
	 * @param passwordConfirm
	 *            the passwordConfirm to set
	 */
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

}
