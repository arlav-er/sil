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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.myportal.entity.decodifiche.DeProvincia;

@Entity
@Table(name = "sintesi_proto", schema = "myportal")
public class SintesiProto extends AbstractEntity implements java.io.Serializable {

	private static final long serialVersionUID = -4305985402415027220L;
	private Integer idSintesiProto;
	private String xmlChiamata;
	private String tipologiaRichiesta;
	private String codfisRichiedente;
	private String jsonPercLavResponse;
	private String nomeRichiedente;
	private String cognomeRichiedente;
	private String numProtocollo;
	private Date dataProtocollo;
	private DeProvincia deProvincia;
	private PfPrincipal pfPrincipal;

	private String sintesiOperatoreCF;
	private Date sintesiDataDecorrenza;
	private String sintesiCodStatoOcc;
	private String sintesiStatoOccDesc;
	private String sintesiCodiceCPIrif;

	@Id
	@SequenceGenerator(name = "sintesi_proto_id_sintesi_proto_seq", sequenceName = "sintesi_proto_id_sintesi_proto_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sintesi_proto_id_sintesi_proto_seq")
	@Column(name = "id_sintesi_proto", unique = true, nullable = false)
	public Integer getIdSintesiProto() {
		return idSintesiProto;
	}

	public void setIdSintesiProto(Integer idSintesiProto) {
		this.idSintesiProto = idSintesiProto;
	}

	@Column(name = "xml_chiamata", columnDefinition = "TEXT")
	public String getXmlChiamata() {
		return xmlChiamata;
	}

	public void setXmlChiamata(String xmlChiamata) {
		this.xmlChiamata = xmlChiamata;
	}

	@Column(name = "tipologia_richiesta", length = 3, nullable = false)
	public String getTipologiaRichiesta() {
		return tipologiaRichiesta;
	}

	public void setTipologiaRichiesta(String tipologiaRichiesta) {
		this.tipologiaRichiesta = tipologiaRichiesta;
	}

	@Column(name = "cod_fis_richiedente", length = 16, nullable = false)
	public String getCodFisRichiedente() {
		return codfisRichiedente;
	}

	public void setCodFisRichiedente(String cod_fisRichiedente) {
		this.codfisRichiedente = cod_fisRichiedente;
	}

	@Column(name = "nome_richiedente", length = 16)
	public String getNomeRichiedente() {
		return nomeRichiedente;
	}

	public void setNomeRichiedente(String nomeRichiedente) {
		this.nomeRichiedente = nomeRichiedente;
	}

	@Column(name = "cognome_richiedente", length = 16, nullable = false)
	public String getCognomeRichiedente() {
		return cognomeRichiedente;
	}

	public void setCognomeRichiedente(String cognomeRichiedente) {
		this.cognomeRichiedente = cognomeRichiedente;
	}

	@Column(name = "num_protocollo", length = 50)
	public String getNumProtocollo() {
		return numProtocollo;
	}

	public void setNumProtocollo(String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_protocollo", nullable = false, length = 13)
	public Date getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(Date dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_provincia", nullable = false)
	public DeProvincia getDeProvincia() {
		return deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Column(name = "json_perc_lav_response", columnDefinition = "TEXT")
	public String getJsonPercLavResponse() {
		return jsonPercLavResponse;
	}

	public void setJsonPercLavResponse(String jsonPercLavResponse) {
		this.jsonPercLavResponse = jsonPercLavResponse;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "sintesi_data_decorrenza", length = 13)
	public Date getSintesiDataDecorrenza() {
		return sintesiDataDecorrenza;
	}

	public void setSintesiDataDecorrenza(Date sintesiDataDecorrenza) {
		this.sintesiDataDecorrenza = sintesiDataDecorrenza;
	}

	@Column(name = "sintesi_stato_occ_desc", length = 100)
	public String getSintesiStatoOccDesc() {
		return sintesiStatoOccDesc;
	}

	public void setSintesiStatoOccDesc(String sintesiStatoOccDesc) {
		this.sintesiStatoOccDesc = sintesiStatoOccDesc;
	}

	@Column(name = "sintesi_cod_cpi_rif", length = 100)
	public String getSintesiCodiceCPIrif() {
		return sintesiCodiceCPIrif;
	}

	public void setSintesiCodiceCPIrif(String sintesiCodiceCPIrif) {
		this.sintesiCodiceCPIrif = sintesiCodiceCPIrif;
	}

	@Column(name = "sintesi_operatore_cf", length = 16)
	public String getSintesiOperatoreCF() {
		return sintesiOperatoreCF;
	}

	public void setSintesiOperatoreCF(String sintesiOperatoreCF) {
		this.sintesiOperatoreCF = sintesiOperatoreCF;
	}

	@Column(name = "sintesi_cod_stato_occ", length = 100)
	public String getSintesiCodStatoOcc() {
		return sintesiCodStatoOcc;
	}

	public void setSintesiCodStatoOcc(String sintesiCodStatoOcc) {
		this.sintesiCodStatoOcc = sintesiCodStatoOcc;
	}
}
