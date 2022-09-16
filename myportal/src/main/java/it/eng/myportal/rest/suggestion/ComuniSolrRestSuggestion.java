package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.entity.home.decodifiche.DeComuneSolrHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

@Stateless
@Path("rest/comuniSolr/")
public class ComuniSolrRestSuggestion extends AbstractRestSuggestion<DeComuneDTO> {

	@EJB
	transient DeComuneSolrHome deComuneSolrHome;

	@Override
	protected ISuggestibleHome<DeComuneDTO> getHome() {
		return deComuneSolrHome;
	}

}
