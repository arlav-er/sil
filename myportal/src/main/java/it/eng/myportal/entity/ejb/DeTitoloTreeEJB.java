package it.eng.myportal.entity.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.exception.MyPortalException;

@Singleton
@Startup
@LocalBean
public class DeTitoloTreeEJB extends DeTitoloHome {
	//sarebbero 4, ma potiamo le foglie
	private final static int NUM_LIVELLI = 4;
	
	
	//Mappa i livelli con le mansioni del corrispondente livello
	private Map<Integer, List<DeTitolo>> deTitoloMap;
		
	@PostConstruct
	public void init() {
		try {
			deTitoloMap = createLevelMap();
		} catch (Exception e) {
			log.error("Errore nella inizializzazione della DeTitolo Tree allo startup!");
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Map<Integer, List<DeTitolo>> getLevelsMap(){
		return deTitoloMap;
	}
	
		
	public List<DeTitolo> getFigli(DeTitolo mansione) throws MyPortalException{
		if(mansione != null && mansione.getCodTitolo() != null){
			return findDeTitoloFigliById(mansione.getCodTitolo());
		} else {
			throw new MyPortalException("getPadre() non può ricevere una mansione nulla");
		}
	}
	
	public DeTitolo getPadre(DeTitolo mansione) throws MyPortalException{
		if(mansione != null && mansione.getCodTitolo() != null){
			return findDeTitoloPadreById(mansione.getCodTitolo()).getPadre();
		} else {
			throw new MyPortalException("getPadre() non può ricevere una mansione nulla");
		}
	}
	
	//Ritorna i titoli del livello specificato, da 1 a 3
	public List<DeTitolo> getLivello(int livello) throws MyPortalException{
		if(livello > 0 && livello <= NUM_LIVELLI){	
			return deTitoloMap.get(livello);
		} else {
			throw new MyPortalException("L'albero delle mansioni ha "+ NUM_LIVELLI + " passato:" + livello);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Map<Integer, List<DeTitolo>> createLevelMap(){
		Map<Integer, List<DeTitolo>> mansioniMap = new HashMap<Integer, List<DeTitolo>>();
		//Liste livelli
		List<List<DeTitolo>> ListOfList = new ArrayList<List<DeTitolo>>(); 
		for(int i=0 ; i<NUM_LIVELLI; i++ ){
			ListOfList.add(new ArrayList<DeTitolo>());
			mansioniMap.put(i+1, ListOfList.get(i));
		}
		
		//Essendo gia ordinata la lista delle mansioni, costruirà ogni livello dopo aver concluso quello precedente
		for(DeTitolo titStudio : findDeTitoloForTree(true)) {
			int compLivello = titStudio.getLivello();
			ListOfList.get(compLivello-1).add(titStudio);					
		}
		return mansioniMap;
	}

}
