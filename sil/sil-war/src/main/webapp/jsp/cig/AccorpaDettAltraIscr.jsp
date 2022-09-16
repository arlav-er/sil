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

<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
    
    String cdnFunzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
	String prgaltraiscr1 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgaltraiscr1");
	String prgaltraiscr2 = StringUtils.getAttributeStrNotNull(serviceRequest,"prgaltraiscr2");
	
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
	String prgAzienda1 = "";
	String prgUnita1 = "";
	
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
	String prgAzienda2 = "";
	String prgUnita2 = "";
	
	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	boolean canView = filter.canView();
	if (! canView){
		response.sendRedirect("../../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canAltraIscr = attributi.containsButton("ACCORPA_ISCR"); 
	SourceBean infoAltraIscr1 = (SourceBean)serviceResponse.getAttribute("M_GetInfoAltraIscr.ISCR1");
	SourceBean infoAltraIscr2 = (SourceBean)serviceResponse.getAttribute("M_GetInfoAltraIscr.ISCR2");
	
	if (infoAltraIscr1 != null) {
		
		strCodiceFiscale1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.CF");
		strragionesociale1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.RAGSOC");
		datInizio1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.DATINIZIO");
		datFine1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.DATFINE");
		tipoIscr1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.TIPOISCR");
		codStato1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.DESCRSTATO");
		codAccordo1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.CODACCORDO");
		datColloquio1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.DATCOLLOQUIO");
		datchiusura1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.DATCHIUSURA");
		motChiusura1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.MOTCHIUSURA");
		strNote1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.STRNOTE");
		prgAzienda1 = StringUtils.getAttributeStrNotNull(infoAltraIscr1,"GET_INFO_ALTRA_ISCR.rows.row.PRGAZIENDA");
		//prgUnita1 = (String) infoAltraIscr1.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.PRGUNITA");
	}
	
	if (infoAltraIscr2 != null) {
		
		strCodiceFiscale2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.CF");
		strragionesociale2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.RAGSOC");
		datInizio2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.DATINIZIO");
		datFine2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.DATFINE");
		tipoIscr2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.TIPOISCR");
		codStato2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.DESCRSTATO");
		codAccordo2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.CODACCORDO");
		datColloquio2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.DATCOLLOQUIO");
		datchiusura2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.DATCHIUSURA");
		motChiusura2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.MOTCHIUSURA");
		strNote2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.STRNOTE");
		prgAzienda2 = StringUtils.getAttributeStrNotNull(infoAltraIscr2,"GET_INFO_ALTRA_ISCR.rows.row.PRGAZIENDA");
		//prgUnita2 = (String) infoAltraIscr2.getAttribute("GET_INFO_ALTRA_ISCR.rows.row.PRGUNITA");
	}
	 
	
	String readonly = "true";
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
<head>
<title>Accorpamento Altre Iscrizioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">

<af:linkScript path="../../js/"/>

<script language="Javascript">

	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;		
		goTo("Page=AccorpaAltreIscrPage&cdnfunzione=<%=cdnFunzione%>&prgaltraiscr1=<%=prgaltraiscr1%>&prgaltraiscr2=<%=prgaltraiscr2%>&cdnLavoratore=<%=cdnLavoratore%>&ISCRIZIONI=1");
	}

	function getItem(name, n) {
    	o = document.getElementsByName(name+n);
    	return o[0];
    }
	
	function accorpa (prgAltraIscrDaAccorpare, prgAltraIscrAccorpante, nIscr1, nIscr2) {
		var ok = false;
		var tipo = '';
		var periodo = '';
		var azienda = '';
		var strMsg = '';
		var mess = '';
		msg = "Verrà accorpata l'iscizione " + nIscr1 + " con la " + nIscr2 + ".\n"
		      + "L'iscrizione " + nIscr1 + " verrà cancellata.\n"
		      + "Procedere con l'accorpamento?";
		if (!confirm(msg))
			return;

		if(getItem("codStato",nIscr1).value == '' && getItem("codStato",nIscr2).value != '') {
			alert("Non è possibile accorpare un'iscrizione valida con una annullata");
			return;
		} else {
			if(document.Frm1.tipoIscr1.value != document.Frm1.tipoIscr2.value) {
				tipo = "\nstesso tipo di iscrizione ";
				ok = true;
			}
			if(document.Frm1.datInizio1.value != document.Frm1.datInizio2.value || document.Frm1.datFine1.value != document.Frm1.datFine2.value ) {
				periodo = "\nstesso periodo di iscrizione ";
				ok = true;
			}
			if(document.Frm1.prgAzienda1.value != document.Frm1.prgAzienda2.value) {
				azienda = "\nstessa Azienda ";
				ok = true;
			}
			if(ok) {
				strMsg = "Attenzione si sta tentando di accorpare due iscrizioni che non hanno: ";
				mess = "\nProcedere con l'accorpamento?"
				strMsg = strMsg + tipo + periodo + azienda + mess;
				if (!confirm(strMsg))
					return;
			}

			urlPage = "AdapterHTTP?PAGE=AccorpaIscrizioniPage&prgAltraIscrDaAccorpare="+prgAltraIscrDaAccorpare+
					  "&prgAltraIscrAccorpante="+prgAltraIscrAccorpante+"&cdnFunzione=<%=cdnFunzione%>"+
					  "&cdnLavoratore=<%=cdnLavoratore%>&codStato1=<%=codStato1%>&codStato2=<%=codStato2%>"+
					  "&codAccordo1=<%=codAccordo1%>&codAccordo2=<%=codAccordo2%>";
			setWindowLocation(urlPage);
		}
	}
	
	var sezioni = new Array();

	function cambia(immagine, sezione) {
		if (sezione.aperta==null) sezione.aperta=true;
		if (sezione.aperta) {
			sezione.style.display="";
			sezione.aperta=false;
			immagine.src="../../img/aperto.gif";
		}
		else {
			sezione.style.display="none";
			sezione.aperta=true;
			immagine.src="../../img/chiuso.gif";
		}
	}
	
	function Sezione(sezione, img,aperta){    
	    this.sezione=sezione;
	    this.sezione.aperta=aperta;
	    this.img=img;
	}

	function initSezioni(sezione){
		sezioni.push(sezione);
	}

</script>
<style type="text/css">
	td.colonna1 {
		width:16%;
		color: #000066; 
		font-family: Verdana, Arial, Helvetica, Sans-serif; 
		font-size: 11px;  
		text-align: right;
		font-weight: normal;
		text-decoration: none;
		padding-right: 12px;
	}
	td.colonna2, td.colonna3 {
		width:42%; 
		color: #000066; 
		font-family: Verdana, Arial, Helvetica, Sans-serif;  
		font-size: 11px; 
		text-align: left;
		text-decoration: none;
	}
	table.sezione2 {
		border-collapse:collapse;
		font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
		font-size: 12px;
		font-weight: bold;
		border-bottom-width: 2px; 
		border-bottom-style: solid;
		border-bottom-color: #000080;
		color:#000080; 
		position: relative;
		width: 94%;
		left: 4%;
		text-align: left;
		text-decoration: none;	
	}
</style>
</head>

<body class="gestione" onload="rinfresca()">

<p class="titolo">Accorpamento Altre Iscrizioni</p>

<af:showErrors />

<%= htmlStreamTop %>
<af:form name="Frm1" action="AdapterHTTP" method="POST">

<af:textBox type="hidden" name="prgAzienda1" value="<%= prgAzienda1 %>" />
<af:textBox type="hidden" name="prgAzienda2" value="<%= prgAzienda2 %>" />

<af:textBox type="hidden" name="prgaltraiscr1" value="<%= prgaltraiscr1 %>" />
<af:textBox type="hidden" name="prgaltraiscr2" value="<%= prgaltraiscr2 %>" />


	
<table class="main">
	<tr>
		<td>
			<table width="100%">
				<tr>
					<td class="colonna1">&nbsp;</td>
					<td class="colonna2" style="text-align:left">Iscrizione 1</td>
					<td class="colonna3" style="text-align:left">Iscrizione 2</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><div class="sezione">Dati iscrizioni</div></td>
	</tr>
	<tr>
		<td>
			<% { %>
				<%@ include file="_datiIscrizioni.inc" %>
			<%}%>	
		</td>		
	</tr>
	<tr>
		<td>
			<%{ %>
				<%@ include file="_datiColloquioAltraIscr.inc" %>
			<%}%>	       
		</td>		
	</tr>
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
	<%@ include file="_ListaPC.inc" %>  
	<%}%>
		</td>		
	</tr>	
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
	<%@ include file="_ListaCorsi.inc" %>  
	<%}%>
		</td>		
	</tr>		
	<tr>
		<td colspan="3">
			<table class="main">  
				<tr>
					<%if (canAltraIscr) {%>              
						<td class="colonna1">&nbsp;</td>
						<td class="colonna2" style="text-align:center">
							<input type="button" class="pulsanti"  value="Accorpa e cancella &gt;&gt;" 
								   onclick="accorpa(document.Frm1.prgaltraiscr1.value, document.Frm1.prgaltraiscr2.value,1,2 )">&nbsp;
						<td class="colonna2" style="text-align:center">
							<input type="button" class="pulsanti"  value="&lt;&lt; Accorpa e cancella"
								   onclick="accorpa(document.Frm1.prgaltraiscr2.value, document.Frm1.prgaltraiscr1.value, 2,1 )">
					<%}%>
				</tr>
				<tr colspan="3"><td>&nbsp;</td></tr>
				<tr>
					<td class="colonna1">&nbsp;</td>
					<td align="center" colspan="2">
						<input type="button" class="pulsante" name="back" value="Chiudi" onclick="goBack()" />
					</td>
				</tr>
			</table>	
		</td>
	</tr>
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>