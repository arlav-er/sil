package it.eng.sil.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class Section extends BodyTagSupport {
	public Section() {
	}

	private String name;

	public void setName(String s) {
		this.name = s;
	}

	public String getName() {
		return this.name;
	}

	protected Sections sections = null;

	public int doStartTag() throws JspException {
		Tag parent = getParent();
		if (parent instanceof Sections) {
			sections = (Sections) parent;
			if (!sections.hasSection(getName()))
				return Tag.SKIP_BODY;
		} else {
			Tag o = TagSupport.findAncestorWithClass(this, Sections.class);
			if (o == null)
				throw new JspException("impossibile trovare il tag Sections");
			Sections sections = (Sections) o;
			if (!sections.hasSection(getName()))
				return Tag.SKIP_BODY;
		}
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		String buffer = bodyContent.getString();
		sections.setBuffer(getName(), buffer);
		return Tag.EVAL_PAGE;
	}
}