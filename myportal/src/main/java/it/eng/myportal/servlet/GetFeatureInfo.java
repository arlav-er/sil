package it.eng.myportal.servlet;

import it.eng.myportal.utils.ConstantsSingleton;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * recupera l'ID della vacancy a partire dal X e Y della mappa
 * 
 * @author coticone
 *
 */
@WebServlet(urlPatterns = { "/faces/secure/getFeatureInfo" })
public class GetFeatureInfo extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String longitudine = request.getParameter("lon");
		String latitudine = request.getParameter("lat");
		String z = request.getParameter("z");
		String qParameter = request.getParameter("q");
		// ricerca sul campo di tipo location = LatLonType
		// field = punto (latitudine, longitudine)
		// d = raggio in kilometri

		qParameter = it.eng.myportal.utils.URL.escapeChrUrl(qParameter);
		String xmlSolr = documentSOLR(qParameter, latitudine, longitudine, z);

		OutputStreamWriter w = new OutputStreamWriter(response.getOutputStream());
		if (xmlSolr == null) {
			xmlSolr = "";
		}
		w.write(xmlSolr);
		w.flush();
		w.close();
	}

/*
	private String documentSOLR(String url) {
		String jsonSolr = null;
		HttpClient httpClient = new HttpClient();
		// Create a method instance.
		GetMethod method = new GetMethod(url);
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				jsonSolr = IOUtils.toString(new URL(url));
			}

		} catch (HttpException e) {
			jsonSolr = "";
			log.error("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			jsonSolr = "";
			log.error("IOException " + e);
		}

		return jsonSolr;
	}
*/

	private String documentSOLR(String queryString, String latitudine, String longitudine, String z) {
		String jsonSolr = null;
		HttpClient httpClient = new HttpClient();

		// Create a method instance.
		String baseDominio = ConstantsSingleton.getSolrUrl();

		GetMethod method = new GetMethod(baseDominio + "/core0/select/?");
		NameValuePair[] vals = new NameValuePair[7];

		int jj = 0;
		NameValuePair val = new NameValuePair();
		val.setName("q");
		val.setValue(queryString);
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("fl");
		val.setValue("id_va_dati_vacancy,ragione_sociale,attivita_principale,comune,contratto");
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("wt");
		val.setValue("json");
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("fq");
		val.setValue("{!geofilt}");
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("sfield");
		val.setValue("punto");
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("pt");
		val.setValue(latitudine + "," + longitudine);
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("d");

		int zoom = Integer.parseInt(z);
		if (zoom > 10)
			val.setValue(Math.pow(1.2, 21) / Math.pow(1.30, zoom) + "");
		else
			val.setValue(Math.pow(1.2, 21) / Math.pow(1.40, zoom) + "");

		vals[jj++] = val;

		method.setQueryString(vals);
		try {

			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				jsonSolr = IOUtils.toString(method.getResponseBodyAsStream());

			}

		} catch (HttpException e) {
			log.error("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			log.error("IOException " + e);
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return jsonSolr;
	}

}
