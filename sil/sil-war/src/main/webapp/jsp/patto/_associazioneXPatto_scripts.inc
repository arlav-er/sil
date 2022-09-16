<%--
///////////////////////////////////////////////////////////////////////////////////
//      all'interno della sezione <script> inserire
//
//    <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
//
//          INSERIRE CON UN COPIA INCOLLA
//
// function getFormObj() {return document.<tuoNomeForm>;}
// 
///////////////////////////////////////////////////////////////////////////////////
//
//	Quando da un link si chiama la PAGE per una operazione ad esempio di cancellazione
//  bisogna inserire nel link una coppia nome=valore generata dalla funzione: 

//             getParametersForPatto(prgElementoAssociatao); // es. s+=getParametersForPatto(prgMansione);
//
/////////////////////////////////////////////////////////////////////
// 
//  bisogna specificare in onsubmit della form la funzione 
//               controllaPatto();
/////////////////////////////////////////////////////////////////////     
--%>

/**
* Array che contiene le chiavi dei records di am_lav_patto_scelta associati alla tabella gestita dalla pagina
* Serve per poter valorizzare il parametro PRG_LAV_PATTO_SCELTA quando la richiesta avviene tramite link
*/
var pkLavPattoScelta = new PattoMap();

function putPattoKey(k,v) {
	this.val[this.val.length]=v;
	this.key[this.key.length]=k;	
}
function getPattoKey(k){	
	var i=0;
	for (;i<this.key.length;i++) {
		if (this.key[i]==k)
			break;			
	}
	if (i==this.key.length) return null;
	else 
	return this.val[i];
}
/**
* Classe che emula una Map
*/
function PattoMap() {
	this.val = new Array();
	this.key = new Array();	
	this.put = putPattoKey;
	this.get = getPattoKey;
}

/**
* Apre una popup window con la lista dei patti aperti (al massimo una sola riga)
* per consentire la loro associazione alle informazioni specifiche della pagina (es. indisponibilità temporanee)
*/
function cercaIlPatto(cdnLav) {
    var urlpage="";
    urlpage+="AdapterHTTP?";
    urlpage+="cdnLavoratore="+cdnLav+"&";
    urlpage+="PAGE=RicercaUltimoPattoPage";
    window.open(urlpage,"Patti", 'toolbar=0, scrollbars=1,width=800,height=400'); 
}

function cercaIlPattoMobilita(cdnLav) {
    var urlpage="";
    urlpage+="AdapterHTTP?";
    urlpage+="cdnLavoratore="+cdnLav+"&SCELTASOLOPATTO297=S&";
    urlpage+="PAGE=RicercaUltimoPattoPage";
    window.open(urlpage,"Patti", 'toolbar=0, scrollbars=1,width=800,height=400'); 
}

/**
* Permette di valorizzare i campi relativi al patto
* Occhio al name dato alla form: se diverso da "form1" impostare correttamente la variabile pattoFrom della funzione
*/
function setPattoAssociato(tipologia, dataStipula, stato, prgPatto) {
    var pattoForm=getFormObj();
    if (pattoForm==null) alert('la variabile globale pattoForm deve essere valorizzata esplicitamente');
    pattoForm.PRG_PATTO_LAVORATORE.value=prgPatto;
    pattoForm.TipoPatto.value=tipologia;
    pattoForm.DATSTIPULA.value=dataStipula;
    if (stato!=null && stato.lenght >25) stato= stato.substring(0,23)+"...";
    pattoForm.StatoPatto.value=stato;
    if (document.getElementById("T_S_PATTO").style.display=="none")
	    cambia(document.getElementById("I_SEL_S_PATTO"), document.getElementById("T_S_PATTO"));
}
function resetPatto() {
    var pattoForm=getFormObj();
    if (pattoForm==null) alert('la variabile globale pattoForm deve essere valorizzata esplicitamente');
    pattoForm.PRG_PATTO_LAVORATORE.value="";
    pattoForm.TipoPatto.value="";
    pattoForm.DATSTIPULA.value="";
    pattoForm.StatoPatto.value="";
}
/**
*   controlla se si vuole cancellare una associazione al patto. In questo caso conpare una finestra di conferma.
*   imposta il valore del parametro operazioneColPatto necessrio al modulo per scegliere quale operazione eseguire
*   da inserire in onSubmit della form
*/
function controllaPatto(){
    var pattoForm=getFormObj();
    if (pattoForm==null) alert('la variabile globale pattoForm deve essere valorizzata esplicitamente');
    var op = getOperation(pattoForm.PRG_PATTO_LAVORATORE.value);
    setParameterForPatto(pattoForm, op);
    var ret=true;
    
    if (op==1)
        ret = confirm("Sei sicuro di voler cancellare la associazione col patto?");
    return ret;
}
/**
* viene valorizzata quando viene caricata la pagina con il prgPattoLavoratore presente
*/
var _prgPatto;

/**
* @return   -1  Inserisci la associazione
*           1   cancella la associazione
*           0   non eseguire operazioni con la tabella am_lav_patto_scelta
*/
function getOperation(newPrgPatto){
    //alert(newPrgPatto+'--'+_prgPatto);
	if (newPrgPatto==null) newPrgPatto="";
    var ret=0;
    if (_prgPatto=='') {
        if (newPrgPatto=='')
            ret=0;
        else 
            ret=-1;
    }
    else {
        if (_prgPatto==newPrgPatto) 
            ret=0;
        else {
            if (newPrgPatto=='')
                ret=1
            else 
                ret=-1;
        }
    }
    //alert('ret='+ret);
    return ret;
}

/**
*   imposta il valore del parametro che informa il modulo sul tipo di operazione da eseguire sulla tabella am_lav_patto_scelta
*/
function setParameterForPatto(f,op){
    //alert(f.operazioneColPatto.value+'--'+op);
    f.operazioneColPatto.value=op;
}
/**
* @return   ritorna la coppia nome=valore da accodare allo url nel caso si faccia una richiesta tramite link
*/
function getParametersForPatto(prgLavPattoScelta) {
    
//	var prgLavPattoScelta = pkLavPattoScelta.get(keyTabAssociata);
    if (prgLavPattoScelta==-1) prgLavPattoScelta='';
    var op = getOperation(prgLavPattoScelta);//alert(keyTabAssociata+"-"+prgLavPattoScelta+"   op="+op);
    if (op!='0') op='1'; // cancello il record di am_lav_patto_scelta presente (0=non fare niente 1=cancella -1=inserisci)
    // questa funzione viene chiamata soltanto quando c'e' una cancellazione
    // invece getOperation funziona bene quando viene chiamata da una submit
    var s="operazioneColPatto="+op;
    s+="&PRG_LAV_PATTO_SCELTA="+prgLavPattoScelta;
    return s;
}