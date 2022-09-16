<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                java.util.*,
                it.eng.afExt.utils.*,
                it.eng.sil.security.* " %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link)
  String _page = (String) serviceRequest.getAttribute("PAGE");
  PageAttribs attributi = new PageAttribs(user, "MobilitaApprovazioneRicercaPage");
  boolean canApprova = attributi.containsButton("APPROVA_MOB");
  String resultConfigMob = serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString():"0";
  Vector listaMob = serviceResponse.getAttributeAsVector("M_MobilitaRicercaApprovazione.ROWS.ROW");
  int sizePagina = listaMob.size();
  String titleDataCr = "";
  String labelNumDelibera = "";
  String labelRegioneCrt = "";
  String labelProvinciaCrt = "";
  String labelStatoMob = "";
  
  if (resultConfigMob.equals("0")) {
	  titleDataCr = "Data CRT";
	  labelNumDelibera = "Numero CRT";
	  labelRegioneCrt = "Regione CRT";
	  labelProvinciaCrt = "Provincia CRT";
	  labelStatoMob = "Stato della richiesta";
  }
  else {
	  titleDataCr = "Data CPM";
	  labelNumDelibera = "Numero CPM";
	  labelRegioneCrt = "Regione CPM";
	  labelProvinciaCrt = "Provincia CPM";
	  labelStatoMob = "Stato della domanda";
  }
  
  String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  String descCPI =  SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_DescCPI.ROWS.ROW.STRDESCRIZIONE");
  
  String CodTipoLista = "";
  Vector CodTipoListaVet = null;
  Vector CodStatoMob = null;
  String codStatiMob = "";
  String descTipoLista = "";
  String descStatoLista = "";
  Vector rowsTipoListaSb = serviceResponse.getAttributeAsVector("M_TIPO_LISTA_SOSPESA.ROWS.ROW");
  Vector rowsStatoListaSb = serviceResponse.getAttributeAsVector("M_MO_STATO.ROWS.ROW");
  int sizeTipoLista = rowsTipoListaSb.size();
  int sizeStatoLista = rowsStatoListaSb.size();
  
  if (serviceRequest.containsAttribute("validaApprov")) {
	String listTipi = StringUtils.getAttributeStrNotNull(serviceRequest, "CodTipoLista");
	CodTipoListaVet = StringUtils.split(listTipi, ",");
  }
  else {
	CodTipoListaVet = serviceRequest.getAttributeAsVector("CodTipoLista");  
  }
  
  if(CodTipoListaVet.size() > 0) {		
	for(int i=0; i<CodTipoListaVet.size(); i++) {
	  	if(!CodTipoListaVet.elementAt(i).equals("")) {
	  		String tipoListaCurr = CodTipoListaVet.elementAt(i).toString().trim();
	  		for (int j=0;j<sizeTipoLista;j++) {
	  	  		SourceBean rowCurr = (SourceBean)rowsTipoListaSb.get(j);
	  	  		String codiceLista = rowCurr.getAttribute("CODICE").toString().trim();
	  	  		if (codiceLista.equalsIgnoreCase(tipoListaCurr)) {
	  	  			if (descTipoLista.equals("")) {
	  	  				descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
	  	  			}
	  	  			else {
	  	  				descTipoLista = descTipoLista + ", " + StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
	  	  			}
	  	  			break;
	  	  		}
	  	  	}
		  	if(CodTipoLista.length() > 0) {
			 	CodTipoLista = CodTipoLista + "," + tipoListaCurr;
			}
		  	else {
			 	CodTipoLista += tipoListaCurr; 
		  	}
		}
	}
  }
  
  if (serviceRequest.containsAttribute("validaApprov")) {
	String statiMobRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoMob");
	CodStatoMob = StringUtils.split(statiMobRicerca, ",");
  }
  else {
	CodStatoMob = serviceRequest.getAttributeAsVector("codStatoMob");  
  }
  
  if(CodStatoMob.size() > 0) {
	  for(int i=0;i<CodStatoMob.size();i++) {
		  String codStatoCurr = CodStatoMob.get(i).toString().trim();
		  for (int j=0;j<sizeStatoLista;j++) {
  	  		SourceBean rowCurr = (SourceBean)rowsStatoListaSb.get(j);
  	  		String codiceStato = rowCurr.getAttribute("CODICE").toString().trim();
  	  		if (codiceStato.equalsIgnoreCase(codStatoCurr)) {
  	  			if (descStatoLista.equals("")) {
  	  				descStatoLista = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
  	  			}
  	  			else {
  	  				descStatoLista = descStatoLista + ", " + StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
  	  			}
  	  			break;
  	  		}
	  	  }
		  if(codStatiMob.equals("")){
			  codStatiMob += CodStatoMob.get(i);
		  }else{
			  codStatiMob = codStatiMob + ", " + CodStatoMob.get(i);
		  }
	  }
  }
       
  String datfineda=StringUtils.getAttributeStrNotNull(serviceRequest,"datfineda");
  String datfinea=StringUtils.getAttributeStrNotNull(serviceRequest,"datfinea");      
  String datinizioda=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
  String datinizioa=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");
  String datdomandada=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandada");
  String datdomandaa=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandaa");
  String _funzione=serviceRequest.getAttribute("CDNFUNZIONE").toString();
  String txtOut = "";
  
  if (!CodTipoLista.equals("")) {
	  txtOut += "Tipo lista <strong>"+ descTipoLista +"</strong>; ";  
  }
  if (!datdomandada.equals("")) {
  	txtOut += "Data domanda da <strong>"+ datdomandada +"</strong>; ";
  }
  if (!datdomandaa.equals("")) {
  	txtOut += "Data domanda a <strong>"+ datdomandaa +"</strong>; ";
  }
  if (!datinizioda.equals("")) {
  	txtOut += "Data inizio da <strong>"+ datinizioda +"</strong>; ";
  }
  if (!datinizioa.equals("")) {
  	txtOut += "Data inizio a <strong>"+ datinizioa +"</strong>; ";
  }
  if (!datfineda.equals("")) {
  	txtOut += "Data fine da <strong>"+ datfineda +"</strong>; ";
  }
  if (!datfinea.equals("")) {
  	txtOut += "Data fine a <strong>"+ datfinea +"</strong>; ";
  }
  if (!codStatiMob.equals("")) {
	  txtOut += labelStatoMob + " <strong>"+ descStatoLista +"</strong>; ";
  }
  if (!CodCPI.equals("")) {
	  txtOut += " Cpi comp.:<strong>" + descCPI + "</strong>; ";  
  }
  
