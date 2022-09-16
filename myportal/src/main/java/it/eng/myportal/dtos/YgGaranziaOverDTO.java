package it.eng.myportal.dtos;

import java.util.Date;

public class YgGaranziaOverDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -6441245506259312959L;

	private PfPrincipalDTO pfPrincipal;
	private DeProvinciaDTO deProvinciaRif;
	private String codiceFiscale;
	private Date dtAdesione;
	private Boolean flgPresoInCarico;
	private Date dtPresaInCarico;
	private DeCpiDTO deCpiAdesione;
	private String flgPercettoreAmmortizzatori;
	private String strMessWsAdesione;
	private String strMessInterfaccia;

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

	public Date getDtPresaInCarico() {
		return dtPresaInCarico;
	}

	public void setDtPresaInCarico(Date dtPresaInCarico) {
		this.dtPresaInCarico = dtPresaInCarico;
	}

	public PfPrincipalDTO getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipalDTO pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	public String getFlgPercettoreAmmortizzatori() {
		return flgPercettoreAmmortizzatori;
	}

	public void setFlgPercettoreAmmortizzatori(String flgPercettoreAmmortizzatori) {
		this.flgPercettoreAmmortizzatori = flgPercettoreAmmortizzatori;
	}

	public String getStrMessWsAdesione() {
		return strMessWsAdesione;
	}

	public void setStrMessWsAdesione(String strMessWsAdesione) {
		this.strMessWsAdesione = strMessWsAdesione;
	}

	public Boolean getFlgPresoInCarico() {
		return flgPresoInCarico;
	}

	public void setFlgPresoInCarico(Boolean flgPresoInCarico) {
		this.flgPresoInCarico = flgPresoInCarico;
	}

	public DeProvinciaDTO getDeProvincia() {
		return deProvinciaRif;
	}

	public void setDeProvincia(DeProvinciaDTO deProvinciaRif) {
		this.deProvinciaRif = deProvinciaRif;
	}

	public DeCpiDTO getDeCpiAdesione() {
		return deCpiAdesione;
	}

	public void setDeCpiAdesione(DeCpiDTO deCpiAdesione) {
		this.deCpiAdesione = deCpiAdesione;
	}

	public String getStrMessInterfaccia() {
		return strMessInterfaccia;
	}

	public void setStrMessInterfaccia(String strMessInterfaccia) {
		this.strMessInterfaccia = strMessInterfaccia;
	}
}
