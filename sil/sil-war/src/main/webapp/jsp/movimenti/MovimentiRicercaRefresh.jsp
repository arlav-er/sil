<!-- @author: Paolo Roccetti -->
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

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
    
<%@ taglib uri="aftags" prefix="af"%>

<%  
    //prelevo i dati della ricerca
    String soggetto = (String) serviceRequest.getAttribute("MOV_SOGG");
    String funzioneaggiornamento = serviceRequest.containsAttribute("AGG_FUNZ")?serviceRequest.getAttribute("AGG_FUNZ").toString():"";
    if (funzioneaggiornamento.equals("")) {
      funzioneaggiornamento = serviceRequest.containsAttribute("AGG_FUNZ_INS_UNITA")?serviceRequest.getAttribute("AGG_FUNZ_INS_UNITA").toString():"";
    }

    //Caso di ricerca su aziende
    String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzienda");
    String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita");
    String ragioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest, "strRagioneSociale");
    String partitaIva = StringUtils.getAttributeStrNotNull(serviceRequest, "STRPARTITAIVA");
    String codiceFiscaleAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "STRCODICEFISCALEAZIENDA");
    String strIndirizzoAzienda = StringUtils.replace(StringUtils.getAttributeStrNotNull(serviceRequest, "STRINDIRIZZO"),"\"","\\\"");
    String comuneAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "comune_az");
    String codComune = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCOM");
    String provAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "prov_az");
    String strTelAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "STRTEL");
    String strFaxAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "STRFAX");
    String strCapAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "STRCAP");
    String codTipoAz = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoAzienda");
    String natGiurAz = StringUtils.getAttributeStrNotNull(serviceRequest, "natGiurAz");
    String descrTipoAz = StringUtils.getAttributeStrNotNull(serviceRequest, "descrTipoAz");
    String numalbointerinali = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNUMALBOINTERINALI");
    String numregcommitt = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNUMREGCOMM");
    String strdescrizioneccnl = StringUtils.getAttributeStrNotNull(serviceRequest, "STRDESCRIZIONECCNL");
    String ccnlaz = StringUtils.getAttributeStrNotNull(serviceRequest, "CCNLAZ");  
    String codAteco = StringUtils.getAttributeStrNotNull(serviceRequest, "codAteco");
    String strDesAtecoUAz = StringUtils.getAttributeStrNotNull(serviceRequest, "strDesAtecoUAz");
    String strPatInail = StringUtils.getAttributeStrNotNull(serviceRequest, "strPatInail");
    String strNumeroInps = StringUtils.getAttributeStrNotNull(serviceRequest, "strNumeroInps");
    String codNatGiurAz = StringUtils.getAttributeStrNotNull(serviceRequest, "CODNATGIURIDICA");
    String strReferente = StringUtils.getAttributeStrNotNull(serviceRequest, "STRREFERENTE");    
    String strFlgDatiOK = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGDATIOK");
    String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI"); 
    
    String codRegione = StringUtils.getAttributeStrNotNull(serviceRequest, "CODREGIONE");   
    
    //Caso di ricerca su lavoratori
    String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
    String codiceFiscaleLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscaleLavoratore");
    String cognome = StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
    String nome = StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
    String datNasc = StringUtils.getAttributeStrNotNull(serviceRequest, "datNasc");
    String codCpiLav = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPILAV");
    
    // Nel nuovo sviluppo dei movimenti deve essere visualizzato anche lo stato occupazionale
    String descrStatoOcc = StringUtils.getAttributeStrNotNull(serviceRequest, "descrStatoOcc");
    String datInizioOcc = StringUtils.getAttributeStrNotNull(serviceRequest, "datInizioOcc");
    String datAnzOcc = StringUtils.getAttributeStrNotNull(serviceRequest, "datAnzOcc");
    
    //Caso di ricerca su mansioni
    String codiceMansione = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceMansione");  
    String descrizioneMansione = StringUtils.getAttributeStrNotNull(serviceRequest, "DescrizioneMansione");
    String flgCambiamentiDati  = serviceRequest.containsAttribute("flgCambiamentiDati")?serviceRequest.getAttribute("flgCambiamentiDati").toString():"";

    String tipoAteco = serviceRequest.containsAttribute("TIPOATECO")?serviceRequest.getAttribute("TIPOATECO").toString():"";    
    String codCom = serviceRequest.containsAttribute("CODCOM")?serviceRequest.getAttribute("CODCOM").toString():"";
	
    if(descrStatoOcc.equals("")) {
    	descrStatoOcc = "Altro: ALTRO";
    }
    
    if(datInizioOcc.equals("")) {
    	Calendar oggi = Calendar.getInstance();
    	String giornoDB = Integer.toString(oggi.get(5));
    	String meseDB = Integer.toString(oggi.get(2)+1);
    	String annoDB = Integer.toString(oggi.get(1));
    	String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;
    	datInizioOcc = dataOdierna;
    }
    
    String cfSommEstera = StringUtils.getAttributeStrNotNull(serviceRequest, "CODFISCAZESTERA");
    String ragSocSommEstera = StringUtils.getAttributeStrNotNull(serviceRequest, "RAGSOCAZESTERA");
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
	<af:linkScript path="../../js/" />

    <!--Crea e popola l'oggetto che consente di recuperare i dati della ricerca-->
    <script type="text/javascript">
    var datacontainer = new Object();
    window.dati = datacontainer;
    datacontainer.soggetto = "<%=soggetto%>";
    datacontainer.funzioneaggiornamento = "<%=funzioneaggiornamento%>";

    datacontainer.prgAzienda = "<%=prgAzienda%>";
    datacontainer.prgUnita = "<%=prgUnita%>";
    datacontainer.ragioneSociale = "<%=StringUtils.replace(ragioneSociale,"\"","\\\"")%>";
    datacontainer.partitaIva = "<%=partitaIva%>";
    datacontainer.codiceFiscaleAzienda = "<%=codiceFiscaleAzienda%>";
    datacontainer.FLGDATIOK = "<%=strFlgDatiOK%>";
    datacontainer.strIndirizzoAzienda = "<%=strIndirizzoAzienda%>";
    datacontainer.comuneAzienda = "<%=comuneAzienda%>";
    datacontainer.codComuneAzienda = "<%=codComune%>";
    datacontainer.strTelAzienda = "<%=strTelAzienda%>";
    datacontainer.strFaxAzienda = "<%=strFaxAzienda%>";
    datacontainer.strCapAzienda = "<%=strCapAzienda%>";
    datacontainer.provAzienda = "<%=provAzienda%>";
    datacontainer.codTipoAz = "<%=codTipoAz%>";
    datacontainer.natGiurAz = "<%=natGiurAz%>";
    datacontainer.strReferente = "<%=strReferente%>";
    datacontainer.codCpi = "<%=codCpi%>";
    datacontainer.codRegione = "<%=codRegione%>";    
    datacontainer.codNatGiurAz = "<%=codNatGiurAz%>";
    datacontainer.descrTipoAz = "<%=descrTipoAz%>";
    datacontainer.numAlboInterinali = "<%=numalbointerinali%>";
    datacontainer.numRegCommitt = "<%=numregcommitt%>";
    datacontainer.descrCCNLAz = "<%=strdescrizioneccnl%>";
    datacontainer.CCNLAz = "<%=ccnlaz%>";    
    datacontainer.codAteco = "<%=codAteco%>";  
    datacontainer.strDesAtecoUAz = "<%=strDesAtecoUAz%>";  
    datacontainer.tipoAteco = "<%=tipoAteco%>";
    datacontainer.codCom = "<%=codCom%>";
    
    datacontainer.strPatInail = "<%=strPatInail%>";
    datacontainer.strNumeroInps = "<%=strNumeroInps%>";
    
    datacontainer.cdnLavoratore = "<%=cdnLavoratore%>";
    datacontainer.codiceFiscaleLavoratore = "<%=codiceFiscaleLavoratore%>";
    datacontainer.cognome = "<%=cognome%>";
    datacontainer.nome = "<%=nome%>";
    datacontainer.datNasc = "<%=datNasc%>";
    datacontainer.codCpiLav = "<%=codCpiLav%>";
    
    datacontainer.descrStatoOcc = "<%=descrStatoOcc%>";
    datacontainer.datInizioOcc = "<%=datInizioOcc%>";
    datacontainer.datAnzOcc = "<%=datAnzOcc%>";
    
    datacontainer.codiceMansione = "<%=codiceMansione%>";
    datacontainer.descrizioneMansione = "<%=descrizioneMansione%>";
    datacontainer.flgCambiamentiDati = "<%=flgCambiamentiDati%>";

    datacontainer.CODFISCAZESTERA = "<%=cfSommEstera%>";
    datacontainer.RAGSOCAZESTERA = "<%=ragSocSommEstera%>";
    
    //Controlla che la funzione di aggiornamento da chiamare esista nella pagina sottostante,
    //altrimenti non fa nulla
    function richiamaAggiornamento() {
    	if (window.opener != null && window.opener.<%=funzioneaggiornamento%> != null) {
    		window.opener.<%=funzioneaggiornamento%>();    		
    	} else window.close();
    }
    </script>
</head>

<body class="gestione" onload="javascript:richiamaAggiornamento();">

</body>
</html>