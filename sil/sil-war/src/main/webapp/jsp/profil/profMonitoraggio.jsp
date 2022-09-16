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
                  it.eng.sil.util.Linguette,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
       
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 
  String strLogin =  (String) serviceResponse.getAttribute("PROFMONITORAGGIO.strlogin");
 
    // Gestione delle linguette
    int cdnFunzione = new Integer ( (String) serviceRequest.getAttribute("cdnfunzione")).intValue();
  
    String cdnStr =   (String) serviceRequest.getAttribute("cdut"); 
   
    BigDecimal cdut = new  BigDecimal(cdnStr);
    
    Linguette l = new Linguette(user, cdnFunzione , "ProfMonitoraggioPage", cdut);
   
    l.setCodiceItem("cdut");
    
   // Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, "ProfMonitoraggioPage");

    String readonlyStr = attributi.containsButton("AGGIORNA")? "false" : "true";
    boolean readonly = new Boolean(readonlyStr).booleanValue();

 	String htmlStreamTop = StyleUtils.roundTopTable(!readonly);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(!readonly);
   
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged(ctrl) {
         <% if (readonlyStr.equalsIgnoreCase("false")){ %> 
        
            flagChanged = true;

            var ck=document.forms[0].gruppi;
    		var msg="I profili: \n\n - Utenti Avanzati SpagoBI\n - Utenti SpagoBI \n\nnon possono essere scelti entrambi";
            
			if (ctrl.value=="/spagobi/advuser"){
						for (var counter = 0; counter < ck.length; counter++)
						{
								if (ck[counter].value=="/spagobi/user" && ck[counter].checked ){
										alert(msg);
										ctrl.checked=false;
								}
						}						
					
			
			}
			
			
			if (ctrl.value=="/spagobi/user"){
						for (var counter = 0; counter < ck.length; counter++)
						{
								if (ck[counter].value=="/spagobi/advuser" && ck[counter].checked ){
										alert(msg);
										ctrl.checked=false;
								}
						}						
					
			
			}
			
            
         <%}%> 
        }
     

</SCRIPT>		


</head>
<body class="gestione" onLoad="rinfresca()">
	
<%@ include file="./testataUtente.inc" %>

<% 
    l.show(out);        
%>
<af:showMessages prefix="PROFMONITORAGGIOSALVA"/>
<af:showErrors />	
	
<af:form method="POST" action="AdapterHTTP">
<af:textBox type="hidden" name="PAGE" value="ProfMonitoraggioSalvaPage"/>
<af:textBox type="hidden" name="cdut" value="<%=Utils.notNull( cdut )%>"/>
<af:textBox name="cdnfunzione" type="hidden" value="<%=String.valueOf(cdnFunzione)%>" />

<af:textBox name="strLogin" type="hidden" value="<%=strLogin%>" />
      
     
<%out.print(htmlStreamTop);%>
<table class="main" >


 <tr>
          <td class="etichetta">
          	
          </td>
          <td class="campo">
          	<b>Profili disponibili</b>
          </td>
        </tr>



<% Vector rows = serviceResponse.getAttributeAsVector("PROFMONITORAGGIO.rows.row");	

  String disabled =  readonlyStr.equalsIgnoreCase("true")? "DISABLED" : "";
  
  for (int i=0;i<rows.size(); i++) {
       SourceBean row = (SourceBean)rows.get(i);
       String codice = (String) row.getAttribute("CODICE");
       String descrizione = (String) row.getAttribute("descrizione");
       String checked = (String) row.getAttribute("CHECKED");

	  //	String tipo = 	codice.equalsIgnoreCase("/spagobi/advuser") || codice.equalsIgnoreCase("/spagobi/user") ? "radio" : "checkbox";  

	%>

       <tr>
          <td class="etichetta">
            <%=descrizione%>
          </td>
          <td class="campo">
          
	

	

	
          
          <INPUT type="checkbox" name="gruppi" value="<%=codice%>" <%=checked%> 
          				<%=disabled%>
			            onClick="fieldChanged(this)">
          

          </td>
        </tr>
    		
    	
  <%}%>  	
    	
    	
    	
    	

	<%	if (attributi.containsButton("AGGIORNA")){%>
	         
	         <tr>
				<td></td>
			</tr>
			<tr><td></td>
				<td nowrap  class="campo">
					<input  class="pulsante"  type="submit" name="SALVA" VALUE="Aggiorna" />
					<input  class="pulsante"  type="reset" VALUE="Annulla" />
				</td>
			</tr>
	<%}%>
	                      
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>

