<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
	// viene profilata la pagina di ricerca tramite MENU
	String _page = "RicCorsiFormazionePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
 	boolean isRicercaCF = false;
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
		
		
		String strTitoloCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"strTitoloCorso");
		String strCodiceCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceCorso");
		String annoCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"annoCorso");
		String dataInizioCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizioCorso");
		String dataFineCorso = StringUtils.getAttributeStrNotNull(serviceRequest, "dataFineCorso");
		String tipoCertificazione = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoCertificazione"); 
		String strEnte	= StringUtils.getAttributeStrNotNull(serviceRequest, "strEnte");
		String codComEnte = StringUtils.getAttributeStrNotNull(serviceRequest, "codComEnte");
		String descComune_H = StringUtils.getAttributeStrNotNull(serviceRequest, "descComune_H");
		String descTipoCert_H = StringUtils.getAttributeStrNotNull(serviceRequest, "descTipoCert_H");
		
 
		String txtOut = "";
		String parameters = "&cdnFunzione="+ _funzione;

        if (!strTitoloCorso.equals("")) {
        	parameters = parameters + "&strTitoloCorso="+strTitoloCorso;
        	txtOut += "Titolo Corso <strong>"+ strTitoloCorso +"</strong>; ";
        }
        if (!strCodiceCorso.equals("")) {
        	parameters = parameters + "&strCodiceCorso="+strCodiceCorso;
        	txtOut += "Codice Corso <strong>"+ strCodiceCorso +"</strong>; ";
        }
        if (!annoCorso.equals("")) {
        	parameters = parameters + "&annoCorso="+annoCorso;
        	txtOut += "Anno Corso <strong>"+ annoCorso +"</strong>; ";
        }
        if (!dataInizioCorso.equals("")) {
        	parameters = parameters + "&dataInizioCorso="+dataInizioCorso;
        	txtOut += "Data Inizio Corso <strong>"+ dataInizioCorso +"</strong>; ";
        }
        if (!dataFineCorso.equals("")) {
        	parameters = parameters + "&dataFineCorso="+dataFineCorso;
        	txtOut += "Data Fine Corso <strong>"+ dataFineCorso +"</strong>; ";
        }
        if (!descTipoCert_H.equals("")) {
        	parameters = parameters + "&tipoCertificazione="+tipoCertificazione;
        	txtOut += "Tipo Certificazione <strong>"+ descTipoCert_H +"</strong>; ";
        }
        if (!strEnte.equals("")) {
        	parameters = parameters + "&strEnte="+strEnte;
        	txtOut += "Descrizione Ente <strong>"+ strEnte +"</strong>; ";
        }
        if (!descComune_H.equals("")) {
        	parameters = parameters + "&codComEnte="+codComEnte;
        	txtOut += "Comune Ente <strong>"+ descComune_H +"</strong>; ";
        }
%>

<html>
<head>
<title>Lista Corsi Formazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	if (window.top.menu != undefined){
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}

	function tornaAllaRicerca() {
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
       	url="AdapterHTTP?PAGE="+"<%=_page%>" + "<%=parameters%>";
		setWindowLocation(url);
	} 	
	function AggiornaForm (PRGRDC, STRPROTOCOLLOINPS) {
       try{
    	   window.opener.document.Frm1.rdcProtocolloINPS.value = STRPROTOCOLLOINPS;
    	   window.opener.document.Frm1.prgRDC.value = PRGRDC;
    	   window.close();
       }
		catch(err) {
			
		}
	  
	}
</script>

</head>
<body class="gestione" onload="rinfresca();">
	<center>
		<font color="red"> 
			<af:showErrors />
		</font>
		
		<%if(txtOut.length() > 0)
          { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
            <table cellpadding="2" cellspacing="10" border="0" width="100%">
             <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
              <%out.print(txtOut);%>
             </td></tr>
            </table>
        <%}%>
        
	</center>


		<af:list moduleName="M_DynamicListaCorsiFormazione"  getBack="true"/>

		
		<center>
			<input class="pulsante" type="button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()" />
		</center>
	 
	<br />
	
</body>
</html>
<%}%>
