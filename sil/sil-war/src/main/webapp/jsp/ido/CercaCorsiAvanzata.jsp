<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  Vector tipiTitoliRows = null;
	tipiTitoliRows = serviceResponse.getAttributeAsVector("M_GetDescCorsi.ROWS.ROW");  
  

  boolean flgTornaDaRicerca = (boolean) serviceRequest.containsAttribute("TORNARICERCA");
  String ricercaStr="";
  if(flgTornaDaRicerca){
	  ricercaStr = serviceRequest.getAttribute("strTitolo").toString();
  }
  boolean formPro = (boolean) serviceRequest.containsAttribute("FORMPRO");
%>

<html>
<head>
<title>Corsi</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<!--
var isConferimentoDid =false; 
<%-- "<%= isConferimentoDid%>"; --%>
var isFromRicerca = "<%=flgTornaDaRicerca%>";
var formPro = "<%=formPro%>";

flgLaurea_array=new Array();


function  AggiornaForm (codCorso, desCorso){
	
	window.opener.document.Frm1.codCorso.value = codCorso;
	window.opener.document.Frm1.codCorsoHid.value=codCorso;
	window.opener.document.Frm1.strTitolo.value = desCorso;

 	
  	window.close();
}



  function selectTitolo(strTitolo) {
	  
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	var url="AdapterHTTP?PAGE=RicercaCorsiPage&TORNALISTA=&strTitolo=";
	
    if (strTitolo.value!="") {
      //window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&strTitolo="+strTitolo.value, "Titoli", 'toolbar=0, scrollbars=1');
      url = url +strTitolo.value;

    }
    if(formPro=='true') {
    	url = url +"&FORMPRO=1";
    }
  
    setWindowLocation(url);

  }
  function evitaEnter() {
    if (window.event.keyCode == 13) window.event.keyCode = 0;
  }

  function onLoad() {
	  if(isFromRicerca=="true"){
		  if(isConferimentoDid){
			  document.Frm1.strTitolo.value = "<%=ricercaStr%>";
		  }
	  }else{
		  if(isConferimentoDid){
			  document.Frm1.strTitolo.value = window.opener.document.Frm1.strTitolo.value;
		  }
	  }
	  
	  
	}
-->
</SCRIPT>

</head>
<body class="gestione" onload="onLoad()">
<af:form name="Frm1" method="POST" action="" dontValidate="true">
<!--onKeyPress="evitaEnter()"-->
<br/>
<p class="titolo"><b>Corsi - ricerca avanzata</b></p>
<TABLE class="lista" align="center">
           

    <TR>
        <TD>Ricerca per descrizione</TD>
        <TD>
          <af:textBox size="30"
                       classNameBase="input" name="strTitolo" />

          <A href="javascript:selectTitolo(document.Frm1.strTitolo);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
        </TD>
    </TR>
</TABLE>
</af:form>
<br/>
<center><input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="javascript:window.close();"/></center>
<br/>
</body>
</html>