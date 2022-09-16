<%
	//Controllo se ho già un successivo e se il movimento è protocollato
	if (prgMovimentoSucc == "" && codStatoAtto.equalsIgnoreCase("PR")) {
		//Guardo se ho dei movimenti da utilizzare come successivi
		Vector successiviCompatibiliRows = serviceResponse.getAttributeAsVector("M_GetMovimentiSuccessiviCompatibili.ROWS.ROW");
		if (successiviCompatibiliRows.size() > 0) {
%>
    <script language="Javascript">
    	function collegaSuccessivo() {
	        var f = "AdapterHTTP?PAGE=MovListaSuccessiviPage&CDNLAVORATORE=<%=cdnLavoratore%>"
	        	+ "&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>"
	        	+ "&DATINIZIOMOV=<%=datInizioMov%>&PRGMOVIMENTO=<%=prgMovimento%>&NUMKLOMOV=<%=numKloMov%>"
	        	+ "&AGGFUNZ=aggiornaDettaglioMovimento&CDNFUNZIONE=<%=_funzione%>";
	        var t = "_blank";
	        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no," 
	        	+ "scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
	        opened = window.open(f, t, feat);    		
    	}
    </script>
	  <%}
	}%>