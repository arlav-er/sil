<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*, it.eng.afExt.utils.StringUtils"%>
<%@ taglib uri="aftags" prefix="af"%>

<% 
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
      var url = "AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value+paramFrequente;
      setWindowLocation(url);
    }
  }

  function selectMansione(desMansione, flgFrequente) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
    if (desMansione.value!="") {
      //window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');      
      var url = "AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+paramFrequente;
      setWindowLocation(url);
    }
  }

-->
</SCRIPT>

</head>
<body class="gestione">
<af:form name="Frm1" method="POST" action="" onSubmit="false" dontValidate="true">
<br/>
<p class="titolo"><b>Mansioni - ricerca avanzata</b></p>
<TABLE class="lista" align="center">
<%SourceBean row_tipiMansione= null;
     String tipiMansione_cod= null;
     String tipiMansione_des=null;
%>                


  <TR>
        <td>Ricerca per Albero-Indicare il primo livello</td>
        <TD nowrap  >
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
          <af:textBox size="30" classNameBase="input" name="desMansione" />

          <A href="javascript:selectMansione(document.Frm1.desMansione, document.Frm1.flgFrequente);">
                <img src="../../img/binocolo.gif" alt="Cerca"></A>
        </TD>
    </TR>

         <TR style="display: none">
        <TD>Ricerca limitata alle sole mansioni di uso frequente</TD>
        <TD>
          <input type="checkbox" name="flgFrequente" value="" />
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