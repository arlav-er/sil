<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnConInfSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnConInfSIL', 'tabConInfSIL');toggle('btnConInfSAP', 'tabConInfSAP');return false" />
      <font size="2">Conoscenze Informatiche</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabConInfSIL">

  <tr>
	<td valign=top class="silTitoloSezione" colspan="2"> &nbsp; </td>
  </tr>

<%
  Vector vettInformatica = serviceResponse.getAttributeAsVector("M_SelectSAPConInf.ROWS.ROW");
  if (vettInformatica != null) {
  String strTipo = "";
    String strDettaglio = "";
    String strLivello = "";
    String strDescInfo = "";
    for (int i = 0; i < vettInformatica.size(); i++) {
      SourceBean beanInformatica = (SourceBean) vettInformatica.get(i);
      strTipo = Utils.notNull(beanInformatica.getAttribute("desTipoInfo"));
      strDettaglio = Utils.notNull(beanInformatica.getAttribute("desDettInfo"));
      strLivello = Utils.notNull(beanInformatica.getAttribute("desGrado"));
      strDescInfo= Utils.notNull(beanInformatica.getAttribute("strDescInfo"));
%>

    <tr>
      <td class="silTitoloSezione" colspan="2"><%=strDettaglio%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Tipo</td>
      <td class="inputView"><%=strTipo%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Livello</td>
      <td class="inputView"><%=strLivello%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Descrizione</td>
      <td class="inputView"><%=strDescInfo%></td>
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
