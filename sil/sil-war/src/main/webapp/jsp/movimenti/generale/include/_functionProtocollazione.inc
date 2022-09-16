<script language = "javascript">
<!--

  <%
    boolean numProtEditable1 = false;
  %>
  var isChanged = false;
  //Controllo della possibilità di sceglire un'opzione o meno.
  function cambiacodMonoMovDich(){
  	  var vOld;
      var vNew;
      <%if(!inserisci && !rettifica){%>
        if (document.Frm1.codMonoMovDichHid.value == "O" )
          vOld = 3;
        else
          if (document.Frm1.codMonoMovDichHid.value == "D" )
            vOld = 2;
          else
              if (document.Frm1.codMonoMovDichHid.value == "C" )
                vOld = 1;

        if (document.Frm1.codMonoMovDich.value == "O" )
          vNew = 3;
        else
          if (document.Frm1.codMonoMovDich.value == "D" )
            vNew = 2;
          else
              if (document.Frm1.codMonoMovDich.value == "C" )
                vNew = 1;        
        if ( vNew != vOld ) {
            if(vNew < vOld){
              alert("Impossibile proseguire con la selezione");
              isChanged = false;
              document.Frm1.codMonoMovDich.value = document.Frm1.codMonoMovDichHid.value;
              document.Frm1.NUOVODOCUMENTO.value = "";
              <% if (numAnnoProtV!=null){%>
                document.Frm1.numAnnoProt.value = "<%= numAnnoProtV %>";
              <%}
              if (numProtV!=null){%>
                document.Frm1.numProtocollo.value = "<%= numProtV.toString() %>";
              <%}
              if (kLockProt!=null){%>
                document.Frm1.KLOCKPROT.value = "<%= kLockProt %>";
              <%}%>
              <%if (datProtV!=null){%>
                document.Frm1.dataProt.value = "<%=datProtV%>";
              <%}%>
              <%if (oraProtV!=null){%>
                document.Frm1.oraProt.value = "<%=oraProtV%>";
              <%}%>
              <%if(datComunicaz!=null){%>
                document.Frm1.DATCOMUNICAZ.value = "<%=datComunicaz%>";
              <%}%>
              cambiaStatoFunction(document.getElementById("ritardo2"), "none");
              gestisciCampiProt("","none");   
              if(document.Frm1.codMonoMovDich.value == "O"){
                //mostraRitardo();
                <%if(numGgTraMovComunicaz!=null){%>
                  document.Frm1.NUMGGTRAMOVCOMUNICAZIONE.value = "<%=numGgTraMovComunicaz%>";
                <%}%>
              } else {
                  //nascondiRitardo();
                }
            } else {
                isChanged = true;
                //Creare un nuovo documento
                document.Frm1.NUOVODOCUMENTO.value = "1";
                //carica il nuovo numero di Protocollo
                <%
                  Vector rws = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
                   String prAutomatica1     = null;
                   String estReportDefautl1 = null;
                   BigDecimal numProtV1     = null;
                   BigDecimal numAnnoProtV1 = null;
                   BigDecimal kLockProt1    = null;
                   String     datProtVl1     = null;
                   String     oraProtVl1     = null;

                  if(rws != null && !rws.isEmpty())
                  { 
                   SourceBean rw = (SourceBean) rws.elementAt(0);
                   prAutomatica1     = (String) rw.getAttribute("FLGPROTOCOLLOAUT");
                   if ( prAutomatica1.equalsIgnoreCase("N") ){ numProtEditable1 = true; }
                   estReportDefautl1 = (String) rw.getAttribute("CODTIPOFILEESTREPORT");
                   numProtV1          = (BigDecimal) rw.getAttribute("NUMPROTOCOLLO");
                   numAnnoProtV1      = (BigDecimal) rw.getAttribute("NUMANNOPROT");
                   kLockProt1        = (BigDecimal) rw.getAttribute("NUMKLOPROTOCOLLO");
                   datProtVl1     = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
                   oraProtVl1     = (new SimpleDateFormat("HH:mm")).format(new Date());
                  }
                %>
                <%if(numProtEditable1){%>
                    gestisciCampiProt("none","");
                    document.Frm1.numProtocollo2.value = "<%= (numProtV1 != null) ? numProtV1 : new BigDecimal(0)%>";
                    document.Frm1.numAnnoProt2.value = "<%= (numAnnoProtV1 != null) ? numAnnoProtV1 : new BigDecimal(0)%>";
                    document.Frm1.dataProt2.value = "<%=(datProtVl1 != null) ? datProtVl1 : ""%>";
                    document.Frm1.oraProt2.value = "<%=(oraProtVl1 != null) ? oraProtVl1 : ""%>";

                    document.Frm1.TIPOPROTOCOLLO.value = "MANUALE";
                <%} else {%>
                      document.Frm1.numProtocollo.value = "<%= (numProtV1 != null) ? numProtV1 : new BigDecimal(0)%>";
                      document.Frm1.numAnnoProt.value = "<%= (numAnnoProtV1 != null) ? numAnnoProtV1 : new BigDecimal(0)%>";
                      document.Frm1.dataProt.value = "<%=(datProtVl1 != null) ? datProtVl1 : ""%>";
                      document.Frm1.oraProt.value = "<%=(oraProtVl1 != null) ? oraProtVl1 : ""%>";
                      cambiaStatoFunction(document.getElementById("dataCom1"), "none");
                      cambiaStatoFunction(document.getElementById("dataCom2"), "");                    
                      document.Frm1.TIPOPROTOCOLLO.value = "AUTOMATICO";  
                  <%}%>
                  
                  document.Frm1.KLOCKPROT.value = "<%=(kLockProt1 != null) ? kLockProt1 : new BigDecimal(0)%>";
                  document.Frm1.DATCOMUNICAZ2.value = "";
                  document.Frm1.NUMGGTRAMOVCOMUNICAZIONE2.value = "";
              }
        } else {
              if ( vNew == vOld ){
                isChanged = false;
                document.Frm1.numProtocollo.value = "<%=numProtV%>";
                document.Frm1.numAnnoProt.value = "<%=numAnnoProtV%>";
                document.Frm1.KLOCKPROT.value = "<%=kLockProt%>";
                document.Frm1.dataProt.value = "<%=datProtV%>";
                document.Frm1.oraProt.value = "<%=oraProtV%>";
                document.Frm1.NUOVODOCUMENTO.value = "";
                document.Frm1.DATCOMUNICAZ.value = "<%=datComunicaz%>";
                gestisciCampiProt("","none");          
                cambiaStatoFunction(document.getElementById("ritardo2"), "none");
                if(document.Frm1.codMonoMovDich.value == "O"){
                  //mostraRitardo();
                } else {
                    //nascondiRitardo();
                  }
              }
          }
    <%} else if (rettifica) {%>
	      if(document.Frm1.codMonoMovDich.value == "O"){
	        //mostraRitardo();
	        <%if(numGgTraMovComunicaz!=null){%>
	          document.Frm1.NUMGGTRAMOVCOMUNICAZIONE.value = "<%=numGgTraMovComunicaz%>";
	        <%}%>
	      } else {
	          //nascondiRitardo();
          }
    <%}%>
    }

