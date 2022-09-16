<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

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

  String strCognome=(String) serviceRequest.getAttribute("Cognome");
  String strNome=(String) serviceRequest.getAttribute("Nome");
  String strCodiceFiscale=(String) serviceRequest.getAttribute("codFis");
  String strSesso=(String) serviceRequest.getAttribute("sesso");
  String strDataNascita=(String) serviceRequest.getAttribute("dataNas");
  String dtmInizioVal = ""; 
  String dtmFineVal= ""; 

  
  String strSiglaOperatore="";
  Vector operatoriRows=null;
  operatoriRows=serviceResponse.getAttributeAsVector("MLISTAOPERATORI.ROWS.ROW");
  SourceBean row=null;
  
  
  boolean readonly = false;
  String htmlStreamTop = StyleUtils.roundTopTable(!readonly);   
  String htmlStreamBottom = StyleUtils.roundBottomTable(!readonly);
  
  SourceBean contDataDefault = (SourceBean) serviceResponse.getAttribute("MSELECTDATADEFAULT");
  SourceBean rowDataDefault = (SourceBean) contDataDefault.getAttribute("ROWS.ROW");
  String strDataDefault = rowDataDefault.containsAttribute("DATA_DEFAULT") ? rowDataDefault.getAttribute("DATA_DEFAULT").toString() : "";
  int nStart=strDataDefault.indexOf('\'');
  int nEnd=strDataDefault.indexOf('\'',nStart+1);   
  strDataDefault=strDataDefault.substring (nStart+1,nEnd);

%>

<HTML>
<HEAD>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
      <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <af:linkScript path="../../js/"/>
      <title>Nuovo Operatore</title>
<script type="text/javascript">
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  
  function controlli() {
	  if (checkDate()){
		return controllaOperatore();
	  }
	  else return false;		  
  }
	  
 
  function fieldChanged() {
	 flagChanged = true;
  }
  
  function checkDate() {
	  	
	  	if( document.getElementsByName("DATFINEVAL")==null ||  	document.getElementsByName("DATFINEVAL")=="" )
	  		return true;
	  	
	  	var objData1 = document.getElementsByName("DATINIZIOVAL");
	  	var objData2 = document.getElementsByName("DATFINEVAL");
	 	

		  strData1=objData1.item(0).value;
		  strData2=objData2.item(0).value;
		
		  //costruisco la data di inizio
		  d1giorno=parseInt(strData1.substr(0,2),10);
		  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero 
		  d1anno=parseInt(strData1.substr(6,4),10);
		  data1=new Date(d1anno, d1mese, d1giorno);
		
		  //costruisce la data di fine
		  d2giorno=parseInt(strData2.substr(0,2),10);
		  d2mese=parseInt(strData2.substr(3,2),10)-1;
		  d2anno=parseInt(strData2.substr(6,4),10);
		  data2=new Date(d2anno, d2mese, d2giorno);
		  
		  ok=true;
		  if (data2 < data1) {
		      alert("La data di fine validità deve essere successiva o uguale alla data di inizio validità");
		      document.getElementsByName("DATINIZIOVAL").item(0).focus();
		      ok=false;
		   }
		  return ok;
	}



  function checkCF (inputName) {
    var cfObj = eval("document.forms[0]." + inputName);
    cfObj.value=cfObj.value.toUpperCase();
    cf=cfObj.value;
    ok=true;
    msg="";
    if (cf != '') {
      if (cf.length==16) {
          for (i=0; i<16 && ok; i++)
          {
              c=cf.charAt(i);
              if (i>=0 && i<=5){
                      ok=!isDigit(c);
                      msg="Errore nei primi sei caratteri del codice fiscale";
              } else if  (i==6 || i==7) { 
                      ok=isDigit(c);
                      msg="Errore nel settimo o nell'ottavo carattere del codice fiscale";
              } else if (i==8) {
                      ok=!isDigit(c);
                      msg="Errore nel nono carattere del codice fiscale";
              } else if (i==9 || i==10) {
                      ok=isDigit(c);
                      msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
              } else if (i==11) {
                      ok=!isDigit(c);
                      msg="Errore nell'undicesimo carattere del codice fiscale";
              } else if (i>=12 && i <=14) {
                      ok=isDigit(c);
                      msg="Errore nel tredicesimo, nel quattordicesimo o nel penultimo carattere del codice fiscale";
              } else if (i==15) {
                      ok=!isDigit(c);
                      msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
              }
          }
      } else {
        ok=false;
        msg="Il codice fiscale deve essere di 16 caratteri";
      }
      if (!ok) {
          alert(msg);
          cfObj.focus();
      }
      return ok;
    }
    else {
      return true;
    }
  }
   


  
