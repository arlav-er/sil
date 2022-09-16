package it.eng.myportal.beans.statistic;

import java.math.BigInteger;

public class StatsGGDTO {
	
	private String descrizioneColonna;
	private BigInteger numTot;
	private BigInteger numReg;
	private BigInteger numNoReg;
	private BigInteger numNoRilevati;
		
	public StatsGGDTO() {
	}
		
	public StatsGGDTO(String descrizioneColonna, BigInteger numTot, BigInteger numReg, BigInteger numNoReg,
			BigInteger numNoRilevati) {
		super();
		this.descrizioneColonna = descrizioneColonna;
		this.numTot = numTot;
		this.numReg = numReg;
		this.numNoReg = numNoReg;
		this.numNoRilevati = numNoRilevati;
	}

	public String getDescrizioneColonna() {
		return descrizioneColonna;
	}

	public void setDescrizioneColonna(String descrizioneColonna) {
		this.descrizioneColonna = descrizioneColonna;
	}

	public BigInteger getNumTot() {
		return numTot;
	}

	public void setNumTot(BigInteger numTot) {
		this.numTot = numTot;
	}

	public BigInteger getNumReg() {
		return numReg;
	}

	public void setNumReg(BigInteger numReg) {
		this.numReg = numReg;
	}

	public BigInteger getNumNoReg() {
		return numNoReg;
	}

	public void setNumNoRer(BigInteger numNoReg) {
		this.numNoReg = numNoReg;
	}

	public BigInteger getNumNoRilevati() {
		return numNoRilevati;
	}

	public void setNumNoRilevati(BigInteger numNoRilevati) {
		this.numNoRilevati = numNoRilevati;
	}

	

	
}
