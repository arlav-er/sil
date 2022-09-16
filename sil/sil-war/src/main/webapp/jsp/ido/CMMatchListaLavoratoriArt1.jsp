<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	ProfileDataFilter filter = new ProfileDataFilter(user, "CMMatchDettGraduatoriaPage");
      
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else{


	    PageAttribs attributi = new PageAttribs(user, "CMMatchDettGraduatoriaPage");
		
	    String cdnFunzione=(String)serviceRequest.getAttribute("CDNFUNZIONE");
	    String cdnStatoRich=(String)serviceRequest.getAttribute("CDNSTATORICH");
	  	String prgRosa =(String)serviceRequest.getAttribute("PRGROSA");
	    String prgTipoRosa =(String)serviceRequest.getAttribute("PRGTIPOROSA");
	  	String prgRichiestaAz=(String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
	  	String prgTipoIncrocio=(String)serviceRequest.getAttribute("PRGTIPOINCROCIO");
	  	String prgIncrocio=(String)serviceRequest.getAttribute("PRGINCROCIO");
	  	String prgAzienda=(String)serviceRequest.getAttribute("PRGAZIENDA");
 	  	String prgUnita=(String)serviceRequest.getAttribute("PRGUNITA");         	  	
	  	String codCpi=(String)serviceRequest.getAttribute("CODCPI");
	  	String concatenaCpi=(String)serviceRequest.getAttribute("CONCATENACPI");   
				
	    String strErrorCode = "";
	    String msgConferma = "";
	    boolean confirm = false;   
	    	   
	    //Gestione cache e recupero eventuali lavoratori già checkati
		NavigationCache checkedArt1Cache = (NavigationCache) sessionContainer.getAttribute("CMMatchLavoratoriArt1ModuleCACHE");
		Vector checkedArt1Vector = new Vector();
		if (checkedArt1Cache != null) {
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
			
	    	String cdnLavoratoreCache = (String) checkedArt1Cache.getField("CDNLAVORATORE"); 
	    
			if (!cdnLavoratore.equals(cdnLavoratoreCache)) {
				//Cache incoerente, la cancello e la ricreo
				sessionContainer.delAttribute("CMMatchLavoratoriArt1ModuleCACHE");
				String[] fields = {"CHECKBOXART1", "CDNLAVORATORE"};
				NavigationCache newCache = new NavigationCache(fields);
				newCache.enable();
				sessionContainer.setAttribute("CMMatchLavoratoriArt1ModuleCACHE", newCache);						
			} else  {
				//Cache coerente, recupero dati
				Object checkedArt1Obj = checkedArt1Cache.getField("CHECKBOXART1");  
				if (checkedArt1Obj != null) {
					if (checkedArt1Obj instanceof Vector) {
						checkedArt1Vector = (Vector) checkedArt1Obj;
					} else if (!"EMPTY".equalsIgnoreCase(checkedArt1Obj.toString())) {
						checkedArt1Vector.addElement(checkedArt1Obj.toString());
					}
				}
			}
		} else {
			//Cache inesistente, la creo
			String[] fields = {"CHECKBOXART1", "CDNLAVORATORE"};
			NavigationCache newCache = new NavigationCache(fields);
			newCache.enable();
			sessionContainer.setAttribute("CMMatchLavoratoriArt1ModuleCACHE", newCache);
		}
		
		Iterator iterCheckedArt1 = checkedArt1Vector.iterator();   				
		
		boolean canModify = true;

		String htmlStreamTop = StyleUtils.roundTopTable(canModify);
		String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<html>
<head>
<title>Lista lavoratori Art. 1</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script type="text/javascript">
		<!--
		//Array con i valori delle checkbox che erano già checkate precedentemente  
    	var checkedArt1List = new Array(<%
    		boolean firstElement = true;
    		while (iterCheckedArt1.hasNext()) {
    			if (!firstElement) {out.print(", ");}
    			else {firstElement = false;}
    			out.print("\"" + (String) iterCheckedArt1.next() + "\"");
    		}
    		%>);
    	
    	
    	//Variabile che indica il numero di checkbox correntemente selezionate	
		var checkNumber = 0;
		
    	//funzione che imposta i valori iniziali delle checkbox scorrendo la form contenuta nella pagina
    	function initCheckboxArt1() {
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];
    			if (checkbox.name == "CHECKBOXART1") {	    			      			     
    				for (var i = 0; (i < checkedArt1List.length) && (!checkbox.checked); i++) {
    					if (checkedArt1List[i] == checkbox.value) {    					   
    						checkbox.checked = true;
    						checkNumber += 1;
    					}
    				} 
    			}
    		}
    		gestisciCampoEmpty();
    	}
    	
    	
    	//Funzione che gestisce tutte le checkbox della pagina
    	function checkAllCheckboxArt1(checked) {
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];    		
    			if (checkbox.name == "CHECKBOXART1") {
    				if (checkbox.type == "checkbox") {     					   				    
    					//Controllo se la checkbox è diversa dalla selezione
    					if (checkbox.checked != checked) {
    						//Modifico la checkbox e aggiorno il contatore    						
    						checkbox.checked = checked;
    						//checkbox.onclick();
    						if (checked) {
    							checkNumber += 1;
    						} else 	{
    							checkNumber -= 1;	
    						}
    					}
    				}
    			}
    		}
    		gestisciCampoEmpty();    		
    	}    
    	
    	//Gestione del campo empty della pagina per l'aggiornamento della cache di navigazione
    	function gestisciCampoEmpty() {
    		document.getElementById("empty").disabled = (checkNumber > 0);
    	}
    	
    	function checkLav() {
	    	var prgLavChk = "";
    		for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];    			
    			if (checkbox.name == "CHECKBOXART1") {
    				if (checkbox.checked) {
    				    if (prgLavChk != "") {
    				    	prgLavChk += "@" + checkbox.value;
    				    }
    				    else {
    				    	prgLavChk = checkbox.value;
    				    }
    				}
    			}
    		} 
    		document.Frm1.CKBOX_ART1.value = prgLavChk;
    	}
    	
    	function associa() {
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
   			if (isInSubmit()) return;
			
			checkLav();											 				  	 
			
			if( document.Frm1.CKBOX_ART1.value == "" ) {
		    	alert("Selezionare almeno un lavoratore!");
		      	return false;
		    }	  	  		      
		    
		    //var url = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage&MODULE=CMCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
		    //url = url+ "&CDNSTATORICH=<%=cdnStatoRich%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>";
		    //url = url+ "&PRGUNITA=<%=prgUnita%>&CODCPI=<%=codCpi%>&CONCATENACPI=<%=concatenaCpi%>&CDNFUNZIONE=<%=cdnFunzione%>";		
			//window.opener.location.replace(url);
		    
		    //window.close();
		    addLavoratori(document.Frm1.CKBOX_ART1.value, document.Frm1.PRGROSA.value);
		      
		    return true;	  
		}
		
		
		function addLavoratori(cdnLavoratoreLista, prgRosa)   {
			opener.matchAggiungiLavoratore(cdnLavoratoreLista, prgRosa);  	
		}		
		    	
       -->    	  	
	 </script>

