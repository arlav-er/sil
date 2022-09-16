package it.eng.sil.util.grabber;/*
								* HTMLTool
								*
								* Copyright 2003 Matteo Baccan <baccan@infomedia.it>
								* www - http://www.baccan.it
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
 * Title:        HTMLtool
 * Description:  Classe di supporto HTML
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author Matteo Baccan
 * @version 1.0
 */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class htmlTool {

	public String getHeader(InputStream is) throws Throwable {
		String cRet = "";
		String cLine;
		do {
			cLine = getLine(is);
			cRet += cLine;
		} while (cLine.length() > 2);

		return cRet;
	}

	public ByteArrayOutputStream getData(InputStream is, int nLen, boolean bAll, int nLine) throws Throwable {
		int nPosR = -10;
		boolean endOfHdr_1stBlankLine = false;
		ByteArrayOutputStream cReply = new ByteArrayOutputStream();

		int nChar;
		while (true) {
			try {
				if (cReply.size() == nLen)
					break;

				nChar = is.read();

				if (nChar == -1)
					break;

				// Questo IF e' ancora tutto da verifiare
				if (!bAll) {
					if (nChar == '\r') {
						if (cReply.size() - nPosR <= 2)
							endOfHdr_1stBlankLine = true;
						nPosR = cReply.size();
						if (endOfHdr_1stBlankLine) {
							if (nLine <= 0)
								break;
							nLine--;
						}
					}
				}
				// Questo IF e' ancora tutto da verifiare

			} catch (Throwable e) {
				break;
			}
			cReply.write(nChar);
		}

		return cReply;
	}

	public String getLine(InputStream is) throws Throwable {
		StringBuffer cReply = new StringBuffer();

		int nChar;
		int nLastChar = 0;
		while (true) {
			try {
				nChar = is.read();
				if (nChar == -1)
					break;

				// If EOL
				if (nChar == '\n' && nLastChar == '\r') {
					// exit at the first empty line
					cReply.append((char) nChar);
					break;
				}

				nLastChar = nChar;
			} catch (Throwable e) {
				break;
			}
			cReply.append((char) nChar);
		}

		return cReply.toString();
	}

	public boolean keepAlive(String cHeader) {
		return cHeader.toUpperCase().indexOf("KEEP-ALIVE") != -1;
	}

	public int contentLength(String cHeader) {
		int nRet = 0;
		try {
			String cLen = getProp(cHeader, "CONTENT-LENGTH:");
			nRet = Integer.parseInt(cLen);
		} catch (Throwable e) {
		}
		return nRet;
	}

	public String setContentLen(String cHeader, int nLen) {
		// Uppercase, browsers use this setting in a different way
		String cNewHeader = cHeader.toUpperCase();

		int nPos = cNewHeader.indexOf("CONTENT-LENGTH:");
		int nSpace = cNewHeader.indexOf(" ", nPos);
		int nSpace2 = cNewHeader.indexOf("\r", nSpace);

		return cHeader.substring(0, nSpace) + " " + nLen + cHeader.substring(nSpace2);
	}

	public String location(String cHeader) {
		return getProp(cHeader, "LOCATION:");
	}

	public String setcookie(String cHeader) {
		return getProp(cHeader, "SET-COOKIE:");
	}

	public String transferEncoding(String cHeader) {
		return getProp(cHeader, "TRANSFER-ENCODING:");
	}

	public String contentDisposition(String cHeader) {
		return getProp(cHeader, "CONTENT-DISPOSITION:");
	}

	public String contentType(String cHeader) {
		return getProp(cHeader, "CONTENT-TYPE:");
	}

	public String getFirstLine(String cHeader) {
		int nPos = cHeader.indexOf("\r");
		return cHeader.substring(0, nPos);
	}

	public boolean isHTML(String cHeader) {
		return contentType(cHeader).equalsIgnoreCase("text/html");
	}

	public boolean isChunked(String cHeader) {
		return transferEncoding(cHeader).equalsIgnoreCase("CHUNKED");
	}

	private String getProp(String cHeader, String cProp) {
		String cRet = "";
		try {
			// Normalize
			String cHeaderU = cHeader.toUpperCase();

			int nPos = cHeaderU.indexOf(cProp.toUpperCase());
			int nSpace = cHeaderU.indexOf(" ", nPos);
			int nSpace2 = cHeaderU.indexOf("\n", nSpace);

			cRet = cHeader.substring(nSpace, nSpace2).trim();
		} catch (Throwable e) {
			cRet = "";
		}

		return cRet;
	}

	public void putData(OutputStream os, String cOut) throws Throwable {
		os.write(cOut.getBytes());
		os.flush();
	}

	public void putData(OutputStream os, ByteArrayOutputStream cOut) throws Throwable {
		os.write(cOut.toByteArray());
		os.flush();
	}
}
