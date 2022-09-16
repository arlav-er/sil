package it.eng.sil.coop.webservices.asl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.axis.AxisFault;

import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.security.handlers.Utility;

public class GestoreAutenticazione {
	private Properties listaProperties;

	public GestoreAutenticazione(String filePropName) throws IOException {
		String s = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "classes"
				+ File.separator + filePropName;
		File f = new File(s);
		listaProperties = new Properties();
		listaProperties.load(new FileInputStream(f));
	}

	public boolean checkCredenziali(String username, String password) throws AxisFault {
		String usernameProp = listaProperties.getProperty("USERNAME");
		String passwordProp = listaProperties.getProperty("PASSWORD");
		passwordProp = Utility.decrypt(passwordProp);
		if (username.equals(usernameProp) && password.equals(passwordProp)) {
			return true;
		} else {
			return false;
		}
	}
}
