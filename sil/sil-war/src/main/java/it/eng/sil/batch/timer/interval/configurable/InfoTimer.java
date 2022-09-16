package it.eng.sil.batch.timer.interval.configurable;

import java.io.Serializable;

public class InfoTimer implements Serializable {

	private static final long serialVersionUID = -8106140814222078231L;

	private String timerName;
	private String classExecuteBatch;
	private String oraInterruzione;
	private String batchUserId;
	private String batchUserProfilo;
	private String batchUserGruppo;

	/**
	 * @param timerName
	 * @param classExecuteBatch
	 * @param oraInterruzione
	 * @param batchUserId
	 * @param batchUserProfilo
	 * @param batchUserGruppo
	 */
	public InfoTimer(String timerName, String classExecuteBatch, String oraInterruzione, String batchUserId,
			String batchUserProfilo, String batchUserGruppo) {
		super();
		this.timerName = timerName;
		this.classExecuteBatch = classExecuteBatch;
		this.oraInterruzione = oraInterruzione;
		this.batchUserId = batchUserId;
		this.batchUserProfilo = batchUserProfilo;
		this.batchUserGruppo = batchUserGruppo;
	}

	/**
	 * @return the timerName
	 */
	public String getTimerName() {
		return timerName;
	}

	/**
	 * @return the classExecuteBatch
	 */
	public String getClassExecuteBatch() {
		return classExecuteBatch;
	}

	/**
	 * @return the oraInterruzione
	 */
	public String getOraInterruzione() {
		return oraInterruzione;
	}

	/**
	 * @return the batchUserId
	 */
	public String getBatchUserId() {
		return batchUserId;
	}

	/**
	 * @return the batchUserProfilo
	 */
	public String getBatchUserProfilo() {
		return batchUserProfilo;
	}

	/**
	 * @return the batchUserGruppo
	 */
	public String getBatchUserGruppo() {
		return batchUserGruppo;
	}

	/**
	 * @return batchUserId + batchUserProfilo + batchUserGruppo
	 */
	public String getBatchUserInfo() {
		return this.batchUserId + " " + this.batchUserProfilo + " " + this.batchUserGruppo;

	}
}
