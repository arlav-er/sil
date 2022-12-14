package it.eng.myportal.entity;

// default package
// Generated 28-feb-2013 11.05.34 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * AtpConsulenza generated by hbm2java
 */
@Entity
@Table(name = "atp_consulenza", schema = "myportal")
public class AtpConsulenza extends AbstractEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4793091667168193739L;
	private Integer idMsgMessaggio;
	private MsgMessaggio msgMessaggio;
	private MsgMessaggioAtipico msgMessaggioAtipico;
	private String note;
	private Integer minuti;

	public AtpConsulenza() {
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "msgMessaggio"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_msg_messaggio", unique = true, nullable = false)
	public Integer getIdMsgMessaggio() {
		return this.idMsgMessaggio;
	}

	public void setIdMsgMessaggio(Integer idMsgMessaggio) {
		this.idMsgMessaggio = idMsgMessaggio;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public MsgMessaggio getMsgMessaggio() {
		return this.msgMessaggio;
	}

	public void setMsgMessaggio(MsgMessaggio msgMessaggio) {
		this.msgMessaggio = msgMessaggio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_msg_messaggio_atipico")
	public MsgMessaggioAtipico getMsgMessaggioAtipico() {
		return this.msgMessaggioAtipico;
	}

	public void setMsgMessaggioAtipico(MsgMessaggioAtipico msgMessaggioAtipico) {
		this.msgMessaggioAtipico = msgMessaggioAtipico;
	}

	@Column(name = "note", length = 3000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "minuti")
	public Integer getMinuti() {
		return this.minuti;
	}

	public void setMinuti(Integer minuti) {
		this.minuti = minuti;
	}

}
