<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    it.eng.sil.security.User,
    it.eng.afExt.utils.*,it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>

    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  BigDecimal prgServizioArea=null;
  String _page = (String) serviceRequest.getAttribute("PAGE");
  SourceBean serviziRows=(SourceBean) serviceResponse.getAttribute("MListaServizi");
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  String labelServizio = "Servizio";
  String labelServizi = "Servizi";
  String umbriaGestAz = "0";
  if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
  	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
  }
  if(umbriaGestAz.equalsIgnoreCase("1")){
  	labelServizio = "Area";
  	labelServizi = "Aree";
  }
  
  SourceBean riga = null;
  String codServizio="";
  String strDescrizione="";
  String strDescrizioneArea="";
  String strDescrizioneAreaVis="";
  String strDescrizioneVisualizza="";
  String strDataInizio="";
  String strDataFine="";
  String btnSalva="Inserisci";
  String btnAnnulla="";
%>
<%String attr   = null;
  String descArea = null;
  String servizio = null;
  String codArea = null;
  String validi = null;
  String tipoAttivita = null;
  String prestazione = null;
  String polAttiva = "";
  String descTipoAttivita = null;
  String descPrestazione = null;
  BigDecimal prgTipoAttivita=null;
  BigDecimal prgPrestazione=null;
  
  String txtOut = "";
%>
     <%attr= "CODSERVIZIO";
       codServizio = (String) serviceRequest.getAttribute(attr);
       if(codServizio != null && !codServizio.equals(""))
       {txtOut += "Codice " + labelServizio + " <strong>"+ codServizio +"</strong>; ";
       }%>
       
      <%attr= "SERVIZIO";
       servizio = (String) serviceRequest.getAttribute(attr);
       if(servizio != null && !servizio.equals(""))
       {txtOut += labelServizio + " <strong>"+ servizio +"</strong>; ";
       }%>
     <%attr= "DESCAREA";
       descArea = (String) serviceRequest.getAttribute(attr);
       if(descArea != null && !descArea.equals(""))
       {txtOut += "Area Appartenenza <strong>"+ descArea +"</strong>; ";
       }%>
     <%attr= "VALIDI";
       validi = (String) serviceRequest.getAttribute(attr);
       if(validi != null && !validi.equals(""))
       {txtOut += "Solo codifiche valide <strong>Sì</strong>;";
       }%>   

       <%attr= "DESCTIPOATTIVITA";
       descTipoAttivita = (String) serviceRequest.getAttribute(attr);
       if(descTipoAttivita != null && !descTipoAttivita.equals(""))
       {txtOut += "Tipo attivit&agrave; <strong>"+ descTipoAttivita + "</strong>;";
       }%>   
       
       <%attr= "DESCPRESTAZIONE";
       descPrestazione = (String) serviceRequest.getAttribute(attr);
       if(descPrestazione != null && !descPrestazione.equals(""))
       {txtOut += "Prestazione <strong>"+ descPrestazione + "</strong>;";
       }%>   
       
       <%attr= "POLATTIVA";
       polAttiva = (String) serviceRequest.getAttribute(attr);
       if(polAttiva != null && !polAttiva.equals("")&& !polAttiva.equalsIgnoreCase("N"))
       {txtOut += "Politica attiva <strong>Sì</strong>;";
       }%>   

      <%codArea = (String) serviceRequest.getAttribute("codArea");    %>
      <%tipoAttivita = (String) serviceRequest.getAttribute("tipoAttivita");    %>
      <%prestazione = (String) serviceRequest.getAttribute("prestazione");    %>
      <%//polAttiva = (String) serviceRequest.getAttribute("polAttiva");    %>

      <%
      	NavigationCache sceltaUnitaAzienda = null;
	  	String[] fields = {"POLATTIVA", "PRESTAZIONE", "TIPOATTIVITA"};
	  	sceltaUnitaAzienda = new NavigationCache(fields);
      	if (sessionContainer.getAttribute("SERVIZIOCACHE") != null)
      	{
      		sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SERVIZIOCACHE");
        	tipoAttivita = sceltaUnitaAzienda.getField("TIPOATTIVITA").toString();   
        	prestazione = sceltaUnitaAzienda.getField("PRESTAZIONE").toString();
       		polAttiva = sceltaUnitaAzienda.getField("POLATTIVA").toString();

      	}
      	else
      	{
      		sceltaUnitaAzienda.enable();
		  	sceltaUnitaAzienda.setField("TIPOATTIVITA", tipoAttivita);
		  	sceltaUnitaAzienda.setField("PRESTAZIONE", prestazione);
		  	sceltaUnitaAzienda.setField("POLATTIVA", polAttiva);
		  	if (polAttiva == null)
		  		sceltaUnitaAzienda.setField("POLATTIVA", "");
		  	if (tipoAttivita == null)
		  		sceltaUnitaAzienda.setField("TIPOATTIVITA", "");
		  	if (prestazione == null)
		  		sceltaUnitaAzienda.setField("PRESTAZIONE", "");
			sessionContainer.setAttribute("SERVIZIOCACHE", sceltaUnitaAzienda);		
		  	
      	}
      %>


       
      
