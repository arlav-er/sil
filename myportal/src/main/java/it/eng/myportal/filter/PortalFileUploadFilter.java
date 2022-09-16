/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.eng.myportal.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.webapp.filter.FileUploadFilter;

public class PortalFileUploadFilter implements Filter {
	
	private FileUploadFilter primefacesFilter;
	private String primefaces;
	private Log log = LogFactory.getLog(this.getClass());
	public void init(FilterConfig filterConfig) throws ServletException {
		primefacesFilter = new FileUploadFilter();
		primefacesFilter.init(filterConfig);
		primefaces = filterConfig.getInitParameter("id");
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
				
		boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
		String uriStr = ((HttpServletRequest) request).getRequestURI();
		if(isMultipart) {			
			//tolto perchè TUTTI i multipart devono passare per primefaces.
			//questa classe andrebbe rimossa
			//if (uriStr.contains("ricerca_new.xhtml") || uriStr.contains("messaging/main.xhtml")
			//		|| uriStr.contains("convenzioni/convenzione_edit.xhtml")
			//		|| uriStr.contains("yg_dichiarazione_neet")) {

				PortalFilterChain godChain = new PortalFilterChain(filterChain);
		        godChain.addFilter(primefacesFilter);
		         
		        godChain.doFilter(request, response);
			//} else {
			//	log.warn("Tutti noi abbiamo perso tempo qui. Prima di ripetere, guarda PortalFileUpload.java, cazzo.");
			//	filterChain.doFilter(request, response);
			//}						
		} else {
			filterChain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}

}
