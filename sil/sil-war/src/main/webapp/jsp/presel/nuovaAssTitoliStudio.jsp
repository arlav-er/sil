
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*

"%>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  String principale = (String) serviceRequest.getAttribute("PRINCIPALE");
  boolean isPrincipale = Boolean.valueOf(principale).booleanValue();
  boolean indietro = serviceRequest.containsAttribute("INDIETRO");
  
  String apriDiv = "none";

  String strCodTitoloPrincipale = "";
  String strDesTitoloPrincipale = "";
  String strDesTipoTitoloPrincipale = "";
  
  String strCodTitoloAssociato = "";
  String strDesTitoloAssociato = "";

  if (!isPrincipale || indietro) {
    strCodTitoloPrincipale = (String) serviceRequest.getAttribute("codTitolo");
    strDesTipoTitoloPrincipale = (String) serviceRequest.getAttribute("strTipoTitolo");
    strDesTitoloPrincipale = (String) serviceRequest.getAttribute("strTitolo");
    if (strCodTitoloPrincipale == null)
      strCodTitoloPrincipale = "";
    if (strDesTipoTitoloPrincipale == null)
      strDesTipoTitoloPrincipale = "";
    if (strDesTitoloPrincipale == null)
      strDesTitoloPrincipale = "";
  }

%>

<html>

<head>
  <title>Nuova Associazione Titoli di Studio</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">
<!--

var flagChanged = false;

function controllaTitoloStudio(codTitolo) {
  var strCodTitolo = new String(codTitolo);
  if (strCodTitolo.substring(strCodTitolo.length-2,strCodTitolo.length) != '00') {
    return true;
  }
  alert('E\' stato indicato un gruppo anzichè un titolo di studi specifico!\nScegliere un titolo di studi.');
  return false;
}


function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {	

  if (codTitolo.value==""){
    strTitolo.value="";
    strTipoTitolo.value="";
      
  }
  else if (codTitolo.value!=codTitoloHid.value){
    window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&codTitolo="+codTitolo.value, "Corsi", 'toolbar=0, scrollbars=1');
  }
}


function clearTitolo() {

  if (Frm1.codTitolo.value=="") {   
    Frm1.codTitoloHid.value=""; 
    Frm1.strTitolo.value=""; 
    Frm1.strTipoTitolo.value="";
    <% if (!isPrincipale) { %>
      Frm1.flgTipo.value="";
    <% } %>
  }

}

function fieldChanged() {

  // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
  //alert("field changed !")  
    
  // NOTE: field-check solo se canModify 
    flagChanged = true;
}

function ricercaAvanzataTitoliStudio() {
  window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
}


function toggleVisStato() 
{
}


function indietro()
{
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var s= "AdapterHTTP?PAGE=NuovaAssTitoliStudioPage";
  s += "&MODULE=M_NuovaAssTitoliStudio";
  s += "&PRINCIPALE=true";      
  s += "&INDIETRO=";
  s += "&CDNFUNZIONE=<%=_funzione%>";
  
  s += "&CODTITOLO=<%=strCodTitoloPrincipale%>";
  s += "&strTipoTitolo=<%=strDesTipoTitoloPrincipale%>";
  s += "&strTitolo=<%=strDesTitoloPrincipale%>";

  setWindowLocation(s);

}


function chiudi()
{
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var s= "AdapterHTTP?PAGE=AssTitoliStudioPage";
  s += "&MODULE=M_ListAssTitoliStudio";
  s += "&CDNFUNZIONE=<%=_funzione%>";
  
  setWindowLocation(s);
  
}

-->
  </SCRIPT>


</head>

<body class="gestione">

  <!--<af:showMessages prefix="M_InsertAssTitoliStudio" />
  <af:showMessages prefix="M_DeleteAssTitoliStudio" />-->
  <af:showErrors />


<p align="center">

<% if (isPrincipale) { %>
<p class="titolo">Titolo principale</p>
<% } else { %>
<p class="titolo">Titolo associato</p>
<% } %>

<div align="center" id="nuovaAssociazioneTitoliStudio">

