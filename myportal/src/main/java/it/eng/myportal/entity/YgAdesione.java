package it.eng.myportal.entity;

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

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeStatoAdesione;
import it.eng.myportal.entity.decodifiche.min.DeStatoAdesioneMin;

@Entity
@Table(name = "yg_adesione", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findYgAdesioneAttivaByIfPfPrincipal", query = "select y from YgAdesione y where y.pfPrincipal.idPfPrincipal = :id_pf_principal and y.flgAdesione = 'Y' order by dtAdesione desc, idYgAdesione desc"),
		@NamedQuery(name = "findYgAdesioneByIfPfPrincipal", query = "select y from YgAdesione y where y.pfPrincipal.idPfPrincipal = :id_pf_principal order by dtAdesione desc, idYgAdesione desc"),
		@NamedQuery(name = "findYgAdesioneByCodiceFiscaleAndCodMonoProv", query = "select y from YgAdesione y, UtenteInfo ut where ut.pfPrincipal.idPfPrincipal = y.pfPrincipal.idPfPrincipal and ut.codiceFiscale = :codice_fiscale and y.codMonoProv = :cod_mono_prov"),
		@NamedQuery(name = "findYgAdesioneInviataByCFAndCodMonoProv", query = "select y from YgAdesione y, UtenteInfo ut, DeStatoAdesioneMin sta where sta.codMonoAttiva = y.deStatoAdesioneMin.codMonoAttiva and ut.pfPrincipal.idPfPrincipal = y.pfPrincipal.idPfPrincipal and ut.codiceFiscale = :codice_fiscale and y.codMonoProv = :cod_mono_prov and y.flgAdesione = 'Y' and sta.codMonoAttiva = 'R' "),
		@NamedQuery(name = "findYgAdesioneByCfDtAdesCodReg", query = "select y from YgAdesione y where y.flgAdesione = 'Y' and upper(y.codiceFiscale) = :codiceFiscale and y.dtAdesione = :dtAdesione and y.deRegione.codRegione = :codRegione order by dtAdesione desc, codMonoProv, idYgAdesione desc"),
		@NamedQuery(name = "findByCodStatoAdesioneMinWithConstraintInDtAdesione", query = "select y from YgAdesione y where y.deStatoAdesioneMin.codStatoAdesioneMin = :codStatoAdesioneMin and y.pfPrincipal.idPfPrincipal = :idPfPrincipal and y.dtAdesione >= :dtAdesione order by dtAdesione desc"),
		@NamedQuery(name = "findLatestByCodiceFiscaleInRegionePortale", query = "select y from YgAdesione y where y.flgAdesione = 'Y' and upper(y.codiceFiscale) = :codiceFiscale and y.deRegione.codRegione = :codRegione order by dtAdesione desc, codMonoProv, idYgAdesione desc") })
