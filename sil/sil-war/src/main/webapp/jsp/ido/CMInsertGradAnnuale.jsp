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
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

// Calcolo la data di riferimento della graduatoria annuale al 31/12/annoprec
Calendar cal = new GregorianCalendar();
Integer annoNow = new Integer(cal.get(Calendar.YEAR));
Integer anno2 = new Integer(cal.get(Calendar.YEAR)-2);
Integer anno = new Integer(cal.get(Calendar.YEAR)-1);
String dataRifGrad = "31/12/"+anno.toString();

String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad");
String annoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"annoGrad");
String annoRifReddito = StringUtils.getAttributeStrNotNull(serviceRequest,"annoRifReddito");
String strnote = StringUtils.getAttributeStrNotNull(serviceRequest,"strnote");
String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");


//Carico valori di default solo se vuoti
if (annoGrad.compareTo("") == 0)
	annoGrad = annoNow.toString();
if (annoRifReddito.compareTo("") == 0)
	annoRifReddito = anno2.toString();



%>
<html>
	<head>
		<title>Inserimento Graduatoria Annuale</title>
			<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	    	<af:linkScript path="../../js/" />	 
	    	
	    	 <script type="text/Javascript">
			  
			  function tornaAllaRicerca()
			  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
			     if (isInSubmit()) return;
			  	 
			     url="AdapterHTTP?PAGE=CMRicercaGradAnnualePage";
			
			     setWindowLocation(url);
			  }
			  	
			 </script>   		    	
	</head>
	<body class="gestione" onload="rinfresca()">
		<!-- messaggi di esito delle operazioni applicative -->
		<font color="red"><af:showErrors/></font>
		<p class="titolo">Inserimento graduatoria annuale</p>
   			<%out.print(htmlStreamTop);%>
		<af:form method="POST" action="AdapterHTTP" name="Frm1" >
   			<input type="hidden" name="PAGE" value="CMTmpGradAnnualePage">
		 	<input type="hidden" name="MODULE" value="M_TmpGradAnnualeModule">
		 	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">   		    		    	
   		 	<table class="main">
   		 		<tr>
          			<td class="etichetta">Anno</td>
          			<td class="campo">
            			<af:textBox type="text" name="annoGrad" value="<%=annoGrad%>" maxlength="4" size="4" title="Anno Graduatoria" required="true"/>
          			</td>
        		</tr> 
   		 	
				<tr>
				    <td class="etichetta">Ambito Territoriale</td>
				    <td colspan=3 class="campo">
				    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
				        	classNameBase="input" addBlank="true" title="Ambito Territoriale" required="true" />
				    </td>
				</tr>


        		<tr>
          			<td class="etichetta">Data riferimento</td>
          			<td class="campo">
          				<af:textBox type="date" name="dataRifGrad" title="Data Riferimento" value="<%=dataRifGrad%>" size="12" maxlength="10" validateOnPost="true"/>            			
          			</td>
        		</tr> 
        		<tr>
		          	<td class="etichetta">Anno riferimento reddito</td>
		          	<td class="campo">
		            	<af:textBox type="text" name="annoRifReddito" value="<%=annoRifReddito%>" maxlength="4" size="4" title="Anno Rif. Reddito" required="true"/>
		          	</td>
        		</tr> 
        		<tr>
          			<td class="etichetta">Tipo graduatoria</td>
          			<td class="campo">
            			<af:comboBox name="codMonoTipoGrad" classNameBase="input" disabled="false" title="Tipo Graduatoria" required="true" >	  	
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
                     				value="<%=strnote%>"/>
        			</td>
       			</tr>
        		<tr><td colspan="2">&nbsp;</td></tr>
        		<tr>
          			<td colspan="2" align="center">
          				<input class="pulsanti" type="submit" name="inserisci" value="Inserisci"/>
          				&nbsp;&nbsp;
         			 	<input type="reset" class="pulsanti" value="Annulla" />
          			</td>
       			</tr>
       			<tr>			
					<td align="center" colspan="2">					
						<input type="button" class="pulsanti" value="Torna alla ricerca" onclick="tornaAllaRicerca()" >
					</td>			
				</tr>
        	</table>
        </af:form>
   		 <%out.print(htmlStreamBottom);%>
	</body>
</html>
