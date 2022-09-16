/**
 * 
 */
package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author iescone
 *
 * DTO utile per la gestione dei gruppi di filtro scaturito a seguito di una ricerca delle offerte di lavoro 
 */
public class RvGroupDTO implements Serializable {

	private static final long serialVersionUID = 422802301371744360L;

	private Map<String,String> mapMansione;
	private Map<String,String> mapContratto;
	private Map<String,String> mapOrario;
	private Map<String,String> mapEsperienza;
	private Map<String,String> mapSettore;
	private Map<String,String> mapLingua;
	private Map<String,String> mapTitoloStudio;
	private Map<String,String> mapDispTrasferte;
	private Map<String,String> mapPatente;
	private Map<String,String> mapAgricolo;

	private List<String> listAgricolo;
	private List<String> listMansione;
	private List<String> listContratto;
	private List<String> listOrario;
	private List<String> listEsperienza;
	private List<String> listSettore;
	private List<String> listLingua;
	private List<String> listTitoloStudio;
	private List<String> listDispTrasferte;
	private List<String> listPatente;
	
	private Map<String,Boolean> checkMansione = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkContratto = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkOrario = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkEsperienza = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkSettore = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkLingua = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkTitoloStudio = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkDispTrasferte = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkPatente = new HashMap<String,Boolean>();
	private Map<String,Boolean> checkAgricolo = new HashMap<String,Boolean>();
	
	
	public List<String> getSelectedMansioni() {
		List<String> selected = new ArrayList<String>();
		for (String mansione : checkMansione.keySet()) {
			if (checkMansione.get(mansione))
				selected.add(mansione);
		}
		return selected;
	}
	
