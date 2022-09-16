<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnPropenSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnPropenSIL', 'tabPropenSIL');toggle('btnPropenSAP', 'tabPropenSAP');return false" />
      <font size="2">Propensioni</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabPropenSIL">
<%@ page import="it.eng.sil.module.sap.*" %>

  <tr>
	<td valign=top class="silTitoloSezione" colspan="2"> &nbsp; </td>
  </tr>

<%
SelectPropenSIL sltPropensioni = new SelectPropenSIL(cdnLavoratore);
List lstMansioni = sltPropensioni.getMansioni();
for(int m = 0; m < lstMansioni.size(); m++){
  MansioneSIL objMansione = (MansioneSIL) lstMansioni.get(m);
%>
    <tr>
     <td class="silTitoloSezione" colspan="2"><%=Utils.notNull(objMansione.getStrDescrizione())%></td>
    </tr>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Orari</td>
      <td class="inputView"><%=objMansione.lista(objMansione.getOrari())%></td>
    </tr>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Turni</td>
      <td class="inputView"><%=objMansione.lista(objMansione.getTurni())%></td>
    </tr>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Comuni</td>
      <td class="inputView"><%=objMansione.lista(objMansione.getComuni())%></td>
    </tr>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Province</td>
      <td class="inputView"><%=objMansione.lista(objMansione.getProvince())%></td>
    </tr>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Regioni</td>
      <td class="inputView"><%=objMansione.lista(objMansione.getRegioni())%></td>
    </tr>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Stati</td>
      <td class="inputView"><%=objMansione.lista(objMansione.getStati())%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Automunito</td>
      <td class="inputView"><%=objMansione.getFlgDispAuto() ? "Sì" : "No"%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Motomunito</td>
      <td class="inputView"><%=objMansione.getFlgDispMoto() ? "Sì" : "No"%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Uso mezzi pubblici</td>
      <td class="inputView"><%=objMansione.getFlgMezziPub() ? "Sì" : "No"%></td>
    </tr>
    <tr>
      <td class="etichetta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>
<%
} // mansione
%>

</table>
<%
}
%>
