package it.eng.sil.tags;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import it.eng.sil.util.patto.PageProperties;

public class DynamicSectionTag extends Section {
	boolean state;
	int prg;
	List rows;
	List actions = new ArrayList();
	String body;
	String title;
	boolean filtra;

	public DynamicSectionTag() {
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public void setTitolo(String s) {
		this.title = s;
	}

	public void setFiltra(boolean filtra) {
		this.filtra = filtra;
	}

	public boolean getFiltra() {
		return filtra;
	}

	public int doStartTag() throws JspException {
		int ret = super.doStartTag();

		if (ret == Tag.SKIP_BODY) {
			return ret;
		} else {
			PageProperties pageProperties = (PageProperties) pageContext.getAttribute("pageProperties");
			String visualizzazione = pageProperties.getProperty(getName()).getMonoVisualizzazione();

			if (visualizzazione.equals("S")) {
				setState();

				return ret;
			} else {
				// visualizzo la sezione solo se ci sono i dati
				if (rows.size() > 0) {
					setState();
					return ret;
				} else {
					// per una corretta gestione delle sezioni aperte e chiuse
					// debbo aggiungere uno script js?si
					sections.setBuffer(getName(), "<script>initSezioni(new Sezione('','',false));</script>");
					return Tag.SKIP_BODY;
				}
			}
		}
	}

	public int doAfterBody() {
		StringBuffer sb = new StringBuffer();
		sb.append("<table width='100%'>");
		sb.append("<tr><td>");
		sb.append(makeHeader());
		sb.append("</td></tr><tr><td>");
		sb.append(body);
		sb.append("</td></tr></table>");
		sections.setBuffer(getName(), sb.toString());

		return Tag.EVAL_PAGE;
	}

	public void addAction(Object action) {
		actions.add(action);
	}

	private String makeHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("<table class='sezione' cellspacing=0 cellpadding=0>");
		sb.append("<tr><td  width=18><img id='IMG");
		sb.append(getPrg());
		sb.append("' src='");
		sb.append(getImg());
		sb.append("' onclick='");
		sb.append(getOnClick());
		sb.append("'>");
		sb.append("</td><td  class='titolo_sezione'>");
		sb.append(title);
		// sb.append("</td>");

		if (getFiltra()) {
			sb.append("&nbsp;<img id='IMG");
			sb.append(getPrg());
			sb.append("' alt='Filtra per data corrente' src='");
			sb.append(getImgFiltro());
			sb.append("' onclick='");
			sb.append(getOnClickFiltra());
			sb.append("'>");
			sb.append("&nbsp;<img id='IMG");
			sb.append(getPrg());
			sb.append("' alt='Non filtrare per data corrente' src='");
			sb.append(getImgTogliFiltro());
			sb.append("' onclick='");
			sb.append(getOnClickTogliFiltro());
			sb.append("'>");
		}
		sb.append("</td>");

		sb.append(getActions());
		sb.append("</tr></table>");

		return sb.toString();
	}

	private String getImg() {
		return getState() ? "../../img/aperto.gif" : "../../img/chiuso.gif";
	}

	private String getImgFiltro() {
		return "../../img/filtro.gif";
	}

	private String getImgTogliFiltro() {
		return "../../img/togli_filtro.gif";
	}

	private String getOnClick() {
		return "cambia(this, document.getElementById(\"TBL" + prg + "\"))";
	}

	private String getOnClickTogliFiltro() {
		return "togliFiltro()";
	}

	private String getOnClickFiltra() {
		return "filtro()";
	}

	public boolean getState() {
		return this.state;
	}

	private void setState() {
		/*
		 * //BigInteger prgSection = (BigInteger) pageContext.getAttribute("prgSection"); String sectionState = (String)
		 * pageContext.getAttribute("sectionState"); //pageContext.setAttribute("prgSection",
		 * BigInteger.valueOf(prgSection.longValue() + 1));
		 * 
		 * if (prgSection.longValue() < sectionState.length()) { state = sectionState.charAt((int)
		 * prgSection.longValue()) == '1'; } else { state = '1' == '1'; }
		 * 
		 * prg = (int) prgSection.longValue();
		 */
		String sectionState = (String) pageContext.getAttribute("sectionState");
		prg = sections.getSectionIndex(getName());
		if (prg < sectionState.length()) {
			state = sectionState.charAt(prg) == '1';
		} else {
			state = '1' == '1';
		}
	}

	private String getActions() {
		StringBuffer args = (StringBuffer) pageContext.getAttribute("args");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < actions.size(); i++) {
			SectionActionTag.SectionActionHTML action = (SectionActionTag.SectionActionHTML) actions.get(i);
			sb.append("<td align='right' width='30'>");
			sb.append(action.toHTML(args.toString()));
			sb.append("</td>");
		}

		return sb.toString();
	}

	public int getPrg() {
		return prg;
	}

	public List getRows() {
		return rows;
	}

	public void setBody(String s) {
		this.body = s;
	}

	public void release() {
		this.actions = new ArrayList();
		this.rows = null;
		this.state = false;
		this.body = null;
		((StringBuffer) pageContext.getAttribute("args")).setLength(0);

	}
}
