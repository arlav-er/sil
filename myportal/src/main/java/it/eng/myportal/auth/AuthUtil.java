package it.eng.myportal.auth;

import javax.servlet.http.HttpSession;

import it.eng.myportal.utils.ConstantsSingleton;

public class AuthUtil {

	private static final String BARRA = "\\";
	private static final String DOPPIA_BARRA = "\\\\";

	private static final String[] net_socials = { ConstantsSingleton.Facebook.TYPE, ConstantsSingleton.Twitter.TYPE,
			ConstantsSingleton.Google.TYPE, ConstantsSingleton.Federa.TYPE, ConstantsSingleton.Icar.TYPE,
			ConstantsSingleton.Spid.TYPE };

	public static String removeSocialPrefix(String username) {
		for (String net_social : net_socials) {
			String id_social = DOPPIA_BARRA + net_social + BARRA;
			if (username.startsWith(id_social)) {
				return username.substring(id_social.length());
			}
		}
		return username;
	}

	public static StringBuilder preparaUrl(HttpSession session, String username, String type) {
		

		// MODIFICHE PER IL CAS
		String loginTicket = (String) session.getAttribute("loginTicket");
		String flowExecutionKey = (String) session.getAttribute("flowExecutionKey");

		return composeLoginUrl(username, type, loginTicket, flowExecutionKey);
	}

	public static StringBuilder composeLoginUrl(String username, String type, String loginTicket,
			String flowExecutionKey) {
		StringBuilder url = new StringBuilder();
		url.append(ConstantsSingleton.MYCAS_URL + "/login?autosubmit=true&");

		String lxt_password = (DOPPIA_BARRA + type + BARRA + username + "__MYCAS__").toUpperCase();
		String lxt_salted = it.eng.myportal.utils.Utils.SHA1.encrypt(lxt_password);

		url.append("lxt_password=" + lxt_salted);
		url.append("&lxt_username=" + DOPPIA_BARRA + type + BARRA + username);
		url.append("&lxt_loginTicket=" + loginTicket);
		url.append("&lxt_flowExecutionKey=" + flowExecutionKey);

		return url;
	}

}
