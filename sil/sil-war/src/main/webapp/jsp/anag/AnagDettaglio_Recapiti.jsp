<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter, 
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canModify = false;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    	PageAttribs attributi = new PageAttribs(user, _page);
    	boolean existsSalva = attributi.containsButton("SALVA");
    	
    	if(!existsSalva){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	
    	}
%>








<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%
  int _funzione=0;
  //inizializzo i campi
  String strIndirizzoRec="";
  String strLocalitaRec="";
  String codComRec="";
  String strComRec="";
  String provRec="";
  String strCapRec="";
  String strTelRes="";
  String strTelDom="";
  String strTelAltro="";
  String strCell="";
  String strEmail="";
  String strFax="";
  String strNote="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String numKloLavoratore="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  //devo prelevare la risposta che mi è pervenuta dalla get

  String response_xml="M_GETLAVORATORERECAPITI.ROW";
    
  try{

    SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);



    if (row.containsAttribute("codComRec")) {codComRec=row.getAttribute("codComRec").toString();}
    if (row.containsAttribute("provRec")) {provRec=row.getAttribute("provRec").toString();}
    if (row.containsAttribute("strComRec")) {
          strComRec=row.getAttribute("strComRec").toString()+(!provRec.equals("")?" ("+provRec+")":"");
    }
    if (row.containsAttribute("strIndirizzoRec")) {strIndirizzoRec=row.getAttribute("strIndirizzoRec").toString();}
    if (row.containsAttribute("strLocalitaRec")) {strLocalitaRec=row.getAttribute("strLocalitaRec").toString();}
    if (row.containsAttribute("strCapRec")) {strCapRec=row.getAttribute("strCapRec").toString();}      
    if (row.containsAttribute("strTelRes")) {strTelRes=row.getAttribute("strTelRes").toString();}
    if (row.containsAttribute("strTelDom")) {strTelDom=row.getAttribute("strTelDom").toString();}
    if (row.containsAttribute("strTelAltro")) {strTelAltro=row.getAttribute("strTelAltro").toString();}
    if (row.containsAttribute("strCell")) {strCell=row.getAttribute("strCell").toString();}
    if (row.containsAttribute("strEmail")) {strEmail=row.getAttribute("strEmail").toString();}
    if (row.containsAttribute("strFax")) {strFax=row.getAttribute("strFax").toString();}
    if (row.containsAttribute("strNote")) {strNote=row.getAttribute("strNote").toString();}
    if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();}
    if (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();}
    if (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
    if (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
    if (row.containsAttribute("numKloLavoratore")) {numKloLavoratore=row.getAttribute("numKloLavoratore").toString();}


  }
  catch(Exception ex){
    // non faccio niente
  }
	
  infCorrentiLav= new InfCorrentiLav(requestContainer.getSessionContainer(), cdnLavoratore, user);
  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  // NOTE: Attributi della pagina (pulsanti e link) 

  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

    %>
<html>
<head>
<title>Anagrafica dettaglio</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
 <af:linkScript path="../../js/"/>


<SCRIPT TYPE="text/javascript">

<!--

 <% 
 	//Genera il Javascript che si occuperà di inserire i links nel footer
   attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>




// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    flagChanged = true;
  }

function codComuneUpperCase(inputName){

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}


function checkCap(inputName){

  var capObj = eval("document.forms[0]." + inputName);
  cap=capObj.value;
  ok=true;
  if ((cap.length>0) && (cap.length<5)) {
    ok=false;
  }

  if (!ok) {
    alert("Il " + capObj.title + " deve essere di 5 cifre");
    capObj.focus();
  }
  return ok;
}



function btFindCittadinanza_onclick(codcittadinanza, codcittadinanzahid, cittadinanza, cittadinanzahid, nazione, nazionehid) {
	
		if (nazione.value == ""){
			nazionehid.value = "";
			codcittadinanzahid.value = "";
			codcittadinanza.value = "";
 			cittadinanzahid.value = "";
			cittadinanza.value = "";
			} else
		if (nazione != nazionehid) 
			window.open ("AdapterHTTP?PAGE=RicercaCittadinanzaPage&nazione="+nazione.value.toUpperCase()+"&retcod="+codcittadinanza.name+"&retcittadinanza="+cittadinanza.name+"&retnazione="+nazione.name);
    
}



