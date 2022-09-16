<%@page import="it.eng.afExt.utils.StringUtils"%>
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
  	SourceBean row = (SourceBean) serviceResponse.getAttribute("ProfDettaglioUtente.rows.row");
   
   	boolean isNuovo=false; 
 
    // Gestione delle linguette
    int cdnFunzione = new Integer ( (String) serviceRequest.getAttribute("cdnfunzione")).intValue();
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    String cdnStr =   (String) serviceRequest.getAttribute("cdut"); 
    
    BigDecimal cdut = null;
    if ((cdnStr!=null) && cdnStr.equals("") ){
      cdut = (BigDecimal) row.getAttribute("cdut");             
    }else{
      cdut = new  BigDecimal(cdnStr);
    }
    
    Linguette l = new Linguette(user, cdnFunzione , _page, cdut);
   
    // Utilizzato solo da Franco  
    l.setCodiceItem("cdut");
    
   // Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, _page);

    String readonlyStr = attributi.containsButton("salva")? "false" : "true";
    boolean readonly = new Boolean(readonlyStr).booleanValue();

 	String htmlStreamTop = StyleUtils.roundTopTable(!readonly);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(!readonly);
 
 
 	// Inizio della pagina vera e propria
   	String cdnlavoratore = (BigDecimal) row.getAttribute("CDNLAVORATORE") != null ? ((BigDecimal) row.getAttribute("CDNLAVORATORE")).toString():"";
   	
   	String strlogin= (String) row.getAttribute("strlogin");             
   	String strCodiceFiscale=(String) row.getAttribute("strCodiceFiscale");
    String strCognome= (String) row.getAttribute("strcognome");   
    String strNome= (String) row.getAttribute("strnome");      
	String datNasc= (String) row.getAttribute("datNasc");
    String strSesso= row.containsAttribute("strSesso")?(String) row.getAttribute("strSesso"):"";

	String codOp=row.containsAttribute("prgSpi")?(String) row.getAttribute("prgSpi").toString():null;
	String nomeOp=(String) row.getAttribute("nomeOp");
	if (codOp==null) nomeOp="Nessun operatore associato";
	String codFisOp=(String) row.getAttribute("codFisOp");
	String dataNasOp=(String) row.getAttribute("datNascOp");
	String scaduto=(String) row.getAttribute("scaduto");

    String strtelefono= (String) row.getAttribute("strtelefono");          
    String strfax= (String) row.getAttribute("strfax");               
    String stremail= (String) row.getAttribute("stremail");             
    String strluogorif= (String) row.getAttribute("strluogorif");          
    String flgabilitato= (String) row.getAttribute("flgabilitato");     
    String flgUtConvenzione= (String) row.getAttribute("flgutconvenzione");
    String strnota= (String) row.getAttribute("strnota");               
    String datinizioval= (String) row.getAttribute("datinizioval");         
    String datfineval= (String) row.getAttribute("datfineval");    
    String flglogged= (String) row.getAttribute("flglogged");     
    BigDecimal numtentativi= (BigDecimal) row.getAttribute("numtentativi");
    
    boolean isFlgConvenzione = false;
    
    if (StringUtils.isFilledNoBlank(flgUtConvenzione) && flgUtConvenzione.equalsIgnoreCase("S")) {
    	isFlgConvenzione = true;
    }
   
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
		        for (i=0; i<16 && ok; i++)
		        {
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
		    } else if (cf.length<16 && cf.length>0) {
		      ok=false;
		      msg="Il codice fiscale deve essere di 16 caratteri";
		    }
		    if (!ok) {
		        alert(msg);
		        cfObj.focus();
		    }
		  return ok;
		}
		
		
		function openAssociaOperatoreWindow() {
		
			cognome=document.Frm1.strCognome.value;
			nome=document.Frm1.strNome.value;
			codFis=document.Frm1.strCodiceFiscale.value;
			dataNas=document.Frm1.datNasc.value;
			strSesso=document.Frm1.strSesso.value;
		
			var s= "AdapterHTTP?PAGE=AssociaUtenteOperatorePage";
			s+="&nome="+nome;
			s+="&cognome="+cognome;
			s+="&codFis="+codFis
			s+="&dataNas="+dataNas;
			s+="&sesso="+strSesso;

	        window.open(s,"", 'toolbar=0, scrollbars=0, width=700, height=500');		
		}

		function aggiornaOperatore (prgSpi, nome, cognome, cf, datanas)
		{
		
			document.Frm1.codOP.value=prgSpi;
			document.Frm1.nomeCognomeOP.value=nome+" " + cognome;
			document.Frm1.codFisOP.value=cf;
			document.Frm1.datNasOP.value=datanas;
		    fieldChanged();

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

</SCRIPT>		


</head>
<body class="gestione" onLoad="rinfresca()">
	
<%@ include file="./testataUtente.inc" %>

<% 
    l.show(out);        
%>
<af:showMessages prefix="ProfSalvaUtente"/>
<af:showErrors />	
	
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="valida()&& controllaDate()">
<af:textBox type="hidden" name="PAGE" value="ProfDettaglioUtentePage"/>
<af:textBox type="hidden" name="cdut" value="<%=Utils.notNull( cdut )%>"/>
<af:textBox name="cdnfunzione" type="hidden" value="<%=String.valueOf(cdnFunzione)%>" />
<input type="hidden" name="cdnlavoratore" value="<%=cdnlavoratore%>"/>      
      
     
<%out.print(htmlStreamTop);%>
<table class="main" >

         
      <% if(cdnlavoratore.equals("")) { %>        
    		
    		<%@ include file="./dettaglioUtente.inc" %>
    	
    			<tr>
    				<td><BR/><BR/></td>
			  	</tr>
               	 <tr>
	                 <td class="etichetta">Operatore Associato:</td>
                     <td nowrap  class="campo"> 
                     	<TABLE border=0>
                      		<TR>
                      			<TD><b>                      					
                      						<af:textBox classNameBase="input" name="nomeCognomeOP" type="text" readonly="true" size="40" value="<%=nomeOp%>"/>
	                      				<BR/>
    	                  					<af:textBox classNameBase="input" name="codFisOP" type="text" readonly="true" size="40" value="<%=codFisOp%>"/>
	    	                  			<BR/>
    	                  					<af:textBox classNameBase="input" name="datNasOP" type="text" readonly="true" size="40" value="<%=dataNasOp%>"/>
    	                  			    <BR/>
    	                  					<af:textBox classNameBase="input" name="scaduto" type="text" readonly="true" size="40" value="<%=scaduto%>"/>
                      				</b>
                      					   <af:textBox name="codOP" type="hidden" size="5" value="<%=codOp%>"/>
                      		     </TD>
								 <TD>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								
								<%if (attributi.containsButton("salva")){%>
								    <input class="pulsante" type="button" name="ASSOCIAUT_OP"  value="Assegna/Cambia Operatore" onclick="javascript:openAssociaOperatoreWindow();"/>
					            <%}%>
					            </TD>
					          </TR>
					     </TABLE>
                    </td>                                                     
                  </tr>


 	<% } else {%>
    	
    		<%@ include file="./dettaglioAccount.inc" %>

	<%} if (attributi.containsButton("salva")){%>
	         
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
	
	<% if (isFlgConvenzione){%>
	         
	         <tr>
				<td></td>
			</tr>
			<tr><td></td>
				<td nowrap  class="campo">
					<input  class="pulsante"  type="submit" name="SBLOCCA" VALUE="Sblocca utente convenzionato" />
				</td>
			</tr>
	<%}%>
	                      
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>

