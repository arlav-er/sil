/********************************************
* Orologio									*
********************************************/
// Questa funzione mostra in un INPUT TEXT l'ora corrente
// Nella pagina HTML occorre inserire il seguente codice
/****************************************************************
*<FORM name="clockform">
*Current Time: <INPUT TYPE="text" name="clockspot" size="15">
*</FORM>
*<SCRIPT language="JavaScript">startclock();</SCRIPT>
****************************************************************/
/*
function startclock_1stV()
{
    var thetime=new Date();
    
    var nhours=thetime.getHours();
    var nmins=thetime.getMinutes();
    var nsecn=thetime.getSeconds();
    var AorP=" ";
    
    if (nhours>=12)
        AorP="P.M.";
    else
        AorP="A.M.";
    
    if (nhours>=13)
        nhours-=12;
    
    if (nhours==0)
     nhours=12;
    
    if (nsecn<10)
     nsecn="0"+nsecn;
    
    if (nmins<10)
     nmins="0"+nmins;
    
    document.clockform.clockspot.value=nhours+": "+nmins+": "+nsecn+" "+AorP;
    
    setTimeout('startclock()',1000);
} 
*/

// Questa funzione mostra in un INPUT TEXT l'ora corrente
// Nella pagina HTML occorre inserire il seguente codice
/****************************************************************
*<FORM name="clockform">
*Current Time: <INPUT TYPE="text" name="clockspot" size="40">
*</FORM>
*<SCRIPT language="JavaScript">startclock();</SCRIPT>
*****************************************************************/
/*
function startclock()
{
     var thetime=new Date();
     
     var nhours=thetime.getHours();
     var nmins=thetime.getMinutes();
     var nsecn=thetime.getSeconds();
     var nday=thetime.getDay();
     var nmonth=thetime.getMonth();
     var ntoday=thetime.getDate();
     var nyear=thetime.getYear();
     var AorP=" ";
     
	
     //if (nhours>=12)
     //    AorP="P.M.";
     //else
     //    AorP="A.M.";
     //
     //if (nhours>=13)
     //    nhours-=12;
     //
	 
     if (nsecn<10)
      nsecn="0"+nsecn;
     
     if (nmins<10)
      nmins="0"+nmins;
     
     if (nday==0)
       nday="Domenica";
     if (nday==1)
       nday="Lunedì";
     if (nday==2)
       nday="Martedì";
     if (nday==3)
       nday="Mercoledì";
     if (nday==4)
       nday="Giovedì";
     if (nday==5)
       nday="Venerdì";
     if (nday==6)
       nday="Sabato";
     
     nmonth+=1;
     
     if (nyear<=99)
       nyear= "19"+nyear;
     
     if ((nyear>99) && (nyear<2000))
      nyear+=1900;
     
     //document.clockform.clockspot.value=nhours+": "+nmins+": "+nsecn+" "+AorP+" "+nday+", "+nmonth+"/"+ntoday+"/"+nyear;
	 document.clockform.clockspot.value=" "+nday+", "+ntoday+"/"+nmonth+"/"+nyear+" - "+nhours+": "+nmins+": "+nsecn;
     setTimeout('startclock()',1000);
}
*/

/***************************************
* Visualizza la data in formato esteso *
***************************************/

function data_estesa()
{
  var giorno = new Array("domenica", "lunedì", "martedì","mercoledì","giovedì", "venerdì","sabato");
  var mese   = new Array("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio" ,"Giugno", "Luglio" ,"Agosto", "Settembre" ,"Ottobre" ,"Novembre","Dicembre");
  d     = new Date();
  set   = d.getDay();
  gg    = d.getDate();
  m     = d.getMonth();
  aa    = d.getYear();
  if (aa<=99)
       aa = "19"+ aa;
     
  if ((aa>99) && (aa<2000))
      aa +=1900;
  document.write(giorno[set]+', '+gg+' '+mese[m]+' '+aa);
}



/******************************************
* FUNZIONI DI UTILITA' PER LA NAVIGAZIONE *
******************************************/

function menu_click(nome)
{
 	var ogg=document.getElementById(nome);
	if(ogg) {
			if(ogg.style.display=="none") {	ogg.style.display="";} 
			else { ogg.style.display="none"; }
	}
}


