//--------------------------------------------------------------------------------------
// SCRIPT COMUNI A TUTTE LE PAGINE WEB DELL'APPLICAZIONE	 
//--------------------------------------------------------------------------------------
var ns = (navigator.appName.indexOf("Netscape")  >= 0); // "Netscape..."  ?    
var ie = (navigator.appName.indexOf("Microsoft") >= 0); // "Microsoft..." ?

// *** showMessage(): visualizza alert con eventuale errore ***
//    PARAMETRI: nessuno
function showMessage()
{
    if (opener != null){      
        opener.top.frame_buttons.DisabilitaButtons();       
    }
    else {        
    	// GG 13-9-2002 - aggiunto controllo esistenza buttoniera (può dare problemi all'avvio)
   		if (typeof top.frame_buttons.DisabilitaButtons != "undefined")
        	top.frame_buttons.DisabilitaButtons();
    }    
}    

// *** lookup(): visualizza pagina lookup in una nuova window modale ***
//    PARAMETRI 1- clsName = nome della classe di lookup
//              2- functionName = nome della funzione javascript che imposta i valori di lookup in mappa
//              3- form    = form contenente il buttons di lookup e i campi di ricerca
function lookup(clsName,functionName,formObject)
{      	        
    var _url = "/jsp/SWGeneralizzato/lookup.jsp?clsName=" + clsName + "&functionName=" + functionName; 
    var _qs  = "";    
    if (formObject != null)  _qs = formToQueryString(formObject);           
    if (_qs != "") _url = _url + "&" + _qs;
    showModalWindow(_url);
}


// *** formToQueryString() : crea la queryString (GET) per un dato form ***
//    PARAMETRI 
//        form    = form contenente i campi /buttons
function formToQueryString(form)
{      	        
    var qs = "";
    var _value = "";        
    //passaggio dei campi del form nella request(NB: gli spazi vanno convertiti in '+')
    var separatore = "";
    for (var i=0; i < form.elements.length; i=i+1){
       if ( form.elements[i].type.toUpperCase() != "BUTTONS" 
         && form.elements[i].value != null)
         //&& form.elements[i].value != "" )   
       {        		       
          var _name  = escape(form.elements[i].name);
          
          if (form.elements[i].type.toUpperCase() != "HIDDEN") 
             { 
              _value = escape(stripInitialFinalWhitespace(form.elements[i].value).toUpperCase());
             }
          else
             {
              _value = escape(form.elements[i].value);	
             }	
                          
          qs = qs + separatore + _name + "=" + _value ;         
          separatore ="&"            
       }    
    }        
    return qs;
}
// *** setTitolo(titolo) : imposta il titolo della pagina corrente nel frame titolo
//    PARAMETRI 1- Titolo della Pagina
function setTitolo(titolo)
{   
    
    if (titolo == "") return;    
    
    if (top != null &&
        top.frame_titolo != null &&
        top.frame_titolo.document != null &&
        top.frame_titolo.document.formTitolo != null &&
        top.frame_titolo.document.formTitolo.titolo != null)
        top.frame_titolo.document.formTitolo.titolo.value = titolo;  
        
//    if (window.showModalDialog)  
//    {   //Explorer                
//    	if ( window.top.dialogArguments != null ) {  	     
//    	     window.top.dialogArguments.top.frame_titolo.document.formTitolo.titolo.value = titolo;
//        }
//        else top.frame_titolo.document.formTitolo.titolo.value = titolo;      	 	        	        	
//    } 
//    else 
//    {   //Netscape 
//    	if (top.opener != null )
//    	{    	    
//            top.opener.top.frame_titolo.document.formTitolo.titolo.value = titolo;
//        }
//    else top.frame_titolo.document.formTitolo.titolo.value = titolo;      	        
//    }         
}	 



//--------------------------------------------------------------------------------------
// Script per gestione finestre modali 
//--------------------------------------------------------------------------------------
var winModalWindow
 
// *** showModalWindow(): visualizza "page" in una nuova finestra modale 
function showModalWindow(page)
{    
    if (window.showModalDialog) {
        window.showModalDialog (page, window,"dialogWidth=600px;dialogHeight=400px;help=no;status=no;resizable=yes");                               
        //window.open (page,"ModalChild","dependent=yes,width=600px,height=400px,help=no,status=no,resizable=yes");   
    }	
    else {    
    	window.top.captureEvents (Event.CLICK|Event.FOCUS);  	      
        window.top.onclick=IgnoreEvents; 
        window.top.onfocus=HandleFocus; 
        winModalWindow = window.open (page,"ModalChild","dependent=yes,width=600px,height=400px,help=no,status=no,resizable=yes");   
        winModalWindow.focus();      
    }    
 }