<table maxwidth="96%" width="96%" align="center" margin="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="sfondo_lista" valign="top" align="left" width="6" height="6px"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td>
    <td class="sfondo_lista" valign="top" align="right" width="6" height="6px"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td class="sfondo_lista" width="6">&nbsp;</td>
    <td class="sfondo_lista" align="center">
      <TABLE class="lista" align="center">
        <af:form  name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaTitoloStudio(Frm1.codTitolo.value)">
  <% if (!isPrincipale) { %>
          <tr valign="top">  
            <td class="etichetta">Titolo Principale</td>
            <td class="campo">
             <af:textBox size="50"
               classNameBase="input" title="Titolo principale" name="strTitoloPrincipale" value="<%=strDesTitoloPrincipale%>" readonly="true"  /> 
            </td>
          </tr>
          <tr><td>&nbsp;</td></tr>
    <% } %>
            <tr valign="top">
              <td class="etichetta">Codice</td>
              <td class="campo">
                    <af:textBox classNameBase="textbox" onKeyUp="fieldChanged();" title="Codice del titolo" name="codTitolo" size="10" maxlength="8" onBlur="clearTitolo();" required="True" />&nbsp;                    
                    <af:textBox type="hidden" name="codTitoloHid" />
                    <A href="javascript:selectTitolo_onClick(Frm1.codTitolo, Frm1.codTitoloHid, Frm1.strTitolo,  Frm1.strTipoTitolo);">
                      <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                    <A href="javascript:ricercaAvanzataTitoliStudio();">
                      Ricerca avanzata
                    </A>                
              </td>
            </tr>
            <tr valign="top">
              <td class="etichetta">Tipo</td>
              <td class="campo">
                  <af:textBox type="hidden" name="flgLaurea" />
                  <af:textBox size="50"
                               classNameBase="input" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
              </td>
            </tr>
            <tr>
              <td class="etichetta">Corso</td>
              <td class="campo">
                  <af:textArea cols="30" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" readonly="true" required="true" />
              </td>
            </tr>
    <% if (indietro) { %>
      <script>
        document.Frm1.codTitolo.value = "<%=strCodTitoloPrincipale%>";
        document.Frm1.strTipoTitolo.value = "<%=strDesTipoTitoloPrincipale%>";
        document.Frm1.strTitolo.value = "<%=strDesTitoloPrincipale%>";        
      </script>      
    <% } %>
    <% if (!isPrincipale) { %>
            <tr>
              <td class="etichetta">Seleziona tipo</td>
              <td class="campo">
                <af:comboBox name="flgTipo" onChange="fieldChanged();" title="Seleziona tipo" required="True">
                  <OPTION value=""></OPTION>
                  <OPTION value="1">Primario</OPTION>
                  <OPTION value="2">Secondario</OPTION>
                </af:comboBox>
              </td>
           </tr>
    <% } %>
      </TABLE>
    </td>
    <td class="sfondo_lista" width="6">&nbsp;</td>
  </tr>
  <tr valign="bottom">
    <td class="sfondo_lista" valign="bottom" align="left" width="6" height="6px"><img src="../../img/angoli/bia4.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td><td class="sfondo_lista" valign="bottom" align="right" width="6" height="6px"><img src="../../img/angoli/bia3.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td colspan="2" align="center">
    <% if (isPrincipale) { %>
      <input class="pulsante" type="submit" value="Avanti" />&nbsp;&nbsp;    
      <input type="hidden" name="PAGE" value="NuovaAssTitoliStudioPage">
      <input type="hidden" name="MODULE" value="M_NuovaAssTitoliStudio">                      
    <% } else { %>
      <input type="button" class="pulsante" value="Indietro" onclick="indietro();" />&nbsp;&nbsp;
      <input class="pulsante" type="submit" value="Associa" />&nbsp;&nbsp;    
      <input type="hidden" name="PAGE" value="AssTitoliStudioPage">
      <input type="hidden" name="MODULE" value="M_InsertAssTitoliStudio">                
      <input type="hidden" name="codTitoloPrincipale" value="<%= strCodTitoloPrincipale %>"/>      
    <% } %>
      <input type="button" class="pulsante" value="Chiudi" onclick="chiudi();" />      
      <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
    </td>
  </tr>
</table>
</af:form>



</div></p>


  
</body>

</html>
