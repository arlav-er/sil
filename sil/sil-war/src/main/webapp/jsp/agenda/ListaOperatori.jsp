<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,com.engiweb.framework.error.EMFErrorHandler,it.eng.afExt.utils.DateUtils,it.eng.sil.security.User,it.eng.afExt.utils.*,it.eng.sil.util.*,java.lang.*,java.text.*,java.math.*,java.sql.*,oracle.sql.*,java.util.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	BigDecimal prgSpi = null;
	String _page = (String) serviceRequest.getAttribute("PAGE");
	SourceBean serviziRows = (SourceBean) serviceResponse.getAttribute("MListaOperatori");
	SourceBean riga = null;
	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	BigDecimal prgOperatore = null;
	String strCognome = "";
	String strNome = "";
	String strSesso = "";
	String strCodiceFiscale = "";
	String strDataNascita = "";
	String btnSalva = "Inserisci";
	String btnAnnulla = "";
	String txtOut = "";

	String attr = "FSTRCOGNOME";
	String cognome = (String) serviceRequest.getAttribute(attr);
	if (cognome != null && !cognome.equals("")) {
		txtOut += "Cognome <strong>" + cognome + "</strong>; ";
	}
	attr = "FSTRNOME";
	String nome = (String) serviceRequest.getAttribute(attr);
	if (nome != null && !nome.equals("")) {
		txtOut += "Nome <strong>" + nome + "</strong>; ";
	}
	attr = "FSTRCODICEFISCALE";
	String cf = (String) serviceRequest.getAttribute(attr);
	if (cf != null && !cf.equals("")) {
		txtOut += "Codice Fiscale <strong>" + cf + "</strong>; ";
	}
	attr = "FSTRSIGLAOPERATORE";
	String siglaOp = (String) serviceRequest.getAttribute(attr);
	if (siglaOp != null && !siglaOp.equals("")) {
		txtOut += "Sigla Operatore <strong>" + siglaOp + "</strong>; ";
	}
	attr = "FDATNASC";
	String dataNascita = (String) serviceRequest.getAttribute(attr);
	if (dataNascita != null && !dataNascita.equals("")) {
		txtOut += "Data Nascita <strong>" + dataNascita + "</strong>; ";
	}
	attr = "VALIDI";
	String validi = (String) serviceRequest.getAttribute(attr);
	if (validi != null && !validi.equals("")) {
		txtOut += "Solo operatori validi <strong>Sì</strong>;";
	}
%>


<html>
<head>
<title>Lista Operatori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script type="text/javascript">
function go(url, alertFlag) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  var _url = "AdapterHTTP?" + url;
	  if (alertFlag == 'TRUE' ) {
	    if (confirm('Confermi operazione')) {
	      setWindowLocation(_url);
	    }
	  }
	  else {
	    setWindowLocation(_url);
	  }
	} 
    function goConfirm(codOperatore,funzione,alertFlag) {
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
    
      var s= "AdapterHTTP"
      if (alertFlag == 'CANCELLA') {
        if (confirm('Confermi Operazione')) {
          s += "?PAGE=GestOperatoriPage";
          s += "&MODULE=MDeleteOperatore";
          s += "&PRGSPI=" + codOperatore;
          s += "&CDNFUNZIONE=" + funzione;
          setWindowLocation(s);
        }
      }
      else {
        s += "?PAGE=DettaglioOperatorePage";
        s += "&PRGSPI=" + codOperatore;
        s += "&CDNFUNZIONE=" + funzione;
        setWindowLocation(s);
      }
    }
    
  </script>
</head>
<body class="gestione" onLoad="rinfresca();">

	<font color="red"> <af:showErrors />
	</font>
	<font color="green"> <af:showMessages prefix="MDeleteOperatore" />
		<af:showMessages prefix="MSalvaOperatore" /> <af:showMessages
			prefix="MAggiornaOperatore" />
	</font>

	<%
		txtOut = "Filtri di ricerca:<br/> " + txtOut;
	%>
	<table cellpadding="2" cellspacing="10" border="0" width="100%">
		<tr>
			<td
				style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
				<%
					out.print(txtOut);
				%>
			</td>
		</tr>
	</table>



	<af:list moduleName="MListaOperatori" />
	
	<center>
		<af:form method="POST" action="AdapterHTTP" dontValidate="true">
			<input type="hidden" name="PAGE" value="InsOperatorePage" />
			<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>" />

			<input type="hidden" name="FSTRCOGNOME" value="<%=cognome%>" />
			<input type="hidden" name="FSTRNOME" value="<%=nome%>" />
			<input type="hidden" name="FSTRCODICEFISCALE" value="<%=cf%>" />
			<input type="hidden" name="FSTRSIGLAOPERATORE" value="<%=siglaOp%>" />
			<input type="hidden" name="FDATNASC" value="<%=dataNascita%>" />
			<input type="hidden" name="VALIDI" value="<%=validi%>" />

			<input class="pulsanti" type="submit" name="inserisci"
				value="Nuovo operatore" />
			<input type="button" class="pulsanti" name="torna"
				value="Torna alla ricerca"
				onclick="go('PAGE=RicercaOperatoriPage&cdnFunzione=<%=_funzione%>&FSTRCOGNOME=<%=cognome%>&FSTRNOME=<%=nome%>&FSTRCODICEFISCALE=<%=cf%>&FSTRSIGLAOPERATORE=<%=siglaOp%>&FDATNASC=<%=dataNascita%>&VALIDI=<%=validi%>', 'FALSE')">

		</af:form>
	</center>
</body>
</html>
