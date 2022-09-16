package it.eng.myportal.dtos;


import java.util.Date;

public class YgAdesioneDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 9124254743797104540L;

	private String codiceFiscale;
	private Date dtAdesione;
	private String identificativoSap;
	private String identificativoSapOld;
	private PfPrincipalDTO pfPrincipal;
	private String codMonoProv;
	private Boolean flgAdesione;
	private String strMessWsAdesione;
	private Boolean flgSap;
	private String strMessWsInvioSap;
	private String strMessWsNotifica;

	private Boolean flgPresoInCarico;
	private PfPrincipalDTO pfPrincipalPic;
	private Date dtPresaInCarico;

	private DeProvinciaDTO deProvinciaNotifica;
	private DeRegioneDTO deRegioneRifNotifica;

	private DeCpiDTO deCpiAdesione;

	private String flgRecuperoProv;
	
	private DeCpiDTO deCpiAssegnazione;
	private String codMonoRecuperoCpi;
	private String strMessAccount;
	
	private String emailRifNotifica;
	private String nomeRifNotifica;
	private String cognomeRifNotifica;

	private Boolean flgCreatoAccount;
	
	private DeStatoAdesioneMinDTO deStatoAdesioneMin;
	private Date dtStatoAdesioneMin;
	private DeStatoAdesioneDTO deStatoAdesione;
	private Date dtFineStatoAdesione;
	private String note;
	
	private DeComuneDTO deComuneResidenzaRifNotifica;
	private DeComuneDTO deComuneDomicilioRifNotifica;
	

	
	public YgAdesioneDTO() {
	}

	public PfPrincipalDTO getPfPrincipal() {
		return this.pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipalDTO pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	public String getIdentificativoSap() {
		return identificativoSap;
	}

	public void setIdentificativoSap(String identificativoSap) {
		this.identificativoSap = identificativoSap;
	}

	public String getIdentificativoSapOld() {
		return identificativoSapOld;
	}

	public void setIdentificativoSapOld(String identificativoSapOld) {
		this.identificativoSapOld = identificativoSapOld;
	}

	public String getCodMonoProv() {
		return codMonoProv;
	}

	public void setCodMonoProv(String codMonoProv) {
		this.codMonoProv = codMonoProv;
	}

	public Boolean getFlgAdesione() {
		return flgAdesione;
	}

	public void setFlgAdesione(Boolean flgAdesione) {
		this.flgAdesione = flgAdesione;
	}

	public String getStrMessWsAdesione() {
		return strMessWsAdesione;
	}

	public void setStrMessWsAdesione(String strMessWsAdesione) {
		this.strMessWsAdesione = strMessWsAdesione;
	}

	public Boolean getFlgSap() {
		return flgSap;
	}

	public void setFlgSap(Boolean flgSap) {
		this.flgSap = flgSap;
	}

	public String getStrMessWsInvioSap() {
		return strMessWsInvioSap;
	}

	public void setStrMessWsInvioSap(String strMessWsInvioSap) {
		this.strMessWsInvioSap = strMessWsInvioSap;
	}

	public String getStrMessWsNotifica() {
		return strMessWsNotifica;
	}

	public void setStrMessWsNotifica(String strMessWsNotifica) {
		this.strMessWsNotifica = strMessWsNotifica;
	}

	public Boolean getFlgPresoInCarico() {
		return flgPresoInCarico;
	}

	public void setFlgPresoInCarico(Boolean flgPresoInCarico) {
		this.flgPresoInCarico = flgPresoInCarico;
	}

	public PfPrincipalDTO getPfPrincipalPic() {
		return pfPrincipalPic;
	}

	public void setPfPrincipalPic(PfPrincipalDTO pfPrincipalPic) {
		this.pfPrincipalPic = pfPrincipalPic;
	}

	public Date getDtPresaInCarico() {
		return dtPresaInCarico;
	}

	public void setDtPresaInCarico(Date dtPresaInCarico) {
		this.dtPresaInCarico = dtPresaInCarico;
	}

	public DeProvinciaDTO getDeProvinciaNotifica() {
		return deProvinciaNotifica;
	}

	public void setDeProvinciaNotifica(DeProvinciaDTO deProvinciaNotifica) {
		this.deProvinciaNotifica = deProvinciaNotifica;
	}

	public DeRegioneDTO getDeRegioneRifNotifica() {
		return deRegioneRifNotifica;
	}

	public void setDeRegioneRifNotifica(DeRegioneDTO deRegioneRifNotifica) {
		this.deRegioneRifNotifica = deRegioneRifNotifica;
	}

	public DeCpiDTO getDeCpiAdesione() {
		return deCpiAdesione;
	}

	public void setDeCpiAdesione(DeCpiDTO deCpiAdesione) {
		this.deCpiAdesione = deCpiAdesione;
	}

	public String getFlgRecuperoProv() {
		return flgRecuperoProv;
	}

	public void setFlgRecuperoProv(String flgRecuperoProv) {
		this.flgRecuperoProv = flgRecuperoProv;
	}

	public DeCpiDTO getDeCpiAssegnazione() {
		return deCpiAssegnazione;
	}

	public void setDeCpiAssegnazione(DeCpiDTO deCpiAssegnazione) {
		this.deCpiAssegnazione = deCpiAssegnazione;
	}

	public String getCodMonoRecuperoCpi() {
		return codMonoRecuperoCpi;
	}

	public void setCodMonoRecuperoCpi(String codMonoRecuperoCpi) {
		this.codMonoRecuperoCpi = codMonoRecuperoCpi;
	}

	public String getStrMessAccount() {
		return strMessAccount;
	}

	public void setStrMessAccount(String strMessAccount) {
		this.strMessAccount = strMessAccount;
	}

	public String getEmailRifNotifica() {
		return emailRifNotifica;
	}

	public void setEmailRifNotifica(String emailRifNotifica) {
		this.emailRifNotifica = emailRifNotifica;
	}

	public String getNomeRifNotifica() {
		return nomeRifNotifica;
	}

	public void setNomeRifNotifica(String nomeRifNotifica) {
		this.nomeRifNotifica = nomeRifNotifica;
	}

	public String getCognomeRifNotifica() {
		return cognomeRifNotifica;
	}

	public void setCognomeRifNotifica(String cognomeRifNotifica) {
		this.cognomeRifNotifica = cognomeRifNotifica;
	}

	public Boolean getFlgCreatoAccount() {
		return flgCreatoAccount;
	}

	public void setFlgCreatoAccount(Boolean flgCreatoAccount) {
		this.flgCreatoAccount = flgCreatoAccount;
	}

	public DeStatoAdesioneDTO getDeStatoAdesione() {
		return deStatoAdesione;
	}

	public void setDeStatoAdesione(DeStatoAdesioneDTO deStatoAdesione) {
		this.deStatoAdesione = deStatoAdesione;
	}

	public Date getDtFineStatoAdesione() {
		return dtFineStatoAdesione;
	}

	public void setDtFineStatoAdesione(Date dtFineStatoAdesione) {
		this.dtFineStatoAdesione = dtFineStatoAdesione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public DeComuneDTO getDeComuneResidenzaRifNotifica() {
		return deComuneResidenzaRifNotifica;
	}

	public void setDeComuneResidenzaRifNotifica(
			DeComuneDTO deComuneResidenzaRifNotifica) {
		this.deComuneResidenzaRifNotifica = deComuneResidenzaRifNotifica;
	}

	public DeComuneDTO getDeComuneDomicilioRifNotifica() {
		return deComuneDomicilioRifNotifica;
	}

	public void setDeComuneDomicilioRifNotifica(
			DeComuneDTO deComuneDomicilioRifNotifica) {
		this.deComuneDomicilioRifNotifica = deComuneDomicilioRifNotifica;
	}

	public DeStatoAdesioneMinDTO getDeStatoAdesioneMin() {
		return deStatoAdesioneMin;
	}

	public void setDeStatoAdesioneMin(DeStatoAdesioneMinDTO deStatoAdesioneMin) {
		this.deStatoAdesioneMin = deStatoAdesioneMin;
	}

	public Date getDtStatoAdesioneMin() {
		return dtStatoAdesioneMin;
	}

	public void setDtStatoAdesioneMin(Date dtStatoAdesioneMin) {
		this.dtStatoAdesioneMin = dtStatoAdesioneMin;
	}

}
