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
  Testata operatoreInfo=null;
  BigDecimal prgSpi = null;
  BigDecimal prgSpiCurr = null;
  String strCognome="";
  String strNome="";
  String strCodiceFiscale="";
  String strSesso="";
  String strDataNascita="";
  String strSiglaOperatore="";
  String strTelOperatore="";
  String strEmailOperatore="";
  Vector operatoriRows=null;
  String dtmInizioVal="";
  String dtmFineVal="";
  String cdnUtins="";
  String cdnUtmod="";
  String dtmins="";
  String dtmmod="";
  
  SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  int nStart=strDataDefault.indexOf('\'');
  int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  strDataDefault=strDataDefault.substring (nStart+1,nEnd);
  
  
  operatoriRows=serviceResponse.getAttributeAsVector("MLISTAOPERATORI.ROWS.ROW");
  SourceBean row = null;
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String moduleName="MAggiornaOperatore";

  SourceBean cont = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOOPERATORE");
  SourceBean rowDettaglio = (SourceBean) cont.getAttribute("ROWS.ROW");
  prgSpi=(BigDecimal) rowDettaglio.getAttribute("PRGSPI");
  String strSpi=prgSpi.toString();
  
  strCognome=rowDettaglio.containsAttribute("STRCOGNOME") ? rowDettaglio.getAttribute("STRCOGNOME").toString() : "";
  strNome=rowDettaglio.containsAttribute("STRNOME") ? rowDettaglio.getAttribute("STRNOME").toString() : "";
  strCodiceFiscale=rowDettaglio.containsAttribute("STRCODICEFISCALE") ? rowDettaglio.getAttribute("STRCODICEFISCALE").toString() : "";
  strSesso=rowDettaglio.containsAttribute("STRSESSO") ? rowDettaglio.getAttribute("STRSESSO").toString() : "";
  strDataNascita= rowDettaglio.containsAttribute("DATNASC") ? rowDettaglio.getAttribute("DATNASC").toString() : "";
  strSiglaOperatore= rowDettaglio.containsAttribute("STRSIGLAOPERATORE") ? rowDettaglio.getAttribute("STRSIGLAOPERATORE").toString() : "";
  strTelOperatore= rowDettaglio.containsAttribute("STRTELOPERATORE") ? rowDettaglio.getAttribute("STRTELOPERATORE").toString() : "";
  strEmailOperatore= rowDettaglio.containsAttribute("STREMAIL") ? rowDettaglio.getAttribute("STREMAIL").toString() : "";
  dtmInizioVal=rowDettaglio.containsAttribute("DATINIZIOVAL") ? rowDettaglio.getAttribute("DATINIZIOVAL").toString() : "";
  dtmFineVal=rowDettaglio.containsAttribute("DATFINEVAL") ? rowDettaglio.getAttribute("DATFINEVAL").toString() : "";
  cdnUtins= rowDettaglio.containsAttribute("cdnUtins") ? rowDettaglio.getAttribute("cdnUtins").toString() : "";
  dtmins=rowDettaglio.containsAttribute("dtmins") ? rowDettaglio.getAttribute("dtmins").toString() : "";
  cdnUtmod=rowDettaglio.containsAttribute("cdnUtmod") ? rowDettaglio.getAttribute("cdnUtmod").toString() : "";
  dtmmod=rowDettaglio.containsAttribute("dtmmod") ? rowDettaglio.getAttribute("dtmmod").toString() : "";
  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);


  String btnSalva = "Aggiorna";
  String btnAnnulla = "Chiudi senza aggiornare";
  //PageAttribs attributi = new PageAttribs(user, "DettaglioOperatorePage");
  //boolean canModify = attributi.containsButton("salva");
  boolean canModify = true;
  if(!canModify) { btnAnnulla = "Chiudi"; }
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  String fcognome = (String) serviceRequest.getAttribute("FSTRCOGNOME");
  String fnome = (String) serviceRequest.getAttribute("FSTRNOME");
  String fcf = (String) serviceRequest.getAttribute("FSTRCODICEFISCALE");
  String fsiglaOp = (String) serviceRequest.getAttribute("FSTRSIGLAOPERATORE");
  String fdataNascita = (String) serviceRequest.getAttribute("FDATNASC");
  String validi = (String) serviceRequest.getAttribute("VALIDI");


%>


<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Operatore</title>

  <script language="JavaScript">

    var operatoreNome=new Array();
    var operatoreCodiceFisc=new Array();
    <%
    String strCg="";
    String strNm="";
    String strCF="";
    int nContElem=0;
    if (operatoriRows.size() > 0) {
      for(int i=0; i<operatoriRows.size(); i++)  { 
        row = (SourceBean) operatoriRows.elementAt(i);
        prgSpiCurr=(BigDecimal) row.getAttribute("PRGSPI");
        if (!prgSpiCurr.equals(prgSpi)) {
          strCg=row.containsAttribute("STRCOGNOME") ? row.getAttribute("STRCOGNOME").toString() : "";
          strNm=row.containsAttribute("STRNOME") ? row.getAttribute("STRNOME").toString() : "";
          strCF=row.containsAttribute("STRCODICEFISCALE") ? row.getAttribute("STRCODICEFISCALE").toString() : "";
          out.print("operatoreNome["+nContElem+"]=\""+ strCg + " " + strNm + "\";\n");
          out.print("operatoreCodiceFisc["+nContElem+"]=\""+ strCF + "\";\n"); 
          nContElem=nContElem+1;
        }
      }
    }
  %>
  </script>
  
</head>

<body class="gestione">
<p class="titolo">Consulta Operatore</p>
<%out.print(htmlStreamTop);%>
<%@ include file="dettaglio_operatore.inc" %>
<div align="center">
<%operatoreInfo.showHTML(out);%>
</div>
<%out.print(htmlStreamBottom);%>
</body>
</html>