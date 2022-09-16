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
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
   SourceBean row_stato=null;
   /*ResponseContainer responseContainer=ResponseContainerAccess.getResponseContainer(request);
   SourceBean serviceResponse = responseContainer.getServiceResponse();

   RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request);
   SourceBean serviceRequest = requestContainer.getServiceRequest();*/
   Vector listaStato= serviceResponse.getAttributeAsVector("M_GETDESTATOOCC.ROWS.ROW");

   //Per il collegamento agli appuntamenti
  int cdnTipoGruppo = user.getCdnTipoGruppo();
  String codCpi="";
  if(cdnTipoGruppo == 1) {
    codCpi =  user.getCodRif();
  }
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca Stato Occupazioale</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
</head>

<script language="JavaScript">

function checkCampiObbligatori(strCodiceFiscale, strCognome)
      { if( ((strCodiceFiscale.value != null) && (strCodiceFiscale.value.length >= 6)) 
            || (strCognome.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
      }

function underConstr()
{ alert("Funzionaliltà non ancora attivata.\nLa ricerca per ora non usa questo filtro");
}

</script>

<SCRIPT TYPE="text/javascript">

  function caricaDettStato(value) {
    var lista_stato_filt_cod=new Array();
    var lista_stato_cod=new Array();
    var lista_stato_filt_des=new Array();
    var lista_stato_des=new Array();
    var lista_stato_noDid_cod = new Array();
    var lista_stato_noDid_des = new Array();
    
    var indice=0;
    var k=0;
    //Si riempie l'aarray filtrato
    var j=1;
    var n=1;
    lista_stato_cod[0]="";
    lista_stato_des[0]="";
    lista_stato_filt_cod[0]="";
    lista_stato_filt_des[0]="";
    lista_stato_noDid_cod[0]="";
    lista_stato_noDid_des[0]="";
    
<%  for(int i=0; i<listaStato.size(); i++)  { 
      row_stato = (SourceBean) listaStato.elementAt(i);
      if ( (row_stato.getAttribute("CODSTATOOCCUPAZRAGG").toString().equals("D")) || (row_stato.getAttribute("CODSTATOOCCUPAZRAGG").toString().equals("I")) ){
	      out.print("lista_stato_filt_cod[j]=\""+ row_stato.getAttribute("CODICE").toString()+"\";\n");
	      out.print("lista_stato_filt_des[j]=\""+ row_stato.getAttribute("DESCRIZIONE").toString()+"\";\n");
        out.print("j=j+1;");
      }
      else {
 	      out.print("lista_stato_noDid_cod[n]=\""+ row_stato.getAttribute("CODICE").toString()+"\";\n");
	      out.print("lista_stato_noDid_des[n]=\""+ row_stato.getAttribute("DESCRIZIONE").toString()+"\";\n");
	      out.print("n=n+1;");
      }
    }
    //Si riempie l'array non filtrato
    for(int i=0; i<listaStato.size(); i++)  { 
      row_stato = (SourceBean) listaStato.elementAt(i);
      out.print("lista_stato_cod["+ (i+1) +"]=\""+ row_stato.getAttribute("CODICE").toString()+"\";\n");
      out.print("lista_stato_des["+ (i+1) +"]=\""+ row_stato.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>

	var optionSelected = document.form1.codStatoOcc.value;
	
   while (document.form1.codStatoOcc.options.length>0) {
        document.form1.codStatoOcc.options[0]=null;
    }
    var opzione=null;
     if (value=="S") {     
    	for (k=0; k<lista_stato_filt_des.length;k++) {
    	if(optionSelected==lista_stato_filt_cod[k]){
        	opzione = new Option(lista_stato_filt_des[k],lista_stato_filt_cod[k], true, true);
        }
        else {
        	opzione = new Option(lista_stato_filt_des[k],lista_stato_filt_cod[k], false, false);
        }
        //document.form1.codStatoOcc.add(opzione);
        document.form1.codStatoOcc.options[k]=opzione;
    	}
    } else if(value=="N"){
    	for (k=0; k<lista_stato_noDid_des.length;k++) {
    	if(optionSelected==lista_stato_noDid_cod[k]){
        	opzione = new Option(lista_stato_noDid_des[k],lista_stato_noDid_cod[k], true, true);
        }
        else{
        	opzione = new Option(lista_stato_noDid_des[k],lista_stato_noDid_cod[k], false, false);
        }
        document.form1.codStatoOcc.options[k]=opzione;
    	}
    }
     else {
	    for (k=0; k<lista_stato_des.length ;k++) {
	    if(optionSelected==lista_stato_cod[k]){
        	opzione = new Option(lista_stato_des[k],lista_stato_cod[k], true, true);
        }
        else{
        	opzione = new Option(lista_stato_des[k],lista_stato_cod[k], false, false);
        }
        //document.form1.codStatoOcc.add(opzione);
        document.form1.codStatoOcc.options[k]=opzione;
	      }
      }
  }

  function visualizzaNumero(val) {
    var numVElement = document.getElementById("numVolte");
    var dataElement = document.getElementById("dataNonPres");
    if ( val == "S" ) {
      numVElement.style.display="";
      if ((document.form1.didRilasciata.value == "N") || (document.form1.didRilasciata.value == "")) {
        dataElement.style.display="";
      } else {
          dataElement.style.display="none";
          document.form1.dataNP.value="";          
       }
    } else {
        numVElement.style.display="none";
        document.form1.numVol.value="";
        if ((document.form1.didRilasciata.value == "N") || (document.form1.didRilasciata.value == "")) {
          dataElement.style.display="none";
          document.form1.dataNP.value="";          
        }
      }
  }

  function obbligatori(){
    if ( document.form1.viewNonPresentati.value=="S") {      
      if ( document.form1.numVol.value=="" ){
        alert("Inserire un valore per il campo 'Numero di volte non presentato'");
        document.form1.numVol.focus();
        return false;
      }
      if (document.form1.didRilasciata.value!='S' && document.form1.dataNP.value=='') {
        alert("Inserire la data dalla quale i soggetti convocati non si sono presentati");
        document.form1.dataNP.focus();
      	return false;
      }
    }    
    return true;
  }
  function valorizzaHidden() {
  	document.form1.descStatoOcc_H.value = document.form1.codStatoOcc.options[document.form1.codStatoOcc.selectedIndex].text;
  	document.form1.descCPI_H.value = document.form1.CodCPI.options[document.form1.CodCPI.selectedIndex].text;
  	return true;
  }
</SCRIPT>

<% boolean readOnlyStr = false;%>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca stato occupazionale</p>
<p align="center">
  <af:form name="form1" action="AdapterHTTP?PAGE=StatoOccRisultRicercaPage" method="POST" onSubmit="obbligatori()&& valorizzaHidden()">
  	<input type="hidden" name="descStatoOcc_H" value=""/>
  	<input type="hidden" name="descCPI_H" value=""/>
  <%out.print(htmlStreamTop);%> 
  <table class="main" border="0">
    <tr><td colspan="2"/>&nbsp;</td></tr>
   <tr>
     <td class="etichetta">Codice fiscale</td>
     <td class="campo"><af:textBox type="text" name="CF" value="" maxlength="16" /></td>
   </tr>
   <tr>
     <td class="etichetta">Cognome </td>
     <td class="campo"><af:textBox type="text" name="cognome" value="" maxlength="100"/></td>
   </tr>   
   <tr>
     <td class="etichetta">Nome</td>
     <td class="campo"><af:textBox type="text" name="nome" value="" maxlength="100" /></td>
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

   <tr><td>&nbsp;</td><tr>
   <tr ><td colspan="2" ><div class="sezione2"/></td></tr>
   <tr>
     <td class="etichetta">DID rilasciata</td>
     <td class="campo">
        <af:comboBox classNameBase="input" name="didRilasciata" onChange="caricaDettStato(this.value);visualizzaNumero(document.form1.viewNonPresentati.value);"
                       addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>">
                 
            <option value="S">Si</option>
            <option value="N">No</option>
            
        </af:comboBox>
     </td>
   </tr>
   <tr>
     <td class="etichetta">Stato occupazionale</td>
     <td class="campo">
       <af:comboBox classNameBase="input" name="codStatoOcc" title="Stato occupazionale" addBlank="true"
                    disabled="<%=String.valueOf(readOnlyStr)%>" required="True" />
       </td>
   </tr>
  <tr>
    <td class="etichetta">Soggetti convocati che non si sono presentati</td>
    <td class="campo">
        <af:comboBox classNameBase="input" name="viewNonPresentati" onChange="visualizzaNumero(this.value)"
                       addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"><!--javascript:caricaDettStato(this.value);-->
            <option value="S">Si</option>
            <option value="N">No</option>
        </af:comboBox>
    </td>
  </tr>
  <tr id='numVolte' style="display:none">
      <td class="etichetta">Num. di volte che non si è presentato</td>
      <td class="campo" nowrap><af:textBox type="number" name="numVol" value="" size="3" maxlength="2"/>&nbsp;*</td>
  </tr>
  <tr id="dataNonPres" style="display:none">
      <td class="etichetta">Dalla data</td>
      <td class="campo">
      	<af:textBox type="date" title="Dalla data" name="dataNP" value="" maxlength="10" size="11" validateOnPost="true" />
      </td> 
   </tr>
  <!--
   <tr>
     <td class="etichetta">Cat. 181</td>
     <td class="campo">
          <af:comboBox classNameBase="input" name="categoria181" onChange="underConstr();"
                       addBlank="true" disabled="<%//=String.valueOf(readOnlyStr)%>">
            <option value="A">Adolescenti</option>
            <option value="G">Giovani</option>
            <%--option value="N">Non giovani</option--%>
          </af:comboBox>
     </td>
   </tr>
  -->
   <%-- <tr>
    <td class="etichetta">Mesi di anzianità</td>
    <td class="campo"><af:textBox type="text" name="mesiAnzianita" value="" /></td>
   </tr> --%>

   <!-- 
   		Commento aggiunto per il congelamento della versione in fase 3
   		Cosimo Togna 28/04/2005
   <tr>
    <td class="etichetta">Soggetti alla legge 407/90</td>
    <td class="campo">
     <af:comboBox name="legge407_90" addBlank="true" disabled="<%//=String.valueOf(readOnlyStr)%>">
      <option value="S">Sì</option>
      <option value="N">No</option>
     </af:comboBox>
    </td>
   </tr>

   <tr>
    <td class="etichetta">Disoccupato/inoccupato di lunga durata</td>
    <td class="campo">
     <af:comboBox name="lungaDur" addBlank="true" disabled="<%//=String.valueOf(readOnlyStr)%>">
      <option value="S">Sì</option>
      <option value="N">No</option>
     </af:comboBox> 
    </td>
   </tr>
   -->

   <tr>
    <td class="etichetta">Revisione / Ricorso</td>
    <td class="campo">
     <af:comboBox name="revRic" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>">
      <option value="S">Sì</option>
      <option value="N">No</option>
     </af:comboBox>
    </td>
   </tr>
   <tr><td>&nbsp;</td><tr>
   <tr><td colspan="2"><div class="sezione2"></td></tr>
   <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="" required="true"/>
    </td>
  <tr>
   <tr><td>&nbsp;</td></tr>
   <tr><td>&nbsp;</td></tr>
   <tr>
    <td colspan="2" align="center">
      <input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla" onClick="javascript:visualizzaNumero('N');document.form1.codStatoOcc.value='';caricaDettStato('');" >
    </td>
   </tr>
  </table>
  <%out.print(htmlStreamBottom);%> 
  <input type="hidden" name="cdnfunzione" value="<%=((String) serviceRequest.getAttribute("CDNFUNZIONE"))%>"/>
  </af:form>
</p>
<script language="javascript">
  caricaDettStato("");
</script>
</body>
</html>
