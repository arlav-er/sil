package it.eng.sil.tags;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author user
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class SectionActionTag extends BodyTagSupport {
	private String img;
	private String onclick;
	private boolean addParams;

	public void setImg(String s) {
		this.img = s;
	}

	public void setOnclick(String s) {
		this.onclick = s.trim();
	}

	public void setAddParams(boolean b) {
		this.addParams = b;
	}

	public int doStartTag() {
		Boolean isReadOnly = (Boolean) pageContext.getAttribute("isReadOnly");
		if (!isReadOnly.booleanValue()) {
			DynamicSectionTag section = (DynamicSectionTag) getParent();
			section.addAction(new SectionActionHTML(img, onclick, addParams));
		}
		return Tag.SKIP_BODY;
	}

	public void release() {
		this.addParams = false;
	}

	class SectionActionHTML {
		String img;
		String onclick;
		boolean addParams;

		SectionActionHTML(String newImg, String newOnclick, boolean newAddParams) {
			this.img = newImg;
			this.onclick = newOnclick;
			this.addParams = newAddParams;
		}

		public String toHTML(String args) {
			StringBuffer sb = new StringBuffer();
			if (addParams && args.trim().length() == 0)
				return ""; // non ci sono dati nella lista
			sb.append("<a  href='#' alt='Cancella tutti' onclick='");
			sb.append(onclick);
			if (addParams)
				sb.insert(sb.length() - 1, args.substring(0, args.length() - 1));
			sb.append("'><img src='");
			sb.append(img);
			sb.append("'></a>");
			return sb.toString();
		}
	}

}
