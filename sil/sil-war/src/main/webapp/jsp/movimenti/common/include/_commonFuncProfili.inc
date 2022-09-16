<SCRIPT language="javascript">

var openedComp;

function gestioneCompetenze(){
      <% if(!consulta) { %>
      		var permettiRicavoFiltro = false;
      		<% if(inserisci || rettifica || (valida && cdnLavoratore!=null && !cdnLavoratore.equals("") &&
      						   prgAzienda!=null && !prgAzienda.equals("") &&
      						   prgUnita!=null && !prgUnita.equals(""))){ %> 
      			permettiRicavoFiltro = true; 
      		<%}%>
      		if(permettiRicavoFiltro){
	            apriRicavoCompetenze();
	     	} else {
	     		<% if(valida){ %>
 	     			  document.Frm1.CANVIEW.value = "true";
	                  document.Frm1.permettiImpatti.value = "true";
	                  gestioneNavigazione(document.Frm1.CODTIPOMOV.value);
	            <% }%>
	     	  }
      <% } else {%>
                  document.Frm1.CANVIEW.value = "true";
                  document.Frm1.permettiImpatti.value = "true"; //13-07-04 momentaneamente aggiunta, perchè da rivedere
                  gestioneNavigazione(document.Frm1.CODTIPOMOV.value);
      <%}%>
    }

function apriRicavoCompetenze(){
  var prgAzienda = document.Frm1.PRGAZIENDA.value;
  var prgUnita = document.Frm1.PRGUNITA.value;      
  var cdnLavoratore = document.Frm1.CDNLAVORATORE.value;
  var codTipoMov = document.Frm1.CODTIPOMOV.value;
  var contesto = "<%=currentcontext%>";  
  
  if (prgAzienda == "" || prgUnita == "" || cdnLavoratore == "") {
  	  alert("Occorre indicare prima l'azienda e il lavoratore");
  } else {
	  var f = "AdapterHTTP?PAGE=CompetenzeAmminPage&PRGAZIENDA=" + prgAzienda + 
	  "&PRGUNITA=" + prgUnita + "&CDNLAVORATORE=" + cdnLavoratore + "&CODTIPOMOV=" + codTipoMov + "&FUNZ_GEST=gestioneNavigazione&CONTESTO=" + contesto;
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=200,height=150,top=100,left=100";
	  openedComp = window.open(f, t, feat); 
  }
}

function gestioneNavigazione(codTipoMov){
  if(document.Frm1.CANVIEW.value == "true"){
    if(codTipoMov == "AVV"){
      goTo<%=(consulta ? "NoCheck" : "Check")%>("<%=pageAvviamento%>");
    }
    if((codTipoMov == "TRA") || (codTipoMov == "PRO")){
      goTo<%=(consulta ? "NoCheck" : "Check")%>("<%=pageTrasfPro%>");
    }
    if(codTipoMov == "CES"){
      goTo<%=(consulta ? "NoCheck" : "Check")%>("<%=pageCessazione%>");
    }
  } else {
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