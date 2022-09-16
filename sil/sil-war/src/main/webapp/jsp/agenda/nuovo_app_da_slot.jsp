<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  oracle.sql.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
    


<%
    String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest,"cod_vista");
    String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");
    String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
    if(mod.equals("")) { mod = "0"; }
    
    String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
    String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
    String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
    String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
    String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
    String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");
    
    
    String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
    String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
    String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
    String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
    String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
    String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
    String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    
    String linkDett = "&MOD=" + mod + "&cod_vista=" + cod_vista;
    if(mod.equals("0")) {
      linkDett += "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
      linkDett += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
    }
    if(mod.equals("2")) {
      linkDett += "&sel_operatore=" + sel_operatore;
      linkDett += "&sel_servizio=" + sel_servizio;
      linkDett += "&sel_aula=" + sel_aula;
      linkDett += "&strRagSoc=" + strRagSoc;
      linkDett += "&strCodiceFiscale=" + strCodiceFiscale;
      linkDett += "&strCognome=" + strCognome;
      linkDett += "&strNome=" + strNome;
      linkDett += "&dataDal=" + dataDal;
      linkDett += "&dataAl=" + dataAl;
      linkDett += "&mese=" + mese;
      linkDett += "&anno=" + anno;
    }
%>

<%  
    String ico = "../../img/b.gif";
    String strDatiContatto = "";
    String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");
    String prgSlot =   (String) serviceRequest.getAttribute("PRGSLOT");
    String prgAppuntamento = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAPPUNTAMENTO");
    String codCpiAppuntamento = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPIAPPUNTAMENTO");

    // NOTA: verificare i nomi esatti dei parametri che serviranno nel caso di prenotazione
    // slot da parte dell'utente esterno (lavoratore/azienda) e (forse) nel caso di prenotazione
    // di un appuntamento per un lavoratore o per un'azienda direttamente dalla rispettiva scheda
    String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
    String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
    String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
    int ok;
    if(!cdnLavoratore.equals("") || (!prgAzienda.equals("") && ! prgUnita.equals(""))) { ok = 1; }
    else { ok = 0; }
%>
<html>
<HEAD>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Nuovo Appuntamento da Slot</title>
</head>

<body class="gestione" onLoad="rinfresca();prosegui(<%=ok%>);">

<p class="titolo">Nuovo Appuntamento da Slot</p>
<!--p align=center>
<img src="../../img/nonImplementata.gif" alt="nonImplementata"/>
</p-->


<script type="text/javascript">
    function conferma(azione){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if (azione=="BACK"){
        document.frmNuovoContatto.PAGE.value="PCalendario";
      }
      doFormSubmit(document.frmNuovoContatto);
    }

    function apriListaLavoratori(prg,codcpi,linkDett){
        var f = "AdapterHTTP?PAGE=RICERCA_AGENDA_LAVORATORI_PAGE&PROV=CONTATTI";
        f += "&PRGCONTATTO=" + prg;
        f += "&CODCPI=" + codcpi + linkDett;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function apriListaAziendeUnita(prg,codcpi){
        var f = "AdapterHTTP?PAGE=PRicercaAzienda&PROV=CONTATTI"
        f+= "&PRGCONTATTO=" + prg + "&CODCPI=" + codcpi;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function eliminaLavAz()
    {
      document.frmNuovoContatto.PRGAZIENDA.value = "";
      document.frmNuovoContatto.PRGUNITA.value = "";
      document.frmNuovoContatto.CDNLAVORATORE.value = "";
      document.frmNuovoContatto.TXTDATICONTATTO.value = "";
      document.frmNuovoContatto.TContatto.src = "../../img/b.gif";
    }

    function annulla()
    {
      document.frmNuovoContatto.PRGAZIENDA.value = "<%=prgAzienda%>";
      document.frmNuovoContatto.PRGUNITA.value = "<%=prgUnita%>";
      document.frmNuovoContatto.CDNLAVORATORE.value = "<%=cdnLavoratore%>";
      document.frmNuovoContatto.TContatto.src = "<%=ico%>";
    }

    function prosegui(n)
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if(n==1) { doFormSubmit(document.frmNuovoContatto); }
    }

    function controlla()
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var cdnLav = document.frmNuovoContatto.CDNLAVORATORE.value;
      var prgAz = document.frmNuovoContatto.PRGAZIENDA.value;
      var prgUn = document.frmNuovoContatto.PRGUNITA.value;

      if(cdnLav!="" || (prgAz!="" && prgUn!="")) { doFormSubmit(document.frmNuovoContatto); }
      else { alert("Per effettuare l'inserimento è necessario scegliere un lavoratore oppure una azienda"); }
    }

