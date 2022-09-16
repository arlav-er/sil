package it.eng.sil.tags.patto;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.SourceBean;

public class PattoAssociatoInList extends TagSupport {
	SourceBean row;
	String key;

	public void setRow(SourceBean sb) {
		this.row = sb;
	}

	public void setKey(String k) {
		this.key = k;
	}

	public int doStartTag() throws JspException {
		if (row.getAttribute("PRGLAVPATTOSCELTA") == null) {
			return Tag.SKIP_BODY;
		} else {
			return Tag.EVAL_BODY_INCLUDE;
		}
	}

	public int doEndTag() throws JspException {
		String tipoPatto = (String) row.getAttribute("FLGPATTO297");
		String tipoPattoTitolo = null;

		if (tipoPatto != null) {
			tipoPattoTitolo = tipoPatto.equalsIgnoreCase("S") ? "Legame col " : "Legame con ";
			tipoPatto = tipoPatto.equalsIgnoreCase("S") ? "Patto 150" : "Accordo Generico";
		}

		String dataStipula = (String) row.getAttribute("datstipula");
		String statoAtto = (String) row.getAttribute("STATOATTO");
		BigDecimal pk = (BigDecimal) row.getAttribute("PRGLAVPATTOSCELTA");
		String pkLavPattoScelta = (pk == null) ? "" : pk.toString();

		if (dataStipula != null) {
			try {
				pageContext.getOut()
						.println("<script>pkLavPattoScelta.put('" + key + "','" + pkLavPattoScelta + "');</script>");
				pageContext.getOut().println("<table><tr><td>&nbsp;</td></tr>");
				pageContext.getOut().println("<tr><td>" + tipoPattoTitolo + "&nbsp;<b>" + tipoPatto + "</b></td></tr>");
				pageContext.getOut()
						.println("<tr><td >del <b>" + dataStipula + "&nbsp;</b>(" + statoAtto + ")</td></tr>");
				pageContext.getOut().println("</table>");
			} catch (IOException e) {
				throw new JspException(e);
			}
		}

		return Tag.EVAL_PAGE;
	}
}
