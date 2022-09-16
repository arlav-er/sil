package it.eng.myportal.timer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.eng.myportal.entity.ejb.ts.TimerNotFoundException;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.ts.TsTimer;
import it.eng.myportal.utils.ConstantsSingleton;

@Singleton
@LocalBean
public class TimerUpdaterSolrSingleton implements ITimerAmministrato {
	@Resource
	TimerService timerService;
	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;
	protected final Log log = LogFactory.getLog(this.getClass());
	private static final String TIMER_NAME = "updaterSolrTimer";

	@EJB
	TsTimerEJB tsTimerEJB;

	public void setTimer(long intervalDuration) {
		log.debug("Setting a programmatic timeout for " + intervalDuration + " milliseconds from now.");
		Timer timer = timerService.createTimer(intervalDuration, "Created new programmatic timer");
	}

	@Timeout
	public void programmaticTimeout() {
		log.info(
				"===BATCH TimerUpdaterSolrSingleton: timeout MANUALE per SOLR, NON controllo abilitazione TIMER START ===");
		this.setLastProgrammaticTimeout(new Date());
		documentSOLR("false");
		log.info("===BATCH TimerUpdaterSolrSingleton: timeout MANUALE per SOLR, NON controllo abilitazione TIMER ===");
	}

	@Schedule(minute = "0/10", hour = "*")
	public void automaticTimeout() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			this.setLastAutomaticTimeout(new Date());
			documentSOLR("false");

		} else {
			log.info("===Timer TimerUpdaterSolrSingleton disabilitato su questo nodo, NON ESEGUO ");
		}
	}

	public boolean isLogicEnabled() {
		try {
			return tsTimerEJB.isTimerLogicEnabled(TIMER_NAME);
		} catch (TimerNotFoundException e) {
			log.info("Timer non trovato: torno FALSE isEnabledDaTsTimer() per " + TIMER_NAME + e.getMessage());
			return false;
		}

	}

	public boolean setLogicEnabled(boolean operativo) {
		try {
			TsTimer tt = tsTimerEJB.setTimerLogicEnabled(TIMER_NAME, operativo);
			return tt.getFlagAbilitato();
		} catch (TimerNotFoundException e) {
			log.error("GRAVE ERRORE set timer " + TIMER_NAME + e.getMessage());
			return false;
		}

	}

	public String getLastProgrammaticTimeout() {
		if (lastProgrammaticTimeout != null) {
			return lastProgrammaticTimeout.toString();
		} else {
			return "never";
		}
	}

	private void setLastProgrammaticTimeout(Date lastTimeout) {
		this.lastProgrammaticTimeout = lastTimeout;
	}

	public String getLastAutomaticTimeout() {
		if (lastAutomaticTimeout != null) {
			return lastAutomaticTimeout.toString();
		} else {
			return "never";
		}
	}

	private void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
		this.lastAutomaticTimeout = lastAutomaticTimeout;
	}

	/**
	 * permette ll'import dei dati delle vacancy su SOLR nel caso clean = true ripulisce tutti gli indici salvati e
	 * reimporta tutto nel caso clean = false aggiunge i dati
	 * 
	 * @param clean
	 * @return
	 */
	private Document documentSOLR(String clean) {
		Document document = null;
		HttpClient httpClient = new HttpClient();

		// Create a method instance.
		String baseDominio = ConstantsSingleton.getSolrUrl();
		GetMethod method = new GetMethod(baseDominio + "/core0/import?");
		NameValuePair[] vals = new NameValuePair[2];

		int jj = 0;
		NameValuePair val = new NameValuePair();
		val.setName("command");
		val.setValue("full-import");
		vals[jj++] = val;

		val = new NameValuePair();
		val.setName("clean");
		val.setValue(clean);
		vals[jj++] = val;

		method.setQueryString(vals);
		try {

			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = IOUtils.toInputStream(IOUtils.toString(method.getResponseBodyAsStream()));

				Reader reader = new InputStreamReader(is, "UTF-8");
				InputSource inpsource = new InputSource(reader);
				inpsource.setEncoding("UTF-8");

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(inpsource);
			}

		} catch (HttpException e) {
			log.error("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			log.error("IOException " + e);
		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException " + e);
		} catch (SAXException e) {
			log.error("SAXException " + e);
		}

		return document;
	}

}
