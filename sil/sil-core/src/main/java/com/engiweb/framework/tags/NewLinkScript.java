package com.engiweb.framework.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Questa classe serve per importare nella JSP gli script JavaScript per permettere tutte le funzionalità di "TextBox" e
 * degli altri custom-Tag.
 * 
 * Esempio di uso: &lt;customTL:linkScript path="/jellicle/js/" /&gt;
 * 
 * path ........ contiene il percorso per arrivare ai file JavaScript. La directory deve almeno contenere i due file: -
 * "FormCheck.js" - "customTL.js"
 * 
 */

public class NewLinkScript extends TagSupport {
	private String path = ""; // Elenco attributi con il lovo valore di
								// DEFAULT!!!

	public int doStartTag() throws JspException {
		try {
			// HttpServletRequest req = ( HttpServletRequest )
			// pageContext.getRequest();
			JspWriter out = pageContext.getOut();

			out.println("<SCRIPT language=\"JavaScript\" src=\"" + path + "FormCheck.js\"></SCRIPT>");
			out.println("<SCRIPT language=\"JavaScript\" src=\"" + path + "customTL.js\"></SCRIPT>");
			out.println("<SCRIPT language=\"JavaScript\" src=\"" + path + "calendar.js\"></SCRIPT>");

		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return SKIP_BODY;
	} // doStartTag

	public String getPath() {
		return path;
	}

	public void setPath(String newPath) {
		path = newPath;
	}

} // LinkScript
