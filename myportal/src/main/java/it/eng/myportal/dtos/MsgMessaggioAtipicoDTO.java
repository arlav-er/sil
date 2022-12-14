package it.eng.myportal.dtos;

// default package
// Generated 20-feb-2013 13.47.02 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.enums.FasciaEta;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MsgMessaggioAtipico generated by hbm2java
 */
public class MsgMessaggioAtipicoDTO extends MsgMessaggioDTO implements IHasRisposte {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DeRegioneDTO deRegione;
	private DeStatoPraticaDTO deStatoPratica;
	private DeTitoloDTO deTitolo;
	private DeComuneDTO deComuneByCodComuneLavoro;
	private DeComuneDTO deComuneByCodComuneResidenza;
	private DeProvinciaDTO deProvincia;
	private String codSesso;
	private FasciaEta fasciaEta;
	private String committenza;
	private Boolean flagAssociazioneProfessionale;
	private String associazioneProfessionale;
	private String altraAttivita;
	private String motivoRifiuto;
	private Integer minuti;
	private String note;
	private DeTipoQuesitoDTO deTipoQuesito;
	private DeTipoPraticaDTO deTipoPratica;
	private DeTipoConsulenzaDTO tipoConsulenza;
	private DeAtpAttivitaSvoltaDTO deAtpAttivitaSvolta;
	private String attivitaSvoltaTesto;
	private DeAttivitaDTO deAttivita;
	private DeAtpContrattoDTO deAtpContrattoDTO;
	private DeAtpContrattoDTO deAtpContrattoUltimoDTO;
	/* serve solo in visualizzazione */
	private DeAtpContrattoDTO deAtpContrattoPadreDTO;
	private String nome;
	private String cognome;
	private String email;
	private String opzStatoOccupazionale;
	private String cap;
	private String telefonoFax;
	private Date dtInoltroCoord;
	private Date dtInoltroCPI;
	private Date dtRisposta;
	private Boolean flagIscrittoCpi;
	private DeComuneDTO comuneIscrizione;
	private DeAttivitaDTO settoreUltimoContratto;
	private Date dtFineUltimoContratto;
	private String allegatoCvName;
	private String allegatoCvNameTmp;
	private Boolean flgSeparataInps;

	/**
	 * L'ultima consulenza inviata dai consulenti
	 */
	private AtpConsulenzaDTO consulenza = new AtpConsulenzaDTO();
	private Set<DeProvinciaDTO> deProvincias = new HashSet<DeProvinciaDTO>(0);

	public MsgMessaggioAtipicoDTO() {
		deRegione = new DeRegioneDTO();
		deProvincia = new DeProvinciaDTO();
		deStatoPratica = new DeStatoPraticaDTO();
		deTitolo = new DeTitoloDTO();
		deComuneByCodComuneLavoro = new DeComuneDTO();
		deComuneByCodComuneResidenza = new DeComuneDTO();
		deProvincia = new DeProvinciaDTO();
		deTipoQuesito = new DeTipoQuesitoDTO();
		deTipoPratica = new DeTipoPraticaDTO();
		deAtpAttivitaSvolta = new DeAtpAttivitaSvoltaDTO();
		deAttivita = new DeAttivitaDTO();
		deAtpContrattoDTO = new DeAtpContrattoDTO();
		tipoConsulenza = new DeTipoConsulenzaDTO();
		settoreUltimoContratto = new DeAttivitaDTO();
		deAtpContrattoUltimoDTO = new DeAtpContrattoDTO();
		comuneIscrizione = new DeComuneDTO();
	}

	public DeAtpContrattoDTO getDeAtpContrattoPadreDTO() {
		return deAtpContrattoPadreDTO;
	}

	public void setDeAtpContrattoPadreDTO(DeAtpContrattoDTO deAtpContrattoPadreDTO) {
		this.deAtpContrattoPadreDTO = deAtpContrattoPadreDTO;
	}

	public String getAllegatoCvName() {
		return allegatoCvName;
	}

	public void setAllegatoCvName(String allegatoCvName) {
		this.allegatoCvName = allegatoCvName;
	}

	public String getAllegatoCvNameTmp() {
		return allegatoCvNameTmp;
	}

	public void setAllegatoCvNameTmp(String allegatoCvNameTmp) {
		this.allegatoCvNameTmp = allegatoCvNameTmp;
	}

	public DeRegioneDTO getDeRegione() {
		return this.deRegione;
	}

	public void setDeRegione(DeRegioneDTO deRegione) {
		this.deRegione = deRegione;
	}

	public DeStatoPraticaDTO getDeStatoPratica() {
		return this.deStatoPratica;
	}

	public void setDeStatoPratica(DeStatoPraticaDTO deStatoPratica) {
		this.deStatoPratica = deStatoPratica;
	}

	public DeTitoloDTO getDeTitolo() {
		return this.deTitolo;
	}

	public void setDeTitolo(DeTitoloDTO deTitolo) {
		this.deTitolo = deTitolo;
	}

	public DeComuneDTO getDeComuneByCodComuneLavoro() {
		return this.deComuneByCodComuneLavoro;
	}

