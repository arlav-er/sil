package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO per i filtri delle ricerche. Può essere usato ancher come elemento della lista delle ricerche.
 * 
 * @author Rodi A.
 * 
 */
public class RvTestataDTO extends AbstractUpdatablePkDTO implements IUpdatable, Serializable {

	private static final long serialVersionUID = -8529104120252922272L;
	private Integer idPfPrincipal;
	private String codProvincia;
	private String strProvincia;
	private String codComune;
	private String strComune;
	private String codRegione;
	private String strRegione;
	private String codMansione;
	private String strMansione;
	private String descrizione;
	private String cosa;
	private String dove;

	private Set<String> setMansione;
	private Set<String> setContratto;
	private Set<String> setOrario;
	private Set<String> setEsperienza;
	private Set<String> setSettore;
	private Set<String> setLingua;
	private Set<String> setTitoloStudio;
	private Set<String> setDispTrasferte;
	private Set<String> setPatente;
	private Set<String> setAgricolo;

	private Integer raggioRicerca;
	/* Indica il numero totale di righe in base ai filtri selezionati */
	private int rowsTotal;
	/* Indica le righe caricate dopo la ricerca */
	private int rowsLoaded;
	/* Indica il numero di righe da caricare dopo aver premuto il pulsante Mostra Altri */
	private int rowsForLoad = 10;
	/* Indica il numero di righe parziali da caricare */
	private int rowsToLoad = 10;
	/* Indica il numero di righe nuove trovate */
	private int rowsNew = 0;

	private int currentPage = 1;

	/*
	 * Indica il filtro utilizzato da SOLR viene usato solo per la mappa in home page
	 */
	private String urlFiltroSolr = "";

	public RvTestataDTO() {
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idUtente) {
		this.idPfPrincipal = idUtente;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getRaggioRicerca() {
		return raggioRicerca;
	}

	public void setRaggioRicerca(Integer raggioRicerca) {
		this.raggioRicerca = raggioRicerca;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getCodComune() {
		return codComune;
	}

	public void setCodComune(String codComune) {
		this.codComune = codComune;
	}

	public String getCodRegione() {
		return codRegione;
	}

	public void setCodRegione(String codRegione) {
		this.codRegione = codRegione;
	}

	public String getCodMansione() {
		return codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	public String getStrProvincia() {
		return strProvincia;
	}

	public void setStrProvincia(String strProvincia) {
		this.strProvincia = strProvincia;
	}

	public String getStrComune() {
		return strComune;
	}

	public void setStrComune(String strComune) {
		this.strComune = strComune;
	}

	public String getStrRegione() {
		return strRegione;
	}

	public void setStrRegione(String strRegione) {
		this.strRegione = strRegione;
	}

	public String getStrMansione() {
		return strMansione;
	}

	public void setStrMansione(String strMansione) {
		this.strMansione = strMansione;
	}

	/**
	 * Restituisce true se è stato settato almeno un filtro geografico: comune, provincia o azienda.
	 * 
	 * @return se è stato importato un filtro geografico
	 */
	public boolean hasGeograficalFilter() {
		return (codComune != null && !codComune.isEmpty()) || (codProvincia != null && !codProvincia.isEmpty())
				|| (codRegione != null && !codRegione.isEmpty());
	}

	public int getRowsLoaded() {
		return rowsLoaded;
	}

	public void setRowsLoaded(int rowsLoaded) {
		this.rowsLoaded = rowsLoaded;
	}

	public int getRowsForLoad() {
		return rowsForLoad;
	}

	public void setRowsForLoad(int rowsForLoad) {
		this.rowsForLoad = rowsForLoad;
	}

	public int getRowsToLoad() {
		return rowsToLoad;
	}

	public void setRowsToLoad(int rowsToLoad) {
		this.rowsToLoad = rowsToLoad;
	}

	public int getRowsTotal() {
		return rowsTotal;
	}

	public void setRowsTotal(int rowsTotal) {
		this.rowsTotal = rowsTotal;
	}

	public String getCosa() {
		return cosa;
	}

	public void setCosa(String cosa) {
		this.cosa = cosa;
	}

	public String getDove() {
		return dove;
	}

	public void setDove(String dove) {
		this.dove = dove;
	}

	public Set<String> getSetMansione() {
		return setMansione;
	}

	public void setSetMansione(Set<String> setMansione) {
		this.setMansione = setMansione;
	}

	public Set<String> getSetContratto() {
		return setContratto;
	}

	public void setSetContratto(Set<String> setContratto) {
		this.setContratto = setContratto;
	}

	public Set<String> getSetOrario() {
		return setOrario;
	}

	public void setSetOrario(Set<String> setOrario) {
		this.setOrario = setOrario;
	}

	public Set<String> getSetEsperienza() {
		return setEsperienza;
	}

	public void setSetEsperienza(Set<String> setEsperienza) {
		this.setEsperienza = setEsperienza;
	}

	public Set<String> getSetSettore() {
		return setSettore;
	}

	public void setSetSettore(Set<String> setSettore) {
		this.setSettore = setSettore;
	}

	public Set<String> getSetLingua() {
		return setLingua;
	}

	public void setSetLingua(Set<String> setLingua) {
		this.setLingua = setLingua;
	}

	public Set<String> getSetTitoloStudio() {
		return setTitoloStudio;
	}

	public void setSetTitoloStudio(Set<String> setTitoloStudio) {
		this.setTitoloStudio = setTitoloStudio;
	}

	public Set<String> getSetDispTrasferte() {
		return setDispTrasferte;
	}

	public void setSetDispTrasferte(Set<String> setDispTrasferte) {
		this.setDispTrasferte = setDispTrasferte;
	}

	public Set<String> getSetPatente() {
		return setPatente;
	}

	public void setSetPatente(Set<String> setPatente) {
		this.setPatente = setPatente;
	}

	/**
	 * @return the rowsNew
	 */
	public int getRowsNew() {
		return rowsNew;
	}

	/**
	 * @param rowsNew
	 *            the rowsNew to set
	 */
	public void setRowsNew(int rowsNew) {
		this.rowsNew = rowsNew;
	}

	public String getUrlFiltroSolr() {
		return urlFiltroSolr;
	}

	public void setUrlFiltroSolr(String urlFiltroSolr) {
		this.urlFiltroSolr = urlFiltroSolr;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public Set<String> getSetAgricolo() {
		return setAgricolo;
	}

	public void setSetAgricolo(Set<String> setAgricolo) {
		this.setAgricolo = setAgricolo;
	}
}
