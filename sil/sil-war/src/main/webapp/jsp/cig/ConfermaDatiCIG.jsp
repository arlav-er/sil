<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  it.eng.sil.security.PageAttribs " %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 

<%      
    //SourceBean row = null;    
    
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    
	PageAttribs attributi = new PageAttribs(user, _page);
    
	String datInizioOld = "";
    String datInizioCig = "";
    String datFineOld = "";
    String datFineCig = "";
    String codstatoatto = "";
    String prgAltraIscr = "";
    String codtipoiscrOld = "";
    String codtipoiscrCig = "";
    String prgAzienda = "";
    String prgUnita = "";
    String datCompetenza = "";
    String codMotChiusuraIscr = "";
    String datChiusuraIscr = "";
    String prgAccordoCig = "";
    String codStato = "";
    String codMonoTipoCompetenza = "";
    String strNote = "";
	String ragioneSociale = "";
	String indirizzo = "";
	String comune = "";
	String tipoIscr = "";
	String tipoIscrCig = "";
	String motChiusura = "";
	String competenza = "";
	String codAccordoOld = "";
	String codAccordoCig = "";
	String strCodFiscale = "";
	String codComCompetenza = "";
	String moduleName = "";
	
    String statoCigCompatibile = "";
    Vector resultCigCompatibili = null;
    String titoloPagina = "";
    
    String htmlStreamTop    = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
    if (serviceRequest.containsAttribute("inserisci")) {
    	moduleName = "M_InsertDomandaCig";	
    }
    else {
    	moduleName = "M_UpdateIscrCIG";
    	prgAltraIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAltraIscr");
    }
    
    if (serviceResponse.containsAttribute(moduleName + ".CIGCOMPATIBILE")) {
    	statoCigCompatibile = serviceResponse.getAttribute(moduleName + ".CIGCOMPATIBILE").toString();
    	resultCigCompatibili = serviceResponse.getAttributeAsVector(moduleName + ".ROWS.ROW");
    	
    	if (statoCigCompatibile.equalsIgnoreCase("VA_CONCESSA")) {
    		titoloPagina = "Conferma dati della domanda";
    		datInizioCig = (String)serviceResponse.getAttribute("M_DataInizioCig.ROWS.ROW.datiniziocigs");
    	    datFineCig = (String)serviceResponse.getAttribute("M_DataFineCig.ROWS.ROW.datfinecigs");
    	}
    	else {
    		titoloPagina = "ATTENZIONE: esistono una o più domande annullate o non concesse " +
    		 " <br> con periodi compatibili alla nuova iscrizione che si sta tentando di inserire.";
    	}
    	//lettura info inserite
    	codComCompetenza = (String)serviceResponse.getAttribute("M_CodComDomLav.ROWS.ROW.codComCompetenza");
        codMonoTipoCompetenza=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoCompetenza");
        codMotChiusuraIscr=StringUtils.getAttributeStrNotNull(serviceRequest,"codMotChiusuraIscr");
        codStato=StringUtils.getAttributeStrNotNull(serviceRequest,"codStato"); 
        codtipoiscrOld=StringUtils.getAttributeStrNotNull(serviceRequest,"codtipoiscr");
        comune=StringUtils.getAttributeStrNotNull(serviceRequest,"comune"); 
        datChiusuraIscr=StringUtils.getAttributeStrNotNull(serviceRequest,"datChiusuraIscr");
        datCompetenza=StringUtils.getAttributeStrNotNull(serviceRequest,"datCompetenza");
        datFineOld=StringUtils.getAttributeStrNotNull(serviceRequest,"datFine"); 
        datInizioOld=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizio"); 
        indirizzo=StringUtils.getAttributeStrNotNull(serviceRequest,"indirizzo"); 
        prgAzienda=StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda"); 
        prgUnita=StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");
        ragioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale"); 
        strCodFiscale=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodFiscale");
        strNote=StringUtils.getAttributeStrNotNull(serviceRequest,"strNote");
    }
%>

