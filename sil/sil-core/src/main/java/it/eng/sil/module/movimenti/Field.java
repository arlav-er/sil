package it.eng.sil.module.movimenti;

/*
 * Classe che contiene le informazioni sui field per il TXTExtractor, ogni field e contraddistinto da un nome, un punto di
 * inizio nel record e un punto di fine.
 * 
 * @author Paolo Roccetti
 * */
public class Field {
	private String name;
	private int init;
	private int ends;

	public Field(String name, int init, int ends) {
		this.name = name;
		this.init = init;
		this.ends = ends;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public int getInit() {
		return init;
	}

	public void setInit(int newInit) {
		init = newInit;
	}

	public int getEnds() {
		return ends;
	}

	public void setEnds(int newEnds) {
		ends = newEnds;
	}
}