package it.eng.myportal.dtos.filter;

import java.util.Date;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.dtos.IDTO;

public class FbSchedaFabbisognoFilterDTO implements IDTO {
	private static final long serialVersionUID = 8285202753562657940L;

	private String ragioneSociale;
	private Date dataDa;
	private Date dataA;
	private boolean escludiInLavorazione;
	private DeMansioneDTO deMansione;
	private DeMansioneMinDTO deMansioneMin;
	private DeTitoloDTO deTitolo;
	private DeComuneDTO deComune;
	private String profilo;

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

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

	public boolean isEscludiInLavorazione() {
		return escludiInLavorazione;
	}

	public void setEscludiInLavorazione(boolean escludiInLavorazione) {
		this.escludiInLavorazione = escludiInLavorazione;
	}

	public DeMansioneDTO getDeMansione() {
		return deMansione;
	}

	public void setDeMansione(DeMansioneDTO deMansione) {
		this.deMansione = deMansione;
	}

	public DeMansioneMinDTO getDeMansioneMin() {
		return deMansioneMin;
	}

	public void setDeMansioneMin(DeMansioneMinDTO deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	public DeTitoloDTO getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitoloDTO deTitolo) {
		this.deTitolo = deTitolo;
	}

	public DeComuneDTO getDeComune() {
		return deComune;
	}

	public void setDeComune(DeComuneDTO deComune) {
		this.deComune = deComune;
	}

	public String getProfilo() {
		return profilo;
	}

	public void setProfilo(String profilo) {
		this.profilo = profilo;
	}

}
