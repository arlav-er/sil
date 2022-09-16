package it.eng.sil.tags.presel;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.RowsFlatIterator;
import it.eng.afExt.utils.StringUtils;

/**
 * Esegue il rendering della lista territorio in cui si raggruppano per mansione: - Comuni - Province - Regioni - Stati
 * 
 * Esempio: &lt;ps:territorioList listDataPath = "M_LISTTERRITORIMANSIONI.SECTION" canDelete = "true" /&gt;
 * 
 * Spiegazione Attributi: listDataPath : (obbligatorio) Path nella request del SourceBean contenente i dati con cui
 * riempire la lista. canDelete : (obbligatorio) Stringa contenente l'indicazione se gli elementi della lista possono
 * essere cancellati, quindi se l'icona X di cancellazione deve essere inserita. Valori ammessi "true" o "false"
 * 
 * @created November 20, 2003
 * @author: Corrado Vaccari
 */
public class TerritorioListTag extends TagSupport {

	private String listDataPath;
	private boolean canDelete;

	private RequestContainer requestContainer;
	private SourceBean serviceRequest;
	private ResponseContainer responseContainer;
	private SourceBean serviceResponse;

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 * @exception JspException
	 *                Description of the Exception
	 */
	public int doEndTag() throws JspException {

		HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();

		requestContainer = RequestContainerAccess.getRequestContainer(httpRequest);
		serviceRequest = requestContainer.getServiceRequest();

		responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
		serviceResponse = responseContainer.getServiceResponse();

		SourceBean section = (SourceBean) serviceResponse.getAttribute(this.listDataPath);
		RowsFlatIterator rowsIterator = new RowsFlatIterator(section);

		try {

			/*
			 * <TABLE class="lista" align="center"> <tr> <td> <A HREF="javascript:MansioneSelect(225);"><IMG
			 * name="image" border="0" src="../../img/detail.gif" alt="Consulta o modifica
			 * mansione" /></A> <A HREF="javascript:MansioneDelete(225, 'Altri specialisti in scienze matematiche,
			 * fisiche e naturali');"><IMG name="image" border="0" src="../img/del.gif" alt="Elimina mansione" /></A>
			 * &nbsp;Mansione <b>Altri specialisti in scienze matematiche, fisiche e naturali </td> </tr> <tr><td
			 * colspan="2" valign="top"><hr/></td> </tr> <tr> <td> <A HREF="javascript:MansioneSelect(241);"><IMG
			 * name="image" border="0" src="../../img/detail.gif" alt="Consulta o modifica mansione" /></A> <A
			 * HREF="javascript:MansioneDelete(241, 'Chimici');"><IMG name="image" border="0" src="../../img/del.gif"
			 * alt="Elimina mansione" /></A> &nbsp;Mansione <b>Chimici </td> </tr> </TABLE>
			 */
			// HttpServletRequest req = ( HttpServletRequest )
			// pageContext.getRequest();
			JspWriter out = pageContext.getOut();

			out.print("<TABLE class=\"lista\" align=\"center\">");

			String lastPrgMansione = "";

			String lastTipo = "";

			while (rowsIterator.hasNext()) {

				SourceBean beanElem = (SourceBean) rowsIterator.next();

				String prgMansione = beanElem.getAttribute("PRGMANSIONE").toString();
				Object prg = beanElem.getAttribute("PRG");

				String descMansione = StringUtils.getAttributeStrNotNull(beanElem, "DESC_MANSIONE");
				String desc = StringUtils.getAttributeStrNotNull(beanElem, "DESCRIZIONE");
				String note = StringUtils.getAttributeStrNotNull(beanElem, "NOTE");

				String tipo = (String) beanElem.getAttribute("TIPO");

				boolean isNuovaMansione = (lastPrgMansione.length() == 0) || !lastPrgMansione.equals(prgMansione);

				if (isNuovaMansione) {

					if (lastPrgMansione.length() > 0) {

						// Se non è la prima mansione,
						// inserisce il separatore
						insertSeparator(out);
					}

					printRow(out, "Mansione <b>" + descMansione + "</b>", 0);

					lastPrgMansione = prgMansione;
					lastTipo = "";
				}

				String descTipo = "";

				String funcName = "";

				String descElem = "";

				if (tipo.equals("COMUNE")) {
					descTipo = "Comuni";
					funcName = "ComuneDelete";
				} else if (tipo.equals("PROVINCIA")) {
					descTipo = "Province";
					funcName = "ProvinciaDelete";
				} else if (tipo.equals("REGIONE")) {
					descTipo = "Regioni";
					funcName = "RegioneDelete";
				} else if (tipo.equals("STATO")) {
					descTipo = "Stati";
					funcName = "StatoDelete";
				}

				if (!lastTipo.equals(tipo)) {

					printRow(out, descTipo, 1);
				}

				/*
				 * <A HREF="javascript:ProvinciaDelete( <%= Utils.notNull(prgDisProvincia) %>, '<%=
				 * descProvincia.replace('\'', '^') %>', '<%= descMansione.replace('\'', '^') %>' );"> <IMG name="image"
				 * border="0" src="../../img/del.gif" alt="Elimina turno" /> </A>
				 */
				StringBuffer sb = new StringBuffer();
				sb.append("<A HREF=\"javascript:");
				sb.append(funcName);
				sb.append("(");
				sb.append(prg);
				sb.append(",\'");
				sb.append(desc.replace('\'', '^'));
				sb.append("\',\'");
				sb.append(descMansione.replace('\'', '^'));
				sb.append("\');\">");
				if (canDelete) {
					sb.append("<IMG name=\"image\" border=\"0\" src=\"../../img/del.gif\" alt=\"Elimina elemento\" />");
				}
				sb.append("</A>");
				sb.append("&nbsp;<b>");
				sb.append(desc);
				sb.append("</b>");

				printRow(out, sb.toString(), 2);

				if (note.length() > 0) {

					sb = new StringBuffer();
					sb.append("Note <b>");
					sb.append(note);
					sb.append("</b>");

					printRow(out, sb.toString(), 3);
				}

				lastTipo = tipo;
			}

			out.print("</TABLE>");

		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_PAGE;
	}

	/**
	 * Sets the listDataPath attribute of the TerritorioListTag object
	 * 
	 * @param listDataPath
	 *            The new listDataPath value
	 */
	public void setListDataPath(String listDataPath) {

		this.listDataPath = listDataPath;
	}

	/**
	 * Sets the canDelete attribute of the TerritorioListTag object
	 * 
	 * @param canDelete
	 *            The new canDelete value
	 */
	public void setCanDelete(String canDelete) {

		this.canDelete = canDelete.equalsIgnoreCase("true");
	}

	/**
	 * Inserisce un separatore.
	 * 
	 * @param out
	 *            Writer
	 * @exception IOException
	 *                In caso di errore
	 */
	private void insertSeparator(JspWriter out) throws IOException {

		out.print("<tr>");
		out.print("  <td colspan=\"2\" valign=\"top\"><hr/></td>");
		out.print("</tr>");
	}

	/**
	 * Inserisce una table-row HTML
	 * 
	 * @param out
	 *            Writer
	 * @param data
	 *            Dati da inserire
	 * @param indent
	 *            Livello di indentazione richiesto
	 * @exception IOException
	 *                In caso di errore
	 */
	private void printRow(JspWriter out, String data, int indent) throws IOException {

		for (int i = 0; i < indent; i++) {
			data = "&nbsp;" + data;
		}

		out.print("<tr><td>");
		out.print(data);
		out.print("</td></tr>");
	}
}
