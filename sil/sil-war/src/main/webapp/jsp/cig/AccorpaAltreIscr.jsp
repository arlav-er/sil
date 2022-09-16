<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE"); 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
String cdnFunzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");

if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {

}


boolean canManage = true;

String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
String prgaltraiscr1 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgaltraiscr1");
String prgaltraiscr2 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgaltraiscr2");

String prgAzienda1 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgazienda1");
String prgAzienda2 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgazienda2");

String prgUnita1 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgunita1");
String prgUnita2 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgunita2");

String strCodiceFiscale1 = ""; 
String strragionesociale1 = "";
String datInizio1 = "";
String datFine1 = "";
String tipoIscr1 = "";
String codStato1 = "";
String codAccordo1 = "";
String datColloquio1 = "";
String datchiusura1 = "";
String motChiusura1 = "";
String strNote1 = "";


String strCodiceFiscale2 = ""; 
String strragionesociale2 = "";
String datInizio2 = "";
String datFine2 = "";
String tipoIscr2 = "";
String codStato2 = "";
String codAccordo2 = "";
String datColloquio2 = "";
String datchiusura2 = "";
String motChiusura2 = "";
String strNote2 = "";



if (serviceRequest.containsAttribute("ISCRIZIONI") ) {
	SourceBean infoAltraIscr1 = (SourceBean)serviceResponse.getAttribute("M_GetInfoAltraIscr.ISCR1");
	SourceBean infoAltraIscr2 = (SourceBean)serviceResponse.getAttribute("M_GetInfoAltraIscr.ISCR2");
	
	if (infoAltraIscr1 != null) {
		
		strCodiceFiscale1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.CF");
		strragionesociale1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.RAGSOC");
		datInizio1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATINIZIO");
		datFine1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATFINE");
		tipoIscr1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.TIPOISCR");
		codStato1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DESCRSTATO");
		codAccordo1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.CODACCORDO");
		datColloquio1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATCOLLOQUIO");
		datchiusura1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATCHIUSURA");
		motChiusura1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.MOTCHIUSURA");
		strNote1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.STRNOTE");
	}
	
	if (infoAltraIscr2 != null) {
		
		strCodiceFiscale2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.CF");
		strragionesociale2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.RAGSOC");
		datInizio2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATINIZIO");
		datFine2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATFINE");
		tipoIscr2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.TIPOISCR");
		codStato2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DESCRSTATO");
		codAccordo2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.CODACCORDO");
		datColloquio2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATCOLLOQUIO");
		datchiusura2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.DATCHIUSURA");
		motChiusura2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.MOTCHIUSURA");
		strNote2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.STRNOTE");
	}
}

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Accorpamento Altre Iscrizioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/" />

