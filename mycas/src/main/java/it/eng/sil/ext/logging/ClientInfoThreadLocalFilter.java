/**
 * Licensed to Inspektr under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Inspektr licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package it.eng.sil.ext.logging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.github.inspektr.common.web.ClientInfo;
import com.github.inspektr.common.web.ClientInfoHolder;

/**
 * Creates a ClientInfo object and passes it to the {@link ClientInfoHolder}
 * <p>
 * If one provides an alternative IP Address Header (i.e. init-param "alternativeIpAddressHeader"), the client IP
 * address will be read from that instead.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0
 *
 */
public final class ClientInfoThreadLocalFilter implements Filter {

	private static final String HEADER_REMOTE_IP = "rerfwfor";
	
//	private static final Logger LOG = LoggerFactory.getLogger(ClientInfoThreadLocalFilter.class);

	public void destroy() {
	}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {
		try {

			String serverAddress = request.getLocalAddr();

			//////////////////////////////////////////////////////////////////////

//			HttpServletRequest req = (HttpServletRequest) request;
//
//			Enumeration<String> headerNames = req.getHeaderNames();
//			while (headerNames.hasMoreElements()) {
//				String headerName = (String) headerNames.nextElement();
//				String headerValue = req.getHeader(headerName);
//
//				String header = String.format("Header(\"%s\")=\"%s\"", headerName, headerValue);
//				LOG.info(header);
//			}

			/////////////////////////////////////////////////////////////////////

			String remoteAddress = ((HttpServletRequest) request).getHeader(HEADER_REMOTE_IP);

			if (remoteAddress == null) {
				remoteAddress = request.getRemoteAddr();
			}else{
				remoteAddress+=" (from LBL header " + HEADER_REMOTE_IP + ")";
			}

			ClientInfo clientInfo = new ClientInfo(serverAddress, remoteAddress);
			ClientInfoHolder.setClientInfo(clientInfo);

			filterChain.doFilter(request, response);
		} finally {
			ClientInfoHolder.clear();
		}
	}

	public void init(final FilterConfig filterConfig) throws ServletException {

	}
}
