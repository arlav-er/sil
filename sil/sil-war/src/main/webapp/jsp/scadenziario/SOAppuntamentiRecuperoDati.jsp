<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");
String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");

%>
<html>
<head>
<af:linkScript path="../../js/" />
<script language="javascript">
function CaricaForm() {
  document.Frm1.PAGEPROVENIENZA.value = parent.localPageProvenienza; 
  document.Frm1.CDNFUNZIONE.value = parent.localFunzione;
  document.Frm1.PRGAZIENDA.value = parent.strPrgAzienda;
  document.Frm1.PRGUNITA.value = parent.strPrgUnita;
  document.Frm1.CDNLAVORATORE.value = parent.strCdnLavoratore;
  document.Frm1.CODCPI.value = parent.codice;
  document.Frm1.DATADAL.value = parent.data_dal;
  document.Frm1.DATAAL.value = parent.data_al;
  document.Frm1.dataDalSlot.value = parent.dataDalSlotLocal;
  document.Frm1.codServizio.value = parent.codServizioLocal;
  
  doFormSubmit(document.Frm1);
}
</script>
</head>
<body>
<af:form name="Frm1" action="AdapterHTTP?PAGE=SOScadAppuntamentiInseritiPage" method="POST" dontValidate="true">
<input type="hidden" name="PAGEPROVENIENZA" value="">
<input type="hidden" name="CDNFUNZIONE" value="">
<input type="hidden" name="PRGAZIENDA" value="">
<input type="hidden" name="PRGUNITA" value="">
<input type="hidden" name="CDNLAVORATORE" value="">
<input type="hidden" name="CODCPI" value="">
<input type="hidden" name="DATAAL" value="">
<input type="hidden" name="DATADAL" value="">


<input type="hidden" name="dataDalSlot" value="">
<input type="hidden" name="codServizio" value="">


</af:form>

<script language="javascript">
  CaricaForm();
</script>

</body>
</html>