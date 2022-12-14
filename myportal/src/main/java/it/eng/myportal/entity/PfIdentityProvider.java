package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.enums.TipoProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

/**
 * Utente generated by hbm2java
 */

@NamedQueries({
	@NamedQuery(name="FIND_BY_DATA", query="select p from PfIdentityProvider p where p.data = :data and p.codTipoProvider = :provider"),
	@NamedQuery(name="FIND_BY_PFP_ID", query="select p from PfIdentityProvider p where p.pfPrincipal.id = :PFPId")
})
@Entity
@Table(name = "pf_identity_provider", schema = "mycas")
public class PfIdentityProvider extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 414536L;
	private Integer idPfIdentityProvider;
	private PfPrincipal pfPrincipal;
	private TipoProvider codTipoProvider;
	private String data;

	public PfIdentityProvider() {
	}

	@Id
	@SequenceGenerator(name = "mycas.pf_identity_provider_id_pf_identity_provider_seq", sequenceName = "mycas.pf_identity_provider_id_pf_identity_provider_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mycas.pf_identity_provider_id_pf_identity_provider_seq")
	@Column(name = "id_pf_identity_provider", unique = true, nullable = false)
	public Integer getIdPfIdentityProvider() {
		return this.idPfIdentityProvider;
	}

	public void setIdPfIdentityProvider(Integer idPfIdentityProvider) {
		this.idPfIdentityProvider = idPfIdentityProvider;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipalByIdPfPrincipal) {
		this.pfPrincipal = pfPrincipalByIdPfPrincipal;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "cod_tipo_provider", nullable = false, length = 8)
	public TipoProvider getCodTipoProvider() {
		return codTipoProvider;
	}

	public void setCodTipoProvider(TipoProvider codTipoProvider) {
		this.codTipoProvider = codTipoProvider;
	}

	@Column(name = "data")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
