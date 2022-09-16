package it.eng.myportal.rest.app;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.helper.AppRequestValidator;
import it.eng.myportal.rest.app.helper.SolrQueryCreator;
import it.eng.myportal.rest.app.helper.SolrQueryCreatorFactory;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * Servlet per recuperare tramite ricerca con APACHE SOLR la lista delle vacancy
 * 
 * Vengono recuperati anche i vari raggruppamenti per i filtri di II livello
 * 
 * @author
 *
 */
@Stateless
@Path("/nocas/app/vacancies2")
public class Vacancies2 {

	protected static Log log = LogFactory.getLog(Vacancies2.class);

	@EJB
	DeComuneHome deComuneHome;

	@POST
	@Path("/list/{tokenSecurity}")
	@Produces("application/json; charset=UTF-8")
	public String index(@PathParam("tokenSecurity") String tokenSecurity, @FormParam("cosa") String cosa,
			@FormParam("dove") String dove, @FormParam("raggruppamenti") boolean getFacet,
			@FormParam("idVaDatiVacancy") String idVaDatiVacancy,
			@FormParam("codMansione") List<String> listaCodMansione,
			@FormParam("codContratto") List<String> listaCodContratto,
			@FormParam("codOrario") List<String> listaCodOrario, @FormParam("codSettore") List<String> listaCodSettore,
			@FormParam("codLingua") List<String> listaCodLingua,
			@FormParam("codTitoloStudio") List<String> listaCodTitoloStudio,
			@FormParam("codPatente") List<String> listaCodPatente, @FormParam("start") String start,
			@FormParam("rows") String rows, @FormParam("dist") String dist, @FormParam("lat") String lat,
			@FormParam("lon") String lon, @DefaultValue("0") @FormParam("ordinamento") String ordinamento) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".index");
		try {
			/*
			 * ------------------------------------- Validazione parametri request -------------------------------------
			 */
			AppRequestValidator validator = new AppRequestValidator();
			// TokenSecurity
			validator.setParam(tokenSecurity);
			validator.checkMaxLengthTokenSecurity().checkPatternAlphaNumNoSpace();
			// Cosa
			validator.setParam(cosa);
			validator.checkMaxLengthCosaDove().checkPatternAlphaNumSpace();
			// Dove
			validator.setParam(dove);
			validator.checkMaxLengthCosaDove().checkPatternAlphaNumSpace();
			// idVaDatiVacancy
			validator.setParam(idVaDatiVacancy);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			// listaCodMansione
			AppRequestValidator validatorColl = new AppRequestValidator();
			validatorColl.setParams(listaCodMansione);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// listaCodContratto
			validatorColl.setParams(listaCodContratto);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// listaCodOrario
			validatorColl.setParams(listaCodOrario);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// listaCodSettore
			validatorColl.setParams(listaCodSettore);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// listaCodLingua
			validatorColl.setParams(listaCodLingua);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// listaCodTitoloStudio
			validatorColl.setParams(listaCodTitoloStudio);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// listaCodPatente
			validatorColl.setParams(listaCodPatente);
			validatorColl.checkMaxLengthCodiceInCollection().checkPatternAlphaNumNoSpaceInCollection();
			// start
			validator.setParam(start);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// rows
			validator.setParam(rows);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// dist
			validator.setParam(dist);
			validator.checkMaxLengthNumeric().checkPatternNumeric();
			// lat
			validator.setParam(lat);
			validator.checkMaxLengthLatLon().checkPatternNumeric();
			// lon
			validator.setParam(lon);
			validator.checkMaxLengthLatLon().checkPatternNumeric();
			// ordinamento
			validator.setParam(ordinamento);
			validator.checkMaxLengthNumeric().checkPatternNumeric();

			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/vacancies2/list/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {

					// Si recuperano le vacancy valide alla data di domani
					Calendar c = Calendar.getInstance();
					//c.add(Calendar.DATE, +1);
					Date vacancyValideAllaData = c.getTime();
					
					SolrQueryCreator creator = SolrQueryCreatorFactory.getSolrQueryCreator(cosa, dove, getFacet, idVaDatiVacancy,
							listaCodMansione, listaCodContratto, listaCodOrario, listaCodSettore, listaCodLingua,
							listaCodTitoloStudio, listaCodPatente, start, rows, dist, lat, lon,
							vacancyValideAllaData /* vacancyValideAllaData */, null /* vacancyModificateAllaData */,
							ordinamento);
					String result = Utils.stringSOLR(creator.getUrlSolr(), creator.createParamsQuerySolr());
					
					String ret = null;
					try {
						ret = creator.parseResultJSON(result);
					} catch (JSONException e) {
						throw new GenericException("Errore nel metodo di recupero della response");
					}
					return ret;
					
				} else {
					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					JSONObject obj = new JSONObject();
					try {
						obj.put("status", "ko");
						obj.put("error", "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}
					return obj.toString();
				}
			} else {
				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				JSONObject obj = new JSONObject();
				try {
					obj.put("status", "ko");
					obj.put("error", "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}
				return obj.toString();
			}
		} catch (AppEjbException e) {
			throw new WebApplicationException(e.createJsonResponse());
		} finally {
			jamonMonitor.stop();
		}
	}
}
