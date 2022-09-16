package it.eng.sil.coop.services;

public class CigException extends Exception {
	private static final long serialVersionUID = -8630078494751997363L;

	private String strsqlcode;
	private String strsqlerrm;

	public CigException(String _strsqlcode, String _strsqlerrm) {
		this.strsqlcode = _strsqlcode;
		this.strsqlerrm = _strsqlerrm;
	}

	public String getStrsqlcode() {
		return strsqlcode;
	}

	public void setStrsqlcode(String strsqlcode) {
		this.strsqlcode = strsqlcode;
	}

	public String getStrsqlerrm() {
		return strsqlerrm;
	}

	public void setStrsqlerrm(String strsqlerrm) {
		this.strsqlerrm = strsqlerrm;
	}
}
