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
                  it.eng.sil.security.User,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  
  InfCorrentiLav infCorrentiLav= null;
  
  Testata operatoreInfo = null;
 
  String  cdnLavoratore = null;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  PageAttribs attributi = new PageAttribs(user, _page); 
  boolean canModify= attributi.containsButton("AGGIORNA");
  
  String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
 
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  
  String datRevisione="";
  String datPrimist="";
  String datRevisionato="";
  String numVerb="";
  String prgVerbaleAcc="";
  String verbale="";
  String NUMKLOVERBALEACC="";
  
  String dtmIns = "";
  String cdnUtMod = "";
  String dtmMod = "";
  String cdnUtIns = "";
  
  String configDiagnFunz = serviceResponse.containsAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM")?
		  	serviceResponse.getAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM").toString():"0";
  
  boolean nuovoVerbale = !(serviceRequest.getAttribute("nuovoVerbale")==null || 
                            ((String)serviceRequest.getAttribute("nuovoVerbale")).length()==0);

   //prgVerbaleAcc = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGVERBALEACC");
   if(!nuovoVerbale){
  		Vector verbaleVec = serviceResponse.getAttributeAsVector("M_LOAD_VERBALEACC.ROWS.ROW");
  		if(verbaleVec != null && !verbaleVec.isEmpty()) { 
  			SourceBean verbaleRow = (SourceBean) verbaleVec.elementAt(0);
  			NUMKLOVERBALEACC = String.valueOf(((BigDecimal)verbaleRow.getAttribute("NUMKLOVERBALEACC")).intValue());
  			prgVerbaleAcc = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "PRGVERBALEACC");
  		 	datPrimist = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "DATPRIMIST");
  			verbale = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "VERBALE");
  			datRevisione = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "DATDAREVISIONE");
  			datRevisionato = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "DATREVISIONATO");
  			numVerb = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "NUMVERB");
  			dtmIns = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "DTMINS");
			cdnUtMod = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "CDNUTMOD");
			dtmMod = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "DTMMOD");
			cdnUtIns = SourceBeanUtils.getAttrStrNotNull(verbaleRow, "CDNUTINS");
  		}
  }
  
	if(!dtmIns.equals("") && !cdnUtMod.equals("") && !dtmMod.equals("") && !cdnUtIns.equals("")) {
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  	}	
  

%>

<html>

<head>
    <title>CMlistaVerAcc.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>   
    <af:linkScript path="../../js/"/>
    <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 

	<script language="Javascript">
	 var configVerbale = '<%=configDiagnFunz%>';
	 
	 function tornaLista() {
		if (isInSubmit()) return;
		var f;
      	f = "AdapterHTTP?PAGE=CMListaVerAccPage";
      	f = f + "&CDNFUNZIONE=<%=cdnFunzione%>";
	  	f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
	  	document.location = f;
     }	
	    
	 function setRevisioneDate() {
		 if (configVerbale == '1') {
			 var d;
			 d = document.Frm1.DATVERBALEPRIMAIST.value;
			 // dd/mm/yyyy
			 meseinizio=d.substring(3,5);
	         giornoinizio=d.substring(0,2);
	         annoinizio=d.substring(6,10);
	         nuovoanno = parseInt(annoinizio)+3;
	         
	         var ddate=giornoinizio+"/"+meseinizio+"/"+nuovoanno;
			 
			 
			 document.Frm1.DATVERBALEDAREVISIONARE.value = ddate;
		 }
	  }   
		
		<% 
	       //Genera il Javascript che si occuperà di inserire i links nel footer
	       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
	    %>
	
	</script>
	
<script language="Javascript" src="../../js/docAssocia.js"></script>
</head>

<body class="gestione" onload="rinfresca();">

	<%
		InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  		testata.setSkipLista(true);
		testata.show(out);
	%>

	<font color="red"><af:showErrors/></font>
	<af:showMessages prefix="M_InsertVerAcc"/>
 	<af:showMessages prefix="M_UpdateVerAcc"/> 

	<af:showErrors />
	
	<af:error />

<p class="titolo">Dettaglio verbale d'accertamento</p>

<af:form method="POST" action="AdapterHTTP" name="Frm1">

<input type="hidden" name="PAGE" value="CMVerbaleAccDettPage"/>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PRGVERBALEACC" value="<%=prgVerbaleAcc%>"/>


<%= htmlStreamTop %>
	<table>
		<tr>
			<td class="etichetta">Data verbale&nbsp;</td>
		    <td class="campo">
		    	<af:textBox validateOnPost="true" type="date" callBackDateFunction="setRevisioneDate()" 
		    		onBlur="if (checkFormatDate(this)) setRevisioneDate();"
		    		name="DATVERBALEPRIMAIST" value="<%=datPrimist%>" size="12" maxlength="12" title="Data verbale di prima istanza" classNameBase="input" required="true"/></td>
		</tr>
		<tr>
			<td class="etichetta">Verbale</td>
			<td class="campo">
		    	<af:textArea cols="80" rows="5" maxlength="2000" classNameBase="textarea"  
                			  name="STRVERBALEPRIMAIST" 
                			  validateOnPost="true" 
                        	  required="false" title="Verbale" value="<%=verbale%>" />
            </td>
         
		</tr>
		<tr>
			<td class="etichetta">Numero pratica</td>
			<td class="campo">
				<af:textBox title="Numero pratica" validateOnPost="true" type="text" name="STRNUMVERBALE" value="<%=numVerb%>" classNameBase="input"/>&nbsp;&nbsp;&nbsp;
			</td>
		</tr>
		<tr>
			<td class="etichetta">Data entro cui effettuare la revisione del verbale&nbsp;</td>
		    <td class="campo">
		    	<af:textBox validateOnPost="true" type="date" name="DATVERBALEDAREVISIONARE" value="<%=datRevisione%>" size="12" maxlength="12" classNameBase="input"/>
		    </td>
		</tr>
		<tr>
			<td class="etichetta">Data avvenuta verifica verbale</td>
		    <td class="campo">
		    	<af:textBox validateOnPost="true" type="date" name="DATVERBALEREVISIONATO" value="<%=datRevisionato%>" size="12" maxlength="12" classNameBase="input"/>
		    </td>
		</tr>
		</table>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<table>
		<% if(nuovoVerbale) {%>
  			<tr>
  				<td>
  					<center>
  						<input type="submit" name="Inserisci" class="pulsanti" value="Inserisci"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
		<% } else if(!nuovoVerbale && canModify) {%>
			<tr>
				<td>
					<center>
						<input type="submit" name="Aggiorna" class="pulsanti" value="Aggiorna"/>
						<input type="hidden" name="NUMKLOVERBALEACC" value="<%=NUMKLOVERBALEACC%>"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
		<%}%>
						<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
					</center>
				</td>
			</tr>
		</table>
			
		<%out.print(htmlStreamBottom);%> 
		
		<% if (!nuovoVerbale){ %>
		<center>
			<table>
				<tr>
					<td align="center">
						<%operatoreInfo.showHTML(out);%>
					</td>
				</tr>
			</table>
		</center>
	 <%}%>
	  
	</af:form>
</body>
</html>