</head>

<body onload="checkError();" class="gestione">
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="MODULE" value="CMAssociaLavoratoriArt1Module"/>
<input name="P_CDNUTENTE" type="hidden" value="<%=user.getCodut()%>"/>
<input name="P_CDNGRUPPO" type="hidden" value="<%=user.getCdnGruppo()%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>"/>  
<input type="hidden" name="PAGE" value="CMMatchLavoratoriArt1Page"/>
<input type="hidden" name="CKBOX_ART1" value=""/>
	<br/>
	<af:error />	
	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">	
				<table width="100%">
					<tr>	
						<td nowrap>
							<input type="checkbox" value="" name="SelectAll" onclick="javascript:checkAllCheckboxArt1(this.checked);"/>&nbsp;Seleziona/Deseleziona tutti
							<input id="empty" type="hidden" value="EMPTY" name="CHECKBOXART1"/>
							&nbsp;&nbsp;
						<td>
						<td align="left" width="100%">
							&nbsp; 
							<input type="image" src="../../img/add.gif" border="0" value="Associa" onclick="return associa();">
							Associa selezionati &nbsp;&nbsp;
						</td>		
					</tr>
				</table>						
			</td>			
			<td>&nbsp;</td>
		</tr>
	</table> 
<%
    SourceBean row = null;
    Vector rows = serviceResponse.getAttributeAsVector("M_StrTipoLista.ROWS.ROW");
    String titoloLista = "";
    if (rows.size()==1) {
       row = (SourceBean)rows.get(0);
       titoloLista = Utils.notNull(row.getAttribute("strdescrizione"));
       if(!titoloLista.equals("")){
       		titoloLista = " - " + titoloLista;
       }
    }
%>   

	<table class="main">
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td align="center">
				<p class="titolo">Elenco Lavoratori iscritti all'art.1<%=titoloLista%></p>
			</td>
		</tr>
	</table>
	
	<af:list moduleName="CMMatchLavoratoriArt1Module" />
	
	<table class="main">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td align="center"><input type="button" class="pulsanti" value="Chiudi" onClick="window.close()"></td>
		</tr>
	</table>	
	<br>       		
</af:form>
<SCRIPT language="javascript">
	initCheckboxArt1();
</SCRIPT>
</body>
</html>
<% } %>