</script>

<%// controllo operatore. E' una copia dei meccanismi creati da Giovanni Landi
 // nelle pagine sotto anag/nuovooperatore.jsp e edettaglio_operatore.inc
 //opportunamente modificati per funzionare con questa pagina (nome della form cambiata)
 //IL TUTTO E' TASSATIVAMENTE DA MODIFICARE, in quanto carica la pagina inutilmente.
%>
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
<script type="text/javascript">
function RicercaOperatore (strRicerca,vettRicerca) {
    var bTrovato=0;
    var xCont;
    if (vettRicerca.length > 0 ) {
        for (xCont=0;xCont<vettRicerca.length;xCont++) {
                if (strRicerca.toUpperCase() == vettRicerca[xCont].toUpperCase()) {
                        bTrovato=1;
                        xCont=vettRicerca.length;
                }
        }
    }
    return bTrovato;
  }

  function controllaOperatore() {
    var strCodiceFiscale='';
    var strNome='';
    var strRisultato=0;
    var strRisultato1=0;
    var strRisultatoFinale=0;
    
    document.Frm1.STRCOGNOME.value=(document.Frm1.STRCOGNOME.value).toUpperCase();
    document.Frm1.STRNOME.value=(document.Frm1.STRNOME.value).toUpperCase();
    document.Frm1.STRCODICEFISCALE.value=(document.Frm1.STRCODICEFISCALE.value).toUpperCase();
        
    strNome=document.Frm1.STRCOGNOME.value + ' ' + document.Frm1.STRNOME.value;
    strCodiceFiscale=document.Frm1.STRCODICEFISCALE.value;
    if (strNome != '') {
      strRisultato=RicercaOperatore(strNome,operatoreNome);
    }
    if ((strRisultato == 0) && (strCodiceFiscale!='')) {
          strRisultato=RicercaOperatore(strCodiceFiscale,operatoreCodiceFisc);
	}


    if (strRisultato == 1) {
        if (confirm('Nome o Codice fiscale già presente, salvare comunque?')) {
    	  return true;
        } else {
          return false;
        }
    }
    else {
        return true;
    }
  }
</script>  
  
  
  


