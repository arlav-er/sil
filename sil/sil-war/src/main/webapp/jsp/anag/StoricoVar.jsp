<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.text.SimpleDateFormat,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  it.eng.sil.util.GiorniNL,
                  it.eng.sil.module.agenda.*,
                  java.util.*,
                  java.math.*,
                  it.eng.sil.security.User,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;

  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  Testata operatoreInfo = null;
  String NUMKLORICHESONDIS = null;
  String canModifyRich = null;

  String  prgAzienda = null;	   
  String  numDisabili = "";
  String  DATINIZIOVALIDITA = null;
  String  NUMBASECOMPUTO = "";
  String  prgRichEsonero = null;	     
  String  PRGRICHESONDISABILI = null;  
  String codMonoCategoria = null;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 


  PageAttribs attributi = new PageAttribs(user, _page); 

  boolean canModify= attributi.containsButton("aggiorna");
  boolean canDelete= attributi.containsButton("RIMUOVI");
  boolean canInsert= attributi.containsButton("INSERISCI"); 

  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify ? "false" : "true"; 


  String paginaNuovaVar = StringUtils.getAttributeStrNotNull(serviceRequest,"paginaNuovaVar");

  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
      
  prgRichEsonero = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgRichEsonero");
  
  canModifyRich = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"canModifyRich");
  
  //NUMBASECOMPUTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"NUMBASECOMPUTO");
  
  String NUMPERCESONERO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"NUMPERCESONERO");
  
  //numDisabili = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"numDisabili"); 

  if(canModifyRich.equals("true")){
  	  canModify = false;
  }
  
  /*--------------------------------------------------------------------------------------------------------------*/    

  
  if(paginaNuovaVar.equals("1")){ 

	  prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	  DATINIZIOVALIDITA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATINIZIOVALIDITA");
	  PRGRICHESONDISABILI = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGRICHESONDISABILI");  
	  codMonoCategoria = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codMonoCategoria");  
      //numDisabili = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"numDisabili");    

	  cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
  }

  String codStatoRich = "";
  String datFineSosp = "";
  String datInizioSosp = "";
  SourceBean richEsonero = (SourceBean) serviceResponse.getAttribute("M_Load_RichEsonero.ROWS.ROW");	
  if (richEsonero != null) {	
	codStatoRich = richEsonero.getAttribute("CODSTATORICHIESTA") == null? "" : (String)richEsonero.getAttribute("CODSTATORICHIESTA");		
	datInizioSosp = richEsonero.getAttribute("DATINIZIOVALIDITA") == null? "" : (String)richEsonero.getAttribute("DATINIZIOVALIDITA");
  	datFineSosp = richEsonero.getAttribute("DATFINE") == null? "" : (String)richEsonero.getAttribute("DATFINE");
  }
  		
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE"));

    
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>

<html>

