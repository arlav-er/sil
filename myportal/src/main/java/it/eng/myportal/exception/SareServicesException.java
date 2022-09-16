package it.eng.myportal.exception;

import javax.ejb.EJBException;

/**
 * @deprecated utilizzare MyPortalException con 'true' come ultimo parametro
 * 
 * @author Rodi A.
 *
 */
public class SareServicesException extends EJBException {

	private static final long serialVersionUID = -7970667856403037721L;

	private String codErrore;

	public SareServicesException(String codErrore, Exception ex) {		
		super(ex);
		this.codErrore = codErrore;
	}

	public SareServicesException(String codErrore) {
		super();
		this.codErrore = codErrore;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

}
