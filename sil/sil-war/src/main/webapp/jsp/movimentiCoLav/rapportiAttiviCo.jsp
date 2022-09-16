<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.gov.mlps.DataModels.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%

String _page = (String) serviceRequest.getAttribute("PAGE");

PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 

String titlePagina = "Elenco Rapporti Attivi";
String dataInizio= null;
String dataFine = null;
GetRapportoLavoroAttivo_Output outputWS  = null;
RapportoLavoroAttivo[] allResults = null;
if(serviceResponse.containsAttribute("M_CONSULTACO.WS_OUTPUT_RLA") && serviceResponse.getAttribute("M_CONSULTACO.WS_OUTPUT_RLA")!=null ){ 
	dataInizio =(String) serviceResponse.getAttribute("M_CONSULTACO.DATA_INIZIO");
	dataFine =(String)  serviceResponse.getAttribute("M_CONSULTACO.DATA_FINE");	
	
	outputWS = (GetRapportoLavoroAttivo_Output) serviceResponse.getAttribute("M_CONSULTACO.WS_OUTPUT_RLA");
	allResults = outputWS.getRapportoLavoroAttivo();
}


String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
boolean readOnlyStr = true; 
%>
 
	<html>
	<head>
	<title><%=titlePagina %></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>
	
	<af:linkScript path="../../js/"/>
	<script language="JavaScript">

var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src=" ../../img/aperto.gif";
hideButtonImg.src=" ../../img/chiuso.gif"


function onOff(index)
{	var div1 = document.getElementById("dett"+index);
	var idImm = document.getElementById("imm1"+index);
	if (div1.style.display=="")
  {	nascondi("dett"+index);
		idImm.src = hideButtonImg.src
	} 
	else
  {	mostra  ("dett"+index);
		idImm.src = showButtonImg.src;
	}
}//onOff()

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}


</script>