	public void setDeComuneByCodComuneLavoro(DeComuneDTO deComuneByCodComuneLavoro) {
		this.deComuneByCodComuneLavoro = deComuneByCodComuneLavoro;
	}

	public DeComuneDTO getDeComuneByCodComuneResidenza() {
		return this.deComuneByCodComuneResidenza;
	}

	public void setDeComuneByCodComuneResidenza(DeComuneDTO deComuneByCodComuneResidenza) {
		this.deComuneByCodComuneResidenza = deComuneByCodComuneResidenza;
	}

	public DeProvinciaDTO getDeProvincia() {
		return this.deProvincia;
	}

	public void setDeProvincia(DeProvinciaDTO deProvincia) {
		this.deProvincia = deProvincia;
	}

	public String getCodSesso() {
		return this.codSesso;
	}

	public void setCodSesso(String codSesso) {
		this.codSesso = codSesso;
	}

	public FasciaEta getFasciaEta() {
		return this.fasciaEta;
	}

	public void setFasciaEta(FasciaEta numEta) {
		this.fasciaEta = numEta;
	}

	public String getCommittenza() {
		return this.committenza;
	}

	public void setCommittenza(String committenza) {
		this.committenza = committenza;
	}

	public Boolean getFlagAssociazioneProfessionale() {
		return this.flagAssociazioneProfessionale;
	}

	public void setFlagAssociazioneProfessionale(Boolean flagAssociazioneProfessionale) {
		this.flagAssociazioneProfessionale = flagAssociazioneProfessionale;
	}

	public String getAssociazioneProfessionale() {
		return this.associazioneProfessionale;
	}

	public void setAssociazioneProfessionale(String associazioneProfessionale) {
		this.associazioneProfessionale = associazioneProfessionale;
	}

	public String getAltraAttivita() {
		return this.altraAttivita;
	}

	public void setAltraAttivita(String altraAttivita) {
		this.altraAttivita = altraAttivita;
	}

	public String getMotivoRifiuto() {
		return this.motivoRifiuto;
	}

	public void setMotivoRifiuto(String motivoRifiuto) {
		this.motivoRifiuto = motivoRifiuto;
	}

	public Integer getMinuti() {
		return minuti;
	}

