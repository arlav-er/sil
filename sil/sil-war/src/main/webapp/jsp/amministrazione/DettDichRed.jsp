<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                it.eng.sil.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                it.eng.sil.security.User,
                it.eng.sil.util.Linguette,
                it.eng.sil.util.*,
                it.eng.afExt.utils.*,
                java.math.BigDecimal,
                it.eng.afExt.utils.StringUtils, 
                it.eng.sil.util.InfCorrentiLav,
                it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>
                
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	boolean flag_competenza = true;
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String prgDichLav = (String) serviceRequest.getAttribute("prgDichLav"); 
	String cdnFunzione= (String)serviceRequest.getAttribute("CDNFUNZIONE");
	String strPopUp = serviceRequest.containsAttribute("POPUP")? serviceRequest.getAttribute("POPUP").toString():"";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

    Testata operatoreInfo   = null;
  
   	Object   cdnUtIns= "",
    	     dtmIns= "",
             cdnUtMod= "",
             dtmMod= "";
	
    Vector rowCpIComp  = serviceResponse.getAttributeAsVector("M_GETCPICORR.ROWS.ROW");
    if(rowCpIComp != null && !rowCpIComp.isEmpty()) {
      	SourceBean firstrow = (SourceBean) rowCpIComp.elementAt(0);
        String codCPItit  = (String) firstrow.getAttribute("CODCPITIT");
        String codmonotipocpi = Utils.notNull(firstrow.getAttribute("CODMONOTIPOCPI"));
        String codCpiUser = Utils.notNull(user.getCodRif());
        if (!codmonotipocpi.equals("C") || !codCPItit.equals(codCpiUser)){
			flag_competenza = false;
        }
    }
  	
  	if ((cdnLavoratore != null) && !cdnLavoratore.equals("")){
  		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	}
  
  	PageAttribs attributi = new PageAttribs(user, "DichRedDettaglioPage");
  	boolean canAnnulla = attributi.containsButton("SALVA") && flag_competenza;
  	boolean percorsoLavoratore = serviceRequest.containsAttribute("POPUP");
  
  	boolean canView=filter.canViewLavoratore();
  	if (!canView){
    	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	}
%>

<% 
    String ricercaGenerale = (String)serviceRequest.getAttribute("ricerca_generale");
    ricercaGenerale = ricercaGenerale==null?"":ricercaGenerale;
	SourceBean dichiarazione = (SourceBean)serviceResponse.getAttribute("M_DICHRED.ROWS.ROW");
    
    String codTipoDich = StringUtils.getAttributeStrNotNull(dichiarazione,"CODTIPODICH");
    String strNote =  StringUtils.getAttributeStrNotNull(dichiarazione,"strNote");
    String datInizio =  StringUtils.getAttributeStrNotNull(dichiarazione,"datInizio");
    String flgsupreddito =  StringUtils.getAttributeStrNotNull(dichiarazione,"flgsupreddito");
	String codstatoatto =  StringUtils.getAttributeStrNotNull(dichiarazione,"codstatoatto");
	String codmotivo = StringUtils.getAttributeStrNotNull(dichiarazione,"codmotivo");
	
    cdnUtIns       = dichiarazione.getAttribute("CDNUTINS");
    dtmIns         = dichiarazione.getAttribute("DTMINS");
    cdnUtMod       = dichiarazione.getAttribute("CDNUTMOD");
    dtmMod         = dichiarazione.getAttribute("DTMMOD");

    operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

    //Variabili per la gestione della protocollazione ================
	String prAutomatica     = null; 
	String estReportDefautl = null;
	BigDecimal numProt     = null;
	BigDecimal annoProt = null;
	BigDecimal prgDocumento = null;
	String     dataProt     = "";
	String     oraProt      = "";
	String     docInOrOut     = "";
	String     docInOrOutDecod = "";
	String     docRif       = "";
	boolean numProtEditable = false;
	Vector rowsPR           = null;
	SourceBean rowPR        = null;
	String     CODSTATOATTO = "";

	SourceBean resp = (SourceBean) serviceResponse.getAttribute("GetDichProtocollato.ROWS.ROW");
	if(resp != null) { 
		dataProt = (String) resp.getAttribute("DATAPROT");
		oraProt = (String) resp.getAttribute("ORAPROT");
		annoProt =  (BigDecimal) resp.getAttribute("NUMANNOPROT");
		numProt = (BigDecimal) resp.getAttribute("NUMPROTOCOLLO"); 
		CODSTATOATTO = (String) resp.getAttribute("CODSTATOATTO");
		docRif = (String) resp.getAttribute("RIF");
		docInOrOut = (String) resp.getAttribute("CODMONOIO");
		prgDocumento = (BigDecimal) resp.getAttribute("PRGDOCUMENTO");
	}
