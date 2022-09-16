package it.eng.myportal.entity;

import java.io.Serializable;
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

import it.eng.myportal.entity.decodifiche.DeStatoFbConvenzione;
import it.eng.myportal.entity.decodifiche.DeTipoFbConvenzione;

@Entity
@Table(name = "fb_convenzione", schema = "myportal")
@NamedQueries(value = {
		@NamedQuery(name = "findConvenzioneByPfPrincipalId", query = "select conv from FbConvenzione conv WHERE conv.pfPrincipalIns.idPfPrincipal = :pfPrincipalId ORDER BY conv.dataStipula desc"),
		@NamedQuery(name = "countConvenzioniAttiveByIdPrincipalAndStato", query = "SELECT COUNT(conv) FROM FbConvenzione conv WHERE conv.pfPrincipalIns.idPfPrincipal = :idPfPrincipal AND conv.codStatoConv.codStatoConv = :codStatoProt AND conv.dataScadenza >= :currentDate"),
		@NamedQuery(name = "countConvenzioniAttiveByIdPrincipalAndTipo", query = "SELECT COUNT(conv) FROM FbConvenzione conv WHERE conv.pfPrincipalIns.idPfPrincipal = :idPfPrincipal AND (NOT conv.codStatoConv.codStatoConv = :codStatoRevocata) AND conv.codTipoConvenzione.codTipoConvenzione = :codTipoConvenzione AND (conv.dataScadenza >= :currentDate OR conv.dataScadenza IS NULL)") })
public class FbConvenzione extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = -8853430589359241719L;

	private Integer idConvenzione;
	private Date dataStipula;
	private String nomeConvenzione;
	private DeTipoFbConvenzione codTipoConvenzione;
	private DeStatoFbConvenzione codStatoConv;
	private Date dataScadenza;
	private String nomeLegaleRappresentante;
	private String cognomeLegaleRappresentante;

	private String numProtocollo;
	private Date dataProtocollo;
	private Integer idPrincipalProtocollo;

	private Date dataRevoca;
	private String motivoRevoca;
	private Integer idPrincipalRevoca;
	private Date dataProtocollazione;

	public FbConvenzione() {
	}

	@Id
	@SequenceGenerator(name = "fb_convenzione_id_convenzione_seq", sequenceName = "fb_convenzione_id_convenzione_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_convenzione_id_convenzione_seq")
	@Column(name = "id_convenzione", unique = true, nullable = false)
	public Integer getIdConvenzione() {
		return idConvenzione;
	}

	@Column(name = "data_stipula", nullable = false)
	public Date getDataStipula() {
		return dataStipula;
	}

	@Column(name = "nome_convenzione", nullable = true, length = 255)
	public String getNomeConvenzione() {
		return nomeConvenzione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_fb_tipo_convenzione")
	public DeTipoFbConvenzione getCodTipoConvenzione() {
		return codTipoConvenzione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_fb_stato_convenzione")
	public DeStatoFbConvenzione getCodStatoConv() {
		return codStatoConv;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_scadenza", length = 13, nullable = true)
	public Date getDataScadenza() {
		return dataScadenza;
	}

	@Column(name = "num_protocollo", length = 100, nullable = true)
	public String getNumProtocollo() {
		return numProtocollo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_repertorio", nullable = true)
	public Date getDataProtocollo() {
		return dataProtocollo;
	}

	@Column(name = "id_principal_protocollo")
	public Integer getIdPrincipalProtocollo() {
		return idPrincipalProtocollo;
	}

	@Column(name = "id_principal_revoca")
	public Integer getIdPrincipalRevoca() {
		return idPrincipalRevoca;
	}

	@Column(name = "motivo_revoca", length = 100)
	public String getMotivoRevoca() {
		return motivoRevoca;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_revoca")
	public Date getDataRevoca() {
		return dataRevoca;
	}

	@Column(name = "nome_legale_rappresentante", length = 50, nullable = false)
	public String getNomeLegaleRappresentante() {
		return nomeLegaleRappresentante;
	}

	@Column(name = "cognome_legale_rappresentante", length = 50, nullable = false)
	public String getCognomeLegaleRappresentante() {
		return cognomeLegaleRappresentante;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_protocollazione")
	public Date getDataProtocollazione() {
		return dataProtocollazione;
	}

	public void setIdConvenzione(Integer idConvenzione) {
		this.idConvenzione = idConvenzione;
	}

	public void setDataStipula(Date dataStipula) {
		this.dataStipula = dataStipula;
	}

	public void setNomeConvenzione(String nomeConvenzione) {
		this.nomeConvenzione = nomeConvenzione;
	}

	public void setCodTipoConvenzione(DeTipoFbConvenzione codTipoConvenzione) {
		this.codTipoConvenzione = codTipoConvenzione;
	}

	public void setCodStatoConv(DeStatoFbConvenzione codStatoConv) {
		this.codStatoConv = codStatoConv;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public void setNumProtocollo(String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	public void setDataProtocollo(Date dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public void setIdPrincipalProtocollo(Integer idPrincipalProtocollo) {
		this.idPrincipalProtocollo = idPrincipalProtocollo;
	}

	public void setDataRevoca(Date dataRevoca) {
		this.dataRevoca = dataRevoca;
	}

	public void setMotivoRevoca(String motivoRevoca) {
		this.motivoRevoca = motivoRevoca;
	}

	public void setIdPrincipalRevoca(Integer idPrincipalRevoca) {
		this.idPrincipalRevoca = idPrincipalRevoca;
	}

	public void setNomeLegaleRappresentante(String nomeLegaleRappresentante) {
		this.nomeLegaleRappresentante = nomeLegaleRappresentante;
	}

	public void setCognomeLegaleRappresentante(String cognomeLegaleRappresentante) {
		this.cognomeLegaleRappresentante = cognomeLegaleRappresentante;
	}

	public void setDataProtocollazione(Date dataProtocoliazione) {
		this.dataProtocollazione = dataProtocoliazione;
	}

}
