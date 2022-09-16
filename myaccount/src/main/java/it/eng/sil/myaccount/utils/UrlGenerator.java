package it.eng.sil.myaccount.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * generates url from map
 * 
 * @author hatemalimam
 */
public class UrlGenerator {

	private static String DEFAULT_ENCODING_FOR_URL = "UTF-8";

	private String address;
	private Map<String, String> map = new LinkedHashMap<String, String>();

	public UrlGenerator() {
		this.address = "";
	}

	public UrlGenerator(String address, Map<String, String> map) {
		this.address = address;
		this.map = map;
	}


	public String getUrl() {
		StringBuilder sb = new StringBuilder(address);

		List<String> listOfParams = new ArrayList<String>();
		for (String param : map.keySet()) {
			listOfParams.add(param + "=" + encodeString(map.get(param)));
		}

		if (!listOfParams.isEmpty()) {
			String query = org.apache.commons.lang3.StringUtils.join(listOfParams, "&");
			sb.append("?");
			sb.append(query);
		}

		return sb.toString();
	}

	private static String encodeString(String name) throws NullPointerException {
		String tmp = null;

		if (name == null)
			return null;

		try {
			tmp = java.net.URLEncoder.encode(name, DEFAULT_ENCODING_FOR_URL);
		} catch (Exception e) {
		}

		if (tmp == null)
			throw new NullPointerException();

		return tmp;
	}

}