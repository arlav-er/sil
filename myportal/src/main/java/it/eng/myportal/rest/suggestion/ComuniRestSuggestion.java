package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
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
@Path("rest/comuni/")
public class ComuniRestSuggestion extends AbstractRestSuggestion<DeComuneDTO>  {
	
	@EJB
	transient DeComuneHome deComuneHome;

	@Override
	protected ISuggestibleHome<DeComuneDTO> getHome() {
		return deComuneHome;
	}
}
