package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AgAppuntamentoDTO;
import it.eng.myportal.entity.enums.TipoAppuntamentoEnum;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAppuntamentoHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class YgDettaglioAppuntamentoBean extends AbstractBaseBean {

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private DeTipoAppuntamentoHome deTipoAppuntamentoHome;

	private AgAppuntamentoDTO agAppuntamentoDTO;
	private String descrizioneCPI;
	private String tipoAppuntamento;

	public AgAppuntamentoDTO getAgAppuntamentoDTO() {
		return agAppuntamentoDTO;
	}

	public void setAgAppuntamentoDTO(AgAppuntamentoDTO agAppuntamentoDTO) {
		this.agAppuntamentoDTO = agAppuntamentoDTO;
	}

	public String getDescrizioneCPI() {
		return descrizioneCPI;
	}

	public void setDescrizioneCPI(String descrizioneCPI) {
		this.descrizioneCPI = descrizioneCPI;
	}

	public String getTipoAppuntamento() {
		return tipoAppuntamento;
	}

	public void setTipoAppuntamento(String tipoAppuntamento) {
		this.tipoAppuntamento = tipoAppuntamento;
	}

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		tipoAppuntamento = deTipoAppuntamentoHome.findDTOById(TipoAppuntamentoEnum.PL_APPGG.getCodTipoAppuntamento())
				.getDescrizione();
		if (getRequestParameter("idAgAppuntamento") != null) {
			Integer idAgAppuntamento = Integer.parseInt(getRequestParameter("idAgAppuntamento"));
			agAppuntamentoDTO = agAppuntamentoHome.findDTOById(idAgAppuntamento);
			descrizioneCPI = agAppuntamentoDTO.getDenominazioneCpi() + "\n" + agAppuntamentoDTO.getIndirizzoCpiStampa();
		} else {
			redirectHome();
		}
	}
}
