package it.eng.sil.util;

import javax.servlet.http.HttpServletRequest;

/** Questa classe indaga il tipo di browser */

public class BrowserSniffer {

	public static String USER_AGENT = "USER-AGENT";

	public static boolean is_ie(HttpServletRequest req) {

		if (req == null) {
			return false;
		}

		String agent = req.getHeader(USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if ((agent.indexOf("msie") != -1) && (agent.indexOf("opera") == -1)) {
			return true;
		} else {
			return false;
		}

	} // end is_ie

	public static boolean is_ie4(HttpServletRequest req) {

		if (req == null) {
			return false;
		}

		String agent = req.getHeader(USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 4") != -1)) {
			return true;
		} else {
			return false;
		}

	} // end is_ie4

	public static boolean is_ie5(HttpServletRequest req) {

		if (req == null) {
			return false;
		}

		String agent = req.getHeader(USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 5.0") != -1)) {
			return true;
		} else {
			return false;
		}

	} // end is_ie5

	public static boolean is_ie5_5(HttpServletRequest req) {

		if (req == null) {
			return false;
		}

		String agent = req.getHeader(USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req) && (agent.indexOf("msie 5.5") != -1)) {
			return true;
		} else {
			return false;
		}

	} // end is_ie5_5

	public static boolean is_ie5_5up(HttpServletRequest req) {

		if (is_ie(req) && !is_ie4(req) && !is_ie5(req)) {
			return true;
		} else {
			return false;
		}

	} // end is_ie5_5up

	public static boolean is_ie6(HttpServletRequest req) {

		if (req == null) {
			return false;
		}

		String agent = req.getHeader(USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if (is_ie(req)) {
			return false;
		}

		return true;

	} // end is_ie6

	public static boolean is_nav(HttpServletRequest req) {

		if (req == null) {
			return false;
		}

		String agent = req.getHeader(USER_AGENT);

		if (agent == null) {
			return false;
		}

		agent = agent.toLowerCase();

		if ((agent.indexOf("mozilla") != -1) && (agent.indexOf("spoofer") == -1) && (agent.indexOf("compatible") == -1)
				&& (agent.indexOf("opera") == -1) && (agent.indexOf("webtv") == -1)
				&& (agent.indexOf("hotjava") == -1)) {

			return true;
		} else {
			return false;
		}

	} // end is_nav

} // end BrowserSniffer
