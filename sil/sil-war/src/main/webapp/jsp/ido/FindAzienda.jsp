<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%  
  SourceBean row=null;
  
	//row=(SourceBean) serviceResponse.getAttribute("M_DYNRICERCAAZIENDEUNITA.ROWS.ROW");
  Vector listaAziende = serviceResponse.getAttributeAsVector("M_DYNRICERCAAZIENDEUNITA.ROWS.ROW");
  
%>

<html>
<head>
<title>Aziende</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">

<!--

function AggiornaForm (prgazienda, prgunita, codicefiscale, piva, ragionesociale, indirizzo, codicecomune, dencomune, codtipoaz)
{
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var i;
    opener.document.Frm1.prgAzienda.value = prgazienda;
    opener.document.Frm1.prgUnita.value = prgunita;
	opener.document.Frm1.cf.value = codicefiscale;
	opener.document.Frm1.piva.value = piva;
	opener.document.Frm1.RagioneSociale.value = ragionesociale.replace(/\^/g, '\'');
	opener.document.Frm1.Indirizzo.value = indirizzo.replace(/\^/g, '\'');    
    opener.document.Frm1.codCom.value = codicecomune;
    opener.document.Frm1.desComune.value = dencomune.replace(/\^/g, '\'');

    if (window.opener.document.Frm1.codTipoAzienda != null)
    {
      window.opener.document.Frm1.codTipoAzienda.options[0].selected = true;
      for (i = 1; i < window.opener.document.Frm1.codTipoAzienda.options.length; i++)
      {  
         if (window.opener.document.Frm1.codTipoAzienda.options[i].value == codtipoaz)                    
             window.opener.document.Frm1.codTipoAzienda.options[i].selected = true;
      } 
    }
    window.opener.disableFieldsAzienda();
    
    var url = "AdapterHTTP?PAGE=RiferimentiAziendaPage&prgAzienda=" + prgazienda + "&prgUnita="+ prgunita;
    setWindowLocation(url);
    
    window.opener.enableFields();
}

-->
</SCRIPT>

</head>
<body class="gestione">

<af:form dontValidate="true">

	<af:list moduleName="M_DynRicercaAziendeUnita" jsSelect="AggiornaForm"/>
<br/>

<center><input type="button" name="chiudi" class="pulsante" value="chiudi" onClick="javascript:window.close();"/></center>

</af:form>

</body>
</html>