//function mostraRitardo(){
//  cambiaStatoFunction(document.getElementById("ritardo1"), "");
//  cambiaStatoFunction(document.getElementById("labelRit"), "");
//}

//function nascondiRitardo(){
//  cambiaStatoFunction(document.getElementById("ritardo1"), "none");
//  cambiaStatoFunction(document.getElementById("labelRit"), "none");
//}

// =============================================================================
//     Sono le stesse funzioni js usate in GestisciStatoDoc.jsp
//                                                                by Davide
// =============================================================================
function checkAndFormatTime(oraObj)
{ var strTime = oraObj.value;
  var strHours = "";
  var strMin   = "";
  var separator = ":";
  var strTimeArray;

  var titleObj = "ora"; 
  if(oraObj.title != null)
  { titleObj = oraObj.title;
  }
  
   if (strTime.indexOf(separator) != -1) {
      strTimeArray = strTime.split(separator);
      if (strTimeArray.length != 2) {
         alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12")
         return false;
      }
      else {
       strHours = strTimeArray[0];
       strMin   = strTimeArray[1];
      }
   }
   else if(strTime.length == 4)
   { //Non c'è il separatore, probabilmente è stata inserita un'orario nel formato 1215 -> 12:15
     //che comunque reputiamo valido...
     strHours = strTime.substr(0,2);
     strMin   = strTime.substr(2,4);
   }
   else
   { alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   var hours = parseInt(strHours, 10);
   var min   = parseInt(strMin, 10);
   
   if(isNaN(hours))
   { alert("L'ora inserita nel campo "+titleObj+" non è un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }
   if(isNaN(min))
   { alert("I minuti inseriti nel campo "+titleObj+" non sono un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   hours = parseInt(strHours, 10);
   min   = parseInt(strMin, 10);
   
   if( hours<0 || hours > 23 )
   { alert("L'ora inserita nel campo "+titleObj+" non è orario valido.\nInserire un'ora compresa fra 0 e 23");
     return false;
   }
   if( min<0 || min > 59 )
   { alert("I minuti inseriti nel campo "+titleObj+" non sono corretti.\nInserire un numero compreso fra 0 e 59");
     return false;
   }

	oraObj.value = (hours<10?"0":"") + hours + separator + (min<10?"0":"") + min;
	return true;	  
   
}//end function


function cambiAnnoProt(dataPRObj,annoProtObj)
{ var dataProt = dataPRObj.value;
  var lun = dataProt.length;

  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
  annoProtObj.value = ""; 
  
  if (lun > 5) {
    var tmpDate = new Object();
    tmpDate.value = dataProt;
    if ( checkFormatDate(tmpDate) ) {
       annoProtObj.value = tmpDate.value.substr(6,10);      
    }
    else if (lun==8 || lun==10) {
      alert("La data di protocollazione non è corretta");
    }
  }
  
}//end function
// =============================================================================

//==============================================================================
//Viene utilizzata per devalorizzare la data di comunicazione se si sceglie "Da
//comunicazione obbligatoria" come tipo "Provenienza", si imposta con la data odierna 
//se vengono scelti gli altri due tipi. Per i giorni di ritardo: si valorizza
//solo se si è scelto come provenienza "Da comunicazione obbligatoria" (CODMONOMOVDICH='O').
function valDevalDatComunicaz(){
  <%--if(inserisci){--%>
  <%if(!rettifica){%>
      if(document.Frm1.codMonoMovDich.value == "O"){
        document.Frm1.DATCOMUNICAZ.value = "";
        <%if(!inserisci){%>
            cambiaStatoFunction(document.getElementById("ritardo2"), "");
            cambiaStatoFunction(document.getElementById("ritardo1"), "none");
            cambiaStatoFunction(document.getElementById("labelRit"), "");
        <%}%>
      } else {
          document.Frm1.DATCOMUNICAZ.value = "<%=DateUtils.getNow()%>";
          <%if(!inserisci){%>
              cambiaStatoFunction(document.getElementById("ritardo2"), "none");
              cambiaStatoFunction(document.getElementById("ritardo1"), "none");
              cambiaStatoFunction(document.getElementById("labelRit"), "none");
          <%}%>
        }
  <%}%>
}


function cambiaStatoFunction(obj, valore){
  if((valore != undefined) && (obj != undefined) && (obj.style.display != valore))
    obj.style.display = valore;
}

function gestisciCampiProt(valoreOrig, valoreNew){
  var stato;
  var stato2;
  stato = valoreOrig;
  stato2 = valoreNew;
  //Sezione campi non editabili
  var objDatCom = document.getElementById("dataCom1");
  var objAnnoProt = document.getElementById("anno");
  var objNumProt = document.getElementById("numeroProt");
  var objDataProt = document.getElementById("dataProt");
  var objOraProt = document.getElementById("oraProt");
  //Sezione campi editabili
  var objDatCom2 = document.getElementById("dataCom2");
  var objAnnoProt2 = document.getElementById("anno2");
  var objNumProt2 = document.getElementById("numeroProt2");
  var objDataProt2 = document.getElementById("dataProt2");
  var objOraProt2 = document.getElementById("oraProt2");

  <%
    if(!inserisci){%>
      
        cambiaStatoFunction(objDatCom, valoreOrig);
        cambiaStatoFunction(objAnnoProt, valoreOrig);
        cambiaStatoFunction(objNumProt, valoreOrig);
        cambiaStatoFunction(objDataProt, valoreOrig);
        cambiaStatoFunction(objOraProt, valoreOrig);

        cambiaStatoFunction(objDatCom2, valoreNew);
        cambiaStatoFunction(objAnnoProt2, valoreNew);
        cambiaStatoFunction(objNumProt2, valoreNew);
        cambiaStatoFunction(objDataProt2, valoreNew);
        cambiaStatoFunction(objOraProt2, valoreNew);        
      
  <%}%>
}

function azzeraCampiFissi(){
  var objForm = document.Frm1;
  
  objForm.numProtocollo.value = "";
  objForm.numAnnoProt.value = "";
  objForm.dataProt.value = "";
  objForm.oraProt.value = "";
  objForm.DATCOMUNICAZ.value = "";
  if(objForm.codMonoMovDich.value == "O"){
    objForm.NUMGGTRAMOVCOMUNICAZIONE.value = "";
  }
}

function swap(){
  var objForm = document.Frm1;
  if(isChanged){
    <%if(numProtEditable1){%>
        azzeraCampiFissi();
        objForm.numProtocollo.value = objForm.numProtocollo2.value;
        objForm.numAnnoProt.value = objForm.numAnnoProt2.value;
        objForm.dataProt.value = objForm.dataProt2.value;
        objForm.oraProt.value = objForm.oraProt2.value;
    <%}%>
    if(objForm.DATCOMUNICAZ2.value == ""){
      alert("Il campo Data Comunicazione è obbligatorio");
      return false;
    } else {
        objForm.DATCOMUNICAZ.value = objForm.DATCOMUNICAZ2.value;
      }
    if(objForm.codMonoMovDich.value == "O"){
      objForm.NUMGGTRAMOVCOMUNICAZIONE.value = objForm.NUMGGTRAMOVCOMUNICAZIONE2.value;
    }
  }
  return true;
}

function salvaDati(){
	//Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	//Controllo di avere il CODSTATOATTO
	if (document.Frm1.CODSTATOATTO.value == "") {
		alert("Il campo " + document.Frm1.CODSTATOATTO.title + " è obbligatorio.");
		return;  
	}
	if(document.Frm1.CODMOTANNULLAMENTO.value == ""  && document.getElementById("obbligo").style.display == "") {
		alert("Il campo " + document.Frm1.CODMOTANNULLAMENTO.title + " è obbligatorio.");
		return;  
	}
	if ( (document.Frm1.CODSTATOATTO.value == "AN" || document.Frm1.CODSTATOATTO.value == "AU" || document.Frm1.CODSTATOATTO.value == "AR") 
		&& (document.Frm1.CODMOTANNULLAMENTO.value == "URG" || document.Frm1.CODMOTANNULLAMENTO.value == "MAG") ) {
		alert("Motivo non consentito.");
		return;	
	}
	//Se ho scelto di rettificare il documento chiedo conferma all'utente
	if (document.Frm1.CODSTATOATTO.value == "AR") {confermaRettifica(document.Frm1.CODSTATOATTO);
	} else if(swap()){
		if(isSanato){
			alert("Impossibile rettificare un movimento coinvolto in una dichiarazione di reddito.");
			return;
		}
		if(confirm("Si desidera proseguire con l'operazione?")){
		    document.Frm1.ACTION.value='aggiorna';
		    document.Frm1.permettiImpatti.value = '<%=	canEditLav%>';
		    doFormSubmit(document.Frm1);
		}
	}
}

//D'Auria Giovanni 10/03/2005 inizio
function chekObbligatorieta(statoAtto){
	var i;
	var statoAttoValue= statoAtto[statoAtto.selectedIndex].value;
	var spanObbligatorio = document.getElementById("obbligo");
	if(statoAttoValue != "PR" && statoAttoValue != "PA"){
		spanObbligatorio.style.display='';
	}else{
		inibisciScelta(statoAtto, "PA");
		spanObbligatorio.style.display='none';
	}

}
function inibisciScelta(combo, scelta){
	var comboValue= combo[combo.selectedIndex].value;
	if(comboValue == scelta){
		alert("Scelta non valida");
				for(i=0; i< combo.options.length; i++){
					if(combo[i].value == '<%=codStatoAtto%>'){	
						break;				
					}
				}
				combo[i].selected=true;
	}
}
//D'Auria Giovanni 10/03/2005 fine

-->
</script>