package it.eng.myportal.dtos;

import java.util.Date;

/**
 * DTO che rappresenta la vetrina di una Azienda
 * 
 * @author Rodi A.
 * 
 */
public class SvAziendaInfoDTO extends AbstractUpdatablePkDTO implements IUpdatable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7139359861769848996L;
	private String ragioneSociale;
	private String chiSiamo;
	private String doveSiamo;
	private String mission;
	private String storia;
	private String profiliRichiesti;
	private String formazioneCrescita;

	private String codiceTemplate;
	private String codiceSezione;

	private Boolean pubblicabile;
	private Date dataPubblicazione;

	public SvAziendaInfoDTO() {
		super();
		setPubblicabile(Boolean.TRUE);
	}

	public String getChiSiamo() {
		return chiSiamo;
	}

	public void setChiSiamo(String chiSiamo) {
		this.chiSiamo = chiSiamo;
	}

	public String getDoveSiamo() {
		return doveSiamo;
	}

	public void setDoveSiamo(String doveSiamo) {
		this.doveSiamo = doveSiamo;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	public String getStoria() {
		return storia;
	}

	public void setStoria(String storia) {
		this.storia = storia;
	}

	public String getProfiliRichiesti() {
		return profiliRichiesti;
	}

	public void setProfiliRichiesti(String profiliRichiesti) {
		this.profiliRichiesti = profiliRichiesti;
	}

	public String getFormazioneCrescita() {
		return formazioneCrescita;
	}

	public void setFormazioneCrescita(String formazioneCrescita) {
		this.formazioneCrescita = formazioneCrescita;
	}

	public String getCodiceTemplate() {
		return codiceTemplate;
	}

	public void setCodiceTemplate(String codSvDeTemplate) {
		this.codiceTemplate = codSvDeTemplate;
	}

	public Boolean getPubblicabile() {
		return pubblicabile;
	}

	public void setPubblicabile(Boolean pubblicabile) {
		this.pubblicabile = pubblicabile;
	}

	public Date getDataPubblicazione() {
		return dataPubblicazione;
	}

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public String getCodiceSezione() {
		return codiceSezione;
	}

	public void setCodiceSezione(String codSvDeSezione) {
		this.codiceSezione = codSvDeSezione;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

}
