package it.eng.myportal.ws;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class CorsoNonAllineatoException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer idCorso;

	public CorsoNonAllineatoException(Integer idCorso) {
		super();
		this.setIdCorso(idCorso);
	}

	public Integer getIdCorso() {
		return idCorso;
	}

	public void setIdCorso(Integer idCorso) {
		this.idCorso = idCorso;
	}

}