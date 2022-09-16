<% 
  if(beanAnagra != null) {
%>
<%
  String strIndirizzoRes = "";
  String strComRes = "";
  String strCapRes = "";
  
  String strIndirizzoDom = "";
  String strComDom = "";
  String strCapDom = "";

    SourceBean beanIndirizzi = (SourceBean) serviceResponse.getAttribute("M_GetLavoratoreIndirizzi.ROW");
    if(beanIndirizzi != null) {
      
      strIndirizzoRes = Utils.notNull(beanIndirizzi.getAttribute("strIndirizzoRes"));
      strComRes = Utils.notNull(beanIndirizzi.getAttribute("strComRes"));
      strCapRes = Utils.notNull(beanIndirizzi.getAttribute("strCapRes"));
      
      strIndirizzoDom = Utils.notNull(beanIndirizzi.getAttribute("strIndirizzoDom"));
      strComDom = Utils.notNull(beanIndirizzi.getAttribute("strComDom"));
      strCapDom = Utils.notNull(beanIndirizzi.getAttribute("strCapDom"));
    }
%>
<%
  String strCellulare = "";
  String strEMail = "";
  String strFax = "";
  

    SourceBean beanRecapiti = (SourceBean) serviceResponse.getAttribute("M_GetLavoratoreRecapiti.ROW");
    if(beanRecapiti != null) {
      strCellulare = Utils.notNull(beanIndirizzi.getAttribute("strCell"));
      strEMail = Utils.notNull(beanIndirizzi.getAttribute("strEMail"));
      strFax = Utils.notNull(beanIndirizzi.getAttribute("strFax"));
    }
%>
	<table class="silTabella">
	  <tr>
	    <td class="silTitoloSezione" colspan="2">
	      <input id="btnAnagraSIL" type="image" src="../../img/aperto.gif" onclick="toggle('btnAnagraSIL', 'tabAnagraSIL');toggle('btnAnagraSAP', 'tabAnagraSAP');return false" />
	      <font size="2">Dati Anagrafici</font>
	    </td>
	  </tr>
	</table>
	
	<table class="silTabella" id="tabAnagraSIL">
    <tr>
      <td class="etichetta, grassetto, indenta">Codice Fiscale</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("strCodiceFiscale"))%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Cognome</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("strCognome"))%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Nome</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("strNome"))%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Sesso</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("strSesso"))%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Data di Nascita</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("datNasc"))%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Comune di Nascita</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("strComNas"))%></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Nazione di cittadinanza</td>
      <td class="inputView"><%=Utils.notNull(beanAnagra.getAttribute("strNazione"))%></td>
    </tr>

    <tr>
      <td class="etichetta, grassetto, indenta">Indirizzo domicilio</td>
      <td class="inputView"><%=strIndirizzoDom%></td>
    </tr>
	<tr>
	  <td class="etichetta, grassetto, indenta">Comune domicilio</td>
	  <td class="inputView"><%=strComDom%></td>
	</tr>
	<tr>
	  <td class="etichetta, grassetto, indenta">Cap domicilio</td>
	  <td class="inputView"><%=strCapDom%></td>
	</tr>
	<tr>
	    <td class="etichetta, grassetto, indenta">Indirizzo residenza</td>
	    <td class="inputView"><%=strIndirizzoRes%></td>
	  </tr>
	<tr>
	  <td class="etichetta, grassetto, indenta">Comune residenza</td>
	  <td class="inputView"><%=strComRes%></td>
	</tr>
	<tr>
	  <td class="etichetta, grassetto, indenta">Cap residenza</td>
	  <td class="inputView"><%=strCapRes%></td>
	</tr>
	
	<tr>
	  <td class="etichetta, grassetto, indenta">Cellulare</td>
	  <td align="left" class="inputView"><%=strCellulare%></td>
	</tr>
	<tr>
	  <td class="etichetta, grassetto, indenta">E-mail</td>
	  <td align="left" class="inputView"><%=strEMail%></td>
	</tr>
	<tr>
	  <td class="etichetta, grassetto, indenta">Fax</td>
	  <td align="left" class="inputView"><%=strFax%></td>
	</tr>
	<tr>
	  <td class="etichetta"><input type="hidden" name="numKloLavoratore" value="<%=Utils.notNull(beanAnagra.getAttribute("numKloLavoratore"))%>"></td>
	  <td class="inputView">&nbsp;</td>
	</tr>
	</table>
<%
  } else {
%>
	<table class="silTabella">
	  <tr>
	    <td align="center" class="msgView" colspan="2">Nessun dato presente</td>
	  </tr>
	</table>
<%
}
%>