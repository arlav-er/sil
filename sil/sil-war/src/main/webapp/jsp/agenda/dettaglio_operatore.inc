<script type="text/javascript">
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  
  
  function fieldChanged() {
      <%if (canModify) {out.print("flagChanged = true;");}%>
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
	 function checkEMail(s) {
		ret = true;
		var regExp = new RegExp("^[^@]+@[^@]+\.[^@]+$");
		res = s.match(regExp);
		if (res==null||res.length==0)
			ret = false;
		return ret;
	}
	function controllaEmail() {	
		if( document.getElementsByName("STREMAIL")==null || document.getElementsByName("STREMAIL")=="" ){
			return true;
		}
	  		
		var ret = true;
		var objEmail = document.getElementsByName("STREMAIL");
		var eMail = objEmail.item(0).value;
		if (eMail!=null && eMail!="" && !checkEMail(eMail))  {
			alert("Indirizzo e-mail non corretto");
			document.frmOperatore.STREMAIL.focus();
			ret = false;
		}
		return ret;
	}   
   
  function annulla() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.frmOperatore.MODULE.value = "";
              document.frmOperatore.MODULE.disabled = true;
              doFormSubmit(document.frmOperatore);
          }
    } else {
        document.frmOperatore.MODULE.value = "";
        document.frmOperatore.MODULE.disabled = true;
        doFormSubmit(document.frmOperatore);
    }
  }

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
    strNome=document.frmOperatore.STRCOGNOME.value + ' ' + document.frmOperatore.STRNOME.value;
    strCodiceFiscale=document.frmOperatore.STRCODICEFISCALE.value;
    if (strNome != '') {
      strRisultato=RicercaOperatore(strNome,operatoreNome);
    }
    if (strRisultato == 0) {
        if (strCodiceFiscale != '') {
                strRisultato1=RicercaOperatore(strCodiceFiscale,operatoreCodiceFisc);
        }
        if (strRisultato1 == 1) {
                strRisultatoFinale=1;                          
        }
    }
    else {
        strRisultatoFinale=1;
    }

    if (strRisultatoFinale == 1) {
        if (!confirm('Nome o Codice fiscale già presente, salvare comunque?')) {
                document.frmOperatore.PAGE.value = "InsOperatorePage";
                doFormSubmit(document.frmOperatore);
        }
        else {
                document.frmOperatore.STRCOGNOME.value=(document.frmOperatore.STRCOGNOME.value).toUpperCase();
                document.frmOperatore.STRNOME.value=(document.frmOperatore.STRNOME.value).toUpperCase();
                document.frmOperatore.STRCODICEFISCALE.value=(document.frmOperatore.STRCODICEFISCALE.value).toUpperCase();
        }
    }
    else {
        document.frmOperatore.STRCOGNOME.value=(document.frmOperatore.STRCOGNOME.value).toUpperCase();
        document.frmOperatore.STRNOME.value=(document.frmOperatore.STRNOME.value).toUpperCase();
        document.frmOperatore.STRCODICEFISCALE.value=(document.frmOperatore.STRCODICEFISCALE.value).toUpperCase();
    }
  }
  
</script>

<af:form name="frmOperatore" action="AdapterHTTP" method="POST" onSubmit="controllaEmail()&&checkDate()">
<input type="hidden" name="PAGE" value="GestOperatoriPage">
<input type="hidden" name="MODULE" value="<%=moduleName%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="PRGSPI" value="<%=strSpi%>">
<input type="hidden" name="DATA_DEFAULT" value="<%=strDataDefault%>">


<input type="hidden" name="FSTRCOGNOME" value="<%=fcognome%>" />
<input type="hidden" name="FSTRNOME" value="<%=fnome%>" />
<input type="hidden" name="FSTRCODICEFISCALE" value="<%=fcf%>" />
<input type="hidden" name="FSTRSIGLAOPERATORE" value="<%=fsiglaOp%>" />
<input type="hidden" name="FDATNASC" value="<%=fdataNascita%>" />
<input type="hidden" name="VALIDI" value="<%=validi%>" />


<p align="center">
<table class="main">

<tr>
<tr>
<td class="etichetta">Codice Fiscale</td>
<td class="campo">
<af:textBox 
            title="Codice fiscale" 
            required="false" 
            type="text" 
            name="STRCODICEFISCALE" 
            value="<%=strCodiceFiscale%>" 
            size="40" 
            maxlength="16" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            validateWithFunction="checkCF"/>
</td>
</tr>


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
            readonly="<%=String.valueOf(!canModify)%>"
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
            readonly="<%=String.valueOf(!canModify)%>"
            maxlength="40"/>
</td>
</tr>


<tr>
<td class="etichetta">Sesso</td>
<td class="campo">
  <af:comboBox 
                name="STRSESSO" 
                required="false" 
                classNameBase="input"
                disabled="<%= String.valueOf( !canModify ) %>"
                onChange="fieldChanged()"
                title="Sesso">
    <OPTION value="">&nbsp;</OPTION>
    <OPTION value="M" <%if (strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
    <OPTION value="F" <%if (strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
  </af:comboBox>
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
            readonly="<%=String.valueOf(!canModify)%>"/>
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
            readonly="<%=String.valueOf(!canModify)%>"
            validateOnPost="true"/>
</td>
</tr>


<tr>
<td class="etichetta">Telefono <br/>(comunicato esternamente<br/> come riferimento)</td>
<td class="campo">
<af:textBox 
            title="Telefono operatore" 
            required="false" 
            type="text" 
            name="STRTELOPERATORE" 
            value="<%=strTelOperatore%>" 
            size="20" 
            maxlength="20" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"/>
</td>
</tr>

<tr>
<td class="etichetta">Email</td>
<td class="campo">
<af:textBox 
            title="Email operatore" 
            required="false" 
            type="text" 
            name="STREMAIL" 
            value="<%=strEmailOperatore%>" 
            size="80" 
            maxlength="80" 
            onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
            validateOnPost="true" />
</td>
</tr>

<tr>
<td class="etichetta">Data Inizio Validità</td>
<td class="campo">
<af:textBox title="Data inizio validità" type="date" required="true" name="DATINIZIOVAL" value="<%=dtmInizioVal%>" size="11" maxlength="10" onKeyUp="fieldChanged();"
            classNameBase="input"
            readonly="<%=String.valueOf(!canModify)%>"
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
            readonly="<%=String.valueOf(!canModify)%>"
            validateOnPost="true"/>
</td>
</tr>

</table>
<br>
<table>
<tr>
  
<td align="center">
<%if(canModify) {%>
    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>" onClick="controllaOperatore();">
<%}%>
</td>

<td align="center">
<input type="button" class="pulsanti" name="ANNULLA" value="<%=btnAnnulla%>" onClick="annulla();">
</td>

</tr>
</table>

</af:form>