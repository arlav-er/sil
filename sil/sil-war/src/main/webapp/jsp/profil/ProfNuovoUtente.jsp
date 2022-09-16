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
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	boolean isNuovo=true; 
    boolean readonly = false;
    
    String htmlStreamTop = StyleUtils.roundTopTable(!readonly);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(!readonly);
 
    
    // GESTIONE ATTRIBUTI....
    String _page = (String) serviceRequest.getAttribute("PAGE");
    PageAttribs attributi = new PageAttribs(user, _page);
    String readonlyStr = attributi.containsButton("salva")? "false" : "true";
 
    int cdnFunzione = new Integer ( (String) serviceRequest.getAttribute("cdnfunzione")).intValue();
 
    BigDecimal cdut = null;
    String strlogin= (String) serviceRequest.getAttribute("strlogin");
    String strpassword= null;
    String strtelefono= (String) serviceRequest.getAttribute("telefono");
    String strfax= (String) serviceRequest.getAttribute("fax");
    String stremail= (String) serviceRequest.getAttribute("mail");
    String strluogorif= (String) serviceRequest.getAttribute("luogorif");
    String flgabilitato= (String) serviceRequest.getAttribute("flgAbilitato");
    String flgUtConvenzione= (String) serviceRequest.getAttribute("flgUtConvenzione");
    String strnota= (String) serviceRequest.getAttribute("note");
    String datinizioval= (String) serviceRequest.getAttribute("datinizioval");
    String datfineval= (String) serviceRequest.getAttribute("datfineval");
    String flglogged= (String) serviceRequest.getAttribute("flglogged"); 
    BigDecimal numtentativi= (BigDecimal) serviceRequest.getAttribute("numtentativi");
    
    String cdnlavoratore = (String) serviceRequest.getAttribute("cdnLavoratore"); 
    	if (cdnlavoratore == null) cdnlavoratore="";
	String strCodiceFiscale = (String) serviceRequest.getAttribute("strCodiceFiscale");
		if (strCodiceFiscale == null) strCodiceFiscale="";
	String strCognome = (String) serviceRequest.getAttribute("strCognome");
		if (strCognome == null) strCognome="";
	String strNome = (String) serviceRequest.getAttribute("strNome");
		if (strNome == null) strNome="";
	String datNasc = (String) serviceRequest.getAttribute("datNasc");
		if (datNasc == null) datNasc="";
	String strSesso = (String) serviceRequest.getAttribute("strSesso");
		if (strSesso == null) strSesso="";
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="JavaScript">

        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (readonlyStr.equalsIgnoreCase("false")){ %> 
            flagChanged = true;
         <%}%> 
        }
        
        function valida() {

			var newPwd = document.forms[0].password.value;
			var newPwd2 = document.forms[0].password2.value;
		 	
			if (newPwd != newPwd2) {
				alert ("La password di conferma è diversa da quella inserita come nuova password");
				return false;
			}

        	return true;
		}
	
        function checkCF (inputName) {
        
			var cfObj = eval("document.forms[0]." + inputName);
			cfObj.value=cfObj.value.toUpperCase();
			cf=cfObj.value;
			ok=true;
			msg="";
				if (cf.length==16) {
			    	for (i=0; i<16 && ok; i++)	{
			        	c=cf.charAt(i);
			            if (i>=0 && i<=5){
			                    ok=!isDigit(c);
			                    msg="Errore nei primi sei caratteri del codice fiscale";
			            } else if  (i==6 || i==7) { 
			                    ok=isDigit(c);
			                    msg="Errore nel settimo o nell'ottavo carattere del codice fiscale";
			            } else if (i==8) {
			                    ok=!isDigit(c);
			                    msg="Errore nel nono carattere del codice fiscale";
			            } else if (i==9 || i==10) {
			                    ok=isDigit(c);
			                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
			            } else if (i==11) {
			                    ok=!isDigit(c);
			                    msg="Errore nell'undicesimo carattere del codice fiscale";
			            } else if (i>=12 && i <=14) {
			                    ok=isDigit(c);
			                    msg="Errore nel tredicesimo, nel quattordicesimo o nel penultimo carattere del codice fiscale";
			            } else if (i==15) {
			                    ok=!isDigit(c);
			                    msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
			            }
			        }
			    } else if (cf.length<16 && cf.length>0){
			      ok=false;
			      msg="Il codice fiscale deve essere di 16 caratteri";
			    }
			    if (!ok) {
			        alert(msg);
			        cfObj.focus();
			    }
			  return ok;
			}
			
			function controllaDate() {
	
				ok=true;
				var dataInizio = document.forms[0].datinizioval.value;
				var dataFine =   document.forms[0].datfineval.value;
  				if (compareDate(dataInizio, dataFine)>0) {
					alert("Data Inizio validità maggiore della data Fine validità ");
					ok=false;
				}
				return ok;
   			}
        
    </script>
	</head>
	<body class="gestione" onload="rinfresca()">
    <br/>
      <p class="titolo">Nuovo utente</p>
          <font color="green"><af:showMessages prefix="ProfSalvaUtente"/></font>
	    <font color="red"><af:showErrors /></font>
	
	  <af:form method="POST" action="AdapterHTTP" onSubmit="valida()&& controllaDate()">
      <af:textBox type="hidden" name="PAGE" value="ProfDettaglioUtentePage"/>
      <af:textBox type="hidden" name="cdut" value="<%=Utils.notNull( cdut )%>"/>
      <af:textBox name="cdnfunzione" type="hidden" value="<%=String.valueOf(cdnFunzione)%>" />
      <input type="hidden" name="cdnlavoratore" value="<%=cdnlavoratore%>"/>
      <p align="center">
      
      <%out.print(htmlStreamTop);%>
      <table class="main">
        
	<% if(cdnlavoratore.equals("")) { %>        
    	
    	<%@ include file="./dettaglioUtente.inc" %>
    	
    <%} else {%>
    
    	<%@ include file="./dettaglioAccount.inc" %>
    	
   <% } if (attributi.containsButton("salva")){%>
	
			<tr>
				<td></td>
			</tr>
			<tr><td></td>
				<td nowrap  class="campo">
					<input  class="pulsante"  type="submit" name="SALVA" VALUE="Inserisci" />
					<input  class="pulsante"  type="reset" VALUE="Annulla" />
				</td>
			</tr>
			<%}%>
			
    	
</table>
<%out.print(htmlStreamBottom);%>
      
</af:form>
</body>
</html>

