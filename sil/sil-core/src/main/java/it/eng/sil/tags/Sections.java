package it.eng.sil.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import it.eng.sil.security.PageAttribs;
import it.eng.sil.util.patto.PageProperties;

/**
 * @todo sistema il metodo setSectionList()
 * 
 */
public class Sections extends TagSupport {
	/**
	 * Se pageAttribs e' null allora gestisco le sezione sequenzialmente
	 */
	private PageAttribs pageAttribs;
	private List buffers;
	private List list = null;

	public Sections() {
	}

	/**
	 * Gli attributi di accessibilita' dell' utente (informa sulla visibilita' o meno di una sezione per uno specifico
	 * utente). Se null si considera che l' utente abbia accesso a tutte le sezioni.
	 */
	public void setPageAttribs(PageAttribs a) {
		this.pageAttribs = a;
	}

	public int doStartTag() {
		PageProperties pp = (PageProperties) pageContext.getAttribute("pageProperties");
		setSectionList(pp, pageAttribs);
		buffers = new ArrayList(list.size());

		for (int i = 0; i < list.size(); i++)
			buffers.add("");

		return Tag.EVAL_BODY_INCLUDE;

		// return Tag.SKIP_BODY;
	}

	public int doAfterBody() throws JspException {
		try {
			for (int i = 0; i < buffers.size(); i++)
				pageContext.getOut().print(buffers.get(i));
		} catch (IOException ioe) {
			throw new JspException(ioe);
		}

		return Tag.EVAL_PAGE;
	}

	public boolean hasSection(String name) {
		boolean ret = false;

		for (int i = 0; i < list.size(); i++) {
			PageProperties.PageProperty property = (PageProperties.PageProperty) list.get(i);

			if (property.getNome().equals(name)) {
				ret = true;

				break;
			}
		}

		return ret;
	}

	public void setBuffer(String name, String s) {
		buffers.set(getSectionIndex(name), s);
	}

	public int getSectionIndex(String name) {
		int i = 0;

		for (; i < list.size(); i++) {
			PageProperties.PageProperty property = (PageProperties.PageProperty) list.get(i);

			if (property.getNome().equals(name)) {
				break;
			}
		}

		if (i == list.size()) {
			i = -1;
		}

		return i;
	}

	private void setSectionList(PageProperties pageProps, PageAttribs pageAttr) {
		list = new ArrayList();

		if (pageAttr == null) {
			if (pageProps == null) {
				list = new ArrayList();
				return;
			}
			List properties = pageProps.getProperties();
			for (int i = 0; i < properties.size(); i++) {
				PageProperties.PageProperty property = (PageProperties.PageProperty) properties.get(i);

				if (!property.getMonoVisualizzazione().equals("N")) {
					list.add(property);
				}
			}

			return;
		}
		if (pageProps == null) {
			List attributes = pageAttr.getSectionList();
			for (int i = 0; i < attributes.size(); i++) {
				PageProperties.PageProperty prop = new PageProperties.PageProperty();
				prop.setNome((String) attributes.get(i));
				prop.setPosizione(i);
				list.add(prop);
			}
			return;
		}

		List attributes = pageAttr.getSectionList();
		List properties = pageProps.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			PageProperties.PageProperty property = (PageProperties.PageProperty) properties.get(i);

			if (attributes.contains(property.getNome())) {
				if (!property.getMonoVisualizzazione().equals("N")) {
					list.add(property);
				}
			}
		}

	}
}