<style type="text/css">
td.campo {
 width: 20%;
}
</style>
	
	</head>
	<body class="gestione" >

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_ConsultaCO"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
 	<br/><br/>
 			

		<% 
	
		if(outputWS!= null && allResults!=null && allResults.length > 0){
			
	%>

 	<div align="center">
 	<table class="main">
			<tr>
				<td colspan="4"><p class="titolo"><%= titlePagina %></p></td>
			</tr>
			<tr>
				<td><br /></td>
			</tr>
		</table>
	</div>	
	<table margin="0" noshade="" width="94%" cellspacing="0" cellpadding="0" border="0px" align="center">
		<tr>
				<td colspan="4">
	       			Periodo di riferimento: <%= dataInizio %> - <%= dataFine %>
	        	</td>
				 
			</tr>
	</table>
 	<%= htmlStreamTopCoop%>
	 <af:form name="Frm1" action="AdapterHTTP" method="POST" >
		<div align="center">



	<table class="main">
		<%for(int i=0; i < allResults.length; i++){
			RapportoLavoroAttivo rapporto = allResults[i]; 
			boolean isRapporto =false;
			boolean isDatore = false;
			
			if(rapporto!=null){
				
				if(rapporto.getRapportoLavoro()!=null){
					isRapporto = true;
				}
				if(rapporto.getDatoreLavoro()!=null){
					isDatore = true;
				}
				Date dataInRapp = isRapporto ? rapporto.getRapportoLavoro().getDataInizio().getTime() : null;
				String strDataInRapp =isRapporto ?  DateUtils.getSimpleDateFormatFixBugMem(it.eng.sil.module.movimenti.consultaCO.Properties.FORMATO_DATA).format(dataInRapp) : "";
				
				Date dataFineRapp =(isRapporto && rapporto.getRapportoLavoro().getDataFine()!=null)?  rapporto.getRapportoLavoro().getDataFine().getTime() : null;
				
				String strDataFineRapp = dataFineRapp==null? "ad oggi" : DateUtils.getSimpleDateFormatFixBugMem(it.eng.sil.module.movimenti.consultaCO.Properties.FORMATO_DATA).format(dataFineRapp);
				String denominazione =isDatore? rapporto.getDatoreLavoro().getDenominazione().toUpperCase() : "";
				String intestazione ="";
				if(isRapporto && isDatore){
					intestazione = strDataInRapp+ " - "+ strDataFineRapp + " presso "+ denominazione;
				}else if(isRapporto){
					intestazione = strDataInRapp+ " - "+ strDataFineRapp ;
				}else if(isDatore){
					intestazione = "Rapporto presso "+ denominazione;
				}
				
		%>	 
		<tr>
		  <td colspan="4">    
		    <div class="sezione2" id="sezione<%=i %>">
		    	<a name="sezione<%=i %>" href="#sezione<%=i %>" onClick="onOff('<%=i %>')"><img align="middle" id="imm1<%=i %>" alt="mostra/nascondi" src=" ../../img/chiuso.gif" border="0"></a>
		    		<b>&nbsp;<%= intestazione %></b>
		    </div>
		  </td>
		</tr>
		
		<tr><td colspan="4">
		<div id="dett<%=i %>" style="display:none">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="2"><b>Datore Lavoro</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Codice fiscale</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="cfDatLav" value='<%=isRapporto ?rapporto.getDatoreLavoro().getCodiceFiscaleDatore() : "" %>'  
			                      readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Denominazione</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="denDatLav" value='<%=isRapporto ?rapporto.getDatoreLavoro().getDenominazione():"" %>'  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			   <td class="campo"  rowspan="3"><b>Sede Legale</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Indirizzo</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="indSedLeg" value='<%=isDatore? rapporto.getDatoreLavoro().getIndirizzoSedeLegale(): "" %>'  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"   /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >CAP</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="capSedLeg" value='<%=isDatore? Utils.notNull(rapporto.getDatoreLavoro().getCAPSedeLegale()) : ""%>'  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Comune/Stato estero</td>
			      	<td>
			      	<%String comuneSedLeg =isDatore? Utils.notNull(rapporto.getDatoreLavoro().getCodiceComuneSedeLegale() )+ " - " + Utils.notNull(rapporto.getDatoreLavoro().getComuneSedeLegale()) : ""; %>
			          <af:textBox classNameBase="input" type="text" name="comSedLeg" value="<%=comuneSedLeg %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			  <td class="campo"  rowspan="3"><b>Sede Lavoro</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Indirizzo</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="indSedLav" value='<%=isDatore? rapporto.getDatoreLavoro().getIndirizzoSedeLavoro(): ""%>'  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"   /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >CAP</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="capSedLav" value='<%=isDatore? Utils.notNull(rapporto.getDatoreLavoro().getCAPSedeLavoro()) : ""%>'  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"   /></td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Comune/Stato estero</td>
			      	<td>
			      	 <%String comuneSedLav =isDatore? Utils.notNull(rapporto.getDatoreLavoro().getCodiceComuneSedeLavoro() )+ " - " + Utils.notNull(rapporto.getDatoreLavoro().getComuneSedeLavoro()) : ""; %>
			          <af:textBox classNameBase="input" type="text" name="comSedLav" value="<%=comuneSedLav %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"   /></td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
			
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			 <tr>
			 	<td class="campo"  rowspan="4"><b>Dati Rapporto</b></td>
			   <td>
			    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
			      <tr>
			      	<td class="etichetta" >Data inizio</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="datInRapp" value="<%=strDataInRapp %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Data fine</td>
			      	<td>
			          <af:textBox classNameBase="input" type="text" name="datFinRapp" value="<%=strDataFineRapp %>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/></td>
			      </tr>
			     <tr>
			      	<td class="etichetta" >Tipologia Contrattuale</td>
			      	<td>
			      	<%String strTipoContr=isRapporto? rapporto.getRapportoLavoro().getCodiceTipologiaContrattuale() + " - " + rapporto.getRapportoLavoro().getTipologiaContrattuale() : ""; %>
			          <af:textBox classNameBase="input" type="text" name="tipoContr" value="<%= strTipoContr%>"  
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			      <tr>
			      	<td class="etichetta" >Tipologia Contrattuale Tedesco</td>
			      	<td>
			      	<%String strTipoContrTed= isRapporto? rapporto.getRapportoLavoro().getTipologiaContrattualeTedesco() : ""; %>
			          <af:textBox classNameBase="input" type="text" name="tipoContrTed" value="<%= Utils.notNull(strTipoContrTed)%>"    
			                      readonly="<%=String.valueOf(readOnlyStr)%>"  /></td>
			      </tr>
			    </table>
			   </td>
			 </tr>
			</table>
		</div>
		</td></tr>
	 	
	
	<%		}
		}
	%>
		</table>


<br/>

</div>

</af:form>
<%= htmlStreamBottomCoop%>
	<%}	%>
<center>
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
	<br>&nbsp;
</center>

</body>
</html>