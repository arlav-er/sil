<SCRIPT language="javascript">

var openedComp;

function gestioneCompetenze(){
	<% if(!consulta) { %>
		var permettiRicavoFiltro = false;
      		<% if(inserisci || rettifica) { %> 
      			permettiRicavoFiltro = true; 
      		<%} else { 
      			if(valida) { %>
      				if (document.Frm1.CDNLAVORATORE.value != "" &&
      					document.Frm1.PRGAZIENDA.value != "" &&
      					document.Frm1.PRGUNITA.value != "" ) { 
      						permettiRicavoFiltro = true; 
      				}
      			<% }
      		 }%>
      		if(permettiRicavoFiltro){
	            apriRicavoCompetenze();
	            //if(document.Frm1.COMPETENZAZILAV.value == "false"){
  				//	alert("Impossibile proseguire con l'inserimento del movimento:\nmanca la competenza amministrativa sul lavoratore e/o sull'azienda.");
	            //	return false;
	            //}
	         } else {
	     		<% if(valida){ %>
 	     			  document.Frm1.CANVIEW.value = "true";
	                  document.Frm1.permettiImpatti.value = "true";
	                  if(document.Frm1.CANVIEW.value == "false"){
  						alert("Impossibile proseguire con l'inserimento del movimento:\nmanca la competenza amministrativa sul lavoratore e/o sull'azienda.");
  						countSubmit = 0;
    				  }
    				  else {
    				  	document.Frm1.submit();
    				  }
    			<%} else { %> 
    				countSubmit = 0;
    			<%}%>
	     	  }
     <%}%>
     return true;
	}

function apriRicavoCompetenze(){
  var prgAzienda = document.Frm1.PRGAZIENDA.value;
  var prgUnita = document.Frm1.PRGUNITA.value;      
  var cdnLavoratore = document.Frm1.CDNLAVORATORE.value;
  var codTipoMov = document.Frm1.CODTIPOMOV.value;
  var contesto = "<%=currentcontext%>"; 
  if (prgAzienda == "" || prgUnita == "" || cdnLavoratore == "") {
  	  alert("Occorre indicare prima l'azienda e il lavoratore");
  	  countSubmit = 0;
  } else {
  	  var f = "AdapterHTTP?PAGE=CompetenzeAmminPage&PRGAZIENDA=" + prgAzienda + 
	  "&PRGUNITA=" + prgUnita + "&CDNLAVORATORE=" + cdnLavoratore + "&CODTIPOMOV=" + codTipoMov + "&FUNZ_GEST=gestioneNavigazione&CONTESTO=" + contesto;
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=200,height=150,top=100,left=100";
	  openedComp = window.open(f, t, feat); 
  }
}

// da modificare
function gestioneNavigazione(codTipoMov){
	if(document.Frm1.CANVIEW.value == "true"){
		document.getElementById("btnSubmitMov").disable = true;
		document.Frm1.submit();
	} else {
		countSubmit = 0;
		alert("Impossibile proseguire con l'inserimento del movimento:\nmanca la competenza amministrativa sul lavoratore e/o sull'azienda.");
	}
}

/*
  Funzione utilizzata nella pagina della consultazione della pagina generale
  per impostare il campo permettiImapatti
*/
function impostaFlagImpatti(){
  <%if(consulta){%> 
    document.Frm1.CANVIEW.value = "true";
    document.Frm1.permettiImpatti.value = "<%=canEditLav%>";
  <%}%>
}

</script>