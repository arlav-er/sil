package it.eng.myportal.entity;

import java.util.Date;

public interface IEntity {
	PfPrincipal getPfPrincipalMod();

	void setPfPrincipalMod(PfPrincipal pfPrincipalByIdPrincipalMod);

	PfPrincipal getPfPrincipalIns();

	void setPfPrincipalIns(PfPrincipal pfPrincipalByIdPrincipalIns);

	Date getDtmIns();

	void setDtmIns(Date dtmIns);

	Date getDtmMod();

	void setDtmMod(Date dtmMod);

}
