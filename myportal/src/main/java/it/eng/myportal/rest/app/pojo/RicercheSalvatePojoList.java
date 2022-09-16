package it.eng.myportal.rest.app.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RicercheSalvatePojoList implements Serializable {

	private static final long serialVersionUID = -5850270481814223155L;

	private Long numRicercheTotali;
	private List<RicercheSalvatePojo> listaRicerche;

	public RicercheSalvatePojoList() {
		listaRicerche = new ArrayList<RicercheSalvatePojo>();
		numRicercheTotali = Long.valueOf(0);
	}

	public Long getNumRicercheTotali() {
		return numRicercheTotali;
	}

	public void setNumRicercheTotali(Long numRicercheTotali) {
		this.numRicercheTotali = numRicercheTotali;
	}

	public List<RicercheSalvatePojo> getListaRicerche() {
		return listaRicerche;
	}

	public void setListaRicerche(List<RicercheSalvatePojo> listaRicerche) {
		this.listaRicerche = listaRicerche;
	}
}
