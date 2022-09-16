package it.eng.myportal.dtos;


public class DeStatoAdesioneMinDTO extends GenericDecodeDTO implements ITreeable {
	
	private static final long serialVersionUID = -5521888829540125891L;
	
	private String codMonoAttiva;
	
	public String getCodMonoAttiva() {
		return codMonoAttiva;
	}
	
	public void setCodMonoAttiva(String codMonoAttiva) {
		this.codMonoAttiva = codMonoAttiva;
	}
	
}