	public List<String> getAvailableMansioni() {
		return listMansione;
	}
		
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Map<String, Boolean> getCheckMansione() {
		return checkMansione;
	}
	public void setCheckMansione(Map<String, Boolean> checkMansione) {
		this.checkMansione = checkMansione;
	}
	public Map<String, Boolean> getCheckContratto() {
		return checkContratto;
	}
	public void setCheckContratto(Map<String, Boolean> checkContratto) {
		this.checkContratto = checkContratto;
	}
	public Map<String, Boolean> getCheckOrario() {
		return checkOrario;
	}
	public void setCheckOrario(Map<String, Boolean> checkOrario) {
		this.checkOrario = checkOrario;
	}
	public Map<String, Boolean> getCheckEsperienza() {
		return checkEsperienza;
	}
	public void setCheckEsperienza(Map<String, Boolean> checkEsperienza) {
		this.checkEsperienza = checkEsperienza;
	}
	public Map<String, Boolean> getCheckSettore() {
		return checkSettore;
	}
	public void setCheckSettore(Map<String, Boolean> checkSettore) {
		this.checkSettore = checkSettore;
	}
	public Map<String, Boolean> getCheckLingua() {
		return checkLingua;
	}
	public void setCheckLingua(Map<String, Boolean> checkLingua) {
		this.checkLingua = checkLingua;
	}
	public Map<String, Boolean> getCheckTitoloStudio() {
		return checkTitoloStudio;
	}
	public void setCheckTitoloStudio(Map<String, Boolean> checkTitoloStudio) {
		this.checkTitoloStudio = checkTitoloStudio;
	}
	public Map<String, Boolean> getCheckDispTrasferte() {
		return checkDispTrasferte;
	}
	public void setCheckDispTrasferte(Map<String, Boolean> checkDispTrasferte) {
		this.checkDispTrasferte = checkDispTrasferte;
	}
	public Map<String, Boolean> getCheckPatente() {
		return checkPatente;
	}
	public void setCheckPatente(Map<String, Boolean> checkPatente) {
		this.checkPatente = checkPatente;
	}
	public Map<String, String> getMapMansione() {
		return mapMansione;
	}
	public void setMapMansione(Map<String, String> mapMansione) {
		this.mapMansione = mapMansione;
	}
	public Map<String, String> getMapContratto() {
		return mapContratto;
	}
	public void setMapContratto(Map<String, String> mapContratto) {
		this.mapContratto = mapContratto;
	}
	public Map<String, String> getMapOrario() {
		return mapOrario;
	}
	public void setMapOrario(Map<String, String> mapOrario) {
		this.mapOrario = mapOrario;
	}
	public Map<String, String> getMapEsperienza() {
		return mapEsperienza;
	}
	public void setMapEsperienza(Map<String, String> mapEsperienza) {
		this.mapEsperienza = mapEsperienza;
	}
	public Map<String, String> getMapSettore() {
		return mapSettore;
	}
	public void setMapSettore(Map<String, String> mapSettore) {
		this.mapSettore = mapSettore;
	}
	public Map<String, String> getMapLingua() {
		return mapLingua;
	}
	public void setMapLingua(Map<String, String> mapLingua) {
		this.mapLingua = mapLingua;
	}
	public Map<String, String> getMapTitoloStudio() {
		return mapTitoloStudio;
	}
	public void setMapTitoloStudio(Map<String, String> mapTitoloStudio) {
		this.mapTitoloStudio = mapTitoloStudio;
	}
	public Map<String, String> getMapDispTrasferte() {
		return mapDispTrasferte;
	}
	public void setMapDispTrasferte(Map<String, String> mapDispTrasferte) {
		this.mapDispTrasferte = mapDispTrasferte;
	}
	public Map<String, String> getMapPatente() {
		return mapPatente;
	}
	public void setMapPatente(Map<String, String> mapPatente) {
		this.mapPatente = mapPatente;
	}
	public List<String> getListMansione() {
		return listMansione;
	}
	public void setListMansione(List<String> listMansione) {
		this.listMansione = listMansione;
	}
	public List<String> getListContratto() {
		return listContratto;
	}
	public void setListContratto(List<String> listContratto) {
		this.listContratto = listContratto;
	}
	public List<String> getListOrario() {
		return listOrario;
	}
	public void setListOrario(List<String> listOrario) {
		this.listOrario = listOrario;
	}
	public List<String> getListEsperienza() {
		return listEsperienza;
	}
	public void setListEsperienza(List<String> listEsperienza) {
		this.listEsperienza = listEsperienza;
	}
	public List<String> getListSettore() {
		return listSettore;
	}
	public void setListSettore(List<String> listSettore) {
		this.listSettore = listSettore;
	}
	public List<String> getListLingua() {
		return listLingua;
	}
	public void setListLingua(List<String> listLingua) {
		this.listLingua = listLingua;
	}
	public List<String> getListTitoloStudio() {
		return listTitoloStudio;
	}
	public void setListTitoloStudio(List<String> listTitoloStudio) {
		this.listTitoloStudio = listTitoloStudio;
	}
	public List<String> getListDispTrasferte() {
		return listDispTrasferte;
	}
	public void setListDispTrasferte(List<String> listDispTrasferte) {
		this.listDispTrasferte = listDispTrasferte;
	}
	public List<String> getListPatente() {
		return listPatente;
	}
	public void setListPatente(List<String> listPatente) {
		this.listPatente = listPatente;
	}

	public List<String> getListAgricolo() {
		return listAgricolo;
	}

	public void setListAgricolo(List<String> listAgricolo) {
		this.listAgricolo = listAgricolo;
	}

	public Map<String,Boolean> getCheckAgricolo() {
		return checkAgricolo;
	}

	public void setCheckAgricolo(Map<String,Boolean> checkAgricolo) {
		this.checkAgricolo = checkAgricolo;
	}

	public Map<String,String> getMapAgricolo() {
		return mapAgricolo;
	}

	public void setMapAgricolo(Map<String,String> mapAgricolo) {
		this.mapAgricolo = mapAgricolo;
	}

}
