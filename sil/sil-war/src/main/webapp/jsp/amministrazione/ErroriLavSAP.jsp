<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.sql.Clob, 
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>
 
<%
	// viene profilata la pagina di ricerca tramite MENU
	String _page = "SAPRicercaPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	String messaggioErrore ="";
	String xmlInviatoErrore="";
	String xmlDaScaricare="";
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {

		SourceBean datiLav = (SourceBean) serviceResponse.getAttribute("M_ErroriSAPLav.ROWS.ROW");

		if(datiLav != null){
			Object messValue = SourceBeanUtils.getAttr(datiLav, "messaggio");
			Clob messaggio = null;
			if (messValue != null) {
				try {
					if (messValue instanceof Clob) {
						messaggio = (Clob) messValue;
					}
				} catch (ClassCastException ignore) {
				}
			}
			if (messaggio != null) {
				messaggioErrore = messaggio.getSubString(1,	(int) messaggio.length());
 				messaggioErrore= StringUtils.formatValue4Html(messaggioErrore);
			}
			Object xmlInviatoValue = SourceBeanUtils.getAttr(datiLav,"xmlInviato");
			Clob xmlInviato = null;
			if (xmlInviatoValue != null) {
				try {
					if (xmlInviatoValue instanceof Clob) {
						xmlInviato = (Clob) xmlInviatoValue;
					}
				} catch (ClassCastException ignore) {
				}
			}
			if (xmlInviato != null) {
				xmlInviatoErrore = xmlInviato.getSubString(1,(int) xmlInviato.length());
				xmlDaScaricare = xmlInviatoErrore.replaceAll("'", "\\\\'");
				xmlInviatoErrore= StringUtils.formatValue4Html(xmlInviatoErrore);
			}
%>

<html>
<head>
<title>Errore SAP</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
 
function scarica(){
	var jsStringError ='<%=xmlDaScaricare%>';
	var data = "data:text/xml;charset=utf-8," + jsStringError;
 	window.open(data);
 	return false;
 }
 
/* Funzione chiamata al caricamento della pagina */
function onLoad() {
	rinfresca();
	/*var messXML = formatXml('< %=messaggioErrore%>');
 	document.getElementById("areaXmlMessage").innerHTML = messXML;
 	var stringaXML = formatXml('< %=xmlInviatoErrore%>');
 	document.getElementById("areaXmlError").innerHTML = stringaXML;*/
 }
</script>

</head>
<body class="gestione" onload="onLoad();">
	<p><strong>XML ERRORE</strong><br/>
	  <textarea rows="20" cols="40" style="border:none;width:100%;" id="areaXmlMessage"><%=messaggioErrore%></textarea>
	</p>
	<p><strong>XML INVIATO</strong><br/>
	
   <textarea rows="20" cols="40" style="border:none;width:100%;" id="areaXmlError"><%=xmlInviatoErrore%></textarea> 
	</p>
 	 	<center>
 	 		<input class="pulsante" type="button" name="Scarica" value="Scarica" onclick="scarica()" />
			<input class="pulsante" type="button" name="Chiudi" value="Chiudi" onclick="window.close()" />
		</center>
 	<br />
	
</body>
</html>
<%
		}else{
%>
<html>
<head>
<title>Errore SAP</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
 

</head>
<body class="gestione" onload="rinfresca();">
	<p><strong>NESSUNA INFO DISPONIBILE</strong>
 	</p> <center>
			<input class="pulsante" type="button" name="Chiudi" value="Chiudi" onclick="window.close()" />
		</center>
</body>
</html>
<%
	}
}
%>
 
 