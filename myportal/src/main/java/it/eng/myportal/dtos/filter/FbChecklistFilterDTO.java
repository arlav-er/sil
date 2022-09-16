package it.eng.myportal.dtos.filter;

import java.util.Date;

import it.eng.myportal.dtos.IDTO;

public class FbChecklistFilterDTO implements IDTO {
	private static final long serialVersionUID = 2540746747818068240L;

	private Integer idPfPrincipal;
	private Date dataDa;
	private Date dataA;
	private boolean soloSchedaFabbisogno;
	private String aziendaOspitante;
	private String codStato;
	private boolean escludiInLavorazione;
	private String codiceFiscale;

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public Date getDataDa() {
		return dataDa;
	}

	public void setDataDa(Date dataDa) {
		this.dataDa = dataDa;
	}

	public Date getDataA() {
		return dataA;
	}

	public void setDataA(Date dataA) {
		this.dataA = dataA;
	}

	public boolean isSoloSchedaFabbisogno() {
		return soloSchedaFabbisogno;
	}

	public void setSoloSchedaFabbisogno(boolean soloSchedaFabbisogno) {
		this.soloSchedaFabbisogno = soloSchedaFabbisogno;
	}

	public String getAziendaOspitante() {
		return aziendaOspitante;
	}

	public void setAziendaOspitante(String aziendaOspitante) {
		this.aziendaOspitante = aziendaOspitante;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public boolean isEscludiInLavorazione() {
		return escludiInLavorazione;
	}

	public void setEscludiInLavorazione(boolean escludiInLavorazione) {
		this.escludiInLavorazione = escludiInLavorazione;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

}
