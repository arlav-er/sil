package it.eng.sil.module.agenda;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.ContextScooping;
import com.engiweb.framework.util.JavaScript;

public class CalUtils {
	public CalUtils() {
	}

	/* @author: Stefania Orioli */
	/*
	 * Questo metodo popola l'ArrayList GiorniNL con le date dei giorni da contrassegnare come non lavorativi tenendo
	 * conto di data di inizio validità e data di fine validità della regola.
	 */
	public static ArrayList setGiorniNL(int anno, SourceBean serviceResponse, String moduleName) {
		SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
		ArrayList giorniNL = new ArrayList();
		// gsett = new ArrayList();
		if (content != null) {
			Vector rowsNL = content.getAttributeAsVector("ROWS.ROW");
			SourceBean row = null;
			String gnl;
			int i, num_gg, num_mm, num_aa;
			int gi, mi, ai, gf, mf, af;
			Calendar datInizio = Calendar.getInstance();
			Calendar datFine = Calendar.getInstance();
			Calendar dataGnl = Calendar.getInstance();
			for (i = 0; i < rowsNL.size(); i++) {
				row = (SourceBean) rowsNL.elementAt(i);
				gnl = "";
				if (row.containsAttribute("numgg") && row.containsAttribute("nummm")) {
					num_gg = Integer.parseInt(row.getAttribute("numgg").toString());
					num_mm = Integer.parseInt(row.getAttribute("nummm").toString()) - 1;
					if (row.containsAttribute("numaaaa")) {
						num_aa = Integer.parseInt(row.getAttribute("numaaaa").toString());
					} else {
						num_aa = anno;
					}
					gnl = num_gg + "/" + num_mm + "/" + num_aa;
					dataGnl.set(num_aa, num_mm, num_gg);
					gi = Integer.parseInt(row.getAttribute("GI").toString());
					mi = Integer.parseInt(row.getAttribute("MI").toString()) - 1;
					ai = Integer.parseInt(row.getAttribute("AI").toString());
					datInizio.set(ai, mi, gi);
					gf = Integer.parseInt(row.getAttribute("GF").toString());
					mf = Integer.parseInt(row.getAttribute("MF").toString()) - 1;
					af = Integer.parseInt(row.getAttribute("AF").toString());
					datFine.set(af, mf, gf);
					if ((dataGnl.after(datInizio) || dataGnl.equals(datInizio))
							&& (dataGnl.before(datFine) || dataGnl.equals(datFine))) {
						giorniNL.add(gnl);
					}
				} /*
					 * else { if(row.containsAttribute("numgsett")) {
					 * gsett.add(row.getAttribute("numgsett").toString()); } }
					 */
				gnl = null;
			} // for
		} // if(content!=null)
		return (giorniNL);
	}

	/*
	 * Questo metodo popola l'ArrayList gsett con i "numeri" dei giorni definiti di riposo settimanale (il controllo su
	 * data di inizio validità e data di fine validità a questo livello non ha senso e viene eseguito run-time dal
	 * metodo checkGsett).
	 */
	public static ArrayList setGsett(int anno, SourceBean serviceResponse, String moduleName) {
		SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
		ArrayList gsett = new ArrayList();
		int i;
		if (content != null) {
			Vector rowsNL = content.getAttributeAsVector("ROWS.ROW");
			SourceBean row = null;
			for (i = 0; i < rowsNL.size(); i++) {
				row = (SourceBean) rowsNL.elementAt(i);
				if (row.containsAttribute("numgsett")) {
					gsett.add(row.getAttribute("numgsett").toString());
				}
			} // for
		} // if(content!=null)
		return (gsett);
	}