// *** closeModalWindow(): chiude la finestra modale;
function closeModalWindow()
{    
    if (window.showModalDialog) {
        window.top.close();        
    }	
    else {        	
    	window.top.opener.releaseEvents (Event.CLICK|Event.FOCUS) ;	
    	window.top.close();    
    }    
}


// *** IgnoreEvents(e): ignora gli eventi di tipo "e" (non fa nulla)
function IgnoreEvents(e)
{
    return false
}

// *** HandleFocus(): se presente la finestra modale, gli ritorna il controllo (focus)
function HandleFocus()
{    
    if (winModalWindow)
    {
        if (!winModalWindow.closed) {           	
            winModalWindow.focus();        
        }
        else {	 
            window.top.releaseEvents (Event.CLICK|Event.FOCUS)            
        }
    }
    return false
}
//FUNZIONE PER DISABILITARE TUTTI  I CAMPI DI UN FORM 
function disableAll(form_input)
{

 for (var contaElem=0;contaElem < form_input.length;contaElem ++)
     {
 
        if // il type è definito, ma non è 'button'   
           ((form_input.elements[contaElem].type) && 
            (form_input.elements[contaElem].type != "button"))
            {  
               disableField(form_input.elements[contaElem]); 
            } 
     }
}	

//LE DUE FUNZIONI SEGUENTI SERVONO PER DISABILITARE/ABILITARE I CAMPI DI UN FORM 
function enableField(oggetto_input)
{
   if (ns)
       {
       	 oggetto_input.className = "edit";
         oggetto_input.disabled= false;
       }
   else
       {
        if (ie)
       	   {
            oggetto_input.className = "edit";
            oggetto_input.disabled= false;
           }	
       }	
}
function disableField(oggetto_input)
{   
   if (ns)
       {  
       	   oggetto_input.className = "view";
           oggetto_input.disabled= true;
       }
   else
       {
        if (ie)
       	   {
            oggetto_input.className = "view";
            //nel caso di textarea la disabilitazione è a livello di tag html
            if (oggetto_input.type != "textarea" )  
                oggetto_input.disabled= true;
       	   }	
       }	
}

//FUNZIONE PER COMPILAZIONE DEI CAMPI OBBLIGATORI DELLA FORM

function checkrequired(campo){
	var pass = true;
	if (document.images){
		for(i=0; i<campo.length ;i++){
			var valore = campo.elements[i];
			if(valore.name.substring(0,8)=="required"){
				if(valore.type=="text"&& valore.value==''){
					pass = false;
					break;
					}
				}
			}
		}
	if(!pass){
		alert("COMPLETARE COMPILAZIONE DEL FORM");
		return false;
	}else 
	return true;
	}

/////////////////////////////////////////////////////////////////////////////
//FUNCTION RELATIVE ALLE OPERAZIONI SULLE DATE
///////////////////////////////////////////////////////////////////////////
// Inversione  della data 
function invDate(objDate)
{
   var invobjDate  = objDate.substr(6,4) + "/" + objDate.substr(3,2) + "/" + objDate.substr(0,2) ;      
 
   return invobjDate ;
}

// compare tra due date
// i valori di ritorno sono:
// 1  = la prima data è maggiore della seconda
// 0  = la prima data è uguale alla seconda
// -1 = la prima data è minore della seconda
function compareDate(objFDate,objSDate)
{
   var esito=0;
   var invobjFDate = invDate(objFDate);
   var invobjSDate = invDate(objSDate);
 
   if (invobjFDate > invobjSDate)
   {
       esito = 1
   }
   else {
       if (invobjFDate == invobjSDate)	 	
        {
       	    esito = 0;
        }
       else {	    
           esito = -1;
            } 
        }     
   return esito ;
}


/////////////////////////////////////////////////////////////////////////////
//FUNCTION RELATIVE AL NAVIGATOR
///////////////////////////////////////////////////////////////////////////

//Ritorna a 'step' pagine indietro nella history del navigator 
function navigatorBack(win,step)
{
   win.location = "/jsp/SWGeneralizzato/back.jsp?step="+step;
}

//Ricarica la pagina corrente 
function navigatorReload(win)
{
   win.location = "/jsp/SWGeneralizzato/back.jsp?step=0";
}   

