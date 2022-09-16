package it.eng.sil.myaccount.model.ejb.stateless.utils;

import java.io.IOException;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chartistjsf.model.chart.BarChartModel;
import org.chartistjsf.model.chart.BarChartSeries;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.sil.myaccount.model.utils.ConstantsSingleton;

/**
 * Lato CLINET di profilatura, recupera informazioni su gruppi/componenti/ecc. in base all'endpoint: se nulla impiega
 * versione locale
 * 
 * Gestione informazioni utente chiama il servizio Rest per il recupero delle informazioni dell'utente
 *
 */
@Stateless
@LocalBean
public class MyAuthServiceRemoteStatsClientEJB {

	private static final int JSON_MAX_SIZE = 1000000;
	protected static final Log log = LogFactory.getLog(MyAuthServiceRemoteStatsClientEJB.class);

	@EJB
	ConstantsSingleton constSingleton;

	private JSONArray getCacheData() throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();

		GetMethod urlAuth = new GetMethod(constSingleton.getMyAuthCacheStatsURL());
		int httpStatus = httpClient.executeMethod(urlAuth);
		String jsonString = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
		urlAuth.releaseConnection();
		if (HttpStatus.SC_OK != httpStatus)
			throw new HttpException("ERRORE getMyAuthCacheStats HTTP_STATUS=" + httpStatus + " : " + jsonString);

		else {
			JSONArray json = new JSONArray(jsonString);

			return json;
		}
	}

	public String getJamonHtml(String columnIndex, String order) throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();

		GetMethod urlAuth = new GetMethod(
				constSingleton.getMyAuthJamonURL() + "?colIdx=" + columnIndex + "&order=" + order);
		int httpStatus = httpClient.executeMethod(urlAuth);
		String htmlData = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
		urlAuth.releaseConnection();
		if (HttpStatus.SC_OK != httpStatus)
			throw new HttpException("ERRORE getJamonHtml HTTP_STATUS=" + httpStatus + " : " + urlAuth);
		else {
			return htmlData;
		}
	}

	public boolean isJamonEnabled() throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();

		GetMethod urlAuth = new GetMethod(constSingleton.getMyAuthJamonURL() + "Status");
		int httpStatus = httpClient.executeMethod(urlAuth);
		String htmlData = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
		urlAuth.releaseConnection();
		if (HttpStatus.SC_OK != httpStatus)
			throw new HttpException("ERRORE isJamonEnabled HTTP_STATUS=" + httpStatus + " : " + htmlData);
		else {
			log.info("JAMON REMOTE Status:" + htmlData);
			return true;
		}
	}

	public String jamonEnable() throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();

		GetMethod urlAuth = new GetMethod(constSingleton.getMyAuthJamonURL() + "Enable");
		int httpStatus = httpClient.executeMethod(urlAuth);
		String htmlData = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
		urlAuth.releaseConnection();
		if (HttpStatus.SC_OK != httpStatus)
			throw new HttpException("ERRORE jamonEnable HTTP_STATUS=" + httpStatus + " : " + htmlData);
		else {
			return htmlData;
		}
	}

	public String jamonReset() throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();

		GetMethod urlAuth = new GetMethod(constSingleton.getMyAuthJamonURL() + "Reset");
		int httpStatus = httpClient.executeMethod(urlAuth);
		String htmlData = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
		urlAuth.releaseConnection();
		if (HttpStatus.SC_OK != httpStatus)
			throw new HttpException("ERRORE jamonEnable HTTP_STATUS=" + httpStatus + " : " + htmlData);
		else {
			return htmlData;
		}
	}

	public String jamonDisable() throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();

		GetMethod urlAuth = new GetMethod(constSingleton.getMyAuthJamonURL() + "Disable");
		int httpStatus = httpClient.executeMethod(urlAuth);
		String htmlData = urlAuth.getResponseBodyAsString(JSON_MAX_SIZE);
		urlAuth.releaseConnection();
		if (HttpStatus.SC_OK != httpStatus)
			throw new HttpException("ERRORE jamonDisable HTTP_STATUS=" + httpStatus + " : " + htmlData);
		else {
			return htmlData;
		}
	}

	public BarChartModel getCacheHitChartData() {
		BarChartModel ret = new BarChartModel();
		try {
			JSONArray rawData = getCacheData();

			BarChartSeries serieHit = new BarChartSeries();
			BarChartSeries serieMiss = new BarChartSeries();
			ArrayList<Number> hitData = new ArrayList<Number>();
			ArrayList<Number> missData = new ArrayList<Number>();

			for (int i = 0; i < rawData.length(); i++) {
				JSONObject cache = (JSONObject) rawData.get(i);
				Long hit = cache.getLong("cacheHit");
				Long miss = cache.getLong("cacheMiss");
				if (hit != 0 || miss != 0) {
					ret.addLabel(cache.getString("name") + " - HIT: " + hit * 100 / (hit + miss) + "%");
					hitData.add(hit);
					missData.add(miss);
				}
			}
			serieHit.setName("Cache HIT");
			serieHit.setData(hitData);
			ret.addSeries(serieHit);

			serieMiss.setName("Cache MISS");
			serieMiss.setData(missData);
			ret.addSeries(serieMiss);

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

}
