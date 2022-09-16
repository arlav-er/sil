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
   Vector tipiAtecoRows = serviceResponse.getAttributeAsVector("M_GetTipiAttivita.ROWS.ROW");
%>

<html>
<head>
<title>Codici di attività</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<!--

function AggiornaForm (codAteco, strAteco, strTipoAteco) {
  
	  window.opener.document.Frm1.codAteco.value = codAteco;
    window.opener.document.Frm1.codAtecoHid.value=codAteco;
		window.opener.document.Frm1.strAteco.value = strAteco.replace('^', '\'');
  	window.opener.document.Frm1.strTipoAteco.value = strTipoAteco.replace('^', '\'');
  	window.close();

}


  function selectTipo_onClick(codTipoAteco) {	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (codTipoAteco.value!=""){
      //window.open("AdapterHTTP?PAGE=CurrAlberoATECOPage&padre="+codTipoAteco.value, "Attività", 'toolbar=0, scrollbars=1');
      var url = "AdapterHTTP?PAGE=CurrAlberoATECOPage&padre="+codTipoAteco.value;
      setWindowLocation(url);
    }
  }

  function selectAteco(strAteco) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (strAteco.value!="") {
      //window.open("AdapterHTTP?PAGE=RicercaAtecoPage&strAteco="+strAteco.value, "Attività", 'toolbar=0, scrollbars=1');
      var url = "AdapterHTTP?PAGE=RicercaAtecoPage&TORNALISTA=&strAteco="+strAteco.value;
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
<p class="titolo"><b>Codici di attività - ricerca avanzata</b></p>
<TABLE class="lista" align="center">
<%SourceBean row_tipiAteco= null;
  String tipiAteco_cod= null;
  String tipiAteco_des=null;
                   %>                
    <TR>
        <td>Ricerca per Albero-Indicare il primo livello</td>
        <TD nowrap>
         <af:comboBox title="Tipo" name="codTipoAteco" onChange="" >
                  <OPTION value=""></OPTION>
                    <% for(int i=0; i<tipiAtecoRows.size(); i++)
                         { row_tipiAteco = (SourceBean) tipiAtecoRows.elementAt(i);
                          tipiAteco_cod= row_tipiAteco.getAttribute("CODICE").toString();
                          tipiAteco_des= row_tipiAteco.getAttribute("DESCRIZIONE").toString();
                         %> 
                         <OPTION value="<%=tipiAteco_cod%>">
                         <%=tipiAteco_des%>
                         </OPTION>
                        <%}%>
          </af:comboBox>
          <A href="javascript:selectTipo_onClick(document.Frm1.codTipoAteco);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
    </TD></TR>
    <TR>
        <TD>Ricerca per descrizione</TD>
        <TD>
          <af:textBox size="30"
                       classNameBase="input" name="strAteco" />

          <A href="javascript:selectAteco(document.Frm1.strAteco);">
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