/////////////////////////////////////////////////////////////////////////////
//FUNCTION RELATIVE ALLA DIGITAZIONE DEI CAMPI DI EDIT
/////////////////////////////////////////////////////////////////////////////

// *** formatText(s): converte tutti i caratteri della stringa s in caratteri     ***
// ***                maiuscoli durante la digitazione.                           *** 
// ***                Deve essere chiamata in seguito all'evento onKeydown        ***
// ***                sul campo interessato:                                      ***
// ***                es. onKeyDown="formatText(document.nomeForm.nomeCampo);"    ***
// *** N.B. Funziona sotto Internet Explorer, sotto Netscape Navigator no         ***
// PARAMETRI      s = stringa da elaborare (può contenere qualsiasi carattere)

//function formatText(s)
//{
//		e = window.event;
//
//		// il carattere digitato è alfabetico minuscolo
//		if ((e.keyCode > 64 && e.keyCode < 91))
//		{
//			var keyStr = String.fromCharCode(e.keyCode);
//			s.value = s.value.substr(0,s.value.length) + keyStr.toUpperCase();
//			e.returnValue = false;
//		}
//		else // verifico se il carattere digitato è una vocale accentata
//			switch(e.keyCode)
//			{
//				case 222:	s.value += "À";
//						e.returnValue = false;
//						break;
//				case 221:	s.value += "Ì";
//						e.returnValue = false;
//						break;
//				case 192:	s.value += "Ò";
//						e.returnValue = false;
//						break;
//				case 191:	s.value += "Ù";
//						e.returnValue = false;
//						break;
//				case 186:	if (e.shiftKey) // se era premuto il tasto 'shift'
//							s.value += "É";
//						else	
//							s.value += "È";
//						e.returnValue = false;
//						break;
//			}			
//}


// *** formatNumber(e):    verifica che siano digitati soli caratteri numerici, impedisce ***
// ***                     la visualizzazione ed elimina dal campo di edit il carattere   ***
// ***                     editato se non numerico.                                       ***
// *** Internet Explorer:  Deve essere chiamata in seguito all'evento onKeydown sul campo ***
// ***                     interessato:                                                   ***
// ***                     onKeyDown="formatNumber(e);"                                   ***
// *** Netscape Navigator: Deve essere definito come gestore dell'evento onKeyDown sul    ***
// ***                     campo interessato:                                             ***
// ***                     es. document.nomeForm.nomeCampo.onkeydown = formatNumber;      ***
// *** PARAMETRI           e = oggetto Event (viene valorizzato solo nel caso di Netscape ***
// ***                         Navigator dal gestore dell'evento)                         ***

function formatNumber(e)
{
	if (ie)
	{
		e = window.event;

		if (!(e.keyCode>47 && e.keyCode<58 ) && !(e.keyCode>95 && e.keyCode<106))
			switch (e.keyCode)
			{
				case  8:	break; // delete
				case  9:	break; // tab
				case 13:	break; // invio
				case 37:	break; // freccia a sx
				case 39:	break; // freccia a dx
				case 45:	break; // ins
				case 46:	break; // canc
				default:	e.returnValue = false;
			}
	}
	if (ns)
	{
		if (!(e.which>47 && e.which<58 ))
			switch (e.which)
			{
				case  8:	break; // delete
				case 13:	break; // invio
				default:	return false;
			}
	}
	return true;
}

// *** formatNumber(e):    verifica che siano digitati soli caratteri numerici, impedisce ***
// ***                     la visualizzazione ed elimina dal campo di edit il carattere   ***
// ***                     editato se non numerico.                                       ***
// *** Internet Explorer:  Deve essere chiamata in seguito all'evento onKeydown sul campo ***
// ***                     interessato:                                                   ***
// ***                     onKeyDown="formatNumber(e);"                                   ***
// *** Netscape Navigator: Deve essere definito come gestore dell'evento onKeyDown sul    ***
// ***                     campo interessato:                                             ***
// ***                     es. document.nomeForm.nomeCampo.onkeydown = formatNumber;      ***
// *** PARAMETRI           e = oggetto Event (viene valorizzato solo nel caso di Netscape ***
// ***                         Navigator dal gestore dell'evento)                         ***