<html>
<head>
  <title>Lista <%=labelServizi %></title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  function go(url, alertFlag) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  var _url = "AdapterHTTP?" + url;
	  if (alertFlag == 'TRUE' ) {
	    if (confirm('Confermi operazione')) {
	      setWindowLocation(_url);
	    }
	  }
	  else {
	    setWindowLocation(_url);
	  }
	} 

  function goConfirm(codServizio,funzione,alertFlag) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP"
    if (alertFlag == 'CANCELLA') {
      if (confirm('Confermi Operazione')) {
        s += "?PAGE=GestServiziPage";
        s += "&MODULE=MDeleteServizio";
        s += "&CODSERVIZIO=" + codServizio;
        s += "&PRGPRESTAZIONE=" + prgPrestazione;
        s += "&PRGTIPOATTIVITA=" + prgTipoAttivita;
        s += "&POLATTIVA=" + polAttiva;
        s += "&CDNFUNZIONE=" + funzione;
        setWindowLocation(s);
      }
    }
    else {
      s += "?PAGE=DettaglioServizioPage";
      s += "&CODSERVIZIO=" + codServizio;
      s += "&CDNFUNZIONE=" + funzione;
      setWindowLocation(s);
    }
  }
  </script>
</head>
<body class="gestione">

<font color="red">
  <af:showErrors/>
</font>
<font color="green">
  <af:showMessages prefix="MDeleteServizio"/>
  <af:showMessages prefix="MSalvaServizio"/>
  <af:showMessages prefix="MAggiornaServizio"/>
</font>

<p align="center">
<% txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%%>

<af:list moduleName="MListaServizi"/>
<center>
<af:form method="POST" action="AdapterHTTP" dontValidate="true">
	<input type="hidden" name="PAGE" value="InsServiziPage"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<input  type="hidden" name="DESCAREA" value="<%=descArea%>"/>    
	<input  type="hidden" name="CODAREA" value="<%=codArea%>"/>   
	<input  type="hidden" name="CODSERVIZIO" value="<%=codServizio%>"/>          
	<input  type="hidden" name="SERVIZIO" value="<%=servizio%>"/>            
	<input  type="hidden" name="VALIDI" value="<%=validi==null?"":validi%>"/>  
	<input  type="hidden" name="TIPOATTIVITA" value="<%=tipoAttivita%>"/>
	<input  type="hidden" name="PRESTAZIONE" value="<%=prestazione%>"/>  
	<input  type="hidden" name="POLATTIVA" value="<%=polAttiva==null?"":polAttiva%>"/>  
	<input  type="hidden" name="DESCTIPOATTIVITA" value="<%=descTipoAttivita%>"/>    
	<input  type="hidden" name="DESCPRESTAZIONE" value="<%=descPrestazione%>"/>
<%	String nuovoPuls = "Nuova "; 
	if(umbriaGestAz.equalsIgnoreCase("0")){
		nuovoPuls = "Nuovo ";
	}
	nuovoPuls = nuovoPuls + labelServizio;
%>
	<input class="pulsanti" type="submit" name="inserisci" value="<%=nuovoPuls%>" />
	<input type="button" class="pulsanti"  name = "torna" value="Torna alla ricerca" 
	onclick= "go('PAGE=RicercaServiziPage&cdnFunzione=<%=_funzione%>&CODAREA=<%=codArea%>&CODSERVIZIO=<%=codServizio%>&SERVIZIO=<%=servizio%>&VALIDI=<%=validi%>&TIPOATTIVITA=<%=tipoAttivita%>&PRESTAZIONE=<%=prestazione%>&POLATTIVA=<%=polAttiva%>', 'FALSE')">

</af:form>
</center>
</body>
</html>