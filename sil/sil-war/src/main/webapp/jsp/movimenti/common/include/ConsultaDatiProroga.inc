<%/*
	File di include che permette la visualizzazione dei dati della proroga.
	Necessita di un SourceBean Java chiamato "dati" da cui estrarre i dati da 
	visualizzare.
*/%>
<p align="left">
<% if (dati.containsAttribute("descrtipomovimento")) {%>
	Tipo Movimento <strong><%=dati.getAttribute("descrtipomovimento")%></strong>;
<%}%>
<% if (dati.containsAttribute("datInizioMov")) {%>
	Data Inizio <strong><%=dati.getAttribute("datInizioMov")%></strong>;
<%}%>
<% if (dati.containsAttribute("numggmovcomunicaz")) {%>
	Giorni prima di comunicazione <strong><%=dati.getAttribute("numggmovcomunicaz")%></strong>; 
<%}%>
<% if (dati.containsAttribute("datFineMov")) {%>
	Scadenza <strong><%=dati.getAttribute("datFineMov")%></strong>
	<%if (dati.containsAttribute("datFineMovEff") && 
	!((String) dati.getAttribute("datFineMovEff")).equalsIgnoreCase((String) dati.getAttribute("datFineMovEff"))) {%>
	(Effettiva <strong><%=dati.getAttribute("datFineMovEff")%></strong>)<%}%>;
<%}%>
<br/>
<% if (dati.containsAttribute("mansione")) {%>
	<br/>Mansione <strong><%=dati.getAttribute("mansione")%></strong>;
<%}%>
<% if (dati.containsAttribute("attivita")) {%>
	<br/>Attività <strong><%=dati.getAttribute("attivita")%></strong>;
<%}%>
<% if (dati.containsAttribute("livello")) {%>
	Livello <strong><%=dati.getAttribute("livello")%></strong>;
<%}%>
<% if (dati.containsAttribute("compenso")) {%>
	Compenso <strong><%=dati.getAttribute("compenso")%></strong>&#8364;;
<%}%>
<% if (dati.containsAttribute("CCNL")) {%>
	<br/>CCNL <strong><%=dati.getAttribute("CCNL")%></strong>;
<%}%>
<% if (dati.containsAttribute("benefici")) {%>
	<br/>Agevolazioni <strong><%=dati.getAttribute("benefici")%></strong>;
<%}%>
</p>