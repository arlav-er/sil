package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeAmbitoDisciplinareDTO;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDisciplinareHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * Servlet per l'autocomplete di DeAmbitoDisciplinare.
 * 
 * @author gicozza
 */
@Stateless
@Path("rest/deAmbitoDisciplinare/")
public class DeAmbitoDisciplinareRestSuggestion extends AbstractRestSuggestion<DeAmbitoDisciplinareDTO> {

	@EJB
	transient DeAmbitoDisciplinareHome deAmbitoDisciplinareHome;

	@Override
	protected ISuggestibleHome<DeAmbitoDisciplinareDTO> getHome() {
		return deAmbitoDisciplinareHome;
	}

}
