package it.eng.myportal.entity.ts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import it.eng.myportal.entity.AbstractDecodeEntity;

/**
 * 
 * 
 * @author
 */

@Entity
@Table(schema = "myportal", name = "ts_timer", uniqueConstraints = {
		@UniqueConstraint(name = "ts_timer_server_name_timer_name_dt_ini_key", columnNames = { "server_name",
				"timer_name", "dt_inizio_val" }) })
@NamedQueries({
		@NamedQuery(name = "tsTimer.getTimerEnabled", query = "select d from TsTimer d where d.timerName = (:parTimerName) and flagAbilitato='Y' and current_date between d.dtInizioVal and d.dtFineVal"),
		@NamedQuery(name = "tsTimer.getTimer", query = "select d from TsTimer d where d.timerName = (:parTimerName)") })
public class TsTimer extends AbstractDecodeEntity implements java.io.Serializable {

	private static final long serialVersionUID = -700665560261760600L;
	public static final String ALL_SERVER_NODE = "*";

	private Integer idTsTimer;
	private String serverName;
	private String timerName;
	private Boolean flagAbilitato;

	public TsTimer() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getIdTsTimer() == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TsTimer)) {
			return false;
		} else {
			return this.getIdTsTimer().equals(((TsTimer) obj).getIdTsTimer());
		}
	}

	@Override
	public int hashCode() {
		return getIdTsTimer().hashCode();
	}

	@Id
	@Column(name = "id_ts_timer", columnDefinition = "serial", unique = true, nullable = false)
	@SequenceGenerator(name = "ts_timer_id_ts_timer_seq", sequenceName = "ts_timer_id_ts_timer_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ts_timer_id_ts_timer_seq")
	public Integer getIdTsTimer() {
		return idTsTimer;
	}

	public void setIdTsTimer(Integer idOpzione) {
		this.idTsTimer = idOpzione;
	}

	@Column(name = "server_name", length = 255, nullable = false)
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Column(name = "timer_name", length = 1000, nullable = false)
	public String getTimerName() {
		return timerName;
	}

	public void setTimerName(String timerName) {
		this.timerName = timerName;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_abilitato", nullable = false, length = 1)
	public Boolean getFlagAbilitato() {
		return flagAbilitato;
	}

	public void setFlagAbilitato(Boolean flagAbilitato) {
		this.flagAbilitato = flagAbilitato;
	}

	public boolean checkServerNode(String nodeName) {
		boolean ret = false;

		if (this.getServerName().equals(TsTimer.ALL_SERVER_NODE) || this.getServerName().equalsIgnoreCase(nodeName)) {
			ret = true;
		}

		return ret;
	}
}
