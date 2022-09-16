package it.eng.sil.tags.presel;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;

/**
 * Crea una combobox con la lista delle mansioni, opzionalmente può esserci una riga "Tutte" e le mansioni essere
 * filtrate.
 * 
 * Esempio: <customTL:mansioniComboBoxTag name="combobox_mansioni" moduleName="M_ListMansioni" insertOptionTutte="true"
 * multipleSelection="true" />
 * 
 * Significato degli attributi: name : il nome del combo-box moduleName : nome del modulo da cui reperire i dati, ad
 * esempio M_ListMansioniDisponibileLavoro oppure M_ListMansioni insertOptionTutte : (true/false, default true) indica
 * se inserire l'option "Tutte" in cima alla lista, che avrà codice "" multipleSelection : (true/false, default true)
 * indica se mostrare le mansioni in una lista che supporti la multiselezione. Se true allora insertOptionTutte viene
 * ignorato e considerato "false".
 * 
 * 
 * @author: Corrado Vaccari
 */
public class MansioniComboBoxTag extends TagSupport {

	private String name;
	private String moduleName = "";
	private boolean insertOptionTutte = false;
	private boolean multipleSelection = true;
	private List selectedList = null;

	public int doEndTag() throws JspException {

		if ((this.name == null) || (this.name.length() == 0))
			throw new IllegalArgumentException("Missing tag name");

		// Le due opzioni non possono essere settate entrambe
		if (multipleSelection)
			insertOptionTutte = false;

		HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		SourceBean serviceResponse = responseContainer.getServiceResponse();

		try {

			/*
			 * <select name="PRGMANSIONE" > <option value="" selected >Tutte</option> <option value="352">Agente di
			 * assicurazione</option> <option value="354">Assistente bagnanti</option> <option value="346">Direttore
			 * generale</option> <option value="245">Ingegnere elettrotecnico</option> </select>
			 * 
			 * In Xml ci si aspetta i dati in un SourceBean con: <ROWS> ... <ROW PRGMANSIONE=".." DESC_MANSIONE=".." />
			 * ... </ROWS>
			 */

			JspWriter out = pageContext.getOut();

			out.print("<SELECT name=\"");
			out.print(this.name);
			out.print("\" ");
			if (multipleSelection)
				out.print("MULTIPLE ");
			out.print(">");

			Vector vectRows = serviceResponse.getAttributeAsVector(moduleName + ".ROWS.ROW");

			if (insertOptionTutte && (vectRows.size() > 1)) {

				out.print("<OPTION VALUE=\"\" SELECTED >Tutte</OPTION>");
			}

			for (Iterator iter = vectRows.iterator(); iter.hasNext();) {

				SourceBean beanMansione = (SourceBean) iter.next();

				String prgMansione = beanMansione.getAttribute("PRGMANSIONE").toString();
				String descMansione = StringUtils.getAttributeStrNotNull(beanMansione, "DESC_MANSIONE");

				StringBuffer sb = new StringBuffer();
				sb.append("<OPTION VALUE=\"");
				sb.append(prgMansione).append("\"");
				// Il "prgMansione" è nella lista degli elementi selezionati?
				if ((selectedList != null) && selectedList.contains(prgMansione)) {
					sb.append(" SELECTED=\"true\"");
				}
				sb.append(">");
				sb.append(descMansione);
				sb.append("</OPTION>");
				out.print(sb.toString());
			}

			out.print("</SELECT>");
		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_PAGE;
	}

	/**
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * 
	 */
	public void setInsertOptionTutte(String insertOptionTutte) {
		this.insertOptionTutte = Boolean.getBoolean(insertOptionTutte);
	}

	/**
	 * 
	 */
	public void setMultipleSelection(String multipleSelection) {
		this.multipleSelection = Boolean.getBoolean(multipleSelection);
	}

	public void setSelectedList(List list) {
		selectedList = list;
	}

	public void release() {
		super.release();
		selectedList = null;
	}

}