package it.eng.sil.myaccount.model.entity.myportal;

import it.eng.sil.myaccount.model.utils.MessaggioUtils;
import it.eng.sil.mycas.model.entity.BaseTabellaGestioneEntity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "msg_messaggio", schema = "myportal")
public class MsgMessaggio extends BaseTabellaGestioneEntity implements java.io.Serializable {
	private static final long serialVersionUID = 90883L;

	private Integer idMsgMessaggio;
	private String ticket;
	private String oggetto;
	private String corpo;
	private Integer idPfPrincipalFrom;
	private Integer idPfPrincipalTo;
	private String codProvinciaTo;
	private Date dtScadenza;
	private String codTipoMessaggio;
	private String codTema;
	private Integer idMsgMessaggioPrec;
	private Integer idMsgMessaggioInoltro;
	private Date scadenza;
	private String destinatario;
	private Boolean letto;

	public MsgMessaggio() {
	}

	public MsgMessaggio(String codTipoMessaggio) {
		this();
		this.codTipoMessaggio = codTipoMessaggio;
		this.idPfPrincipalFrom = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, MessaggioUtils.GIORNI_SCADENZA);
		this.scadenza = c.getTime();
		letto = false;
	}

	@Id
	@SequenceGenerator(name = "msg_messaggio_id_msg_messaggio_seq", sequenceName = "msg_messaggio_id_msg_messaggio_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_messaggio_id_msg_messaggio_seq")
	@Column(unique = true, nullable = false)
	public Integer getIdMsgMessaggio() {
		return this.idMsgMessaggio;
	}

	public void setIdMsgMessaggio(Integer idMsgMessaggio) {
		this.idMsgMessaggio = idMsgMessaggio;
	}

	@Column(length = 100)
	public String getTicket() {
		return this.ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Column(nullable = false, length = 250)
	public String getOggetto() {
		return this.oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	@Column(nullable = false, length = 4000)
	public String getCorpo() {
		return this.corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	@Column(nullable = false)
	public Integer getIdPfPrincipalFrom() {
		return this.idPfPrincipalFrom;
	}

	public void setIdPfPrincipalFrom(Integer id) {
		this.idPfPrincipalFrom = id;
	}

	@Column
	public Integer getIdPfPrincipalTo() {
		return this.idPfPrincipalTo;
	}

	public void setIdPfPrincipalTo(Integer id) {
		this.idPfPrincipalTo = id;
	}

	@Column(length = 8)
	public String getCodProvinciaTo() {
		return codProvinciaTo;
	}

	public void setCodProvinciaTo(String codProvinciaTo) {
		this.codProvinciaTo = codProvinciaTo;
	}

	@Temporal(TemporalType.DATE)
	@Column(length = 13)
	public Date getDtScadenza() {
		return this.dtScadenza;
	}

	public void setDtScadenza(Date dtScadenza) {
		this.dtScadenza = dtScadenza;
	}

	@Column(nullable = false)
	public String getCodTipoMessaggio() {
		return this.codTipoMessaggio;
	}

	public void setCodTipoMessaggio(String id) {
		this.codTipoMessaggio = id;
	}

	@Column
	public String getCodTema() {
		return this.codTema;
	}

	public void setCodTema(String id) {
		this.codTema = id;
	}

	@Column
	public Integer getIdMsgMessaggioPrec() {
		return this.idMsgMessaggioPrec;
	}

	public void setIdMsgMessaggioPrec(Integer id) {
		this.idMsgMessaggioPrec = id;
	}

	@Column
	public Integer getIdMsgMessaggioInoltro() {
		return this.idMsgMessaggioInoltro;
	}

	public void setIdMsgMessaggioInoltro(Integer id) {
		this.idMsgMessaggioInoltro = id;
	}

	@Transient
	public Date getScadenza() {
		return scadenza;
	}

	public void setScadenza(Date datScad) {
		this.scadenza = datScad;
	}

	@Transient
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	@Transient
	public Boolean getLetto() {
		return letto;
	}

	public void setLetto(Boolean letto) {
		this.letto = letto;
	}

}