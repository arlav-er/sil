package it.eng.sil.util.grabber;

/*
 * HTMLbase
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA (or visit
 * their web site at http://www.gnu.org/).
 *
 */

/**
 * Title:        HTMLBase
 * Description:  Classe base per la costruzione dei plugin
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author Matteo Baccan
 * @version 1.0
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public abstract class htmlbase {

	public htmlbase() {
	}

	private String cookieHeader = "";
	private String locationHeader = "";
	private String contentDispositionHeader = "";
	private String contentTypeHeader = "";

	protected void processField(HttpURLConnection con) {
		cookieHeader = "";
		locationHeader = "";
		contentDispositionHeader = "";
		contentTypeHeader = "";

		// I cookie possono essere N .. provo a collezionarli
		int n = 1; // n=0 has no key, and the HTTP return status in the value
					// field
		boolean done = false;
		while (!done) {
			String headerKey = con.getHeaderFieldKey(n);
			String headerVal = con.getHeaderField(n);
			if (headerKey == null && headerVal == null)
				done = true;
			else if (headerKey != null && headerVal != null) {
				if (headerKey.equalsIgnoreCase("set-cookie")) {
					int nSpace = headerVal.indexOf(" ");
					if (nSpace != -1)
						headerVal = headerVal.substring(0, nSpace);
					cookieHeader += headerVal + " ";
				} else if (headerKey.equalsIgnoreCase("location")) {
					locationHeader = headerVal;
				} else if (headerKey.equalsIgnoreCase("Content-Disposition")) {
					contentDispositionHeader = headerVal;
				} else if (headerKey.equalsIgnoreCase("Content-Type")) {
					contentTypeHeader = headerVal;
				}
			}
			n++;
		}
	}

	protected String getCookie() {
		return cookieHeader;
	}

	protected String getLocation() {
		return locationHeader;
	}

	protected String getContentDisposition() {
		return contentDispositionHeader;
	}

	protected String getContentType() {
		return contentTypeHeader;
	}

	protected StringBuffer getPage(String cUrl) throws Throwable {
		return getPage(cUrl, null, 0, true, "");
	}

	protected StringBuffer getPage(String cUrl, String cCookie) throws Throwable {
		return getPage(cUrl, cCookie, 0, true, "");
	}

	protected StringBuffer getPage(String cUrl, String cCookie, int nLine) throws Throwable {
		return getPage(cUrl, cCookie, nLine, true, "");
	}

	protected StringBuffer getPage(String cUrl, String cCookie, int nLine, boolean bAll) throws Throwable {
		return getPage(cUrl, cCookie, nLine, bAll, "");
	}

	protected StringBuffer getPage(String cUrl, String cCookie, int nLine, boolean bAll, String cRef) throws Throwable {
		return getPage(cUrl, cCookie, nLine, bAll, cRef, "");
	}

	// /*
	protected StringBuffer getPage(String cUrl, String cCookie, int nLine, boolean bAll, String cRef,
			String cAuthorization) throws Exception {
		boolean endOfHdr_1stBlankLine = false; // [YS]
		// [YS] Signal End of Header =possibly= at First blank line
		// [YS] il piu' semplice segnale di termine dell' header e' dato dalla
		// prima linea vuota

		URL urlObject = new URL(cUrl);

		HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
		con.setRequestProperty("User-Agent", getAgent());
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("Accept-Language", "it");
		// con.setRequestProperty( "Keep-Alive", "false" );
		con.setRequestProperty("Host", urlObject.getHost());
		if (cCookie != null)
			con.setRequestProperty("Cookie", cCookie);
		// Referer
		if (cRef.length() > 0)
			con.setRequestProperty("Referer", cRef);

		// Proxy user e password
		String cEncode = System.getProperty("proxyUser", "");
		if (cEncode.length() > 0) {
			if (System.getProperty("proxyPassword", "").length() > 0)
				cEncode += ":" + System.getProperty("proxyPassword", "");
			cEncode = new String(Base64.encode(cEncode.getBytes()));
			con.setRequestProperty("Proxy-authorization", "Basic " + cEncode);
		}

		if (cAuthorization.length() > 0) {
			String cEncodeA = new String(Base64.encode(cAuthorization.getBytes()));
			con.setRequestProperty("Authorization", "Basic " + cEncodeA);
		}

		// Prendo I cookie
		processField(con);

		// Lettura risultato
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String inputLine;
		StringBuffer sb = new StringBuffer();

		int nPos = 0;
		// Correzione di Marco Sburlati, grazie Marco (Matteo Baccan)
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine).append((char) 13).append((char) 10);

			if (inputLine.length() == 0) {
				endOfHdr_1stBlankLine = true;
			}
			if (!bAll) {
				if (endOfHdr_1stBlankLine) {
					if (nPos == nLine)
						break;
					nPos++;
				}
			}
		}

		// NON va bene .. no no no .. non posso sconnettere col jdk Microsoft!!
		// .. ma siamo fuori?
		// con.disconnect();

		// Questo invece non da errore, ma con Microsoft e' come se comunque
		// scaricasse
		// la pagina .. mannaggia mannaggia
		in.close();

		return sb;
	}

	protected byte[] getPageBytes(String cUrl, String cCookie) throws Exception {
		URL urlObject = new URL(cUrl);

		HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
		con.setRequestProperty("User-Agent", getAgent());
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("Accept-Language", "it");
		// con.setRequestProperty( "Keep-Alive", "false" );
		con.setRequestProperty("Host", urlObject.getHost());
		if (cCookie != null)
			con.setRequestProperty("Cookie", cCookie);

		// Proxy user e password
		String cEncode = System.getProperty("proxyUser", "");
		if (cEncode.length() > 0) {
			if (System.getProperty("proxyPassword", "").length() > 0)
				cEncode += ":" + System.getProperty("proxyPassword", "");
			cEncode = new String(Base64.encode(cEncode.getBytes()));
			con.setRequestProperty("Proxy-authorization", "Basic " + cEncode);
		}

		// Prendo I cookie
		processField(con);

		// Lettura risultato
		InputStream in = con.getInputStream();

		ByteArrayOutputStream cReply = new ByteArrayOutputStream();
		int c;
		while ((c = in.read()) != -1)
			cReply.write(c);

		in.close();

		return cReply.toByteArray();
	}

	protected HttpURLConnection streamPageTop(String cUrl, String cCookie) throws Exception {
		return streamPageTop(cUrl, cCookie, "");
	}

	protected HttpURLConnection streamPageTop(String cUrl, String cCookie, String cRef) throws Exception {
		URL urlObject = new URL(cUrl);

		HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
		con.setRequestProperty("User-Agent", getAgent());
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("Accept-Language", "it");
		// con.setRequestProperty( "Keep-Alive", "false" );
		con.setRequestProperty("Host", urlObject.getHost());
		if (cCookie != null)
			con.setRequestProperty("Cookie", cCookie);
		// Referer
		if (cRef.length() > 0)
			con.setRequestProperty("Referer", cRef);

		// Proxy user e password
		String cEncode = System.getProperty("proxyUser", "");
		if (cEncode.length() > 0) {
			if (System.getProperty("proxyPassword", "").length() > 0)
				cEncode += ":" + System.getProperty("proxyPassword", "");
			cEncode = new String(Base64.encode(cEncode.getBytes()));
			con.setRequestProperty("Proxy-authorization", "Basic " + cEncode);
		}

		// Prendo i cookie
		processField(con);

		return con;
	}

	protected void streamPageBody(HttpURLConnection con, OutputStream SO, int nLine, boolean bAll) throws Throwable {
		boolean endOfHdr_1stBlankLine = false;

		// Lettura risultato
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String inputLine;
		htmlTool html = new htmlTool();

		int nPos = 0;
		while ((inputLine = in.readLine()) != null) {
			html.putData(SO, inputLine + (char) 13 + (char) 10);

			if (inputLine.length() == 0) {
				endOfHdr_1stBlankLine = true;
			}
			if (!bAll) {
				if (endOfHdr_1stBlankLine) {
					if (nPos == nLine)
						break;
					nPos++;
				}
			}
		}

		// NON va bene .. no no no .. non posso sconnettere col jdk Microsoft!!
		// .. ma siamo fuori?
		// con.disconnect();

		// Questo invece non da errore, ma con Microsoft e' come se comunque
		// scaricasse
		// la pagina .. mannaggia mannaggia
		con.getInputStream().close();
	}

	// */

	/*
	 * // In lavorazione // protected StringBuffer getPage( String cUrl, String cCookie, int nLine, boolean bAll )
	 * throws Throwable { // Informazioni sul proxy boolean bProxy =
	 * System.getProperty("http.proxySet","false").equalsIgnoreCase("true"); String cProxy = ""; int nProxyPort = 0; //
	 * URL remoto URL oUrl = new URL( cUrl ); // cosa devo prendere? int nRemotePort = oUrl.getPort(); if(
	 * nRemotePort==-1 ) nRemotePort=80; String cRemoteHost = oUrl.getHost(); String cFile = oUrl.getFile(); // Preparo
	 * la chiamata StringBuffer sb = new StringBuffer(); if( bProxy ) { sb.append( "GET " + cUrl +" HTTP/1.1\r\n" ); }
	 * else { sb.append( "GET " +cFile +" HTTP/1.1\r\n" ); } sb.append( "User-Agent: " +getAgent() +"\r\n" ); sb.append(
	 * "Pragma: no-cache\r\n" ); sb.append( "Accept-Language: it\r\n" ); sb.append( "Host: " +cRemoteHost +"\r\n" ); if(
	 * cCookie!=null ) sb.append( "Cookie: " +cCookie +"\r\n" ); // Proxy user e password String cEncode =
	 * System.getProperty("proxyUser", ""); if( cEncode.length()>0 ){ if( System.getProperty("proxyPassword",
	 * "").length()>0 ) cEncode += ":" +System.getProperty("proxyPassword", ""); cEncode = new String( Base64.encode(
	 * cEncode.getBytes() ) ); sb.append( "Proxy-authorization: Basic " +cEncode +"\r\n" ); } sb.append( "\r\n" ); //
	 * Ora mi connetto e sparo la chiamata Socket socket = null; if( bProxy ) { cProxy =
	 * System.getProperty("http.proxyHost",""); nProxyPort = Double.valueOf( System.getProperty("http.proxyPort","8080")
	 * ).intValue(); socket = new Socket(cProxy, nProxyPort); } else { // Output socket = new Socket(cRemoteHost,
	 * nRemotePort); } // Scrivo il dato OutputStream os = socket.getOutputStream(); htmlTool html = new htmlTool();
	 * html.putData( os, sb.toString() );
	 * 
	 * sb = new StringBuffer(); // Leggo il dato InputStream is = socket.getInputStream(); // Leggo l'header String cRH
	 * = html.getHeader( is ); sb.append( is ); //sb.append( "\r\n" );
	 * 
	 * cookieHeader = html.setcookie(cRH); locationHeader = html.location(cRH); contentDispositionHeader =
	 * html.contentDisposition(cRH); contentTypeHeader = html.contentType(cRH);
	 * 
	 * sb = new StringBuffer(); int nRL = html.contentLength(cRH); if( nRL>0 ){ sb.append( html.getData( is, nRL, bAll,
	 * nLine ) ); } else if( html.isChunked( cRH ) ){ ByteArrayOutputStream cRD = new ByteArrayOutputStream();
	 * readChunk( html, is, cRD, bAll, nLine ); sb.append( cRD ); } else if( is.available()>0 ){ sb.append(
	 * html.getData( is, -1, bAll, nLine ) ); }
	 * 
	 * try { socket.close(); } catch (Throwable e) { }
	 * 
	 * 
	 * return sb; }
	 * 
	 * private void readChunk( htmlTool html, InputStream SI, OutputStream OO, boolean bAll, int nLine ) throws
	 * Throwable {
	 * 
	 * ByteArrayOutputStream cRPD; int nLen = 0; do { String cLine = html.getLine( SI ); nLen = Integer.parseInt(
	 * cLine.trim(), 16 ); // read len + 2 = CRLF cRPD = html.getData( SI, nLen+2, bAll, nLine ); // Put output
	 * //html.putData( OO, cLine );
	 * 
	 * //System.out.println( cLine+cRPD );
	 * 
	 * html.putData( OO, cRPD ); } while ( nLen>0 ); } //
	 */

	protected StringBuffer postPage(String cUrl, String cCookie, String cPost) throws Exception {
		return postPage(cUrl, cCookie, cPost, "", "");
	}

	protected StringBuffer postPage(String cUrl, String cCookie, String cPost, String cRef) throws Exception {
		return postPage(cUrl, cCookie, cPost, cRef, "");
	}

	protected StringBuffer postPage(String cUrl, String cCookie, String cPost, String cRef, String cAuthorization)
			throws Exception {

		// Preparo il post
		URL urlObject = new URL(cUrl);
		HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		// Evito il redirect
		HttpURLConnection.setFollowRedirects(false);
		// con.setInstanceFollowRedirects(false);

		// User agent random
		con.setRequestProperty("User-Agent", getAgent());

		// No cache
		con.setRequestProperty("Pragma", "no-cache");

		// Referer
		if (cRef.length() > 0)
			con.setRequestProperty("Referer", cRef);

		// Preparo il post in italiano
		con.setRequestProperty("Content-Length", "" + cPost.length());
		con.setRequestProperty("Accept-Language", "it");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if (cCookie != null)
			con.setRequestProperty("Cookie", cCookie);

		// Proxy user e password
		String cEncode = System.getProperty("proxyUser", "");
		if (cEncode.length() > 0) {
			if (System.getProperty("proxyPassword", "").length() > 0)
				cEncode += ":" + System.getProperty("proxyPassword", "");
			cEncode = new String(Base64.encode(cEncode.getBytes()));
			con.setRequestProperty("Proxy-authorization", "Basic " + cEncode);
		}

		if (cAuthorization.length() > 0) {
			// con.setRequestProperty( "Authorization", "Basic " +cEncode );
		}

		PrintWriter out = new PrintWriter(con.getOutputStream());
		out.print(cPost);
		out.flush();
		out.close();

		// Lettura risultato
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		// Prendo I cookie
		processField(con);

		String inputLine;
		StringBuffer sb = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
			sb.append(inputLine).append((char) 13).append((char) 10);

		in.close();

		return sb;
	}

	protected void putFile(String sb) {
		putFile(new StringBuffer(sb), "");
	}

	protected void putFile(StringBuffer sb) {
		putFile(sb, "");
	}

	protected void putFile(String sb, String cAdd) {
		putFile(new StringBuffer(sb), cAdd);
	}

	protected void putFile(StringBuffer sb, String cAdd) {
		String cPath = "";
		try {
			FileWriter fw = new FileWriter(cPath + this.getClass().getName() + cAdd + ".txt");
			fw.write(sb.toString());
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected String getAgent() {
		String cAgent = "";
		// Millisecondi di sistema
		long nMill = System.currentTimeMillis();
		// Modulo 11
		nMill = nMill % 11;

		switch ((int) nMill) {
		case 0:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.0.3705; .NET CLR 1.1.4322)";
			break;
		case 1:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)";
			break;
		case 2:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.0.3705)";
			break;
		case 3:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)";
			break;
		case 4:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";
			break;
		case 5:
			cAgent = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)";
			break;
		case 6:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)";
			break;
		case 7:
			cAgent = "Mozilla/4.0 (compatible; MSIE 5.01; Windows 98)";
			break;
		case 8:
			cAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90; Hotbar 4.3.1.0)";
			break;
		case 9:
			cAgent = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";
			break;
		case 10:
			cAgent = "Mozilla/4.0 (compatible; MSIE 5.5; Windows 95)";
			break;
		}
		return cAgent;
	}

	public static String replace(String s, String s1, String s2) {
		StringBuffer stringbuffer = new StringBuffer();
		long l = s.length() - s1.length();
		long l1 = s.length();
		long l2 = s1.length() - 1;
		for (int i = 0; (long) i < l1; i++)
			if ((long) i <= l) {
				if (s.startsWith(s1, i)) {
					stringbuffer.append(s2);
					i = (int) ((long) i + l2);
				} else {
					stringbuffer.append(s.charAt(i));
				}
			} else {
				stringbuffer.append(s.charAt(i));
			}

		return stringbuffer.toString();
	}

	protected String filterRem(String y) {
		return filter(y, "<!--", "-->");
	}

	protected String filter(String y, String cOn, String cOff) {
		StringBuffer oBuf = new StringBuffer();

		String cUpper = y.toUpperCase();
		cOn = cOn.toUpperCase();
		cOff = cOff.toUpperCase();

		boolean isTag = false;
		for (int nPos = 0; nPos < y.length(); nPos++) {
			while (cUpper.startsWith(cOn, nPos)) {
				int nLastPos = cUpper.indexOf(cOff, nPos + 1);
				if (nLastPos == -1)
					break;
				nPos = nLastPos + cOff.length();
			}
			if (nPos >= 0 && nPos < y.length())
				oBuf.append(y.charAt(nPos));
		}
		return oBuf.toString();
	}

	// Vettore email
	private Vector aEmail = new Vector();
	private Vector aSize = new Vector();

	protected boolean addEmailInfo(String cEmail, int nLen) {
		boolean bRet = false;
		if (!aEmail.contains(cEmail)) {
			bRet = true;
			aEmail.addElement(cEmail);
			aSize.addElement(new Double(nLen));
		}
		return bRet;
	}

	public int getMessageNum() {
		return aEmail.size();
	}

	public int getMessageSize() {
		int nTot = 0;
		for (int n = 0; n < getMessageNum(); n++)
			nTot += getMessageSize(n + 1);
		return nTot;
	}

	public void invertSort() {
		Vector aEmail2 = new Vector();
		Vector aSize2 = new Vector();
		for (int n = getMessageNum() - 1; n >= 0; n--) {
			aEmail2.addElement(aEmail.elementAt(n));
			aSize2.addElement(aSize.elementAt(n));
		}
		aEmail = aEmail2;
		aSize = aSize2;
	}

	public int getMessageSize(int nPos) {
		return ((Double) aSize.elementAt(nPos - 1)).intValue();
	}

	public String getMessageID(int nPos) {
		return (String) aEmail.elementAt(nPos - 1);
	}

	/*
	 * // Funzioni di getmail public String getMessage( int nPos ) { return getMessage( nPos, 0, true ); } public String
	 * getMessageTop( int nPos, int nLine ) { return getMessage( nPos, nLine, false ); }
	 * 
	 * 
	 * protected abstract String getMessage( int nPos, int nLine, boolean bAll );
	 */

	public boolean streamMessage(OutputStream SO, int nPos) throws Exception {
		return streamMessage(SO, nPos, 0, true);
	}

	public boolean streamMessageTop(OutputStream SO, int nPos, int nLine) throws Exception {
		return streamMessage(SO, nPos, nLine, false);
	}

	public boolean streamMessage(OutputStream SO, int nPos, int nLine, boolean bAll) throws Exception {
		return false;
	}

}