	public void setMinuti(Integer minuti) {
		this.minuti = minuti;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public DeTipoQuesitoDTO getDeTipoQuesito() {
		return deTipoQuesito;
	}

	public void setDeTipoQuesito(DeTipoQuesitoDTO deTipoQuesito) {
		this.deTipoQuesito = deTipoQuesito;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpzStatoOccupazionale() {
		return opzStatoOccupazionale;
	}

	public void setOpzStatoOccupazionale(String opzStatoOccupazionale) {
		this.opzStatoOccupazionale = opzStatoOccupazionale;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getTelefonoFax() {
		return telefonoFax;
	}

	public void setTelefonoFax(String telefonoFax) {
		this.telefonoFax = telefonoFax;
	}

	public DeTipoPraticaDTO getDeTipoPratica() {
		return deTipoPratica;
	}

	public void setDeTipoPratica(DeTipoPraticaDTO deTipoPratica) {
		this.deTipoPratica = deTipoPratica;
	}

	public DeAtpAttivitaSvoltaDTO getDeAtpAttivitaSvolta() {
		return deAtpAttivitaSvolta;
	}

	public void setDeAtpAttivitaSvolta(DeAtpAttivitaSvoltaDTO deAtpAttivitaSvolta) {
		this.deAtpAttivitaSvolta = deAtpAttivitaSvolta;
	}

	public DeAttivitaDTO getDeAttivita() {
		return deAttivita;
	}

	public void setDeAttivita(DeAttivitaDTO deAttivita) {
		this.deAttivita = deAttivita;
	}

	public DeAtpContrattoDTO getDeAtpContrattoDTO() {
		return deAtpContrattoDTO;
	}

	public void setDeAtpContrattoDTO(DeAtpContrattoDTO deAtpContrattoDTO) {
		this.deAtpContrattoDTO = deAtpContrattoDTO;
	}

	public Set<DeProvinciaDTO> getDeProvincias() {
		return deProvincias;
	}

	public void setDeProvincias(Set<DeProvinciaDTO> deProvincias) {
		this.deProvincias = deProvincias;
	}

	/**
	 * Recupera il messaggio inoltrato dal coordinatore al CPI
	 * 
	 * @return
	 */
	public MsgMessaggioDTO getInoltroDaCoordinatoreVersoCPI() {
		if (getInoltri() != null && !getInoltri().isEmpty())
			return getInoltri().get(0);
		else
			return null;
	}

	/**
	 * Recupera i messaggi inoltrati dal CPI ai consulenti
	 * 
	 * @return
	 */
	public List<MsgMessaggioDTO> getInoltriDaCPIversoConsulente() {
		if (getInoltroDaCoordinatoreVersoCPI() != null && getInoltroDaCoordinatoreVersoCPI().getInoltri() != null
				&& !getInoltroDaCoordinatoreVersoCPI().getInoltri().isEmpty())
			return getInoltroDaCoordinatoreVersoCPI().getInoltri();
		else
			return null;
	}

	public List<MsgMessaggioDTO> getInoltriDaCoordinatoreVersoCPI() {
		return getInoltri();
	}

	/**
	 * Restituisce true se il consulente ha risposto alla pratica e non l'ha
	 * rifiutata
	 * 
	 * @return
	 */
	public boolean isConsulenteOk() {
		return deStatoPratica.getId().equals(ConstantsSingleton.DeStatoPratica.CON_RISP);
	}

	/**
	 * Restituisce true se il consulente ha risposto alla pratica, rifiutandola
	 * 
	 * @return
	 */
	public boolean isConsulenteKo() {
		return deStatoPratica.getId().equals(ConstantsSingleton.DeStatoPratica.CON_RIF);
	}

	/**
	 * Restituisce true se la pratica ?? stata inoltrata dal coordinatore al CPI
	 * 
	 * @return
	 */
	public boolean isInoltratoCpi() {
		return deStatoPratica.getId().equals(ConstantsSingleton.DeStatoPratica.INOL_CPI);
	}

	/**
	 * Restituisce true se la pratica ?? stata inoltrata dal CPI al consulente
	 * 
	 * @return
	 */
	public boolean isInoltratoConsulente() {
		return deStatoPratica.getId().equals(ConstantsSingleton.DeStatoPratica.INOL_CON);
	}

	/**
	 * Restituisce true se il CPI ha risposto al cittadino e la pratica ?? chiusa
	 * 
	 * @return
	 */
	public boolean isRispostoCittadino() {
		return deStatoPratica.getId().equals(ConstantsSingleton.DeStatoPratica.CHIUSA);
	}

	/**
	 * Restituisce true se il CPI ha risposto al cittadino e la pratica ?? chiusa
	 * 
	 * @return
	 */
	public boolean isPraticaRifiutata() {
		return deStatoPratica.getId().equals(ConstantsSingleton.DeStatoPratica.RIFIUTO);
	}

	public DeTipoConsulenzaDTO getTipoConsulenza() {
		return tipoConsulenza;
	}

	public void setTipoConsulenza(DeTipoConsulenzaDTO tipoConsulenza) {
		this.tipoConsulenza = tipoConsulenza;
	}

	public MsgMessaggioDTO getRisposta() {
		if (this.risposte != null)
			return this.risposte.get(0);
		else
			return null;
	}

	public String getAttivitaSvoltaTesto() {
		return attivitaSvoltaTesto;
	}

	public void setAttivitaSvoltaTesto(String attivitaSvoltaTesto) {
		this.attivitaSvoltaTesto = attivitaSvoltaTesto;
	}

	public Date getDtInoltroCoord() {
		return dtInoltroCoord;
	}

	public void setDtInoltroCoord(Date dtInoltroCoord) {
		this.dtInoltroCoord = dtInoltroCoord;
	}

	public Date getDtInoltroCPI() {
		return dtInoltroCPI;
	}

	public void setDtInoltroCPI(Date dtInoltroCPI) {
		this.dtInoltroCPI = dtInoltroCPI;
	}

	public Date getDtRisposta() {
		return dtRisposta;
	}

	public void setDtRisposta(Date dtRisposta) {
		this.dtRisposta = dtRisposta;
	}

	public Boolean getFlagIscrittoCpi() {
		return flagIscrittoCpi;
	}

	public void setFlagIscrittoCpi(Boolean flagIscrittoCpi) {
		this.flagIscrittoCpi = flagIscrittoCpi;
	}

	public DeComuneDTO getComuneIscrizione() {
		return comuneIscrizione;
	}

	public void setComuneIscrizione(DeComuneDTO comuneIscrizione) {
		this.comuneIscrizione = comuneIscrizione;
	}

	public DeAtpContrattoDTO getDeAtpContrattoUltimoDTO() {
		return deAtpContrattoUltimoDTO;
	}

	public void setDeAtpContrattoUltimoDTO(DeAtpContrattoDTO deAtpContrattoUltimoDTO) {
		this.deAtpContrattoUltimoDTO = deAtpContrattoUltimoDTO;
	}

	public DeAttivitaDTO getSettoreUltimoContratto() {
		return settoreUltimoContratto;
	}

	public void setSettoreUltimoContratto(DeAttivitaDTO settoreUltimoContratto) {
		this.settoreUltimoContratto = settoreUltimoContratto;
	}

	public Date getDtFineUltimoContratto() {
		return dtFineUltimoContratto;
	}

	public void setDtFineUltimoContratto(Date dtFineUltimoContratto) {
		this.dtFineUltimoContratto = dtFineUltimoContratto;
	}

	public AtpConsulenzaDTO getConsulenza() {
		return consulenza;
	}

	public void setConsulenza(AtpConsulenzaDTO consulenza) {
		this.consulenza = consulenza;
	}

	public Boolean getFlgSeparataInps() {
		return flgSeparataInps;
	}

	public void setFlgSeparataInps(Boolean flgSeparataInps) {
		this.flgSeparataInps = flgSeparataInps;
	}

}