<script language="Javascript">

	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";

	//funzione che funge da costruttore per gli oggetti sezione
	function Sezione(sezione, img,aperta){    
    	this.sezione=sezione;
    	this.sezione.aperta=aperta;
    	this.img=img;
	}

	//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa
	function cambia(immagine, sezione) {
		if (sezione.aperta==null) sezione.aperta=true;
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src=imgChiusa;
    		immagine.alt="Apri";
		}
		else {
			sezione.style.display="";
			sezione.aperta=true;
			immagine.src=imgAperta;
    		immagine.alt="Chiudi";
		}
	}

	function cambiaLavMC(elem,stato){
  		divVar = document.getElementById(elem);
  		divVar.style.display = stato;
	}

	//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa per l'azienda, lav e mov
	function cambiaTendina(immagine,sezione) {
  		if (immagine.alt == "Apri"){
  			cambiaLavMC(sezione,"");
    		immagine.src=imgAperta;
    		immagine.alt="Chiudi";
  		} else {
    		cambiaLavMC(sezione,"none");
    		immagine.src=imgChiusa;
    		immagine.alt="Apri";
  		}
	}

	function getItem(name, n) {
    	o = document.getElementsByName(name+n);
    	return o[0];
    }
	
	
	function apriIscrizione(soggetto, funzionediaggiornamento,n) {
    	        var f = "AdapterHTTP?PAGE=ListaAccorpaIscrPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&numero=" + n + "&CDNFUNZIONE=<%= cdnFunzione %>" + "&cdnLavoratore=<%= cdnLavoratore %>";
				var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
    	        opened = window.open(f, "_blank", feat);
    	        
   }

   function aggiornaIscrizione(n,prgAzienda, prgUnita, datInizio, datFine, strragionesociale,cf,tipoIscr,codAccordo,descrStato,prgaltraiscr) {

	   if (getItem("prgaltraiscr", n%2+1).value == prgaltraiscr) {
   			alert("Impossibile scegliere due iscrizioni uguali!");
	    	opened.close();
   			return;
   		}

	    getItem("prgAzienda", n).value = prgAzienda;
	    getItem("prgUnita", n).value = prgUnita;
		getItem("prgaltraiscr", n).value = prgaltraiscr;
		getItem("datInizio", n).value = datInizio;
		getItem("datFine", n).value = datFine;
		getItem("strragionesociale", n).value = strragionesociale;
		getItem("codiceFiscaAzi", n).value = cf;
		getItem("tipoIscr", n).value = tipoIscr;
		getItem("codAccordo", n).value = codAccordo;
		getItem("codStato", n).value = descrStato;
		opened.close();
		cambiaLavMC("IscrizioneSez"+n,"");
		var imgV = document.getElementById("tendinaIscr"+n);
		imgV.src=imgAperta;
        imgV.alt = "Chiudi";
	}

	function azzeraIscrizione(n){
		getItem("prgAzienda", n).value = '';
		getItem("prgUnita", n).value = '';
		getItem("prgaltraiscr", n).value = '';
		getItem("datInizio", n).value = '';
		getItem("datFine", n).value = '';
		getItem("strragionesociale", n).value = '';
		getItem("codiceFiscaAzi", n).value = '';
		getItem("tipoIscr", n).value = '';
		getItem("codAccordo", n).value = '';
		getItem("codStato", n).value = '';
		var imgV = document.getElementById("tendinaIscr"+n);
        cambiaLavMC("IscrizioneSez"+n,"none");
        imgV.src=imgChiusa;
        imgV.alt = "Apri";
	}

	 function controllaIscr() {
	 	if (document.Frm1.prgaltraiscr1.value=='' || document.Frm1.prgaltraiscr2.value=='') {
	    	alert("seleziona due iscrizioni");
	    	return false;
	 	}    	
	    return true;
	 }

	 function gestisciSezione() {
		 if (document.Frm1.prgaltraiscr1.value!='' || document.Frm1.prgaltraiscr2.value!='') {
			cambiaLavMC("IscrizioneSez1","");
			var imgV = document.getElementById("tendinaIscr1");
			imgV.src=imgAperta;
		    imgV.alt = "Chiudi";

		    cambiaLavMC("IscrizioneSez2","");
		    var imgV = document.getElementById("tendinaIscr2");
			imgV.src=imgAperta;
		    imgV.alt = "Chiudi";
		}
		rinfresca();

		return true;
	}

</script>

</head>

<body class="gestione"  onload="gestisciSezione()">



<p class="titolo">Accorpamento Altre Iscrizioni</p>

