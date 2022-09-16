package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.MsgMessaggio;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_tipo_messaggio", schema = "myportal")
public class DeTipoMessaggio extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 33063L;
	private String codTipoMessaggio;
	private String descrizione;

	private Set<MsgMessaggio> msgMessaggios = new HashSet<MsgMessaggio>(0);

	public DeTipoMessaggio() {
	}

	public DeTipoMessaggio(String codTipoMessaggio, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codTipoMessaggio = codTipoMessaggio;
		this.descrizione = descrizione;

	}

	public DeTipoMessaggio(String codTipoMessaggio, String descrizione, Date dtInizioVal, Date dtFineVal,
			Set<MsgMessaggio> msgMessaggios) {
		super(dtInizioVal, dtFineVal);
		this.codTipoMessaggio = codTipoMessaggio;
		this.descrizione = descrizione;
		this.msgMessaggios = msgMessaggios;
	}

	@Id
	@Column(name = "cod_tipo_messaggio", unique = true, nullable = false, length = 8)
	public String getCodTipoMessaggio() {
		return this.codTipoMessaggio;
	}

	public void setCodTipoMessaggio(String codTipoMessaggio) {
		this.codTipoMessaggio = codTipoMessaggio;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTipoMessaggio")
	public Set<MsgMessaggio> getMsgMessaggios() {
		return this.msgMessaggios;
	}

	public void setMsgMessaggios(Set<MsgMessaggio> msgMessaggios) {
		this.msgMessaggios = msgMessaggios;
	}
}