function avviso()
{
    alert("Servizio non ancora attivo.");
}

function rif_percorso()
{
 	var ogg=top.frames[0];
	var rif=ogg.document.getElementById("str_percorso");
	return(rif);
}

function entra_profilo()
{
 	var f,t;
	f="logos.html";
	t="logos";
	open(f,t);
	f="menu_operatore.html";
	t="menu";
	open(f,t);
	f="blank.html";
	t="gestione";
	open(f,t);
}	
	
function apri_ricerca()
{
	var f,t;
	//parent.frames[0].str_percorso.innerHTML="<b>scheda lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>scheda lavoratore</b>";
	f="ricerca.html";
	t="gestione";
	open(f,t);
	f="pp_scheda.html";
	t="nav";
	open(f,t);
}

function apri_ricerca2()
{
	var f,t;
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>accordo lavoratore</b>";
	f="ricerca2.html";
	t="gestione";
	open(f,t);
	f="blank3.html";
	t="nav";
	open(f,t);
}

function apri_ricerca_sint()
{
	var f,t;
	//parent.frames[0].str_percorso.innerHTML="<b>scheda lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>scheda sintetica</b>";
	f="ricerca_sint.html";
	t="gestione";
	open(f,t);
	f="pp_scheda.html";
	t="nav";
	open(f,t);
}

function apri_accordo()
{
	var f,t;
	//parent.frames[0].str_percorso.innerHTML="<b>accordo lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>accordo lavoratore</b>";
	f="menu_accordo.html";
	t="gestione";
	open(f,t);
	f="blank3.html";
	t="nav";
	open(f,t);
}

function nuova_ricerca()
{
 	var f,t;
	//parent.frames[0].str_percorso.innerHTML="<b>scheda lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>scheda lavoratore</b>";
	f="ricerca.html";
	t="gestione";
	open(f,t);
}

function nuova_ricerca2()
{
 	var f,t;
	//parent.frames[0].str_percorso.innerHTML="<b>accordo lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>accordo lavoratore</b>";
	f="ricerca2.html";
	t="gestione";
	open(f,t);
}

function nuova_ricerca_sint()
{
 	var f,t;
	//parent.frames[0].str_percorso.innerHTML="<b>scheda lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>scheda sintetica</b>";
	f="ricerca_sint.html";
	t="gestione";
	open(f,t);
}

function apri_nuovo_lav()
{
 	var f, t;
	//parent.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>nuovo lavoratore</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;&nbsp;<b>nuovo lavoratore</b>";
	f="nuovo_lav.html";
	t="gestione";
	open(f,t);
}

function apri_nuovo_amm()
{
 	var f, t;
	f="menu_lav_nuovo.html";
	t="gestione";
	open(f,t);
	ammNuovoOnClick();
}

function apri_nuovo_anag2()
{
 	apri_menu_nuovo_lav("Giorgio Verdi");
 	var f, t;
	f="menu_lav_nuovo.html";
	t="gestione";
	var w=open(f,t);
	w.focus();
	//anag2NuovoClick();
}

/*******************************************
* FUNZIONI PER IL MENU INTERNO ALLA PAGINA *
*******************************************/
function rif_nome(nome)
{
 	var ogg=document.getElementById(nome);
	return(ogg);
}

function init_menu()
{
 	rif_nome("tabAnag").className="bordoblu";
	rif_nome("tabAmm").className="bordoblu";
	rif_nome("tabCurr").className="bordoblu";
	
	rif_nome("tabAnag").onclick=anagraficaOnClick;
	rif_nome("tabAmm").onclick=amministrOnClick;
	rif_nome("tabCurr").onclick=currOnClick;
	
	rif_nome("anagrafica").style.display="none";
	rif_nome("amministr").style.display="none";
	rif_nome("curr").style.display="none";
}