function formatNumber(e)
{
	if (ie)
	{
		e = window.event;

		if (!(e.keyCode>47 && e.keyCode<58 ) && !(e.keyCode>95 && e.keyCode<106))
			switch (e.keyCode)
			{
				case  8:	break; // delete
				case  9:	break; // tab
				case 13:	break; // invio
				case 37:	break; // freccia a sx
				case 39:	break; // freccia a dx
				case 45:	break; // ins
				case 46:	break; // canc
				default:	e.returnValue = false;
			}
	}
	if (ns)
	{
		if (!(e.which>47 && e.which<58 ))
			switch (e.which)
			{
				case  8:	break; // delete
				case 13:	break; // invio
				default:	return false;
			}
	}
	return true;
}

// ***     formatText2(e,s): verifica che siano digitati soli caratteri alfabetici impedisce  ***
// ***                         la visualizzazione ed elimina dal campo di edit il carattere   ***
// ***                         editato se non alfabetico                                      ***
// *** Internet Explorer:      Deve essere chiamata in seguito all'evento onKeydown sul campo ***
// ***                         interessato:                                                   ***
// ***                         onKeyDown="formatText2(e,this);"                         ***
// *** PARAMETRI               e = oggetto Event (viene valorizzato solo nel caso di Netscape ***
// ***                         Navigator dal gestore dell'evento)                             ***
// ***                         s = stringa da elaborare                                       ***

// *** formatCommaNumber(e,s): verifica che siano digitati soli caratteri numerici impedisce  ***
// ***                         la visualizzazione ed elimina dal campo di edit il carattere   ***
// ***                         editato se non numerico, consente l'inserimento della virgola  ***
// ***                         decimale.                                                      ***
// *** Internet Explorer:      Deve essere chiamata in seguito all'evento onKeydown sul campo ***
// ***                         interessato:                                                   ***
// ***                         onKeyDown="formatCommaNumber(e,this);"                         ***
// *** Netscape Navigator:     Deve essere chiamata dal gestore dell'evento onKeyDown sul     ***
// ***                         campo interessato.                                             ***
// ***                         Il gestore va definito nel seguente modo:                      ***
// ***                           function nomeGestoreEvento(e)                                ***
// ***                           { return formatCommaNumber(e,document.nomeForm.nomeCampo); } ***
// ***                         quindi lo si applica al campo:                                 ***
// ***                           document.nomeForm.nomeCampo.onkeydown = formatCommaNumber;   ***
// *** PARAMETRI               e = oggetto Event (viene valorizzato solo nel caso di Netscape ***
// ***                         Navigator dal gestore dell'evento)                             ***
// ***                         s = stringa da elaborare                                       ***

function formatCommaNumber(e,s)
{
	if (ie)
	{
		e = window.event;
//    alert(e.keyCode);
		if (!(e.keyCode>47 && e.keyCode<58 ) && !(e.keyCode>95 && e.keyCode<106) &&
		    //  e.keyCode != 188 &&  
        e.keyCode != 110
        &&  e.keyCode != 190
        )
			switch (e.keyCode)
			{
				case  8:	break; // delete
				case  9:	break; // tab
				case 13:	break; // invio
				case 37:	break; // <--
				case 39:	break; // -->
				case 45:	break; // ins
				case 46:	break; // canc
				default:	e.returnValue = false;
			}
//		if (e.keyCode === 188 && s.value.indexOf(",") != -1)
//			e.returnValue = false;

		if (e.keyCode === 110 && s.value.indexOf(".") != -1)
			e.returnValue = false;
		if (e.keyCode === 190 && s.value.indexOf(".") != -1)
			e.returnValue = false;

	}
	
	if (ns)
	{
		if (!(e.which>47 && e.which<58 ) && e.which != 46)
			switch (e.which)
			{
				case  8:	return true; // delete
				case 13:	return true; // invio
				default:	return false;
			}
		
		if (e.which === 46 && s.value.indexOf(".") != -1)
				return false;
	}
	return true;	
}


function formatText2(e)
{
	if (ie)
	{
		e = window.event;
		
		if (!(e.keyCode>=65 && e.keyCode<=90 ))
			switch (e.keyCode)
			{
				case  8:	break; // delete
				case  9:	break; // tab
				case 13:	break; // invio
				case 37:	break; // <--
				case 39:	break; // -->
				case 45:	break; // ins
				case 46:	break; // canc
				default:	e.returnValue = false;
			}
	}
	
	return true;	
}