%>

<html>
<head>
 <title>RISULTATO DELLA RICERCA</title>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

<script type="text/JavaScript">

function verificaApprovazione() {
    var cntIscr = 0;
    if (document.form1.provCRTApprov.value == "" && document.form1.regioneCRTApprov.value == "") {
		alert("Attenzione! Bisogna indicare l'ente, regione o provincia.");
      	return false;
	}
    
    if (document.form1.provCRTApprov.value != "" && document.form1.regioneCRTApprov.value != "") {
		alert("Attenzione! Bisogna indicare come ente la regione o la provincia.");
      	return false;
	}
	if(form1.checkboxmob!=undefined) {
		var lenChecks = form1.checkboxmob.length;
		if(lenChecks>1){
			for(i=0;i<lenChecks;i++) {
				if (form1.checkboxmob[i].checked) {
					cntIscr = cntIscr + 1;
				}
			}
		}
		else {
			if (form1.checkboxmob.checked) {
				cntIscr = cntIscr + 1;
			}
		}
		
		if (cntIscr > 0) {
			return true;
		}
		else {
			alert("Bisogna selezionare almeno una riga");
			return false;
		}
	}
	else {
		return false;
	}
}

function checkAllCheckboxMob(sel) {

	if(form1.checkboxmob!=undefined) {
		var lenChecks = form1.checkboxmob.length;
		
		if(lenChecks>1){
			for(i=0;i<lenChecks;i++) {
				form1.checkboxmob[i].checked = !form1.checkboxmob[i].checked;
			}
		}else{
			form1.checkboxmob.checked = !form1.checkboxmob.checked;
		}
	}
}

function aggiornaOperazione() {
	form1.OP_APPROVAZIONE.value = 'true';
}

