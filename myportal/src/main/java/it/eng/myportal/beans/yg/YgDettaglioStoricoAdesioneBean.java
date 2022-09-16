package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.dtos.YgAdesioneStoriaDTO;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgAdesioneStoriaHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class YgDettaglioStoricoAdesioneBean extends AbstractBaseBean {

	private Integer idYgAdesione;
	private YgAdesioneDTO ygAdesioneDTO;
	private List<YgAdesioneStoriaDTO> ygAdesioneStoriaList;

	@EJB
	YgAdesioneHome ygAdesioneHome;

	@EJB
	YgAdesioneStoriaHome ygAdesioneStoriaHome;

	public void checkViewPage() {
		if (!ConstantsSingleton.isYgStoricoEnabled()) {
			log.warn("Il dettaglio storico delle adesioni YG è disabilitato per questo ambiente.");
			redirectHome();
			return;
		}

		if (!session.isProvincia()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione provincia.");
			redirectHome();
			return;
		}
	}

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			Map<String, String> map = getRequestParameterMap();
			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("idYgAdesione")) {
					idYgAdesione = Integer.parseInt(entry.getValue());
					break;
				}
			}

			log.debug("Dettaglio storico adesione id=" + idYgAdesione);
			ygAdesioneDTO = ygAdesioneHome.findDTOById(idYgAdesione);
			ygAdesioneStoriaList = ygAdesioneStoriaHome.findDTOByIdYgAdesione(idYgAdesione);
		} catch (Exception e) {
			redirectGrave("generic.manipulation_error");
		}
	}

	public Integer getIdYgAdesione() {
		return idYgAdesione;
	}

	public void setIdYgAdesione(Integer idYgAdesione) {
		this.idYgAdesione = idYgAdesione;
	}

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public List<YgAdesioneStoriaDTO> getYgAdesioneStoriaList() {
		return ygAdesioneStoriaList;
	}

	public void setYgAdesioneStoriaList(List<YgAdesioneStoriaDTO> ygAdesioneStoriaList) {
		this.ygAdesioneStoriaList = ygAdesioneStoriaList;
	}
}
