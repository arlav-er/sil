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
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	Vector messaggi = serviceResponse.getAttributeAsVector("message.rows.row");
%>




<html>
<head>
	<title>Messaggi</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
	<af:linkScript path="../../js/"/>
	<script>
	function chiudiFinestra(){
      	if(document.Frm1.conferma.checked == true ){
      		return true;
      		}
      	else {
      		window.close();
      		return false;
      		}
      	return false;
       }
	</script>
	<style>
		
		td.avviso{
			font-family: Verdana, Arial, Helvetica, Sans-serif; 
			font-size: 18px;
			font-weight: bold;
			font-variant:inherit;
			font-weight:bolder;
			color : white;
			background-color: red;
		}
		
		.dispari{
		
		}
		td.dispari {
			border-bottom-width: 1px;
			border-bottom-style: solid; 
			color: #000066; 
			background-color: #cce4ff;
			font-family: Verdana, Arial, Helvetica, Sans-serif; 
			font-size: 12px;
			font-weight: normal;
			
			text-align: justify;

		}

		td.pari {
			border-bottom-width: 1px;
			border-bottom-style: solid; 
			color: #000066; 
			background-color: #e8f3ff;
			font-family: Verdana, Arial, Helvetica, Sans-serif; 
			font-size: 12px;
			font-weight: normal;
	
			text-align: justify;
		}
		td.oggettodispari {
			color: #000066; 
			background-color: #cce4ff;
			font-family: Verdana, Arial, Helvetica, Sans-serif; 
			font-size: 11px;
			font-weight: bold;
		}

		td.oggettopari {
			color: #000066; 
			background-color: #e8f3ff;
			font-family: Verdana, Arial, Helvetica, Sans-serif; 
			font-size: 11px;
			font-weight: bold;
		}
		
	</style>
	
	

</head>
<body>
	
	<af:form action="../../servlet/fv/AdapterHTTP" onSubmit="chiudiFinestra()" method="POST" dontValidate="true" name="Frm1">	
		<table class="main" width="100%" height="95%">
			<tr>
				<td align="center" valign="middle">
					<table class="main">
						<tr>
							<td>&nbsp;</td>
							<td class="avviso">Avviso
							</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
			
					<% if (messaggi.size()!=0) { %>
						<input type="hidden" name="numeroMessaggi" value="<%=messaggi.size()%>" />
						<input type="hidden" name="utente" value="<%=user.getCodut()%>" />
						<input type="hidden" name="PAGE" value="MessaggiVistiPage" />
			
						
						<% 
							String stileRiga="pari";	
							for(int i=0;i<messaggi.size();i++){
					 		
					 		// Prelievo dati per ogni alert (riga)
					 		SourceBean row= (SourceBean) messaggi.get(i); 
							String strMittente = row.getAttribute("cognome")+ 
												" " + row.getAttribute("nome")+ " " + 
												"(" +row.getAttribute("login")+")";
			
							String strOggetto = (String) row.getAttribute("oggetto");
							if (row.getAttribute("oggetto")==null)	{
								strOggetto="";
							}
			
							String strCorpo = (String) row.getAttribute("corpo");
							String alert = StringUtils.replace(strCorpo,"\n","<br>");
					 		if (row.getAttribute("corpo")==null)	{
								strCorpo="";
							}
					 		
					 		int priorita = ( (BigDecimal)row.getAttribute("priorita") ).intValue();
					 		
					 		int codMessaggio = ( (BigDecimal)row.getAttribute("codmessaggio") ).intValue();
					 		
					 		
					 		// Parte di presentazione
					 		
					 		%>
			
							
					 		<tr>
					 			<td>&nbsp;</td>
					 			<th>Inviato da <%=strMittente%></th>
					 			<td>&nbsp;</td>
					 		</tr>
				
					 		<tr>
					 			<td>&nbsp;</td>
					 			<td class="oggetto<%=stileRiga%>">
									<%=strOggetto %>
					
					 			</td>
					 			<td>&nbsp;</td>
					 		</tr>
					 		
						 	<tr>
						 		<td align="center" valign="middle">
						 			<img border="0" src="../../img/stop<%=priorita%>.gif" width="35" height="44">
						 		<td class="<%=stileRiga%>">
						 				
										<%=alert %>
									
								</td>
								<td>&nbsp;</td>
						 	</tr>
					 		<tr>
					 			<td colspan="3">&nbsp;</td>
					 		</tr>
					 		<input type="hidden" name="codmessaggio<%=i%>" value="<%=codMessaggio%>" />
					 	
							<% 
								if (i%2==0){
										stileRiga="dispari";	
										}
								else{
										stileRiga="pari";
									}
							} // for %>
											
					<% } %>
							
							<tr>
					 			<td>&nbsp;</td>
					 			<td colspan="2">
									<input type="checkbox" class="input" name="conferma" value="conferma">
					 				Non visualizzare più questi messaggi<br/>&nbsp;
								</td>
							</tr>
							
					 		<tr>
					 			<td>&nbsp;</td>
					 			<td colspan="2">
								
									<input type="submit" class="pulsante" value="Chiudi"/>
					
								</td>
							</tr>
							
					</table>
				</td>
			</tr>
		</table>
	</af:form>
	
</body>
</html>
