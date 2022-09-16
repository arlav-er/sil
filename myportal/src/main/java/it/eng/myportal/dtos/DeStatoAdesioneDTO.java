package it.eng.myportal.dtos;


public class DeStatoAdesioneDTO extends GenericDecodeDTO implements ITreeable {

	private static final long serialVersionUID = -2515203803110454530L;
	
	private String codMonoClasse;

	public String getCodMonoClasse() {
		return codMonoClasse;
	}

	public void setCodMonoClasse(String codMonoClasse) {
		this.codMonoClasse = codMonoClasse;
	}
	
}
