<%
	//Variabile per un eventuale ritorno al movimento protocollato da uno rettificato
	String prgMovimentoProtDaRett = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOPROTDARETTIFICA");
	if (!prgMovimentoProtDaRett.equals("")) {%>
	<input type="hidden" name="PRGMOVIMENTOPROTDARETTIFICA" value="<%=prgMovimentoProtDaRett%>"/>
<%}%>