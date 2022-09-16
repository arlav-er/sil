package it.eng.myportal.dtos;


public class AtpConsulenzaDTO extends MsgMessaggioDTO {

	private static final long serialVersionUID = -3753231340571489657L;
	
	private String note;
	private Integer minuti;

	public AtpConsulenzaDTO() {
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getMinuti() {
		return minuti;
	}

	public void setMinuti(Integer minuti) {
		this.minuti = minuti;
	}

	

}
