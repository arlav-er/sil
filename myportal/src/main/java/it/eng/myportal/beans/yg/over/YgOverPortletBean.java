package it.eng.myportal.beans.yg.over;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AgAppuntamentoDTO;
import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class YgOverPortletBean extends AbstractBaseBean {

	@EJB
	YgGaranziaOverHome ygGaranziaOverHome;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	private YgGaranziaOverDTO adesione;
	private AppuntamentoDTO appuntamento;

	@PostConstruct
	public void postConstruct() {
		adesione = ygGaranziaOverHome.findByPfPrincipal(session.getPrincipalId());

		if (adesione != null && adesione.getFlgPresoInCarico()) {
			AgAppuntamentoDTO agAppuntamento = agAppuntamentoHome.findDTOAppuntamentoGaranziaOver(
					session.getPrincipalId(), adesione.getId());
			appuntamento = agAppuntamentoHome.findAppuntamentoDTObyIdAgAppuntamento(agAppuntamento.getId());
		}
	}

	public YgGaranziaOverDTO getAdesione() {
		return adesione;
	}

	public void setAdesione(YgGaranziaOverDTO adesione) {
		this.adesione = adesione;
	}

	public AppuntamentoDTO getAppuntamento() {
		return appuntamento;
	}

	public void setAppuntamento(AppuntamentoDTO appuntamento) {
		this.appuntamento = appuntamento;
	}
}
