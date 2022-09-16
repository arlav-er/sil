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
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.security.handlers.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
	String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
	List props = (List) serviceResponse.getAttribute("PROFWSSECURITY.props");
	int j = 100;
	
	// GESTIONE ATTRIBUTI....
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user, _page);
	
	String readonlyStr = attributi.containsButton("salva")? "false" : "true";
    boolean readonly = new Boolean(readonlyStr).booleanValue();
	
	
	
%>



<html>
<head>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

</head>
<body class="gestione" onload="rinfresca()">
<br>

<p class="titolo">Configurazione sicurezza web services</p>

<af:showMessages prefix="ProfWsSecuritySalva" />
<af:showErrors />


<af:form method="POST" action="AdapterHTTP">
	<% if(!readonly){ %>
		<af:textBox type="hidden" name="PAGE" value="ProfWsSecuritySalvaPage" />
	<%}%>

	<table class="main">

		<%
			Iterator iter = props.iterator();
			while (iter.hasNext()) {
						String[] prop = (String[]) iter.next();
						String propName = prop[0];
						String propValue = prop[1];

						j++;
						String orderedPropName = "P" + j + propName;
					
						if (propName.endsWith("Descrizione")) {%>
									<tr>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
						
									<tr>
										<td align="right">
											<b><%=propValue%></b>
										
											<af:textBox type="hidden" 
														name="<%=orderedPropName%>" 
														value="<%=propValue%>" />
										</td>
									</tr>
									

						 <%}else{
					
					
								String etichetta = "";
								String disabled = "true";
		
		
								
								if (propName.endsWith(".ds.username")) {%>
												<tr>
													<td class="etichetta">&nbsp;</td>
													<td align="left"><i> Web service con dati sensibili </i></td>
												</tr>
								<%}
								
								if (propName.equals("mittente")) {
									propValue = Utility.decrypt(propValue);
								}
							
								if (propName.endsWith("username")) {
									etichetta = "username";
								} else if (propName.endsWith("password")) {
									etichetta = "password";
								} else if (propName.endsWith("validita.da")) {
									etichetta = "valido da";
									disabled = "false";
								} else if (propName.endsWith("validita.a")) {
									etichetta = "a";
									disabled = "false";
								} else if (propName.endsWith("log.ws_out")) {
									etichetta = "File di log in uscita";
								} else if (propName.endsWith("log.ws_in")) {
									etichetta = "File di log in entrata";
								} else if (propName.endsWith("log.enabled")) {
									etichetta = "Log abilitato";
								} else {
									etichetta = propName;
								}
								
							
								if(readonly)
									disabled="true";
		
								%>
		
								<tr>
									<td class="etichetta"><%=etichetta%></td>
									<td class="campo"><af:textBox type="text"
										name="<%=orderedPropName%>" value="<%=propValue%>"
										disabled="<%=disabled%>" size="50" /></td>
								</tr>
					
		

					<% } //end else
			} // while
			%>
			

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>


	<%if (attributi.containsButton("salva")){%>			

			<tr>
				<td>&nbsp;</td>
				<td nowrap class="campo"><input class="pulsante" type="submit"
					value="Aggiorna" /> &nbsp;&nbsp; <input class="pulsante" type="reset"
					value="Annulla" /></td>
			</tr>

	<%}%>

	</table>

</af:form>

</body>
</html>





