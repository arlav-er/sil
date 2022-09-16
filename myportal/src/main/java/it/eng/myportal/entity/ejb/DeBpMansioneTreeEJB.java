package it.eng.myportal.entity.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;
import it.eng.myportal.exception.MyPortalException;

@Singleton
@Startup
@LocalBean
public class DeBpMansioneTreeEJB extends DeBpMansioneHome {

	private final static int NUM_LIVELLI = 6;
	
	
	//Mappa i livelli con le mansioni del corrispondente livello
	private Map<Integer, List<DeBpMansione>> deBpMansioneMap;
	private Map<Integer, List<DeBpMansione>> deBpMansioneMapAgricoli;
		
	@PostConstruct
	public void init() {
		try {
			deBpMansioneMap = createLevelMap();
			deBpMansioneMapAgricoli = createLevelMapAgricoli();
		} catch (Exception e) {
			log.error("Errore nella inizializzazione della DeMansione Tree allo startup!");
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Map<Integer, List<DeBpMansione>> getLevelsMap(){
		return deBpMansioneMap;
	}
	
	public Map<Integer, List<DeBpMansione>> getLevelsMapAgricoli(){
		return deBpMansioneMapAgricoli;
	}
	
	public List<DeBpMansione> getFigli(DeBpMansione mansione) throws MyPortalException{
		if(mansione != null && mansione.getCodMansione() != null){
			return findDeBpMansioneFigliById(mansione.getCodMansione());
		} else {
			throw new MyPortalException("getPadre() non può ricevere una mansione nulla");
		}
	}
	
	public DeBpMansione getPadre(DeBpMansione mansione) throws MyPortalException{
		if(mansione != null && mansione.getCodMansione() != null){
			return findDeBpMansionePadreById(mansione.getCodMansione()).getPadre();
		} else {
			throw new MyPortalException("getPadre() non può ricevere una mansione nulla");
		}
	}
	
	public DeBpMansione getPadreAgricolo(DeBpMansione mansione) throws MyPortalException{
		if(mansione != null && mansione.getCodMansione() != null){
			return findDeBpMansionePadreById(mansione.getCodMansione());
		} else {
			throw new MyPortalException("getPadre() non può ricevere una mansione nulla");
		}
	}
	
	//Ritorna le mansioni del livello specificato
	public List<DeBpMansione> getLivello(int livello) throws MyPortalException{
		if(livello > 0 && livello <= NUM_LIVELLI){	
			return deBpMansioneMap.get(livello);
		} else {
			throw new MyPortalException("L'albero delle mansioni ha "+ NUM_LIVELLI + " passato:" + livello);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Map<Integer, List<DeBpMansione>> createLevelMap(){
		Map<Integer, List<DeBpMansione>> mansioniMap = new HashMap<Integer, List<DeBpMansione>>();
		//Liste livelli
		List<List<DeBpMansione>> ListOfList = new ArrayList<List<DeBpMansione>>(); 
		for(int i=0 ; i<NUM_LIVELLI; i++ ){
			ListOfList.add(new ArrayList<DeBpMansione>());
			mansioniMap.put(i+1, ListOfList.get(i));
		}
		
		//Essendo gia ordinata la lista delle mansioni, costruirà ogni livello dopo aver concluso quello precedente
		for(DeBpMansione mansione : findDeBpMansioneForTree()) {
			ListOfList.get(mansione.getLivello()-1).add(mansione);					
		}
		return mansioniMap;
	}
	
	
	private Map<Integer, List<DeBpMansione>> createLevelMapAgricoli(){
		Map<Integer, List<DeBpMansione>> mansioniMap = new HashMap<Integer, List<DeBpMansione>>();
		//Liste livelli
		List<List<DeBpMansione>> ListOfList = new ArrayList<List<DeBpMansione>>(); 
		for(int i=0 ; i<NUM_LIVELLI; i++ ){
			ListOfList.add(new ArrayList<DeBpMansione>());
			mansioniMap.put(i+1, ListOfList.get(i));
		}
		
		//Essendo gia ordinata la lista delle mansioni, costruirà ogni livello dopo aver concluso quello precedente
		DeBpMansione mansioneCurr = null;
		for(DeBpMansione mansione : findDeBpMansioneAgricoliForTree()) {
			ListOfList.get(mansione.getLivello()-1).add(mansione); // 4 digit
			
			mansioneCurr = getPadreAgricolo(mansione);
			if(!ListOfList.get(mansioneCurr.getLivello()-1).contains(mansioneCurr))
			  ListOfList.get(mansioneCurr.getLivello()-1).add(mansioneCurr); // 3 digit
			
			
			mansioneCurr = getPadreAgricolo(mansioneCurr);	
			if(!ListOfList.get(mansioneCurr.getLivello()-1).contains(mansioneCurr))
			  ListOfList.get(mansioneCurr.getLivello()-1).add(mansioneCurr);// 2 digit
			
			mansioneCurr = getPadreAgricolo(mansioneCurr);	
			if(!ListOfList.get(mansioneCurr.getLivello()-1).contains(mansioneCurr))
			  ListOfList.get(mansioneCurr.getLivello()-1).add(mansioneCurr);// 1 digit
				
		}
		return mansioniMap;
	}
/*
	public DeBpMansione estraiPadre(DeBpMansione mansioneCurr, int numlivello){
		mansioneCurr = getPadre(mansione);	
		ListOfList.get(mansione.getLivello()-numlivello).add(mansioneCurr);// 2 digit
	}
*/	
}
