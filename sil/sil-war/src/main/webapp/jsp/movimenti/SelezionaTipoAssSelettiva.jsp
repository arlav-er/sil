<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.security.User,
    it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>


<%  

	String codTipoAss ="";
    String strDescrTipoAss="";
    //Estraggo il tipoAss e la descrizione dalla request
    codTipoAss = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoAss");
    strDescrTipoAss = StringUtils.getAttributeStrNotNull(serviceRequest, "descrTipoAss");
	
	//D'Auria Giovanni 10/03/2005	inizio
	String mesiApprendistato =	"";
	//serviceResponse.getAttribute("M_MOVGETNUMDURATAAPPRENDIST.ROWS.ROW.NUMMESIAPPRENDISTATO");
	if((boolean)serviceResponse.containsAttribute("M_MOVGETNUMDURATAAPPRENDIST.ROWS.ROW.NUMMESIAPPRENDISTATO")){
		mesiApprendistato = (SourceBeanUtils.getAttrBigDecimal(serviceResponse,"M_MOVGETNUMDURATAAPPRENDIST.ROWS.ROW.NUMMESIAPPRENDISTATO")).toString(); 
	}
	//D'Auria Giovanni 10/03/2005	fine
    //Estraggo dalla request il nome della funzione a cui passare i dati trovati
    String updateFunctionName = StringUtils.getAttributeStrNotNull(serviceRequest, "updateFunctionName");
     
    //Estraggo dal modulo la lista dei codTipoAss compatibili
    AbstractList assCompatibili = (AbstractList) serviceResponse.getAttributeAsVector("ListaAvviamentoSelettiva.ROWS.ROW");

    //Controllo se c'è uno e un solo codice/descrizione compatibile con quello scritto dall'utente
    boolean oneAndOnly = false;
    String codice = "";
    String descrizione = "";
   	String codcontratto = "";
    String codmonotipo = "";
    if (assCompatibili.size() == 1) {
      oneAndOnly = true; 
      SourceBean row = (SourceBean) assCompatibili.get(0);
      codice = StringUtils.getAttributeStrNotNull(row, "codice");
      //Escapizzo eventuali apici
      codice = StringUtils.replace(codice, "'", "\\\'");
      descrizione = StringUtils.getAttributeStrNotNull(row, "descrizione");
      //Escapizzo eventuali apici
      descrizione = StringUtils.replace(descrizione, "'", "\\\'");
      codmonotipo = StringUtils.getAttributeStrNotNull(row, "codmonotipo");
      codmonotipo = StringUtils.replace(codmonotipo, "'", "\\\'");
      codcontratto = StringUtils.getAttributeStrNotNull(row, "codcontratto");
    }
    
    //Modifica D'auria Giovanni 14/04/2005 inizio
    	String strPage="";
    	String codTipoAz="";
    	String codNatGiuridica="";
    	String codMonoTempo="";
    	String criterio="";
    	String codCCNLAzienda="";
    	
        strPage=StringUtils.getAttributeStrNotNull(serviceRequest, "PAGE");
    	codTipoAz=StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOAZIENDA");
    	codNatGiuridica=StringUtils.getAttributeStrNotNull(serviceRequest, "CODNATGIURIDICA");
    	codMonoTempo=StringUtils.getAttributeStrNotNull(serviceRequest, "codMonoTempo");
    	criterio=StringUtils.getAttributeStrNotNull(serviceRequest, "CRITERIO");
    	codCCNLAzienda=StringUtils.getAttributeStrNotNull(serviceRequest, "codCCNLAz");
    	String f="";
    	f = "AdapterHTTP?PAGE="+strPage+"&CODTIPOAZIENDA=" + codTipoAz + "&CODNATGIURIDICA=" + codNatGiuridica + "&codMonoTempo=" + codMonoTempo;
		f = f + "&CRITERIO=" + criterio;
		//f = f + "&descrTipoAss=" + strDescrTipoAss;
		f = f + "&updateFunctionName=" + updateFunctionName;
		f = f + "&codCCNLAz=" + codCCNLAzienda;
    	
    //Modifica D'auria Giovanni 14/04/2005 fine
   
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Lista Tipi Assunzione</title>

    <script type="text/javascript">
    
<!--
    function riportaCodice(codice, descrizione,codmonotipo,codcontratto, mesiApprendistato) {
   		if((codice == "NO7" || codice == "NB7") && <%=!oneAndOnly%>){
      		  setWindowLocation("<%=f%>&codTipoAss=" + codice + "&descrTipoAss=" + descrizione );

      }else{
     
	      window.opener.<%=updateFunctionName%>(codice, descrizione,codmonotipo,codcontratto, mesiApprendistato);
	      window.close();
      }
    }
-->
    </script>
</head>
<body class="gestione" onload="<%=(oneAndOnly ? "riportaCodice('" + codice + "', '" + descrizione + "','" + codmonotipo + "','" + codcontratto + "','" + mesiApprendistato + "');" : "")%>">

  <af:list moduleName="ListaAvviamentoSelettiva" jsSelect="riportaCodice"/>

  <table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td align="center">
      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
    </td>
  </tr>
  </table>
</body>
</html>