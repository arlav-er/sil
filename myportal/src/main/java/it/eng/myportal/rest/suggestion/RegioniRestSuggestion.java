package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
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
@Path("rest/regioni/")
public class RegioniRestSuggestion extends AbstractRestSuggestion<DeRegioneDTO>  {
	
	@EJB
	transient DeRegioneHome deRegioneHome;

	@Override
	protected ISuggestibleHome<DeRegioneDTO> getHome() {
		return deRegioneHome;
	}
}
