<CENTER>
<script type="text/javascript">
<!--
//Ritorna alla lista dei risultati della copia prospetti
function ritornaListaRisultati() {
    var RitornaPrgRisult = document.forms[0].PRGRISULTATO.value;
	var get = "AdapterHTTP?PAGE=CopiaProspVisualizzaRisultPage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=SAME" +
	          "&RITORNAPRGRISULTATO="+ RitornaPrgRisult + "#RITORNA_PRGRISULTATO_" + RitornaPrgRisult;
	setWindowLocation(get);
} 
//-->
</script>
<%
//Gestione del pulsante di ritorno, l'attributo PAGERITORNOLISTA mi dice a quale lista devo tornare 
//(se non c'è non creo il pulsante)
String pageRetLista = StringUtils.getAttributeStrNotNull(serviceRequest, "PAGERITORNOLISTA");
if (!pageRetLista.equals("")) {	
	String ritornaAlPrgRisultato = "";
   	if ( serviceRequest.containsAttribute("PRGRISULTATO") ) {
      	ritornaAlPrgRisultato = (String) serviceRequest.getAttribute("PRGRISULTATO");
   	}
   	if (ritornaAlPrgRisultato != null && !ritornaAlPrgRisultato.equals("")) {
      	%><input type="hidden" name="PRGRISULTATO" value="<%=ritornaAlPrgRisultato%>"/><%
   	} else {
      	%><input type="hidden" name="PRGRISULTATO" value=""/><%
   	}
	out.println("<a href=\"#\" onClick=\"ritornaListaRisultati()\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>");
	%>
	<input type="hidden" name="PAGERITORNOLISTA" value="<%=pageRetLista%>"/>
<%}%>
</CENTER> 