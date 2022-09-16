package it.eng.sil.tags.patto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import it.eng.sil.tags.Sections;
import it.eng.sil.util.patto.PageProperties;

public class TitoloCampiTag extends TagSupport {
	private String titolo;
	private List valori;
	private boolean compatto = false;
	private boolean ripetuto = false;
	private Iterator campi;
	private boolean sezioneVuota = false;
	private String nome;

	public TitoloCampiTag() {
	}

	public void setNome(String s) {
		this.nome = s;
	}

	/*
	 * public void setCompatto(boolean newCompatto) { this.compatto = newCompatto; }
	 */
	public void setSezioneVuota(boolean newVuota) {
		this.sezioneVuota = newVuota;
	}

	public void setRipetuto(boolean newRipetuto) {
		this.ripetuto = newRipetuto;
	}

	/*
	 * public boolean getCompatto() { return this.compatto; }
	 */
	public boolean getRipetuto() {
		return this.ripetuto;
	}

	public void setTitolo(String newTitolo) {
		this.titolo = newTitolo;
	}

	public String getTitolo() {
		return this.titolo;
	}

	public void setValori(List newValori) {
		if (newValori == null) {
			newValori = new ArrayList();
		}

		this.valori = newValori;
	}

	public List getValori() {
		return this.valori;
	}

	public int doStartTag() throws JspException {
		// gestione degli attributi di visibilita' della sottosezione
		Tag o = TagSupport.findAncestorWithClass(this, Sections.class);
		if (o == null)
			throw new JspException("impossibile trovare il tag Sections");
		Sections sections = (Sections) o;
		if (!sections.hasSection(this.nome))
			return Tag.SKIP_BODY;
		PageProperties pageProperties = (PageProperties) pageContext.getAttribute("pageProperties");

		try {
			compatto = !pageProperties.getProperty(nome).isElencoEspanso();
		} catch (Exception e) {
			compatto = false;
		}

		if (sezioneVuota && (valori.size() == 0)) {
			return Tag.SKIP_BODY;
		}

		if (compatto) {
			pageContext.setAttribute("_titolo", titolo);
			pageContext.setAttribute("_campo", getValoriCompatti());
		} else {
			pageContext.setAttribute("_titolo", titolo);
			campi = valori.iterator();

			String val = "";

			if (campi.hasNext()) {
				val = (String) campi.next();
			}

			pageContext.setAttribute("_campo", val);
		}

		return Tag.EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public int doAfterBody() throws JspException {
		if (compatto) {
			return Tag.SKIP_BODY;
		}

		if (!ripetuto) {
			pageContext.setAttribute("_titolo", "");
		}

		if (campi.hasNext()) {
			pageContext.setAttribute("_campo", (String) campi.next());

			return IterationTag.EVAL_BODY_AGAIN;
		} else {
			return Tag.SKIP_BODY;
		}
	}

	private String getValoriCompatti() {
		StringBuffer sb = new StringBuffer();

		// sb.append("<table><tr><td>");
		for (int i = 0; i < valori.size(); i++) {
			sb.append((String) valori.get(i));
			sb.append(", ");
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 2);
		}

		// sb.append("</td></tr></table>");
		return sb.toString();
	}
}
