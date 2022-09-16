package it.eng.myportal.beans.adesione;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.BdAdesioneDTO;
import it.eng.myportal.dtos.DeBandoProgrammaDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.entity.home.BdAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeBandoProgrammaHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.ws.adesioneReimpiego.input.DichiarazioneCheck;

@ManagedBean
@ViewScoped
public class CercaBdAdesioneBean extends AbstractBaseBean {

	@EJB
	private BdAdesioneHome bdAdesioneHome;
	@EJB
	private DeBandoProgrammaHome deBandoProgrammaHome;
	@EJB
	private DeCpiHome deCpiHome;
	
	private String codFiscale;
	private String cognome;
	private String nome;
	private DeBandoProgrammaDTO deBandoProgrammaDTO;
	private Date dtAdesioneA;
	private Date dtAdesioneDa;
	private DichiarazioneCheck tipoDichiarazione;
	private String fasciaEta;
	private List<DeCpiDTO> selectedDeCpiDTOs;
	
	private List<DeBandoProgrammaDTO> deBandoProgrammaDTOList = new ArrayList<DeBandoProgrammaDTO>();
	private List<BdAdesioneDTO> bdAdesioneDTOs = new ArrayList<BdAdesioneDTO>();
	private List<DeCpiDTO> deCpiDTOs = new ArrayList<DeCpiDTO>();
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		deBandoProgrammaDTOList = deBandoProgrammaHome.findAllDTO();
		deCpiDTOs = deCpiHome.findDTOValidiByCodRegione(ConstantsSingleton.COD_REGIONE.toString());
	}

	public String getCodFiscale() {
		return codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDtAdesioneA() {
		return dtAdesioneA;
	}

	public void setDtAdesioneA(Date dtAdesioneA) {
		this.dtAdesioneA = dtAdesioneA;
	}

	public Date getDtAdesioneDa() {
		return dtAdesioneDa;
	}

	public void setDtAdesioneDa(Date dtAdesioneDa) {
		this.dtAdesioneDa = dtAdesioneDa;
	}

	public List<DeBandoProgrammaDTO> getDeBandoProgrammaDTOList() {
		return deBandoProgrammaDTOList;
	}

	public void setDeBandoProgrammaDTOList(List<DeBandoProgrammaDTO> deBandoProgrammaDTOList) {
		this.deBandoProgrammaDTOList = deBandoProgrammaDTOList;
	}

	public DeBandoProgrammaDTO getDeBandoProgrammaDTO() {
		return deBandoProgrammaDTO;
	}

	public void setDeBandoProgrammaDTO(DeBandoProgrammaDTO deBandoProgrammaDTO) {
		this.deBandoProgrammaDTO = deBandoProgrammaDTO;
	}
	
	public List<DeCpiDTO> getSelectedDeCpiDTOs() {
		return selectedDeCpiDTOs;
	}

	public void setSelectedDeCpiDTOs(List<DeCpiDTO> selectedDeCpiDTOs) {
		this.selectedDeCpiDTOs = selectedDeCpiDTOs;
	}

	public List<BdAdesioneDTO> getBdAdesioneDTOs() {
		return bdAdesioneDTOs;
	}

	public void setBdAdesioneDTOs(List<BdAdesioneDTO> bdAdesioneDTOs) {
		this.bdAdesioneDTOs = bdAdesioneDTOs;
	}
	
	public List<DeCpiDTO> getDeCpiDTOs() {
		return deCpiDTOs;
	}

	public void setDeCpiDTOs(List<DeCpiDTO> deCpiDTOs) {
		this.deCpiDTOs = deCpiDTOs;
	}

	public DichiarazioneCheck getTipoDichiarazione() {
		return tipoDichiarazione;
	}

	public void setTipoDichiarazione(DichiarazioneCheck dichiarazione) {
		this.tipoDichiarazione = dichiarazione;
	}
	
	public String getFasciaEta() {
		return fasciaEta;
	}

	public void setFasciaEta(String fasciaEta) {
		this.fasciaEta = fasciaEta;
	}

	public DichiarazioneCheck[] getTipoDichiarazioneValues() {
		return DichiarazioneCheck.values();
	}
	
	public boolean showTipoDichiarazioneInput(){
		if(deBandoProgrammaDTO == null){
			return false;
		} else {
			return deBandoProgrammaDTO.getId().equals(ConstantsSingleton.DeBandoProgramma.COD_REI) ? true : false;
		}
	}
	
	public boolean showEtaOver30Input(){
		if(deBandoProgrammaDTO == null){
			return false;
		} else {
			return deBandoProgrammaDTO.getId().equals(ConstantsSingleton.DeBandoProgramma.COD_UMBAT) ? true : false;
		}
	}
	
	public void switchShowedInputRelatedProgramma(final AjaxBehaviorEvent event){
		deBandoProgrammaDTO = (DeBandoProgrammaDTO)((UIOutput)event.getSource()).getValue();
		tipoDichiarazione = null;
		fasciaEta = null;
	}
	
	public void cerca(){
		bdAdesioneDTOs = bdAdesioneHome.findDTOByFilter(nome, cognome, codFiscale,
				deBandoProgrammaDTO != null ? deBandoProgrammaDTO.getId() : null, 
				dtAdesioneDa, dtAdesioneA, 
				tipoDichiarazione != null ? tipoDichiarazione.value() : null, fasciaEta, selectedDeCpiDTOs);			
	}
	
}
