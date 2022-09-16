package it.eng.myportal.beans.voucher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.VchEntiAccreditatiDTO;
import it.eng.myportal.dtos.VchRichiestaAttDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VchEntiAccreditatiHome;
import it.eng.myportal.entity.home.VchRichiestaAttHome;

@ManagedBean(name="vchProvinciaBean")
@ViewScoped
public class VchProvinciaBean extends AbstractBaseBean {

	@EJB
	VchRichiestaAttHome vchRichiestaAttHome;
	
	@EJB
	VchEntiAccreditatiHome vchEntiAccreditatiHome;
	
	@EJB
	UtenteInfoHome utenteInfoHome;
	
	private List<VchRichiestaAttDTO> vchRichiestaAttDTOList = new ArrayList<VchRichiestaAttDTO>();
	private List<VchEntiAccreditatiDTO> vchEntiAccreditatiDTOList = new ArrayList<VchEntiAccreditatiDTO>();
	private VchEntiAccreditatiDTO vchEntiAccreditatiDTO;
	private String codFiscaleCitt;
	private Integer codAttivazione;
	private Date dataDa;
	private Date dataA;
	
	private boolean showList = false;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		vchEntiAccreditatiDTOList = vchEntiAccreditatiHome.findAllDTO();
	}
	
	//Getter and setter
	public Date getDataDa() {
		return dataDa;
	}
	
	public void setDataDa(Date dataDa) {
		this.dataDa = dataDa;
	}
	
	public Date getDataA() {
		return dataA;
	}
	
	public void setDataA(Date dataA) {
		this.dataA = dataA;
	}
	
	public Integer getCodAttivazione() {
		return codAttivazione;
	}
	
	public void setCodAttivazione(Integer codAttivazione) {
		this.codAttivazione = codAttivazione;
	}
	
	public String getCodFiscaleCitt() {
		return codFiscaleCitt;
	}

	public void setCodFiscaleCitt(String codFiscaleCitt) {
		this.codFiscaleCitt = codFiscaleCitt;
	}

	public VchEntiAccreditatiDTO getVchEntiAccreditatiDTO() {
		return vchEntiAccreditatiDTO;
	}

	public void setVchEntiAccreditatiDTO(VchEntiAccreditatiDTO vchEntiAccreditatiDTO) {
		this.vchEntiAccreditatiDTO = vchEntiAccreditatiDTO;
	}

	public List<VchEntiAccreditatiDTO> getVchEntiAccreditatiDTOList() {
		return vchEntiAccreditatiDTOList;
	}

	public void setVchEntiAccreditatiDTOList(List<VchEntiAccreditatiDTO> vchEntiAccreditatiDTOList) {
		this.vchEntiAccreditatiDTOList = vchEntiAccreditatiDTOList;
	}

	public int getSizeList() {
		return vchRichiestaAttDTOList.size();
	}
	
	public List<VchRichiestaAttDTO> getVchRichiestaAttDTOList() {
		return vchRichiestaAttDTOList;
	}

	public void setVchRichiestaAttDTOList(List<VchRichiestaAttDTO> vchRichiestaAttDTOList) {
		this.vchRichiestaAttDTOList = vchRichiestaAttDTOList;
	}
	

	public boolean isShowList() {
		return showList;
	}

	public void setShowList(boolean showList) {
		this.showList = showList;
	}

	//Metodi
	public void cercaVchRichiesta(){
		vchRichiestaAttDTOList = vchRichiestaAttHome.findAllByFilter(codAttivazione, codFiscaleCitt, vchEntiAccreditatiDTO, dataDa, dataA);
		showList = true;
		log.info("Lista delle richieste voucher");
	}
	
	
}
