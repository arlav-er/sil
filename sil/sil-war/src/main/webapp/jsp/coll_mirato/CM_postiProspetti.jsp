<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%	
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspPostiDispPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 	
	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String message = "";
	String prgMansioniDisp = "";
	String numPosti = "";
	String strNote = "";
	String codMansione = "";
	String codMonoCategoria = "";	
	String codMonoTipo = "";
	String codMonoStatoProspetto = "";
	String descrMansione = "";
	String descrTipoMansione = "";
	String codComDisp = "";
	String strComDisp = "";	
	String numKloMansioniDisp = "";
	String flgmezzipubblici = "";
	String flgturninotturni = "";
	String flgbarriere = "";
	String strdescrcompiti = ""; 
	String strcapacita = "";
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	//INFORMAZIONI OPERATORE
	Object cdnUtIns	= null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;   
	Linguette l = null;
	
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
	}		
	
	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspPostiDettModule.ROWS.ROW");				
	if (dett != null) {	
		message = "UPDATE";
		prgMansioniDisp = dett.getAttribute("prgMansioniDisp") == null? "" : ((BigDecimal)dett.getAttribute("prgMansioniDisp")).toString();
		numKloMansioniDisp = dett.getAttribute("numKloMansioniDisp") == null? "" : ((BigDecimal)dett.getAttribute("numKloMansioniDisp")).toString();		
		numPosti = dett.getAttribute("numPosti") == null? "" : ((BigDecimal)dett.getAttribute("numPosti")).toString();
		strNote = dett.getAttribute("strNote") == null? "" : (String)dett.getAttribute("strNote");		
		codMansione = dett.getAttribute("codMansione") == null? "" : (String)dett.getAttribute("codMansione");
		codMonoCategoria = dett.getAttribute("codMonoCategoria") == null? "" : (String)dett.getAttribute("codMonoCategoria");		
		codMonoTipo = dett.getAttribute("codMonoTipo") == null? "" : (String)dett.getAttribute("codMonoTipo");
		descrMansione = dett.getAttribute("descrMansione") == null? "" : (String)dett.getAttribute("descrMansione");
		descrTipoMansione = dett.getAttribute("descrTipoMansione") == null? "" : (String)dett.getAttribute("descrTipoMansione");
		codComDisp = dett.getAttribute("CODCOMDISP") == null? "" : (String)dett.getAttribute("CODCOMDISP");
		strComDisp = dett.getAttribute("COMUNE") == null? "" : (String)dett.getAttribute("COMUNE");
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		flgmezzipubblici = dett.getAttribute("flgmezzipubblici") == null? "" : (String)dett.getAttribute("flgmezzipubblici");
		flgturninotturni = dett.getAttribute("flgturninotturni") == null? "" : (String)dett.getAttribute("flgturninotturni");
		flgbarriere = dett.getAttribute("flgbarriere") == null? "" : (String)dett.getAttribute("flgbarriere");
		strdescrcompiti = dett.getAttribute("strdescrcompiti") == null? "" : (String)dett.getAttribute("strdescrcompiti");
		strcapacita = dett.getAttribute("strcapacita") == null? "" : (String)dett.getAttribute("strcapacita");
		
		cdnUtIns = dett.getAttribute("cdnUtIns");
		dtmIns = dett.getAttribute("dtmIns");
		cdnUtMod = dett.getAttribute("cdnUtMod");
		dtmMod = dett.getAttribute("dtmMod");
		
		operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		
	}
	else {
		message = "INSERT";			
	}
	
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMProspPostiDispPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");    	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)) {
    	canModify = false;
    } 	
	String fieldReadOnly = canModify ? "false" : "true";  			
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/CommonXMLHTTPRequest.js"></SCRIPT>

<script language="javascript">	
	
	var flagChanged = false;  

	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	

	function tornaLista() {
		var s= "AdapterHTTP?PAGE=CMProspPostiDispPage";
	    s += "&CDNFUNZIONE=<%= cdnfunzione%>";
	    s += "&PRGPROSPETTOINF=<%= prgProspettoInf%>";
	    s += "&codStatoAtto=<%= codStatoAtto%>";
	    setWindowLocation(s);
	}

	function controllaCampi() {		
		
		//var numDirigenti = parseInt(document.Frm1.numDirigenti.value);
	
		/*if (isNaN(numDirigenti)) {
			alert("ERRORE: Il campo deve essere numerico!");
			return false;
		}*/
				
		return true;      
	}
	
	function checkMansione(nameMansione) {
		var cod = new String(eval('document.Frm1.' + nameMansione + '.value'));
		if (cod == "") return true;
		if (cod.substring(cod.length-2,cod.length) == '00') {
			if (confirm("Non è stata indicata una mansione specifica. Continuare?")) {
				return controllaQualificaOnSubmit(nameMansione);
			}
		} else return controllaQualificaOnSubmit(nameMansione);
	}
	
	function controllaQualificaOnSubmit(campoQualifica) {
		var qualifica = new String(eval('document.Frm1.' + campoQualifica + '.value'));
		var exist = false;
		try {
			exist = controllaEsistenzaChiave(qualifica, "CODMANSIONE", "DE_MANSIONE");
		} catch (e) {
			return confirm("Impossibile controllare che la qualifica " + qualifica + " esista, proseguire comunque?");
		}
		if (!exist) {
			alert("Il codice della qualifica " + qualifica + " non esiste");
			return false;
		} else return true;
	}
			
