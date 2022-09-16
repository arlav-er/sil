<!-- @author: Giordano Gritti -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*,
                  java.text.*,
                  it.eng.afExt.utils.*"
%> 


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%			
			String _page 			= 	(String) serviceRequest.getAttribute("PAGE");
			int _cdnFunz 			= 	Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
			String cdnFunzione 		= 	(String) serviceRequest.getAttribute("CDNFUNZIONE");
			String cdncomponente 	= 	(String) serviceRequest.getAttribute("CDNCOMPONENTE");
			String cdnLavoratore 	= 	(String) serviceRequest.getAttribute("cdnLavoratore");
														
			BigDecimal cdnUtIns 		= 	null;
			String dtmIns 				= 	"";
			BigDecimal cdnUtMod 		= 	null;
			String dtmMod 				= 	"";	
			BigDecimal prgAzienda		= 	null;	
			BigDecimal prgUnita			=	null;
			BigDecimal prgRichiestaAz	= 	null;
			
			String avviso				=	"";
			String gradDef				=	"";
			String gradEnte				=   "";
			String flgPubblicata		=  	"";
			
			
			Vector vett 			= serviceResponse.getAttributeAsVector("M_AS_GetPubbNote.ROWS.ROW");
			SourceBean sbNote 		= (SourceBean) vett.get(0);
			
			cdnUtIns 				= (BigDecimal) sbNote.getAttribute("CDNUTINS"); 
			dtmIns 					= StringUtils.getAttributeStrNotNull(sbNote, "DTMINS");
			cdnUtMod				= (BigDecimal) sbNote.getAttribute("CDNUTMOD");
			dtmMod 					= StringUtils.getAttributeStrNotNull(sbNote, "DTMMOD");
			prgAzienda				= (BigDecimal) sbNote.getAttribute("PRGAZIENDA");
			prgUnita				= (BigDecimal) sbNote.getAttribute("PRGUNITA");
			prgRichiestaAz			= (BigDecimal) sbNote.getAttribute("PRGRICHIESTAAZ");
			
			avviso					= StringUtils.getAttributeStrNotNull(sbNote, "STRNOTAAVVISOPUBB");
    		gradDef					= StringUtils.getAttributeStrNotNull(sbNote, "STRNOTAGRADDEF");
    		gradEnte				= StringUtils.getAttributeStrNotNull(sbNote, "STRNOTAGRADENTE");
    		flgPubblicata			= StringUtils.getAttributeStrNotNull(sbNote, "FLGPUBBLICATA");
    		
			//INFORMAZIONI OPERATORE
			Testata operatoreInfo 	= 	null;
			//LINGUETTE sono create e mostrate dopo il tag body 
			Linguette l 			= 	null;
				    							
			PageAttribs attributi 	= 	new PageAttribs(user, _page);
			
			operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);			
			//controllo sulla modalità di evasione
			String codiceEv			= "";
			Vector mod				= 	serviceResponse.getAttributeAsVector("M_IdoGetStatoRich.ROWS.ROW");				
			for (int i = 0; i<mod.size(); i++) {
				SourceBean sbMod 	= (SourceBean)mod.get(i);
				codiceEv 			=  StringUtils.getAttributeStrNotNull(sbMod, "codEvasione");
			}			
			
			//conttrollo sulla profilatura e sulla modalità di evasione,
			//se il codice di evasione è AS è permessa la modfica altrimenti sola lettura 
			boolean btnAggiorna		= 	true;
			boolean canModify 		= 	true;			
			boolean readOnlyStr     = 	false;
			boolean readFlgPubS     = 	false; 		
    					
			if (!codiceEv.equals("AS")){
				readFlgPubS = true;
				readOnlyStr = true;
				btnAggiorna = false;
				canModify	= false;
				  
				avviso = "";
    			gradDef = "";
    			gradEnte = "";				
    			flgPubblicata = "";
    			
			} else {
				canModify     			=	attributi.containsButton("AGGIORNA");		
				readOnlyStr 			=   !attributi.containsButton("AGGIORNA");
				readFlgPubS				=  	!attributi.containsButton("AGGIORNA");		
				btnAggiorna				=	canModify && btnAggiorna;	
			}	
			
			String htmlStreamTop 	= 	StyleUtils.roundTopTable(canModify);
			String htmlStreamBottom = 	StyleUtils.roundBottomTable(canModify);
