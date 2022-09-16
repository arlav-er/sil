<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.User,
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
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%
	
	String  titolo = "Nuovo Soggetto Accreditato";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	//int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	//int cdnfunzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");

	

	// CONTROLLO ACCESSO ALLA PAGINA
	/* Commentato per test
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	*/

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	
	
	

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	
	
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String strCodCpi;
	strCodCpi =  user.getCodRif();
	//Solamente per testare ricordare di eliminare la riga
	canModify=true;
	
	String strCap="";
    String strCom="";
    String codCom="";
    
    String cfSel=" ";
	 String descComuniSel=" ";
	 String denominazioneSel="";
	 String codComune="";
	 String tipoRicerca="";
	 
	   cfSel  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cfSel");
	   if (StringUtils.isEmpty(cfSel)){
		   cfSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "cfSel");
	   }
	   //alert(cpi);
	   descComuniSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "descComuniSel");
	   if (StringUtils.isEmpty(descComuniSel)){
		   descComuniSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "descComuniSel");
	   }
 		
	   denominazioneSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "denominazioneSel");
	   if (StringUtils.isEmpty(denominazioneSel)){
		   denominazioneSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "denominazioneSel");
	   }
 		
	   codComune=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codComuneSel");
	   if (StringUtils.isEmpty(codComune)){
		   codComune= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "codComuneSel");
	   }
	   
	   tipoRicerca=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "tipoRicerca");
	   if (StringUtils.isEmpty(tipoRicerca)){
		   tipoRicerca= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "tipoRicerca");
	   }
	   
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
		String token = "_TOKEN_" + "ListaPage";
		String goBackInf = "Torna alla lista";
		String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
		if (StringUtils.isEmpty(goBackUrl)) {
			goBackInf = "Torna alla lista";
			goBackUrl = "PAGE=VisualizzaSogAccVoucherPage&cdnfunzione=" + cdnfunzioneStr;
			goBackUrl +="&cfSel="+cfSel;
			goBackUrl +="&denominazioneSel="+denominazioneSel;
			goBackUrl +="&descComuniSel="+descComuniSel;
			goBackUrl +="&codComuneSel="+codComune;
			goBackUrl +="&tipoRicerca="+tipoRicerca;
		}
	  
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>
<%@ include file="../coll_mirato/CM_CommonScripts.inc" %>


<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		goTo("<%= goBackUrl %>");
	}

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

	function callSalvaSoggettoAcc(){
 
		   if (!isRequired('codiceFiscaleTx') ||  !checkCFAz("codiceFiscaleTx")) {
				return false;
			}
		   
		   if(!isRequired("sede")){
			   return false;
		   }
		  
		   var url = "AdapterHTTP?PAGE=MakeSoggettoAccreditatoPage" 
		   url += "&cfSel="+document.form.codiceFiscaleTx.value;
	   	   url += "&denominazioneSel="+document.form.denominazioneTx.value;
	   	   url += "&codSedeSel="+document.form.sede.value;
	   	   url += "&codComuneSel="+document.form.codCom.value;
	   	   url += "&indirizzoSel="+document.form.indirizzo.value;
	   	   url += "&descComuneSel="+document.form.strCom.value;
	   	
		//alert("Il valore di url "+url);
	    setWindowLocation(url);
	}

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">



