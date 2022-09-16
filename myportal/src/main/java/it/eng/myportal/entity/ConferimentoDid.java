package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.min.DeCittadinanzaMin;
import it.eng.myportal.entity.decodifiche.min.DeCondizioneOccupazMin;
import it.eng.myportal.entity.decodifiche.min.DeIscrizioneCorsoMin;
import it.eng.myportal.entity.decodifiche.min.DePosizioneProfessionaleMin;
import it.eng.myportal.entity.decodifiche.min.DePresenzaItaliaMin;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "conferimento_did", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findConferimentoDidByIdPrincipal", query = "SELECT cd FROM ConferimentoDid cd WHERE cd.pfPrincipal.idPfPrincipal = :idPfPrincipal") })
public class ConferimentoDid extends AbstractEntity {
	private static final long serialVersionUID = -1314195527831660563L;

	private Integer idConferimentoDid;
	private PfPrincipal pfPrincipal;
	private Date dataDid;
	private String opzTipoEvento;
	private String codEntePromotore;
	private Date dataEvento;
	private String codiceFiscale;
	private Integer eta;
	private String opzGenere;
	private DeCittadinanzaMin deCittadinanzaMin;
	private DePresenzaItaliaMin dePresenzaItaliaMin;
	private DeProvincia deProvinciaResidenza;
	private Boolean flgEsperienzaLavoro;
	private DeCondizioneOccupazMin deCondizioneOccupazMin;
	private Integer numMesiRapporto;
	private DePosizioneProfessionaleMin dePosizioneProfessionaleMin;
	private Integer numMesiRicercaLavoro;
	private DeIscrizioneCorsoMin deIscrizioneCorsoMin;
	private Integer numComponentiFamiglia;
	private Boolean flgFigliCarico;
	private Boolean flgFigliMinoriCarico;
	private String condizioneOccupazCalc;
	private String durataDisoccupazCalc;
	private DeTitolo deTitolo;
	private Long idProfiling;
	private Double probabilita;
	private Date dataInserimento;

	@Id
	@SequenceGenerator(name = "conferimento_did_id_conferimento_did_seq", sequenceName = "conferimento_did_id_conferimento_did_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conferimento_did_id_conferimento_did_seq")
	@Column(name = "id_conferimento_did", unique = true, nullable = false)
	public Integer getIdConferimentoDid() {
		return idConferimentoDid;
	}

	public void setIdConferimentoDid(Integer idConferimentoDid) {
		this.idConferimentoDid = idConferimentoDid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false, columnDefinition = "integer not null")
	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_did")
	public Date getDataDid() {
		return dataDid;
	}

	public void setDataDid(Date dataDid) {
		this.dataDid = dataDid;
	}

	@Column(name = "opz_tipo_evento", length = 1, nullable = false)
	public String getOpzTipoEvento() {
		return opzTipoEvento;
	}

	public void setOpzTipoEvento(String opzTipoEvento) {
		this.opzTipoEvento = opzTipoEvento;
	}

	@Column(name = "cod_ente_promotore", length = 2000, nullable = false)
	public String getCodEntePromotore() {
		return codEntePromotore;
	}