%>

<%@ include file="_infCorrentiAzienda.inc" %>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">


<af:linkScript path="../../js/" /> 

<title>Note per le pubblicazioni</title>

<script language="Javascript">
<%-- Savino 22/01/2007: perche' caricare il menu? Va fatto solo la prima volta che si entra in un nuovo contesto. --%>
    <%-- window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>); --%>
	<%//Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);%>
	
	// Rilevazione Modifiche da parte dell'utente
	var flagChanged = false;
	        
	function fieldChanged() {
	<% if (!readOnlyStr){ %> 
	  flagChanged = true;
	<%}%> 
	}
	
</script>

</head>
<body class="gestione" onload="rinfresca();">


<%
    //informazione sintetiche relative all'azienda
    if(infCorrentiAzienda != null) {%> 
       <div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%	}
	// Creo le linguettte
	if(prgRichiestaAz != null && !prgRichiestaAz.equals("")  ) {
		l = new Linguette( user,  _cdnFunz, _page, prgRichiestaAz);
  		l.setCodiceItem("PRGRICHIESTAAZ");    
 		l.show(out);
	}
%>

<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>
<font color="green"> 
 <af:showMessages prefix="M_AS_UpdPubbNote"/>
</font>
<br>

<%out.print(htmlStreamTop);%> 
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
	<table>
<% 
if (flgPubblicata.equalsIgnoreCase("S")  && codiceEv.equals("AS")) {
	readFlgPubS = true;
}    
%>		
		<tr>
	    	<td class="etichetta">Nota avviso pubblico</td>
	    	<td colspan=3 class="campo">	
				<af:textArea classNameBase="textarea" name="STRNOTAAVVISOPUBB" value="<%=avviso%>"
	                 cols="60" rows="4" maxlength="1000" onKeyUp="fieldChanged();"
	                 readonly="<%=String.valueOf(readFlgPubS)%>" />
	      	</td>
		</tr>
		<tr>
	    	<td class="etichetta">Nota graduatoria definitiva</td>
	    	<td colspan=3 class="campo">	
				<af:textArea classNameBase="textarea" name="STRNOTAGRADDEF" value="<%=gradDef%>"
	                 cols="60" rows="4" maxlength="1000" onKeyUp="fieldChanged();"
	                 readonly="<%=String.valueOf(readOnlyStr)%>"/>
	      	</td>
		</tr>	
		<tr>
	    	<td class="etichetta">Nota graduatoria da inviare all'Ente</td>
	    	<td colspan=3 class="campo">	
				<af:textArea classNameBase="textarea" name="STRNOTAGRADENTE" value="<%=gradEnte%>"
	                 cols="60" rows="4" maxlength="1000" onKeyUp="fieldChanged();"
	                 readonly="<%=String.valueOf(readOnlyStr)%>"/>
	      	</td>
		</tr> 	
	</table>
	
	<input type="hidden" name="PAGE" value="ASNotePubbPage"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>" />
	<input type="hidden" name="CDNCOMPONENTE" value="<%=cdncomponente%>"/>
	<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>"/>
	
	<% if (readOnlyStr){%>
	<table>
		<tr>
			<td align="center">
				
			</td>
		</tr>
	</table>

	<%}	else {%>
	<table >
		<tr>
			<td>&nbsp;
			</td>
		</tr>
		<tr>			
			<td align="center">
				<input type="submit" class="pulsanti<%=((btnAggiorna)?"":"Disabled")%>" <%=(!btnAggiorna)?"disabled=\"true\"":""%>" value="aggiorna" name="btnAggiorna">
				
			</td>				
		</tr>
		<tr>			
			<td>&nbsp;
			</td>
		</tr>
	</table>	
	<%}%>

	
	<%out.print(htmlStreamBottom);
		if (operatoreInfo!=null) {
	%>
  	<center>
  		<table>
  			<tr>
  				<td align="center">
	<%  operatoreInfo.showHTML(out);%>
  				</td>
  			</tr>
		
  		</table>
  	</center>
  	<%}%>
</af:form>
</body>
</html>



