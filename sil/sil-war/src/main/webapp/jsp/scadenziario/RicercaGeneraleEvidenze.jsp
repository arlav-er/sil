<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
  //PageAttribs attributi = new PageAttribs(user, "RicercaGeneraleEvidenzePage");
  //boolean canModify = attributi.containsButton("nuovo");

 
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleRic");
 String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognomeRic");
 String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNomeRic");
 String messaggio        = StringUtils.getAttributeStrNotNull(serviceRequest,"messaggioRic");
 String prgTipoEvidenza  = StringUtils.getAttributeStrNotNull(serviceRequest,"prgTipoEvidenzaRic");
 String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
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
     function recuperaLavoratoriConEvidenzaIR() {


	 url="AdapterHTTP?PAGE=ListEvidenzeCoopPage";
	 url += "&strCodTipoEvidenza=AV";
	 url += "&prgtipoevidenza=241";
     setWindowLocation(url);


}
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        //attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
      <!--
      function checkCampiObbligatori() {
      	return true;
        if( ((document.Frm1.strCodiceFiscale.value != null) && (document.Frm1.strCodiceFiscale.value.length >= 6)) 
            || (document.Frm1.strCognome.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
      }
      -->
    </script>
    
    
    
	</head>
	<body class="gestione" onload="rinfresca()">
  <p class="titolo">Ricerca Evidenze Lavoratori</p>
    <%out.print(htmlStreamTop);%>
	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">
      <table class="main">
        <%--<tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>--%>
        <tr><td colspan="2"/>&nbsp;</td></tr>
        <tr>
          <td class="etichetta">Codice fiscale</td>
          <td class="campo">
            <af:textBox type="text" name="strCodiceFiscaleRic" value="<%=strCodiceFiscale%>" size="20" maxlength="16"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Cognome</td>
          <td class="campo">
            <af:textBox type="text" name="strCognomeRic" value="<%=strCognome%>" size="20" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Nome</td>
          <td class="campo">
            <af:textBox type="text" name="strNomeRic" value="<%=strNome%>" size="20" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">tipo ricerca</td>
          <td class="campo">
          <table colspacing="0" colpadding="0" border="0">
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
        <tr><td class="etichetta">&nbsp;</td></tr>
        <tr>
          <td class="etichetta">Evidenza tipo</td>
          <td class="campo">
	          <af:comboBox  name="prgTipoEvidenzaRic" title="Evidenza Tipo" selectedValue="<%=prgTipoEvidenza%>" 
	              moduleName="MTipiEvidenze" addBlank="true" required="true"/>
          </td>
        </tr>
        <tr>
			<td class="etichetta">Il messaggio contiene&nbsp;</td>
			<td class="campo">
				<af:textBox classNameBase="input"
				title="Contenuto messaggio"
				type="text" name="messaggioRic" value="<%=messaggio%>" size="30" maxlength="100"
				readonly="false"/>&nbsp;
		   </td>
		</tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
          &nbsp;&nbsp;
          <input type="reset" class="pulsanti" value="Annulla" />
			
          </td>
        </tr>
        <input type="hidden" name="PAGE" value="ListaGeneraleEvidenzePage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      </table>
      </af:form>

<%out.print(htmlStreamBottom);%>
	</body>
</html>