function tornaAllaRicerca() {  
	// Se la pagina è già in submit, ignoro questo nuovo invio!
   	if (isInSubmit()) return;
  	url="AdapterHTTP?PAGE=MobilitaApprovazioneRicercaPage";
	url += "&CDNFUNZIONE="+"<%=_funzione%>";
  	setWindowLocation(url);
}

</script>

</head>

<body onload="checkError();rinfresca()" class="gestione">
<br/><p class="titolo">RISULTATO DELLA RICERCA SULLA MOBILITA'</p>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>
<af:form name="form1" action="AdapterHTTP" method="POST" onSubmit="verificaApprovazione()">
<%if ( (canApprova) && (sizePagina > 0) ) {%>
<table align="center">
	<tr>
		<td class="azzurro_bianco">	
			<table width="100%">
				<tr>	
					<td colspan="5">
						<input type="checkbox" value="" name="SelectAll" onclick="javascript:checkAllCheckboxMob(this.checked);"/>&nbsp;Seleziona/Deseleziona tutti
					</td>
				</tr>
				
				<tr>
				<td><%=titleDataCr %></td>
				<td><%=labelNumDelibera %></td>
				<td><%=labelRegioneCrt %></td>
				<td><%=labelProvinciaCrt %></td>
				<td><%=labelStatoMob %></td>
				</tr>
				
				<tr>
				<td>
				<af:textBox type="date" name="datApprovazione" title="Data approvazione" validateOnPost="true" 
						value="" size="10" maxlength="10" required="true"/>
				</td>
				<td>
				<af:textBox classNameBase="input" name="numCRTApprovazione" value=""
	               		size="15" maxlength="15" title="<%= labelNumDelibera%>"
	               		required="true" />
				</td>
				<td>
				<af:comboBox classNameBase="input" addBlank="true" name="regioneCRTApprov"
				    	multiple="false"
		            	moduleName="M_MobGetRegioni" />
				</td>
				<td>
				<af:comboBox classNameBase="input" addBlank="true" name="provCRTApprov"
				    	multiple="false"
		            	moduleName="M_MobGetProvince" />
				</td>
				<td>
				<af:comboBox classNameBase="input" addBlank="true" name="statoApprov"
					    multiple="false" required="true" title="<%= labelStatoMob %>"
			            moduleName="M_MO_STATO" />
				</td>
				</tr>
				 
			     <tr>
			       
			       <td align="center" colspan="5">
				    	<input class="pulsante" type="submit" name="validaApprov" value="Valida" onclick="aggiornaOperazione();"/>
			       </td>
			       
				</tr>
			</table>						
		</td>			
		<td>&nbsp;</td>
	</tr>
</table>
<%} %>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_ApprovazioneIscrizioneSospese"/>
</font>

<af:error/>
<af:list moduleName="M_MobilitaRicercaApprovazione" canDelete="0" configProviderClass="it.eng.sil.module.amministrazione.MobilitaApprovazioneListConfig"/>

<input type="hidden" name="PAGE" value="MobilitaApprovazioneRisultRicercaPage"/>
<input type="hidden" name="cdnfunzione" value="<%=_funzione%>"/>
<input type="hidden" name="datfineda" value="<%=datfineda%>"/>
<input type="hidden" name="datfinea" value="<%=datfinea%>"/>
<input type="hidden" name="datinizioda" value="<%=datinizioda%>"/>
<input type="hidden" name="datinizioa" value="<%=datinizioa%>"/>
<input type="hidden" name="datdomandada" value="<%=datdomandada%>"/>
<input type="hidden" name="datdomandaa" value="<%=datdomandaa%>"/>
<input type="hidden" name="CodCPI" value="<%=CodCPI%>"/>
<input type="hidden" name="CodTipoLista" value="<%=CodTipoLista%>"/>
<input type="hidden" name="codStatoMob" value="<%=codStatiMob%>"/>
<input type="hidden" name="OP_APPROVAZIONE" value="false"/>
</af:form>

<table class="main">
  <tr>
  	<td align="center">
  		<input type="button" class="pulsanti" value="Torna alla ricerca" onclick="tornaAllaRicerca();">
  	</td>
  </tr>
	
</table>

</body>
</html>