<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	
	boolean canView = filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	PageAttribs attributi = new PageAttribs(user, _page);

 
 	String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");

 
 String strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
 String strCognomeLav = StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
 String strNomeLav = StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
 String dataEventoDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataEventoDa");
 String dataEventoA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataEventoA");
 String statoInvio = StringUtils.getAttributeStrNotNull(serviceRequest, "statoInvio");
 String tipoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoDomanda");
 String codStatoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoDomanda");
 String tipoEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoEvento");

 String ordDataEventoDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataEventoDC");
 String ordDataEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataEvento");
 String ordINPSDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordINPSDC");
 String ordINPS = StringUtils.getAttributeStrNotNull(serviceRequest, "ordINPS");
 String ordDataInvioDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataInvioDC");
 String ordDataInvio = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataInvio");
 String ordCF = StringUtils.getAttributeStrNotNull(serviceRequest, "ordCF");
 
 String isComeBack = StringUtils.getAttributeStrNotNull(serviceRequest, "isComeBack");
 //if(StringUtils.isEmptyNoBlank(isComeBack)){
	 ordDataEvento = "si";
	 ordDataEventoDC ="D";
	 ordINPS = "si";
	 ordINPSDC ="C";
	 ordDataInvio = "si";
	 ordDataInvioDC ="D";
	 ordCF ="si"; 
	 codStatoDomanda = "AC";
// } 
%>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	  String fDelLav = "";
%>
<html>
<head>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

	<script language="Javascript" src="../../js/documenti/lookEntityDoc.js"></script>


     <script language="Javascript">
     
     function resetCampi() {
    	 document.Frm1.reset(); 
    	  document.Frm1.cdnLavoratore.value="";
		 document.Frm1.tipoEvento.value="";
    	 document.Frm1.tipoDomanda.value="";  
    	 try {
	    	 document.Frm1.statoInvio[2].checked=true;
    	}catch(error) {}
     }
     function azzeraLavoratore(){
         document.Frm1.codiceFiscaleLavoratore.value = "";
           document.Frm1.cognome.value = "";
           document.Frm1.nome.value = "";
           document.Frm1.cdnLavoratore.value = "";
     }
	     
	     function gestisciOrdinamento(obj){
	    	 if(!obj.checked){
		    	 var nameRadio = obj.name+"DC";
		    	 var radios =  document.getElementsByName(nameRadio);
		    	 var i;
		    	 for (i = 0; i < radios.length; i++) {
		    		 radios[i].checked = false;
		    	 }
	    	 }
	     }
     
     	function controllaCampi() {
			var dataEventoDa = document.Frm1.dataEventoDa.value;
			var dataEventoA = document.Frm1.dataEventoA.value;
				
			var checkDate =  confrontaDate(dataEventoDa, dataEventoA);
			if(checkDate<0){
				alert("Data evento da maggiore di Data evento a"); 
				document.Frm1.dataEventoDa.focus();
			    return false;
			}
			return true;
   		}

