<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
  //da cambiare!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
  boolean canModify = attributi.containsButton("nuovo");

 
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
 String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
 String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 String datnasc          = StringUtils.getAttributeStrNotNull(serviceRequest,"datnasc");
 String codComNas        = StringUtils.getAttributeStrNotNull(serviceRequest,"codComNas");
 String strComNas        = StringUtils.getAttributeStrNotNull(serviceRequest,"strComNas");
 String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
 String codCPI			 = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");

%>

<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
	<head>
		<title>Ricerca sull'anagrafica lavoratori</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/" />

     <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
      <!--
      function checkCampiObbligatori()
      { 
        
        //controllo la lunghezza del cf o del cognome solo se sono stati inseriti
        if ((document.Frm1.strCodiceFiscale.value!="") && (document.Frm1.strCodiceFiscale.value.length<6)) {
        	alert("Inserire almeno sei caratteri del codice fiscale");
        	return false;
       	}
       	if ((document.Frm1.strCognome.value!="") && (document.Frm1.strCognome.value.length < 2)) {
       		alert("Inserire almeno due caratteri del cognome");
        	return false;
       	}

        return true;
      }
      
      
      function annulla(){
      	document.Frm1.strCodiceFiscale.value="";
      	document.Frm1.strCognome.value="";
      	document.Frm1.strNome.value="";
      	document.Frm1.tipoRicerca[0].checked=true;
      	document.Frm1.CodCPI.value="";
      }
      
      -->
    </script>
    
    
    
	</head>
	<body class="gestione" onload="rinfresca()">
  <p class="titolo">Ricerca dichiarazioni di sospensione per contrazione d'attività</p>
    <%out.print(htmlStreamTop);%>
			<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkCampiObbligatori()">
      <table class="main">
        <tr><td colspan="2"/><br/>Per effettuare una ricerca partendo dai dati anagrafici &egrave; necessario inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
        <tr><td colspan="2"/>&nbsp;</td></tr>
        <tr>
          <td class="etichetta">Codice fiscale</td>
          <td class="campo">
            <af:textBox type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Cognome</td>
          <td class="campo">
            <af:textBox type="text" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Nome</td>
          <td class="campo">
            <af:textBox type="text" name="strNome" value="<%=strNome%>" size="20" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">tipo ricerca</td>
          <td class="campo">
          <table border="0">
          <tr>
          <%if (tipoRicerca.equals("iniziaPer")) {%>
           <td><input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
           <td><input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per</td>
          <%} else {%>
           <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
           <td><input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per</td>
          <%}%>
          </tr>
          </table>
          </td>
        </tr>
        <tr>
		  <td class="etichetta" nowrap>
			Centro per l'Impiego competente
		  </td> 
		  <td class="campo">
			 <af:comboBox name="CodCPI" title="CPI competente" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=codCPI%>" required="true"/>
		  </td>
		  </tr>  
        
        <tr><td class="etichetta">&nbsp;</td></tr>
        
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
          &nbsp;&nbsp;
          <input type="button" class="pulsanti" value="Annulla" onclick="annulla()" />
			
          </td>
        </tr>
        <input type="hidden" name="PAGE" value="AmmListaDichSospPage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      </table>
      </af:form>
        

<%out.print(htmlStreamBottom);%>
	</body>
</html>

