<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);



    boolean ricercaXCodice=(boolean) serviceRequest.containsAttribute("codcom");
    String  ricercaComune = StringUtils.getAttributeStrNotNull(serviceRequest,"tipo");
    
    String retcod=(String) serviceRequest.getAttribute("retcod");
    String retcodhid = retcod.concat("Hid");   
    String retnome=(String) serviceRequest.getAttribute("retnome");
    String retnomehid=retnome.concat("Hid");
    String retcap= serviceRequest.containsAttribute("retcap")?serviceRequest.getAttribute("retcap").toString():"";
    String retcaphid=retcap.concat("Hid");   

    boolean nuovoLav = serviceRequest.containsAttribute("nuovoLav");
%>



<html>
<head>
<title>Ricerca Comune</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
function AggiornaForm (codCom, strDenominazione, strIstat, strCap, codCPI) {

    window.opener.document.forms[0].<%=retcod%>.value = codCom;
    if (window.opener.document.forms[0].<%=retcodhid%> != null) {
        window.opener.document.forms[0].<%=retcodhid%>.value = codCom;  
    }

    if(strIstat != '') {
        if (strIstat.charAt(strIstat.length-1)==' ') strIstat = strIstat.substring(0, strIstat.length-1);
        window.opener.document.forms[0].<%=retnome%>.value = strDenominazione + ' (' + strIstat + ')';
    }
    else {
        window.opener.document.forms[0].<%=retnome%>.value = strDenominazione;
    }

        if (window.opener.document.forms[0].<%=retnomehid%> != null) {
            window.opener.document.forms[0].<%=retnomehid%>.value = strDenominazione + ' (' + strIstat + ')';
        }
    <%if (!retcap.equals("")) { %>
        window.opener.document.forms[0].<%=retcap%>.value = strCap;
        if (window.opener.document.forms[0].<%=retcaphid%> != null) {
            window.opener.document.forms[0].<%=retcaphid%>.value = strCap;  
        }
    <%} %>

        
    //Notifico che ho già inserito il comune e che tale inserimento nondeve essere rifatto
    //quando si esegue la submit nella pagina chiamante
    window.opener.campiComuneCompletati(true);


    
    /* gestione codcpi nella pagina degli indirizzi */
    if (arguments.length==5) {
        if (codCPI!=null) {
            try {
                if (window.opener.document.forms[0].codCPIifDOMeqRESHid!=null) { // esiste questo campo: siamo nella pagina degli indirizzi
                    findFromResidenza = <%= retcod.equalsIgnoreCase("codComRes")%>;
                    codCpiItem= window.opener.document.forms[0].codCPI.value;
                    strCpiItem = window.opener.document.forms[0].strCPI.value;
                    if (window.opener.document.forms[0].codComdom != null) {
                      codComItem = window.opener.document.forms[0].codComdom.value;
                    }
                    if (window.opener.document.forms[0].strComdom != null) {
                      strComItem = window.opener.document.forms[0].strComdom.value;
                    }
                    if (window.opener.document.forms[0].strCapDom != null) {
                      strCapItem = window.opener.document.forms[0].strCapDom.value;
                    }
                    if (window.opener.document.forms[0].strIndirizzodom != null) {
                      strIndirizzoItem = window.opener.document.forms[0].strIndirizzodom.value;
                    }
                    if (window.opener.document.forms[0].strLocalitadom != null) {
                      strLocalitaItem = window.opener.document.forms[0].strLocalitadom.value;
                    }
                    //
                    if (findFromResidenza ){
                        // questa e' l'unica condizione per la chiamata del modulo "M_SaveLavoratoreIndirizzi_DOM_uguale_RES"
                        if (codComItem=="") window.opener.document.forms[0].codCPIifDOMeqRESHid.value=codCPI; 
                    }
                    else {
                        // la ricerca e' avvenuta dalla sezione del domicilio:  
                        // debbo fare dei controlli ed emettere opportuni messaggi
                        if (codCpiItem=="" && strCpiItem=="") {
                            window.opener.document.forms[0].codCPI.value=codCPI;
                            if (window.opener.document.forms[0].codCPIText != null){
                              window.opener.document.forms[0].codCPIText.value=codCPI;
                            }
                            window.opener.document.forms[0].strCPI.value=strDenominazione+' ('+strIstat+')'; 
                        }
                        else {
                          if (window.opener.document.forms[0].codCPI.value != codCPI) {
                             //var warningStr = "Il comune di domicilio trovato ("+strDenominazione+")\nnon corrisponde con il Cpi di competenza:\n\ncod CPI trovato: "+codCPI+"\ncod CPI inserito: "+codCpiItem+"\n\nCambiare il CPI di compentenza?";
                             var warningStr = "Il comune di domicilio trovato non corrisponde con il CPI di competenza:\n\ncomune trovato: "+strDenominazione+"\ncomune con CPI compentente: "+strCpiItem+"\n\nCambiare il CPI di compentenza?";
                              if ( window.opener.document.forms[0].inserimentoLav != null) {
                                 if( confirm(warningStr) )
                                 {  
                                    window.opener.document.forms[0].codCPI.value=codCPI;
                                    if (window.opener.document.forms[0].codCPIText != null){
                                      window.opener.document.forms[0].codCPIText.value=codCPI;
                                    }
                                    window.opener.document.forms[0].strCPI.value=strDenominazione+' ('+strIstat+')'; 
                                 }
                              } else {
                                 alert("Il comune di domicilio trovato non corrisponde con il CPI di competenza:\n\ncomune trovato: "+strDenominazione+"\ncomune con CPI compentente: "+strCpiItem);
                              }
                          }
                        }
                    
                    }//else domicilio
                }
            }catch(e) {alert(e.value);} 
        }
    }
    
    window.close();
}

