<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter, 
                it.eng.sil.module.coop.GetDatiPersonali,
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore= "1";//(String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	
	
	boolean canModify = false;
	
	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
%>



<%
  int _funzione=0;
  //inizializzo i campi
  String strIndirizzoRec="";
  String strLocalitaRec="";
  String codComRec="";
  String strComRec="";
  String provRec="";
  String strCapRec="";
  /* TODO Savino 10/10/2006: i recapiti telefonici non vengono mostrati
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
  */
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  //devo prelevare la risposta che mi è pervenuta dalla get

  String response_xml="M_COOP_AnagRecapiti_dalla_cache.ROWS.ROW";
    
  try{

    SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);

	if (row!=null) {
	
	    if (row.containsAttribute("codComRec")) {codComRec=row.getAttribute("codComRec").toString();}
	    if (row.containsAttribute("provRec")) {provRec=row.getAttribute("provRec").toString();}
	    if (row.containsAttribute("strComRec")) {
	          strComRec=row.getAttribute("strComRec").toString()+(!provRec.equals("")?" ("+provRec+")":"");
	    }
	    if (row.containsAttribute("strIndirizzoRec")) {strIndirizzoRec=row.getAttribute("strIndirizzoRec").toString();}
	    if (row.containsAttribute("strLocalitaRec")) {strLocalitaRec=row.getAttribute("strLocalitaRec").toString();}
	    if (row.containsAttribute("strCapRec")) {strCapRec=row.getAttribute("strCapRec").toString();}      
	    /* TODO Savino: 10/10/2006 i recapiti telefonici non vengono mostrati
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
	    */
	}

  }
  catch(Exception ex){
    // non faccio niente
    ex.printStackTrace();
  }
	
//  infCorrentiLav= new InfCorrentiLav(requestContainer.getSessionContainer(), cdnLavoratore, user);
//  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  // NOTE: Attributi della pagina (pulsanti e link) 

  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

    %>
<html>
<head>
<title>Anagrafica dettaglio</title>

<link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
 <af:linkScript path="../../js/"/>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>

<SCRIPT TYPE="text/javascript">

//-->
</SCRIPT>


</head>
<body class="gestione">

<%@ include file="_testataLavoratore.inc" %>
<%@ include file="_linguetta.inc" %>


 <font color="red">
     <af:showErrors/>
 </font>
<af:form method="POST" action="AdapterHTTP" name="Frm1" >

<p align="center">
<%out.print(htmlStreamTop);%>
<table  class="main">

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
    <td class="campo"><af:textBox classNameBase="input"   type="text" name="strIndirizzoRec" value="<%=strIndirizzoRec%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>"/><br/></td>
</tr>
<tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input"  type="text" name="strLocalitaRec" value="<%=strLocalitaRec%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>"/></td>
</tr>
<tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input"  type="text" name="codComRec" value="<%=codComRec%>" size="4" maxlength="4" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <af:textBox classNameBase="input" type="text" name="strComRec" value="<%=strComRec%>" size="30" maxlength="50" 
          readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
    
    </td>
</tr>
<tr>
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" type="text" title="Cap" name="strCapRec" value="<%=strCapRec%>" size="5" maxlength="5"  readonly="<%= String.valueOf(!canModify) %>" />
     
    </td>
</tr>
<tr><td colspan="2">&nbsp;</td>
</tr>

</table>


<center>

</center>
<%out.print(htmlStreamBottom);%>
<br/>
<p align="center">

</p>
<br/>
</af:form>

</body>
</html>
