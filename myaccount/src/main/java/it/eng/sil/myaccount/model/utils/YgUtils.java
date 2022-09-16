package it.eng.sil.myaccount.model.utils;

import it.eng.sil.myaccount.utils.Utils;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class YgUtils {
	private static final int JSON_MAX_SIZE = 1000000;
	protected static Log log = LogFactory.getLog(YgUtils.class);
	
	public static boolean updateAdesioneYg(Integer idPfPrincipal, String codProvinciaRif, String urlPortale) {

		boolean success = false;
		
		try {			
			String tokenSecurity = Utils.buildTokenSecurity(urlPortale);

			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(urlPortale + tokenSecurity);
			method.addParameter("idPfPrincipal", "" + idPfPrincipal);
			method.addParameter("codProvinciaRif", codProvinciaRif);			

			client.executeMethod(method);
			String jsonString = method.getResponseBodyAsString(JSON_MAX_SIZE);

			try {
				JSONObject obj = new JSONObject(jsonString);
				String esito = obj.getString("status");
				if ("ok".equalsIgnoreCase(esito)) {
					success = true;
				} else {
					success = false;
				}
			} catch (JSONException e) {
				log.error("Errore parsing risposta", e);
				success = false;
				return success;
			} 

		} catch (IOException e1) {
			log.error("Errore, non è stato possibile connettersi al portale", e1);
			success = false;
			return success;
		}
		
		return success;
		
	}

}