</script>	    
</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca eventi di condizionalità</p>
<p align="center">
<af:form action="AdapterHTTP" method="POST"	name="Frm1" onSubmit="controllaCampi()">
	<%out.print(htmlStreamTop);%>
	
	<table class="main">
		        <!-- sezione lavoratore -->
	        <tr class="note">
			    <td colspan="2">
				<div class="sezione2">Lavoratore&nbsp;&nbsp;
					<a href="#" onClick="apriSelezionaLavoratore();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
					&nbsp;<a href="#" onClick="javascript:azzeraLavoratore();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
					
				</div>
			</td>
			</tr>
			<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />

			<%@ include file="lookLavoratore.inc" %>
	  	<tr><td colspan="2"><hr width="90%"/></td></tr>
	  	<tr>
					<td class="etichetta">Data evento da</td>
					<td class="campo"><af:textBox validateOnPost="true"
							type="date" name="dataEventoDa" title="Data evento da"
							value="<%=dataEventoDa%>" size="10" maxlength="10" />
						&nbsp;&nbsp;a&nbsp;&nbsp; <af:textBox validateOnPost="true"
							type="date" name="dataEventoA" title="Data evento a"
							value="<%=dataEventoA%>" size="10" maxlength="10" /></td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Tipo evento
	        </td>
			<td class="campo">
			   <af:comboBox name="tipoEvento" classNameBase="input"  addBlank="true"   title="Tipo evento" 
                  		moduleName="M_ComboCondizionalita" selectedValue="<%=tipoEvento%>"
               />
	 		</td>
		</tr> 
 		<tr>
			<td class="etichetta">Stato invio</td>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      			<%if (statoInvio.equals("D")) {%>
		       				<td>
		       					<input type="radio" name="statoInvio" value="I"/> Inviato&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="statoInvio" value="D" CHECKED /> Da inviare&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="statoInvio" value="" /> Tutti gli stati
		       				</td>
		      			<%} else if (statoInvio.equals("I"))  {%>
		       				<td>
		       					<input type="radio" name="statoInvio" value="I" CHECKED /> Inviato&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="statoInvio" value="D" /> Da inviare&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="statoInvio" value="" /> Tutti gli stati
		       				</td>
		       			<%} else {%>
		       				<td>
		       					<input type="radio" name="statoInvio" value="I" /> Inviato&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="statoInvio" value="D" /> Da inviare&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="statoInvio" value="" CHECKED /> Tutti gli stati
		       				</td>
		      			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	
   
	 <tr>
	        <td class="etichetta">
	       		Tipo Domanda
	        </td>
			<td class="campo">
			   <af:comboBox name="tipoDomanda" classNameBase="input"  addBlank="true"   title="Tipo Domanda" 
                  		moduleName="M_ComboCondizionalitaNoTabella" selectedValue="<%=tipoDomanda%>"
               />
	 		</td>
		</tr>
			 
		<tr><td colspan="2"><hr width="90%"/></td></tr>
	   
		<tr>
			<td class="etichetta" rowspan="5">Ordinamento per</td>
		</tr>	
		<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      		<td>
		      		<input type="checkbox" name="ordDataEvento" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordDataEvento.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Data evento
					</td>			
		      			<%if (ordDataEventoDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordDataEventoDC" value="D"/> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataEventoDC" value="C" CHECKED /> crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordDataEventoDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordDataEventoDC" value="D" CHECKED /> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataEventoDC" value="C" /> crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else{%>
		       				<td>
		       					<input type="radio" name="ordDataEventoDC" value="D" /> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataEventoDC" value="C" /> crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	
	<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr><td>
		      		<input type="checkbox" name="ordDataInvio" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordDataInvio.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Data invio
				</td>			
		      		
		      			<%if (ordDataInvioDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordDataInvioDC" value="D"/> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataInvioDC" value="C" CHECKED /> crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordDataInvioDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordDataInvioDC" value="D" CHECKED /> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataInvioDC" value="C" /> crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else  {%>
		       				<td>
		       					<input type="radio" name="ordDataInvioDC" value="D" /> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataInvioDC" value="C" /> crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	        
		<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr><td>
		      		<input type="checkbox" name="ordINPS" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordINPS.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Prot. INPS
				</td>			
		      		
		      			<%if (ordINPSDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordINPSDC" value="D"/> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordINPSDC" value="C" CHECKED /> crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordINPSDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordINPSDC" value="D" CHECKED /> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordINPSDC" value="C" /> crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else  {%>
		       				<td>
		       					<input type="radio" name="ordINPSDC" value="D" /> decrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordINPSDC" value="C" /> crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>
		<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr><td>
		      		<input type="checkbox" name="ordCF" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordCF.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Codice fiscale
				</td>			
		        	</tr>
		        </table>
		    </td>
		</tr>		
		<tr><td colspan="2">&nbsp;</td></tr>
	    <tr>
	    	<td colspan="2" align="center">
		    	<input class="pulsanti" type="submit" name="cerca" value="Cerca" />
		    	&nbsp;&nbsp;
			  <input type="reset" class="pulsanti" value="Annulla" onclick="resetCampi();"/>		
		        
	        </td>
	    </tr>
	    <input type="hidden" name="PAGE" value="ListaRicercaCondizionalitaPage"/>
	    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>

  
		</table>
	</af:form>
	<%out.print(htmlStreamBottom);%>
	
	</body>
</html>