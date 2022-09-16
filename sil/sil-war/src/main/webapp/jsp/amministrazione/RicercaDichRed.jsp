<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.Linguette,
                  java.math.BigDecimal,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.InfCorrentiLav" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
    PageAttribs attributi = new PageAttribs(user, "RicercaDichRedPage");

    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");    

    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>


<html>
<head>
	<title>Ricerca redditi sanati</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">

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

  function checkDate() {

  strData1=document.frm1.dataInizio.value;
  strData2=document.frm1.dataFine.value;

  //costruisco la data della richiesta
  d1giorno=parseInt(strData1.substr(0,2),10);
  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
  d1anno=parseInt(strData1.substr(6,4),10);
  data1=new Date(d1anno, d1mese, d1giorno);

  //costruisce la data di scadenza
  d2giorno=parseInt(strData2.substr(0,2),10);
  d2mese=parseInt(strData2.substr(3,2),10)-1;
  d2anno=parseInt(strData2.substr(6,4),10);
  data2=new Date(d2anno, d2mese, d2giorno);
  
  ok=true;
  if (data2 < data1) {
      alert("La data inizio deve essere precedente alla data fine");
      document.frm1.dataFine.focus();
      ok=false;
   }
  return ok;
  }

</script>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer (per il ritorna 16/06/2004)
   attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);  
      %>
</script>
</head>

<body class="gestione" onload="rinfresca();">
<% if (cdnLavoratore != null) { 
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.show(out);
        String _page = (String)serviceRequest.getAttribute("PAGE");
//        Linguette _linguetta = new Linguette(user, Integer.parseInt(cdnFunzione,10), "ListaDichRedPage", new BigDecimal(cdnLavoratore)); 
//        _linguetta.show(out);
   } %>
<br>
<p class="titolo">Ricerca redditi sanati</p>
<p align="center">
<af:form name="frm1" action="AdapterHTTP" method="POST" onSubmit="checkDate()">
<% out.print(htmlStreamTop); %> 
  <table>  
  <%--  nel caso in cui la pagina sia stata richesta nell' ambito di un lavoratore allora nella maschera di ricerca non dovra' 
        comparire la sezione relativa ai dati del lavoratore e dovra' comparire in alto la sezione delle inf correnti --%>
<% if (cdnLavoratore==null)  { %>

    <input type="hidden" name="ricerca_generale" value="true">


  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo"><af:textBox type="text" name="CF" value="" size="20" maxlength="16"/></td>
  </tr>
  <tr>
    <td class="etichetta">Cognome</td>
    <td><af:textBox type="text" name="COGNOME" value="" size="20" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><af:textBox type="text" name="NOME" value="" size="20" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">tipo ricerca</td>
    <td class="campo">
    <table colspacing="0" colpadding="0" border="0">
    <tr>
     <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
     <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
    </tr>
    </table>
    </td>
  </tr>
  
  <tr><td colspan="2"><hr width="90%"/></td></tr>
<%  } else { // passo il cdnLavoratore alla query dinamica %>
    <tr><td colspan="2"><input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"></td></tr>
<%  } %>
  <tr>
    <td class="etichetta">Tipo Dichiarazione</td>
    <td class="campo"><af:comboBox name="codtipodich" moduleName="M_ELENCOTIPODICH" addBlank="true"/></td>
  </tr>
  <tr>
    <td class="etichetta">Data da</td>
    <td ><af:textBox type="date" name="dataInizio" title="Data inizio" value="" size="12" maxlength="10"  validateOnPost="true" required="true" />
        &nbsp;&nbsp;&nbsp;&nbsp;a &nbsp;&nbsp;<af:textBox type="date" name="dataFine" value="" size="12" maxlength="10" title="Data fine" validateOnPost="true" required="true" />
    </td>
  </tr>


    
    <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Centro per l'Impiego</td>
    <td class="campo"><af:comboBox title="Centro per l'Impiego" name="CodCPI" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="" required="true"/></td>
  </tr>

  <tr>
    <td class="etichetta">Stato dichiarazione</td>
    <td class="campo"><af:comboBox title="Stato dichiarazione" name="codStatoAttoRicerca" moduleName="M_COMBO_STATO_ATTO_PAR" addBlank="true" selectedValue="" /></td>
  </tr>

  <tr><td colspan="2">

    <input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input  type="hidden" name="PAGE" value="ListaDichRedPage">
    &nbsp;</td>
 </tr>
  <tr>
    <td colspan="2" align="center">
 <input type="submit" class="pulsanti"  name = "Invia" value="Cerca" >
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
      &nbsp;&nbsp;      
    </td>
    </tr>
  </table>
  <%out.print(htmlStreamBottom);%> 
  </af:form>

</body>
</html>
