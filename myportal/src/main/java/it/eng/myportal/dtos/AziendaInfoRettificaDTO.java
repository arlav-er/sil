package it.eng.myportal.dtos;

public class AziendaInfoRettificaDTO extends AziendaInfoDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1658287689107546043L;
	/* indica se si tratta di una nuova rettifica o meno */
	private boolean nuova;
	private PfPrincipalDTO pfPrincipalDTO;

	public AziendaInfoRettificaDTO() {
		nuova = true;
	}

	public boolean isNuova() {
		return nuova;
	}

	public void setNuova(boolean nuova) {
		this.nuova = nuova;
	}

	public PfPrincipalDTO getPfPrincipalDTO() {
		return pfPrincipalDTO;
	}

	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}

}
