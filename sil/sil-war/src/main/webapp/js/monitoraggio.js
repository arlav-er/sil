// monitoraggio.js

var RITARDO = 2000;
var msec; 
var timerID ;
var timerRunning ;
var delay;

var l_fsMenu ;
var l_fsMain ;
var l_fsFooter ;


function stopTimer()
{
    if(timerRunning)
        clearTimeout(timerID);
    timerRunning = false;
}


// Apertura 				
function initTimerApertura()
{


	l_fsMenu =window.top.document.getElementById('fsMenu');
	l_fsMain =window.top.document.getElementById('fsMain');
	l_fsFooter =window.top.document.getElementById('fsFooter');

	msec= RITARDO;
	timerID = null;
	timerRunning = false;
	delay = 40;
	
	stopTimer();
	startTimerApertura();
}

function startTimerApertura()
{

    if (msec<0)
    {
        stopTimer();
    }
    else
    {
    	var dimColFsMenu = Math.floor(msec * 220 /RITARDO) + ",*";
    	var dimRowsFsMain = "0,"  + Math.floor(msec * 68 /RITARDO) + ",*";
    	var dimRowsFsFooter ="*," +  Math.floor(msec * 42 /RITARDO);
    	
    	l_fsMenu.cols=dimColFsMenu;
    	l_fsMain.rows=dimRowsFsMain;
    	l_fsFooter.rows=dimRowsFsFooter;
		
		msec = msec - 100;
        timerRunning = true;
        timerID = self.setTimeout("startTimerApertura()", delay);
    }
}

// Chiusura 				

function initTimerChiusura()
{
	l_fsMenu =window.top.document.getElementById('fsMenu');
	l_fsMain =window.top.document.getElementById('fsMain');
	l_fsFooter =window.top.document.getElementById('fsFooter');
	
	msec= RITARDO;
	timerID = null;
	timerRunning = false;
	delay = 40;

    stopTimer();
    startTimerChiusura();
}

function startTimerChiusura()
{
    if (msec<0)
    {
        stopTimer();
      //  window.location.href="<%=redirectURL%>";
    }
    else
    {
    	var dimColFsMenu = Math.floor(220 - (msec * 220 /RITARDO)) + ",*";
    	var dimRowsFsMain = "0,"  + Math.floor(68 - (msec * 68 /RITARDO)) + ",*";
    	var dimRowsFsFooter ="*," +  Math.floor(42 - (msec * 42 /RITARDO));
    	
    	l_fsMenu.cols=dimColFsMenu;
    	l_fsMain.rows=dimRowsFsMain;
    	l_fsFooter.rows=dimRowsFsFooter;
		
		msec = msec - 100;
        timerRunning = true;
        timerID = self.setTimeout("startTimerChiusura()", delay);
    }
}


