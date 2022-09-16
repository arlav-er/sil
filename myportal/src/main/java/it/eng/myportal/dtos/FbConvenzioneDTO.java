package it.eng.myportal.dtos;

import java.util.Date;

import it.eng.myportal.utils.ConstantsSingleton;

public class FbConvenzioneDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -1756737498764539394L;

	private Date dataStipula;
	private String nomeConvenzione;
	private DeTipoFbConvenzioneDTO codTipoConvenzione;
	private DeStatoFbConvenzioneDTO codStatoConv;
	private Date dataScadenza;
	private String nomeLegaleRappresentante;
	private String cognomeLegaleRappresentante;

	private String numProtocollo;
	private Date dataProtocollo;
	private Integer idPrincipalProtocollo;

	private Date dataRevoca;
	private String motivoRevoca;
	private Integer idPrincipalRevoca;

	private String ragioneSociale;
	private Date dataProtocollazione;
	private String codiceFiscale;

	public FbConvenzioneDTO() {
		codTipoConvenzione = new DeTipoFbConvenzioneDTO();
		codStatoConv = new DeStatoFbConvenzioneDTO();
	}

	public boolean isConfirmed() {
		if (getCodStatoConv().getId() != null) {
			return ConstantsSingleton.DeStatoFbConvenzione.CONFERMATA.equals(getCodStatoConv().getId());
		} else
			return false;
	}

	public boolean isInLavorazione() {
		if (getCodStatoConv().getId() != null) {
			return ConstantsSingleton.DeStatoFbConvenzione.IN_LAVORAZIONE.equals(getCodStatoConv().getId());
		} else
			return false;
	}

	public boolean isProtocolla() {
		if (getCodStatoConv().getId() != null)
			return ConstantsSingleton.DeStatoFbConvenzione.PROTOCOLLATA.equals(getCodStatoConv().getId());
		else
			return false;
	}

	public boolean canRevoca() {
		// Può essere revocata se è protocollata o confermata
		return isProtocolla() || isConfirmed();
	}

	public boolean isRevoca() {
		if (getCodStatoConv().getId() != null)
			return ConstantsSingleton.DeStatoFbConvenzione.REVOCATA.equals(getCodStatoConv().getId());
		else
			return false;
	}

	public Date getDataRevoca() {
		return dataRevoca;
	}

	public void setDataRevoca(Date dataRevoca) {
		this.dataRevoca = dataRevoca;
	}

	public String getMotivoRevoca() {
		return motivoRevoca;
	}

	public void setMotivoRevoca(String motivoRevoca) {
		this.motivoRevoca = motivoRevoca;
	}

	public Integer getIdPrincipalRevoca() {
		return idPrincipalRevoca;
	}

	public void setIdPrincipalRevoca(Integer idPrincipalRevoca) {
		this.idPrincipalRevoca = idPrincipalRevoca;
	}

	public Date getDataStipula() {
		return dataStipula;
	}

	public void setDataStipula(Date dataStipula) {
		this.dataStipula = dataStipula;
	}

	public String getNomeConvenzione() {
		return nomeConvenzione;
	}

	public void setNomeConvenzione(String nomeConvenzione) {
		this.nomeConvenzione = nomeConvenzione;
	}

	public DeTipoFbConvenzioneDTO getCodTipoConvenzione() {
		return codTipoConvenzione;
	}

	public void setCodTipoConvenzione(DeTipoFbConvenzioneDTO codTipoConvenzione) {
		this.codTipoConvenzione = codTipoConvenzione;
	}

	public DeStatoFbConvenzioneDTO getCodStatoConv() {
		return codStatoConv;
	}

	public void setCodStatoConv(DeStatoFbConvenzioneDTO codStatoConv) {
		this.codStatoConv = codStatoConv;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getNumProtocollo() {
		return numProtocollo;
	}

	public void setNumProtocollo(String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	public Date getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(Date dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public Integer getIdPrincipalProtocollo() {
		return idPrincipalProtocollo;
	}

	public void setIdPrincipalProtocollo(Integer idPrincipalProtocollo) {
		this.idPrincipalProtocollo = idPrincipalProtocollo;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getNomeLegaleRappresentante() {
		return nomeLegaleRappresentante;
	}

	public void setNomeLegaleRappresentante(String nomeLegaleRappresentante) {
		this.nomeLegaleRappresentante = nomeLegaleRappresentante;
	}

	public String getCognomeLegaleRappresentante() {
		return cognomeLegaleRappresentante;
	}

	public void setCognomeLegaleRappresentante(String cognomeLegaleRappresentante) {
		this.cognomeLegaleRappresentante = cognomeLegaleRappresentante;
	}

	public Date getDataProtocollazione() {
		return dataProtocollazione;
	}

	public void setDataProtocollazione(Date dataProtocoliazione) {
		this.dataProtocollazione = dataProtocoliazione;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

}