</script>

<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 

  <%@ include file="Mansioni_CommonScripts.inc" %>
  <%@ include file="ControllaMansione.inc" %>
  <%@ include file="../global/Function_CommonRicercaComune.inc" %>
</head>

<body class="gestione" onload="rinfresca()">
<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}

l.show(out);
%>

<p class="titolo">Dettaglio del Posto Disponibile</p>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspPostiSaveModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
    

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMProspPostiDispPage"/>
<input type="hidden" name="MODULE" value="CMProspPostiSaveModule"/>
<input type="hidden" name="MESSAGE" value="<%=message%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="prgMansioniDisp" value="<%=prgMansioniDisp%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>"/>  
<input type="hidden" name="numKloMansioniDisp" value="<%=numKloMansioniDisp%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>   

<%out.print(htmlStreamTop);%>

<table class="main" border="0">				
<tr>
	<td colspan="2"/>&nbsp;</td>   
</tr>
<tr>
    <td class="etichetta2">Numero posti</td>
 	<td class="campo2">
		<af:textBox type="integer" title="Numero Posti" name="numPosti" value="<%= String.valueOf(numPosti)%>" size="5" maxlength="10" validateOnPost="true" onKeyUp="fieldChanged();" required="true" readonly="<%=fieldReadOnly%>"/>
    </td>	  
</tr>	
<tr>
	<td class="etichetta2">Categoria</td>
	<td class="campo2">
		<af:comboBox name="codMonoCategoria" classNameBase="input" required="true" disabled="<%=fieldReadOnly%>">	  	
		    <option value=""  <% if ( "".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %> ></option>               
            <option value="D" <% if ( "D".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>Disabili</option>
            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>Categoria protetta ex art. 18</option>               
        </af:comboBox>    
    </td>   
</tr>
<tr>
    <td class="etichetta2">Tipo assunzione</td>
	<td class="campo2">
		<af:comboBox name="codMonoTipo" classNameBase="input" disabled="<%=fieldReadOnly%>">	  	 
			<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipo) )  { %>SELECTED="true"<% } %> ></option>           
            <option value="M" <% if ( "M".equalsIgnoreCase(codMonoTipo) )  { %>SELECTED="true"<% } %>>Nominativa</option>
            <option value="R" <% if ( "R".equalsIgnoreCase(codMonoTipo) )  { %>SELECTED="true"<% } %>>Numerica</option>               	        
        </af:comboBox> 
    </td>
</tr>
<tr>
	<td class="etichetta">Codice mansione</td>
    <td class="campo">    
    	<af:textBox classNameBase="input" name="CODMANSIONE" validateWithFunction='<%= (canModify) ? "checkMansione" : ""%>' value="<%=codMansione%>" title="Codice Mansione" size="7" maxlength="7" required="true" readonly="<%=fieldReadOnly%>"/>
        <af:textBox type="hidden" name="codMansioneHid" />  
        <input type="hidden" name="flgFrequente" value="true"/>  
        <% if (canModify) { %>
        	<a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
        	<a href="javascript:ricercaAvanzataMansioni();">
            Ricerca avanzata
        	</a>
    	<%}%>
  </td>
</tr> 
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      	<af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      	<af:textBox classNameBase="input" name="strTipoMansione" value="<%=descrTipoMansione%>" readonly="true" size="48" />
	</td>