	/*
	 * Dati in input una certa data e il numero del giorno della settimana in cui cade controlla se è questo è stato
	 * definito di riposo settimanale, e in questo caso se rispetta le regole su data di inizio validità e data di fine
	 * validità restituendo a seconda del caso il valore true se il giorno è da considerarsi come festivo e il valore
	 * false altrimenti.
	 */
	public static boolean checkGsett(int gg, Calendar data, SourceBean serviceResponse, String moduleName) {
		SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
		Vector rowsNL = content.getAttributeAsVector("ROWS.ROW");
		SourceBean row = null;
		int gi, mi, ai, gf, mf, af;
		Calendar datInizio = Calendar.getInstance();
		Calendar datFine = Calendar.getInstance();
		boolean trovato = false;
		int i = 0;
		ai = mi = gi = 0;
		af = mf = gf = 0;
		while (i < rowsNL.size() && !trovato) {
			row = (SourceBean) rowsNL.elementAt(i);
			if (row.containsAttribute("NUMGSETT")) {
				if (Integer.parseInt(row.getAttribute("NUMGSETT").toString()) == gg) {
					// trovato = true;
					gi = Integer.parseInt(row.getAttribute("GI").toString());
					mi = Integer.parseInt(row.getAttribute("MI").toString()) - 1;
					ai = Integer.parseInt(row.getAttribute("AI").toString());
					gf = Integer.parseInt(row.getAttribute("GF").toString());
					mf = Integer.parseInt(row.getAttribute("MF").toString()) - 1;
					af = Integer.parseInt(row.getAttribute("AF").toString());
					datInizio.set(ai, mi, gi);
					datFine.set(af, mf, gf);
					if ((data.after(datInizio) || data.equals(datInizio))
							&& (data.before(datFine) || data.equals(datFine))) {
						trovato = true;
					}
				}
			}
			i++;
		}

		if (trovato) {
			/*
			 * datInizio.set(ai, mi, gi); datFine.set(af, mf, gf); if((data.after(datInizio) || data.equals(datInizio))
			 * && (data.before(datFine) || data.equals(datFine))) { return(true); } else { return(false); }
			 */
			return (true);
		} else {
			return (false);
		}
	}

	public static String generaColonne(SourceBean moduleBean) {
		SourceBean layout = (SourceBean) moduleBean.getAttribute("CONFIG");
		StringBuffer buf = new StringBuffer();
		buf.append("<tr>\n");
		buf.append("<th class=\"ag_lista\">&nbsp;</th>\n");
		Vector columns = layout.getAttributeAsVector("COLUMNS.COLUMN");
		String nomeColonna = "";
		SourceBean row = null;
		int i = 0;
		for (i = 0; i < columns.size(); i++) {
			row = (SourceBean) columns.elementAt(i);
			nomeColonna = row.getAttribute("LABEL").toString();
			buf.append("<th class=\"ag_lista\">&nbsp;" + nomeColonna + "&nbsp;</th>\n");
		}
		buf.append("</tr>\n");

		return (buf.toString());
	}

