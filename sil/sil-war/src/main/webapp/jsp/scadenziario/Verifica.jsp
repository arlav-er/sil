<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%
	int _funzione = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));
	PageAttribs attributi = new PageAttribs(user, "VerLavoratoriPage");
	boolean canVerStatoOcc = attributi
			.containsButton("CAMBIO_STATO_OCCUPAZ_LAV");
	boolean canVerMansDispTerr = attributi
			.containsButton("DISPONIBILITA_TERRITORIO_LAV");
	boolean canVerEsclusiRosa = attributi
			.containsButton("ESCLUSI_ROSA_LAV");
	boolean canVerNoMansPronti = attributi
			.containsButton("PRONTI_INC_NO_MANSIONI");
	boolean canVerCandidaInviate = attributi
			.containsButton("CANDIDATURE_INVIATE");

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpi = "";
	if (cdnTipoGruppo == 1) {
		codCpi = user.getCodRif();
	}
%>

<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=utf-8">
<title>Verifica</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script language="javascript">
  function GoVerifiche(nTipoVerifiche) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    switch(nTipoVerifiche) {
      case 1:
        document.Frm1.VERIFICA.value = "1";
        document.Frm1.PAGE.value = "SCADFiltriVerificheLavoratorePage";
        break;
        
      case 2:
        document.Frm1.VERIFICA.value = "2";
        document.Frm1.PAGE.value = "SCADFiltriVerificheLavoratorePage";
        break;

      case 3:
        document.Frm1.VERIFICA.value = "3";
        document.Frm1.PAGE.value = "SCADFiltriVerificheLavoratorePage";
        break;      
        
      case 4:
      	document.Frm1.VERIFICA.value="4";
      	document.Frm1.PAGE.value = "SCADFiltriVerificheLavoratorePage";  
		break;
      case 5:
    	  document.Frm1.VERIFICA.value="5";
          document.Frm1.PAGE.value = "SCADFiltriVerificheLavoratorePage";
       break;
    }
    doFormSubmit(document.Frm1);
  }
</script>
</head>
<body class="gestione" onload="rinfresca();">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<p class="titolo">Verifiche</p>
	<%
		out.print(htmlStreamTop);
	%>
	
	<table class="main" align="center">
		<%
			if (canVerStatoOcc) {
		%>
		<tr>
			<td class="etichetta2" nowrap>Soggetti con appuntamenti e stato
			occupazionale variato</td>
			<td class="campo2"><A href="javascript:GoVerifiche(1);"><img
				src="../../img/verifiche.gif" border="0"></img> </A></td>
		</tr>
		<%
			}

				if (canVerMansDispTerr) {
		%>
		<tr>
			<td class="etichetta2" nowrap>Soggetti senza disponibilità
			territoriale</td>
			<td class="campo2"><A href="javascript:GoVerifiche(2);"><img
				src="../../img/verifiche.gif" border="0"></img> </A></td>
		</tr>
		<%
			}

				if (canVerEsclusiRosa) {
		%>
		<tr>
			<td class="etichetta2" nowrap>Soggetti esclusi dalla rosa dopo la
			DID</td>
			<td class="campo2"><A href="javascript:GoVerifiche(3);"><img
				src="../../img/verifiche.gif" border="0"></img> </A></td>
		</tr>
		<%
			}

				if (canVerNoMansPronti) {
		%>
		<tr>
			<td class="etichetta2" nowrap>Soggetti pronti all'incrocio senza
			mansioni</td>
			<td class="campo2"><A href="javascript:GoVerifiche(4);"><img
				src="../../img/verifiche.gif" border="0"></img> </A></td>
		</tr>
		<%
			}

				if (canVerCandidaInviate) {
		%>
		<tr>
			<td class="etichetta2" width="50%" nowrap>Candidature inviate a Cliclavoro</td>
			<td class="campo2" width="50%"><A href="javascript:GoVerifiche(5);"><img
				src="../../img/verifiche.gif" border="0"></img> </A></td>
		</tr>
		<%
			}
		%>
	</table>
	<%
		out.print(htmlStreamBottom);
	%> <input type="hidden" name="CDNFUNZIONE"
		value="<%=_funzione%>" /> <input type="hidden" name="PAGE" value="" />
	<input type="hidden" name="VERIFICA" value="" /> <input type="hidden"
		name="CODCPI" value="<%=codCpi%>" />
</af:form>
</body>
</html>