<head>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    
    <af:linkScript path="../../js/"/>

	<script language="JavaScript">
          
          var flagChanged = false;
          var flagDisEffChanged = false;
        
          function fieldChanged() {
          	var readOnly = <%=readOnlyStr%>;
          	if (!readOnly){ 
            	flagChanged = true;
           	} 
          }
          
          function fieldDisEffChanged() {
          	var readOnly = <%=readOnlyStr%>;
          	if (!readOnly){ 
            	flagDisEffChanged = true;
           	} 
          }          

		  function isNumeric(val){
		  	  return(parseFloat(val,10)==(val*1));
		  }

		  function numeroDisabili(numBaseComputo, categoria){
	  	  	 
	  	  	 var numPercEsonero = '<%=NUMPERCESONERO%>';
	  	  	 var numDis;
	  	  	 var numQuota;
			  
			  /*
			  //vecchia versione si presume la fascia di appartenenza dalla quota
			  if(numBaseComputo < 15){
			  	  numQuota = 0;
			  }			  
			  if(numBaseComputo >= 15 && numBaseComputo <= 35){
			  	  numQuota = 1;
			  }
			  if(numBaseComputo >= 36 && numBaseComputo <= 50){
			  	  numQuota = 2;			  	  
			  }
			  if(numBaseComputo > 50){
				  numQuota = roundInt((numBaseComputo/100)*7);
			  }
              */
              // nuova versione: viene calcolata sempre il 7%
              
              if(categoria == "C"){
			  	numQuota = 1;
			  }
			  else if(categoria == "B"){
			  	numQuota = 2;			  	  
			  }
			  else {
              	//numQuota = roundInt((numBaseComputo/100)*7);
              	numQuota = ((numBaseComputo/100)*7);
              }                            
              
              // uguale alla procedura del riepilogo prospetto
              p_numEsonero = (numQuota/100)*numPercEsonero;              
              p_gap = Math.ceil(p_numEsonero) - p_numEsonero;
              if (p_gap > 0.5) {
              	numDis = Math.round((numQuota/100)*numPercEsonero);
              }
              else{ 
              	numDis = Math.ceil(p_numEsonero);  
              }

              //numDis = roundInt((numQuota/100)*numPercEsonero);
			  
		  	  return numDis;
		  }
		  
		  function roundInt(numero) {
		  	  if( (numero - parseInt(numero)) > 0.5 ){
			  	  numero = parseInt(numero) + 1;
			  }
		  	  else{	  
			  	  numero = parseInt(numero);
			  }
			  return numero;		  	
		  }
		  		  
		  function calcolaNumDisabili() {
	  	  	  var numBaseComputo = document.Frm1.NUMBASECOMPUTO.value;
	  	  	  var categoria = document.Frm1.codCategoria.value;
	  	  	  
	  	  	  if (categoria == "" || categoria == null) {
		  	  	categoria = "A";
	  	  	  }
	  	  	  	  	  	  
			  if(isNumeric(numBaseComputo)){
				  document.Frm1.numDisabili.value = numeroDisabili(numBaseComputo,categoria);
				  if (!flagDisEffChanged) {
				  	document.Frm1.numDisEff.value = numeroDisabili(numBaseComputo,categoria);
				  }
			  }	else if (numBaseComputo == ""){
		  		  document.Frm1.numDisabili.value = "";
				  if (!flagDisEffChanged) {
					document.Frm1.numDisEff.value = "";
				  }			  	
			  }  	  	  	  	  	  
		  }
		  
		  function checkDate (strdata1, strdata2) {
		  	
			  annoVar1 = strdata1.substr(6,4);
			  meseVar1 = strdata1.substr(3,2);
			  giornoVar1 = strdata1.substr(0,2);
			  dataVarInt1 = parseInt(annoVar1 + meseVar1 + giornoVar1, 10);
			  
			  annoVar2 = strdata2.substr(6,4);
			  meseVar2 = strdata2.substr(3,2);
			  giornoVar2 = strdata2.substr(0,2);
			  dataVarInt2 = parseInt(annoVar2 + meseVar2 + giornoVar2, 10);
			  
			  if (dataVarInt1 < dataVarInt2) {
			      return 1;
			  }
			  else {
			      if (dataVarInt1 > dataVarInt2) {
			        return 2;
			      }
			      else {
			        return 0;
			      }
			  }
		  }
		  
		  function controllaDati(){
		  	  var dataInizioVar = document.Frm1.DATINIZIOVALIDITA.value;
	  	  	  var numBaseComputo = document.Frm1.NUMBASECOMPUTO.value;
	  	  	  var numDisCalc = document.Frm1.numDisabili.value;	  	  	  
	  	  	  var numDisEff = document.Frm1.numDisEff.value;
	  	  	  var prgProspettoInf = document.Frm1.PRGPROSPETTOINF.value;
	  	  	  var codMonoStatoProspetto = document.Frm1.CODMONOSTATOPROSPETTO.value;
	  	  	  var msg;
	  	  	  var datInizioSosp = "<%=datInizioSosp%>";
	  	  	  var datFineSosp = "<%=datFineSosp%>";
	  	  	  
	  	  	  if(isNumeric(numBaseComputo) && isNumeric(numDisEff) && dataInizioVar != ""){
				  if (parseInt(numDisEff) > parseInt(numDisCalc)) {
			    	  msg = "Num. disabili eff. deve essere minore o uguale a Num. disabili calc.";
					  alert (msg);
					  return false;				  	
				  }
				  
				  var date1 = checkDate(datInizioSosp,dataInizioVar);
				  var date2 = checkDate(dataInizioVar,datFineSosp); 
				  if ( date1 == 2 || (date2 == 0 || date2 == 2 ) ){
	  				alert("La data inizio validità deve essere compresa tra la data inizio e data fine validità della richiesta di esonero");
				  	return false;
				  }
				  
				  if(prgProspettoInf != null && prgProspettoInf != "") {	
				  	if( codMonoStatoProspetto == "S" || codMonoStatoProspetto == "U" ) {
				  		msg = "Attenzione: il prospetto non verrà modificato perché è storicizzato!";
						return confirm(msg);	
				  	}else{		  	
					  	var numannoprosp = document.Frm1.NUMANNO_PROSPETTO.value;
					  	var percesonprosp = document.Frm1.NUMPERCESONERO_PROSPETTO.value;
					  	var perceson = document.Frm1.NUMPERCESONERO.value;
					  	var datafineesonprosp = document.Frm1.DATFINEESONERO_PROSPETTO.value;   				  	
					  	var datfinesosp = document.Frm1.DATFINESOSP.value;   
					 	msg = "Attenzione: verrà modificato il prospetto "+numannoprosp+" in corso d'anno \nmodificando la % di esonero (da "+percesonprosp+" a "+perceson+") e la data di esonero (da "+datafineesonprosp+" a "+datfinesosp+")!";
						return confirm(msg);					   
				  	}
				  }
				  
				  return true;
			  }else if(!isNumeric(numBaseComputo)){
		    	  msg = "Base di computo deve essere un campo numerico";
				  alert (msg);
				  return false;
			  }else if(!isNumeric(numDisEff)){
		    	  msg = "Num. disabili eff. deve essere un campo numerico";
				  alert (msg);
				  return false;
			  }	 			  			  			   	  	  	  	  	  
		  } 
		 
		
		function fnc_UpdateClose(){
		// dalla lista "storico variazioni", premendo chiudi, seguiva un errore ovvero non era possibile ricaricare la pagina 
		 <% Vector numDisVec = serviceResponse.getAttributeAsVector("M_List_Variazioni.ROWS.ROW");
			if(numDisVec.size() >= 1) { 
				SourceBean beannumDis = (SourceBean) numDisVec.elementAt(0);
				if(!beannumDis.containsAttribute("ROW") ){
				NUMBASECOMPUTO = ((BigDecimal) beannumDis.getAttribute("NUMBASECOMPUTO")).toString(); 
				numDisabili = ((BigDecimal) beannumDis.getAttribute("NUMDISABILI")).toString(); %>
			<%}
		  }%>
			window.opener.document.Frm1.numDisabili.value='<%=numDisabili%>';
			window.opener.document.Frm1.NUMBASECOMPUTO.value='<%=NUMBASECOMPUTO%>';
			window.close();
		 }
		 
		
		function agganciaProsp() {			
			var divVar = document.getElementById("divListaProsp");
			divVar.style.display = "inline";			
		}		   		

		function annullaProsp() {			
			document.Frm1.PRGPROSPETTOINF.value = "";		
		}	
		
		function selectDatiProsp(numBase, numPerc, datFine, numAnno, prgProspetto, codMonoStatoProspetto, codCategoriaProspetto) {			
			document.Frm1.PRGPROSPETTOINF.value = prgProspetto;	
			document.Frm1.CODMONOSTATOPROSPETTO.value = codMonoStatoProspetto;
			document.Frm1.NUMPERCESONERO_PROSPETTO.value = numPerc;
			document.Frm1.DATFINEESONERO_PROSPETTO.value = datFine;
			document.Frm1.NUMANNO_PROSPETTO.value = numAnno;
			document.Frm1.CATEGORIA_PROSPETTO.value = codCategoriaProspetto;
			if(isNumeric(numBase)) {			
				document.Frm1.NUMBASECOMPUTO.value = numBase;
				document.Frm1.codCategoria.value = codCategoriaProspetto;
				calcolaNumDisabili();
			}
			else {
				document.Frm1.NUMBASECOMPUTO.value = 0;
			}
		}
		

	</script>
	<script language="Javascript" src="../../js/docAssocia.js"></script>
