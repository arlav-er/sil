package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * REST
 * Prende in input un termine di ricerca e restituisce
 * la lista dei risultati in formato JSON.
 * 
 * Valutare un helper per la trasformazione in String del risultato
 * 
 * @author Turrini
 *
 */
@Stateless
@Path("rest/cittadinanza/")
public class CittadinanzaRestSuggestion extends AbstractRestSuggestion<DeCittadinanzaDTO> {

	
	@EJB
	transient DeCittadinanzaHome deCittadinanzaHome;

	@Override
	protected ISuggestibleHome<DeCittadinanzaDTO> getHome() {
		return deCittadinanzaHome;
	}

	
	
}
