<%/*
	File di include che permette la visualizzazione dei dati della trasformazione.
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
<% if (dati.containsAttribute("strMatricola")) {%>
	Matricola <strong><%=dati.getAttribute("strMatricola")%></strong>;
<%}%>
<% if (dati.containsAttribute("tipoTempo")) {%>
	Tempo <strong><%=dati.getAttribute("tipoTempo")%></strong>;
<%}%>
<% if (dati.containsAttribute("datFineMov")) {%>
	Scadenza <strong><%=dati.getAttribute("datFineMov")%></strong>
	<%if (dati.containsAttribute("datFineMovEff") && 
	!((String) dati.getAttribute("datFineMovEff")).equalsIgnoreCase((String) dati.getAttribute("datFineMovEff"))) {%>
	(Effettiva <strong><%=dati.getAttribute("datFineMovEff")%></strong>)<%}%>;
<%}%>
<% if (dati.containsAttribute("orario")) {%>
	Orario <strong><%=dati.getAttribute("orario")%></strong>;
<%}%>
<% if (dati.containsAttribute("numOreSett")) {%>
	Ore Settimanali <strong><%=dati.getAttribute("numOreSett")%></strong>;
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
<% if (dati.containsAttribute("tipotrasformazione")) {%>
	<br/>Tipo trasformazione <strong><%=dati.getAttribute("tipotrasformazione")%></strong>;
<%}%>
</p>