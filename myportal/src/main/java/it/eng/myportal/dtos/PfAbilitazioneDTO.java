package it.eng.myportal.dtos;

public class PfAbilitazioneDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -1236604306938033548L;

	private String codRuoloPortale;
	private String codAttivitaPf;
	private String codFiltro;
	private Boolean flagInserimento;
	private Boolean flagModifica;
	private Boolean flagLettura;
	private Boolean flagCancellazione;
	private Boolean flagAmministrazione;
	private Boolean flagVisibile;

	public PfAbilitazioneDTO() {
		super();
	}

	public Boolean getFlagInserimento() {
		return flagInserimento;
	}

	public Boolean getFlagModifica() {
		return flagModifica;
	}

	public Boolean getFlagLettura() {
		return flagLettura;
	}

	public Boolean getFlagCancellazione() {
		return flagCancellazione;
	}

	public Boolean getFlagAmministrazione() {
		return flagAmministrazione;
	}

	public Boolean getFlagVisibile() {
		return flagVisibile;
	}

	public void setFlagInserimento(Boolean flagInserimento) {
		this.flagInserimento = flagInserimento;
	}

	public void setFlagModifica(Boolean flagModifica) {
		this.flagModifica = flagModifica;
	}

	public void setFlagLettura(Boolean flagLettura) {
		this.flagLettura = flagLettura;
	}

	public void setFlagCancellazione(Boolean flagCancellazione) {
		this.flagCancellazione = flagCancellazione;
	}

	public void setFlagAmministrazione(Boolean flagAmministrazione) {
		this.flagAmministrazione = flagAmministrazione;
	}

	public void setFlagVisibile(Boolean flagVisibile) {
		this.flagVisibile = flagVisibile;
	}

	public String getCodRuoloPortale() {
		return codRuoloPortale;
	}

	public String getCodAttivitaPf() {
		return codAttivitaPf;
	}

	public String getCodFiltro() {
		return codFiltro;
	}

	public void setCodRuoloPortale(String codRuoloPortale) {
		this.codRuoloPortale = codRuoloPortale;
	}

	public void setCodAttivitaPf(String codAttivitaPf) {
		this.codAttivitaPf = codAttivitaPf;
	}

	public void setCodFiltro(String codFiltro) {
		this.codFiltro = codFiltro;
	}

}
