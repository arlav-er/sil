package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioAdesione;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class YgEsitoRipristinoAdesioneBean extends AbstractBaseBean {

	private RisultatoInvioAdesione risultatoInvioAdesione;

	public RisultatoInvioAdesione getRisultatoInvioAdesione() {
		return risultatoInvioAdesione;
	}

	public void setRisultatoInvioAdesione(RisultatoInvioAdesione risultatoInvioAdesione) {
		this.risultatoInvioAdesione = risultatoInvioAdesione;
	}

	@Override
	@PostConstruct
	public void postConstruct() {
		risultatoInvioAdesione = (RisultatoInvioAdesione) getFacesContext().getExternalContext().getRequestMap()
				.get("risultatoInvioAdesione");
	}
}
