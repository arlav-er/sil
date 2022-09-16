<SCRIPT language="javascript">
<!--

function apriControlloLavoratore(cdnLav) {
	<%if (provenienza!=null && !provenienza.equalsIgnoreCase("validazione")) { %>
	var f = "AdapterHTTP?PAGE=ControlloMbCmEtaLavPage&PROVENIENZA=<%=provenienza%>&cdnLavoratore="+cdnLav;
	var t = "_blank";
	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=50,height=50,top=0,left=100";
	opened = window.open(f, t, feat);
	<%}%>
}

var datiSensibiliLavoratore = null;
  
  <%
	  SourceBean datiLav =  (SourceBean)serviceResponse.getAttribute("M_GetMbCmEtaLav.rows.row");
	  String setDatiLav = it.eng.sil.module.movimenti.DatiSensibiliLavoratore.toJS(datiLav);
	  out.print(setDatiLav);
  %>
  
function controllaInfoLavoratore() {
  msg = "";
	if (datiSensibiliLavoratore==null) return; // non ci sono dati da controllare
	else {
  		if (datiSensibiliLavoratore.etaMinoreDi15)
			msg += "Il lavoratore ha meno di 15 anni.\n"
		if (datiSensibiliLavoratore.disabile) {
			if ( (document.forms[0] != null) && (document.forms[0].LAVORATOREDISABILE != null) ) {
				document.forms[0].LAVORATOREDISABILE.value = "true";
			}	
			msg += "Il lavoratore e' disabile.\n";
		}
		
		if (datiSensibiliLavoratore.inCollMirato) {
			if ( (document.forms[0] != null) && (document.forms[0].LAVORATORECOLLMIRATO != null) ) {
				document.forms[0].LAVORATORECOLLMIRATO.value = "true";
			}
		}
		else {
			if ( (document.forms[0] != null) && (document.forms[0].LAVORATORECOLLMIRATO != null) ) {
				document.forms[0].LAVORATORECOLLMIRATO.value = "false";
			}
		}
		if (datiSensibiliLavoratore.inMobilita)
			msg += "Il lavoratore e' in mobilità.\n";
		if (datiSensibiliLavoratore.inSosp)
			msg += "Il lavoratore è in attesa di mobilità (sospeso).";
		if (msg!="")
			alert(msg);
	}
}

//-->
</SCRIPT>