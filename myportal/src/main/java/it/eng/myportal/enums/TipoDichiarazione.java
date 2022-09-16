package it.eng.myportal.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public enum TipoDichiarazione {
	//Enumerazione
	NASPI("NASPI"),
	CIGS("CIGS");
	
	//Variabili e blocchi statici
	private static List<SelectItem> dichiarazioneBdAdesioneList;
	private String label;
	
	static {
		dichiarazioneBdAdesioneList = new ArrayList<SelectItem>();
		for (TipoDichiarazione current : TipoDichiarazione.values()) {
			dichiarazioneBdAdesioneList.add(new SelectItem(current, current.getLabel()));
		}
	}
	
	//Costruttore
	private TipoDichiarazione(String label){
		this.label=label;
	}
	
	//Getter and setter
	public String getLabel(){
		return label;
	}

	public static List<SelectItem> asSelectItems() {
		return dichiarazioneBdAdesioneList;
	}
	
}