public class YgAdesione extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 9124254743797104540L;

	private Integer idYgAdesione;
	private String codiceFiscale;
	private Date dtAdesione;
	private String identificativoSap;
	private String identificativoSapOld;
	private PfPrincipal pfPrincipal;
	private String codMonoProv;
	private Boolean flgAdesione;
	private String strMessWsAdesione;
	private Boolean flgSap;
	private String strMessWsInvioSap;
	private String strMessWsNotifica;

	private Boolean flgPresoInCarico;
	private PfPrincipal pfPrincipalPic;
	private Date dtPresaInCarico;

	private DeProvincia deProvinciaNotifica;
	private DeRegione deRegioneRifNotifica;

	private DeCpi deCpiAdesione;

	/**
	 * può assumere i seguenti valori: Y: provincia trovata e record processato N: record non processato F: sap non
	 * trovata O: provincia fuori regione
	 */
	private String flgRecuperoProv;

	private DeCpi deCpiAssegnazione;
	private String codMonoRecuperoCpi;
	private String strMessAccount;

	private String emailRifNotifica;
	private String nomeRifNotifica;
	private String cognomeRifNotifica;

	private Boolean flgCreatoAccount;

	private DeStatoAdesione deStatoAdesione;
	private Date dtFineStatoAdesione;
	private String note;

	private DeComune deComuneResidenzaRifNotifica;
	private DeComune deComuneDomicilioRifNotifica;

	private DeStatoAdesioneMin deStatoAdesioneMin;
	private Date dtStatoAdesioneMin;

	public YgAdesione() {
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Id
	@SequenceGenerator(name = "yg_adesione_id_yg_adesione_seq", sequenceName = "yg_adesione_id_yg_adesione_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_adesione_id_yg_adesione_seq")
	@Column(name = "id_yg_adesione", unique = true, nullable = false)
	public Integer getIdYgAdesione() {
		return idYgAdesione;
	}

	public void setIdYgAdesione(Integer idYgAdesione) {
		this.idYgAdesione = idYgAdesione;
	}

	@Column(name = "codice_fiscale", length = 16)
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_adesione", nullable = true, length = 13)
	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	@Column(name = "identificativo_sap_old", length = 11)
	public String getIdentificativoSapOld() {
		return identificativoSapOld;
	}

	public void setIdentificativoSapOld(String identificativoSapOld) {
		this.identificativoSapOld = identificativoSapOld;
	}

	@Column(name = "identificativo_sap", length = 11)
	public String getIdentificativoSap() {
		return identificativoSap;
	}

	public void setIdentificativoSap(String identificativoSap) {
		this.identificativoSap = identificativoSap;
	}

	@Column(name = "cod_mono_prov", length = 1)
	public String getCodMonoProv() {
		return codMonoProv;
	}

	public void setCodMonoProv(String codMonoProv) {
		this.codMonoProv = codMonoProv;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_adesione", length = 1)
	public Boolean getFlgAdesione() {
		return flgAdesione;
	}

	public void setFlgAdesione(Boolean flgAdesione) {
		this.flgAdesione = flgAdesione;
	}

	@Column(name = "str_mess_ws_adesione", length = 40000)
	public String getStrMessWsAdesione() {
		return strMessWsAdesione;
	}

	public void setStrMessWsAdesione(String strMessWsAdesione) {
		this.strMessWsAdesione = strMessWsAdesione;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_sap", length = 1)
	public Boolean getFlgSap() {
		return flgSap;
	}

	public void setFlgSap(Boolean flgSap) {
		this.flgSap = flgSap;
	}

	@Column(name = "str_mess_ws_invio_sap", length = 40000)
	public String getStrMessWsInvioSap() {
		return strMessWsInvioSap;
	}

	public void setStrMessWsInvioSap(String strMessWsInvioSap) {
		this.strMessWsInvioSap = strMessWsInvioSap;
	}

	@Column(name = "str_mess_ws_notifica", length = 40000)
	public String getStrMessWsNotifica() {
		return strMessWsNotifica;
	}

	public void setStrMessWsNotifica(String strMessWsNotifica) {
		this.strMessWsNotifica = strMessWsNotifica;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_preso_in_carico", length = 1)
	public Boolean getFlgPresoInCarico() {
		return flgPresoInCarico;
	}

	public void setFlgPresoInCarico(Boolean flgPresoInCarico) {
		this.flgPresoInCarico = flgPresoInCarico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_principal_pic", nullable = false)
	public PfPrincipal getPfPrincipalPic() {
		return pfPrincipalPic;
	}

	public void setPfPrincipalPic(PfPrincipal pfPrincipalPic) {
		this.pfPrincipalPic = pfPrincipalPic;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_presa_in_carico", nullable = true, length = 13)
	public Date getDtPresaInCarico() {
		return dtPresaInCarico;
	}

	public void setDtPresaInCarico(Date dtPresaInCarico) {
		this.dtPresaInCarico = dtPresaInCarico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia_rif_notifica")
	public DeProvincia getDeProvincia() {
		return this.deProvinciaNotifica;
	}

	public void setDeProvincia(DeProvincia deProvinciaNotifica) {
		this.deProvinciaNotifica = deProvinciaNotifica;
	}

	// codifica regionale interna, NON ministeriale
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_regione_rif_notifica")
	public DeRegione getDeRegione() {
		return this.deRegioneRifNotifica;
	}

	public void setDeRegione(DeRegione deRegioneRifNotifica) {
		this.deRegioneRifNotifica = deRegioneRifNotifica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_cpi_adesione")
	public DeCpi getDeCpiAdesione() {
		return deCpiAdesione;
	}

	public void setDeCpiAdesione(DeCpi deCpiAdesione) {
		this.deCpiAdesione = deCpiAdesione;
	}

	@Column(name = "flg_recupero_prov", length = 1)
	public String getFlgRecuperoProv() {
		return flgRecuperoProv;
	}

	public void setFlgRecuperoProv(String flgRecuperoProv) {
		this.flgRecuperoProv = flgRecuperoProv;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_cpi_assegnazione")
	public DeCpi getDeCpiAssegnazione() {
		return deCpiAssegnazione;
	}

	public void setDeCpiAssegnazione(DeCpi deCpiAssegnazione) {
		this.deCpiAssegnazione = deCpiAssegnazione;
	}

	@Column(name = "cod_mono_recupero_cpi", length = 1)
	public String getCodMonoRecuperoCpi() {
		return codMonoRecuperoCpi;
	}

	public void setCodMonoRecuperoCpi(String codMonoRecuperoCpi) {
		this.codMonoRecuperoCpi = codMonoRecuperoCpi;
	}

	@Column(name = "str_mess_account", length = 2000)
	public String getStrMessAccount() {
		return strMessAccount;
	}

	public void setStrMessAccount(String strMessAccount) {
		this.strMessAccount = strMessAccount;
	}

	@Column(name = "email_rif_notifica", length = 100)
	public String getEmailRifNotifica() {
		return emailRifNotifica;
	}

	public void setEmailRifNotifica(String emailRifNotifica) {
		this.emailRifNotifica = emailRifNotifica;
	}

	@Column(name = "nome_rif_notifica", length = 50)
	public String getNomeRifNotifica() {
		return nomeRifNotifica;
	}

	public void setNomeRifNotifica(String nomeRifNotifica) {
		this.nomeRifNotifica = nomeRifNotifica;
	}

	@Column(name = "cognome_rif_notifica", length = 50)
	public String getCognomeRifNotifica() {
		return cognomeRifNotifica;
	}

	public void setCognomeRifNotifica(String cognomeRifNotifica) {
		this.cognomeRifNotifica = cognomeRifNotifica;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_creato_account", length = 1)
	public Boolean getFlgCreatoAccount() {
		return flgCreatoAccount;
	}

	public void setFlgCreatoAccount(Boolean flgCreatoAccount) {
		this.flgCreatoAccount = flgCreatoAccount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_stato_adesione")
	public DeStatoAdesione getDeStatoAdesione() {
		return deStatoAdesione;
	}

	public void setDeStatoAdesione(DeStatoAdesione deStatoAdesione) {
		this.deStatoAdesione = deStatoAdesione;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_fine_stato_adesione", nullable = true, length = 13)
	public Date getDtFineStatoAdesione() {
		return dtFineStatoAdesione;
	}

	public void setDtFineStatoAdesione(Date dtFineStatoAdesione) {
		this.dtFineStatoAdesione = dtFineStatoAdesione;
	}

	@Column(name = "note", length = 200)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_com_res_rif_notifica")
	public DeComune getDeComuneResidenzaRifNotifica() {
		return deComuneResidenzaRifNotifica;
	}

	public void setDeComuneResidenzaRifNotifica(DeComune deComuneResidenzaRifNotifica) {
		this.deComuneResidenzaRifNotifica = deComuneResidenzaRifNotifica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_com_dom_rif_notifica")
	public DeComune getDeComuneDomicilioRifNotifica() {
		return deComuneDomicilioRifNotifica;
	}

	public void setDeComuneDomicilioRifNotifica(DeComune deComuneDomicilioRifNotifica) {
		this.deComuneDomicilioRifNotifica = deComuneDomicilioRifNotifica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_stato_adesione_min")
	public DeStatoAdesioneMin getDeStatoAdesioneMin() {
		return deStatoAdesioneMin;
	}

	public void setDeStatoAdesioneMin(DeStatoAdesioneMin deStatoAdesioneMin) {
		this.deStatoAdesioneMin = deStatoAdesioneMin;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_stato_adesione_min", nullable = true, length = 13)
	public Date getDtStatoAdesioneMin() {
		return dtStatoAdesioneMin;
	}

	public void setDtStatoAdesioneMin(Date dtStatoAdesioneMin) {
		this.dtStatoAdesioneMin = dtStatoAdesioneMin;
	}

}
