<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnForProSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnForProSIL', 'tabForProSIL');toggle('btnForProSAP', 'tabForProSAP');return false" />
      <font size="2">Formazione Professionale</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabForProSIL">

  <tr>
	<td valign=top class="silTitoloSezione" colspan="2"> &nbsp; </td>
  </tr>

<%
  Vector vettCorsi = serviceResponse.getAttributeAsVector("M_SelectSAPForPro.ROWS.ROW");
  String codCorso = "";
  String desCorso = "";
  String numAnnoCorso = "";
  String flgCompletato = "";
  String strEnte = "";
  String strDescrizione = "";

  if (vettCorsi != null) {
    for (int i = 0; i < vettCorsi.size(); i++) {
      SourceBean beanCorso = (SourceBean) vettCorsi.get(i);
      codCorso = Utils.notNull(beanCorso.getAttribute("codCorso"));
      desCorso = Utils.notNull(beanCorso.getAttribute("desCorso"));
      if (desCorso.equals(""))
        desCorso = Utils.notNull(beanCorso.getAttribute("strDescrizione"));
      numAnnoCorso = Utils.notNull(beanCorso.getAttribute("numAnno"));
      flgCompletato = Utils.notNull((String) beanCorso.getAttribute("flgCompletato"));
      strEnte = Utils.notNull(beanCorso.getAttribute("strEnte"));
      strDescrizione = Utils.notNull(beanCorso.getAttribute("strContenuto"));
%>

  <tr>
    <td class="silTitoloSezione" colspan="2"><%=desCorso%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Codice</td>
    <td class="campo"><%=codCorso%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Anno</td>
    <td class="campo"><%=numAnnoCorso%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Contenuto</td>
    <td class="campo"><%=strDescrizione%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Ente</td>
    <td class="campo"><%=strEnte%></td>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Completato</td>
    <td class="campo"><%=flgCompletato.equals("S") ? "Sì" : "No"%></td>
  </tr>
    <tr>
      <td class="etichetta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>

<%
    }
  }
%>
</table>
<%
}
%>