</SCRIPT>
</head>
<body class="gestione">
<%  
    SourceBean rows_ = (SourceBean) serviceResponse.getAttribute("M_RicercaComuneStato.ROWS");
    Vector rows = serviceResponse.getAttributeAsVector("M_RicercaComuneStato.ROWS.ROW");

    int currentPage = Integer.parseInt(rows_.getAttribute("CURRENT_PAGE").toString());
    
    if (rows.size()==1 && currentPage == 1) {
        //Ho ottenuto un solo comune/stato costruisco posso 
        //inserirlo direttamente nella pagina chiamante 
        SourceBean row = (SourceBean)rows.get(0);
        String codcom           = (String)row.getAttribute("CODCOM");
        String strdenominazione = (String)row.getAttribute("STRDENOMINAZIONE");
        String stristat         = (String)row.getAttribute("STRISTAT");
        String strcap           = (String )row.getAttribute("STRCAP");
        String codcpi           = (String)row.getAttribute("CODCPI");

        strdenominazione = StringUtils.replace(strdenominazione,"'","\\'");


        //Costrisco la chiamata alla funzion js Aggiorna
        //implementata sopra in base ai parametri passati
        StringBuffer jsCommand = new StringBuffer();
        jsCommand.append("AggiornaForm('");
        jsCommand.append(codcom);
        jsCommand.append("','");
        jsCommand.append(strdenominazione);
        jsCommand.append("','");
        jsCommand.append(stristat);
        jsCommand.append("','");
        if (strcap == null)
          strcap = "";
        jsCommand.append(strcap);           
        if (codcpi!=null) {
            jsCommand.append("','");
            jsCommand.append(codcpi);
        }
        jsCommand.append("');");
%>
    <script><%=jsCommand.toString()%></script>
<%  } 
    else if (rows.size()==0)
    { //Non ho trovato nessun comune/stato.%>
      <br/><br/>
      <%out.print(htmlStreamTop);%>
      <table  class="main" border="0">
      <tr><td collspan="2">&nbsp;</td></tr><%
      if( ricercaComune.equalsIgnoreCase("COMUNI") )
      { if(ricercaXCodice){%> 
           <tr><td collspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td collspan="2"><strong>Controllare che il codice non sia errato</strong></td></tr> 
        <%} else {%> 
           <tr><td collspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td collspan="2"><strong>corrispondente alla descrizione data</strong></td></tr> 
        <%}
      }
      else if( ricercaComune.equalsIgnoreCase("STATI") ) 
      { if(ricercaXCodice){%> 
           <tr><td collspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td collspan="2"><strong>Controllare che il codice non sia errato</strong></td></tr> 
        <%} else {%> 
           <tr><td collspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td collspan="2"><strong>corrispondente alla descrizione data</strong></td></tr> 
        <%}
      }
      else
      { if(ricercaXCodice){%> 
           <tr><td collspan="2"><strong>Nessun comune o stato trovato</strong></td></tr>
           <tr><td collspan="2"><strong>Controllare che il codice non sia errato</strong></td></tr> 
        <%} else {%> 
           <tr><td collspan="2"><strong>Nessun comune o stato trovato</strong></td></tr>
           <tr><td collspan="2"><strong>corrispondente alla descrizione data</strong></td></tr> 
        <%}
      }%>
      
      
      <tr><td collspan="2">&nbsp;</td></tr>
      <tr><td collspan="2"><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="window.close()"/></td></tr>
      <tr><td collspan="2">&nbsp;</td></tr>
      <tr><td collspan="2">&nbsp;</td></tr>
      </table>
      <%out.print(htmlStreamBottom);
    }

    else 
    {//Ho trovato più comuni/stati che inizano con la stringa passata
    %><br/><af:list moduleName="M_RicercaComuneStato" jsSelect="AggiornaForm"/><br/>
      <center><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="window.close()"/></center>
  <%}%>

<%@ include file="/jsp/MIT.inc" %>
</body>
</html>
