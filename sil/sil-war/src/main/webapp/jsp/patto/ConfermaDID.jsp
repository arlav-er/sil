<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.SourceBeanUtils,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _current_page = (String) serviceRequest.getAttribute("PAGE");
String cdnLav = serviceRequest.containsAttribute("cdnLavoratore")?serviceRequest.getAttribute("cdnLavoratore").toString():"";
String _cdnFunz = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String prgDid = (String) serviceRequest.getAttribute("PRGDICHDISP");
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
String dataDichirazioneDid = serviceRequest.containsAttribute("dataDichDid")?serviceRequest.getAttribute("dataDichDid").toString():"";
String dataOdierna = DateUtils.getNow();
String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");


boolean isConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma"); 
//boolean isButtonGestioneConsenso = false;
boolean isConsensoAttivo = false;
String ipOperatore = null;
String firmaGrafometrica = null;

if(isConsenso){
/*	  ProfileDataFilter filterConsenso = new ProfileDataFilter(user, "HomeConsensoPage");
  isButtonGestioneConsenso = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.viewGestioneConsensoBtn") 
		  						&& filterConsenso.canView();*/
	  isConsensoAttivo = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo");
	  firmaGrafometrica = serviceResponse.containsAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo")?serviceResponse.getAttribute("M_VerificaAmConsensoFirma.consensoFirmaAttivo").toString():"";
	  if (!firmaGrafometrica.equalsIgnoreCase("") && firmaGrafometrica.equalsIgnoreCase("si")){
		  firmaGrafometrica = "OK";
	  }
	  if(isConsensoAttivo){
		 ipOperatore = serviceResponse.getAttribute("M_VerificaAmConsensoFirma.ipOperatore").toString();
	  }
}



boolean apriDivAnn = false;
if(apriDiv == null) { 
	apriDiv = "none"; 
}
else { 
	apriDiv = "";
	apriDivAnn = true;
}
boolean canModify = true;
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
String dataDichiarazione = "";
String codstatoatto = "";
String strNoteDich = "";
Object prgDidAnnuale= "",
	   cdnUtIns= "",
	   dtmIns= "",
	   cdnUtMod= "",
	   dtmMod= "";
BigDecimal numkloConcorrenza = null;
Testata operatoreInfo = null;

if(serviceResponse.containsAttribute("M_Get_Dich_Annuale")) { 
  	SourceBean rowDich = (SourceBean)serviceResponse.getAttribute("M_Get_Dich_Annuale.ROWS.ROW");
  	dataDichiarazione = StringUtils.getAttributeStrNotNull(rowDich, "datdichiarazione");
  	codstatoatto = StringUtils.getAttributeStrNotNull(rowDich, "codstatoatto");
  	strNoteDich = StringUtils.getAttributeStrNotNull(rowDich, "strnote");
  	prgDidAnnuale= rowDich.getAttribute("PRGDIDANNUALE");
  	cdnUtIns = rowDich.getAttribute("CDNUTINS");
    dtmIns	= rowDich.getAttribute("DTMINS");
    cdnUtMod = rowDich.getAttribute("CDNUTMOD");
    dtmMod = rowDich.getAttribute("DTMMOD");
    numkloConcorrenza = (BigDecimal)rowDich.getAttribute("numklodidann");
    numkloConcorrenza = numkloConcorrenza.add(new BigDecimal("1"));
    operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
}

if(ipOperatore==null)
	ipOperatore = serviceRequest.containsAttribute("ipOperatore")?serviceRequest.getAttribute("ipOperatore").toString():"";
%>

