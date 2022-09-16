<%/*
	Gestione del pulsante per cfl
	richiede un'origine dei dati da cui recuperare:
	-datCFL
	-strTipoCFL
	-strNumCFL		
*/	
	String DATCFL =  StringUtils.getAttributeStrNotNull(dataOrigin,"DATCFL");
	String STRNUMCFL =  StringUtils.getAttributeStrNotNull(dataOrigin,"STRNUMCFL");
	String STRTIPOCFL =  StringUtils.getAttributeStrNotNull(dataOrigin,"STRTIPOCFL");
	
	//Guardo nella request se devo resettare il CFL
	String rescfl = StringUtils.getAttributeStrNotNull(serviceRequest,"RESETCFL");
	boolean resetcfl = "true".equalsIgnoreCase(rescfl);
	boolean cflimpostato = false;
	
	if (!DATCFL.equals("") || !STRNUMCFL.equals("") || !STRTIPOCFL.equals("")) 
	{
		cflimpostato = true;
	}
	
	//Contesto
	String contestoCFL = "consulta";
	if (inserisci) {contestoCFL = "inserisci";}
	else if (valida) {contestoCFL = "valida";}
%>
    <script language="Javascript">
	//Variabili per Autorizzazione a CFL
	var datCFL = '<%=DATCFL%>';
	var strNumCFL = '<%=STRNUMCFL%>'; 
	var strTipoCFL = '<%=STRTIPOCFL%>';
	var contestoCFL = '<%=contestoCFL%>';
<% if (valida) {%>
	var prgMovimentoApp = '<%=prgMovimentoApp%>';
<%} else {%>
	var prgMovimentoApp = '';
<%}%>
	var cflimpostato = <%=cflimpostato%>;
	
<% if (cflimpostato && resetcfl) {%>
	resetCFL();
<%}%>
	</SCRIPT>