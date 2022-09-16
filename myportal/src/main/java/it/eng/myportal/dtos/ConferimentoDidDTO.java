package it.eng.myportal.dtos;

import java.util.Date;

public class ConferimentoDidDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -5252487191617672080L;

	private Integer idPfPrincipal;
	private Date dataDid;
	private String opzTipoEvento;
	private String codEntePromotore;
	private Date dataEvento;
	private String codiceFiscale;
	private Integer eta;
	private String opzGenere;
	private DeCittadinanzaMinDTO deCittadinanzaMinDTO;
	private DePresenzaItaliaMinDTO dePresenzaItaliaMinDTO;
	private DeProvinciaDTO deProvinciaResidenzaDTO;
	private Boolean flgEsperienzaLavoro;
	private DeCondizioneOccupazMinDTO deCondizioneOccupazMinDTO;
	private Integer numMesiRapporto;
	private DePosizioneProfessionaleMinDTO dePosizioneProfessionaleMinDTO;
	private Integer numMesiRicercaLavoro;
	private DeIscrizioneCorsoMinDTO deIscrizioneCorsoMinDTO;
	private Integer numComponentiFamiglia;
	private Boolean flgFigliCarico;
	private Boolean flgFigliMinoriCarico;
	private String condizioneOccupazCalc;
	private String durataDisoccupazCalc;
	private DeTitoloDTO deTitoloDTO;
	private Long idProfiling;
	private Double probabilita;
	private Date dataInserimento;

	public Date getDataDid() {
		return dataDid;
	}

	public void setDataDid(Date dataDid) {
		this.dataDid = dataDid;
	}

	public String getOpzTipoEvento() {
		return opzTipoEvento;
	}

	public void setOpzTipoEvento(String opzTipoEvento) {
		this.opzTipoEvento = opzTipoEvento;
	}

	public String getCodEntePromotore() {
		return codEntePromotore;
	}

	public void setCodEntePromotore(String codEntePromotore) {
		this.codEntePromotore = codEntePromotore;
	}

	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Integer getEta() {
		return eta;
	}

	public void setEta(Integer eta) {
		this.eta = eta;
	}

	public String getOpzGenere() {
		return opzGenere;
	}

	public void setOpzGenere(String opzGenere) {
		this.opzGenere = opzGenere;
	}

	public Boolean getFlgEsperienzaLavoro() {
		return flgEsperienzaLavoro;
	}

	public void setFlgEsperienzaLavoro(Boolean flgEsperienzaLavoro) {
		this.flgEsperienzaLavoro = flgEsperienzaLavoro;
	}

	public Integer getNumMesiRapporto() {
		return numMesiRapporto;
	}

	public void setNumMesiRapporto(Integer numMesiRapporto) {
		this.numMesiRapporto = numMesiRapporto;
	}

	public Integer getNumMesiRicercaLavoro() {
		return numMesiRicercaLavoro;
	}

	public void setNumMesiRicercaLavoro(Integer numMesiRicercaLavoro) {
		this.numMesiRicercaLavoro = numMesiRicercaLavoro;
	}

	public Integer getNumComponentiFamiglia() {
		return numComponentiFamiglia;
	}

	public void setNumComponentiFamiglia(Integer numComponentiFamiglia) {
		this.numComponentiFamiglia = numComponentiFamiglia;
	}

	public Boolean getFlgFigliCarico() {
		return flgFigliCarico;
	}

	public void setFlgFigliCarico(Boolean flgFigliCarico) {
		this.flgFigliCarico = flgFigliCarico;
	}

	public Boolean getFlgFigliMinoriCarico() {
		return flgFigliMinoriCarico;
	}

	public void setFlgFigliMinoriCarico(Boolean flgFigliMinoriCarico) {
		this.flgFigliMinoriCarico = flgFigliMinoriCarico;
	}

	public String getCondizioneOccupazCalc() {
		return condizioneOccupazCalc;
	}

	public void setCondizioneOccupazCalc(String condizioneOccupazCalc) {
		this.condizioneOccupazCalc = condizioneOccupazCalc;
	}

	public String getDurataDisoccupazCalc() {
		return durataDisoccupazCalc;
	}

	public void setDurataDisoccupazCalc(String durataDisoccupazCalc) {
		this.durataDisoccupazCalc = durataDisoccupazCalc;
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public DeCittadinanzaMinDTO getDeCittadinanzaMinDTO() {
		return deCittadinanzaMinDTO;
	}

	public void setDeCittadinanzaMinDTO(DeCittadinanzaMinDTO deCittadinanzaMinDTO) {
		this.deCittadinanzaMinDTO = deCittadinanzaMinDTO;
	}

	public DePresenzaItaliaMinDTO getDePresenzaItaliaMinDTO() {
		return dePresenzaItaliaMinDTO;
	}

	public void setDePresenzaItaliaMinDTO(DePresenzaItaliaMinDTO dePresenzaItaliaMinDTO) {
		this.dePresenzaItaliaMinDTO = dePresenzaItaliaMinDTO;
	}

	public DeProvinciaDTO getDeProvinciaResidenzaDTO() {
		return deProvinciaResidenzaDTO;
	}

	public void setDeProvinciaResidenzaDTO(DeProvinciaDTO deProvinciaResidenzaDTO) {
		this.deProvinciaResidenzaDTO = deProvinciaResidenzaDTO;
	}

	public DeCondizioneOccupazMinDTO getDeCondizioneOccupazMinDTO() {
		return deCondizioneOccupazMinDTO;
	}

	public void setDeCondizioneOccupazMinDTO(DeCondizioneOccupazMinDTO deCondizioneOccupazMinDTO) {
		this.deCondizioneOccupazMinDTO = deCondizioneOccupazMinDTO;
	}

	public DePosizioneProfessionaleMinDTO getDePosizioneProfessionaleMinDTO() {
		return dePosizioneProfessionaleMinDTO;
	}

	public void setDePosizioneProfessionaleMinDTO(DePosizioneProfessionaleMinDTO dePosizioneProfessionaleMinDTO) {
		this.dePosizioneProfessionaleMinDTO = dePosizioneProfessionaleMinDTO;
	}

	public DeIscrizioneCorsoMinDTO getDeIscrizioneCorsoMinDTO() {
		return deIscrizioneCorsoMinDTO;
	}

	public void setDeIscrizioneCorsoMinDTO(DeIscrizioneCorsoMinDTO deIscrizioneCorsoMinDTO) {
		this.deIscrizioneCorsoMinDTO = deIscrizioneCorsoMinDTO;
	}

	public DeTitoloDTO getDeTitoloDTO() {
		return deTitoloDTO;
	}

	public void setDeTitoloDTO(DeTitoloDTO deTitoloDTO) {
		this.deTitoloDTO = deTitoloDTO;
	}

	public Long getIdProfiling() {
		return idProfiling;
	}

	public void setIdProfiling(Long idProfiling) {
		this.idProfiling = idProfiling;
	}

	public Double getProbabilita() {
		return probabilita;
	}

	public void setProbabilita(Double probabilita) {
		this.probabilita = probabilita;
	}

	public Date getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Date dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConferimentoDidDTO other = (ConferimentoDidDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