function anagraficaOnClick()
{
 	init_menu();
	rif_nome("tabAnag").className="sel";
	rif_nome("tabAnag").onclick=null;
	rif_nome("anagrafica").style.display="";
	var f,t;
	f="dati_anagrafici.html";
	t="dati";
	open(f,t);
	rif_nome("anag1").className="sel";
	rif_nome("anag2").className="bordoblu";
	rif_nome("anag3").className="bordoblu";
	rif_nome("anag1").onclick=null;
	rif_nome("anag2").onclick=anag2Click;
	rif_nome("anag3").onclick=anag3Click;
	//parent.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>dati anagrafici</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;&nbsp;<b>dati anagrafici</b>";
	bk.style.visibility="hidden";
	bk.onclick=null;
	fw.style.visibility="visible";
	fw.onclick=anag2Click;
}

function amministrOnClick()
{
	init_menu();
	rif_nome("tabAmm").className="sel";
	rif_nome("tabAmm").onclick=null;
	rif_nome("amministr").style.display="";
	var f,t;
	f="dati_amministrativi.html";
	t="dati";
	open(f,t);
	rif_nome("amm1").className="sel";
	rif_nome("amm2").className="bordoblu";
	rif_nome("amm3").className="bordoblu";
	rif_nome("amm1").onclick=null;
	rif_nome("amm2").onclick=amm2Click;
	rif_nome("amm3").onclick=amm3Click;
	//parent.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>dati amministrativi</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;<b>dati amministrativi</b>";
	bk.style.visibility="hidden";
	bk.onclick=null;
	fw.style.visibility="visible";
	fw.onclick=amm2Click;
}

function currOnClick()
{
	init_menu();
	rif_nome("tabCurr").className="sel";
	rif_nome("tabCurr").onclick=null;
	rif_nome("curr").style.display="";
	var f,t;
	f="dati_curr.html";
	t="dati";
	open(f,t);
	rif_nome("curr1").className="sel";
	rif_nome("curr2").className="bordoblu";
	rif_nome("curr3").className="bordoblu";
	rif_nome("curr1").onclick=null;
	rif_nome("curr2").onclick=curr2Click;
	rif_nome("curr3").onclick=curr3Click;
	//parent.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>dati curriculari</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;<b>dati curriculari</b>";
	bk.style.visibility="hidden";
	bk.onclick=null;
	fw.style.visibility="visible";
	fw.onclick=curr2Click;
}

function anag2Click()
{
 	var f,t;
	f="dati_anagrafici2.html";
	t="dati";
	open(f,t);
	rif_nome("anag1").className="bordoblu";
	rif_nome("anag2").className="sel";
	rif_nome("anag3").className="bordoblu";
	rif_nome("anag1").onclick=anagraficaOnClick;
	rif_nome("anag2").onclick=null;
	rif_nome("anag3").onclick=anag3Click;
	bk.style.visibility="visible";
	bk.onclick=anagraficaOnClick;
	fw.style.visibility="visible";
	fw.onclick=anag3Click;
}

function anag2Salva()
{
 	parent.anag2Click();
}

function anag3Click()
{
 	var f,t;
	f="dati_anagrafici3.html";
	t="dati";
	open(f,t);
	rif_nome("anag1").className="bordoblu";
	rif_nome("anag2").className="bordoblu";
	rif_nome("anag3").className="sel";
	rif_nome("anag1").onclick=anagraficaOnClick;
	rif_nome("anag2").onclick=anag2Click;
	rif_nome("anag3").onclick=null;
	bk.style.visibility="visible";
	bk.onclick=anag2Click;
	fw.style.visibility="hidden";
	fw.onclick=null;
}

function anag3Salva()
{
 	parent.anag3Click();
}

function amm2Click()
{
 	var f,t;
	f="dati_amministrativi2.html";
	t="dati";
	open(f,t);
	rif_nome("amm1").className="bordoblu";
	rif_nome("amm2").className="sel";
	rif_nome("amm3").className="bordoblu";
	rif_nome("amm1").onclick=amministrOnClick;
	rif_nome("amm2").onclick=null;
	rif_nome("amm3").onclick=amm3Click;
	bk.style.visibility="visible";
	bk.onclick=amministrOnClick;
	fw.style.visibility="visible";
	fw.onclick=amm3Click;
}

function amm2Salva()
{
 	parent.amm2Click();
}