</head>

<body class="gestione" onload="rinfresca();">
<af:showErrors />
		<%
		if(paginaNuovaVar.equals("1")){
		%>
		
		<div id="divListaProsp" style="display:none">
			<af:list moduleName="CMRichEsoneroListaProspModule" skipNavigationButton="1" jsSelect="selectDatiProsp"/>	
		</div>
		
		<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati()">
			
			<input type="hidden" name="inserisciVariazione" value="1">
			<input type="hidden" name="PAGE" value="CMRichEsoneroStoricoVar" />		
	
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
			
			<input type="hidden" name="PRGPROSPETTOINF" value="">
			<input type="hidden" name="CODMONOSTATOPROSPETTO" value="">
			<input type="hidden" name="NUMPERCESONERO_PROSPETTO" value="">
			<input type="hidden" name="DATFINEESONERO_PROSPETTO" value="">
			<input type="hidden" name="NUMANNO_PROSPETTO" value="">
			<input type="hidden" name="CATEGORIA_PROSPETTO" value="">
			<input type="hidden" name="DATFINESOSP" value="<%=datFineSosp%>">
					
			<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
			<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
			<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
			<input type="hidden" name="prgRichEsonero" value="<%=prgRichEsonero%>">
			<input type="hidden" name="PRGRICHESONDISABILI" value="<%=PRGRICHESONDISABILI%>">
			<input type="hidden" name="NUMKLORICHESONDIS" value="1">
			
		    <table class="main" border="0">
		        <tr><td colspan="2"/>&nbsp;</td></tr>
		        <tr>
		          <td colspan="2">
		          	<p class="titolo">Inserimento nuova variazione</p>
		          </td>
		        </tr>
		        <tr><td colspan="2"/>&nbsp;</td></tr>	        
			</table>			
	    	<%= htmlStreamTop %>

		    <table class="main" border="0">
		        <tr><td colspan="2"/>&nbsp;</td></tr>
		        <tr>
				   	<td class="etichetta2">Categoria</td>
					<td class="campo2" colspan="3">
						<af:comboBox name="codCategoria" classNameBase="input" required="true" disabled="<%=fieldReadOnly%>" title="Categoria" onChange="calcolaNumDisabili()" >	  
				            <option value=""  <% if ( "".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>></option>
				            <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>più di 50 dipendenti</option>
				           	<option value="B" <% if ( "B".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>da 36 a 50 dipendenti</option>               
				           	<option value="C" <% if ( "C".equalsIgnoreCase(codMonoCategoria) )  { %>SELECTED="true"<% } %>>da 15 a 35 dipendenti</option> 
				        </af:comboBox> 
				    </td> 	
	    		</tr>	
		        <tr><td colspan="2"/>&nbsp;</td></tr>
		    	<tr>
			        <td class="etichetta">Base di computo</td>
				    <td class="campo">
				      <af:textBox title="Base di computo" classNameBase="input" name="NUMBASECOMPUTO" size="12" required="true"  readonly="<%=fieldReadOnly%>" onKeyUp="calcolaNumDisabili()" onBlur="calcolaNumDisabili()"/>
				      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				      Quota Scomputata&nbsp;	
				      <af:textBox title="Num. disabili calc." classNameBase="input" name="numDisabili" size="5" readonly="true" />
				    </td>	
	    		</tr> 
		    	<tr>
				    <td class="etichetta">% Esonero</td>	
				    <td class="campo">
				      <af:textBox title="Percentuale di esonero" value="<%=NUMPERCESONERO%>" classNameBase="input" name="NUMPERCESONERO" size="12" readonly="true" />
				    </td>	
	    		</tr>		    	
		    	<tr>
			        <td class="etichetta">Data inizio validita'</td>
				    <td class="campo">
				      <af:textBox title="Data inizio validita" validateOnPost="true" disabled="false" type="date" name="DATINIZIOVALIDITA" value="" size="12" required="true" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
				      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Quota Effettiva&nbsp;&nbsp;	
				      <af:textBox title="Num. disabili eff." classNameBase="input" name="numDisEff" size="5" readonly="<%=fieldReadOnly%>" onKeyUp="fieldDisEffChanged();"/>				    
				    </td>	
	    		</tr>   
		    	<tr>
		    		<td colspan="2">&nbsp;</td>
		    	</tr>		    			    	
		    	
	        </table>	        
	        <%= htmlStreamBottom %>						
			
		    <table class="main" border="0">
		    	<tr>
				    <td colspan="2" align="center">
				      <input class="pulsanti" type="submit" name="inserisci" value="Inserisci Variazione" onClick="">
			          &nbsp;&nbsp;
			          <input type="reset" class="pulsanti" value="Annulla" onClick="annullaProsp()"/>
			          &nbsp;&nbsp;
			          <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
				    </td>	
	    		</tr>
	    		<%
	    		if (("AP").equalsIgnoreCase(codStatoRich)) {
	    		%>	    		
		    		<tr>
					    <td colspan="2" align="center">
				          &nbsp;&nbsp;				    
					      <input class="pulsanti" type="button" name="aggancia" value="Aggancia Prospetto" onClick="agganciaProsp()">
				          &nbsp;&nbsp;
					    </td>	
		    		</tr>
	    		<%
	    		}
	    		%>
			</table>				        
		</af:form>
			
		<%}else{%>
		<br><br>
		
		<af:list moduleName="M_List_Variazioni" skipNavigationButton="0" canDelete="<%= canModify ? \"1\" : \"0\" %>" canInsert="<%= canModify ? \"1\" : \"0\" %>"/>		
			    
	    <table align="center">
	    	<tr>
			    <td>
			    	<input type="button" class="pulsanti" value="Chiudi" onClick="fnc_UpdateClose()">
			    </td>	
    		</tr>	    		
        </table>
		<%}%>
	
</body>
</html>
