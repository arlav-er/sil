/*
  $Id: CharacterEncodingFilter.java 990 2009-10-28 21:57:18Z dfisher $

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 990 $
  Updated: $Date: 2009-10-28 17:57:18 -0400 (Wed, 28 Oct 2009) $
 */
package it.eng.myauthservice.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets the request and/or response character encoding based on the presence of
 * either or both of the following init params, {@link #REQUEST_CHARSET_NAME}
 * {@link #RESPONSE_CHARSET_NAME}.
 * 
 * <p>
 * <strong>NOTE:</strong> this filter should be as close to the beginning of the
 * filter chain as possible. Any filters that obtain request parameters, e.g.
 * <code>request.getParameter(...)</code>, prior to setting the character set
 * via this filter will use the platform default character set.
 * </p>
 * 
 * @author Middleware Services
 * @version $Revision: 990 $
 */
public class CharacterEncodingFilter implements Filter {

	/**
	 * Name of init param for setting request character encoding. The value
	 * should be a valid Java character set name, e.g. UTF-8.
	 */
	public static final String REQUEST_CHARSET_NAME = "requestCharsetName";

	/**
	 * Name of init param for setting response character encoding. The value
	 * should be a valid Java character set name, e.g. UTF-8.
	 */
	public static final String RESPONSE_CHARSET_NAME = "responseCharsetName";

	/** Request character set name. */
	private String requestCharsetName;

	/** Response character set name. */
	private String responseCharsetName;

	public void init(final FilterConfig filterConfig) throws ServletException {
		if (filterConfig.getInitParameter(REQUEST_CHARSET_NAME) != null) {
			requestCharsetName = filterConfig
					.getInitParameter(REQUEST_CHARSET_NAME);
		}
		if (filterConfig.getInitParameter(RESPONSE_CHARSET_NAME) != null) {
			responseCharsetName = filterConfig
					.getInitParameter(RESPONSE_CHARSET_NAME);
		}
	}


	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader("X-UA-Compatible", "IE=Edge");

		/**
		 * I servizi che producono formati binari (immagini o pdf) 
		 * non devono avere impostato UTF-8
		 * 
		 */
		
		String uriStr = ((HttpServletRequest) request).getRequestURI();
		if (	   !uriStr.contains("getFoto") 
				&& !uriStr.contains("getPoiMask")
				&& !uriStr.contains("getPoiVacanciesValide")
				&& !uriStr.contains("GeoCache")
				&& !uriStr.contains("getFeatureInfo")
				&& !uriStr.contains("getSvImmagine")
				&& !uriStr.contains("getTmpFoto") 
				&& !uriStr.contains("stampa")
				&& !uriStr.contains("stipula_did")
				&& !uriStr.contains("getAccreditamentoAzienda")
				&& !uriStr.contains("getCurriculumUtente")
				&& !uriStr.contains("getLetteraAccompagnamento")
				&& !uriStr.contains("getAllegatoCv")
				&& !uriStr.contains("getAllegatoCandidatura")
				&& !uriStr.contains("getAllegatoMsg")
				&& !uriStr.contains("rest")) {

			if (requestCharsetName != null) {
				request.setCharacterEncoding(requestCharsetName);
			}
			if (responseCharsetName != null) {
				response.setCharacterEncoding(responseCharsetName);
			}
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
	}
}
