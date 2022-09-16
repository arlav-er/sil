package it.eng.myportal.entity.ts;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;

/**
 * Tabella per memorizzazione di parametri di sistema.
 * 
 * @see TsGetOpzioniEJB
 * 
 *      questi parametri andrebbero gestiti da web, ma e` possibile alterarli anche a DB
 * 
 * @author Vincenzo C.
 *
 */
@Entity
@Table(name = "ts_opzioni", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findByCodOpz", query = "select d from TsOpzioni d where d.codOpzione = (:parOpz)") })
public class TsOpzioni implements java.io.Serializable {

	private static final long serialVersionUID = 8746758232108213480L;
	private Integer idOpzione;
	private TsOpzioneEnum codOpzione;
	private Long numValore;

	// solo uno di questi sara diverso da null
	private String strValore;
	private String strNote;
	private Date dtValore;

	public TsOpzioni() {
	}

	@Id
	@Column(name = "id_opzione", columnDefinition = "serial")
	@SequenceGenerator(name = "TsOpzioniSequence", sequenceName = "ts_opzioni_id_opzione_seq", schema = "myportal", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TsOpzioniSequence")
	public Integer getIdOpzione() {
		return idOpzione;
	}

	public void setIdOpzione(Integer idOpzione) {
		this.idOpzione = idOpzione;
	}

	@Column(name = "num_valore", nullable = true)
	public Long getNumValore() {
		return numValore;
	}

	public void setNumValore(Long numValore) {
		this.numValore = numValore;
	}

	@Column(name = "str_valore", nullable = true, length = 255)
	public String getStrValore() {
		return strValore;
	}

	public void setStrValore(String strValore) {
		this.strValore = strValore;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "cod_opzione", nullable = false, unique = true)
	public TsOpzioneEnum getCodOpzione() {
		return codOpzione;
	}

	public void setCodOpzione(TsOpzioneEnum codOpzione) {
		this.codOpzione = codOpzione;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getIdOpzione() == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TsOpzioni)) {
			return false;
		} else {
			return this.getIdOpzione().equals(((TsOpzioni) obj).getIdOpzione());
		}
	}

	@Override
	public int hashCode() {
		return getIdOpzione().hashCode();
	}

	@Column(name = "str_note", length = 2000)
	public String getStrNote() {
		return strNote;
	}

	public void setStrNote(String strNote) {
		this.strNote = strNote;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_valore", nullable = true, length = 29)
	public Date getDtValore() {
		return dtValore;
	}

	public void setDtValore(Date dtValore) {
		this.dtValore = dtValore;
	}
}
