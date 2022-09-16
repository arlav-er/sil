package it.eng.myportal.ws.mysap.pojo;

import java.util.List;

import it.eng.myportal.dtos.CvAgevolazioneDTO;
import it.eng.myportal.dtos.CvAlboDTO;
import it.eng.myportal.dtos.CvCompetenzeTrasvDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvEsperienzeProfDTO;
import it.eng.myportal.dtos.CvFormazioneDTO;
import it.eng.myportal.dtos.CvInformaticaDTO;
import it.eng.myportal.dtos.CvIstruzioneDTO;
import it.eng.myportal.dtos.CvLinguaDTO;
import it.eng.myportal.dtos.CvPatenteDTO;
import it.eng.myportal.dtos.CvPatentinoDTO;
//import it.eng.myportal.dtos.CvProfDesiderateDTO;
import it.eng.myportal.entity.AcCandidatura;

public class CurriculumVitaeFull {

	private CvCompetenzeTrasvDTO cvCompetenzeTrasvDTO;
	private CvDatiPersonaliDTO cvDatiPersonaliDTO;

	private CvInformaticaDTO cvInformaticaDTO;
	private List<AcCandidatura> listaCandidaturaDTO;
	private List<CvAgevolazioneDTO> listaCvAgevolazioneDTO;

	private List<CvAlboDTO> listaCvAlboDTO;
	private List<CvEsperienzeProfDTO> listaCvEsperienzeProfDTO;
	private List<CvFormazioneDTO> listaCvFormazioneDTO;
	private List<CvIstruzioneDTO> listaCvIstruzioneDTO;
	private List<CvLinguaDTO> listaCvLinguaDTO;
	
	private NewCvAltreInfoDTO cvAltreInfoDTO;
	

	private List<CvPatenteDTO> listaCvPatenteDTO;

	private List<CvPatentinoDTO> listaCvPatentinoDTO;
	private List<NewCvProfDesiderateDTO> listaCvProfDesiderateDTO;

	public CvCompetenzeTrasvDTO getCvCompetenzeTrasvDTO() {
		return cvCompetenzeTrasvDTO;
	}

	public CvDatiPersonaliDTO getCvDatiPersonaliDTO() {
		return cvDatiPersonaliDTO;
	}
	
	public CvInformaticaDTO getCvInformaticaDTO() {
		return cvInformaticaDTO;
	}

	public List<AcCandidatura> getListaCandidaturaDTO() {
		return listaCandidaturaDTO;
	}

	
	public List<CvAgevolazioneDTO> getListaCvAgevolazioneDTO() {
		return listaCvAgevolazioneDTO;
	}

	public List<CvAlboDTO> getListaCvAlboDTO() {
		return listaCvAlboDTO;
	}

	public List<CvEsperienzeProfDTO> getListaCvEsperienzeProfDTO() {
		return listaCvEsperienzeProfDTO;
	}

	public List<CvFormazioneDTO> getListaCvFormazioneDTO() {
		return listaCvFormazioneDTO;
	}

	public List<CvIstruzioneDTO> getListaCvIstruzioneDTO() {
		return listaCvIstruzioneDTO;
	}

	public List<CvLinguaDTO> getListaCvLinguaDTO() {
		return listaCvLinguaDTO;
	}

	public List<CvPatenteDTO> getListaCvPatenteDTO() {
		return listaCvPatenteDTO;
	}

	public List<CvPatentinoDTO> getListaCvPatentinoDTO() {
		return listaCvPatentinoDTO;
	}



	public void setCvCompetenzeTrasvDTO(CvCompetenzeTrasvDTO cvCompetenzeTrasvDTO) {
		this.cvCompetenzeTrasvDTO = cvCompetenzeTrasvDTO;
	}

	public void setCvDatiPersonaliDTO(CvDatiPersonaliDTO cvDatiPersonaliDTO) {
		this.cvDatiPersonaliDTO = cvDatiPersonaliDTO;
	}

	public void setCvInformaticaDTO(CvInformaticaDTO cvInformaticaDTO) {
		this.cvInformaticaDTO = cvInformaticaDTO;
	}

	public void setListaCandidaturaDTO(List<AcCandidatura> listaCandidaturaDTO) {
		this.listaCandidaturaDTO = listaCandidaturaDTO;
	}

	public void setListaCvAgevolazioneDTO(List<CvAgevolazioneDTO> listaCvAgevolazioneDTO) {
		this.listaCvAgevolazioneDTO = listaCvAgevolazioneDTO;
	}

	public void setListaCvAlboDTO(List<CvAlboDTO> listaCvAlboDTO) {
		this.listaCvAlboDTO = listaCvAlboDTO;
	}

	public void setListaCvEsperienzeProfDTO(List<CvEsperienzeProfDTO> listaCvEsperienzeProfDTO) {
		this.listaCvEsperienzeProfDTO = listaCvEsperienzeProfDTO;
	}

	public void setListaCvFormazioneDTO(List<CvFormazioneDTO> listaCvFormazioneDTO) {
		this.listaCvFormazioneDTO = listaCvFormazioneDTO;
	}

	public void setListaCvIstruzioneDTO(List<CvIstruzioneDTO> listaCvIstruzioneDTO) {
		this.listaCvIstruzioneDTO = listaCvIstruzioneDTO;
	}

	public void setListaCvLinguaDTO(List<CvLinguaDTO> listaCvLinguaDTO) {
		this.listaCvLinguaDTO = listaCvLinguaDTO;
	}

	public void setListaCvPatenteDTO(List<CvPatenteDTO> listaCvPatenteDTO) {
		this.listaCvPatenteDTO = listaCvPatenteDTO;
	}

	public void setListaCvPatentinoDTO(List<CvPatentinoDTO> listaCvPatentinoDTO) {
		this.listaCvPatentinoDTO = listaCvPatentinoDTO;
	}

	public NewCvAltreInfoDTO getCvAltreInfoDTO() {
		return cvAltreInfoDTO;
	}

	public void setCvAltreInfoDTO(NewCvAltreInfoDTO cvAltreInfoDTO) {
		this.cvAltreInfoDTO = cvAltreInfoDTO;
	}

	public List<NewCvProfDesiderateDTO> getListaCvProfDesiderateDTO() {
		return listaCvProfDesiderateDTO;
	}

	public void setListaCvProfDesiderateDTO(List<NewCvProfDesiderateDTO> listaCvProfDesiderateDTO) {
		this.listaCvProfDesiderateDTO = listaCvProfDesiderateDTO;
	}


}
