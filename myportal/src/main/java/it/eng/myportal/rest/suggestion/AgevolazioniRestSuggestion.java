package it.eng.myportal.rest.suggestion;

import it.eng.myportal.dtos.DeAgevolazioneDTO;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * REST
 * Prende in input un termine di ricerca e restituisce
 * la lista dei risultati in formato JSON.
 * 
 * 
 * @author Rodi A.
 *
 */
@Stateless
@Path("rest/agevolazioni/")
public class AgevolazioniRestSuggestion extends AbstractRestSuggestion<DeAgevolazioneDTO>
{
	
	protected static Log log = LogFactory.getLog(AgevolazioniRestSuggestion.class);
	
	@EJB
	transient DeAgevolazioneHome deAgevolazioneHome;

	@Override
	protected ISuggestibleHome<DeAgevolazioneDTO> getHome() {		
		return deAgevolazioneHome;
	}
	
}