</tr>
<tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" title="Codice comune" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComDisp, document.Frm1.codComDispHid, document.Frm1.strComDisp, document.Frm1.strComDispHid, null, null, 'codice');"
                  type="text" name="codComDisp" value="<%=codComDisp%>" size="4" maxlength="4"
                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <%if (canModify) { %>
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComDisp, document.Frm1.strComDisp, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <%}%>
    <af:textBox type="hidden" name="codComDispHid" value="<%=codComDisp%>"/>
    <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComDisp, document.Frm1.codComDispHid, document.Frm1.strComDisp, document.Frm1.strComDispHid, null, null, 'descrizione');"
                name="strComDisp"  value="<%=strComDisp%>" size="30" maxlength="50"
                readonly="<%= String.valueOf(!canModify) %>" title="Comune" />&nbsp;
    <%if (canModify) { %>
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComDisp, document.Frm1.strComDisp, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
    <%}%>
    <af:textBox type="hidden"  name="strComDispHid" value="<%=strComDisp%>"/>
    </td>
</tr>
<tr>
  	<td class="etichetta">Descrizione mansione</td>
  	<td class="campo">
      	<af:textArea cols="30" rows="2" name="DESCMANSIONE" classNameBase="textarea" readonly="true" maxlength="100" value="<%= descrMansione %>" />
  	</td>
</tr>
<tr>
  	<td class="etichetta">Descrizione compiti</td>
  	<td class="campo">
      	<af:textArea cols="30" rows="2" name="DESCCOMPITI" title="Descrizione compiti" required="true" classNameBase="textarea" readonly="<%=fieldReadOnly%>" maxlength="2000" value="<%= strdescrcompiti %>" />
  	</td>
</tr>
<tr>
  	<td class="etichetta">Capacità richieste/controindicazioni</td>
  	<td class="campo">
      	<af:textArea cols="30" rows="2" name="DESCCAPACITA" title="Capacità richieste/controindicazioni" required="true" classNameBase="textarea" readonly="<%=fieldReadOnly%>" maxlength="2000" value="<%= strcapacita %>" />
  	</td>
</tr>
<tr>
   <td class="etichetta">Presenza di barriere architettoniche</td>
   <td class="campo">
   	<af:comboBox name="flgbarriere" title="Presenza di barriere architettoniche" classNameBase="input" disabled="<%=fieldReadOnly%>" required="true">
    	<option value=""  <% if ( "".equalsIgnoreCase(flgbarriere) )  { %>SELECTED="true"<% } %> ></option>
    	<option value="S"  <% if ( "S".equalsIgnoreCase(flgbarriere) )  { %>SELECTED="true"<% } %> >Si</option>
    	<option value="N"  <% if ( "N".equalsIgnoreCase(flgbarriere) )  { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>  
    </td> 
</tr>
<tr>
   <td class="etichetta">Raggiungibilità mezzi pubblici</td>
   <td class="campo">
   	<af:comboBox name="flgmezzipubblici" title="Raggiungibilità mezzi pubblici" classNameBase="input" disabled="<%=fieldReadOnly%>" required="true">
    	<option value=""  <% if ( "".equalsIgnoreCase(flgmezzipubblici) )  { %>SELECTED="true"<% } %> ></option>
    	<option value="S"  <% if ( "S".equalsIgnoreCase(flgmezzipubblici) )  { %>SELECTED="true"<% } %> >Si</option>
    	<option value="N"  <% if ( "N".equalsIgnoreCase(flgmezzipubblici) )  { %>SELECTED="true"<% } %> >No</option>  
    </af:comboBox>  
    </td> 
</tr>
<tr>
   <td class="etichetta">Turni notturni</td>
   <td class="campo">
   	<af:comboBox name="flgturninotturni" title="Turni notturni" classNameBase="input" disabled="<%=fieldReadOnly%>" required="true">
    	<option value=""  <% if ( "".equalsIgnoreCase(flgturninotturni) )  { %>SELECTED="true"<% } %> ></option>
    	<option value="S"  <% if ( "S".equalsIgnoreCase(flgturninotturni) )  { %>SELECTED="true"<% } %> >Si</option>
    	<option value="N"  <% if ( "N".equalsIgnoreCase(flgturninotturni) )  { %>SELECTED="true"<% } %> >No</option>  
    </af:comboBox>  
    </td> 
</tr>
<tr>  
  	<td class="etichetta">Note</td>
  	<td class="campo">
      	<af:textArea cols="50" rows="5" name="strNote" classNameBase="textarea" maxlength="1000" value="<%= strNote %>" readonly="<%=fieldReadOnly%>"/>
  	</td>
</tr>
<tr>
	<td colspan="2">		   		
		<%
		if (canModify) {
			if (("UPDATE").equalsIgnoreCase(message)) {
		%>
				<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" />	
		<%
			}
			else {
		%>		
				<input type="submit" class="pulsante" name="inserisci" value="Inserisci" />			
		<%
			}
		}
		%>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" class="pulsante" name="torna" value="Torna alla lista" onclick="tornaLista()" />
	</td>
</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
<p align="center">
<% if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
</p>
<br/>
</body>
</html>