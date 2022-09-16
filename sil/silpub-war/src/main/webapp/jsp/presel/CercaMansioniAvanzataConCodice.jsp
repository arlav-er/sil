<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*, it.eng.afExt.utils.StringUtils"%>
<%@ taglib uri="aftags" prefix="af"%>

<% 

    String codMansione= "";

	ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
	SourceBean serviceResponse = responseContainer.getServiceResponse();

  RequestContainer requestContainer= RequestContainerAccess.getRequestContainer(request);
  SourceBean serviceRequest=requestContainer.getServiceRequest();

  Vector tipiMansioneRows=null;
   tipiMansioneRows= serviceResponse.getAttributeAsVector("M_ListTipiMansione.ROWS.ROW");  

%>

<html>
<head>
<title>Mansioni - Ricerca avanzata</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<%
  String codMansioneItem = (String)serviceRequest.getAttribute("_codMansioneItem");
  String descMansioneItem =  (String)serviceRequest.getAttribute("_descMansioneItem");
  String tipoMansioneItem = (String)serviceRequest.getAttribute("_tipoMansione");
%>
<!-- 
function AggiornaForm (codMansione, desMansione, strTipoMansione) {
  
	  window.opener.document.Frm1.CODMANSIONE.value = codMansione;
    window.opener.document.Frm1.codMansioneHid.value=codMansione;
		window.opener.document.Frm1.DESCMANSIONE.value = desMansione;
  	window.opener.document.Frm1.strTipoMansione.value = strTipoMansione;
  	window.close();

}

  function selectTipo_onClick(codTipoMansione, flgFrequente) {	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (codTipoMansione.value!=""){
      flgFrequente=document.Frm1.flgFrequente;
      paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
      //window.open("AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');
      <% if (serviceRequest.containsAttribute("RICERCA2")) {%>
      		var url = "AdapterHTTP?PAGE=CurrAlberoMansioniPage2&padre="+codTipoMansione.value+paramFrequente;
      <% } else {%>
      		var url = "AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value+paramFrequente;
      <%}%>
      setWindowLocation(url);
    }
  }

  function selectMansione(desMansione, flgFrequente) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
    if (desMansione.value!="") {
      //window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');      
      <%if (!serviceRequest.containsAttribute("RICERCA2")) {%>
      var urlPage = "AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+paramFrequente;
      <%} else  {%>
      var urlPage = "AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+paramFrequente;
      //urlPage +="&_codMansioneItem="+codMansioneItem; 
      /*
      urlPage +="&_descMansioneItem="+descMansioneItem; 
      urlPage +="&_tipoMansioneItem="+tipoMansioneItem; 
      */
      urlPage +="&RICERCA2=2"; 
      <%}%>
	  setWindowLocation(urlPage);
    }
  

  }

  function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (codMansione.value==""){

      descMansione.value="";
      strTipoMansione.value="";      
    }
    else if (codMansione.value!=codMansioneHid.value){
    <%if (!serviceRequest.containsAttribute("RICERCA2")) {%>
      var urlPage = "AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value;// "Mansioni", 'toolbar=0, scrollbars=1';     
    <%} else  {%>
      var urlPage = "AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value;
      /*
      urlPage +="&_codMansioneItem="+codMansioneItem;
      urlPage +="&_descMansioneItem="+descMansioneItem;
      urlPage +="&_tipoMansioneItem="+tipoMansioneItem; 
      */
      urlPage +="&RICERCA2=2"; 
      <%}%>
      setWindowLocation(urlPage);
    }
  }


//-->
</SCRIPT>

</head>
<body class="gestione">
<af:form name="Frm1" method="POST" action="" >
<br/>
<p class="titolo"><b>Mansioni - ricerca avanzata</b></p>
<TABLE class="lista" align="center">
<%SourceBean row_tipiMansione= null;
     String tipiMansione_cod= null;
     String tipiMansione_des=null;
                   %>                


       


 <tr>
    <td class="etichetta">Codice mansione</td>
    <td class="campo">
      <af:textBox 
        classNameBase="input" 
        name="CODMANSIONE" 
        size="7" 
        maxlength="6" 
        value="<%= codMansione.toString() %>" 
  
      />
      
      <af:textBox 
        type="hidden" 
        name="codMansioneHid" 
        value="<%= codMansione.toString() %>" 
      />
      
      <af:textBox 
        type="hidden" 
        name="DESCMANSIONE" 
        value="" 
              />

      
    <af:textBox 
        type="hidden" 
        name="strTipoMansione" 
        value="" 

        
      />
      

          <a href="javascript:selectMansione_onClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
   
    </td>
  </tr>           





  <TR>
        <td>Ricerca per Albero-Indicare il primo livello</td>
        <TD>
         <af:comboBox title="Tipo" name="codTipoMansione" onChange="" >
                  <OPTION value=""></OPTION>
                    <% for(int i=0; i<tipiMansioneRows.size(); i++)
                         { row_tipiMansione = (SourceBean) tipiMansioneRows.elementAt(i);
                          tipiMansione_cod= row_tipiMansione.getAttribute("CODICE").toString();
                          tipiMansione_des= tipiMansione_cod+" - "+row_tipiMansione.getAttribute("DESCRIZIONE").toString();
                         %> 
                         <OPTION value="<%=tipiMansione_cod%>">
                         <%=tipiMansione_des%>
                         </OPTION>
                        <%}%>
          </af:comboBox>
          <A href="javascript:selectTipo_onClick(document.Frm1.codTipoMansione, document.Frm1.flgFrequente);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
    </TD>
  </TR>

     <TR>
        <TD>Ricerca per descrizione</TD>
        <TD>
          <af:textBox size="30"
                       classNameBase="input" name="desMansione" />

          <A href="javascript:selectMansione(document.Frm1.desMansione, document.Frm1.flgFrequente);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
        </TD>
    </TR>

         <TR>
        <TD>Ricerca limitata alle sole mansioni di uso frequente</TD>
        <TD>
          <input type="checkbox" name="flgFrequente" value="" checked="true" />
        </TD>
    </TR>

</TABLE>
</af:form>
<br/>
<center><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="javascript:window.close();"/></center>
<br/>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>