	public void setCodEntePromotore(String codEntePromotore) {
		this.codEntePromotore = codEntePromotore;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_evento", nullable = false)
	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	@Column(name = "codice_fiscale", length = 16, nullable = false)
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Column(name = "eta")
	public Integer getEta() {
		return eta;
	}

	public void setEta(Integer eta) {
		this.eta = eta;
	}

	@Column(name = "opz_genere", length = 1)
	public String getOpzGenere() {
		return opzGenere;
	}

	public void setOpzGenere(String opzGenere) {
		this.opzGenere = opzGenere;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_cittadinanza_min")
	public DeCittadinanzaMin getDeCittadinanzaMin() {
		return deCittadinanzaMin;
	}

	public void setDeCittadinanzaMin(DeCittadinanzaMin deCittadinanzaMin) {
		this.deCittadinanzaMin = deCittadinanzaMin;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_presenza_italia_min")
	public DePresenzaItaliaMin getDePresenzaItaliaMin() {
		return dePresenzaItaliaMin;
	}

	public void setDePresenzaItaliaMin(DePresenzaItaliaMin dePresenzaItaliaMin) {
		this.dePresenzaItaliaMin = dePresenzaItaliaMin;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_provincia_res")
	public DeProvincia getDeProvinciaResidenza() {
		return deProvinciaResidenza;
	}

	public void setDeProvinciaResidenza(DeProvincia deProvinciaResidenza) {
		this.deProvinciaResidenza = deProvinciaResidenza;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_esperienza_lavoro", length = 1)
	public Boolean getFlgEsperienzaLavoro() {
		return flgEsperienzaLavoro;
	}

	public void setFlgEsperienzaLavoro(Boolean flgEsperienzaLavoro) {
		this.flgEsperienzaLavoro = flgEsperienzaLavoro;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_condizione_occupaz_min")
	public DeCondizioneOccupazMin getDeCondizioneOccupazMin() {
		return deCondizioneOccupazMin;
	}

	public void setDeCondizioneOccupazMin(DeCondizioneOccupazMin deCondizioneOccupazMin) {
		this.deCondizioneOccupazMin = deCondizioneOccupazMin;
	}

	@Column(name = "num_mesi_rapporto")
	public Integer getNumMesiRapporto() {
		return numMesiRapporto;
	}

	public void setNumMesiRapporto(Integer numMesiRapporto) {
		this.numMesiRapporto = numMesiRapporto;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_posizione_professionale_min")
	public DePosizioneProfessionaleMin getDePosizioneProfessionaleMin() {
		return dePosizioneProfessionaleMin;
	}

	public void setDePosizioneProfessionaleMin(DePosizioneProfessionaleMin dePosizioneProfessionaleMin) {
		this.dePosizioneProfessionaleMin = dePosizioneProfessionaleMin;
	}

	@Column(name = "num_mesi_ricerca_lavoro")
	public Integer getNumMesiRicercaLavoro() {
		return numMesiRicercaLavoro;
	}

	public void setNumMesiRicercaLavoro(Integer numMesiRicercaLavoro) {
		this.numMesiRicercaLavoro = numMesiRicercaLavoro;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_iscrizione_corso_min")
	public DeIscrizioneCorsoMin getDeIscrizioneCorsoMin() {
		return deIscrizioneCorsoMin;
	}

	public void setDeIscrizioneCorsoMin(DeIscrizioneCorsoMin deIscrizioneCorsoMin) {
		this.deIscrizioneCorsoMin = deIscrizioneCorsoMin;
	}

	@Column(name = "num_componenti_famiglia")
	public Integer getNumComponentiFamiglia() {
		return numComponentiFamiglia;
	}

	public void setNumComponentiFamiglia(Integer numComponentiFamiglia) {
		this.numComponentiFamiglia = numComponentiFamiglia;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_figli_carico", length = 1)
	public Boolean getFlgFigliCarico() {
		return flgFigliCarico;
	}

	public void setFlgFigliCarico(Boolean flgFigliCarico) {
		this.flgFigliCarico = flgFigliCarico;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_figli_minori_carico", length = 1)
	public Boolean getFlgFigliMinoriCarico() {
		return flgFigliMinoriCarico;
	}

	public void setFlgFigliMinoriCarico(Boolean flgFigliMinoriCarico) {
		this.flgFigliMinoriCarico = flgFigliMinoriCarico;
	}

	@Column(name = "condizione_occupaz_calc", length = 8)
	public String getCondizioneOccupazCalc() {
		return condizioneOccupazCalc;
	}

	public void setCondizioneOccupazCalc(String condizioneOccupazCalc) {
		this.condizioneOccupazCalc = condizioneOccupazCalc;
	}

	@Column(name = "durata_disoccupaz_calc", length = 8)
	public String getDurataDisoccupazCalc() {
		return durataDisoccupazCalc;
	}

	public void setDurataDisoccupazCalc(String durataDisoccupazCalc) {
		this.durataDisoccupazCalc = durataDisoccupazCalc;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_titolo")
	public DeTitolo getDeTitolo() {
		return deTitolo;
	}

	public void setDeTitolo(DeTitolo deTitolo) {
		this.deTitolo = deTitolo;
	}

	@Column(name = "id_profiling")
	public Long getIdProfiling() {
		return idProfiling;
	}

	public void setIdProfiling(Long idProfiling) {
		this.idProfiling = idProfiling;
	}

	@Column(name = "probabilita")
	public Double getProbabilita() {
		return probabilita;
	}

	public void setProbabilita(Double probabilita) {
		this.probabilita = probabilita;
	}

	@Column(name = "data_inserimento")
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
		result = prime * result + ((idConferimentoDid == null) ? 0 : idConferimentoDid.hashCode());
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
		ConferimentoDid other = (ConferimentoDid) obj;
		if (idConferimentoDid == null) {
			if (other.idConferimentoDid != null)
				return false;
		} else if (!idConferimentoDid.equals(other.idConferimentoDid))
			return false;
		return true;
	}
}