String disabilitaBottone = String.valueOf( (!canAnnulla)||percorsoLavoratore|| CODSTATOATTO.equals("AN"));

%>

<html>
<head>
	<script language="Javascript" src="../../js/docAssocia.js"></script>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/"/>
 <script language="Javascript">
  <%
       if (cdnLavoratore!=null)attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>

  function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}

function goAnnullaDic(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var motivazione=document.Form1.motivazione.options[document.Form1.motivazione.selectedIndex].value;
  if(motivazione==''){
  	alert('Il campo motivazione è obbligatorio');
  	return;
  }
  var _url = "AdapterHTTP?" + url +"&codStatoAtto="+document.Form1.statoAtto.options[document.Form1.statoAtto.selectedIndex].value+"&motivazione="+motivazione+"&FORZA_INSERIMENTO=false&CONTINUA_CALCOLO_SOCC=false";
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}

 function cambiaStato(){

 	var stato=document.Form1.statoAtto.options[document.Form1.statoAtto.selectedIndex].value;
 	divVar = document.getElementById('annullaDic');
	
 	if(stato=='AN'){
 		divVar.style.display = "inline";
 	}
 	else{
 		divVar.style.display = "none";
 	}
 }
 
</script>

<%if (strPopUp.equals("")) {%>
    <script language="Javascript">   
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
    </script>
   
   <%
   InfCorrentiLav testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   testata.setPaginaLista("LISTADICHREDPAGE");
//   testata.setSkipLista(true);
   testata.show(out);
   }
%>


</head>

