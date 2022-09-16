<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  // NOTE: Attributi della pagina (pulsanti e link) 
 
 
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
  String cdnLavoratore  = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  
  
  
  //Programma
 //Dati Programmi
  String     strtitolo         = null;
  String     strnote           = null; 
  String     strcodiceesterno  = null; 
  String     datinizio         = null; 
  String     datfine           = null; 
  String     numklokloprogrammaq = null;
  String     codstatoprogramma = null;
  String     prgprogrammaq      = null;	
  String     dtmIns             = null; 
  String     dtmMod             = null;    
  BigDecimal cdnUtIns           = null; 
  BigDecimal cdnUtMod           = null;
  String     strAzienda         = null;
  

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Ricerca Programmi</title>


  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        //attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </SCRIPT>

 
<!--Funzioni per l'aggiornamento della form -->
<script type="text/javascript"> </script>
</head>
<body class="gestione" onload="rinfresca();">
  <br/>
  <p class="titolo">Ricerca Programmi</p>
  <br/>
		<center>
		<af:form name="Frm1" method="POST" action="AdapterHTTP">
        
        <input type="hidden" name="PAGE" value="ProgrammiRicercaListaPage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
        <input type="hidden" name="prgprogrammaq" value="<%=prgprogrammaq%>"/>
        <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
       
      		
      <%out.print(htmlStreamTop);%>
      <table class="main" border="0">     
           <tr>
            <td colspan="2">

						<table class="main" width="100%" border="0">
							<tr>
								<td class="etichetta">Titolo</td>
								<td class="campo"><af:textBox classNameBase="input"
										type="text" name="strtitolo" value="<%=strtitolo%>" size="50"
										maxlength="50" /></td>
							</tr>
								<tr>
								<td class="etichetta">Ente</td>
								<td class="campo"><af:textBox classNameBase="input"
										type="text" name="strAzienda"
										value="<%=strAzienda%>" size="50" maxlength="50" /></td>
							</tr>
							<tr>
								<td class="etichetta">Data Inizio</td>
								<td class="campo"><af:textBox classNameBase="input"
										type="date" name="datinizio" value="<%=datinizio%>"
										validateOnPost="true" size="12" maxlength="10"
										onKeyUp="fieldChanged();" title="Data Inizio" /></td>
							</tr>
							<tr>
								<td class="etichetta">Data Fine</td>
								<td class="campo"><af:textBox classNameBase="input"
										type="date" name="datfine" value="<%=datfine%>"
										validateOnPost="true" size="12" maxlength="10"
										onKeyUp="fieldChanged();" title="Data Inizio" />
								</td>
							</tr>
						
							
							<tr>
								<td class="etichetta">Stato</td>
								<td class="campo"><af:comboBox classNameBase="input"
										onChange="fieldChanged()" name="codstatoprogramma"
										moduleName="M_Combo_Stato_Programma"
										selectedValue="<%=codstatoprogramma%>" addBlank="true"
										title="Stato Programma" /></td>
							</tr>

						</table></td>
        </tr>
 
  
      <tr><td colspan="2">&nbsp;</td></tr>                                  
      <tr>
        <td colspan="2" align="center">
        <input class="pulsanti" type="submit" name="Cerca" value="Cerca"/>
        &nbsp;&nbsp;
        <input class="pulsanti" type="reset" name="annulla" value="Annulla"/>
        </td>
      </tr>
      <tr>
		<td colspan="2" align="center">
		&nbsp;
		</td>
	  </tr>
      
        
      </table>
      <%out.print(htmlStreamBottom);%>
      </af:form>
      <br/> 
	</center>

	</body>
</html>