// *** formatDate(s):      verifica che siano digitati soli caratteri numerici, impedisce ***
// ***                     la visualizzazione ed elimina dal campo di edit il carattere   ***
// ***                     editato se non numerico, inserisce il separatore '/' dopo il   ***
// ***                     giorno e dopo il mese (nel formato GG e MM rispettivamente).   ***
// *** Internet Explorer:  Deve essere chiamata in seguito all'evento onKeydown sul campo ***
// ***                     interessato:                                                   ***
// ***                     onKeyDown="formatDate(this);"                                  ***
// *** PARAMETRI           s = stringa da elaborare                                       ***

function formatDate(s)
{
	if (ie)
	{
		e = window.event;
                
		if (s.value.length <10)
		{
			if (!(e.keyCode>47 && e.keyCode<58) && !(e.keyCode>95 && e.keyCode<106))
				switch (e.keyCode)
				{
					case  8:{ // se cancello una cifra dopo lo '/' devo cancellare
						  // anche il separatore
							if (s.value.length===4 || s.value.length===7)
							{
								s.value=s.value.substr(0,s.value.length-2);
								e.returnValue = false;
							}
							break; // delete
						}
					case   9:	break; // tab
					case  13:	break; // invio
					case  46:	break; // canc
					case 111:       break; // "/"
					default:	e.returnValue = false;
				}
			else if ( (s.value.length === 2 || s.value.length === 5) && !(e.shiftKey) )
				  s.value += "/"; 

		}
		else // non consento la digitazione di un anno > 9999
			switch (e.keyCode)
			{
				case  8:	break; // delete
				case  9:	break; // tab
				case 13:	break; // invio
				case 46:	break; // canc
				default:	e.returnValue = false;
			}
	}

	if (ns)
	{
//		var tmp = s.value;
//		if (tmp.length < 10 && e.which>47 && e.which<58)
//		{
//			if (tmp.length === 2 || tmp.length === 5)
//				tmp += "/";
//			tmp += String.fromCharCode(e.which);
//			s.value = tmp;
//			return false;
//		}
//		else switch (e.which)
//		{
//			case  8:{
//					s.value = s.value.substr(0,s.value.length-1);
//					return false;
//				}
//			case 13:	return true; // invio
//			default:	return false;
//		}
//		return false;
	}	
	return true;	
}

// *** checkNumber(s):  verifica che siano stati digitati soli caratteri numerici.  ***
// ***                  Deve essere chiamata in seguito all'evento onBlur sul campo ***
// ***                  interessato:                                                ***
// ***                  es. onBlur="checkNumber(document.form.nomeCampo;)"          ***
// *** N.B. Serve per la verifica del campo digitato sotto Netscape Navigator       ***
//    PARAMETRI   s = stringa da elaborare

function checkNumber(s)
{
	if (ie){}
	if (ns && !isInteger(s.value))
	{
		alert("ERRORE: Il campo deve essere numerico");
		return false;
	}
	return true;
}

// *** checkCommaNumber(s): verifica che sia stato digitato un numero con virgola.  ***
// ***                      Deve essere chiamata in seguito all'evento onBlur sul   ***
// ***                      campo interessato:                                      ***
// ***                      es. onBlur="checkCommaNumber(document.form.nomeCampo;)" ***
// *** N.B. Serve per la verifica del campo digitato sotto Netscape Navigator       ***
//    PARAMETRI   s = stringa da elaborare

function checkCommaNumber(s)
{
	if (ie){}
	if (ns)
	{
		var checkOK = true;
		var foundComma = false;

		if (s.value == ",") 
			checkOK = false;
		else
		{	// Analizza la stringa carattere per carattere finchè
  			// non ne trova uno non numerico.
			// Se lo trova ritorna false, altrimenti true.

  			for (var i = 0; i < s.value.length; i++)
    			{// Controlla che il carattere corrente sia numerico.
	        		var c = s.value.charAt(i);
	
		        	if ((c == ",") && !foundComma) 
	        			foundComma = true;
	        		else if (!isDigit(c)) checkOK = false;
  			}
  		}

  		if (!checkOK)
  			alert("ERRORE: Il campo deve essere numerico con virgola");
  			
		return checkOK;
  	}
  	return true;
}