<html>
<head>
  <title>Conferma dati della domanda</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css"/> 
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <af:linkScript path="../../js/"/>
  
  
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  
  function tornaDettaglio() {
  	if (isInSubmit()) return;
   	var s= "AdapterHTTP?"
       s += "PAGE=CigLavListaPage&";
       s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
       s += "CDNFUNZIONE=<%= cdnFunzione %>";
   	setWindowLocation(s);
  }
  
  function associaDomandaCig() {
  	if (isInSubmit()) return;
  	//Controllo che sia stato selezionata una domanda
    var numCigSel=0;
    var indiceSel = 0;
    var codiceCigSel;
    var lenChecks = document.Frm1.CigCompatibile.length;
    if (lenChecks!=undefined && lenChecks > 1) {
	     for(var i = 0; i < lenChecks; i++) {
	      if(document.Frm1.CigCompatibile[i].checked) {
	      	numCigSel++;
	      	indiceSel=i;
	      	codiceCigSel=document.Frm1.CigCompatibile[i].value;
	      }
	    }
	}
	else {
		if (document.Frm1.CigCompatibile.checked) {
			numCigSel++;
	      	indiceSel=0;
	      	codiceCigSel=document.Frm1.CigCompatibile.value;
		}
	}
    if (numCigSel==0){ 
    	alert ("Selezionare almeno una domanda");
    	return;
    }
    else {
      if (numCigSel>1){
        alert ("Selezionare un'unica domanda");
    	return;
      }
    }
    document.Frm1.prgAccordo.value = codiceCigSel;
    document.Frm1.datInizio.value = '<%= datInizioCig%>';
    document.Frm1.datFine.value = '<%= datFineCig%>';
    var campo = eval("document.Frm1.codTipoIscr_" + indiceSel);
    document.Frm1.codtipoiscr.value = campo.value;
    doFormSubmit(document.Frm1);
  }

</SCRIPT>
    
<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="Javascript">
<%	
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore+"&prgAltraIscr="+prgAltraIscr);
%>
</script>

</head>