<body class="gestione" onload="rinfresca()">
	<center>
        <af:form name="Form1" method="GET" action="">
        <input type="hidden" name="FORZA_INSERIMENTO" value="false">
		<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false">
        <table >
	       	<tr>
        		<td>
        			<br/>
                                     
        			<% //if ( !codstatoatto.equalsIgnoreCase("AN") ){ 
	        			out.print(htmlStreamTop);%>
	        			<table class="main">
	        				<% if ( !codstatoatto.equalsIgnoreCase("AN") ){ %>
	        				<tr>
	        					<td align="left">
	        						Stato atto
	        						<af:comboBox classNameBase="input" title="Stato dell'atto" name="statoAtto" selectedValue="<%=codstatoatto%>" addBlank="true" required="true" moduleName="StatoAtto" onChange="cambiaStato()" disabled="<%=disabilitaBottone%>"/> 
	        					</td>
	        					<td colspan="2">
	     							<div id="annullaDic" style="display: none">
	        							Motivazione
	        							<af:comboBox classNameBase="input" title="Motivazione" name="motivazione" addBlank="true" required="true" moduleName="Motivazione"/>        						
	        							<input type="button" value="Salva" class="pulsanti" onclick="goAnnullaDic('PAGE=ListaDichRedPage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>&prgDichLav=<%=prgDichLav%>&prgDocumento=<%=Utils.notNull(prgDocumento)%>&MODULE=AnnullaDichiarazione', 'FALSE')">
	        						</div> 	   							
	        					</td>
	        				</tr>
	        				<% } else if(codstatoatto.equalsIgnoreCase("AN")) {%>
	        				<tr>
	        					<td align="left">
	        						Stato atto
	        						<af:comboBox classNameBase="input" title="Stato dell'atto" name="statoAtto" selectedValue="<%=codstatoatto%>" addBlank="true" required="true" moduleName="StatoAtto" onChange="cambiaStato()" disabled="true"/> 
	        					</td>
	        					<td colspan="2">
	     							
	        							Motivazione
	        							<af:comboBox classNameBase="input" title="Motivazione" selectedValue="<%=codmotivo%>" name="motivazione" disabled="true" required="true" moduleName="Motivazione"/>        							        							
	        						   							
	        					</td>
	        				</tr>
	        				<% } %>
	        			</table>
	        			<table class="main">
							<tr>
					        	<td align="right">anno&nbsp;</td>
        						<td>
        							<af:textBox classNameBase="input" type="text" name="annoProt" value="<%=Utils.notNull(annoProt)%>"
                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" trim="false"/>
                                </td>
        						<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
        						<td>
        							<af:textBox classNameBase="input" type="text" 
        								name="numProt" value="<%=Utils.notNull(numProt)%>"
                                    	 readonly="true" title="Numero protocollo" size="8" 
                                    	 maxlength="100" trim="false"/>
                                </td>
        						<td class="etichetta2">data
				           			<af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" 
				                       maxlength="10" title="data di protocollazione" classNameBase="input" 
				                       readonly="true" required="false" trim="false"/>
				                </td>
				                <%if (ProtocolloDocumentoUtil.protocollazioneLocale()) {%>
							    <td class="etichetta2">ora
							    	<af:textBox name="oraProt" type="text" value="<%=oraProt%>" size="6"  maxlength="5"
							                       title="data di protocollazione" classNameBase="input" readonly="true"
							                        required="false" trim="false"/>
							    </td>
							    <%} else {%>
							    <td><input type="hidden" name="oraProt" value="00:00">
							    <%} %>
    						</tr>
	        			</table>
	        			<table class="main">
			    			<tr>
			    				<td class="etichetta2">Doc. di</td>
			    				<td>
			    					<table cellpadding="0" cellspacing="0" border="0" width="100%">
			        					<tr>
			        						<td class="campo2">
												<af:textBox name="docInOrOut" type="text" value="<%=docInOrOut%>" size="<%=10%>"
							                           title="data di protocollazione" classNameBase="input" readonly="true"
							                           validateOnPost="false" required="false" trim ="false"/>
								            </td>
			            					<td class="etichetta2">Rif.</td>
			            					<td class="campo2">
			            						<af:textBox name="rif" value="<%=docRif%>" size="<%=60%>"
							                           title="riferimento" classNameBase="input" readonly="true"
							                           validateOnPost="false" required="false" trim ="false"/>
							                </td>
			        					</tr>
			        				</table>
			    				</td>
			    			</tr>
						</table>
	        			<%out.print(htmlStreamBottom);
	        	//	}
	        	%> 
    			   
        		</td>
        	</tr>
        	
        	<tr><td align="left">
            <table>
	            <tr>
                    <td class="etichetta2" style="width:300;">Il lavoratore dichiara il : 
                    <td  class="etichetta2">mancato superamento del limite<td><input type="radio" name="limite" value="inf" <%= (codTipoDich.equals("DGRN") || codTipoDich.equals("DDRN"))?"checked":""%> disabled="true">
                    <td  class="etichetta2">superamento del limite<input type="radio" name="limite"  value="sup" <%= (codTipoDich.equals("DGRS") || codTipoDich.equals("DDRS"))?"checked":""%> disabled="true">
                </tr>
            </table>
            <tr><td align="left">
            <table>
                <tr>
                    <td class="etichetta">tipo dichiarazione: 
                    <td class="etichetta">Dettaglio<td><input type="radio" name="tipoDichiarazione" value="dett" <%= (codTipoDich.equals("DDRS") || codTipoDich.equals("DDRN"))?"checked":""%> disabled="true">
                    <td class="etichetta">Generica<input type="radio" name="tipoDichiarazione" value="gen" <%= (codTipoDich.equals("DGRS") || codTipoDich.equals("DGRN"))?"checked":""%> disabled="true">
                </tr>
            </table>
            <tr><td align="left">
            <table>
            	<tr>
            		<td class="etichetta">Data dichiarazione
            		<td class="campo"><af:textBox name="datSitSanata" value="<%=datInizio%>" readonly="true" 
                    		 type="date" classNameBase="input" size="11" maxlength="10"   required="true" />
            	</tr>
            </table>
         </table>
        <%out.print(htmlStreamTop);%>
        <af:list moduleName="M_DETTDICHRED" />
        <table>
          <tr>
		    <td>&nbsp;</td>
		    <td>&nbsp;</td>
		    <td align="left" style="vertical-align:top">note&nbsp;&nbsp;</td>
          	<td align="left"><af:textArea cols="80" rows="5" name="strNote" readonly="true" maxlength="1000" value="<%=strNote%>"/></td>
          </tr>
        </table>
        <table>
	       <tr>     
		<%if (!strPopUp.equals("")){%>	       
			<td align="center">
	            <input type="button" name="chiudi"  value="Chiudi" id="chiudi" class="pulsanti" onclick="window.close();">
	        </td>		
		<%}%>
	       </tr>

          <tr>
               		<td colspan="9" align="left">
		                N.B.: il reddito sanato viene valorizzato a zero in caso di dichiarazione generica di mancato superamento.
		                <br/>
		                N.B.: il reddito sanato viene valorizzato a nullo in caso di dichiarazione generica di superamento.
	                </td>
               </tr>
            </table>
	    <p align="center">
		<% operatoreInfo.showHTML(out); %>
	    </p>
        <%out.print(htmlStreamBottom);%>        
        </af:form>
	</center>


</body>
</html>