	public static String generaRighe(SourceBean moduleBean, SourceBean serviceResponse, String moduleName,
			RequestContainer requestContainer, ResponseContainer responseContainer, String linkDett,
			boolean canDelete) {
		SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
		SourceBean layout = (SourceBean) moduleBean.getAttribute("CONFIG");
		Vector columns = layout.getAttributeAsVector("COLUMNS.COLUMN");

		// ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean selectCaption = (SourceBean) layout.getAttribute("CAPTIONS.SELECT_CAPTION");
		SourceBean deleteCaption = (SourceBean) layout.getAttribute("CAPTIONS.DELETE_CAPTION");
		Vector genericCaption = layout.getAttributeAsVector("CAPTIONS.CAPTION");

		Vector rows = content.getAttributeAsVector("ROWS.ROW");
		SourceBean row = null;
		StringBuffer buf = new StringBuffer();
		int i = 0;
		String label = "";
		String image = "";
		String confirm = "";
		Vector parameters = null;
		String parametersStr = "";
		SourceBean caption = null;

		// String imgGruppo = "<img src=\"../../img/omini.gif\" border=\"0\"
		// alt=\"+\">";
		String imgSingolo = "<img src=\"../../img/omino.gif\" border=\"0\" alt=\"\">";
		String imgAzienda = "<img src=\"../../img/azienda.gif\" border=\"0\" alt=\"\">";
		String ico = "";
		// String ico2 = "";
		// String linkGruppo = "";

		Object fieldObject = null;
		String field = "";
		String nomeColonna = "";
		int tipo = -1;

		for (i = 0; i < rows.size(); i++) {
			row = (SourceBean) rows.elementAt(i);
			buf.append("<TR valign=\"middle\">\n");
			buf.append("<TD class=\"ag_lista\">\n");
			buf.append("<TABLE border=0><TR>\n");
			if (selectCaption != null) {
				label = selectCaption.getAttribute("LABEL").toString();
				if ((label == null) || (label.length() == 0)) {
					label = "Selezionare un dettaglio";
				}
				image = selectCaption.getAttribute("IMAGE").toString();
				if ((image == null) || (image.length() == 0)) {
					image = "../../img/detail.gif";
				}
				confirm = selectCaption.getAttribute("CONFIRM").toString();
				if ((confirm == null) || (confirm.length() == 0)) {
					confirm = "FALSE";
				}
				parameters = selectCaption.getAttributeAsVector("PARAMETER");
				parametersStr = getParametersList(parameters, row, responseContainer, requestContainer);
				buf.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + moduleName + "(" + "'"
						+ parametersStr + linkDett + "'" + ", " + "'" + confirm + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + image + "\" alt=\""
						+ label + "\"/></A></TD>\n");
			} // if (selectCaption != null)
			if (deleteCaption != null && canDelete) {
				label = (String) deleteCaption.getAttribute("LABEL");
				if ((label == null) || (label.length() == 0)) {
					label = "Cancellare un dettaglio";
				}
				image = deleteCaption.getAttribute("IMAGE").toString();
				if ((image == null) || (image.length() == 0)) {
					image = "../../img/del.gif";
				}
				confirm = deleteCaption.getAttribute("CONFIRM").toString();
				if ((confirm == null) || (confirm.length() == 0)) {
					confirm = "FALSE";
				}
				parameters = deleteCaption.getAttributeAsVector("PARAMETER");
				parametersStr = getParametersList(parameters, row, responseContainer, requestContainer);
				buf.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + moduleName + "(" + "'"
						+ parametersStr + linkDett + "'" + ", " + "'" + confirm + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + image + "\" alt=\" "
						+ label + "\"/ ></A></TD>\n");
			} // if (deleteCaption != null)
			for (int j = 0; j < genericCaption.size(); j++) {
				caption = (SourceBean) genericCaption.elementAt(j);
				label = caption.getAttribute("LABEL").toString();
				if ((label == null) || (label.length() == 0)) {
					label = "";
				}
				image = caption.getAttribute("IMAGE").toString();
				if ((image == null) || (image.length() == 0)) {
					image = "";
				}
				confirm = caption.getAttribute("CONFIRM").toString();
				if ((confirm == null) || (confirm.length() == 0)) {
					confirm = "FALSE";
				}
				parameters = caption.getAttributeAsVector("PARAMETER");
				parametersStr = getParametersList(parameters, row, responseContainer, requestContainer);
				buf.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + moduleName + "(" + "'"
						+ parametersStr + linkDett + "'" + ", " + "'" + confirm + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + image + "\" alt=\""
						+ label + "\"/></A></TD>\n");
			}
			buf.append("</TR></TABLE>\n");
			buf.append("</TD>\n");

			for (int j = 0; j < columns.size(); j++) {
				nomeColonna = (String) ((SourceBean) columns.elementAt(j)).getAttribute("NAME");
				fieldObject = row.getAttribute(nomeColonna);
				field = null;
				if (nomeColonna.toUpperCase().equals("LAV_AZ")) {
					if (row.containsAttribute("TIPO")) {
						tipo = Integer.parseInt(row.getAttribute("TIPO").toString());
						switch (tipo) {
						case 1:
							// E' un lavoratore oppure non ci sono nè lavoratori
							// nè Azienda
							ico = imgSingolo + "&nbsp;&nbsp;&nbsp;";
							break;
						case 2:
							// E' un'azienda
							ico = imgAzienda + "&nbsp;&nbsp;&nbsp;";
							break;
						default:
							ico = "";
						} // end switch
					} // end if(row.containsAttribute("TIPO"))...
				} // end if(nomeColonna.toUpperCase().equals("LAV_AZ"))...
				if (fieldObject != null) {
					field = fieldObject.toString();
				} else {
					field = "&nbsp;";
				}
				if ((field.equals("")) || (field == null) || (field.equals(" "))) {
					field = "&nbsp;";
				}
				if (nomeColonna.toUpperCase().equals("LAV_AZ")) {
					buf.append("<TD class=\"ag_lista\"> " + ico + field + "</TD>\n");
				} else {
					buf.append("<TD class=\"ag_lista\"> " + field + "</TD>\n");
				}
			} // for (int j = 0; j < _columns.size(); j++)
			buf.append("</TR>\n");
		} // for (int i = 0; i < rows.size(); i++)
		return (buf.toString());
	} // public String generaRighe

	// Inserisce il codice javascript
	public static String makeJScript(String moduleName) {
		StringBuffer buf = new StringBuffer();
		buf.append("<SCRIPT language=\"Javascript\" type=\"text/javascript\">\n");
		buf.append("<!--\n");
		buf.append("function goConfirm" + moduleName + "(url, alertFlag) {\n");

		// GG 1/10/2004 - il corpo della funzione è stato spostato nella
		// funzione "goConfirmGenericCustomTL()" nel file customTL.js
		buf.append("  goConfirmGenericCustomTL(url, alertFlag);  // in customTL.js\n");

		buf.append("}\n");
		buf.append("// -->\n");
		buf.append("</SCRIPT>\n");

		return (buf.toString());
	} // public String makeJScript(String moduleName)

	private static String getParametersList(Vector parameters, SourceBean row, ResponseContainer responseContainer,
			RequestContainer requestContainer) {
		StringBuffer parametersList = new StringBuffer();
		String name = "";
		String type = "";
		String value = "";
		String scope = "";
		Object valueObject = null;

		for (int i = 0; i < parameters.size(); i++) {
			name = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("NAME");
			type = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("TYPE");
			value = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("VALUE");
			scope = (String) ((SourceBean) parameters.elementAt(i)).getAttribute("SCOPE");
			if (name != null) {
				parametersList.append(JavaScript.escape(name.toUpperCase()) + "=");
				if ((type != null) && type.equalsIgnoreCase("RELATIVE")) {
					if ((scope != null) && scope.equalsIgnoreCase("LOCAL")) {
						valueObject = row.getAttribute(value);
						if (valueObject != null) {
							value = valueObject.toString();
						}
					} else {
						value = (String) ContextScooping.getScopedParameter(requestContainer, responseContainer, value,
								scope);
					}
				} // if ((type != null) && type.equalsIgnoreCase("RELATIVE"))
				if (value == null) {
					value = "";
				}
				parametersList.append(JavaScript.escape(value) + "&");
			} // if (name != null)
		} // for (int i = 0; i < parameters.size(); i++)
		return (parametersList.toString());
	} // private String getParametersList(Vector parameters, SourceBean row)
		// throws JspException

	public static String generaRighe2(SourceBean moduleBean, SourceBean serviceResponse, String moduleName,
			RequestContainer requestContainer, ResponseContainer responseContainer, String linkDett,
			boolean canDelete) {
		SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
		SourceBean layout = (SourceBean) moduleBean.getAttribute("CONFIG");
		Vector columns = layout.getAttributeAsVector("COLUMNS.COLUMN");

		// ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean selectCaption = (SourceBean) layout.getAttribute("CAPTIONS.SELECT_CAPTION");
		SourceBean deleteCaption = (SourceBean) layout.getAttribute("CAPTIONS.DELETE_CAPTION");
		Vector genericCaption = layout.getAttributeAsVector("CAPTIONS.CAPTION");

		Vector rows = content.getAttributeAsVector("PAGED_LIST.ROWS.ROW");
		SourceBean row = null;
		StringBuffer buf = new StringBuffer();
		int i = 0;
		String label = "";
		String image = "";
		String confirm = "";
		Vector parameters = null;
		String parametersStr = "";
		SourceBean caption = null;

		// String imgGruppo = "<img src=\"../../img/omini.gif\" border=\"0\"
		// alt=\"+\">";
		String imgSingolo = "<img src=\"../../img/omino.gif\" border=\"0\" alt=\"\">";
		String imgAzienda = "<img src=\"../../img/azienda.gif\" border=\"0\" alt=\"\">";
		String ico = "";
		// String ico2 = "";
		// String linkGruppo = "";

		Object fieldObject = null;
		String field = "";
		String nomeColonna = "";
		int tipo = -1;

		for (i = 0; i < rows.size(); i++) {
			row = (SourceBean) rows.elementAt(i);
			buf.append("<TR valign=\"middle\">\n");
			buf.append("<TD class=\"ag_lista\">\n");
			buf.append("<TABLE border=0><TR>\n");
			if (selectCaption != null) {
				label = selectCaption.getAttribute("LABEL").toString();
				if ((label == null) || (label.length() == 0)) {
					label = "Selezionare un dettaglio";
				}
				image = selectCaption.getAttribute("IMAGE").toString();
				if ((image == null) || (image.length() == 0)) {
					image = "../../img/detail.gif";
				}
				confirm = selectCaption.getAttribute("CONFIRM").toString();
				if ((confirm == null) || (confirm.length() == 0)) {
					confirm = "FALSE";
				}
				parameters = selectCaption.getAttributeAsVector("PARAMETER");
				parametersStr = getParametersList(parameters, row, responseContainer, requestContainer);
				buf.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + moduleName + "(" + "'"
						+ parametersStr + linkDett + "'" + ", " + "'" + confirm + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + " src=\"" + image + "\" alt=\""
						+ label + "\"/></A></TD>\n");
			} // if (selectCaption != null)
			if (deleteCaption != null && canDelete) {
				label = (String) deleteCaption.getAttribute("LABEL");
				if ((label == null) || (label.length() == 0)) {
					label = "Cancellare un dettaglio";
				}
				image = deleteCaption.getAttribute("IMAGE").toString();
				if ((image == null) || (image.length() == 0)) {
					image = "../../img/del.gif";
				}
				confirm = deleteCaption.getAttribute("CONFIRM").toString();
				if ((confirm == null) || (confirm.length() == 0)) {
					confirm = "FALSE";
				}
				parameters = deleteCaption.getAttributeAsVector("PARAMETER");
				parametersStr = getParametersList(parameters, row, responseContainer, requestContainer);
				buf.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + moduleName + "(" + "'"
						+ parametersStr + linkDett + "'" + ", " + "'" + confirm + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + image + "\" alt=\" "
						+ label + "\"/ ></A></TD>\n");
			} // if (deleteCaption != null)
			for (int j = 0; j < genericCaption.size(); j++) {
				caption = (SourceBean) genericCaption.elementAt(j);
				label = caption.getAttribute("LABEL").toString();
				if ((label == null) || (label.length() == 0)) {
					label = "";
				}
				image = caption.getAttribute("IMAGE").toString();
				if ((image == null) || (image.length() == 0)) {
					image = "";
				}
				confirm = caption.getAttribute("CONFIRM").toString();
				if ((confirm == null) || (confirm.length() == 0)) {
					confirm = "FALSE";
				}
				parameters = caption.getAttributeAsVector("PARAMETER");
				parametersStr = getParametersList(parameters, row, responseContainer, requestContainer);
				buf.append("<TD align=\"center\"><A href=\"javascript://\" onclick=\"goConfirm" + moduleName + "(" + "'"
						+ parametersStr + linkDett + "'" + ", " + "'" + confirm + "'"
						+ "); return false;\"><IMG name=\"image\" border=\"0\" " + "src=\"" + image + "\" alt=\""
						+ label + "\"/></A></TD>\n");
			}
			buf.append("</TR></TABLE>\n");
			buf.append("</TD>\n");

			for (int j = 0; j < columns.size(); j++) {
				nomeColonna = (String) ((SourceBean) columns.elementAt(j)).getAttribute("NAME");
				fieldObject = row.getAttribute(nomeColonna);
				field = null;
				if (nomeColonna.toUpperCase().equals("LAV_AZ")) {
					if (row.containsAttribute("TIPO")) {
						tipo = Integer.parseInt(row.getAttribute("TIPO").toString());
						switch (tipo) {
						case 1:
							// E' un lavoratore oppure non ci sono nè lavoratori
							// nè Azienda
							ico = imgSingolo + "&nbsp;&nbsp;&nbsp;";
							break;
						case 2:
							// E' un'azienda
							ico = imgAzienda + "&nbsp;&nbsp;&nbsp;";
							break;
						default:
							ico = "";
						} // end switch
					} // end if(row.containsAttribute("TIPO"))...
				} // end if(nomeColonna.toUpperCase().equals("LAV_AZ"))...
				if (fieldObject != null) {
					field = fieldObject.toString();
				} else {
					field = "&nbsp;";
				}
				if ((field.equals("")) || (field == null) || (field.equals(" "))) {
					field = "&nbsp;";
				}
				if (nomeColonna.toUpperCase().equals("LAV_AZ")) {
					buf.append("<TD class=\"ag_lista\"> " + ico + field + "</TD>\n");
				} else {
					buf.append("<TD class=\"ag_lista\"> " + field + "</TD>\n");
				}
			} // for (int j = 0; j < _columns.size(); j++)
			buf.append("</TR>\n");
		} // for (int i = 0; i < rows.size(); i++)
		return (buf.toString());
	} // public String generaRighe2

}