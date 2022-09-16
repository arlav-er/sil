<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  SourceBean aziendeRows=(SourceBean) serviceResponse.getAttribute("M_DynRicercaAziende");
%>

<html>
<head>
<title>Aziende</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm (codAteco, tipoAteco, desAteco, codFiscale, pIva, ragSoc, indirizzo, codcom, descom, codnatgiur)
{
	  window.opener.document.Frm1.codAteco.value = codAteco;
 	  window.opener.document.Frm1.codAtecoHid.value = codAteco;
    window.opener.document.Frm1.strTipoAteco.value = tipoAteco;
    window.opener.document.Frm1.strAteco.value = desAteco;
    window.opener.document.Frm1.strCodFiscaleAzienda.value=codFiscale;
    window.opener.document.Frm1.strPartitaIvaAzienda.value=pIva;
    window.opener.document.Frm1.strRagSocialeAzienda.value=ragSoc;
    window.opener.document.Frm1.strIndirizzoAzienda.value=indirizzo;
    window.opener.document.Frm1.codComAzienda.value=codcom;
    window.opener.document.Frm1.codComAziendaHid.value=codcom;
    window.opener.document.Frm1.strComAzienda.value=descom;
    window.opener.document.Frm1.strComAziendaHid.value=descom;
    window.opener.document.Frm1.codNatGiuridica.value=codnatgiur;
    
  	window.close();
}

-->
</SCRIPT>

</head>
<body class="gestione" onLoad="resizeTo(700,400);"
>
<br/>
<center><input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="javascript:window.close();"/></center>
<br/>
<TABLE class="lista" align="center">
<%
  Vector rows_Vector = null;
  rows_Vector = aziendeRows.getAttributeAsVector("ROWS.ROW"); 
  if (rows_Vector.size()!=0) { %>
<TR>
<TH class="lista">&nbsp;</TH>
<TH class="lista">&nbsp;CF&nbsp;</TH>
<TH class="lista">&nbsp;P.IVA&nbsp;</TH>
<TH class="lista">&nbsp;Azienda&nbsp;</TH>
<TH class="lista">&nbsp;Indirizzo&nbsp;</TH>
<TH class="lista">&nbsp;Comune&nbsp;</TH>
</TR>
<%        SourceBean riga= null;
        String codFiscale="";
        String pIva="";
        String indirizzo="";
        String strRagioneSociale="";
        String descom="";
        String provincia="";
        String codcom="";
        String codAteco="";
        String desAteco="";
        String tipoAteco="";
        String codnatgiur="";
     	for (int i = 0; i < rows_Vector.size(); i++) {
          riga=(SourceBean) rows_Vector.elementAt(i); 
          codAteco=StringUtils.getAttributeStrNotNull(riga, "codAteco");
          tipoAteco=StringUtils.getAttributeStrNotNull(riga, "tipoAteco");
          desAteco=StringUtils.getAttributeStrNotNull(riga, "desAteco");
          codFiscale=StringUtils.getAttributeStrNotNull(riga, "strCodiceFiscale");
          pIva=StringUtils.getAttributeStrNotNull(riga, "strPartitaIva");
          indirizzo=StringUtils.getAttributeStrNotNull(riga, "strIndirizzo");
          strRagioneSociale=StringUtils.getAttributeStrNotNull(riga, "strRagioneSociale");
          codcom=StringUtils.getAttributeStrNotNull(riga, "codCom");
          descom=StringUtils.getAttributeStrNotNull(riga, "descom");
          provincia=StringUtils.getAttributeStrNotNull(riga, "provincia");
          provincia=(provincia!="")?" ("+provincia+")":"";
          codnatgiur=StringUtils.getAttributeStrNotNull(riga, "codnatgiur");
	            out.print("<TR class=\"lista\"><TD class=\"lista\">" +
                         "<A HREF=\"javascript:AggiornaForm('" + codAteco + "','" + StringUtils.replace(tipoAteco,"\'", "\\\'") + "','" + 
                          StringUtils.replace(desAteco, "\'", "\\\'")+ "','" + codFiscale + "','" + pIva + "','" + 
                          StringUtils.replace(StringUtils.replace(strRagioneSociale,"\"", ""),"\'","\\\'") +  "','" + StringUtils.replace(indirizzo, "\'", "\\\'") + "','" + 
                          codcom + "','" + StringUtils.replace(descom,"\'", "\\\'") + "','" + codnatgiur + "');\"><IMG name=\"image\" border=\"0\"  src=\"../../img/add.gif\" alt=\"Dettaglio\"/></A></td><td class=\"lista\">"+
                          codFiscale +"</TD><td class=\"lista\">"+ 
                          pIva +"</TD><td class=\"lista\">"+ strRagioneSociale +"</TD><td class=\"lista\">"+ 
                          indirizzo +"</TD><td class=\"lista\">"+ descom + provincia + "</TD></TR>");
      }

}//if row_Vector!=null
    else {  %>
    <tr class="lista"><td class="lista">Nessun risultato trovato</td></tr>
<%    } %>
</table>
<af:form method="POST" action="AdapterHTTP" dontValidate="true">
<input type="hidden" name="PAGE" value="AnagDettaglioPage"/>
<input type="hidden" name="flag_insert" value="1"/>
</af:form>
</body>
</html>
