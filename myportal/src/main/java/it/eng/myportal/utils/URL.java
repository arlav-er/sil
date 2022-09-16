package it.eng.myportal.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe di utilità per costruire una URL
 * @author Rodi A.
 *
 */
public class URL {
	private String url;
	private Map<String,String> parameters = new HashMap<String, String>();
	
	public URL(String path) {
		this.url = path;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(url);
		if (parameters.size() > 0) {
			ret.append("?");
			Set<String> set = parameters.keySet();
			for (String key : set) {
				ret.append(key + "=" + parameters.get(key));
				ret.append("&");
			}			
		}
		return ret.toString();
		
	}

	public static String escape(String input) {
		//input = replace(input, "%", "%25");
		input = replace(input, " ", "%20");
		input = replace(input, "\"", "%22");		
		input = replace(input, "'", "%27");
		input = replace(input, "[", "%5B");
		input = replace(input, "]", "%5D");
		input = replace(input, "{", "%7B");
		input = replace(input, "}", "%7D");
		
//		input = replace(input, "<", "%3C");
//		input = replace(input, "<", "%3E");
//		input = replace(input, "?", "%3F");
//		input = replace(input, "&", "%26");		
		return input;
	}
	
	/**
	 * FIXME perchè è stato implementato un metodo di replace?
	 */
	public static String replace(String toParse, String replacing, String replaced) {
		if (toParse == null || replacing == null) {
			return toParse;
		}		
		
		if (replaced != null) {
			int parameterIndex = toParse.indexOf(replacing);
			while (parameterIndex != -1) {
				String newToParse = toParse.substring(0, parameterIndex);
				newToParse += replaced;
				newToParse += toParse.substring(parameterIndex + replacing.length(), toParse.length());
				toParse = newToParse;
				parameterIndex = toParse.indexOf(replacing, parameterIndex + replaced.length());
			} 
		} 
		return toParse;
	}
	
	public static String escapeSolr(String input) {
		//input = replace(input, "%", "%25");
		input = replace(input, " ", "%20");
		input = replace(input, "\"", "%22");		
		input = replace(input, "'", "%27");
		input = replace(input, ":", "%3A");
		input = replace(input, "|", "%7C");		
		input = replace(input, "<", "%3C");
		input = replace(input, ">", "%3E");
		return input;
	}
	
	public static String escapeChrUrl(String input) {
		input = replace(input, "ì", "%EC");
		input = replace(input, "í", "%ED");
		input = replace(input, "ò", "%F2");
		input = replace(input, "ó", "%F3");
		input = replace(input, "ù", "%F9");
		input = replace(input, "ú", "%FA");
		input = replace(input, "à", "%E0");
		input = replace(input, "á", "%E1");
		input = replace(input, "è", "%E8");
		input = replace(input, "é", "%E9");
		return input;
	}
	
	
}
