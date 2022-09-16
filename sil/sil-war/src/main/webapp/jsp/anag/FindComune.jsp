<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.*"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);



    boolean ricercaXCodice=(boolean) serviceRequest.containsAttribute("codcom");
    String  ricercaComune = StringUtils.getAttributeStrNotNull(serviceRequest,"tipo");
    
    String retcod= StringUtils.getAttributeStrNotNull( serviceRequest,"retcod");
    String retcodhid = retcod.concat("Hid");   
    String retnome= StringUtils.getAttributeStrNotNull( serviceRequest,"retnome");
    String retnomehid=retnome.concat("Hid");
    String retcap= serviceRequest.containsAttribute("retcap")?serviceRequest.getAttribute("retcap").toString():"";
    String retcaphid=retcap.concat("Hid");   
    String codCPIcampo = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIcampo");

    String funzione    = StringUtils.getAttributeStrNotNull(serviceRequest,"funzione");
    String funzione_2  = StringUtils.getAttributeStrNotNull(serviceRequest,"funzione_2");

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
        if(strCap ==''){ 
        	if("<%=retcap%>" === 'strCapDom' && typeof(window.opener.document.forms[0].capEsteroDomicilio) != "undefined"){
     			window.opener.document.forms[0].<%=retcap%>.value = "00000";
     	        if (window.opener.document.forms[0].<%=retcaphid%> != null) {
     	            window.opener.document.forms[0].<%=retcaphid%>.value = "00000";  
     	        }
    		}else 
        	if(typeof(window.opener.document.forms[0].capEsteroResidenza) != "undefined"){
	 			window.opener.document.forms[0].<%=retcap%>.value = "00000";
	 	        if (window.opener.document.forms[0].<%=retcaphid%> != null) {
	 	            window.opener.document.forms[0].<%=retcaphid%>.value = "00000";  
	 	        }
			}
        }
          
    <%}  %>
 
    <%if (!codCPIcampo.equals("")) { %>
        window.opener.document.forms[0].<%=codCPIcampo%>.value = codCPI;
    <%} %>

        
    //Notifico che ho già inserito il comune e che tale inserimento nondeve essere rifatto
    //quando si esegue la submit nella pagina chiamante
    window.opener.campiComuneCompletati(true);


   //--------------------------------------
   //Questa sezione provvede a invocare la funzione passata come argomento, qualora si rendano necessarie delle
   //operazioni da eseguire in seguito alla ricerca di un comune (vedi pagina dei recapiti per un lavoratore)
   <%if (funzione != "") {%>
     <%-- alert("FUNCTION:: "+<%=funzione%>); --%>
     window.opener.<%=funzione%>;
   <%}%>
   //--------------------------------------

   <%if (funzione_2 != "") {%>
     window.opener.<%=funzione_2%>;
   <%}%>


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
        //Ho ottenuto un solo comune/stato posso 
        //inserirlo direttamente nella pagina chiamante 
        SourceBean row = (SourceBean)rows.get(0);
        String codcom           = StringUtils.getAttributeStrNotNull(row,"CODCOM");
        String strdenominazione = StringUtils.getAttributeStrNotNull(row,"STRDENOMINAZIONE");
        String stristat         = StringUtils.getAttributeStrNotNull(row,"STRISTAT");
        String strcap           = StringUtils.getAttributeStrNotNull(row,"STRCAP");
        String codcpi           = StringUtils.getAttributeStrNotNull(row,"CODCPI");
               codcpi = codcpi.trim();

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
        jsCommand.append(strcap);           
        jsCommand.append("','");
        jsCommand.append(codcpi);
        jsCommand.append("');");
%>
    <script><%=jsCommand.toString()%></script>
<%  } 
    else if (rows.size()==0)
    { //Non ho trovato nessun comune/stato.%>
      <br/><br/>
      <%out.print(htmlStreamTop);%>
      <table  class="main" border="0">
      <tr><td colspan="2">&nbsp;</td></tr><%
      if( ricercaComune.equalsIgnoreCase("COMUNI") )
      { if(ricercaXCodice){%> 
           <tr><td colspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td colspan="2"><strong>Controllare che il codice non sia errato</strong></td></tr> 
        <%} else {%> 
           <tr><td colspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td colspan="2"><strong>corrispondente alla descrizione data</strong></td></tr> 
        <%}
      }
      else if( ricercaComune.equalsIgnoreCase("STATI") ) 
      { if(ricercaXCodice){%> 
           <tr><td colspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td colspan="2"><strong>Controllare che il codice non sia errato</strong></td></tr> 
        <%} else {%> 
           <tr><td colspan="2"><strong>Nessun comune trovato</strong></td></tr>
           <tr><td colspan="2"><strong>corrispondente alla descrizione data</strong></td></tr> 
        <%}
      }
      else
      { if(ricercaXCodice){%> 
           <tr><td colspan="2"><strong>Nessun comune o stato trovato</strong></td></tr>
           <tr><td colspan="2"><strong>Controllare che il codice non sia errato</strong></td></tr> 
        <%} else {%> 
           <tr><td colspan="2"><strong>Nessun comune o stato trovato</strong></td></tr>
           <tr><td colspan="2"><strong>corrispondente alla descrizione data</strong></td></tr> 
        <%}
      }%>
      
      
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr><td colspan="2"><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="window.close()"/></td></tr>
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr><td colspan="2">&nbsp;</td></tr>
      </table>
      <%out.print(htmlStreamBottom);
    }

    else 
    {//Ho trovato più comuni/stati che inizano con la stringa passata
    %><br/><af:list moduleName="M_RicercaComuneStato" jsSelect="AggiornaForm"/><br/>
      <center><input type="button" class="pulsante" name="chiudi" value="chiudi" onClick="window.close()"/></center>
  <%}%>


</body>
</html>
