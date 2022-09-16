<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Riferimenti
</title>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">

function  Riferimento() {
      this.Nome=null;
      this.Cognome=null;
      this.Telefono=null;
      this.Fax=null;
      this.E_mail=null;
      this.copia = copia;
}

 

var ArrayRif = new Array ();

function carica()
{ if (window.opener.CaricaRiferimenti!=null)
  { window.opener.CaricaRiferimenti(ArrayRif);
  }
  window.close();
}


function copia(rifer)
{
rifer.Nome = this.Nome;
rifer.Cognome = this.Cognome;
rifer.Telefono = this.Telefono;
rifer.Fax = this.Fax;
rifer.E_mail = this.E_mail;
return (rifer);
}
<%





String strCognomeRiferimento="";
String strNomeRiferimento="";
String strTelRiferimento="";
String strFaxRiferimento="";
String strEmailRiferimento="";
SourceBean riga= null;

Vector RiferimentiAzienda = serviceResponse.getAttributeAsVector("M_GETREFERENTIAZIENDA.ROWS.ROW");

if (RiferimentiAzienda != null) {
    for (int i = 0; i < RiferimentiAzienda.size(); i++) {
          riga=(SourceBean) RiferimentiAzienda.elementAt(i);
                strCognomeRiferimento     =StringUtils.getAttributeStrNotNull(riga, "STRCOGNOME");
                strNomeRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STRNOME");
                strTelRiferimento     =StringUtils.getAttributeStrNotNull(riga, "STRTELEFONO");
                strFaxRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STRFAX");
                strEmailRiferimento     = StringUtils.getAttributeStrNotNull(riga, "STREMAIL");

%>

 var riferimenti = new Riferimento();
  riferimenti.Cognome="<%=strCognomeRiferimento%>";
  riferimenti.Nome="<%=strNomeRiferimento%>";
  riferimenti.Telefono="<%=strTelRiferimento%>";
  riferimenti.Fax="<%=strFaxRiferimento%>";
  riferimenti.E_mail="<%=strEmailRiferimento%>";
  ArrayRif.push(riferimenti);
<% } }%>

</SCRIPT>

</head>
<body onload="carica();">
</body>
</html>
