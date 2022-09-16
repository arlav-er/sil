package it.eng.myportal.dtos;

import java.util.Date;
import java.util.List;

public class YgAdesioneAziendaDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5874968256731830037L;
		
	private PfPrincipalDTO pfPrincipalDTO;
	private Boolean flgAdesione;
	private Date dtAdesione;
	private DeQualificaSrqDTO deQualificaSrqDTO;
	private DeAttivitaMinDTO deAttivitaMinDTO;
	private List<YgAdesioneAziendaSedeDTO> ygAdesioneAziendaSedeDTOList;	
	
	private String email1;
	private String email2;
	private String email3;
	
	public PfPrincipalDTO getPfPrincipalDTO() {
		return pfPrincipalDTO;
	}
	
	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}
	
	public Boolean getFlgAdesione() {
		return flgAdesione;
	}
	
	public void setFlgAdesione(Boolean flgAdesione) {
		this.flgAdesione = flgAdesione;
	}
	
	public Date getDtAdesione() {
		return dtAdesione;
	}
	
	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}
	
	public DeQualificaSrqDTO getDeQualificaSrqDTO() {
		return deQualificaSrqDTO;
	}
	
	public void setDeQualificaSrqDTO(DeQualificaSrqDTO deQualificaSrqDTO) {
		this.deQualificaSrqDTO = deQualificaSrqDTO;
	}
	
	public DeAttivitaMinDTO getDeAttivitaMinDTO() {
		return deAttivitaMinDTO;
	}
	
	public void setdeAttivitaMinDTO(DeAttivitaMinDTO deAttivitaMinDTO) {
		this.deAttivitaMinDTO = deAttivitaMinDTO;
	}
	
	public List<YgAdesioneAziendaSedeDTO> getYgAdesioneAziendaSedeDTOList() {
		return ygAdesioneAziendaSedeDTOList;
	}
	
	public void setYgAdesioneAziendaSedeDTOList(
			List<YgAdesioneAziendaSedeDTO> ygAdesioneAziendaSedeDTOList) {
		this.ygAdesioneAziendaSedeDTOList = ygAdesioneAziendaSedeDTOList;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}
	
}