<%@page import="it.eng.afExt.utils.DateUtils"%>
<%@page import="it.eng.afExt.utils.StringUtils"%><html>
<head>
<title>Conferma dichiarazione annuale</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
 <script language="JavaScript">
	var flagChanged = false;
	 
	function fieldChanged() {
		flagChanged = true;
	}
 	function ControllaData() {
 		var dataDichAnnualeInt = 0;
 		var annoDichInt = 0;
 		var annoOdiernoInt = 0;
 		var d = new Date();
        annoOdierno = d.getFullYear().toString();
        meseOdierno = (d.getMonth() + 1).toString();
        if (meseOdierno.length == 1) {
        	meseOdierno = '0' + meseOdierno;
        }
        giornoOdierno = d.getDate().toString();
        if (giornoOdierno.length == 1) {
        	giornoOdierno = '0' + giornoOdierno;
        }
 		if (document.Frm1.DATDICHANNUALE.value != "") {
 			dataDichAnnuale = new String(document.Frm1.DATDICHANNUALE.value);
 		    annoDich = dataDichAnnuale.substr(6,4);
 		    meseDich = dataDichAnnuale.substr(3,2);
 		    giornoDich = dataDichAnnuale.substr(0,2);
 		   	annoDichInt = parseInt(annoDich, 10);
 		    dataDichAnnualeInt = parseInt(annoDich + meseDich + giornoDich, 10);
 		   	if ('<%=dataDichirazioneDid%>' != '') {
 				dataDichDid = new String('<%=dataDichirazioneDid%>');
 				annoDichDid = dataDichDid.substr(6,4);
 				annoDichDidInt = parseInt(annoDichDid, 10);
 				
 				if (annoDichInt <= annoDichDidInt) {
 					alert('Non è possibile rilasciare una dichiarazione nello stesso anno(o anno precedente) della did.');
 					return false;
 				}
 			}
			annoOdiernoInt = parseInt(annoOdierno, 10);
			if (annoDichInt < annoOdiernoInt) {
				if (confirm("Attenzione, si tratta di una dichiarazione pregressa, vuoi proseguire?")) {
					document.Frm1.confirmOperation.value = 'true';
					doFormSubmit(document.Frm1);
				}
				else {
					return false;
				}	
			}	
 		}
 		
        var dataOdiernaInt = parseInt(annoOdierno + meseOdierno + giornoOdierno, 10);
		if (dataDichAnnualeInt > dataOdiernaInt) {
			alert('Non è possibile rilasciare una dichiarazione con data futura.');
			return false;	
		}		
 		return true;
 	}

 	function Select(prg) {
      	var s= "AdapterHTTP?PAGE=ConfermaAnnualeDidPage";
      	s += "&PRGDIDANNUALE=" + prg;
      	s += "&CDNLAVORATORE=<%= cdnLav %>";
      	s += "&CDNFUNZIONE=<%= _cdnFunz %>";
      	s += "&DATADICHDID=<%= dataDichirazioneDid %>";
      	s += "&firmaGrafometrica=<%= firmaGrafometrica %>";
    	s += "&ipOperatore=<%= ipOperatore %>";
    	s += "&PRGDICHDISP=<%= prgDid %>";
      	s += "&APRIDIV=1";
      	setWindowLocation(s);
    }
 	
 	function apriGestioneConsenso( ) {
 	  var urlpage="AdapterHTTP?";
 	    urlpage+="PAGE=HomeConsensoPage&";
 	    urlpage+="CDNFUNZIONE=<%=_cdnFunz%>&";
 	    urlpage+="cdnLavoratore=<%=cdnLav%>&";
// 	  	    setOpenerWindowLocation(urlpage);
 			self.close();
 	  	    window.open(urlpage, "main");
 	}
 	
 </script>
 
</head>
<body class="gestione" onload="rinfresca()">
<font color="red">
	<af:showErrors/>
</font>
<font color="green">
 	<af:showMessages prefix="M_Inserisci_Dich_Annuale"/>
 	<af:showMessages prefix="M_Annulla_Dich_Annuale"/>
</font>

<center>
<p><af:list moduleName="M_Lista_Dich_Annuali" 
	jsSelect="Select" skipNavigationButton="1"/></p>
</center>
	<%if(isConsenso){%>
 	   	  <af:showMessages prefix="M_VerificaAmConsensoFirma"/>
	   	 	
     <%}%>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaData()">
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
<tr>
<td class="etichetta">Data dich.annuale &nbsp;</td>
<td class="campo">
<af:textBox classNameBase="input" type="date" name="DATDICHANNUALE" value="<%=dataOdierna%>"
	required="true" title="Data conferma dich.annuale"
    validateOnPost="true" size="12" maxlength="10" />
</td>
</tr>
<tr>
<td class="etichetta">Note &nbsp;</td>
<td class="campo">
<af:textArea classNameBase="textarea" name="strNote" value=""
	cols="60" rows="4" maxlength="1000" />
</td>
</tr>
</table>

<input type="hidden" name="PAGE" value="REPORTFRAMEBACKPAGE"/>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLav%>"/>
<input type="hidden" name="PRGDICHDISP" value="<%=prgDid%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>">
<%
String annoProt = SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_GETPROTOCOLLAZIONE.ROWS.ROW.NUMANNOPROT");
String numProt  = SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_GETPROTOCOLLAZIONE.ROWS.ROW.NUMPROTOCOLLO");
//
Calendar now = Calendar.getInstance();    
String giorno = Integer.toString(now.get(Calendar.DATE));    if(giorno.length()<=1) { giorno = "0" + giorno; }
String mese   = Integer.toString(now.get(Calendar.MONTH)+1); if(mese.length()<=1)   { mese   = "0" + mese; }
String anno   = Integer.toString(now.get(Calendar.YEAR));    if(anno.length()<=1)   { anno   = "0" + anno; }
String dataProt = giorno +"/"+ mese +"/"+ anno; 			
String ora = Integer.toString(now.get(Calendar.HOUR_OF_DAY)); if(ora.length()<=1)    { ora    = "0" + ora; }
String minuti = Integer.toString(now.get(Calendar.MINUTE));   if(minuti.length()<=1) { minuti = "0" + minuti; }
String oraProt = ora + ":" + minuti;
String dataOraProt = dataProt + " "  + oraProt;
%>

