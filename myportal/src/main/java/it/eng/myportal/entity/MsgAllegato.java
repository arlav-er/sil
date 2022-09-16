package it.eng.myportal.entity;

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

/**
 * 
 * @author Rodi A.
 *
 */
@Entity
@Table(name = "msg_allegato", schema = "myportal")
public class MsgAllegato extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 389378L;
	private Integer idMsgAllegato;
	private MsgMessaggio msgMessaggio;

	private byte[] contenuto;
	private String filename;

	public MsgAllegato() {
	}

	@Id
	@SequenceGenerator(name = "msg_allegato_id_msg_allegato_seq", sequenceName = "msg_allegato_id_msg_allegato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_allegato_id_msg_allegato_seq")
	@Column(name = "id_msg_allegato", unique = true, nullable = false)
	public Integer getIdMsgAllegato() {
		return this.idMsgAllegato;
	}

	public void setIdMsgAllegato(Integer idMsgAllegato) {
		this.idMsgAllegato = idMsgAllegato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_msg_messaggio", nullable = false)
	public MsgMessaggio getMsgMessaggio() {
		return this.msgMessaggio;
	}

	public void setMsgMessaggio(MsgMessaggio msgMessaggio) {
		this.msgMessaggio = msgMessaggio;
	}

	@Column(name = "contenuto")
	public byte[] getContenuto() {
		return this.contenuto;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	@Column(name = "filename")
	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