<p class="titolo"><%= titolo %></p>
<font color="red">
<af:showErrors />
</font>
<font color="green">
<af:showMessages  prefix="" />
<af:showMessages />
</font>



	<af:form name="form" action="AdapterHTTP" method="POST">

	<input type="hidden" name="PAGE" value=""/>
	<input type="hidden" name="codiceFiscale" value="<%= cfSel %>"/>
	<input type="hidden" name="desComune" value="<%= descComuniSel %>"/>
	<input type="hidden" name="denominazione" value="<%= denominazioneSel %>"/>
	<input type="hidden" name="codComune" value="<%= codComune %>"/>
	<input type="hidden" name="tipoRicerca" value="<%= tipoRicerca %>"/>
	
	
	
	<%= htmlStreamTop %>
	<table class="main">
	


	<%--
		Campi  degli accreditamenti 
	--%>
	<%-- ***************************************************************************** --%>
	<p align="center">
	<tr>
			
	<td class="campo">
			<p align="center">
			<table   border="0"  align="center"  >
			
		   	 <tr><td colspan="4"/><br/>Soggetto Accreditato</td></tr>
		   	 <tr><td colspan="4"/>&nbsp;</td></tr>
			  <tr>
			  <td  class="etichetta"  align="right">
					Codice Fiscale
				</td>
			    <td   class="campo" align="left">
			    	<af:textBox title="Codice Fiscale" classNameBase="input" name="codiceFiscaleTx" 
			    		type="text" readonly="false" size="16" value="" 
			    		maxlength="16" required="true" />
			   </td>
				<td  class="etichetta"  align="left">	
					Denominazione
					<af:textBox title="Denominazione" classNameBase="input" name="denominazioneTx" 
					type="text" readonly="false" size="60" value="" maxlength="100" />
				 </td>
				 <td  class="campo"  align="left">
					&nbsp;
				</td>
			</tr>
		   <br/>
		    <tr><td colspan="4"/>&nbsp;</td></tr>
		   <tr>
				<td  class="etichetta" " align="right">
				Sede
				</td>
				<td class="campo" width="5%" align="left">
					<af:textBox classNameBase="inputUpper" name="sede" title="Sede"
					type="text" readonly="false" size="8" value="" required="true"
					maxlength="8" />
				</td>
				<td  class="etichetta" " align="left">	
					Indirizzo&nbsp;
					<af:textBox title="Indirizzo" classNameBase="input" name="indirizzo"
						 type="text" readonly="false" size="60" value=""  maxlength="60"/>
				</td>
				 <td  class="campo"  align="left">
					&nbsp;
				</td>
				
		   </tr>
			<br/>
		    <tr><td colspan="4"/>&nbsp;</td></tr>
		   <tr>
		 		<td class="etichetta" width="10%">Comune&nbsp;</td>
    			<td class="campo" width="5%">
    							<af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.form.codCom, document.form.codComHid, document.form.strCom, document.form.strComHid, document.form.strCap, document.form.strCapHid, 'codice');" 
                                  type="text" name="codCom" value="<%=codCom%>" title="codice comune "
                                  size="4" maxlength="4" validateWithFunction="" readonly="false"/>&nbsp;
   
    								<A HREF="javascript:btFindComuneCAP_onclick(document.form.codCom, 
                                                document.form.strCom, 
                                                document.form.strCap, 
                                                'codice','',null,'');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                                 
                                 
                  </td>
                                    
    				<td class="campo" >
    							<af:textBox type="hidden" name="codComHid" value="<%=codCom%>"/>
    							<af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.form.codCom, document.form.codComHid, document.form.strCom, document.form.strComHid, document.form.strCap, document.form.strCapHid, 'descrizione');"
               					 	type="text" name="strCom" value="<%=strCom%>" size="50" maxlength="50" title="descrizione comune "
                					inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                					readonly="false"/>
    					</td>
    					<td class="campo"  align="center">
    							<A HREF="javascript:btFindComuneCAP_onclick(document.form.codCom, 
                                                document.form.strCom, 
                                                document.form.strCap, 
                                                'descrizione','',null,'');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
   						</td>
   						<td class="campo" >
   							<af:textBox type="hidden" name="strComHid" value="<%=strCom%>" />
					    	<af:textBox type="hidden" name="strCap" value="<%=strCap%>" />
					    </td>
   						
		  </tr>
		  
		   </table>
		   </p>
  	<br/>
 	</td>
	</tr>
		<table >
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
            	<input type="button" class="pulsante" name="back" value="<%= goBackInf %>" onclick="goBack()" />
            	&nbsp;
              	<input class="pulsanti" type="button" name="salva" value="Salva"   onclick="callSalvaSoggettoAcc()"  />
             
            </td>
          </tr>
        </table>
	</tr>
	 </p>
	 </table>
	<%= htmlStreamBottom %>
	
</af:form>
</body>
</html>	



