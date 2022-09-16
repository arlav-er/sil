<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- @author: Giordano Gritti -->
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, 
                java.math.*,
                java.net.URLEncoder"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
		String _page = (String) serviceRequest.getAttribute("PAGE");
		int _funzione=0;
		_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
		ProfileDataFilter filter = new ProfileDataFilter(user, _page);
		
		SourceBean row  = null;
		BigDecimal cdnLav	= null;		
		Vector vettRis = serviceResponse.getAttributeAsVector("M_LISTA_AVVIAMENTI.ROWS.ROW");
		for(int i = 0; i<vettRis.size(); i++){
			row = (SourceBean) vettRis.get(i);
			cdnLav = (BigDecimal)row.getAttribute("CDNLAVORATORE");
		}
			
		
		// recupero i parametri di ricerca dalla request
		String strCF 		= StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
		String strCognome 	= StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
		String strNome 		= StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
		String strRagSoc	= StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale");
		String numRich		= StringUtils.getAttributeStrNotNull(serviceRequest,"prgRichiestaAz");
		String numAnno 		= StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
		String dataDa 		= StringUtils.getAttributeStrNotNull(serviceRequest,"datAvvDal");
		String dataA 		= StringUtils.getAttributeStrNotNull(serviceRequest,"datAvvAl");
		String statoAvv		= StringUtils.getAttributeStrNotNull(serviceRequest,"statoAvv");
		String flgNoStato	= StringUtils.getAttributeStrNotNull(serviceRequest,"flgAvvNoStato");
		String cpi		 	= StringUtils.getAttributeStrNotNull(serviceRequest,"sel_cpi");
		
		String attr   = null;
		String valore = null;
		String txtOut = "";
		
		attr= "codiceFiscaleLavoratore";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !strCF.equals("") )
        {txtOut += " Codice Fiscale <strong>" + valore + "</strong>; ";
        }
		attr= "cognome";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !strCognome.equals("") )
        {txtOut += " Cognome <strong>" + valore + "</strong>; ";
        }
		attr= "nome";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !strNome.equals("") )
        {txtOut += " Nome <strong>" + valore + "</strong>; ";
        }
		attr= "ragioneSociale";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !strRagSoc.equals("") )
        {txtOut += " Ragione Sociale <strong>" + valore + "</strong>; ";
        }
		attr= "prgRichiestaAz";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !numRich.equals("") )
        {txtOut += " Numero Richiesta <strong>" + valore + "</strong>; ";
        }
		attr= "anno";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !numAnno.equals("") )
        {txtOut += " Anno Richiesta <strong>" + valore + "</strong>; ";
        }
        attr= "datAvvDal";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !dataDa.equals("") )
        {txtOut += " Data inizio avv.  <strong>" + valore + "</strong>; ";
        }
		attr= "datAvvAl";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !dataA.equals("") )
        {txtOut += " Data fine avv. <strong>" + valore + "</strong>; ";
        }        
		attr= "strStatoAvv";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !dataA.equals("") )
        {txtOut += " Stato avv. <strong>" + valore + "</strong>; ";
        }  
		attr= "flgAvvNoStato";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !flgNoStato.equals("") )
        {txtOut += " Senza stato avv. <strong>" + valore + "</strong>; ";
        }     
		attr= "strCPI";
        valore = (String) serviceRequest.getAttribute(attr);
        if(valore != null && !valore.equals("") && !cpi.equals("") )
        {txtOut += " Cpi competente <strong>" + valore + "</strong>; ";
        }      
        

                       
		//formattazione pagina jsp
		String htmlStreamTop = StyleUtils.roundTopTable(false);
		String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
 
<html>
<head>
<title>Lista Lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script TYPE="text/javascript">

function invioMassivo() {
if (  IsElementiSelezionati('prgEvidenza') && confirm("Creare una nuova evidenza per tutti gli avviamenti selezionati?") ) {
	var mes	=document.frmLista.tipoMess.options[document.frmLista.tipoMess.selectedIndex].text;
	var s	= "AdapterHTTP?PAGE=AS_NuovaEvidenzaPage&INVIOMASSIVO=true&TipoMessEv=" + mes;
   
    var chkboxObjEval = document.getElementsByName("prgEvidenza");
    var chkboxObj=eval(chkboxObjEval);
    var cdnLav="";
    for(i=0; i<chkboxObj.length; i++) {
  		if(chkboxObj[i].checked) {
  			if(cdnLav.length>0) { cdnLav += URLEncoder.encode("|"); }
  			cdnLav += chkboxObj[i].value;
  			}
  	  	}	
    s +="&CDNLAVORATORE=" + cdnLav;    
    setWindowLocation(s);
    }
}
//controllo sulla selezione degli appuntamnenti
function IsElementiSelezionati(listName) {	
	var elementi= document.getElementsByName(listName);
	msg = "E' necessario selezionare\nalmeno un avviamento"
	for (var i= 0; i < elementi.length; i++) {
		if ( elementi[i].checked ) {
		return true;
		}	
	}		
	alert(msg);
	return (false);
}  


//gestione checkbox
function selDeselApp(){
	var coll = document.getElementsByName("SEL");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("prgEvidenza");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
}
//torna alla ricerca
function tornaAllaRicerca(){
	var cdnfunz = <%=_funzione%>;
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  		url="AdapterHTTP?PAGE=ASRicercaAvvSelezionePage&CDNFUNZIONE=" +cdnfunz;
    	setWindowLocation(url);
}

 
</script>

</head>

<body class="gestione" onload="rinfresca()">
<!-- paremetri della ricerca -->
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>
<af:form action="AdapterHTTP" name="frmLista" method="GET" >
<input name="cdnLavoratore" type="hidden" value="<%=cdnLav%>"/>

<table width="98%" align="center" bgcolor="#cce4ff">
	<tr valign="middle">
		<td align="left" valign="middle">
			<input type="checkbox" name="SEL" onClick="selDeselApp();" />&nbsp;Seleziona/Deseleziona tutti &nbsp;&nbsp;		
	    </td>
	   
	    <td align="right" valign="middle">Tipo di messaggio&nbsp;	
	    	<af:comboBox name="tipoMess" 
	                 size="1"
	                 title="tipoMess"
	                 multiple="false"
	                 required="false"
	                 focusOn="false"		                 
	                 moduleName="M_TIPO_MESS_EVIDENZA"
	                 addBlank="true"
	                 blankValue=""
	   		 />
	   	</td>				
		<td align="right" valign="middle" width="15%">
			<input type="button" class="pulsanti" value="Nuova evidenza" onclick="invioMassivo()">
		</td>	
	</tr>
</table>
<af:list moduleName="M_LISTA_AVVIAMENTI" />

</af:form>
<table class="main" align="center">	<tr>
		<td align="center"><input type="button" class="pulsanti"
			value="Ritorna alla ricerca" onclick="tornaAllaRicerca()">
		</td>
	</tr>
</table>
<%out.print(htmlStreamBottom);%>
</body>
</html>

