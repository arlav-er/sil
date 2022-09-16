<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.text.*,java.util.*"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
	it.eng.afExt.utils.StringUtils,
	it.eng.sil.security.* " %>
                  
              
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

String strcodfisc = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscaleLavoratore");
String cognomeLav = StringUtils.getAttributeStrNotNull(serviceRequest, "cognome");
String nomeLav = StringUtils.getAttributeStrNotNull(serviceRequest, "nome");
String dataNascitaLav = StringUtils.getAttributeStrNotNull(serviceRequest, "datnasc");

String ragioneSocialeAzienda =  StringUtils.getAttributeStrNotNull(serviceRequest, "ragioneSociale");
String ragioneSocialeAziendaUt =  StringUtils.getAttributeStrNotNull(serviceRequest, "ragioneSocialeUt");

String dataMovDa =  StringUtils.getAttributeStrNotNull(serviceRequest, "datmovimentoda");
String dataMovA =  StringUtils.getAttributeStrNotNull(serviceRequest, "datmovimentoa");

String tipoMovimento =  StringUtils.getAttributeStrNotNull(serviceRequest, "tipoMovimento");
String tipoMovimentoDescr =  StringUtils.getAttributeStrNotNull(serviceRequest, "movimentoDescr");

String statoMovimento =  StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");
String statoMovimentoDescr =  StringUtils.getAttributeStrNotNull(serviceRequest, "statoDescr");

String parameters = "";
String txtFilters = "";

if (!strcodfisc.equals("")) {
	parameters = parameters + "&codiceFiscaleLavoratore="+strcodfisc;
	txtFilters += " Movimenti del Lavoratore: " + strcodfisc ;
	txtFilters += " - " + cognomeLav + " " + nomeLav + " - " + dataNascitaLav;
}
if (!ragioneSocialeAzienda.equals("")) {
	parameters = parameters + "&ragioneSociale="+ragioneSocialeAzienda;
	txtFilters += "<br/> Azienda: " + ragioneSocialeAzienda;
}
if (!ragioneSocialeAziendaUt.equals("")) {
	parameters = parameters + "&ragioneSocialeUt="+ragioneSocialeAziendaUt;
	txtFilters += "<br/> Azienda utilizzatrice/Ente promotore: " + ragioneSocialeAziendaUt;
}
if (!dataMovDa.equals("")&& !dataMovA.equals("")) {
	parameters = parameters + "&datmovimentoda="+dataMovDa;
	parameters = parameters + "&datmovimentoa="+dataMovA;
	txtFilters += "<br/> Data movimento dal: " + dataMovDa + " al: " + dataMovA;
}else if (!dataMovA.equals("")) {
	parameters = parameters + "&datmovimentoa="+dataMovA;
	txtFilters += "<br/> Data movimento al: " + dataMovA;
}else if (!dataMovDa.equals("")) {
	parameters = parameters + "&datmovimentoda="+dataMovDa;
	txtFilters += "<br/> Data movimento dal: " + dataMovDa;
}
if (!tipoMovimento.equals("")) {
	parameters = parameters + "&tipoMovimento="+tipoMovimento;
	parameters = parameters + "&movimentoDescr="+tipoMovimento;
	txtFilters += "<br/> Tipo movimento: " + tipoMovimentoDescr;
}
if (!statoMovimento.equals("")) {
	parameters = parameters + "&CODSTATOATTO="+statoMovimento;
	parameters = parameters + "&statoMovimentoDescr="+statoMovimentoDescr;
	txtFilters += "<br/> Stato movimento: " + statoMovimentoDescr;
}

Vector movimentiFiltrati= serviceResponse.getAttributeAsVector("M_ListaForzaModMov.ROWS.ROW");    	
boolean canDo = (movimentiFiltrati.size()>=1)? true : false;


