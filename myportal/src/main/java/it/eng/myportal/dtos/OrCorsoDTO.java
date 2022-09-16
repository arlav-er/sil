package it.eng.myportal.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrCorsoDTO extends AbstractUpdatablePkDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5495345756931436985L;
	
    private Integer idOrEnteCorso;
    private String ragioneSocialeEnte;
    private String codMansione;
    private String strMansione;
    private String codQualificaSrqRilasciata;
    private String strQualificaSrqRilasciata;
    private String codProfessione;
    private String strProfessione;
    private String titoloCorso;
    private String codTipoFormazione;
    private String strTipoFormazione;
    private String descrizioneCorso;
    private String contenutiPercorso;
    private String requisitiAccesso;
    private String iscrizione;
    private String criteriSelezione;
    private Date dtAvvio;
    private String attestatoRilascio;
    private String durata;
    private String periodoSvolgimento;
    private Integer numeroPartecipanti;
    private BigDecimal quotaIscrizioneIndividuale;
    private String amministrazioneCompetente;
    private String codiceIdentificativo;
    private String scuolaCapofila;
    private String universita;
    private String imprese;
    private Date dtmIns;
    private Date dtmMod;
    private Integer idPrincipalIns;
    private Integer idPrincipalMod;
    private List<OrSedeCorsoDTO> orSedeCorsos;
    private List<OrEdizioneAvviataDTO> orEdizioneAvviatas;
    private Integer numeroSedi;
    
    /**
     * id del corso by orienter
     */
    private Integer idCorso;
    
       
    
	public Integer getIdOrEnteCorso() {
		return idOrEnteCorso;
	}
	public void setIdOrEnteCorso(Integer idOrEnteCorso) {
		this.idOrEnteCorso = idOrEnteCorso;
	}
	public String getRagioneSocialeEnte() {
		return ragioneSocialeEnte;
	}
	public void setRagioneSocialeEnte(String ragioneSocialeEnte) {
		this.ragioneSocialeEnte = ragioneSocialeEnte;
	}
	public String getCodMansione() {
		return codMansione;
	}
	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}
	public String getStrMansione() {
		return strMansione;
	}
	public void setStrMansione(String strMansione) {
		this.strMansione = strMansione;
	}
	public String getTitoloCorso() {
		return titoloCorso;
	}
	public void setTitoloCorso(String titoloCorso) {
		this.titoloCorso = titoloCorso;
	}
	public String getCodTipoFormazione() {
		return codTipoFormazione;
	}
	public void setCodTipoFormazione(String codTipoFormazione) {
		this.codTipoFormazione = codTipoFormazione;
	}
	public String getDescrizioneCorso() {
		return descrizioneCorso;
	}
	public void setDescrizioneCorso(String descrizioneCorso) {
		this.descrizioneCorso = descrizioneCorso;
	}
	public String getContenutiPercorso() {
		return contenutiPercorso;
	}
	public void setContenutiPercorso(String contenutiPercorso) {
		this.contenutiPercorso = contenutiPercorso;
	}
	public String getRequisitiAccesso() {
		return requisitiAccesso;
	}
	public void setRequisitiAccesso(String requisitiAccesso) {
		this.requisitiAccesso = requisitiAccesso;
	}
	public String getIscrizione() {
		return iscrizione;
	}
	public void setIscrizione(String iscrizione) {
		this.iscrizione = iscrizione;
	}
	public String getCriteriSelezione() {
		return criteriSelezione;
	}
	public void setCriteriSelezione(String criteriSelezione) {
		this.criteriSelezione = criteriSelezione;
	}
	public Date getDtAvvio() {
		return dtAvvio;
	}
	public void setDtAvvio(Date dtAvvio) {
		this.dtAvvio = dtAvvio;
	}
	public String getAttestatoRilascio() {
		return attestatoRilascio;
	}
	public void setAttestatoRilascio(String attestatoRilascio) {
		this.attestatoRilascio = attestatoRilascio;
	}
	public String getDurata() {
		return durata;
	}
	public void setDurata(String durata) {
		this.durata = durata;
	}
	public String getPeriodoSvolgimento() {
		return periodoSvolgimento;
	}
	public void setPeriodoSvolgimento(String periodoSvolgimento) {
		this.periodoSvolgimento = periodoSvolgimento;
	}
	public Integer getNumeroPartecipanti() {
		return numeroPartecipanti;
	}
	public void setNumeroPartecipanti(Integer numeroPartecipanti) {
		this.numeroPartecipanti = numeroPartecipanti;
	}
	public BigDecimal getQuotaIscrizioneIndividuale() {
		return quotaIscrizioneIndividuale;
	}
	public void setQuotaIscrizioneIndividuale(BigDecimal quotaIscrizioneIndividuale) {
		this.quotaIscrizioneIndividuale = quotaIscrizioneIndividuale;
	}
	public String getAmministrazioneCompetente() {
		return amministrazioneCompetente;
	}
	public void setAmministrazioneCompetente(String amministrazioneCompetente) {
		this.amministrazioneCompetente = amministrazioneCompetente;
	}
	public String getCodiceIdentificativo() {
		return codiceIdentificativo;
	}
	public void setCodiceIdentificativo(String codiceIdentificativo) {
		this.codiceIdentificativo = codiceIdentificativo;
	}
	public String getScuolaCapofila() {
		return scuolaCapofila;
	}
	public void setScuolaCapofila(String scuolaCapofila) {
		this.scuolaCapofila = scuolaCapofila;
	}
	public String getUniversita() {
		return universita;
	}
	public void setUniversita(String universita) {
		this.universita = universita;
	}
	public String getImprese() {
		return imprese;
	}
	public void setImprese(String imprese) {
		this.imprese = imprese;
	}
	public Date getDtmIns() {
		return dtmIns;
	}
	public void setDtmIns(Date dtmIns) {
		this.dtmIns = dtmIns;
	}
	public Date getDtmMod() {
		return dtmMod;
	}
	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}
	public Integer getIdPrincipalIns() {
		return idPrincipalIns;
	}
	public void setIdPrincipalIns(Integer idPrincipalIns) {
		this.idPrincipalIns = idPrincipalIns;
	}
	public Integer getIdPrincipalMod() {
		return idPrincipalMod;
	}
	public void setIdPrincipalMod(Integer idPrincipalMod) {
		this.idPrincipalMod = idPrincipalMod;
	}
	public List<OrSedeCorsoDTO> getOrSedeCorsos() {
		return orSedeCorsos;
	}
	public void setOrSedeCorsos(List<OrSedeCorsoDTO> orSedeCorsos) {
		this.orSedeCorsos = orSedeCorsos;
	}
	public String getStrTipoFormazione() {
		return strTipoFormazione;
	}
	public void setStrTipoFormazione(String strTipoFormazione) {
		this.strTipoFormazione = strTipoFormazione;
	}
	public Integer getNumeroSedi() {
		return numeroSedi;
	}
	public void setNumeroSedi(Integer numeroSedi) {
		this.numeroSedi = numeroSedi;
	}
	public List<OrEdizioneAvviataDTO> getOrEdizioneAvviatas() {
		return orEdizioneAvviatas;
	}
	public void setOrEdizioneAvviatas(List<OrEdizioneAvviataDTO> orEdizioneAvviatas) {
		this.orEdizioneAvviatas = orEdizioneAvviatas;
	}
	public String getCodQualificaSrqRilasciata() {
		return codQualificaSrqRilasciata;
	}
	public void setCodQualificaSrqRilasciata(String codQualificaSrqRilasciata) {
		this.codQualificaSrqRilasciata = codQualificaSrqRilasciata;
	}
	public String getStrQualificaSrqRilasciata() {
		return strQualificaSrqRilasciata;
	}
	public void setStrQualificaSrqRilasciata(String strQualificaSrqRilasciata) {
		this.strQualificaSrqRilasciata = strQualificaSrqRilasciata;
	}
	public String getCodProfessione() {
		return codProfessione;
	}
	public void setCodProfessione(String codProfessione) {
		this.codProfessione = codProfessione;
	}
	public String getStrProfessione() {
		return strProfessione;
	}
	public void setStrProfessione(String strProfessione) {
		this.strProfessione = strProfessione;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrCorsoDTO [idOrEnteCorso=");
		builder.append(idOrEnteCorso);
		builder.append(", ragioneSocialeEnte=");
		builder.append(ragioneSocialeEnte);
		builder.append(", codMansione=");
		builder.append(codMansione);
		builder.append(", strMansione=");
		builder.append(strMansione);
		builder.append(", codQualificaSrqRilasciata=");
		builder.append(codQualificaSrqRilasciata);
		builder.append(", strQualificaSrqRilasciata=");
		builder.append(strQualificaSrqRilasciata);
		builder.append(", codProfessione=");
		builder.append(codProfessione);
		builder.append(", strProfessione=");
		builder.append(strProfessione);
		builder.append(", titoloCorso=");
		builder.append(titoloCorso);
		builder.append(", codTipoFormazione=");
		builder.append(codTipoFormazione);
		builder.append(", strTipoFormazione=");
		builder.append(strTipoFormazione);
		builder.append(", descrizioneCorso=");
		builder.append(descrizioneCorso);
		builder.append(", contenutiPercorso=");
		builder.append(contenutiPercorso);
		builder.append(", requisitiAccesso=");
		builder.append(requisitiAccesso);
		builder.append(", iscrizione=");
		builder.append(iscrizione);
		builder.append(", criteriSelezione=");
		builder.append(criteriSelezione);
		builder.append(", dtAvvio=");
		builder.append(dtAvvio);
		builder.append(", attestatoRilascio=");
		builder.append(attestatoRilascio);
		builder.append(", durata=");
		builder.append(durata);
		builder.append(", periodoSvolgimento=");
		builder.append(periodoSvolgimento);
		builder.append(", numeroPartecipanti=");
		builder.append(numeroPartecipanti);
		builder.append(", quotaIscrizioneIndividuale=");
		builder.append(quotaIscrizioneIndividuale);
		builder.append(", amministrazioneCompetente=");
		builder.append(amministrazioneCompetente);
		builder.append(", codiceIdentificativo=");
		builder.append(codiceIdentificativo);
		builder.append(", scuolaCapofila=");
		builder.append(scuolaCapofila);
		builder.append(", universita=");
		builder.append(universita);
		builder.append(", imprese=");
		builder.append(imprese);
		builder.append(", dtmIns=");
		builder.append(dtmIns);
		builder.append(", dtmMod=");
		builder.append(dtmMod);
		builder.append(", idPrincipalIns=");
		builder.append(idPrincipalIns);
		builder.append(", idPrincipalMod=");
		builder.append(idPrincipalMod);
		builder.append(", orSedeCorsos=");
		builder.append(orSedeCorsos);
		builder.append(", orEdizioneAvviatas=");
		builder.append(orEdizioneAvviatas);
		builder.append(", numeroSedi=");
		builder.append(numeroSedi);
		builder.append(", idCorso=");
		builder.append(idCorso);
		builder.append("]");
		return builder.toString();
	}
	public Integer getIdCorso() {
		return idCorso;
	}
	public void setIdCorso(Integer idCorso) {
		this.idCorso = idCorso;
	}
	
	
	
	
	
}
