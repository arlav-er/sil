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
	boolean isConferimentoDid = false;//(boolean) serviceRequest.containsAttribute("confDid");
  if(!isConferimentoDid){
	  tipiTitoliRows = serviceResponse.getAttributeAsVector("M_GETTIPOTITOLI.ROWS.ROW");  
  }else if(isConferimentoDid){
 	  tipiTitoliRows = serviceResponse.getAttributeAsVector("M_CCD_GetTipoTitoli.ROWS.ROW");  
  }

  boolean flgTornaDaRicerca = (boolean) serviceRequest.containsAttribute("TORNARICERCA");
  String ricercaStr="";
  if(flgTornaDaRicerca){
	  ricercaStr = serviceRequest.getAttribute("strTitolo").toString();
  }
%>

<html>
<head>
<title>Titoli di studio</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<!--
var isConferimentoDid =false; 
<%-- "<%= isConferimentoDid%>"; --%>
var isFromRicerca = "<%=flgTornaDaRicerca%>";
flgLaurea_array=new Array();


function AggiornaForm (codTitolo, desTitolo, desTipologia, flgLaurea) {
  
	window.opener.document.Frm1.codTitolo.value = codTitolo;
    window.opener.document.Frm1.codTitoloHid.value=codTitolo;
	window.opener.document.Frm1.strTitolo.value = desTitolo.replace('^', '\'');
  	window.opener.document.Frm1.strTipoTitolo.value = desTipologia.replace('^', '\'');
 	window.opener.document.Frm1.flgLaurea.value = flgLaurea;
    window.opener.toggleVisStato();
  	window.close();
}


  function selectTipo_onClick(codTipoTitolo) {	
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  
    if (codTipoTitolo.value!=""){
      //window.open("AdapterHTTP?PAGE=CurrAlberoTitoliPage&padre="+codTipoTitolo.value, "Titoli", 'toolbar=0, scrollbars=1');
      var url = "AdapterHTTP?PAGE=CurrAlberoTitoliPage&padre="+codTipoTitolo.value;
      setWindowLocation(url);
    }
  }

  function selectTitolo(strTitolo) {
	  
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (strTitolo.value!="") {
      //window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&strTitolo="+strTitolo.value, "Titoli", 'toolbar=0, scrollbars=1');
      var url = "AdapterHTTP?PAGE=RicercaTitoloStudioPage&TORNALISTA=&strTitolo="+strTitolo.value;

      if(isConferimentoDid){
    	  url = "AdapterHTTP?confDid=1&PAGE=CCD_RicercaTitoloStudioPage&TORNALISTA=&strTitolo="+strTitolo.value;
      }
       setWindowLocation(url);
    }
  

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
<p class="titolo"><b>Titoli di studio - ricerca avanzata</b></p>
<TABLE class="lista" align="center">
<%SourceBean row_tipiTitoli= null;
                   String tipiTitoli_cod= null;
                   String tipiTitoli_des=null;
                   String tipiTitoli_flag=null;
                   String arrayTipiTitoliString=""; 
                   %>                
     <%if(!isConferimentoDid){ %>              
    <TR>
        <td>Ricerca per Albero-Indicare il primo livello</td>
        <TD>
         <af:comboBox title="Tipo" name="codTipoTitolo" onChange="" >
                  <OPTION value=""></OPTION>
                    <% for(int i=0; i<tipiTitoliRows.size(); i++)
                         { row_tipiTitoli = (SourceBean) tipiTitoliRows.elementAt(i);
                          tipiTitoli_cod= row_tipiTitoli.getAttribute("CODICE").toString();
                          tipiTitoli_des = row_tipiTitoli.getAttribute("DESCRIZIONE").toString();
                          if (tipiTitoli_des.length() > 80)
                        	  tipiTitoli_des = tipiTitoli_des.substring(0, 76)+"...";
                          tipiTitoli_flag= row_tipiTitoli.getAttribute("flgLaurea").toString();
                          if (row_tipiTitoli.getAttribute("flgLaurea").equals("S")) {
                              arrayTipiTitoliString=arrayTipiTitoliString+"flgLaurea_array.push('"+row_tipiTitoli.getAttribute("CODICE")+"');\n";
                          }
                         %> 
                         <OPTION value="<%=tipiTitoli_cod%>">
                         <%=tipiTitoli_des%>
                         </OPTION>
                        <%}%>
          </af:comboBox>
          <A href="javascript:selectTipo_onClick(document.Frm1.codTipoTitolo);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
    </TD></TR>
    <%} %>
    <TR>
        <TD>Ricerca per descrizione</TD>
        <TD>
          <af:textBox size="30"
                       classNameBase="input" name="strTitolo" />

                  <SCRIPT>
                    <%out.print(arrayTipiTitoliString);%>
                  </SCRIPT>
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