//-->
   window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</SCRIPT>


</head>
<body class="gestione">
<%
 _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 Linguette l  = new Linguette(user,  _funzione, _page, new BigDecimal(cdnLavoratore));
 infCorrentiLav.show(out); 
 l.show(out);
 
  
 
%>


<SCRIPT>
   window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</SCRIPT>

<font color="green">
 <af:showMessages prefix="M_SaveLavoratoreRecapiti"/>
</font>


 <font color="red">
     <af:showErrors/>
 </font>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="btFindComuneCAP_onSubmit(Frm1.codComRec, Frm1.strComRec, Frm1.strCapRec,false)">
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>

<p align="center">
<%out.print(htmlStreamTop);%>
<table  class="main">
<!--
<tr>
  <td colspan="2">&nbsp;</td></tr>

<tr>
    <td class="etichetta">Telefono Residenza&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelRes" value="<%=strTelRes%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Telefono Domicilio&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelDom" value="<%=strTelDom%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Altro telefono&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelAltro" value="<%=strTelAltro%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Cellulare&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strCell" value="<%=strCell%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Email&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strEmail" value="<%=strEmail%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Fax&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strFax" value="<%=strFax%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
-->
<tr>
  <td colspan="2">&nbsp;</td>
</tr>
<tr>
  <td colspan="2"><div class="sezione2">Indirizzo per la corrispondenza</div></td>
</tr>
<tr>
  <td colspan="2">&nbsp;</td>
</tr>
<tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();"  type="text" name="strIndirizzoRec" value="<%=strIndirizzoRec%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>"/><br/></td>
</tr>
<tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strLocalitaRec" value="<%=strLocalitaRec%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(Frm1.codComRec, Frm1.codComRecHid, Frm1.strComRec, Frm1.strComRecHid, Frm1.strCapRec, Frm1.strCapRecHid,'codice');" type="text" name="codComRec" value="<%=codComRec%>" size="4" maxlength="4" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <% if(canModify) { %>
    <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codComRec, Frm1.strComRec, Frm1.strCapRec, 'codice','',null,'inserisciComRecNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <% } %>
    <af:textBox type="hidden" name="codComRecHid" value="<%=codComRec%>"/>
    <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(Frm1.codComRec, Frm1.codComRecHid, Frm1.strComRec, Frm1.strComRecHid,Frm1.strCapRec, Frm1.strCapRecHid, 'descrizione');" type="text" name="strComRec" value="<%=strComRec%>" size="30" maxlength="50" 
          inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
    <% if(canModify) { %>
    <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codComRec, Frm1.strComRec, Frm1.strCapRec, 'descrizione','',null,'inserisciComRecNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    <% } %>
    <af:textBox type="hidden" name="strComRecHid" value="<%=strComRec%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" title="Cap" name="strCapRec" value="<%=strCapRec%>" size="5" maxlength="5" validateWithFunction="checkCap" readonly="<%= String.valueOf(!canModify) %>" />
      <af:textBox type="hidden" name="strCapRecHid" value="<%=strCapRec%>"/>
    </td>
</tr>
<tr><td colspan="2">&nbsp;</td>
</tr>

</table>


<input type="hidden" name="numKloLavoratore" value="<%=((int) Integer.parseInt(numKloLavoratore) + 1)%>">
<center>
<% 
if (canModify) { %>
  <table>
  <tr><td align="center">
  <input class="pulsante" type="submit" name="salva" value="Aggiorna">
  </td></tr>
  </table>
<% } %>
</center>
<%out.print(htmlStreamBottom);%>
<br/>
<p align="center">
<%operatoreInfo.showHTML(out);%>
</p>
<br/>
<input type="hidden" name="PAGE" value="AnagDettaglioPageRecapiti">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
</af:form>

</body>
</html>

<%}  %>
