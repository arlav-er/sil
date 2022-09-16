package it.eng.sil.coop.bean.blen.dto;

public class RosaInsert {
	private String prgRosa;
	private String prgIncrocio;

	public RosaInsert(String prgRosa, String prgIncrocio) {
		this.prgRosa = prgRosa;
		this.prgIncrocio = prgIncrocio;
	}

	public String getPrgRosa() {
		return prgRosa;
	}

	public String getPrgIncrocio() {
		return prgIncrocio;
	}

}