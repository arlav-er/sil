//package it.eng.sil.myaccount.model.utils;
//
//import it.eng.sil.myaccount.model.entity.profile.PfPrincipal;
//import it.eng.sil.mycas.model.entity.BaseTabellaSistemaEntity;
//import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
//import it.eng.sil.mycas.model.entity.decodifiche.DeTipoServizio;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
//
///**
// * Classe DEPRECATA, va utilizzata tswsclietnt e/o server TODO rimozione
// * 
// * @author pegoraro
// */
//@Deprecated
//@Entity
//@Table(name = "ws_endpoint", schema = "mycas")
//@NamedQueries({
//		@NamedQuery(name = "findEndpointByTipoServizio", query = "select s from TsWsEndpoint s where s.deTipoServizio.codTipoServizio = :tipoServizio"),
//		@NamedQuery(name = "findEndpointByTipoServizioProvincia", query = "select s from TsWsEndpoint s where s.deTipoServizio.codTipoServizio = :tipoServizio and s.deProvincia.codProvincia = :codProvincia"), })
//public class TsWsEndpoint extends BaseTabellaSistemaEntity implements java.io.Serializable {
//
//	private static final long serialVersionUID = 257731L;
//	private Integer idWsEndpoint;
//	private DeTipoServizio deTipoServizio;
//	private DeProvincia deProvincia;
//	private String address;
//	private String username;
//	private String passWord;
//
//	public TsWsEndpoint() {
//	}
//
//	public TsWsEndpoint(Integer idWsEndpoint, PfPrincipal pfPrincipalByIdPrincipalMod,
//			PfPrincipal pfPrincipalByIdPrincipalIns, DeTipoServizio deTipoServizio, DeProvincia deProvincia,
//			String address, String username, String passWord, Date dtmIns, Date dtmMod) {
//		this.idWsEndpoint = idWsEndpoint;
//		this.deTipoServizio = deTipoServizio;
//		this.deProvincia = deProvincia;
//		this.address = address;
//		this.username = username;
//		this.passWord = passWord;
//
//	}
//
//	@Id
//	@SequenceGenerator(name = "ws_endpoint_id_ws_endpoint_seq", sequenceName = "mycas.ws_endpoint_id_ws_endpoint_seq", allocationSize = 1)
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ws_endpoint_id_ws_endpoint_seq")
//	@Column(name = "id_ws_endpoint", unique = true, nullable = false)
//	public Integer getIdWsEndpoint() {
//		return this.idWsEndpoint;
//	}
//
//	public void setIdWsEndpoint(Integer idWsEndpoint) {
//		this.idWsEndpoint = idWsEndpoint;
//	}
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "cod_tipo_servizio", nullable = false)
//	public DeTipoServizio getDeTipoServizio() {
//		return this.deTipoServizio;
//	}
//
//	public void setDeTipoServizio(DeTipoServizio deTipoServizio) {
//		this.deTipoServizio = deTipoServizio;
//	}
//
//	@Column(name = "address", nullable = false)
//	public String getAddress() {
//		return this.address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "cod_provincia", nullable = false)
//	public DeProvincia getDeProvincia() {
//		return deProvincia;
//	}
//
//	public void setDeProvincia(DeProvincia deProvincia) {
//		this.deProvincia = deProvincia;
//	}
//
//	@Column(name = "username", nullable = false, length = 20)
//	public String getUsername() {
//		return this.username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	@Column(name = "pass_word", nullable = false, length = 1024)
//	public String getPassWord() {
//		return this.passWord;
//	}
//
//	public void setPassWord(String passWord) {
//		this.passWord = passWord;
//	}
//
//}
