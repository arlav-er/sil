<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ page import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*"%>
<%@ taglib uri="aftags" prefix="af"%>

<%
  SourceBean rows_Approx=(SourceBean) serviceResponse.getAttribute("M_RICERCACITTADINANZA");

  String retcod=(String) serviceRequest.getAttribute("retcod");
  String retcittadinanza=(String) serviceRequest.getAttribute("retcittadinanza");
  String retnazione=(String) serviceRequest.getAttribute("retnazione");
%>

<html>
<head>
<title>Nazionalità</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm (ID, cit, naz) {
	  window.opener.document.Frm1.<%=retcod%>Hid.value = ID;
		window.opener.document.Frm1.<%=retcod%>.value = ID;
		window.opener.document.Frm1.<%=retcittadinanza%>Hid.value = cit.replace('^', '\'');
		window.opener.document.Frm1.<%=retcittadinanza%>.value = cit.replace('^', '\'');
 		window.opener.document.Frm1.<%=retnazione%>Hid.value = naz.replace('^', '\'');
		window.opener.document.Frm1.<%=retnazione%>.value = naz.replace('^', '\'');
  	window.close();
}

-->
</SCRIPT>

</head>
<body class="gestione">
<br/>
<center><input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="javascript:window.close();"/></center>
<br/>
<TABLE class="lista" align="center">
<%
Vector rows_ApproxVector = null;
if (rows_Approx != null) 
		rows_ApproxVector = rows_Approx.getAttributeAsVector("ROWS.ROW");  //
if (rows_ApproxVector.size()!=0) {
      SourceBean riga= null;
      String cittadinanza="";
      String codcittadinanza="";
      String nazione="";
      %>
      <TR>
      <TH class="lista">&nbsp;</TH>
      <TH class="lista">&nbsp;Nazione&nbsp;</TH>
      <TH class="lista">&nbsp;Cittadinanza&nbsp;</TH>
      </TR>
      <%
      for (int i = 0; i < rows_ApproxVector.size(); i++) {
            riga=(SourceBean) rows_ApproxVector.elementAt(i);
            cittadinanza=riga.getAttribute("strDescrizione").toString();
            codcittadinanza=riga.getAttribute("codCittadinanza").toString();
            nazione=riga.getAttribute("strNazione").toString();
            if (rows_ApproxVector.size()>1) {      
               out.print("<TR class=\"lista\"><TD class=\"lista\"><A HREF=\"javascript:AggiornaForm('" + codcittadinanza + "', '" + cittadinanza.replace('\'', '^') + "','"+nazione.replace('\'','^') + "');\"><IMG name=\"image\" border=\"0\"  src=\"../../img/add.gif\" alt=\"Inserisci\"/></A></td><td class=\"lista\">"+ nazione +"</td><TD class=\"lista\">" + cittadinanza  + "</td></TR>");
            }
            else { %>
               <SCRIPT TYPE="text/javascript">
                   <!--
                    AggiornaForm('<%=codcittadinanza%>', '<%=cittadinanza.replace('\'', '^')%>', '<%=nazione.replace('\'', '^')%>');
                    this.close;
                    -->
               </SCRIPT>
      <%    }
      }
  }  else {  %>
     <tr class="lista"><td class="lista">Nessun risultato trovato</td></tr>
     <script type="text/javascript">
      <!--
        window.opener.document.Frm1.<%=retcod%>Hid.value = '';
    		window.opener.document.Frm1.<%=retcod%>.value = '';
        window.opener.document.Frm1.<%=retcittadinanza%>Hid.value = '';
    		window.opener.document.Frm1.<%=retcittadinanza%>.value = '';
     		window.opener.document.Frm1.<%=retnazione%>Hid.value = '';
    		window.opener.document.Frm1.<%=retnazione%>.value = '';
      -->
     </script>
<%}%>
</table>
<af:form method="POST" action="AdapterHTTP" dontValidate="true">
<input type="hidden" name="PAGE" value="AnagDettaglioPage"/>
<input type="hidden" name="flag_insert" value="1"/>
</af:form>
<%//out.print(serviceResponse.toXML());%>
</body>
</html>