<%
//questa pagina serve sia per salvare che, a salvataggio avvenuto, per trasferire
	//il codice dell'operatore nuovo alla pagina dell'utente.
	//identifico il salvataggio avvenuto guardando il sourcebean MSALVAOPERATORE: se esiste, e
	//se il suo attributo INSERT_OK è "true", allora, via javascript, chiudo tutto e trasmetto
	//il nome, cognome, e prgSpi sulla pagina chiamante.

  String infoInserimento=null;
  infoInserimento=(String) serviceResponse.getAttribute("MSALVAOPERATORE.INSERT_OK");
  if (infoInserimento!=null) {
  	if (infoInserimento.equals("TRUE")) {
  		//abbiamo inserito bene!
  		//leggiamo il prgSpi e usciamo
  		String prgSpi=serviceResponse.containsAttribute("MDETTAGLIOOPERATORE.ROWS.ROW.PRGSPI")?(String) serviceResponse.getAttribute("MDETTAGLIOOPERATORE.ROWS.ROW.PRGSPI").toString():null;
  		/* Stefy - 19/10/2004 - Da null pointer se non si inseriscono tutti i dati
  		strCognome=(String) serviceResponse.getAttribute("MDETTAGLIOOPERATORE.ROWS.ROW.STRCOGNOME").toString();
  		strNome=(String) serviceResponse.getAttribute("MDETTAGLIOOPERATORE.ROWS.ROW.STRNOME").toString();
  		strCodiceFiscale=(String) serviceResponse.getAttribute("MDETTAGLIOOPERATORE.ROWS.ROW.STRCODICEFISCALE").toString();
  		strDataNascita=(String) serviceResponse.getAttribute("MDETTAGLIOOPERATORE.ROWS.ROW.DATNASC").toString();
  		*/
  		SourceBean dettOpe = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOOPERATORE");
  		SourceBean rowOpe = null;
  		if(dettOpe != null) {
  			rowOpe = (SourceBean) dettOpe.getAttribute("ROWS.ROW")	;
  			if(rowOpe != null) {
  				strCognome = StringUtils.getAttributeStrNotNull(rowOpe, "STRCOGNOME");
  				strNome = StringUtils.getAttributeStrNotNull(rowOpe, "STRNOME");
  				strCodiceFiscale = StringUtils.getAttributeStrNotNull(rowOpe, "STRCODICEFISCALE");
  				strDataNascita = StringUtils.getAttributeStrNotNull(rowOpe, "DATNASC");
    			strSiglaOperatore= StringUtils.getAttributeStrNotNull(rowOpe, "STRSIGLAOPERATORE");
  			}
  		}
  		
  		%>
  			<script type="text/javascript">
  				window.opener.aggiornaOperatore('<%=prgSpi%>', '<%=strNome%>', '<%=strCognome%>', '<%=strCodiceFiscale%>', '<%=strDataNascita%>');
  				window.close();
  			</script>
  	   <%
  	
  	}
  	
  
  
  } else {
%>


</Head>
<Body class="gestione">
<p class="titolo">Nuovo Operatore</p>
<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="controlli()">
<input type="hidden" name="PAGE" value="InsertUtenteOperatorePage">

<input type="hidden" name="DATA_DEFAULT" value="<%=strDataDefault%>">

<p align="center">
        <%out.print(htmlStreamTop);%>
<table class="main">

<tr>
<td class="etichetta">Cognome</td>
<td class="campo">
<af:textBox 
            type="text" 
            name="STRCOGNOME" 
            title="Cognome" 
            required="false" 
            size="40" 
            value="<%=strCognome%>" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"
            maxlength="40"/>
</td>
</tr>

<tr>
<td class="etichetta">Nome</td>
<td class="campo">
<af:textBox 
            type="text" 
            name="STRNOME" 
            title="Nome" 
            required="false" 
            size="40" 
            value="<%=strNome%>" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"
            maxlength="40"/>
</td>
</tr>

<tr>
<td class="etichetta">Codice Fiscale</td>
<td class="campo">
<af:textBox 
            title="Codice fiscale" 
            required="false" 
            type="text" 
            name="STRCODICEFISCALE" 
            value="<%=strCodiceFiscale%>" 
            size="21" 
            maxlength="16" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"
            validateWithFunction="checkCF"/>
</td>
</tr>

<tr>
<td class="etichetta">Sesso</td>
<td class="campo">
  <af:comboBox 
                name="STRSESSO" 
                required="false" 
                classNameBase="input"
                disabled="false"
                onChange="fieldChanged()"
                title="Sesso">
    <OPTION value="">&nbsp;</OPTION>
    <OPTION value="M" <%if (strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
    <OPTION value="F" <%if (strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
  </af:comboBox>
</td>
</tr>

<tr>
<td class="etichetta">Data Nascita</td>
<td class="campo">
<af:textBox 
            title="Data nascita" 
            type="date" 
            required="false" 
            name="DATNASC" 
            value="<%=strDataNascita%>" 
            size="11" 
            maxlength="10" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"
            validateOnPost="true"/>
</td>
</tr>
<tr>
<td class="etichetta">Sigla operatore</td>
<td class="campo">
<af:textBox 
            title="Sigla operatore" 
            required="false" 
            name="STRSIGLAOPERATORE" 
            value="<%=strSiglaOperatore%>" 
            size="80" 
            maxlength="80" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"/>
</td>
</tr>

<tr>
<td class="etichetta">Data Inizio Validità</td>
<td class="campo">
<af:textBox title="Data inizio validità" type="date" required="true" name="DATINIZIOVAL"
		    value="<%=dtmInizioVal%>"
		    size="11"
		    maxlength="10"
		    onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"
            validateOnPost="true"/>
</td>
</tr>

<tr>
<td class="etichetta">Data Fine Validità</td>
<td class="campo">
<af:textBox 
            title="Data fine validità" 
            type="date" 
            required="false" 
            name="DATFINEVAL" 
            value="<%=dtmFineVal%>" 
            size="11" 
            maxlength="10" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="false"
            validateOnPost="true"/>
</td>
</tr>


</table>
<br>
<table>
<tr>
  
<td align="center">
    <input type="submit" class="pulsante" name="SALVA" value="Inserisci"/>
</td>

<td align="center">
<input type="button" class="pulsante" name="ANNULLA" value="Chiudi senza inserire" onClick="window.close();">
</td>

</tr>
</table>
 <%out.print(htmlStreamBottom);%>
</af:form>

</BODY>
<%}%>
</HTML>
