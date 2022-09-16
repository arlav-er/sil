package it.eng.sil.myaccount.controller.mbeans;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.enums.OTPRequestEnum;
import it.eng.sil.myaccount.model.ejb.stateless.utils.UtilsEJB;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeRegione;
import it.eng.sil.mycas.model.manager.decodifiche.DeRegioneEJB;
import it.eng.sil.mycas.utils.CodiceAmministrativoUtil;

/**
 *
 * ManagedBean JSF per contenere i dati dell'utente che ha effettuato l'accesso al sistema. Il Bean viene creato una
 * sola volta per ogni sessione e ricava tutte le informazioni utili sull'utente connesso.
 */
@ManagedBean(name = "utils")
@ApplicationScoped
public class UtilsBean {

	protected static Log log = LogFactory.getLog(UtilsBean.class);
	// timestamp di quando è stata inizializzata l'applicazione
	private long starttime;

	private Properties errorProperties;

	@EJB
	ConstantsSingleton costanti;

	@EJB
	UtilsEJB utilsEJB;

	@EJB
	DeRegioneEJB deRegioneEJB;

	// private List<SelectItem> tipoComunicazione;

	@PostConstruct
	public void postConstruct() {
		starttime = new Date().getTime();
		loadErrorsProperties();
	}

	public String getDbInfo() {
		return "" + utilsEJB.getCurrentSchema() + " on " + utilsEJB.getCurrentDb() + " (" + utilsEJB.getDbAddress()
				+ ") ";
	}

	public String getDbSize() {
		return "" + utilsEJB.getSchemaTotalSize();
	}

	public String getLocalConfig() {
		String codreg = costanti.getCodRegione();
		try {
			DeRegione myConfReg = deRegioneEJB.findById(codreg);
			return "Regione: " + myConfReg.getDescrizione() + " (" + codreg + ")";
		} catch (MyCasNoResultException e) {
			return "Errore recupero config";
		}
	}

	public static String escapeJS(String value) {
		value = replace(value, "\\", "\\\\"); // backslash --> doppio
		// backslash
		value = replace(value, "\"", "\\\""); // doppio-apice --> backslash +
		// doppio-apice
		value = replace(value, "\n", "\\n"); // invio --> backslash + invio
		value = replace(value, "\'", "\\'"); // apice --> backslash + apice
		// apice
		value = replace(value, "'", "\\'");

		return value;
	}

	public static String replace(String subject, String find, String replace) {
		if (subject == null) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		int l = find.length();
		int s = 0;
		int i = subject.indexOf(find);
		while (i != -1) {
			buf.append(subject.substring(s, i));
			buf.append(replace);
			s = i + l;
			i = subject.indexOf(find, s);
		}
		buf.append(subject.substring(s));
		return buf.toString();
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public void loadErrorsProperties() {
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource("/it/eng/sil/myaccount/resources/errors.properties");
		try {
			errorProperties = new Properties();
			errorProperties.load(url.openStream());
		} catch (java.io.IOException e) {
			log.fatal("Impossibile caricare errors.properties", e);
		}
	}

	public String getErrorProperty(String codErrore) {
		String msgText = "";
		try {
			msgText = (String) errorProperties.get(codErrore);
			msgText.getBytes();// Just trigger the catch if null
		} catch (Exception e) {
			msgText = "Impossibile trovare la property con codice: " + codErrore;
			log.error("Impossibile trovare la property con codice:" + codErrore + ". " + e.getMessage());
		}
		return msgText;

	}

	public String getBundleProperty(String resource, String property) {
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource("/it/eng/sil/myaccount/resources/" + resource + ".properties");
		try {
			Properties bundle = new Properties();
			bundle.load(url.openStream());
			return (String) bundle.get(property);
		} catch (Exception e) {
			log.fatal("Impossibile caricare propery", e);
		}
		return new String();
	}

	public String getUiProperty(String property) {
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource("/it/eng/sil/myaccount/resources/ui.properties");
		try {
			Properties bundle = new Properties();
			bundle.load(url.openStream());
			return (String) bundle.get(property);
		} catch (Exception e) {
			log.fatal("Impossibile caricare propery", e);
		}
		return new String();
	}

	public final String formatDate(Date valueData, String formatoData) {
		SimpleDateFormat fd = new SimpleDateFormat(formatoData);
		return fd.format(valueData);
	}

	public final String capitalize(String data) {
		return StringUtils.capitalize(data);
	}

	public String getCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -18);
		return dateFormat.format(c.getTime());
	}

	public String getMinCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -120);
		return dateFormat.format(c.getTime());
	}

	public String getMaxIscrizioneCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, 0);
		return dateFormat.format(c.getTime());
	}

	public String getMinIscrizioneCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -60);
		return dateFormat.format(c.getTime());
	}

	public String getMaxCalendarDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c = Calendar.getInstance();

		c.add(Calendar.YEAR, -10);
		return dateFormat.format(c.getTime());
	}

	public String currentTimeMillis() {
		return System.currentTimeMillis() + "";
	}

	public String getPortaleURL() {
		return costanti.getPortaleURL();
	}

	public boolean isNuovoSareEnabled() {
		return costanti.isPortingNuovoSareEnabled() || costanti.isSuperUserNuovoSareEnabled();
	}
	
	public Boolean isAbilitatoCanaleSms() {
		return costanti.isOTPAbilitato(OTPRequestEnum.SMS);
	}

	public boolean isPortingNuovoSareEnabled() {
		return costanti.isPortingNuovoSareEnabled();
	}

	public boolean isSuperuserNuovoSareEnabled() {
		return costanti.isSuperUserNuovoSareEnabled();
	}

	public boolean isVDA() {
		return costanti.isVDA();
	}

	public boolean isUmbria() {
		return costanti.isUmbria();
	}

	public boolean isCalabria() {
		return costanti.isCalabria();
	}

	public boolean isPat() {
		return costanti.isTrento();
	}

	public String getNomeRegione() {
		return costanti.getNomeRegione();
	}
	
	public String getRenderCodServiziAmm(Integer idPfPrincipal) {
		return Utils.getRenderCodServiziAmministrativi(CodiceAmministrativoUtil.codAmmEncode(idPfPrincipal));
	}

}
