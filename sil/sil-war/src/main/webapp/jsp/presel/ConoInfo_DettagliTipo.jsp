<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  /*
	ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
	SourceBean serviceResponse = responseContainer.getServiceResponse();

  RequestContainer requestContainer= RequestContainerAccess.getRequestContainer(request);
  SourceBean serviceRequest=requestContainer.getServiceRequest();
  */
  Vector vectDettagliTipo= serviceResponse.getAttributeAsVector("M_LISTDETTAGLICONOSCENZAINFO.ROWS.ROW");
%>

<html>

<head>
  <title>Dettagli del Tipo di Conoscenza Informatica</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
    function DettaglioSelect(id, desc) {

      window.opener.MainForm.CODDETTINFO.value = id;
      window.opener.MainForm.DESCDETTINFO.value = desc.replace('^', '\'');

      window.close();
    }
  </SCRIPT>

</head>

<body class="gestione">

  <TABLE class="lista" align="center">
    <TR>
      <TH class="lista">&nbsp;</TH>
      <TH class="lista">&nbsp;Descrizione&nbsp;</TH>
    </TR>
  
  <%
    for (Iterator iter= vectDettagliTipo.iterator(); iter.hasNext();) {

      SourceBean beanDettaglio= (SourceBean)iter.next();

      Object codice      = beanDettaglio.getAttribute("CODICE");
      String descrizione = (String)beanDettaglio.getAttribute("DESCRIZIONE");

  %>
      <TR class="lista">
        <TD class="lista">   
          <A HREF="javascript:DettaglioSelect('<%= codice %>', '<%= descrizione.replace('\'', '^') %>');" >
            <IMG name="image" border="0" src="../../img/detail.gif" alt="Descrizione" />
          </A>
        </TD>
        <TD class="lista"><%= descrizione %></td>
      </TR> 
  <%
    }
  %>

  </table>
  
</body>

</html>
