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
  PageAttribs attributi = new PageAttribs(user, "DelegaAnagRicercaPage");
  boolean canModify = attributi.containsButton("nuovo");

 
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
 String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
 String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 String datnasc          = StringUtils.getAttributeStrNotNull(serviceRequest,"datnasc");
 String codComNas        = StringUtils.getAttributeStrNotNull(serviceRequest,"codComNas");
 String strComNas        = StringUtils.getAttributeStrNotNull(serviceRequest,"strComNas");
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
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
      <!--
      function checkCampiObbligatori()
      { if( ((document.Frm1.strCodiceFiscale.value != null) && (document.Frm1.strCodiceFiscale.value.length >= 6)) 
            || (document.Frm1.strCognome.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
      }
      -->
    </script>
    
    
    
	</head>
	<body class="gestione" onload="rinfresca()">
  <p class="titolo">Ricerca sull'anagrafica Lavoratori</p>
    <%out.print(htmlStreamTop);%>
			<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkCampiObbligatori() && btFindComuneCAP_onSubmit(document.Frm1.codComNas, document.Frm1.strComNas, null, false)">
      <table class="main">
        <tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
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
          <td class="etichetta">Data di nascita</td>
          <td class="campo">
          <af:textBox validateOnPost="true" type="date" name="datnasc" value="<%=datnasc%>"   size="10" maxlength="10"/>
          </td>
        </tr>
        <tr>
			<td class="etichetta">Comune di nascita&nbsp;</td>
			<td class="campo">
				<af:textBox classNameBase="input"
				title="Comune di nascita"
				onKeyUp="PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'codice');"
				type="text" name="codComNas" value="<%=codComNas%>" size="4" maxlength="4"
				readonly="false"/>&nbsp; 
			<A
				HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'codice','');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per codice"/></a>&nbsp; 
				<af:textBox type="hidden"
							name="codComNasHid" value="" />


				
				<af:textBox type="text"
							classNameBase="input"
							onKeyUp="PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'descrizione');"
							name="strComNas" value="<%=strComNas%>" size="30" maxlength="50"
							readonly="false"/>
			<A
				HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'descrizione','');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per descrizione"/></a>&nbsp; 
		   <af:textBox type="hidden" 
		    				  name="strComNasHid" value=""/>
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
        <input type="hidden" name="PAGE" value="DelegaAnagRicercaPage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      </table>
      </af:form>
      

      <af:form method="POST" action="AdapterHTTP" dontValidate="true">
      <span class="bottoni">
<!--
<%if (canModify) { %>
        <input class="pulsanti" type="submit" name="inserisci" value="Inserisci un nuovo lavoratore"/>
        <input type="hidden" name="NEW_SESSION" value="TRUE"/>
        <input type="hidden" name="PAGE" value="AnagDettaglioPageAnag"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
        <input type="hidden" name="flag_insert" value="1"/>
<%}%> -->
      </span>
			</af:form>      


<%out.print(htmlStreamBottom);%>
	</body>
</html>