// *** checkCorrectDate(s): verifica che sia stata digitata una data corretta, cioè   ***
// ***                      con giorno G o GG, mese M o MM, anno YY o YYYY.           ***
// ***                      I separatori considerati validi sono '/',' ','.','-'.     ***
// ***                      Richiama la funzione checkFormatDate(s) di FormCheck.     ***
// *** Netscape Navigator:  Deve essere chiamata dal gestore dell'evento onBlur sul   ***
// ***                      campo interessato.                                        ***
// ***                      Il gestore va definito nel seguente modo:                 ***
// ***                       function nomeGestoreEvento(e)                            ***
// ***                       { checkCorrectDate(document.nomeForm.nomeCampo); }       ***
// ***                      quindi lo si applica al campo:                            ***
// ***                      quindi lo si applica al campo (per evitare problemi con   ***
// ***                      Internet Explorer è meglio inserirlo in un if):           ***
// ***                       if(ns)                                                   ***
// ***                         document.nomeForm.nomeCampo.onblur = checkCorrectDate; ***
// *** N.B. Serve per la verifica del campo data digitato sotto Netscape Navigator    ***
//    PARAMETRI   s = stringa da elaborare

function checkCorrectDate(s)
{
//	if (ie)	{}
	if (ns)
	{
		var checkOK = true;
		
		// Consento tutti i formati data con giorno e mese numerici di uno o due caratteri,
		// anno di due o quattro caratteri, e 
		for (var i=0;i<s.value.length;i++)
			if (!isDigit(s.value.charAt(i)) && 
			     s.value.charAt(i) != '/'   &&
			     s.value.charAt(i) != ' '   &&
			     s.value.charAt(i) != '-'   &&
			     s.value.charAt(i) != '.')
				checkOK = false;
		
		if (!checkOK || isInteger(s.value) || !checkFormatDate(s))
		{
			alert("ERRORE: Il campo deve essere una data");
			return false;
		}
	}
	return true;
}

function setIdPratica(pratica)
{   
    
    if (pratica == "") return;    
    
    if (top != null &&
        top.frame_titolo != null &&
        top.frame_titolo.document != null &&
        top.frame_titolo.document.formInfoNavig != null &&
        top.frame_titolo.document.formInfoNavig.infoItemLabel != null)
        top.frame_titolo.document.formInfoNavig.infoItemLabel.value = "ID Pratica:";  

    if (top != null &&
        top.frame_titolo != null &&
        top.frame_titolo.document != null &&
        top.frame_titolo.document.formInfoNavig != null &&
        top.frame_titolo.document.formInfoNavig.infoItemValue != null)
        top.frame_titolo.document.formInfoNavig.infoItemValue.value = pratica;  

}

function resetIdPratica()
{   
    
    if (top != null &&
        top.frame_titolo != null &&
        top.frame_titolo.document != null &&
        top.frame_titolo.document.formInfoNavig != null &&
        top.frame_titolo.document.formInfoNavig.infoItemLabel != null)
        top.frame_titolo.document.formInfoNavig.infoItemLabel.value = "";  

    if (top != null &&
        top.frame_titolo != null &&
        top.frame_titolo.document != null &&
        top.frame_titolo.document.formInfoNavig != null &&
        top.frame_titolo.document.formInfoNavig.infoItemValue != null)
        top.frame_titolo.document.formInfoNavig.infoItemValue.value = "";  

}

function insertStatoIntervento(objStat, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objStat.options[i++] = new Option("","",false,false);       
   }       
   objStat.options[i++] = new Option("Aperti","Aperti",false,false); 
   objStat.options[i++] = new Option("Chiusi per SdM","Chiusi per SdM",false,false); 
   objStat.options[i++] = new Option("Chiusi per CS","Chiusi per CS",false,false); 
   objStat.options[i++] = new Option("Sospesi","Sospesi",false,false);
   objStat.options[i++] = new Option("Annullati","Annullati",false,false);
   objStat.options[i++] = new Option("Uscite a vuoto","Uscite a vuoto",false,false); 
   objStat.options[i++] = new Option("Richiesta di sospensione","Richiesta di sospensione",false,false);
   objStat.options[i++] = new Option("Richiesta Uscita a Vuoto","Richiesta Uscita a Vuoto",false,false);
   objStat.options[i++] = new Option("Chiusi con costi aggiuntivi","Chiusi con costi aggiuntivi",false,false);   
}

function insertEnteEsecutore(objEnte, eok)
{
   var i = 0;
   if (eok == true) 
   {
       objEnte.options[i++] = new Option("","",false,false);       
   }       
   objEnte.options[i++] = new Option("S.d.M.","S.d.M.",false,false); 
   objEnte.options[i++] = new Option("C.S.","C.S.",false,false);      
}

function writeFooter()
{
document.write("<HR><TABLE><TR><TD class='footer'>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a cura di: Regione Emilia-Romagna - Direzione Generale Agricoltura.<br>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"/jsp/SWGeneralizzato/contatti.jsp\" >Assistenza</a><BR>");
document.write("</TD></TR></TABLE>");
}