%>
<html>
<head>
  	<title>Lista movimenti per forzatura</title>

  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  	<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  	<af:linkScript path="../../js/"/>
  	
	<script type="text/Javascript">
  	function tornaAllaRicerca() {  
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
     	if (isInSubmit()) return;
	   	url="AdapterHTTP?PAGE=ForzaModMovPage";
		url += "&CDNFUNZIONE="+"<%=_funzione%>";
		url += "<%=parameters%>";
		
	    setWindowLocation(url);
  	}
  	 var numMovimentiSel=0;
    //Funzione che viene eseguita ad ogni click delle checkbox relativa a quella riga
  	function checkboxMovimentiForzatura(checkbox, PRGMOVIMENTO) {		
  	}
	var allChecked="NO_ACTION";
  	function checkAllMovimentiForzatura() {	
  		 
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
 		if (isInSubmit()) return;
 		var form = document.Frm1;
       
        if(allChecked=="NO_ACTION") {	
        	allChecked = "SELEZIONATI";
        	 for(var i = 0; i < form.elements.length ; i ++) {
                 if(form.elements[i].name == "ckeckboxMovimenti" ) {
                 	
                 	 form.elements[i].checked = "true";
                    	 numMovimentiSel++;
                 }
             } 
        }else if(allChecked=="SELEZIONATI") {	
        	allChecked = "NO_ACTION";
       		 numMovimentiSel=0;
       	 for(var i = 0; i < form.elements.length ; i ++) {
                if(form.elements[i].name == "ckeckboxMovimenti" ) {
                	
                	 form.elements[i].checked = null;
                   
                }
            } 
       }
       
  	}
	
  	 function confirmValidateSelected() {
 		// Se la pagina è già in submit, ignoro questo nuovo invio!
 		if (isInSubmit()) return;
 		
 		//Controllo se la ricerca non ha prodotto alcun risultato  
 		 if(<%=!canDo%>){
 			alert("Non ci sono movimenti da modificare");
              	return;
 		}	
 		 
 		//Controllo che sia stato selezionato almeno un movimento	
 		var form = document.Frm1;
        
         for(var i = 0; i < form.elements.length ; i ++) {
             if(form.elements[i].name == "ckeckboxMovimenti" && form.elements[i].checked) {
                	 numMovimentiSel++;
             }
         } 
 	    if (numMovimentiSel==0){ 
 	    	alert ("Selezionare almeno un movimento");
 	    	return ;
     	}else{
     		doFormSubmit(document.Frm1);
     	}
     }//end_function
 
  	</script>
</head>

<body class="gestione" onload="rinfresca()">

<%
if (txtFilters.length() > 0) {
	txtFilters = "Filtri di ricerca:<br/> " + txtFilters;
%>
<table cellpadding="2" cellspacing="10" border="0" width="100%">
	<tr>
		<td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
		<%
      	out.print(txtFilters);
        %>
		</td>
	</tr>
</table>
<%
}
%>
 
<af:form name="Frm1" method="GET" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="ListaMovModificabilePage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	<input type="hidden" name="codiceFiscaleLavoratore" value="<%=strcodfisc%>"/>
	<input type="hidden" name="cognome" value="<%=cognomeLav%>"/>
	<input type="hidden" name="nome" value="<%=nomeLav%>"/>
	<input type="hidden" name="datnasc" value="<%=dataNascitaLav%>"/>
	<input type="hidden" name="ragioneSociale" value="<%=ragioneSocialeAzienda%>"/>
	<input type="hidden" name="ragioneSocialeUt" value="<%=ragioneSocialeAziendaUt%>"/>
	<input type="hidden" name="movimentoDescr" value="<%=tipoMovimentoDescr%>"/>
	<input type="hidden" name="statoDescr" value="<%=statoMovimentoDescr%>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE")%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA")%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA")%>"/>
    <input type="hidden" name="PRGAZIENDAUt" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUt")%>"/>
    <input type="hidden" name="PRGUNITAUt" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUt")%>"/>
    <input type="hidden" name="datmovimentoda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "datmovimentoda")%>"/>
    <input type="hidden" name="datmovimentoa" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "datmovimentoa")%>"/>
    <input type="hidden" name="tipoMovimento" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "tipoMovimento")%>"/>
	<input type="hidden" name="CODSTATOATTO" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO")%>"/>
  <center>

  <af:list moduleName="M_ListaForzaModMov"/>
  </center>
  <br/>
  	  <table>
          <tr>
        <td>
            <table width="100%">
                <tr>
                    <td align=right><input type="checkbox" id="selectAll" onclick="checkAllMovimentiForzatura()" >Seleziona/Deseleziona tutti i movimenti</td> 
                </tr>    
            </table>
        </td>
    </tr>
	  </table>
  <center>
	  <table>
	  	 <tr>
			  <td colspan="2" align="center">
			      <input class="pulsanti" type="button" onclick="confirmValidateSelected();" name="valida" value="Modifica selezionati" />
			</td>
		  </tr>
		  	 <tr>
			  <td colspan="2" align="center">
		  
			      <input class="pulsante" type="button" name="torna" value="Torna alla ricerca" onclick="tornaAllaRicerca()"/>
			  </td>
		  </tr>
		  <tr>
		<td colspan="2" align="center">
		&nbsp;
		</td>
	  </tr>
	  </table>
  </center>
</af:form>
</body>
</html>


