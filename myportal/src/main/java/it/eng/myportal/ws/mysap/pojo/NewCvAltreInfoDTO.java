package it.eng.myportal.ws.mysap.pojo;

import java.util.List;

import it.eng.myportal.dtos.DeTrasfertaDTO;
import it.eng.myportal.dtos.GenericFiltroDecodeDTO;
import it.eng.myportal.dtos.IDecode;

public class NewCvAltreInfoDTO {
	
	private DeTrasfertaDTO tipoTrasferta;
	private Boolean automunito;
	private Boolean motomunito;
	private List<GenericFiltroDecodeDTO> listaAgevolazioniDTO;
	private String ulterioriInfo;
	private Integer id;
	
	public NewCvAltreInfoDTO() {
	}

	public Integer getIdCvDatiPersonali() {
		return id;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.id = idCvDatiPersonali;
	}

	public DeTrasfertaDTO getTipoTrasferta() {
		return tipoTrasferta;
	}

	public void setTipoTrasferta(DeTrasfertaDTO tipoTrasferta) {
		this.tipoTrasferta = tipoTrasferta;
	}

	public Boolean getAutomunito() {
		return automunito;
	}

	public void setAutomunito(Boolean automunito) {
		this.automunito = automunito;
	}

	public Boolean getMotomunito() {
		return motomunito;
	}

	public void setMotomunito(Boolean motomunito) {
		this.motomunito = motomunito;
	}

//	public List<IDecode> getListaAgevolazioniDTO() {
//		return listaAgevolazioniDTO;
//	}
//
//	public void setListaAgevolazioniDTO(List<IDecode> listaAgevolazioniDTO) {
//		this.listaAgevolazioniDTO = listaAgevolazioniDTO;
//	}

	public String getUlterioriInfo() {
		return ulterioriInfo;
	}

	public void setUlterioriInfo(String ulterioriInfo) {
		this.ulterioriInfo = ulterioriInfo;
	}

	public List<GenericFiltroDecodeDTO> getListaAgevolazioniDTO() {
		return listaAgevolazioniDTO;
	}

	public void setListaAgevolazioniDTO(List<GenericFiltroDecodeDTO> listaAgevolazioniDTO) {
		this.listaAgevolazioniDTO = listaAgevolazioniDTO;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
