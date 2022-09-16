package it.eng.myportal.dtos;

import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

public class CvVisualizzaDTO implements IHasPrimaryKey<Integer> {
	private static final long serialVersionUID = -8031770837296891912L;

	private Integer id;
	private String nome;
	private String cognome;
	private CvDatiPersonaliDTO cvDatiPersonaliDTO;
	private List<CvIstruzioneDTO> listaCvIstruzioneDTO;
	private List<CvFormazioneDTO> listaCvFormazioneDTO;
	private List<CvEsperienzeProfDTO> listaCvEsperienzeProfessionaliDTO;
	private List<CvLinguaDTO> listaCvLinguaDTO;
	private List<CvPatenteDTO> listaCvPatentiDTO;
	private List<CvPatentinoDTO> listaCvPatentiniDTO;
	private List<CvAlboDTO> listaCvAlboDTO;
	private List<CvAgevolazioneDTO> listaCvAgevolazioni;
	private CvInformaticaDTO cvInformaticaDTO;
	private CvAbilitazioniDTO cvAbilitazioniDTO;
	private CvCompetenzeTrasvDTO cvCompetenzeTrasvDTO;
	private CvAltreInfoDTO cvAltreInfoDTO;
	private List<CvProfDesiderateDTO> listCvProfDesiderataDTO;
	private Boolean sincronizzatoCliclavoro;
	private Boolean testataVisible;

	public CvVisualizzaDTO() {

	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer idCvVisualizza) {
		this.id = idCvVisualizza;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public CvDatiPersonaliDTO getCvDatiPersonaliDTO() {
		return cvDatiPersonaliDTO;
	}

	public List<CvEsperienzeProfDTO> getListaCvEsperienzeProfessionaliDTO() {
		return listaCvEsperienzeProfessionaliDTO;
	}

	public List<CvFormazioneDTO> getListaCvFormazioneDTO() {
		return listaCvFormazioneDTO;
	}

	public List<CvIstruzioneDTO> getListaCvIstruzioneDTO() {
		return listaCvIstruzioneDTO;
	}

	public CvInformaticaDTO getCvInformaticaDTO() {
		return cvInformaticaDTO;
	}

	public List<CvLinguaDTO> getListaCvLinguaDTO() {
		return listaCvLinguaDTO;
	}

	public void setCvDatiPersonaliDTO(CvDatiPersonaliDTO cvDatiPersonaliDTO) {
		this.cvDatiPersonaliDTO = cvDatiPersonaliDTO;
	}

	public void setListaCvEsperienzeProfessionaliDTO(List<CvEsperienzeProfDTO> listaCvEsperienzeProfessionaliDTO) {
		this.listaCvEsperienzeProfessionaliDTO = listaCvEsperienzeProfessionaliDTO;
	}

	public void setListaCvFormazioneDTO(List<CvFormazioneDTO> listaCvFormazioneDTO) {
		this.listaCvFormazioneDTO = listaCvFormazioneDTO;
	}

	public void setListaCvIstruzioneDTO(List<CvIstruzioneDTO> listaCvIstruzioneDTO) {
		this.listaCvIstruzioneDTO = listaCvIstruzioneDTO;
	}

	public void setCvInformaticaDTO(CvInformaticaDTO cvInformaticaDTO) {
		this.cvInformaticaDTO = cvInformaticaDTO;
	}

	public void setListaCvLinguaDTO(List<CvLinguaDTO> listaCvLinguaDTO) {
		this.listaCvLinguaDTO = listaCvLinguaDTO;
	}

	public CvAltreInfoDTO getCvAltreInfoDTO() {
		return cvAltreInfoDTO;
	}

	public void setCvAltreInfoDTO(CvAltreInfoDTO cvAltreInfoDTO) {
		this.cvAltreInfoDTO = cvAltreInfoDTO;
	}

	public CvCompetenzeTrasvDTO getCvCompetenzeTrasvDTO() {
		return cvCompetenzeTrasvDTO;
	}

	public void setCvCompetenzeTrasvDTO(CvCompetenzeTrasvDTO cvCompetenzeTrasvDTO) {
		this.cvCompetenzeTrasvDTO = cvCompetenzeTrasvDTO;
	}

	public CvAbilitazioniDTO getCvAbilitazioniDTO() {
		return cvAbilitazioniDTO;
	}

	public void setCvAbilitazioniDTO(CvAbilitazioniDTO cvAbilitazioniDTO) {
		this.cvAbilitazioniDTO = cvAbilitazioniDTO;
	}

	public List<CvProfDesiderateDTO> getListCvProfDesiderataDTO() {
		return listCvProfDesiderataDTO;
	}

	public void setListCvProfDesiderataDTO(List<CvProfDesiderateDTO> listCvProfDesiderataDTO) {
		this.listCvProfDesiderataDTO = listCvProfDesiderataDTO;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CvVisualizzaDTO))
			return false;
		Integer otherId = ((CvVisualizzaDTO) o).getId();
		if (otherId != null && this.getId() != null)
			return otherId.intValue() == this.getId().intValue();
		else
			return false;
	}

	@Override
	public int hashCode() {
		if (this.getId() != null)
			return this.getId() % ConstantsSingleton.DEFAULT_BUCKET_SIZE;
		else
			return 0;
	}

	public List<CvPatenteDTO> getListaCvPatentiDTO() {
		return listaCvPatentiDTO;
	}

	public void setListaCvPatentiDTO(List<CvPatenteDTO> listaCvPatentiDTO) {
		this.listaCvPatentiDTO = listaCvPatentiDTO;
	}

	public List<CvPatentinoDTO> getListaCvPatentiniDTO() {
		return listaCvPatentiniDTO;
	}

	public void setListaCvPatentiniDTO(List<CvPatentinoDTO> listaCvPatentiniDTO) {
		this.listaCvPatentiniDTO = listaCvPatentiniDTO;
	}

	public List<CvAlboDTO> getListaCvAlboDTO() {
		return listaCvAlboDTO;
	}

	public void setListaCvAlboDTO(List<CvAlboDTO> listaCvAlboDTO) {
		this.listaCvAlboDTO = listaCvAlboDTO;
	}

	public List<CvAgevolazioneDTO> getListaCvAgevolazioni() {
		return listaCvAgevolazioni;
	}

	public void setListaCvAgevolazioni(List<CvAgevolazioneDTO> listaCvAgevolazioni) {
		this.listaCvAgevolazioni = listaCvAgevolazioni;
	}

	public Boolean getSincronizzatoCliclavoro() {
		return sincronizzatoCliclavoro;
	}

	public void setSincronizzatoCliclavoro(Boolean sincronizzatoCliclavoro) {
		this.sincronizzatoCliclavoro = sincronizzatoCliclavoro;
	}

	/**
	 * Il proprietario dell'oggetto CvVisualizzaDTO e' lo stesso dell'oggetto CvDatiPersonali cui si riferisce
	 */
	public boolean isProprietary(Integer idPfPrincipal) {
		return cvDatiPersonaliDTO.isProprietario(idPfPrincipal);
	}

	public Boolean getTestataVisible() {
		return testataVisible;
	}

	public void setTestataVisible(Boolean testataVisible) {
		this.testataVisible = testataVisible;
	}

}
