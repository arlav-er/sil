<!-- @author: Giovanni Landi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String strSpi="";
  String strCognome="";
  String strNome="";
  String strCodiceFiscale="";
  String strSesso="";
  String strDataNascita="";
  String strSiglaOperatore="";
  String strTelOperatore="";
  String strEmailOperatore="";
  Vector operatoriRows=null;
  operatoriRows=serviceResponse.getAttributeAsVector("MLISTAOPERATORI.ROWS.ROW");
  SourceBean row = null;
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String dtmInizioVal="";
  String dtmFineVal="";
  String moduleName="MSalvaOperatore";
  String btnSalva="Inserisci";
  String btnAnnulla="Chiudi senza inserire";
  boolean canModify = true;
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  String fcognome = (String) serviceRequest.getAttribute("FSTRCOGNOME");
  String fnome = (String) serviceRequest.getAttribute("FSTRNOME");
  String fcf = (String) serviceRequest.getAttribute("FSTRCODICEFISCALE");
  String fsiglaOp = (String) serviceRequest.getAttribute("FSTRSIGLAOPERATORE");
  String fdataNascita = (String) serviceRequest.getAttribute("FDATNASC");
  String validi = (String) serviceRequest.getAttribute("VALIDI");
  
  SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  int nStart=strDataDefault.indexOf('\'');
  int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  strDataDefault=strDataDefault.substring (nStart+1,nEnd);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Nuovo Operatore</title>

  <script language="JavaScript">

    var operatoreNome=new Array();
    var operatoreCodiceFisc=new Array();
    <%
    String strCg="";
    String strNm="";
    String strCF="";
    if (operatoriRows.size() > 0) {
      for(int i=0; i<operatoriRows.size(); i++)  { 
        row = (SourceBean) operatoriRows.elementAt(i);
        strCg=row.containsAttribute("STRCOGNOME") ? row.getAttribute("STRCOGNOME").toString() : "";
        strNm=row.containsAttribute("STRNOME") ? row.getAttribute("STRNOME").toString() : "";
        strCF=row.containsAttribute("STRCODICEFISCALE") ? row.getAttribute("STRCODICEFISCALE").toString() : "";
        out.print("operatoreNome["+i+"]=\""+ strCg + " " + strNm + "\";\n");
        out.print("operatoreCodiceFisc["+i+"]=\""+ strCF + "\";\n");
      }
    }
  %>
  </script>
  
</head>

<body class="gestione">
<p class="titolo">Nuovo Operatore</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_operatore.inc" %>
<%out.print(htmlStreamBottom);%>
</body>
</html>