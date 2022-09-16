<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
   //prelevo la funzione che devo chiamare per aggiornare la pagina
   String funzioneaggiornamento = (String) serviceRequest.getAttribute("AGG_FUNZ");
   String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
   String titolo="Ricerca Ente Accreditato";  
   String cdnLav = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAV");
   String prgColloquioProgramma = (String) serviceRequest.getAttribute("CODPROGRAMMA");
   //Oggetti per l'applicazione dello stile grafico
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
	<head>
		<title><%=titolo%></title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
   <af:linkScript path="../../js/"/>    
    <script language="Javascript">

  </script>

	</head>
	<body class="gestione">
		<p class="titolo"><%=titolo%></p>
		<p align="center">
	<af:form name="Form1" method="POST" action="AdapterHTTP" >
    <input type="hidden" name="PAGE" value="PattoListaEnteAccreditatoPage"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>

    <!--Passo l'informazione su che tipo di ricerca dovrò eseguire e sulla funzione
    da chiamare al ritorno-->
    <input type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
    <input type="hidden" name="CONTESTOPROVENIENZA" value="PATTO"/>
    <input type="hidden" name="CDNLAV" value="<%=cdnLav%>"/>
    <input type="hidden" name="CODPROGRAMMA" value="<%=Utils.notNull(prgColloquioProgramma)%>"/>
    
    <%out.print(htmlStreamTop);%>
    <table class="main">

            <tr>
              <td class="etichetta">Codice Fiscale</td>
              <td class="campo">
                <af:textBox type="text" name="cf" validateOnPost="true" value="" size="30" maxlength="16"/>
              </td>
            </tr>            
            <tr>
              <td class="etichetta">Ragione Sociale</td>
              <td class="campo">
                <af:textBox type="text" name="ragioneSociale" validateOnPost="true" value="" size="30" maxlength="100"/>                
              </td>
            </tr>

    
    <!--Inserisco i pulsanti necessari-->
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2" align="center">
      <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
      </td>
    </tr>         
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2" align="center">
      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
      </td>
    </tr>
    </table>
    <%out.print(htmlStreamBottom);%>
    </af:form>
   </center>
  </body>
</html>
