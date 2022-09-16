package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AgAppuntamentoDTO;
import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class YgAppuntamentoPortletBean extends AbstractBaseBean {

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private YgAdesioneHome ygAdesione;

	private AgAppuntamentoDTO agAppuntamentoDTO;

	public AgAppuntamentoDTO getAgAppuntamentoDTO() {
		return agAppuntamentoDTO;
	}

	public void setAgAppuntamentoDTO(AgAppuntamentoDTO agAppuntamentoDTO) {
		this.agAppuntamentoDTO = agAppuntamentoDTO;
	}

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		YgAdesione ade = ygAdesione.findLatestAttivaByIdPfPrincipal(session.getPrincipalId());
		agAppuntamentoDTO = agAppuntamentoHome.findDTOAppuntamentoYG(session.getPrincipalId(), ade.getIdYgAdesione());
	}
}
