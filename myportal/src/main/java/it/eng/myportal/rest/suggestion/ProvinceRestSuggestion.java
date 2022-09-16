package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
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
@Path("rest/province/")
public class ProvinceRestSuggestion extends AbstractRestSuggestion<DeProvinciaDTO>  {
	
	@EJB
	transient DeProvinciaHome deProvinciaHome;

	@Override
	protected ISuggestibleHome<DeProvinciaDTO> getHome() {
		return deProvinciaHome;
	}
}
