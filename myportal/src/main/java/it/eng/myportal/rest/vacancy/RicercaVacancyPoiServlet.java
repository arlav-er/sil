package it.eng.myportal.rest.vacancy;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Servlet per recuperare tramite ricerca con APACHE SOLR la lista delel vacancy
 * 
 * vengono recuperati anche i vari raggruppamenti per i filtri di II livello
 * 
 * @author
 *
 */
@WebServlet(asyncSupported = false, name = "RicercaVacancyPoiServlet", urlPatterns = { "/ricercaVacancyPoi" })
public class RicercaVacancyPoiServlet extends HttpServlet {
	private static final long serialVersionUID = 2369224145329958397L;

	protected Log log = LogFactory.getLog(this.getClass());

	protected String urlSolr = ConstantsSingleton.getSolrUrl() + "/core0/select/?";


	/**
	 * Servlet esposta
	 * 
	 * @param par
	 *            String
	 * @return String
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/csv");
		resp.setCharacterEncoding("UTF-8");

		String xmlLista = "";
		RvTestataDTO filter = null;

		HttpSession session = req.getSession(true);
		Object objFilter = session.getAttribute("FILTRI_RICERCA");
		if (objFilter != null) {
			filter = (RvTestataDTO) objFilter;
		} else {
			filter = new RvTestataDTO();
		}

		xmlLista = findDTOByFilter(filter);
		resp.getWriter().write(xmlLista);
	}

	/**
	 * Servizio che permette di ricercare le Vacancy presenti a sistema. Prende in input un filtro di ricerca e
	 * restituisce la lista delle offerte di lavoro che corrispondo ai parametri.
	 *
	 * @param filter
	 *            il filtro di ricerca
	 * @return la lista dei risultati.
	 */
	public String findDTOByFilter(RvTestataDTO filter) {
		String retXml = "";
		String filtro = "";
		/* aggiungo il filtro sulla mansione */
		if (filter.getStrMansione() != null && !filter.getStrMansione().isEmpty()) {
			/* aggiungo il codice mansione nella clausola WHERE */
			filtro = filter.getStrMansione() + "*";
		} else {
			filtro = "*%3A*";
		}
		/* aggiungo il filtro geografico (comune, provincia o regione) */
		if (filter.hasGeograficalFilter()) {
			/* i tre filtri geografici sono mutualmente esclusivi */
			if (filter.getStrComune() != null && !filter.getStrComune().isEmpty()) {
				/* aggiungo il codice comune nella clausola WHERE */
				filtro += "+AND+dove%3A" + filter.getStrComune();
			} else if (filter.getStrProvincia() != null && !filter.getStrProvincia().isEmpty()) {
				/* aggiungo il codice provincia nella clausola WHERE */
				filtro += "+AND+dove%3A" + filter.getStrProvincia();
			} else if (filter.getStrRegione() != null && !filter.getStrRegione().isEmpty()) {
				/* aggiungo il codice regione nella clausola WHERE */
				filtro += "+AND+dove%3A" + filter.getStrRegione();
			}
		}

		try {
			URL url = new URL(urlSolr + "q=" + filtro
					+ "&start=0&rows=10000000&fl=descrizione,longitudine,latitudine&wt=csv");
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type", "text-plain; charset=UTF-8");
			httpURLConnection.setRequestProperty("Accept", "text-plain, text/csv");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.connect();

			InputStream is = httpURLConnection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			retXml = sb.toString();

			httpURLConnection.disconnect();

		} catch (MalformedURLException e) {
			log.error("MalformedURLException " + e);
		} catch (IOException e) {
			log.error("IOException " + e);
		}

		return retXml;
	}

}
