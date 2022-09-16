package it.eng.myportal.entity;

public enum CodValutazioneEnum {
	L0("L0"),
	L1("L1"),
	L2("L2"),
	L3("L3"),
	L4("L4");
	
	//L1,L2,L3,L4,L0;
	private String value;
	public String getValue() {
	    return value;
	   }
	private CodValutazioneEnum(String value) {
	  this.value = value;
	 } 
}