function formatDecimalNumber(value, numInt, numDec)
{	
		//controllo corretta formattazione campo (es. 4 interi + ',' + 2 decimali)
		//NB: indexOf parte da 0
		if (value  == '')
			return true;
			
		var lenTot = value.length - 1;
		var posComma =  value.indexOf(",");
		if (posComma == -1)
		 posComma =  value.indexOf(".");
		 
		if (posComma == 0)
			return false;		 		 
			
		if (posComma != -1 )
		{							
				//controllo interi
				if ((posComma) > numInt )
					return false;
				//controllo decimali
				if ((lenTot - posComma) > numDec) 				
					return false;			
		}
		else
		{
			//controllo interi
			if (lenTot  >= numInt)
				return false;
		}		
		return true;
}

function Confronta_Date(data1,data2)
{
// Metodo che permette il confronto fra due date
	
   var y1,y2,m1,m2,d1,d2;
   var tmpdata1,tmpdata2;
  
     d1=data1.value.substring(0,2);
     d2=data2.value.substring(0,2);
     m1=data1.value.substring(3,5);
     m2=data2.value.substring(3,5);
     y1=data1.value.substring(6);
     y2=data2.value.substring(6);
  
   tmpdata1=y1+m1+d1;
   tmpdata2=y2+m2+d2;
  
   if(tmpdata1>tmpdata2)
      return 1;
	// Ritorna 1 se la data1 e maggiore di data 2      
   if(tmpdata2>tmpdata1)   
      return 2;
      // Ritorna 2 se la data2 e maggiore di data 1
   if(tmpdata1==tmpdata2)   
      return 0;
      // Ritorna 0 se la data1 e uguale a data 2

}

function formatValue(value)
{
    //funzione di formattazione del numero in un astringa con 2 decimali (value.00)
    var tmpValue = "";
    var tmpDec = "";
    var tmpInt = "";
    var tmpValueString = value.toString();

    if (tmpValueString.length == 0 || tmpValueString == "" || tmpValueString== "0")
        return "0";

    var posComma = tmpValueString.indexOf(".");
    if (posComma > -1)
    {
      tmpInt = tmpValueString.substr(0,posComma);
      tmpDec = tmpValueString.substr(posComma+1);
      if (tmpDec.length == 1)    
        tmpDec =  tmpDec + "0";

      tmpValue = tmpInt + "." + tmpDec;
    }
    else
      tmpValue = tmpValueString + ".00";
    
    return tmpValue;
}

function roundVal(num,dec) {
 
 if (dec==0) return Math.round(num);
 
 num=parseInt(Math.round(parseFloat(Math.abs(num))*Math.pow(10,dec)),10);
 var tmp_num = num.toString();
 
 while (tmp_num.length<dec+1) 	tmp_num="0"+tmp_num;
  
 // get decimal part
 tmp_num=""+tmp_num;
   dpart=tmp_num.substring((tmp_num.length-dec),tmp_num.length+1);

// remove trailing 0
 while (dpart.charAt(dpart.length-1)=="0") dpart=dpart.substring(0,dpart.length-1);
 
 tmp_num=tmp_num.substr(0,(tmp_num.length-dec));
 
 if (dpart==0) return tmp_num;
 else   return tmp_num+"."+dpart;
 
}


// *** GG xx-07-2002 - data una stringa in GG-MM-AAAA la formatta come AAAA-MM-GGG e inserendo il ***
// ***                 carattere di separazione come "-".                                         *** 
function formatDateForReport(strDate)
{ 	
	var newDate = "";
	
	if (strDate == null || strDate == "") return newDate;
	
	newDate = strDate.substr(6,4) + "-" + strDate.substr(3,2) + "-" + strDate.substr(0,2);
	
	return newDate;
}

