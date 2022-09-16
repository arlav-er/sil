package it.eng.sil.module.movimenti;

/*
 * Classe che contiene le informazioni sui parametri per i processori, ogni parameter e contraddistinto da un nome ed un 
 * value.
 * 
 * @author Paolo Roccetti
 * */
public class Parameter {
	private String name;
	private String value;

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		value = newValue;
	}
}