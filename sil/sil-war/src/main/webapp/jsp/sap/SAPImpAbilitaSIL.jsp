<% if(beanAnagra != null) {%>
<table class="silTabella">
  <tr>
    <td class="silTitoloSezione" colspan="2">
      <input id="btnAbilitaSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnAbilitaSIL', 'tabAbilitaSIL');toggle('btnAbilitaSAP', 'tabAbilitaSAP');return false" />
      <font size="2">Abilitazioni</font>
    </td>
  </tr>
</table>

<table class="silTabella" id="tabAbilitaSIL">
<%
  Vector vettAbiPatenti = serviceResponse.getAttributeAsVector("M_SelectSAPAbilitaPatenti.ROWS.ROW");
  if (vettAbiPatenti.size() > 0) {
%>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Patenti</td>
      <td>
      	<table id="tabAbilitaSilPatenti">
		<%
	    for (int i = 0; i < vettAbiPatenti.size(); i++) {
	        SourceBean beanAbiPatenti = (SourceBean) vettAbiPatenti.get(i);
	    %>	      	
      	<tr>
	      <td class="campo"><%=Utils.notNull(beanAbiPatenti.getAttribute("strdescrizione"))%></td>	
  	    </tr>
	    <%}%>   	      	
      	</table>
      </td>      
    </tr>
<%
  } 
  Vector vettAbiPatentini = serviceResponse.getAttributeAsVector("M_SelectSAPAbilitaPatentini.ROWS.ROW");
  if (vettAbiPatentini.size() > 0) {
%>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Patentini</td>
      <td>
      	<table id="tabAbilitaSilPatentini">
		<%
	    for (int i = 0; i < vettAbiPatentini.size(); i++) {
	        SourceBean beanAbiPatentini = (SourceBean) vettAbiPatentini.get(i);
	    %>	      	
      	<tr>
	      <td class="campo"><%=Utils.notNull(beanAbiPatentini.getAttribute("strdescrizione"))%></td>	
  	    </tr>
	    <%}%>   	      	
      	</table>
      </td>      
    </tr>
<%
  } 
  Vector vettAbiAlbi = serviceResponse.getAttributeAsVector("M_SelectSAPAbilitaAlbi.ROWS.ROW");
  if (vettAbiAlbi.size() > 0) { 
%>
    <tr>
      <td valign="top" class="etichetta, grassetto, indenta">Albi</td>
      <td>
      	<table id="tabAbilitaSilAlbi">
		<%
	    for (int i = 0; i < vettAbiAlbi.size(); i++) {
	        SourceBean beanAbiAlbi = (SourceBean) vettAbiAlbi.get(i);
	    %>	      	
      	<tr>
	      <td class="campo"><%=Utils.notNull(beanAbiAlbi.getAttribute("strdescrizione"))%></td>	
  	    </tr>
	    <%}%>   	      	
      	</table>
      </td>      
    </tr>
<%
  }  
%>
    <tr>
      <td class="etichetta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>

</table>
<%
}
%>
