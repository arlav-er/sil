<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,  
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

BigDecimal cdnUtIns 		= 	null;
String dtmIns 				= 	"";
BigDecimal cdnUtMod 		= 	null;
String dtmMod 				= 	"";	



Testata operatoreInfo 	= 	null;
	


String _page = (String) serviceRequest.getAttribute("PAGE");
PageAttribs attributi =	new PageAttribs(user, _page);

boolean canModify =	attributi.containsButton("AGGIORNA");
boolean btnAggiorna	= true;
btnAggiorna	= canModify && btnAggiorna;

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

SourceBean testataGrad = (SourceBean) serviceResponse.getAttribute("M_GetTestataGraduatoriaAnnuale");
SourceBean testata = (SourceBean) testataGrad.getAttribute("ROWS.ROW");

cdnUtIns 				= (BigDecimal) testata.getAttribute("CDNUTINS"); 
dtmIns 					= StringUtils.getAttributeStrNotNull(testata, "DTMINS");
cdnUtMod				= (BigDecimal) testata.getAttribute("CDNUTMOD");
dtmMod 					= StringUtils.getAttributeStrNotNull(testata, "DTMMOD");

operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(testata, "codMonoTipoGrad");
String strnota = StringUtils.getAttributeStrNotNull(testata, "STRNOTA");
String dataRifGrad = StringUtils.getAttributeStrNotNull(testata, "DATRIFERIMENTO");
Object annoGrad = testata.getAttribute("NUMANNO");
Object annoRifReddito = testata.getAttribute("NUMANNOREDDITO");
Object numKloGrad = testata.getAttribute("NUMKLOGRADUATORIA");
Object PRGGRADUATORIA = testata.getAttribute("PRGGRADUATORIA");
String ambTerr = StringUtils.getAttributeStrNotNull(testata, "PROVINCIA_ISCR");

%>

<html>
	<head>
		<title>Modifica Graduatoria Annuale</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	    	<af:linkScript path="../../js/" />	 
	    	<script type="text/Javascript">
	    	
		    	function tornaAllaLista() {  
		    	
					if (isInSubmit()) return;
					<%
					// Recupero l'eventuale URL generato dalla LISTA precedente
					String token = "_TOKEN_CMListaGradAnnualePage";
					String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());				
					%>
		      		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");

				}
	    	
	    	</script>
	</head>
	<body class="gestione" onload="rinfresca()">
		<p class="titolo">Modifica Graduatoria Annuale</p>
		<!-- messaggi di esito delle operazioni applicative -->
		<font color="red"><af:showErrors/></font>
		<font color="green"> 
		 <af:showMessages prefix="CMModificaNotaGradAnnModule"/>
		</font>
		<br>
   			<% out.print(htmlStreamTop); %>
   		<af:form method="POST" action="AdapterHTTP" name="Frm1" >
   		<input type="hidden" name="PAGE" value="CMModificaNotaGradAnnPage">
		<input type="hidden" name="MODULE" value="CMModificaNotaGradAnnModule">
		<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
		<input type="hidden" name="NUMKLOGRADUATORIA" value="<%=numKloGrad%>">
		<input type="hidden" name="PRGGRADUATORIA" value="<%=PRGGRADUATORIA%>">
		 	
   		<table class="main">

				<tr>
          			<td class="etichetta">Ambito Territoriale</td>
          			<td class="campo">
            			<input type="text" name="PROVINCIA_ISCR" value="<%=ambTerr%>" class="inputView" readonly="true" maxlength="4" size="50" title="Ambito Territoriale" />
          			</td>
        		</tr> 				<tr>
          			<td class="etichetta">Anno</td>
          			<td class="campo">
            			<input type="text" name="annoGrad" value="<%=annoGrad.toString()%>" class="inputView" readonly="true" maxlength="4" size="4" title="Anno Graduatoria" />
          			</td>
        		</tr> 
        		<tr>
          			<td class="etichetta">Data riferimento</td>
          			<td class="campo">
          				<input type="text" name="dataRifGrad" title="Data Riferimento" value="<%=dataRifGrad%>" class="inputView" readonly="true" size="12" maxlength="10" />            			
          			</td>
        		</tr> 
        		<tr>
		          	<td class="etichetta">Anno riferimento reddito</td>
		          	<td class="campo">
		            	<input type="text" name="annoRifReddito" class="inputView" readonly="true" value="<%=annoRifReddito.toString()%>" maxlength="4" size="4" title="Anno Rif. Reddito" />
		          	</td>
        		</tr> 
        		<tr>
          			<td class="etichetta">Tipo graduatoria</td>
          			<td class="campo">
            			<af:comboBox name="codMonoTipoGrad" classNameBase="input" disabled="true" title="Tipo Graduatoria"  >	  	
	   						<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %> ></option>            
	   						<option value="D" <% if ( "D".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.8</option>
	   						<option value="A" <% if ( "A".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.18</option>                					        		
						</af:comboBox>
          			</td>
        		</tr>  
        		<tr>
        			<td class="etichetta">Note</td>
        			<td class="campo">
        				<af:textArea cols="50"  
                     				rows="4" 
                    				name="strnote" 
                     				classNameBase="textarea" 
                     				maxlength="100"
                     				value="<%=strnota%>"/>
        			</td>
       			</tr>
       			<tr><td colspan="2">&nbsp;</td></tr>
       			<% if (btnAggiorna) { %>
       			<tr>
       				<td colspan="2" align="center">
       					<input class="pulsanti" type="submit" name="btnAggiorna" value="Aggiorna"/>
       				</td>
       			</tr>
       			<%} %>
				<tr align="center">
					<td align="center" colspan="2">
					<input type="button" class="pulsanti" value="Torna alla lista" onclick="tornaAllaLista()" ></td>
				</tr>	
        	</table>
        </af:form>
   		 <%out.print(htmlStreamBottom);%>
   		 
   		 <center>
  		<table>
  			<tr>
  				<td align="center">
					<%  operatoreInfo.showHTML(out);%>
  				</td>
  			</tr>
		
  		</table>
  	</center>
	</body>
</html>