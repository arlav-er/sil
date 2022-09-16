package it.eng.myportal.dtos;

import java.util.Date;

import it.eng.myportal.entity.CodValutazioneEnum;

/**
 * Data transfer object della tabella Candidatura, per la visualizzazione
 * 
 * @author Enrico D'Angelo
 * 
 * @see AbstractUpdatablePkDTO
 */
public class AcVisualizzaCandidaturaDTO extends AcCandidaturaDTO {
	private static final long serialVersionUID = 471713547950092173L;

	private String nomeCandidato;
	private String cognomeCandidato;
	private String descrizioneCurriculum;
	private String nomeLetteraAcc;
	private String ragioneSocialeAz;
	private String CodiceFiscaleCandicato;

	private String ragioneSocialePalese;
	private Integer idPfPrincipalPalese;
	private Integer idVetrinaPalese;

	private Integer idVetrina;
	private String codIdoneitaCandidatura;
	private String descrizioneVacancy;
	private String statoOccupazionale;
	private String flgIntermittente;	
	private boolean flagCresco;
	private boolean flagCVSelected = false;
	private boolean flagIdo;
	private boolean flagIdoVA;
    private CodValutazioneEnum codValutazioneAcCandidatura;
    private String codValutazioneAcCandidaturaDescr;
    private Date dataContatto;
	
	public boolean isFlagCVSelected() {
		return flagCVSelected;
	}

	public void setFlagCVSelected(boolean flagCVSelected) {
		this.flagCVSelected = flagCVSelected;
	}

	public AcVisualizzaCandidaturaDTO() {
	}

	public String getNomeCandidato() {
		return nomeCandidato;
	}

	public void setNomeCandidato(String nomeCandidato) {
		this.nomeCandidato = nomeCandidato;
	}

	public String getCognomeCandidato() {
		return cognomeCandidato;
	}

	public void setCognomeCandidato(String cognomeCandidato) {
		this.cognomeCandidato = cognomeCandidato;
	}

	public String getRagioneSocialeAz() {
		return ragioneSocialeAz;
	}

	public void setRagioneSocialeAz(String ragioneSocialeAz) {
		this.ragioneSocialeAz = ragioneSocialeAz;
	}

	public String getDescrizioneVacancy() {
		return descrizioneVacancy;
	}

	public void setDescrizioneVacancy(String descrizioneVacancy) {
		this.descrizioneVacancy = descrizioneVacancy;
	}

	public String getNomeLetteraAcc() {
		return nomeLetteraAcc;
	}

	public void setNomeLetteraAcc(String nomeLetteraAcc) {
		this.nomeLetteraAcc = nomeLetteraAcc;
	}

	public Integer getIdVetrina() {
		return idVetrina;
	}

	public void setIdVetrina(Integer idVetrina) {
		this.idVetrina = idVetrina;
	}

	public String getDescrizioneCurriculum() {
		return descrizioneCurriculum;
	}

	public void setDescrizioneCurriculum(String descrizioneCurriculum) {
		this.descrizioneCurriculum = descrizioneCurriculum;
	}

	public String getStatoOccupazionale() {
		return statoOccupazionale;
	}

	public void setStatoOccupazionale(String statoOccupazionale) {
		this.statoOccupazionale = statoOccupazionale;
	}

	public boolean getFlagCresco() {
		return flagCresco;
	}

	public void setFlagCresco(boolean flagCresco) {
		this.flagCresco = flagCresco;
	}


	public String getRagioneSocialePalese() {
		return ragioneSocialePalese;
	}

	public void setRagioneSocialePalese(String ragioneSocialePalese) {
		this.ragioneSocialePalese = ragioneSocialePalese;
	}

	public Integer getIdPfPrincipalPalese() {
		return idPfPrincipalPalese;
	}

	public void setIdPfPrincipalPalese(Integer idPfPrincipalPalese) {
		this.idPfPrincipalPalese = idPfPrincipalPalese;
	}

	public Integer getIdVetrinaPalese() {
		return idVetrinaPalese;
	}

	public void setIdVetrinaPalese(Integer idVetrinaPalese) {
		this.idVetrinaPalese = idVetrinaPalese;
	}

	@Override
	public String toString() {
		return String
				.format("AcVisualizzaCandidaturaDTO [id=%s, idCvDatiPersonali=%s, nomeCandidato=%s, cognomeCandidato=%s, descrizioneCurriculum=%s, idazienda=%s, ragioneSocialeAz=%s, idVaDatiVacancy=%s, descrizioneVacancy=%s, idCvLetteraAcc=%s, nomeLetteraAcc=%s, ultimaModifica=%s]",
						id, super.getIdCvDatiPersonali(), nomeCandidato, cognomeCandidato, descrizioneCurriculum,
						super.getIdPfPrincipalAzienda(), ragioneSocialeAz, super.getIdVaDatiVacancy(),
						descrizioneVacancy, super.getIdCvLetteraAcc(), nomeLetteraAcc, getDtmMod());
	}

	public String getFlgIntermittente() {
		return flgIntermittente;
	}

	public void setFlgIntermittente(String flgIntermittente) {
		this.flgIntermittente = flgIntermittente;
	}

	public String getCodIdoneitaCandidatura() {
		return codIdoneitaCandidatura;
	}

	public void setCodIdoneitaCandidatura(String codIdoneitaCandidatura) {
		this.codIdoneitaCandidatura = codIdoneitaCandidatura;
	}

	public boolean isFlagIdo() {
		return flagIdo;
	}

	public void setFlagIdo(boolean flagIdo) {
		this.flagIdo = flagIdo;
	}
	
	public boolean isFlagIdoVA() {
		return flagIdoVA;
	}

	public void setFlagIdoVA(boolean flagIdoVA) {
		this.flagIdoVA = flagIdoVA;
	}
	
	public CodValutazioneEnum getCodValutazioneAcCandidatura() {
		return codValutazioneAcCandidatura;
	}

	public void setCodValutazioneAcCandidatura(CodValutazioneEnum codValutazioneAcCandidatura) {
		this.codValutazioneAcCandidatura = codValutazioneAcCandidatura;
	}

	public String getCodValutazioneAcCandidaturaDescr() {
		return codValutazioneAcCandidaturaDescr;
	}

	public void setCodValutazioneAcCandidaturaDescr(String codValutazioneAcCandidaturaDescr) {
		this.codValutazioneAcCandidaturaDescr = codValutazioneAcCandidaturaDescr;
	}

	public Date getDataContatto() {
		return dataContatto;
	}

	public void setDataContatto(Date dataContatto) {
		this.dataContatto = dataContatto;
	}

	public String getCodiceFiscaleCandicato() {
		return CodiceFiscaleCandicato;
	}

	public void setCodiceFiscaleCandicato(String codiceFiscaleCandicato) {
		CodiceFiscaleCandicato = codiceFiscaleCandicato;
	}
}
