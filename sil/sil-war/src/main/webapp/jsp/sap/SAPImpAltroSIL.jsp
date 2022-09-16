<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnAltroSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnAltroSIL', 'tabAltroSIL');toggle('btnAltroSAP', 'tabAltroSAP');return false" />
      <font size="2">Altre informazioni</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabAltroSIL">

  <tr>
    <td class="etichetta, grassetto, indenta">Note</td>
    <td class="inputView"></td>
  </tr>
</table>
<%
}
%>
