package it.eng.myportal.entity;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
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

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeTipoServizio;

/**
 *@author Rodi A.
 */
@Entity
@Table(name = "ws_endpoint", schema = "mycas")
public class WsEndpoint extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;
	private Integer idWsEndpoint;
	private DeTipoServizio deTipoServizio;
	private DeProvincia deProvincia;
	private String address;
	private String username;
	private String passWord;

	public WsEndpoint() {
	}

	public WsEndpoint(Integer idWsEndpoint, PfPrincipal pfPrincipalByIdPrincipalMod,
			PfPrincipal pfPrincipalByIdPrincipalIns, DeTipoServizio deTipoServizio, DeProvincia deProvincia, String address, String username, String passWord, Date dtmIns,
			Date dtmMod) {
		super(pfPrincipalByIdPrincipalIns, pfPrincipalByIdPrincipalMod, dtmIns, dtmMod);
		this.idWsEndpoint = idWsEndpoint;
		this.deTipoServizio = deTipoServizio;
		this.deProvincia = deProvincia;
		this.address = address;
		this.username = username;
		this.passWord = passWord;

	}

	@Id
	@SequenceGenerator(name = "mycas.ws_endpoint_id_ws_endpoint_seq", sequenceName = "mycas.ws_endpoint_id_ws_endpoint_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mycas.ws_endpoint_id_ws_endpoint_seq")
	@Column(name = "id_ws_endpoint", unique = true, nullable = false)
	public Integer getIdWsEndpoint() {
		return this.idWsEndpoint;
	}

	public void setIdWsEndpoint(Integer idWsEndpoint) {
		this.idWsEndpoint = idWsEndpoint;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_tipo_servizio", nullable = false)
	public DeTipoServizio getDeTipoServizio() {
		return this.deTipoServizio;
	}

	public void setDeTipoServizio(DeTipoServizio deTipoServizio) {
		this.deTipoServizio = deTipoServizio;
	}

	@Column(name = "address", nullable = false)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia", nullable = false)
	public DeProvincia getDeProvincia() {
		return deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}
	
	@Column(name="username", nullable=false, length=20)
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Column(name="pass_word", nullable=false, length=1024)
    public String getPassWord() {
        return this.passWord;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
	

}