function amm3Click()
{
 	var f,t;
	f="dati_amministrativi3.html";
	t="dati";
	open(f,t);
	rif_nome("amm1").className="bordoblu";
	rif_nome("amm2").className="bordoblu";
	rif_nome("amm3").className="sel";
	rif_nome("amm1").onclick=amministrOnClick;
	rif_nome("amm2").onclick=amm2Click;
	rif_nome("amm3").onclick=null;
	bk.style.visibility="visible";
	bk.onclick=amm2Click;
	fw.style.visibility="hidden";
	fw.onclick=null;
}

function amm3Salva()
{
 	parent.amm3Click();
}

function curr2Click()
{
 	var f,t;
	f=apri_curr();
	t="dati";
	//alert(f);
	open(f,t);
	
	rif_nome("curr2").className="sel";
	rif_nome("curr3").className="bordoblu";
	rif_nome("curr1").className="bordoblu";
	rif_nome("curr1").onclick=currOnClick;
	rif_nome("curr2").onclick=null;
	rif_nome("curr3").onclick=curr3Click;
	bk.style.visibility="visible";
	bk.onclick=currOnClick;
	fw.style.visibility="visible";
	fw.onclick=curr3Click;
}

function curr2Salva()
{
 	parent.curr2Click();
}

function curr3Click()
{
 	var f,t;
	f="dati_curr3.html";
	t="dati";
	open(f,t);
	rif_nome("curr1").className="bordoblu";
	rif_nome("curr2").className="bordoblu";
	rif_nome("curr3").className="sel";
	rif_nome("curr1").onclick=currOnClick;
	rif_nome("curr2").onclick=curr2Click;
	rif_nome("curr3").onclick=null;
	bk.style.visibility="visible";
	bk.onclick=curr2Click;
	fw.style.visibility="hidden";
	fw.onclick=null;
}

function anagNuovoOnClick()
{
 	init_menu2();
	rif_nome("tabAnag").className="sel";
	rif_nome("tabAnag").onclick=null;
	rif_nome("anagrafica").style.display="";
	var f,t;
	f="dati_anag_nuovo.html";
	t="dati";
	open(f,t);
	rif_nome("anag1").className="sel";
	rif_nome("anag2").className="bordoblu";
	rif_nome("anag3").className="bordoblu";
	rif_nome("anag1").onclick=null;
	rif_nome("anag2").onclick=anag2NuovoClick;
	rif_nome("anag3").onclick=avviso;
	//top.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>dati anagrafici</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;<b>dati anagrafici</b>";
	bk.style.visibility="hidden";
	bk.onclick=null;
	fw.style.visibility="visible";
	fw.onclick=anag2NuovoClick;
}

function anag2NuovoClick()
{
 	init_menu2();
	rif_nome("tabAnag").className="sel";
	rif_nome("tabAnag").onclick=null;
	rif_nome("anagrafica").style.display="";
 	var f,t;
	f="dati_anagrafici2_nuovo.html";
	t="dati";
	open(f,t);
	rif_nome("anag1").className="bordoblu";
	rif_nome("anag2").className="sel";
	rif_nome("anag3").className="bordoblu";
	rif_nome("anag1").onclick=anagNuovoOnClick;
	rif_nome("anag2").onclick=null;
	rif_nome("anag3").onclick=avviso;
	//top.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>dati anagrafici</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;<b>dati anagrafici</b>";
	bk.style.visibility="visible";
	bk.onclick=anagNuovoOnClick;
	fw.style.visibility="hidden";
	fw.onclick=null;
}

function ammNuovoOnClick()
{
	init_menu2();
	rif_nome("tabAmm").className="sel";
	rif_nome("tabAmm").onclick=null;
	rif_nome("amministr").style.display="";
	var f,t;
	f="dati_amm_nuovo.html";
	t="dati";
	open(f,t);
	rif_nome("amm1").className="sel";
	rif_nome("amm2").className="bordoblu";
	rif_nome("amm3").className="bordoblu";
	rif_nome("amm1").onclick=null;
	rif_nome("amm2").onclick=avviso;
	rif_nome("amm3").onclick=avviso;
	//top.frames[0].str_percorso.innerHTML="scheda lavoratore&nbsp;>&nbsp;<b>dati amministrativi</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;scheda lavoratore&nbsp;&gt;<b>dati amministrativi</b>";
	bk.style.visibility="hidden";
	bk.onclick=null;
	fw.style.visibility="hidden";
	fw.onclick=null;
}

