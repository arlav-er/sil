<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
//  PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
//  boolean canModify = attributi.containsButton("nuovo");

 //boolean canInsert=attributi.containsButton("INSERISCI");
 
 String codCpiBudget= StringUtils.getAttributeStrNotNull(serviceRequest, "codCpiBudget");
 if(StringUtils.isEmptyNoBlank(codCpiBudget)){
	 codCpiBudget= StringUtils.getAttributeStrNotNull(serviceRequest, "codiceCPISel");
 }
 String annoRicerca= StringUtils.getAttributeStrNotNull(serviceRequest, "AnnoSel");

 
 

  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  PageAttribs attributi = new PageAttribs(user, "IdoPubbRicercaPage");
  boolean canInsert=attributi.containsButton("INSERISCI");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"ANNO");
  String CDNUT = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUT");
  String codMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE");
  String codTipoMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE");
  String datPubblicazione = StringUtils.getAttributeStrNotNull(serviceRequest,"DATPUBBLICAZIONE");
  String datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(serviceRequest,"DATSCADENZAPUBBLICAZIONE");
  String descMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE");
  String flgPubblicata = StringUtils.getAttributeStrNotNull(serviceRequest,"FLGPUBBLICATA");
  if (flgPubblicata.equals(""))
    flgPubblicata="INPUB";
  String Indirizzo =  StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo");
  String numRichiesta = StringUtils.getAttributeStrNotNull(serviceRequest,"NUMRICHIESTA");
  String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
    String UTRIC = StringUtils.getAttributeStrNotNull(serviceRequest,"UTRIC");
      
  String codMonoCMcatPubb = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCMcatPubb");
      //String cerca = StringUtils.getAttributeStrNotNull(serviceRequest,"cerca");
  String cf = StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
  String codCom = StringUtils.getAttributeStrNotNull(serviceRequest,"codCom");
  String codComHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codComHid");
  String codMansioneHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codMansioneHid");
//  String codTipoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda");
  String desComune = StringUtils.getAttributeStrNotNull(serviceRequest,"desComune");
  String desComuneHid = StringUtils.getAttributeStrNotNull(serviceRequest,"desComuneHid");
  String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");
  String strTipoMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione");
  String utente = StringUtils.getAttributeStrNotNull(serviceRequest,"utente");
  if (utente==""){   
    utente="1";
  }
  
  

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca pubblicazioni</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>


<script language="Javascript">
     <% 
      //Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

<SCRIPT TYPE="text/javascript">
/*Funzione che chiama la pagina dei Budget Centri Impiego */
function chiamaTotaliBudgetCPI()
{  
   // Se la pagina è già in submit, ignoro questo nuovo invio!
  // alert("sto chiamando i totali");
	if( !validateInteger('ANNOBUDGET')){
		  return false;	
	}
	
	var descCPI = document.Frm1.descrCPIhid;
	var comboCPI = document.Frm1.codCpi;
	 
	
	if(comboCPI.value != ""){
		descCPI.value = comboCPI.options[comboCPI.selectedIndex].text;	
	}
	 
		//alert("prosegue submit");
  	url="AdapterHTTP?PAGE=VisualizzaTotaliBudgetPage";
   	url += "&CDNFUNZIONE="+"<%=_funzione%>";
  	url += "&AnnoSel="+document.Frm1.ANNOBUDGET.value;
   	url += "&codiceCPISel="+document.Frm1.codCpi.value;
	url += "&codiceCPIDescr="+document.Frm1.descrCPIhid.value;
   		//alert("Il valore di url "+url);
  	setWindowLocation(url);
	
}

