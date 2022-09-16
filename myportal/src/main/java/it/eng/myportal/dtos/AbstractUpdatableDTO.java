package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Classe astratta per DTO aggiornabili
 * 
 * @see AbstractHasPrimaryKeyDTO
 * @see IUpdatable
 * 
 * @author Rodi A.
 * 
 * 
 */
public abstract class AbstractUpdatableDTO implements IUpdatable {
	private static final long serialVersionUID = 3325061940296668773L;

	protected Date dtmIns;
	protected Date dtmMod;
	protected Integer idPrincipalIns;
	protected Integer idPrincipalMod;

	public Date getDtmIns() {
		return dtmIns;
	}

	public void setDtmIns(Date dtmIns) {
		this.dtmIns = dtmIns;
	}

	public Date getDtmMod() {
		return dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

	public Integer getIdPrincipalIns() {
		return idPrincipalIns;
	}

	public void setIdPrincipalIns(Integer idPrincipalIns) {
		this.idPrincipalIns = idPrincipalIns;
	}

	public Integer getIdPrincipalMod() {
		return idPrincipalMod;
	}

	public void setIdPrincipalMod(Integer idPrincipalMod) {
		this.idPrincipalMod = idPrincipalMod;
	}

}
