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
  Vector tipiMansioneRows =  serviceResponse.getAttributeAsVector("M_ListTipiMansione.ROWS.ROW");
  String flgIdo = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGIDO");
  String indiceMansione = StringUtils.getAttributeStrNotNull(serviceRequest, "indiceMansione");
%>

<html>
<head>
<title>Mansioni - Ricerca avanzata</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script language="javascript">
	var flgProvIdo = '<%=flgIdo%>';
	var indiceFieldMansione = '<%=indiceMansione%>';
</script>

<SCRIPT TYPE="text/javascript">

<!--
function AggiornaForm (codMansione, desMansione, strTipoMansione) {
	if (indiceFieldMansione == '') {
		window.opener.document.Frm1.CODMANSIONE.value = codMansione;
	    window.opener.document.Frm1.codMansioneHid.value=codMansione;
		window.opener.document.Frm1.DESCMANSIONE.value = desMansione;
	  	window.opener.document.Frm1.strTipoMansione.value = strTipoMansione;
	}
	else {
		var fieldMansione = eval('window.opener.document.Frm1.CODMANSIONE' + indiceFieldMansione);
		var fieldMansioneHid = eval('window.opener.document.Frm1.codMansioneHid' + indiceFieldMansione);
		var fieldMansioneDesc = eval('window.opener.document.Frm1.DESCMANSIONE' + indiceFieldMansione);
		var fieldMansioneTipo = eval('window.opener.document.Frm1.strTipoMansione' + indiceFieldMansione);
		fieldMansione.value = codMansione;
		fieldMansioneHid.value = codMansione;
		fieldMansioneDesc.value = desMansione;
		fieldMansioneTipo.value = strTipoMansione;
	}
  	window.close();

}

  function selectTipo_onClick(codTipoMansione, flgFrequente) {	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    //var index = codTipoMansione.selectedIndex;
    //var optionSelected = codTipoMansione[index];
    //alert(optionSelected.text);
    //alert(optionSelected.value);
    
	    if (codTipoMansione.value!=""){
	      //flgFrequente=document.Frm1.flgFrequente;
	      //paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
	      //window.open("AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');
	      var url = "AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value;
	      //if (flgProvIdo == 'S') {
	      //	url = url + "&FLGIDO=S";
	     //}
	     if (indiceFieldMansione != '') {
      		url = url + "&indiceMansione="+indiceFieldMansione;
      	 }
	     setWindowLocation(url);
	    }
  }

  function selectMansione(desMansione, flgFrequente) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
        
    if (desMansione.value!="") {
      //window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');      
      var url = "AdapterHTTP?PAGE=RicercaMansionePage&TORNALISTA=&desMansione="+desMansione.value+paramFrequente;
      if (flgProvIdo == 'S') {
    	url = url + "&FLGIDO=S";
      }
      if (indiceFieldMansione != '') {
      	url = url + "&indiceMansione="+indiceFieldMansione;
      }
      setWindowLocation(url);
    }
  }

  function evitaEnter() {
    if (window.event.keyCode == 13) window.event.keyCode = 0;
  }

-->
</SCRIPT>

</head>
<body class="gestione">
<af:form name="Frm1" method="POST" action="" dontValidate="true">
<!--onKeyPress="evitaEnter()"--> 
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
          <af:textBox size="30"
                       classNameBase="input" name="desMansione" />

          <A href="javascript:selectMansione(document.Frm1.desMansione, document.Frm1.flgFrequente);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
        </TD>
    </TR>

      <TR>
        <TD>Ricerca limitata alle sole mansioni di uso frequente</TD>
        <TD>
          <input type="checkbox" name="flgFrequenteDisabilitato" value="" checked="true" />
          <input type="hidden" name="flgFrequente" value="" checked="false" />
        </TD>
        
    </TR> 

</TABLE>
</af:form>
<br/>
<center><input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="javascript:window.close();"/></center>
<br/>
</body>
</html>