package it.eng.sil.util;

public class StyleUtils {
	public StyleUtils() {
	}

	/* @author: Stefania Orioli */

	// Stondature TOP
	// Va chiamato prima delle tabelle <table class="main">.....
	public static String roundTopTable(boolean canModify) {
		StringBuffer buf = new StringBuffer();
		String className = "";
		if (canModify) {
			className = "prof_mod";
		} else {
			className = "prof_ro";
		}

		buf.append(
				"<table align=\"center\" cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"6\" class=\"" + className + "\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		buf.append("</td>");
		buf.append("<td class=\"" + className + "\" align=\"right\" valign=\"top\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr>");
		buf.append("<td class=\"" + className + "\" width=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" width=\"100%\" align=\"center\">");
		return (buf.toString());
	}

	public static String roundTopTable(String className) {
		StringBuffer buf = new StringBuffer();

		buf.append(
				"<table align=\"center\" cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"6\" class=\"" + className + "\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		buf.append("</td>");
		buf.append("<td class=\"" + className + "\" align=\"right\" valign=\"top\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr>");
		buf.append("<td class=\"" + className + "\" width=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" width=\"100%\" align=\"center\">");
		return (buf.toString());
	}

	// Stondature BOTTOM
	// Va chiamato per le tabelle <table class="main"> dopo il tag di chiusura
	// </table>
	public static String roundBottomTable(boolean canModify) {
		StringBuffer buf = new StringBuffer();
		String className = "";
		if (canModify) {
			className = "prof_mod";
		} else {
			className = "prof_ro";
		}

		buf.append("</td><td class=\"" + className + "\" width=\"6\"></td></tr>");
		buf.append("<tr><td class=\"" + className + "\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"" + className + "\" height=\"6\" align=\"center\" valign=\"middle\">&nbsp;");
		buf.append("</td><td class=\"" + className + "\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table><br>&nbsp;");
		return (buf.toString());
	}

	public static String roundBottomTable(String className) {
		StringBuffer buf = new StringBuffer();

		buf.append("</td><td class=\"" + className + "\" width=\"6\"></td></tr>");
		buf.append("<tr><td class=\"" + className + "\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"" + className + "\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"" + className + "\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table><br>&nbsp;");
		return (buf.toString());
	}

	// Stondature BOTTOM
	/* Come la funzione precedente ma senza i <br> ... */
	// Va chiamato per le tabelle <table class="main"> dopo il tag di chiusura
	// </table>
	public static String roundBottomTableNoBr(boolean canModify) {
		StringBuffer buf = new StringBuffer();
		String className = "";
		if (canModify) {
			className = "prof_mod";
		} else {
			className = "prof_ro";
		}

		buf.append("</td><td class=\"" + className + "\" width=\"6\"></td></tr>");
		buf.append("<tr><td class=\"" + className + "\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"" + className + "\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"" + className + "\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		return (buf.toString());
	}

	// Stondature TOP - Elementi INFO
	// Va chiamato prima delle tabelle <table class="main">.....
	public static String roundTopTableInfo() {
		StringBuffer buf = new StringBuffer();
		String className = "info";

		buf.append(
				"<table align=\"center\" cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"6\" class=\"" + className + "\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		buf.append("</td>");
		buf.append("<td class=\"" + className + "\" align=\"right\" valign=\"top\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr>");
		buf.append("<td class=\"" + className + "\" width=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" width=\"100%\" align=\"center\">");
		return (buf.toString());
	}

	// Stondature BOTTOM - Elementi INFO
	// Va chiamato per le tabelle <table class="main"> dopo il tag di chiusura
	// </table>
	public static String roundBottomTableInfo() {
		StringBuffer buf = new StringBuffer();
		String className = "info";

		buf.append("</td><td class=\"" + className + "\" width=\"6\"></td></tr>");
		buf.append("<tr><td class=\"" + className + "\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"" + className + "\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"" + className + "\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table><br>&nbsp;");
		return (buf.toString());
	}

	/* Come la funzione precedente ma senza i <br> ... */
	// Stondature BOTTOM - Elementi INFO
	// Va chiamato per le tabelle <table class="main"> dopo il tag di chiusura
	// </table>
	public static String roundBottomTableInfoNobr() {
		StringBuffer buf = new StringBuffer();
		String className = "info";

		buf.append("</td><td class=\"" + className + "\" width=\"6\"></td></tr>");
		buf.append("<tr><td class=\"" + className + "\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"" + className + "\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"" + className + "\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");
		return (buf.toString());
	}

	// per inserire il bottone di ritorno alla lista
	// Stondature TOP - Elementi INFO
	// Va chiamato prima delle tabelle <table class="main">.....
	public static String roundTopTableInfoRetLista(String urlDiLista) {
		StringBuffer buf = new StringBuffer();
		String className = "info";

		buf.append(
				"<table align=\"center\" cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" maxwidth=\"94%\" width=\"94%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"6\" class=\"" + className + "\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">&nbsp;");
		buf.append("</td>");
		if (!urlDiLista.equals("") && urlDiLista != null) {
			buf.append("<td rowspan=\"3\" class=\"info\" height=\"6px\">");
			buf.append("<a href=\"AdapterHTTP?" + urlDiLista
					+ "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>");
			buf.append("</td>");
		}
		buf.append("<td class=\"" + className + "\" align=\"right\" valign=\"top\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr>");
		buf.append("<td class=\"" + className + "\" width=\"6\">&nbsp;</td>");
		buf.append("<td class=\"" + className + "\" width=\"100%\" align=\"center\">");
		return (buf.toString());
	}

	// Elemento TOP per i Layer: crea la parte superiore di una tabella con
	// bordo grigio
	public static String roundLayerTop(boolean canModify) {
		StringBuffer buf = new StringBuffer();
		String className = "";
		if (canModify) {
			className = "prof_mod";
		} else {
			className = "prof_ro";
		}

		buf.append("<!-- Tabella esterna: BORDO GRIGIO -->");
		buf.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" margin=\"0\">");
		buf.append("<tr>");
		buf.append(
				"<td valign=\"top\" align=\"left\" width=\"6\" height=\"6\"><img src=\"../../img/angoli/gri1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"tdGrigioTop\" height=\"6\">&nbsp;</td>");
		buf.append(
				"<td valign=\"top\" align=\"right\" width=\"6\" height=\"6\"><img src=\"../../img/angoli/gri2.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("</tr>");
		buf.append("<tr>");
		buf.append("<td class=\"tdGrigioLeft\" width=\"6\">&nbsp;</td>");
		buf.append("<td align=\"center\">");

		// Elemento Stondature TOP
		buf.append(
				"<table align=\"center\" cellspacing=\"0\" margin=\"0\" cellpadding=\"0\" border=\"0px\" width=\"100%\" noshade>");
		buf.append("<tr><td align=\"left\" valign=\"top\" width=\"6\" height=\"6\" class=\"" + className + "\">");
		buf.append("<img src=\"../../img/angoli/bia1.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" align=\"center\" valign=\"middle\" cellpadding=\"2px\">");
		buf.append("</td>");
		buf.append("<td class=\"" + className + "\" align=\"right\" valign=\"top\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia2.gif\" width=\"6\" height=\"6\">");
		buf.append("</td></tr>");
		buf.append("<tr>");
		buf.append("<td class=\"" + className + "\" width=\"6\"></td>");
		buf.append("<td class=\"" + className + "\" width=\"100%\" align=\"center\">");

		return (buf.toString());
	}

	// Elemento BOTTOM per i Layer: crea la parte inferiore di una tabella con
	// bordo grigio
	public static String roundLayerBottom(boolean canModify) {
		StringBuffer buf = new StringBuffer();
		String className = "";
		if (canModify) {
			className = "prof_mod";
		} else {
			className = "prof_ro";
		}

		// Stondature Elemento Bottom
		buf.append("</td><td class=\"" + className + "\" width=\"6\"></td></tr>");
		buf.append("<tr><td class=\"" + className + "\" align=\"left\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia4.gif\"></td>");
		buf.append("<td class=\"" + className + "\" height=\"6\" align=\"center\" valign=\"middle\"&nbsp;");
		buf.append("</td><td class=\"" + className + "\" align=\"right\" valign=\"bottom\" width=\"6\" height=\"6\">");
		buf.append("<img src=\"../../img/angoli/bia3.gif\"></td></tr></table>");

		buf.append("<!-- Tabella esterna: BORDO GRIGIO -->");
		buf.append("</td>");
		buf.append("<td class=\"tdGrigioRight\" width=\"6\">&nbsp;</td>");
		buf.append("</tr>");
		buf.append("<tr valign=\"bottom\">");
		buf.append(
				"<td valign=\"bottom\" align=\"left\" width=\"6\" height=\"6\"><img src=\"../../img/angoli/gri4.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("<td class=\"tdGrigioBottom\" height=\"6\">&nbsp;</td>");
		buf.append(
				"<td valign=\"bottom\" align=\"right\" width=\"6\" height=\"6\"><img src=\"../../img/angoli/gri3.gif\" width=\"6\" height=\"6\"></td>");
		buf.append("</tr>");
		buf.append("</table>");

		return (buf.toString());
	}
}
