package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

public class RegisterPatronatoDTO extends RegisterDTO implements IDTO {

	private static final long serialVersionUID = 1L;

	private DeProvinciaDTO provincia;
	private String denominazione;
	private List<DeProvinciaDTO> altreProvince;

	public RegisterPatronatoDTO() {
		super();
		provincia = new DeProvinciaDTO();
		altreProvince = new ArrayList<DeProvinciaDTO>();
		this.denominazione = "";
	}

	public DeProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public List<DeProvinciaDTO> getAltreProvince() {
		return altreProvince;
	}

	public void setAltreProvince(List<DeProvinciaDTO> altreProvince) {
		this.altreProvince = altreProvince;
	}

}