function init_menu2()
{
 	rif_nome("tabAnag").className="bordoblu";
	rif_nome("tabAmm").className="bordoblu";
	rif_nome("tabCurr").className="bordoblu";
	
	rif_nome("tabAnag").onclick=anagNuovoOnClick;
	rif_nome("tabAmm").onclick=ammNuovoOnClick;
	rif_nome("tabCurr").onclick=avviso;
	
	rif_nome("anagrafica").style.display="none";
	rif_nome("amministr").style.display="none";
	rif_nome("curr").style.display="none";
}


/**********
* ACCORDO *
**********/

function init_accordo()
{
 	rif_nome("tabAccordo").className="bordoblu";
	rif_nome("tabPatto").className="bordoblu";
	
	rif_nome("tabAccordo").onclick=accordoOnClick;
	rif_nome("tabPatto").onclick=pattoOnClick;
}

function accordoOnClick()
{
 	init_accordo();
	rif_nome("tabAccordo").className="sel";
	rif_nome("tabAccordo").onclick=null;
	var f,t;
	f="dati_accordo.html";
	t="dati";
	open(f,t);
	//parent.frames[0].str_percorso.innerHTML="accordo lavoratore&nbsp;>&nbsp;<b>accordo generico</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;accordo lavoratore&nbsp;&gt;&nbsp;<b>accordo generico</b>";
}

function pattoOnClick()
{
	init_accordo();
	rif_nome("tabPatto").className="sel";
	rif_nome("tabPatto").onclick=null;
	var f,t;
	f="dati_patto.html";
	t="dati";
	open(f,t);
	//parent.frames[0].str_percorso.innerHTML="accordo lavoratore&nbsp;>&nbsp;<b>patto 297</b>";
	var str=rif_percorso();
	str.innerHTML="preselezione&nbsp;&gt;&nbsp;accordo lavoratore&nbsp;&gt;&nbsp;<b>patto 297</b>";
}


/*********************
* FUNZIONI PER IL CV *
*********************/	

function apri_curr()
{
 	//var i=top.frames[0].ins_titolocurr;
	var ogg=top.frames[0];
	var rif=ogg.document.getElementById("ins_titolocurr");
 	if(rif.innerHTML=="1") { return("dati_curr_dopo_ins.html"); }
	else { return("dati_curr_primo_ins.html"); }
}

function ins_tit()
{
 	//alert(top.frames[0].ins_titolocurr); 
    //top.frames[0].ins_titolocurr=1;
	var ogg=top.frames[0];
	var rif=ogg.document.getElementById("ins_titolocurr");
	//alert(rif.innerHTML);
	rif.innerHTML="1";
	rif.style.display="none";
	curr2Salva();
}


/**********************************
* Funzioni per il menu operatore  *
**********************************/
function rif_nome2(nome)
{
 	var fr=parent.frames[1];
	//alert(fr.name);
	var ogg=fr.document.getElementById(nome);
	return(ogg);
}

function apri_lav(nome)
{
 	cancella_menu_lav();
 	var otr=rif_nome2("lavoratore");
	rif_nome2("nome_lav").innerHTML=nome;
	otr.style.display="";
	otr=rif_nome2("vlavoratore");
	//alert(otr.nodeName);
	var coll=otr.childNodes;
	//alert(coll.length);
	var otd=coll[0];
	//alert(otd.nodeName);
	var str="<table><tr><td width='3%'>&nbsp;</td>";
	str +="<td><a href='menu_lavoratore.html' target='gestione'><img src='imgs/punto.gif' border='0'>";
	str +="Scheda lavoratore</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='menu_accordo.html' target='gestione'>";
	str +="<img src='imgs/punto.gif' border='0'>Accordo lavoratore</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='#' onClick='avviso();'>";
	str +="<img src='imgs/punto.gif' border='0'>Scheda Esiti</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='sintetica1.html' target='gestione'>";
	str +="<img src='imgs/punto.gif' border='0'>Scheda Sintetica<a></td></tr></table>";
	otd.innerHTML=str;
	otr.style.display="";
	var omenu=rif_nome2("menu_generale");
	//alert(omenu.nodeName);
	//omenu.click();
	parent.frames[1].apri_menu(omenu);
	var f,t;
	f="menu_lavoratore.html";
	t="gestione";
	open(f,t);
}

