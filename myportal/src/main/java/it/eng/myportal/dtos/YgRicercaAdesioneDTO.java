package it.eng.myportal.dtos;

import it.eng.myportal.enums.YgAssegnazioneProvincia;
import it.eng.myportal.enums.YgRegioneRifNotifica;
import it.eng.myportal.enums.YgStatoPresaCarico;
import it.eng.myportal.enums.YgTipoAdesione;
import it.eng.myportal.enums.YgTipoRicercaAdesione;
import it.eng.myportal.utils.yg.YgConstants;

import java.util.Date;

public class YgRicercaAdesioneDTO implements IDTO {

	private static final long serialVersionUID = -6953856694366872888L;

	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String email;
	private Date dataAdesioneDa;
	private Date dataAdesioneA;
	private Date dataFineStatoAdesioneDa;
	private Date dataFineStatoAdesioneA;
	private DeCpiDTO cpiAdesione;
	private String id;
	private int maxResults;
	private int startResultsFrom;
	private YgTipoRicercaAdesione tipoRicerca = YgTipoRicercaAdesione.INIZIA_PER;
	private YgTipoAdesione tipoAdesione = YgTipoAdesione.TUTTI;
	private YgStatoPresaCarico statoPresaCarico = YgStatoPresaCarico.TUTTI;
	private YgRegioneRifNotifica regioneRifNotifica = YgRegioneRifNotifica.TUTTE;
	private DeStatoAdesioneDTO statoAdesione;
	private DeStatoAdesioneMinDTO statoAdesioneMin;
	private YgAssegnazioneProvincia assegnazioneProvincia = YgAssegnazioneProvincia.TUTTE;
	/*
	 * il numero di pagina in paginazione, serve per il pulsante
	 * "torna indietro"
	 */
	private int page;

	private static int NUMERO_RIGHE_VISUALIZZATE = 10;

	public YgRicercaAdesioneDTO() {
		super();

		maxResults = NUMERO_RIGHE_VISUALIZZATE;
		startResultsFrom = 0;
		page = 1;

		cpiAdesione = new DeCpiDTO();
		statoAdesione = new DeStatoAdesioneDTO();
		statoAdesioneMin = new DeStatoAdesioneMinDTO();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataAdesioneDa() {
		return dataAdesioneDa;
	}

	public void setDataAdesioneDa(Date dataAdesioneDa) {
		this.dataAdesioneDa = dataAdesioneDa;
	}

	public Date getDataAdesioneA() {
		return dataAdesioneA;
	}

	public void setDataAdesioneA(Date dataAdesioneA) {
		this.dataAdesioneA = dataAdesioneA;
	}

	public YgTipoRicercaAdesione getTipoRicerca() {
		return tipoRicerca;
	}

	public void setTipoRicerca(YgTipoRicercaAdesione tipoRicerca) {
		this.tipoRicerca = tipoRicerca;
	}

	public YgTipoAdesione getTipoAdesione() {
		return tipoAdesione;
	}

	public void setTipoAdesione(YgTipoAdesione tipoAdesione) {
		this.tipoAdesione = tipoAdesione;
	}

	public YgStatoPresaCarico getStatoPresaCarico() {
		return statoPresaCarico;
	}

	public void setStatoPresaCarico(YgStatoPresaCarico statoPresaCarico) {
		this.statoPresaCarico = statoPresaCarico;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getStartResultsFrom() {
		return startResultsFrom;
	}

	public void setStartResultsFrom(int startResultsFrom) {
		this.startResultsFrom = startResultsFrom;
	}

	public YgRegioneRifNotifica getRegioneRifNotifica() {
		return regioneRifNotifica;
	}

	public void setRegioneRifNotifica(YgRegioneRifNotifica regioneRifNotifica) {
		this.regioneRifNotifica = regioneRifNotifica;
	}

	public DeCpiDTO getCpiAdesione() {
		return cpiAdesione;
	}

	public void setCpiAdesione(DeCpiDTO cpiAdesione) {
		this.cpiAdesione = cpiAdesione;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDataFineStatoAdesioneDa() {
		return dataFineStatoAdesioneDa;
	}

	public void setDataFineStatoAdesioneDa(Date dataFineStatoAdesioneDa) {
		this.dataFineStatoAdesioneDa = dataFineStatoAdesioneDa;
	}

	public Date getDataFineStatoAdesioneA() {
		return dataFineStatoAdesioneA;
	}

	public void setDataFineStatoAdesioneA(Date dataFineStatoAdesioneA) {
		this.dataFineStatoAdesioneA = dataFineStatoAdesioneA;
	}

	public DeStatoAdesioneDTO getStatoAdesione() {
		return statoAdesione;
	}

	public void setStatoAdesione(DeStatoAdesioneDTO statoAdesione) {
		this.statoAdesione = statoAdesione;
	}

	@Override
	public YgRicercaAdesioneDTO clone() {
		YgRicercaAdesioneDTO res = new YgRicercaAdesioneDTO();

		res.setNome(this.getNome());
		res.setCognome(this.getCognome());
		res.setCodiceFiscale(this.getCodiceFiscale());
		res.setEmail(this.getEmail());
		res.setDataAdesioneDa(this.getDataAdesioneDa());
		res.setDataAdesioneA(this.getDataAdesioneA());
		res.setDataFineStatoAdesioneDa(this.getDataFineStatoAdesioneDa());
		res.setDataFineStatoAdesioneA(this.getDataFineStatoAdesioneA());
		res.setMaxResults(this.getMaxResults());
		res.setStartResultsFrom(this.getStartResultsFrom());
		res.setTipoRicerca(this.getTipoRicerca());
		res.setTipoAdesione(this.getTipoAdesione());
		res.setStatoPresaCarico(this.getStatoPresaCarico());
		res.setRegioneRifNotifica(this.getRegioneRifNotifica());
		res.setStatoAdesione(this.getStatoAdesione());
		res.setAssegnazioneProvincia(this.getAssegnazioneProvincia());
		res.setCpiAdesione(this.getCpiAdesione());
		res.setStatoAdesioneMin(this.getStatoAdesioneMin());
		return res;
	}

	public YgAssegnazioneProvincia getAssegnazioneProvincia() {
		return assegnazioneProvincia;
	}

	public void setAssegnazioneProvincia(YgAssegnazioneProvincia assegnazioneProvincia) {
		this.assegnazioneProvincia = assegnazioneProvincia;
	}

	public String getCodSenzaStato() {
		return YgConstants.COD_FILTRO_RICERCA_ADESIONE_SENZA_STATO;
	}

	public String getLabelSenzaStato() {
		return YgConstants.LABEL_FILTRO_RICERCA_ADESIONE_SENZA_STATO;
	}

	public DeStatoAdesioneMinDTO getStatoAdesioneMin() {
		return statoAdesioneMin;
	}

	public void setStatoAdesioneMin(DeStatoAdesioneMinDTO statoAdesioneMin) {
		this.statoAdesioneMin = statoAdesioneMin;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
