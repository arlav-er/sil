package it.eng.myportal.rest.kiosk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.ejb.stateless.kiosk.KioskEjb;
import it.eng.myportal.rest.app.CheckerSec;
import it.eng.myportal.rest.kiosk.pojo.StampaPercorsoLavoratore;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
@Path("nocas/app/kiosk")
public class Kiosk {
	private static final String MEDIA_TYPE_APPLICATION_PDF = "application/pdf";

	protected static Log log = LogFactory.getLog(Kiosk.class);

	private static final String LAVORATORI_PATH = "/lavoratori/";
	private static final String JSON_GET_STAMPA_PERCORSO_LAV_CONTEXT = "/percorsoLavoratore";
	private static final String ROOT_APP_PATH = "/secure/nocas/app/kiosk";

	@EJB
	private KioskEjb kioskEjb;

	@GET
	@Path(LAVORATORI_PATH + "{codFiscale}" + JSON_GET_STAMPA_PERCORSO_LAV_CONTEXT + "/{token}")
	@Produces(MEDIA_TYPE_APPLICATION_PDF)
	public byte[] percorsoLavoratore(@PathParam("codFiscale") String codFiscale,
			@PathParam("token") String tokenSecurity, @QueryParam("codProvincia") String codProvincia,
			@QueryParam("dataInizio") RestDateParam dataInizio, @QueryParam("dataFine") RestDateParam dataFine) {

		Date dataInizioWithDefault = null;
		Date dataFineWithDefault = null;
		StampaPercorsoLavoratore stampaValida = null;

		try {
			dataInizioWithDefault = getDateDefault(dataInizio, "19000101");
			dataFineWithDefault = getDateDefault(dataFine, new Date());
		} catch (ParseException e) {
			log.error("Errore in creazione date di default");
		}

		CheckerSec checkToken = new CheckerSec(tokenSecurity,
				ROOT_APP_PATH + LAVORATORI_PATH + codFiscale.toUpperCase() + JSON_GET_STAMPA_PERCORSO_LAV_CONTEXT,
				ConstantsSingleton.Kiosk.KEY, true /* useUTC */);

		try {
			if (checkToken.isOk()) {
				List<StampaPercorsoLavoratore> listPercorsoLavoratore = kioskEjb.percorsoLavoratore(codFiscale,
						codProvincia, dataInizioWithDefault, dataFineWithDefault);
				// Recupero percorso lavoratore valido
				stampaValida = percorsoLavoratoreValido(listPercorsoLavoratore);
			} else {
				Response response = createJsonResponse(checkToken.renderTokenSec().toString(),
						Response.Status.BAD_REQUEST);
				throw new WebApplicationException(response);
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			Response response = createJsonResponse(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
			throw new WebApplicationException(response);
		}

		return stampaValida.getPdfContent();
	}

	private static Date getDateDefault(RestDateParam dta, String dateDefault) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		if (dta != null && dta.getDate() != null) {
			return dta.getDate();
		} else {
			return df.parse(dateDefault);
		}
	}

	private static Date getDateDefault(RestDateParam dta, Date dateDefault) throws ParseException {
		if (dta != null && dta.getDate() != null) {
			return dta.getDate();
		} else {
			return dateDefault;
		}
	}

	private Response createJsonResponse(String msg, Response.Status status) {
		ResponseBuilder builder = Response.status(status);
		builder.entity(msg);
		Response response = builder.build();
		return response;
	}

	private StampaPercorsoLavoratore percorsoLavoratoreValido(List<StampaPercorsoLavoratore> listPercorsoLavoratore) {

		if (listPercorsoLavoratore == null || listPercorsoLavoratore.isEmpty()) {
			// Nessun percorso lavoratore trovato: non dovrebbe mai capitare
			throw new WebApplicationException(createJsonResponse(
					"Percorso lavoratore non trovato, rivolgiti al centro per l'Impiego", Response.Status.NOT_FOUND));
		}

		// Liste suddivise per tipologia di ritorno
		List<StampaPercorsoLavoratore> listCountOk = new ArrayList<>();
		List<StampaPercorsoLavoratore> listCountException = new ArrayList<>();
		List<StampaPercorsoLavoratore> listCountEsitoNotFound = new ArrayList<>();
		List<StampaPercorsoLavoratore> listCountEsitoOther = new ArrayList<>();

		for (StampaPercorsoLavoratore stampa : listPercorsoLavoratore) {
			if (stampa.isEsitoPositivo()) {
				listCountOk.add(stampa);
			} else if (stampa.isNotFound())
				listCountEsitoNotFound.add(stampa);
			else if (stampa.getCodEsito() != null && stampa.getCodEsito() > 0)
				listCountEsitoOther.add(stampa);
			else if (stampa.getException() != null)
				listCountException.add(stampa);
		}

		if (!listCountOk.isEmpty()) {
			// Trovato almeno un risultato
			if (listCountOk.size() == 1) {
				// Presente un solo risultato
				return listCountOk.get(0);
			} else {
				// E' presente più di un percorso lavoratore valido
				throw new WebApplicationException(
						createJsonResponse("Errore in fase di stampa, rivolgersi agli sportelli (dati non coerenti)",
								Response.Status.CONFLICT));
			}
		} else if (listCountEsitoNotFound.size() == listPercorsoLavoratore.size()) {
			// Tutti i Sil hanno risposto con Lavoratore non trovato
			throw new WebApplicationException(
					createJsonResponse("Errore in fase di stampa, rivolgersi agli sportelli (lavoratore non trovato)",
							Response.Status.NOT_FOUND));
		} else if (listCountEsitoOther.size() > 0) {
			// Altra tipologia di errore proveniente da SIL

			// Si considera la prima stampa andata in errore
			StampaPercorsoLavoratore err = listCountEsitoOther.get(0);
			String msg = String.format(
					"Errore nella stampa del percorso lavoratore, rivolgersi agli sportelli (Cod. %d, %s)",
					err.getCodEsito(), err.getDescrizione());

			log.error("ERRORE KIOSKO: Provincia " + err.getCodProvincia() + " - " + msg);

			throw new WebApplicationException(createJsonResponse(msg, Response.Status.INTERNAL_SERVER_ERROR));
		} else {
			// Casistica con eccezioni (es. SIL non attivo)

			// Si considera la prima stampa andata in errore
			StampaPercorsoLavoratore err = null;
			if (!listCountException.isEmpty()) {
				err = listCountException.get(0);
			}
			String msg = "Errore nel recupero dei dati per la stampa del percorso lavoratore";

			log.error("ERRORE KIOSKO: " + (err != null ? ("Provincia " + err.getCodProvincia() + " - ") : "") + msg);

			throw new WebApplicationException(createJsonResponse(msg, Response.Status.INTERNAL_SERVER_ERROR));
		}
	}
}
