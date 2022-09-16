package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.eng.myportal.entity.CodValutazioneEnum;

/**
 * Data transfer object della tabella Candidatura
 * 
 * 
 * @author Rodi A.
 * 
 * @see AbstractUpdatablePkDTO
 */
public class AcCandidaturaDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 6375989575058389965L;

	// curriculum nel caso sia una candidatura interna
	private Integer idCvDatiPersonali;
	// curriculum nel caso sia una candidatura verso clic lavoro
	private Integer idCvDatiPersonaliCl;
	// true se la candidatura avviene verso una vacancy di clic lavoro
	private boolean flagClicLavoro;
	// Id dell'azienda verso la quale ci si candida
	private Integer idPfPrincipalAzienda;

	private Integer idPfPrincipalAziendaPalese;

	private Integer idVaDatiVacancy;
	private Integer idCvLetteraAcc;
	private DeIdoneitaCandidaturaDTO deIdoneitaCandidaturaDTO;

	// ID del file allegato
	private Integer idAcAllegato;

	// Nome del file allegato
	private String allegatoFileName;

	// nome del file temporaneo che deve essere allegato
	private String allegatoFileNameTmp;

	// Commento personale al messaggio scritto all'invio di una candidatura
	private String commento;

	private String codProvenienzaCandidatura;
	
	// Dati aggiuntivi SIL, solo per candidature a vacancy CRESCO
	private List<AcCandidaturaDatiDTO> acCandidaturaDatiList = new ArrayList<AcCandidaturaDatiDTO>(0);
	
	private int numCandidatura;
	
	private Date dataContatto;
	
	private CodValutazioneEnum codValutazioneAcCandidatura;

	public AcCandidaturaDTO() {
	}

	private AcCandidaturaDTO(Integer idAcAllegato) {
		super();
		this.idAcAllegato = idAcAllegato;
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public Integer getIdPfPrincipalAzienda() {
		return idPfPrincipalAzienda;
	}

	public void setIdPfPrincipalAzienda(Integer idPfPrincipalAzienda) {
		this.idPfPrincipalAzienda = idPfPrincipalAzienda;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getIdCvLetteraAcc() {
		return idCvLetteraAcc;
	}

	public void setIdCvLetteraAcc(Integer idCvLetteraAcc) {
		this.idCvLetteraAcc = idCvLetteraAcc;
	}

	public DeIdoneitaCandidaturaDTO getDeIdoneitaCandidaturaDTO() {
		return deIdoneitaCandidaturaDTO;
	}

	public void setDeIdoneitaCandidaturaDTO(DeIdoneitaCandidaturaDTO deIdoneitaCandidaturaDTO) {
		this.deIdoneitaCandidaturaDTO = deIdoneitaCandidaturaDTO;
	}

	@Override
	public String toString() {
		return String
				.format("AcCandidaturaDTO [id=%s, descrizioneCurriculum=%s, idaziendainfo=%s, idVaDatiVacancy=%s, idCvLetteraAcc=%s, descrizioneIdoneitaCandidatura=%s, ultimaModifica=%s]",
						id, idCvDatiPersonali, idPfPrincipalAzienda, idVaDatiVacancy, idCvLetteraAcc,
						deIdoneitaCandidaturaDTO.descrizione, getDtmMod());
	}

	public String getAllegatoFileName() {
		return allegatoFileName;
	}

	public void setAllegatoFileName(String allegatoFileName) {
		this.allegatoFileName = allegatoFileName;
	}

	public String getAllegatoFileNameTmp() {
		return allegatoFileNameTmp;
	}

	public void setAllegatoFileNameTmp(String allegatoFileNameTmp) {
		this.allegatoFileNameTmp = allegatoFileNameTmp;
	}

	public Integer getIdAcAllegato() {
		return idAcAllegato;
	}

	public void setIdAcAllegato(Integer idAcAllegato) {
		this.idAcAllegato = idAcAllegato;
	}

	public Integer getIdCvDatiPersonaliCl() {
		return idCvDatiPersonaliCl;
	}

	public void setIdCvDatiPersonaliCl(Integer idCvDatiPersonaliCl) {
		this.idCvDatiPersonaliCl = idCvDatiPersonaliCl;
	}

	public boolean getFlagClicLavoro() {
		return flagClicLavoro;
	}

	public void setFlagClicLavoro(boolean flagClicLavoro) {
		this.flagClicLavoro = flagClicLavoro;
	}

	public String getCommento() {
		return commento;
	}

	public void setCommento(String commento) {
		this.commento = commento;
	}

	public List<AcCandidaturaDatiDTO> getAcCandidaturaDatiList() {
		return acCandidaturaDatiList;
	}

	public void setAcCandidaturaDatiList(List<AcCandidaturaDatiDTO> acCandidaturaDatiList) {
		this.acCandidaturaDatiList = acCandidaturaDatiList;
	}

	public String getCodProvenienzaCandidatura() {
		return codProvenienzaCandidatura;
	}

	public void setCodProvenienzaCandidatura(String codProvenienzaCandidatura) {
		this.codProvenienzaCandidatura = codProvenienzaCandidatura;
	}

	public int getNumCandidatura() {
		return numCandidatura;
	}

	public void setNumCandidatura(int numCandidatura) {
		this.numCandidatura = numCandidatura;
	}

	public CodValutazioneEnum getCodValutazioneAcCandidatura() {
		return codValutazioneAcCandidatura;
	}

	public void setCodValutazioneAcCandidatura(CodValutazioneEnum codValutazioneAcCandidatura) {
		this.codValutazioneAcCandidatura = codValutazioneAcCandidatura;
	}

	public Date getDataContatto() {
		return dataContatto;
	}

	public void setDataContatto(Date dataContatto) {
		this.dataContatto = dataContatto;
	}

	public Integer getIdPfPrincipalAziendaPalese() {
		return idPfPrincipalAziendaPalese;
	}

	public void setIdPfPrincipalAziendaPalese(Integer idPfPrincipalAziendaPalese) {
		this.idPfPrincipalAziendaPalese = idPfPrincipalAziendaPalese;
	}
}
