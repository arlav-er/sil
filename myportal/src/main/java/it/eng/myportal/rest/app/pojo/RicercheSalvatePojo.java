package it.eng.myportal.rest.app.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RicercheSalvatePojo implements Serializable {

	private static final long serialVersionUID = -6775689015608008378L;

	private String id;
	private String descrizione;

	private String cosa;
	private String dove;

	private String dtRicerca;

	private List<String> codContratto = new ArrayList<String>();
	private List<String> codLingua = new ArrayList<String>();
	private List<String> codMansione = new ArrayList<String>();
	private List<String> codOrario = new ArrayList<String>();
	private List<String> codPatente = new ArrayList<String>();
	private List<String> codSettore = new ArrayList<String>();
	private List<String> codTitoloStudio = new ArrayList<String>();

	public RicercheSalvatePojo() {
		super();
	}

	public RicercheSalvatePojo(String descrizione, String cosa, String dove, List<String> codContratto,
			List<String> codLingua, List<String> codMansione, List<String> codOrario, List<String> codPatente,
			List<String> codSettore, List<String> codTitoloStudio) {
		super();
		this.descrizione = descrizione;
		this.cosa = cosa;
		this.dove = dove;
		this.codContratto = codContratto;
		this.codLingua = codLingua;
		this.codMansione = codMansione;
		this.codOrario = codOrario;
		this.codPatente = codPatente;
		this.codSettore = codSettore;
		this.codTitoloStudio = codTitoloStudio;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
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

	public String getDtRicerca() {
		return dtRicerca;
	}

	public void setDtRicerca(String dtRicerca) {
		this.dtRicerca = dtRicerca;
	}

	public List<String> getCodContratto() {
		return codContratto;
	}

	public void setCodContratto(List<String> codContratto) {
		this.codContratto = codContratto;
	}

	public List<String> getCodLingua() {
		return codLingua;
	}

	public void setCodLingua(List<String> codLingua) {
		this.codLingua = codLingua;
	}

	public List<String> getCodMansione() {
		return codMansione;
	}

	public void setCodMansione(List<String> codMansione) {
		this.codMansione = codMansione;
	}

	public List<String> getCodOrario() {
		return codOrario;
	}

	public void setCodOrario(List<String> codOrario) {
		this.codOrario = codOrario;
	}

	public List<String> getCodPatente() {
		return codPatente;
	}

	public void setCodPatente(List<String> codPatente) {
		this.codPatente = codPatente;
	}

	public List<String> getCodSettore() {
		return codSettore;
	}

	public void setCodSettore(List<String> codSettore) {
		this.codSettore = codSettore;
	}

	public List<String> getCodTitoloStudio() {
		return codTitoloStudio;
	}

	public void setCodTitoloStudio(List<String> codTitoloStudio) {
		this.codTitoloStudio = codTitoloStudio;
	}
}
