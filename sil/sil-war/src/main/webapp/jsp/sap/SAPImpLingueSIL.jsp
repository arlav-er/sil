<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnLingueSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnLingueSIL', 'tabLingueSIL');toggle('btnLingueSAP', 'tabLingueSAP');return false" />
      <font size="2">Lingue</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabLingueSIL">

  <tr>
	<td valign=top class="silTitoloSezione" colspan="2"> &nbsp; </td>
  </tr>

<%
  Vector vettLingue = serviceResponse.getAttributeAsVector("M_ListConoscenzeLing.ROWS.ROW");
  String strLingua = "";
  String strGradoLetto = "";
  String strGradoScritto = "";
  String strGradoParlato = "";
  String flgCertificata = "";

  if (vettLingue != null) {
    for (int i = 0; i < vettLingue.size(); i++) {
      SourceBean beanLingua = (SourceBean) vettLingue.get(i);

      strLingua = Utils.notNull(beanLingua.getAttribute("Lingua"));
      strGradoLetto = Utils.notNull(beanLingua.getAttribute("Letto"));
      strGradoScritto = Utils.notNull(beanLingua.getAttribute("Scritto"));
      strGradoParlato = Utils.notNull(beanLingua.getAttribute("Parlato"));
      flgCertificata = Utils.notNull(beanLingua.getAttribute("Certificata"));
%>

  <tr valign="top">
    <td class="silTitoloSezione" colspan="2"><%=strLingua%></td>
  </tr>

  <tr valign="top">
    <td class="etichetta, grassetto, indenta">Letto</td>
    <td class="campo"><%=strGradoLetto%></td>
  </tr>

  <tr valign="top">
    <td class="etichetta, grassetto, indenta">Scritto</td>
    <td class="campo"><%=strGradoScritto%></td>
  </tr>

  <tr valign="top">
    <td class="etichetta, grassetto, indenta">Parlato</td>
    <td class="campo"><%=strGradoParlato%></td>
  </tr>

  <tr valign="top">
    <td class="etichetta, grassetto, indenta">Certificata</td>
    <td class="campo"><%=flgCertificata.equals("S") ? "Sì" : "No"%></td>
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
