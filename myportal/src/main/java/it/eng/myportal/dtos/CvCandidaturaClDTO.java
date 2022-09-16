package it.eng.myportal.dtos;

import java.util.Date;

/**
 * Data transfer object del CV,
 * 
 * @author Turrini
 * 
 * @see AbstractUpdatablePkDTO
 * @see ICurriculumSection
 * @see IHasUniqueValue
 */

public class CvCandidaturaClDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 900087551059311555L;
	private DeStatoInvioClDTO deStatoInvioCl;
	private DeTipoComunicazioneClDTO deTipoComunicazioneCl;
	private String codComunicazione;
	private String codComunicazionePrec;
	private Date dtInvio;

	public CvCandidaturaClDTO() {
		super();
		deTipoComunicazioneCl = new DeTipoComunicazioneClDTO();
		deStatoInvioCl = new DeStatoInvioClDTO();
	}

	public DeStatoInvioClDTO getDeStatoInvioCl() {
		return deStatoInvioCl;
	}

	public void setDeStatoInvioCl(DeStatoInvioClDTO deStatoInvioClDTO) {
		this.deStatoInvioCl = deStatoInvioClDTO;
	}

	public DeTipoComunicazioneClDTO getDeTipoComunicazioneCl() {
		return deTipoComunicazioneCl;
	}

	public void setDeTipoComunicazioneCl(
			DeTipoComunicazioneClDTO deTipoComunicazioneClDTO) {
		this.deTipoComunicazioneCl = deTipoComunicazioneClDTO;
	}

	public String getCodComunicazione() {
		return codComunicazione;
	}

	public void setCodComunicazione(String codComunicazione) {
		this.codComunicazione = codComunicazione;
	}

	public String getCodComunicazionePrec() {
		return codComunicazionePrec;
	}

	public void setCodCandidaturaPrec(String codComunicazionePrec) {
		this.codComunicazionePrec = codComunicazionePrec;
	}

	public Date getDtInvio() {
		return dtInvio;
	}

	public void setDtInvio(Date dtInvio) {
		this.dtInvio = dtInvio;
	}

}
