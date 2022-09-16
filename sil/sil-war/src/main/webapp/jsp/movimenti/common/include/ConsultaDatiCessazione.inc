<%/*
	File di include che permette la visualizzazione dei dati della cessazione.
	Necessita di un SourceBean Java chiamato "dati" da cui estrarre i dati da 
	visualizzare.
*/%>
<p align="left">
<% if (dati.containsAttribute("descrtipomovimento")) {%>
	Tipo Movimento <strong><%=dati.getAttribute("descrtipomovimento")%></strong>;
<%}%>
<% if (dati.containsAttribute("datInizioMov")) {%>
	Data Cessazione <strong><%=dati.getAttribute("datInizioMov")%></strong>;
<%}%>
<% if (dati.containsAttribute("numggmovcomunicaz")) {%>
	Giorni prima di comunicazione <strong><%=dati.getAttribute("numggmovcomunicaz")%></strong>; 
<%}%>
<% if (dati.containsAttribute("motivoCess")) {%>
	Motivo <strong><%=dati.getAttribute("motivoCess")%></strong>;
<%}%>
<br/>
<% if (dati.containsAttribute("mansione")) {%>
	<br/>Mansione <strong><%=dati.getAttribute("mansione")%></strong>;
<%}%>
<% if (dati.containsAttribute("livello")) {%>
	Livello <strong><%=dati.getAttribute("livello")%></strong>;
<%}%>
</p>