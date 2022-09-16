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
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  
  String apriDiv = "none";


  String strCodMansionePrincipale = "";
  String strDesTipoMansionePrincipale = "";
  String strDesMansionePrincipale = "";
  
  String strCodMansioneAssociata = "";
  String strDesMansioneAssociata = "";

    boolean canInsert = true;
    boolean canModify = true;
    boolean canDelete = true;
    boolean readOnlyStr = true;

    readOnlyStr = !canModify;
%>

<html>

<head>
  <title>Nuova Associazione Mansione</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">
<!--

var flagChanged = false;



function clearMansione() 
{
  if (document.Frm1.CODMANSIONE.value=="") {   
    document.Frm1.codMansioneHid.value=""; 
    document.Frm1.strTipoMansione.value=""; 
    document.Frm1.DESCMANSIONE.value=""; 
    document.Frm1.flgTipo.value="";
  }
}


function fieldChanged() {

  // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
  //alert("field changed !")  
    
  // NOTE: field-check solo se canModify 
    flagChanged = true;
}


function toggleVisStato() 
{
}




function chiudi()
{
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var s= "AdapterHTTP?PAGE=AssMansioniPage";
  s += "&MODULE=M_ListAssMansioni";
  s += "&CDNFUNZIONE=<%=_funzione%>";
  
  setWindowLocation(s);
}




  function controllaCodiceMansione (codMansione) 
  {
    var strCodMansione = new String(codMansione);
    var strSubCodMansione = strCodMansione.substring(strCodMansione.length-2,strCodMansione.length);

    if (strSubCodMansione == '00') {
      alert('E\' stato indicato un gruppo anzichè una mansione specifica!\nScegliere una mansione.');
      return false;
    }
    document.Frm1.codMansionePrincipale.value = strCodMansione;
    document.Frm1.CODMANSIONE.value = strCodMansione.substring(0,strCodMansione.length-2) + "00";
    document.Frm1.flgTipo.value = '1';
    return true;
  }

-->
  </SCRIPT>

  <%@ include file="Mansioni_CommonScripts.inc" %>
  
</head>

<body class="gestione">

  <af:showErrors />


<p align="center">


<p class="titolo">Mansione principale</p>

<div align="center" id="nuovaAssociazioneMansioni">

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
        <af:form  name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCodiceMansione(Frm1.CODMANSIONE.value)">
            <tr valign="top">
                <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
                <td class="campo">
                    <input type="checkbox" name="flgFrequente" value="" checked="true"/>        
                </td>
            </tr>
            <tr>
              <td class="etichetta">Codice mansione</td>
              <td class="campo">
                <af:textBox 
                  classNameBase="input" 
                  name="CODMANSIONE" 
                  title="Codice Mansione"
                  size="7" 
                  maxlength="7" 
                  onBlur="clearMansione();"
                  required="true"
                />

                <af:textBox 
                  type="hidden" 
                  name="codMansioneHid" 
                />
      
                <% if (canModify) { %>
                    <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                    <A href="javascript:ricercaAvanzataMansioni();">
                        Ricerca avanzata
                    </A>
                <%}%>
              </td>
            </tr>           
            <tr valign="top">
              <td class="etichetta">Tipo</td>
              <td class="campo">
                <af:textBox classNameBase="input" name="strTipoMansione" readonly="true" size="48" />
              </td>
            </tr>
            <tr>
              <td class="etichetta">Descrizione</td>
              <td class="campo">
                  <af:textArea cols="30" 
                               rows="4" 
                               name="DESCMANSIONE" 
                               classNameBase="textarea"
                               readonly="true" 
                               required="true"
                               maxlength="100"  />
              </td>
            </tr>
            <tr>
              <td class="campo">
                  <af:textBox type="hidden" name="flgTipo" />
              </td>
            </tr>
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
      <input class="pulsante" type="submit" value="Inserisci" >&nbsp;&nbsp;    
      <input type="hidden" name="PAGE" value="AssMansioniPage">
      <input type="hidden" name="MODULE" value="M_InsertAssMansioni">                
      <input type="hidden" name="codMansionePrincipale" >      
      <input type="button" class="pulsante" value="Chiudi" onclick="chiudi();" >      
      <input type="hidden" name="cdnFunzione" value="<%= _funzione %>" >
    </td>
  </tr>
</table>
</af:form>



</div></p>


  
</body>

</html>