</script>


<af:form name="frmNuovoContatto" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="InsAppDaSlotPage">

<input type="hidden" name="prgParSlot" value="<%=prgSlot%>">
<input type="hidden" name="codParCpi" value="<%=codCpi%>">
<input type="hidden" name="cdnParUtente" value="<%=user.getCodut()%>">
<!--input type="hidden" name="prgParAppuntamento" value="<%=prgAppuntamento%>">
<input type="hidden" name="codParCpiAppunt" value="<%=codCpiAppuntamento%>"-->
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">

<p align="center">
<table class="main">
<tr class="note">
  <td colspan="2">
  <div class="sezione2">Lavoratore/Azienda
  &nbsp;&nbsp;
  <a href="#" onClick="javascript:eliminaLavAz();"><img src="../../img/del.gif" alt="Elimina"></a>
  </div>
  </td>
</tr>
<tr class="note">
  <td class="etichetta">
      Ricerca&nbsp;Azienda&nbsp;&nbsp;
      <a href="#" onClick="javascript:apriListaAziendeUnita('<%=prgSlot%>','<%=codCpi%>');">
      <img src="../../img/binocolo.gif" alt="Cerca Azienda"></a>
      <br>
      Ricerca&nbsp;Lavoratore&nbsp;&nbsp;
      <a href="#" onClick="javascript:apriListaLavoratori('<%=prgSlot%>','<%=codCpi%>')">
      <img src="../../img/binocolo.gif" alt="Cerca Lavoratore"></a>
  </td>
  <td class="campo">
    <div><img name="TContatto" id="TContatto" src="<%=ico%>" alt=""/></div>
    <textarea cols=60 rows=4 name="TXTDATICONTATTO" disabled><%=strDatiContatto%></textarea>    
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<input name="MOD" type="hidden" value="<%=mod%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>
<%if(mod.equals("0")) {%>
    <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
    <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
    <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
    <input name="giorno" type="hidden" value="<%=giorno%>"/>
    <input name="mese" type="hidden" value="<%=mese%>"/>
    <input name="anno" type="hidden" value="<%=anno%>"/>
<%} else {%>
  <%if(mod.equals("2")) {%>
        <input name="sel_operatore" type="hidden" value="<%=sel_operatore%>"/>
        <input name="sel_servizio" type="hidden" value="<%=sel_servizio%>"/>
        <input name="sel_aula" type="hidden" value="<%=sel_aula%>"/>
        <input name="strRagSoc" type="hidden" value="<%=strRagSoc%>"/>
        <input name="strCodiceFiscale" type="hidden" value="<%=strCodiceFiscale%>"/>
        <input name="strCognome" type="hidden" value="<%=strCognome%>"/>
        <input name="strNome" type="hidden" value="<%=strNome%>"/>
        <input name="mese" type="hidden" value="<%=mese%>"/>
        <input name="anno" type="hidden" value="<%=anno%>"/>
        <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
        <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
  <%}%>
<%}%>

<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" name="Inserisci" value="Inserisci" onClick="javascript: controlla()";>
  &nbsp;&nbsp;
  <input type="button" class="pulsanti" name="INDIETRO" value="Chiudi" onCLick="javascript:conferma('BACK');">  
  </td>
</tr>
</table>
</af:form>


</body>
</html>
