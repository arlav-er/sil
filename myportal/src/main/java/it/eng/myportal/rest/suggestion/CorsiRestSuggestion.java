package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeCorsoDTO;
import it.eng.myportal.entity.home.decodifiche.DeCorsoHome;
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
 * @author Pegoraro A., Rodi A.
 *
 */
@Stateless
@Path("rest/corsi/")
public class CorsiRestSuggestion extends AbstractRestSuggestion<DeCorsoDTO> {

	
	@EJB
	transient DeCorsoHome deCorsoHome;

	@Override
	protected ISuggestibleHome<DeCorsoDTO> getHome() {
		return deCorsoHome;
	}

	
	
}
