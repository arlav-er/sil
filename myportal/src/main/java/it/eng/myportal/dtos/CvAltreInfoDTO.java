package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object del CV, sezione Altre Info
 * 
 * @author iescone
 *
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 */

public class CvAltreInfoDTO extends AbstractUpdatablePkDTO implements ICurriculumSection {
	private static final long serialVersionUID = -1809903148450852906L;

	private DeTrasfertaDTO tipoTrasferta;
	private Boolean automunito;
	private Boolean motomunito;
	private List<IDecode> listaAgevolazioniDTO;
	private String ulterioriInfo;
	private String limitazioni;

	public CvAltreInfoDTO() {
		super();
		tipoTrasferta = new DeTrasfertaDTO();
		listaAgevolazioniDTO = new ArrayList<IDecode>();
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

	public List<IDecode> getListaAgevolazioniDTO() {
		return listaAgevolazioniDTO;
	}

	public void setListaAgevolazioniDTO(List<IDecode> listaAgevolazioniDTO) {
		this.listaAgevolazioniDTO = listaAgevolazioniDTO;
	}

	public String getUlterioriInfo() {
		return ulterioriInfo;
	}

	public void setUlterioriInfo(String ulterioriInfo) {
		this.ulterioriInfo = ulterioriInfo;
	}

	public String getLimitazioni() {
		return limitazioni;
	}

	public void setLimitazioni(String limitazioni) {
		this.limitazioni = limitazioni;
	}

}
