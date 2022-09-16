package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeAreaSilDTO;
import it.eng.myportal.entity.home.decodifiche.DeAreaSilHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

@Stateless
@Path("rest/deAreaSil/")
public class DeAreaSilRestSuggestion extends AbstractRestSuggestion<DeAreaSilDTO> {

	@EJB
	transient DeAreaSilHome deAreaSilHome;

	@Override
	protected ISuggestibleHome<DeAreaSilDTO> getHome() {
		return deAreaSilHome;
	}
}
