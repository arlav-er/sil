<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.afExt.utils.StringUtils,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
	// NOTE: Attributi della pagina (pulsanti e link) 
  	PageAttribs attributi = new PageAttribs(user, "MobilitaConsultaFileRegPage");
  	boolean canDelete= attributi.containsButton("RIMUOVI");
    String cdnFunzione = (String)serviceRequest.getAttribute("cdnFunzione");

    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
    String strCodiceFiscale=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
  	String strCognomeNome=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognomeNome");
  	String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  	String CodProvincia=StringUtils.getAttributeStrNotNull(serviceRequest,"CodProvincia");
  	
  	String strAzRagioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest,"strAzRagioneSociale");
  	
  	String CodMbTipo=StringUtils.getAttributeStrNotNull(serviceRequest,"CodMbTipo");      
  	String datInizioDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioDa");
  	String datInizioA=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioA");
  	String datFineDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datFineDa"); 
  	String datFineA=StringUtils.getAttributeStrNotNull(serviceRequest,"datFineA");    
  	String strNumAtto=StringUtils.getAttributeStrNotNull(serviceRequest,"strNumAtto");
  	String datCRT=StringUtils.getAttributeStrNotNull(serviceRequest,"datCRT");  
  	String codEnteDetermina=StringUtils.getAttributeStrNotNull(serviceRequest,"codEnteDetermina");
%>

<html>
<head>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
<script type="text/JavaScript">

 function TornaAllaRicerca() 
{ // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
    
	url="AdapterHTTP?PAGE=MobilitaConsultaFileRegPage"; 
    url += "&cdnFunzione="+"<%=cdnFunzione%>";
    url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
    url += "&strCognomeNome="+"<%=strCognomeNome%>"; 
    url += "&CodCPI="+"<%=CodCPI%>";
    url += "&CodProvincia="+"<%=CodProvincia%>";
    url += "&strAzRagioneSociale="+"<%=strAzRagioneSociale%>";
    url += "&CodMbTipo="+"<%=CodMbTipo%>";
    url += "&datInizioDa="+"<%=datInizioDa%>";
    url += "&datInizioA="+"<%=datInizioA%>"; 
    url += "&datFineDa="+"<%=datFineDa%>";
    url += "&datFineA="+"<%=datFineA%>";
    url += "&strNumAtto="+"<%=strNumAtto%>";
    url += "&datCRT="+"<%=datCRT%>";
    url += "&codEnteDetermina="+"<%=codEnteDetermina%>";
    setWindowLocation(url);
}

function confirmDeleteFiltrati() {
	if(confirm('Vuoi eliminare le mobilità filtrate?')) {
		return true;
  	} 
  	else {
  		return false;
  	}
}

</script>
</head>

<body onload="checkError();rinfresca()">
<af:error/>
<br/><p class="titolo">Lista Mobilità da File Regionale</p>
<%String attr   = null;
  String valore = null;
  String txtOut = "";
%>
     <%attr= "strCodiceFiscale";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Codice Fiscale <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "strCognomeNome";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Cognome e Nome <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "descCPI_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "CPI <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "descProvincia_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Provincia <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "strAzRagioneSociale"; //obietti del patto
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Ragione sociale Azienda <strong>"+ valore +"</strong>; ";
       }%>
       <%attr= "descTipoMob_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Tipo Mobilità <strong>" + valore + "</strong>; ";
       }%>
     <%attr= "datInizioDa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Inizio da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datInizioA";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Inizio a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datFineDa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Fine da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datFineA";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Fine a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "strNumAtto";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Numero Approvazione <strong>" + valore + "</strong>; ";
       }%>
       <%attr= "datCRT";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Approvazione <strong>" + valore + "</strong>; ";
       }%>
       <%attr= "codEnteDetermina";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Sigla Ente Approvazione <strong>" + valore + "</strong>; ";
       }%>

      
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>
	<af:form name="FormListaMobReg" action="AdapterHTTP" method="GET" onSubmit="confirmDeleteFiltrati()">
	<af:list moduleName="MobilitaListaFileReg"/>
	<center>
	<table>
	<tr><td>
	<input class="pulsante" type ="button" name="torna" value="Torna alla Ricerca" onclick="TornaAllaRicerca()"/>
	</td>
	<td>
	<%if (canDelete) {%>
	<input type="submit" class="pulsanti" name="CANCELLAFILTRATI" value="Cancella filtrati">
	<%} else {%>
	&nbsp;
	<%}%>
	</td>
	</tr>
	</table>
	</center>
		
	<!--PARAMETRI USATI NELLA RICERCA PER FILTRARE LE MOBILITA' -->
    <input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscale%>"/>
    <input type="hidden" name="strCognomeNome" value="<%=strCognomeNome%>"/>
    <input type="hidden" name="CodCPI" value="<%=CodCPI%>"/>
    <input type="hidden" name="CodProvincia" value="<%=CodProvincia%>"/>
    <input type="hidden" name="strAzRagioneSociale" value="<%=strAzRagioneSociale%>"/>
    <input type="hidden" name="CodMbTipo" value="<%=CodMbTipo%>"/>
    <input type="hidden" name="datInizioDa" value="<%=datInizioDa%>"/>
    <input type="hidden" name="datInizioA" value="<%=datInizioA%>"/>
    <input type="hidden" name="datFineDa" value="<%=datFineDa%>"/>
    <input type="hidden" name="datFineA" value="<%=datFineA%>"/>
    <input type="hidden" name="strNumAtto" value="<%=strNumAtto%>"/>
    <input type="hidden" name="datCRT" value="<%=datCRT%>"/>
    <input type="hidden" name="codEnteDetermina" value="<%=codEnteDetermina%>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input type="hidden" name="PAGE" value="MobilitaConsultaFileRegPage"/>
    
</af:form>

</body>
</html>