<af:form name="Frm1" action="AdapterHTTP" method="GET" onSubmit="controllaIscr()">

	<af:textBox type="hidden" name="PAGE" value="AccorpaIscrDettaglioPage" />
	<af:textBox type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
	<af:textBox type="hidden" name="prgaltraiscr1" value="<%=prgaltraiscr1%>"/>
	<af:textBox type="hidden" name="prgaltraiscr2" value="<%=prgaltraiscr2%>"/>
	<af:textBox type="hidden" name="prgAzienda1" value="<%=prgAzienda1%>"/>
	<af:textBox type="hidden" name="prgAzienda2" value="<%=prgAzienda2%>"/>
	<af:textBox type="hidden" name="prgUnita1" value="<%=prgUnita1%>"/>
	<af:textBox type="hidden" name="prgUnita2" value="<%=prgUnita2%>"/>
	<af:textBox type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>"/>
	
	<%=htmlStreamTop%>
	<table class="main">

		<%-- ***************************************************************************** --%>
		<tr class="note">
			<td colspan="2">
			<div class="sezione2"><img id='tendinaIscr1' alt='Apri' src='../../img/chiuso.gif'
				 onclick="cambiaTendina(this,'IscrizioneSez1');" />&nbsp;&nbsp;&nbsp;Iscrizione 1&nbsp;&nbsp; 
				<% if (canManage) {%>
					<a href="#" onClick="javascript:apriIscrizione('Iscrizione', 'aggiornaIscrizione','1');"><img
								src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;&nbsp;<a href="#"
								onClick="javascript:azzeraIscrizione('1');"><img
								src="../../img/del.gif" alt="Azzera Iscrizione">
					</a>
				<%}%>
			</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="IscrizioneSez1" style="display:none">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo" valign="bottom">
						<af:textBox classNameBase="input" type="text" name="codiceFiscaAzi1" readonly="true"
									value="<%=strCodiceFiscale1%>" size="30" maxlength="16" /> 
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Ragione Sociale</td>
                	<td class="campo">
                		<af:textBox classNameBase="input" type="text" name="strragionesociale1" readonly="true" value="<%=strragionesociale1%>" size="50" maxlength="100"/>
                	</td>
				</tr>
				<tr>
       				<td class="etichetta">Data Inizio</td>
        			<td class="campo">
        				<af:textBox onKeyUp="fieldChanged();" title="Data inizio iscrizione" classNameBase="input" 
                					readonly="true"  type="date" name="datInizio1" value="<%=datInizio1%>" 
                					size="12" maxlength="10"/>
        			</td>
				</tr>
        		<tr>
       				<td class="etichetta">Data Fine</td>
        			<td class="campo">
        				<af:textBox onKeyUp="fieldChanged();" title="Data fine" classNameBase="input" 
                					readonly="true" type="date"
                    				name="datFine1" value="<%=datFine1%>" size="12" maxlength="10"/>
        			</td>
				</tr>
				<tr>
					<td class="etichetta">Tipo Iscrizione</td>
            		<td class="campo">
            			<af:textBox classNameBase="input" type="text" name="tipoIscr1" readonly="true" value="<%=tipoIscr1%>" size="30" maxlength="30"/>
                	</td>
        		</tr>
        		<tr>
					<td class="etichetta">Stato</td>
            		<td class="campo">
            			<af:textBox classNameBase="input" type="text" name="codStato1" readonly="true" value="<%=codStato1%>" size="50" maxlength="100"/>
    				</td>
         		</tr>
         		<tr>
					<td class="etichetta">Codice Domanda</td>
            		<td class="campo">
            			<af:textBox classNameBase="input" type="text" name="codAccordo1" readonly="true" value="<%=codAccordo1%>" size="30" maxlength="100"/> 
    				</td>
         		</tr>
			</table>
		</div>
		</td>
		</tr>
		
		<tr class="note">
			<td colspan="2">
			<div class="sezione2"><img id='tendinaIscr2' alt='Apri' src='../../img/chiuso.gif'
				onclick="cambiaTendina(this,'IscrizioneSez2');" />&nbsp;&nbsp;&nbsp;Iscrizione 2 &nbsp;&nbsp; 
				<%if (canManage) {%><a href="#"
				onClick="javascript:apriIscrizione('Iscrizione', 'aggiornaIscrizione','2');"><img
				src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;&nbsp;<a href="#"
				onClick="javascript:azzeraIscrizione('2');"><img
				src="../../img/del.gif" alt="Azzera Iscrizione"></a><%}%></div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="IscrizioneSez2" style="display:none">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale Azienda</td>
					<td class="campo" valign="bottom">
						<af:textBox classNameBase="input" type="text" name="codiceFiscaAzi2" readonly="true"
									value="<%=strCodiceFiscale2%>" size="30" maxlength="16" /> 
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Ragione Sociale</td>
                	<td class="campo">
                		<af:textBox classNameBase="input" type="text" name="strragionesociale2" readonly="true" value="<%=strragionesociale2%>" size="50" maxlength="100"/>
                	</td>
				</tr>
				<tr>
       				<td class="etichetta">Data Inizio</td>
        			<td class="campo">
        				<af:textBox onKeyUp="fieldChanged();" title="Data inizio iscrizione" classNameBase="input" 
                					readonly="true"  type="date" name="datInizio2" value="<%=datInizio2%>" 
                					size="12" maxlength="10"/>
        			</td>
				</tr>
        		<tr>
       				<td class="etichetta">Data Fine</td>
        			<td class="campo">
        				<af:textBox onKeyUp="fieldChanged();" title="Data fine" classNameBase="input" 
                					readonly="true" type="date"
                    				name="datFine2" value="<%=datFine2%>" size="12" maxlength="10"/>
        			</td>
				</tr>
				<tr>
					<td class="etichetta">Tipo Iscrizione</td>
            		<td class="campo">
            			<af:textBox classNameBase="input" type="text" name="tipoIscr2" readonly="true" value="<%=tipoIscr2%>" size="50" maxlength="100"/>
                	</td>
        		</tr>
        		<tr>
					<td class="etichetta">Stato</td>
            		<td class="campo">
            			<af:textBox classNameBase="input" type="text" name="codStato2" readonly="true" value="<%=codStato2%>" size="50" maxlength="100"/>
    				</td>
         		</tr>
         		<tr>
					<td class="etichetta">Codice Domanda</td>
            		<td class="campo">
            			<af:textBox classNameBase="input" type="text" name="codAccordo2" readonly="true" value="<%=codAccordo2%>" size="30" maxlength="100"/> 
    				</td>
         		</tr>
			</table>
		</div>
		</td>
		</tr>
		<tr>
			<td colspan="2">
			<%if (canManage) {%><input type="submit" class="pulsanti" value="Visualizza e confronta" /><%}%>
			</td>
		</tr>
		
	</table>
	<%=htmlStreamBottom%>
</af:form>
</body>
</html>