function callInsertNewBudget()
{
 
    if(!isRequired('ANNOBUDGET') || !validateInteger('ANNOBUDGET')){
		  return false;	
    }
	if (!isRequired('codCpi')){
		  return false;	
	}else if( document.Frm1.codCpi.value == "undefined"){
		alert("Il campo " + document.Frm1.codCpi.title + " è obbligatorio");
		return false;
	}
    if(!isRequired('EuroBudget') ||!validateInteger('EuroBudget')){
		  return false;	
  	}
 
	 
	var    url = "AdapterHTTP?PAGE=InsertBudgetPerCpiPage" 
		   url += "&AnnoSel="+document.Frm1.ANNOBUDGET.value;
	   	   url += "&codiceCPISel="+document.Frm1.codCpi.value;
	   	   url += "&importoSel="+document.Frm1.EuroBudget.value;
	   	
		//alert("Il valore di url "+url);
	  setWindowLocation(url);
	
		  
}
function selectItemByValue(){
	var elmnt= document.getElementsByName('codCpi')[0];

	  for(var i=0; i < elmnt.options.length; i++)
	  {
	    if(elmnt.options[i].value === '') {
	      elmnt.selectedIndex = i;
	      break;
	    }
	  }
	}
</SCRIPT>


</head>

<body class="gestione" onload="rinfresca();">
<p class="titolo">BUDGET per CPI</p>
<font color="red">
<af:showErrors />
</font>
<font color="green">
<af:showMessages prefix="M_INSERT_NEW_BUDGET" />
</font>


<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP"   >
<input type="hidden" name="PAGE" value="VisualizzaTotaliBudgetPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="CDNUT" value="<%= codiceUtente %>"/>
<input type="hidden" name="prgAzienda"/>
<input type="hidden" name="prgUnita"/>




<p align="center">
<table class="main"> 
		 <tr>
				<td>
					<table  align="center" border="0">
				     	<tr>
				     		<td   colspan="2">
				     			 Anno Budget
				     			
				     			 <af:textBox title="Anno Budget" type="text" classNameBase="input" name="ANNOBUDGET" value="<%=annoRicerca %>" size="4" maxlength="4" />
				     		</td>	
				     		<td   colspan="2">
				     			 Centro per l&rsquo;impiego
				     			
								<af:comboBox classNameBase="input" title="Centro per l&rsquo;Impiego" name="codCpi" 
									moduleName="M_ELENCO_CPI_BUDGET" addBlank="true" selectedValue="<%=codCpiBudget%>" />
									<input name="descrCPIhid" type="hidden" value=""/>
							
				     		</td>
				     	</tr>
				     </table>
	  				<br/><br/>
	  
	   			</td>
      </tr>   
	  <tr>
	  		<td align="center">
	  			<div style="width:900px; height:200px; background:#e8f3ff; border:1px solid black;">
					<table class="main"  border="0">
						 <tr>
						 	<td  >&nbsp;</td>
							<td   class="etichetta" align="center" >Inserire l&rsquo;importo in caso di richiesta di Inserimento di un Nuovo Budget</td>
							<td  >&nbsp;</td>
						</tr>
					</table>
					 <br/><br/>
					<table  align="center"  border="0">
						<tr>
							
							<td  >&nbsp;</td>
							<td  align="left">Euro
								
								<af:textBox type="text" classNameBase="input" name="EuroBudget" value="" size="10" maxlength="10" title="Euro"/>
							</td>
							
						</tr>
						
					</table>
					 <br/><br/>
					<table align="center"  border="0">
							 <tr>
								<td colspan="2" align="center">
									<input class="pulsanti" type="button" name="InsertNewBudget" value="Inserisci Nuovo Budget"  onclick="callInsertNewBudget()" />
								</td>
							</tr>
					</table>
			 </div>
			</td>
    </tr>   
     <br/><br/>
     
              
</table>
</p>
<br/>
		<table class="main">
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
              <input class="pulsanti" type="button" name="cerca" value="Cerca"   onclick="chiamaTotaliBudgetCPI()"/>
             &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla" onClick="javascript:document.Frm1.ANNOBUDGET.value='';document.Frm1.descrCPIhid.value='';selectItemByValue();" >
            </td>
          </tr>
        </table>
         <br/>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>