<body class="gestione" onload="rinfresca();">
	
  <%  
   		InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
        Linguette _linguetta = new Linguette(user, 1 , "CigLavListaPage", new BigDecimal(cdnLavoratore)); 
        _linguetta.show(out);
    
  %>
   <p class="titolo"><%=titoloPagina %></p>
   <p>
  <font color="green"><af:showMessages prefix="M_InsertDomandaCig" /></font>
  <font color="green"><af:showMessages prefix="M_UpdateIscrCIG" /></font>
  <font color="red"><af:showErrors/></font>
  </p>    
  <af:form method="POST" action="AdapterHTTP" name="Frm1">
  	<%= htmlStreamTop %>
  	
	<%
	if (statoCigCompatibile.equalsIgnoreCase("VA_NON_CONCESSA") || statoCigCompatibile.equalsIgnoreCase("AN")) {
	%>
		<table class="main">
		
		<tr>
          <td colspan="4">
            <div class="sezione2">
            Dati Inseriti
          </td>
        </tr>
		
    	<tr>
       		<td>Data Inizio</td>
    		<td>Data Fine</td>
    		<td>Ragione Sociale</td>
    		<td>Tipo Iscrizione</td>
    	</tr>
    	<tr>
    		<td>
        		<af:textBox title="Data inizio iscrizione" classNameBase="input" 
                			readonly="true"  type="text" name="datInizioOld" value="<%=datInizioOld%>" 
                    		size="12"/>
        	</td>
        	<td>
        		<af:textBox title="Data fine" classNameBase="input" 
                			readonly="true" type="text" name="datFineOld" value="<%=datFineOld%>" 
                    		size="12"/>
        	</td>
        	<td>
            	<af:textBox title="Ragione sociale" classNameBase="input" 
                			readonly="true" type="text" name="ragSociale" value="<%=ragioneSociale%>" 
                    		size="12"/>
            	
            </td>
        	<td>
            	<af:comboBox disabled="true"  name="codtipoiscrOld" classNameBase="input" 
                    		 moduleName="CI_TIPO_ISCR" selectedValue="<%= codtipoiscrOld %>"
                        	 addBlank="true" title="Tipo Iscrizione"/>
            	
            </td>
    	</tr>
    	
    	<tr>
          <td colspan="4">&nbsp;
          </td>
        </tr> 
	    </table>
	    
	    <table class="main">
	    	<tr>
          	<td colspan="6">
            <div class="sezione2">
            Domande compatibili
          	</td>
        	</tr> 
	    
	    	<tr>
	       		<td>Data Inizio</td>
	    		<td>Data Fine</td>
	    		<td>Ragione Sociale</td>
	    		<td>Tipo Iscrizione</td>
	    		<td>Codice Domanda</td>
	    		<td>Stato</td>
	    	</tr>
	    	<% for (int j=0;j<resultCigCompatibili.size();j++) {
	    		SourceBean cigCurr = (SourceBean)resultCigCompatibili.get(j);
	    		String nameInizioCig = "datInizioCig_" + j;
	    		String nameFineCig = "datFineCig_" + j;
	    		String nameRagSocCig = "ragioneSocialeCig_" + j;
	    		String nameTipoIscr = "codTipoIscr_" + j;
	    		String nameDescrTipoIscr = "descrCodTipoIscr_" + j;
	    		String nameCodiceCig = "codiceCig_" + j;
	    		String nameStatoCig = "statoCig_" + j;
    	    	codtipoiscrCig = (String)cigCurr.getAttribute("codtipoiscrCig");
    	    	String descrCodTipoIscr = (String)cigCurr.getAttribute("strdescrizioneCig");
    	    	codAccordoCig = (String)cigCurr.getAttribute("codAccordoCig");
    	   	 	prgAccordoCig = (String)cigCurr.getAttribute("prgAccordoCig");
    	   	 	datInizioCig = (String)cigCurr.getAttribute("datiniziocigs");
     	    	datFineCig = (String)cigCurr.getAttribute("datfinecigs");
     	    	codstatoatto = (String)cigCurr.getAttribute("codstatoatto");
     	    	%>
     	    	<tr>
	    		<td>
	    		<af:textBox title="Data inizio iscrizione" classNameBase="input" 
	                			readonly="true"  type="text" name="<%= nameInizioCig%>" value="<%=datInizioCig%>" 
	                    		size="12"/>
	    		</td>
	        	<td>
	        	<af:textBox title="Data fine iscrizione" classNameBase="input" 
	                			readonly="true"  type="text" name="<%= nameFineCig%>" value="<%=datFineCig%>" 
	                    		size="12"/>
	        	</td>
	        	<td>
	        	<af:textBox title="Ragione sociale" classNameBase="input" 
	                			readonly="true"  type="text" name="<%= nameRagSocCig%>" value="<%=ragioneSociale%>" 
	                    		size="12"/>
	                    		
	            </td>
	        	<td>
	        	<af:textBox title="Tipo iscrizione" classNameBase="input" 
	                			readonly="true"  type="text" name="<%= nameDescrTipoIscr%>" value="<%=descrCodTipoIscr%>" 
	                    		size="12"/>
	        	
	        	<input type="hidden" name="<%= nameTipoIscr%>" value="<%=codtipoiscrCig%>">
	            </td>
	        	<td>
	        	<af:textBox title="Codice domanda" classNameBase="input" 
	                			readonly="true"  type="text" name="<%= nameCodiceCig%>" value="<%=codAccordoCig%>" 
	                    		size="12"/>
	            </td>
	        	<td>
	        	<af:textBox title="stato" classNameBase="input" 
	                			readonly="true"  type="text" name="<%=  nameStatoCig%>" value="<%=codstatoatto%>" 
	                    		size="12"/>
	            </td>
	    		</tr>
	    	<% } %>
	    
	    <tr><td colspan="6">&nbsp;
    	</td></tr>
	    </table>
	    
		<center>
    	<table> 
	    	<tr>
	          	<td align="center">
	            <input type="submit" class="pulsanti" name="Salva" value="Procedi con l'inserimento">
	            </td>
	            <td align="center">
	            <input type="button" class="pulsanti" name="annulla" value="Annulla" onClick="tornaDettaglio()">
	            </td>
	    	</tr>
	    </table>
	    </center>
    <% } else {
    	%>
    	<table class="main">
    	<tr>
          <td colspan="4">
            <div class="sezione2">
            Dati Inseriti
          </td>
        </tr>
        
    	<tr>
       		<td>Data Inizio</td>
    		<td>Data Fine</td>
    		<td>Tipo Iscrizione</td>
    		<td>Codice comunicazione</td>
    		
    	</tr>
    	<tr>
    		<td>
        		<af:textBox title="Data inizio iscrizione" classNameBase="input" 
                			readonly="true"  type="text" name="datInizioOld" value="<%=datInizioOld%>" 
                    		size="12"/>
        	</td>
        	<td>
        		<af:textBox title="Data fine" classNameBase="input" 
                			readonly="true" type="text" name="datFineOld" value="<%=datFineOld%>" 
                    		size="12"/>
        	</td>
        	<td>
            	<af:comboBox disabled="true"  name="codtipoiscrOld" classNameBase="input" 
                    		 moduleName="CI_TIPO_ISCR" selectedValue="<%= codtipoiscrOld %>"
                        	 addBlank="true" title="Tipo Iscrizione"/>
            	
            </td>
        	<td>
            	<af:textBox readonly="true" name="codAccordoOld" classNameBase="input" 
                    		 value="<%=codAccordoOld%>" title="Codice comunicazione"/>
            </td>
    	</tr>
    	<tr><td colspan="4">&nbsp;
    	</td></tr>
	    </table>
    	
    	<table class="main">
    	<tr>
          	<td colspan="4">
            <div class="sezione2">
            Domande compatibili
          	</td>
        	</tr>
    	<tr>
       		<td>Data Inizio</td>
    		<td>Data Fine</td>
    		<td>Tipo Iscrizione</td>
    		<td>Codice comunicazione domanda</td>
    	</tr>
    	
    	<% for (int j=0;j<resultCigCompatibili.size();j++) {
    		SourceBean cigCurr = (SourceBean)resultCigCompatibili.get(j);
    		String nameInizioCig = "datInizioCig_" + j;
    		String nameFineCig = "datFineCig_" + j;
    		String nameTipoIscr = "codTipoIscr_" + j;
    		String nameCodiceCig = "codiceCig_" + j;
    		String nameDescrTipoIscr = "descrCodTipoIscr_" + j;
    		String descrCodTipoIscr = (String)cigCurr.getAttribute("strdescrizioneCig");
   	    	codtipoiscrCig = (String)cigCurr.getAttribute("codtipoiscrCig");
   	    	codAccordoCig = (String)cigCurr.getAttribute("codAccordoCig");
   	   	 	prgAccordoCig = (String)cigCurr.getAttribute("prgAccordoCig");
    	    %>
    	   	<tr>
    		<td>
    		<input type="checkbox" name="CigCompatibile" value="<%=prgAccordoCig%>"/>&nbsp;
    		<af:textBox title="Data inizio iscrizione" classNameBase="input" 
                			readonly="true"  type="text" name="<%= nameInizioCig%>" value="<%=datInizioCig%>" 
                    		size="12"/>
    		</td>
        	<td>
        	<af:textBox title="Data fine iscrizione" classNameBase="input" 
                			readonly="true"  type="text" name="<%= nameFineCig%>" value="<%=datFineCig%>" 
                    		size="12"/>
        	</td>
        	<td>
        	
        	<af:textBox title="Tipo iscrizione" classNameBase="input" 
	                			readonly="true"  type="text" name="<%= nameDescrTipoIscr%>" value="<%=descrCodTipoIscr%>" 
	                    		size="12"/>
	        <input type="hidden" name="<%= nameTipoIscr%>" value="<%=codtipoiscrCig%>">
        	</td>
        	<td>
        	<af:textBox title="Codice comunicazione domanda" classNameBase="input" 
                			readonly="true"  type="text" name="<%= nameCodiceCig%>" value="<%=codAccordoCig%>" 
                    		size="12"/>
            </td>
    		</tr>
    	<% } %>
    <tr><td colspan="4">&nbsp;
    </td></tr>
    </table>
    
    <center>
    <table> 
    	<tr>
          	<td align="center">
            	<input type="button" class="pulsanti" name="Salva" value="Salva" onclick="associaDomandaCig();">
            </td>
            <td align="center">
            	<input type="submit" class="pulsanti" name="annulla" value="Annulla">
            </td>
    	</tr>
    </table>
    </center>
    <% }%>
      <%= htmlStreamBottom %>
 	  <input type="hidden" name="PAGE" value="CigLavListaPage">
      <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>                              
      <input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
      <input type="hidden" name="prgAltraIscr" value="<%=prgAltraIscr%>"/>
      <input type="hidden" name="datInizio" value="<%=datInizioOld%>"/>
      <input type="hidden" name="datFine" value="<%=datFineOld%>"/>
      <input type="hidden" name="prgAccordo" value=""/>
      <input type="hidden" name="codtipoiscr" value="<%=codtipoiscrOld%>"/>
      <input type="hidden" name="codMonoTipoCompetenza" value="<%=codMonoTipoCompetenza%>"/>
      <input type="hidden" name="codMotChiusuraIscr" value="<%=codMotChiusuraIscr%>"/>
      <input type="hidden" name="codStato" value="<%=codStato%>"/>
      <input type="hidden" name="datChiusuraIscr" value="<%=datChiusuraIscr%>"/>
      <input type="hidden" name="datCompetenza" value="<%=datCompetenza%>"/>
      <input type="hidden" name="strNota" value="<%=strNote%>"/>
      <input type="hidden" name="codComCompetenza" value="<%=codComCompetenza%>"/>
      <input type="hidden" name="LISTA" value="1"/>
      <input type="hidden" name="conferma" value="1"/>
      <% if (serviceRequest.containsAttribute("inserisci")) { %>
      <input type="hidden" name="inserisci" value="1"/>
      <% } else { %>
      <input type="hidden" name="aggiorna" value="1"/>
      <% } %>   
    </af:form>
    
  </body>
</html>

