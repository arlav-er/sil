package com.engiweb.framework.configuration;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.engiweb.framework.init.InitializerManager;

public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 5973782302889581813L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Properties props = System.getProperties();
		Enumeration<?> propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			System.out.println("ConfigServlet::init: " + name + " [" + System.getProperty(name) + "]");
		}
		String afRootPath = config.getInitParameter("AF_ROOT_PATH");
		if (afRootPath == null)
			afRootPath = config.getServletContext().getRealPath("");
		System.out.println("ConfigServlet::init: AF_ROOT_PATH = " + afRootPath);
		ConfigSingleton.setRootPath(afRootPath);
		String afConfigFile = config.getInitParameter("AF_CONFIG_FILE");
		System.out.println("ConfigServlet::init: AF_CONFIG_FILE = " + afConfigFile);
		ConfigSingleton.setConfigFileName(afConfigFile);

		String context = config.getServletContext().getContextPath();
		System.out.println(
				"ConfigServlet::init: il contesto " + context + " e' stato recuperato dal CONTEXT-ROOT della web-app");

		if (context.startsWith("/"))
			context = context.substring(1);
		ConfigSingleton.setContextName(context);

		System.out.println("ConfigServlet::init: CONTEXT_NAME = " + context);

		this.initEncrypterKey();

		InitializerManager.init();
	}

	private void initEncrypterKey() {
		String encrypterKeyBase64 = System.getProperty("_ENCRYPTER_KEY_.base64");

		if (encrypterKeyBase64 != null && !encrypterKeyBase64.isEmpty()) {
			String encrypterKey = new String(java.util.Base64.getDecoder().decode(encrypterKeyBase64));
			System.setProperty("_ENCRYPTER_KEY_", encrypterKey);
			System.out.println(
					"ConfigServlet::init: è presente il base64 dell'encrypter key, si effettua il decode per ricavare l'encrypter key. Chiave: "
							+ encrypterKeyBase64 + " --> " + encrypterKey);
		} else {
			System.out.println(
					"ConfigServlet::init: non è presente il base64 dell'encrypter key, assicurarsi di aver settato la proprietà _ENCRYPTER_KEY_");
		}
	}
}
