package it.eng.myportal.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Transient;

import org.apache.commons.lang3.time.DateFormatUtils;

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.enums.TipoNotificaEnum;
import it.eng.myportal.rest.app.helper.StatoNotifica;

/**
 * AppNotifica entity
 */
@Entity
@Table(name = "app_notifica", schema = "mycas")

@NamedQueries({
		@NamedQuery(name = "appNotifica.getNotificheBroadcast", query = "select n from AppNotifica n where n.pfPrincipalDest=null  and n.stato in (:listStato) order by n.idAppNotifica desc"),
		@NamedQuery(name = "appNotifica.getCountNotificheBroadcast", query = "select count(n.id) from AppNotifica n where n.pfPrincipalDest=null and n.stato in (:listStato)"),
		@NamedQuery(name = "appNotifica.getNotifichePersonali", query = "select n from AppNotifica n where n.pfPrincipalDest.idPfPrincipal=:idPfPrincipalDest and n.stato in (:listStato) order by n.idAppNotifica desc"),
		@NamedQuery(name = "appNotifica.getCountNotifichePersonali", query = "select count(n.id) from AppNotifica n where n.pfPrincipalDest.idPfPrincipal=:idPfPrincipalDest and n.stato in (:listStato)"),
		@NamedQuery(name = "appNotifica.getCountNuoveNotifichePersonali", query = "select count(n.id) from AppNotifica n where n.pfPrincipalDest.idPfPrincipal=:idPfPrincipalDest and n.stato in (:listStato)"),
		@NamedQuery(name = "appNotifica.getNotificheInviate", query = "select n from AppNotifica n where n.deProvincia.codProvincia=:codProvincia or n.deProvincia=null order by n.idAppNotifica desc"),
		@NamedQuery(name = "appNotifica.getCountNotificheInviate", query = "select count(n.id) from AppNotifica n where n.deProvincia.codProvincia=:codProvincia or n.deProvincia=null"),
		@NamedQuery(name = "appNotifica.findBySid", query = "select n from AppNotifica n where n.sidNotifica=:sidNotifica order by n.idAppNotifica") })
public class AppNotifica extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 8007213986009507530L;

	private Integer idAppNotifica;
	private String titolo;
	private String sottotitolo;
	private String messaggio;
	private DeProvincia deProvincia;
	private PfPrincipal pfPrincipalDest;
	private String sidNotifica;
	private TipoNotificaEnum tipoNotifica;
	private StatoNotifica stato;
	private String additionalData;

	@Id
	@SequenceGenerator(name = "mycas.app_notifica_id_app_notifica_seq", sequenceName = "mycas.app_notifica_id_app_notifica_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mycas.app_notifica_id_app_notifica_seq")
	@Column(name = "id_app_notifica", unique = true, nullable = false)
	public Integer getIdAppNotifica() {
		return idAppNotifica;
	}

	public void setIdAppNotifica(Integer idAppNotifica) {
		this.idAppNotifica = idAppNotifica;
	}

	@Column(name = "titolo", nullable = true, length = 255)
	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	@Column(name = "sottotitolo", nullable = true, length = 255)
	public String getSottotitolo() {
		return sottotitolo;
	}

	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}

	@Column(name = "messaggio", nullable = true, length = 2000)
	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia", nullable = true)
	public DeProvincia getDeProvincia() {
		return this.deProvincia;
	}

	public void setDeProvincia(DeProvincia deProvincia) {
		this.deProvincia = deProvincia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_principal_dest", nullable = true)
	public PfPrincipal getPfPrincipalDest() {
		return pfPrincipalDest;
	}

	public void setPfPrincipalDest(PfPrincipal pfPrincipalDest) {
		this.pfPrincipalDest = pfPrincipalDest;
	}

	@Column(name = "sid_notifica", nullable = true, length = 36)
	public String getSidNotifica() {
		return sidNotifica;
	}

	public void setSidNotifica(String sidNotifica) {
		this.sidNotifica = sidNotifica;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_notifica", length = 12)
	public TipoNotificaEnum getTipoNotifica() {
		return tipoNotifica;
	}

	public void setTipoNotifica(TipoNotificaEnum tipoNotifica) {
		this.tipoNotifica = tipoNotifica;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "stato", nullable = true, length = 1)
	public StatoNotifica getStato() {
		return stato;
	}

	public void setStato(StatoNotifica stato) {
		this.stato = stato;
	}

	@Column(name = "additional_data", nullable = true, length = 255)
	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public AppNotifica() {
	}

	public AppNotifica(String titolo, String sottotitolo, String messaggio, DeProvincia deProvincia,
			PfPrincipal pfPrincipalDest, PfPrincipal pfPrincipal, String sidNotifica, TipoNotificaEnum tipoNotifica,
			StatoNotifica stato) {
		super();
		this.idAppNotifica = null;
		this.messaggio = messaggio;

		this.deProvincia = deProvincia;
		this.pfPrincipalDest = pfPrincipalDest;
		this.sottotitolo = sottotitolo;
		this.titolo = titolo;
		this.sidNotifica = sidNotifica;
		this.tipoNotifica = tipoNotifica;
		this.stato = stato;

		Date now = new Date();
		this.setDtmIns(now);
		this.setDtmMod(now);
		this.setPfPrincipalIns(pfPrincipal);
		this.setPfPrincipalMod(pfPrincipal);
	}

	@Transient
	public boolean isBroadcast() {
		boolean ret = false;

		/*
		 * Se tipoNotifica BROADCAST lo è sicuramente, altrimenti si verifica se il destinatario è nullo (l'informazione
		 * tipoNotifica è stata aggiunta successivamente, senza bonifica del pregresso
		 */
		if (TipoNotificaEnum.BROADCAST.equals(this.getTipoNotifica())) {
			ret = true;
		} else if (this.getPfPrincipalDest() == null) {
			ret = true;
		}

		return ret;
	}

	@Transient
	public String getDtNotifica() {
		String ret = null;

		if (this.getDtmIns() != null) {
			ret = DateFormatUtils.formatUTC(this.getDtmIns(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()) + "Z";
		}

		return ret;
	}

	@Transient
	public boolean isNuova() {
		boolean ret = false;

		if (!isBroadcast() && stato != null && (stato.equals(StatoNotifica.R) || stato.equals(StatoNotifica.S)))
			ret = true;

		return ret;
	}

}