<input type="hidden" name="ACTION_REDIRECT" value="RPT_STAMPA_DICH_ANNUALE"/>
<input type="hidden" name="QUERY_STRING" value="PAGE=ConfermaAnnualeDidPage&amp;CDNLAVORATORE=<%=cdnLav%>&amp;CDNFUNZIONE=<%=_cdnFunz%>&amp;PRGDICHDISP=<%=prgDid%>&amp;pageDocAssociata=DispoDettaglioPage"/>
<input type="hidden" name="REFRESH_MAIN" value="true"/>

<input type="hidden" name="annoProt" value="<%=annoProt%>"/>
<input type="hidden" name="apri" value="true"/>
<input type="hidden" name="asAttachment" value="false"/>
<input type="hidden" name="dataOraProt" value="<%= dataOraProt%>"/>
<input type="hidden" name="docInOut" value="I"/>
<input type="hidden" name="numProt" value="<%=numProt%>"/>                                               
<input type="hidden" name="protAutomatica" value="S"/>                    
<input type="hidden" name="rptAction" value="RPT_STAMPA_DICH_ANNUALE"/>                                                                    
<input type="hidden" name="salvaDB" value="true"/>           
<input type="hidden" name="tipoDoc" value="IMDICANN"/>                           
<input type="hidden" name="tipoFile" value="PDF"/>
<input type="hidden" name="pagina" value="DispoDettaglioPage"/>
<input type="hidden" name="pageDocAssociata" value="DispoDettaglioPage"/>
<input type="hidden" name="pageReload" value="ConfermaAnnualeDidPage"/>
<input type="hidden" name="confirmOperation" value="false"/>
<input type="hidden" name="dataDichDid" value="<%= dataDichirazioneDid%>"/>
<input type="hidden" name="firmaGrafometrica" value="<%= firmaGrafometrica%>"/>
<input type="hidden" name="ipOperatore" value="<%= ipOperatore%>"/>

<br>
<center>
<table>
<tr><td>
<input class="pulsante" type="submit" name="BtnInserisci" value="Inserisci">&nbsp;&nbsp;
<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
</td></tr>
</table>
</center>
<%out.print(htmlStreamBottom);%>
</af:form>

<% if (apriDivAnn) {%>
	<af:form method="POST" action="AdapterHTTP" name="MainForm">
		<input type="hidden" name="firmaGrafometrica" value="<%= firmaGrafometrica%>"/>
		<input type="hidden" name="ipOperatore" value="<%= ipOperatore%>"/>
	<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
	     style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
	    <%out.print(divStreamTop);%>
		<table width="100%">
		<tr>
	        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false">
	        </td>
	        <td height="16" class="azzurro_bianco">
	        Dichiarazione annuale
	        </td>
	        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
	      </tr>
	    </table>
	    <br>
	    <table class="main" border="0">
		<tr>
		<td class="etichetta">Data dich.annuale &nbsp;</td>
		<td class="campo">
		<af:textBox classNameBase="input" type="date" name="DATDICHANNUALE" value="<%=dataDichiarazione%>"
			required="true" title="Data conferma dich.annuale"
		    validateOnPost="true" size="12" maxlength="10" readonly="true"/>
		</td>
		</tr>
		<tr>
		<td class="etichetta">Note &nbsp;</td>
		<td class="campo">
		<af:textArea classNameBase="textarea" name="strNote" value="<%=strNoteDich%>"
			cols="60" rows="4" maxlength="1000" readonly="true"/>
		</td>
		</tr>
		</table>
		
		<br>
		<center>
		<table>
		<tr><td>
		<input class="pulsante" type="submit" name="ANNULLADICH" value="Annulla Dichiarazione">
		</td></tr>
		</table>
		</center>
		
		<table>
		<tr><td align="center"> 
		<p align="center">
	  	<%if (operatoreInfo != null) {
	  		operatoreInfo.showHTML(out);
	  	}%>
        </p>
		</td>
		</tr>
		</table>
	  <%out.print(divStreamBottom);%>  
	  <input type="hidden" name="PAGE" value="ConfermaAnnualeDidPage"/>
	  <input type="hidden" name="PRGDIDANNUALE" value="<%=prgDidAnnuale%>"/>
	  <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLav%>"/>
	  <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
	  <input type="hidden" name="DATADICHDID" value="<%=dataDichirazioneDid%>"/>
	  <input type="hidden" name="PRGDICHDISP" value="<%=prgDid%>"/>
	  <input type="hidden" name="NUMKLODIDANN" value="<%=numkloConcorrenza.toString()%>"/>
	</div>
		
	
	</af:form>
<%}%>
</body>
</html>