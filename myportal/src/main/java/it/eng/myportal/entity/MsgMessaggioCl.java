package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import it.eng.myportal.entity.decodifiche.DeStatoInvioCl;

/**
 * MsgMessaggioCl
 * 
 * @author Rodi
 */
@NamedQueries({ @NamedQuery(name = "findMessaggioByCodComunicazione", query = "select m from MsgMessaggioCl m where m.codComunicazione = :codComunicazione") })
@Entity
@Table(name = "msg_messaggio_cl", schema = "myportal")
public class MsgMessaggioCl extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 978984L;

	private MsgMessaggio msgMessaggio;
	private Integer idMsgMessaggio;

	private String codComunicazione;
	private String codComunicazionePrec;

	private DeStatoInvioCl deStatoInvioCl;

	private Integer codTipoSoggettoMitt;

	private MsgSoggettoCl mittente;

	private MsgSoggettoCl destinatario;

	public MsgMessaggioCl() {
	}

	@Column(name = "cod_comunicazione", nullable = false, length = 25)
	public String getCodComunicazione() {
		return codComunicazione;
	}

	@Column(name = "cod_comunicazione_prec", nullable = true, length = 25)
	public String getCodComunicazionePrec() {
		return codComunicazionePrec;
	}

	@Column(name = "cod_tipo_soggetto_mitt", nullable = false)
	public Integer getCodTipoSoggettoMitt() {
		return codTipoSoggettoMitt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_stato_invio", nullable = false)
	public DeStatoInvioCl getDeStatoInvioCl() {
		return deStatoInvioCl;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_soggetto_dest", nullable = false)
	public MsgSoggettoCl getDestinatario() {
		return destinatario;
	}

	@Id
	@Column(name = "id_msg_messaggio", unique = true, nullable = false)
	public Integer getIdMsgMessaggio() {
		return idMsgMessaggio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_soggetto_mitt", nullable = false)
	public MsgSoggettoCl getMittente() {
		return mittente;
	}

	@PrimaryKeyJoinColumn
	@OneToOne(fetch = FetchType.LAZY)
	public MsgMessaggio getMsgMessaggio() {
		return msgMessaggio;
	}

	public void setCodComunicazione(String codComunicazione) {
		this.codComunicazione = codComunicazione;
	}

	public void setCodComunicazionePrec(String codComunicazionePrec) {
		this.codComunicazionePrec = codComunicazionePrec;
	}

	public void setCodTipoSoggettoMitt(Integer codTipoSoggettoMitt) {
		this.codTipoSoggettoMitt = codTipoSoggettoMitt;
	}

	public void setDeStatoInvioCl(DeStatoInvioCl deStatoInvioCl) {
		this.deStatoInvioCl = deStatoInvioCl;
	}

	public void setDestinatario(MsgSoggettoCl destinatario) {
		this.destinatario = destinatario;
	}

	public void setIdMsgMessaggio(Integer idMsgMessaggio) {
		this.idMsgMessaggio = idMsgMessaggio;
	}

	public void setMittente(MsgSoggettoCl mittente) {
		this.mittente = mittente;
	}

	public void setMsgMessaggio(MsgMessaggio msgMessaggio) {
		this.msgMessaggio = msgMessaggio;
	}
}
