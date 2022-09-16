package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeGradoLinDTO;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * Servlet per l'autocomplete dei titoli.
 * Prende in input un termine di ricerca e restituisce la lista dei risultati in formato JSON.
 * @author Pegoraro, Rodi A.
 *
 */
@Stateless
@Path("rest/gradolingua/")
public class GradoLinguaRestSuggestion extends AbstractRestSuggestion<DeGradoLinDTO>  {
	
	@EJB
	transient DeGradoLinHome deGradoLinHome;

	@Override
	protected ISuggestibleHome<DeGradoLinDTO> getHome() {
		return deGradoLinHome;
	}
}
