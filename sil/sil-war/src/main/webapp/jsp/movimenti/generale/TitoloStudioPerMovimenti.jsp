<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>




      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean canModify   = false;

SourceBean row = (SourceBean)serviceResponse.getAttribute("M_GetDescTitoloStudio.ROWS");

String desTitolo     = StringUtils.getAttributeStrNotNull(row,"ROW.DESCRIZIONE");
String desTipoTitolo = StringUtils.getAttributeStrNotNull(row,"ROW.TIPOTITOLO");

String codTitolo  = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOTITOLOlav");

String modTitolo   = StringUtils.getAttributeStrNotNull(serviceRequest,"MODIFICATITOLO");
if (!modTitolo.equals("")) 
{ if ( modTitolo.equalsIgnoreCase("true") ) canModify = true;
  else canModify = false;
}
boolean readOnlyStr = !canModify;



String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>



<html>
 <head>
    <!-- ../jsp/movimenti/generale/TitoloStudioPerMovimenti.jsp -->
    <title>Titolo di studio</title>     
    <%@ include file="../../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    
<SCRIPT TYPE="text/javascript">
function ricercaAvanzataTitoliStudio() {
  window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
}

function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {	

  if (codTitolo.value==""){
    strTitolo.value="";
    strTipoTitolo.value="";
      
  }
  else if (codTitolo.value!=codTitoloHid.value){
    window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&codTitolo="+codTitolo.value, "Titoli", 'toolbar=0, scrollbars=1');
  }
}

function clearTitolo() {

  if (document.Frm1.codTitolo.value=="") {   
    document.Frm1.codTitoloHid.value=""; 
    document.Frm1.strTitolo.value=""; 
    document.Frm1.strTipoTitolo.value=""; 
  }

}



function toggleVisStato()
{//Non fa nulla è stata aggiunta solo per poter mantenere in piedi lo stesso meccanismo usato nella pagina delle conoscenze
 //Titolo di studio (e anche perchè sono le sette di sera e si deve consegnare nonsi fa in tempo a mettere in piedi un'altro meccanismo...)
}

function aggiornaCodTitolo()
{ if (document.forms[0].codTitolo.value == "")     { alert("Il \'Codice\' non è valorizzato"); return false; }
  if (document.forms[0].strTipoTitolo.value == "") { alert("Il \'Tipo\' non è valorizzato"); return false; }
  if (document.forms[0].strTitolo.value == "")     { alert("Il \'Corso\' non è valorizzato"); return false; }
  controllaTitoloStudio(document.forms[0].codTitolo.value);
  window.opener.document.forms[0].CODTIPOTITOLOlav.value = window.document.Frm1.codTitolo.value;
  window.close();
}


function controllaTitoloStudio(codTitolo) {
  var strCodTitolo = new String(codTitolo);
  if (strCodTitolo.substring(strCodTitolo.length-2,strCodTitolo.length) != '00') {
    return true;
  }
  else {
    if (confirm('Non è stato indicato un titolo di studio specifico, continuare ?')) {
      return true;
    }
    else {
      return false;
    }
  }
}

function cancellaCampi()
{ //alert("cancellaCampi()");
  document.forms[0].strTipoTitolo.value = "";
  document.forms[0].strTitolo.value = "";
}


</script>

 </head>
 
 <body>
  <br/><br/>
  <af:form name="Frm1" method="POST" action="AdapterHTTP">
    <%out.print(htmlStreamTop);%>
    <br/>
    <p class="titolo">Titolo di studio</p>
    <table class="main" border="0">
              <tr valign="top">
                <td class="etichetta">Codice</td>
                <td class="campo">
                    <af:textBox classNameBase="input" title="Codice del titolo" value="<%=codTitolo%>" name="codTitolo"
                                size="10" maxlength="6" onBlur="clearTitolo();" required="True" onKeyUp="fieldChanged();"
                                readonly="<%= String.valueOf(!canModify) %>" onKeyUp="cancellaCampi();" />&nbsp;                    
                    <af:textBox type="hidden" name="codTitoloHid" value="<%=codTitolo%>"/>
                    <%if (canModify) { %>
                        <A href="javascript:selectTitolo_onClick(document.Frm1.codTitolo, document.Frm1.codTitoloHid, document.Frm1.strTitolo,  document.Frm1.strTipoTitolo);">
                          <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                        <A href="javascript:ricercaAvanzataTitoliStudio();">
                          Ricerca avanzata
                        </A>
                    <%}%>
                </td>
              </tr>
              <tr valign="top">
                <td class="etichetta">Tipo</td>
                <td class="campo">
                  <af:textBox type="hidden" name="flgLaurea" value="" />
                  <af:textBox size="50"
                               value="<%=desTipoTitolo%>"
                               classNameBase="input" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
                </td>
              </tr>
              <tr>
                <td class="etichetta">Corso</td>
                <td class="campo">
                  <af:textArea cols="30" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" value="<%=desTitolo%>" readonly="true" required="true" />
                </td>
              </tr>
      <tr><td>&nbsp;</td></tr>
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2">
      <%if(!readOnlyStr) {%>
          <input class="pulsante" type="button" name="aggiorna" value="Aggiorna" onClick="javascript:aggiornaCodTitolo()"/>
      <%}%>
          <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="javascript:window.close()"/>
          </td>
      </tr>
    </table>
    <%out.print(htmlStreamBottom);%>
  </af:form>
 </body>
</html>