//MARZIA:10/9/2002
// **********************************************
//funzione che riceve il nome din un form e un array contenente il nome di un gruppo di campi
//Disabilita tutti i controlli del for:
//-se bottoni li disabilita, altrimenti li mette readOnly
//poi riabilita i controlli i cui nomi sono contenuti nell'array ricevuto
// GG 21-10-2002 - aggiunto controllo cambio di stile (come "class=xxx")
// ********************************
function disabilitaForm(nameForm,campiAbilita){
  //disabilita tutti i controlli di una data form
  var frm;
  frm = document.forms[nameForm];
  for (var i=0; i< frm.elements.length; i++){
    if ((frm.elements(i).type == "button")){ 
      frm.elements(i).disabled=true;
      frm.elements(i).className="GreyButton";   // GG 21-10-2002 - cambio di stile: equivale al consueto "CLASS=xxx"
    }else {
      if (frm.elements(i).type == "select-one"){
        frm.elements(i).disabled=true;
        frm.elements(i).className="VIEW";
      }
      else{
        frm.elements(i).readOnly=true;
        frm.elements(i).className="VIEW";
      }
    }
  }//for
  //riabilita i campi i cui nomi sono contenuti nel vettore campiAbilita
  if (campiAbilita != null){
    for (i=0; i<campiAbilita.length; i++){
      if ((frm.elements[campiAbilita[i]].type == "button")){ 
        frm.elements[campiAbilita[i]].disabled=false;
        frm.elements[campiAbilita[i]].className="BlueButton";   // GG 21-10-2002 - cambio di stile: equivale al consueto "CLASS=xxx"
      }else {
        if (frm.elements[campiAbilita[i]].type == "select-one"){
          frm.elements[campiAbilita[i]].disabled=false;
          frm.elements[campiAbilita[i]].className="VIEW";
        }
        else{
          frm.elements[campiAbilita[i]].readOnly=false;
          frm.elements[campiAbilita[i]].className="VIEW";
        }
      }
    }//for
  }
}
// ******************************************************************

//MARZIA 15/10/2002***************
//funzione che riceve un oggetto di tipo SELECT (sel) e ritorna
//una stringa del tipo 'cod1','cod2','cod3' ecc..
//dove i codici sono i valori degli elementi della combo  sel
function concatenaCodici(sel){
  var str = "";
  for (var j=0; j < sel.options.length; j++){
    if (str != ""){
      str = str + "-"  + sel.options(j).value ;
    }else{
      str = str   + sel.options(j).value;
    }
  }
  return  escape(str);
}
// **********************


// Funzione che converte una data da formato STRINGA "GG/MM/AAAA" in oggetto DATE.
// by GIGI 9/5/2003
function convertString2Date (dataStr) {
  var aa = parseInt(dataStr.substr(6,4),10);
  var mm = parseInt(dataStr.substr(3,2),10)-1;
  var gg = parseInt(dataStr.substr(0,2),10);
  var dataObj = new Date(Date.UTC(aa, mm, gg));
  return dataObj;
}

// Funzione che confronta due DATE in formato STRINGA "GG/MM/AAAA".
// Rende il NUMERO DI GIORNI tra le due date:
//  == 0  -->  date coincidenti
//  > 0   -->  data1 < data2
//  < 0   -->  data1 > data2
// (ossia esegue la sottrazione tra data2 e data1)
// by GIGI 9/5/2003
function confrontaDate(dataStr1, dataStr2) {
  var data1 = convertString2Date (dataStr1);
  var data2 = convertString2Date (dataStr2);

  var diffTime = data2.getTime() - data1.getTime();
  var diffGiorni = parseInt(diffTime/1000/60/60/24); 
  return diffGiorni;
}
// **********************

// funzione per aggiungere n giorni alla data passata
// NB: prima di chiamare la funzione verificare che 
// la data passata sia effettivamente una data corretta (es. validateDate(......)!!!
// input: stringa dd/mm/aaaa
// return: stringa dd/mm/aaaa
function aggiungiGiorni(data, giorni)
{	
	var dataMod = convertString2Date(data);	
    var dataNew = new Date(dataMod.getTime() + giorni*86400000);
    var vDay = dataNew.getDate();
	var vMonth = dataNew.getMonth() + 1;
	var vYear = dataNew.getFullYear();
	var vDD = (vDay.toString().length < 2) ? "0" + vDay : vDay;	
	var vMM = (vMonth.toString().length < 2) ? "0" + vMonth : vMonth;	
	var vY4 = new String(vYear);
	var vData = vDD + "\/" + vMM + "\/" + vY4;

    return vData;
}

// funzione per controllare se una data fa parte di un anno bisestile
// la data da passare alla funzione dev'essere prima passata dalla funzione validateDate(data)
// Ritorna TRUE se l'anno della data è bisestile

function isAnnoBisestile(data){
	var dataObj = convertString2Date(data);
	var anno = dataObj.getFullYear();
	if (anno%100==0 && anno%400==0){
		return true;
	}
	if (anno%4==0){
		return true;
	}
	return false;
	
}