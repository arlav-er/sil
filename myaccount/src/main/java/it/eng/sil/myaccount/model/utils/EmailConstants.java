package it.eng.sil.myaccount.model.utils;

import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Singleton
@Startup
public class EmailConstants extends Properties {

	private static final long serialVersionUID = 8582422087951753506L;
	// private static final String FILE_CONST_PROPS = "/it/eng/sil/myaccount/resources/const.properties";
	private static final String FILE_EMAIL_PROPS = "/it/eng/sil/myaccount/resources/email.properties";

	public static final String JNDI_BASE = "java:module/";

	protected static Log log = LogFactory.getLog(EmailConstants.class);

	@EJB
	ConstantsSingleton constantsSingleton;

	/**
	 * il caricamento delle properties avviene su due file: common e specifico
	 */
	@PostConstruct
	private void postConstruct() {
		// Si carica prima il file di proprietà email
		URL urlPropCommon = Thread.currentThread().getContextClassLoader().getResource(FILE_EMAIL_PROPS);
		try {
			this.load(urlPropCommon.openStream());
		} catch (java.io.IOException e) {
			throw new RuntimeException("Impossibile caricare il file " + FILE_EMAIL_PROPS);
		}
	}

	public String getEmailFrom() {
		return constantsSingleton.getEmailFrom();
	}

	private String getDirectoryRegione() {
		if (constantsSingleton.isUmbria()) {
			return "umbria";
		} else if (constantsSingleton.isCalabria()) {
			return "calabria";
		} else if (constantsSingleton.isTrento()) {
			return "trento";
		} else if (constantsSingleton.isVDA()) {
			return "vda";
		} else if (constantsSingleton.isPuglia()) {
			return "puglia";
		} else {
			return "rer";
		}
	}

	public String getEmailRegisterCittadino(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".register.cittadino");
	}

	public String getEmailRegisterAzienda(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".register.azienda");
	}

	public String getEmailConfirmUtente(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".confirm.cittadino");
	}

	public String getEmailConfirmAzienda(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".confirm.azienda");
	}

	public String getEmailRecuperaPassword(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".password.recupera");
	}

	public String getEmailValidaAzienda(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".valida.azienda");
	}

	public String getEmailNotificaRicevuta(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".notifica.azienda");
	}

	public String getEmailAccreditamentoForteNoPec(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".accreditamentoForteNoPec.cittadino");
	}

	public String getEmailAccreditamentoForteConPec(String codiceRegione) {
		return getProperty(getDirectoryRegione() + ".accreditamentoForteConPec.cittadino");
	}
}
