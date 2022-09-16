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

import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeProvincia;

@Entity
@Table(name = "yg_garanzia_over", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findYgAdesioneOverByIdPfPrincipal", query = "SELECT y FROM YgGaranziaOver y WHERE y.pfPrincipal.idPfPrincipal = :id_pf_principal ORDER BY dtAdesione desc, idYgGaranziaOver desc"),
		@NamedQuery(name = "findYgAdesioneOverCountByCf", query = "SELECT COUNT(y) FROM YgGaranziaOver y WHERE upper(y.codiceFiscale) = :codice_fiscale AND y.dtAdesione IS NOT NULL"),
		@NamedQuery(name = "findYgAdesioneOverValideByIdPfPrincipal", query = "SELECT y FROM YgGaranziaOver y WHERE y.pfPrincipal.idPfPrincipal = :idPfPrincipal AND y.dtAdesione IS NOT NULL ORDER BY y.dtAdesione"),
		@NamedQuery(name = "findYgAdesioneOverValideByIdPfPrincipalAndCodFiscale", query = "SELECT y FROM YgGaranziaOver y WHERE (y.pfPrincipal.idPfPrincipal = :idPfPrincipal OR upper(y.codiceFiscale) = :codiceFiscale) AND y.dtAdesione IS NOT NULL ORDER BY y.dtAdesione"),
		@NamedQuery(name = "findYgAdesioneOverValideByCf", query = "SELECT y FROM YgGaranziaOver y WHERE upper(y.codiceFiscale) = :codice_fiscale AND y.dtAdesione IS NOT NULL ORDER BY y.dtAdesione ASC") })
public class YgGaranziaOver extends AbstractEntity implements java.io.Serializable {

	private static final long serialVersionUID = 9124254743797104540L;

	private Integer idYgGaranziaOver;
	private String codiceFiscale;
	private Date dtAdesione;
	private Date dtPresaInCarico;
	private DeCpi deCpiAdesione;
	private PfPrincipal pfPrincipal;

	private String flgPercettoreAmmortizzatori;

	private String strMessWsAdesione;
	private String strMessInterfaccia;
	private Boolean flgPresoInCarico;

	private DeProvincia deProvinciaRif;

	public YgGaranziaOver() {
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
	@SequenceGenerator(name = "yg_garanzia_over_id_yg_garanzia_over_seq", sequenceName = "yg_garanzia_over_id_yg_garanzia_over_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_garanzia_over_id_yg_garanzia_over_seq")
	@Column(name = "id_yg_garanzia_over", unique = true, nullable = false)
	public Integer getIdYgGaranziaOver() {
		return idYgGaranziaOver;
	}

	public void setIdYgGaranziaOver(Integer idYgAdesione) {
		this.idYgGaranziaOver = idYgAdesione;
	}

	@Column(name = "codice_fiscale", length = 16, nullable = false)
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

	@Column(name = "str_mess_ws_adesione",columnDefinition="text")
	public String getStrMessWsAdesione() {
		return strMessWsAdesione;
	}

	public void setStrMessWsAdesione(String strMessWsInvioSap) {
		this.strMessWsAdesione = strMessWsInvioSap;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia_rif")
	public DeProvincia getDeProvincia() {
		return this.deProvinciaRif;
	}

	public void setDeProvincia(DeProvincia deProvinciaNotifica) {
		this.deProvinciaRif = deProvinciaNotifica;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_preso_in_carico", length = 1, nullable = false)
	public Boolean getFlgPresoInCarico() {
		return flgPresoInCarico;
	}

	public void setFlgPresoInCarico(Boolean flgPresoInCarico) {
		this.flgPresoInCarico = flgPresoInCarico;
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
	@JoinColumn(name = "cod_cpi_adesione")
	public DeCpi getDeCpiAdesione() {
		return deCpiAdesione;
	}

	public void setDeCpiAdesione(DeCpi deCpiAdesione) {
		this.deCpiAdesione = deCpiAdesione;
	}

	@Column(length = 1, nullable = false, name = "flg_percettore_ammortizzatori")
	public String getFlgPercettoreAmmortizzatori() {
		return flgPercettoreAmmortizzatori;
	}

	public void setFlgPercettoreAmmortizzatori(String flgRecuperoProv) {
		this.flgPercettoreAmmortizzatori = flgRecuperoProv;
	}

	@Column(name = "str_mess_interfaccia", columnDefinition="text")
	public String getStrMessInterfaccia() {
		return strMessInterfaccia;
	}

	public void setStrMessInterfaccia(String strMessInterfaccia) {
		this.strMessInterfaccia = strMessInterfaccia;
	}

}
