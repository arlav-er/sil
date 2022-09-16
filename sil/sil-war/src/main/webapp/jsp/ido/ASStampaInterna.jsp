<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,java.util.*,it.eng.sil.security.*,it.eng.sil.util.*,it.eng.afExt.utils.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%
	String prgRichiestaAz = (String) serviceRequest
			.getAttribute("prgRichiestaAz");
	String prgTipoIncrocio = (String) serviceRequest
			.getAttribute("prgTipoIncrocio");
	String descCpi = (String) serviceRequest.getAttribute("descCpi");
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String _cdnFunzione = (String) serviceRequest
			.getAttribute("CDNFUNZIONE");
	String prgAzienda = (String) serviceRequest
			.getAttribute("PRGAZIENDA");
	String prgUnita = (String) serviceRequest.getAttribute("PRGUNITA");

	String prgRosa = (String) serviceRequest.getAttribute("PRGROSA");
	String prgTipoRosa = (String) serviceRequest
			.getAttribute("PRGTIPOROSA");

	String codCpi = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest
			.getAttribute("ConcatenaCpi");

	String pageLink = "ASMatchDettGraduatoriaPage";
	String moduleLink = "ASCandidatiGraduatoria";
	String tipoStampa = "ALSEVO";
	if (("10").equalsIgnoreCase(prgTipoIncrocio)
			|| ("11").equalsIgnoreCase(prgTipoIncrocio)
			|| ("12").equalsIgnoreCase(prgTipoIncrocio)) {
		pageLink = "CMMatchDettGraduatoriaPage";
		moduleLink = "CMCandidatiGraduatoria";
		tipoStampa = "ALL68O";
	}

	PageAttribs attributi = new PageAttribs(user, _page);

	boolean stampaPos = false;
	boolean stampaAlf = false;

	stampaPos = attributi.containsButton("STAMPA_INTERNA_POS");
	stampaAlf = attributi.containsButton("STAMPA_INTERNA_ALF");

	String ConfigGraduatoria_cm = serviceResponse
			.containsAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm") ? serviceResponse
			.getAttribute(
					"M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm")
			.toString()
			: "0";

	String posizione = "";

	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

<%
	String queryString = null;
%>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script language="JavaScript">

function Stampa(posizione,tipo){
    if (tipo=="list")
		apriGestioneDoc('RPT_STAMPA_INTERNA', '&tipo=list&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&codCpi=<%=codCpi%>&ConcatenaCpi=<%=ConcatenaCpi%>&PRGROSA=<%=prgRosa%>&posizione='+posizione,'<%=tipoStampa%>')
	else
		apriGestioneDoc('RPT_STAMPA_INTERNA', '&tipo=grad&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&codCpi=<%=codCpi%>&ConcatenaCpi=<%=ConcatenaCpi%>&PRGROSA=<%=prgRosa%>&posizione='+posizione,'<%=tipoStampa%>')
}


function indietro() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

     url="AdapterHTTP?PAGE="+"<%=pageLink%>";  
     url += "&MODULE="+"<%=moduleLink%>";
     url += "&PRGRICHIESTAAZ="+"<%=prgRichiestaAz%>";
     url += "&PRGTIPOINCROCIO="+"<%=prgTipoIncrocio%>";
     url += "&CDNFUNZIONE="+"<%=_cdnFunzione%>";
     url += "&PRGAZIENDA="+"<%=prgAzienda%>";
     url += "&PRGUNITA="+"<%=prgUnita%>";
     url += "&PRGROSA="+"<%=prgRosa%>";
     url += "&PRGTIPOROSA="+"<%=prgTipoRosa%>";
      url += "&codCpi="+"<%=codCpi%>";
     url += "&ConcatenaCpi="+"<%=ConcatenaCpi%>";
     
     setWindowLocation(url);
 }

</script>
</head>
<body class="gestione" onLoad="rinfresca();">
<br />

<af:form name="form1" method="POST" action="AdapterHTTP">
	<%=htmlStreamTop%>



	<table class="main" cellpadding="2" cellspacing="2" width="100%">
		



		<%
			if ("3".equals(ConfigGraduatoria_cm)||"4".equals(ConfigGraduatoria_cm) ) {
		%>

		<tr>
			<td style="text-align: center;" colspan="2" rowspan="1">
			<p class="titolo">Elenco stampe interne</p>
			</td>
		</tr>

		<tr>
			<td>
			<p class="titolo">Graduatoria</p>
			</td>
			<td>
			<p class="titolo">Lista Fuori Graduatoria</p>
			</td>
		</tr>

		<tr>
			<%
				if (stampaPos) {
			%>
			<td align="left"><a href="#" onClick="Stampa('pos');"> <img
				name="dettImg" src="../../img/text.gif"
				alt="Stampa interna ordinata per posizione" /></a>&nbsp; <a href="#"
				onClick="Stampa('pos','grad');">Stampa interna ordinata per
			posizione</a></td>
			<%
				}
			%>
			<%
				if (stampaAlf) {
			%>
			<td align="left"><img name="dettImg" src="../../img/text.gif"
				alt="Stampa interna in ordine alfabetico" />&nbsp; <a href="#"
				onClick="Stampa('alf','list');">Stampa interna in ordine
			alfabetico</a></td>
			<%
				}
			%>


		</tr>
		<%
			if (stampaAlf) {
		%>
		<tr>
			<td align="left"><a href="#" onClick="Stampa('alf');"> <img
				name="dettImg" src="../../img/text.gif"
				alt="Stampa interna in ordine alfabetico" /></a>&nbsp; <a href="#"
				onClick="Stampa('alf','grad');">Stampa interna in ordine
			alfabetico</a></td>

		</tr>
		<%
			}
		%>

		<%
			} else {
		%>

		<tr>
			<td width="33%">&nbsp;</td>
			<td width="50%">&nbsp;</td>
			<td width="33%">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">
			<p class="titolo">Elenco stampe interne</p>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			if (stampaPos) {
		%>
		<tr>
			<td></td>
			<td align="left">&nbsp; <a href="#" onClick="Stampa('pos');">
			<img name="dettImg" src="../../img/text.gif"
				alt="Stampa interna ordinata per posizione" /></a>&nbsp; 
				<a href="#" onClick="Stampa('pos');">Stampa interna ordinata per posizione</a></td>
			<td></td>
		</tr>
		<%
			}
					if (stampaAlf) {
		%>
		<tr>
			<td></td>
			<td align="left">&nbsp; <a href="#" onClick="Stampa('alf');">
			<img name="dettImg" src="../../img/text.gif"
				alt="Stampa interna in ordine alfabetico" /></a>&nbsp; <a href="#"
				onClick="Stampa('alf');">Stampa interna in ordine alfabetico</a></td>
			<td></td>
		</tr>
		<%
			}
		%>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>







		<%
			}
		%>










	</table>
	<%=htmlStreamBottom%>

</af:form>

</body>
</html>
