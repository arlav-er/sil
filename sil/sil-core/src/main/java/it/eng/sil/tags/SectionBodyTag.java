package it.eng.sil.tags;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.util.patto.PageProperties;

/**
 * Generated tag class.
 */
public class SectionBodyTag extends BodyTagSupport {

	private String args;
	private StringBuffer sb = new StringBuffer();
	private List rows;
	private Iterator rowsIter;

	public String getArgs() {
		return this.args;
	}

	public int doStartTag() throws JspException {
		sb.append("<TABLE id='TBL");
		sb.append(((DynamicSectionTag) getParent()).getPrg());
		sb.append("' style='width:100%;display:");
		sb.append(getState() ? "inline" : "none");
		sb.append("'><script>initSezioni(new Sezione(document.getElementById('TBL");
		sb.append(((DynamicSectionTag) getParent()).getPrg());
		sb.append("'),document.getElementById('IMG");
		sb.append(((DynamicSectionTag) getParent()).getPrg());
		sb.append("'),");
		sb.append(getState());
		sb.append("));</script><TR><TD>");
		rows = ((DynamicSectionTag) getParent()).getRows();
		rowsIter = rows.iterator();
		if (rowsIter.hasNext()) {
			pageContext.setAttribute("row", rowsIter.next());
			// return BodyTag.EVAL_BODY_BUFFERED;
		} else {
			PageProperties pageProperties = (PageProperties) pageContext.getAttribute("pageProperties");
			if (pageProperties.getProperty(getName()).isVisualizzaStruttura()) {
				try {
					pageContext.setAttribute("row", new SourceBean("ROOT"));
				} catch (Exception e) {
					throw new JspException(e.getMessage());
				}
				// return BodyTag.EVAL_BODY_BUFFERED;
			} else
				return Tag.SKIP_BODY;
		}
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspException {
		if (rowsIter.hasNext()) {
			Object o = rowsIter.next();
			pageContext.setAttribute("row", o);
			return IterationTag.EVAL_BODY_AGAIN;
		} else
			return Tag.SKIP_BODY;

	}

	public int doEndTag() throws JspException, JspException {
		if (bodyContent != null) {
			sb.append(bodyContent.getString());
		}
		sb.append("</td></tr></table>");
		((DynamicSectionTag) getParent()).setBody(sb.toString());
		return Tag.EVAL_PAGE;
	}

	public String getBody() {
		return this.sb.toString();
	}

	private boolean getState() {
		return ((DynamicSectionTag) getParent()).getState();
	}

	private String getName() {
		return ((DynamicSectionTag) getParent()).getName();
	}

	public void release() {
		sb = new StringBuffer();
		args = null;
		rowsIter = null;
		if (bodyContent != null) {
			// try {
			bodyContent.clearBody();
			// }catch(Exception e) {}
		}
	}
}