function apri_sint(nome)
{
 	cancella_menu_lav();
 	var otr=rif_nome2("lavoratore");
	rif_nome2("nome_lav").innerHTML=nome;
	otr.style.display="";
	otr=rif_nome2("vlavoratore");
	//alert(otr.nodeName);
	var coll=otr.childNodes;
	//alert(coll.length);
	var otd=coll[0];
	//alert(otd.nodeName);
	var str="<table><tr><td width='3%'>&nbsp;</td>";
	str +="<td><a href='menu_lavoratore.html' target='gestione'><img src='imgs/punto.gif' border='0'>";
	str +="Scheda lavoratore</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='menu_accordo.html' target='gestione'>";
	str +="<img src='imgs/punto.gif' border='0'>Accordo lavoratore</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='#' onClick='avviso();'>";
	str +="<img src='imgs/punto.gif' border='0'>Scheda Esiti</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='sintetica1.html' target='gestione'>";
	str +="<img src='imgs/punto.gif' border='0'>Scheda Sintetica<a></td></tr></table>";
	otd.innerHTML=str;
	otr.style.display="";
	var omenu=rif_nome2("menu_generale");
	//alert(omenu.nodeName);
	//omenu.click();
	parent.frames[1].apri_menu(omenu);
	var f,t;
	f="sintetica1.html";
	t="gestione";
	open(f,t);
	var strp=rif_percorso();
	strp.innerHTML="preselezione&nbsp;&gt;&nbsp;<b>scheda sintetica</b>";
}

function apri_menu_nuovo_lav(nome)
{
 	cancella_menu_lav();
 	var otr=rif_nome2("lavoratore");
	rif_nome2("nome_lav").innerHTML=nome;
	otr.style.display="";
	otr=rif_nome2("vlavoratore");
	//alert(otr.nodeName);
	var coll=otr.childNodes;
	//alert(coll.length);
	var otd=coll[0];
	//alert(otd.nodeName);
	var str="<table><tr><td width='3%'>&nbsp;</td>";
	str +="<td><a href='menu_lav_nuovo.html' target='gestione'><img src='imgs/punto.gif' border='0'>";
	str +="Scheda lavoratore</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='#' onClick='avviso();'>";
	str +="<img src='imgs/punto.gif' border='0'>Accordo lavoratore</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='#' onClick='avviso();'>";
	str +="<img src='imgs/punto.gif' border='0'>Scheda Esiti</a></td></tr>";
	str +="<tr><td width='3%'>&nbsp;</td><td><a href='sintetica2.html' target='gestione'>";
	str +="<img src='imgs/punto.gif' border='0'>Scheda Sintetica<a></td></tr></table>";
	otd.innerHTML=str;
	otr.style.display="";
	var omenu=rif_nome2("menu_generale");
	//alert(omenu.nodeName);
	//omenu.click();
	parent.frames[1].apri_menu(omenu);
}

function cancella_menu_lav()
{
 	rif_nome2("nome_lav").innerHTML="";
	var otr=rif_nome2("lavoratore");
	otr.style.display="none";
	otr=rif_nome2("vlavoratore");
	var coll=otr.childNodes;
	var otd=coll[0];
	otd.innerHTML="";
	otr.style.display="none";
}

function chiudi_lavoratore()
{
 	cancella_menu_lav();
	var f,t;
	f="blank.html";
	t="gestione";
	open(f,t);
	t="nav";
	open(f,t);
	var omenu=rif_nome2("menu_generale");
	var img=omenu.childNodes[0];
	//alert(parent.frames[1].img_cl.src);
	if(img.src==parent.frames[1].img_cl.src) {
		img.src="imgs/aperto.gif";
		rif_nome2("vmenu").style.display="";
	}
	var str=rif_percorso();
	str.innerHTML="<b>Home</b>";;
}
